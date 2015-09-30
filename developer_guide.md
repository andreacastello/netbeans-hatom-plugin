# Before starting #
The Netbeans module works on Netbeans IDE 6.1 and the latest version 6.5.
Source code structure

Source code for Netbeans module is organized as follows:

**it.pronetics.madstore.hatom** is the main package for source code and configurations; it contains two files:

  * Bundle.properties
  * layer.xml

The Bundle.properties file defines the attributes needed for module's installation and integration with the Netbeans infrastructure.

The layer.xml file defines what are the exact points where the plugin interacts with the Netbeans infrastructure and APIs.

Under the main package, three other packages are found.

  * the completion package provides classes for code completion tasks.
  * the syntax package provides classes for code highlight task. The syntax package contains a child package, resources, which only contains resource files used by Netbeans Options window.
  * the validator package contains two subpackages: action provides the entry point class for the validator task action, while the engine package provides classes for performing validation of all hAtom elements.

Developers who would like to modify or extend the hAtom Netbeans plugin may find useful the pages dedicated to the three main modules of the plugin.

### Code completion ###
[Code completion](http://code.google.com/p/netbeans-hatom-plugin/wiki/hatom_code_completion)

### Syntax highlight ###
[Syntax highlight](http://code.google.com/p/netbeans-hatom-plugin/wiki/hAtom_syntax_highlight)

### Hatom validation ###
[hAtom microformat validation](http://code.google.com/p/netbeans-hatom-plugin/wiki/hatom_microformat_validation)