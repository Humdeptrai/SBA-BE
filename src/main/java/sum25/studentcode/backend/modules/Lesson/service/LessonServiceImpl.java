package sum25.studentcode.backend.modules.Lesson.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.studentcode.backend.model.Grade;
import sum25.studentcode.backend.model.Lesson;
import sum25.studentcode.backend.modules.Lesson.dto.request.LessonRequest;
import sum25.studentcode.backend.modules.Lesson.dto.response.LessonGradeResponse;
import sum25.studentcode.backend.modules.Lesson.dto.response.LessonResponse;
import sum25.studentcode.backend.modules.Lesson.repository.LessonRepository;
import sum25.studentcode.backend.modules.Grade.repository.GradeRepository;
import sum25.studentcode.backend.modules.Auth.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final GradeRepository gradeRepository;
    private final UserService userService;

    @Override
    public LessonResponse createLesson(LessonRequest request) {
        if (lessonRepository.existsByLessonTitle(request.getLessonTitle())) {
            throw new RuntimeException("Lesson with this title already exists");
        }

        Lesson lesson = Lesson.builder()
                .lessonTitle(request.getLessonTitle())
                .lessonContent(request.getLessonContent())
                .lessonObjectives(request.getLessonObjectives())
                .lessonType(request.getLessonType())
                .grade(request.getGradeId() != null ? gradeRepository.findById(request.getGradeId())
                        .orElseThrow(() -> new RuntimeException("Grade not found")) : null)
                .durationMinutes(request.getDurationMinutes())
                .methodology(request.getMethodology())
                .materials(request.getMaterials())
                .homework(request.getHomework())
                .createdBy(userService.getCurrentUser())
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
        lesson.setGrade(request.getGradeId() != null ? gradeRepository.findById(request.getGradeId())
                .orElseThrow(() -> new RuntimeException("Grade not found")) : null);
        lesson.setLessonContent(request.getLessonContent());
        lesson.setLessonObjectives(request.getLessonObjectives());
        lesson.setLessonType(request.getLessonType());
        lesson.setDurationMinutes(request.getDurationMinutes());
        lesson.setMethodology(request.getMethodology());
        lesson.setMaterials(request.getMaterials());
        lesson.setHomework(request.getHomework());

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
    public List<LessonResponse> getAllLessons(Long userId) {
        return lessonRepository.findAllByCreatedBy_UserId(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<LessonResponse> getAllLessons() {
        return lessonRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<LessonGradeResponse> getLessonForGrade(Long id) {
        return lessonRepository.findAllByGrade_GradeId(id).stream().map(
                this::toGradeResponse
        ).toList();
    }

    private LessonGradeResponse toGradeResponse(Lesson lesson) {
        return LessonGradeResponse.builder()
                .lessonId(lesson.getLessonId())
                .lessonTitle(lesson.getLessonTitle())
                .build();
    }
    private LessonResponse toResponse(Lesson lesson) {
        return LessonResponse.builder()
                .lessonId(lesson.getLessonId())
                .lessonTitle(lesson.getLessonTitle())
                .lessonContent(lesson.getLessonContent())
                .lessonObjectives(lesson.getLessonObjectives())
                .gradeName(lesson.getGrade() != null ? lesson.getGrade().getGradeLevel() : null)
                .createdAt(lesson.getCreatedAt())
                .gradeId(lesson.getGrade().getGradeId() != null ? lesson.getGrade().getGradeId() : null)
                .updatedAt(lesson.getUpdatedAt())
                .lessonType(lesson.getLessonType())
                .durationMinutes(lesson.getDurationMinutes())
                .methodology(lesson.getMethodology())
                .materials(lesson.getMaterials())
                .homework(lesson.getHomework())
                .build();
    }
}
