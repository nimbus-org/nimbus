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
package jp.ossc.nimbus.serverless.aws;

import java.lang.reflect.Method;

import jp.ossc.nimbus.beans.ServiceNameEditor;
import jp.ossc.nimbus.beans.PropertyAccess;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.service.aop.InterceptorChainFactory;
import jp.ossc.nimbus.service.aop.InterceptorChain;
import jp.ossc.nimbus.service.aop.InvocationContext;
import jp.ossc.nimbus.service.aop.DefaultMethodInvocationContext;

import com.amazonaws.services.lambda.runtime.Context;

/**
 * Nimbusのサービスをロードする機能を持つLambdaアプリケーションのリクエストハンドラ抽象クラス。<p>
 * <table border="1">
 *   <tr bgcolor="#CCCCFF"><th>環境変数名</th><th>値</th></tr>
 *   <tr><td>{@link #ENV_SERVICE_DEFINITION_DIRECTORIES}</td><td>サービス定義ファイルの配置ディレクトリをパスセパレータ区切りで指定する</td></tr>
 *   <tr><td>{@link #ENV_SERVICE_DEFINITION_FILTER}</td><td>サービス定義ファイルの配置ディレクトリの配下の、サービス定義ファイルを特定するフィルタ文字列を指定する</td></tr>
 *   <tr><td>{@link #ENV_SERVICE_DEFINITION_PATHS}</td><td>サービス定義ファイルのパスをパスセパレータ区切りで指定する</td></tr>
 *   <tr><td>{@link #ENV_INTERCEPTOR_CHAIN_FACTORY_SERVICE_NAME}</td><td>{@link InterceptorChainFactory}サービスのサービス名を指定する</td></tr>
 *   <tr><td>{@link #CONTEXT_PROPERTIES}</td><td>初期化時に、com.amazonaws.services.lambda.runtime.Contextから取得して、システムプロパティに転写するプロパティ名を、カンマ区切りで指定する。システムプロパティ名は、com.amazonaws.services.lambda.runtime.Context.プロパティ名となる。</td></tr>
 * </table>
 *
 * @author M.Takata
 */
public abstract class NimbusRequestHandler<I,O,CI,CO>{
    
    protected static final String ENV_SERVICE_DEFINITION_DIRECTORIES = "SERVICE_DEFINITION_DIRECTORIES";
    protected static final String ENV_SERVICE_DEFINITION_FILTER = "SERVICE_DEFINITION_FILTER";
    protected static final String ENV_SERVICE_DEFINITION_PATHS = "SERVICE_DEFINITION_PATHS";
    protected static final String ENV_INTERCEPTOR_CHAIN_FACTORY_SERVICE_NAME = "INTERCEPTOR_CHAIN_FACTORY_SERVICE_NAME";
    protected static final String ENV_CONTEXT_PROPERTIES = "CONTEXT_PROPERTIES";
    
    protected boolean isInitialized;
    protected boolean initializeResult;
    protected InterceptorChainFactory interceptorChainFactory;
    protected Method processHandleRequestMethod;
    protected PropertyAccess propertyAccess;
    
    /**
     * 環境変数からサービス定義ファイルの配置ディレクトリを取得する。<p>
     *
     * @return サービス定義ファイルの配置ディレクトリの配列
     */
    protected String[] getServiceDefinitionDirectories(){
        String value = System.getenv(ENV_SERVICE_DEFINITION_DIRECTORIES);
        if(value == null || value.length() == 0){
            return null;
        }
        return value.split(System.getProperty("path.separator"));
    }
    
    /**
     * 環境変数からサービス定義ファイルを特定するフィルタ文字列を取得する。<p>
     *
     * @return フィルタ文字列
     */
    protected String getServiceDefinitionFilter(){
        return getEnvString(ENV_SERVICE_DEFINITION_FILTER);
    }
    
    /**
     * 環境変数からサービス定義ファイルのパスを取得する。<p>
     *
     * @return サービス定義ファイルのパスの配列
     */
    protected String[] getServiceDefinitionPaths(){
        String value = System.getenv(ENV_SERVICE_DEFINITION_PATHS);
        if(value == null || value.length() == 0){
            return null;
        }
        return value.split(System.getProperty("path.separator"));
    }
    
