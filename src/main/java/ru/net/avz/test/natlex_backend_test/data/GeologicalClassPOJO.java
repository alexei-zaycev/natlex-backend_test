package ru.net.avz.test.natlex_backend_test.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.core.style.ToStringCreator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.persistence.*;
import java.util.Objects;

/**
 * 
 * @author Zaitsev Alexei (aka AZ) / alexei.zaycev@ya.ru / alexei.zaycev@gmail.com
 */
@Immutable
@Entity
//@Transactional
@Table(name = GeologicalClassPOJO.DB__TABLE,
       indexes = { @Index(columnList = GeologicalClassPOJO.DB__KEY__NAME),
                   @Index(columnList = GeologicalClassPOJO.JSON__KEY__CODE) })
public class GeologicalClassPOJO {

    static final String DB__TABLE                                   = "geo_classes";

    static final String DB__PK__ID                                  = "id";
    static final String DB__KEY__NAME                               = "name";
    static final String DB__KEY__CODE                               = "code";
    private static final String DB__KEY__SECTION                    = "section_id";
    /** @see #section */ public static final String DB__FK__SECTION = "section";

    public static final String JSON__KEY__NAME                      = DB__KEY__NAME;
    public static final String JSON__KEY__CODE                      = DB__KEY__CODE;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = DB__PK__ID, unique = true, nullable = false)
    @Nullable private volatile Long id;

    @Column(name = DB__KEY__NAME, nullable = false)
    @Nullable private volatile String name;

    @Column(name = DB__KEY__CODE, nullable = false)
    @Nullable private volatile String code;

    @ManyToOne(optional = false)
    @JoinColumn(name = DB__KEY__SECTION, nullable = false)
    @Nullable protected volatile SectionPOJO section;

    protected GeologicalClassPOJO() {}

    @JsonCreator
    public GeologicalClassPOJO(
            @JsonProperty(JSON__KEY__NAME) @Nonnull String name,
            @JsonProperty(DB__KEY__CODE) @Nonnull String code) {

        assert name != null : "<name> is null";
        assert code != null : "<code> is null";

        this.name = name;
        this.code = code;
    }

    @JsonIgnore
    protected @Nullable Long id() {
        return id;
    }

    @JsonProperty(JSON__KEY__NAME)
    public @Nonnull String name() {
        return Objects.requireNonNull(name);
    }

    @JsonProperty(JSON__KEY__CODE)
    public @Nonnull String code() {
        return Objects.requireNonNull(code);
    }

    @JsonIgnore
    protected @Nullable SectionPOJO section() {
        return section;
    }

    @Override
    public @Nonnull String toString() {

        return new ToStringCreator(this)
                .append(JSON__KEY__NAME, name())
                .append(JSON__KEY__CODE, code())
                .toString();
    }

}