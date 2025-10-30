package sum25.studentcode.backend.modules.Exam.service;

import sum25.studentcode.backend.modules.Exam.dto.request.ExamRequest;
import sum25.studentcode.backend.modules.Exam.dto.response.ExamResponse;

import java.util.List;

public interface ExamService {
    ExamResponse createExam(ExamRequest request);
    ExamResponse getExamById(Long id);
    List<ExamResponse> getAllExams();
    ExamResponse updateExam(Long id, ExamRequest request);
    void deleteExam(Long id);
    List<ExamResponse> getExamsByLesson(Long lessonId);

}