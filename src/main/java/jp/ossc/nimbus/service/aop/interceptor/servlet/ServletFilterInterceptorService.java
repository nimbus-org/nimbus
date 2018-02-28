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

import java.util.regex.*;
import javax.servlet.*;
import javax.servlet.http.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.aop.*;

/**
 * 抽象サーブレットフィルタインターセプタ。<p>
 *
 * @author M.Takata
 */
public abstract class ServletFilterInterceptorService extends ServiceBase
 implements Interceptor, ServletFilterInterceptorServiceMBean{
    
    private static final long serialVersionUID = -3078358390936975946L;
    
    protected String[] enabledURLs;
    protected Pattern[] enabledURLPatterns;
    
    protected String[] disabledURLs;
    protected Pattern[] disabledURLPatterns;
    
    protected String[] enabledURIs;
    protected Pattern[] enabledURIPatterns;
    
    protected String[] disabledURIs;
    protected Pattern[] disabledURIPatterns;
    
    protected String[] enabledPaths;
    protected Pattern[] enabledPathPatterns;
    
    protected String[] disabledPaths;
    protected Pattern[] disabledPathPatterns;
    
    // ServletFilterInterceptorServiceMBeanのJavaDoc
    public void setEnabledURLs(String[] urls){
        enabledURLs = urls;
    }
    
    // ServletFilterInterceptorServiceMBeanのJavaDoc
    public String[] getEnabledURLs(){
        return enabledURLs;
    }
    
    // ServletFilterInterceptorServiceMBeanのJavaDoc
    public void setDisabledURLs(String[] urls){
        disabledURLs = urls;
    }
    
    // ServletFilterInterceptorServiceMBeanのJavaDoc
    public String[] getDisabledURLs(){
        return disabledURLs;
    }
    
    // ServletFilterInterceptorServiceMBeanのJavaDoc
    public void setEnabledURIs(String[] uris){
        enabledURIs = uris;
    }
    
    // ServletFilterInterceptorServiceMBeanのJavaDoc
    public String[] getEnabledURIs(){
        return enabledURIs;
    }
    
    // ServletFilterInterceptorServiceMBeanのJavaDoc
    public void setDisabledURIs(String[] uris){
        disabledURIs = uris;
    }
    
    // ServletFilterInterceptorServiceMBeanのJavaDoc
    public String[] getDisabledURIs(){
        return disabledURIs;
    }
    
    // ServletFilterInterceptorServiceMBeanのJavaDoc
    public void setEnabledPaths(String[] paths){
        enabledPaths = paths;
    }
    
    // ServletFilterInterceptorServiceMBeanのJavaDoc
    public String[] getEnabledPaths(){
        return enabledPaths;
    }
    
    // ServletFilterInterceptorServiceMBeanのJavaDoc
    public void setDisabledPaths(String[] paths){
        disabledPaths = paths;
    }
    
    // ServletFilterInterceptorServiceMBeanのJavaDoc
    public String[] getDisabledPaths(){
        return disabledPaths;
    }
    
    /**
     * サービスの開始前処理を行う。<p>
     *
     * @exception Exception サービスの開始前処理に失敗した場合
     */
    public void preStartService() throws Exception{
        super.preStartService();
        if(enabledURLs != null && enabledURLs.length != 0){
            enabledURLPatterns = new Pattern[enabledURLs.length];
            for(int i = 0; i < enabledURLs.length; i++){
                enabledURLPatterns[i] = Pattern.compile(enabledURLs[i]);
            }
        }
        if(disabledURLs != null && disabledURLs.length != 0){
            disabledURLPatterns = new Pattern[disabledURLs.length];
            for(int i = 0; i < disabledURLs.length; i++){
                disabledURLPatterns[i] = Pattern.compile(disabledURLs[i]);
            }
        }
        if(enabledURIs != null && enabledURIs.length != 0){
            enabledURIPatterns = new Pattern[enabledURIs.length];
            for(int i = 0; i < enabledURIs.length; i++){
                enabledURIPatterns[i] = Pattern.compile(enabledURIs[i]);
            }
        }
        if(disabledURIs != null && disabledURIs.length != 0){
            disabledURIPatterns = new Pattern[disabledURIs.length];
            for(int i = 0; i < disabledURIs.length; i++){
                disabledURIPatterns[i] = Pattern.compile(disabledURIs[i]);
            }
        }
        if(enabledPaths != null && enabledPaths.length != 0){
            enabledPathPatterns = new Pattern[enabledPaths.length];
            for(int i = 0; i < enabledPaths.length; i++){
                enabledPathPatterns[i] = Pattern.compile(enabledPaths[i]);
            }
        }
        if(disabledPaths != null && disabledPaths.length != 0){
            disabledPathPatterns = new Pattern[disabledPaths.length];
            for(int i = 0; i < disabledPaths.length; i++){
                disabledPathPatterns[i] = Pattern.compile(disabledPaths[i]);
            }
        }
    }
    
    /**
     * スレッドコンテキストを初期化して、次のインターセプタを呼び出す。<p>
     * サービスが開始されていない場合は、何もせずに次のインターセプタを呼び出す。<br>
     *
     * @param context 呼び出しのコンテキスト情報
     * @param chain 次のインターセプタを呼び出すためのチェーン
     * @return 呼び出し結果の戻り値
     * @exception Throwable 呼び出し先で例外が発生した場合、またはこのインターセプタで任意の例外が発生した場合。但し、本来呼び出される処理がthrowしないRuntimeException以外の例外をthrowしても、呼び出し元には伝播されない。
     */
    public Object invoke(
        InvocationContext context,
        InterceptorChain chain
    ) throws Throwable{
        final ServletFilterInvocationContext filtreContext
             = (ServletFilterInvocationContext)context;
        final ServletRequest request = filtreContext.getServletRequest();
        if(request instanceof HttpServletRequest){
            final HttpServletRequest httpReq = (HttpServletRequest)request;
            if(enabledURLPatterns != null && enabledURLPatterns.length != 0){
                final String reqURL = httpReq.getRequestURL().toString();
                if(!checkPatterns(reqURL, enabledURLPatterns)){
                    return chain.invokeNext(context);
                }
            }
            if(enabledURIPatterns != null && enabledURIPatterns.length != 0){
                final String reqURI = httpReq.getRequestURI().toString();
                if(!checkPatterns(reqURI, enabledURIPatterns)){
                    return chain.invokeNext(context);
                }
            }
            if(enabledPathPatterns != null && enabledPathPatterns.length != 0){
                String reqPath = httpReq.getServletPath();
                if(httpReq.getPathInfo() != null){
                    reqPath = reqPath + httpReq.getPathInfo();
                }
                if(!checkPatterns(reqPath, enabledPathPatterns)){
                    return chain.invokeNext(context);
                }
            }
            if(disabledURLPatterns != null && disabledURLPatterns.length != 0){
                final String reqURL = httpReq.getRequestURL().toString();
                if(checkPatterns(reqURL, disabledURLPatterns)){
                    return chain.invokeNext(context);
                }
            }
            if(disabledURIPatterns != null && disabledURIPatterns.length != 0){
                final String reqURI = httpReq.getRequestURI().toString();
                if(checkPatterns(reqURI, disabledURIPatterns)){
                    return chain.invokeNext(context);
                }
            }
            if(disabledPathPatterns != null
                 && disabledPathPatterns.length != 0){
                String reqPath = httpReq.getServletPath();
                if(httpReq.getPathInfo() != null){
                    reqPath = reqPath + httpReq.getPathInfo();
                }
                if(checkPatterns(reqPath, disabledPathPatterns)){
                    return chain.invokeNext(context);
                }
            }
        }
        return invokeFilter(filtreContext, chain);
    }
    
    protected boolean checkPatterns(
        String target,
        Pattern[] patterns
    ){
        for(int i = 0; i < patterns.length; i++){
            final Matcher m = patterns[i].matcher(target);
            if(m.matches()){
                return true;
            }
        }
        return false;
    }
    
    /**
     * サーブレット呼び出しをインターセプトする。<p>
     *
     * @param context サーブレットフィルタ呼び出しのコンテキスト情報
     * @param chain 次のインターセプタを呼び出すためのチェーン
     * @return 呼び出し結果の戻り値
     * @exception Throwable 呼び出し先で例外が発生した場合、またはこのインターセプタで任意の例外が発生した場合
     */
    public abstract Object invokeFilter(
        ServletFilterInvocationContext context,
        InterceptorChain chain
    ) throws Throwable;
}