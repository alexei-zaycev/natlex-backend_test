package ru.net.avz.test.natlex_backend_test.dao.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;
import ru.net.avz.test.natlex_backend_test.Utils;
import ru.net.avz.test.natlex_backend_test.dao.repository.GeologicalClassesRepository;
import ru.net.avz.test.natlex_backend_test.data.GeologicalClassPOJO;

import javax.annotation.Nullable;
import javax.persistence.EntityManager;

/**
 * базовая реализация {@link GeologicalClassesRepository}
 *
 * @author Zaitsev Alexei (aka AZ) / alexei.zaycev@ya.ru / alexei.zaycev@gmail.com
 */
@Primary
@Repository
public class GeologicalClassesRepositoryImpl
        extends SimpleJpaRepository<GeologicalClassPOJO, Long>
        implements GeologicalClassesRepository {

    public GeologicalClassesRepositoryImpl(
            @Autowired @Nullable EntityManager em) {

        super(GeologicalClassPOJO.class, Utils.requireDI(EntityManager.class, em));
    }
    
}