package sum25.studentcode.backend.modules.QuestionType.service;

import sum25.studentcode.backend.modules.QuestionType.dto.request.QuestionTypeRequest;
import sum25.studentcode.backend.modules.QuestionType.dto.response.QuestionTypeResponse;

import java.util.List;

public interface QuestionTypeService {
    QuestionTypeResponse createQuestionType(QuestionTypeRequest request);
    QuestionTypeResponse getQuestionTypeById(Long id);
    List<QuestionTypeResponse> getAllQuestionTypes();
    QuestionTypeResponse updateQuestionType(Long id, QuestionTypeRequest request);
    void deleteQuestionType(Long id);
}