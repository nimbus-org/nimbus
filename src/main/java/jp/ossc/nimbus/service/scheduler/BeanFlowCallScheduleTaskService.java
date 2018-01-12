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
package jp.ossc.nimbus.service.scheduler;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.beancontrol.interfaces.*;

/**
 * BeanFlow呼び出しスケジュールタスク。<p>
 *
 * @author M.Takata
 */
public class BeanFlowCallScheduleTaskService extends ServiceBase
 implements ScheduleTask, BeanFlowCallScheduleTaskServiceMBean{
    
    private static final long serialVersionUID = 7633274480901701353L;
    
    /**
     * {@link BeanFlowInvokerFactory}サービスのサービス名。<p>
     */
    protected ServiceName beanFlowInvokerFactoryServiceName;
    
    /**
     * {@link BeanFlowInvokerFactory}。<p>
     */
    protected BeanFlowInvokerFactory beanFlowInvokerFactory;
    
    /**
     * 実行する業務フロー名。<p>
     */
    protected String beanFlowName;
    
    /**
     * 実行する業務フローに渡す入力オブジェクト。<p>
     */
    protected Object beanFlowInput;
    
    // BeanFlowCallScheduleTaskServiceMBeanのJavaDoc
    public void setBeanFlowInvokerFactoryServiceName(ServiceName name){
        beanFlowInvokerFactoryServiceName = name;
    }
    // BeanFlowCallScheduleTaskServiceMBeanのJavaDoc
    public ServiceName getBeanFlowInvokerFactoryServiceName(){
        return beanFlowInvokerFactoryServiceName;
    }
    
    // BeanFlowCallScheduleTaskServiceMBeanのJavaDoc
    public void setBeanFlowName(String name){
        beanFlowName = name;
    }
    // BeanFlowCallScheduleTaskServiceMBeanのJavaDoc
    public String getBeanFlowName(){
        return beanFlowName;
    }
    
    // BeanFlowCallScheduleTaskServiceMBeanのJavaDoc
    public void setBeanFlowInput(Object in){
        beanFlowInput = in;
    }
    // BeanFlowCallScheduleTaskServiceMBeanのJavaDoc
    public Object getBeanFlowInput(){
        return beanFlowInput;
    }
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始処理に失敗した場合
     */
    public void startService() throws Exception{
        if(beanFlowInvokerFactoryServiceName != null){
            beanFlowInvokerFactory
                 = (BeanFlowInvokerFactory)ServiceManagerFactory
                    .getServiceObject(beanFlowInvokerFactoryServiceName);
        }
        if(beanFlowInvokerFactory == null){
            throw new IllegalArgumentException(
                "beanFlowInvokerFactory is null"
            );
        }
        if(beanFlowName == null){
            throw new IllegalArgumentException(
                "beanFlowName must be specified."
            );
        }
    }
    
    /**
     * {@link BeanFlowInvokerFactory}を設定する。<p>
     *
     * @param factory BeanFlowInvokerFactory
     */
    public void setBeanFlowInvokerFactory(BeanFlowInvokerFactory factory){
        beanFlowInvokerFactory = factory;
    }
    
    // ScheduleTaskのJavaDoc
    public void run() throws Exception{
        final BeanFlowInvoker flowInvoker = beanFlowInvokerFactory
            .createFlow(beanFlowName);
        flowInvoker.invokeFlow(beanFlowInput);
    }
}