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

import java.lang.reflect.*;
import java.util.*;

import jp.ossc.nimbus.beans.ServiceNameEditor;
import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.context.Context;
import jp.ossc.nimbus.service.interpreter.*;
import jp.ossc.nimbus.service.proxy.invoker.ClusterInvokerServiceMBean;
import jp.ossc.nimbus.util.converter.*;

/**
 * サービス呼び出しスケジュール実行。<p>
 * 指定されたサービスのメソッドを実行する。<br>
 * {@link Schedule#setTaskName(String)}に、サービスマネージャ名#サービス名.メソッド名(引数型1,...引数型n)を指定する。また、{@link Schedule#setInput(Object)}には、引数配列を生成するスクリプトを指定する。<br>
 * また、呼び出し対象のサービスが、プロキシで{@link jp.ossc.nimbus.service.proxy.invoker.ClusterInvokerService ClusterInvokerService}を使用している場合に限り、サービス名の後ろに"*"を付与することで、ブロードキャスト呼び出しを指定できる。<br>
 *
 * @author M.Takata
 */
public class ServiceCallScheduleExecutorService
 extends AbstractScheduleExecutorService
 implements ScheduleExecutor, ServiceCallScheduleExecutorServiceMBean{
    
    {
        type = DEFAULT_EXECUTOR_TYPE;
    }
    
    private ServiceName interpreterServiceName;
    private Interpreter interpreter;
    
    private ServiceName threadContextServiceName;
    private Context threadContext;
    
    public void setInterpreterServiceName(ServiceName name){
        interpreterServiceName = name;
    }
    public ServiceName getInterpreterServiceName(){
        return interpreterServiceName;
    }
    
    public void setThreadContextServiceName(ServiceName name){
        threadContextServiceName = name;
    }
    public ServiceName getThreadContextServiceName(){
        return threadContextServiceName;
    }
    
    public void setInterpreter(Interpreter interpreter){
        this.interpreter = interpreter;
    }
    
    public void setThreadContext(Context context){
        this.threadContext = context;
    }
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始処理に失敗した場合
     */
    public void startService() throws Exception{
        
        if(interpreterServiceName != null){
            interpreter = (Interpreter)ServiceManagerFactory
                .getServiceObject(interpreterServiceName);
        }
        if(interpreter == null){
            ScriptEngineInterpreterService interpreter = new ScriptEngineInterpreterService();
            interpreter.create();
            interpreter.start();
            this.interpreter = interpreter;
        }
        if(threadContextServiceName != null){
            threadContext = (Context)ServiceManagerFactory
                .getServiceObject(threadContextServiceName);
        }
    }
    
    /**
     * 指定されたスケジュールの呼び出し対象のサービスが存在し、メソッドが存在するかチェックする。<p>
     *
     * @param schedule スケジュール
     * @exception Exception 指定されたスケジュールの呼び出し対象のサービスが存在しない、またはメソッドが存在しない場合
     */
    protected void checkPreExecute(Schedule schedule) throws Exception{
        parseTaskName(schedule);
    }
    
    private Object[] parseTaskName(Schedule schedule) throws Exception{
        String taskName = schedule.getTaskName();
        if(taskName == null){
            throw new IllegalArgumentException("TaskName is null. schedule=" + schedule);
        }
        int index = taskName.lastIndexOf("(");
        if(index == -1){
            index = taskName.lastIndexOf(".");
        }else{
            index = taskName.lastIndexOf(".", index);
        }
        if(index == -1){
            throw new IllegalArgumentException("TaskName is illegal format. taskName=" + taskName);
        }
        String serviceNameStr = taskName.substring(0, index);
        String methodSigniture = taskName.substring(index + 1);
        Boolean isBroadcast = null;
        if(serviceNameStr.charAt(serviceNameStr.length() - 1) == '*'){
            isBroadcast = Boolean.TRUE;
            serviceNameStr = serviceNameStr.substring(0, serviceNameStr.length() - 1);
        }
        ServiceNameEditor serviceNameEditor = new ServiceNameEditor();
        serviceNameEditor.setServiceManagerName(getServiceManagerName());
        serviceNameEditor.setAsText(serviceNameStr);
        final ServiceName serviceName = (ServiceName)serviceNameEditor.getValue();
        if(!ServiceManagerFactory.isRegisteredService(serviceName)){
            throw new IllegalArgumentException("Service is not found. serviceName=" + serviceName);
        }
        Object service = ServiceManagerFactory.getServiceObject(serviceName);
        index = methodSigniture.indexOf("(");
        String methodName = null;
        List argTypes = null;
        if(index != -1){
            methodName = methodSigniture.substring(0, index);
            final int lastIndex = methodSigniture.lastIndexOf(")");
            if(index > lastIndex || lastIndex == -1){
                throw new IllegalArgumentException("TaskName is illegal format. taskName=" + taskName);
            }
            final String argTypesStr = methodSigniture.substring(index + 1, lastIndex).trim();
            String[] argTypesStrArray = argTypesStr.split(",");
            try{
                for(int i = 0; i < argTypesStrArray.length; i++){
                    if(argTypes == null){
                        argTypes = new ArrayList();
                    }
                    argTypes.add(Utility.convertStringToClass(argTypesStrArray[i].trim()));
                }
            }catch(ClassNotFoundException e){
                throw new IllegalArgumentException("TaskName is illegal. taskName=" + taskName, e);
            }
        }else{
            methodName = methodSigniture;
        }
        Method method = null;
        try{
            method = service.getClass().getMethod(methodName, (Class[])(argTypes == null ? null : argTypes.toArray(new Class[argTypes.size()])));
        }catch(Exception e){
            throw new IllegalArgumentException("TaskName is illegal. taskName=" + taskName, e);
        }
        return new Object[]{service, method, isBroadcast};
    }
    
    protected void convertInput(Schedule schedule) throws ConvertException{
        super.convertInput(schedule);
        if(schedule.getInput() != null){
            try{
                schedule.setInput(interpreter.evaluate(schedule.getInput().toString()));
            }catch(Exception e){
                throw new ConvertException(e);
            }
        }
    }
    
    public boolean controlState(String id, int cntrolState) throws ScheduleStateControlException {
        return false;
    }
    
    protected Schedule executeInternal(Schedule schedule) throws Throwable{
        final Object[] targets = parseTaskName(schedule);
        final Object service = targets[0];
        final Method method = (Method)targets[1];
        final Boolean isBroadcast = (Boolean)targets[2];
        Object[] args = null;
        if(method.getParameterCount() != 0){
            final Object input = schedule.getInput();
            if(method.getParameterCount() == 1){
                args = new Object[]{input};
            }else if(input != null){
                if(input instanceof List){
                    args = ((List)input).toArray();
                }else if(input.getClass().isArray()){
                    args = (Object[])input;
                }
            }
        }
        if(threadContext != null){
            if(isBroadcast == null){
                threadContext.remove(ClusterInvokerServiceMBean.CONTEXT_KEY_INVOKE_BROADCAST);
            }else{
                threadContext.put(ClusterInvokerServiceMBean.CONTEXT_KEY_INVOKE_BROADCAST, Boolean.TRUE);
            }
        }
        Object ret = method.invoke(service, args);
        if(ret != null){
            BeanJSONConverter converter = new BeanJSONConverter();
            converter.setIgnoreExceptionProperty(true);
            try{
                ret = new StringStreamConverter().convertToObject(converter.convertToStream(ret));
            }catch(Exception e){
            }
            schedule.setOutput(ret);
        }
        return schedule;
    }
}