    /**
     * 環境変数から{@link InterceptorChainFactory}サービスのサービス名を取得する。<p>
     *
     * @return InterceptorChainFactoryサービスのサービス名
     */
    protected ServiceName getInterceptorChainFactoryServiceName(){
        return getEnvServiceName(ENV_INTERCEPTOR_CHAIN_FACTORY_SERVICE_NAME);
    }
    
    /**
     * 環境変数からシステムプロパティに転写するコンテキストのプロパティ名配列を取得する。<p>
     *
     * @return システムプロパティに転写するコンテキストのプロパティ名配列
     */
    protected String[] getContextProperties(){
        String value = System.getenv(ENV_CONTEXT_PROPERTIES);
        if(value == null || value.length() == 0){
            return null;
        }
        return value.split(",");
    }
    
    /**
     * 指定された変数名の環境変数を取得する。<p>
     *
     * @param envName 環境変数名
     * @return 環境変数
     */
    protected String getEnvString(String envName){
        String value = System.getenv(envName);
        if(value == null || value.length() == 0){
            return null;
        }
        return value;
    }
    
    /**
     * 指定された変数名の環境変数サービス名を取得する。<p>
     *
     * @param envName 環境変数名
     * @return 環境変数サービス名
     */
    protected ServiceName getEnvServiceName(String envName){
        String value = System.getenv(envName);
        if(value == null || value.length() == 0){
            return null;
        }
        ServiceNameEditor editor = new ServiceNameEditor();
        editor.setAsText(value);
        return (ServiceName)editor.getValue();
    }
    
    /**
     * 初期化処理を行う。<p>
     * 初回呼び出し時のみ、同期化しながら{@link #init(Context) 初期化処理}を行う。すでに呼び出されている場合は、何もせずに初期化処理結果のみ返す。<br>
     *
     * @param context コンテキスト
     * @return 初期化処理結果
     */
    protected boolean processInit(Context context){
        if(isInitialized){
            return initializeResult;
        }
        synchronized(this){
            if(isInitialized){
                return initializeResult;
            }
            initializeResult = init(context);
            isInitialized = true;
        }
        return initializeResult;
    }
    
    protected void setSystemProperty(Context context, String propertyName){
        try{
            Object value = propertyAccess.get(context, propertyName);
            if(value != null){
                System.setProperty(Context.class.getName() + '.' + propertyName, value.toString());
            }
        }catch(Exception e){
        }
    }
    
    /**
     * 初期化処理を行う。<p>
     * サービス定義の読み込みを行う。<br>
     *
     * @param context コンテキスト
     * @return 初期化処理結果
     */
    protected boolean init(Context context){
        propertyAccess = new PropertyAccess();
        propertyAccess.setIgnoreNullProperty(true);
        
        String[] contextProperties = getContextProperties();
        if(contextProperties != null){
            for(int i = 0; i < contextProperties.length; i++){
                setSystemProperty(context, contextProperties[i]);
            }
        }
        
        String[] dirs = getServiceDefinitionDirectories();
        if(dirs != null){
            String filter = getServiceDefinitionFilter();
            for(int i = 0; i < dirs.length; i++){
                ServiceManagerFactory.loadManagers(dirs[i], filter);
            }
        }
        String[] paths = getServiceDefinitionPaths();
        if(paths != null){
            for(int i = 0; i < paths.length; i++){
                ServiceManagerFactory.loadManager(paths[i]);
            }
        }
        ServiceManagerFactory.loadManager();
        boolean result = ServiceManagerFactory.checkLoadManagerCompleted();
        if(result){
            ServiceName interceptorChainFactoryServiceName = getInterceptorChainFactoryServiceName();
            if(interceptorChainFactoryServiceName != null){
                interceptorChainFactory = (InterceptorChainFactory)ServiceManagerFactory
                    .getServiceObject(interceptorChainFactoryServiceName);
                try{
                    processHandleRequestMethod = getClass().getMethod(
                        "processHandleRequest",
                        new Class[]{Object.class, RequestContext.class}
                    );
                }catch(Exception e){
                }
            }
        }
        return result;
    }
    
    /**
     * 要求コンテキストの生成を行う。<p>
     *
     * @param input 入力
     * @param context コンテキスト
     * @return 要求コンテキスト
     */
    protected RequestContext<CI, CO> processCreateRequestContext(Context context){
        return new RequestContext<CI, CO>(context);
    }
    
