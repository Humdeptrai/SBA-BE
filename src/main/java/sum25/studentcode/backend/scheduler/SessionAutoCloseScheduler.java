package sum25.studentcode.backend.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sum25.studentcode.backend.model.PracticeSession;
import sum25.studentcode.backend.model.StudentPractice;
import sum25.studentcode.backend.modules.PracticeSession.repository.PracticeSessionRepository;
import sum25.studentcode.backend.modules.StudentPractice.repository.StudentPracticeRepository;
import sum25.studentcode.backend.modules.StudentPractice.service.StudentPracticeServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SessionAutoCloseScheduler {

    private final PracticeSessionRepository practiceSessionRepository;
    private final StudentPracticeRepository studentPracticeRepository;
    private final StudentPracticeServiceImpl studentPracticeService; // ✅ để gọi calculateTotalScore()

    /** 🕒 Chạy mỗi 5 phút */
    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void autoCloseExpiredSessions() {
        LocalDateTime now = LocalDateTime.now();
        log.info("⏱️ Scheduler đang quét session hết hạn tại {}", now);

        // 1️⃣ Lấy các session đã hết hạn nhưng vẫn còn active
        List<PracticeSession> expiredSessions = practiceSessionRepository.findAll()
                .stream()
                .filter(s -> s.getIsActive() && now.isAfter(s.getEndTime()))
                .toList();

        for (PracticeSession session : expiredSessions) {
            session.setIsActive(false);
            practiceSessionRepository.save(session);

            // 2️⃣ Cập nhật các student chưa nộp bài
            List<StudentPractice> unfinishedPractices = studentPracticeRepository.findAll()
                    .stream()
                    .filter(p -> p.getPracticeSession().getSessionId().equals(session.getSessionId())
                            && p.getStatus() == StudentPractice.PracticeStatus.IN_PROGRESS)
                    .toList();

            for (StudentPractice sp : unfinishedPractices) {
                try {
                    sp.setStatus(StudentPractice.PracticeStatus.SUBMITTED);
                    sp.setSubmitTime(now);

                    // ✅ Tính điểm tự động
                    sp.setTotalScore(studentPracticeService.calculateTotalScore(sp));

                    studentPracticeRepository.save(sp);
                } catch (Exception e) {
                    log.error("❌ Lỗi khi auto-submit practice ID {}: {}", sp.getPracticeId(), e.getMessage());
                }
            }

            log.info("🔒 Đã tự đóng session [{}] — auto-submit {} bài chưa nộp.",
                    session.getSessionName(), unfinishedPractices.size());
        }
    }
}
