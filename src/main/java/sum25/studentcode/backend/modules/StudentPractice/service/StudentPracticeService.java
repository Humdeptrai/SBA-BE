package sum25.studentcode.backend.modules.StudentPractice.service;

import sum25.studentcode.backend.model.StudentPractice;
import sum25.studentcode.backend.modules.StudentPractice.dto.request.StudentEnrollRequest;
import sum25.studentcode.backend.modules.StudentPractice.dto.request.TeacherGradeRequest;
import sum25.studentcode.backend.modules.StudentPractice.dto.response.PracticeQuestionResponse;
import sum25.studentcode.backend.modules.StudentPractice.dto.response.StudentEnrollResponse;
import sum25.studentcode.backend.modules.StudentPractice.dto.response.StudentPracticeResponse;
import sum25.studentcode.backend.modules.StudentPractice.dto.response.StudentRankingResponse;
import sum25.studentcode.backend.modules.StudentPractice.dto.response.StudentAnswerDetailResponse;

import java.util.List;

public interface StudentPracticeService {
    StudentPracticeResponse getStudentPracticeById(Long id);
    List<StudentPracticeResponse> getAllStudentPractices();
    void deleteStudentPractice(Long id);
    StudentEnrollResponse enrollStudent(StudentEnrollRequest request);
    StudentPracticeResponse submitPractice(Long practiceId); // Học sinh nộp bài
    StudentPracticeResponse gradePractice(Long practiceId, TeacherGradeRequest request); // Giáo viên chấm điểm
    List<PracticeQuestionResponse> getQuestionsForPractice(Long practiceId);


    List<StudentPracticeResponse> getStudentPracticeRecords();

    List<StudentRankingResponse> getRankings(String order);

    List<StudentAnswerDetailResponse> getStudentAnswersDetails(String sessonCode);
}