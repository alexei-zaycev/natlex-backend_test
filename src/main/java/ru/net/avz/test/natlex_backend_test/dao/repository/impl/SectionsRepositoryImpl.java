package ru.net.avz.test.natlex_backend_test.dao.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;
import ru.net.avz.test.natlex_backend_test.Utils;
import ru.net.avz.test.natlex_backend_test.dao.repository.SectionsRepository;
import ru.net.avz.test.natlex_backend_test.data.SectionPOJO;

import javax.annotation.Nullable;
import javax.persistence.EntityManager;

/**
 * базовая реализация {@link SectionsRepository}
 *
 * @author Zaitsev Alexei (aka AZ) / alexei.zaycev@ya.ru / alexei.zaycev@gmail.com
 */
@Primary
@Repository
public abstract class SectionsRepositoryImpl
        extends SimpleJpaRepository<SectionPOJO, Long>
        implements SectionsRepository {

    public SectionsRepositoryImpl(
            @Autowired @Nullable EntityManager em) {

        super(SectionPOJO.class, Utils.requireDI(EntityManager.class, em));
    }
    
}