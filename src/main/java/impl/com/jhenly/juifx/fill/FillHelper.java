/** Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License. */
package impl.com.jhenly.juifx.fill;

import com.jhenly.juifx.control.Fillable;

import impl.com.jhenly.juifx.util.Utils;


public class FillHelper {
    private static final FillHelper theInstance;
    private static FillAccessor fillAccessor;
    
    static {
        theInstance = new FillHelper();
        Utils.forceInit(Fill.class);
    }
    
    protected FillHelper() {}
    
    private static FillHelper getInstance() { return theInstance; }
    
    public static void initHelper(Fill fill) {
        setHelper(fill, getInstance());
    }
    
    protected static FillHelper getHelper(Fill fill) {
        
        FillHelper helper = fillAccessor.getHelper(fill);
        if (helper == null) {
            String fillType;
            if (fill instanceof Fill) {
                fillType = "Fill";
            } else {
                fillType = "Fill";
            }
            
            throw new UnsupportedOperationException(
                "Applications should not extend the " + fillType + " class directly.");
        }
        return helper;
    }
    
    protected static void setHelper(Fill fill, FillHelper fillHelper) {
        fillAccessor.setHelper(fill, fillHelper);
    }
    
    public static boolean fillHasSpecial(Fill fill) { return fillAccessor.fillHasSpecial(fill); }
    
    public static Fill replaceSpecialsInFill(Fill fill, Fillable fable) {
        return fillAccessor.replaceSpecialsInFill(fill, fable);
    }
    
    /**************************************************************************
     *                                                                        *
     * Fill Accessor                                                          *
     *                                                                        *
     *************************************************************************/
    
    public static void setFillAccessor(final FillAccessor newAccessor) {
        if (fillAccessor != null) { throw new IllegalStateException(); }
        
        fillAccessor = newAccessor;
    }
    
    public static FillAccessor getFillAccessor() {
        if (fillAccessor == null) { throw new IllegalStateException(); }
        
        return fillAccessor;
    }
    
    public interface FillAccessor {
        FillHelper getHelper(Fill fill);
        void setHelper(Fill fill, FillHelper fillHelper);
        
        boolean fillHasSpecial(Fill fill);
        Fill replaceSpecialsInFill(Fill fill, Fillable fable);
        
    }
    
}
