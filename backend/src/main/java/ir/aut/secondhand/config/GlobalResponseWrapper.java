package ir.aut.secondhand.config;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Intercepts controller responses and ensures consistent JSON structure for REST output.
 *
 * Adapts collection responses, plain string responses, and object responses so that all
 * successful payloads include a status indicator and a predictable envelope.
 */
@ControllerAdvice
public class GlobalResponseWrapper implements ResponseBodyAdvice<Object> {

    private static final Logger log = LoggerFactory.getLogger(GlobalResponseWrapper.class);
    private final ObjectMapper objectMapper;

    public GlobalResponseWrapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return MappingJackson2HttpMessageConverter.class.isAssignableFrom(converterType);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        if (body == null) {
            return null;
        }
        if (body instanceof Collection || body.getClass().isArray()) {
            Map<String, Object> listResponse = new LinkedHashMap<>();
            listResponse.put("status", 200);
            listResponse.put("items", body);
            return listResponse;
        }
        if (body instanceof String) {
            Map<String, Object> stringResponse = new LinkedHashMap<>();
            stringResponse.put("status", 200);
            stringResponse.put("message", body);
            return stringResponse;
        }
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> flatMap = objectMapper.convertValue(body, Map.class);
            if (!flatMap.containsKey("status")) {
                flatMap.put("status", 200);
            }
            return flatMap;
        } catch (Exception e) {
            return body;
        }
    }
}