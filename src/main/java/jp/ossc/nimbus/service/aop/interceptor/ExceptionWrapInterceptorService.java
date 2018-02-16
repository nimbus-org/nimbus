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

import java.util.*;
import java.lang.reflect.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.util.*;
import jp.ossc.nimbus.service.aop.*;
import jp.ossc.nimbus.service.message.*;

/**
 * 例外ラップインターセプタ。<p>
 * メソッドの呼び出しの結果、例外がthrowされた時に、指定された例外でラップしてthrowし直すインターセプタである。<br>
 * 以下に、NullPointerExceptionを独自例外クラスでラップしてthrowするインターセプタのサービス定義例を示す。<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * 
 * &lt;server&gt;
 *     
 *     &lt;manager name="Sample"&gt;
 *         
 *         &lt;service name="NullPointerWrapExceptionTrowInterceptor"
 *                  code="jp.ossc.nimbus.service.aop.interceptor.ExceptionWrapInterceptorService"&gt;
 *             &lt;attribute name="WrapExceptionMapping"&gt;java.lang.NullPointerException=sample.SampleException&lt;/attribute&gt;
 *             &lt;attribute name="Message"&gt;予期しない例外です。&lt;/attribute&gt;
 *         &lt;/service&gt;
 *         
 *     &lt;/manager&gt;
 *     
 * &lt;/server&gt;
 * </pre>
 * 
 * @author M.Takata
 */
