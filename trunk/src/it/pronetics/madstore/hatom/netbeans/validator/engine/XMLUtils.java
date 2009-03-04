/*
 * ---------------------------------------------------------------------------------
 * XMLUtils.java - Storia delle modifiche
 * ---------------------------------------------------------------------------------
 * 16/09/2008 - 1.0: Prima versione.
 * 03/10/2008 - 1.1: Removed newDocumentFromNode(), its logic was inherently wrong. 
 *                   Added nodeAttributeEquals.
 */
package it.pronetics.madstore.hatom.netbeans.validator.engine;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.openide.util.Exceptions;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.SAXException;

/**
 * Provides utility methods for working with XML files using DOM classes.<br>
 *
 * @author  Andrea Castello.
 * @version 1.1
 */
public class XMLUtils {
    
    /** 
     * Since XMLUtils provides just static utility methods, no class instances
     * are allowed.<br>
     */
    private XMLUtils() {}
    
    /**
     * Metodo per creare un Document rappresentante un file xml.
     * 
     * @param base
     *            String nome che si desidera dare alla root
     * @return Document documento xml
     * @throws IOException
     *             viene lanciata se non � stata possibile la creazione un
     *             document
     */
    public static Document newDocument(String base) throws IOException {
        Document document = null;
        DocumentBuilderFactory builderFact = DocumentBuilderFactory
                .newInstance();
        DocumentBuilder docBuilder;
        try {
            docBuilder = builderFact.newDocumentBuilder();
            document = docBuilder.newDocument();
            Element root = document.createElement(base);
            document.appendChild(root);
        } catch (ParserConfigurationException e) {
            Exceptions.attachLocalizedMessage(e, "XmlUtil.createDocument()" + e.getMessage());
        }
        return document;
    }
     
    /**
     * Creates a Document from the given InputStream.
     * 
     * @param is Stream of the XML file that have to be loaded into the document
     * @return Document a DOM document.
     * @throws IOException in case a file reading problem occurs.
     */
    public static Document getDocument(InputStream is) throws IOException {
        
        BufferedInputStream bis = new BufferedInputStream(is);
         
        Document document = null;
        DocumentBuilderFactory builderFact = DocumentBuilderFactory
                .newInstance();
        builderFact.setValidating(false);
        builderFact.setIgnoringElementContentWhitespace(true);
        DocumentBuilder docBuilder;
        try {
            docBuilder = builderFact.newDocumentBuilder();
            document = docBuilder.parse(bis);
        } catch (ParserConfigurationException e) {
            Exceptions.attachLocalizedMessage(e, "XmlUtils.getDocument()" + e.getMessage());
            throw new IOException(e.getMessage());
        } catch (SAXException se) {
            Exceptions.attachLocalizedMessage(se, "XmlUtils.getDocument()" + se.getMessage());
            throw new IOException(se.getMessage());
        }
        return document;
    }    

    /**
     * Gets a node's text value given its parent node and the name of the wanted child node.<br>
     * 
     * @param fatherNode the parent node
     * @param nodeName name of the wanted child node
     * @return String text node value
     */
    public static String getNodeValue(Node fatherNode, String nodeName) {
        String value = null;
        if (((Element) fatherNode).getElementsByTagName(nodeName) != null) {
            Node node = ((Element) fatherNode).getElementsByTagName(nodeName)
                    .item(0);
            value = node.getTextContent(); 
            if (value!=null){
                value = value.trim();
            }
        }        
        return value;
    }
    
    
    /**
     * Gets a node list from document, given the father node and the children node name.
     * 
     * @param fatherNode parent node that containes the node list
     * @param nodeName name of the nodes in the resulting node list
     * @return NodeList containes node of the given name
     */
    public static NodeList getNodeList(Node fatherNode, String nodeName) {
        NodeList list = null;
        if (((Element) fatherNode).getElementsByTagName(nodeName) != null) {
           list =  ((Element) fatherNode).getElementsByTagName(nodeName);
        }
        return list;
    }    

