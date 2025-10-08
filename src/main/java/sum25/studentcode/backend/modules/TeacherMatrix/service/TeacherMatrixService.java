package sum25.studentcode.backend.modules.TeacherMatrix.service;

import sum25.studentcode.backend.modules.TeacherMatrix.dto.request.TeacherMatrixRequest;
import sum25.studentcode.backend.modules.TeacherMatrix.dto.response.TeacherMatrixResponse;

import java.util.List;

public interface TeacherMatrixService {
    TeacherMatrixResponse createTeacherMatrix(TeacherMatrixRequest request);
    TeacherMatrixResponse getTeacherMatrixById(Long id);
    List<TeacherMatrixResponse> getAllTeacherMatrices();
    TeacherMatrixResponse updateTeacherMatrix(Long id, TeacherMatrixRequest request);
    void deleteTeacherMatrix(Long id);
}