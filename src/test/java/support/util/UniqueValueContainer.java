package support.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author : sechan
 */
public class UniqueValueContainer<T> {
    private final Set<T> container;
    private final Supplier<T> generator;

    public UniqueValueContainer(Supplier<T> generator) {
        container = new HashSet<>();
        this.generator = generator;
    }

    public T gen() {
        T value = null;
        boolean isUnique = false;
        while(!isUnique) {
            value = generator.get();
            isUnique = !container.contains(value);
        }
        container.add(value);
        return value;
    }

    public T getAny() {
        return container.stream().findAny().orElseThrow();
    }

    public Set<T> getContainer() {
        return Collections.unmodifiableSet(container);
    }
}
