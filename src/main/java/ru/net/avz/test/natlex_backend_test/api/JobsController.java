package ru.net.avz.test.natlex_backend_test.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.net.avz.test.natlex_backend_test.Utils;
import ru.net.avz.test.natlex_backend_test.dao.JobDAO;
import ru.net.avz.test.natlex_backend_test.data.JobPOJO;
import ru.net.avz.test.natlex_backend_test.data.SectionPOJO;
import ru.net.avz.test.natlex_backend_test.service.JobParsingException;
import ru.net.avz.test.natlex_backend_test.service.JobService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static ru.net.avz.test.natlex_backend_test.config.WebSecurityConfig.ROLE__ADMIN;
import static ru.net.avz.test.natlex_backend_test.config.WebSecurityConfig.ROLE__USER;

/**
 * 
 * @author Zaitsev Alexei (aka AZ) / alexei.zaycev@ya.ru / alexei.zaycev@gmail.com
 */
@RestController
@RequestMapping(path = "/api/jobs")
public class JobsController {

    @Nonnull private final JobDAO jobDAO;
    @Nonnull private final JobService jobService;

    public JobsController(
            @Autowired @Nullable JobDAO jobDAO,
            @Autowired @Nullable JobService jobService) {

        this.jobDAO = Utils.requireDI(JobDAO.class, jobDAO);
        this.jobService = Utils.requireDI(JobService.class, jobService);
    }

    @Secured({ROLE__USER, ROLE__ADMIN})
    @GetMapping(path = "",
                produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody @Nonnull CompletableFuture<Stream<String>> listAllJobs() {

        return jobDAO.getAllJobs()
                     .thenApply(jobs -> jobs.map(JobPOJO::id));
    }

    @Secured({ROLE__USER, ROLE__ADMIN})
    @GetMapping(path = "/{jobId}",
                produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody @Nonnull ResponseEntity<JobPOJO> getJob(
            @PathVariable("jobId") @Nonnull String jobId) {

        assert jobId != null : "<jobId> is null";

        return ResponseEntity.of(
                    jobDAO.getJob(jobId));
    }

    @Secured(ROLE__ADMIN)
    @PostMapping(path = "",
                 consumes = { MediaType.MULTIPART_FORM_DATA_VALUE, Utils.MEDIA_TYPE__APPLICATION_XLS__VALUE },
                 produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody @Nonnull ResponseEntity<Map<String, String>> addJob(
            @RequestParam(name = "file", required = false) @Nullable MultipartFile fileOnPart,      // FORM.<file>
            @Payload @Nullable InputStream fileOnBody)                                              // PAYLOAD
            throws IOException {

        String jobId = jobService.nextJobId();

        if (fileOnPart != null) {

            if (!Utils.MEDIA_TYPE__APPLICATION_XLS.equals(MediaType.valueOf(fileOnPart.getContentType())))
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build();

            jobService.parseXLS(jobId, fileOnPart.getInputStream(), -1, -1);

        } else if (fileOnBody != null) {

            jobService.parseXLS(jobId, fileOnBody, -1, -1);

        } else {
            throw new JobParsingException("request is empty");
        }

        return ResponseEntity.ok(
                    Map.of(SectionPOJO.DB__FK__JOB, jobId));
    }

}