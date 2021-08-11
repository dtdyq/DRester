package rester.core.proc;

import rester.core.Task;
import rester.model.HttpContainer;

import java.util.HashMap;
import java.util.Map;

public abstract class Processor {
    private Processor next;

    public Processor next() {
        return next;
    }

    public void next(Processor processor) {
        this.next = processor;
    }

    public abstract void proc(Task task, int index, HttpContainer container);

    protected void nextProc(Task task, int index, HttpContainer container) {
        if (next() != null) {
            next().proc(task, index, container);
        }
    }

    protected Map<String, Object> args(int index, Task task) {
        Map<String, Object> context = new HashMap<>();
        task.getContext()
            .getGlobalArgs()
            .forEach((key, values) -> context.put(key,
                values.isEmpty() ? "" : index >= values.size() ? values.get(values.size() - 1) : values.get(index)));
        task.getArgs()
            .forEach((key, values) -> context.put(key,
                values.isEmpty() ? "" : index >= values.size() ? values.get(values.size() - 1) : values.get(index)));
        context.put("index", index);
        context.put("name", task.getName());
        return context;
    }

    protected String normalize(String s) {
        return s == null ? s : s.replaceAll("\\r", "").replaceAll("\\n", "");
    }
}
