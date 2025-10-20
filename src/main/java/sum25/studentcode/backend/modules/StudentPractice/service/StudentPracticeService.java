package sum25.studentcode.backend.modules.StudentPractice.service;

import sum25.studentcode.backend.modules.StudentPractice.dto.request.StudentEnrollRequest;
import sum25.studentcode.backend.modules.StudentPractice.dto.request.TeacherGradeRequest;
import sum25.studentcode.backend.modules.StudentPractice.dto.response.StudentEnrollResponse;
import sum25.studentcode.backend.modules.StudentPractice.dto.response.StudentPracticeResponse;

import java.util.List;

public interface StudentPracticeService {
    StudentPracticeResponse getStudentPracticeById(Long id);
    List<StudentPracticeResponse> getAllStudentPractices();
    void deleteStudentPractice(Long id);
    StudentEnrollResponse enrollStudent(StudentEnrollRequest request);
    StudentPracticeResponse submitPractice(Long practiceId); // Học sinh nộp bài
    StudentPracticeResponse gradePractice(Long practiceId, TeacherGradeRequest request); // Giáo viên chấm điểm


}