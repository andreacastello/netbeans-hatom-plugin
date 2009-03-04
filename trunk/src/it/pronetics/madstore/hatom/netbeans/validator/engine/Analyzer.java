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

/**
 * Interface for hAtom nodes analyzers
 * 
 * @author Andrea Castello
 * @version 1.2
 */
public interface Analyzer {
    
    // hAtom keywords
    public static final String KEYWORD_HFEED =  "hfeed";
    public static final String KEYWORD_HENTRY = "hentry";
    public static final String KEYWORD_ENTRY_KEY = "entry-key";
    public static final String KEYWORD_ENTRY_TITLE = "entry-title";
    public static final String KEYWORD_ENTRY_CONTENT = "entry-content";
    public static final String KEYWORD_ENTRY_SUMMARY = "entry-summary";
    public static final String KEYWORD_BOOKMARK = "bookmark";
    public static final String KEYWORD_UPDATED = "updated";
    public static final String KEYWORD_PUBLISHED = "published";
    public static final String KEYWORD_AUTHOR = "author";

    // Class names of commonly used analyzers
    public static final String HENTRY_CHILD_ANALYZER = "HentryChildAnalyzer";
    public static final String DATE_TIME_ANALYZER = "DateTimeAnalyzer";
    public static final String AUTHOR_ANALYZER = "AuthorVcardAnalyzer";

    // hCard keywords
    public static final String KEYWORD_VCARD = "vcard";
    
    // rel-tag keyword 
    public static final String KEYWORD_TAG = "tag";
    
    // title attribute name
    public static final String ATTR_NAME_TITLE = "title";
    
    public void analyze() throws IOException;
    
}
