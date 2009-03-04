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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;
import org.openide.util.Exceptions;

/**
 * Singleton class that loads the <b>hAtom</b> keyword list from a file into
 * a cache that will be used in the autocompletion task.<br>
 * The file that stores the keywords is the <code>tag.properties</code> contained in the module.<br>
 * 
 * @author Andrea Castello
 * @version 1.0
 */
public class TagCache {

    
    // Path of the properties file where hAtom keywords are stored. 
    private final static String PROPERTIES_CPATH = "it.pronetics.madstore.hatom.netbeans.syntax.tags";
    // Key of the module name property
    private final static String NB_MODULE_NAME_KEY = "OpenIDE-Module-Name";
    // Used to access the module properties file.<br>
    private ResourceBundle bundle = null;
    // Keywords list.
    private List<String> tagList = null;
    // Single class instance.
    private static TagCache instance = null;

    /**
     * When it is called by the static <code>getCache</code> method, it loads the list of the hAtom keywords
     * into its unique instance.<br> 
     * <br>
     */
    private TagCache(){
        loadTags();
    }
    
    /** 
     * Gets the single instance for this object.<br>
     * @return the keyword cache.
     */
    public static synchronized TagCache getCache(){
        if (instance == null){
            instance = new TagCache();
        }
        
        return instance;
    }

    /**
     * Loads the hAtom keyword list from the module's property file.<br>
     */
    private void loadTags() {
        try {
            bundle = ResourceBundle.getBundle(PROPERTIES_CPATH);
            Enumeration<String> en = bundle.getKeys();
            String key = null;
            setTagList(new ArrayList<String>());
            for (; en.hasMoreElements(); ) {
                key = en.nextElement();
                // We don't load the module name property
                if (key!=null && !key.equals(NB_MODULE_NAME_KEY)){
                    getTagList().add(bundle.getString(key).trim());
                }
            }
        }
        catch(Exception ex){
            Exceptions.attachMessage(ex, "Unable to load tag list for code completion");
            ex.printStackTrace(); 
        }
    }

    /**
     * Returns the complete hAtom keyword list.<br>
     * @return 
     */
    public List<String> getTagList() {
        return tagList;
    }

    /**
     * Sets the hAtom keyword list.<br>
     * @param tagList keyword list.
     */
    private void setTagList(List<String> tagList) {
        this.tagList = tagList;
    }
}
