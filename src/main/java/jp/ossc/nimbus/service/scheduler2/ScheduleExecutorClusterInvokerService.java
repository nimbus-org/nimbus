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

import java.util.*;
import java.lang.reflect.Method;

import jp.ossc.nimbus.service.aop.*;
import jp.ossc.nimbus.service.keepalive.KeepAliveChecker;
import jp.ossc.nimbus.service.proxy.RemoteServiceCallException;
import jp.ossc.nimbus.service.proxy.invoker.*;

/**
 * ScheduleExecutorクラスタInvokerサービス。<p>
 * 分散された{@link ScheduleExecutor}から、特定のScheduleExecutorを呼び出すクラスタInvokerサービスである。<br>
 * {@link Schedule#getExecutorKey()}が特定の実行キーを返す場合は、その実行キーに該当するScheduleExecutorを選んで呼び出す。<br>
 * nullを返す場合は、{@link jp.ossc.nimbus.service.keepalive.KeepAliveCheckerSelector KeepAliveCheckerSelector}が選択したScheduleExecutorを呼び出す。<br>
 * 
 * @author M.Takata
 */
public class ScheduleExecutorClusterInvokerService extends ClusterInvokerService
 implements ScheduleExecutorClusterInvokerServiceMBean{
    
    private static final long serialVersionUID = 353616770490347609L;
    protected static Method GET_KEY_METHOD;
    protected static final Object[] GET_KEY_METHOD_PARAM = new Object[0];
    
    static{
        try{
            GET_KEY_METHOD = ScheduleExecutor.class.getMethod("getKey", (Class[])null);
        }catch(NoSuchMethodException e){
            GET_KEY_METHOD = null;
        }
    }
    
    protected String key;
    protected String type;
    
    protected Map executeInvokerMap = Collections.synchronizedMap(new HashMap());
    
    // ScheduleExecutorClusterInvokerServiceMBeanのJavaDoc
    public void setKey(String key){
        this.key = key;
    }
    // ScheduleExecutorClusterInvokerServiceMBeanのJavaDoc
    public String getKey(){
        return key;
    }
    
    // ScheduleExecutorClusterInvokerServiceMBeanのJavaDoc
    public void setType(String type){
        this.type = type;
    }
    // ScheduleExecutorClusterInvokerServiceMBeanのJavaDoc
    public String getType(){
        return type;
    }
    
    /**
     * サービスの停止処理を行う。<p>
     *
     * @exception Exception サービスの停止処理に失敗した場合
     */
    public void stopService() throws Exception{
        super.stopService();
        executeInvokerMap.clear();
    }
    
    /**
     * {@link jp.ossc.nimbus.service.proxy.RemoteServerInvoker RemoteServerInvoker}インタフェースを実装したRMIオブジェクトを呼び出す。<p>
     * 
     * @param context 呼び出しのコンテキスト情報
     * @return 呼び出し結果の戻り値
     * @exception Throwable 呼び出し先で例外が発生した場合
     */
    public Object invoke(InvocationContext context) throws Throwable{
        MethodInvocationContext mthodContext = (MethodInvocationContext)context;
        Method method = mthodContext.getTargetMethod();
        Class[] paramTypes = method.getParameterTypes();
        if("execute".equals(method.getName())){
            Schedule schedule = (Schedule)mthodContext.getParameters()[0];
            try{
                KeepAliveCheckInvoker invoker = null;
                if(schedule.getExecutorKey() == null){
                    invoker = (KeepAliveCheckInvoker)selector.selectChecker();
                }else{
                    KeepAliveChecker[] checkers = selector.getSelectableCheckers();
                    if(checkers == null || checkers.length == 0){
                        throw new RemoteServiceCallException("No selectable KeepAliveCheckInvoker.");
                    }
                    
                    MethodInvocationContext tmpContext = new DefaultMethodInvocationContext(
                        mthodContext.getTargetObject(),
                        GET_KEY_METHOD,
                        GET_KEY_METHOD_PARAM
                    );
                    for(int i = 0; i < checkers.length; i++){
                        invoker = (KeepAliveCheckInvoker)checkers[i];
                        try{
                            String executorKey = (String)invoker.invoke(tmpContext);
                            if(schedule.getExecutorKey().equals(executorKey)){
                                break;
                            }
                        }catch(Throwable th){
                        }
                        invoker = null;
                    }
                    if(invoker == null){
                        invoker = (KeepAliveCheckInvoker)selector.selectChecker();
                    }
                }
                if(invoker == null){
                    throw new RemoteServiceCallException("No selectable KeepAliveCheckInvoker.");
                }
                executeInvokerMap.put(schedule.getId(), invoker);
                return invoker.invoke(context);
            }finally{
                executeInvokerMap.remove(schedule.getId());
            }
        }else if("controlState".equals(method.getName())){
            String id= (String)mthodContext.getParameters()[0];
            KeepAliveCheckInvoker invoker = (KeepAliveCheckInvoker)executeInvokerMap.get(id);
            if(invoker != null){
                return invoker.invoke(context);
            }else{
                KeepAliveChecker[] checkers = selector.getSelectableCheckers();
                if(checkers == null || checkers.length == 0){
                    throw new RemoteServiceCallException("No selectable KeepAliveCheckInvoker.");
                }
                
                for(int i = 0; i < checkers.length; i++){
                    invoker = (KeepAliveCheckInvoker)checkers[i];
                    try{
                        Boolean ret = (Boolean)invoker.invoke(context);
                        if(ret != null && ret.booleanValue()){
                            return Boolean.TRUE;
                        }
                    }catch(ScheduleStateControlException e){
                        throw e;
                    }catch(Throwable th){
                    }
                }
                return Boolean.FALSE;
            }
            
        }else if("getKey".equals(method.getName())){
            return key == null ? getServiceName() : key;
        }else if("getType".equals(method.getName())){
            if(type == null){
                KeepAliveChecker[] checkers = selector.getSelectableCheckers();
                if(checkers == null || checkers.length == 0){
                    throw new RemoteServiceCallException("No selectable KeepAliveCheckInvoker.");
                }
                return ((KeepAliveCheckInvoker)checkers[0]).invoke(context);
            }else{
                return type;
            }
        }else if("getScheduleManager".equals(method.getName())){
            return null;
        }else if("setScheduleManager".equals(method.getName())){
            return null;
        }else if("toString".equals(method.getName()) && paramTypes.length == 0){
            return toString();
        }else if("hashCode".equals(method.getName()) && paramTypes.length == 0){
            return new Integer(hashCode());
        }else if("equals".equals(method.getName())
            && paramTypes.length == 1
            && paramTypes[0].equals(java.lang.Object.class)){
            return equals(mthodContext.getParameters()[0]) ? Boolean.TRUE : Boolean.FALSE;
        }else{
            return super.invoke(context);
        }
    }
}
