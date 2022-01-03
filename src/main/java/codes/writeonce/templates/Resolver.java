package codes.writeonce.templates;

import javax.annotation.Nonnull;

public interface Resolver<E extends Throwable> {

    void resolve(@Nonnull String name, @Nonnull AppenderAppendable<E> listener) throws E;
}
