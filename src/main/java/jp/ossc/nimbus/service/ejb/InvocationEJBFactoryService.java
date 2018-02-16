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
package jp.ossc.nimbus.service.ejb;

import java.lang.reflect.*;
import javax.naming.*;
import javax.rmi.PortableRemoteObject;
import javax.ejb.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.cache.CacheMap;
import jp.ossc.nimbus.service.jndi.JndiFinder;

/**
 * 汎用EJBファクトリ。<p>
 * 任意のEJBを取得するためのEJBファクトリサービスである。そのため、以下の特定のEJBを取得するメソッドはサポートしていない。<br>
 * <ul>
 *   <li>{@link #get(String)}</li>
 *   <li>{@link #get(String, Object[])}</li>
 * </ul>
 * 以下に、サービス定義例を示す。<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * 
 * &lt;server&gt;
 *     
 *     &lt;manager name="Sample"&gt;
 *         
 *         &lt;service name="EJBFactory"
 *                  code="jp.ossc.nimbus.service.ejb.InvocationEJBFactoryService"&gt;
 *             &lt;attribute name="JndiFinderServiceName"&gt;#JndiFinder&lt;/attribute&gt;
 *             &lt;depends&gt;JndiFinder&lt;/depends&gt;
 *         &lt;/service&gt;
 *         
 *         &lt;service name="JndiFinder"
 *                  code="jp.ossc.nimbus.service.jndi.CachedJndiFinderService"&gt;
 *             &lt;attribute name="Environment"&gt;
 *                 java.naming.factory.initial=org.jnp.interfaces.NamingContextFactory
 *                 java.naming.factory.url.pkgs=org.jboss.naming:org.jnp.interfaces
 *                 java.naming.provider.url=localhost:1099
 *             &lt;/attribute&gt;
 *         &lt;/service&gt;
 *         
 *     &lt;/manager&gt;
 *     
 * &lt;/server&gt;
 * </pre>
 * 
 * @author  M.Takata
 * @see JndiFinder
 * @see CacheMap
 */
