package ru.isin.security.service.properties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.lang.NonNull;

/**
 * Проперти класс, поля которого нужны для задания сервера на котором запущено приложение.
 *
 * @author Krylov Sergey (02.04.2021)
 */
@Getter
@ConstructorBinding
@ConfigurationProperties(prefix = "isin.system")
public class SystemProperties {
    @NonNull
    private final String host;
    private final String port;

    /**
     * Проперти используемые для приложения.
     *
     * @param host хост приложения.
     * @param port порт приложения.
     */
    public SystemProperties(@NonNull String host, String port) {
        this.host = host;
        if (port == null) {
            this.port = "";
        } else {
            this.port = ":" + port;
        }
    }
}
