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

import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.StyledDocument;

/**
 * Defines a search filter for the hAtom keywords that must be set in the code completion popup<br>
 * 
 * The criteria we use to define the filter is this: "The filter starts from the nearest character that
 * preceedes the current caret position and follows one of the characters defined in the
 * <code>FILTER_START_CHARS</code> array.<br>
 * <br><br>
 * We assume that filtering operation are executed one by one, by a single user, therefore the class is 
 * defined as <i>singleton</i>.<br>
 * 
 * @author Andrea Castello
 * @version 1.0
 */
public class Filter {

    /** Useful constant for empty strings. */
    public final static String EMPRTY_STRING = "";
    
    /** Set of characters that can define the filter's "left border".*/
    private static final char[] FILTER_START_CHARS = { '"', ' ', '<', '>', '=' };
    
    /** The textual body of the filter.<br> */
    private String filterText = EMPRTY_STRING;
    
    /** Singleton instance of the filter */
    private static Filter filter = null;
   
    /** Index of the filter's starting character.<br> */
    private int filterOffset = -1;
    
    /** Private constructor (<i>singleton</i> pattern) for the filter object.<br> */
    private Filter(){}
    
    /**
     * Access method for the filter' singleton instance.<br>
     * @return the filter
     */
    public static synchronized Filter getFilter(){
        if (filter == null){
            filter = new Filter();
        }
        
        return filter;
    }
    
    /** 
     * Returns the text of the filter.<br><br>
     * @param doc Document on which the completion task must be applied.<br>
     * @param offset current caret position.<br>
     * @return filter's text.<br>
     */
    public String getText(StyledDocument doc, int offset){
        try{
            this.filterOffset = getInitialOffset(doc, offset);
            setText( doc.getText(filterOffset +1, offset - filterOffset -1) );
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return filterText;
    }
    
    /**
     * sets the filter's text.<br>
     * @param text the filter's text.<br>
     */
    public void setText(String text){
        this.filterText = text;
    }
    
    /**
     * Returns the starting index character of the filter.<br>
     * @return filter's initial char index
     */
    public int getFilterOffset(){
        return filterOffset;
    }
    
    /** 
     * Calculaters the filter's <b>left border</b>, according to the given definition (see class description).
     * The filter's real starting character is <code>getInitialOffset() + 1</code>.<br><br>
     * 
     * @param doc Document on which the completion task must be applied.<br>
     * @param offset current cursor position.<br>
     * @return the index of the filter's left border.<br>
     * @throws javax.swing.text.BadLocationException Invalid caret position in the document.<br>
     */
    private static int getInitialOffset(StyledDocument document, int offset) throws BadLocationException {
        int initialOffset = -1;
        // We get the line where is the current caret position
        Element lineElement = document.getParagraphElement(offset);
        // line starts here
        int start = lineElement.getStartOffset();
        int refPoint = offset - 1;
        
        char c;
        
        while (offset - 1 > start){
            try {
                c = document.getText(refPoint, offset - refPoint).charAt(0);
                if (isFilterStartChar(c)) {
                    initialOffset = refPoint;
                    break;
                }
            } catch (BadLocationException ex) {
                throw (BadLocationException) new BadLocationException(
                        "calling getText(" + start + ", " + (start + 1) +
                        ") on a document of length: " + document.getLength(), start).initCause(ex);
            }
            refPoint--;
        }
        
        return initialOffset;
    }
    
    /**
     * Verifies whether the parameter character is found in the array of possible "left borders" or not.<br><br>
     * @param c the character to be found in the array
     * @return <code>true</code> id the character is found in the array, <code>false</code> otherwise.
     */
    private static boolean isFilterStartChar(char c){
        
        for(int i=0; i < FILTER_START_CHARS.length; i++){
            if (c == FILTER_START_CHARS[i]){
                return true;
            }
        }
        
        return false;
    }
    
}