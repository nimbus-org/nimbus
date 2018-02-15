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

import java.io.*;

import jp.ossc.nimbus.core.*;

/**
 * スレッドローカルQueueサービス。<p>
 *
 * @author M.Takata
 */
public class ThreadLocalQueueService extends ServiceBase
 implements Queue, ThreadLocalQueueServiceMBean,
            Serializable{
    
    private static final long serialVersionUID = 6381359533066826317L;
    
    private DefaultQueueFactoryService defaultQueueFactory;
    private FactoryService queueFactory;
    private ServiceName queueFactoryServiceName;
    private ThreadLocal threadLocalQueues;
    
    public void setQueueFactoryServiceName(ServiceName name){
        queueFactoryServiceName = name;
    }
    public ServiceName getQueueFactoryServiceName(){
        return queueFactoryServiceName;
    }
    
    public void createService() throws Exception{
        threadLocalQueues = new ThreadLocal(){
            protected synchronized Object initialValue(){
                return queueFactory.newInstance();
            }
        };
    }
    
    public void startService() throws Exception{
        // QueueFactoryサービスの生成または取得
        if(queueFactoryServiceName != null) {
            queueFactory =(FactoryService)ServiceManagerFactory.getService(getQueueFactoryServiceName());
        }
        if(queueFactory == null) {
            if(getDefaultQueueFactoryService() == null){
                final DefaultQueueFactoryService defaultQueueFactory
                     = new DefaultQueueFactoryService();
                defaultQueueFactory.create();
                defaultQueueFactory.start();
                setDefaultQueueFactoryService(defaultQueueFactory);
            }else{
                getDefaultQueueFactoryService().start();
            }
            queueFactory = getDefaultQueueFactoryService();
        }
    }
    
    public void stopService() throws Exception{
        // Queueサービスを無名サービスとして生成している場合、
        // そのサービスを停止する
        if(getQueueFactoryService() == getDefaultQueueFactoryService()){
            getDefaultQueueFactoryService().stop();
        }
    }
    
    public void destroyService() throws Exception{
        
        // Queueサービスを無名サービスとして生成している場合、
        // そのサービスを破棄する
        if(getQueueFactoryService() == getDefaultQueueFactoryService()
            && getDefaultQueueFactoryService() != null){
            getDefaultQueueFactoryService().destroy();
            setDefaultQueueFactoryService(null);
        }
        
        threadLocalQueues = null;
    }
    
    protected void setQueueFactoryService(FactoryService queueFactory){
        this.queueFactory = queueFactory;
    }
    
    protected FactoryService getQueueFactoryService(){
        return queueFactory;
    }
    
    protected DefaultQueueFactoryService getDefaultQueueFactoryService(){
        return defaultQueueFactory;
    }
    
    protected void setDefaultQueueFactoryService(DefaultQueueFactoryService queueFactory){
        defaultQueueFactory = queueFactory;
    }
    
    protected Queue getThreadLocalQueue(){
        return (Queue)threadLocalQueues.get();
    }
    
    public void push(Object item){
        getThreadLocalQueue().push(item);
    }
    public boolean push(Object item, long timeout){
        return getThreadLocalQueue().push(item, timeout);
    }
    public Object get(){
        return getThreadLocalQueue().get();
    }
    public Object get(long timeOutMs){
        return getThreadLocalQueue().get(timeOutMs);
    }
    public Object peek(){
        return getThreadLocalQueue().peek();
    }
    public Object peek(long timeOutMs){
        return getThreadLocalQueue().peek(timeOutMs);
    }
    public Object remove(Object item){
        return getThreadLocalQueue().remove(item);
    }
    public void clear(){
        getThreadLocalQueue().clear();
    }
    public int size(){
        return getThreadLocalQueue().size();
    }
    public void accept(){
        getThreadLocalQueue().accept();
    }
    public void release(){
        getThreadLocalQueue().release();
    }
    
    public long getCount(){
        return getThreadLocalQueue().getCount();
    }
    
    public int getWaitCount(){
        return getThreadLocalQueue().getWaitCount();
    }
    
    public void clearAll(){
        threadLocalQueues = new ThreadLocal();
    }
}