package sum25.studentcode.backend.modules.StudentAnswers.service;

import sum25.studentcode.backend.modules.StudentAnswers.dto.request.StudentAnswersRequest;
import sum25.studentcode.backend.modules.StudentAnswers.dto.response.StudentAnswersResponse;

import java.util.List;

public interface StudentAnswersService {
    StudentAnswersResponse createStudentAnswer(StudentAnswersRequest request);
    StudentAnswersResponse getStudentAnswerById(Long id);
    List<StudentAnswersResponse> getAllStudentAnswers();
    StudentAnswersResponse updateStudentAnswer(Long id, StudentAnswersRequest request);
    void deleteStudentAnswer(Long id);
    void saveDraftAnswer(StudentAnswersRequest request);
    List<StudentAnswersResponse> getAnswersByPracticeId(Long practiceId);
}