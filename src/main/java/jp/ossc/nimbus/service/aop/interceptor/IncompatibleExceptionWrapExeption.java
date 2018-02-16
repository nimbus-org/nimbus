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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Constructor;

import jp.ossc.nimbus.core.NimbusClassLoader;

/**
 * {@link IncompatibleExceptionWrapInterceptorService}が例外をラップした場合にthrowされる例外。<p>
 * 
 * @author M.Takata
 * @see IncompatibleExceptionWrapInterceptorService
 */
public class IncompatibleExceptionWrapExeption extends RuntimeException{
    
    private static final long serialVersionUID = 5174983898976241044L;
    
    private static final String SERVLET_EXCEPTION_NAME = "javax.servlet.ServletException";
    private static final String GET_ROOT_CAUSE_METHOD = "getRootCause";
    private static final String JMS_EXCEPTION_NAME = "javax.jms.JMSException";
    private static final String GET_LINKED_EXCEPTION_METHOD = "getLinkedException";
    private static final String SET_LINKED_EXCEPTION_METHOD = "setLinkedException";
    private static final String GET_ERROR_CODE_METHOD = "getErrorCode";
    
    private String sourceExceptionClassName;
    private String errorCode;
    
    /**
     * 指定された例外をラップする例外インスタンスを生成する。<p>
     */
    public IncompatibleExceptionWrapExeption(Throwable source){
        super(source.getMessage(), getCause(source));
        setStackTrace(source.getStackTrace());
        sourceExceptionClassName = source.getClass().getName();
        if(sourceExceptionClassName.equals(JMS_EXCEPTION_NAME)){
            try{
                errorCode = (String)source.getClass()
                    .getMethod(GET_ERROR_CODE_METHOD, (Class[])null).invoke(source, (Object[])null);
            }catch(NoSuchMethodException e){
            }catch(IllegalAccessException e){
            }catch(InvocationTargetException e){
            }
        }
    }
    
    public Throwable unwrap(){
        final ClassLoader loader = NimbusClassLoader.getInstance();
        try{
            Class clazz = Class.forName(sourceExceptionClassName, true, loader);
            String message = getMessage();
            Throwable cause = getCause();
            Throwable result = null;
            if(clazz.getName().equals(JMS_EXCEPTION_NAME)){
                Constructor c = clazz.getConstructor(new Class[]{String.class, String.class});
                result = (Throwable)c.newInstance(new Object[]{message, errorCode});
                if(cause != null){
                    clazz.getMethod(SET_LINKED_EXCEPTION_METHOD, new Class[]{Exception.class}).invoke(result, new Object[]{cause});
                }
            }else{
                if(message != null && cause != null){
                    Constructor c = clazz.getConstructor(new Class[]{String.class, Throwable.class});
                    result = (Throwable)c.newInstance(new Object[]{message, cause});
                }else if(message != null && cause == null){
                    Constructor c = clazz.getConstructor(new Class[]{String.class});
                    result = (Throwable)c.newInstance(new Object[]{message});
                }else if(message == null && cause != null){
                    Constructor c = clazz.getConstructor(new Class[]{Throwable.class});
                    result = (Throwable)c.newInstance(new Object[]{cause});
                }else if(message == null && cause == null){
                    result = (Throwable)clazz.newInstance();
                }
            }
            result.setStackTrace(getStackTrace());
            return result;
        }catch(ClassNotFoundException e){
            return this;
        }catch(SecurityException e){
            return this;
        }catch(InstantiationException e){
            return this;
        }catch(NoSuchMethodException e){
            return this;
        }catch(IllegalAccessException e){
            return this;
        }catch(InvocationTargetException e){
            return this;
        }
    }
    
    /**
     * 指定された例外から原因を取得する。<p>
     *
     * @param th 例外
     * @return 原因
     */
    public static Throwable getCause(Throwable th){
        Throwable cause = null;
        if(th.getClass().getName().equals(SERVLET_EXCEPTION_NAME)){
            cause = th.getCause();
            if(cause == null){
                // 例外がServletExceptionの場合は、ルートの原因を取得
                try{
                    cause = (Throwable)th.getClass()
                        .getMethod(GET_ROOT_CAUSE_METHOD, (Class[])null).invoke(th, (Object[])null);
                }catch(NoSuchMethodException e){
                }catch(IllegalAccessException e){
                }catch(InvocationTargetException e){
                }
            }
        }else if(th.getClass().getName().equals(JMS_EXCEPTION_NAME)){
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
    
    /**
     * 指定された例外に原因を設定する。<p>
     *
     * @param th 例外
     * @param cause 原因
     */
    public static void setCause(Throwable th, Throwable cause){
        if(th.getClass().getName().equals(JMS_EXCEPTION_NAME)){
            try{
                th.getClass().getMethod(SET_LINKED_EXCEPTION_METHOD, new Class[]{Exception.class}).invoke(th, new Object[]{cause});
            }catch(NoSuchMethodException e){
            }catch(IllegalAccessException e){
            }catch(InvocationTargetException e){
            }
        }else{
            th.initCause(cause);
        }
    }
}

