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

package it.pronetics.madstore.hatom.netbeans.validator.action;

import it.pronetics.madstore.hatom.netbeans.validator.engine.Report;
import it.pronetics.madstore.hatom.netbeans.validator.engine.ValidatorCache;
import it.pronetics.madstore.hatom.netbeans.validator.engine.ValidatorEngine;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.List;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import org.openide.cookies.EditorCookie;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.openide.windows.TopComponent;
import org.openide.nodes.Node;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;

/**
 * Performs the validation action for currently selected (and opened) document.<br>
 * 
 * @author Andrea Castello
 * @version 1.4
 */
public final class ValidatorAction extends CallableSystemAction implements PropertyChangeListener {

    // Output tab name 
    private static final String TAB_NAME = "hAtom validation result";
  
    /**
     * Creates a new instance of ValidatorAction.<br>
     * Default setting for the action button in the IDE toolbar is disabled.<br>
     * It also register itself as a PropertyChangeListener for the TopComponent registry.<br> 
     */
    public ValidatorAction(){
        setEnabled(false);
        TopComponent.getRegistry().addPropertyChangeListener(this);
    }
    
    /**
     * Performs the document's validation task.<br> 
     */
    public void performAction() {
        // Sets the validation process start time. 
        TimeMeasurer timeMeasurer = new TimeMeasurer();
        
        // Name of the document that has to be validated
        String documentName = getOpenDocumentName();
        
        // The currently open and selected document
        StyledDocument document = getStyledDocument();
        
        // "Root" object for document validation. 
        ValidatorEngine engine;
       
         // Object that writes into the output tab
        OutputWriter writer = openOutput(documentName);
  
        if (document!=null){
            try {
                engine = ValidatorCache.getInstance().createEngine(documentName);         
                engine.validate(document);
            } catch (BadLocationException ex) {       
                Report.addErrorReport(documentName, "Unable to read document \n "+ ex.getClass().getName()+": "+ex.getMessage());
                
            } catch (IOException ex) {
                Report.addErrorReport(documentName, "Unable to parse document \n "+ ex.getClass().getName()+": "+ex.getMessage());
            }
        }
        else {
             Report.addErrorReport(documentName, "Unable to load editor document \n ");
        }
       
        // Prints the report then closes the output object 
        printReports(documentName, writer, timeMeasurer);
        
        // removes the ValidatorEngine object from the cache of validation engines
        ValidatorCache.getInstance().remove(documentName);
        
    }

    /**
     * Find the currently used document in the list of the IDE's open nodes.<br>
     * 
     * @param arr all the nodes open in Netbeans editor.
     * @return the found document, or null
     */
    private StyledDocument findUsedDocument(Node[] activatedNodes) {
        
        StyledDocument styledDoc = null;
        
        for (int j = 0; j < activatedNodes.length; j++) {

            EditorCookie ec = activatedNodes[j].getCookie(EditorCookie.class);
            if (ec != null) {
                // try to get the document which is currently used by the IDE user.
                styledDoc = ec.getDocument();
                
                if (styledDoc != null) {
                    //documentName = arr[j].getName();
                    return styledDoc;
                }
            }
        } //end for
        
        // if no document has been found, return null
        return null;
    }
    
    /**
     * Gets the currently open and selected document in the IDE.<br>
     * @return document associated with the selected and opened node in the IDE, or null if nothing has been found.
     */
    private StyledDocument getStyledDocument() {
        // First, get the list of the open nodes in the IDE.
        TopComponent activeComponent = TopComponent.getRegistry().getActivated();
        
        Node[] activatedNodes = activeComponent.getActivatedNodes();
            
            if (activatedNodes != null) {
                
                return findUsedDocument(activatedNodes);
                
            }
        // end for

        // if no node is opened, return null
        return null;
    }

    
    public String getName() {
        return NbBundle.getMessage(ValidatorAction.class, "CTL_ValidatorAction");
    }

    /**
     * Gets the full name of the icon that will be used in the IDE menu bar or
     * dropdown menu to invoke the validation action 
     * @return icon full path 
     */
    @Override
    protected String iconResource() {
        return "it/pronetics/madstore/hatom/netbeans/validator/action/hatomValidate.png";
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    /**
     * Tells whether this action is asynchronous or not.<br> 
     * @return <code>true</code> if the action has to be executed in its own new thread, 
     * <code>false</code> otherwise. This implementation always returns <code>true</code>. 
     */
    @Override
    protected boolean asynchronous() {
        return true;
    }

    /**
     * Rettrieves the IO handler and the out writer objects, the opens the output tab.<br>
     * <b>Each new invocation of a validation process opens a new tab</b><br> 
     */
    private OutputWriter openOutput(String documentName) {
        
        // "hAtom" tab is created in output window for writing the list of tags
        InputOutput io = IOProvider.getDefault().getIO(TAB_NAME, true);
        io.select();

        OutputWriter writer = io.getOut();

        try {
            writer.reset(); //clean the output tab

            writer.println("Validating file "+ documentName +". Please wait..." + "\n");
        }
        catch (IOException ex) { // Usually occurs when is not possible to clean output tab
            writer.println("I/O Error: " + ex.getMessage());
            ex.printStackTrace(); // Prints exception in Netbeans standard output tab
        }
     
        return writer;
    }
    
    /**
     * Prints validation reports in a dedicated tab of the IDE.<br>
     * 
     * @param documentName name of the document that has to be validated
     * @param writer dedicated IO writer for validation output tab
     * @param timeMeasurer 
     */ 
    private void printReports(String documentName, OutputWriter writer, TimeMeasurer timeMeasurer) {

        ValidatorEngine engine = ValidatorCache.getInstance().getEngine(documentName);

        List<Report> reports = engine.getReports();
        if (reports != null && reports.size() > 0) {
            
            writer.println("Document contains " + reports.size() +" errors \n");

            for (Report report : reports) {
                writer.println(report.asString());
            }

        } else {
            writer.println("Document contains valid hAtom microformat \n");
        }

        writer.print(" Validation completed in: " + timeMeasurer.getElapsedTime() + " milliseconds");
         
        writer.flush();
        writer.close();
    }
    
    /**
     * On every property change event occurring to the activated component, it enables or disables
     * the action button according to the document's extension.<br>
     * 
     * @param evt The property change event
     */
    public void propertyChange(PropertyChangeEvent evt) {
        String fileName = TopComponent.getRegistry().getActivated().getDisplayName();
        try {
            setEnabled(fileName != null && isHtmlFile(fileName));
        } catch (Exception ex) {
            // Happens in case of file without extension or TopComponent object that are NOT files.
            setEnabled(false); 
        }   
    }
        
    /**
     * Checks if the fileName string is the regular name of an (x)html file.<br>
     * This method uses a simple extension check.<br>
     * 
     * @param fileName Name of the activated tab.
     * @return Returns <code>true</code> if the file name has a HTML or XHTML extension, <false>otherwise</false>. 
     * @throws java.lang.Exception in case fileName is <code>null</code> or has no extension token.
     */
    private boolean isHtmlFile(String fileName) throws Exception {
        
        String nameAndExt[] = fileName.split("\\.");
        return (nameAndExt[1].equalsIgnoreCase("html") || nameAndExt[1].equalsIgnoreCase("xhtml"));
        
    }
    
    /**
     * Returns the name of the currently open and selected document in Netbeans source editor
     * window, if present, <code>null</code> otherwise.<br> <br>
     * 
     * @return document' name
     */
    private String getOpenDocumentName() {
        
        TopComponent activeComponent = TopComponent.getRegistry().getActivated();
        return activeComponent.getName();
        
    }
}