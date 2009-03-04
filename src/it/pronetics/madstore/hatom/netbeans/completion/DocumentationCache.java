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

package it.pronetics.madstore.hatom.netbeans.completion;

import java.util.ResourceBundle;
import org.openide.util.Exceptions;

/**
 * Singleton class that loads the <b>hAtom</b> documentation from a properties file into
 * a cache that will be used in the autocompletion documentation panel.<br>
 * The file that stores the keywords is the <code>documentation.properties</code> contained in the 
 * completion package.<br>
 * 
 * @author Andrea Castello
 * @version 1.0
 */
public class DocumentationCache {

    
    // Path of the properties file where hAtom documentation is stored. 
    private final static String DOCFILE_PATH = "it.pronetics.madstore.hatom.netbeans.completion.documentation";
    // Used to access the module properties file.<br>
    private ResourceBundle bundle = null;
    // Single class instance.
    private static DocumentationCache instance = null;

    /**
     * When it is called by the static <code>getCache</code> method, this constructor gets the ResourceBundle instance
     * associated with the hAtom documentation file.<br> 
     * <br>
     */
    private DocumentationCache(){
        try {
            bundle = ResourceBundle.getBundle(DOCFILE_PATH); 
        }
        catch(Exception ex){
            Exceptions.attachMessage(ex, "Unable to load tag list for code completion");
            ex.printStackTrace(); // writes on the IDE's output tab.
        }
    }
    
    /** 
     * Gets the single instance for this object.<br>
     * @return the documentation cache.
     */
    public static synchronized DocumentationCache getCache(){
        if (instance == null){
            instance = new DocumentationCache();
        }
        
        return instance;
    }

    /**
     * Gets the proper docs for the given hAtom keyword.<br>
     * 
     * @param elementName the hAtom keyword for which we want to get the documents.
     * @return the docs associated with the given hAtom keyword
     */
    public String getElementDocumentation(String elementName){
        return bundle.getString(elementName);
    }
}