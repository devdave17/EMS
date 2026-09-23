/* =========================================
   EMS — Mock Examination Data
   ========================================= */

const MOCK_EXAMS = [

    {
        id: "EXAM-001",

        title: "Operating Systems",

        code: "CS-301",

        semester: "MCA Semester I",

        instructor: "Dr. Sharma",

        date: "2026-09-28",

        startTime: "10:00",

        endTime: "11:30",

        durationMinutes: 90,

        totalQuestions: 50,

        totalMarks: 100,

        status: "UPCOMING",

        attemptStatus: "NOT_STARTED",

        proctored: true,

        cameraRequired: true,

        microphoneRequired: true,

        screenMonitoringRequired: true,

        instructions: [
            "Ensure your camera and microphone are working.",
            "Keep your examination environment clear.",
            "Do not leave the examination window during the exam.",
            "Submit your answers before the examination ends."
        ]
    },


    {
        id: "EXAM-002",

        title: "Database Management Systems",

        code: "CS-302",

        semester: "MCA Semester I",

        instructor: "Prof. Mehta",

        date: "2026-10-02",

        startTime: "02:00",

        endTime: "03:30",

        durationMinutes: 90,

        totalQuestions: 50,

        totalMarks: 100,

        status: "UPCOMING",

        attemptStatus: "NOT_STARTED",

        proctored: true,

        cameraRequired: true,

        microphoneRequired: true,

        screenMonitoringRequired: true,

        instructions: [
            "Complete the system check before starting.",
            "Use a stable internet connection.",
            "Keep the camera enabled during the examination."
        ]
    },


    {
        id: "EXAM-003",

        title: "Computer Networks",

        code: "CS-303",

        semester: "MCA Semester I",

        instructor: "Dr. Patel",

        date: "2026-10-07",

        startTime: "10:30",

        endTime: "12:00",

        durationMinutes: 90,

        totalQuestions: 60,

        totalMarks: 100,

        status: "UPCOMING",

        attemptStatus: "NOT_STARTED",

        proctored: false,

        cameraRequired: false,

        microphoneRequired: false,

        screenMonitoringRequired: false,

        instructions: [
            "Read all questions carefully.",
            "Submit the examination before the end time."
        ]
    },


    {
        id: "EXAM-004",

        title: "Computer Programming",

        code: "CS-304",

        semester: "MCA Semester I",

        instructor: "Prof. Shah",

        date: "2026-09-16",

        startTime: "10:00",

        endTime: "11:30",

        durationMinutes: 90,

        totalQuestions: 50,

        totalMarks: 100,

        status: "COMPLETED",

        attemptStatus: "SUBMITTED",

        proctored: true,

        cameraRequired: true,

        microphoneRequired: true,

        screenMonitoringRequired: true,

        instructions: []
    }

];