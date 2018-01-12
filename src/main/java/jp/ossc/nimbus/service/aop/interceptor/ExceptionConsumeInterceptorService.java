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
package jp.ossc.nimbus.service.aop.interceptor;

import java.beans.*;
import java.util.*;
import java.lang.reflect.*;

import jp.ossc.nimbus.beans.*;
import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.core.ServiceLoader;
import jp.ossc.nimbus.service.aop.*;
import jp.ossc.nimbus.service.log.*;

/**
 * 指定例外潰しインターセプタ。<p>
 * メソッドの呼び出しの結果、例外がthrowされた時に、指定された例外をキャッチし、握り潰して戻り値を返すインターセプタである。<br>
 * 以下に、指定例外潰しインターセプタのサービス定義例を示す。<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * 
 * &lt;server&gt;
 *     
 *     &lt;manager name="Sample"&gt;
 *         
 *         &lt;service name="ExceptionConsumeInterceptor"
 *                  code="jp.ossc.nimbus.service.aop.interceptor.ExceptionConsumeInterceptorService"&gt;
 *             &lt;attribute name="ExceptionClassNames"&gt;java.lang.NullPointerException&lt;/attribute&gt;
 *         &lt;/service&gt;
 *         
 *     &lt;/manager&gt;
 *     
 * &lt;/server&gt;
 * </pre>
 * 
 * @author N.Saisho
 */
public class ExceptionConsumeInterceptorService extends ServiceBase
 implements Interceptor, ExceptionConsumeInterceptorServiceMBean{
    
    private static final long serialVersionUID = 5814164743760498457L;
    
    private String[] exceptionClassNames;
    private Class[] exceptionClasses;
    private ServiceName logServiceName;
    private Logger log;
    
    private String logKey;
    private String[] logArgs;
    private Locale logLocale;
    private Object returnValue;
    private boolean isLoggingException;
    
    // ExceptionConsumeInterceptorServiceMBeanのJavaDoc
    public void setExceptionClassNames(String[] classnames){
        exceptionClassNames = classnames;
    }
    
    // ExceptionConsumeInterceptorServiceMBeanのJavaDoc
    public String[] getExceptionClassNames(){
        return exceptionClassNames;
    }
    
    // ExceptionConsumeInterceptorServiceMBeanのJavaDoc
    public void setReturnValue(Object val){
        returnValue = val;
    }
    
    // ExceptionConsumeInterceptorServiceMBeanのJavaDoc
    public Object getReturnValue(){
        return returnValue;
    }
    
    // ExceptionConsumeInterceptorServiceMBeanのJavaDoc
    public void setLoggerServiceName(ServiceName name){
        logServiceName = name;
    }

    // ExceptionConsumeInterceptorServiceMBeanのJavaDoc
    public ServiceName getLoggerServiceName(){
        return logServiceName;
    }

    // ExceptionConsumeInterceptorServiceMBeanのJavaDoc
    public void setLoggerMessageCode(String key){
        logKey = key;
    }
    
    // ExceptionConsumeInterceptorServiceMBeanのJavaDoc
    public String getLoggerMessageCode(){
        return logKey;
    }
    
    // ExceptionConsumeInterceptorServiceMBeanのJavaDoc
    public void setLoggerMessageArgs(String[] args){
        logArgs = args;
    }
    
    // ExceptionConsumeInterceptorServiceMBeanのJavaDoc
    public String[] getLoggerMessageArgs(){
        return logArgs;
    }
    
    // ExceptionConsumeInterceptorServiceMBeanのJavaDoc
    public void setLoggerMessageLocale(Locale locale){
        logLocale = locale;
    }
    
    // ExceptionConsumeInterceptorServiceMBeanのJavaDoc
    public Locale getLoggerMessageLocale(){
        return logLocale;
    }
    
    // ExceptionConsumeInterceptorServiceMBeanのJavaDoc
    public void setLoggingException(boolean isLogging){
        isLoggingException = isLogging;
    }
    
    // ExceptionConsumeInterceptorServiceMBeanのJavaDoc
    public boolean isLoggingException(){
        return isLoggingException;
    }
    
    /**
     * Loggerを設定する。<p>
     *
     * @param log ログサービス
     */
    public void setLoggerService(Logger log){
        this.log = log;
    }
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始処理に失敗した場合
     */
    public void startService() throws Exception{
        
        if(logServiceName != null){
            if(logKey == null){
                throw new IllegalArgumentException("LoggerMessageCode is null.");
            }
            if(log == null) {
                log = (Logger)ServiceManagerFactory.getServiceObject(logServiceName);
            }
        }
        if(exceptionClassNames != null){
            exceptionClasses = new Class[exceptionClassNames.length];
            final ClassLoader loader = NimbusClassLoader.getInstance();
            for(int i = 0; i < exceptionClassNames.length; i++){
                exceptionClasses[i] = Class.forName(
                    exceptionClassNames[i],
                    true,
                    loader
                );
            }
        }
    }
    
    /*
     * 設定された例外をcatchして握りつぶして、nullを返す。<p>
     * サービスが開始されていない場合は、何もせずに、次のインターセプタを呼び出す。
     *
     * @param context 呼び出しのコンテキスト情報
     * @param chain 次のインターセプタを呼び出すためのチェーン
     * @return 呼び出し結果の戻り値
     * @exception Throwable 設定された例外
     */
    public Object invoke(
        InvocationContext context,
        InterceptorChain chain
    ) throws Throwable{
        if(getState() == STARTED){
            try{
                return chain.invokeNext(context);
            }catch(Throwable th){
                if(exceptionClasses != null){
                    final Class caught = th.getClass();
                    for(int i = 0; i < exceptionClasses.length; i++){
                        if(exceptionClasses[i].isAssignableFrom(caught)){
                            if(logKey != null){
                                Logger logger = log == null ? getLogger() : log;
                                if(logger != null){
                                    logger.write(
                                        logLocale,
                                        logKey,
                                        logArgs,
                                        isLoggingException ? th : null
                                    );
                                }
                            }
                            return createReturnValue(
                                (MethodInvocationContext)context
                            );
                        }
                    }
                }
                throw th;
            }
        }else{
            return chain.invokeNext(context);
        }
    }
    
    private Object createReturnValue(MethodInvocationContext context){
        if(returnValue == null){
            return returnValue;
        }
        final Method method = context.getTargetMethod();
        final Class retType = method.getReturnType();
        if(Void.TYPE.equals(retType)){
            return null;
        }else if(returnValue != null && retType.isInstance(returnValue)){
            return returnValue;
        }else{
            ServiceLoader loader = null;
            try{
                final ServiceMetaData data = ServiceManagerFactory
                    .getServiceMetaData(getServiceNameObject());
                if(data != null){
                    loader = data.getServiceLoader();
                }
            }catch(ServiceNotFoundException e){
            }
            PropertyEditor editor = null;
            if(loader == null){
                editor = NimbusPropertyEditorManager.findEditor(retType);
            }else{
                editor = loader.findEditor(retType);
            }
            if(editor == null){
                return null;
            }else{
                editor.setAsText(returnValue.toString());
                return editor.getValue();
            }
        }
    }
}
