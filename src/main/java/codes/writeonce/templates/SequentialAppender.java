package codes.writeonce.templates;

import javax.annotation.Nonnull;

import static java.util.Objects.requireNonNull;

public class SequentialAppender<E extends Throwable> implements Appender<E> {

    @Nonnull
    private final Appender<E>[] appenders;

    public SequentialAppender(@Nonnull Appender<E>[] appenders) {
        this.appenders = requireNonNull(appenders);
    }

    @Override
    public void appendTo(@Nonnull AppenderAppendable<E> listener) throws E {
        requireNonNull(listener);
        for (final Appender<E> appender : appenders) {
            appender.appendTo(listener);
        }
    }
}
