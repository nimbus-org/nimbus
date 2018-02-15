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
import java.util.regex.*;
import javax.servlet.*;
import javax.servlet.http.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.beans.ServiceNameEditor;
import jp.ossc.nimbus.service.aop.*;

/**
 * 選択可能サーブレットフィルタインターセプタ。<p>
 * リクエストのURL、URI、サーブレットパス毎に、異なるインターセプタに振り分けるインターセプタである。<br>
 *
 * @author M.Takata
 */
public class SelectableServletFilterInterceptorService
 extends ServletFilterInterceptorService
 implements SelectableServletFilterInterceptorServiceMBean{
    
    private static final long serialVersionUID = 6609672536666072027L;
    
    protected String[] urlAndInterceptorServiceNameMapping;
    protected Map urlAndInterceptorServiceNameMap;
    
    protected String[] uriAndInterceptorServiceNameMapping;
    protected Map uriAndInterceptorServiceNameMap;
    
    protected String[] pathAndInterceptorServiceNameMapping;
    protected Map pathAndInterceptorServiceNameMap;
    
    // SelectableServletFilterInterceptorServiceMBeanのJavaDoc
    public void setURLAndInterceptorServiceNameMapping(String[] mapping){
        urlAndInterceptorServiceNameMapping = mapping;
    }
    
    // SelectableServletFilterInterceptorServiceMBeanのJavaDoc
    public String[] getURLAndInterceptorServiceNameMapping(){
        return urlAndInterceptorServiceNameMapping;
    }
    
    // SelectableServletFilterInterceptorServiceMBeanのJavaDoc
    public void setURIAndInterceptorServiceNameMapping(String[] mapping){
        uriAndInterceptorServiceNameMapping = mapping;
    }
    
    // SelectableServletFilterInterceptorServiceMBeanのJavaDoc
    public String[] getURIAndInterceptorServiceNameMapping(){
        return uriAndInterceptorServiceNameMapping;
    }
    
    // SelectableServletFilterInterceptorServiceMBeanのJavaDoc
    public void setPathAndInterceptorServiceNameMapping(String[] mapping){
        pathAndInterceptorServiceNameMapping = mapping;
    }
    
    // SelectableServletFilterInterceptorServiceMBeanのJavaDoc
    public String[] getPathAndInterceptorServiceNameMapping(){
        return pathAndInterceptorServiceNameMapping;
    }
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始処理に失敗した場合
     */
    public void createService() throws Exception{
        urlAndInterceptorServiceNameMap = new LinkedHashMap();
        uriAndInterceptorServiceNameMap = new LinkedHashMap();
        pathAndInterceptorServiceNameMap = new LinkedHashMap();
    }
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始処理に失敗した場合
     */
    public void startService() throws Exception{
        super.preStartService();
        
        final ServiceNameEditor editor = new ServiceNameEditor();
        editor.setServiceManagerName(getServiceManagerName());
        
        if(urlAndInterceptorServiceNameMapping != null
             && urlAndInterceptorServiceNameMapping.length != 0){
            for(int i = 0; i < urlAndInterceptorServiceNameMapping.length; i++){
                final int index = urlAndInterceptorServiceNameMapping[i]
                    .lastIndexOf('=');
                if(index == urlAndInterceptorServiceNameMapping[i].length() - 1
                     || index == -1){
                    throw new IllegalArgumentException(
                        "Invalid format : "
                             + urlAndInterceptorServiceNameMapping[i]
                    );
                }
                editor.setAsText(
                    urlAndInterceptorServiceNameMapping[i].substring(index + 1)
                );
                urlAndInterceptorServiceNameMap.put(
                    Pattern.compile(
                        urlAndInterceptorServiceNameMapping[i]
                            .substring(0, index)
                    ),
                    editor.getValue()
                );
            }
        }
        if(uriAndInterceptorServiceNameMapping != null
             && uriAndInterceptorServiceNameMapping.length != 0){
            for(int i = 0; i < uriAndInterceptorServiceNameMapping.length; i++){
                final int index = uriAndInterceptorServiceNameMapping[i]
                    .lastIndexOf('=');
                if(index == uriAndInterceptorServiceNameMapping[i].length() - 1
                     || index == -1){
                    throw new IllegalArgumentException(
                        "Invalid format : "
                             + uriAndInterceptorServiceNameMapping[i]
                    );
                }
                editor.setAsText(
                    uriAndInterceptorServiceNameMapping[i].substring(index + 1)
                );
                uriAndInterceptorServiceNameMap.put(
                    Pattern.compile(
                        uriAndInterceptorServiceNameMapping[i]
                            .substring(0, index)
                    ),
                    editor.getValue()
                );
            }
        }
        if(pathAndInterceptorServiceNameMapping != null
             && pathAndInterceptorServiceNameMapping.length != 0){
            for(int i = 0; i < pathAndInterceptorServiceNameMapping.length; i++){
                final int index = pathAndInterceptorServiceNameMapping[i]
                    .lastIndexOf('=');
                if(index == pathAndInterceptorServiceNameMapping[i].length() - 1
                     || index == -1){
                    throw new IllegalArgumentException(
                        "Invalid format : "
                             + pathAndInterceptorServiceNameMapping[i]
                    );
                }
                editor.setAsText(
                    pathAndInterceptorServiceNameMapping[i].substring(index + 1)
                );
                pathAndInterceptorServiceNameMap.put(
                    Pattern.compile(
                        pathAndInterceptorServiceNameMapping[i]
                            .substring(0, index)
                    ),
                    editor.getValue()
                );
            }
        }
    }
    
    
    /**
     * サーブレット呼び出しをインターセプトして、選択可能なインターセプタの中から該当するインターセプタを見つけて呼び出す。<p>
     *
     * @param context サーブレットフィルタ呼び出しのコンテキスト情報
     * @param chain 次のインターセプタを呼び出すためのチェーン
     * @return 呼び出し結果の戻り値
     * @exception Throwable 呼び出し先で例外が発生した場合、またはこのインターセプタで任意の例外が発生した場合
     */
    public Object invokeFilter(
        ServletFilterInvocationContext context,
        InterceptorChain chain
    ) throws Throwable{
        final ServletRequest request = context.getServletRequest();
        if(request instanceof HttpServletRequest){
            final HttpServletRequest httpReq = (HttpServletRequest)request;
            if(urlAndInterceptorServiceNameMap != null
                 && urlAndInterceptorServiceNameMap.size() != 0){
                final String reqURL = httpReq.getRequestURL().toString();
                final Interceptor interceptor = getInterceptor(
                    reqURL,
                    urlAndInterceptorServiceNameMap
                );
                if(interceptor != null){
                    return interceptor.invoke(context, chain);
                }
            }
            if(uriAndInterceptorServiceNameMap != null
                 && uriAndInterceptorServiceNameMap.size() != 0){
                final String reqURI = httpReq.getRequestURI().toString();
                final Interceptor interceptor = getInterceptor(
                    reqURI,
                    uriAndInterceptorServiceNameMap
                );
                if(interceptor != null){
                    return interceptor.invoke(context, chain);
                }
            }
            if(pathAndInterceptorServiceNameMap != null
                 && pathAndInterceptorServiceNameMap.size() != 0){
                String reqPath = httpReq.getServletPath();
                if(httpReq.getPathInfo() != null){
                    reqPath = reqPath + httpReq.getPathInfo();
                }
                final Interceptor interceptor = getInterceptor(
                    reqPath,
                    pathAndInterceptorServiceNameMap
                );
                if(interceptor != null){
                    return interceptor.invoke(context, chain);
                }
            }
        }
        return chain.invokeNext(context);
    }
    
    protected Interceptor getInterceptor(
        String target,
        Map patternMap
    ){
        final Iterator keys = patternMap.keySet().iterator();
        while(keys.hasNext()){
            final Pattern pattern = (Pattern)keys.next();
            final Matcher m = pattern.matcher(target);
            if(m.matches()){
                final ServiceName name = (ServiceName)patternMap.get(pattern);
                try{
                    return (Interceptor)ServiceManagerFactory
                        .getServiceObject(name);
                }catch(ServiceNotFoundException e){
                }
            }
        }
        return null;
    }
}