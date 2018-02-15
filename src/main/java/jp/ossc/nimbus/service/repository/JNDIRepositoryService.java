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
package jp.ossc.nimbus.service.repository;

import java.io.*;
import java.util.*;
import javax.naming.*;

import jp.ossc.nimbus.core.*;

/**
 * RMIオブジェクトをJNDIサーバにバインドする{@link Repository}サービス。<p>
 * 
 * @author M.Takata
 */
public class JNDIRepositoryService extends ServiceBase
 implements JNDIRepositoryServiceMBean, Serializable{
    
    private static final long serialVersionUID = -586686693860861821L;
    
    private final Map registered = new Hashtable();
    private Properties environment;
    private transient InitialContext context;
    
    // JNDIRepositoryServiceMBeanのJavaDoc
    public Properties getEnvironment(){
        return environment;
    }
    
    // JNDIRepositoryServiceMBeanのJavaDoc
    public void setEnvironment(Properties env){
        environment = env;
    }
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始処理に失敗した場合
     */
    public void startService() throws Exception{
        context = getInitialContext();
    }
    
    /**
     * サービスの破棄処理を行う。<p>
     *
     * @exception Exception サービスの破棄処理に失敗した場合
     */
    public void destroyService() throws Exception{
        context = null;
    }
    
    // RepositoryのJavaDoc
    public Object get(String name){
        if(registered.containsKey(name)){
            return registered.get(name);
        }
        try{
            return context.lookup(name);
        }catch(NamingException e){
        }
        return null;
    }
    
    // RepositoryのJavaDoc
    public boolean register(String name, Object obj){
        boolean exists = false;
        try{
            context.lookup(name);
            exists = true;
        }catch(NamingException e){
            exists = false;
        }
        try{
            if(!exists){
                int index = -1;
                String tmp = name;
                Context tmpContext = context;
                while((index = tmp.indexOf('/')) != -1){
                    String subContext = tmp.substring(0, index);
                    try{
                        tmpContext = tmpContext.createSubcontext(subContext);
                    }catch(NameAlreadyBoundException already){
                        try{
                            tmpContext = (Context)tmpContext.lookup(subContext);
                        }catch(NamingException e){}
                    }
                    if(index != tmp.length() - 1){
                        tmp = tmp.substring(index + 1);
                    }else{
                        break;
                    }
                }
            }
            context.rebind(name, obj);
        }catch(NamingException e){
            e.printStackTrace();
            return false;
        }
        registered.put(name, obj);
        return true;
    }
    
    // RepositoryのJavaDoc
    public boolean unregister(String name){
        registered.remove(name);
        try{
            context.unbind(name);
        }catch(NamingException e){
            return false;
        }
        return true;
    }
    
    // RepositoryのJavaDoc
    public boolean isRegistered(String name){
        return get(name) != null;
    }
    
    // RepositoryのJavaDoc
    public Set nameSet(){
        return new HashSet(registered.keySet());
    }
    
    // RepositoryのJavaDoc
    public Set registeredSet(){
        final Set result = new HashSet();
        final Iterator registeredNames = nameSet().iterator();
        while(registeredNames.hasNext()){
            final Object obj = get((String)registeredNames.next());
            if(obj != null){
                result.add(obj);
            }
        }
        return result;
    }
    
    private InitialContext getInitialContext() throws NamingException{
        InitialContext context = null;
        if(environment == null){
            context = new InitialContext();
        }else{
            context = new InitialContext(environment);
        }
        return context;
    }
    
    private void readObject(java.io.ObjectInputStream in)
     throws IOException, ClassNotFoundException{
        in.defaultReadObject();
        try{
            context = getInitialContext();
        }catch(NamingException e){
            e.printStackTrace();
        }
    }
}
