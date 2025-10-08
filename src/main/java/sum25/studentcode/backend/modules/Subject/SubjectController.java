package sum25.studentcode.backend.modules.Subject;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sum25.studentcode.backend.modules.Subject.dto.request.SubjectRequest;
import sum25.studentcode.backend.modules.Subject.dto.response.SubjectResponse;
import sum25.studentcode.backend.modules.Subject.service.SubjectService;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @PostMapping
    public SubjectResponse createSubject(@RequestBody SubjectRequest request) {
        return subjectService.createSubject(request);
    }

    @GetMapping("/{id}")
    public SubjectResponse getSubjectById(@PathVariable Long id) {
        return subjectService.getSubjectById(id);
    }

    @GetMapping
    public List<SubjectResponse> getAllSubjects() {
        return subjectService.getAllSubjects();
    }

    @PutMapping("/{id}")
    public SubjectResponse updateSubject(@PathVariable Long id, @RequestBody SubjectRequest request) {
        return subjectService.updateSubject(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteSubject(@PathVariable Long id) {
        subjectService.deleteSubject(id);
    }
}