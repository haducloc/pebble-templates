/*
 * This file is part of Pebble.
 * <p>
 * Copyright (c) 2014 by Mitchell Bösecke
 * <p>
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.pebble;

import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.extension.AbstractExtension;
import io.pebbletemplates.pebble.extension.Function;
import io.pebbletemplates.pebble.loader.StringLoader;
import io.pebbletemplates.pebble.extension.escaper.EscapeFilter;
import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class EscaperExtensionTest {

  @Test
  void testEscapeHtml() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{{ '&<>\"\\'' | escape }}");

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("&amp;&lt;&gt;&quot;&#39;", writer.toString());
  }

  @Test
  void testPrintBigDecimal() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(true).build();

    String source = "{{ num }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Map<String, Object> context = new HashMap<>();
    BigDecimal num = new BigDecimal("1234E+4");
    context.put("num", num);

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals(num.toPlainString(), writer.toString());
    assertNotEquals(num.toString(), writer.toString());
  }

  @Test
  void testEscapeContextVariable() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{{ text | escape(strategy='html') }}");

    Map<String, Object> context = new HashMap<>();
    context.put("text", "&<>\"'");
    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("&amp;&lt;&gt;&quot;&#39;", writer.toString());
  }

  @Test
  void testEscapeWithNamedArguments() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{{ '&<>\"\\'' | escape(strategy='html') }}");

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("&amp;&lt;&gt;&quot;&#39;", writer.toString());
  }

  @Test
  void testAutoescapeLiteral() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();
    PebbleTemplate template = pebble.getTemplate("{{ '<br />' }}");
    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("<br />", writer.toString());
  }

  @Test
  void testAutoescapePrintExpression() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();
    PebbleTemplate template = pebble.getTemplate("{{ text }}");
    Map<String, Object> context = new HashMap<>();
    context.put("text", "<br />");
    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("&lt;br /&gt;", writer.toString());
  }

  @Test
  void testAutoescapeNonString() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();
    PebbleTemplate template = pebble.getTemplate("{{ text }}");
    Map<String, Object> context = new HashMap<>();
    context.put("text", Collections.singletonList("<br />"));
    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("[&lt;br /&gt;]", writer.toString());
  }

  @Test
  void testDisableAutoEscaping() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false)
        .autoEscaping(false).build();
    PebbleTemplate template = pebble.getTemplate("{{ text }}");
    Map<String, Object> context = new HashMap<>();
    context.put("text", "<br />");
    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("<br />", writer.toString());
  }

  @Test
  void testEscapeIntoAbbreviate() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();
    PebbleTemplate template = pebble.getTemplate("{{ text | escape | abbreviate(5)}}");
    Map<String, Object> context = new HashMap<>();
    context.put("text", "1234567");
    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("12...", writer.toString());
  }

  @Test
  void testDoubleEscaping() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();
    PebbleTemplate template = pebble.getTemplate("{{ text | escape }}");
    Map<String, Object> context = new HashMap<>();
    context.put("text", "<br />");
    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("&lt;br /&gt;", writer.toString());
  }

  @Test
  void testAutoescapeToken() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false)
        .autoEscaping(false).build();
    PebbleTemplate template = pebble.getTemplate(
        "{% autoescape 'html' %}{{ text }}{% endautoescape %}"
            + "{% autoescape %}{{ text }}{% endautoescape %}"
            + "{% autoescape false %}{{ text }}{% endautoescape %}");
    Map<String, Object> context = new HashMap<>();
    context.put("text", "<br />");
    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("&lt;br /&gt;&lt;br /&gt;<br />", writer.toString());
  }

  @Test
  void testAutoEscapingMacroOutput() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();
    PebbleTemplate template = pebble
        .getTemplate("{{ test(danger) }}{% macro test(input) %}<{{ input }}>{% endmacro %}");
    Writer writer = new StringWriter();
    Map<String, Object> context = new HashMap<>();
    context.put("danger", "<br>");
    template.evaluate(writer, context);
    assertEquals("<&lt;br&gt;>", writer.toString());
  }

  @Test
  void testAutoEscapingInclude() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().strictVariables(false).build();
    PebbleTemplate template = pebble.getTemplate("templates/template.autoescapeInclude1.peb");
    Map<String, Object> context = new HashMap<>();
    context.put("text", "<br>");
    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("<&lt;br&gt;>", writer.toString());
  }

  @Test
  void testAutoEscapingParentFunction() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().strictVariables(false).build();
    PebbleTemplate template = pebble.getTemplate("templates/template.autoescapeParent1.peb");
    Map<String, Object> context = new HashMap<>();
    context.put("text", "<br>");
    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("<&lt;br&gt;>", writer.toString());
  }

  @Test
  void testAutoEscapingBlockFunction() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();
    PebbleTemplate template = pebble
        .getTemplate("{% block header %}<{{ text }}>{% endblock %}{{ block('header') }}");
    Map<String, Object> context = new HashMap<>();
    context.put("text", "<br>");
    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("<&lt;br&gt;><&lt;br&gt;>", writer.toString());
  }

  @Test
  void testCustomEscapingStrategy() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false)
        .defaultEscapingStrategy("custom")
        .addEscapingStrategy("custom", input -> input.replace('a', 'b')).build();

    // replaces all a's with b's
    PebbleTemplate template = pebble.getTemplate("{{ text }}");
    Map<String, Object> context = new HashMap<>();
    context.put("text", "my name is alex");
    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("my nbme is blex", writer.toString());
  }

  @Test
  void testEscapeFunction() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false)
        .extension(new TestExtension()).build();
    PebbleTemplate template = pebble.getTemplate("{{ bad() }}");
    Map<String, Object> context = new HashMap<>();
    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("&lt;script&gt;alert(&quot;injection&quot;);&lt;/script&gt;", writer.toString());
  }

  @Test
  void testNoEscapeMacro() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();
    PebbleTemplate template = pebble
        .getTemplate("{{ test() }}{% macro test() %}<br/>{% endmacro %}");
    Map<String, Object> context = new HashMap<>();
    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("<br/>", writer.toString());
  }

  @Test
  void testCompareSafeStrings() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();
    PebbleTemplate template = pebble.getTemplate("{{ text|raw == text|raw }}");
    Map<String, Object> context = new HashMap<>();
    context.put("text", "a");
    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("true", writer.toString());
  }

  @Test
  void testEscapeJson() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder()
        .loader(new StringLoader())
        .strictVariables(false)
        .defaultEscapingStrategy(EscapeFilter.JSON_ESCAPE_STRATEGY)
        .build();

    PebbleTemplate template = pebble.getTemplate("{{ text }}");

    Map<String, Object> context = new HashMap<>();
    context.put("text", "{\"a\": \"a/b/c\"}");

    Writer writer = new StringWriter();
    template.evaluate(writer, context);

    assertEquals("{\\\"a\\\": \\\"a/b/c\\\"}", writer.toString());
  }

  public static class TestExtension extends AbstractExtension {

    @Override
    public Map<String, Function> getFunctions() {
      return Collections.singletonMap("bad", new Function() {

        @Override
        public List<String> getArgumentNames() {
          return null;
        }

        @Override
        public Object execute(Map<String, Object> args, PebbleTemplate self,
                              EvaluationContext context, int lineNumber) {
          return "<script>alert(\"injection\");</script>";
        }

      });
    }

  }
}
