package sum25.studentcode.backend.core.util;

import org.springframework.stereotype.Component;



import jakarta.servlet.http.HttpServletRequest;
import sum25.studentcode.backend.core.response.MetaInfo;

@Component
public class MetaBuilder {

    public MetaInfo fromRequest(HttpServletRequest request) {
        MetaInfo meta = new MetaInfo();
        meta.setClientIp(request.getRemoteAddr());
        meta.setPath(request.getRequestURI());
        meta.setMethod(request.getMethod());
        meta.setHost(request.getServerName());
        meta.setUserAgent(request.getHeader("User-Agent"));
        meta.setReferer(request.getHeader("Referer"));
        meta.setTraceId(request.getHeader("X-Trace-Id"));
        return meta;
    }
}
