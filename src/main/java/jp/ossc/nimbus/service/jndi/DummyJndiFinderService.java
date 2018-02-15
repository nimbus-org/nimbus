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
package jp.ossc.nimbus.service.jndi;

import java.util.*;
import javax.naming.*;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceNotFoundException;

/**
 * ダミーJndiファインダーサービス。<p>
 * 
 * @author M.Takata
 */
public class DummyJndiFinderService extends ServiceBase
 implements DummyJndiFinderServiceMBean, JndiFinder{
    
    private static final long serialVersionUID = -6086629800301585665L;

    private Map jndiMap;
    
    private String jndiPrefix;
    
    private ServiceName jndiFinderServiceName;
    private JndiFinder realJndiFinder;
    
    public void setJndiMapping(String jndiName, Object obj){
        jndiMap.put(jndiName, obj);
    }
    
    public void setJndiMappingServiceName(String jndiName, ServiceName name){
        jndiMap.put(jndiName, name);
    }
    
    public void setJndiFinderServiceName(ServiceName name){
        jndiFinderServiceName = name;
    }
    public ServiceName getJndiFinderServiceName(){
        return jndiFinderServiceName;
    }
    
    public void setPrefix(String prefix){
        jndiPrefix = prefix;
    }
    
    public String getPrefix(){
        return jndiPrefix;
    }
    
    public void createService() throws Exception{
        jndiMap = new HashMap();
    }
    
    public void startService() throws Exception{
        if(jndiFinderServiceName != null){
            realJndiFinder = (JndiFinder)ServiceManagerFactory.getServiceObject(jndiFinderServiceName);
        }
    }
    
    public Object lookup(String name) throws NamingException{
        String jndiName = name;
        if(jndiPrefix != null){
            if(name == null){
                jndiName = jndiPrefix;
            }else{
                jndiName = jndiPrefix + name;
            }
        }
        
        if(!jndiMap.containsKey(jndiName)){
            
            if(realJndiFinder != null){
                return realJndiFinder.lookup(name);
            }
            
            throw new NameNotFoundException(jndiName);
        }
        Object result = jndiMap.get(jndiName);
        if(result instanceof ServiceName){
            try{
                result = ServiceManagerFactory.getServiceObject((ServiceName)result);
            }catch(ServiceNotFoundException e){
                throw new NameNotFoundException(e.toString());
            }
        }
        return result;
    }
    
    public Object lookup() throws NamingException{
        return lookup(null);
    }
    
    public void clearCache(){
        if(realJndiFinder != null){
            realJndiFinder.clearCache();
        }
    }
    
    public void clearCache(String jndiName){
        if(realJndiFinder != null){
            realJndiFinder.clearCache(name);
        }
    }
}
