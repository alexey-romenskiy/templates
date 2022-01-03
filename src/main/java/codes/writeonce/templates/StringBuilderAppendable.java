package codes.writeonce.templates;

import javax.annotation.Nonnull;

import static java.util.Objects.requireNonNull;

public class StringBuilderAppendable implements GenericAppendable<RuntimeException> {

    @Nonnull
    private final StringBuilder stringBuilder;

    public StringBuilderAppendable(@Nonnull StringBuilder stringBuilder) {
        this.stringBuilder = requireNonNull(stringBuilder);
    }

    @Nonnull
    @Override
    public GenericAppendable<RuntimeException> append(@Nonnull CharSequence csq) {
        stringBuilder.append(requireNonNull(csq));
        return this;
    }

    @Nonnull
    @Override
    public GenericAppendable<RuntimeException> append(@Nonnull CharSequence csq, int start, int end) {
        stringBuilder.append(requireNonNull(csq), start, end);
        return this;
    }

    @Nonnull
    @Override
    public GenericAppendable<RuntimeException> append(char c) {
        stringBuilder.append(c);
        return this;
    }
}
