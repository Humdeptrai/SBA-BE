package giahuypro.jwtrefreshtokenaccesstoken.config;

import giahuypro.jwtrefreshtokenaccesstoken.model.*;
import giahuypro.jwtrefreshtokenaccesstoken.repository.model.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@Profile({"default"})
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final SubjectRepository subjectRepository;
    private final GradeRepository gradeRepository;
    private final LevelRepository levelRepository;
    private final LessonRepository lessonRepository;
    private final QuestionTypeRepository questionTypeRepository;
    private final QuestionsRepository questionsRepository;
    private final OptionsRepository optionsRepository;
    private final ExamRepository examRepository;
    private final MatrixRepository matrixRepository;
    private final MatrixQuestionRepository matrixQuestionRepository;
    private final PracticeSessionRepository practiceSessionRepository;
    private final StudentPracticeRepository studentPracticeRepository;
    private final StudentAnswersRepository studentAnswersRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final AppSettingRepository appSettingRepository;

    public DataSeeder(RoleRepository roleRepository,
                      UserRepository userRepository,
                      UserRoleRepository userRoleRepository,
                      SubjectRepository subjectRepository,
                      GradeRepository gradeRepository,
                      LevelRepository levelRepository,
                      LessonRepository lessonRepository,
                      QuestionTypeRepository questionTypeRepository,
                      QuestionsRepository questionsRepository,
                      OptionsRepository optionsRepository,
                      ExamRepository examRepository,
                      MatrixRepository matrixRepository,
                      MatrixQuestionRepository matrixQuestionRepository,
                      PracticeSessionRepository practiceSessionRepository,
                      StudentPracticeRepository studentPracticeRepository,
                      StudentAnswersRepository studentAnswersRepository,
                      WalletRepository walletRepository,
                      TransactionRepository transactionRepository,
                      AppSettingRepository appSettingRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.subjectRepository = subjectRepository;
        this.gradeRepository = gradeRepository;
        this.levelRepository = levelRepository;
        this.lessonRepository = lessonRepository;
        this.questionTypeRepository = questionTypeRepository;
        this.questionsRepository = questionsRepository;
        this.optionsRepository = optionsRepository;
        this.examRepository = examRepository;
        this.matrixRepository = matrixRepository;
        this.matrixQuestionRepository = matrixQuestionRepository;
        this.practiceSessionRepository = practiceSessionRepository;
        this.studentPracticeRepository = studentPracticeRepository;
        this.studentAnswersRepository = studentAnswersRepository;
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.appSettingRepository = appSettingRepository;
    }

    @Override
    public void run(String... args) {
        // 1. Roles
        Role roleTeacher = roleRepository.findByRoleName("TEACHER").orElseGet(() ->
                roleRepository.save(Role.builder().roleName("TEACHER").description("Teacher role").build()));
        Role roleStudent = roleRepository.findByRoleName("STUDENT").orElseGet(() ->
                roleRepository.save(Role.builder().roleName("STUDENT").description("Student role").build()));

        // 2. Users
        User teacher = userRepository.findByEmail("teacher@example.com").orElseGet(() ->
                userRepository.save(User.builder()
                        .firstName("John").lastName("Teacher")
                        .email("teacher@example.com")
                        .password("password")
                        .build()));
        User student = userRepository.findByEmail("student@example.com").orElseGet(() ->
                userRepository.save(User.builder()
                        .firstName("Jane").lastName("Student")
                        .email("student@example.com")
                        .password("password")
                        .build()));

        // 3. UserRole mappings (idempotent: check existing by scanning)
        ensureUserRole(teacher, roleTeacher);
        ensureUserRole(student, roleStudent);

        // 4. Subject
        Subject math = subjectRepository.findBySubjectCode("MATH101").orElseGet(() ->
                subjectRepository.save(Subject.builder()
                        .subjectName("Mathematics Basics")
                        .subjectCode("MATH101")
                        .creditId(3)
                        .syllabus("Numbers, algebra and geometry basics")
                        .build()));

        // 5. Grade
        Grade grade10 = gradeRepository.existsByGradeLevel("Grade 10")
                ? gradeRepository.findAll().stream().filter(g -> "Grade 10".equals(g.getGradeLevel())).findFirst().orElse(null)
                : gradeRepository.save(Grade.builder().gradeLevel("Grade 10").description("Tenth grade").build());

        // 6. Level
        Level levelEasy = levelRepository.existsByLevelName("EASY")
                ? levelRepository.findAll().stream().filter(l -> "EASY".equals(l.getLevelName())).findFirst().orElse(null)
                : levelRepository.save(Level.builder().levelName("EASY").difficultyScore(1).description("Easy level").build());

        // 7. Lesson
        Lesson lesson1 = lessonRepository.findAll().stream()
                .filter(l -> "Introduction to Numbers".equals(l.getLessonTitle()))
                .findFirst().orElseGet(() ->
                        lessonRepository.save(Lesson.builder()
                                .grade(grade10)
                                .lessonTitle("Introduction to Numbers")
                                .lessonContent("Natural numbers and integers")
                                .lessonObjectives("Understand basic number types")
                                .subject(math)
                                .build())
                );

        // 8. Question Type
        QuestionType mcq = questionTypeRepository.existsByTypeName("MCQ")
                ? questionTypeRepository.findAll().stream().filter(q -> "MCQ".equals(q.getTypeName())).findFirst().orElse(null)
                : questionTypeRepository.save(QuestionType.builder().typeName("MCQ").description("Multiple choice").enabledAt(true).build());

        // 9. Questions
        Questions q1 = questionsRepository.findAll().stream().filter(q ->
                "What is 2 + 2?".equals(q.getQuestionText())).findFirst().orElseGet(() ->
                questionsRepository.save(Questions.builder()
                        .lesson(lesson1)
                        .questionType(mcq)
                        .level(levelEasy)
                        .questionText("What is 2 + 2?")
                        .correctAnswer("4")
                        .explanation("2 plus 2 equals 4")
                        .subject(math)
                        .build()));

        // 10. Options
        if (optionsRepository.findAll().stream().noneMatch(o -> o.getQuestion() != null && o.getQuestion().getQuestionId() != null && o.getQuestion().getQuestionId().equals(q1.getQuestionId()))) {
            optionsRepository.saveAll(List.of(
                    Options.builder().question(q1).optionText("3").isCorrect(false).optionOrder(1).build(),
                    Options.builder().question(q1).optionText("4").isCorrect(true).optionOrder(2).build(),
                    Options.builder().question(q1).optionText("5").isCorrect(false).optionOrder(3).build()
            ));
        }

        // 11. Exam
        Exam midterm = examRepository.findAll().stream().filter(e -> "Math Midterm".equals(e.getExamName())).findFirst().orElseGet(() ->
                examRepository.save(Exam.builder()
                        .examName("Math Midterm")
                        .description("Midterm exam for basics")
                        .durationMinutes(60)
                        .examDate(LocalDateTime.now().plusDays(7))
                        .subject(math)
                        .build()));

        // 12. Matrix
        Matrix matrix = matrixRepository.findAll().stream().filter(m -> "Baseline Matrix".equals(m.getMatrixName())).findFirst().orElseGet(() ->
                matrixRepository.save(Matrix.builder()
                        .exam(midterm)
                        .matrixName("Baseline Matrix")
                        .description("One easy question")
                        .totalQuestions(1)
                        .build()));

        // 13. MatrixQuestion
        if (matrixQuestionRepository.findAll().stream().noneMatch(mq -> mq.getMatrix() != null && mq.getMatrix().getMatrixId().equals(matrix.getMatrixId())
                && mq.getQuestion() != null && mq.getQuestion().getQuestionId().equals(q1.getQuestionId()))) {
            matrixQuestionRepository.save(MatrixQuestion.builder()
                    .matrix(matrix)
                    .question(q1)
                    .marksAllocated(new BigDecimal("1.0"))
                    .build());
        }

        // 14. PracticeSession
        PracticeSession session = practiceSessionRepository.findAll().stream().filter(s -> "PS-0001".equals(s.getSessionCode())).findFirst().orElseGet(() ->
                practiceSessionRepository.save(PracticeSession.builder()
                        .exam(midterm)
                        .student(student)
                        .teacher(teacher)
                        .sessionCode("PS-0001")
                        .sessionName("Practice for Midterm")
                        .startTime(LocalDateTime.now())
                        .endTime(LocalDateTime.now().plusHours(1))
                        .isActive(true)
                        .maxParticipants(30)
                        .build()));

        // 15. StudentPractice
        StudentPractice sp = studentPracticeRepository.findAll().stream().filter(p -> p.getPracticeSession() != null && p.getPracticeSession().getSessionId().equals(session.getSessionId()) && p.getStudent() != null && p.getStudent().getUserId().equals(student.getUserId())).findFirst().orElseGet(() ->
                studentPracticeRepository.save(StudentPractice.builder()
                        .practiceSession(session)
                        .student(student)
                        .perTime(LocalDateTime.now())
                        .submitTime(LocalDateTime.now().plusMinutes(30))
                        .totalScore(new BigDecimal("1.0"))
                        .status("SUBMITTED")
                        .build()));

        // 16. StudentAnswers
        if (studentAnswersRepository.findAll().stream().noneMatch(a -> a.getStudentPractice() != null && a.getStudentPractice().getPracticeId().equals(sp.getPracticeId()))) {
            StudentAnswers ans = new StudentAnswers();
            ans.setStudentPractice(sp);
            ans.setQuestion(q1);
            ans.setSelectedOptionId(null); // free-text question in this seed
            ans.setIsCorrect(true);
            ans.setAnsweredAt(LocalDateTime.now());
            studentAnswersRepository.save(ans);
        }

        // 17. Wallet
        Wallet studentWallet = walletRepository.findByUser_UserId(student.getUserId()).orElseGet(() ->
                walletRepository.save(Wallet.builder()
                        .user(student)
                        .balance(new BigDecimal("100.00"))
                        .currency("USD")
                        .isActive(true)
                        .build()));

        // 18. Transaction
        if (transactionRepository.findAll().isEmpty()) {
            Transaction tx = new Transaction();
            tx.setUser(student);
            tx.setWallet(studentWallet);
            tx.setTransactionType("DEBIT");
            tx.setAmount(new BigDecimal("-10.00"));
            tx.setBalanceBefore(studentWallet.getBalance());
            tx.setBalanceAfter(studentWallet.getBalance().add(new BigDecimal("-10.00")));
            tx.setDescription("Practice session fee");
            tx.setReferenceId("PS-0001");
            tx.setStatus("COMPLETED");
            tx.setTransactionDate(LocalDateTime.now());
            transactionRepository.save(tx);
        }

        // 19. AppSetting
        if (appSettingRepository.findAll().isEmpty()) {
            AppSetting setting = AppSetting.builder()
                    .settingName("site_name")
                    .settingValue("SBA Platform")
                    .description("Application display name")
                    .build();
            appSettingRepository.save(setting);
        }
    }

    private void ensureUserRole(User user, Role role) {
        boolean exists = user.getUserRoles() != null && user.getUserRoles().stream().anyMatch(ur -> role.getRoleId().equals(ur.getRole().getRoleId()));
        if (!exists) {
            UserRole ur = new UserRole();
            ur.setUser(user);
            ur.setRole(role);
            ur.setAssignedAt(LocalDateTime.now());
            userRoleRepository.save(ur);
        }
    }
}
