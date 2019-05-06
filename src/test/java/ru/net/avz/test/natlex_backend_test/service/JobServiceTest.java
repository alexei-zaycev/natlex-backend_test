package ru.net.avz.test.natlex_backend_test.service;

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

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author Zaitsev Alexei (aka AZ) / alexei.zaycev@ya.ru / alexei.zaycev@gmail.com
 */
@RunWith(SpringRunner.class)
@EnableAsync
@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureTestEntityManager
public class JobServiceTest {

    @Autowired private JobService jobService;

    @Test
    @DirtiesContext
    public void test() {

        List<JobPOJO> expectJobs = TestHelper.generateTestJobs();

        List<JobPOJO> actualJobs =
                Stream.of("test_111.xls", "test_222.xls", "test_333.xls")
                      .map(JobService.class::getResourceAsStream)
                      .map(content -> Pair.of(jobService.nextJobId(), content))
                      .map(task -> jobService.parseXLS(task.getFirst(), task.getSecond(), -1, -1))
                      .map(CompletableFuture::join)     // ждем окончания обработки
                      .collect(Collectors.toList());

        assertThat(actualJobs)
                .describedAs("JobService.parseXLS()")
                .usingElementComparator(TestHelper.JOB_COMPARATOR__BY_HASH_WITH_IGNORE_JPA_FIELDS_AND_ID)
                .containsOnlyElementsOf(expectJobs);
    }

}