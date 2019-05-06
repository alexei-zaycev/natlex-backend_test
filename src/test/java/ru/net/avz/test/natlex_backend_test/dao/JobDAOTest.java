package ru.net.avz.test.natlex_backend_test.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import ru.net.avz.test.natlex_backend_test.TestHelper;
import ru.net.avz.test.natlex_backend_test.data.JobPOJO;
import ru.net.avz.test.natlex_backend_test.data.SectionPOJO;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 
 * @author Zaitsev Alexei (aka AZ) / alexei.zaycev@ya.ru / alexei.zaycev@gmail.com
 */
@RunWith(SpringRunner.class)
@EnableAsync
@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureTestEntityManager
public class JobDAOTest {

    @Autowired private JobDAO jobDAO;

    @Test
    @DirtiesContext
    public void test() {

        TestHelper.runJobsTest(
                TestHelper.generateTestJobs(),
                TestHelper.JOB_COMPARATOR__BY_HASH_WITH_IGNORE_JPA_FIELDS,
                TestHelper.SECTION_COMPARATOR__BY_HASH_WITH_IGNORE_JPA_FIELDS,
                Pair.of(
                        job -> String.format("JobDAO.addJob(%s)", job),
                        this::addJob),
                Pair.of(
                        "JobDAO.getAllJobs()",
                        this::getAllJobs),
                Pair.of(
                        jobId -> String.format("JobDAO.getJob(\"%s\")", jobId),
                        this::getJob),
                Pair.of(
                        filter -> String.format("JobDAO.findAllSectionsByFilters(%s,%s,%s,%s)",
                                        filter.jobId != null ? '"' + filter.jobId + '"' : null,
                                        filter.sectionName != null ? '"' + filter.sectionName + '"' : null,
                                        filter.geoClassName != null ? '"' + filter.geoClassName + '"' : null,
                                        filter.geoClassCode != null ? '"' + filter.geoClassCode + '"' : null),
                        this::findAllSectionsByFilters));
    }

    private @Nonnull JobPOJO addJob(
            @Nonnull JobPOJO job) {

        assert job != null : "<job> is null";

        return jobDAO.addJob(job)
                     .join();
    }

    private @Nonnull List<JobPOJO> getAllJobs() {

        return jobDAO.getAllJobs()
                     .thenApply(jobs -> jobs.collect(Collectors.toList()))
                     .join();
    }

    private @Nonnull Optional<JobPOJO> getJob(
            @Nonnull String jobId) {

        assert jobId != null : "<jobId> is null";

        return jobDAO.getJob(jobId);
    }

    public @Nonnull List<SectionPOJO> findAllSectionsByFilters(
            @Nonnull TestHelper.SectionsFilter filter) {

        assert filter != null : "<filter> is null";

        return jobDAO.findAllSectionsByFilters(
                            filter.jobId,
                            filter.sectionName,
                            filter.geoClassName,
                            filter.geoClassCode)
                     .thenApply(jobs -> jobs.collect(Collectors.toList()))
                     .join();
    }

}