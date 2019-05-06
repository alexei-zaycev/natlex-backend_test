package ru.net.avz.test.natlex_backend_test;

import org.jetbrains.annotations.Contract;
import org.springframework.data.util.Pair;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import ru.net.avz.test.natlex_backend_test.data.GeologicalClassPOJO;
import ru.net.avz.test.natlex_backend_test.data.JobPOJO;
import ru.net.avz.test.natlex_backend_test.data.SectionPOJO;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;

/**
 *
 * @author Zaitsev Alexei (aka AZ) / alexei.zaycev@ya.ru / alexei.zaycev@gmail.com
 */
public class TestHelper {

    @Contract("->new")
    public static @Nonnull List<JobPOJO> generateTestJobs() {

        return List.of(
                new JobPOJO(
                        UUID.randomUUID().toString(),
                        List.of(
                                new SectionPOJO(
                                        "Section 1.111",
                                        List.of(
                                                new GeologicalClassPOJO("Geo Class 11.111", "GC11.111"),
                                                new GeologicalClassPOJO("Geo Class 12.111", "GC12.111"))),
                                new SectionPOJO(
                                        "Section 2.111",
                                        List.of(
                                                new GeologicalClassPOJO("Geo Class 21.111", "GC21.111"))),
                                new SectionPOJO(
                                        "Section 3.111",
                                        List.of(
                                                new GeologicalClassPOJO("Geo Class 31.111", "GC31.111"))))),
                new JobPOJO(
                        UUID.randomUUID().toString(),
                        List.of(
                                new SectionPOJO(
                                        "Section 1.222",
                                        List.of(
                                                new GeologicalClassPOJO("Geo Class 11.222", "GC11.222"),
                                                new GeologicalClassPOJO("Geo Class 12.222", "GC12.222"))),
                                new SectionPOJO(
                                        "Section 2.222",
                                        List.of(
                                                new GeologicalClassPOJO("Geo Class 21.222", "GC21.222"))),
                                new SectionPOJO(
                                        "Section 3.222",
                                        List.of(
                                                new GeologicalClassPOJO("Geo Class 31.222", "GC31.222"))))),
                new JobPOJO(
                        UUID.randomUUID().toString(),
                        List.of(
                                new SectionPOJO(
                                        "Section 1.333",
                                        List.of(
                                                new GeologicalClassPOJO("Geo Class 11.333", "GC11.333"),
                                                new GeologicalClassPOJO("Geo Class 12.333", "GC12.333"))),
                                new SectionPOJO(
                                        "Section 2.333",
                                        List.of(
                                                new GeologicalClassPOJO("Geo Class 21.333", "GC21.333"))),
                                new SectionPOJO(
                                        "Section 3.333",
                                        List.of(
                                                new GeologicalClassPOJO("Geo Class 31.333", "GC31.333"))))));
    }

    public static final Comparator<JobPOJO> JOB_COMPARATOR__BY_HASH_WITH_IGNORE_JPA_FIELDS =
            Comparator.comparingInt(job -> Utils.elementsHashWithIgnoreOrder(
                    job.sections()
                            .stream()
                            .flatMap(section -> section.geologicalClasses()
                                                        .stream()
                                                        .map(geoClass -> Objects.hash(
                                                                                job.id(),
                                                                                section.name(),
                                                                                geoClass.name(),
                                                                                geoClass.code())))));

    public static final Comparator<JobPOJO> JOB_COMPARATOR__BY_HASH_WITH_IGNORE_JPA_FIELDS_AND_ID =
            Comparator.comparingInt(job -> Utils.elementsHashWithIgnoreOrder(
                    job.sections()
                            .stream()
                            .flatMap(section -> section.geologicalClasses()
                                                        .stream()
                                                        .map(geoClass -> Objects.hash(
                                                                                section.name(),
                                                                                geoClass.name(),
                                                                                geoClass.code())))));

    public static final Comparator<SectionPOJO> SECTION_COMPARATOR__BY_HASH_WITH_IGNORE_JPA_FIELDS =
            Comparator.comparingInt(section -> Utils.elementsHashWithIgnoreOrder(
                    section.geologicalClasses()
                            .stream()
                            .map(geoClass -> Objects.hash(
                                                    section.name(),
                                                    geoClass.name(),
                                                    geoClass.code()))));

    public static final Comparator<GeologicalClassPOJO> GEO_CLASS_COMPARATOR__BY_HASH_WITH_IGNORE_JPA_FIELDS =
            Comparator.comparingInt(geoClass -> Objects.hash(
                                                    geoClass.name(),
                                                    geoClass.code()));

    public static final class SectionsFilter {

        @Nullable public final String jobId;
        @Nullable public final String sectionName;
        @Nullable public final String geoClassName;
        @Nullable public final String geoClassCode;

