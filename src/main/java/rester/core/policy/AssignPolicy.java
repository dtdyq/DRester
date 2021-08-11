package rester.core.policy;

import java.util.List;

public interface AssignPolicy<T> {
    List<List<T>> assign(List<T> source, int count);
}
