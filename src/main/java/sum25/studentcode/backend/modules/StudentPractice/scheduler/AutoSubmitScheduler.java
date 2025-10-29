package sum25.studentcode.backend.modules.StudentPractice.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sum25.studentcode.backend.model.Exam;
import sum25.studentcode.backend.model.PracticeSession;
import sum25.studentcode.backend.model.StudentPractice;
import sum25.studentcode.backend.modules.StudentAnswers.repository.StudentAnswersRepository;
import sum25.studentcode.backend.modules.StudentPractice.repository.StudentPracticeRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AutoSubmitScheduler {

    private final StudentPracticeRepository studentPracticeRepository;
    private final StudentAnswersRepository studentAnswersRepository;

    /**
     * 🕐 Chạy mỗi 1 phút để auto-submit các bài làm đã hết giờ.
     * Cửa sổ làm bài: end = exam.examDate + durationMinutes
     * Logic điểm: cộng tổng marksEarned trong student_answers.
     */
    @Scheduled(fixedRate = 60_000) // ⏱ mỗi 1 phút
    public void autoSubmitExpiredPractices() {
        LocalDateTime now = LocalDateTime.now();

        // 1️⃣ Lấy tất cả practice đang làm
        List<StudentPractice> inProgress = studentPracticeRepository
                .findByStatus(StudentPractice.PracticeStatus.IN_PROGRESS);

        if (inProgress.isEmpty()) return;

        List<StudentPractice> toSave = new ArrayList<>();

        for (StudentPractice practice : inProgress) {
            try {
                PracticeSession session = practice.getPracticeSession();
                if (session == null || session.getMatrix() == null || session.getMatrix().getExam() == null) {
                    continue; // thiếu liên kết cần thiết
                }

                Exam exam = session.getMatrix().getExam();
                if (exam.getExamDate() == null || exam.getDurationMinutes() == null) {
                    continue; // chưa cấu hình thời gian
                }

                LocalDateTime start = exam.getExamDate();
                LocalDateTime end = start.plusMinutes(exam.getDurationMinutes());

                // 2️⃣ Nếu đã quá giờ thì auto-submit
                if (now.isAfter(end)) {
                    BigDecimal totalScore = BigDecimal.ZERO;
                    var answers = studentAnswersRepository.findByStudentPractice_PracticeId(practice.getPracticeId());
                    for (var ans : answers) {
                        totalScore = totalScore.add(
                                ans.getMarksEarned() != null ? ans.getMarksEarned() : BigDecimal.ZERO
                        );
                    }

                    // 3️⃣ Cập nhật trạng thái + thời điểm nộp
                    practice.setStatus(StudentPractice.PracticeStatus.SUBMITTED);
                    practice.setSubmitTime(end);
                    practice.setTotalScore(totalScore);
                    toSave.add(practice);

                    System.out.printf("✅ [AUTO] Submitted practiceId=%d | totalScore=%s%n",
                            practice.getPracticeId(), totalScore);
                }
            } catch (Exception e) {
                System.err.printf("⚠️ Lỗi khi auto-submit practiceId=%d: %s%n",
                        practice.getPracticeId(), e.getMessage());
            }
        }

        // 4️⃣ Lưu thay đổi
        if (!toSave.isEmpty()) {
            studentPracticeRepository.saveAll(toSave);
        }
    }
}