public class ExceptionWrapInterceptorService extends ServiceBase
 implements Interceptor, ExceptionWrapInterceptorServiceMBean{
    
    private static final long serialVersionUID = 8729196964110200620L;
    
    private static final Class[] MSG_PARAM_TYPE = new Class[]{String.class};
    private static final Class[] CAUSE_PARAM_TYPE
         = new Class[]{Throwable.class};
    private static final Class[] MSG_CAUSE_PARAM_TYPE
         = new Class[]{String.class, Throwable.class};
    
    private Properties wrapExceptionMapping;
    private ClassMappingTree wrapExceptionMappingTree;
    private String message;
    private ServiceName messageRecordFactoryServiceName;
    private MessageRecordFactory messageRecordFactory;
    private String messageKey;
    private String[] messageArgs;
    private Locale messageLocale;
    
    // ExceptionWrapInterceptorServiceMBeanのJavaDoc
    public void setWrapExceptionMapping(Properties mapping){
        wrapExceptionMapping = mapping;
    }
    
    // ExceptionWrapInterceptorServiceMBeanのJavaDoc
    public Properties getWrapExceptionMapping(){
        return wrapExceptionMapping;
    }
    
    // ExceptionWrapInterceptorServiceMBeanのJavaDoc
    public void setMessage(String msg){
        message = msg;
    }
    
    // ExceptionWrapInterceptorServiceMBeanのJavaDoc
    public String getMessage(){
        return message;
    }
    
    // ExceptionWrapInterceptorServiceMBeanのJavaDoc
    public void setMessageRecordFactoryServiceName(ServiceName name){
        messageRecordFactoryServiceName = name;
    }
    
    // ExceptionWrapInterceptorServiceMBeanのJavaDoc
    public ServiceName getMessageRecordFactoryServiceName(){
        return messageRecordFactoryServiceName;
    }
    
    // ExceptionWrapInterceptorServiceMBeanのJavaDoc
    public void setMessageKey(String key){
        messageKey = key;
    }
    
    // ExceptionWrapInterceptorServiceMBeanのJavaDoc
    public String getMessageKey(){
        return messageKey;
    }
    
    // ExceptionWrapInterceptorServiceMBeanのJavaDoc
    public void setMessageArgs(String[] args){
        messageArgs = args;
    }
    
    // ExceptionWrapInterceptorServiceMBeanのJavaDoc
    public String[] getMessageArgs(){
        return messageArgs;
    }
    
    // ExceptionWrapInterceptorServiceMBeanのJavaDoc
    public void setMessageLocale(Locale locale){
        messageLocale = locale;
    }
    
    // ExceptionWrapInterceptorServiceMBeanのJavaDoc
    public Locale getMessageLocale(){
        return messageLocale;
    }
    
    /**
     * MessageRecordFactoryを設定する。
     */
    public void setMessageRecordFactoryService(MessageRecordFactory messageRecordFactory) {
		this.messageRecordFactory = messageRecordFactory;
	}

	/**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始処理に失敗した場合
     */
    public void startService() throws Exception{
        if(wrapExceptionMapping != null){
            if(wrapExceptionMappingTree == null){
                wrapExceptionMappingTree = new ClassMappingTree();
            }
            final ClassLoader loader = NimbusClassLoader.getInstance();
            final Iterator keys = wrapExceptionMapping.keySet().iterator();
            while(keys.hasNext()){
                final String key = (String)keys.next();
                final String val = wrapExceptionMapping.getProperty(key);
                wrapExceptionMappingTree.add(
                    Class.forName(key, true, loader),
                    Class.forName(val, true, loader)
                );
            }
        }
    }
    
    /**
     * サービスの停止処理を行う。<p>
     *
     * @exception Exception サービスの停止処理に失敗した場合
     */
    public void stopService() throws Exception{
        if(wrapExceptionMappingTree != null){
            wrapExceptionMappingTree.clear();
        }
    }
    
    private Throwable createThrowable(Throwable th){
        if(wrapExceptionMappingTree == null){
            return th;
        }
        final Class wrapExceptionClass
             = (Class)wrapExceptionMappingTree.getValue(th.getClass());
        if(wrapExceptionClass == null){
            return th;
        }
        final String msg = createMessage();
        if(msg == null){
            Constructor c = null;
            try{
                c = wrapExceptionClass
                    .getConstructor(CAUSE_PARAM_TYPE);
                return (Throwable)c.newInstance(new Object[]{th});
            }catch(NoSuchMethodException e){
            }catch(InstantiationException e){
            }catch(IllegalAccessException e){
            }catch(InvocationTargetException  e){
            }
            
            try{
                c = wrapExceptionClass
                    .getConstructor(MSG_PARAM_TYPE);
                return (Throwable)c.newInstance(new Object[]{th.getMessage()});
            }catch(NoSuchMethodException e){
            }catch(InstantiationException e){
            }catch(IllegalAccessException e){
            }catch(InvocationTargetException  e){
            }
        }else{
            Constructor c = null;
            try{
                c = wrapExceptionClass
                    .getConstructor(MSG_CAUSE_PARAM_TYPE);
                return (Throwable)c.newInstance(new Object[]{msg, th});
            }catch(NoSuchMethodException e){
            }catch(InstantiationException e){
            }catch(IllegalAccessException e){
            }catch(InvocationTargetException  e){
            }
            
            try{
                c = wrapExceptionClass
                    .getConstructor(MSG_PARAM_TYPE);
                return (Throwable)c.newInstance(new Object[]{msg});
            }catch(NoSuchMethodException e){
            }catch(InstantiationException e){
            }catch(IllegalAccessException e){
            }catch(InvocationTargetException  e){
            }
        }
        try{
            return (Throwable)wrapExceptionClass.newInstance();
        }catch(IllegalAccessException e){
        }catch(InstantiationException e){
        }
        return th;
    }
    
    private String createMessage(){
        if(message == null && messageKey != null){
            if(messageRecordFactoryServiceName != null){
                messageRecordFactory = (MessageRecordFactory)ServiceManagerFactory
                        .getServiceObject(messageRecordFactoryServiceName);
            }
            MessageRecordFactory factory = messageRecordFactory;
            if(factory == null){
                factory = getMessageRecordFactory();
            }
            if(factory != null){
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
     * 設定された例外をcatchして設定されたラップ例外でラップしてthrowする。<p>
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
                throw createThrowable(th);
            }
        }else{
            return chain.invokeNext(context);
        }
    }
}
