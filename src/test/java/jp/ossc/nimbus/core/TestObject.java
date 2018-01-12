/*
 * This software is distributed under following license based on modified BSD
 * style license.
 * ----------------------------------------------------------------------
 * 
 * Copyright 2003 The Nimbus Project. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE NIMBUS PROJECT ``AS IS'' AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN
 * NO EVENT SHALL THE NIMBUS PROJECT OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of the Nimbus Project.
 */
package jp.ossc.nimbus.core;

import java.util.*;
import java.net.*;

public class TestObject
 implements TestApplication, TestObjectMBean, java.io.Serializable{
    
    private static final long serialVersionUID = 8111675849576578483L;
    private int intValue;
    private String stringValue;
    private URL urlValue;
    private String[] stringArrayValue;
    private Properties propertiesValue;
    
    public void setInt(int val){
        intValue = val;
    }
    public int getInt(){
        return intValue;
    }
    
    public void setString(String a){
        stringValue = a;
    }
    public String getString(){
        return stringValue;
    }
    
    public void setURL(URL url){
        urlValue = url;
    }
    public URL getURL(){
        return urlValue;
    }
    
    public void setStringArray(String[] array){
        final Set set = new HashSet();
        for(int i = 0; i < array.length; i++){
            set.add(array[i]);
        }
        stringArrayValue = array;
    }
    public String[] getStringArray(){
        return stringArrayValue;
    }
    
    public void setProperties(Properties prop){
        propertiesValue = prop;
    }
    public Properties getProperties(){
        return propertiesValue;
    }
    
    public void call(String message){
    }
    
}