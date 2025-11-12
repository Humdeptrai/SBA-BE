package sum25.studentcode.backend.modules.Lesson.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sum25.studentcode.backend.model.LessonFile;
import sum25.studentcode.backend.modules.Lesson.repository.LessonFileRepository;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonFileServiceImpl implements LessonFileService {

    private final LessonFileRepository lessonFileRepository;

    @Override
    @Transactional
    public LessonFile saveFile(String lessonId, MultipartFile file) {
        try {
            LessonFile lessonFile = LessonFile.builder()
                    .lessonId(lessonId)
                    .fileName(file.getOriginalFilename())
                    .fileType(file.getContentType())
                    .data(file.getBytes())
                    .build();

            return lessonFileRepository.save(lessonFile);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file", e);
        }
    }

    @Override
    public List<LessonFile> getFilesByLessonId(String lessonId) {
        return lessonFileRepository.findAll().stream()
                .filter(file -> file.getLessonId().equals(lessonId))
                .toList();
    }

    @Override
    public LessonFile getFileById(String id) {
        return lessonFileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found"));
    }

    @Override
    @Transactional
    public void deleteFile(String id) {
        lessonFileRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteFileByLessonIdAndFileId(String lessonId, String fileId) {
        LessonFile file = lessonFileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));
        if (!file.getLessonId().equals(lessonId)) {
            throw new RuntimeException("File does not belong to the specified lesson");
        }
        lessonFileRepository.deleteById(fileId);
    }
}
