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

import javax.swing.text.JTextComponent;
import org.netbeans.spi.editor.completion.CompletionProvider;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;

/**
 * Handles the task of code completion.<br>
 * It creates the task, in a method createTask() which is invoked by Netbeans platform.<br>
 * 
 * @author Andrea Castello
 * @version 1.0
 */
public class HatomCompletionProvider implements CompletionProvider {

    /**
     * Creates the autocompletion task in an asynchronous mode.<br>
     * 
     * @param queryType query tyoe (see API for <code>Completionprovider</code> interface
     * @param component Visual component on which completion must be executed.
     * @return The completion task.
     */
    public CompletionTask createTask(int queryType, JTextComponent component) {
        // If a different query type from the expected one is passed, we return null.
        if (queryType != CompletionProvider.COMPLETION_QUERY_TYPE){
            return null;
        }
        // Asynchronous task creation.
        return new AsyncCompletionTask(new HatomAsynchCompletionQuery(), component);
    }

    /**
     * Implementation of this method return a fixed value, which is the one corresponding to the simple completion.
     without tooltip or documentation parts.<br><br>
     * 
     * @param component Visual component on which completion must be executed.
     * @param typedText type text by hte user
     * @return query type
     */
    public int getAutoQueryTypes(JTextComponent component, String typedText) {
        return COMPLETION_QUERY_TYPE; 
    }
    
}
