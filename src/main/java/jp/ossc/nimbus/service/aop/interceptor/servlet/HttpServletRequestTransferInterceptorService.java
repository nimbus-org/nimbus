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
package jp.ossc.nimbus.service.aop.interceptor.servlet;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import jp.ossc.nimbus.beans.Property;
import jp.ossc.nimbus.beans.PropertyAccess;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.service.aop.InterceptorChain;
import jp.ossc.nimbus.service.aop.ServletFilterInvocationContext;
import jp.ossc.nimbus.service.context.Context;

/**
 * HttpServletRequestプロパティ設定インターセプタ。
 * <p>
 *
 * @author M.Ishida
 */
public class HttpServletRequestTransferInterceptorService extends ServletFilterInterceptorService implements
        HttpServletRequestTransferInterceptorServiceMBean {

    private static final long serialVersionUID = 8599129621419714729L;

    protected ServiceName threadContextServiceName;
    protected Context threadContext;
    protected PropertyAccess propertyAccess;
    protected Map requestPropertyAndContextKeyMapping;

    public void setThreadContextServiceName(ServiceName name) {
        threadContextServiceName = name;
    }

    public ServiceName getThreadContextServiceName() {
        return threadContextServiceName;
    }

    public void setRequestPropertyAndContextKeyMapping(Map mapping) {
        requestPropertyAndContextKeyMapping = mapping;
    }

    public Map getRequestPropertyAndContextKeyMapping() {
        return requestPropertyAndContextKeyMapping;
    }

    /**
     * サービスの生成処理を行う。
     * <p>
     *
     * @exception Exception サービスの生成に失敗した場合
     */
    public void createService() throws Exception {
        propertyAccess = new PropertyAccess();
        propertyAccess.setIgnoreNullProperty(true);
    }

    /**
     * サービスの開始処理を行う。
     * <p>
     *
     * @exception Exception サービスの開始に失敗した場合
     */
    public void startService() throws Exception {
        if (requestPropertyAndContextKeyMapping == null || requestPropertyAndContextKeyMapping.size() == 0) {
            throw new IllegalArgumentException("RequestPropertyAndContextKeyMapping must be specified.");
        }
        if (threadContextServiceName != null) {
            threadContext = (Context) ServiceManagerFactory.getServiceObject(threadContextServiceName);
        }
        Iterator entries = requestPropertyAndContextKeyMapping.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry) entries.next();
            Property prop = propertyAccess.getProperty((String) entry.getKey());
            if (!prop.isReadable(HttpServletRequest.class)) {
                throw new IllegalArgumentException("'" + entry.getKey() + "' cannot acquire from a request. value=null");
            }
        }

    }

    /**
     * RequestPropertyAndContextKeyMappingに設定されたマッピングに従い、
     * HttpServletRequestのプロパティ値をThreadContextに設定して、次のインターセプタを呼び出す。
     * <p>
     * サービスが開始されていない場合は、何もせずに次のインターセプタを呼び出す。<br>
     *
     * @param context 呼び出しのコンテキスト情報
     * @param chain 次のインターセプタを呼び出すためのチェーン
     * @return 呼び出し結果の戻り値
     * @exception Throwable 呼び出し先で例外が発生した場合、またはこのインターセプタで任意の例外が発生した場合。但し、
     *                本来呼び出される処理がthrowしないRuntimeException以外の例外をthrowしても
     *                、呼び出し元には伝播されない。
     */
    public Object invokeFilter(ServletFilterInvocationContext context, InterceptorChain chain) throws Throwable {
        if (getState() == STARTED) {
            final HttpServletRequest request = (HttpServletRequest) context.getServletRequest();
            Iterator entries = requestPropertyAndContextKeyMapping.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry entry = (Map.Entry) entries.next();
                Object value = null;
                try {
                    value = propertyAccess.get(request, (String) entry.getKey());
                } catch (InvocationTargetException e) {
                    throw e.getTargetException();
                }
                threadContext.put(entry.getValue(), value);
            }
        }
        return chain.invokeNext(context);
    }
}
