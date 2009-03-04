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

import static it.pronetics.madstore.hatom.netbeans.validator.engine.HentryChildAnalyzer.HENTRY_CHILDREN;
import static it.pronetics.madstore.hatom.netbeans.validator.engine.ValidatorEngine.ATTR_CLASS;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openide.util.Exceptions;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;

/**
 * Performs validation subtask for hentry attributes
 * @author Andrea Castello
 * @version 1.3
 */
public class HentryAnalyzer extends BaseAnalyzer {

    // Map that associates hAtom keywords (keys) to the implementation of analyzer classes (values).
    public static final Map<String, String> VALIDATION_CONFIG_CLASSES = new HashMap<String, String>();


    static {
        VALIDATION_CONFIG_CLASSES.put(KEYWORD_ENTRY_TITLE, HENTRY_CHILD_ANALYZER); // required
        VALIDATION_CONFIG_CLASSES.put(KEYWORD_TAG, HENTRY_CHILD_ANALYZER);
        VALIDATION_CONFIG_CLASSES.put(KEYWORD_ENTRY_CONTENT, HENTRY_CHILD_ANALYZER);
        VALIDATION_CONFIG_CLASSES.put(KEYWORD_ENTRY_SUMMARY, HENTRY_CHILD_ANALYZER);
        VALIDATION_CONFIG_CLASSES.put(KEYWORD_BOOKMARK, HENTRY_CHILD_ANALYZER);
        VALIDATION_CONFIG_CLASSES.put(KEYWORD_UPDATED, DATE_TIME_ANALYZER); // required
        VALIDATION_CONFIG_CLASSES.put(KEYWORD_PUBLISHED, DATE_TIME_ANALYZER);
        VALIDATION_CONFIG_CLASSES.put(KEYWORD_AUTHOR, AUTHOR_ANALYZER); // required
        VALIDATION_CONFIG_CLASSES.put(KEYWORD_ENTRY_KEY, HENTRY_CHILD_ANALYZER); // required
    }
    public final static String[] CHECKABLE_KEYWORDS;


    static {
        CHECKABLE_KEYWORDS = new String[HATOM_HENTRY_ATTRIBUTES.size() + 2];
        System.arraycopy(HENTRY_CHILDREN, 0, CHECKABLE_KEYWORDS, 0, HATOM_HENTRY_ATTRIBUTES.size());

        CHECKABLE_KEYWORDS[CHECKABLE_KEYWORDS.length - 2] = HfeedAnalyzer.KEYWORD_HFEED;
        CHECKABLE_KEYWORDS[CHECKABLE_KEYWORDS.length - 1] = HfeedAnalyzer.KEYWORD_HENTRY;
    }

    // All the keywords that MUST appear in a valid hAtom document
    public static final String[] MANDATORY_KEYWORDS = {KEYWORD_ENTRY_KEY, KEYWORD_ENTRY_TITLE, KEYWORD_UPDATED, KEYWORD_AUTHOR};
    // If true, it is both a hfeed AND a hentry node.
    private boolean hfeedHentryNode = false;

    /**
     * Creates a new instance of HentryAnalyzer and fills the VALIDATION_CONFIG_CLASSES map with the association between
     * hAtom keywords and their analyzeer classes.<br>
     *
     * @param node Node that containes the hAtom entry (hentry)
     */
    public HentryAnalyzer(Node node) {
        setNode(node);
        setHfeedHentryNode();
    }

    /**
     * Perform validation of hentry nodes and delegates validation of its child nodes to specific
     * Analyzer subclasses.<br>
     *
     * @throws java.io.IOException In case the document cannot be parsed into a DOM object
     */
    public void analyze() throws IOException {

        List<Node> hentryNodes;

        // 1 - The hfeed node can also be an hentry node.
        if (isHfeedHentryNode()) {

            hentryNodes = new ArrayList<Node>();
            hentryNodes.add(node);

        } else {
            // Looks for hentry nodes
            hentryNodes = searchNodes(node, ATTR_CLASS, KEYWORD_HENTRY);

            NodeList nodeList = node.getChildNodes();
            // We search for unmatching nodes under the root node.
            // This finds hentry children placed outside hentry nodes
            for (int i = 0; i < nodeList.getLength(); i++) {
                ValidatorEngine.analyzeUnmatchingNodes(getXhtmlDoc(), nodeList.item(i), getDocumentName(), CHECKABLE_KEYWORDS);
            }
        }

        // We check for nested nodes
        checkNestedNodes(hentryNodes, ValidatorEngine.ATTR_CLASS, KEYWORD_HENTRY);

        // Validate children for hentry list
        validateHentriesChildren(hentryNodes);

        // Checks for duplicate entry-key
        validateEntryKeyValues(hentryNodes);
    }

