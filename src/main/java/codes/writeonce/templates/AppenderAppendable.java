package codes.writeonce.templates;

import javax.annotation.Nonnull;

public interface AppenderAppendable<E extends Throwable> extends GenericAppendable<E> {

    @Nonnull
    GenericAppendable<E> append(@Nonnull Appender<E> appender) throws E;
}
