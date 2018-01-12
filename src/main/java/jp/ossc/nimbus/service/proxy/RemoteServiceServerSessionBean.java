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
package jp.ossc.nimbus.service.proxy;

import java.net.*;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import javax.ejb.*;
import javax.naming.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.beans.*;
import jp.ossc.nimbus.service.aop.*;
import jp.ossc.nimbus.service.aop.invoker.*;
import jp.ossc.nimbus.service.performance.ResourceUsage;

/**
 * リモートサービスサーバEJB実装クラス。<p>
 * リモートサービスのFacadeとなるStateless Session Beanの実装クラスである。<br>
 * このEJB実装クラスを使用する場合、EJBのデプロイメント記述子ejb-jar.xmlの&lt;ejb-class&gt;要素に、このクラスのクラス名を指定する必要がある。<br>
 * <pre>
 *   &lt;ejb-jar&gt;
 *     &lt;enterprise-beans&gt;
 *       &lt;session&gt;
 *         &lt;ejb-name&gt;RemoteServiceServer1&lt;/ejb-name&gt;
 *               :
 *         &lt;ejb-class&gt;jp.ossc.nimbus.service.remote.RemoteServiceServerSessionBean&lt;/ejb-class&gt;
 *               :
 * </pre>
 * 
 * @author M.Takata
 * @see RemoteServiceServerEJBHome
 * @see RemoteServiceServerEJBObject
 */
public class RemoteServiceServerSessionBean implements SessionBean{
    
    private static final long serialVersionUID = -1629897916230733253L;
    
    /**
     * EJBの環境変数をJNDIからlookupする為のJNDIコンテキスト名。<p>
     */
    private static final String JAVA_ENV_KEY = "java:comp/env";
    
    /**
     * &lt;env-entry&gt;要素の子要素&lt;env-entry-name&gt;で指定するリモートサービス名のキー名。<p>
     */
    public static final String REMOTE_SERVICE_NAME_ENV_KEY = "remote-service-name";
    
    /**
     * &lt;env-entry&gt;要素の子要素&lt;env-entry-name&gt;で指定するInterceptorChainListサービス名のキー名。<p>
     */
    public static final String INTERCEPTOR_CHAIN_LIST_SERVICE_NAME_ENV_KEY = "interceptor-chain-list-service-name";
    
    /**
     * &lt;env-entry&gt;要素の子要素&lt;env-entry-name&gt;で指定するInterceptorChainFactoryサービス名のキー名。<p>
     */
    public static final String INTERCEPTOR_CHAIN_FACTORY_SERVICE_NAME_ENV_KEY = "interceptor-chain-factory-service-name";
    
    /**
     * &lt;env-entry&gt;要素の子要素&lt;env-entry-name&gt;で指定するInvokerサービス名のキー名。<p>
     */
    public static final String INVOKER_SERVICE_NAME_ENV_KEY = "invoker-service-name";
    
    /**
     * &lt;env-entry&gt;要素の子要素&lt;env-entry-name&gt;で指定するResourceUsageサービス名のキー名。<p>
     */
    public static final String RESOURCE_USAGE_SERVICE_NAME_ENV_KEY = "resource-usage-service-name";
    
    /**
     * &lt;env-entry&gt;要素の子要素&lt;env-entry-name&gt;で指定するリモートサービス定義ファイルパスのキー名。<p>
     */
    public static final String SERVICE_PATH_ENV_KEY = "service-path";
    
    /**
     * SessionContext オブジェクト。<p>
     */
    private SessionContext sessionContext;
    
    /**
     * リモートサービス名。<p>
     */
    private ServiceName remoteServiceName;
    
    /**
     * InterceptorChainListサービス名。<p>
     */
    private ServiceName interceptorChainListServiceName;
    
    /**
     * InterceptorChainFactoryサービス名。<p>
     */
    private ServiceName interceptorChainFactoryServiceName;
    
    /**
     * Invokerサービス名。<p>
     */
    private ServiceName invokerServiceName;
    
    /**
     * ResourceUsageサービス名。<p>
     */
    private ServiceName resourceUsageServiceName;
    
    /**
     * リモートサービス定義ファイルパス。<p>
     */
    private String servicePath;
    
    private MethodReflectionCallInvokerService defaultInvoker;
    
