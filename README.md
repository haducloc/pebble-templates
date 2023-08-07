# appslandia-pebble
- Fork of PebbleTemplates with dynamic named arguments supported for functions, filters, and tests.

# Original project
- https://github.com/PebbleTemplates/pebble.git

# License
- Please refer to the license in the original project.
- All code in this fork is the property of the original developer.
  
# What I modified
- I have added the DynamicNamedArguments interface. Any Function, filter, or test that implements this interface will support dynamic named arguments.
- I have also made modifications to the original ArgumentsNode.java file in order to deactivate the validation of named arguments if the invocableWithNamedArguments implements the DynamicNamedArguments interface.
- I have also supplied my own pom.xml, which is based on the original pom.xml created by the initial developer.
