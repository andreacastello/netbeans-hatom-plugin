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

import it.pronetics.madstore.hatom.netbeans.validator.engine.BaseAnalyzer;
import it.pronetics.madstore.hatom.netbeans.validator.engine.HentryChildAnalyzer;
import it.pronetics.madstore.hatom.netbeans.validator.engine.Report;
import it.pronetics.madstore.hatom.netbeans.validator.engine.ValidatorCache;
import it.pronetics.madstore.hatom.netbeans.validator.engine.ValidatorEngine;

import java.io.IOException;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import static org.junit.Assert.*;

/**
 * Simple test class for HentryChildAnalyzer 
 * 
 * @author Andrea Castello
 * @version 1.0
 */
public class HentryChildAnalyzerTest {
    
    private static final String DOC_NAME = "testHentryChild";

    public HentryChildAnalyzerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        // We create cache that will be used to store reports fot this test class
        ValidatorCache.getInstance().createEngine(DOC_NAME);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of analyze method, of class HentryChildAnalyzer.
     */
    @Test
    public void testAnalyze() throws Exception {
        System.out.println("Test analyze");
        
        // Regular ENTRY-TITLE
        String hentryTitle = "<h3 class=\"entry-title\">" +
            "<a href=\"http://www.microformats.org/blog/...\" rel=\"bookmark\" title=\"...\">Valid HATOM feed</a></h3>";
 
        // Possible hentry category
        String hentryCategory = "<li><a href=\"http://technorati.com/tag/mediawiki\" rel=\"tag\">mediawiki</a></li>";
        
        // Entry-content
        String hentryContent = "<div class=\"entry-content\"><p>We’ve restored the wiki</p></div>";
        
        // Entry-content inside a wrong attribute name
        String wrongContent = "<div href=\"entry-content\"><p>Test</p></div>";
        
        assertEquals(0, testSingleAnalyze(hentryTitle, ValidatorEngine.ATTR_CLASS, BaseAnalyzer.KEYWORD_ENTRY_TITLE ));
        
        assertEquals(0, testSingleAnalyze(hentryCategory, ValidatorEngine.ATTR_REL, BaseAnalyzer.KEYWORD_TAG ));
    
        assertEquals(0, testSingleAnalyze(hentryContent, ValidatorEngine.ATTR_CLASS, BaseAnalyzer.KEYWORD_ENTRY_CONTENT ));
        
        assertEquals(1, testSingleAnalyze(wrongContent, ValidatorEngine.ATTR_CLASS, BaseAnalyzer.KEYWORD_ENTRY_CONTENT ));
    }
    
    
    /**
     * Tests method that finds unwanted nested hAtom nodes.
     * @throws java.lang.Exception
     */
    @Test
    public void testNestedNodes() throws Exception {
        
        System.out.println("Testing nested nodes");
        
        // Regular ENTRY-TITLE with unwanted nested entry-title node
        String nestedHentryTitle = "<h3 class=\"entry-title\">" +
            "<a href=\"http://www.microformats.org/blog/...\" rel=\"bookmark\" title=\"...\">Valid HATOM feed</a>" +
            "<h1 class=\"entry-title\"></h1></h3>";
 
        assertEquals(1, testSingleAnalyze(nestedHentryTitle, ValidatorEngine.ATTR_CLASS, BaseAnalyzer.KEYWORD_ENTRY_TITLE));
        
    }

    
    private int testSingleAnalyze(String xhtmlTag, String attrName, String attributeValue){
        
        Document doc;
        
        HentryChildAnalyzer instance = new HentryChildAnalyzer();
        try {
            doc = TestUtils.stringToDom(xhtmlTag);
        } catch (Exception ex) {
           fail("Unable to create DOM document :"+ex.getMessage());
           ex.printStackTrace();
           return -1;
        }
        
        instance.setXhtmlDoc(doc);
        instance.setNode(doc.getDocumentElement());
        instance.setAttributeName(attrName);
        instance.setAttributeValue(attributeValue);
        instance.setDocumentName(DOC_NAME);
        
        try {
            instance.analyze();
            
            List<Report> reps = ValidatorCache.getInstance().getEngine(DOC_NAME).getReports(); 
            
            TestUtils.dumpReports(reps);
            
            int reportSize = reps.size(); 
            
            ValidatorCache.getInstance().getEngine(DOC_NAME).getReports().clear();
            
            return reportSize;
            
            
        } catch (IOException iOException) {
            fail("Analyze raised an exception :"+iOException.getMessage());
            return -1;
        }
        
    }

}
