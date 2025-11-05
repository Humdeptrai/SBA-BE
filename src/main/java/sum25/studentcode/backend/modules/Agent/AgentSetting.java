package sum25.studentcode.backend.modules.Agent;

import org.springframework.stereotype.Component;

@Component
public class AgentSetting {
    private String  AGENT_URL = "https://izffzjus5.tino.page/webhook-test/generate/question";

    public String getAgentUrl() {
        return AGENT_URL;
    }
}
