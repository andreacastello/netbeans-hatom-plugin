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

import java.util.HashMap;
import java.util.Map;

/**
 * Creates and stores the ValidationEngine objects.<br>
 * It also ensures that only one engine exist for every open document.<br>
 * 
 * @author Andrea Castello
 * @version 1.1
 */
public class ValidatorCache {

    // Map of the negines: it associates a Netbeans document element to a different engine instance
    private Map<String, ValidatorEngine> cache;
    
    // Unique instance of the cache.
    private static ValidatorCache instance;
    
    // Creates an instance of the cache an initializes the map where
    // validation engines are stored
    private ValidatorCache(){
        cache = new HashMap<String, ValidatorEngine>();
    }
    
    /**
     * Instance's access method.<br>
     * @return
     */
    public static synchronized ValidatorCache getInstance(){
        if (instance == null){
            instance = new ValidatorCache();
        }
        
        return instance;
    }
    
    /**
     * Retrieves the engine instance associated with the given <code>documentName</code>.<br>
     * @param documentName the document name associates with an engine instance
     * @return Validation engine for the given <code>documentName</code>
     */
    public ValidatorEngine getEngine(String documentName){
        
        ValidatorEngine engine = cache.get(documentName);
        if (engine == null){
            engine = createEngine(documentName);
        }
                
        return engine;
        
    }
    
    /**
     * Creates a validation engine for the document named <code>documentName</code> 
     * and stores it in the cache.<br>
     * @param documentName Name of the document for which engine will be created.
     * @return The newly created validation engine.
     */
    public ValidatorEngine createEngine(String documentName){
        
        ValidatorEngine engine = new ValidatorEngine();
        engine.setDocumentName(documentName);
        cache.put(documentName, engine);
        
        return engine;
    }
    
    /**
     * Removes from cache the engine object identified by the <code>documentName</code> key.<br>
     * @param documentName Name of the document for which engine will be created.
     * @return the validation engine that has been just removed form cache.
     */
    public ValidatorEngine remove(String documentName){
        return cache.remove(documentName);
    }
    
}
