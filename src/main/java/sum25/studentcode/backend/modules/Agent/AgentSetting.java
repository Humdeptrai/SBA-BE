package sum25.studentcode.backend.modules.Agent;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class AgentSetting {
    private final String  AGENT_URL = "https://izffzjus5.tino.page/webhook/generate/question";
    private final String AGENT_MATRIX = "https://izffzjus5.tino.page/webhook/generate/question/matrix";
    private final BigDecimal COST_PER_REQUEST = BigDecimal.valueOf(50000); // Example cost per request
    public String getAgentUrl() {
        return AGENT_URL;
    }

    public BigDecimal getCostPerRequest() {
        return COST_PER_REQUEST;
    }
}
