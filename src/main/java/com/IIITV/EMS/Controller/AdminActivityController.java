package com.IIITV.EMS.Controller;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.IIITV.EMS.Model.ActivityLog;
import com.IIITV.EMS.Repository.ActivityLogRepository;

@RestController
@RequestMapping("/api/admin/activity")
public class AdminActivityController {

    private final ActivityLogRepository activityLogRepository;

    public AdminActivityController(ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    @GetMapping
    public List<ActivityLog> getActivityLogs(
            @RequestParam(defaultValue = "100") int limit) {
        requireAdmin();
        int boundedLimit = Math.min(Math.max(limit, 1), 200);
        return activityLogRepository.findAllByOrderByOccurredAtDesc(
                PageRequest.of(0, boundedLimit));
    }

    private void requireAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()))) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Admin access required");
        }
    }
}
