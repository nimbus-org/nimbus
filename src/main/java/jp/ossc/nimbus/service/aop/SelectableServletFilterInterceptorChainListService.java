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
package jp.ossc.nimbus.service.aop;

import java.util.*;
import java.util.regex.*;
import javax.servlet.*;
import javax.servlet.http.*;

import jp.ossc.nimbus.beans.*;
import jp.ossc.nimbus.core.*;

/**
 * サーブレットフィルタの呼び出し情報を使って、{@link InterceptorChainList}を振り分けるInterceptorChainListインタフェースの実装サービス。<p>
 * 以下に、特定のパス毎に異なる{@link InterceptorChainList インターセプタチェーンリスト}を選択するインターセプタチェーンリストのサービス定義例を示す。<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * 
 * &lt;server&gt;
 *     
 *     &lt;manager name="Sample"&gt;
 *         
 *         &lt;service name="InterceptorChainList"
 *                  code="jp.ossc.nimbus.service.aop.SelectableServletFilterInterceptorChainListService"&gt;
 *             &lt;attribute name="EnabledPathMapping"&gt;
 *                 /hoge\.html=#HogeInterceptorChainList
 *                 /fuga.*=#FugaInterceptorChainList
 *             &lt;/attribute&gt;
 *             &lt;attribute name="DefaultInterceptorChainListServiceName"&gt;#DefaultInterceptorChainList&lt;/attribute&gt;
 *         &lt;/service&gt;
 *         
 *     &lt;/manager&gt;
 *     
 * &lt;/server&gt;
 * </pre>
 *
 * @author M.Takata
 */
