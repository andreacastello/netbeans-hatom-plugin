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

import org.netbeans.modules.editor.NbEditorKit;

/**
 * Minimal subclass of NbEditorKit.<br> 
 * Used just for configuration purposes.<br>
 * 
 * @author Andrea Castello
 * @version 1.0
 */
public class HatomEditorKit extends NbEditorKit {

    public HatomEditorKit(){
        super();
    }
    
    /**
     * Returns the content tpe for wich the syntax highlight part is registered.
     * It's a static "text/html".<br>
     * 
     * @return content type
     */
    @Override
    public String getContentType() {
        return "text/html";
    }

}
