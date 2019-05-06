package ru.net.avz.test.natlex_backend_test.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = SectionPOJO.DB__TABLE,
       indexes = @Index(columnList = SectionPOJO.DB__KEY__NAME))
public class SectionPOJO {

    static final String DB__TABLE                               = "sections";

    static final String DB__PK__ID                              = "id";
    static final String DB__KEY__NAME                           = "name";
    static final String DB__KEY__GEO_CLASSES                    = "geo_classes";
    private static final String DB__KEY__JOB                    = "job_id";
    /** @see #job */ public static final String DB__FK__JOB     = "job";

    public static final String JSON__KEY__NAME                  = DB__KEY__NAME;
    public static final String JSON__KEY__GEO_CLASSES           = "geologicalClasses";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = DB__PK__ID, unique = true, nullable = false)
    @Nullable private volatile Long id;

    @Column(name = DB__KEY__NAME, nullable = false)
    @Nullable private volatile String name;

    @OneToMany(targetEntity = GeologicalClassPOJO.class, mappedBy = GeologicalClassPOJO.DB__FK__SECTION, cascade = CascadeType.ALL, orphanRemoval = true)
    @Column(name = DB__KEY__GEO_CLASSES, nullable = false)
    @Nullable private volatile List<GeologicalClassPOJO> geologicalClasses;

    @ManyToOne(optional = false)
    @JoinColumn(name = DB__KEY__JOB, nullable = false)
    @Nullable protected volatile JobPOJO job;

    protected SectionPOJO() {}

    @JsonCreator
    public SectionPOJO(
            @JsonProperty(JSON__KEY__NAME) @Nonnull String name,
            @JsonProperty(JSON__KEY__GEO_CLASSES) @Nonnull List<GeologicalClassPOJO> geologicalClasses) {

        assert name != null : "<name> is null";
        assert geologicalClasses != null : "<geologicalClasses> is null";

        this.name = name;
        this.geologicalClasses = List.copyOf(geologicalClasses);

        geologicalClasses.forEach(geologicalClass -> {

            assert geologicalClass.section == null;

            geologicalClass.section = SectionPOJO.this;
        });
    }

    @JsonIgnore
    protected @Nullable Long id() {
        return id;
    }

    @JsonProperty(JSON__KEY__NAME)
    public @Nonnull String name() {
        return Objects.requireNonNull(name);
    }

    @JsonProperty(JSON__KEY__GEO_CLASSES)
    public @Nonnull List<GeologicalClassPOJO> geologicalClasses() {
        return Objects.requireNonNull(geologicalClasses);
    }

    @JsonIgnore
    protected @Nullable JobPOJO job() {
        return job;
    }

    @Override
    public @Nonnull String toString() {

        return new ToStringCreator(this)
                .append(JSON__KEY__NAME, name())
                .append(JSON__KEY__GEO_CLASSES, geologicalClasses())
                .toString();
    }

}