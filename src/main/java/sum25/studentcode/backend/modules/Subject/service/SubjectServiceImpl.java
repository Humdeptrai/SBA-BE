package sum25.studentcode.backend.modules.Subject.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sum25.studentcode.backend.model.Subject;
import sum25.studentcode.backend.modules.Subject.dto.request.SubjectRequest;
import sum25.studentcode.backend.modules.Subject.dto.response.SubjectResponse;
import sum25.studentcode.backend.modules.Subject.repository.SubjectRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;

    @Override
    public SubjectResponse createSubject(SubjectRequest request) {
        Subject subject = Subject.builder()
                .subjectName(request.getSubjectName())
                .subjectCode(request.getSubjectCode())
                .creditId(request.getCreditId())
                .syllabus(request.getSyllabus())
                .build();
        subject = subjectRepository.save(subject);
        return convertToResponse(subject);
    }

    @Override
    public SubjectResponse getSubjectById(Long id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subject not found"));
        return convertToResponse(subject);
    }

    @Override
    public List<SubjectResponse> getAllSubjects() {
        return subjectRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SubjectResponse updateSubject(Long id, SubjectRequest request) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subject not found"));
        subject.setSubjectName(request.getSubjectName());
        subject.setSubjectCode(request.getSubjectCode());
        subject.setCreditId(request.getCreditId());
        subject.setSyllabus(request.getSyllabus());
        subject = subjectRepository.save(subject);
        return convertToResponse(subject);
    }

    @Override
    public void deleteSubject(Long id) {
        if (!subjectRepository.existsById(id)) {
            throw new RuntimeException("Subject not found");
        }
        subjectRepository.deleteById(id);
    }

    private SubjectResponse convertToResponse(Subject subject) {
        SubjectResponse response = new SubjectResponse();
        response.setSubjectId(subject.getSubjectId());
        response.setSubjectName(subject.getSubjectName());
        response.setSubjectCode(subject.getSubjectCode());
        response.setCreditId(subject.getCreditId());
        response.setSyllabus(subject.getSyllabus());
        response.setCreatedAt(subject.getCreatedAt());
        response.setUpdatedAt(subject.getUpdatedAt());
        return response;
    }
}