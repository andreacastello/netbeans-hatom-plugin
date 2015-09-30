For the validation task, the layer.xml file defines an "Action" object responsible for the task's lifecycle.

```
<folder name="Actions">
    <folder name="BpelNodes">
        <file name="it-pronetics-madstore-hatom-validator-action-ValidatorAction.instance"/>
    </folder>
</folder>
```

The ValidatorAction class is created and started by the Netbeans platform. The validation action is started with the invocation of the performAction() method, which implements the one defined in the org.openide.util.actions.CallableSystemAction interface. from  takes care of

  * retrieving the XHTML document on which validation has been requested
  * creating new instance of the ValidatorEngine class and starting the validation algorithm
  * start output handling

ValidatorAction also overrides the method

```
protected boolean asynchronous() {
    return true;
}
```

which allows the IDE performing more than one validation task at the time by starting each one in a dedicate Thread.

The validation process is started by the user by clicking on a button in the IDE main toolbar. This button's position is defined in the layer.xml file as follows

```
<folder name="Toolbars">
    <folder name="Build">
       <file name="it-pronetics-madstore-hatom-validator-action-ValidatorAction.shadow">
           <attr name="originalFile" stringvalue="Actions/BpelNodes/it-pronetics-madstore-hatom-validator-action-ValidatorAction.instance"/>
           <attr name="position" intvalue="500"/>
      </file>
   </folder>
</folder>
```

this piece of XML code configures places the button immediately after the "Build" toolbar and binds it to the ValidatorAction class.

## Validation engine ##

All class in the it.pronetics.madstore.hatom.validator.engine are part of the validation engine. The structure of the engine it's modeled as a Chain-of-responsibility in a tree form, where the engine access point, the ValidationEngine class, delegates the execution of the main validation task to the HfeedAnalyzer, which also delegates smaller parts of the task to other classes.

All the classes involved in the validation algorithm use the suffix "Analyzer" and are implementation of the Analyzer interface. Analyzer defines just one method, analyze(), while most of the common methods are accessible to the implementing classes via the abstract class BaseAnalyzer.

Current implementation of validator engine makes use of org.w3c.dom APIs.

Validation criteria for hAtom microformat are listed and explained here.

## Error report ##

Validation errors are stored as Report objects  that are organized as a ` List<Report> `  in the ValidatorEngine. A new ValidatorEngine is created for every process, whose reference is stored inside a map of the singleton object ValidatorCache. ValidationEngines are mapped in the cache using the document names as keys.

Every validation process (therefore every ValidationEngine) is executed asynchronously and is associated with a dedicated output tab. The new tab is created in the ValidationAction class, using method openOutput

```
private OutputWriter openOutput(String documentName) {

    InputOutput io = IOProvider.getDefault().getIO(TAB_NAME, true);
    io.select();

    OutputWriter writer = io.getOut();

    //...
}
```
The true value in the getIO method tells the Netbeans infrastructure to create a new InputOutput instance for each invocation.

The select() method makes the newly created output panel visibile.private OutputWriter openOutput(String documentName) {

    InputOutput io = IOProvider.getDefault().getIO(TAB_NAME, true);
    io.select();

    OutputWriter writer = io.getOut();

    //...
}
}}}```