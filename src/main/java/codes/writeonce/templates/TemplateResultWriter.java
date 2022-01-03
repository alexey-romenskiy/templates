package codes.writeonce.templates;

import javax.annotation.Nonnull;

import static java.util.Objects.requireNonNull;

public class TemplateResultWriter<E extends Throwable> implements AppenderAppendable<E> {

    @Nonnull
    private final GenericAppendable<? extends E> appendable;

    public TemplateResultWriter(@Nonnull GenericAppendable<? extends E> appendable) {
        this.appendable = requireNonNull(appendable);
    }

    @Nonnull
    @Override
    public GenericAppendable<E> append(@Nonnull Appender<E> appender) throws E {
        appender.appendTo(this);
        return this;
    }

    @Nonnull
    @Override
    public TemplateResultWriter<E> append(@Nonnull CharSequence csq) throws E {
        appendable.append(requireNonNull(csq));
        return this;
    }

    @Nonnull
    @Override
    public TemplateResultWriter<E> append(@Nonnull CharSequence csq, int start, int end) throws E {
        appendable.append(requireNonNull(csq), start, end);
        return this;
    }

    @Nonnull
    @Override
    public TemplateResultWriter<E> append(char c) throws E {
        appendable.append(c);
        return this;
    }
}
