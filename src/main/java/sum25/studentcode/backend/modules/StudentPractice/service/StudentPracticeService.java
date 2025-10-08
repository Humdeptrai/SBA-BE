package sum25.studentcode.backend.modules.StudentPractice.service;

import sum25.studentcode.backend.modules.StudentPractice.dto.request.StudentPracticeRequest;
import sum25.studentcode.backend.modules.StudentPractice.dto.response.StudentPracticeResponse;

import java.util.List;

public interface StudentPracticeService {
    StudentPracticeResponse createStudentPractice(StudentPracticeRequest request);
    StudentPracticeResponse getStudentPracticeById(Long id);
    List<StudentPracticeResponse> getAllStudentPractices();
    StudentPracticeResponse updateStudentPractice(Long id, StudentPracticeRequest request);
    void deleteStudentPractice(Long id);
}