    /**
     * Gets the node value
     * 
     * @param node Node 
     * @return String node value
     */
    public static String getNodeValue(Node node) {
        String value = null;
        if (node.getFirstChild() != null) {
            value = node.getFirstChild().getNodeValue();
        }
        return value;
    }

    
    /**
     * Gets the unique child node of the given <code>parent</code> element.<br>
     * @param parent the parent node
     * @param childName name of the wanted child node
     * @return the unique child of the parent node 
     */
    public static Element getUniqueChild(Element parent, String childName){
        Element child = null;
        if (parent!=null && childName!=null){
            NodeList list = parent.getElementsByTagName(childName);
            if(list!=null && list.getLength()>0){
                child = (Element)list.item(0);
            }
        }    
        return child;
    }
    
    /**
     * Gets a list of nodes that matches the given attribute name and attribute value
     * @param attrName the given attribute name
     * @param attrValue the given attribute value
     * @param node the node under which search must be performed
     * @return List of nodes that matches the given attribute properties
     */
    public static List<Node> getNodesByAttributeValue(Node node, String attrName, String attrValue){
        List<Node> list = new ArrayList<Node>();
        
        NodeList nodeList = node.getChildNodes();
        if (nodeList!=null){
            Node child;
            for(int i=0; i<nodeList.getLength(); i++){
                child = nodeList.item(i);
                if (nodeAttributeMatches(child, attrName, attrValue)){
                    list.add(child);
                }
            }
        }
        
        return list;
    }
    
    /**
     * Check whether the given nodes containes an attribute with the given name and value.<br>
     * The "matching" is assumed to be true even if the attribute just starts or ends with the given
     * attribute value.<br>
     * This has been allowed to match multiple keyword attributes such as in 
     * <code>class="hfeed hentry"</code> cases.<br>
     * 
     * @param node node to be analyzed
     * @param attrName the attribute name to be matched
     * @param attrValue the attribute value to be matched
     * @return <code>true</code> if node containes the given attributes, <code>false</code> otherwise (even if node is null)
     */
    public static boolean nodeAttributeMatches(Node node, String attrName, String attrValue){
        
         if (node!=null && node.getNodeType()==Node.ELEMENT_NODE){
            
             String value = ((Element)node).getAttribute(attrName);
             return (value!=null && value.length()>0 && attributeValueMatches(value, attrValue) );
         
         }
         else {
            
             return false;
             
         }
    }
    
    public static boolean attributeValueMatches(String value, String expectedValue){
        
        try { // attribute value is longer than the expected one, it may be a composite one (ie: hfeed hentry)
            if (value.length() > expectedValue.length() ){
                return ( 
                        ( value.startsWith(expectedValue) && value.charAt(expectedValue.length()) == ' ') ||  
                        ( value.endsWith(expectedValue) && 
                                value.charAt(value.length() - expectedValue.length() - 1) == ' ')
                       );
            }
            else {
               return (value.equalsIgnoreCase(expectedValue));
            }
        }
        catch(NullPointerException npe){
            return false;
        }
        
    }
    
    
    /**
     * Check whether the given nodes containes an attribute with the given name and value.<br>
     * The name and value of the attribute MUST be the same of the node ones in order to have true as
     * a result.<br> The check is case insensitive.<br>
     * 
     * @param node node to be analyzed
     * @param attrName the attribute name to be matched
     * @param attrValue the attribute value to be matched
     * @return <code>true</code> if node containes the given attributes, <code>false</code> otherwise (even if node is null)
     */
    public static boolean nodeAttributeEquals(Node node, String attrName, String attrValue){
        
         if (node!=null && node.getNodeType()==Node.ELEMENT_NODE){
            
             String value = ((Element)node).getAttribute(attrName);
             return (value!=null && value.equalsIgnoreCase(attrValue) );
         
         }
         else {
            
             return false;
             
         }
    }
    
    /**
     * Creates a Node iterator from the given Document object, starting from the given node.<br>
     * @param doc document from which iterator will be created
     * @param node node from which iterator we will start
     * @return the resulting iterator
     */
    public static NodeIterator getNodeIterator(Document doc, Node node){
        
        DocumentTraversal traversal = ((DocumentTraversal) doc); 
        
        return (traversal).createNodeIterator(node, NodeFilter.SHOW_ALL, null, true);
    }
    
}