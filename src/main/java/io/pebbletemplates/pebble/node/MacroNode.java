/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.pebble.node;

import io.pebbletemplates.pebble.extension.NodeVisitor;
import io.pebbletemplates.pebble.node.expression.Expression;
import io.pebbletemplates.pebble.template.EvaluationContextImpl;
import io.pebbletemplates.pebble.template.Macro;
import io.pebbletemplates.pebble.template.PebbleTemplateImpl;
import io.pebbletemplates.pebble.template.ScopeChain;
import io.pebbletemplates.pebble.utils.LimitedSizeWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MacroNode extends AbstractRenderableNode {

  private final String name;

  private final ArgumentsNode args;

  private final BodyNode body;

  public MacroNode(String name, ArgumentsNode args, BodyNode body) {
    this.name = name;
    this.args = args;
    this.body = body;
  }

  @Override
  public void render(PebbleTemplateImpl self, Writer writer, EvaluationContextImpl context) {
    // do nothing
  }

  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }

  public Macro getMacro() {
    return new Macro() {

      @Override
      public List<String> getArgumentNames() {
        List<String> names = new ArrayList<>();
        for (NamedArgumentNode arg: MacroNode.this.getArgs().getNamedArgs()) {
          names.add(arg.getName());
        }
        return names;
      }

      @Override
      public String getName() {
        return MacroNode.this.name;
      }

      @Override
      public String call(PebbleTemplateImpl self, EvaluationContextImpl context,
          Map<String, Object> macroArgs) {
        Writer writer = LimitedSizeWriter.from(new StringWriter(), context);
        ScopeChain scopeChain = context.getScopeChain();

        // scope for default arguments
        scopeChain.pushLocalScope();
        
        // global vars provided by extensions
        context.getExtensionRegistry().getGlobalVariables().forEach(scopeChain::put);
        
        for (NamedArgumentNode arg: MacroNode.this.getArgs().getNamedArgs()) {
          Expression<?> valueExpression = arg.getValueExpression();
          if (valueExpression == null) {
            scopeChain.put(arg.getName(), null);
          } else {
            scopeChain.put(arg.getName(), arg.getValueExpression().evaluate(self, context));
          }
        }

        // scope for user provided arguments
        scopeChain.pushScope(macroArgs);

        try {
          MacroNode.this.getBody().render(self, writer, context);
        } catch (IOException e) {
          throw new RuntimeException("Could not evaluate macro [" + MacroNode.this.name + "]", e);
        }

        scopeChain.popScope(); // user arguments
        scopeChain.popScope(); // default arguments

        return writer.toString();
      }

    };
  }

  public BodyNode getBody() {
    return this.body;
  }

  public ArgumentsNode getArgs() {
    return this.args;
  }

  public String getName() {
    return this.name;
  }

}
