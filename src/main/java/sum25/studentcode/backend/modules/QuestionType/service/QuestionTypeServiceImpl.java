package sum25.studentcode.backend.modules.QuestionType.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sum25.studentcode.backend.model.QuestionType;
import sum25.studentcode.backend.modules.QuestionType.dto.request.QuestionTypeRequest;
import sum25.studentcode.backend.modules.QuestionType.dto.response.QuestionTypeResponse;
import sum25.studentcode.backend.modules.QuestionType.repository.QuestionTypeRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionTypeServiceImpl implements QuestionTypeService {

    private final QuestionTypeRepository questionTypeRepository;

    @Override
    public QuestionTypeResponse createQuestionType(QuestionTypeRequest request) {
        QuestionType questionType = QuestionType.builder()
                .typeName(request.getTypeName())
                .description(request.getDescription())
                .enabledAt(request.getEnabledAt())
                .build();
        questionType = questionTypeRepository.save(questionType);
        return convertToResponse(questionType);
    }

    @Override
    public QuestionTypeResponse getQuestionTypeById(Long id) {
        QuestionType questionType = questionTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("QuestionType not found"));
        return convertToResponse(questionType);
    }

    @Override
    public List<QuestionTypeResponse> getAllQuestionTypes() {
        return questionTypeRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public QuestionTypeResponse updateQuestionType(Long id, QuestionTypeRequest request) {
        QuestionType questionType = questionTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("QuestionType not found"));
        questionType.setTypeName(request.getTypeName());
        questionType.setDescription(request.getDescription());
        questionType.setEnabledAt(request.getEnabledAt());
        questionType = questionTypeRepository.save(questionType);
        return convertToResponse(questionType);
    }

    @Override
    public void deleteQuestionType(Long id) {
        if (!questionTypeRepository.existsById(id)) {
            throw new RuntimeException("QuestionType not found");
        }
        questionTypeRepository.deleteById(id);
    }

    private QuestionTypeResponse convertToResponse(QuestionType questionType) {
        QuestionTypeResponse response = new QuestionTypeResponse();
        response.setQuestionTypeId(questionType.getQuestionTypeId());
        response.setTypeName(questionType.getTypeName());
        response.setDescription(questionType.getDescription());
        response.setEnabledAt(questionType.getEnabledAt());
        response.setCreatedAt(questionType.getCreatedAt());
        response.setUpdatedAt(questionType.getUpdatedAt());
        return response;
    }
}