        public SectionsFilter(
                @Nullable String jobId,
                @Nullable String sectionName,
                @Nullable String geoClassName,
                @Nullable String geoClassCode) {

            this.jobId = jobId;
            this.sectionName = sectionName;
            this.geoClassName = geoClassName;
            this.geoClassCode = geoClassCode;
        }
    }

    public static void runJobsTest(
            @Nonnull List<JobPOJO> expectJobs,
            @Nonnull Comparator<JobPOJO> jobComparator,
            @Nonnull Comparator<SectionPOJO> sectionComparator,
            @Nullable Pair<Function<JobPOJO, String>, Function<JobPOJO, JobPOJO>> addJob,
            @Nonnull Pair<String, Supplier<? extends List<JobPOJO>>> getAllJobs,
            @Nonnull Pair<Function<String, String>, Function<String, Optional<JobPOJO>>> getJobById,
            @Nonnull Pair<Function<SectionsFilter, String>, Function<SectionsFilter, ? extends List<SectionPOJO>>> findAllSectionsByFilters) {

        assert expectJobs != null : "<expectJobs> is null";
        assert jobComparator != null : "<jobComparator> is null";
        assert sectionComparator != null : "<sectionComparator> is null";
        assert getAllJobs != null : "<getAllJobs> is null";
        assert getJobById != null : "<getJobById> is null";
        assert findAllSectionsByFilters != null : "<findAllSectionsByFilters> is null";

        List<JobPOJO> addedJobs = new ArrayList<>(expectJobs.size());

        if (addJob != null) {
            expectJobs.forEach(job -> {

                final JobPOJO addedJob = addJob.getSecond().apply(job);

                assertThat(addedJob)
                        .describedAs(addJob.getFirst().apply(job))
                        .usingComparator(jobComparator)
                        .isEqualTo(job);

                addedJobs.add(addedJob);
            });
        } else {
            addedJobs.addAll(expectJobs);
        }

        final List<JobPOJO> actualJobs = getAllJobs.getSecond().get();

        assertThat(actualJobs)
                .describedAs(getAllJobs.getFirst())
                .usingElementComparator(jobComparator)
                .containsOnlyElementsOf(addedJobs);

        addedJobs.forEach(expectJob -> {

            final Optional<JobPOJO> actualJob = getJobById.getSecond().apply(expectJob.id());

            assertThat(actualJob)
                    .describedAs(getJobById.getFirst().apply(expectJob.id()))
                    .get()
                    .usingComparator(jobComparator)
                    .isEqualTo(expectJob);

            expectJob.sections().forEach(expectSection -> {

                final SectionsFilter actualSectionsFilter = new SectionsFilter(expectJob.id(), expectSection.name(), null, null);
                final List<SectionPOJO> actualSections = findAllSectionsByFilters.getSecond().apply(actualSectionsFilter);

                assertThat(actualSections)
                        .describedAs(findAllSectionsByFilters.getFirst().apply(actualSectionsFilter))
                        .usingElementComparator(sectionComparator)
                        .containsOnly(expectSection);

                expectSection.geologicalClasses().forEach(expectGeologicalClass -> {

                    final SectionsFilter filteredByNameFilter = new SectionsFilter(expectJob.id(), null, expectGeologicalClass.name(), null);
                    final List<SectionPOJO> filtered_byName = findAllSectionsByFilters.getSecond().apply(filteredByNameFilter);

                    assertThat(filtered_byName)
                            .describedAs(findAllSectionsByFilters.getFirst().apply(filteredByNameFilter))
                            .usingElementComparator(sectionComparator)
                            .containsOnly(expectSection);

                    final SectionsFilter filteredByCodeFilter = new SectionsFilter(expectJob.id(), null, null, expectGeologicalClass.code());
                    final List<SectionPOJO> filtered_byCode = findAllSectionsByFilters.getSecond().apply(filteredByCodeFilter);;

                    assertThat(filtered_byCode)
                            .describedAs(findAllSectionsByFilters.getFirst().apply(filteredByCodeFilter))
                            .usingElementComparator(sectionComparator)
                            .containsOnly(expectSection);
                });

            });
        });
    }

    /**
     * обвязка к {@link MockMvc#perform(RequestBuilder)}, совместимая как с синхронными так и с асинхронными ресурсами
     *
     * @param isAsync признак асинхронного режима работы ресурса
     * @param req запрос
     * @return ответ
     */
    public static @Nonnull ResultActions mockMvcPerform(
            @Nonnull MockMvc mockMvc,
            boolean isAsync,
            @Nonnull RequestBuilder req)
            throws Exception {

        assert mockMvc != null : "<mockMvc> is null";
        assert req != null : "<req> is null";

        if (isAsync) {

            MvcResult async = mockMvc.perform(req).andReturn();

            return mockMvc.perform(asyncDispatch(async));

        } else {
            return mockMvc.perform(req);
        }
    }

}
