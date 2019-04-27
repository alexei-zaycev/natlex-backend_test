package ru.net.avz.test.natlex_backend_test.service;

import javax.annotation.Nonnull;

/**
 * Ошибка парсинга исходного XLS-файла
 *
 * @author Zaitsev Alexei (aka AZ) / alexei.zaycev@ya.ru / alexei.zaycev@gmail.com
 */
public class JobParsingException
        extends RuntimeException {

    public JobParsingException() {

        super();
    }

    public JobParsingException(
            @Nonnull String message) {

        super(message);
    }

    public JobParsingException(
            @Nonnull String message,
            @Nonnull Throwable cause) {

        super(message, cause);
    }

    public JobParsingException(
            @Nonnull Throwable cause) {

        super(cause);
    }

}