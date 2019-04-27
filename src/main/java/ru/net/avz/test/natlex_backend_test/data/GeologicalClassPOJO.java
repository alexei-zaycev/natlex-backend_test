package ru.net.avz.test.natlex_backend_test.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.core.style.ToStringCreator;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.persistence.*;

/**
 * 
 * @author Zaitsev Alexei (aka AZ) / alexei.zaycev@ya.ru / alexei.zaycev@gmail.com
 */
@Immutable
@Entity
//@Transactional
@Table(name = "geo_classes")
public class GeologicalClassPOJO {

    static final String DB__PK__ID                                  = "id";
    static final String DB__KEY__NAME                               = "name";
    static final String DB__KEY__CODE                               = "code";
    private static final String DB__KEY__SECTION                    = "section_id";
    /** @see #section */ public static final String DB__FK__SECTION = "section";

    public static final String JSON__KEY__NAME                      = DB__KEY__NAME;
    public static final String JSON__KEY__CODE                      = DB__KEY__CODE;

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = DB__PK__ID, unique = true, nullable = false)
    @Nonnull private volatile Long id;

    @Column(name = DB__KEY__NAME, nullable = false)
    @Nonnull private volatile String name;

    @Column(name = DB__KEY__CODE, nullable = false)
    @Nonnull private volatile String code;

    @ManyToOne(optional = false)
    @JoinColumn(name = DB__KEY__SECTION, nullable = false)
    @Nonnull volatile SectionPOJO section;

    GeologicalClassPOJO() {
    }

    public GeologicalClassPOJO(
            @Nonnull String name,
            @Nonnull String code) {

        assert name != null : "<name> is null";
        assert code != null : "<code> is null";

        this.name = name;
        this.code = code;
    }

    @JsonIgnore
    public @Nonnull Long id() {
        return id;
    }

    @JsonProperty(JSON__KEY__NAME)
    public @Nonnull String name() {
        return name;
    }

    @JsonProperty(JSON__KEY__CODE)
    public @Nonnull String code() {
        return code;
    }

    @JsonIgnore
    public @Nonnull SectionPOJO section() {
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