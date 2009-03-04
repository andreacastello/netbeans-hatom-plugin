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

import it.pronetics.madstore.hatom.netbeans.syntax.TagCache;

import java.util.List;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
/**
 * Defines the query that is executed by the code completion task in order to fill the completion popup.<br> 
 * <br>
 * It only implements an abstract method defined in its superclass.<br>
 * 
 * @author Andrea Castello
 * @version 1.0
 */
public class HatomAsynchCompletionQuery extends AsyncCompletionQuery {

    /**
     * Executes the method that set the filtered values that will be used to fill the
     * code completion popup.<br> 
     * 
     * @param completionRS Result set containing data that will be used to fill the completion popup.
     * @param doc Document on which the completion task must be applied.
     * @param caretOffset Index of the current caret position.
     */
    @Override
    protected void query(CompletionResultSet completionRS, Document doc, int caretOffset) {
        // First, we retrieve the filters defined for the hAtom microformat completion
        String strFilter = Filter.EMPRTY_STRING;
        Filter filter = Filter.getFilter();
        
        try {
            StyledDocument styledDoc = (StyledDocument) doc;            
            // Get the filter's text based on actual carte position.
            strFilter = filter.getText(styledDoc, caretOffset);
            
        } catch (Exception ex) {
            ex.printStackTrace();
            // if an error occurs, an empty filter is set, so that the completion popup 
            // will be filled with all the hAtom keywords.
            strFilter = Filter.EMPRTY_STRING;
        }

        // Lista completa dei tag/parole chiave hAtom
        List<String> hatomTags = TagCache.getCache().getTagList();

        // Gets the hAtom keywords that match the given filter value.
        for (String tag : hatomTags) {
            boolean startWithFilter = tag.startsWith(strFilter); 
            if (!tag.equals(Filter.EMPRTY_STRING) &&  startWithFilter) {
                completionRS.addItem(new HatomCompletionItem(tag, filter.getFilterOffset(), caretOffset));
            }
        }

        // This is required by the Netbeans API docs.
        // After finish() is invoked, no further modifications to the result set are allowed.
        completionRS.finish();

    }

}
