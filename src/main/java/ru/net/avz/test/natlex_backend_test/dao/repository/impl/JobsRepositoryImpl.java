package ru.net.avz.test.natlex_backend_test.dao.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;
import ru.net.avz.test.natlex_backend_test.Utils;
import ru.net.avz.test.natlex_backend_test.dao.repository.JobsRepository;
import ru.net.avz.test.natlex_backend_test.data.JobPOJO;

import javax.annotation.Nullable;
import javax.persistence.EntityManager;

/**
 * базовая реализация {@link JobsRepository}
 *
 * @author Zaitsev Alexei (aka AZ) / alexei.zaycev@ya.ru / alexei.zaycev@gmail.com
 */
@Primary
@Repository
public abstract class JobsRepositoryImpl
        extends SimpleJpaRepository<JobPOJO, String>
        implements JobsRepository {

    public JobsRepositoryImpl(
            @Autowired @Nullable EntityManager em) {

        super(JobPOJO.class, Utils.requireDI(EntityManager.class, em));
    }
    
}