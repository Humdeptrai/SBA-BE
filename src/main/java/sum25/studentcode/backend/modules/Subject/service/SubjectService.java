package sum25.studentcode.backend.modules.Subject.service;

import sum25.studentcode.backend.modules.Subject.dto.request.SubjectRequest;
import sum25.studentcode.backend.modules.Subject.dto.response.SubjectResponse;

import java.util.List;

public interface SubjectService {
    SubjectResponse createSubject(SubjectRequest request);
    SubjectResponse getSubjectById(Long id);
    List<SubjectResponse> getAllSubjects();
    SubjectResponse updateSubject(Long id, SubjectRequest request);
    void deleteSubject(Long id);
}