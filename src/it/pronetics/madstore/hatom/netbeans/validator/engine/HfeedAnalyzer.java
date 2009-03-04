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
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;
import static it.pronetics.madstore.hatom.netbeans.validator.engine.ValidatorEngine.ALL_KEYWORDS;

/**
 * Analyzer class for hfeed entity inside an hAtom XHTML document.<br>
 * It checks if one ore more hfeeds entities exist and, in the ngative case, it associates
 * the whole document to a hfeed.<br>
 * It also checks presence and consistence of feed-key keywords.<br>
 * Delegates analysis of hfeed child entities to other specific Analyzer implementors.<br>
 *
 * @author Andrea Castello
 * @version 1.3
 */
public class HfeedAnalyzer extends BaseAnalyzer {

    // No hfeed attribute, so hfeed is assumed to be the page itself.
    private static final int STATE_EQUALS_PAGE = 0;
    // There's only one feed attribute in the page.
    private static final int STATE_SINGLE_FEED = 1;
    // There are more than one hfeed attribute in the page
    private static final int STATE_MULTI_FEED = 2;
    // Feed key postion states
    // Undefined feed key state
    private static final byte FEED_KEY_POS_UNDEFINDED = -1;
    // Feed key position: before the hentries
    private static final byte FEED_KEY_POS_BEFORE = 0;
    // Feed key position: after the hentries
    private static final byte FEED_KEY_POS_AFTER = 1;

    // Constant for FEED-KEY
    public static final String FEED_KEY = "feed-key";
    // List of feed keys found in the document
    private List<String> feedKeyList;
    // Type of hfeed layout we have in the document
    private int state = STATE_EQUALS_PAGE; // default is page feed

    /**
     * Sets document and creates the NodeIterator for the given XHTML DOM document.<br>
     * @param doc The document to be analyzed
     */
    public void init(Document doc){

        setXhtmlDoc(doc);

        feedKeyList = new ArrayList<String>();

        attrValueCache.put(Analyzer.ATTR_NAME_TITLE, feedKeyList);
    }

    public void analyze() throws IOException {
        // 1- Check if hfeed exist
        List<Node> hfeedNodes = searchNodes(getXhtmlDoc(),  ValidatorEngine.ATTR_CLASS, KEYWORD_HFEED);

        // 2- Get the hfeed layout state
        evalFeedState(hfeedNodes);

        if (hfeedNodes!=null){
        	// Check if there are nested hfeed (which is not allowed)
            checkNestedNodes(hfeedNodes, ValidatorEngine.ATTR_CLASS, Analyzer.KEYWORD_HFEED);
        }

        switch(state){
            case STATE_EQUALS_PAGE:
                // Analyze the child element feed-key
                analyzeFeedKey(getXhtmlDoc().getDocumentElement());
                // Hfeed is the page itself, now we search for the entries
                analyzeHentries(getXhtmlDoc().getDocumentElement());
                break;

            case STATE_SINGLE_FEED:
                // Analyze the child element feed-key
                analyzeFeedKey(hfeedNodes.get(0));
                // Hfeed is a single one, now we search for the entries under its node
                analyzeHentries(hfeedNodes.get(0));

                ValidatorEngine.analyzeUnmatchingNodes(getXhtmlDoc(), getXhtmlDoc(), getDocumentName(), ALL_KEYWORDS);

                break;

            case STATE_MULTI_FEED:

                for(Node tempNode: hfeedNodes){
                    // Analyze feed keys
                    analyzeFeedKey(tempNode);
                    // Analyze single hentries
                    analyzeHentries(tempNode);
                }

                ValidatorEngine.analyzeUnmatchingNodes(getXhtmlDoc(), getXhtmlDoc(), getDocumentName(), ALL_KEYWORDS);
                break;
        }

    }

    /**
     * Gets the node's feed key value (ie: title="key_001"), cheks if it is empty and - if present -
     * adds it to the cache of feed key values.<br>
     *
     * @param aNode A node containing the feedKey hAtom keyword.
     */

    private void addFeedKeyValue(Node aNode) {

        if (!addMappedAttributeValue(Analyzer.ATTR_NAME_TITLE, aNode)){
            Report report = new Report(FEED_KEY + " cannot have empty value", aNode);
            engine = ValidatorCache.getInstance().getEngine(getDocumentName());
            engine.addReport(report);
        }

    }

