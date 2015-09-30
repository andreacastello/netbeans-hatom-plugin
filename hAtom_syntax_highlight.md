The layer.xml file defines the interaction between the plugin and Netbeans API for what concerns the syntax highlight implementation and configuration  with the following lines

```
<attr name="SystemFileSystem.localizingBundle" stringvalue="it.pronetics.madstore.hatom.Bundle"/>
 <folder name="NetBeans">
     <folder name="Defaults">
        <file name="coloring.xml" url="syntax/resources/NetBeans-Hatom-fontsColors.xml">
            <attr name="SystemFileSystem.localizingBundle" stringvalue="it.pronetics.madstore.hatom.Bundle"/>
        </file>
     </folder>
 </folder>
 <folder name="CityLights">
     <folder name="Defaults">
         <file name="coloring.xml" url="syntax/resources/CityLights-Properties-fontsColors.xml">
             <attr name="SystemFileSystem.localizingBundle" stringvalue="it.pronetics.madstore.hatom.Bundle"/>
         </file>
     </folder>
</folder>
<file name="language.instance">
     <attr name="instanceCreate" methodvalue="it.pronetics.madstore.hatom.syntax.HatomTokenId.language"/>
     <attr name="instanceOf" stringvalue="org.netbeans.api.lexer.Language"/>
</file>
<file name="NbEditorKit.instance">
    <attr name="instanceCreate" methodvalue="it.pronetics.madstore.hatom.syntax.HatomEditorKit.create"/>
    <attr name="instanceClass" stringvalue="it.pronetics.madstore.hatom.syntax.HatomEditorKit"/>
</file>
```

The ` <folder> ` nodes named _"Netbeans"_ and _"CityLight"_ define **fonts and colors configuration values for two Netbeans highlight profiles**. The ` <file> ` nodes that are children of ` <folder> ` define the files where these values are stored. Hatom plugin stores all these configuration files under the [...]**syntax.resources** package.

The ` <file> ` nodes name "language.instance" and "NbEditorKit.instance" have a different purpose. Language.instance values defines what API and classes will be used for our custom syntax highlight feature. The **org.netbeans.api.lexer.Language** class is a central point of the Lexer API, a new infrastructure for syntax highlight introduced in version 5 of Netbeans that, from version 6.1 on, is the platform's default syntax API.

The plugin's Language class implementation is defined as an inner class inside the HatomTokenId class and is responsable for creation of the HatomLexer and the HatomTokenId objects, which are used by the IDE's infrastructure.

NbEditorKit.instance values define what implementation of the NbEditorKit base class must be used. The **HatomEditorKit** class relies completely on the basic NbEditorKit implementation; it only specifies the content-type text/html as default return value of method

```
getContentType()
```

Netbeans syntax highlight algorithm is based on the concept of tokens, pieces of text that comply with a well know pattern.

HatomTokenId is an enum that specifies the custom tokens used in the highlight of hAtom keywords. There are just two tokens: KEYWORD and TEXT. A piece of XHTML is a KEWORD token when it matches exactly with a hAtom keyword, while it is a TEXT token in all other cases. All the other tokens are inherited from the HTML syntax highlight, using the embedding method of  our Language implementation.

```
if (token.id().equals(HatomTokenId.TEXT)){
    return LanguageEmbedding.create(HTMLTokenId.language(), 0, 0, true);
}
```

The embedding method returns a Language which embeds the TEXT token into the HTMLTokenId enum of Netbeans' native HTML syntax highlight

The HatomLexer class provides the algorithm for hAtom Token recognition, which is based on a "character by character scanning" approach defined by Netbeans' Lexer interface.

## Option window: hAtom syntax example ##

It is possible to show a preview of hAtom syntax highlight in the IDE's Options Window. The following snippet of the layer.xml file tells the Netbeans platform where to find the hAtom preview example. Note that text/html content-type is always mapped using the ` <folder> ` tags.

```
<folder name="OptionsDialog">
    <folder name="PreviewExamples">
        <folder name="text">
            <file name="html" url="syntax/resources/HatomExample"/>
        </folder>
    </folder>
</folder>
```