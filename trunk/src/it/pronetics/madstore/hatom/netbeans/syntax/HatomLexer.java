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

import java.util.List;
import org.netbeans.api.lexer.Token;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerInput;
import org.netbeans.spi.lexer.LexerRestartInfo;
import org.netbeans.spi.lexer.TokenFactory;

/**
 * Lexical analyzer for hAtom keywords embedded in HTML or XHTML files.<br>
 * As of current version, it just creates tokens for simple keywords such <code>hentry</code>.
 *
 * @author Andrea Castello
 * @version 1.1
 */
public class HatomLexer implements Lexer<HatomTokenId> {

    // End of file character
    private static final int EOF = LexerInput.EOF;
    /** Set of characters that can define a hAtom keyword's "left border".*/
    private static final char[] PRE_KEYWORD_CHARS = {'"', ' ', '<'};
    /** Set of characters that can define a hAtom keyword's "left border".*/
    private static final char[] POST_KEYWORD_CHARS = {'"', ' ', '>'};    // Lexer internal states
    private static final int INIT = 0;
    private static final int ISI_KEYWORD = 1; // is a keyword part
    private static final int ISA_KEYWORD = 2; // after a keyword
    // Input to be analyzed
    private LexerInput input;
    // Factory that creates the token instances,
    private TokenFactory<HatomTokenId> tokenFactory;
    // Lexer state
    private int state;
    // Lenght of the currently analyzed token
    private int tokenLenght;
    // cache containing all the hAtom keywords
    private final static TagCache tagCache = TagCache.getCache();

    /**
     * Creates the hAtom lexer instance starting from the infos retrieved by Netbeans platform.<br>
     * @param info object containing information about hAtomToken ids and the associated language / embedded language.
     */
    public HatomLexer(LexerRestartInfo<HatomTokenId> info) {
        this.input = info.input();
        this.tokenFactory = info.tokenFactory();
        this.state = (info.state() != null) ? (Integer) info.state() : INIT;

    }

    /**
     * Gets the next token in the lexer input.<br>
     * @return The id of the newly found token.
     */
    public Token<HatomTokenId> nextToken() {

        Token<HatomTokenId> token = null;

        int c = input.read();

        switch (state) {
            case INIT:
                token = findNextToken(c);
                break;
            case ISI_KEYWORD:
                token = goToKeywordEnd(c);
                break;
            case ISA_KEYWORD:
                token = nextTokenAfterKeyword(c);
                break;
            default:
                ;
        }

        return token;
    }

    /**
     * Returns this lexer' state.<br>
     */
    public Object state() {
        return this.state;
    }

    // Not implemented
    public void release() {
    }

    /** 
     * Goes to the end of file and returns the last token.
     * @return the last text token
     */
    private Token<HatomTokenId> goToEOF() {

        if (input.readLengthEOF() == 1) {
            return null; //just EOL is read
        } else {
            //there is something else in the buffer except EOL
            //we will return last token now
            input.backup(1); //backup the EOL, we will return null in next nextToken() call
            return tokenFactory.createToken(HatomTokenId.TEXT);
        }
    }

    /**
     * Checks whether the passed character is one of the allowed keyword's left limits
     * @param c the character to be analyzed
     * @return <code>true</code> if c is an allowed character for keyword's left limit, <code>false</code> otherwise
     */
    private boolean isPreKeywordChar(int c) {

        for (int i = 0; i < PRE_KEYWORD_CHARS.length; i++) {
            if (c == PRE_KEYWORD_CHARS[i]) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks whether the passed character is one of the allowed keyword's right limits
     * @param c the character to be analyzed
     * @return <code>true</code> if c is an allowed character for keyword's right limit, <code>false</code> otherwise
     */
    private boolean isPostKeywordChar(int c) {

        for (int i = 0; i < POST_KEYWORD_CHARS.length; i++) {
            if (c == POST_KEYWORD_CHARS[i]) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check if the parameter character is the end of an hAtom keyword.<br>
     * @param c character to be analyzed
     * @return true if the character is the end of a hAtom keyword, false otherwise
     */
    private boolean isKeyWordEnd(int c) {

        List<String> tags = tagCache.getTagList();
        CharSequence cs = input.readText();

        for (String tag : tags) {
            if (cs.toString().endsWith(tag)) {

                // Key word end reach, we read one ore character
                // in order to find if the keyword has been completed or is part of a longer word
                c = input.read();

                if (isPostKeywordChar(c)) { // keyword really ends here
                    
                    tokenLenght = tag.length();
                    input.backup(tokenLenght + 2); // back two the characters before keyword starts

                    // Now we also check that the character before the keyword is valid
                    c = input.read();

                    if (isPreKeywordChar(c)) {
                        return true;
                    } else {
                        // Keyword not found, we can go right after the analyzed word
                        skip(tokenLenght);
                    }
                }
            }
        }

        return false;
    }

    /**
     * Searches for the token following a hAtom keyword.<br>
     * 
     * @param c character 
     * @return the Token to which the character belongs
     */
    private Token<HatomTokenId> nextTokenAfterKeyword(int c) {
        return findNextToken(c);
    }

    /**
     * Goes to keyword (discovered with the invocation of <code>isKeyWordEnd(int)</code>
     * and returns the <code>KEYWORD</code> token.<br>
     * @param c character to be analyzed
     * @return token id (always KEYWORD)
     */
    private Token<HatomTokenId> goToKeywordEnd(int c) {
        for (int i = 0; i < tokenLenght - 1; i++) {
            input.read();
        }

        state = ISA_KEYWORD;
        return tokenFactory.createToken(HatomTokenId.KEYWORD);
    }

    /**
     * Finds the next text token, analyzing all the characters in the input until it
     * finds one or EOF.<br>
     * @param c character to be analyzed
     * @return the id of the found token or null if it reaches the EOF.
     */
    private Token<HatomTokenId> findNextToken(int c) {

        while (true) {
            
            switch (c) {
                
                case 'd': // character that are all the possible end letters for hAtom keywords.
                case 'e':
                case 'y':
                case 't':
                case 'r':
                case 'k':
                case 'g':
                    if (isKeyWordEnd(c)) {
                        state = ISI_KEYWORD;
                        return tokenFactory.createToken(HatomTokenId.TEXT);
                    }
                    break;
                case EOF:
                    return goToEOF();
                default:
                    ;
            }

            c = input.read();
        }
    }

    /**
     * Reads a variable number of character which have to be skipped by the lexer.<br>
     * Skipped characters must have already been analyzed by the lexer.<br>
     * @param skipChars Number of characters that must be skipped.
     */
    private void skip(int skipChars) {
        
        for (int i = 0; i < skipChars; i++) {
            input.read();
        }
    }
}