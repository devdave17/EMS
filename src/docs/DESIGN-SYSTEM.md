# EMS — Design System

## Design Direction

The Exam Management System uses a professional, modern academic interface.

The visual language should communicate:

- Trust
- Security
- Clarity
- Focus
- Professionalism
- Ease of use

The interface must remain clean and functional.
Avoid unnecessary animations, decorative elements, and visual clutter.

---

# Color System

## Primary Brand

Primary:
#3730A3

Primary Hover:
#312E81

Primary Light:
#EEF2FF

Primary Dark:
#1E1B4B

---

## Neutral Colors

Background:
#F8FAFC

Surface:
#FFFFFF

Surface Secondary:
#F1F5F9

Border:
#E2E8F0

Border Strong:
#CBD5E1

Text Primary:
#0F172A

Text Secondary:
#475569

Text Muted:
#64748B

Text Disabled:
#94A3B8

---

## Status Colors

Success:
#16A34A

Success Background:
#F0FDF4

Warning:
#D97706

Warning Background:
#FFFBEB

Danger:
#DC2626

Danger Background:
#FEF2F2

Info:
#2563EB

Info Background:
#EFF6FF

---

# Usage Rules

Primary color is mainly used for:

- Primary buttons
- Active navigation
- Important actions
- Links
- Selected states
- Brand elements

Red is reserved for:

- Errors
- Destructive actions
- Critical exam/proctoring alerts

Green is reserved for:

- Successful actions
- Completed states
- Positive status

Orange is reserved for:

- Warnings
- Attention-required states

Blue is reserved for:

- Informational states

Most of the interface should remain neutral using white, light gray, and dark gray.

---

# Typography

Primary Font:

Inter

Fallback:

system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif

The typography should be:

- Clean
- Highly readable
- Professional
- Consistent

Avoid decorative or handwritten fonts.

---

# General Visual Style

Border Radius:

Small:
6px

Medium:
8px

Large:
12px

Extra Large:
16px

Use medium radius as the default for most interface elements.

---

# Shadow Philosophy

Use subtle shadows only.

Cards and panels should primarily rely on:

- Borders
- Spacing
- Surface contrast

Avoid heavy glowing shadows.

---

# Animation Philosophy

Animations should be minimal.

Allowed:

- Small hover transitions
- Button state transitions
- Modal entrance
- Sidebar transitions

Avoid:

- Large page animations
- Excessive motion
- Decorative animations
- Continuous animated backgrounds

---

# Layout Philosophy

The application is desktop-first but responsive.

Desktop:

Primary dashboard layout uses:

Sidebar
+
Topbar
+
Main Content

Mobile:

Sidebar transforms into a mobile navigation pattern.

Content remains readable and usable without horizontal scrolling wherever possible.

# Typography Scale

## Font Weights

Regular:
400

Medium:
500

Semi Bold:
600

Bold:
700

Use 800 or heavier weights only when genuinely required.

---

## Text Sizes

Display:
36px
Line Height: 44px
Weight: 700

Page Heading:
28px
Line Height: 36px
Weight: 700

Section Heading:
22px
Line Height: 30px
Weight: 600

Card Heading:
18px
Line Height: 26px
Weight: 600

Body Large:
16px
Line Height: 24px
Weight: 400

Body:
14px
Line Height: 22px
Weight: 400

Body Small:
13px
Line Height: 20px
Weight: 400

Caption:
12px
Line Height: 18px
Weight: 500

Button Text:
14px
Line Height: 20px
Weight: 600

---

# Spacing System

Use a consistent 4px-based spacing scale.

4px
8px
12px
16px
20px
24px
32px
40px
48px
64px
80px

---

# Spacing Guidelines

## Small Elements

Icon and text:
8px

Input label and input:
8px

Related form fields:
16px

---

## Cards

Card internal padding:
24px

Compact card padding:
16px

Card content gap:
16px

---

## Sections

Small section gap:
24px

Standard section gap:
32px

Large section gap:
48px

---

# Layout Dimensions

## Application Sidebar

Desktop expanded width:
260px

Desktop collapsed width:
72px

Sidebar should remain fixed while the main content scrolls.

---

## Topbar

Height:
72px

Topbar should remain visually distinct from the main content.

---

## Main Content

Desktop horizontal padding:
32px

Tablet horizontal padding:
24px

Mobile horizontal padding:
16px

---

## Content Width

Standard application content should use:

max-width:
1440px

Very large screens should not stretch content unnecessarily.

---

# Component Heights

## Buttons

Small:
36px

Medium:
40px

