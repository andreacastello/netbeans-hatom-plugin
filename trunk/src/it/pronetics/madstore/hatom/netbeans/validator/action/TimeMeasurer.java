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


package it.pronetics.madstore.hatom.netbeans.validator.action;

/**
 * Simple class that measures a time interval.<br>
 * 
 * @author Andrea Castello
 * @version 1.0
 */
public class TimeMeasurer {

    // Time of measure start in milliseconds
    private long startTime;
    
    /**
     * Creates a new instance of TimeMeasurer that sets the measure start
     * time in the moment of object creation.<br>
     */
    public TimeMeasurer(){
        startTime = System.currentTimeMillis();
    }

    /**
     * 
     * @return the measure' start time in milliseconds
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * Sets the measure' start time, overwriting the start time set at object creation time.<br>
     * 
     * @param startTime the measure' start time
     */
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    /**
     * Returns the amount passed between the measure start time and the moment of
     * this method invocation.<br>
     * 
     * @return the measure's elapsed time.<br>
     */
    public long getElapsedTime(){
        return System.currentTimeMillis() - startTime;
    }
}