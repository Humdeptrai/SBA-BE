package sum25.studentcode.backend.modules.Lesson;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sum25.studentcode.backend.modules.Auth.service.UserService;
import sum25.studentcode.backend.modules.Lesson.dto.request.LessonCollectionRequest;
import sum25.studentcode.backend.modules.Lesson.dto.response.LessonCollectionResponse;
import sum25.studentcode.backend.modules.Lesson.service.LessonCollectionService;

import java.util.List;

@RestController
@RequestMapping("/api/lesson-collections")
@RequiredArgsConstructor
public class LessonCollectionController {

    private final LessonCollectionService lessonCollectionService;
    private final UserService userService;
    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public LessonCollectionResponse createCollection(@RequestBody LessonCollectionRequest request) {
        return lessonCollectionService.createCollection(request);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    public LessonCollectionResponse getCollectionById(@PathVariable Long id) {
        return lessonCollectionService.getCollectionById(id);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    public List<LessonCollectionResponse> getAllCollections() {
        return lessonCollectionService.getAllCollections();
    }

    @GetMapping("/by-teacher")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    public List<LessonCollectionResponse> getCollectionsByTeacher() {
        return lessonCollectionService.getCollectionsByTeacher(userService.getCurrentUser().getUserId());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public LessonCollectionResponse updateCollection(@PathVariable Long id, @RequestBody LessonCollectionRequest request) {
        return lessonCollectionService.updateCollection(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public void deleteCollection(@PathVariable Long id) {
        lessonCollectionService.deleteCollection(id);
    }
}