package rester.core.policy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomAssignPolicy implements AssignPolicy<Integer> {

    @Override
    public List<List<Integer>> assign(List<Integer> source, int count) {
        List<List<Integer>> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            result.add(new ArrayList<>());
        }
        Random random = new Random();
        for (Integer t : source) {
            result.get(random.nextInt(count)).add(t);
        }
        return result;
    }
}
