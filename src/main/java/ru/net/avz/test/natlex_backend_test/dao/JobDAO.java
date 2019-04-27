package ru.net.avz.test.natlex_backend_test.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import ru.net.avz.test.natlex_backend_test.Utils;
import ru.net.avz.test.natlex_backend_test.dao.repository.GeologicalClassesRepository;
import ru.net.avz.test.natlex_backend_test.dao.repository.JobsRepository;
import ru.net.avz.test.natlex_backend_test.dao.repository.SectionsRepository;
import ru.net.avz.test.natlex_backend_test.data.GeologicalClassPOJO;
import ru.net.avz.test.natlex_backend_test.data.JobPOJO;
import ru.net.avz.test.natlex_backend_test.data.SectionPOJO;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 *
 * @author Zaitsev Alexei (aka AZ) / alexei.zaycev@ya.ru / alexei.zaycev@gmail.com
 */
@Repository
public class JobDAO {

    private final Logger LOG                                = LoggerFactory.getLogger(this.getClass());

    private static final String MSG__ADD                    = "ADD {}";

    @Nonnull private final JobsRepository jobsRepository;
    @Nonnull private final SectionsRepository sectionsRepository;
    @Nonnull private final GeologicalClassesRepository geologicalClassesRepository;

    public JobDAO(
            @Autowired @Nullable JobsRepository jobsRepository,
            @Autowired @Nullable SectionsRepository sectionsRepository,
            @Autowired @Nullable GeologicalClassesRepository geologicalClassesRepository) {

        this.jobsRepository = Utils.requireDI(JobsRepository.class, jobsRepository);
        this.sectionsRepository = Utils.requireDI(SectionsRepository.class, sectionsRepository);
        this.geologicalClassesRepository = Utils.requireDI(GeologicalClassesRepository.class, geologicalClassesRepository);
    }

    /**
     * @return множество всех задач
     */
    @Async
    public @Nonnull CompletableFuture<Stream<JobPOJO>> getAllJobs() {

        return CompletableFuture.completedFuture(
                StreamSupport.stream(jobsRepository.findAll().spliterator(), true));
    }

    /**
     * @param jobId уникальный идентификатор задачи (см. {@link JobPOJO#id()})
     * @return инстанс задачи или EMPTY если задача с указанным идентификатор отсутствует
     */
    public @Nonnull Optional<JobPOJO> getJob(
            @Nonnull String jobId) {

        assert jobId != null : "<jobId> is null";

        return jobsRepository.findById(jobId);
    }

    /**
     * @param sectionNamePredicate условие фильтрации для {@link SectionPOJO#name()}
     * @param geoClassNamePredicate условие фильтрации для {@link GeologicalClassPOJO#name()}
     * @param geoClassCodePredicate условие фильтрации для {@link GeologicalClassPOJO#code()}
     * @return отфильтрованное (из <b>ВСЕХ</b> задач) множество секций
     */
    @Async
    public @Nonnull CompletableFuture<Stream<SectionPOJO>> filterSectionsForAllJobs(
            @Nullable Predicate<String> sectionNamePredicate,
            @Nullable Predicate<String> geoClassNamePredicate,
            @Nullable Predicate<String> geoClassCodePredicate) {

        @Nullable Stream<SectionPOJO> sections = null;

        if (sectionNamePredicate != null) {

            sections = StreamSupport.stream(sectionsRepository.findAll().spliterator(), true)
                                    .filter(section -> sectionNamePredicate.test(section.name()));
        }

        if (geoClassNamePredicate != null || geoClassCodePredicate != null) {

            Stream<GeologicalClassPOJO> geologicalClasses =
                    sections != null
                            ? sections.flatMap(section -> section.geologicalClasses().parallelStream())
                            : StreamSupport.stream(geologicalClassesRepository.findAll().spliterator(), true);

            sections = geologicalClasses
                            .filter(geoClass -> geoClassNamePredicate == null || geoClassNamePredicate.test(geoClass.name()))
                            .filter(geoClass -> geoClassCodePredicate == null || geoClassCodePredicate.test(geoClass.code()))
                            .map(GeologicalClassPOJO::section)
                            .collect(Collectors.groupingBy(SectionPOJO::id))    // надо избавиться от дублей из-за 1:N
                            .values()
                            .stream()
                            .map(list -> list.get(0));
        }

        if (sections == null) {
            sections = StreamSupport.stream(sectionsRepository.findAll().spliterator(), true);
        }

        return CompletableFuture.completedFuture(sections);
    }

    /**
     *
     * @param job инстанс задачи
     */
    @Async
    public @Nonnull CompletableFuture<JobPOJO> addJob(
            @Nonnull JobPOJO job) {

        assert job != null : "<job> is null";

        jobsRepository.save(job);

        LOG.info(
                Utils.MARKER__DAO,
                MSG__ADD,
                job);

        return CompletableFuture.completedFuture(job);
    }

}