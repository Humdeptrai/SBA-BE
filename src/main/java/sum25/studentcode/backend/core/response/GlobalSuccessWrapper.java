package sum25.studentcode.backend.core.response;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import sum25.studentcode.backend.core.annotation.SkipWrap;
import sum25.studentcode.backend.core.util.MetaBuilder;

import java.util.UUID;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalSuccessWrapper implements ResponseBodyAdvice<Object> {

    private final MetaBuilder metaBuilder;
    private final HttpServletRequest request;

    private final String VERSION = "1.0.0";

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        String path = request.getRequestURI();

        // Nếu có @SkipWrap ở method hoặc class => bỏ qua
        return !returnType.hasMethodAnnotation(SkipWrap.class)
                && !returnType.getDeclaringClass().isAnnotationPresent(SkipWrap.class)
                && !returnType.getParameterType().equals(BaseResponse.class)
                && !returnType.getParameterType().equals(ResponseEntity.class)

                && !path.startsWith("/api/v3/api-docs")
                && !path.startsWith("/api/swagger")
                && !path.startsWith("/api/webjars");
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            org.springframework.http.server.ServerHttpRequest req,
            org.springframework.http.server.ServerHttpResponse res) {

        // Nếu là lỗi thì không động vào (đã handled ở GlobalExceptionHandler)
        if (body instanceof BaseResponse<?>)
            return body;

        return BaseResponse.success(
                body,
                "Success",
                200,
                metaBuilder.fromRequest(request),
                UUID.randomUUID().toString(),
                VERSION);
    }
}