    /**
     * Convenience method to add an entry-key value to a list or report an error in case it has an empty
     * value.<br> It is assumed that the parameter node is has a non-empty entry-key keyword.
     *
     * @param aNode the node
     */
    private void addEntryKeyValue(Node aNode) {

        if (!addMappedAttributeValue(Analyzer.ATTR_NAME_TITLE, aNode)) {
            Report report = new Report(KEYWORD_ENTRY_KEY + " cannot have empty value", aNode);
            engine = ValidatorCache.getInstance().getEngine(getDocumentName());
            engine.addReport(report);
        }

    }

    /**
     * Reports an error in case the given node contains an entry-key value which has already been
     * found in the current validation process (entry-key values MUST be unique inside their parent hfeed
     * element). If the entry-key value is unique, it is added to the entry-key values list.<br>
     * It is assumed that the parameter node is has a non-empty entry-key keyword.<br>
     *
     * @param aNode node containing an entry-key value.
     */
    private void analyzeDuplicateKeyValue(Node aNode) {
        // Title attribute value of a entry-key hatom element
        String value = ((Element) aNode).getAttribute(Analyzer.ATTR_NAME_TITLE);

        if (isDuplicateAttributeValue(Analyzer.ATTR_NAME_TITLE, value)) {
            Report report = new Report(KEYWORD_ENTRY_KEY + " value " + value + " is already present in current hentry", aNode);
            engine = ValidatorCache.getInstance().getEngine(getDocumentName());
            engine.addReport(report);
        } else { // entry-key is unique, we add it to the entry-key values list.
            addEntryKeyValue(aNode);
        }
    }

    /**
     * Checks that all the mandatory attributes under the hentry node are present.<br>
     * If they are not present an error report is added to the validation report list.<br>
     * @param hentryNode node to be checked
     */
    private void checkMandatoryAttributes(Node hentryNode) {

        NodeIterator iterator = XMLUtils.getNodeIterator(getXhtmlDoc(), hentryNode);

        String keywordValue = "";

        Node n;

        for (int i = 0; i < MANDATORY_KEYWORDS.length; i++) {

            while ((n = iterator.nextNode()) != null) {

                if (XMLUtils.nodeAttributeMatches(n, ATTR_CLASS, MANDATORY_KEYWORDS[i])) {
                    keywordValue = MANDATORY_KEYWORDS[i];
                }

            }

            // Bring iterator back to first node
            while ((n = iterator.previousNode()) != null) {
            }

            // No mandatory keyword, has been found
            if ("".equals(keywordValue)) {
                Report report = new Report("Mandatory entry child keyword " + MANDATORY_KEYWORDS[i] + " is missing", hentryNode);
                engine = ValidatorCache.getInstance().getEngine(getDocumentName());
                engine.addReport(report);
            } else {
                // mandatory keyword has been found, reset it for the next iteration
                keywordValue = "";
            }
        }

        iterator.detach();

    }

    /**
     * Creates the proper Analyzer implementation for the given attribute/hatom keyword value.
     * @param item node to be analyzed
     * @param attrName attribute name
     * @param attrValue attribute value that matches an hAtom keyword
     * @return the correct Analyzer implementation
     */
    private Analyzer getChildAnalyzer(Node item, String attrName, String attrValue) {

        String className = "it.pronetics.madstore.hatom.netbeans.validator.engine." + VALIDATION_CONFIG_CLASSES.get(attrValue);
        HentryChildAnalyzer analyzer;

        try {
            analyzer = (HentryChildAnalyzer) Class.forName(className).newInstance();
        } catch (Exception ex) {
            analyzer = new HentryChildAnalyzer(); // we fall back to the basic analyzer
            Exceptions.printStackTrace(ex);
        }

        analyzer.setDocumentName(getDocumentName());
        analyzer.setXhtmlDoc(getXhtmlDoc());
        analyzer.setNode(item);
        analyzer.setAttributeName(attrName);
        analyzer.setAttributeValue(attrValue);

        return analyzer;
    }

