package ru.net.avz.test.natlex_backend_test.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.core.style.ToStringCreator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.persistence.*;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Zaitsev Alexei (aka AZ) / alexei.zaycev@ya.ru / alexei.zaycev@gmail.com
 */
@Immutable
@Entity
//@Transactional
@Table(name = JobPOJO.DB__TABLE)
public class JobPOJO {

    static final String DB__TABLE                   = "jobs";

    static final String DB__PK__ID                  = "id";
    static final String DB__KEY__SECTIONS           = "sections";

    public static final String JSON__KEY__ID        = DB__PK__ID;
    public static final String JSON__KEY__SECTIONS  = DB__KEY__SECTIONS;

    @Id
    @Column(name = DB__PK__ID, unique = true, nullable = false)
    @Nullable private volatile String id;

    @OneToMany(targetEntity = SectionPOJO.class, mappedBy = SectionPOJO.DB__FK__JOB, cascade = CascadeType.ALL, orphanRemoval = true)
    @Column(name = DB__KEY__SECTIONS, nullable = false)
    @Nullable private volatile List<SectionPOJO> sections;

    protected JobPOJO() {}

    @JsonCreator
    public JobPOJO(
            @JsonProperty(JSON__KEY__ID) @Nonnull String id,
            @JsonProperty(JSON__KEY__SECTIONS) @Nonnull List<SectionPOJO> sections) {

        assert id != null : "<id> is null";
        assert sections != null : "<sections> is null";

        this.id = id;
        this.sections = List.copyOf(sections);

        sections.forEach(section -> {

            assert section.job == null;

            section.job = JobPOJO.this;
        });
    }

    @JsonProperty(JSON__KEY__ID)
    public @Nonnull String id() {
        return Objects.requireNonNull(id);
    }

    @JsonProperty(JSON__KEY__SECTIONS)
    public @Nonnull List<SectionPOJO> sections() {
        return Objects.requireNonNull(sections);
    }

    @Override
    public @Nonnull String toString() {

        return new ToStringCreator(this)
                .append(JSON__KEY__ID, id())
                .append(JSON__KEY__SECTIONS, sections())
                .toString();
    }

}