    /**
     * Checks whether the "title" attribute value of a node containing a feed-key keyword (in the form of an class attribute value), occurs more than once
     * in the whole document.<br>
     * If more than one
     *
     * @param aNode the node to be analyzed
     */
    private void analyzeDuplicateKeyValue(Node aNode) {
        // Title attribute value
        String value = ((Element)aNode).getAttribute(Analyzer.ATTR_NAME_TITLE);

        if (isDuplicateAttributeValue(Analyzer.ATTR_NAME_TITLE, value)){
            Report report = new Report(FEED_KEY + " value " + value + " is already present in the document", aNode);
            engine = ValidatorCache.getInstance().getEngine(getDocumentName());
            engine.addReport(report);
        }
    }

    /**
     * Performs validation of feed-key mandatory keyword, contained inside a class attribute value. <br>
     *
     * @param aNode an hfeed node.
     */
    private void analyzeFeedKey(Node aNode) {

        NodeIterator iterator = XMLUtils.getNodeIterator(getXhtmlDoc(), aNode);

        int feedKeyPositionState = FEED_KEY_POS_UNDEFINDED;

        String keywordValue = "";

        Node n;

        while ((n = iterator.nextNode()) != null) {


            if (XMLUtils.nodeAttributeMatches(n, ValidatorEngine.ATTR_CLASS, FEED_KEY)) {

                // If we have a multi feed document we check for duplicate keys
                if (state == STATE_MULTI_FEED) {
                    analyzeDuplicateKeyValue(n);
                }

                // Feed key has been found before any hentry element, so set the proper position state.
                if (feedKeyPositionState == FEED_KEY_POS_UNDEFINDED) {
                    feedKeyPositionState = FEED_KEY_POS_BEFORE;
                }

                // A new FEED_KEY keyword has been found
                if ("".equals(keywordValue)) {
                    keywordValue = FEED_KEY;
                    addFeedKeyValue(n);
                } else { // There's more than one feed-key for this hfeed, this makes the feed invalid.
                    Report report = new Report(FEED_KEY + " keyword is already present for this hfeed", n);
                    engine = ValidatorCache.getInstance().getEngine(getDocumentName());
                    engine.addReport(report);
                }

            } else {
                // If it's not a feed key element, we must check that no hentry occurs before it.
                feedKeyPositionState = checkFeedKeyPosition(feedKeyPositionState, n);
            }
        }

        // No feed-key has been found
        if ("".equals(keywordValue)) {
            Report report = new Report("Mandatory hfeed child keyword "+ FEED_KEY + " is missing", aNode);
            engine = ValidatorCache.getInstance().getEngine(getDocumentName());
            engine.addReport(report);
        }

    }


    /**
     * Creates a hentryAnalyzer object, associates it with a hfeed child node and
     * delegates the child node analysis.<br>
     *
     * @param node hfeed child node
     * @throws java.io.IOException in case
     */
    private void analyzeHentries(Node aNode) throws IOException {
        HentryAnalyzer hentryAnalizer = new HentryAnalyzer(aNode);
        hentryAnalizer.setDocumentName(getDocumentName());
        hentryAnalizer.setXhtmlDoc(getXhtmlDoc());
        hentryAnalizer.analyze();
    }

    /**
     * Checks whether the current node is a hentry element and creates an error report if no
     * feed-key element has already been found.<br>
     *
     * In case more that one hentry are found before the feed-key, <b>only one error report is
     * created</b><br>
     *
     * @param currentPositionState the feed key position state before this node is analyzed
     * @param aNode the node to be checked against the feed-key
     * @return the new feed-key position state
     */
    private int checkFeedKeyPosition(int currentPositionState, Node aNode) {

        if (XMLUtils.nodeAttributeMatches(aNode, ValidatorEngine.ATTR_CLASS, KEYWORD_HENTRY) &&
                currentPositionState == FEED_KEY_POS_UNDEFINDED) {

            currentPositionState = FEED_KEY_POS_AFTER;
            Report report = new Report(FEED_KEY + " MUST be placed before any hentry", aNode);
            engine = ValidatorCache.getInstance().getEngine(getDocumentName());
            engine.addReport(report);

        }

        return currentPositionState;
    }


    /**
     * Evaluates the hfeed state based on the retrieved hfeed list.
     * If no hfeed keyword has been found, hfeed is the whole page.
     * Then we make a difference between pages with a single hfeed and pages with
     * multiple hfeeds, that require further validations.<br>
     *
     * @param list list of tags that contain the hfeed format
     */
    private void evalFeedState(List<Node> list){

        if (list!=null && list.size()==1){
            state = STATE_SINGLE_FEED;
        }
        else if (list!=null && list.size()>1){
            state = STATE_MULTI_FEED;
        }

    }

}
