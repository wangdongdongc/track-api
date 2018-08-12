package wdd.api.track.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wdd.api.track.constant.JsonP;
import wdd.api.track.util.JsonPResponse;
import wdd.api.track.util.JsonResponse;

import javax.servlet.http.HttpServletRequest;

/**
 * WebTrackController 异常处理
 */
@RestControllerAdvice(basePackageClasses = WebTrackController.class)
public class WebTrackControllerExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(WebTrackControllerExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.OK)
    public Object illegalArgumentExceptionHandler(IllegalArgumentException e, HttpServletRequest request) {
        String callback = request.getParameter(JsonP.CALLBACK_PARAM);
        if (callback != null) {
            return JsonPResponse.ofError(callback,
                    "[IllegalArgumentException] " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } else {
            return JsonResponse.ofError("[IllegalArgumentException] " + e.getMessage());
        }
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public Object commonExceptionHandler(Exception e, HttpServletRequest request) {
        String callback = request.getParameter("callback");
        LOG.error("[服务器异常]", e);
        if (callback != null) {
            return JsonPResponse.ofError(callback,
                    "[InternalServerException]", HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            return JsonResponse.ofError("[InternalServerException]");
        }
    }
}
