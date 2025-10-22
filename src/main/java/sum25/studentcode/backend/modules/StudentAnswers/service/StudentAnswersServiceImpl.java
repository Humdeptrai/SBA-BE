package sum25.studentcode.backend.modules.StudentAnswers.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sum25.studentcode.backend.model.Questions;
import sum25.studentcode.backend.model.StudentAnswers;
import sum25.studentcode.backend.model.StudentPractice;
import sum25.studentcode.backend.modules.Questions.repository.QuestionsRepository;
import sum25.studentcode.backend.modules.StudentAnswers.dto.request.StudentAnswersRequest;
import sum25.studentcode.backend.modules.StudentAnswers.dto.response.StudentAnswersResponse;
import sum25.studentcode.backend.modules.StudentAnswers.repository.StudentAnswersRepository;
import sum25.studentcode.backend.modules.StudentPractice.repository.StudentPracticeRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentAnswersServiceImpl implements StudentAnswersService {

    private final StudentAnswersRepository studentAnswersRepository;
    private final StudentPracticeRepository studentPracticeRepository;
    private final QuestionsRepository questionsRepository;

    @Override
    public StudentAnswersResponse createStudentAnswer(StudentAnswersRequest request) {
        StudentPractice studentPractice = studentPracticeRepository.findById(request.getPracticeId())
                .orElseThrow(() -> new RuntimeException("StudentPractice not found"));
        Questions question = questionsRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new RuntimeException("Question not found"));

        boolean isCorrect = false;
        BigDecimal marksEarned = BigDecimal.ZERO;

        // ✅ Nếu là câu hỏi trắc nghiệm
        if (question.getQuestionType() != null &&
                "Multiple Choice".equalsIgnoreCase(question.getQuestionType().getTypeName())) {

            Long correctOptionId = question.getOptions()
                    .stream()
                    .filter(opt -> Boolean.TRUE.equals(opt.getIsCorrect()))
                    .map(opt -> opt.getOptionId())
                    .findFirst()
                    .orElse(null);

            isCorrect = correctOptionId != null && correctOptionId.equals(request.getSelectedOptionId());
        }

        // ✅ Nếu đúng thì tính điểm theo độ khó
        if (isCorrect) {
            if (question.getLevel() != null) {
                marksEarned = BigDecimal.valueOf(question.getLevel().getDifficultyScore());
            } else {
                marksEarned = BigDecimal.valueOf(1); // mặc định 1 điểm nếu chưa có level
            }
        }

        StudentAnswers studentAnswer = StudentAnswers.builder()
                .studentPractice(studentPractice)
                .question(question)
                .selectedOptionId(request.getSelectedOptionId())
                .isCorrect(isCorrect)
                .marksEarned(marksEarned)
                .answeredAt(LocalDateTime.now())
                .build();

        studentAnswer = studentAnswersRepository.save(studentAnswer);
        return convertToResponse(studentAnswer);
    }


    @Override
    public StudentAnswersResponse getStudentAnswerById(Long id) {
        StudentAnswers studentAnswer = studentAnswersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("StudentAnswer not found"));
        return convertToResponse(studentAnswer);
    }

    @Override
    public List<StudentAnswersResponse> getAllStudentAnswers() {
        return studentAnswersRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public StudentAnswersResponse updateStudentAnswer(Long id, StudentAnswersRequest request) {
        StudentAnswers existing = studentAnswersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("StudentAnswer not found"));

        Questions question = questionsRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new RuntimeException("Question not found"));

        boolean isCorrect = false;
        if (question.getQuestionType() != null &&
                "Multiple Choice".equalsIgnoreCase(question.getQuestionType().getTypeName())) {
            Long correctOptionId = question.getOptions()
                    .stream()
                    .filter(opt -> Boolean.TRUE.equals(opt.getIsCorrect()))
                    .map(opt -> opt.getOptionId())
                    .findFirst()
                    .orElse(null);
            isCorrect = correctOptionId != null && correctOptionId.equals(request.getSelectedOptionId());
        }

        // ❌ Không tính điểm ở đây nữa
        existing.setQuestion(question);
        existing.setSelectedOptionId(request.getSelectedOptionId());
        existing.setIsCorrect(isCorrect);
        existing.setMarksEarned(BigDecimal.ZERO);
        existing.setAnsweredAt(LocalDateTime.now());

        studentAnswersRepository.save(existing);
        return convertToResponse(existing);
    }

    @Override
    public void deleteStudentAnswer(Long id) {
        if (!studentAnswersRepository.existsById(id)) {
            throw new RuntimeException("StudentAnswer not found");
        }
        studentAnswersRepository.deleteById(id);
    }

    private StudentAnswersResponse convertToResponse(StudentAnswers entity) {
        StudentAnswersResponse response = new StudentAnswersResponse();
        response.setAnswerId(entity.getAnswerId());
        response.setPracticeId(entity.getStudentPractice().getPracticeId());
        response.setQuestionId(entity.getQuestion().getQuestionId());
        response.setSelectedOptionId(entity.getSelectedOptionId());
        response.setIsCorrect(entity.getIsCorrect());
        response.setMarksEarned(entity.getMarksEarned());
        response.setAnsweredAt(entity.getAnsweredAt());
        return response;
    }
}
