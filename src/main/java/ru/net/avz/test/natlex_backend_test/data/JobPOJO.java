package ru.net.avz.test.natlex_backend_test.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.core.style.ToStringCreator;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.persistence.*;
import java.util.List;

/**
 *
 * @author Zaitsev Alexei (aka AZ) / alexei.zaycev@ya.ru / alexei.zaycev@gmail.com
 */
@Immutable
@Entity
//@Transactional
@Table(name = "jobs")
public class JobPOJO {

    static final String DB__PK__ID                  = "id";
    static final String DB__KEY__SECTIONS           = "sections";

    public static final String JSON__KEY__ID        = DB__PK__ID;
    public static final String JSON__KEY__SECTIONS  = DB__KEY__SECTIONS;

    @Id
    @Column(name = DB__PK__ID, unique = true, nullable = false)
    @Nonnull private volatile String id;

    @OneToMany(targetEntity = SectionPOJO.class, mappedBy = SectionPOJO.DB__FK__JOB, cascade = CascadeType.ALL, orphanRemoval = true)
    @Column(name = DB__KEY__SECTIONS, nullable = false)
    @Nonnull private volatile List<SectionPOJO> sections;

    JobPOJO() {
    }

    public JobPOJO(
            @Nonnull String id,
            @Nonnull List<SectionPOJO> sections) {

        assert id != null : "<id> is null";
        assert sections != null : "<sections> is null";

        this.id = id;
        this.sections = List.copyOf(sections);

        this.sections.forEach(section -> section.job = JobPOJO.this);
    }

    @JsonProperty(JSON__KEY__ID)
    public @Nonnull String id() {
        return id;
    }

    @JsonProperty(JSON__KEY__SECTIONS)
    public @Nonnull List<SectionPOJO> sections() {
        return sections;
    }

    @Override
    public @Nonnull String toString() {

        return new ToStringCreator(this)
                .append(JSON__KEY__ID, id())
                .append(JSON__KEY__SECTIONS, sections())
                .toString();
    }

}