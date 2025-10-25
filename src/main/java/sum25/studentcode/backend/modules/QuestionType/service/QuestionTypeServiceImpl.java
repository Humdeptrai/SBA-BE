package sum25.studentcode.backend.modules.QuestionType.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.studentcode.backend.model.QuestionType;
import sum25.studentcode.backend.modules.QuestionType.dto.request.QuestionTypeRequest;
import sum25.studentcode.backend.modules.QuestionType.dto.response.QuestionTypeResponse;
import sum25.studentcode.backend.modules.QuestionType.repository.QuestionTypeRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class QuestionTypeServiceImpl implements QuestionTypeService {

    private final QuestionTypeRepository questionTypeRepository;

    @Override
    public QuestionTypeResponse createQuestionType(QuestionTypeRequest request) {
        if (questionTypeRepository.existsByTypeName(request.getTypeName())) {
            throw new RuntimeException("Question type already exists");
        }

        QuestionType type = QuestionType.builder()
                .typeName(request.getTypeName())
                .description(request.getDescription())
                .enabledAt(request.getEnabledAt() != null ? request.getEnabledAt() : true)
                .build();

        questionTypeRepository.save(type);
        return toResponse(type);
    }

    @Override
    public QuestionTypeResponse updateQuestionType(Long id, QuestionTypeRequest request) {
        QuestionType type = questionTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question type not found"));

        type.setTypeName(request.getTypeName());
        type.setDescription(request.getDescription());
        type.setEnabledAt(request.getEnabledAt());

        questionTypeRepository.save(type);
        return toResponse(type);
    }

    @Override
    public void deleteQuestionType(Long id) {
        if (!questionTypeRepository.existsById(id)) {
            throw new RuntimeException("Question type not found");
        }
        questionTypeRepository.deleteById(id);
    }

    @Override
    public QuestionTypeResponse getQuestionTypeById(Long id) {
        QuestionType type = questionTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question type not found"));
        return toResponse(type);
    }

    @Override
    public List<QuestionTypeResponse> getAllQuestionTypes() {
        return questionTypeRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private QuestionTypeResponse toResponse(QuestionType type) {
        return QuestionTypeResponse.builder()
                .questionTypeId(type.getQuestionTypeId())
                .typeName(type.getTypeName())
                .description(type.getDescription())
                .enabledAt(type.getEnabledAt())
                .createdAt(type.getCreatedAt())
                .updatedAt(type.getUpdatedAt())
                .build();
    }
}
