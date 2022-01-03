package codes.writeonce.templates;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.function.Function;

public class Templates {

    private final Map<String, Appender<RuntimeException>> templates = new HashMap<>();

    private final LinkedHashSet<String> stack = new LinkedHashSet<>();

    private final Resolver<RuntimeException> runtimeResolver = new Resolver<RuntimeException>() {

        @Override
        public void resolve(@Nonnull String name, @Nonnull AppenderAppendable<RuntimeException> listener) {
            final Appender<RuntimeException> template = templates.get(name);
            if (template == null) {
                append(this, listener, name);
            } else {
                listener.append(template);
            }
        }
    };

    private final Resolver<RuntimeException> staticResolver = (name, listener) -> {
        final Appender<RuntimeException> template = templates.get(name);
        if (template == null) {
            listener.append(l -> append(runtimeResolver, l, name));
        } else {
            listener.append(template);
        }
    };

    @Nonnull
    private final ArrayDequePool<TemplateParser<RuntimeException>> templateParserPool;

    @Nonnull
    private final Function<String, CharSequence> parameters;

    @Nonnull
    private final Function<String, CharSequence> parseableParameters;

    public Templates(
            @Nonnull ArrayDequePool<TemplateParser<RuntimeException>> templateParserPool,
            @Nonnull Function<String, CharSequence> parameters,
            @Nonnull Function<String, CharSequence> parseableParameters
    ) {
        this.templateParserPool = templateParserPool;
        this.parameters = parameters;
        this.parseableParameters = parseableParameters;
    }

    @Nonnull
    public Appender<RuntimeException> parse(@Nonnull CharSequence source) {

        final DeferredTemplateResultWriter<RuntimeException> writer = new DeferredTemplateResultWriter<>();
        parse(staticResolver, writer, source);
        return writer.build();
    }

    @Nonnull
    public Appender<RuntimeException> parse(@Nonnull CharSequence source,
            @Nonnull Resolver<RuntimeException> compileResolver) {

        final DeferredTemplateResultWriter<RuntimeException> writer = new DeferredTemplateResultWriter<>();
        parse(compileResolver, writer, source);
        return writer.build();
    }

    @Nonnull
    public Appender<RuntimeException> resolve(@Nonnull String name) {

        final DeferredTemplateResultWriter<RuntimeException> writer = new DeferredTemplateResultWriter<>();

        runtimeResolver.resolve(name, writer);

        return writer.build();
    }

    public void parse(@Nonnull Map<String, String> sources) {

        final Resolver<RuntimeException> compileResolver = new Resolver<RuntimeException>() {
            @Override
            public void resolve(@Nonnull String name, @Nonnull AppenderAppendable<RuntimeException> listener) {
                final Appender<RuntimeException> template = templates.get(name);
                if (template == null) {
                    final String source = sources.get(name);
                    if (source == null) {
                        listener.append(l -> append(this, l, name));
                    } else {
                        parse(this, sources, name, source);
                    }
                } else {
                    listener.append(template);
                }
            }
        };

        while (!sources.isEmpty()) {
            final Map.Entry<String, String> entry = sources.entrySet().iterator().next();
            parse(compileResolver, sources, entry.getKey(), entry.getValue());
        }
    }

    private void parse(
            @Nonnull Resolver<RuntimeException> resolver,
            @Nonnull Map<String, String> sources,
            @Nonnull String name,
            @Nonnull String source
    ) {
        final DeferredTemplateResultWriter<RuntimeException> writer = new DeferredTemplateResultWriter<>();
        appendParseable(resolver, writer, name, source);
        templates.put(name, writer.build());
        sources.remove(name);
    }

    private void append(
            @Nonnull Resolver<RuntimeException> resolver,
            @Nonnull AppenderAppendable<RuntimeException> listener,
            @Nonnull String name
    ) {
        final CharSequence parameter = parameters.apply(name);
        if (parameter == null) {
            final CharSequence parseableParameter = parseableParameters.apply(name);
            if (parseableParameter == null) {
                throw new IllegalArgumentException("Parameter \"" + name + "\" not defined");
            } else {
                appendParseable(resolver, listener, name, parseableParameter);
            }
        } else {
            listener.append(parameter);
        }
    }

    private void appendParseable(
            @Nonnull Resolver<RuntimeException> resolver,
            @Nonnull AppenderAppendable<RuntimeException> listener,
            @Nonnull String name,
            @Nonnull CharSequence source
    ) {
        if (!stack.add(name)) {
            throw new IllegalArgumentException("Template reference cycle detected: " + stack);
        }

        try {
            parse(resolver, listener, source);
        } finally {
            stack.remove(name);
        }
    }

    private void parse(
            @Nonnull Resolver<RuntimeException> resolver,
            @Nonnull AppenderAppendable<RuntimeException> listener,
            @Nonnull CharSequence source
    ) {
        final TemplateParser<RuntimeException> templateParser = templateParserPool.get();

        try {
            templateParser.reset(resolver, listener).append(source).end();
        } finally {
            templateParserPool.put(templateParser);
        }
    }
}
