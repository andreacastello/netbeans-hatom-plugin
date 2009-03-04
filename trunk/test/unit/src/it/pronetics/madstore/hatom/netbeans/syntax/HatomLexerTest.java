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

package it.pronetics.madstore.hatom.netbeans.syntax;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;

/**
 *
 * @author Andrea Castello
 * @version 1.0
 */
public class HatomLexerTest extends TestCase {

    public HatomLexerTest(String testName) {
        super(testName);
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        
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
    
    public static String getTestStringFile(){
        String texthatomHtml = "<!--"+ 
            "Document   : newhtml   Created on : 10-set-2008, 16.04.17 Author     : Andrea -->" +
            "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">" +
            "<html><head><title></title><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">" +
            "</head><body><div class=\"hfeed\"></div><div class=\"author\"></div><div class=\"hentry\"></div></body></html>";
        
        return texthatomHtml;
    }
    

    /**
     * Test of nextToken method, of class HatomLexer.
     */
    @Test
    public void testNextToken() {
        System.out.println("nextToken");
        TokenHierarchy<String> thi = TokenHierarchy.create(getTestStringFile(), HatomTokenId.language());
        TokenSequence<?> sequence = thi.tokenSequence(HatomTokenId.language());
        
        assertTokenCount(sequence.tokenCount(), 7 );
        
        HatomTokenId[] ids = { HatomTokenId.TEXT, HatomTokenId.KEYWORD, HatomTokenId.TEXT, HatomTokenId.KEYWORD, HatomTokenId.TEXT,
                               HatomTokenId.KEYWORD, HatomTokenId.TEXT }; 
        
        int i = 0;
        while(sequence.moveNext()){
            assertTokenEquals(sequence, ids[i]);
            i++;
        }
        
    }

    public void assertTokenEquals(TokenSequence<?> ts, HatomTokenId id){
        Token<?> t = ts.token();
        
        TestCase.assertNotNull("Token is null", t);
         
        HatomTokenId tId = (HatomTokenId)t.id();
        TestCase.assertEquals("Tokens are NOT equal", tId, id);
        
        System.out.println(" --------------- PRINTING TOKENS ----------------------");
        System.out.println(t.text().toString());
        System.out.println();
    }
    
    public void assertTokenCount(int count, int expectedResult) {
        TestCase.assertEquals("Count is NOT equal", count, expectedResult);
    }
    

}