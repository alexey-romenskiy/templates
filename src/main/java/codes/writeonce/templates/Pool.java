package codes.writeonce.templates;

import javax.annotation.Nonnull;

public interface Pool<T> {

    @Nonnull
    T get();

    void put(@Nonnull T value);
}
