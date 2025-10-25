package sum25.studentcode.backend.modules.Lesson.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.studentcode.backend.model.Grade;
import sum25.studentcode.backend.model.Lesson;
import sum25.studentcode.backend.modules.Lesson.dto.request.LessonRequest;
import sum25.studentcode.backend.modules.Lesson.dto.response.LessonResponse;
import sum25.studentcode.backend.modules.Lesson.repository.LessonRepository;
import sum25.studentcode.backend.modules.Grade.repository.GradeRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final GradeRepository gradeRepository;

    @Override
    public LessonResponse createLesson(LessonRequest request) {
        if (lessonRepository.existsByLessonTitle(request.getLessonTitle())) {
            throw new RuntimeException("Lesson with this title already exists");
        }

        Lesson lesson = Lesson.builder()
                .lessonTitle(request.getLessonTitle())
                .lessonContent(request.getLessonContent())
                .lessonObjectives(request.getLessonObjectives())
                .build();

        if (request.getGradeId() != null) {
            Grade grade = gradeRepository.findById(request.getGradeId())
                    .orElseThrow(() -> new RuntimeException("Grade not found"));
            lesson.setGrade(grade);
        }

        lessonRepository.save(lesson);
        return toResponse(lesson);
    }

    @Override
    public LessonResponse updateLesson(Long id, LessonRequest request) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        lesson.setLessonTitle(request.getLessonTitle());
        lesson.setLessonContent(request.getLessonContent());
        lesson.setLessonObjectives(request.getLessonObjectives());

        if (request.getGradeId() != null) {
            Grade grade = gradeRepository.findById(request.getGradeId())
                    .orElseThrow(() -> new RuntimeException("Grade not found"));
            lesson.setGrade(grade);
        } else {
            lesson.setGrade(null);
        }

        lessonRepository.save(lesson);
        return toResponse(lesson);
    }

    @Override
    public void deleteLesson(Long id) {
        if (!lessonRepository.existsById(id)) {
            throw new RuntimeException("Lesson not found");
        }
        lessonRepository.deleteById(id);
    }

    @Override
    public LessonResponse getLessonById(Long id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));
        return toResponse(lesson);
    }

    @Override
    public List<LessonResponse> getAllLessons() {
        return lessonRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private LessonResponse toResponse(Lesson lesson) {
        return LessonResponse.builder()
                .lessonId(lesson.getLessonId())
                .lessonTitle(lesson.getLessonTitle())
                .lessonContent(lesson.getLessonContent())
                .lessonObjectives(lesson.getLessonObjectives())
                .gradeId(lesson.getGrade() != null ? lesson.getGrade().getGradeId() : null)
                .createdAt(lesson.getCreatedAt())
                .updatedAt(lesson.getUpdatedAt())
                .build();
    }
}
