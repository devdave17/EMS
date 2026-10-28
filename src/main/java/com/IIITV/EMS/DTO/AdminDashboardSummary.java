
package com.IIITV.EMS.DTO;

public record AdminDashboardSummary(
        long totalUsers,
        long totalStudents,
        long totalTeachers,
        long totalAdmins,
        long activeUsers,
        long inactiveUsers,
        long blockedUsers,
        long totalQuizzes,
        long publishedQuizzes,
        long draftQuizzes,
        long closedQuizzes
) {}
