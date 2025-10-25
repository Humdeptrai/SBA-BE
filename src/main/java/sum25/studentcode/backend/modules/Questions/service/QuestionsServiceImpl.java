package sum25.studentcode.backend.modules.Questions.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.studentcode.backend.model.*;
import sum25.studentcode.backend.modules.Lesson.repository.LessonRepository;
import sum25.studentcode.backend.modules.Level.repository.LevelRepository;
import sum25.studentcode.backend.modules.Options.dto.response.OptionsResponse;
import sum25.studentcode.backend.modules.Options.repository.OptionsRepository;
import sum25.studentcode.backend.modules.QuestionType.repository.QuestionTypeRepository;
import sum25.studentcode.backend.modules.Questions.dto.request.QuestionsRequest;
import sum25.studentcode.backend.modules.Questions.dto.response.QuestionsResponse;
import sum25.studentcode.backend.modules.Questions.repository.QuestionsRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class QuestionsServiceImpl implements QuestionsService {

    private final QuestionsRepository questionsRepository;
    private final LessonRepository lessonRepository;
    private final LevelRepository levelRepository;
    private final QuestionTypeRepository questionTypeRepository;
    private final OptionsRepository optionsRepository;

    @Override
    public QuestionsResponse createQuestion(QuestionsRequest request) {
        Lesson lesson = lessonRepository.findById(request.getLessonId())
                .orElseThrow(() -> new RuntimeException("Lesson not found"));
        Level level = levelRepository.findById(request.getLevelId())
                .orElseThrow(() -> new RuntimeException("Level not found"));
        QuestionType questionType = questionTypeRepository.findById(request.getQuestionTypeId())
                .orElseThrow(() -> new RuntimeException("Question type not found"));

        Questions question = Questions.builder()
                .questionText(request.getQuestionText())
                .correctAnswer(request.getCorrectAnswer())
                .explanation(request.getExplanation())
                .lesson(lesson)
                .level(level)
                .questionType(questionType)
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

        if (request.getQuestionText() != null) question.setQuestionText(request.getQuestionText());
        if (request.getCorrectAnswer() != null) question.setCorrectAnswer(request.getCorrectAnswer());
        if (request.getExplanation() != null) question.setExplanation(request.getExplanation());

        if (request.getLessonId() != null) {
            Lesson lesson = lessonRepository.findById(request.getLessonId())
                    .orElseThrow(() -> new RuntimeException("Lesson not found"));
            question.setLesson(lesson);
        }

        if (request.getLevelId() != null) {
            Level level = levelRepository.findById(request.getLevelId())
                    .orElseThrow(() -> new RuntimeException("Level not found"));
            question.setLevel(level);
        }

        if (request.getQuestionTypeId() != null) {
            QuestionType questionType = questionTypeRepository.findById(request.getQuestionTypeId())
                    .orElseThrow(() -> new RuntimeException("Question type not found"));
            question.setQuestionType(questionType);
        }

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

    @Override
    public List<OptionsResponse> getOptionsByQuestionId(Long questionId) {
        return optionsRepository.findByQuestion_QuestionId(questionId).stream()
                .map(option -> {
                    OptionsResponse res = new OptionsResponse();
                    res.setOptionId(option.getOptionId());
                    res.setQuestionId(option.getQuestion().getQuestionId());
                    res.setOptionText(option.getOptionText());
                    res.setIsCorrect(option.getIsCorrect());
                    res.setOptionOrder(option.getOptionOrder());
                    res.setCreatedAt(option.getCreatedAt());
                    res.setUpdatedAt(option.getUpdatedAt());
                    return res;
                }).collect(Collectors.toList());
    }

    private QuestionsResponse convertToResponse(Questions question) {
        QuestionsResponse response = new QuestionsResponse();
        response.setQuestionId(question.getQuestionId());
        response.setQuestionText(question.getQuestionText());
        response.setCorrectAnswer(question.getCorrectAnswer());
        response.setExplanation(question.getExplanation());
        response.setCreatedAt(question.getCreatedAt());
        response.setUpdatedAt(question.getUpdatedAt());
        if (question.getLesson() != null) response.setLessonId(question.getLesson().getLessonId());
        if (question.getLevel() != null) response.setLevelId(question.getLevel().getLevelId());
        if (question.getQuestionType() != null) response.setQuestionTypeId(question.getQuestionType().getQuestionTypeId());
        return response;
    }
}
