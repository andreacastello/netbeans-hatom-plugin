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
import java.io.InputStream;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.w3c.dom.Document;

/**
 * Simple test class for hAtom feed validation
 * @author Andrea Castello
 * @version 1.0
 */
public class HfeedAnalyzerTest {

    private static final String DOC_NAME = "testHfeed";
    // Directory where test files are stored
    public static final String XHTML_DIR = "/it/pronetics/madstore/hatom/xhtml/";
    
    public HfeedAnalyzerTest() {
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
    
       
    private void singleHfeedTest() {
    	
    	System.out.println("Tests for single hfeed files");
    	
    	// 1 - Single hfeed, single hentry file
        InputStream is = this.getClass().getResourceAsStream(XHTML_DIR + "hfeed_hentry_ok.xhtml");
        assertEquals(0, testSingleAnalyze(is));
        
        // 2 - File with nested hfeed element. Should return two errors: 1 nested hfeed, 1 misplaced hfeed (under hentry).
        is = this.getClass().getResourceAsStream(XHTML_DIR + "hfeedNested_1level.xhtml");
        assertEquals(2, testSingleAnalyze(is));
        
        // 3 - File with deeply nested hfeed element.
        is = this.getClass().getResourceAsStream(XHTML_DIR + "hfeedNested_deeply.xhtml");
        assertEquals(1, testSingleAnalyze(is));
        
        // 4 - Multiple nested hfeed at different levels
        is = this.getClass().getResourceAsStream(XHTML_DIR + "hfeedNested_multi.xhtml");
        assertEquals(5, testSingleAnalyze(is));
        
        // 5 - No feed-key. Should return 2 errors: missing feed-key and feed-key must be hfeed first child
        is = this.getClass().getResourceAsStream(XHTML_DIR + "hfeed_noFeedKey.xhtml");
        assertEquals(2, testSingleAnalyze(is));
        
        // 6 - Feed-key not before any hentry.
        is = this.getClass().getResourceAsStream(XHTML_DIR + "hfeed_feedKeyNotFirst.xhtml");
        assertEquals(1, testSingleAnalyze(is));
        
        // 7 - Feed-key empty.
        is = this.getClass().getResourceAsStream(XHTML_DIR + "hfeed_feedKeyEmpty.xhtml");
        assertEquals(1, testSingleAnalyze(is));
        
        // 8 - Multiple feed-key in the same hfeed.
        is = this.getClass().getResourceAsStream(XHTML_DIR + "hfeed_feedKeyEmpty.xhtml");
        assertEquals(1, testSingleAnalyze(is));
       
    }
    
    private void multiHfeedTest(){
    	
    	System.out.println("Tests for multiple hfeed files");
    	InputStream is;
    	
    	// 1 - File with nested hfeed element. Should return three errors: 2 nested hfeed, 1 misplaced hfeed (under hentry).
        is = this.getClass().getResourceAsStream(XHTML_DIR + "multiHfeed_Nested.xhtml");
        assertEquals(3, testSingleAnalyze(is));
        
        // 2 - File with nested hfeed element.
        is = this.getClass().getResourceAsStream(XHTML_DIR + "multiHfeed_NoFeedKey.xhtml");
        assertEquals(4, testSingleAnalyze(is));
        
        // 3 - File with empty feed-key element
        is = this.getClass().getResourceAsStream(XHTML_DIR + "multiHfeed_EmptyFeedKey.xhtml");
        assertEquals(1, testSingleAnalyze(is));
        
        // 4 - File with duplicate feed-key value
        is = this.getClass().getResourceAsStream(XHTML_DIR + "multiHfeed_DuplicateKeyValue.xhtml");
        assertEquals(1, testSingleAnalyze(is));
        
        // 5 - File with 2 hfeed and 3 feed-keys, 2 of them with a duplicate value.
        is = this.getClass().getResourceAsStream(XHTML_DIR + "multiHfeed_DuplicateFeedKeyAndValue.xhtml");
        assertEquals(2, testSingleAnalyze(is));
        
    }
    
    /**
     * Test of analyze method, of class HfeedAnalyzer.
     */
    @Test
    public void testAnalyze() throws Exception {
        
        singleHfeedTest();
        
        multiHfeedTest();
    }
    
    private int testSingleAnalyze(InputStream is){
        
        Document doc;
        
        HfeedAnalyzer instance = new HfeedAnalyzer();
        try {
            doc = XMLUtils.getDocument(is);
            is.close();
        } catch (Exception ex) {
           fail("Unable to create DOM document :"+ex.getMessage());
           ex.printStackTrace();
           return -1;
        }
        
        instance.setXhtmlDoc(doc);
        instance.setNode(doc.getDocumentElement());
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
