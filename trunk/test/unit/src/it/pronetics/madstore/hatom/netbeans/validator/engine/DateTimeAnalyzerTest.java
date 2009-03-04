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
import it.pronetics.madstore.hatom.netbeans.validator.engine.DateTimeAnalyzer;
import it.pronetics.madstore.hatom.netbeans.validator.engine.ValidatorCache;
import it.pronetics.madstore.hatom.netbeans.validator.engine.ValidatorEngine;

import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import static org.junit.Assert.*;

/**
 * Simple test class for DateTimeAnalyzer 
 * 
 * @author Andrea Castello
 * @version 1.0
 */
public class DateTimeAnalyzerTest {

    private static final String DOC_NAME = "testDateTimePattern";
    
    public DateTimeAnalyzerTest() {
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
     * Test of analyze method, of class DateTimeAnalyzer.
     */
    @Test
    public void testAnalyze() throws Exception {
        
        System.out.println(" Test analyze ");
        
        // Correct PUBLISHED keyword with datetime pattern
        String dateTimeOkPub = "<abbr class=\"published\" title=\"2005-10-10T14:07:00-07:00\">October 10th, 2005</abbr>";
        
        // Correct UPDATED keyword with datetime pattern
        String dateTimeOkUpd = "<abbr class=\"updated\" title=\"2005-10-10T14:07:00-07:00\">October 10th, 2005</abbr>";
        
        // UPDATED with NO datetime
        String dateTimeNoDTP = "<abbr class=\"updated\">October 10th, 2005</abbr>";
        
        // PUBLISHED keyword with WRONG datetime pattern
        String dateTimeWrongDTP = "<abbr class=\"published\" title=\"2005-10-10\">October 10th, 2005</abbr>";
        
        // Correct PUBLISHED keyword, but NOT inside an <abbr> tag
        String dateTimeNoAbbr = "<other class=\"published\" title=\"2005-10-10T14:07:00-07:00\">October 10th, 2005</other>";
        
        // UPDATED keyword, but NOT inside an <abbr> tag AND with NO datetime pattern
        String dateTimeNoAbbrNoDTP = "<other class=\"updated\">October 10th, 2005</other>";
        
        // It has datetime pattern and abbr, but no hAtom tag
        String dateTimeWrongKeyword = "<abbr class=\"other\" title=\"2005-10-10T14:07:00-07:00\">October 10th, 2005</abbr>";
        
        assertEquals( 0, testSingleAnalyze(dateTimeOkPub, BaseAnalyzer.KEYWORD_PUBLISHED) );
        
        assertEquals ( 0, testSingleAnalyze(dateTimeOkUpd, BaseAnalyzer.KEYWORD_UPDATED) );
        
        assertEquals ( 1, testSingleAnalyze(dateTimeNoDTP, BaseAnalyzer.KEYWORD_UPDATED) );
        
        assertEquals ( 1, testSingleAnalyze(dateTimeWrongDTP, BaseAnalyzer.KEYWORD_PUBLISHED) );
        
        assertEquals ( 1, testSingleAnalyze(dateTimeNoAbbr, BaseAnalyzer.KEYWORD_PUBLISHED) );
        
        assertEquals ( 2, testSingleAnalyze(dateTimeNoAbbrNoDTP, BaseAnalyzer.KEYWORD_UPDATED) );
        
        // This is formally not a hAtom format, so it is ignored by validation of updated/published hAtom format
        assertEquals ( 0, testSingleAnalyze(dateTimeWrongKeyword, BaseAnalyzer.KEYWORD_PUBLISHED) );
    }
    
    private int testSingleAnalyze(String xhtmlTag, String attributeValue){
        
        Document doc;
        
        DateTimeAnalyzer instance = new DateTimeAnalyzer();
        try {
            doc = TestUtils.stringToDom(xhtmlTag);
        } catch (Exception ex) {
           fail("Unable to create DOM document :"+ex.getMessage());
           ex.printStackTrace();
           return -1;
        }
        
        instance.setXhtmlDoc(doc);
        instance.setNode(doc.getDocumentElement());
        instance.setAttributeName(ValidatorEngine.ATTR_CLASS);
        instance.setAttributeValue(attributeValue);
        instance.setDocumentName(DOC_NAME);
        
        try {
            instance.analyze();
            
            int reportSize = ValidatorCache.getInstance().getEngine(DOC_NAME).getReports().size(); 
            
            TestUtils.dumpReports(ValidatorCache.getInstance().getEngine(DOC_NAME).getReports());
            
            ValidatorCache.getInstance().getEngine(DOC_NAME).getReports().clear();
          
            return reportSize;
            
            
        } catch (IOException iOException) {
            fail("Analyze raised an exception :"+iOException.getMessage());
            return -1;
        }
        
    }
}