Large:
44px

Primary application actions should normally use:
40px to 44px

---

## Inputs

Small:
36px

Default:
40px

Large:
44px

Default form controls should use:
40px

---

## Icon Sizes

Extra Small:
14px

Small:
16px

Default:
20px

Large:
24px

Feature Icon:
32px

---

# Border Width

Default:
1px

Use thicker borders only for focused or highly emphasized states.

---

# Responsive Breakpoints

Mobile Small:
< 480px

Mobile:
480px – 767px

Tablet:
768px – 1023px

Desktop:
1024px – 1279px

Large Desktop:
1280px+

---

# Responsive Rules

## Mobile

- Sidebar becomes mobile navigation
- Dashboard grids stack vertically
- Tables may become scrollable or transform into card layouts
- Form fields become full width
- Large headings scale down
- Horizontal spacing reduces
- Secondary information may collapse behind expandable sections

---

## Tablet

- Sidebar may remain visible depending on available width
- Multi-column layouts reduce columns
- Dashboard cards may use 2-column layouts
- Main content spacing reduces

---

## Desktop

- Full sidebar
- Multi-column dashboard layouts
- Large tables
- Side-by-side panels
- Expanded information density

---

# Grid Rules

Dashboard statistics:

Desktop:
4 columns where appropriate

Tablet:
2 columns

Mobile:
1 column

Standard content grids should use flexible columns rather than fixed pixel widths.

---

# Interaction Target

Interactive controls should provide comfortable click/touch areas.

Minimum target:
40px

Preferred target:
44px

This applies especially to:

- Buttons
- Navigation items
- Icon buttons
- Exam question navigation
- Checkboxes and radio controls where practical



# Navigation System

## Overall Dashboard Structure

All authenticated dashboard pages follow:

Sidebar
+
Topbar
+
Main Content

The sidebar controls primary navigation.
The topbar provides global user actions.
The main content contains page-specific information.

---

# Sidebar

## Desktop

Expanded width:
260px

Collapsed width:
72px

Sidebar remains fixed.

The main content scrolls independently.

---

## Sidebar Structure

### Brand Area

Logo
+
EMS
+
Exam Management System

When collapsed:
Logo/Icon only

---

### Primary Navigation

Navigation items should contain:

Icon
+
Label
+
Optional badge

Active item should use:

Primary background or light primary background
+
Primary text
+
Clear visual indicator

---

### Bottom Navigation

Bottom area may contain:

Settings
Help
Logout

Logout should remain visually separated from normal navigation.

---

# Student Navigation

Dashboard
Exams
Results

Optional secondary items:

Notifications
Profile
Settings

The student should see only functions relevant to the student role.

---

# Teacher / Proctor Navigation

Dashboard
Exams
Question Papers
Students
Live Monitoring
Alerts
Results
Reports

Optional:

Notifications
Profile
Settings

Exam creation, question management, assignment, proctoring and monitoring should remain logically grouped.

---

# Admin Navigation

Dashboard
Users
Exams
Question Papers
Monitoring
Analytics
Reports

Optional:

Settings
Profile
Notifications

Administrative configuration should not clutter the primary dashboard.

---

# Exam Authority Navigation

Dashboard
Statistics
Exam Records
Reports

Optional:

Notifications
Profile
Settings

The authority interface should focus on examination-level information rather than operational configuration.

---

# Navigation Grouping

Teacher / Proctor navigation should conceptually use:

EXAMINATION

- Exams
- Question Papers
- Students

MONITORING

- Live Monitoring
- Alerts

ANALYSIS

- Results
- Reports

Admin navigation should conceptually use:

MANAGEMENT

- Users
- Exams
- Question Papers

MONITORING

- Monitoring

ANALYSIS

- Analytics
- Reports

---

# Progressive Disclosure in Navigation

Do not expose every advanced action directly from the sidebar.

Example:

Teacher:

Sidebar
↓
Exams
↓
Exam List
↓
Select Exam
↓
Exam Details
↓
Actions

Possible actions:

- Edit Exam
- Manage Questions
- Assign Students
- Proctoring Settings
- Publish Exam

Advanced configuration should appear only inside the appropriate workflow.

---

# Topbar

Topbar height:
72px

Topbar contains:

Left:
Current page title / breadcrumb

Right:

Notifications
User profile
Role indicator
Profile menu

---

# Breadcrumbs

Use breadcrumbs when the user is more than one level deep.

Example:

Dashboard
>
Exams
>
Operating Systems
>
Question Paper

Avoid breadcrumbs for simple top-level pages.

---

