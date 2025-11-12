package sum25.studentcode.backend.modules.Lesson.service;

import sum25.studentcode.backend.modules.Lesson.dto.request.LessonCollectionRequest;
import sum25.studentcode.backend.modules.Lesson.dto.response.LessonCollectionResponse;

import java.util.List;

public interface LessonCollectionService {
    List<LessonCollectionResponse> getAllCollections();
    LessonCollectionResponse getCollectionById(Long id);
    LessonCollectionResponse createCollection(LessonCollectionRequest request);
    LessonCollectionResponse updateCollection(Long id, LessonCollectionRequest request);
    void deleteCollection(Long id);
    List<LessonCollectionResponse> getCollectionsByTeacher(Long teacherId);
}