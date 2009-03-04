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

import java.io.IOException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Base class for analyzing nodes that are children of an hentry node.<br>
 * 
 * @author Andrea Castello
 * @version 1.5
 */
public class HentryChildAnalyzer extends BaseAnalyzer {

	public final static String[] HENTRY_CHILDREN = HATOM_HENTRY_ATTRIBUTES.keySet().toArray( new String[HATOM_HENTRY_ATTRIBUTES.size()]);
	
    // attribute name of the children, usually class or rel
    protected String attributeName;
    
    // attribute value of the children, it is one of the hAtom keywords
    protected String attributeValue;
    
    public HentryChildAnalyzer() {
        
    }

    public HentryChildAnalyzer(Node node, String attrName, String attrValue) {

        this();

        setAttributeName(attrName);
        setAttributeValue(attrValue);
        setNode(node);
    }

    /**
     * Returns the attribute name of a node, usually class or rel.
     * Used by subclasses that perform spcific validation on simple nodes.
     * @return the attribute name
     */
    public String getAttributeName() {
        return attributeName;
    }

    /**
     * Setss the attribute name of a node, usually class or rel.
     * Used by subclasses that perform spcific validation on simple nodes.
     * @param the attribute name
     */
    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    /**
     * Returns the attribute value of a node.
     * Used by subclasses that perform spcific validation on simple nodes.
     * @return the attribute value
     */
    public String getAttributeValue() {
        return attributeValue;
    }

    /**
     * Setss the attribute value of a node.
     * Used by subclasses that perform spcific validation on simple nodes.
     * @return the attribute value
     */
    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }
    
    /**
     * Performs validation of simple hentry child nodes, which means simple html tags whose attribute matches
     * a given atrribute name and a given attribute value.<br>
     * 
     */
    public void analyze() throws IOException {

        if (!XMLUtils.nodeAttributeMatches(node, attributeName, attributeValue)) {

            searchInvalidAttributes();

        } else { // Is a valid hAtom node, check if there are nested nodes
            for (int i = 0; i < HENTRY_CHILDREN.length; i++) {
                checkNestedNode(node, getAttributeName(), HENTRY_CHILDREN[i]);
            }
        }
    }

    /**
     * Searches for hAtom keywords that should NOT be in this node.<br>
     * <br>Note:</b>This method MUST be inovked after it's already known that the node has not a valid hAtom
     * keyword.<br>
     * If one or more of them are found, an error report is added to the validation report list.<br>
     */
    protected void searchInvalidAttributes() {

        Node n = getNode();

        if (n != null) {

            NamedNodeMap attrMap = n.getAttributes();
            Node attrNode;
            Report report;

            if (attrMap != null) {

                for (int i = 0; i < attrMap.getLength(); i++) {

                    attrNode = attrMap.item(i);
                    String attrName = attrNode.getNodeName();
                    String attrValue = attrNode.getNodeValue();

                    if (XMLUtils.attributeValueMatches(attrValue, getAttributeValue())) {

                        report = new Report("hAtom keyword " + getAttributeValue() + " cannot be used in attribute " + attrName, getNode());
                        engine = ValidatorCache.getInstance().getEngine(getDocumentName());
                        engine.addReport(report);

                    }
                }
            }
        }
    }
}
