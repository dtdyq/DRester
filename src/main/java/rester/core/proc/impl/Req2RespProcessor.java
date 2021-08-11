package rester.core.proc.impl;

import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;

import kong.unirest.HttpRequestWithBody;
import kong.unirest.HttpResponse;
import rester.core.Task;
import rester.core.proc.Processor;
import rester.model.HttpContainer;
import rester.model.Request;
import rester.model.Response;
import rester.tools.LOG;
import rester.tools.UTIL;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class Req2RespProcessor extends Processor {
    private static final Logger LOGGER = LOG.getLog("ReqProc");

    @Override
    public void proc(Task task, int index, HttpContainer container) {
        Response response = new Response();
        long cur = 0;
        try {
            Request request = container.getRequest();
            HttpRequestWithBody temp = container.getInstance().request(request.getMethod(), request.getUrl());

            if (StringUtils.isNotBlank(request.getBasicAuthUser())
                && StringUtils.isNotBlank(request.getBasicAuthPwd())) {
                temp.basicAuth(UTIL.decode(request.getBasicAuthUser()), UTIL.decode(request.getBasicAuthPwd()));
            }
            Optional.ofNullable(request.getTimeout()).ifPresent(temp::socketTimeout);
            Optional.ofNullable(request.getBody()).ifPresent(temp::body);
            Optional.ofNullable(request.getHeader()).ifPresent(temp::headers);

            cur = System.currentTimeMillis();
            Retryer<HttpResponse<String>> retryer = RetryerBuilder.<HttpResponse<String>>newBuilder()
                .retryIfException()
                .retryIfRuntimeException()
                .withWaitStrategy(WaitStrategies.fixedWait(1, TimeUnit.SECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(task.getCfg().getRetryTimes()))
                .retryIfResult(Objects::isNull)
                .build();
            HttpResponse<String> ret = retryer.call(temp::asString);
            response.setCost((int) (System.currentTimeMillis() - cur));
            Optional.ofNullable(ret).ifPresent(resp -> {
                response.setCode(resp.getStatus());
                response.setBody(resp.getBody());
            });
            container.setResponse(response);
        } catch (Exception e) {
            LOGGER.error("req error for task {} index {} : {}", task.getName(), index, e.getMessage());
            response.setCost((int) (System.currentTimeMillis() - cur));
            response.setExceptMsg(getExceptMsg(e));
            response.setCode(-1);
            response.setBody("");
            container.setResponse(response);
        } finally {
            nextProc(task, index, container);
        }
    }

    private String getExceptMsg(Throwable e) {
        String s = e.getMessage();
        while (e.getCause() != null) {
            e = e.getCause();
            s = e.getMessage();
        }
        return s;
    }
}
