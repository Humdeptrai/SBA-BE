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
import sum25.studentcode.backend.modules.Questions.dto.request.QuestionsRequest;
import sum25.studentcode.backend.modules.Questions.repository.QuestionsRepository;
import sum25.studentcode.backend.modules.Options.repository.OptionsRepository;
import sum25.studentcode.backend.modules.Matrix.repository.MatrixRepository;
import sum25.studentcode.backend.modules.PracticeSession.repository.PracticeSessionRepository;
import sum25.studentcode.backend.modules.StudentPractice.repository.StudentPracticeRepository;
import sum25.studentcode.backend.modules.StudentAnswers.repository.StudentAnswersRepository;
import sum25.studentcode.backend.modules.Wallet.repository.WalletRepository;
import sum25.studentcode.backend.modules.Lesson.repository.LessonCollectionRepository;
import sum25.studentcode.backend.modules.Lesson.repository.LessonFileRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
    private final MatrixRepository matrixRepository;
    private final PracticeSessionRepository practiceSessionRepository;
    private final StudentPracticeRepository studentPracticeRepository;
    private final StudentAnswersRepository studentAnswersRepository;
    private final WalletRepository walletRepository;
    private final LessonCollectionRepository lessonCollectionRepository;
    private final LessonFileRepository lessonFileRepository;
    @Override
    public void run(String... args) throws Exception {
        PasswordEncoder encoder = new BCryptPasswordEncoder();

        if (userRepository.count() == 0) {
            User teacher = User.builder()
                    .username("teacher")
                    .password(encoder.encode("teacher123"))
                    .role(Role.TEACHER)
                    .build();
            userRepository.save(teacher);

            Wallet wallet = Wallet.builder()
                    .user(teacher)
                    .balance(BigDecimal.valueOf(1111000.00)) // Số dư ban đầu 1000 VND
                    .currency("VND")
                    .isActive(true)
                    .build();
            walletRepository.save(wallet);

            User admin = User.builder()
                    .username("admin")
                    .password(encoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);

            User student = User.builder()
                    .username("student")
                    .password(encoder.encode("student123"))
                    .role(Role.STUDENT)
                    .build();
            userRepository.save(student);
        }

        if (gradeRepository.count() == 0) {
            gradeRepository.save(Grade.builder()
                    .gradeLevel("Grade 10")
                    .description("Basic level")
                    .build());
        }

        if (lessonRepository.count() == 0) {
            Grade grade = gradeRepository.findAll().get(0);
            lessonRepository.save(
                    Lesson.builder()
                            .grade(grade)
                            .lessonTitle("English Basics")
                            .lessonContent("Introduction to English vocabulary and grammar")
                            .lessonObjectives("Help students understand basic English concepts")
                            .thumbnailUrl("https://example.com/thumbnail.jpg")
                            .build()
            );
        }

        if (lessonCollectionRepository.count() == 0) {
            User teacher = userRepository.findByUsername("teacher").orElseThrow();
            Lesson lesson = lessonRepository.findAll().get(0);
            lessonCollectionRepository.save(
                    LessonCollection.builder()
                            .collectionName("Unit 1: My School")
                            .description("Basic English lessons for beginners")
                            .createdBy(teacher)
                            .lessons(List.of(lesson))
                            .build()
            );
        }

        if (lessonFileRepository.count() == 0) {
            Lesson lesson = lessonRepository.findAll().get(0);
            lessonFileRepository.save(
                    LessonFile.builder()
                            .lessonId(lesson.getLessonId().toString())
                            .fileName("sample.pdf")
                            .fileType("application/pdf")
                            .data(null) // No data for seeding
                            .displayOrder(1)
                            .build()
            );
        }

        if (levelRepository.count() == 0) {
            levelRepository.save(Level.builder()
                    .levelName("Easy")
                    .difficultyScore(1.0)
                    .description("Easy level")
                    .build());
            levelRepository.save(Level.builder()
                    .levelName("Medium")
                    .difficultyScore(2.0)
                    .description("Medium level")
                    .build());
        }

        if (questionTypeRepository.count() == 0) {
            questionTypeRepository.save(QuestionType.builder()
                    .typeName("Multiple Choice")
                    .description("Choose one correct answer")
                    .enabledAt(true)
                    .build());
        }

        if (matrixRepository.count() == 0) {
            Matrix matrix = Matrix.builder()
                    .matrixName("English Grammar 1")
                    .description("Basic English grammar structure")
                    .totalQuestions(2)
                    .totalMarks(BigDecimal.valueOf(100))
                    .build();
            matrixRepository.save(matrix);
        }

        if (questionsRepository.count() == 0) {
            Lesson lesson = lessonRepository.findAll().get(0);
            Level easy = levelRepository.findAll().get(0);
            QuestionType mcq = questionTypeRepository.findAll().get(0);

            Questions q1 = Questions.builder()
                    .lesson(lesson)
                    .level(easy)
                    .questionType(mcq)
                    .questionText("What is the synonym of 'happy'?")
                    .correctAnswer("joyful")
                    .knowledgeLevel(QuestionsRequest.KnowledgeLevel.APPLY)
                    .explanation("Basic vocabulary")
                    .build();
            questionsRepository.save(q1);

            Questions q2 = Questions.builder()
                    .lesson(lesson)
                    .level(easy)
                    .questionType(mcq)
                    .questionText("Choose the correct form: She ___ to school.")
                    .correctAnswer("goes")
                    .knowledgeLevel(QuestionsRequest.KnowledgeLevel.RECALL)
                    .explanation("Present simple tense")
                    .build();
            questionsRepository.save(q2);
        }

        if (optionsRepository.count() == 0) {
            Questions q1 = questionsRepository.findAll().get(0);
            optionsRepository.save(Options.builder()
                    .question(q1).optionText("sad").isCorrect(false).optionOrder(1).build());
            optionsRepository.save(Options.builder()
                    .question(q1).optionText("joyful").isCorrect(true).optionOrder(2).build());

            Questions q2 = questionsRepository.findAll().get(1);
            optionsRepository.save(Options.builder()
                    .question(q2).optionText("go").isCorrect(false).optionOrder(1).build());
            optionsRepository.save(Options.builder()
                    .question(q2).optionText("goes").isCorrect(true).optionOrder(2).build());
        }

        if (practiceSessionRepository.count() == 0) {
            User teacher = userRepository.findByUsername("teacher")
                    .orElseThrow(() -> new RuntimeException("Admin user not found"));
            Matrix matrix = matrixRepository.findAll().get(0);

            PracticeSession session = PracticeSession.builder()
                    .matrix(matrix)
                    .sessionName("English Practice Session 1")
                    .sessionCode("ENGLISH001")
                    .teacher(teacher)
                    .isActive(true)
                    .maxParticipants(30)
                    .currentParticipants(0)
                    .autoClose(true)
                    .examDate(LocalDateTime.now().plusMinutes(1)) // bắt đầu sau 1 phút
                    .durationMinutes(30)
                    .build();

            practiceSessionRepository.save(session);
        }

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