public class InvocationEJBFactoryService extends ServiceBase
 implements EJBFactory, InvocationEJBFactoryServiceMBean{
    
    private static final long serialVersionUID = 1678166099743647407L;
    
    /**
     * EJBObjectのキャッシュ。<p>
     *
     * @see #getRemoteCacheMapServiceName()
     * @see #setRemoteCacheMapServiceName(ServiceName)
     */
    protected CacheMap remoteCache;
    
    /**
     * EJBObjectのキャッシュサービスのサービス名。<p>
     * このサービス名のサービスは、{@link CacheMap}を実装している必要がある。<br>
     *
     * @see #getRemoteCacheMapServiceName()
     * @see #setRemoteCacheMapServiceName(ServiceName)
     */
    protected ServiceName remoteCacheServiceName;
    
    /**
     * JndiFinderサービス。<p>
     *
     * @see #getJndiFinderServiceName()
     * @see #setJndiFinderServiceName(ServiceName)
     */
    protected JndiFinder jndiFinder;
    
    /**
     * JndiFinderのキャッシュサービスのサービス名。<p>
     * このサービス名のサービスは、{@link JndiFinder}を実装している必要がある。<br>
     *
     * @see #getJndiFinderServiceName()
     * @see #setJndiFinderServiceName(ServiceName)
     */
    protected ServiceName jndiFinderServiceName;
    
    // InvocationEJBFactoryMBeanのJavaDoc
    public ServiceName getRemoteCacheMapServiceName(){
        return remoteCacheServiceName;
    }
    
    // InvocationEJBFactoryMBeanのJavaDoc
    public void setRemoteCacheMapServiceName(ServiceName serviceName){
        remoteCacheServiceName = serviceName;
    }
    
    // InvocationEJBFactoryMBeanのJavaDoc
    public ServiceName getJndiFinderServiceName(){
        return jndiFinderServiceName;
    }
    
    // InvocationEJBFactoryMBeanのJavaDoc
    public void setJndiFinderServiceName(ServiceName serviceName){
        jndiFinderServiceName = serviceName;
    }
    
    /**
     * JndiFinderを設定する。
     */
    public void setJndiFinder(JndiFinder jndiFinder) {
        this.jndiFinder = jndiFinder;
    }

    /**
     * CacheMapを設定する。
     */
    public void setCacheMap(CacheMap remoteCache) {
        this.remoteCache = remoteCache;
    }

    /**
     * EJBファクトリの初期化処理を行う。<p>
     * ここでは、以下の処理を行う。<br>
     * <ol>
     *   <li>{@link #setRemoteCacheMapServiceName(ServiceName)}で設定されたEJBObjectのキャッシュを設定する。</li>
     *   <li>{@link #setJndiFinderServiceName(ServiceName)}で設定されたJndiFinderサービスを設定する。設定されていない場合は、例外をthrowする。</li>
     * </ol>
     * 
     * @exception Exception InitialContextの初期化に失敗した場合、または、setJndiFinderServiceName(ServiceName)で有効なJndiFinderサービスが設定されていない場合
     */
    public void startService() throws Exception{
        if(remoteCacheServiceName != null){
            remoteCache = (CacheMap)ServiceManagerFactory.getServiceObject(
                remoteCacheServiceName
            );
        }
        if(jndiFinderServiceName != null){
            jndiFinder = (JndiFinder)ServiceManagerFactory.getServiceObject(
                jndiFinderServiceName
            );
        }
        if(jndiFinder == null){
            throw new Exception("Property \"jndiFinder\" is null.");
        }
    }
    
    /**
     * EJBファクトリの破棄処理を行う。<p>
     * ここでは、以下の処理を行う。<br>
     * <ol>
     *   <li>EJBObjectのキャッシュの参照を破棄する。</li>
     *   <li>JndiFinderサービスの参照を破棄する。</li>
     * </ol>
     * 
     * @exception Exception InitialContextのクローズに失敗した場合
     */
    public void destroyService() throws Exception{
        remoteCache = null;
        jndiFinder = null;
    }
    
    /**
     * 未サポート。<p>
     * このEJBファクトリでは、EJBHomeの型を解決できないと、createメソッドのMethodオブジェクトが取得できず、リフレクション呼び出しを行えない。従って、EJBHomeの型が特定できない、このメソッドはサポートできない。<br>
     */
    public EJBObject get(
        String name
    ) throws NamingException, CreateException, NoSuchMethodException,
             IllegalAccessException, InvocationTargetException{
        throw new UnsupportedOperationException();
    }
    
    /**
     * 未サポート。<p>
     * このEJBファクトリでは、EJBLocalHomeの型を解決できないと、createメソッドのMethodオブジェクトが取得できず、リフレクション呼び出しを行えない。従って、EJBLocalHomeの型が特定できない、このメソッドはサポートできない。<br>
     */
    public EJBLocalObject getLocal(
        String name
    ) throws NamingException, CreateException, NoSuchMethodException,
             IllegalAccessException, InvocationTargetException{
        throw new UnsupportedOperationException();
    }
    
    /**
     * 未サポート。<p>
     * このEJBファクトリでは、EJBHomeの型を解決できないと、createメソッドのMethodオブジェクトが取得できず、リフレクション呼び出しを行えない。従って、EJBHomeの型が特定できない、このメソッドはサポートできない。<br>
     */
    public EJBObject get(
        String name,
        Object[] params
    ) throws NamingException, CreateException, NoSuchMethodException,
             IllegalAccessException, InvocationTargetException{
        throw new UnsupportedOperationException();
    }
    
    /**
     * 未サポート。<p>
     * このEJBファクトリでは、EJBLocaHomeの型を解決できないと、createメソッドのMethodオブジェクトが取得できず、リフレクション呼び出しを行えない。従って、EJBLocaHomeの型が特定できない、このメソッドはサポートできない。<br>
     */
    public EJBLocalObject getLocal(
        String name,
        Object[] params
    ) throws NamingException, CreateException, NoSuchMethodException,
             IllegalAccessException, InvocationTargetException{
        throw new UnsupportedOperationException();
    }
    
    /**
     * 指定した名前のEJBのEJBObjectを取得する。<p>
     * 設定された{@link JndiFinder}を使って、指定された名前でEJBHomeをlookupする。また、lookupしたEJBHomeに対して、引数なしのcreateメソッドを呼び出してEJBObjectを取得する。<br>
     *
     * @param name JndiFinderサービスに渡す名前。
     * @param homeType EJBHomeのクラスオブジェクト
     * @return 指定したJNDI名に対応するEJBObject
     * @exception NamingException EJBHomeのlookupに失敗した場合
     * @exception CreateException EJBHomeのcreateメソッドを呼び出した際に例外が発生した場合
     * @exception NoSuchMethodException EJBHomeのcreateメソッドが見つからない場合
     * @exception IllegalAccessException EJBHomeのcreateメソッドを呼び出した時に、アクセス修飾子によるアクセス権限が不正な場合
     * @exception InvocationTargetException EJBHomeのcreateメソッドを呼び出した時に、呼び出し先で何らかの例外が発生した場合
     */
    public EJBObject get(
        String name,
        Class homeType
    ) throws NamingException, CreateException, NoSuchMethodException,
             IllegalAccessException, InvocationTargetException{
        return get(name, homeType, null, null, null);
    }
    
    /**
     * 指定した名前のEJBのEJBLocalObjectを取得する。<p>
     * 設定された{@link JndiFinder}を使って、指定された名前でEJBLocalHomeをlookupする。また、lookupしたEJBLocalHomeに対して、引数なしのcreateメソッドを呼び出してEJBLocalObjectを取得する。<br>
     *
     * @param name JndiFinderサービスに渡す名前。
     * @param homeType EJBLocalHomeのクラスオブジェクト
     * @return 指定したJNDI名に対応するEJBLocalObject
     * @exception NamingException EJBLocalHomeのlookupに失敗した場合
     * @exception CreateException EJBLocalHomeのcreateメソッドを呼び出した際に例外が発生した場合
     * @exception NoSuchMethodException EJBLocalHomeのcreateメソッドが見つからない場合
     * @exception IllegalAccessException EJBLocalHomeのcreateメソッドを呼び出した時に、アクセス修飾子によるアクセス権限が不正な場合
     * @exception InvocationTargetException EJBLocalHomeのcreateメソッドを呼び出した時に、呼び出し先で何らかの例外が発生した場合
     */
    public EJBLocalObject getLocal(
        String name,
        Class homeType
    ) throws NamingException, CreateException, NoSuchMethodException,
             IllegalAccessException, InvocationTargetException{
        return getLocal(name, homeType, null, null, null);
    }
    
    /**
     * 指定した名前のEJBのEJBObjectを取得する。<p>
     * 設定された{@link JndiFinder}を使って、指定された名前でEJBHomeをlookupする。また、lookupしたEJBHomeに対して、指定した引数のcreateメソッドを呼び出してEJBObjectを取得する。<br>
     *
     * @param name JndiFinderサービスに渡す名前。
     * @param homeType EJBHomeのクラスオブジェクト
     * @param paramTypes 引数の型配列
     * @param params 引数の配列
     * @return 指定したJNDI名に対応するEJBObject
     * @exception NamingException EJBHomeのlookupに失敗した場合
     * @exception CreateException EJBHomeのcreateメソッドを呼び出した際に例外が発生した場合
     * @exception NoSuchMethodException EJBHomeのcreateメソッドが見つからない場合
     * @exception IllegalAccessException EJBHomeのcreateメソッドを呼び出した時に、アクセス修飾子によるアクセス権限が不正な場合
     * @exception InvocationTargetException EJBHomeのcreateメソッドを呼び出した時に、呼び出し先で何らかの例外が発生した場合
     */
    public EJBObject get(
        String name,
        Class homeType,
        Class[] paramTypes,
        Object[] params
    ) throws NamingException, CreateException, NoSuchMethodException,
             IllegalAccessException, InvocationTargetException{
        return get(name, homeType, null, paramTypes, params);
    }
    
    /**
     * 指定した名前のEJBのEJBLocalObjectを取得する。<p>
     * 設定された{@link JndiFinder}を使って、指定された名前でEJBLocalHomeをlookupする。また、lookupしたEJBLocalHomeに対して、指定した引数のcreateメソッドを呼び出してEJBLocalObjectを取得する。<br>
     *
     * @param name JndiFinderサービスに渡す名前。
     * @param homeType EJBLocalHomeのクラスオブジェクト
     * @param paramTypes 引数の型配列
     * @param params 引数の配列
     * @return 指定したJNDI名に対応するEJBLocalObject
     * @exception NamingException EJBLocalHomeのlookupに失敗した場合
     * @exception CreateException EJBLocalHomeのcreateメソッドを呼び出した際に例外が発生した場合
     * @exception NoSuchMethodException EJBLocalHomeのcreateメソッドが見つからない場合
     * @exception IllegalAccessException EJBLocalHomeのcreateメソッドを呼び出した時に、アクセス修飾子によるアクセス権限が不正な場合
     * @exception InvocationTargetException EJBLocalHomeのcreateメソッドを呼び出した時に、呼び出し先で何らかの例外が発生した場合
     */
    public EJBLocalObject getLocal(
        String name,
        Class homeType,
        Class[] paramTypes,
        Object[] params
    ) throws NamingException, CreateException, NoSuchMethodException,
             IllegalAccessException, InvocationTargetException{
        return getLocal(name, homeType, null, paramTypes, params);
    }
    
    /**
     * 指定した名前のEJBのEJBObjectを取得する。<p>
     * 設定された{@link JndiFinder}を使って、指定された名前でEJBHomeをlookupする。また、lookupしたEJBHomeに対して、指定した引数のcreateメソッドを呼び出してEJBObjectを取得して、EJBObjectを目的のタイプにキャストして返す。<br>
     *
     * @param name JndiFinderサービスに渡す名前。
     * @param homeType EJBHomeのクラスオブジェクト
     * @param remoteType EJBObjectのクラスオブジェクト
     * @param paramTypes 引数の型配列
     * @param params 引数の配列
     * @return 指定したJNDI名に対応するEJBObject
     * @exception NamingException EJBHomeのlookupに失敗した場合
     * @exception CreateException EJBHomeのcreateメソッドを呼び出した際に例外が発生した場合
     * @exception NoSuchMethodException EJBHomeのcreateメソッドが見つからない場合
     * @exception IllegalAccessException EJBHomeのcreateメソッドを呼び出した時に、アクセス修飾子によるアクセス権限が不正な場合
     * @exception InvocationTargetException EJBHomeのcreateメソッドを呼び出した時に、呼び出し先で何らかの例外が発生した場合
     */
    public EJBObject get(
        String name,
        Class homeType,
        Class remoteType,
        Class[] paramTypes,
        Object[] params
    ) throws NamingException, CreateException, NoSuchMethodException,
             IllegalAccessException, InvocationTargetException{
        EJBObject remote = null;
        RemoteKey key = null;
        if(remoteCache != null){
            key = new RemoteKey(name, params);
            remote = (EJBObject)remoteCache.get(key);
        }
        final EJBHome home = createHome(name, homeType);
        if(remote == null){
            remote = createRemote(home, homeType, paramTypes, params);
            
            if(remoteCache != null){
                remoteCache.put(key, remote);
            }
        }
        if(remoteType == null){
            return remote;
        }
        return (EJBObject)PortableRemoteObject.narrow(remote, remoteType);
    }
    
    /**
     * 指定した名前のEJBのEJBLocalObjectを取得する。<p>
     * 設定された{@link JndiFinder}を使って、指定された名前でEJBLocalHomeをlookupする。また、lookupしたEJBLocalHomeに対して、指定した引数のcreateメソッドを呼び出してEJBObjectを取得して、EJBObjectを目的のタイプにキャストして返す。<br>
     *
     * @param name JndiFinderサービスに渡す名前。
     * @param homeType EJBLocalHomeのクラスオブジェクト
     * @param localType EJBLocalObjectのクラスオブジェクト
     * @param paramTypes 引数の型配列
     * @param params 引数の配列
     * @return 指定したJNDI名に対応するEJBLocalObject
     * @exception NamingException EJBLocalHomeのlookupに失敗した場合
     * @exception CreateException EJBLocalHomeのcreateメソッドを呼び出した際に例外が発生した場合
     * @exception NoSuchMethodException EJBLocalHomeのcreateメソッドが見つからない場合
     * @exception IllegalAccessException EJBLocalHomeのcreateメソッドを呼び出した時に、アクセス修飾子によるアクセス権限が不正な場合
     * @exception InvocationTargetException EJBLocalHomeのcreateメソッドを呼び出した時に、呼び出し先で何らかの例外が発生した場合
     */
    public EJBLocalObject getLocal(
        String name,
        Class homeType,
        Class localType,
        Class[] paramTypes,
        Object[] params
    ) throws NamingException, CreateException, NoSuchMethodException,
             IllegalAccessException, InvocationTargetException{
        EJBLocalObject local = null;
        final EJBLocalHome home = createLocalHome(name, homeType);
        if(local == null){
            local = createLocal(home, homeType, paramTypes, params);
        }
        if(localType == null){
            return local;
        }
        return (EJBLocalObject)PortableRemoteObject.narrow(local, localType);
    }
    
    /**
     * 指定した名前のEJBのEJBHomeを取得して、指定された型にキャストする。<p>
     * {@link JndiFinder#lookup(String)}で、EJBHomeを取得して、指定された型にキャストする。<br>
     * 
     * @param name JndiFinderサービスに渡す名前。
     * @param type EJBHomeをキャストする型
     * @return EJBHomeオブジェクト
     * @exception NamingException lookupに失敗した場合
     */
    protected EJBHome createHome(String name, Class type)
     throws NamingException{
        final EJBHome home = (EJBHome)jndiFinder.lookup(name);
        if(type == null){
            return home;
        }
        return (EJBHome)PortableRemoteObject.narrow(home, type);
    }
    
    /**
     * 指定した名前のEJBのEJBLocalHomeを取得して、指定された型にキャストする。<p>
     * {@link JndiFinder#lookup(String)}で、EJBLocalHomeを取得して、指定された型にキャストする。<br>
     * 
     * @param name JndiFinderサービスに渡す名前。
     * @param type EJBLocalHomeをキャストする型
     * @return EJBLocalHomeオブジェクト
     * @exception NamingException lookupに失敗した場合
     */
    protected EJBLocalHome createLocalHome(String name, Class type)
     throws NamingException{
        final EJBLocalHome home = (EJBLocalHome)jndiFinder.lookup(name);
        if(type == null){
            return home;
        }
        return (EJBLocalHome)PortableRemoteObject.narrow(home, type);
    }
    
    /**
     * EJBObjectを生成する。<p>
     * リフレクションAPIを使って、EJBHomeのcreateメソッドを呼び出す。<br>
     *
     * @param home EJBHomeオブジェクト
     * @param homeType EJBHomeクラス
     * @param paramTypes createメソッドの引数の型配列
     * @param params createメソッドの引数配列
     * @return EJBObjectオブジェクト
     * @exception CreateException EJBHomeのcreateメソッドを呼び出した際に例外が発生した場合
     * @exception NoSuchMethodException EJBHomeのcreateメソッドが見つからない場合
     * @exception IllegalAccessException EJBHomeのcreateメソッドを呼び出した時に、アクセス修飾子によるアクセス権限が不正な場合
     * @exception InvocationTargetException EJBHomeのcreateメソッドを呼び出した時に、呼び出し先で何らかの例外が発生した場合
     */
    protected EJBObject createRemote(
        EJBHome home,
        Class homeType,
        Class[] paramTypes,
        Object[] params
    ) throws CreateException, NoSuchMethodException, IllegalAccessException,
             InvocationTargetException{
        final Method create
             = homeType.getMethod(EJB_CREATE_METHOD_NAME, paramTypes);
        try{
            return (EJBObject)create.invoke(home, params);
        }catch(InvocationTargetException e){
            Throwable th = e.getTargetException();
            if(th instanceof CreateException){
                throw (CreateException)th;
            }else{
                throw e;
            }
        }
    }
    
    /**
     * EJBLocalObjectを生成する。<p>
     * リフレクションAPIを使って、EJBLocalHomeのcreateメソッドを呼び出す。<br>
     *
     * @param home EJBLocalHomeオブジェクト
     * @param homeType EJBLocalHomeクラス
     * @param paramTypes createメソッドの引数の型配列
     * @param params createメソッドの引数配列
     * @return EJBLocalObjectオブジェクト
     * @exception CreateException EJBLocalHomeのcreateメソッドを呼び出した際に例外が発生した場合
     * @exception NoSuchMethodException EJBLocalHomeのcreateメソッドが見つからない場合
     * @exception IllegalAccessException EJBLocalHomeのcreateメソッドを呼び出した時に、アクセス修飾子によるアクセス権限が不正な場合
     * @exception InvocationTargetException EJBLocalHomeのcreateメソッドを呼び出した時に、呼び出し先で何らかの例外が発生した場合
     */
    protected EJBLocalObject createLocal(
        EJBLocalHome home,
        Class homeType,
        Class[] paramTypes,
        Object[] params
    ) throws CreateException, NoSuchMethodException, IllegalAccessException,
             InvocationTargetException{
        final Method create
             = homeType.getMethod(EJB_CREATE_METHOD_NAME, paramTypes);
        try{
            return (EJBLocalObject)create.invoke(home, params);
        }catch(InvocationTargetException e){
            Throwable th = e.getTargetException();
            if(th instanceof CreateException){
                throw (CreateException)th;
            }else{
                throw e;
            }
        }
    }
    
    /**
     * 指定した名前のEJBのキャッシュを無効化する。<p>
     * {@link #setJndiFinderServiceName(ServiceName)}で、JndiFinderが設定されている場合は、JndiFinderのキャッシュから指定されたJNDI名のEJBHomeを削除する。<br>
     * {@link #setRemoteCacheMapServiceName(ServiceName)}で、EJBObjectのキャッシュが設定されている場合は、そのキャッシュから指定されたJNDI名のEJBObjectを削除する。<br>
     * 
     * @param name JndiFinderサービスに渡す名前。
     */
    public void invalidate(String name){
        if(jndiFinder != null){
            jndiFinder.clearCache(name);
        }
        if(remoteCache != null){
            remoteCache.remove(new RemoteKey(name));
        }
    }
    
    /**
     * EJBのキャッシュを無効化する。<p>
     * {@link #setJndiFinderServiceName(ServiceName)}で、JndiFinderが設定されている場合は、JndiFinderのキャッシュを削除する。<br>
     * {@link #setRemoteCacheMapServiceName(ServiceName)}で、EJBObjectのキャッシュが設定されている場合は、そのキャッシュを削除する。<br>
     */
    public void invalidate(){
        if(jndiFinder != null){
            jndiFinder.clearCache();
        }
        if(remoteCache != null){
            remoteCache.clear();
        }
    }
    
    /**
     * EJBObjectを識別するためのキー。<p>
     * EJBHomeのJNDI名と、EJBObjectを生成する際に使用するEJBHomeのcreateメソッドの引数配列を合わせて、一意なキーとする。<br>
     *
     * @author M.Takata
     */
    protected class RemoteKey{
        
        /**
         * EJBHomeのJNDI名。<p>
         */
        private final String jndiName;
        
        /**
         * EJBHomeのcreateメソッドの引数配列。<p>
         */
        private final Object[] params;
        
        /**
         * 同じEJBHomeから生成された全てのEJBObjectのキーである事を示すフラグ。<p>
         */
        private boolean isAll;
        
        /**
         * 同じEJBHomeから生成された全てのEJBObjectのキーとしてのインスタンスを生成する。<p>
         * 
         * @param jndiName EJBHomeのJNDI名
         */
        public RemoteKey(String jndiName){
            this(jndiName, null);
            isAll = true;
        }
        
        /**
         * 指定されたJNDI名のEJBHomeから、指定された引数配列でcreateされたEJBObjectのキーとしてのインスタンスを生成する。<p>
         *
         * @param jndiName EJBHomeのJNDI名
         * @param params EJBHomeのcreateメソッドの引数配列
         */
        public RemoteKey(String jndiName, Object[] params){
            this.jndiName = jndiName;
            this.params = params;
        }
        
        /**
         * このオブジェクトと他のオブジェクトが等しいかどうかを示す。<p>
         * JNDI名と、createメソッドの引数配列の比較を行い、等しい場合にtrueを返す。但し、比較対象のRemoteKeyインスタンスがRemoteKey(String)で生成されている場合は、JNDI名の比較のみ行う。<br>
         *
         * @param obj 比較対象の参照オブジェクト
         * @return 引数に指定されたオブジェクトとこのオブジェクトが等しい場合は true、そうでない場合は false
         */
        public boolean equals(Object obj){
            if(obj == null){
                return false;
            }
            if(obj == this){
                return true;
            }
            if(!(obj instanceof RemoteKey)){
                return false;
            }
            final RemoteKey key = (RemoteKey)obj;
            if(!jndiName.equals(key.jndiName)){
                return false;
            }else if(key.isAll){
                return true;
            }else if(params == null){
                return key.params == null;
            }else if(key.params == null){
                return false;
            }else if(params.length != key.params.length){
                return false;
            }else{
                boolean result = true;
                for(int i = 0; i < params.length; i++){
                    if(params[i] == null){
                        if(key.params[i] != null){
                            return false;
                        }
                    }else if(!params[i].equals(key.params[i])){
                        return false;
                    }
                }
                return result;
            }
        }
        
        /**
         * オブジェクトのハッシュコード値を返す。<p>
         *
         * @return このオブジェクトのハッシュコード値
         */
        public int hashCode(){
            if(params != null){
                return jndiName.hashCode() + params.hashCode();
            }else{
                return jndiName.hashCode();
            }
        }
    }
}
