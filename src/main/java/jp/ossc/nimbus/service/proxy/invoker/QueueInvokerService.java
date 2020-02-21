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
package jp.ossc.nimbus.service.proxy.invoker;

import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.service.aop.InvocationContext;
import jp.ossc.nimbus.service.aop.Invoker;
import jp.ossc.nimbus.service.queue.Queue;

/**
 * {@link Queue}呼び出しインボーカ。<p>
 * Queueを使って、リモートのサービスを非同期に呼び出す。制約として、戻り値は返せないため、戻り値が必要なメソッドを呼び出すと、戻り値はnullとなる。<br>
 * リモートに、{@link jp.ossc.nimbus.service.proxy.QueueRemoteServiceServerService QueueRemoteServiceServerService}が配置されていなければならない。<br>
 *
 * @author M.Takata
 */
public class QueueInvokerService extends ServiceBase implements Invoker, QueueInvokerServiceMBean{
    
    private ServiceName queueServiceName;
    private Queue queue;
    
    public void setQueueServiceName(ServiceName name){
        queueServiceName = name;
    }
    public ServiceName getQueueServiceName(){
        return queueServiceName;
    }
    
    public void setQueue(Queue queue){
        this.queue = queue;
    }
    
    public void startService() throws Exception{
        if(queueServiceName != null){
            queue = (Queue)ServiceManagerFactory
                .getServiceObject(queueServiceName);
        }
        
        if(queue == null){
            throw new IllegalArgumentException("Queue is null.");
        }
    }
    
    /**
     * Queueを使って、非同期メソッド呼び出しを行う。<p>
     * 
     * @param context 呼び出しのコンテキスト情報
     * @return null
     * @exception Throwable Queueにpushした時に例外が発生した場合
     */
    public Object invoke(InvocationContext context) throws Throwable{
        queue.push(context);
        return null;
    }
}