package de.tum.in.www1.artemis.service;

import static de.tum.in.www1.artemis.web.rest.util.ResponseUtil.*;

import java.time.ZonedDateTime;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import de.tum.in.www1.artemis.domain.Course;
import de.tum.in.www1.artemis.domain.User;
import de.tum.in.www1.artemis.domain.exam.Exam;
import de.tum.in.www1.artemis.domain.exam.StudentExam;
import de.tum.in.www1.artemis.repository.ExamRepository;
import de.tum.in.www1.artemis.repository.StudentExamRepository;

/**
 * Service implementation to check student exam access.
 */
@Service
public class StudentExamAccessService {

    private final CourseService courseService;

    private final UserService userService;

    private final AuthorizationCheckService authorizationCheckService;

    private final ExamRepository examRepository;

    private final StudentExamRepository studentExamRepository;

    public StudentExamAccessService(CourseService courseService, UserService userService, AuthorizationCheckService authorizationCheckService, ExamRepository examRepository,
            StudentExamRepository studentExamRepository) {
        this.courseService = courseService;
        this.userService = userService;
        this.authorizationCheckService = authorizationCheckService;
        this.examRepository = examRepository;
        this.studentExamRepository = studentExamRepository;
    }

    /**
     * Checks if the current user is allowed to see the requested student exam.
     *
     * @param courseId      the if of the course
     * @param examId        the id of the exam
     * @param studentExamId the id of the student exam
     * @param testRun flag to determine if test run
     * @param <T>           The type of the return type of the requesting route so that the
     *                      response can be returned there
     * @return an Optional with a typed ResponseEntity. If it is empty all checks passed
     */
    public <T> Optional<ResponseEntity<T>> checkStudentExamAccess(Long courseId, Long examId, Long studentExamId, boolean testRun) {
        User currentUser = userService.getUserWithGroupsAndAuthorities();
        return checkStudentExamAccess(courseId, examId, studentExamId, currentUser, testRun);
    }

    /**
     * Checks if the current user is allowed to see the requested student exam.
     *
     * @param courseId      the if of the course
     * @param examId        the id of the exam
     * @param studentExamId the id of the student exam
     * @param currentUser   the current user
     * @param testRun       flag to determine if this is a testRun
     * @param <T>           The type of the return type of the requesting route so that the
     *                      response can be returned there
     * @return an Optional with a typed ResponseEntity. If it is empty all checks passed
     */
    public <T> Optional<ResponseEntity<T>> checkStudentExamAccess(Long courseId, Long examId, Long studentExamId, User currentUser, boolean testRun) {
        Optional<ResponseEntity<T>> courseAndExamAccessFailure = checkCourseAndExamAccess(courseId, examId, currentUser, testRun);
        if (courseAndExamAccessFailure.isPresent()) {
            return courseAndExamAccessFailure;
        }

        // Check that the student exam exists
        Optional<StudentExam> studentExam = studentExamRepository.findById(studentExamId);
        if (studentExam.isEmpty()) {
            return Optional.of(notFound());
        }

        // Check that the examId equals the id of the exam of the student exam
        if (!studentExam.get().getExam().getId().equals(examId)) {
            return Optional.of(conflict());
        }

        // Check that the student of the required student exam is the current user
        if (!studentExam.get().getUser().equals(currentUser)) {
            return Optional.of(forbidden());
        }

        return Optional.empty();
    }

    /**
     * Checks if the current user is allowed to access the requested exam.
     *
     * @param courseId      the if of the course
     * @param examId        the id of the exam
     * @param currentUser   the user
     * @param testRun       flag to determine if this is a testRun
     * @param <T>           The type of the return type of the requesting route so that the
     *                      response can be returned there
     * @return an Optional with a typed ResponseEntity. If it is empty all checks passed
     */
    public <T> Optional<ResponseEntity<T>> checkCourseAndExamAccess(Long courseId, Long examId, User currentUser, boolean testRun) {
        // Check that the exam exists
        Optional<Exam> exam = examRepository.findById(examId);
        if (exam.isEmpty()) {
            return Optional.of(notFound());
        }

        // Check that the exam belongs to the course
        if (!exam.get().getCourse().getId().equals(courseId)) {
            return Optional.of(conflict());
        }

        Course course = courseService.findOne(courseId);
        if (testRun) {
            // Check that the current user is at least instructor in the course.
            if (!authorizationCheckService.isAtLeastInstructorInCourse(course, currentUser)) {
                return Optional.of(forbidden());
            }
        }
        else {
            // Check that the current user is at least student in the course.
            if (!authorizationCheckService.isAtLeastStudentInCourse(course, currentUser)) {
                return Optional.of(forbidden());
            }

            // Check that the exam is already visible. After the exam, we directly show the summary!
            if (exam.get().getVisibleDate() != null && (exam.get().getVisibleDate().isAfter(ZonedDateTime.now()))) {
                return Optional.of(forbidden());
            }

            // Check that the current user is registered for the exam
            if (!examRepository.isUserRegisteredForExam(examId, currentUser.getId())) {
                return Optional.of(forbidden());
            }
        }

        return Optional.empty();
    }
}
