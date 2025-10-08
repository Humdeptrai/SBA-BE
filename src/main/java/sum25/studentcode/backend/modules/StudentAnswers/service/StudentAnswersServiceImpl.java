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
        StudentAnswers studentAnswer = StudentAnswers.builder()
                .studentPractice(studentPractice)
                .question(question)
                .selectedOptionId(request.getSelectedOptionId())
                .isCorrect(request.getIsCorrect())
                .marksEarned(request.getMarksEarned())
                .answeredAt(request.getAnsweredAt())
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
        StudentAnswers studentAnswer = studentAnswersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("StudentAnswer not found"));
        StudentPractice studentPractice = studentPracticeRepository.findById(request.getPracticeId())
                .orElseThrow(() -> new RuntimeException("StudentPractice not found"));
        Questions question = questionsRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new RuntimeException("Question not found"));
        studentAnswer.setStudentPractice(studentPractice);
        studentAnswer.setQuestion(question);
        studentAnswer.setSelectedOptionId(request.getSelectedOptionId());
        studentAnswer.setIsCorrect(request.getIsCorrect());
        studentAnswer.setMarksEarned(request.getMarksEarned());
        studentAnswer.setAnsweredAt(request.getAnsweredAt());
        studentAnswer = studentAnswersRepository.save(studentAnswer);
        return convertToResponse(studentAnswer);
    }

    @Override
    public void deleteStudentAnswer(Long id) {
        if (!studentAnswersRepository.existsById(id)) {
            throw new RuntimeException("StudentAnswer not found");
        }
        studentAnswersRepository.deleteById(id);
    }

    private StudentAnswersResponse convertToResponse(StudentAnswers studentAnswer) {
        StudentAnswersResponse response = new StudentAnswersResponse();
        response.setAnswerId(studentAnswer.getAnswerId());
        response.setPracticeId(studentAnswer.getStudentPractice().getPracticeId());
        response.setQuestionId(studentAnswer.getQuestion().getQuestionId());
        response.setSelectedOptionId(studentAnswer.getSelectedOptionId());
        response.setIsCorrect(studentAnswer.getIsCorrect());
        response.setMarksEarned(studentAnswer.getMarksEarned());
        response.setAnsweredAt(studentAnswer.getAnsweredAt());
        return response;
    }
}