# Page Header

Every major page should have:

Page Title
+
Short supporting description
+
Primary action where appropriate

Example:

Exams
Manage scheduled and published examinations.

[Create Exam]

---

# Dashboard Philosophy

Dashboards should answer:

1. What is happening?
2. What requires attention?
3. What should the user do next?

Avoid filling dashboards with unnecessary charts.

---

# Student Dashboard

Primary content:

Welcome / profile context

Upcoming Exam
- Exam name
- Date
- Time
- Duration
- Status
- Action

Recent Results

Quick Statistics where useful

Important Notifications

Primary CTA:
Start Exam / View Exam

---

# Teacher / Proctor Dashboard

Primary content:

Today's Exams

Active Exams

Candidates

Pending Alerts

Recent Activity

Primary Actions:

Create Exam
View Exams
Open Monitoring

---

# Admin Dashboard

Primary content:

Total Users

Active Exams

Scheduled Exams

System Alerts

Recent Activity

Analytics summary

Primary Actions:

Manage Users
Manage Exams
View Reports

---

# Exam Authority Dashboard

Primary content:

Total Examinations

Completed Examinations

Active Examinations

Examination Statistics

Recent Reports

Primary Action:

View Reports

---

# Cards

Cards should provide focused information.

Do not create a card for every piece of information.

Preferred structure:

Card Header
+
Main Value / Content
+
Supporting Information
+
Optional Action

---

# Tables

Use tables for:

- Users
- Exams
- Candidates
- Results
- Alerts
- Reports

Tables should provide:

Clear column names
Readable spacing
Status indicators
Relevant actions

Do not overload tables with unnecessary columns.

---

# Status Indicators

Use badges/pills for statuses such as:

Upcoming
Active
Completed
Draft
Published
Submitted
Pending
Reviewed
Flagged

Status should be communicated through:

Color
+
Text

Do not rely on color alone.

---

# Mobile Navigation

On mobile:

Sidebar should not permanently consume screen space.

Use:

Topbar
+
Menu button
+
Slide-in / overlay navigation

Navigation closes after selecting a page.

---

# Mobile Dashboard

Content becomes single-column wherever necessary.

Priority order:

Primary action
↓
Important information
↓
Secondary information
↓
Optional information

---

# Navigation Interaction Rules

Hover:
Subtle visual feedback

Active:
Clearly visible state

Focus:
Visible keyboard focus state

Disabled:
Reduced emphasis and non-interactive appearance

Loading:
Preserve layout while indicating loading

Error:
Keep user context and explain the problem near the affected action

Success:
Show clear confirmation without unnecessarily interrupting the workflow.


# Component System

The EMS frontend uses reusable UI components.
Components should have consistent structure, spacing, states, and behavior.

---

# Buttons

## Primary Button

Use for the main action on a page.

Examples:

- Create Exam
- Start Exam
- Submit Exam
- Publish Exam
- Save Changes

Style:

Primary background
White text
Medium border radius
Clear hover state

---

## Secondary Button

Use for supporting actions.

Examples:

- Cancel
- Back
- View Details
- Preview

Style:

White / surface background
Border
Primary or dark text

---

## Danger Button

Use only for destructive or high-risk actions.

Examples:

- Delete User
- Delete Exam
- Remove Question
- End Exam

Style:

Danger color
Clear destructive indication

---

## Ghost Button

Use for low-emphasis actions.

Examples:

- More
- Close
- Optional actions

Ghost buttons should not visually compete with primary actions.

---

# Button States

Every interactive button should support:

Default
Hover
Focus
Active
Disabled
Loading

Loading state should preserve the button dimensions.

Example:

Save
↓
Saving...

Avoid layout shifts when changing button states.

---

# Inputs

Standard input structure:

Label
+
Input
+
Optional Help Text
+
Error Message

Examples:

Username
Password
Exam Title
Duration
Date
Time

---

# Input States

Every input should support:

Default
Focus
Filled
Disabled
Read Only
Error
Success

Focused inputs must have a clearly visible focus indicator.

---

# Form Guidelines

Forms should:

- Use clear labels
- Group related fields
- Avoid unnecessary fields
- Explain validation errors
- Preserve entered data when possible
- Clearly identify required fields

Long forms should be divided into logical sections.

Example:

Create Exam

1. Basic Details
2. Schedule
3. Questions
4. Students
5. Proctoring
6. Review & Publish

---

# Cards

Cards are used for grouped information.

Examples:

- Upcoming Exam
- Statistics
- Candidate Information
- Alert Summary
- Result Summary

Card structure:

