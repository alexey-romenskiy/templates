package codes.writeonce.templates;

import javax.annotation.Nonnull;

import static java.util.Objects.requireNonNull;

public class EmptyAppender<E extends Throwable> implements Appender<E> {

    @Override
    public void appendTo(@Nonnull AppenderAppendable<E> listener) {
        requireNonNull(listener);
    }
}
