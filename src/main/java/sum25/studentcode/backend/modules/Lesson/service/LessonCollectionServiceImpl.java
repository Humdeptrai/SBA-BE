package sum25.studentcode.backend.modules.Lesson.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.studentcode.backend.model.Lesson;
import sum25.studentcode.backend.model.LessonCollection;
import sum25.studentcode.backend.model.User;
import sum25.studentcode.backend.modules.Auth.repository.UserRepository;
import sum25.studentcode.backend.modules.Auth.service.UserService;
import sum25.studentcode.backend.modules.Lesson.dto.request.LessonCollectionRequest;
import sum25.studentcode.backend.modules.Lesson.dto.response.LessonCollectionResponse;
import sum25.studentcode.backend.modules.Lesson.dto.response.LessonSummary;
import sum25.studentcode.backend.modules.Lesson.repository.LessonCollectionRepository;
import sum25.studentcode.backend.modules.Lesson.repository.LessonRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonCollectionServiceImpl implements LessonCollectionService {

    private final LessonCollectionRepository lessonCollectionRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Override
    @Transactional(readOnly = true)
    public List<LessonCollectionResponse> getAllCollections() {
        return lessonCollectionRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public LessonCollectionResponse getCollectionById(Long id) {
        LessonCollection collection = lessonCollectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Collection not found"));
        return mapToResponse(collection);
    }

    @Override
    @Transactional
    public LessonCollectionResponse createCollection(LessonCollectionRequest request) {
        // Get current user (assuming from security context)
        User currentUser = userService.getCurrentUser(); // Implement this method

        List<Lesson> lessons = lessonRepository.findAllById(request.getLessonIds());

        LessonCollection collection = LessonCollection.builder()
                .collectionName(request.getCollectionName())
                .description(request.getDescription())
                .createdBy(currentUser)
                .lessons(lessons)
                .build();

        LessonCollection saved = lessonCollectionRepository.save(collection);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public LessonCollectionResponse updateCollection(Long id, LessonCollectionRequest request) {
        LessonCollection collection = lessonCollectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Collection not found"));

        List<Lesson> lessons = lessonRepository.findAllById(request.getLessonIds());

        collection.setCollectionName(request.getCollectionName());
        collection.setDescription(request.getDescription());
        collection.setLessons(lessons);

        LessonCollection updated = lessonCollectionRepository.save(collection);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void deleteCollection(Long id) {
        lessonCollectionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LessonCollectionResponse> getCollectionsByTeacher(Long teacherId) {
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        return lessonCollectionRepository.findAll().stream()
                .filter(collection -> collection.getCreatedBy().equals(teacher))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private LessonCollectionResponse mapToResponse(LessonCollection collection) {
        List<LessonSummary> lessonSummaries = collection.getLessons().stream()
                .map(lesson -> LessonSummary.builder()
                        .lessonId(lesson.getLessonId())
                        .lessonTitle(lesson.getLessonTitle())
                        .lessonType(lesson.getLessonType())
                        .durationMinutes(lesson.getDurationMinutes())
                        .createdAt(lesson.getCreatedAt())
                        .createdByUsername(lesson.getCreatedBy().getUsername())
                        .build())
                .collect(Collectors.toList());

        return LessonCollectionResponse.builder()
                .collectionId(collection.getCollectionId())
                .collectionName(collection.getCollectionName())
                .description(collection.getDescription())
                .createdByUsername(collection.getCreatedBy().getUsername())
                .lessons(lessonSummaries)
                .build();
    }

    private User getCurrentUser() {
        // Implement to get current authenticated user
        // For now, return a dummy user
        return userRepository.findByUsername("teacher").orElseThrow();
    }
}