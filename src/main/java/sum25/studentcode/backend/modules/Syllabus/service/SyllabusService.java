package sum25.studentcode.backend.modules.Syllabus.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sum25.studentcode.backend.modules.Syllabus.dto.request.SyllabusRequest;
import sum25.studentcode.backend.modules.Syllabus.dto.response.SyllabusResponse;
import sum25.studentcode.backend.modules.Syllabus.entity.Syllabus;
import sum25.studentcode.backend.modules.Syllabus.repository.SyllabusRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SyllabusService {

    private final SyllabusRepository syllabusRepository;

    public SyllabusResponse createSyllabus(SyllabusRequest request) {
        Syllabus syllabus = Syllabus.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .subjectId(request.getSubjectId())
                .gradeId(request.getGradeId())
                .createdBy_UserId(request.getCreatedBy_UserId())
                .build();

        Syllabus savedSyllabus = syllabusRepository.save(syllabus);
        return mapToResponse(savedSyllabus);
    }

    public SyllabusResponse getSyllabusById(Long id) {
        Syllabus syllabus = syllabusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Syllabus not found"));
        return mapToResponse(syllabus);
    }

    public List<SyllabusResponse> getAllSyllabuses(Long userId) {
        List<Syllabus> syllabuses = syllabusRepository.findByCreatedBy_UserId(userId);
        return syllabuses.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public SyllabusResponse updateSyllabus(Long id, SyllabusRequest request) {
        Syllabus syllabus = syllabusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Syllabus not found"));

        syllabus.setTitle(request.getTitle());
        syllabus.setDescription(request.getDescription());
        syllabus.setSubjectId(request.getSubjectId());
        syllabus.setGradeId(request.getGradeId());

        Syllabus updatedSyllabus = syllabusRepository.save(syllabus);
        return mapToResponse(updatedSyllabus);
    }

    public void deleteSyllabus(Long id) {
        syllabusRepository.deleteById(id);
    }

    public byte[] exportSyllabus(Long id) {
        Syllabus syllabus = syllabusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Syllabus not found"));

        // TODO: Implement PDF export logic using a library like iText or Apache PDFBox
        // For now, return a simple byte array placeholder
        String content = "Syllabus: " + syllabus.getTitle() + "\nDescription: " + syllabus.getDescription();
        return content.getBytes();
    }

    private SyllabusResponse mapToResponse(Syllabus syllabus) {
        return SyllabusResponse.builder()
                .id(syllabus.getId())
                .title(syllabus.getTitle())
                .description(syllabus.getDescription())
                .subjectId(syllabus.getSubjectId())
                .gradeId(syllabus.getGradeId())
                .createdBy_UserId(syllabus.getCreatedBy_UserId())
                .createdAt(syllabus.getCreatedAt())
                .updatedAt(syllabus.getUpdatedAt())
                .build();
    }
}