    /**
     * Performs validation subtasks on entry-key values, if one or more of the is found under the given node.<br>
     *
     * The following checks are performed:<br>
     * <li> entry-key element value (ie: title="key_001") is used only once
     * inside a collection of hentry elements.
     * <li> entry-key value is not an empty string
     *
     * @param aNode node containing the hentry parent element in the hAtom hierarchy (hfeed).<br>
     */
    private void validateEntryKeyValues(Node aNode) {

        String keywordValue = "";

        // We get an iterator over all the elements in the hfeed hierarchy
        NodeIterator iterator = XMLUtils.getNodeIterator(getXhtmlDoc(), aNode);

        Node n;

        while ((n = iterator.nextNode()) != null) {

            if (XMLUtils.nodeAttributeMatches(n, ATTR_CLASS, KEYWORD_ENTRY_KEY)) {

                analyzeDuplicateKeyValue(n);

                // A new feed-key keyword has been found
                if ("".equals(keywordValue)) {
                    keywordValue = KEYWORD_ENTRY_KEY;
                } else { // There's more than one feed-key for this hfeed, this makes the feed invalid.
                    Report report = new Report("There's more than one " + KEYWORD_ENTRY_KEY + " in the hentry", aNode);
                    engine = ValidatorCache.getInstance().getEngine(getDocumentName());
                    engine.addReport(report);
                }
            }
        }

        iterator.detach();
    }

    /**
     * Convenience method for validating a list of hentry collections<br>
     *
     * @param nodes a collection of hentries.
     */
    private void validateEntryKeyValues(List<Node> nodes) {

        for (Node aNode : nodes) {

            validateEntryKeyValues(aNode);

        }
    }

    /**
     * Performs all the possibile validations (one for each type of hAtom keyword that is under hentry)
     * on the current piece of document that is under the given node, using a NodeIterator that
     * starts immediately under the node.<br>
     *
     * @param node Node corresponding to the hAtom hentry.
     * @throws java.io.IOException
     */
    private void validateHentryChildren(Node node) throws IOException {

        NodeIterator iterator = XMLUtils.getNodeIterator(getXhtmlDoc(), node);
        Analyzer childAnalyzer;
        Node n;

        String[] keys = new String[HATOM_HENTRY_ATTRIBUTES.size()];
        HATOM_HENTRY_ATTRIBUTES.keySet().toArray(keys);

        while ((n = iterator.nextNode()) != null) {

            for (int j = 0; j < keys.length; j++) {
                childAnalyzer = getChildAnalyzer(n, HATOM_HENTRY_ATTRIBUTES.get(keys[j]), keys[j]);
                childAnalyzer.analyze();

            }

        }

        iterator.detach();
    }

    /**
     * Performs all the possibile validations (one for each type of hAtom keyword that is under hentry)
     * on each node in the given node list, using <code>validateHentryChildren(Node node)</code>.
     * <br>
     *
     * @param nodeList
     * @throws java.io.IOException
     */
    private void validateHentriesChildren(List<Node> nodeList) throws IOException {

        for (Node hentryNode : nodeList) {

            checkMandatoryAttributes(hentryNode);

            NodeList nl = hentryNode.getChildNodes();
            Node n;
            for (int i = 0; i < nl.getLength(); i++) {
                n = nl.item(i);
                if (n != null && n.getNodeType() == Node.ELEMENT_NODE) {

                    validateHentryChildren(n);
                }
            }

        }
    }

    public boolean isHfeedHentryNode() {
        return hfeedHentryNode;
    }

    /**
     * Convenience method that checks whether the given node is an hAtom entry or not and sets setHfeedHentryNode.<br>
     *
     * @param node Node to be checked
     * @return <code>true</code> if the node is an hAtom entry (hentry), <code>false</code> otherwise
     */
    private void setHfeedHentryNode() {
        if (getNode() != null) {
            hfeedHentryNode = XMLUtils.nodeAttributeMatches(node, ATTR_CLASS, KEYWORD_HENTRY);
        }
    }
}
