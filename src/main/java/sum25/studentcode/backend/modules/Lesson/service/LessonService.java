package sum25.studentcode.backend.modules.Lesson.service;

import sum25.studentcode.backend.modules.Lesson.dto.request.LessonRequest;
import sum25.studentcode.backend.modules.Lesson.dto.response.LessonResponse;

import java.util.List;

public interface LessonService {

    LessonResponse createLesson(LessonRequest request);

    LessonResponse updateLesson(Long id, LessonRequest request);

    void deleteLesson(Long id);

    LessonResponse getLessonById(Long id);

    List<LessonResponse> getAllLessons();
}