Header
+
Content
+
Optional Footer / Action

Cards should not be nested excessively.

---

# Statistic Cards

Statistic cards should contain:

Label
+
Large Value
+
Supporting context

Optional:

Trend / status indicator

Avoid displaying too many statistic cards on one screen.

---

# Modal

Use modals for focused actions that require temporary attention.

Examples:

- Confirm Exam Submission
- Delete User
- Publish Exam
- End Monitoring Session
- View Quick Details

Modal structure:

Overlay
↓
Modal Header
↓
Modal Content
↓
Modal Actions

The primary and destructive actions should be clearly differentiated.

---

# Toast Notifications

Use toast notifications for short-lived feedback.

Examples:

Success:
"Exam created successfully."

Error:
"Unable to save exam."

Info:
"Exam has been scheduled."

Warning:
"Exam starts in 10 minutes."

Toast messages should remain concise.

Do not use toast notifications for critical information that requires user action.

---

# Alerts

Use inline alerts for contextual information.

Examples:

Warning:
"Camera permission is required before starting this exam."

Error:
"Your exam could not be submitted."

Info:
"Your answers are being saved automatically."

Success:
"System check completed successfully."

---

# Badges

Use badges for compact status information.

Examples:

Upcoming
Active
Completed
Draft
Published
Pending
Reviewed
Flagged

Badges should contain both:

Text
+
Visual distinction

---

# Tables

Tables should support:

Header
Rows
Status
Actions

Optional features:

Search
Filter
Sort
Pagination

Do not add these features unless they are actually useful on that page.

---

# Table Row Actions

Common actions:

View
Edit
Manage
Review
Delete

Use an action menu when many actions exist.

Avoid placing too many visible buttons inside each row.

---

# Dropdown Menus

Use dropdown menus for:

User profile
Secondary actions
Filters
Sorting
Role selection where appropriate

Dropdowns should close when:

- Selecting an option
- Clicking outside
- Pressing Escape

---

# Tabs

Use tabs only when multiple related views belong to the same context.

Examples:

Exam Details:

Overview
Questions
Students
Monitoring
Results

Candidate Details:

Overview
Activity
Alerts
Evidence

Do not use tabs as a replacement for navigation.

---

# Search

Search should be used where users are likely to manage many records.

Examples:

Users
Exams
Candidates
Question Bank

Search should provide:

Clear input
Search icon where appropriate
Clear action
Useful empty state

---

# Filters

Filters should be available for data-heavy screens.

Examples:

Exam Status
Date
Course
Candidate
Alert Type

Filters should be easy to clear.

---

# Empty States

When no data exists, show:

Clear title
+
Short explanation
+
Relevant action

Example:

"No exams scheduled"

"Create an exam to start managing your examination schedule."

[Create Exam]

Avoid showing a completely blank page.

---

# Loading States

Use loading indicators when data or actions take time.

Prefer:

Skeletons
or
Small inline loaders

Avoid full-screen loaders unless the entire application is loading.

The layout should remain stable during loading.

---

# Error States

Errors should explain:

What happened
+
What the user can do next

Example:

"Unable to load exams."

[Try Again]

Avoid technical backend details in the user-facing message.

---

# Confirmation States

Critical actions should provide explicit confirmation.

Examples:

Submit Exam
Delete Exam
Publish Exam
End Monitoring

For irreversible actions, explain the consequence before confirmation.

---

# Exam-Specific Components

The online examination interface requires dedicated components.

## Exam Timer

Display:

Time Remaining

Timer should be highly visible but not visually distracting.

Critical remaining time may use a warning state.

---

## Question Palette

Show:

Question number
+
Status

Possible states:

Not Visited
Not Answered
Answered
Marked for Review
Answered + Marked for Review
Current

---

## Question Card

Contains:

Question Number
Question Text
Options / Answer Area
Optional Question Metadata

The question interface should prioritize readability and focus.

---

## Exam Navigation

Primary controls:

Previous
Save / Next
Submit

Question navigation should remain predictable.

---

## Proctoring Status

Student should see a compact monitoring status.

Examples:

Camera: Active
Microphone: Active
Screen: Sharing
Monitoring: Active

This status should not dominate the exam interface.

---

# Monitoring Components

Teacher / Proctor interface may contain:

Candidate Card
Candidate Grid
Video Preview
Screen Preview
Monitoring Status
Alert Indicator
Alert Timeline
Evidence Preview

---

# Alert Severity

Alerts may be represented using:

Informational
Warning
Critical

Severity should be communicated through:

Text
+
Icon
+
Appropriate status color

