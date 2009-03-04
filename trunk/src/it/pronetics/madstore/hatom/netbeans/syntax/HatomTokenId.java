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

import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;
import org.netbeans.api.html.lexer.HTMLTokenId;
import org.netbeans.api.lexer.InputAttributes;
import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.LanguagePath;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.spi.lexer.LanguageEmbedding;
import org.netbeans.spi.lexer.LanguageHierarchy;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;

/**
 * Enumerates all the token ids for this syntax highlight module.
 * The ids define a "language" syntax and are used inside the lexical analyzer class ("Lexer").<br>
 * 
 * @author Andrea Castello
 * @version 1.1
 */
public enum HatomTokenId implements TokenId {

    // The token IDs may be assigned to categories, and
    // the coloring information can then be assigned directly
    // to a tokenId.name(), for example "NAME", or to a token
    // category, for example "separator"  
    KEYWORD("keyword"),
    TEXT("text");
    
    private String primaryCategory;
    
    private HatomTokenId(String primaryCategory) {
        this.primaryCategory = primaryCategory;
    }
    
    public String primaryCategory() {
        return primaryCategory;
    }
    
    /** 
     * Create the "language" syntax object for the hAtom format.
     */
    private static final Language<HatomTokenId> language = new LanguageHierarchy<HatomTokenId>() {
        // Specifies the association of the token IDs for
        // this language with the coloring information registered
        // in the layer file for the MIME type:
        @Override
        protected String mimeType() {
            return "text/html";
        }

        /**
         * Creates all the token ids for this language.
         */
        protected Collection<HatomTokenId> createTokenIds() {
            return EnumSet.allOf(HatomTokenId.class);
        }
        
        // Creates extra token categories, for explicit association
        // of token IDs into categories:
        @Override
        protected Map<String, Collection<HatomTokenId>> createTokenCategories() {
            return null;
        }
        
        /**
         * Defines an embedded language for a specified token.
         * In our case, we just embed HTML syntax language in tokens with id "TEXT", which is always HTML language
         */
        @Override
        protected LanguageEmbedding<?> embedding(Token<HatomTokenId> token, LanguagePath languagePath, InputAttributes inputAttributes) {
            
            if (token.id().equals(HatomTokenId.TEXT)){
                return LanguageEmbedding.create(HTMLTokenId.language(), 0, 0, true);
            }
            
            return null;
        }
         
        
        /**
         * Provides information needed for restarting the Lexer.<br>
         */ 
        @Override
        protected Lexer<HatomTokenId> createLexer(LexerRestartInfo<HatomTokenId> info) {
            return new HatomLexer(info);
        }
    }.language();


    /**
     * Returns the language for this token is
     * @return
     */
    public static Language<HatomTokenId> language() {
        return language;
    }
    

}
