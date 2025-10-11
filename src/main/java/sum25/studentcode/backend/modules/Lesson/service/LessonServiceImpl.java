package sum25.studentcode.backend.modules.Lesson.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sum25.studentcode.backend.model.Grade;
import sum25.studentcode.backend.model.Lesson;
import sum25.studentcode.backend.modules.Grade.repository.GradeRepository;
import sum25.studentcode.backend.modules.Lesson.dto.request.LessonRequest;
import sum25.studentcode.backend.modules.Lesson.dto.response.LessonResponse;
import sum25.studentcode.backend.modules.Lesson.repository.LessonRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final GradeRepository gradeRepository;

    @Override
    public LessonResponse createLesson(LessonRequest request) {
        Grade grade = gradeRepository.findById(request.getGradeId())
                .orElseThrow(() -> new RuntimeException("Grade not found"));
        Lesson lesson = Lesson.builder()
                .grade(grade)
                .lessonTitle(request.getLessonTitle())
                .lessonContent(request.getLessonContent())
                .lessonObjectives(request.getLessonObjectives())
                .build();
        lesson = lessonRepository.save(lesson);
        return convertToResponse(lesson);
    }

    @Override
    public LessonResponse getLessonById(Long id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));
        return convertToResponse(lesson);
    }

    @Override
    public List<LessonResponse> getAllLessons() {
        return lessonRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public LessonResponse updateLesson(Long id, LessonRequest request) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));
        Grade grade = gradeRepository.findById(request.getGradeId())
                .orElseThrow(() -> new RuntimeException("Grade not found"));
        lesson.setGrade(grade);
        lesson.setLessonTitle(request.getLessonTitle());
        lesson.setLessonContent(request.getLessonContent());
        lesson.setLessonObjectives(request.getLessonObjectives());
        lesson = lessonRepository.save(lesson);
        return convertToResponse(lesson);
    }

    @Override
    public void deleteLesson(Long id) {
        if (!lessonRepository.existsById(id)) {
            throw new RuntimeException("Lesson not found");
        }
        lessonRepository.deleteById(id);
    }

    private LessonResponse convertToResponse(Lesson lesson) {
        LessonResponse response = new LessonResponse();
        response.setLessonId(lesson.getLessonId());
        response.setGradeId(lesson.getGrade().getGradeId());
        response.setLessonTitle(lesson.getLessonTitle());
        response.setLessonContent(lesson.getLessonContent());
        response.setLessonObjectives(lesson.getLessonObjectives());
        response.setCreatedAt(lesson.getCreatedAt());
        response.setUpdatedAt(lesson.getUpdatedAt());
        return response;
    }
}