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

public class TestService
 implements TestApplication, TestServiceMBean, java.io.Serializable{
    
    private static final long serialVersionUID = -8267380522487579430L;
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
    
    private int state = DESTROYED;
    private String name;
    private transient ServiceManager manager;
    private String managerName;
    
    public void setServiceName(String name){
        this.name = name;
    }
    
    public String getServiceName(){
        return name;
    }
    
    public int getState(){
        return state;
    }
    
    public String getStateString(){
        return STATES[state];
    }
    
    public void create() throws Exception{
        if(state == CREATED || state == CREATING || state == STARTED){
            return;
        }
        
        state = CREATING;
        
        if(manager == null && managerName != null){
            manager = ServiceManagerFactory.findManager(managerName);
        }
        if(manager != null && getServiceName() != null){
            manager.registerService(getServiceName(), this);
        }
        
        state = CREATED;
    }
    
    public void start() throws Exception{
        if(state == DESTROYED || state == FAILED){
            throw new IllegalStateException(
                "State is illegal : " + STATES[state]
            );
        }
        if(state == STARTED || state == STARTING){
            return;
        }
        
        state = STARTING;
        
        state = STARTED;
    }
    
    public void stop(){
        if(state != STARTED){
            return;
        }
        
        state = STOPPING;
        
        state = STOPPED;
    }
    
    public void destroy(){
        if(state == DESTROYED || state == DESTROYING){
            return;
        }
        if(state != STOPPED){
            stop();
        }
        
        state = DESTROYING;
        
        if(manager == null && managerName != null){
            manager = ServiceManagerFactory.findManager(managerName);
        }
        if(manager != null){
            manager.unregisterService(getServiceName());
        }
        
        state = DESTROYED;
    }
    
    public String getServiceManagerName(){
        return managerName;
    }
    
    public void setServiceManagerName(String name){
        managerName = name;
    }
    
    public ServiceName getServiceNameObject(){
        return new ServiceName(getServiceManagerName(), getServiceName());
    }
    
    public void call(String message){
    }
}