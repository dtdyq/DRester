package rester.core.proc.impl;

import rester.core.Task;
import rester.core.proc.Processor;
import rester.model.HttpContainer;
import rester.model.Request;
import rester.model.Response;
import rester.model.assertion.Assertion;
import rester.tools.JSON;

import java.nio.charset.Charset;
import java.util.stream.Collectors;

public class AssertionProcessor extends Processor {

    @Override
    public void proc(Task task, int index, HttpContainer container) {

        Request request = container.getRequest();
        Response response = container.getResponse();
        if (String.valueOf(response.getCode()).startsWith("2")) {
            request.getAssertions().forEach(assertion -> assertion(assertion, response));
        } else {
            response.getFailedAsserts()
                .addAll(request.getAssertions().stream().map(Assertion::getId).collect(Collectors.toList()));
        }
        nextProc(task, index, container);
    }

    private void assertion(Assertion assertion, Response response) {
        switch (assertion.getRef()) {
            case code: {
                if (checkNumber(assertion, response.getCode())) {
                    response.getSucceedAsserts().add(assertion.getId());
                } else {
                    response.getFailedAsserts().add(assertion.getId());
                }
            }
                break;
            case header: {
                if (check(assertion, JSON.toJson(response.getHeader()))) {
                    response.getSucceedAsserts().add(assertion.getId());
                } else {
                    response.getFailedAsserts().add(assertion.getId());
                }
            }
                break;
            case resp: {
                if (check(assertion, response.getBody())) {
                    response.getSucceedAsserts().add(assertion.getId());
                } else {
                    response.getFailedAsserts().add(assertion.getId());
                }
            }
                break;
            case respSize: {
                if (checkNumber(assertion,
                    response.getBody() == null ? 0 : response.getBody().getBytes(Charset.forName("utf-8")).length)) {
                    response.getSucceedAsserts().add(assertion.getId());
                } else {
                    response.getFailedAsserts().add(assertion.getId());
                }
            }
                break;
            case cost: {
                if (checkNumber(assertion, response.getCost())) {
                    response.getSucceedAsserts().add(assertion.getId());
                } else {
                    response.getFailedAsserts().add(assertion.getId());
                }
            }
                break;
        }
    }

    private boolean checkNumber(Assertion assertion, int real) {
        switch (assertion.getOper()) {
            case eq:
                return Integer.parseInt(assertion.getExpect()) == real;
            case ne:
                return Integer.parseInt(assertion.getExpect()) != real;
            case lt:
                return real < Integer.parseInt(assertion.getExpect());
            case le:
                return real <= Integer.parseInt(assertion.getExpect());
            case ge:
                return real >= Integer.parseInt(assertion.getExpect());
            case gt:
                return real > Integer.parseInt(assertion.getExpect());
            case match:
                return String.valueOf(real).matches(assertion.getExpect());
            case nomatch:
                return !String.valueOf(real).matches(assertion.getExpect());
        }
        return false;
    }

    private boolean check(Assertion assertion, String real) {
        if (real == null) {
            return false;
        }
        switch (assertion.getOper()) {
            case eq:
                return assertion.getExpect().equals(real);
            case ne:
                return !assertion.getExpect().equals(real);
            case lt:
                return real.compareTo(assertion.getExpect()) < 0;
            case le:
                return real.compareTo(assertion.getExpect()) <= 0;
            case ge:
                return real.compareTo(assertion.getExpect()) >= 0;
            case gt:
                return real.compareTo(assertion.getExpect()) > 0;
            case match:
                return real.matches(assertion.getExpect());
            case nomatch:
                return !real.matches(assertion.getExpect());
        }
        return false;
    }
}