Do not rely on color alone.

---

# Evidence Review

Evidence review should provide:

Candidate
Event Type
Timestamp
Evidence Preview
Event Description
Review Status
Reviewer Action

Possible review states:

Pending
Reviewed
Dismissed
Escalated

---

# Accessibility

Interactive components must support:

Keyboard navigation
Visible focus
Readable text contrast
Clear labels
Meaningful button text
Accessible form errors

Information should not be communicated through color alone.

---

# Component Consistency Rule

A component should have one standard design across the entire application.

Example:

A primary button on the Student dashboard
and
a primary button on the Admin dashboard

must follow the same visual system.

Only context and content should change.

---

# Reusability Rule

Before creating a new UI component, check whether an existing component can be reused.

Prefer:

One reusable component
+
Different content / state

instead of:

Multiple visually similar custom components.


# Student Examination UX

The examination interface is the most focused part of the EMS application.

Its primary objective is to help the student:

- Understand the current question
- Answer confidently
- Track remaining time
- Navigate questions
- Understand monitoring status
- Submit safely

The exam screen must avoid unnecessary navigation and distractions.

---

# Exam Entry Flow

Student selects an available exam.

Exam Details
↓
Eligibility Check
↓
System Check
↓
Exam Instructions
↓
Start Exam

---

# Exam Details Screen

Display:

Exam Name
Subject / Course
Date
Start Time
End Time
Duration
Total Questions
Total Marks
Attempt Status
Proctoring Requirement

Primary Action:

Start System Check

Student should clearly understand the exam before proceeding.

---

# Pre-Exam System Check

The system check should verify the required examination environment.

Possible checks:

Camera
Microphone
Screen Sharing
Browser / Environment
Network Status

Each check should have a visible state:

Checking
Passed
Failed
Required Action

Example:

Camera
✓ Camera detected

Microphone
✓ Microphone available

Screen Sharing
✓ Screen sharing ready

---

# System Check Failure

If a required check fails:

Show:

Problem
+
Reason
+
Recommended Action
+
Retry

Example:

"Camera access is required."

[Allow Camera]

Do not allow the student to start the exam until required checks are completed.

---

# Exam Instructions

Instructions should be displayed before the exam starts.

Include:

Exam rules
Duration
Submission rules
Proctoring requirements
Important warnings

Primary Action:

I Understand & Start Exam

This should be the final confirmation before entering the exam.

---

# Online Exam Layout

The exam interface should use a dedicated layout.

Header:

EMS
Exam Name
Time Remaining
Monitoring Status

Main Area:

Question
+
Answer Options

Secondary Area:

Question Palette
+
Exam Status

Footer / Navigation:

Previous
Save & Next
Submit Exam

---

# Exam Header

The header should show:

Exam Name
Question Progress
Remaining Time
Monitoring Status

Example:

Operating Systems
Question 12 of 50
42:18 Remaining
Monitoring Active

The timer should remain visible during the examination.

---

# Question Area

The question should be the visual focus.

Structure:

Question Number
+
Question Text
+
Answer Options

Avoid unnecessary decoration.

Answer options must have a large, comfortable click area.

---

# Question States

Each question can have:

Not Visited
Not Answered
Answered
Marked for Review
Answered + Marked for Review
Current

The question palette should visually distinguish these states.

Each state must use:

Text / Shape / Icon
+
Color where appropriate

Color alone should never communicate the state.

---

# Question Navigation

Student controls:

Previous
Save & Next

Question palette allows direct navigation.

Navigation must remain predictable.

Do not unexpectedly move the student to another question after an action.

---

# Answer Persistence

Selected answers should be retained while the student navigates.

The frontend should maintain the current exam state.

Future backend integration will persist answers through the API.

---

# Auto Save Indicator

The exam interface may show a compact status:

Saving...
Saved
Unable to Save

The status should remain subtle.

The exam should remain focused on the question.

---

# Timer States

Normal:

Regular timer presentation

Warning:

Reduced remaining time

Critical:

Very little remaining time

The timer should become more visually prominent as the remaining time becomes critical.

Do not use excessive animation.

---

# Exam Submission

Submit action should require confirmation.

Flow:

Submit Exam
↓
Confirmation Modal
↓
Review Status
↓
Confirm Submission
↓
Submission Processing
↓
Submission Successful

---

# Submit Confirmation

Show:

Answered Questions
Unanswered Questions
Marked for Review

Example:

Answered: 42
Unanswered: 5
Marked for Review: 3

Message:

"Are you sure you want to submit your exam?"

Primary action:

