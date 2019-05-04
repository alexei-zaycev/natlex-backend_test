package ru.net.avz.test.natlex_backend_test.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.net.avz.test.natlex_backend_test.Utils;
import ru.net.avz.test.natlex_backend_test.dao.JobDAO;
import ru.net.avz.test.natlex_backend_test.service.JobService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static ru.net.avz.test.natlex_backend_test.config.WebSecurityConfig.ROLE__ADMIN;
import static ru.net.avz.test.natlex_backend_test.config.WebSecurityConfig.ROLE__USER;

/**
 *
 * @author Zaitsev Alexei (aka AZ) / alexei.zaycev@ya.ru / alexei.zaycev@gmail.com
 */
@Controller
public class FilterSectionsController {

    private static final String ENDPOINT        = "/sections";
    private static final String TEMPLATE        = "sections";

    @Nonnull private final JobDAO jobDAO;
    @Nonnull private final JobService jobService;

    public FilterSectionsController(
            @Autowired @Nullable JobDAO jobDAO,
            @Autowired @Nullable JobService jobService) {

        this.jobDAO = Utils.requireDI(JobDAO.class, jobDAO);
        this.jobService = Utils.requireDI(JobService.class, jobService);
    }

    @Secured({ROLE__USER, ROLE__ADMIN})
    @RequestMapping(ENDPOINT)
    public CompletableFuture<String> filterSections(
            @RequestParam(name = "jobId", required = false) @Nullable String jobId,
            @RequestParam(name = "sectionName", required = false) @Nullable String sectionName,
            @RequestParam(name = "geoClassName", required = false) @Nullable String geoClassName,
            @RequestParam(name = "geoClassCode", required = false) @Nullable String geoClassCode,
            @Nonnull Model model) {

        assert model != null : "<model> is null";

        if (jobId != null && sectionName != null && geoClassName != null && geoClassCode != null) {

            return jobDAO.findAllSectionsByFilters(
                                StringUtils.isEmpty(jobId) ? null : jobId,
                                StringUtils.isEmpty(sectionName) ? null : sectionName,
                                StringUtils.isEmpty(geoClassName) ? null : geoClassName,
                                StringUtils.isEmpty(geoClassCode) ? null : geoClassCode)
                         .thenApply(sections -> sections.collect(Collectors.toList()))
                         .thenApply(sections -> {

                                model.addAttribute("jobId", jobId)
                                        .addAttribute("sectionName", sectionName)
                                        .addAttribute("geoClassName", geoClassName)
                                        .addAttribute("geoClassCode", geoClassCode)
                                        .addAttribute("sections", sections);

                                return TEMPLATE;
                         });

        } else {
            return CompletableFuture.completedFuture(TEMPLATE);
        }
    }

}