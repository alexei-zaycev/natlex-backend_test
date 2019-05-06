package ru.net.avz.test.natlex_backend_test.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.awaitility.Awaitility;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StreamUtils;
import ru.net.avz.test.natlex_backend_test.TestHelper;
import ru.net.avz.test.natlex_backend_test.Utils;
import ru.net.avz.test.natlex_backend_test.config.WebSecurityConfig;
import ru.net.avz.test.natlex_backend_test.dao.repository.JobsRepository;
import ru.net.avz.test.natlex_backend_test.data.JobPOJO;
import ru.net.avz.test.natlex_backend_test.data.SectionPOJO;
import ru.net.avz.test.natlex_backend_test.service.JobService;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.instanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 *
 * @author Zaitsev Alexei (aka AZ) / alexei.zaycev@ya.ru / alexei.zaycev@gmail.com
 */
@RunWith(SpringRunner.class)
@EnableAsync
@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureTestEntityManager
@AutoConfigureMockMvc
public class JobsControllerTest {

    @Autowired private JobsRepository jobsRepository;
    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper jsonMapper;

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", password = "admin", roles = WebSecurityConfig.ROLE__ADMIN)
    public void test()
        throws Exception {

        List<JobPOJO> expectJobs =
                List.of("test_111.xls", "test_222.xls", "test_333.xls")
                        .stream()
                        .map(this::addJob)
                        .collect(Collectors.toUnmodifiableList());

        TestHelper.runJobsTest(
                expectJobs,
                TestHelper.JOB_COMPARATOR__BY_HASH_WITH_IGNORE_JPA_FIELDS,
                TestHelper.SECTION_COMPARATOR__BY_HASH_WITH_IGNORE_JPA_FIELDS,
                null,
                Pair.of(
                        "GET /api/jobs/",
                        this::getAllJobs),
                Pair.of(
                        jobId -> String.format("GET /api/jobs/%s/", jobId),
                        this::getJob),
                Pair.of(
                        filter -> String.format("GET /api/sections/filter?jobId=%s&sectionName=%s&geoClassName=%s&geoClassCode=%s",
                                        filter.jobId != null ? '"' + filter.jobId + '"' : null,
                                        filter.sectionName != null ? '"' + filter.sectionName + '"' : null,
                                        filter.geoClassName != null ? '"' + filter.geoClassName + '"' : null,
                                        filter.geoClassCode != null ? '"' + filter.geoClassCode + '"' : null),
                        this::findAllSectionsByFilters));

        // "левый" jobId -> 404 NOT FOUND
        TestHelper.mockMvcPerform(
                        mvc,
                        false,
                        get(String.format("/api/jobs/%s/",UUID.randomUUID())).accept(MediaType.APPLICATION_JSON_UTF8))
                  .andExpect(status().isNotFound());
    }

    private @Nonnull JobPOJO addJob(
            @Nonnull String xlsFilename) {

        assert xlsFilename != null : "<xlsFilename> is null";

        try {

            byte[] xlsBytes = StreamUtils.copyToByteArray(
                    JobService.class.getResourceAsStream(xlsFilename));

            String jsonResp =
                    TestHelper.mockMvcPerform(
                                    mvc,
                                    false,
                                    post("/api/jobs/")
                                            .contentType(Utils.MEDIA_TYPE__APPLICATION_XLS__VALUE)
                                            .accept(MediaType.APPLICATION_JSON_UTF8)
                                            .content(xlsBytes))
                            .andExpect(status().isOk())
                            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("job", instanceOf(String.class)))
                            .andReturn()
                            .getResponse()
                            .getContentAsString();

            String jobId = (String) Objects.requireNonNull(
                    jsonMapper.readValue(jsonResp, Map.class)
                            .get("job"));

            // ждем окончания обработки
            return Awaitility.await()
                        .timeout(20, TimeUnit.SECONDS)
                        .pollInterval(1, TimeUnit.SECONDS)
                        .until(
                                () -> jobsRepository.findById(jobId).orElse(null),
                                Objects::nonNull);

        } catch (RuntimeException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new IllegalStateException(ex);
        }
    }

    private @Nonnull List<String> listAllJobs() {

        try {

            String jsonResp =
                    TestHelper.mockMvcPerform(
                                    mvc,
                                    true,
                                    get("/api/jobs/")
                                            .accept(MediaType.APPLICATION_JSON_UTF8))
                            .andExpect(status().isOk())
                            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                            .andReturn()
                            .getResponse()
                            .getContentAsString();

            return jsonMapper.readValue(jsonResp, new TypeReference<ArrayList<String>>() {});

        } catch (RuntimeException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new IllegalStateException(ex);
        }
    }

    private @Nonnull List<JobPOJO> getAllJobs() {

        return listAllJobs()
                .stream()
                .map(this::getJob)
                .map(Optional::get)
                .collect(Collectors.toUnmodifiableList());
    }

    private @Nonnull Optional<JobPOJO> getJob(
            @Nonnull String jobId) {

        assert jobId != null : "<jobId> is null";

        try {

            String jsonResp =
                    TestHelper.mockMvcPerform(
                                    mvc,
                                    false,
                                    get(String.format("/api/jobs/%s/",
                                                    jobId))
                                            .accept(MediaType.APPLICATION_JSON_UTF8))
                            .andExpect(status().isOk())
                            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                            .andReturn()
                            .getResponse()
                            .getContentAsString();

            return jsonMapper.readValue(jsonResp, new TypeReference<Optional<JobPOJO>>() {});

        } catch (RuntimeException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new IllegalStateException(ex);
        }
    }

    public @Nonnull List<SectionPOJO> findAllSectionsByFilters(
            @Nonnull TestHelper.SectionsFilter filter) {

        assert filter != null : "<filter> is null";

        try {

            String jsonResp =
                    TestHelper.mockMvcPerform(
                                    mvc,
                                    true,
                                    get(String.format("/api/sections/filter?jobId=%s&sectionName=%s&geoClassName=%s&geoClassCode=%s",
                                                    Objects.toString(filter.jobId, ""),
                                                    Objects.toString(filter.sectionName, ""),
                                                    Objects.toString(filter.geoClassName, ""),
                                                    Objects.toString(filter.geoClassCode, "")))
                                            .accept(MediaType.APPLICATION_JSON_UTF8))
                            .andExpect(status().isOk())
                            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                            .andReturn()
                            .getResponse()
                            .getContentAsString();

            return jsonMapper.readValue(jsonResp, new TypeReference<ArrayList<SectionPOJO>>() {});

        } catch (RuntimeException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new IllegalStateException(ex);
        }
    }

}