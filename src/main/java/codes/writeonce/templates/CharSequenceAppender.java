package codes.writeonce.templates;

import javax.annotation.Nonnull;

import static java.util.Objects.requireNonNull;

public class CharSequenceAppender<E extends Throwable> implements Appender<E> {

    @Nonnull
    private final CharSequence charSequence;

    public CharSequenceAppender(@Nonnull CharSequence charSequence) {
        this.charSequence = requireNonNull(charSequence);
    }

    @Override
    public void appendTo(@Nonnull AppenderAppendable<E> listener) throws E {
        listener.append(charSequence);
    }
}
