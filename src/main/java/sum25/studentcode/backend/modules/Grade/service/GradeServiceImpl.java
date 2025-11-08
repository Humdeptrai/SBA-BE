package sum25.studentcode.backend.modules.Grade.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sum25.studentcode.backend.model.Grade;
import sum25.studentcode.backend.modules.Grade.dto.request.GradeRequest;
import sum25.studentcode.backend.modules.Grade.dto.response.GradeResponse;
import sum25.studentcode.backend.modules.Grade.repository.GradeRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GradeServiceImpl implements GradeService {

    private final GradeRepository gradeRepository;

    @Override
    public GradeResponse createGrade(GradeRequest request) {
        Grade grade = Grade.builder()
                .gradeLevel(request.getGradeLevel())
                .description(request.getDescription())
                .build();
        grade = gradeRepository.save(grade);
        return convertToResponse(grade);
    }

    @Override
    public GradeResponse getGradeById(Long id) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grade not found"));
        return convertToResponse(grade);
    }

    @Override
    public List<GradeResponse> getAllGrades(Long userId) {
        return gradeRepository.findAllByCreatedBy_UserId(userId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public GradeResponse updateGrade(Long id, GradeRequest request) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grade not found"));
        grade.setGradeLevel(request.getGradeLevel());
        grade.setDescription(request.getDescription());
        grade = gradeRepository.save(grade);
        return convertToResponse(grade);
    }

    @Override
    public void deleteGrade(Long id) {
        if (!gradeRepository.existsById(id)) {
            throw new RuntimeException("Grade not found");
        }
        gradeRepository.deleteById(id);
    }

    private GradeResponse convertToResponse(Grade grade) {
        GradeResponse response = new GradeResponse();
        response.setGradeId(grade.getGradeId());
        response.setGradeLevel(grade.getGradeLevel());
        response.setDescription(grade.getDescription());
        response.setCreatedAt(grade.getCreatedAt());
        response.setUpdatedAt(grade.getUpdatedAt());
        return response;
    }
}