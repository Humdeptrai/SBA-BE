package sum25.studentcode.backend.modules.Syllabus;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sum25.studentcode.backend.modules.Syllabus.dto.request.SyllabusRequest;
import sum25.studentcode.backend.modules.Syllabus.dto.response.SyllabusResponse;
import sum25.studentcode.backend.modules.Syllabus.service.SyllabusService;

import java.util.List;

@RestController
@RequestMapping("/api/syllabuses")
@RequiredArgsConstructor
public class SyllabusController {

    private final SyllabusService syllabusService;

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public SyllabusResponse createSyllabus(@RequestBody SyllabusRequest request) {
        return syllabusService.createSyllabus(request);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    public SyllabusResponse getSyllabusById(@PathVariable Long id) {
        return syllabusService.getSyllabusById(id);
    }

    @GetMapping("/by/user/{userId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    public List<SyllabusResponse> getAllSyllabuses(@PathVariable Long userId) {
        return syllabusService.getAllSyllabuses(userId);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public SyllabusResponse updateSyllabus(@PathVariable Long id, @RequestBody SyllabusRequest request) {
        return syllabusService.updateSyllabus(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public void deleteSyllabus(@PathVariable Long id) {
        syllabusService.deleteSyllabus(id);
    }

    @GetMapping("/{id}/export")
    @PreAuthorize("hasRole('TEACHER')")
    public byte[] exportSyllabus(@PathVariable Long id) {
        return syllabusService.exportSyllabus(id);
    }
}