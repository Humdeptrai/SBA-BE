package sum25.studentcode.backend.modules.MatrixQuestion.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sum25.studentcode.backend.model.Matrix;
import sum25.studentcode.backend.model.MatrixQuestion;
import sum25.studentcode.backend.model.Questions;
import sum25.studentcode.backend.modules.Matrix.repository.MatrixRepository;
import sum25.studentcode.backend.modules.MatrixQuestion.dto.request.MatrixQuestionRequest;
import sum25.studentcode.backend.modules.MatrixQuestion.dto.response.MatrixQuestionResponse;
import sum25.studentcode.backend.modules.MatrixQuestion.repository.MatrixQuestionRepository;
import sum25.studentcode.backend.modules.Questions.repository.QuestionsRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatrixQuestionServiceImpl implements MatrixQuestionService {

    private final MatrixQuestionRepository matrixQuestionRepository;
    private final MatrixRepository matrixRepository;
    private final QuestionsRepository questionsRepository;

    @Override
    public MatrixQuestionResponse createMatrixQuestion(MatrixQuestionRequest request) {
        Matrix matrix = matrixRepository.findById(request.getMatrixId())
                .orElseThrow(() -> new RuntimeException("Matrix not found"));
        Questions question = questionsRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new RuntimeException("Question not found"));
        MatrixQuestion matrixQuestion = MatrixQuestion.builder()
                .matrix(matrix)
                .question(question)
                .marksAllocated(request.getMarksAllocated())
                .build();
        matrixQuestion = matrixQuestionRepository.save(matrixQuestion);
        return convertToResponse(matrixQuestion);
    }

    @Override
    public MatrixQuestionResponse getMatrixQuestionById(Long id) {
        MatrixQuestion matrixQuestion = matrixQuestionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("MatrixQuestion not found"));
        return convertToResponse(matrixQuestion);
    }

    @Override
    public List<MatrixQuestionResponse> getAllMatrixQuestions() {
        return matrixQuestionRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MatrixQuestionResponse updateMatrixQuestion(Long id, MatrixQuestionRequest request) {
        MatrixQuestion matrixQuestion = matrixQuestionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("MatrixQuestion not found"));
        Matrix matrix = matrixRepository.findById(request.getMatrixId())
                .orElseThrow(() -> new RuntimeException("Matrix not found"));
        Questions question = questionsRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new RuntimeException("Question not found"));
        matrixQuestion.setMatrix(matrix);
        matrixQuestion.setQuestion(question);
        matrixQuestion.setMarksAllocated(request.getMarksAllocated());
        matrixQuestion = matrixQuestionRepository.save(matrixQuestion);
        return convertToResponse(matrixQuestion);
    }

    @Override
    public void deleteMatrixQuestion(Long id) {
        if (!matrixQuestionRepository.existsById(id)) {
            throw new RuntimeException("MatrixQuestion not found");
        }
        matrixQuestionRepository.deleteById(id);
    }

    private MatrixQuestionResponse convertToResponse(MatrixQuestion matrixQuestion) {
        MatrixQuestionResponse response = new MatrixQuestionResponse();
        response.setMatrixQuestionId(matrixQuestion.getMatrixQuestionId());
        response.setMatrixId(matrixQuestion.getMatrix().getMatrixId());
        response.setQuestionId(matrixQuestion.getQuestion().getQuestionId());
        response.setMarksAllocated(matrixQuestion.getMarksAllocated());
        response.setCreatedAt(matrixQuestion.getCreatedAt());
        response.setUpdatedAt(matrixQuestion.getUpdatedAt());
        return response;
    }
}