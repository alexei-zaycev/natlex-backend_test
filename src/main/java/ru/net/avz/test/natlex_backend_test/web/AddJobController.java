package ru.net.avz.test.natlex_backend_test.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.net.avz.test.natlex_backend_test.Utils;
import ru.net.avz.test.natlex_backend_test.dao.JobDAO;
import ru.net.avz.test.natlex_backend_test.service.JobService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

import static ru.net.avz.test.natlex_backend_test.config.WebSecurityConfig.ROLE__ADMIN;

/**
 *
 * @author Zaitsev Alexei (aka AZ) / alexei.zaycev@ya.ru / alexei.zaycev@gmail.com
 */
@Controller
public class AddJobController {

    private static final String ENDPOINT        = "/add_job";
    private static final String TEMPLATE        = "add_job";

    @Nonnull private final JobDAO jobDAO;
    @Nonnull private final JobService jobService;

    public AddJobController(
            @Autowired @Nullable JobDAO jobDAO,
            @Autowired @Nullable JobService jobService) {

        this.jobDAO = Utils.requireDI(JobDAO.class, jobDAO);
        this.jobService = Utils.requireDI(JobService.class, jobService);
    }

    @Secured({ROLE__ADMIN})
    @GetMapping(ENDPOINT)
    public String addJob_showForm(
            @Nonnull Model model) {

        assert model != null : "<model> is null";

        return TEMPLATE;
    }

    @Secured({ROLE__ADMIN})
    @PostMapping(ENDPOINT)
    public String addJob_doAdd(
            @RequestParam("file") @Nonnull MultipartFile file,
            @Nonnull RedirectAttributes redirectAttributes)
            throws IOException {

        assert file != null : "<file> is null";
        assert redirectAttributes != null : "<redirectAttributes> is null";

        String jobId = jobService.nextJobId();

        jobService.parseXLS(jobId, file.getInputStream(), -1, -1);

        redirectAttributes.addFlashAttribute(
                "message",
                "file " + file.getOriginalFilename() + " successfully uploaded, scheduled job " + jobId);

        return "redirect:" + ENDPOINT;
    }

}