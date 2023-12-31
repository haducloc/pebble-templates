/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.pebble.template;

import io.pebbletemplates.pebble.extension.NamedArguments;
import java.util.Map;

public interface Macro extends NamedArguments {

  String getName();

  String call(PebbleTemplateImpl self, EvaluationContextImpl context, Map<String, Object> args);
}