    /**
     * リモート呼び出しされるサービスを呼び出す。<p>
     * 呼び出しコンテキストの{@link InvocationContext#getTargetObject()}で取得したサービス名のサービスをローカルの{@link ServiceManager}から取得して、{@link InvocationContext#setTargetObject(Object)}で、呼び出しコンテキストに設定する。<br>
     * InvocationContext.getTargetObject()でサービス名が取得できない場合は、&lt;env-entry&gt; "remote-service-name"で設定されたサービス名のサービスを取得して、呼び出しコンテキストに設定する。<br>
     * その後、&lt;env-entry&gt; "interceptor-chain-list-service-name"と"invoker-service-name"で指定された{@link InterceptorChainList}と{@link Invoker}を持った、{@link InterceptorChain}を生成して、呼び出す。<br>
     * 
     * @param context 呼び出しコンテキスト
     * @return サービスの呼び出し結果
     * @exception java.rmi.RemoteException リモート呼び出しされるサービスの呼び出しに失敗した場合
     * @exception Exception リモート呼び出しされるサービスの呼び出しに失敗した場合
     */
    public Object invoke(InvocationContext context)
     throws Exception, java.rmi.RemoteException{
        
        InterceptorChain chain = null;
        if(interceptorChainFactoryServiceName == null){
            chain = new DefaultInterceptorChain(
                interceptorChainListServiceName,
                invokerServiceName
            );
            if(invokerServiceName == null && defaultInvoker != null){
                ((DefaultInterceptorChain)chain).setInvoker(defaultInvoker);
            }
        }else{
            StringBuffer key = new StringBuffer();
            Object target = context.getTargetObject();
            if(target != null){
                key.append(target);
            }
            if(context instanceof MethodInvocationContext){
                Method method = ((MethodInvocationContext)context).getTargetMethod();
                if(method != null){
                    final MethodEditor editor = new MethodEditor();
                    editor.setValue(method);
                    key.append(':').append(editor.getAsText());
                }
            }
            InterceptorChainFactory interceptorChainFactory = (InterceptorChainFactory)ServiceManagerFactory
                    .getServiceObject(interceptorChainFactoryServiceName);
            chain = interceptorChainFactory.getInterceptorChain(key.length() == 0 ? null : key.toString());
        }
        
        ServiceName serviceName = remoteServiceName;
        if(serviceName == null
            && context.getTargetObject() != null
            && context.getTargetObject() instanceof ServiceName){
            serviceName = (ServiceName)context.getTargetObject();
        }
        if(serviceName == null){
            throw new ServiceNotFoundException(null);
        }
        context.setTargetObject(
            ServiceManagerFactory.getServiceObject(serviceName)
        );
        try{
            chain.setCurrentInterceptorIndex(-1);
            return chain.invokeNext(context);
        }catch(Exception e){
            throw e;
        }catch(Throwable e){
            e.printStackTrace();
            return null;
        }finally{
            chain.setCurrentInterceptorIndex(-1);
        }
    }
    
    public boolean isAlive(ServiceName name) throws java.rmi.RemoteException{
        ServiceName serviceName = remoteServiceName;
        if(name != null){
            if(remoteServiceName != null
                 && !remoteServiceName.equals(name)){
                return false;
            }
            serviceName = name;
        }
        if(serviceName == null){
            return true;
        }else{
            try{
                final Service service = ServiceManagerFactory.getService(serviceName);
                return service != null && service.getState() == Service.STARTED;
            }catch(ServiceNotFoundException e){
                return false;
            }
        }
    }
    
    public Comparable getResourceUsage() throws java.rmi.RemoteException{
        return resourceUsageServiceName == null ? null : ((ResourceUsage)ServiceManagerFactory.getServiceObject(resourceUsageServiceName)).getUsage();
    }
    
    /**
     * 関連付けられたSessionContextを設定する。<p>
     * 
     * @param context SessionContextオブジェクト
     * @exception EJBException システムレベルのエラーが原因で障害が発生した場合
     * @exception RemoteException この例外は、EJB 1.0仕様向けに書かれたエンタープライズBean に下位互換性を持たせるためにメソッドのシグニチャーに定義されている。EJB 1.1仕様向けに書かれたエンタープライズBeanは、この例外の代わりにjavax.ejb.EJBExceptionをスローする必要がある。EJB2.0以降の仕様向けに書かれたエンタープライズBeanは、この例外の代わりにjavax.ejb.EJBExceptionをスローする必要がある
     * @see #getSessionContext()
     */
    public void setSessionContext(final SessionContext context)
     throws EJBException, RemoteException{
        sessionContext = context;
    }
    
