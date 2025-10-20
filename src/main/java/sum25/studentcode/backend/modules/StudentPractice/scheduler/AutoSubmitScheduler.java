package sum25.studentcode.backend.modules.StudentPractice.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sum25.studentcode.backend.model.StudentPractice;
import sum25.studentcode.backend.modules.StudentAnswers.repository.StudentAnswersRepository;
import sum25.studentcode.backend.modules.StudentPractice.repository.StudentPracticeRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AutoSubmitScheduler {

    private final StudentPracticeRepository studentPracticeRepository;
    private final StudentAnswersRepository studentAnswersRepository;

    /**
     * 🕒 Chạy mỗi 5 phút để auto-submit các bài làm đã hết giờ.
     * Nếu bài đang IN_PROGRESS và session đã quá hạn -> set SUBMITTED + tính điểm.
     */
    @Scheduled(fixedRate = 300000) // mỗi 5 phút (300.000ms)
    public void autoSubmitExpiredPractices() {
        // 1️⃣ Lấy danh sách các bài làm chưa nộp mà đã quá thời gian thi
        List<StudentPractice> expiredPractices = studentPracticeRepository
                .findByStatusAndPracticeSession_EndTimeBefore(
                        StudentPractice.PracticeStatus.IN_PROGRESS,
                        LocalDateTime.now()
                );

        if (expiredPractices.isEmpty()) return;

        for (StudentPractice practice : expiredPractices) {
            try {
                // 2️⃣ Tính tổng điểm từ các câu trả lời
                BigDecimal totalScore = studentAnswersRepository
                        .findByStudentPractice_PracticeId(practice.getPracticeId())
                        .stream()
                        .map(ans -> ans.getMarksEarned() != null ? ans.getMarksEarned() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                // 3️⃣ Cập nhật trạng thái
                practice.setStatus(StudentPractice.PracticeStatus.SUBMITTED);
                practice.setSubmitTime(practice.getPracticeSession().getEndTime());
                practice.setTotalScore(totalScore);

                System.out.printf("✅ Auto-submitted practiceId=%d | totalScore=%.2f%n",
                        practice.getPracticeId(), totalScore);
            } catch (Exception e) {
                System.err.printf("⚠️ Lỗi khi auto-submit practiceId=%d: %s%n",
                        practice.getPracticeId(), e.getMessage());
            }
        }

        // 4️⃣ Lưu toàn bộ về DB
        studentPracticeRepository.saveAll(expiredPractices);
    }
}
