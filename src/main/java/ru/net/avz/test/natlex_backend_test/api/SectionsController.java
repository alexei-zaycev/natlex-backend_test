package ru.net.avz.test.natlex_backend_test.api;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.net.avz.test.natlex_backend_test.Utils;
import ru.net.avz.test.natlex_backend_test.dao.JobDAO;
import ru.net.avz.test.natlex_backend_test.data.SectionPOJO;
import ru.net.avz.test.natlex_backend_test.service.JobService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * 
 * @author Zaitsev Alexei (aka AZ) / alexei.zaycev@ya.ru / alexei.zaycev@gmail.com
 */
@RestController
@RequestMapping(path = "/api/sections")
public class SectionsController {

    @Nonnull private final JobDAO jobDAO;
    @Nonnull private final JobService jobService;

    public SectionsController(
            @Autowired @Nullable JobDAO jobDAO,
            @Autowired @Nullable JobService jobService) {

        this.jobDAO = Utils.requireDI(JobDAO.class, jobDAO);
        this.jobService = Utils.requireDI(JobService.class, jobService);
    }

    @GetMapping(path = "/filter",
                produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody @Nonnull CompletableFuture<Stream<SectionPOJO>> filterSections(
            @RequestParam(name = "jobId", required = false) @Nullable String jobId,
            @RequestParam(name = "sectionName", required = false) @Nullable String sectionName,
            @RequestParam(name = "geoClassName", required = false) @Nullable String geoClassName,
            @RequestParam(name = "geoClassCode", required = false) @Nullable String geoClassCode) {

        return jobDAO.findAllSectionsByFilters(jobId, sectionName, geoClassName, geoClassCode);
    }

    @GetMapping(path = "/export",
                produces = Utils.MEDIA_TYPE__APPLICATION_XLS__VALUE)
    public @ResponseBody @Nonnull CompletableFuture<ResponseEntity<byte[]>> exportSections(
            @RequestParam(name = "jobId", required = false) @Nullable String jobId,
            @RequestParam(name = "sectionName", required = false) @Nullable String sectionName,
            @RequestParam(name = "geoClassName", required = false) @Nullable String geoClassName,
            @RequestParam(name = "geoClassCode", required = false) @Nullable String geoClassCode) {

        return jobDAO.findAllSectionsByFilters(jobId, sectionName, geoClassName, geoClassCode)
                     .thenCompose(jobService::buildXLS)
                     .thenApply(HSSFWorkbook::getBytes)
                     .thenApply(bytes -> ResponseEntity.ok()
                                                       .contentType(Utils.MEDIA_TYPE__APPLICATION_XLS)
                                                       .contentLength(bytes.length)
                                                       .body(bytes));
    }

}