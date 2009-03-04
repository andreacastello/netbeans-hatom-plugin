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

import it.pronetics.madstore.hatom.netbeans.validator.engine.AuthorVcardAnalyzer;
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
 * Simple test class for AuthorVcardAnalyzer 
 * 
 * @author Andrea Castello
 * @version 1.0
 */
public class AuthorVcardAnalyzerTest {

    private static final String DOC_NAME = "testVcardAuthor";
    
    public AuthorVcardAnalyzerTest() {}

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
     * Test of analyze method, of class AuthorVcardAnalyzer.
     */
    @Test
    public void testAnalyze() throws Exception {
        System.out.println(" Test analyze ");
        
        // Correct AUTHOR VCARD ATTRIBUTE
        String vcardOk = "<address class=\"vcard author\"><a class=\"url fn\" href=\"http://theryanking.com\">Ryan King</a></address>";
        
        // Invalid vcard, there's no Fn child node
        String vcardNoFn = "<address class=\"vcard author\"></address>";
        
        // Invalid author vcard
        String noVCard = "<address class=\" author\"></address>";
        
        assertTrue( testSingleAnalyze(vcardOk) );
        
        assertFalse ( testSingleAnalyze(vcardNoFn) );
        
        assertFalse ( testSingleAnalyze(noVCard) );
    }
    
    private boolean testSingleAnalyze(String xhtmlTag){
        
        Document doc;
        
        AuthorVcardAnalyzer instance = new AuthorVcardAnalyzer();
        try {
            doc = TestUtils.stringToDom(xhtmlTag);
        } catch (Exception ex) {
           fail("Unable to create DOM document :"+ex.getMessage());
           ex.printStackTrace();
           return false;
        }
        
        instance.setXhtmlDoc(doc);
        instance.setNode(doc.getDocumentElement());
        instance.setAttributeName(ValidatorEngine.ATTR_CLASS);
        instance.setAttributeValue("author");
        instance.setDocumentName(DOC_NAME);
        
        try {
            instance.analyze();
            
            if (ValidatorCache.getInstance().getEngine(DOC_NAME).getReports().size()>0){
                ValidatorCache.getInstance().getEngine(DOC_NAME).getReports().clear();
                return false; // Error has been found
            }
            else {
                return true;
            }
            
            
        } catch (IOException iOException) {
            fail("Analyze raised an exception :"+iOException.getMessage());
            return false;
        }
        
    }
}
