package codes.writeonce.templates;

import javax.annotation.Nonnull;

public interface GenericAppendable<E extends Throwable> {

    @Nonnull
    GenericAppendable<E> append(@Nonnull CharSequence csq) throws E;

    @Nonnull
    GenericAppendable<E> append(@Nonnull CharSequence csq, int start, int end) throws E;

    @Nonnull
    GenericAppendable<E> append(char c) throws E;
}
