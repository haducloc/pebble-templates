package io.pebbletemplates.pebble.extension.core;

import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;

import java.util.Map;

/**
 * Implementation for the test function 'defined'.
 *
 * Inversion of 'null' test function to provide better compatibility with the original twig version
 * and JTwig.
 *
 * @author Thomas Hunziker
 */
public class DefinedTest extends NullTest {

  @Override
  public boolean apply(Object input, Map<String, Object> args, PebbleTemplate self,
                       EvaluationContext context, int
      lineNumber) {
    return !super.apply(input, args, self, context, lineNumber);
  }

}
