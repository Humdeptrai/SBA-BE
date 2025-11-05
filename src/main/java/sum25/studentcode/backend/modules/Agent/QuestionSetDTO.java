package sum25.studentcode.backend.modules.Agent;

import java.util.List;

public class QuestionSetDTO {
    private List<QuestionDTO> questions;
    private MetadataDTO metadata;

    public List<QuestionDTO> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionDTO> questions) {
        this.questions = questions;
    }

    public MetadataDTO getMetadata() {
        return metadata;
    }

    public void setMetadata(MetadataDTO metadata) {
        this.metadata = metadata;
    }
}
