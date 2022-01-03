package codes.writeonce.templates;

import javax.annotation.Nonnull;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public class MapResolver<E extends Throwable> implements Resolver<E> {

    @Nonnull
    private final Map<String, String> map;

    public MapResolver(@Nonnull Map<String, String> map) {
        this.map = requireNonNull(map);
    }

    @Override
    public void resolve(@Nonnull String name, @Nonnull AppenderAppendable<E> listener) throws E {
        listener.append(requireNonNull(map.get(requireNonNull(name))));
    }
}
