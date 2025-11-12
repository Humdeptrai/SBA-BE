package sum25.studentcode.backend.modules.Lesson;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sum25.studentcode.backend.modules.Lesson.dto.request.LessonRequest;
import sum25.studentcode.backend.modules.Lesson.dto.response.LessonGradeResponse;
import sum25.studentcode.backend.modules.Lesson.dto.response.LessonResponse;
import sum25.studentcode.backend.model.LessonFile;
import sum25.studentcode.backend.modules.Lesson.service.LessonFileService;
import sum25.studentcode.backend.modules.Lesson.service.LessonService;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;

@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;
    private final LessonFileService lessonFileService;

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public LessonResponse createLesson(@RequestBody LessonRequest request) {
        return lessonService.createLesson(request);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    public LessonResponse getLessonById(@PathVariable Long id) {
        return lessonService.getLessonById(id);
    }

    @GetMapping("/by/user/{userId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    public List<LessonResponse> getAllLessons(@PathVariable Long userId) {
        return lessonService.getAllLessons(userId);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    public List<LessonResponse> getAllLessons() {
        return lessonService.getAllLessons();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public LessonResponse updateLesson(@PathVariable Long id, @RequestBody LessonRequest request) {
        return lessonService.updateLesson(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public void deleteLesson(@PathVariable Long id) {
        lessonService.deleteLesson(id);
    }

    @PostMapping("/{lessonId}/upload")
    @PreAuthorize("hasRole('TEACHER')")
    public void uploadFile(@PathVariable Long lessonId, @RequestParam("file") MultipartFile file) {
        lessonFileService.saveFile(lessonId.toString(), file);
    }

    @GetMapping("/{lessonId}/download")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    public List<LessonFile> downloadFiles(@PathVariable Long lessonId) {
        return lessonFileService.getFilesByLessonId(lessonId.toString());
    }

    @GetMapping("/{lessonId}/files")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    public List<LessonFile> getFilesByLesson(@PathVariable Long lessonId) {
        return lessonFileService.getFilesByLessonId(lessonId.toString());
    }

    @GetMapping("/files/{fileId}/content")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    public ResponseEntity<byte[]> downloadFileContent(@PathVariable String fileId) {
        LessonFile file = lessonFileService.getFileById(fileId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(file.getFileType()));
        headers.setContentDispositionFormData("inline", file.getFileName());
        return ResponseEntity.ok()
                .headers(headers)
                .body(file.getData());
    }

    @DeleteMapping("/files/{fileId}")
    @PreAuthorize("hasRole('TEACHER')")
    public void deleteFile(@PathVariable String fileId) {
        lessonFileService.deleteFile(fileId);
    }

    @DeleteMapping("/{lessonId}/files/{fileId}")
    @PreAuthorize("hasRole('TEACHER')")
    public void deleteFileByLesson(@PathVariable Long lessonId, @PathVariable String fileId) {
        lessonFileService.deleteFileByLessonIdAndFileId(lessonId.toString(), fileId);
    }

    @GetMapping("/grade/{gradeId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    public List<LessonGradeResponse> getLessonsByGrade(@PathVariable Long gradeId) {
        return lessonService.getLessonForGrade(gradeId);
    }
}