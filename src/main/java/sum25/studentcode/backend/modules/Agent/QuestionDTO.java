package sum25.studentcode.backend.modules.Agent;

import lombok.Getter;
import lombok.Setter;
import sum25.studentcode.backend.modules.Questions.dto.request.QuestionsRequest;

import java.util.Map;

@Getter
@Setter
public class QuestionDTO {
    private int questionId;
    private String question;
    private Map<String, String> options;
    private String correctAnswer;
    private QuestionsRequest.KnowledgeLevel knowledgeLevel;
    private String explanation;

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public void setOptions(Map<String, String> options) {
        this.options = options;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}
