package codes.writeonce.templates;

import javax.annotation.Nonnull;

public interface Appender<E extends Throwable> {

    void appendTo(@Nonnull AppenderAppendable<E> listener) throws E;
}
