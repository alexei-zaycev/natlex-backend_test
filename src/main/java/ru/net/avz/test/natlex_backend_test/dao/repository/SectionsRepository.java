package ru.net.avz.test.natlex_backend_test.dao.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.net.avz.test.natlex_backend_test.data.SectionPOJO;

/**
 * интерфейс хранилища {@link SectionPOJO}
 *
 * @author Zaitsev Alexei (aka AZ) / alexei.zaycev@ya.ru / alexei.zaycev@gmail.com
 */
@Repository
public interface SectionsRepository
        extends CrudRepository<SectionPOJO, Long> {

}