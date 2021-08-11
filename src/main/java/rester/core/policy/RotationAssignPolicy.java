package rester.core.policy;

import java.util.ArrayList;
import java.util.List;

public class RotationAssignPolicy implements AssignPolicy<Integer> {

    @Override
    public List<List<Integer>> assign(List<Integer> source, int count) {
        List<List<Integer>> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            result.add(new ArrayList<>());
        }
        for (int i = 0; i < source.size(); i++) {
            result.get(i % count).add(source.get(i));
        }
        return result;
    }
}
