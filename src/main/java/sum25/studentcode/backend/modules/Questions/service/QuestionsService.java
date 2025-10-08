package sum25.studentcode.backend.modules.Questions.service;

import sum25.studentcode.backend.modules.Questions.dto.request.QuestionsRequest;
import sum25.studentcode.backend.modules.Questions.dto.response.QuestionsResponse;

import java.util.List;

public interface QuestionsService {
    QuestionsResponse createQuestion(QuestionsRequest request);
    QuestionsResponse getQuestionById(Long id);
    List<QuestionsResponse> getAllQuestions();
    QuestionsResponse updateQuestion(Long id, QuestionsRequest request);
    void deleteQuestion(Long id);
}