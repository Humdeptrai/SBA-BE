package sum25.studentcode.backend.modules.Options.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
public class OptionsServiceImpl implements OptionsService {

    private final OptionsRepository optionsRepository;
    private final QuestionsRepository questionsRepository;

    @Override
    public OptionsResponse createOption(OptionsRequest request) {
        Questions question = questionsRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new RuntimeException("Question not found"));
        Options option = Options.builder()
                .question(question)
                .optionText(request.getOptionText())
                .isCorrect(request.getIsCorrect())
                .optionOrder(request.getOptionOrder())
                .build();
        option = optionsRepository.save(option);
        return convertToResponse(option);
    }

    @Override
    public OptionsResponse getOptionById(Long id) {
        Options option = optionsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Option not found"));
        return convertToResponse(option);
    }

    @Override
    public List<OptionsResponse> getAllOptions() {
        return optionsRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OptionsResponse updateOption(Long id, OptionsRequest request) {
        Options option = optionsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Option not found"));
        Questions question = questionsRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new RuntimeException("Question not found"));
        option.setQuestion(question);
        option.setOptionText(request.getOptionText());
        option.setIsCorrect(request.getIsCorrect());
        option.setOptionOrder(request.getOptionOrder());
        option = optionsRepository.save(option);
        return convertToResponse(option);
    }

    @Override
    public void deleteOption(Long id) {
        if (!optionsRepository.existsById(id)) {
            throw new RuntimeException("Option not found");
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