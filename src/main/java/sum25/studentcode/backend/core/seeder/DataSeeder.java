package sum25.studentcode.backend.core.seeder;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import sum25.studentcode.backend.model.*;
import sum25.studentcode.backend.modules.Auth.repository.UserRepository;
import sum25.studentcode.backend.modules.Grade.repository.GradeRepository;
import sum25.studentcode.backend.modules.Lesson.repository.LessonRepository;
import sum25.studentcode.backend.modules.Level.repository.LevelRepository;
import sum25.studentcode.backend.modules.QuestionType.repository.QuestionTypeRepository;
import sum25.studentcode.backend.modules.Questions.repository.QuestionsRepository;
import sum25.studentcode.backend.modules.Options.repository.OptionsRepository;
import sum25.studentcode.backend.modules.Exam.repository.ExamRepository;
import sum25.studentcode.backend.modules.Matrix.repository.MatrixRepository;
import sum25.studentcode.backend.modules.PracticeSession.repository.PracticeSessionRepository;
import sum25.studentcode.backend.modules.StudentPractice.repository.StudentPracticeRepository;
import sum25.studentcode.backend.modules.StudentAnswers.repository.StudentAnswersRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final GradeRepository gradeRepository;
    private final LessonRepository lessonRepository;
    private final LevelRepository levelRepository;
    private final QuestionTypeRepository questionTypeRepository;
    private final QuestionsRepository questionsRepository;
    private final OptionsRepository optionsRepository;
    private final ExamRepository examRepository;
    private final MatrixRepository matrixRepository;
    private final PracticeSessionRepository practiceSessionRepository;
    private final StudentPracticeRepository studentPracticeRepository;
    private final StudentAnswersRepository studentAnswersRepository;

    @Override
    public void run(String... args) throws Exception {
        PasswordEncoder encoder = new BCryptPasswordEncoder();

        // 1️⃣ USER
        if (userRepository.count() == 0) {
            User teacher = User.builder()
                    .username("admin")
                    .password(encoder.encode("admin123"))
                    .role(Role.TEACHER)
                    .build();
            userRepository.save(teacher);

            User student = User.builder()
                    .username("student")
                    .password(encoder.encode("student123"))
                    .role(Role.STUDENT)
                    .build();
            userRepository.save(student);
        }

        // 2️⃣ GRADE
        if (gradeRepository.count() == 0) {
            gradeRepository.save(Grade.builder()
                    .gradeLevel("Grade 10")
                    .description("Basic level")
                    .build());
        }

        // 3️⃣ LESSON
        if (lessonRepository.count() == 0) {
            Grade grade = gradeRepository.findAll().get(0);
            lessonRepository.save(
                    Lesson.builder()
                            .grade(grade)
                            .lessonTitle("Algebra Basics")
                            .lessonContent("Introduction to equations and variables")
                            .lessonObjectives("Help students understand basic algebra concepts")
                            .build()
            );
        }

        // 4️⃣ LEVEL
        if (levelRepository.count() == 0) {
            levelRepository.save(Level.builder().levelName("Easy").difficultyScore(1).description("Easy level").build());
            levelRepository.save(Level.builder().levelName("Medium").difficultyScore(2).description("Medium level").build());
        }

        // 5️⃣ QUESTION TYPE
        if (questionTypeRepository.count() == 0) {
            questionTypeRepository.save(QuestionType.builder()
                    .typeName("Multiple Choice")
                    .description("Choose one correct answer")
                    .enabledAt(true)
                    .build());
        }

        // 6️⃣ EXAM
        if (examRepository.count() == 0) {
            Lesson lesson = lessonRepository.findAll().get(0);
            examRepository.save(
                    Exam.builder()
                            .lesson(lesson) // ✅ gắn exam với lesson
                            .examName("Math Entrance Test")
                            .examCode("MATH001")
                            .description("Initial placement test for students")
                            .durationMinutes(30)
                            .examDate(LocalDateTime.now().plusDays(3)) // bắt đầu sau 3 ngày
                            .isActive(true)
                            .build()
            );
        }

        // 7️⃣ MATRIX
        if (matrixRepository.count() == 0) {
            Matrix matrix = Matrix.builder()
                    .exam(examRepository.findAll().get(0))
                    .lesson(lessonRepository.findAll().get(0))
                    .matrixName("Math Matrix 1")
                    .description("Basic matrix structure for exam")
                    .totalQuestions(3)
                    .build();
            matrixRepository.save(matrix);
        }

        // 8️⃣ QUESTIONS
        if (questionsRepository.count() == 0) {
            Lesson lesson = lessonRepository.findAll().get(0);
            Level easy = levelRepository.findAll().get(0);
            QuestionType mcq = questionTypeRepository.findAll().get(0);

            Questions q1 = Questions.builder()
                    .lesson(lesson)
                    .level(easy)
                    .questionType(mcq)
                    .questionText("What is 2 + 2?")
                    .correctAnswer("4")
                    .explanation("Basic addition")
                    .build();
            questionsRepository.save(q1);

            Questions q2 = Questions.builder()
                    .lesson(lesson)
                    .level(easy)
                    .questionType(mcq)
                    .questionText("What is 5 - 3?")
                    .correctAnswer("2")
                    .explanation("Basic subtraction")
                    .build();
            questionsRepository.save(q2);
        }

        // 9️⃣ OPTIONS
        if (optionsRepository.count() == 0) {
            Questions q1 = questionsRepository.findAll().get(0);
            optionsRepository.save(Options.builder().question(q1).optionText("3").isCorrect(false).optionOrder(1).build());
            optionsRepository.save(Options.builder().question(q1).optionText("4").isCorrect(true).optionOrder(2).build());

            Questions q2 = questionsRepository.findAll().get(1);
            optionsRepository.save(Options.builder().question(q2).optionText("1").isCorrect(false).optionOrder(1).build());
            optionsRepository.save(Options.builder().question(q2).optionText("2").isCorrect(true).optionOrder(2).build());
        }

        // 🔟 PRACTICE SESSION
        if (practiceSessionRepository.count() == 0) {
            User teacher = userRepository.findByUsername("admin")
                    .orElseThrow(() -> new RuntimeException("Admin user not found"));
            Matrix matrix = matrixRepository.findAll().get(0);

            PracticeSession session = PracticeSession.builder()
                    .matrix(matrix)
                    .sessionName("Math Practice Session 1")
                    .sessionCode("MATH001")
                    .teacher(teacher)
                    .isActive(true)
                    .maxParticipants(30)
                    .currentParticipants(0)
                    .autoClose(true)
                    .build();

            practiceSessionRepository.save(session);
        }

        // 1️⃣1️⃣ STUDENT PRACTICE
        if (studentPracticeRepository.count() == 0) {
            User student = userRepository.findByUsername("student").orElseThrow();
            PracticeSession session = practiceSessionRepository.findAll().get(0);

            StudentPractice practice = StudentPractice.builder()
                    .practiceSession(session)
                    .student(student)
                    .status(StudentPractice.PracticeStatus.IN_PROGRESS)
                    .perTime(LocalDateTime.now())
                    .build();

            studentPracticeRepository.save(practice);
        }

        // 1️⃣2️⃣ STUDENT ANSWER
        if (studentAnswersRepository.count() == 0) {
            StudentPractice practice = studentPracticeRepository.findAll().get(0);
            Questions q1 = questionsRepository.findAll().get(0);

            studentAnswersRepository.save(
                    StudentAnswers.builder()
                            .studentPractice(practice)
                            .question(q1)
                            .selectedOptionId(2L)
                            .isCorrect(true)
                            .marksEarned(BigDecimal.valueOf(10))
                            .answeredAt(LocalDateTime.now())
                            .build()
            );
        }
    }
}
