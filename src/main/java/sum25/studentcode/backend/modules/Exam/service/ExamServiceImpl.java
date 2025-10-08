package sum25.studentcode.backend.modules.Exam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sum25.studentcode.backend.model.Exam;
import sum25.studentcode.backend.model.Subject;
import sum25.studentcode.backend.modules.Exam.dto.request.ExamRequest;
import sum25.studentcode.backend.modules.Exam.dto.response.ExamResponse;
import sum25.studentcode.backend.modules.Exam.repository.ExamRepository;
import sum25.studentcode.backend.modules.Subject.repository.SubjectRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;
    private final SubjectRepository subjectRepository;

    @Override
    public ExamResponse createExam(ExamRequest request) {
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Subject not found"));
        Exam exam = Exam.builder()
                .examName(request.getExamName())
                .description(request.getDescription())
                .durationMinutes(request.getDurationMinutes())
                .examDate(request.getExamDate())
                .subject(subject)
                .build();
        exam = examRepository.save(exam);
        return convertToResponse(exam);
    }

    @Override
    public ExamResponse getExamById(Long id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exam not found"));
        return convertToResponse(exam);
    }

    @Override
    public List<ExamResponse> getAllExams() {
        return examRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ExamResponse updateExam(Long id, ExamRequest request) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exam not found"));
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Subject not found"));
        exam.setExamName(request.getExamName());
        exam.setDescription(request.getDescription());
        exam.setDurationMinutes(request.getDurationMinutes());
        exam.setExamDate(request.getExamDate());
        exam.setSubject(subject);
        exam = examRepository.save(exam);
        return convertToResponse(exam);
    }

    @Override
    public void deleteExam(Long id) {
        if (!examRepository.existsById(id)) {
            throw new RuntimeException("Exam not found");
        }
        examRepository.deleteById(id);
    }

    private ExamResponse convertToResponse(Exam exam) {
        ExamResponse response = new ExamResponse();
        response.setExamId(exam.getExamId());
        response.setExamName(exam.getExamName());
        response.setDescription(exam.getDescription());
        response.setDurationMinutes(exam.getDurationMinutes());
        response.setExamDate(exam.getExamDate());
        response.setSubjectId(exam.getSubject().getSubjectId());
        response.setCreatedAt(exam.getCreatedAt());
        response.setUpdatedAt(exam.getUpdatedAt());
        return response;
    }
}