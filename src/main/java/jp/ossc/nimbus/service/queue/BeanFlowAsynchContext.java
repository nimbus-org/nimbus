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

import java.rmi.RemoteException;

import jp.ossc.nimbus.service.beancontrol.interfaces.BeanFlowInvoker;
import jp.ossc.nimbus.service.beancontrol.BeanFlowMonitor;
import jp.ossc.nimbus.service.beancontrol.BeanFlowAsynchInvokeCallback;

/**
 * BeanFlow非同期実行コンテキスト。<p>
 *
 * @author M.Takata
 */
public class BeanFlowAsynchContext extends AsynchContext{
    
    private static final long serialVersionUID = -634815787855558999L;
    
    protected String flowName;
    protected BeanFlowInvoker invoker;
    protected BeanFlowMonitor monitor;
    protected BeanFlowAsynchInvokeCallback callback;
    
    public BeanFlowAsynchContext(){
    }
    
    public BeanFlowAsynchContext(String flowName){
        this.flowName = flowName;
    }
    
    public BeanFlowAsynchContext(String flowName, Object input){
        super(input);
        this.flowName = flowName;
    }
    
    public BeanFlowAsynchContext(String flowName, Object input, Queue queue){
        super(input, queue);
        this.flowName = flowName;
    }
    
    public BeanFlowAsynchContext(BeanFlowInvoker invoker, Object input, BeanFlowMonitor monitor){
        this(invoker, input, monitor, (Queue)null);
    }
    
    public BeanFlowAsynchContext(BeanFlowInvoker invoker, Object input, BeanFlowMonitor monitor, Queue queue){
        super(input, queue);
        this.invoker = invoker;
        this.monitor = monitor;
    }
    public BeanFlowAsynchContext(BeanFlowInvoker invoker, Object input, BeanFlowMonitor monitor, BeanFlowAsynchInvokeCallback callback){
        super(input);
        this.invoker = invoker;
        this.monitor = monitor;
        this.callback = callback;
    }
    
    public void setFlowName(String name){
        flowName = name;
    }
    
    public String getFlowName(){
        return invoker == null ? flowName : invoker.getFlowName();
    }
    
    public void setBeanFlowInvoker(BeanFlowInvoker invoker){
        this.invoker = invoker;
    }
    
    public BeanFlowInvoker getBeanFlowInvoker(){
        return invoker;
    }
    
    public void setBeanFlowMonitor(BeanFlowMonitor monitor){
        this.monitor = monitor;
    }
    
    public BeanFlowMonitor getBeanFlowMonitor(){
        return monitor;
    }
    
    public void response() throws Exception{
        if(callback == null){
            super.response();
        }else{
            if(!isCancel()){
                callback.reply(output, null);
            }
        }
    }
    
    public void setThrowable(Throwable th){
        super.setThrowable(th);
        if(callback != null && !isCancel()){
            try{
                callback.reply(null, th);
            }catch(RemoteException e){
            }
        }
    }
    
    public String toString(){
        StringBuilder buf = new StringBuilder(super.toString());
        buf.deleteCharAt(buf.length() - 1);
        buf.append(", flowName=").append(getFlowName());
        buf.append('}');
        return buf.toString();
    }
}