package sum25.studentcode.backend.core.response;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sum25.studentcode.backend.core.util.MetaBuilder;

import java.util.UUID;

@Component
public class ResponseFactory {

    private final MetaBuilder metaBuilder;

    @Value("${app.version}")
    private String version = "1.0.0";

    public ResponseFactory(MetaBuilder metaBuilder) {
        this.metaBuilder = metaBuilder;
    }

    public <T> BaseResponse<T> success(HttpServletRequest request, T data, String message) {
        return BaseResponse.success(
            data,
            message,
            200,
            metaBuilder.fromRequest(request),
            UUID.randomUUID().toString(),
            version
        );
    }

    public <T> BaseResponse<T> success(HttpServletRequest request, T data) {
        return success(request, data, "Success");
    }
}
