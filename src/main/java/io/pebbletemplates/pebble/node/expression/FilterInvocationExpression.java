/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.pebble.node.expression;

import io.pebbletemplates.pebble.extension.NodeVisitor;
import io.pebbletemplates.pebble.node.ArgumentsNode;
import io.pebbletemplates.pebble.template.EvaluationContextImpl;
import io.pebbletemplates.pebble.template.PebbleTemplateImpl;

/**
 * The right hand side to the filter expression.
 *
 * @author Mitchell
 */
public class FilterInvocationExpression implements Expression<Object> {

  private final String filterName;

  private final ArgumentsNode args;

  private final int lineNumber;

  public FilterInvocationExpression(String filterName, ArgumentsNode args, int lineNumber) {
    this.filterName = filterName;
    this.args = args;
    this.lineNumber = lineNumber;
  }

  @Override
  public Object evaluate(PebbleTemplateImpl self, EvaluationContextImpl context) {
    // see FilterExpression.java
    throw new UnsupportedOperationException();
  }

  public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }

  public ArgumentsNode getArgs() {
    return this.args;
  }

  public String getFilterName() {
    return this.filterName;
  }

  @Override
  public int getLineNumber() {
    return this.lineNumber;
  }

}
