package sum25.studentcode.backend.modules.Questions.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sum25.studentcode.backend.model.*;
import sum25.studentcode.backend.modules.Questions.dto.request.QuestionsRequest;
import sum25.studentcode.backend.modules.Questions.dto.response.QuestionsResponse;
import sum25.studentcode.backend.modules.Questions.repository.QuestionsRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionsServiceImpl implements QuestionsService {

    private final QuestionsRepository questionsRepository;
    // Add other repositories as needed, e.g., LessonRepository, etc.

    @Override
    public QuestionsResponse createQuestion(QuestionsRequest request) {
        // Fetch related entities
        // Lesson lesson = lessonRepository.findById(request.getLessonId()).orElseThrow();
        // etc.
        Questions question = Questions.builder()
                .questionText(request.getQuestionText())
                .correctAnswer(request.getCorrectAnswer())
                .explanation(request.getExplanation())
                // .lesson(lesson)
                // .questionType(questionType)
                // .level(level)
                // .subject(subject)
                .build();
        question = questionsRepository.save(question);
        return convertToResponse(question);
    }

    @Override
    public QuestionsResponse getQuestionById(Long id) {
        Questions question = questionsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        return convertToResponse(question);
    }

    @Override
    public List<QuestionsResponse> getAllQuestions() {
        return questionsRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public QuestionsResponse updateQuestion(Long id, QuestionsRequest request) {
        Questions question = questionsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        question.setQuestionText(request.getQuestionText());
        question.setCorrectAnswer(request.getCorrectAnswer());
        question.setExplanation(request.getExplanation());
        // Update relationships
        question = questionsRepository.save(question);
        return convertToResponse(question);
    }

    @Override
    public void deleteQuestion(Long id) {
        if (!questionsRepository.existsById(id)) {
            throw new RuntimeException("Question not found");
        }
        questionsRepository.deleteById(id);
    }

    private QuestionsResponse convertToResponse(Questions question) {
        QuestionsResponse response = new QuestionsResponse();
        response.setQuestionId(question.getQuestionId());
        response.setQuestionText(question.getQuestionText());
        response.setCorrectAnswer(question.getCorrectAnswer());
        response.setExplanation(question.getExplanation());
        response.setCreatedAt(question.getCreatedAt());
        response.setUpdatedAt(question.getUpdatedAt());
        // Set IDs for relationships
        if (question.getLesson() != null) response.setLessonId(question.getLesson().getLessonId());
        if (question.getQuestionType() != null) response.setQuestionTypeId(question.getQuestionType().getQuestionTypeId());
        if (question.getLevel() != null) response.setLevelId(question.getLevel().getLevelId());
        if (question.getSubject() != null) response.setSubjectId(question.getSubject().getSubjectId());
        return response;
    }
}