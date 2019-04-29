package ru.net.avz.test.natlex_backend_test.dao.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.net.avz.test.natlex_backend_test.data.GeologicalClassPOJO;
import ru.net.avz.test.natlex_backend_test.data.JobPOJO;
import ru.net.avz.test.natlex_backend_test.data.SectionPOJO;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * интерфейс хранилища {@link JobPOJO}
 *
 * @author Zaitsev Alexei (aka AZ) / alexei.zaycev@ya.ru / alexei.zaycev@gmail.com
 */
@Repository
public interface JobsRepository
        extends CrudRepository<JobPOJO, String> {

    /**
     * @param jobId значение-образец для {@link JobPOJO#id()}
     * @param sectionName значение-образец для {@link SectionPOJO#name()}
     * @param geoClassName значение-образец для {@link GeologicalClassPOJO#name()}
     * @param geoClassCode значение-образец для {@link GeologicalClassPOJO#code()}
     * @return отфильтрованное (из <b>ВСЕХ</b> задач) множество секций
     */
    @Query("SELECT DISTINCT S " +
                "FROM SectionPOJO S " +
                "INNER JOIN JobPOJO J ON J.id = S.job " +
                "INNER JOIN GeologicalClassPOJO G ON G.section = S.id " +
                "WHERE (:jobId IS NULL OR J.id = :jobId) " +
                       "AND (:sectionName IS NULL OR S.name = :sectionName) " +
                       "AND (:geoClassName IS NULL OR G.name = :geoClassName) " +
                       "AND (:geoClassCode IS NULL OR G.code = :geoClassCode) ")
    @Nonnull List<SectionPOJO> findAllSectionsByFilters(
            @Param("jobId") @Nullable String jobId,
            @Param("sectionName") @Nullable String sectionName,
            @Param("geoClassName") @Nullable String geoClassName,
            @Param("geoClassCode") @Nullable String geoClassCode);

}