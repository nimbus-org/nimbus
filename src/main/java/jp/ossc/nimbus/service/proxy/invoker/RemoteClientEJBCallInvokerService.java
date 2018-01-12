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
package jp.ossc.nimbus.service.proxy.invoker;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.ejb.*;
import jp.ossc.nimbus.service.aop.*;
import jp.ossc.nimbus.service.proxy.RemoteServerInvoker;
import jp.ossc.nimbus.service.proxy.RemoteServiceCallException;

/**
 * リモートクライアントEJB呼び出しInvoker。<p>
 * EJB経由で、リモートサーバ上のサービスを呼び出すためのInvokerである。<br>
 * リモートサーバ側に、{@link RemoteServerInvoker}インタフェースを実装したEJBがデプロイされていなければならない。従って、{@link jp.ossc.nimbus.service.proxy.RemoteServiceServerSessionBean RemoteServiceServerSessionBean}をリモートサーバ側に、デプロイしておく。<br>
 *
 * @author M.Takata
 */
public class RemoteClientEJBCallInvokerService extends ServiceBase
 implements Invoker, java.io.Serializable,
            RemoteClientEJBCallInvokerServiceMBean{
    
    private static final long serialVersionUID = -7734676901899009764L;
    
    private ServiceName ejbFactoryServiceName;
    private EJBFactory ejbFactory;
    private String jndiName;
    private ServiceName remoteServiceName;
    
    // RemoteClientEJBCallInvokerServiceMBeanのJavaDoc
    public void setEJBFactoryServiceName(ServiceName name){
        ejbFactoryServiceName = name;
    }
    // RemoteClientEJBCallInvokerServiceMBeanのJavaDoc
    public ServiceName getEJBFactoryServiceName(){
        return ejbFactoryServiceName;
    }
    
    // RemoteClientEJBCallInvokerServiceMBeanのJavaDoc
    public void setRemoteServerEJBJndiName(String name){
        jndiName = name;
    }
    // RemoteClientEJBCallInvokerServiceMBeanのJavaDoc
    public String getRemoteServerEJBJndiName(){
        return jndiName;
    }
    
    // RemoteClientEJBCallInvokerServiceMBeanのJavaDoc
    public void setRemoteServiceName(ServiceName name){
        remoteServiceName = name;
    }
    // RemoteClientEJBCallInvokerServiceMBeanのJavaDoc
    public ServiceName getRemoteServiceName(){
        return remoteServiceName;
    }
    
    /**
     * {@link RemoteServerInvoker}インタフェースを実装したEJBを取得する{@link jp.ossc.nimbus.service.ejb.EJBFactory EJBFactory}サービスを設定する。<p>
     *
     * @param ejbFactory EJBFactoryサービス
     */
    public void setEjbFactory(EJBFactory ejbFactory) {
        this.ejbFactory = ejbFactory;
    }
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始処理に失敗した場合
     */
    public void startService() throws Exception{
        if(ejbFactoryServiceName != null){
            ejbFactory = (EJBFactory)ServiceManagerFactory
                .getServiceObject(ejbFactoryServiceName);
        }
        if(ejbFactory == null) {
            throw new IllegalArgumentException(
                "EjbFactoryServiceName or EjbFactory must be specified."
            );
        }
        
        if(jndiName == null){
            throw new IllegalArgumentException(
                "jndiName must be specified."
            );
        }
    }
    
    /**
     * {@link RemoteServerInvoker}インタフェースを実装したEJBを呼び出す。<p>
     * 
     * @param context 呼び出しのコンテキスト情報
     * @return 呼び出し結果の戻り値
     * @exception Throwable 呼び出し先で例外が発生した場合
     */
    public Object invoke(InvocationContext context) throws Throwable{
        final MethodInvocationContext methodContext
             = (MethodInvocationContext)context;
        try{
            final RemoteServerInvoker serverInvoker
                 = (RemoteServerInvoker)ejbFactory.get(jndiName);
            if(remoteServiceName != null){
                methodContext.setTargetObject(remoteServiceName);
            }
            return serverInvoker.invoke(context);
        }catch(javax.naming.NamingException e){
            throw new RemoteServiceCallException(e);
        }catch(javax.ejb.CreateException e){
            throw new RemoteServiceCallException(e);
        }catch(NoSuchMethodException e){
            throw new RemoteServiceCallException(e);
        }catch(IllegalAccessException e){
            throw new RemoteServiceCallException(e);
        }catch(java.lang.reflect.InvocationTargetException e){
            throw new RemoteServiceCallException(e.getTargetException());
        }
    }
}