public class SelectableServletFilterInterceptorChainListService
 extends ServiceBase
 implements SelectableServletFilterInterceptorChainListServiceMBean,
            InterceptorChainList{
    
    private static final long serialVersionUID = -3624632759924394508L;
    
    private Map enabledURLMapping;
    private Map enabledURLChainMapping;
    
    private Map enabledURIMapping;
    private Map enabledURIChainMapping;
    
    private Map enabledPathMapping;
    private Map enabledPathChainMapping;
    
    private Map disabledURLMapping;
    private Map disabledURLChainMapping;
    
    private Map disabledURIMapping;
    private Map disabledURIChainMapping;
    
    private Map disabledPathMapping;
    private Map disabledPathChainMapping;
    
    private ServiceName defaultInterceptorChainListServiceName;
    private InterceptorChainList defaultInterceptorChainList;
    
    // SelectableServletFilterInterceptorChainListServiceMBeanのJavaDoc
    public void setEnabledURLMapping(Map mapping){
        enabledURLMapping = mapping;
    }
    
    // SelectableServletFilterInterceptorChainListServiceMBeanのJavaDoc
    public Map getEnabledURLMapping(){
        return enabledURLMapping;
    }
    
    // SelectableServletFilterInterceptorChainListServiceMBeanのJavaDoc
    public void setEnabledURIMapping(Map mapping){
        enabledURIMapping = mapping;
    }
    
    // SelectableServletFilterInterceptorChainListServiceMBeanのJavaDoc
    public Map getEnabledURIMapping(){
        return enabledPathMapping;
    }
    
    // SelectableServletFilterInterceptorChainListServiceMBeanのJavaDoc
    public void setEnabledPathMapping(Map mapping){
        enabledPathMapping = mapping;
    }
    
    // SelectableServletFilterInterceptorChainListServiceMBeanのJavaDoc
    public Map getEnabledPathMapping(){
        return enabledPathMapping;
    }
    
    // SelectableServletFilterInterceptorChainListServiceMBeanのJavaDoc
    public void setDisabledURLMapping(Map mapping){
        disabledURLMapping = mapping;
    }
    
    // SelectableServletFilterInterceptorChainListServiceMBeanのJavaDoc
    public Map getDisabledURLMapping(){
        return disabledURLMapping;
    }
    
    // SelectableServletFilterInterceptorChainListServiceMBeanのJavaDoc
    public void setDisabledURIMapping(Map mapping){
        disabledURIMapping = mapping;
    }
    
    // SelectableServletFilterInterceptorChainListServiceMBeanのJavaDoc
    public Map getDisabledURIMapping(){
        return disabledPathMapping;
    }
    
    // SelectableServletFilterInterceptorChainListServiceMBeanのJavaDoc
    public void setDisabledPathMapping(Map mapping){
        disabledPathMapping = mapping;
    }
    
    // SelectableServletFilterInterceptorChainListServiceMBeanのJavaDoc
    public Map getDisabledPathMapping(){
        return disabledPathMapping;
    }
    
    // SelectableServletFilterInterceptorChainListServiceMBeanのJavaDoc
    public void setDefaultInterceptorChainListServiceName(ServiceName name){
        defaultInterceptorChainListServiceName = name;
    }
    
    // SelectableServletFilterInterceptorChainListServiceMBeanのJavaDoc
    public ServiceName getDefaultInterceptorChainListServiceName(){
        return defaultInterceptorChainListServiceName;
    }
    
    /**
     * サービスの生成処理を行う。<p>
     *
     * @exception Exception サービスの生成に失敗した場合
     */
    public void createService() throws Exception{
        enabledURLChainMapping = new LinkedHashMap();
        enabledURIChainMapping = new LinkedHashMap();
        enabledPathChainMapping = new LinkedHashMap();
        disabledURLChainMapping = new LinkedHashMap();
        disabledURIChainMapping = new LinkedHashMap();
        disabledPathChainMapping = new LinkedHashMap();
    }
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始に失敗した場合
     */
    public void startService() throws Exception{
        
        final ServiceNameEditor editor = new ServiceNameEditor();
        editor.setServiceManagerName(getServiceManagerName());
        
        if(enabledURLMapping != null && enabledURLMapping.size() != 0){
            initMapping(editor, enabledURLMapping, enabledURLChainMapping);
        }
        
        if(enabledURIMapping != null && enabledURIMapping.size() != 0){
            initMapping(editor, enabledURIMapping, enabledURIChainMapping);
        }
        
        if(enabledPathMapping != null && enabledPathMapping.size() != 0){
            initMapping(editor, enabledPathMapping, enabledPathChainMapping);
        }
        
        if(disabledURLMapping != null && disabledURLMapping.size() != 0){
            initMapping(editor, disabledURLMapping, disabledURLChainMapping);
        }
        
        if(disabledURIMapping != null && disabledURIMapping.size() != 0){
            initMapping(editor, disabledURIMapping, disabledURIChainMapping);
        }
        
        if(disabledPathMapping != null && disabledPathMapping.size() != 0){
            initMapping(editor, disabledPathMapping, disabledPathChainMapping);
        }
        
        if(defaultInterceptorChainListServiceName != null){
            defaultInterceptorChainList
                 = (InterceptorChainList)ServiceManagerFactory
                    .getServiceObject(defaultInterceptorChainListServiceName);
        }
    }
    
    /**
     * サービスの停止処理を行う。<p>
     *
     * @exception Exception サービスの停止に失敗した場合
     */
    public void stopService() throws Exception{
        enabledURLChainMapping.clear();
        enabledURIChainMapping.clear();
        enabledPathChainMapping.clear();
        disabledURLChainMapping.clear();
        disabledURIChainMapping.clear();
        disabledPathChainMapping.clear();
    }
    
    /**
     * サービスの破棄処理を行う。<p>
     *
     * @exception Exception サービスの破棄に失敗した場合
     */
    public void destroyService() throws Exception{
        enabledURLChainMapping = null;
        enabledURIChainMapping = null;
        enabledPathChainMapping = null;
        disabledURLChainMapping = null;
        disabledURIChainMapping = null;
        disabledPathChainMapping = null;
    }
    
    private void initMapping(
        ServiceNameEditor editor,
        Map mapping,
        Map chainMapping
    ){
        final Iterator urls = mapping.keySet().iterator();
        while(urls.hasNext()){
            final String url = (String)urls.next();
            final Pattern pattern = Pattern.compile(url);
            
            final String serviceNameStr = (String)mapping.get(url);
            editor.setAsText(serviceNameStr);
            final ServiceName name = (ServiceName)editor.getValue();;
            final InterceptorChainList chain = (InterceptorChainList)
                ServiceManagerFactory.getServiceObject(name);
            
            chainMapping.put(pattern, chain);
        }
    }
    
    /**
     * 指定されたインデックスのインターセプタを取得する。<p>
     * 引数で指定された呼び出しコンテキスト情報を{@link ServletFilterInvocationContext}にキャストして、リクエストのパス情報を取得し、そのパスにマッピングされた{@link InterceptorChainList}から、インターセプタを取得する。<br>
     *
     * @param context 呼び出しのコンテキスト情報。{@link ServletFilterInvocationContext}でなければならない。
     * @param index インターセプタのチェーン上のインデックス
     * @return 指定されたインデックスのインターセプタ。指定されたインデックスのインターセプタが存在しない場合は、nullを返す
     */
    public Interceptor getInterceptor(InvocationContext context, int index){
        if(getState() != STARTED){
            return null;
        }
        InterceptorChainList chainList = null;
        final ServletFilterInvocationContext filtreContext
             = (ServletFilterInvocationContext)context;
        final ServletRequest request = filtreContext.getServletRequest();
        if(request instanceof HttpServletRequest){
            final HttpServletRequest httpReq = (HttpServletRequest)request;
            if(enabledURLChainMapping.size() != 0){
                final String reqURL = httpReq.getRequestURL().toString();
                chainList = selectInterceptorChainList(
                    reqURL,
                    enabledURLChainMapping,
                    true
                );
            }
            if(chainList == null && enabledURIChainMapping.size() != 0){
                final String reqURI = httpReq.getRequestURI().toString();
                chainList = selectInterceptorChainList(
                    reqURI,
                    enabledURIChainMapping,
                    true
                );
            }
            if(chainList == null && enabledPathChainMapping.size() != 0){
                String reqPath = httpReq.getServletPath();
                if(httpReq.getPathInfo() != null){
                    reqPath = reqPath == null ? httpReq.getPathInfo() : (reqPath + httpReq.getPathInfo());
                }
                chainList = selectInterceptorChainList(
                    reqPath,
                    enabledPathChainMapping,
                    true
                );
            }
            if(chainList == null && disabledURLChainMapping.size() != 0){
                final String reqURL = httpReq.getRequestURL().toString();
                chainList = selectInterceptorChainList(
                    reqURL,
                    disabledURLChainMapping,
                    false
                );
            }
            if(chainList == null && disabledURIChainMapping.size() != 0){
                final String reqURI = httpReq.getRequestURI().toString();
                chainList = selectInterceptorChainList(
                    reqURI,
                    disabledURIChainMapping,
                    false
                );
            }
            if(chainList == null && disabledPathChainMapping.size() != 0){
                String reqPath = httpReq.getPathInfo();
                if(reqPath == null){
                    reqPath = httpReq.getServletPath();
                }
                chainList = selectInterceptorChainList(
                    reqPath,
                    disabledPathChainMapping,
                    false
                );
            }
        }
        if(chainList == null){
            chainList = defaultInterceptorChainList;
        }
        if(chainList == null){
            return null;
        }
        return chainList.getInterceptor(context, index);
    }
    
    private InterceptorChainList selectInterceptorChainList(
        String target,
        Map patternMapping,
        boolean isMatch
    ){
        final Iterator patterns = patternMapping.keySet().iterator();
        while(patterns.hasNext()){
            final Pattern pattern = (Pattern)patterns.next();
            final Matcher m = pattern.matcher(target);
            if(m.matches() == isMatch){
                return (InterceptorChainList)patternMapping.get(pattern);
            }
        }
        return null;
    }
}
