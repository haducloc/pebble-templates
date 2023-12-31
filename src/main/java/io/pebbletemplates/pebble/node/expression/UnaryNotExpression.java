/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.pebble.node.expression;

import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.template.EvaluationContextImpl;
import io.pebbletemplates.pebble.template.PebbleTemplateImpl;
import io.pebbletemplates.pebble.utils.TypeUtils;

import static io.pebbletemplates.pebble.utils.TypeUtils.compatibleCast;

public class UnaryNotExpression extends UnaryExpression {

  @Override
  public Boolean evaluate(PebbleTemplateImpl self, EvaluationContextImpl context) {
    Object result = this.getChildExpression().evaluate(self, context);
    if (result != null) {
      if (result instanceof Boolean
              || result instanceof Number
              || result instanceof String) {
        return !TypeUtils.compatibleCast(result, Boolean.class);
      }
      throw new PebbleException(
              null,
              String.format(
                      "Unsupported value type %s. Expected Boolean, String, Number in \"if\" statement",
                      result.getClass().getSimpleName()),
              this.getLineNumber(),
              self.getName());

    }
    // input is null
    if (context.isStrictVariables()) {
      throw new PebbleException(null,
              "null value given to not() and strict variables is set to true", this.getLineNumber(),
              self.getName());
    }
    return true;
  }
}
