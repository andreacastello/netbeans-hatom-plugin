The **layer.xml** file defines the interaction between the plugin and Netbeans API with the following lines

```
<folder name="CompletionProviders">
    <file name="it-pronetics-madstore-hatom-completion-HatomCompletionProvider.instance"/>
</folder>
```

Netbeans infrastructure uses class HatomCompletionProvider implementation of CompletionProvider to perform a custom code completion task defined for content-type text/html, which is configured using the nested tags

```
<folder name="text">
<folder name="html"> ...
```

The **HatomCompletionProvider** method

```
createTask(int queryType, JTextComponent component)
```

creates a task object that can be synchronous or asynchronous. In our case, an **asynchronous** task object is provided by returning an instance of Netbeans' **AsyncCompletionTask** class, whose constructor receives our implementation of AsynchCompletionQuery: **HatomAsynchCompletionQuery**. This class defines the query that is executed by the code completion task in order to fill the completion popup and implements just one method:

```
query(CompletionResultSet completionRS, Document doc, int caretOffset)
```

This method creates and sets the filtered values that will be used to fill the code completion popup. See Netbeans plugin overview to know more about user-perspective meaning of "filtered" completion list. The complete list of hAtom keywords is loaded into a singleton cache class (**TagCache**) from the properties file **tags.properties** under the syntax package.

The real action of code completion is performed by **HatomCompletionItem** class. Again, this class implements Netbeans' interface **CompletionItem** and provides APIs for executing the completion task and rendering its UI. The method

```
defaultAction(JTextComponent textComponent)
```

defines the action that must be executed when the user press enter (or on a mouse click) on the given text component.
The text that the user has already typed after completion popup appers is removed from text and replaced by the proper hAtom keyword.

Method

```
createDocumentationTask()
```

is the point where is created the asynchronous task for hAtom documentation provider, which is show together with the code completion popup. Documentation items are provided by **HatomCompletionDocumentation** class, a singleton class similar to TagCache, and loaded from the **documentation.properties** file.

## More on keyword filtering ##
As we said before, the hAtom keywords shown when a completion popup is activated can be filtered according to criteria defined in the Filter class. The filter text is just a string whose starting point is  the nearest character that preceedes the current caret position and follows one of the characters defined in the FILTER\_START\_CHARS array.The filter starting point is calculated using the

```
getInitialOffset(StyledDocument document, int offset)
```

method.

In this version of the plugin, we assume that filtering operations are executed one by one, by a single user.