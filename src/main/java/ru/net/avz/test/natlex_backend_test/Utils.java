package ru.net.avz.test.natlex_backend_test;

import org.jetbrains.annotations.Contract;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.http.MediaType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.stream.Stream;

/**
 *
 * @author Zaitsev Alexei (aka AZ) / alexei.zaycev@ya.ru / alexei.zaycev@gmail.com
 */
public final class Utils {

    public static final Marker MARKER__DAO      = MarkerFactory.getMarker("DAO");
    public static final Marker MARKER__CORE     = MarkerFactory.getMarker("CORE");

    public final static String MEDIA_TYPE__APPLICATION_YAML__VALUE      = "application/yaml";
    public final static MediaType MEDIA_TYPE__APPLICATION_YAML          = MediaType.valueOf(MEDIA_TYPE__APPLICATION_YAML__VALUE);
    public final static String MEDIA_TYPE__APPLICATION_XLS__VALUE       = "application/vnd.ms-excel";
    public final static MediaType MEDIA_TYPE__APPLICATION_XLS           = MediaType.valueOf(MEDIA_TYPE__APPLICATION_XLS__VALUE);

    /**
     * @param instClass класс инжектируемой сущности
     * @param inst кандидат на экземпляр инжектируемой сущности
     * @return экземпляр инжектируемой сущности
     * @throws NullPointerException в случае если переданный кандидат на экземпляр инжектируемой сущности равен NULL
     */
    @Contract(value = "_,null->fail; _,!null->param2", pure = true)
    public static @Nonnull <T> T requireDI(
            @Nonnull Class<T> instClass,
            @Nullable T inst)
            throws NullPointerException {

        assert instClass != null : "<instClass> is null";

        if (inst != null) {

           return inst;

        } else {
            throw new NullPointerException(
                    String.format("inject failed: <%s>", instClass));
        }
    }

    /**
     * @param elements множество элементов
     * @return хэш последовательности элементов, игнорируя их порядок
     */
    public static <T> int elementsHashWithIgnoreOrder(
            @Nonnull Stream<T> elements) {

        assert elements != null : "<elements> is null";

        return elements
                .sequential()
                .mapToInt(Objects::hashCode)
                .sorted()
                .reduce(1, (accumulator, value) -> 31 * accumulator + Integer.hashCode(value));
    }

    private Utils() {}

}