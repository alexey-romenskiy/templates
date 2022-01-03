package codes.writeonce.templates;

import org.junit.Test;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class TemplatesTest {

    @Test
    public void parse1() {

        final Map<String, String> map1 = new HashMap<>();
        map1.put("foo", "12${bar}3");

        final Map<String, String> map2 = new HashMap<>();
        map2.put("bar", "456");

        final Map<String, String> map3 = new HashMap<>();
        map3.put("baz", "78${foo}9");

        final Templates templates = new Templates(new ArrayDequePool<>(TemplateParser::new), map2::get, map3::get);
        templates.parse(map1);

        final Appender<RuntimeException> appender = templates.parse("foo${foo}bar$${bar}foo$$${ baz }$$");
        assertEquals("foo124563bar${bar}foo$781245639$$", render(appender));
    }

    @Test
    public void parse1b() {

        final Map<String, String> map2 = new HashMap<>();
        map2.put("bar", "456");

        final Map<String, String> map3 = new HashMap<>();
        map3.put("foo", "12${bar}3");
        map3.put("baz", "78${foo}9");

        final Templates templates = new Templates(new ArrayDequePool<>(TemplateParser::new), map2::get, map3::get);

        final Appender<RuntimeException> appender = templates.parse("foo${foo}bar$${bar}foo$$${ baz }$$");
        assertEquals("foo124563bar${bar}foo$781245639$$", render(appender));
    }

    @Test
    public void parse2() {

        final Map<String, String> map1 = new HashMap<>();
        map1.put("baz", "78${foo}9");

        final Map<String, String> map2 = new HashMap<>();
        map2.put("bar", "456");

        final Map<String, String> map3 = new HashMap<>();
        map3.put("foo", "12${bar}3");

        final Templates templates = new Templates(new ArrayDequePool<>(TemplateParser::new), map2::get, map3::get);
        templates.parse(map1);

        final Appender<RuntimeException> appender = templates.parse("foo${foo}bar$${bar}foo$$${ baz }$$");
        assertEquals("foo124563bar${bar}foo$781245639$$", render(appender));
    }

    @Test
    public void parse2b() {

        final Map<String, String> map2 = new HashMap<>();
        map2.put("bar", "456");

        final Map<String, String> map3 = new HashMap<>();
        map3.put("baz", "78${foo}9");
        map3.put("foo", "12${bar}3");

        final Templates templates = new Templates(new ArrayDequePool<>(TemplateParser::new), map2::get, map3::get);

        final Appender<RuntimeException> appender = templates.parse("foo${foo}bar$${bar}foo$$${ baz }$$");
        assertEquals("foo124563bar${bar}foo$781245639$$", render(appender));
    }

    @Test(expected = IllegalArgumentException.class)
    public void parse3() {

        final Map<String, String> map1 = new HashMap<>();
        map1.put("foo", "12${baz}3");

        final Map<String, String> map2 = new HashMap<>();
        map2.put("bar", "456");

        final Map<String, String> map3 = new HashMap<>();
        map3.put("baz", "78${foo}9");

        final Templates templates = new Templates(new ArrayDequePool<>(TemplateParser::new), map2::get, map3::get);
        templates.parse(map1);

        final Appender<RuntimeException> appender = templates.parse("foo${foo}bar$${bar}foo$$${ baz }$$");
        assertEquals("foo124563bar${bar}foo$781245639$$", render(appender));
    }

    @Nonnull
    private String render(Appender<RuntimeException> appender) {
        final StringBuilder builder = new StringBuilder();
        appender.appendTo(new TemplateResultWriter<>(new StringBuilderAppendable(builder)));
        return builder.toString();
    }
}