    /**
     * 入力変換処理を行う。<p>
     * 入力型から要求コンテキストの入力型へキャストする。<br/>
     *
     * @param input 入力
     * @param context 要求コンテキスト
     * @return 要求コンテキストの入力
     * @exception Throwable 入力変換処理で例外が発生した場合
     */
    protected CI processConvertToInput(I input, RequestContext<CI, CO> context) throws Throwable{
        return (CI)input;
    }
    
    /**
     * 出力変換処理を行う。<p>
     * 要求コンテキストから出力を取り出し出力型へキャストする。<br/>
     *
     * @param context 要求コンテキスト
     * @return 出力
     * @exception Throwable 出力変換処理で例外が発生した場合
     */
    protected O processConvertToOutput(RequestContext<CI, CO> context) throws Throwable{
        return (O)context.getOutput();
    }
    
    /**
     * {@link InterceptorChain}を経由して、{@link #processHandleRequest(RequestContext) リクエスト処理}を行う。<p>
     *
     * @param input 入力
     * @param context 要求コンテキスト
     * @return 出力
     * @exception Throwable リクエスト処理で例外が発生した場合
     */
    protected O processHandleRequestWithInterceptorChain(I input, RequestContext<CI, CO> context) throws Throwable{
        final InterceptorChain chain = interceptorChainFactory.getInterceptorChain(context.getContext().getFunctionName());
        final InvocationContext ic = new DefaultMethodInvocationContext(
            this,
            processHandleRequestMethod,
            new Object[]{input, context}
        );
        return (O)chain.invokeNext(ic);
    }
    
    /**
     * リクエスト処理を行う。<p>
     * <ol>
     * <li>{@link #processConvertToInput(Object,RequestContext) 入力変換処理}を行い、要求コンテキストに入力を設定する。</li>
     * <li>{@link #processValidate(RequestContext) 検証処理}を行う。</li>
     * <li>検証処理に成功した場合、{@link #processRequest(RequestContext) リクエスト処理}を行う。</li>
     * <li>{@link #processConvertToOutput(Object,RequestContext) 出力変換処理}を行う。</li>
     * </ol>
     *
     * @param input 入力
     * @param context 要求コンテキスト
     * @return 出力
     * @exception Throwable リクエスト処理で例外が発生した場合
     */
    public O processHandleRequest(I input, RequestContext<CI, CO> context) throws Throwable{
        context.setInput(processConvertToInput(input, context));
        if(processValidate(context)){
            processRequest(context);
        }
        return processConvertToOutput(context);
    }
    
    /**
     * 検証処理を実装する。<p>
     * デフォルトでは、trueを返すのみ。必要に応じて、オーバーライドしてください。<br>
     *
     * @param context 要求コンテキスト
     * @return 検証が正しく行われた場合は、true。そうでない場合は、false
     * @exception Throwable 検証処理で例外が発生した場合
     */
    protected boolean processValidate(RequestContext<CI, CO> context) throws Throwable{
        return true;
    }
    
    /**
     * リクエスト処理を実装する。<p>
     *
     * @param context 要求コンテキスト
     * @exception Throwable リクエスト処理で例外が発生した場合
     */
    protected abstract void processRequest(RequestContext<CI, CO> context) throws Throwable;
    
    /**
     * 初期化エラー処理を実装する。<p>
     * デフォルトでは、nullを返す。必要に応じて、オーバーライドしてください。<br>
     *
     * @param context コンテキスト
     * @return 出力
     */
    protected O processInitError(Context context){
        return null;
    }
    
    /**
     * 例外が発生した場合のエラー処理を実装する。<p>
     * デフォルトでは、{@link Error}または{@link RuntimeException}の場合は、再スロー。それ以外の場合は、エラーログを出力する。必要に応じて、オーバーライドしてください。<br>
     *
     * @param input 入力
     * @param context 要求コンテキスト
     * @return 出力
     */
    protected O processHandleError(I input, RequestContext<CI, CO> context, Throwable th){
        if(th instanceof Error){
            throw (Error)th;
        }else if(th instanceof RuntimeException){
            throw (RuntimeException)th;
        }else{
            ServiceManagerFactory.getLogger().write("ERROR", "Error occured.", th);
            return null;
        }
    }
}