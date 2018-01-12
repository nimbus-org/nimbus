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
package jp.ossc.nimbus.service.scheduler2;

import java.util.Date;
import java.util.Calendar;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.beancontrol.interfaces.*;


/**
 * BeanFlow条件スケジュール作成サービス。<p>
 * スケジュールの作成有無の判定をBeanFlowに委譲する。<br>
 *
 * @author M.Takata
 */
public class BeanFlowConditionScheduleMakerService
 extends DefaultScheduleMakerService
 implements jp.ossc.nimbus.service.scheduler.DateEvaluator, BeanFlowConditionScheduleMakerServiceMBean{
    
    private static final long serialVersionUID = 9128789021473711234L;
    protected ServiceName beanFlowInvokerFactoryServiceName;
    protected BeanFlowInvokerFactory beanFlowInvokerFactory;
    
    protected String flowName;
    
    // BeanFlowConditionScheduleMakerServiceMBeanのJavaDoc
    public void setBeanFlowInvokerFactoryServiceName(ServiceName name){
        beanFlowInvokerFactoryServiceName = name;
    }
    // BeanFlowConditionScheduleMakerServiceMBeanのJavaDoc
    public ServiceName getBeanFlowInvokerFactoryServiceName(){
        return beanFlowInvokerFactoryServiceName;
    }
    
    // BeanFlowConditionScheduleMakerServiceMBeanのJavaDoc
    public void setFlowName(String name){
        flowName = name;
    }
    // BeanFlowConditionScheduleMakerServiceMBeanのJavaDoc
    public String getFlowName(){
        return flowName;
    }
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始処理に失敗した場合
     */
    public void startService() throws Exception{
        
        if(beanFlowInvokerFactoryServiceName != null){
            beanFlowInvokerFactory = (BeanFlowInvokerFactory)ServiceManagerFactory
                .getServiceObject(beanFlowInvokerFactoryServiceName);
        }
        if(beanFlowInvokerFactory == null){
            throw new IllegalArgumentException("BeanFlowInvokerFactory is null.");
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
    
    /**
     * {@link BeanFlowInvokerFactory}を取得する。<p>
     *
     * @return BeanFlowInvokerFactory
     */
    public BeanFlowInvokerFactory getBeanFlowInvokerFactory(){
        return beanFlowInvokerFactory;
    }
    
    /**
     * この日付で、スケジュールを作成する必要があるかどうかを判定する。<p>
     * {@link #setFlowName(String)}で設定されたBeanFlow名または、{@link ScheduleMaster#getScheduleType()}で取得されるスケジュール種別名のBeanFlowを実行して、その戻り値でスケジュールの作成有無を判断する。<br>
     * BeanFlowの引数には、配列で、このメソッドの引数であるdateとmasterを渡す。<br>
     *
     * @param date 作成日
     * @param master スケジュールマスタ
     * @return trueの場合、作る必要がある
     * @exception ScheduleMakeException 判定に失敗した場合
     */
    protected boolean isNecessaryMake(Date date, ScheduleMaster master)
     throws ScheduleMakeException{
        final String conditionFlowName = flowName == null ? master.getScheduleType() : flowName;
        final BeanFlowInvoker invoker = beanFlowInvokerFactory.createFlow(
            conditionFlowName
        );
        if(invoker == null){
            throw new ScheduleMakeException("BeanFlow is not found : " + conditionFlowName);
        }
        
        boolean result = false;
        try{
            Object ret = invoker.invokeFlow(new Object[]{date, master});
            if(ret instanceof Boolean){
                result = ((Boolean)ret).booleanValue();
            }else{
                throw new ScheduleMakeException("Return of BeanFlow is not boolean : " + ret);
            }
        }catch(Exception e){
            throw new ScheduleMakeException(e);
        }
        return result;
    }
    
    public boolean equalsDate(String key, Calendar cal) throws Exception{
        final String conditionFlowName = flowName == null ? key : flowName;
        final BeanFlowInvoker invoker = beanFlowInvokerFactory.createFlow(
            conditionFlowName
        );
        if(invoker == null){
            throw new ScheduleMakeException("BeanFlow is not found : " + conditionFlowName);
        }
        
        boolean result = false;
        try{
            Object ret = invoker.invokeFlow(new Object[]{cal.getTime()});
            if(ret instanceof Boolean){
                result = ((Boolean)ret).booleanValue();
            }else{
                throw new ScheduleMakeException("Return of BeanFlow is not boolean : " + ret);
            }
        }catch(Exception e){
            throw new ScheduleMakeException(e);
        }
        return result;
    }
}