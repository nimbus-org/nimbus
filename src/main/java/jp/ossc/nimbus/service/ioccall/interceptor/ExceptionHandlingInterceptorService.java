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
package jp.ossc.nimbus.service.ioccall.interceptor;

import java.util.*;
import java.lang.reflect.InvocationTargetException;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.beans.*;
import jp.ossc.nimbus.ioc.*;
import jp.ossc.nimbus.util.*;
import jp.ossc.nimbus.service.aspect.interfaces.InterceptorException;
import jp.ossc.nimbus.service.aspect.interfaces.TargetCheckedException;
import jp.ossc.nimbus.service.aspect.interfaces.TargetUncheckedException;
import jp.ossc.nimbus.service.aop.InvocationContext;
import jp.ossc.nimbus.service.aop.MethodInvocationContext;

/**
 * 例外処理インターセプタ。<p>
 *
 * @author M.Takata
 */
public class ExceptionHandlingInterceptorService extends ServiceBase
 implements ExceptionHandlingInterceptorServiceMBean,
            jp.ossc.nimbus.service.aspect.interfaces.Interceptor,
            jp.ossc.nimbus.service.aop.Interceptor{
    
    private static final long serialVersionUID = 346215587946433868L;
    
    private static final String SERVLET_EXCEPTION_NAME = "javax.servlet.ServletException";
    private static final String GET_ROOT_CAUSE_METHOD = "getRootCause";
    private static final String JMS_EXCEPTION_NAME = "javax.jms.JMSException";
    private static final String GET_LINKED_EXCEPTION_METHOD = "getLinkedException";
    
    protected Properties exceptionAndHandlerMapping;
    protected ClassMappingTree exceptionMapForHandler;
    protected ServiceName defaultExceptionHandlerServiceName;
    protected ExceptionHandler defaultExceptionHandler;
    
    // ExceptionHandlingInterceptorServiceMBeanのJavaDoc
    public void setExceptionAndHandlerMapping(Properties map){
        exceptionAndHandlerMapping = map;
    }
    
    // ExceptionHandlingInterceptorServiceMBeanのJavaDoc
    public Properties getExceptionAndHandlerMapping(){
        return exceptionAndHandlerMapping;
    }
    
    // ExceptionHandlingInterceptorServiceMBeanのJavaDoc
    public void setDefaultExceptionHandlerServiceName(ServiceName name){
        defaultExceptionHandlerServiceName = name;
    }
    
    // ExceptionHandlingInterceptorServiceMBeanのJavaDoc
    public ServiceName getDefaultExceptionHandlerServiceName(){
        return defaultExceptionHandlerServiceName;
    }
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始に失敗した場合
     */
    public void startService() throws Exception{
        if(exceptionAndHandlerMapping != null){
            exceptionMapForHandler = new ClassMappingTree();
            final ClassLoader loader = NimbusClassLoader.getInstance();
            final ServiceNameEditor editor = new ServiceNameEditor();
            editor.setServiceManagerName(getServiceManagerName());
            final Iterator exNames
                 = exceptionAndHandlerMapping.keySet().iterator();
            while(exNames.hasNext()){
                final String exName = (String)exNames.next();
                final Class clazz = Class.forName(exName, true, loader);
                final String name
                     = (String)exceptionAndHandlerMapping.get(exName);
                editor.setAsText(name);
                final ServiceName serviceName = (ServiceName)editor.getValue();
                exceptionMapForHandler.add(
                    clazz,
                    ServiceManagerFactory.getServiceObject(serviceName)
                );
            }
        }
        if(defaultExceptionHandlerServiceName != null){
            defaultExceptionHandler = (ExceptionHandler)ServiceManagerFactory
                .getServiceObject(defaultExceptionHandlerServiceName);
        }
    }
    
    /**
     * 指定された例外のハンドラクラスをマップから見つけて返す。<p>
     *
     * @param th 例外(ハンドリング対象の例外を含んでいる)
     * @return 例外ハンドラ
     */
    protected ExceptionHandler getTargetExceptionHandlerCause(Throwable th){
        ExceptionHandler handler = (ExceptionHandler)getTargetHandlerCause(exceptionMapForHandler, th);
        return handler == null ? defaultExceptionHandler : handler;
    }
    
    /**
     * 指定された例外のハンドラをマップから見つけて返す。<p>
     *
     * @param handlers ハンドラのマップ
     * @param th 例外(ハンドリング対象の例外を含んでいる)
     * @return 例外ハンドラ
     */
    protected Object getTargetHandlerCause(ClassMappingTree handlers, Throwable th){
        if(handlers == null){
            return null;
        }
        // 例外クラスに関連付いている例外ハンドラを取得
        Object handler = handlers.getValue(th.getClass());
        if(handler != null){
            return handler;
        }
        Throwable cause = getCause(th);
        return cause == null ? null : getTargetHandlerCause(handlers, cause);
    }
    
    /**
     * 指定された例外から、ハンドリング対象の例外を取り出す。<p>
     * 
     * @param handlers ハンドラのマップ
     * @param th 例外
     * @return ハンドリング対象の例外
     */
    protected Throwable getTargetException(ClassMappingTree handlers, Throwable th) {
        if(handlers == null){
            return th;
        }
        // 例外クラスに関連付いている例外ハンドラを取得
        Object handler = handlers.getValue(th.getClass());
        if(handler != null){
            return th;
        }
        
        Throwable cause = getCause(th);
        return cause == null ? null : getTargetException(handlers, cause);
    }
    
    /**
     * 指定された例外から原因を取得する。<p>
     *
     * @param th 例外
     * @return 原因
     */
    protected Throwable getCause(Throwable th){
        Throwable cause = null;
        String thClassName = th.getClass().getName();
        if(thClassName.equals(SERVLET_EXCEPTION_NAME)){
            // 例外がServletExceptionの場合は、ルートの原因を取得
            try{
                cause = (Throwable)th.getClass()
                    .getMethod(GET_ROOT_CAUSE_METHOD, (Class[])null).invoke(th, (Object[])null);
            }catch(NoSuchMethodException e){
            }catch(IllegalAccessException e){
            }catch(InvocationTargetException e){
            }
        }else if(thClassName.equals(JMS_EXCEPTION_NAME)){
            // 例外がJMSExceptionの場合は、リンク例外を取得
            try{
                cause = (Exception)th.getClass()
                    .getMethod(GET_LINKED_EXCEPTION_METHOD, (Class[])null).invoke(th, (Object[])null);
            }catch(NoSuchMethodException e){
            }catch(IllegalAccessException e){
            }catch(InvocationTargetException e){
            }
        }else{
            cause = th.getCause();
        }
        return cause == th ? null : cause;
    }
    
    
    public Object invokeChain(
        Object inputObj,
        jp.ossc.nimbus.service.aspect.interfaces.InterceptorChain interceptChain
    ) throws InterceptorException, TargetCheckedException,
             TargetUncheckedException{
        try{
            return invokeInternal(inputObj, interceptChain, null);
        }catch(InterceptorException e){
            throw e;
        }catch(TargetCheckedException e){
            throw e;
        }catch(TargetUncheckedException e){
            throw e;
        }catch(Throwable th){
            throw new InterceptorException(th);
        }
    }
    
    public Object invoke(
        InvocationContext context,
        jp.ossc.nimbus.service.aop.InterceptorChain chain
    ) throws Throwable{
        return invokeInternal(context, null, chain);
    }
    
    protected Object invokeInternal(
        Object inputObj,
        jp.ossc.nimbus.service.aspect.interfaces.InterceptorChain interceptChain,
        jp.ossc.nimbus.service.aop.InterceptorChain chain
    ) throws Throwable{
        Object input = inputObj;
        if(chain != null){
            input = ((MethodInvocationContext)input).getParameters()[0];
        }
        if(getState() == STARTED){
            Object ret = null;
            try{
                if(interceptChain != null){
                    ret = interceptChain.invokeChain(input);
                }else{
                    ret = chain.invokeNext((InvocationContext)inputObj);
                }
            }catch(Throwable th){
                if(!handleException(th, input, null)){
                    throw th;
                }
                ret = input;
            }
            if(ret != null && ret instanceof CommandBase){
                final CommandBase comBase = (CommandBase)ret;
                if(comBase.getStatus() == CommandBase.C_STATUS_ERROR){
                    final Throwable[] ths = comBase.getExceptions();
                    if(ths != null && ths.length != 0){
                        for(int i = 0; i < ths.length; i++){
                            try{
                                handleException(
                                    ths[i],
                                    input,
                                    comBase
                                );
                            }catch(Throwable th){
                            }
                        }
                    }
                }
            }
            return ret;
        }else{
            if(interceptChain != null){
                return interceptChain.invokeChain(input);
            }else{
                return chain.invokeNext((InvocationContext)inputObj);
            }
        }
    }
    
    protected boolean handleException(
        Throwable th,
        Object input,
        Object output
    ) throws Throwable{
        ExceptionHandler handler = getTargetExceptionHandlerCause(th);
        if(handler != null){
            // ハンドリング対象の例外
            Throwable targetTh = getTargetException(
                exceptionMapForHandler,
                th
            );
            if(targetTh == null){
                targetTh = th;
            }
            handler.handleException(targetTh, input, output);
            return true;
        }
        return false;
    }
}