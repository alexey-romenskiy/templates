package codes.writeonce.templates;

import org.junit.Test;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class TemplateParserTest {

    @Test
    public void put1() {
        check("foo${foo}bar$${bar}foo$$${ foo }$$", "foo123bar${bar}foo$123$$");
    }

    @Test
    public void put1b() {
        check("foo${foo}bar$${bar}foo$$${ foo }", "foo123bar${bar}foo$123");
    }

    @Test(expected = IllegalStateException.class)
    public void put2() {
        getParser(new StringBuilder()).append("foo${foo}bar$${bar}foo$$${ foo ").end();
    }

    @Test(expected = IllegalStateException.class)
    public void put3() {
        getParser(new StringBuilder()).append("foo${foo}bar$${bar}foo$$${ foo").end();
    }

    @Test(expected = IllegalStateException.class)
    public void put4() {
        getParser(new StringBuilder()).append("foo${foo}bar$${bar}foo$$${ ").end();
    }

    @Test(expected = IllegalStateException.class)
    public void put5() {
        getParser(new StringBuilder()).append("foo${foo}bar$${bar}foo$$${").end();
    }

    @Test
    public void put6() {
        check("foo${foo}bar$${bar}foo$$$", "foo123bar${bar}foo$$$");
    }

    @Test
    public void put7() {
        check("foo${foo}bar$${bar}foo$$$x", "foo123bar${bar}foo$$$x");
    }

    @Test
    public void put8() {
        check("foo$$bar", "foo$$bar");
    }

    @Test
    public void put8b() {
        check("foo$bar", "foo$bar");
    }

    private static void check(String template, String result) {
        final StringBuilder builder = new StringBuilder();
        getParser(builder).append(template).end();
        assertEquals(result, builder.toString());
    }

    @Nonnull
    private static TemplateParser<RuntimeException> getParser(StringBuilder builder) {
        return new TemplateParser<>(
                new MapResolver<>(getParameters()),
                new TemplateResultWriter<>(new StringBuilderAppendable(builder))
        );
    }

    @Nonnull
    private static Map<String, String> getParameters() {
        final Map<String, String> map = new HashMap<>();
        map.put("foo", "123");
        map.put("bar", "456");
        return map;
    }
}