    /**
     * 関連付けられたSessionContextを取得する。<p>
     * 
     * @return SessionContextオブジェクト
     * @see #setSessionContext(SessionContext)
     */
    public SessionContext getSessionContext(){
        if(sessionContext == null){
            throw new IllegalStateException("session context is invalid");
        }
        return sessionContext;
    }
    
    /**
     * 関連付けられたEJBContextを取得する。<p>
     * 
     * @return EJBContextオブジェクト
     */
    public EJBContext getEJBContext(){
        return getSessionContext();
    }
    
    /**
     * このEJBのリモート参照であるEJBObjectオブジェクトを取得する。<p>
     * 
     * @return EJBObjectオブジェクト
     */
    public EJBObject getEJBObject() {
        return getSessionContext().getEJBObject();
    }
    
    /**
     * activateメソッドは、インスタンスが「非活性化」状態から活性化状態になるときに呼び出される。<p>
     * このインスタンスでは、以前にejbPassivate()メソッドで解放したリソースをすべて取得する必要がある。<br>
     * <p>
     * このメソッドは、トランザクションコンテキストを使用しないで呼び出される。
     *
     * @exception EJBException システムレベルのエラーが原因で障害が発生した場合
     * @exception RemoteException この例外は、EJB 1.0仕様向けに書かれたエンタープライズBean に下位互換性を持たせるためにメソッドのシグニチャーに定義されている。EJB 1.1仕様向けに書かれたエンタープライズBeanは、この例外の代わりにjavax.ejb.EJBExceptionをスローする必要がある。EJB2.0以降の仕様向けに書かれたエンタープライズBeanは、この例外の代わりにjavax.ejb.EJBExceptionをスローする必要がある
     */
    public void ejbActivate() throws EJBException, RemoteException{}
    
    /**
     * passivateメソッドは、インスタンスが「非活性化」状態になる前に呼び出される。<p>
     * このインスタンスでは、あとでejbActivate()メソッドで取得しなおすことができるリソースをすべて解放する必要がある。<br>
     * <p>
     * passivateメソッドが完了したら、このインスタンスは、コンテナがJava Serializationプロトコルを使ってインスタンスの状態を外部化し、保管しておける状態にならなければなりません。<br>
     * <p>
     * このメソッドは、トランザクションコンテキストを使用しないで呼び出されます。<br>
     * 
     * @exception EJBException システムレベルのエラーが原因で障害が発生した場合
     * @exception RemoteException この例外は、EJB 1.0仕様向けに書かれたエンタープライズBean に下位互換性を持たせるためにメソッドのシグニチャーに定義されている。EJB 1.1仕様向けに書かれたエンタープライズBeanは、この例外の代わりにjavax.ejb.EJBExceptionをスローする必要がある。EJB2.0以降の仕様向けに書かれたエンタープライズBeanは、この例外の代わりにjavax.ejb.EJBExceptionをスローする必要がある
     */
    public void ejbPassivate() throws EJBException, RemoteException{}
    
    /**
     * コンテナでは、セッションオブジェクトの有効期間を終わらせる前に、このメソッドを呼び出します。<p>
     * この処理は、クライアントが削除オペレーションを呼び出した結果として、またはコンテナがタイムアウト後にセッションオブジェクトを終了させるときに行われます。<br>
     * <p>
     * このメソッドは、トランザクションコンテキストを使用しないで呼び出されます。<br>
     * 
     * @exception EJBException システムレベルのエラーが原因で障害が発生した場合
     * @exception RemoteException この例外は、EJB 1.0仕様向けに書かれたエンタープライズBean に下位互換性を持たせるためにメソッドのシグニチャーに定義されている。EJB 1.1仕様向けに書かれたエンタープライズBeanは、この例外の代わりにjavax.ejb.EJBExceptionをスローする必要がある。EJB2.0以降の仕様向けに書かれたエンタープライズBeanは、この例外の代わりにjavax.ejb.EJBExceptionをスローする必要がある
     */
    public void ejbRemove() throws EJBException, RemoteException{
        if(defaultInvoker != null){
            defaultInvoker.stop();
            defaultInvoker.destroy();
            defaultInvoker = null;
        }
        
        if(servicePath != null){
            final URL serviceURL
                 = getClass().getClassLoader().getResource(servicePath);
            ServiceManagerFactory.unloadManager(serviceURL);
        }
    }
    
