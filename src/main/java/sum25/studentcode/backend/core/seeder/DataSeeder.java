package sum25.studentcode.backend.core.seeder;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import sum25.studentcode.backend.model.*;
import sum25.studentcode.backend.modules.AppSetting.repository.AppSettingRepository;
import sum25.studentcode.backend.modules.Auth.repository.UserRepository;
import sum25.studentcode.backend.modules.Exam.repository.ExamRepository;
import sum25.studentcode.backend.modules.Grade.repository.GradeRepository;
import sum25.studentcode.backend.modules.Level.repository.LevelRepository;
import sum25.studentcode.backend.modules.Matrix.repository.MatrixRepository;
import sum25.studentcode.backend.modules.Options.repository.OptionsRepository;
import sum25.studentcode.backend.modules.PracticeSession.repository.PracticeSessionRepository;
import sum25.studentcode.backend.modules.QuestionType.repository.QuestionTypeRepository;
import sum25.studentcode.backend.modules.Questions.repository.QuestionsRepository;
import sum25.studentcode.backend.modules.StudentAnswers.repository.StudentAnswersRepository;
import sum25.studentcode.backend.modules.StudentPractice.repository.StudentPracticeRepository;
import sum25.studentcode.backend.modules.TeacherMatrix.repository.TeacherMatrixRepository;
import sum25.studentcode.backend.modules.Transaction.repository.TransactionRepository;
import sum25.studentcode.backend.modules.Wallet.repository.WalletRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final GradeRepository gradeRepository;
    private final LevelRepository levelRepository;
    private final QuestionTypeRepository questionTypeRepository;
    private final ExamRepository examRepository;
    private final MatrixRepository matrixRepository;
    private final QuestionsRepository questionsRepository;
    private final OptionsRepository optionsRepository;
    private final PracticeSessionRepository practiceSessionRepository;
    private final StudentPracticeRepository studentPracticeRepository;
    private final StudentAnswersRepository studentAnswersRepository;
    private final TeacherMatrixRepository teacherMatrixRepository;
    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final AppSettingRepository appSettingRepository;

    @Override
    public void run(String... args) throws Exception {
        // Seed users
        if (userRepository.count() == 0) {
            User admin = User.builder()
                    .username("admin")
                    .password("$2a$10$exampleHashedPassword") // Use BCryptPasswordEncoder to hash
                    .role(Role.TEACHER)
                    .build();
            userRepository.save(admin);

            User student = User.builder()
                    .username("student")
                    .password("$2a$10$exampleHashedPassword")
                    .role(Role.STUDENT)
                    .build();
            userRepository.save(student);
        }

        // Seed grades
        if (gradeRepository.count() == 0) {
            Grade grade10 = Grade.builder()
                    .gradeLevel("Grade 10")
                    .description("10th grade")
                    .build();
            gradeRepository.save(grade10);

            Grade grade11 = Grade.builder()
                    .gradeLevel("Grade 11")
                    .description("11th grade")
                    .build();
            gradeRepository.save(grade11);
        }

        // Seed levels
        if (levelRepository.count() == 0) {
            Level easy = Level.builder()
                    .levelName("Easy")
                    .difficultyScore(1)
                    .description("Easy level")
                    .build();
            levelRepository.save(easy);

            Level medium = Level.builder()
                    .levelName("Medium")
                    .difficultyScore(2)
                    .description("Medium level")
                    .build();
            levelRepository.save(medium);
        }

        // Seed question types
        if (questionTypeRepository.count() == 0) {
            QuestionType multipleChoice = QuestionType.builder()
                    .typeName("Multiple Choice")
                    .description("Multiple choice questions")
                    .enabledAt(true)
                    .build();
            questionTypeRepository.save(multipleChoice);

            QuestionType trueFalse = QuestionType.builder()
                    .typeName("True/False")
                    .description("True or false questions")
                    .enabledAt(true)
                    .build();
            questionTypeRepository.save(trueFalse);
        }

        // Seed exams (depends on subjects)
        if (examRepository.count() == 0) {
            Exam mathExam = Exam.builder()
                    .examName("Math Exam 1")
                    .description("First math exam")
                    .durationMinutes(60)
                    .examDate(LocalDateTime.now().plusDays(7))
                    .build();
            examRepository.save(mathExam);
        }

        // Seed matrices (depends on exams)
        if (matrixRepository.count() == 0) {
            Exam exam = examRepository.findAll().get(0);
            Matrix matrix = Matrix.builder()
                    .exam(exam)
                    .matrixName("Math Matrix 1")
                    .description("Matrix for math exam")
                    .totalQuestions(10)
                    .build();
            matrixRepository.save(matrix);
        }

        // Seed questions (depends on subjects, levels, questionTypes)
        if (questionsRepository.count() == 0) {
            Level easy = levelRepository.findAll().stream()
                    .filter(l -> "Easy".equals(l.getLevelName()))
                    .findFirst().orElseThrow();
            QuestionType mc = questionTypeRepository.findAll().stream()
                    .filter(qt -> "Multiple Choice".equals(qt.getTypeName()))
                    .findFirst().orElseThrow();

            Questions question = Questions.builder()
                    .questionText("What is 2 + 2?")
                    .correctAnswer("4")
                    .questionType(mc)
                    .build();
            questionsRepository.save(question);
        }

        // Seed options (depends on questions)
        if (optionsRepository.count() == 0) {
            Questions question = questionsRepository.findAll().get(0);
            Options option1 = Options.builder()
                    .question(question)
                    .optionText("3")
                    .isCorrect(false)
                    .optionOrder(1)
                    .build();
            optionsRepository.save(option1);

            Options option2 = Options.builder()
                    .question(question)
                    .optionText("4")
                    .isCorrect(true)
                    .optionOrder(2)
                    .build();
            optionsRepository.save(option2);
        }

        // Seed practice sessions (depends on exams, users)
        if (practiceSessionRepository.count() == 0) {
            Exam exam = examRepository.findAll().get(0);
            User student = userRepository.findAll().stream()
                    .filter(u -> "student".equals(u.getUsername()))
                    .findFirst().orElseThrow();
            User teacher = userRepository.findAll().stream()
                    .filter(u -> "admin".equals(u.getUsername()))
                    .findFirst().orElseThrow();

            PracticeSession session = PracticeSession.builder()
                    .exam(exam)
                    .student(student)
                    .sessionCode("SESSION001")
                    .teacher(teacher)
                    .sessionName("Practice Session 1")
                    .startTime(LocalDateTime.now())
                    .endTime(LocalDateTime.now().plusHours(1))
                    .isActive(true)
                    .maxParticipants(50)
                    .build();
            practiceSessionRepository.save(session);
        }

        // Seed student practices (depends on practice sessions, users)
        if (studentPracticeRepository.count() == 0) {
            PracticeSession session = practiceSessionRepository.findAll().get(0);
            User student = userRepository.findAll().stream()
                    .filter(u -> "student".equals(u.getUsername()))
                    .findFirst().orElseThrow();

            StudentPractice practice = StudentPractice.builder()
                    .practiceSession(session)
                    .student(student)
                    .perTime(LocalDateTime.now())
                    .submitTime(LocalDateTime.now().plusMinutes(30))
                    .totalScore(BigDecimal.valueOf(85.0))
                    .status("Completed")
                    .build();
            studentPracticeRepository.save(practice);
        }

        // Seed student answers (depends on student practices, questions)
        if (studentAnswersRepository.count() == 0) {
            StudentPractice practice = studentPracticeRepository.findAll().get(0);
            Questions question = questionsRepository.findAll().get(0);

            StudentAnswers answer = StudentAnswers.builder()
                    .studentPractice(practice)
                    .question(question)
                    .selectedOptionId(2L) // Assuming option 2 is correct
                    .isCorrect(true)
                    .marksEarned(BigDecimal.valueOf(10.0))
                    .answeredAt(LocalDateTime.now())
                    .build();
            studentAnswersRepository.save(answer);
        }

        // Seed teacher matrices (depends on users, matrices)
        if (teacherMatrixRepository.count() == 0) {
            User teacher = userRepository.findAll().stream()
                    .filter(u -> "admin".equals(u.getUsername()))
                    .findFirst().orElseThrow();
            Matrix matrix = matrixRepository.findAll().get(0);

            TeacherMatrix tm = TeacherMatrix.builder()
                    .teacher(teacher)
                    .matrix(matrix)
                    .grade(BigDecimal.valueOf(90.0))
                    .assignmentDate(LocalDateTime.now())
                    .build();
            teacherMatrixRepository.save(tm);
        }

        // Seed wallets (depends on users)
        if (walletRepository.count() == 0) {
            User student = userRepository.findAll().stream()
                    .filter(u -> "student".equals(u.getUsername()))
                    .findFirst().orElseThrow();

            Wallet wallet = Wallet.builder()
                    .user(student)
                    .balance(BigDecimal.valueOf(100.0))
                    .currency("USD")
                    .isActive(true)
                    .build();
            walletRepository.save(wallet);
        }

        // Seed transactions (depends on wallets, users)
        if (transactionRepository.count() == 0) {
            Wallet wallet = walletRepository.findAll().get(0);
            User student = userRepository.findAll().stream()
                    .filter(u -> "student".equals(u.getUsername()))
                    .findFirst().orElseThrow();

            Transaction transaction = Transaction.builder()
                    .wallet(wallet)
                    .user(student)
                    .transactionType("Deposit")
                    .amount(BigDecimal.valueOf(50.0))
                    .balanceBefore(BigDecimal.valueOf(50.0))
                    .balanceAfter(BigDecimal.valueOf(100.0))
                    .description("Initial deposit")
                    .referenceId("REF001")
                    .status("Completed")
                    .transactionDate(LocalDateTime.now())
                    .build();
            transactionRepository.save(transaction);
        }

        // Seed app settings
        if (appSettingRepository.count() == 0) {
            AppSetting setting = AppSetting.builder()
                    .settingName("app.version")
                    .settingValue("1.0.0")
                    .description("Application version")
                    .build();
            appSettingRepository.save(setting);
        }
    }
}