package rester.core.policy;

import java.util.ArrayList;
import java.util.List;

public class SegmentAssignPolicy implements AssignPolicy<Integer> {
    @Override
    public List<List<Integer>> assign(List<Integer> source, int count) {
        List<List<Integer>> result = new ArrayList<>();
        int remainder = source.size() % count;
        int number = source.size() / count;
        int offset = 0;
        for (int i = 0; i < count; i++) {
            List<Integer> value;
            if (remainder > 0) {
                value = source.subList(i * number + offset, (i + 1) * number + offset + 1);
                remainder--;
                offset++;
            } else {
                value = source.subList(i * number + offset, (i + 1) * number + offset);
            }
            result.add(value);
        }
        return result;
    }
}
