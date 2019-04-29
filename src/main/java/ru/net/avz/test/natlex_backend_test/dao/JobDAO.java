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
     * @param jobId значение-образец для {@link JobPOJO#id()}
     * @param sectionName значение-образец для {@link SectionPOJO#name()}
     * @param geoClassName значение-образец для {@link GeologicalClassPOJO#name()}
     * @param geoClassCode значение-образец для {@link GeologicalClassPOJO#code()}
     * @return отфильтрованное (из <b>ВСЕХ</b> задач) множество секций
     */
    @Async
    public @Nonnull CompletableFuture<Stream<SectionPOJO>> findAllSectionsByFilters(
            @Nullable String jobId,
            @Nullable String sectionName,
            @Nullable String geoClassName,
            @Nullable String geoClassCode) {

        return CompletableFuture.completedFuture(
                jobsRepository.findAllSectionsByFilters(jobId, sectionName, geoClassName, geoClassCode)
                              .stream());
    }

    /**
     * @param jobIdPredicate условие фильтрации для {@link JobPOJO#id()}
     * @param sectionNamePredicate условие фильтрации для {@link SectionPOJO#name()}
     * @param geoClassNamePredicate условие фильтрации для {@link GeologicalClassPOJO#name()}
     * @param geoClassCodePredicate условие фильтрации для {@link GeologicalClassPOJO#code()}
     * @return отфильтрованное (из <b>ВСЕХ</b> задач) множество секций
     */
    @Async
    public @Nonnull CompletableFuture<Stream<SectionPOJO>> findAllSectionsByFilters(
            @Nullable Predicate<String> jobIdPredicate,
            @Nullable Predicate<String> sectionNamePredicate,
            @Nullable Predicate<String> geoClassNamePredicate,
            @Nullable Predicate<String> geoClassCodePredicate) {

        @Nullable Stream<SectionPOJO> sections = null;

        if (jobIdPredicate != null) {

            sections = StreamSupport.stream(jobsRepository.findAll().spliterator(), true)
                                    .filter(job -> jobIdPredicate.test(job.id()))
                                    .flatMap(job -> job.sections().parallelStream());
        }

        if (sectionNamePredicate != null) {

            if (sections == null)
                sections = StreamSupport.stream(sectionsRepository.findAll().spliterator(), true);

            sections = sections.filter(section -> sectionNamePredicate.test(section.name()));
        }

        if (geoClassNamePredicate != null || geoClassCodePredicate != null) {

            if (sections == null)
                sections = StreamSupport.stream(sectionsRepository.findAll().spliterator(), true);

            sections = sections.filter(section -> section.geologicalClasses().parallelStream()
                                                         .anyMatch(geoClass -> (geoClassNamePredicate == null || geoClassNamePredicate.test(geoClass.name()))
                                                                                && (geoClassCodePredicate == null || geoClassCodePredicate.test(geoClass.code()))));
        }

        assert sections != null;

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