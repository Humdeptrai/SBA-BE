package sum25.studentcode.backend.modules.Agent;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import sum25.studentcode.backend.model.*;
import sum25.studentcode.backend.modules.Lesson.repository.LessonRepository;
import sum25.studentcode.backend.modules.Level.repository.LevelRepository;
import sum25.studentcode.backend.modules.Options.repository.OptionsRepository;
import sum25.studentcode.backend.modules.QuestionType.repository.QuestionTypeRepository;
import sum25.studentcode.backend.modules.Questions.repository.QuestionsRepository;

import java.util.List;
import java.util.Map;

@Service

@RequiredArgsConstructor
public class AgentService {

    @Autowired
    private RestTemplate restTemplate;

    private final QuestionsRepository questionsRepository;
    private final OptionsRepository optionsRepository;
    private final LevelRepository levelRepository;
    private final QuestionTypeRepository questionTypeRepository;
    private final LessonRepository lessonRepository;
    @Autowired
    private AgentSetting agentSetting;

    @Transactional
    public List<QuestionSetDTO> sendPostQuestionGenerateRequest(Map<String, String> requestBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<List<QuestionSetDTO>> response = restTemplate.exchange(
                agentSetting.getAgentUrl(),
                org.springframework.http.HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<List<QuestionSetDTO>>() {
                }
        );

        List<QuestionSetDTO> dtoList = response.getBody();

        for (QuestionSetDTO dto : dtoList) {
            // Save question
            for (QuestionDTO questionDTO : dto.getQuestions()) {
                Level level = levelRepository.findById(Long.parseLong(dto.getMetadata().getLevel()))
                        .orElseThrow(() -> new RuntimeException("Level not found: " + dto.getMetadata().getLevel()));

                Lesson lesson = lessonRepository.findById(Long.parseLong(dto.getMetadata().getLessonID()))
                        .orElseThrow(() -> new RuntimeException("Lesson not found: " + dto.getMetadata().getLessonID()));

                QuestionType questionType = questionTypeRepository.findById(dto.getMetadata().getQuestionTypeId())
                        .orElseThrow(() -> new RuntimeException("QuestionType not found: " + dto.getMetadata().getQuestionTypeId()));

                Questions question = new Questions();
                question.setLevel(level);
                question.setLesson(lesson);
                question.setQuestionText(questionDTO.getQuestion());
                question.setCorrectAnswer(questionDTO.getCorrectAnswer());
                question.setQuestionType(questionType);

                question.setExplanation("" + questionDTO.getExplanation());
                question.setIsActive(true);
                question = questionsRepository.save(question);

                int order = 1;
                for (Map.Entry<String, String> optionEntry : questionDTO.getOptions().entrySet()) {
                    String optionKey = optionEntry.getKey();
                    String optionValue = optionEntry.getValue();

                    // Here you can create and save Option entities associated with the question
                    // For example:
                    Options option = new Options();
                    option.setQuestion(question);
                    option.setOptionText(optionValue);
                    option.setIsCorrect(optionKey.equalsIgnoreCase(questionDTO.getCorrectAnswer()));
                    option.setOptionOrder(order);
                    order++;
                    optionsRepository.save(option);
                }
            }
        }

        return response.getBody();
    }

}
