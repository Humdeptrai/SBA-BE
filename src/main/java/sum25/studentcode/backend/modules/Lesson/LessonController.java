package sum25.studentcode.backend.modules.Lesson;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sum25.studentcode.backend.modules.Lesson.dto.request.LessonRequest;
import sum25.studentcode.backend.modules.Lesson.dto.response.LessonResponse;
import sum25.studentcode.backend.modules.Lesson.service.LessonService;

import java.util.List;

@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    @PostMapping
    public LessonResponse createLesson(@RequestBody LessonRequest request) {
        return lessonService.createLesson(request);
    }

    @GetMapping("/{id}")
    public LessonResponse getLessonById(@PathVariable Long id) {
        return lessonService.getLessonById(id);
    }

    @GetMapping
    public List<LessonResponse> getAllLessons() {
        return lessonService.getAllLessons();
    }

    @PutMapping("/{id}")
    public LessonResponse updateLesson(@PathVariable Long id, @RequestBody LessonRequest request) {
        return lessonService.updateLesson(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteLesson(@PathVariable Long id) {
        lessonService.deleteLesson(id);
    }
}