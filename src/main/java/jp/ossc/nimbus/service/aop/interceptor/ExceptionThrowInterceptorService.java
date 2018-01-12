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

import java.util.Locale;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.aop.*;
import jp.ossc.nimbus.service.message.*;

/**
 * 例外インターセプタ。<p>
 * メソッドの呼び出しに対して、何もせずに例外を返すインターセプタである。<br>
 * 以下に、UnsupportedOperationExceptionをthrowするインターセプタのサービス定義例を示す。<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * 
 * &lt;server&gt;
 *     
 *     &lt;manager name="Sample"&gt;
 *         
 *         &lt;service name="UnsupportedOperationExceptionTrowInterceptor"
 *                  code="jp.ossc.nimbus.service.aop.interceptor.ExceptionThrowInterceptorService"&gt;
 *             &lt;attribute name="ExceptionClassName"&gt;java.lang.UnsupportedOperationException&lt;/attribute&gt;
 *             &lt;attribute name="Message"&gt;このメソッドは呼び出してはいけません。&lt;/attribute&gt;
 *         &lt;/service&gt;
 *         
 *     &lt;/manager&gt;
 *     
 * &lt;/server&gt;
 * </pre>
 * 
 * @author M.Takata
 */
public class ExceptionThrowInterceptorService extends ServiceBase
 implements Interceptor, ExceptionThrowInterceptorServiceMBean, ExceptionThrow{
    
    private static final long serialVersionUID = -7750833087537700407L;
    
    private String exceptionClassName = RuntimeException.class.getName();
    private Throwable exception;
    private String message;
    private ServiceName messageRecordFactoryServiceName;
    private MessageRecordFactory messageRecordFactory;
    private String messageKey;
    private String[] messageArgs;
    private Locale messageLocale;
    private boolean isEnabled = true;
    
    // ExceptionThrowInterceptorServiceMBeanのJavaDoc
    public void setEnabled(boolean enabled){
        isEnabled = enabled;
    }
    
    // ExceptionThrowInterceptorServiceMBeanのJavaDoc
    public boolean isEnabled(){
        return isEnabled;
    }
    
    // ExceptionThrowInterceptorServiceMBeanのJavaDoc
    public void setExceptionClassName(String className){
        exceptionClassName = className;
    }
    
    // ExceptionThrowInterceptorServiceMBeanのJavaDoc
    public String getExceptionClassName(){
        return exceptionClassName;
    }
    
    // ExceptionThrowInterceptorServiceMBeanのJavaDoc
    public void setMessage(String msg){
        message = msg;
    }
    
    // ExceptionThrowInterceptorServiceMBeanのJavaDoc
    public String getMessage(){
        return message;
    }
    
    // ExceptionThrowInterceptorServiceMBeanのJavaDoc
    public void setMessageRecordFactoryServiceName(ServiceName name){
        messageRecordFactoryServiceName = name;
    }
    
    // ExceptionThrowInterceptorServiceMBeanのJavaDoc
    public ServiceName getMessageRecordFactoryServiceName(){
        return messageRecordFactoryServiceName;
    }
    
    // ExceptionThrowInterceptorServiceMBeanのJavaDoc
    public void setMessageKey(String key){
        messageKey = key;
    }
    
    // ExceptionThrowInterceptorServiceMBeanのJavaDoc
    public String getMessageKey(){
        return messageKey;
    }
    
    // ExceptionThrowInterceptorServiceMBeanのJavaDoc
    public void setMessageArgs(String[] args){
        messageArgs = args;
    }
    
    // ExceptionThrowInterceptorServiceMBeanのJavaDoc
    public String[] getMessageArgs(){
        return messageArgs;
    }
    
    // ExceptionThrowInterceptorServiceMBeanのJavaDoc
    public void setMessageLocale(Locale locale){
        messageLocale = locale;
    }
    
    // ExceptionThrowInterceptorServiceMBeanのJavaDoc
    public Locale getMessageLocale(){
        return messageLocale;
    }
    
    /**
     * MessageRecordFactoryを設定する。<p>
     *
     * @param factory MessageRecordFactory
     */
    public void setMessageRecordFactoryService(MessageRecordFactory factory) {
        messageRecordFactory = factory;
    }
    
    public void setException(Throwable ex) {
        exception = ex;
    }
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception 指定された例外が生成できない場合
     */
    public void startService() throws Exception{
        createThrowable();
    }
    
    private Throwable createThrowable() throws Exception{
        if(exception != null){
            return exception;
        }
        final String msg = createMessage();
        if(msg == null){
            return (Throwable)Class.forName(
                exceptionClassName,
                true,
                NimbusClassLoader.getInstance()
                ).newInstance();
        }else{
            try{
                return (Throwable)Class.forName(
                    exceptionClassName,
                    true,
                    NimbusClassLoader.getInstance()
                ).getConstructor(new Class[]{String.class})
                    .newInstance(new Object[]{msg});
            }catch(java.lang.reflect.InvocationTargetException e){
                final Throwable th = e.getTargetException();
                if(th instanceof Exception){
                    throw (Exception)th;
                }else{
                    throw (Error)th;
                }
            }
        }
    }
    
    private String createMessage(){
        if(message == null){
            if(messageRecordFactoryServiceName != null){
                messageRecordFactory
                     = (MessageRecordFactory)ServiceManagerFactory
                            .getServiceObject(messageRecordFactoryServiceName);
            }
            MessageRecordFactory factory = messageRecordFactory;
            if(factory == null){
                factory = getMessageRecordFactory();
            }
            if(factory == null){
                return message;
            }
            if(messageKey != null){
                if(messageArgs == null || messageArgs.length == 0){
                    if(messageLocale == null){
                        return factory.findMessage(messageKey);
                    }else{
                        return factory.findMessage(
                            messageLocale,
                            messageKey
                        );
                    }
                }else{
                    if(messageLocale == null){
                        return factory.findEmbedMessage(
                            messageKey,
                            messageArgs
                        );
                    }else{
                        return factory.findEmbedMessage(
                            messageLocale,
                            messageKey,
                            messageArgs
                        );
                    }
                }
            }
        }
        return message;
    }
    
    /**
     * 設定された例外をthrowする。<p>
     * サービスが開始されていない場合は、設定された例外はthrowせずに、次のインターセプタを呼び出す。
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
        if(getState() == STARTED && isEnabled){
            throw createThrowable();
        }else{
            return chain.invokeNext(context);
        }
    }
}
