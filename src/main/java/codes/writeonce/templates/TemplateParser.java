package codes.writeonce.templates;

import javax.annotation.Nonnull;

import static java.util.Objects.requireNonNull;

public class TemplateParser<E extends Throwable> {

    private int state = 0;

    private int count;

    private int line = 1;

    private int column = 1;

    @Nonnull
    private final StringBuilder builder = new StringBuilder();

    @Nonnull
    private Resolver<E> resolver;

    @Nonnull
    private AppenderAppendable<E> listener;

    public TemplateParser() {
        this(new DummyResolver<>(), new DummyAppenderAppendable<>());
    }

    public TemplateParser(@Nonnull Resolver<E> resolver, @Nonnull AppenderAppendable<E> listener) {
        this.resolver = requireNonNull(resolver);
        this.listener = requireNonNull(listener);
    }

    public TemplateParser<E> reset(@Nonnull Resolver<E> resolver, @Nonnull AppenderAppendable<E> listener) {
        this.resolver = requireNonNull(resolver);
        this.listener = requireNonNull(listener);
        state = 0;
        line = 1;
        column = 1;
        builder.setLength(0);
        return this;
    }

    @Nonnull
    public TemplateParser<E> end() throws E {
        switch (state) {
            case 0:
                state = 5;
                break;
            case 1:
                appendEscaped(count);
                state = 5;
                break;
            case 2:
            case 3:
            case 4:
                throw new IllegalStateException("Unexpected end at line " + line + ", column " + column);
            default:
                throw new IllegalStateException("Illegal state at line " + line + ", column " + column);
        }
        return this;
    }

    @Nonnull
    public TemplateParser<E> append(char c) throws E {
        switch (state) {
            case 0:
                switch (c) {
                    case '$':
                        state = 1;
                        count = 1;
                        break;
                    default:
                        listener.append(c);
                }
                break;
            case 1:
                switch (c) {
                    case '$':
                        count++;
                        break;
                    case '{':
                        appendEscaped(count / 2);
                        if (count % 2 == 0) {
                            listener.append(c);
                            state = 0;
                        } else {
                            state = 2;
                        }
                        break;
                    default:
                        appendEscaped(count);
                        state = 0;
                        listener.append(c);
                }
                break;
            case 2:
                if (!Character.isWhitespace(c)) {
                    if (c == '}') {
                        throw new IllegalArgumentException(
                                "Expected variable name at line " + line + ", column " + column);
                    }
                    state = 3;
                    builder.append(c);
                }
                break;
            case 3:
                if (Character.isWhitespace(c)) {
                    state = 4;
                } else if (c == '}') {
                    appendVariable();
                    state = 0;
                } else {
                    builder.append(c);
                }
                break;
            case 4:
                if (!Character.isWhitespace(c)) {
                    if (c == '}') {
                        appendVariable();
                        state = 0;
                    } else {
                        throw new IllegalArgumentException("Expected '}' at line " + line + ", column " + column);
                    }
                }
                break;
            default:
                throw new IllegalStateException("Illegal state at line " + line + ", column " + column);
        }

        switch (c) {
            case '\n':
                column = 1;
                line++;
                break;
            case '\r':
                break;
            default:
                column++;
        }

        return this;
    }

    @Nonnull
    public TemplateParser<E> append(@Nonnull CharSequence csq) throws E {
        final int length = csq.length();
        for (int i = 0; i < length; i++) {
            append(csq.charAt(i));
        }
        return this;
    }

    private void appendVariable() throws E {
        final String name = builder.toString();
        builder.setLength(0);
        resolver.resolve(name, listener);
    }

    private void appendEscaped(int n) throws E {
        if (n != 0) {
            do {
                listener.append('$');
            } while (--n != 0);
        }
    }

    private static class DummyResolver<E extends Throwable> implements Resolver<E> {

        @Override
        public void resolve(@Nonnull String name, @Nonnull AppenderAppendable<E> listener) {
            throw new UnsupportedOperationException();
        }
    }

    private static class DummyAppenderAppendable<E extends Throwable> implements AppenderAppendable<E> {

        @Nonnull
        @Override
        public GenericAppendable<E> append(@Nonnull Appender<E> appender) {
            throw new UnsupportedOperationException();
        }

        @Nonnull
        @Override
        public GenericAppendable<E> append(@Nonnull CharSequence csq) {
            throw new UnsupportedOperationException();
        }

        @Nonnull
        @Override
        public GenericAppendable<E> append(@Nonnull CharSequence csq, int start, int end) {
            throw new UnsupportedOperationException();
        }

        @Nonnull
        @Override
        public GenericAppendable<E> append(char c) {
            throw new UnsupportedOperationException();
        }
    }
}
