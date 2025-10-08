package sum25.studentcode.backend.modules.Grade.service;

import sum25.studentcode.backend.modules.Grade.dto.request.GradeRequest;
import sum25.studentcode.backend.modules.Grade.dto.response.GradeResponse;

import java.util.List;

public interface GradeService {
    GradeResponse createGrade(GradeRequest request);
    GradeResponse getGradeById(Long id);
    List<GradeResponse> getAllGrades();
    GradeResponse updateGrade(Long id, GradeRequest request);
    void deleteGrade(Long id);
}