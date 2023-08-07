/*
 * This file is part of Pebble.
 * <p>
 * Copyright (c) 2014 by Mitchell Bösecke
 * <p>
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.pebble.node;

import io.pebbletemplates.pebble.extension.NodeVisitor;
import io.pebbletemplates.pebble.node.expression.Expression;
import io.pebbletemplates.pebble.template.EvaluationContextImpl;
import io.pebbletemplates.pebble.template.PebbleTemplateImpl;

import java.io.Writer;

public class SetNode extends AbstractRenderableNode {

  private final String name;

  private final Expression<?> value;

  public SetNode(int lineNumber, String name, Expression<?> value) {
    super(lineNumber);
    this.name = name;
    this.value = value;
  }

  @Override
  public void render(PebbleTemplateImpl self, Writer writer, EvaluationContextImpl context) {
    context.getScopeChain().set(this.name, this.value.evaluate(self, context));
  }

  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }

  public Expression<?> getValue() {
    return this.value;
  }

  public String getName() {
    return this.name;
  }

}
