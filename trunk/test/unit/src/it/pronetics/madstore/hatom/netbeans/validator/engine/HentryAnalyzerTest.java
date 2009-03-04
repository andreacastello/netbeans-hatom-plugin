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

import static it.pronetics.madstore.hatom.netbeans.validator.engine.HfeedAnalyzerTest.XHTML_DIR;

import it.pronetics.madstore.hatom.netbeans.validator.engine.BaseAnalyzer;
import it.pronetics.madstore.hatom.netbeans.validator.engine.HentryAnalyzer;
import it.pronetics.madstore.hatom.netbeans.validator.engine.HfeedAnalyzer;
import it.pronetics.madstore.hatom.netbeans.validator.engine.Report;
import it.pronetics.madstore.hatom.netbeans.validator.engine.ValidatorCache;
import it.pronetics.madstore.hatom.netbeans.validator.engine.XMLUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import static org.junit.Assert.*;

/**
 *
 * @author Andrea Castello
 * @version 1.0
 */
public class HentryAnalyzerTest {

    private static final String DOC_NAME = "testHentry";
    
    public HentryAnalyzerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
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
    
    @Test
    public void testAnalyze() throws Exception {
    	
    	testSingleHentry();
    	
    	testMultipleHentry();
    	
    }

    private void testMultipleHentry() throws Exception {
		
    	System.out.println("Testing multiple hentry");
    	
    	InputStream is;
    	
    	// 1 - Two hentries, duplicate entry-keys, one of them has a duplicate value
        is = this.getClass().getResourceAsStream(XHTML_DIR + "multiHentry_duplicateEntryKey.xhtml");
        assertEquals(2, testSingleAnalyze(is, false));
        
        // 2 - Two hentries, four entry-keys, 2 per hentry and 2 duplicates in separate hentries
        is = this.getClass().getResourceAsStream(XHTML_DIR + "multiHentry_duplicateEntryKeyValue.xhtml");
        assertEquals(4, testSingleAnalyze(is, false));
        
        // 3 - Same entry-key in different hfeed. This is allowed
        is = this.getClass().getResourceAsStream(XHTML_DIR + "multiHentry_equalsEntryKeyInDifferentHfeed.xhtml");
        assertEquals(0, testSingleAnalyze(is, false));
    	
	}

	/**
     * Test of analyze method, of class HentryAnalyzer.
     */
    private void testSingleHentry() throws Exception {

    	System.out.println("Testing single hentry");
    	
    	InputStream is;
    	
    	// 1 - Single hentry file, no errors
        is = this.getClass().getResourceAsStream(XHTML_DIR + "hfeed_hentry_ok.xhtml");
        assertEquals(0, testSingleAnalyze(is, false));
        
    	// 2 - No entry-key
        is = this.getClass().getResourceAsStream(XHTML_DIR + "hentry_noEntryKey.xhtml");
        assertEquals(1, testSingleAnalyze(is, true));
        
        // 3 - Empty entry-key
        is = this.getClass().getResourceAsStream(XHTML_DIR + "hentry_emptyEntryKey.xhtml");
        assertEquals(1, testSingleAnalyze(is, true));
        
        // 4 - Duplicate entry-key element and value
        is = this.getClass().getResourceAsStream(XHTML_DIR + "hentry_EntryKeyDuplicate.xhtml");
        assertEquals(2, testSingleAnalyze(is, true));
        
        // 5 - Invalid position of a hentry child element
        is = this.getClass().getResourceAsStream(XHTML_DIR + "hentry_invalidChildrenPosition.xhtml");
        assertEquals(1, testSingleAnalyze(is, false));
        
    	// 6 - No mandatory children. 3 errors: no updated, no entry-title, no author
    	is = this.getClass().getResourceAsStream(XHTML_DIR + "hentry_noMandatoryChildren.xhtml");
        assertEquals(3, testSingleAnalyze(is, true));
    	
        // 7 - Hentry with a nested hentry tag and (incidentally) a duplicate entry-key
        is = this.getClass().getResourceAsStream(XHTML_DIR + "hentry_nested.xhtml");
        assertEquals(2, testSingleAnalyze(is, true));
        
    }
    
    private int testSingleAnalyze(InputStream is, boolean isHentryXML){
        
        Document doc;
        BaseAnalyzer analyzer;
        
        try {
            doc = XMLUtils.getDocument(is);
            is.close();
        } catch (Exception ex) {
           fail("Unable to create DOM document :"+ex.getMessage());
           ex.printStackTrace();
           return -1;
        }
        
        if (isHentryXML){
        	analyzer = new HentryAnalyzer(doc.getDocumentElement());
        }
        else {
        	analyzer = new HfeedAnalyzer();
        	analyzer.setNode(doc.getDocumentElement());
        }
        
        analyzer.setXhtmlDoc(doc);
        analyzer.setDocumentName(DOC_NAME);
        
        try {
        	analyzer.analyze();
            
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
