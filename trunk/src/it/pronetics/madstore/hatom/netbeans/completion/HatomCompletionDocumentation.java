/**
 * Copyright 2008 - 2009 Pro-Netics S.P.A.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */


package it.pronetics.madstore.hatom.netbeans.completion;

import java.net.URL;
import javax.swing.Action;
import org.netbeans.spi.editor.completion.CompletionDocumentation;

/**
 * Handler class for code completion documentation panel and infrastructure.<br>
 * 
 * @author Andrea Castello
 * @version 1.0
 */
public class HatomCompletionDocumentation implements CompletionDocumentation {

    // The completion item associated with this object documentations.<br>
    private HatomCompletionItem completionItem;
    
    /**
     * Creates a new instance of HatomCompletionDocumentation associated to the
     * given completion item.<br>
     * 
     * @param item the associated completion item
     */
    public HatomCompletionDocumentation(HatomCompletionItem item){
        completionItem = item;
    }
    
    /**
     * Returns the text of the documentation for the current completion item.<br>
     *  
     * @return the docs text
     */
    public String getText() {
        
        return DocumentationCache.getCache().getElementDocumentation(completionItem.getText());
        
    }

    // ------------------ The following methods are not used -------------------
    
    public URL getURL() {
        return null;
    }

    public CompletionDocumentation resolveLink(String arg0) {
        return null;
    }

    public Action getGotoSourceAction() {
        return null;
    }

}