Submit Exam

Secondary action:

Continue Exam

---

# Submission Success

After successful submission:

Show:

Success indicator
Exam name
Submission time
Submission status

Primary action:

View Result

If result is not immediately available:

"Your responses have been submitted successfully. Results will be available after evaluation."

---

# Browser / Exit Warning

The interface should provide a clear warning when leaving an active exam.

Possible situations:

Page refresh
Navigation away
Browser close
Back navigation

The frontend should warn the student that the examination session is active.

Actual enforcement will be handled by the future backend / examination service.

---

# Student Proctoring Status

The student should see only a compact status area.

Example:

● Camera Active
● Screen Sharing Active
● Monitoring Active

If there is a problem:

! Camera Disconnected
! Screen Sharing Stopped

The student should receive clear instructions about what action is required.

---

# Monitoring UX

The monitoring interface belongs primarily to the Teacher / Proctor side.

The student view should not expose internal alert logic or evidence review information.

---

# Teacher / Proctor Live Monitoring

The monitoring dashboard should provide a real-time overview of active candidates.

Primary information:

Active Exam
Candidate Count
Online Candidates
Monitoring Issues
Active Alerts

---

# Candidate Monitoring Layout

Possible layout:

Candidate Grid
+
Candidate Status
+
Alert Indicators

Each candidate card may contain:

Candidate Name
Candidate ID
Exam Status
Camera Status
Screen Status
Connection Status
Alert Count
Current State

---

# Candidate Status

Possible states:

Not Started
Waiting
In Progress
Disconnected
Completed
Submitted

---

# Monitoring States

Camera:

Active
Inactive
Disconnected

Screen:

Sharing
Stopped
Unavailable

Connection:

Stable
Weak
Disconnected

---

# Live Monitoring Actions

Teacher / Proctor may:

View Candidate
Open Candidate Details
Inspect Alerts
Review Evidence
Return to Candidate List

Avoid destructive actions directly inside the candidate card.

---

# Alert System

The system may generate alerts for configured suspicious behaviour.

Each alert should contain:

Candidate
Alert Type
Timestamp
Severity
Status
Evidence Availability

Possible severity:

Informational
Warning
Critical

---

# Alert Panel

The panel should allow the reviewer to:

View New Alerts
Filter Alerts
Open Alert
Review Evidence
Mark Review Status

---

# Alert Status

Possible states:

New
Under Review
Reviewed
Dismissed
Escalated

---

# Evidence Review

Evidence review should provide context for the alert.

Display:

Candidate
Alert Type
Timestamp
Evidence Preview
Description
Current Review Status

Possible reviewer actions:

Mark Reviewed
Dismiss
Escalate

Actions should be clearly separated from destructive actions.

---

# Candidate Details

Candidate details can contain:

Profile Information
Exam Information
Current Status
Monitoring Status
Activity
Alerts
Evidence

Use tabs when the information becomes large.

Suggested tabs:

Overview
Activity
Alerts
Evidence

---

# Monitoring Dashboard Priorities

The monitoring dashboard should answer:

1. Which exams are currently active?
2. Which candidates are currently active?
3. Which candidates require attention?
4. Which alerts are new?
5. Which evidence needs review?

Do not overwhelm the proctor with unnecessary statistics.

---

# Exam Completion

When all candidates finish:

Live Monitoring
↓
Exam Completed
↓
Final Candidate Status
↓
Alerts / Evidence Review
↓
Results / Reports

---

# Frontend Prototype Behavior

Because the current project is frontend-only:

- Candidate data will use mock data
- Exam data will use mock data
- Questions will use mock data
- Monitoring status will be simulated
- Alerts will use simulated events
- Evidence will use demonstration data
- Timer will function in JavaScript
- Answer state will function in the frontend

Actual:

- Authentication
- Database persistence
- Webcam service
- Screen capture service
- Suspicious-behaviour detection
- Evidence storage
- Result persistence

will be integrated later.

The UI should be designed so these future services can replace the mock implementation without changing the overall user experience.

---

# Exam UX Principle

During an exam:

Focus first
↓
Clarity second
↓
Monitoring visibility third
↓
Secondary information last

The student should never feel that the monitoring dashboard is the primary interface.

---

# Proctoring UX Principle

During monitoring:

Candidate awareness
↓
Alert visibility
↓
Evidence context
↓
Review action

The interface should prioritize information that requires attention.

# Authentication & Entry Experience

Authentication is the gateway to the EMS application.

The entry experience should be professional, focused, and secure-looking.

The login page should not expose dashboard functionality before authentication.

