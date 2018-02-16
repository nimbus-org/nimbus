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

import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.aop.*;
import jp.ossc.nimbus.service.context.*;

/**
 * HTTPレスポンス設定インターセプタ。<p>
 * 以下に、HTTPレスポンスヘッダに、ヘッダ名"test"に"hoge"という値を設定するHTTPレスポンス設定インターセプタのサービス定義例を示す。<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * 
 * &lt;server&gt;
 *     
 *     &lt;manager name="Sample"&gt;
 *         
 *         &lt;service name="HttpServletResponseSetInterceptor"
 *                  code="jp.ossc.nimbus.service.aop.interceptor.servlet.HttpServletResponseSetInterceptorService"&gt;
 *             &lt;attribute name="SetHeader(test)"&gt;hoge;/attribute&gt;
 *         &lt;/service&gt;
 *         
 *     &lt;/manager&gt;
 *     
 * &lt;/server&gt;
 * </pre>
 *
 * @author M.Takata
 */
public class HttpServletResponseSetInterceptorService
 extends ServletResponseSetInterceptorService
 implements HttpServletResponseSetInterceptorServiceMBean{
    
    private static final long serialVersionUID = 9064558932042485512L;
    
    protected Map setHeaderMap;
    protected Map addHeaderMap;
    protected List cookies;
    
    protected ServiceName contextServiceName;
    protected Context context;
    protected Properties setHeaderContextKeys;
    protected Properties addHeaderContextKeys;
    
    public void setSetHeaders(Map headers){
        setHeaderMap.putAll(headers);
    }
    public Map getSetHeaders(){
        return setHeaderMap;
    }
    public void setSetHeader(String name, String value){
        setHeaderMap.put(name, value);
    }
    public String getSetHeader(String name){
        return (String)setHeaderMap.get(name);
    }
    public void removeSetHeader(String name){
        setHeaderMap.remove(name);
    }
    public void clearSetHeaders(){
        setHeaderMap.clear();
    }
    
    public void setSetHeaderContextKeys(Properties keys){
        setHeaderContextKeys = keys;
    }
    public Properties getSetHeaderContextKeys(){
        return setHeaderContextKeys;
    }
    
    public void setAddHeader(String name, String value){
        List values = null;
        if(addHeaderMap.containsKey(name)){
            values = (List)addHeaderMap.get(name);
        }else{
            values = new ArrayList();
            addHeaderMap.put(name, values);
        }
        values.add(value);
    }
    public String[] getAddHeaders(String name){
        if(addHeaderMap.containsKey(name)){
            final List values = (List)addHeaderMap.get(name);
            return (String[])values.toArray(new String[values.size()]);
        }else{
            return null;
        }
    }
    public void removeAddHeader(String name){
        if(addHeaderMap.containsKey(name)){
            final List values = (List)addHeaderMap.get(name);
            values.remove(name);
            if(values.size() == 0){
                addHeaderMap.remove(values);
            }
        }
    }
    public void clearAddHeaders(){
        addHeaderMap.clear();
    }
    
    public void setAddHeaderContextKeys(Properties keys){
        addHeaderContextKeys = keys;
    }
    public Properties getAddHeaderContextKeys(){
        return addHeaderContextKeys;
    }
    
    public void addCookie(Cookie cookie){
        cookies.add(cookie);
    }
    
    public void removeCookie(String name){
        final Iterator itr = cookies.iterator();
        while(itr.hasNext()){
            final Cookie cookie = (Cookie)itr.next();
            if(name.equals(cookie.getName())){
                itr.remove();
            }
        }
    }
    
    public void clearCookies(){
        cookies.clear();
    }
    
    public void setContextServiceName(ServiceName name){
        contextServiceName = name;
    }
    public ServiceName getContextServiceName(){
        return contextServiceName;
    }
    
    public void createService() throws Exception{
        super.createService();
        setHeaderMap = new HashMap();
        addHeaderMap = new HashMap();
        cookies = new ArrayList();
    }
    
    public void startService() throws Exception{
        super.startService();
        if(contextServiceName != null){
            context = (Context)ServiceManagerFactory
                .getServiceObject(contextServiceName);
        }
    }
    
    public void destroyService() throws Exception{
        super.destroyService();
        setHeaderMap = null;
        addHeaderMap = null;
        cookies = null;
    }
    
    /**
     * レスポンスに設定して、次のインターセプタを呼び出す。<p>
     * サービスが開始されていない場合は、何もせずに次のインターセプタを呼び出す。<br>
     *
     * @param context 呼び出しのコンテキスト情報
     * @param chain 次のインターセプタを呼び出すためのチェーン
     * @return 呼び出し結果の戻り値
     * @exception Throwable 呼び出し先で例外が発生した場合、またはこのインターセプタで任意の例外が発生した場合。但し、本来呼び出される処理がthrowしないRuntimeException以外の例外をthrowしても、呼び出し元には伝播されない。
     */
    public Object invokeFilter(
        ServletFilterInvocationContext context,
        InterceptorChain chain
    ) throws Throwable{
        try{
            return super.invokeFilter(context, chain);
        }finally{
            if(getState() == STARTED){
                
                final ServletResponse response = context.getServletResponse();
                if(response instanceof HttpServletResponse){
                    final HttpServletResponse httpRes = (HttpServletResponse)response;
                    
                    Iterator keys = addHeaderMap.keySet().iterator();
                    while(keys.hasNext()){
                        final String key = (String)keys.next();
                        final List values = (List)addHeaderMap.get(key);
                        for(int i = 0, imax = values.size(); i < imax; i++){
                            httpRes.addHeader(key, (String)values.get(i));
                        }
                    }
                    
                    keys = setHeaderMap.keySet().iterator();
                    while(keys.hasNext()){
                        final String key = (String)keys.next();
                        final String value = (String)setHeaderMap.get(key);
                        httpRes.setHeader(key, value);
                    }
                    
                    if(this.context != null){
                        if(addHeaderContextKeys != null){
                            keys = addHeaderContextKeys.keySet().iterator();
                            while(keys.hasNext()){
                                final String key = (String)keys.next();
                                final String name = addHeaderContextKeys.getProperty(key);
                                final Object value = this.context.get(key);
                                if(value != null){
                                    if(value instanceof Date){
                                        httpRes.addDateHeader(name, ((Date)value).getTime());
                                    }else{
                                        httpRes.addHeader(name, value.toString());
                                    }
                                }
                            }
                        }
                        if(setHeaderContextKeys != null){
                            keys = setHeaderContextKeys.keySet().iterator();
                            while(keys.hasNext()){
                                final String key = (String)keys.next();
                                final String name = setHeaderContextKeys.getProperty(key);
                                final Object value = this.context.get(key);
                                if(value != null){
                                    if(value instanceof Date){
                                        httpRes.setDateHeader(name, ((Date)value).getTime());
                                    }else{
                                        httpRes.setHeader(name, value.toString());
                                    }
                                }
                            }
                        }
                    }
                    
                    if(cookies.size() != 0){
                        final HttpServletRequest request
                             = (HttpServletRequest)context.getServletRequest();
                        final Cookie[] reqCookies = request.getCookies();
                        for(int i = 0, imax = cookies.size(); i < imax; i++){
                            final Cookie cookie = (Cookie)cookies.get(i);
                            boolean isMatch = false;
                            if(reqCookies != null){
                                for(int j = 0, jmax = reqCookies.length; j < jmax; j++){
                                    if(reqCookies[j].getName().equals(cookie.getName())
                                        && reqCookies[j].getValue().equals(cookie.getValue())
                                    ){
                                        isMatch = true;
                                        break;
                                    }
                                }
                            }
                            if(!isMatch){
                                final Cookie clone = (Cookie)cookie.clone();
                                if(clone.getPath() == null){
                                    clone.setPath(request.getContextPath());
                                }
                                httpRes.addCookie(clone);
                            }
                        }
                    }
                }
            }
        }
    }
}
