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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Analyzer for hAtom "author" nodes.<br>
 * 
 * Actual implementation checks the following requisites:<br>
 * <li>author is a class attribute 
 * <li>author is a vcard (with fn child attribute).
 * <br>
 * 
 * @author Andrea Castello
 * @version 1.1
 */
public class AuthorVcardAnalyzer extends HentryChildAnalyzer {

    // Constants for recurring vcard author attributes.
    private static final String ATTR_VCARD = "vcard";
    private static final String ATTR_FN = "fn";

    /**
     * Creates a new instance of AuthorVcardAnalyzer.<br>
     */
    public AuthorVcardAnalyzer() {
    }

    /**
     * Creates a new instance of AuthorVcardAnalyzer.<br>
     * 
     * @param node Node that must be analyzed
     * @param attrName name of the attribute of the hAtom element that should be found in the node
     * @param attrValue value of the hAtom element that should be found in the node 
     */
    public AuthorVcardAnalyzer(Node node, String attrName, String attrValue) {
        super(node, attrName, attrValue);
    }

    /**
     * Performs analysis of a possible author hAtom node.<br>
     */
    @Override
    public void analyze() throws IOException {
        // Composed attribute value
        String vcardAuthorAttr = ATTR_VCARD + " " + getAttributeValue();

        if (!XMLUtils.nodeAttributeMatches(getNode(), getAttributeName(), vcardAuthorAttr)) {

            searchInvalidAttributes();

        } else { // Is a valid hAtom node, check if there are nested nodes

            String[] keys = new String[HATOM_HENTRY_ATTRIBUTES.size()];  
            HATOM_HENTRY_ATTRIBUTES.keySet().toArray(keys);
            
            for (int i = 0; i < keys.length; i++) {
                checkNestedNode(getNode(), getAttributeName(), keys[i]);
            }

            // node is a vcard author, but further validation has to be done. We must check that node has
            // a child node with class attribute "fn"
            checkFn();
        }

    }

    /**
     * Checks that "author vcard" node has a child node which contains an "fn" class attribute
     */
    private void checkFn() {

        NodeList nl = getNode().getChildNodes();
        if (nl != null) {
            Node child;
            for (int i = 0; i < nl.getLength(); i++) {
                child = nl.item(i);
                if (isFn(child)) {
                    return;
                }
            }
            // If we arrive here, no fn attribute has been found
            createNoFnReport();
        } else {
            createNoFnReport();
        }
    }

    /**
     * Convenience method for report creation in case of missing fn class attribute.<br>
     */
    private void createNoFnReport() {
        String message = "Node" + getNode().getNodeName() + "must be a valid hCard and must contain a fn property";
        Report report = new Report(message, getNode());
        engine = ValidatorCache.getInstance().getEngine(getDocumentName());
        engine.addReport(report);
    }

    /**
     * Check is node <code>child</code> is a regular fn node.
     * @param child
     * @return <code>true</code> if <code>child</code> is fn attribute, <code>false</code> otherwise
     */
    private boolean isFn(Node child) {
        return XMLUtils.nodeAttributeMatches(child, ValidatorEngine.ATTR_CLASS, ATTR_FN);
    }
}