---

# Application Entry Flow

Application Open
↓
Entry Point
↓
Login
↓
Credential Validation
↓
Role Determination
↓
Role-Based Access
↓
Role Dashboard

---

# Entry Point

The root application entry is:

index.html

The entry point should redirect or navigate the user toward the authentication experience.

The entry point should remain lightweight.

---

# Login Page

The login page should contain:

EMS Logo / Brand
Application Name
Short supporting description

Login Form:

User ID / Username
Password

Primary Action:

Sign In

Supporting actions may include:

Forgot Password
Help / Support

Do not add unnecessary promotional content.

---

# Login Layout

Desktop:

Left:
Brand / EMS information

Right:
Login form

Alternative compact layout may be used when appropriate.

Mobile:

Single-column layout

Brand
↓
Login Form
↓
Supporting Actions

---

# Login Branding

The branding area should communicate:

Exam Management System
Secure Online Examination Platform

Keep the message concise.

Avoid large marketing sections.

---

# Login Form States

Initial:

Empty form

Typing:

Normal input state

Focused:

Visible focus indicator

Validation Error:

Clear field-level error

Submitting:

Button changes to loading state

Authentication Error:

Show a clear form-level message

Success:

Proceed to role-based destination

---

# Authentication Errors

Examples:

Invalid credentials
Account inactive
Unauthorized role
Session expired

User-facing errors should remain clear and non-technical.

Do not expose backend implementation details.

---

# Role-Based Entry

After successful authentication, the user's role determines the application area.

Possible roles:

STUDENT
TEACHER / PROCTOR
ADMIN
AUTHORITY

Example:

Student
↓
Student Dashboard

Teacher / Proctor
↓
Teacher Dashboard

Admin
↓
Admin Dashboard

Authority
↓
Authority Dashboard

---

# Role Security Principle

Frontend role checks improve user experience but are not a replacement for backend authorization.

Future Spring Boot implementation must independently validate:

Authentication
+
Role
+
Permission

before allowing protected operations.

---

# Role Indicator

Inside authenticated areas, the current role should be visible when useful.

Example:

Student

Teacher / Proctor

Administrator

Exam Authority

Do not display unnecessary technical role codes to normal users.

---

# Session State

The frontend should conceptually maintain:

Authenticated / Not Authenticated
+
Current User
+
Current Role
+
Session State

Future implementation will receive authentication/session information from the backend.

---

# Protected Routes

Protected application screens should not be directly accessible to unauthenticated users.

Example:

Unauthenticated
↓
Attempt to open Student Dashboard
↓
Redirect to Login

Authenticated Student
↓
Attempt to open Admin Dashboard
↓
Access Denied

---

# Authorization States

The UI should support:

Authenticated
Unauthenticated
Unauthorized
Session Expired

---

# Unauthorized Page

The unauthorized screen should show:

Access Denied

"You do not have permission to access this page."

Primary action:

Return to Dashboard

Secondary action:

Go to Login

---

# Session Expiry

When a session expires:

Show a clear message.

Example:

"Your session has expired. Please sign in again."

Primary action:

Sign In Again

Avoid silently losing the user's context whenever possible.

---

# Logout

Logout should be available through the user profile menu.

Flow:

Profile Menu
↓
Logout
↓
Confirmation where appropriate
↓
Session Cleared
↓
Login

---

# Password UX

Password field should support:

Show / Hide password

The interface should not expose passwords in plain text outside the explicit visibility control.

---

# Forgot Password

The frontend should provide a placeholder workflow for:

Forgot Password
↓
Account Identification
↓
Recovery / Verification
↓
Reset Password

Actual password recovery will be implemented through the backend later.

---

# Authentication Prototype

Current frontend-only implementation:

Login credentials:
Mock data

Authentication:
Frontend simulation

Session:
Frontend state / storage

Role:
Mock user role

Protected navigation:
Frontend simulation

Future implementation:

Login
↓
Spring Boot Authentication API
↓
Authentication Response
↓
Token / Session
↓
Role + Permissions
↓
Protected Application

---

# Authentication Architecture Principle

Do not place authentication logic directly inside individual page files.

Centralize authentication behavior in:

assets/js/core/auth.js

Centralize route/access checks in:

assets/js/core/guard.js

Centralize application state in:

assets/js/core/state.js

Future API communication will be handled through:

services/api/

Mock authentication will be handled through:

services/mock/

---

# Login Experience Principle

The user should understand:

Where they are
+
What application they are entering
+
What credentials are required
+
What happens after login

without unnecessary explanation.

