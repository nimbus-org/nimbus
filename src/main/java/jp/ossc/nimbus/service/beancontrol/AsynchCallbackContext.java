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
package jp.ossc.nimbus.service.beancontrol;

import java.util.*;

import jp.ossc.nimbus.service.context.Context;

/**
 * 業務フロー非同期実行コールバックコンテキスト。<p>
 *
 * @author M.Takata
 */
public class AsynchCallbackContext{
    protected String flowName;
    protected Object input;
    protected Object output;
    protected Throwable throwable;
    protected Map context;
    protected Map threadContext;
    
    public AsynchCallbackContext(String flowName, Object input){
        this.flowName = flowName;
        this.input = input;
    }
    
    protected void setFlowName(String name){
        flowName = name;
    }
    
    public String getFlowName(){
        return flowName;
    }
    
    protected void setInput(Object input){
        this.input = input;
    }
    public Object getInput(){
        return input;
    }
    
    protected void setOutput(Object output){
        this.output = output;
    }
    public Object getOutput(){
        return output;
    }
    
    public void checkError() throws Throwable{
        if(throwable != null){
            throw throwable;
        }
    }
    
    protected void setThrowable(Throwable th){
        throwable = th;
    }
    public Throwable getThrowable(){
        return throwable;
    }
    
    protected void putThreadContextAll(Context context){
        if(threadContext != null){
            threadContext = new HashMap();
        }
        threadContext.putAll(context);
    }
    
    public Object getThreadContext(String key){
        return threadContext == null ? null : threadContext.get(key);
    }
    
    protected void setContext(String key, Object value){
        if(context != null){
            context = new HashMap();
        }
        context.put(key, value);
    }
    
    public Object getContext(String key){
        return context == null ? null : context.get(key);
    }
    
    public Map getContextMap(){
        if(context != null){
            context = new HashMap();
        }
        return context;
    }
}