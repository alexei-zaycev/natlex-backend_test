package ru.net.avz.test.natlex_backend_test.dao.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.net.avz.test.natlex_backend_test.data.SectionPOJO;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * интерфейс хранилища {@link SectionPOJO}
 *
 * @author Zaitsev Alexei (aka AZ) / alexei.zaycev@ya.ru / alexei.zaycev@gmail.com
 */
@Repository
public interface SectionsRepository
        extends CrudRepository<SectionPOJO, Long> {

    /**
     * @param name значение-образец для {@link SectionPOJO#name()}
     * @return отфильтрованное множество секций
     */
    @Query("SELECT S FROM SectionPOJO S WHERE S.name = :name")
    List<SectionPOJO> findByName(
            @Param("name") @Nonnull String name);

}