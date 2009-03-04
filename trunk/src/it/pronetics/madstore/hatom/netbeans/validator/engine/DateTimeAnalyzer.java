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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Analyzes a node, checking that matches some features of the hAtom keywords that use datetime design pattern 
 * (ie: updated or published).<br>
 * 
 * @author Andrea Castello
 * @version 1.1
 */
public class DateTimeAnalyzer extends HentryChildAnalyzer {
    
    // Regular expression that validates the date pattern YYYY-MM-DDTHH:MM:SSZ. Right now it does not validate the date value.
    private final static String DATETIME_REGEXP = "^[0-9]{4}-[0-9]{2}-[0-9]{2}.*T[^+\\-]*([1-9]|1[0-2]):[0-5]\\d(:[0-5]\\d(\\.\\d{1,3})?)?[+\\-]?[0-9]{2}:[0-9]{2}";
    
    // Name of the HTML tag where datetime pattern is usually found
    private String DATE_TIME_PATTERN_TAG = "abbr";
    
    /**
     * Creates a new instance of DateTimeAnalyzer.<br>
     */
    public DateTimeAnalyzer(){}
    
    /**
     * Creates a new instance of DateTimeAnalyzer.<br>
     * 
     * @param node Node that must be analyzed
     * @param attrName name of the attribute of the hAtom element that should be found in the node
     * @param attrValue value of the hAtom element that should be found in the node 
     */
    public DateTimeAnalyzer(Node node, String attrName, String attrValue){
        super(node, attrName, attrValue);
    }
    
    /**
     * Performs validation of an XHTML hAtom microformat that implements the datetime design pattern.<br>
     */
    @Override
    public void analyze() throws IOException {
        
        // It's not the node we are looking for, let check if there are some unwanted hAtom attributes/formats
        if (!XMLUtils.nodeAttributeMatches(getNode(), getAttributeName(), getAttributeValue())){
            
            searchInvalidAttributes();
            
        }
        else { // Is a valid hAtom node, check if there are nested nodes
            
            String[] keys = new String[HATOM_HENTRY_ATTRIBUTES.size()];  
            HATOM_HENTRY_ATTRIBUTES.keySet().toArray(keys);
            
            for (int i=0; i<keys.length; i++){
                checkNestedNode(getNode(), getAttributeName(), keys[i]);
            }
            
            Report report;
        
            // Check if hAtom value is inside an <abbr> tag
            if(!isAbbrNode()){
                report = new Report(getAttributeValue() + " hAtom keyword must be contained inside an <abbr> tag", getNode());
                engine = ValidatorCache.getInstance().getEngine(getDocumentName());
                engine.addReport(report);
            }
        
            // Analyze date and time pattern
            if(!dateTimeMatches()){
                report = new Report(getAttributeValue()+" date does not match pattern YYYY-MM-DDTHH:MM:SS+ZZ:ZZ", getNode());
                engine = ValidatorCache.getInstance().getEngine(getDocumentName());
                engine.addReport(report);
            }
        }
        
    }

    /**
     * Check if the node datetime matches the datetime design pattern for hAtom microformat.<br>
     * <b>Current implementation validates that datetime format, but not its value.</b><br>
     * @return
     */
    private boolean dateTimeMatches() {
       
        boolean result = false;
        
        String dateTime = ((Element)getNode()).getAttribute(Analyzer.ATTR_NAME_TITLE);
        if (dateTime!=null){
            Pattern p = Pattern.compile(DATETIME_REGEXP);
            Matcher matcher = p.matcher(dateTime);
            result = matcher.matches();
        }
        
        return result;
    }

    /**
     * Returns <code>true</code> if teh current node is an <abbr> tag, <code>false</code> otherwise.<br>
     * @return
     */
    private boolean isAbbrNode() {
       // Node name should never be null: in case of non-element node, it should return #text or similar meta-names
       return getNode().getNodeName().equalsIgnoreCase(DATE_TIME_PATTERN_TAG);
    }   
}