    /**
     * コンテナでは、セッションオブジェクトの有効期間を開始させる前に、このメソッドを呼び出します。<p>
     * この処理は、クライアントが生成オペレーションを呼び出した結果として行われます。<br>
     * <p>
     * このメソッドは、トランザクションコンテキストを使用しないで呼び出されます。<br>
     * 
     * @exception EJBException システムレベルのエラーが原因で障害が発生した場合
     * @exception RemoteException この例外は、EJB 1.0仕様向けに書かれたエンタープライズBean に下位互換性を持たせるためにメソッドのシグニチャーに定義されている。EJB 1.1仕様向けに書かれたエンタープライズBeanは、この例外の代わりにjavax.ejb.EJBExceptionをスローする必要がある。EJB2.0以降の仕様向けに書かれたエンタープライズBeanは、この例外の代わりにjavax.ejb.EJBExceptionをスローする必要がある
     */
    public void ejbCreate() throws EJBException, RemoteException{
        final ServiceNameEditor editor = new ServiceNameEditor();
        
        final String remoteServiceNameStr
             = getEnvProperty(REMOTE_SERVICE_NAME_ENV_KEY);
        if(remoteServiceNameStr != null){
            editor.setAsText(remoteServiceNameStr);
            remoteServiceName = (ServiceName)editor.getValue();
        }
        
        final String interceptorChainListServiceNameStr
             = getEnvProperty(INTERCEPTOR_CHAIN_LIST_SERVICE_NAME_ENV_KEY);
        if(interceptorChainListServiceNameStr != null){
            editor.setAsText(interceptorChainListServiceNameStr);
            interceptorChainListServiceName = (ServiceName)editor.getValue();
        }
        
        final String interceptorChainFactoryServiceNameStr
             = getEnvProperty(INTERCEPTOR_CHAIN_FACTORY_SERVICE_NAME_ENV_KEY);
        if(interceptorChainFactoryServiceNameStr != null){
            editor.setAsText(interceptorChainFactoryServiceNameStr);
            interceptorChainFactoryServiceName = (ServiceName)editor.getValue();
        }
        
        final String invokerServiceNameStr
             = getEnvProperty(INVOKER_SERVICE_NAME_ENV_KEY);
        if(invokerServiceNameStr == null){
            try{
                if(defaultInvoker == null){
                    defaultInvoker = new MethodReflectionCallInvokerService();
                    defaultInvoker.create();
                }
                defaultInvoker.start();
            }catch(Exception e){
                throw new EJBException(e);
            }
        }else{
            editor.setAsText(invokerServiceNameStr);
            invokerServiceName = (ServiceName)editor.getValue();
        }
        
        final String resourceUsageServiceNameStr
             = getEnvProperty(RESOURCE_USAGE_SERVICE_NAME_ENV_KEY);
        if(resourceUsageServiceNameStr != null){
            editor.setAsText(resourceUsageServiceNameStr);
            resourceUsageServiceName = (ServiceName)editor.getValue();
        }
        
        servicePath = getEnvProperty(SERVICE_PATH_ENV_KEY);
        if(servicePath != null){
            final URL serviceURL
                 = getClass().getClassLoader().getResource(servicePath);
            ServiceManagerFactory.loadManager(serviceURL);
        }
    }
    
    /**
     * EJBの環境変数をJNDIからlookupして、取得する。<p>
     *
     * @param name 環境変数名
     * @return 環境変数。見つからない場合は、nullを返す。
     */
    private static String getEnvProperty(String name){
        String value = null;
        try{
            final Context context = new InitialContext();
            final Context env = (Context)context.lookup(JAVA_ENV_KEY);
            value = (String)env.lookup(name);
        }catch(NamingException e){
        }
        return value;
    }
}