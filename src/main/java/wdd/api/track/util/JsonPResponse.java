package wdd.api.track.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;

public class JsonPResponse {

    private static final ObjectMapper objectMapper;
    static {
        objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    private boolean ok;
    private int code;
    private String description;
    private Object data;

    private JsonPResponse() { }

    public static String of(final String callback, final Object data) {
        JsonPResponse response = new JsonPResponse();
        response.ok = true;
        response.code = HttpStatus.OK.value();
        response.description = "成功";
        response.data = data;
        String responseStr;
        try {
            responseStr = objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("数据转换为JSON时出现异常", e);
        }
        return String.format("%s(%s)", callback, responseStr);
    }

    public static String of(final String jsonpCallback, final Object data, String description) {
        JsonPResponse response = new JsonPResponse();
        response.ok = true;
        response.code = HttpStatus.OK.value();
        response.description = description;
        response.data = data;
        String responseStr;
        try {
            responseStr = objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("数据转换为JSON时出现异常", e);
        }
        return String.format("%s(%s)", jsonpCallback, responseStr);
    }

    public static String ofError(final String jsonpCallback, String description, HttpStatus httpStatus) {
        JsonPResponse response = new JsonPResponse();
        response.ok = false;
        response.code = httpStatus.value();
        response.description = description;
        response.data = null;
        String responseStr;
        try {
            responseStr = objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("数据转换为JSON时出现异常", e);
        }
        return String.format("%s(%s)", jsonpCallback, responseStr);
    }
}
