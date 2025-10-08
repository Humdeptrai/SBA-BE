package sum25.studentcode.backend.modules.TeacherMatrix.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sum25.studentcode.backend.model.Matrix;
import sum25.studentcode.backend.model.TeacherMatrix;
import sum25.studentcode.backend.model.User;
import sum25.studentcode.backend.modules.Auth.repository.UserRepository;
import sum25.studentcode.backend.modules.Matrix.repository.MatrixRepository;
import sum25.studentcode.backend.modules.TeacherMatrix.dto.request.TeacherMatrixRequest;
import sum25.studentcode.backend.modules.TeacherMatrix.dto.response.TeacherMatrixResponse;
import sum25.studentcode.backend.modules.TeacherMatrix.repository.TeacherMatrixRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherMatrixServiceImpl implements TeacherMatrixService {

    private final TeacherMatrixRepository teacherMatrixRepository;
    private final UserRepository userRepository;
    private final MatrixRepository matrixRepository;

    @Override
    public TeacherMatrixResponse createTeacherMatrix(TeacherMatrixRequest request) {
        User teacher = userRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        Matrix matrix = matrixRepository.findById(request.getMatrixId())
                .orElseThrow(() -> new RuntimeException("Matrix not found"));
        TeacherMatrix teacherMatrix = TeacherMatrix.builder()
                .teacher(teacher)
                .matrix(matrix)
                .grade(request.getGrade())
                .assignmentDate(request.getAssignmentDate())
                .build();
        teacherMatrix = teacherMatrixRepository.save(teacherMatrix);
        return convertToResponse(teacherMatrix);
    }

    @Override
    public TeacherMatrixResponse getTeacherMatrixById(Long id) {
        TeacherMatrix teacherMatrix = teacherMatrixRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TeacherMatrix not found"));
        return convertToResponse(teacherMatrix);
    }

    @Override
    public List<TeacherMatrixResponse> getAllTeacherMatrices() {
        return teacherMatrixRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TeacherMatrixResponse updateTeacherMatrix(Long id, TeacherMatrixRequest request) {
        TeacherMatrix teacherMatrix = teacherMatrixRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TeacherMatrix not found"));
        User teacher = userRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        Matrix matrix = matrixRepository.findById(request.getMatrixId())
                .orElseThrow(() -> new RuntimeException("Matrix not found"));
        teacherMatrix.setTeacher(teacher);
        teacherMatrix.setMatrix(matrix);
        teacherMatrix.setGrade(request.getGrade());
        teacherMatrix.setAssignmentDate(request.getAssignmentDate());
        teacherMatrix = teacherMatrixRepository.save(teacherMatrix);
        return convertToResponse(teacherMatrix);
    }

    @Override
    public void deleteTeacherMatrix(Long id) {
        if (!teacherMatrixRepository.existsById(id)) {
            throw new RuntimeException("TeacherMatrix not found");
        }
        teacherMatrixRepository.deleteById(id);
    }

    private TeacherMatrixResponse convertToResponse(TeacherMatrix teacherMatrix) {
        TeacherMatrixResponse response = new TeacherMatrixResponse();
        response.setTeacherMatrixId(teacherMatrix.getTeacherMatrixId());
        response.setTeacherId(teacherMatrix.getTeacher().getUserId());
        response.setMatrixId(teacherMatrix.getMatrix().getMatrixId());
        response.setGrade(teacherMatrix.getGrade());
        response.setAssignmentDate(teacherMatrix.getAssignmentDate());
        response.setCreatedAt(teacherMatrix.getCreatedAt());
        response.setUpdatedAt(teacherMatrix.getUpdatedAt());
        return response;
    }
}