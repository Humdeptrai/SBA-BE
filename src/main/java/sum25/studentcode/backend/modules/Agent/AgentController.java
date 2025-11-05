package sum25.studentcode.backend.modules.Agent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/agent")
public class AgentController {

    @Autowired
    private AgentService agentService;

    @PostMapping("/generate/question")
    public ResponseEntity<List<QuestionSetDTO>> generate(@RequestBody Map<String, String> request) {
        List<QuestionSetDTO> result = agentService.sendPostQuestionGenerateRequest(request);
        return ResponseEntity.ok(result);
    }
}
