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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.netbeans.api.editor.completion.Completion;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;
import org.netbeans.spi.editor.completion.support.CompletionUtilities;
import org.openide.util.Exceptions;

/**
 * The list that appears in the popup completion object.<br>
 * 
 * @author Andrea Castello
 * @version 1.0
 */
public class HatomCompletionItem implements CompletionItem {
    
    /**
     * Completion text (ie: value "hfeed").
     */
    private String text;
    /**
     * Selected field color.
     */
    private static Color fieldColor = Color.decode("0x0000B2");
    /**
     * Current caret postion inside the text/html document. 
     */
    private int caretOffset;
    /**
     * Index of the filter's starting character. This means that it is the index at which the completed text
     * will be inserted.
     */
    private int startOffset;
    
    /**
     * Creates a new instance with all the data needed to fill the object and use it.<br>
     * @param text text
     * @param startOffset Index of the filter's starting character.
     * @param caretOffset Current caret postion inside the used document.
     */
    public HatomCompletionItem(String text, int startOffset, int caretOffset){
        this.text = text;
        this.caretOffset = caretOffset;
        this.startOffset = startOffset;
    }
    
    /**
     * Returns the color of the selected item in the completion popup<br>
     * @return Selected color
     */
    public static Color getFieldColor() {
        return fieldColor;
    }

    /**
     * Sets the color for the selected item in the completion popup<br>
     * @param aFieldColor selected item color
     */
    public static void setFieldColor(Color aFieldColor) {
        fieldColor = aFieldColor;
    }
    
    /**
     * Defines the action that must be executed when the user press enter (or on a mouse clic) on the
     * request completion item.<br>
     * 
     * The text that the user has already typed is removed from text and replaced by the completed hAtom keyword.<br>
     * 
     * @param txtComponent Text component that holds the document where completion task must be performed.<br>
     */
    public void defaultAction(JTextComponent textComponent) {
        
        StyledDocument styledDocument = (StyledDocument) textComponent.getDocument();
        try{
            int len = caretOffset - startOffset -1;
            if (len>0){
                // Removal of the text already typed by the user.
                styledDocument.remove(startOffset + 1, len);
            }
            // Insertion of thge completed hAtom keyword.
            styledDocument.insertString(caretOffset - len, text, null);
        }
        catch(BadLocationException blex){
            Exceptions.attachMessage(blex, "Unable to perform completion on the document");
            blex.printStackTrace();
        }
        //Closes completion popup.
        Completion.get().hideAll();

    }

    /** Non implementato */
    public void processKeyEvent(KeyEvent keyEvent) {}

    public int getPreferredWidth(Graphics g, Font font) {
        return CompletionUtilities.getPreferredWidth(text, null, g, font);
    }

    /**
     * Disegna la porzione di HTML corrispondente al popup dell'autocompletamento.<br>
     * @param g The current graphic context.
     * @param defaultFont Default font
     * @param defaultColor Element's default color
     * @param backgroundColor background color
     * @param width Width of the drawable area
     * @param height Height of the drawable area
     * @param selected Whether an item is selected in the list or not.
     */
    public void render(Graphics g, Font defaultFont, Color defaultColor, Color backgroundColor, int width, int height, boolean selected) {
            CompletionUtilities.renderHtml(null, text, null, g, defaultFont,
            (selected ? Color.white : fieldColor), width, height, selected);
    }

    /**
     * Creates and returns an asynchronous completion task with an included documentation panel.<br>
     * 
     * @return an asynchronous completion task.
     */
    public CompletionTask createDocumentationTask() {
        
        return new AsyncCompletionTask( new AsyncCompletionQuery() {
            
            protected void query(CompletionResultSet completionResultSet, Document document, int i) {
                completionResultSet.setDocumentation(new HatomCompletionDocumentation(HatomCompletionItem.this));
                completionResultSet.finish();
            }
        } );

    }

    /**
     * Do not use. Not implemented
     * @return <b>Always</b> null.
     */
    public CompletionTask createToolTipTask() {
        return null;
    }

    /** 
     * From the Netbeans API:
     * "When enabled for the item the instant substitution should process the item in the same way like when the item is displayed and Enter key
     * gets pressed by the user.<br>
     * Instant substitution is invoked when there would be just a single item displayed in the completion popup window.<br>
     * The implementation can invoke the defaultAction(JTextComponent) if necessary.<br>
     * This method gets invoked from AWT thread". 
     * <br><br>
     * This version always returns <code>false</code>.<br>
     * 
     * @param component Visual component on which the completion task must be applied.<br>
     * @return <code>true</code> if the instant substitution was successfully done, <code>false</code> means that the instant substitution should not be done 
     * for this item and the completion item should normally be displayed.<br>
     */
    public boolean instantSubstitution(JTextComponent component) {
        return false;
    }

    /**
     * Sort priority.<br>
     * From Netbeans API javadoc: "A lower value means a lower index of the item in the completion result list".<br>
     * 
     * @return constant value zero.
     */
    public int getSortPriority() {
        return 0;
    }

    public CharSequence getSortText() {
        return text;
    }

    public CharSequence getInsertPrefix() {
        return text;
    }

    /**
     *  
     * @return completion text.<br>
     */
    public String getText() {
        return text;
    }

    /**
     * Sets completion text.<br>
     */ 
    public void setText(String text) {
        this.text = text;
    }

    /**
     * 
     * @return Current caret offset<br>.
     */
    public int getCaretOffset() {
        return caretOffset;
    }

    /**
     * Sets the current caret offset<br>.
     * @param caretOffset carte offset
     */
    public void setCaretOffset(int caretOffset) {
        this.caretOffset = caretOffset;
    }

}