package rester.core.proc;

import rester.core.Task;
import rester.core.proc.impl.AssertionProcessor;
import rester.core.proc.impl.PostLogProcessor;
import rester.core.proc.impl.PreLogProcessor;
import rester.core.proc.impl.RenderArgsProcessor;
import rester.core.proc.impl.Req2RespProcessor;
import rester.core.proc.impl.ResultRecordProcessor;
import rester.model.HttpContainer;

public final class ProcChain {
    private Processor head;

    private ProcChain() {
        head = new RenderArgsProcessor();
        ResultRecordProcessor resultRecordProcessor = new ResultRecordProcessor();
        Req2RespProcessor req2RespProcessor = new Req2RespProcessor();
        PreLogProcessor preLogProcessor = new PreLogProcessor();
        PostLogProcessor postLogProcessor = new PostLogProcessor();
        AssertionProcessor assertionProcessor = new AssertionProcessor();

        head.next(preLogProcessor);
        preLogProcessor.next(req2RespProcessor);
        req2RespProcessor.next(assertionProcessor);
        assertionProcessor.next(postLogProcessor);
        postLogProcessor.next(resultRecordProcessor);
    }

    public static ProcChain inst() {
        return new ProcChain();
    }

    public void process(Task task, int index, HttpContainer container) {
        head.proc(task, index, container);
    }
}
