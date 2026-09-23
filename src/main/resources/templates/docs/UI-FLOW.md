# Exam Management System — UI Flow

## Project Objective

Frontend-only prototype of an Exam Management System.
Backend will be integrated later using Java Spring Boot APIs.

---

# User Roles

1. Student
2. Teacher / Proctor
3. Admin
4. Exam Authority

---

# Global Flow

Login
↓
Role Authentication
↓
Role Dashboard
↓
Role-specific Modules
↓
Detailed Actions
↓
Confirmation / Result / Report

---

# Student Flow

Login
↓
Student Dashboard
↓
Upcoming Exams
↓
Exam Details
↓
System Check
↓
Exam Instructions
↓
Start Exam
↓
Online Exam
↓
Submit Confirmation
↓
Submission Successful
↓
Results

---

# Teacher / Proctor Flow

Login
↓
Teacher Dashboard
↓
Exam Management
↓
Create / Edit Exam
↓
Question Paper
↓
Assign Students
↓
Proctoring Settings
↓
Publish Exam

During Exam
↓
Live Monitoring
↓
Candidate List
↓
Candidate Monitoring
↓
Alerts
↓
Evidence Review

After Exam
↓
Evaluation / Results
↓
Reports

---

# Admin Flow

Login
↓
Admin Dashboard
↓
Users
↓
Exam Management
↓
Question Paper Management
↓
Monitoring Settings
↓
Analytics
↓
Reports

---

# Exam Authority Flow

Login
↓
Authority Dashboard
↓
Examination Statistics
↓
Exam Records
↓
Reports

<!-- /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// -->
# Screen Inventory

## Common Screens

1. Login
2. Unauthorized / Access Denied
3. Profile
4. Notifications
5. Settings

---

## Student Screens

1. Student Dashboard
2. Exam Schedule
3. Exam Details
4. Pre-Exam System Check
5. Exam Instructions
6. Online Exam
7. Question Navigation
8. Submit Exam Confirmation
9. Submission Success
10. Results
11. Result Details

---

## Teacher / Proctor Screens

1. Teacher Dashboard
2. Exam List
3. Create Exam
4. Edit Exam
5. Exam Details
6. Question Paper Management
7. Question Editor
8. Assign Students
9. Proctoring Settings
10. Publish Exam
11. Live Monitoring Dashboard
12. Candidate Details
13. Alerts
14. Evidence Review
15. Exam Results
16. Reports

---

## Admin Screens

1. Admin Dashboard
2. User Management
3. User Details
4. Exam Management
5. Question Paper Management
6. Monitoring Settings
7. Analytics
8. Reports
8. System Settings

---

## Exam Authority Screens

1. Authority Dashboard
2. Examination Statistics
3. Examination Records
4. Reports

# Examination Lifecycle

## Before Exam

Student Login
↓
Dashboard
↓
Upcoming Exam
↓
Exam Details
↓
Check Eligibility
↓
System Check
↓
Camera Check
↓
Microphone Check
↓
Screen Sharing Check
↓
Exam Instructions
↓
Start Exam

## During Exam

Exam Timer
↓
Question Display
↓
Answer Selection
↓
Auto Save State
↓
Question Navigation
↓
Marked / Unanswered Status
↓
Proctoring Status
↓
Warnings / Alerts
↓
Submit

## Exam Submission

Submit Button
↓
Confirmation Modal
↓
Final Submission
↓
Submission Success
↓
Exam Completed

## After Exam

Evaluation
↓
Result Generated
↓
Student Result
↓
Result Details