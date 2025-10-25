package sum25.studentcode.backend.modules.Options.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.studentcode.backend.core.exception.ApiException;
import sum25.studentcode.backend.model.Options;
import sum25.studentcode.backend.model.Questions;
import sum25.studentcode.backend.modules.Options.dto.request.OptionsRequest;
import sum25.studentcode.backend.modules.Options.dto.response.OptionsResponse;
import sum25.studentcode.backend.modules.Options.repository.OptionsRepository;
import sum25.studentcode.backend.modules.Questions.repository.QuestionsRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OptionsServiceImpl implements OptionsService {

    private final OptionsRepository optionsRepository;
    private final QuestionsRepository questionsRepository;

    @Override
    public OptionsResponse createOption(OptionsRequest request) {
        Questions question = questionsRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new ApiException("QUESTION_NOT_FOUND", "Không tìm thấy câu hỏi.", 404));

        // ✅ Kiểm tra trùng optionText trong cùng 1 question
        boolean duplicateText = optionsRepository.existsByQuestionAndOptionTextIgnoreCase(question, request.getOptionText());
        if (duplicateText) {
            throw new ApiException(
                    "DUPLICATE_OPTION_TEXT",
                    "Đáp án \"" + request.getOptionText() + "\" đã tồn tại trong câu hỏi này.",
                    400
            );
        }

        // ✅ Kiểm tra trùng optionOrder trong cùng 1 question
        boolean duplicateOrder = optionsRepository.existsByQuestionAndOptionOrder(question, request.getOptionOrder());
        if (duplicateOrder) {
            throw new ApiException(
                    "DUPLICATE_OPTION_ORDER",
                    "Thứ tự đáp án (optionOrder=" + request.getOptionOrder() + ") đã được sử dụng trong câu hỏi này.",
                    400
            );
        }

        Options option = Options.builder()
                .question(question)
                .optionText(request.getOptionText().trim())
                .isCorrect(request.getIsCorrect())
                .optionOrder(request.getOptionOrder())
                .build();

        optionsRepository.save(option);
        return convertToResponse(option);
    }

    @Override
    public OptionsResponse getOptionById(Long id) {
        Options option = optionsRepository.findById(id)
                .orElseThrow(() -> new ApiException("OPTION_NOT_FOUND", "Không tìm thấy đáp án.", 404));
        return convertToResponse(option);
    }

    @Override
    public List<OptionsResponse> getAllOptions() {
        List<Options> list = optionsRepository.findAll();
        if (list.isEmpty()) {
            throw new ApiException("EMPTY_LIST", "Chưa có đáp án nào trong hệ thống.", 404);
        }
        return list.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    public OptionsResponse updateOption(Long id, OptionsRequest request) {
        Options option = optionsRepository.findById(id)
                .orElseThrow(() -> new ApiException("OPTION_NOT_FOUND", "Không tìm thấy đáp án.", 404));

        Questions question = questionsRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new ApiException("QUESTION_NOT_FOUND", "Không tìm thấy câu hỏi.", 404));

        // ✅ Kiểm tra trùng optionText (trừ chính option hiện tại)
        boolean duplicateText = optionsRepository.existsByQuestionAndOptionTextIgnoreCaseAndOptionIdNot(
                question, request.getOptionText(), id
        );
        if (duplicateText) {
            throw new ApiException(
                    "DUPLICATE_OPTION_TEXT",
                    "Đáp án \"" + request.getOptionText() + "\" đã tồn tại trong câu hỏi này.",
                    400
            );
        }

        // ✅ Kiểm tra trùng optionOrder (trừ chính option hiện tại)
        boolean duplicateOrder = optionsRepository.existsByQuestionAndOptionOrderAndOptionIdNot(
                question, request.getOptionOrder(), id
        );
        if (duplicateOrder) {
            throw new ApiException(
                    "DUPLICATE_OPTION_ORDER",
                    "Thứ tự đáp án (optionOrder=" + request.getOptionOrder() + ") đã được sử dụng trong câu hỏi này.",
                    400
            );
        }

        option.setQuestion(question);
        option.setOptionText(request.getOptionText().trim());
        option.setIsCorrect(request.getIsCorrect());
        option.setOptionOrder(request.getOptionOrder());

        optionsRepository.save(option);
        return convertToResponse(option);
    }

    @Override
    public void deleteOption(Long id) {
        if (!optionsRepository.existsById(id)) {
            throw new ApiException("OPTION_NOT_FOUND", "Không tìm thấy đáp án.", 404);
        }
        optionsRepository.deleteById(id);
    }

    private OptionsResponse convertToResponse(Options option) {
        OptionsResponse response = new OptionsResponse();
        response.setOptionId(option.getOptionId());
        response.setQuestionId(option.getQuestion().getQuestionId());
        response.setOptionText(option.getOptionText());
        response.setIsCorrect(option.getIsCorrect());
        response.setOptionOrder(option.getOptionOrder());
        response.setCreatedAt(option.getCreatedAt());
        response.setUpdatedAt(option.getUpdatedAt());
        return response;
    }
}
