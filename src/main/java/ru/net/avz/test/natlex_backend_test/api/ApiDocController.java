package ru.net.avz.test.natlex_backend_test.api;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.net.avz.test.natlex_backend_test.Utils;

import javax.annotation.Nonnull;
import java.io.IOException;

import static ru.net.avz.test.natlex_backend_test.config.WebSecurityConfig.ROLE__ADMIN;
import static ru.net.avz.test.natlex_backend_test.config.WebSecurityConfig.ROLE__USER;

/**
 * 
 * @author Zaitsev Alexei (aka AZ) / alexei.zaycev@ya.ru / alexei.zaycev@gmail.com
 */
@RestController
@RequestMapping(path = "/api")
public class ApiDocController {

    private static final String API_SPEC_FILENAME = "api.yaml";

    @Secured({ROLE__USER, ROLE__ADMIN})
    @GetMapping(path = "",
                produces = Utils.MEDIA_TYPE__APPLICATION_YAML__VALUE)
    public @ResponseBody @Nonnull ResponseEntity<byte[]> filterSections()
            throws IOException {

        byte[] bytes = StreamUtils.copyToByteArray(
                            ApiDocController.class.getResourceAsStream(API_SPEC_FILENAME));

        return ResponseEntity.ok()
                             .contentType(Utils.MEDIA_TYPE__APPLICATION_YAML)
                             .contentLength(bytes.length)
                             .header(HttpHeaders.CONTENT_DISPOSITION, "filename=\"" + API_SPEC_FILENAME + "\"")
                             .body(bytes);
    }

}