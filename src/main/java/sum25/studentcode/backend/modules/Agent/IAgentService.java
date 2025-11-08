package sum25.studentcode.backend.modules.Agent;

import java.util.List;
import java.util.Map;

public interface IAgentService {
    List<QuestionSetDTO> sendPostQuestionGenerateRequest(Map<String, String> requestBody);
}
