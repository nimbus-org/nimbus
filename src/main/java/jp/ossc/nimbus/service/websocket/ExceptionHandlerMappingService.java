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
package jp.ossc.nimbus.service.websocket;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.websocket.Session;

import jp.ossc.nimbus.beans.ServiceNameEditor;
import jp.ossc.nimbus.core.NimbusClassLoader;
import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.util.ClassMappingTree;

/**
 * 例外ハンドラマッピングサービス。
 * <p>
 *
 * @author M.Ishida
 */
public class ExceptionHandlerMappingService extends ServiceBase implements ExceptionHandlerMappingServiceMBean {

    private static final String SERVLET_EXCEPTION_NAME = "javax.servlet.ServletException";
    private static final String GET_ROOT_CAUSE_METHOD = "getRootCause";
    private static final String JMS_EXCEPTION_NAME = "javax.jms.JMSException";
    private static final String GET_LINKED_EXCEPTION_METHOD = "getLinkedException";

    protected Properties exceptionAndHandlerMapping;
    protected ClassMappingTree exceptionMapForHandler;
    protected ServiceName defaultExceptionHandlerServiceName;
    protected ExceptionHandler defaultExceptionHandler;

    @Override
    public void setExceptionAndHandlerMapping(Properties map) {
        exceptionAndHandlerMapping = map;
    }

    @Override
    public Properties getExceptionAndHandlerMapping() {
        return exceptionAndHandlerMapping;
    }

    @Override
    public void setDefaultExceptionHandlerServiceName(ServiceName name) {
        defaultExceptionHandlerServiceName = name;
    }

    @Override
    public ServiceName getDefaultExceptionHandlerServiceName() {
        return defaultExceptionHandlerServiceName;
    }

    @Override
    public void startService() throws Exception {
        final ClassLoader loader = NimbusClassLoader.getInstance();
        if (exceptionAndHandlerMapping != null) {
            exceptionMapForHandler = new ClassMappingTree();
            final ServiceNameEditor editor = new ServiceNameEditor();
            editor.setServiceManagerName(getServiceManagerName());
            final Iterator exNames = exceptionAndHandlerMapping.keySet().iterator();
            while (exNames.hasNext()) {
                final String exName = (String) exNames.next();
                final Class clazz = Class.forName(exName, true, loader);
                final String name = (String) exceptionAndHandlerMapping.get(exName);
                editor.setAsText(name);
                final ServiceName serviceName = (ServiceName) editor.getValue();
                exceptionMapForHandler.add(clazz, ServiceManagerFactory.getServiceObject(serviceName));
            }
        }
        if (defaultExceptionHandlerServiceName != null) {
            defaultExceptionHandler = (ExceptionHandler) ServiceManagerFactory
                    .getServiceObject(defaultExceptionHandlerServiceName);
        }
    }

    /**
     * 指定された例外のハンドラクラスをマップから見つけて返す。
     * <p>
     *
     * @param th 例外(ハンドリング対象の例外を含んでいる)
     * @return 例外ハンドラ
     */
    protected ExceptionHandler getTargetExceptionHandlerCause(Throwable th) {
        ExceptionHandler handler = (ExceptionHandler) getTargetHandlerCause(exceptionMapForHandler, th);
        return handler == null ? defaultExceptionHandler : handler;
    }

    /**
     * 指定された例外のハンドラをマップから見つけて返す。
     * <p>
     *
     * @param handlers ハンドラのマップ
     * @param th 例外(ハンドリング対象の例外を含んでいる)
     * @return 例外ハンドラ
     */
    protected Object getTargetHandlerCause(ClassMappingTree handlers, Throwable th) {
        if (handlers == null) {
            return null;
        }
        // 例外クラスに関連付いている例外ハンドラを取得
        Object handler = handlers.getValue(th.getClass());
        if (handler != null) {
            return handler;
        }
        Throwable cause = getCause(th);
        return cause == null ? null : getTargetHandlerCause(handlers, cause);
    }

    /**
     * 指定された例外から、ハンドリング対象の例外を取り出す。
     * <p>
     *
     * @param handlers ハンドラのマップ
     * @param th 例外
     * @return ハンドリング対象の例外
     */
    protected Throwable getTargetException(ClassMappingTree handlers, Throwable th) {
        if (handlers == null) {
            return th;
        }
        // 例外クラスに関連付いている例外ハンドラを取得
        Object handler = handlers.getValue(th.getClass());
        if (handler != null) {
            return th;
        }

        Throwable cause = getCause(th);
        return cause == null ? null : getTargetException(handlers, cause);
    }

    /**
     * 指定された例外が無視対象かどうかを判断する。
     * <p>
     *
     * @param ignoreExceptions 無視対象の例外Set
     * @param th 例外
     * @return 無視対象結果
     */
    protected boolean isIgnoreException(Set ignoreExceptions, Throwable th) {
        if (ignoreExceptions == null || ignoreExceptions.isEmpty()) {
            return false;
        }
        boolean isIgnore = ignoreExceptions.contains(th.getClass());
        if (isIgnore) {
            return true;
        }

        Throwable cause = getCause(th);
        return cause == null ? false : isIgnoreException(ignoreExceptions, cause);
    }

    /**
     * 指定された例外から原因を取得する。
     * <p>
     *
     * @param th 例外
     * @return 原因
     */
    protected Throwable getCause(Throwable th) {
        Throwable cause = null;
        String thClassName = th.getClass().getName();
        if (thClassName.equals(SERVLET_EXCEPTION_NAME)) {
            // 例外がServletExceptionの場合は、ルートの原因を取得
            try {
                cause = (Throwable) th.getClass().getMethod(GET_ROOT_CAUSE_METHOD, (Class[]) null)
                        .invoke(th, (Object[]) null);
            } catch (NoSuchMethodException e) {
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            }
        } else if (thClassName.equals(JMS_EXCEPTION_NAME)) {
            // 例外がJMSExceptionの場合は、リンク例外を取得
            try {
                cause = (Exception) th.getClass().getMethod(GET_LINKED_EXCEPTION_METHOD, (Class[]) null)
                        .invoke(th, (Object[]) null);
            } catch (NoSuchMethodException e) {
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            }
        } else {
            cause = th.getCause();
        }
        return cause == th ? null : cause;
    }

    /**
     * 例外をハンドリングする。
     * <p>
     *
     * @param th 例外
     * @param session 例外が発生したSession
     */
    public void handleException(Session session, Throwable th) throws Throwable {
        ExceptionHandler handler = getTargetExceptionHandlerCause(th);
        if (handler != null) {
            // ハンドリング対象の例外
            Throwable targetTh = getTargetException(exceptionMapForHandler, th);
            if (targetTh == null) {
                targetTh = th;
            }
            handler.handleException(session, targetTh);
        }
    }
}