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
package jp.ossc.nimbus.service.queue;

import java.util.Map;
import java.util.HashMap;

import jp.ossc.nimbus.service.context.Context;

/**
 * 非同期実行コンテキスト。<p>
 *
 * @author M.Takata
 */
public class AsynchContext implements java.io.Serializable, Cloneable{
    
    private static final long serialVersionUID = 1707869608895589769L;
    
    protected Object input;
    protected Object output;
    protected Throwable throwable;
    protected Queue responseQueue;
    protected Map threadContext;
    protected boolean isCancel;
    protected long startTime;
    protected long timeout;
    
    public AsynchContext(){
    }
    
    public AsynchContext(Object input){
        this.input = input;
    }
    
    public AsynchContext(Object input, Queue queue){
        this.input = input;
        responseQueue = queue;
    }
    
    public void setInput(Object input){
        this.input = input;
    }
    public Object getInput(){
        return input;
    }
    
    public boolean isCancel(){
        return isCancel;
    }
    public void cancel(){
        isCancel = true;
    }
    
    public void setResponseQueue(Queue queue){
        responseQueue = queue;
    }
    public Queue getResponseQueue(){
        return responseQueue;
    }
    
    public void setOutput(Object output){
        this.output = output;
    }
    public Object getOutput(){
        return output;
    }
    
    public void startTimeout(long timeout){
        startTimeout(System.currentTimeMillis(), timeout);
    }
    
    public void startTimeout(long startTime, long timeout){
        this.startTime = startTime;
        this.timeout = timeout;
    }
    
    public boolean isEnabledTimeout(){
        return timeout > 0;
    }
    
    public long geTimeout(){
        return timeout;
    }
    
    public long getCurrentTimeout(){
        return getCurrentTimeout(System.currentTimeMillis());
    }
    
    public long getCurrentTimeout(long currentTime){
        return timeout > 0 ? (startTime + timeout - currentTime) : timeout;
    }
    
    public void response() throws Exception{
        if(responseQueue != null){
            responseQueue.push(this);
        }
    }
    
    public void checkError() throws Throwable{
        if(throwable != null){
            throw throwable;
        }
    }
    
    public void setThrowable(Throwable th){
        throwable = th;
    }
    public Throwable getThrowable(){
        return throwable;
    }
    
    public void putThreadContext(Object key, Object value){
        if(threadContext == null){
            threadContext = new HashMap();
        }
        threadContext.put(key, value);
    }
    
    public void putThreadContextAll(Context context){
        if(threadContext == null){
            threadContext = new HashMap();
        }
        threadContext.putAll(context);
    }
    
    public void applyThreadContext(Context context){
        if(threadContext != null){
            context.putAll(threadContext);
        }
    }
    
    public void clearThreadContext(){
        if(threadContext != null){
            threadContext = null;
        }
    }
    
    public void clear(){
        input = null;
        output = null;
        throwable = null;
        responseQueue = null;
        threadContext = null;
        isCancel = false;
        startTime = 0;
        timeout = 0;
    }
    
    public Object clone(){
        AsynchContext clone = null;
        try{
            clone = (AsynchContext)super.clone();
        }catch(CloneNotSupportedException e){
            return null;
        }
        clone.output = null;
        clone.throwable = null;
        if(threadContext != null){
            clone.threadContext = new HashMap();
            clone.threadContext.putAll(threadContext);
        }
        clone.isCancel = false;
        clone.startTime = 0;
        clone.timeout = 0;
        return clone;
    }
    
    public String toString(){
        StringBuilder buf = new StringBuilder(super.toString());
        buf.append('{');
        buf.append("input=").append(input);
        buf.append(", output=").append(output);
        buf.append(", throwable=").append(throwable);
        buf.append(", isCancel=").append(isCancel);
        buf.append(", startTime=").append(startTime);
        buf.append(", timeout=").append(timeout);
        buf.append('}');
        return buf.toString();
    }
}