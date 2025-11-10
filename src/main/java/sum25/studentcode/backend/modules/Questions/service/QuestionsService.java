    package sum25.studentcode.backend.modules.Questions.service;

    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.Pageable;
    import sum25.studentcode.backend.modules.Options.dto.response.OptionsResponse;
    import sum25.studentcode.backend.modules.Questions.dto.request.QuestionsRequest;
    import sum25.studentcode.backend.modules.Questions.dto.response.QuestionsResponse;

    import java.util.List;

    public interface QuestionsService {
        QuestionsResponse createQuestion(QuestionsRequest request);
        QuestionsResponse getQuestionById(Long id);
        List<QuestionsResponse> getAllQuestions(Pageable pageable , Long userId);
        QuestionsResponse updateQuestion(Long id, QuestionsRequest request);
        void deleteQuestion(Long id);
        List<OptionsResponse> getOptionsByQuestionId(Long questionId);
        List<QuestionsResponse> getQuestionsByLessonId(Long lessonId);
        Page<QuestionsResponse> getQuestionForMatrixWithUniqueByLevelName(String levelName,Long lessonId, int page, int size);

    }