package sum25.studentcode.backend.core.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    LESSON_NOT_FOUND("LESSON_NOT_FOUND", "Lesson not found", 404),
    USER_NOT_FOUND("USER_NOT_FOUND", "Không tìm thấy user trong hệ thống.", 404),
    USER_ALREADY_EXISTS("USER_ALREADY_EXISTS", "Tên người dùng đã tồn tại.", 400),
    MATRIX_NAME_DUPLICATE("MATRIX_NAME_DUPLICATE", "Tên ma trận đã tồn tại trong bài học này.", 400),
    PRACTICE_NOT_FOUND("PRACTICE_NOT_FOUND", "Không tìm thấy lượt luyện tập.", 404),
    ACCESS_DENIED("ACCESS_DENIED", "Bạn không có quyền truy cập.", 403),
    SESSION_NOT_FOUND("SESSION_NOT_FOUND", "Buổi luyện tập không tồn tại.", 404),
    SESSION_INACTIVE("SESSION_INACTIVE", "Buổi luyện tập không hoạt động.", 400),
    SESSION_TIME_INVALID("SESSION_TIME_INVALID", "Thời gian buổi luyện tập không hợp lệ.", 400),
    INVALID_STATUS("INVALID_STATUS", "Trạng thái không hợp lệ.", 400),
    MATRIX_NOT_FOUND("MATRIX_NOT_FOUND", "Không tìm thấy ma trận đề thi.", 404),
    MATRIX_EMPTY("MATRIX_EMPTY", "Đề thi chưa có câu hỏi.", 404),
    NO_ANSWERS("NO_ANSWERS", "Chưa có câu trả lời nào được nộp.", 400),
    EMPTY_LIST("EMPTY_LIST", "Danh sách trống.", 404),
    INVALID_SESSION_CODE("INVALID_SESSION_CODE", "Mã code không khớp với buổi luyện tập.", 400),
    SESSION_NOT_STARTED("SESSION_NOT_STARTED", "Bài thi chưa đến thời gian bắt đầu.", 400),
    SESSION_ENDED("SESSION_ENDED", "Bài thi đã kết thúc.", 400),
    ALREADY_SUBMITTED("ALREADY_SUBMITTED", "Bạn đã nộp bài thi này rồi. Không thể làm lại.", 400);

    private final String code;
    private final String message;
    private final int status;

    ErrorCode(String code, String message, int status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

}
