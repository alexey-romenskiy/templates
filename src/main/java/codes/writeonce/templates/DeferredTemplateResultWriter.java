package codes.writeonce.templates;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class DeferredTemplateResultWriter<E extends Throwable> implements AppenderAppendable<E> {

    @Nonnull
    private final StringBuilder builder = new StringBuilder();

    @Nonnull
    private final List<Appender<E>> appenders = new ArrayList<>();

    @Nonnull
    @Override
    public GenericAppendable<E> append(@Nonnull Appender<E> appender) {
        flush();
        appenders.add(requireNonNull(appender));
        return this;
    }

    @Nonnull
    @Override
    public DeferredTemplateResultWriter<E> append(@Nonnull CharSequence csq) {
        builder.append(requireNonNull(csq));
        return this;
    }

    @Nonnull
    @Override
    public DeferredTemplateResultWriter<E> append(@Nonnull CharSequence csq, int start, int end) {
        builder.append(requireNonNull(csq), start, end);
        return this;
    }

    @Nonnull
    @Override
    public DeferredTemplateResultWriter<E> append(char c) {
        builder.append(c);
        return this;
    }

    public Appender<E> build() {
        flush();
        switch (appenders.size()) {
            case 0:
                return new EmptyAppender<>();
            case 1:
                return appenders.get(0);
            default:
                return new SequentialAppender<>(getAppenders());
        }
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    private Appender<E>[] getAppenders() {
        return appenders.toArray(new Appender[0]);
    }

    private void flush() {
        if (builder.length() != 0) {
            appenders.add(new CharSequenceAppender<>(builder.toString()));
            builder.setLength(0);
        }
    }
}
