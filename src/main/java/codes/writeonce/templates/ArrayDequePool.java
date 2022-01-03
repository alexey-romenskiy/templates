package codes.writeonce.templates;

import javax.annotation.Nonnull;
import java.util.ArrayDeque;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public class ArrayDequePool<T> implements Pool<T> {

    @Nonnull
    private final ArrayDeque<T> deque = new ArrayDeque<>();

    @Nonnull
    private final Supplier<T> supplier;

    public ArrayDequePool(@Nonnull Supplier<T> supplier) {
        this.supplier = requireNonNull(supplier);
    }

    @Nonnull
    @Override
    public T get() {
        final T first = deque.pollFirst();
        if (first == null) {
            return requireNonNull(supplier.get());
        }
        return first;
    }

    @Override
    public void put(@Nonnull T value) {
        deque.addFirst(requireNonNull(value));
    }
}
