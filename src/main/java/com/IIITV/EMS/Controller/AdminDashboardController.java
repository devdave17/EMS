
package com.IIITV.EMS.Controller;

import com.IIITV.EMS.DTO.AdminDashboardSummary;
import com.IIITV.EMS.Model.QuizStatus;
import com.IIITV.EMS.Model.Role;
import com.IIITV.EMS.Model.UserStatus;
import com.IIITV.EMS.Repository.QuizRepository;
import com.IIITV.EMS.Repository.UserRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {

    private final UserRepository userRepository;
    private final QuizRepository quizRepository;

    public AdminDashboardController(
            UserRepository userRepository,
            QuizRepository quizRepository) {
        this.userRepository = userRepository;
        this.quizRepository = quizRepository;
    }

    @GetMapping("/summary")
    public AdminDashboardSummary getDashboardSummary() {

        return new AdminDashboardSummary(
                userRepository.count(),
                userRepository.countByRole(Role.STUDENT),
                userRepository.countByRole(Role.TEACHER),
                userRepository.countByRole(Role.ADMIN),

                userRepository.countByStatus(UserStatus.ACTIVE),
                userRepository.countByStatus(UserStatus.INACTIVE),
                userRepository.countByStatus(UserStatus.BLOCKED),

                quizRepository.count(),
                quizRepository.countByStatus(QuizStatus.PUBLISHED),
                quizRepository.countByStatus(QuizStatus.DRAFT),
                quizRepository.countByStatus(QuizStatus.CLOSED)
        );
    }
}
