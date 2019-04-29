package ru.net.avz.test.natlex_backend_test.dao.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.net.avz.test.natlex_backend_test.data.GeologicalClassPOJO;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * интерфейс хранилища {@link GeologicalClassPOJO}
 *
 * @author Zaitsev Alexei (aka AZ) / alexei.zaycev@ya.ru / alexei.zaycev@gmail.com
 */
@Repository
public interface GeologicalClassesRepository
        extends CrudRepository<GeologicalClassPOJO, Long> {

    /**
     * @param name значение-образец для {@link GeologicalClassPOJO#name()}
     * @return отфильтрованное множество гео-классов
     */
    @Query("SELECT G FROM GeologicalClassPOJO G WHERE G.name = :name")
    List<GeologicalClassPOJO> findByName(
            @Param("name") @Nonnull String name);

    /**
     * @param code значение-образец для {@link GeologicalClassPOJO#code()}
     * @return отфильтрованное множество гео-классов
     */
    @Query("SELECT G FROM GeologicalClassPOJO G WHERE G.code = :code")
    List<GeologicalClassPOJO> findByCode(
            @Param("code") @Nonnull String code);

    /**
     * @param name значение-образец для {@link GeologicalClassPOJO#name()}
     * @param code значение-образец для {@link GeologicalClassPOJO#code()}
     * @return отфильтрованное множество гео-классов
     */
    @Query("SELECT G FROM GeologicalClassPOJO G WHERE G.name = :name AND G.code = :code")
    List<GeologicalClassPOJO> findByNameAndCode(
            @Param("name") @Nonnull String name,
            @Param("code") @Nonnull String code);

}