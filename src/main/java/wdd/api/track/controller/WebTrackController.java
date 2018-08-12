package wdd.api.track.controller;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.AsyncRestTemplate;
import wdd.api.track.constant.JsonP;
import wdd.api.track.model.Behavior;
import wdd.api.track.model.Tracker;
import wdd.api.track.service.BehaviorService;
import wdd.api.track.service.TrackerService;
import wdd.api.track.util.JsonPResponse;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 浏览器端用户行为收集接口
 */
@RequestMapping("/web")
@RestController
public class WebTrackController {

    private static final Logger LOG = LoggerFactory.getLogger(WebTrackController.class);
    private static final FastDateFormat indexNameDateFormat = FastDateFormat.getInstance("yyyy.MM.dd");

    @Value("${elasticsearch.host}")
    private String esHost;

    @Value("${elasticsearch.port}")
    private int esPort;

    @Resource
    private AsyncRestTemplate asyncRestTemplate;

    @Resource
    private TrackerService trackerService;

    @Resource
    private BehaviorService behaviorService;

    @GetMapping("/test1")
    public Object test1(@RequestParam("s1") Long s1, @RequestParam("s2") Long s2) {
        return behaviorService.getBehaviorIdByTypeId(s1, s2);
    }

    @GetMapping("/test2")
    public Object test2() {
        return trackerService.findOrCreateTrackerByUserId(new BigInteger("34562270"));
    }

    /**
     * 将用户的在（支持 JsonP {@link JsonPResponse}）
     */
    @GetMapping("/behavior")
    public String trackWebBehavior(
            @RequestParam(value = JsonP.CALLBACK_PARAM, required = false) String callback,
            @RequestParam(value = "trackerId", required = false) String trackerId,
            @RequestParam(value = "behaviorId", required = false) final String behaviorId,
            @RequestParam(value = "detail", required = false) String detail,
            @RequestParam(value = "url", required = false) String url
    ) {
        Assert.isTrue(StringUtils.isNotBlank(callback), "invalid request");
        Assert.isTrue(StringUtils.isNotBlank(trackerId), "invalid request");
        Assert.isTrue(StringUtils.isNotBlank(behaviorId), "invalid request");

        final Tracker tracker = trackerService.findTrackerById(trackerId);
        Assert.notNull(tracker, "invalid request");

        final Pair<Long, Long> typeIdPair = behaviorService.getTypeIdByBehaviorId(behaviorId);
        Behavior behavior = new Behavior(typeIdPair.getLeft(), typeIdPair.getRight());
        behavior.setUserId(tracker.getUserId());
        behavior.setDetail(detail);
        behavior.setUrl(url);

        final Map<String, Object> uriVariables = new HashMap<>();
        uriVariables.put("esHost", esHost);
        uriVariables.put("esPort", esPort);
        uriVariables.put("esIndex", "user-behavior-"  + indexNameDateFormat.format(new Date()));
        uriVariables.put("docType", "behavior");

        final ListenableFuture<ResponseEntity<String>> result =
                asyncRestTemplate.postForEntity("http://{esHost}:{esPort}/{esIndex}/{docType}",
                        new HttpEntity<>(behavior), String.class, uriVariables);

        result.addCallback(res -> {}, ex -> LOG.error("Fail to index behavior data " + behavior, ex));

        return JsonPResponse.of(callback, "ok");
    }
}
