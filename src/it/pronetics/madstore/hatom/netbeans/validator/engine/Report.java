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

package it.pronetics.madstore.hatom.netbeans.validator.engine;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Contains info about a validation error, including the node where it is occurred,
 * an error message and complete node path.<br>
 * 
 * @author Andrea Castello
 * @version 1.2
 */
public class Report {
    
    /**
     * Creates a new empty instance fo Report.<br>
     */ 
    public Report(){}
    
    /**
     * Creates a report for the given node and with the given message.<br>
     * @param message
     * @param node
     */
    public Report(String message, Node node){
        this.message = message;
        this.node = node;
    }

    // Node that has been analyzed
    private Node node;
    
    // Report Message
    private String message = "";
    
    // Path from root to node
    private String nodePath = "";

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
        extractPath();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNodePath() {
        return nodePath;
    }
    
    private void setNodePath(String path) {
        this.nodePath = path;
    }

    private void extractPath() {
        
        List<String> pathNames = new ArrayList<String>();
        
        Node temp;
        
        if (getNode()!=null){
            
            pathNames.add(getNode().getNodeName());
            
            temp = getNode();
            
            while( (temp = temp.getParentNode() )!=null){
                pathNames.add(temp.getNodeName());
            }
            
            StringBuffer buf = new StringBuffer();
            
            for(int i = pathNames.size() -1; i>=0; i--){
                buf.append(pathNames.get(i));
                if (i!=0){
                    buf.append(" > ");
                }
            }
            
            setNodePath(buf.toString());
        }
    }
    
    private String getStringNode(){
        StringBuffer buf = new StringBuffer();
        if (node!=null){
            buf.append("<");
            buf.append(node.getNodeName());
            buf.append(" ");
            NamedNodeMap attrMap = node.getAttributes();
            if (attrMap!=null ){
                
                for (int i = 0; i < attrMap.getLength(); i++) {
                    Attr attr = (Attr) attrMap.item(i);
                    buf.append(attr.getName());
                    buf.append("=\"");
                    buf.append(attr.getValue());
                    buf.append("\" ");
                }
                
            }
            
            buf.append(">");
        }
        
        return buf.toString();
    }
    
    
    public String asString(){
        
        StringBuffer buf = new StringBuffer("--------------------------------------\n");
        buf.append(getMessage()+"\n");
        if (getNode()!=null){
            buf.append("Node name: "+getNode().getNodeName()+"\n");
            buf.append("Node path: "+getNodePath()+"\n");
        }
        
        buf.append(getStringNode()+" \n");
        
        buf.append("--------------------------------------\n\n"); 
        
        return buf.toString();
    }
    
    /**
     * Add a simple error report to the validator engine for the given document.<br>
     * The report will only be a user readable error message.<br>
     * @param documentName Name of the document (used to retrieve the proper validation enegine).
     * @param message Error message
     */
    public static void addErrorReport(String documentName, String message){
        Report report = new Report();
        report.setMessage(message);
        ValidatorEngine engine = ValidatorCache.getInstance().getEngine(documentName);
        engine.addReport(report);
    }
}