---

# First-Time User Principle

The first interaction should be simple:

Open EMS
↓
Login
↓
Role Dashboard

Do not force users through a long onboarding process unless the project later requires it.

# Responsive Design System

The EMS frontend must work across desktop, tablet, and mobile devices.

The design should adapt to the available space without changing the core workflow.

---

# Responsive Philosophy

Desktop-first development

+
Responsive behavior from the beginning

The desktop interface should provide the complete experience.

Smaller screens should reorganize information rather than simply shrink everything.

---

# Desktop Layout

For desktop screens:

Sidebar:
Visible

Topbar:
Visible

Main Content:
Full dashboard layout

Cards:
Multi-column where appropriate

Tables:
Full table layout

Forms:
Multi-column where useful

---

# Tablet Layout

For tablet screens:

Sidebar:
May collapse or transform depending on available width

Topbar:
Remains visible

Cards:
Reduce number of columns

Forms:
Reduce multi-column layouts

Tables:
Allow horizontal scrolling when necessary

Secondary information:
May collapse into expandable sections

---

# Mobile Layout

For mobile screens:

Sidebar:
Hidden by default

Navigation:
Menu button + overlay / slide-in navigation

Topbar:
Compact

Cards:
Single-column

Forms:
Single-column

Tables:
Scrollable or transformed into mobile-friendly layouts

Buttons:
Full width where appropriate

Secondary content:
Collapsed or moved below primary content

---

# Mobile Priority

Information hierarchy should follow:

1. Primary action
2. Important information
3. Supporting information
4. Optional information

Do not give equal visual weight to everything.

---

# Responsive Dashboard Rules

Student Dashboard:

Upcoming Exam
↓
Primary Action
↓
Important Notifications
↓
Recent Results
↓
Secondary Information

Teacher / Proctor Dashboard:

Active Exams
↓
Monitoring
↓
Alerts
↓
Recent Activity
↓
Secondary Information

Admin Dashboard:

Important Statistics
↓
System Activity
↓
Management Actions
↓
Analytics / Reports

Authority Dashboard:

Examination Statistics
↓
Important Records
↓
Reports
↓
Secondary Information

---

# Responsive Exam Screen

The examination interface requires special treatment.

Desktop:

Question Area
+
Question Palette / Exam Status

Tablet:

Question Area
+
Collapsible Question Palette

Mobile:

Primary Question Area
+
Compact Timer
+
Expandable Question Navigation

The question itself must remain the most prominent element.

The interface must never make the student scroll excessively just to answer a question.

---

# Responsive Monitoring Screen

Desktop:

Candidate Grid
+
Monitoring Information
+
Alert Panel

Tablet:

Reduced Candidate Grid
+
Collapsible Alert Panel

Mobile:

Candidate List / Compact Candidate Cards
↓
Candidate Details
↓
Alerts
↓
Evidence

The mobile monitoring experience should prioritize candidate status and alerts over secondary statistics.

---

# Overflow Rules

Prevent unintended horizontal page scrolling.

Exceptions may include:

- Large data tables
- Detailed monitoring layouts
- Specialized examination information

When horizontal scrolling is required:

Provide a clear scrollable container.

Do not allow the entire application viewport to become horizontally scrollable.

---

# Accessibility System

Accessibility is part of the core UI system.

The application should remain usable through:

Keyboard
+
Mouse
+
Touch

---

# Keyboard Navigation

Interactive elements should be reachable through keyboard navigation.

Expected focus order:

Navigation
↓
Page Content
↓
Actions

Do not create keyboard traps.

---

# Focus States

Every interactive control should have a visible focus state.

Applies to:

Buttons
Links
Inputs
Selects
Tabs
Navigation Items
Menus

Focus should not depend only on color.

---

# Semantic HTML

Use appropriate HTML elements.

Examples:

Button:
For actions

Anchor:
For navigation

Label:
For form fields

Heading:
For content hierarchy

Table:
For tabular data

Form:
For user input

Avoid using generic `div` elements when a semantic element is appropriate.

---

# Form Accessibility

Every form input should have:

Visible Label
+
Associated Input

Required fields should be clearly identified.

Validation errors should:

- Identify the affected field
- Explain the problem
- Provide guidance when useful

---

# Error Accessibility

Important errors must be understandable without depending only on color.

Example:

Incorrect:

Red border only

Correct:

Red border
+
Error icon
+
"Password is incorrect."

---

# Status Accessibility

Statuses should use:

Color
+
Text
+
Optional Icon

Example:

Incorrect:

Red dot

Correct:

● Critical