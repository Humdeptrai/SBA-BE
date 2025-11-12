package sum25.studentcode.backend.modules.Lesson.service;

import org.springframework.web.multipart.MultipartFile;
import sum25.studentcode.backend.model.LessonFile;

import java.util.List;

public interface LessonFileService {
    LessonFile saveFile(String lessonId, MultipartFile file);
    List<LessonFile> getFilesByLessonId(String lessonId);
    LessonFile getFileById(String id);
    void deleteFile(String id);
    void deleteFileByLessonIdAndFileId(String lessonId, String fileId);
}
