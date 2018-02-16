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
package jp.ossc.nimbus.service.jndi;

import java.io.*;
import java.util.*;

import javax.naming.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.daemon.*;
import jp.ossc.nimbus.service.cache.*;
import jp.ossc.nimbus.service.keepalive.*;

/**
 * リモートオブジェクトキャッシュ付きJndiFinderサービス。<p>
 * JNDIのIntialContextの初期化プロパティを属性として設定できる。<br>
 * また、{@link jp.ossc.nimbus.service.cache.CacheMap キャッシュマップ}サービスを属性として設定すると、このJndiFinderでlookupしたリモートオブジェクトをキャッシュする事ができる。<br>
 * さらに、lookup時の通信エラーのリトライや、JNDIサーバの定期的な生存チェックなどの機能を持つ。<br>
 * <p>
 * 以下に、サービス定義例を示す。<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * 
 * &lt;server&gt;
 *     
 *     &lt;manager name="Sample"&gt;
 *         
 *         &lt;service name="JndiFinder"
 *                  code="jp.ossc.nimbus.service.jndi.CachedJndiFinderService"&gt;
 *             &lt;attribute name="Environment"&gt;
 *                 java.naming.factory.initial=org.jnp.interfaces.NamingContextFactory
 *                 java.naming.factory.url.pkgs=org.jboss.naming:org.jnp.interfaces
 *                 java.naming.provider.url=localhost
 *             &lt;/attribute&gt;
 *         &lt;/service&gt;
 *         
 *     &lt;/manager&gt;
 *     
 * &lt;/server&gt;
 * </pre>
 * 
 * @author Y.Tokuda
 * @see CacheMap
 */
public class CachedJndiFinderService extends ServiceBase
 implements JndiFinder, DaemonRunnable, KeepAliveChecker,
            CachedJndiFinderServiceMBean {
    
    private static final long serialVersionUID = 2361330897642105726L;
    
    /**
     * JNDI Lookup時の接頭辞のデフォルト値。<p>
     */
    private static final String C_NONE = ""; //$NON-NLS-1$
    
    /**
     * JNDIサーバ生存確認用のJNDI名。<p>
     */
    private static final String ROOT_CONTEXT = "/";
    
    /**
     * JNDI IntialContext環境プロパティ。<p>
     */
    private Properties contextEnv;
    
    /**
     * JNDIコンテキスト。<p>
     */
    private InitialContext initialCtx;
    
    /**
     * リモートオブジェクトキャッシュサービス名。<p>
     */
    private ServiceName remoteObjCacheServiceName;
    
    /**
     * リモートオブジェクトキャッシュサービス。<p>
     */
    private CacheMap remoteObjCache;
    
    /**
     * JNDIプレフィクス。<p>
     * デフォルトは空文字。<br>
     */
    private String jndiPrefix = C_NONE;
    
    /**
     * lookupエラー時のリトライ回数。<p>
     * デフォルトは、リトライなし。<br>
     */
    private int lookupRetryCount = 0;
    
    /**
     * lookupエラー時のリトライ間隔 [msec]。<p>
     * デフォルトは、1秒。<br>
     */
    private long retryInterval = 1000;
    
    /**
     * リトライ対象の例外クラス名配列。<p>
     */
    private String[] retryExceptionClassNames = DEFAULT_RETRY_EXCXEPTION_NAME;
    
    /**
     * リトライ対象の例外クラス配列。<p>
     */
    private Class[] retryExceptionClasses;
    
    /**
     * JNDIサーバの生存確認をするかどうかのフラグ。<p>
     */
    private boolean isAliveCheckJNDIServer;
    
    /**
     * JNDIサーバの生存しているかどうかのフラグ。<p>
     */
    private boolean isAliveJNDIServer;
    
    /**
     * JNDIサーバの生存確認をする間隔[msec]。<p>
     */
    private long aliveCheckJNDIServerInterval = 60000;
    
    /**
     * {@link Daemon}オブジェクト。<p>
     */
    private Daemon daemon;
    
    private boolean isLoggingDeadJNDIServer = true;
    
    private boolean isLoggingRecoverJNDIServer = true;
    
    private String deadJNDIServerLogMessageId = JNDI_SERVER_DEAD_MSG_ID;
    
    private String recoverJNDIServerLogMessageId = JNDI_SERVER_RECOVER_MSG_ID;
    
    private List keepAliveListeners;
    
    /**
     * サービスの生成処理を行う。<p>
     *
     * @exception Exception サービスの生成処理に失敗した場合
     */
    public void createService() throws Exception{
        daemon = new Daemon(this);
        daemon.setName("Nimbus JndiCheckDaemon " + getServiceNameObject());
        keepAliveListeners = new ArrayList();
    }
    
    /**
     * CacheMapを設定する。
     */
    public void setCacheMap(CacheMap remoteObjCache) {
        this.remoteObjCache = remoteObjCache;
    }

    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始処理に失敗した場合
     */
    public void startService() throws Exception{
        
        if(retryExceptionClassNames != null
             && retryExceptionClassNames.length != 0){
            final ClassLoader loader = NimbusClassLoader.getInstance();
            retryExceptionClasses = new Class[retryExceptionClassNames.length];
            for(int i = 0; i < retryExceptionClassNames.length; i++){
                retryExceptionClasses[i] = Class.forName(
                    retryExceptionClassNames[i],
                    true,
                    loader
                );
            }
        }
        
        //キャッシュサービスの取得
        if(remoteObjCacheServiceName != null){
            remoteObjCache = (CacheMap)ServiceManagerFactory
                .getServiceObject(remoteObjCacheServiceName);
        }
        
        if(contextEnv == null){
            initialCtx = new InitialContext();
        }else{
            initialCtx = new InitialContext(contextEnv);
        }
        
        isAliveJNDIServer = true;
        
        if(isAliveCheckJNDIServer){
            // デーモン起動
            daemon.start();
        }
    }
    
    /**
     * サービスの停止処理を行う。<p>
     *
     * @exception Exception サービスの停止処理に失敗した場合
     */
    public void stopService() throws Exception{
        
        // デーモン停止
        daemon.stop();
        
        initialCtx.close();
    }
    
    /**
     * サービスの破棄処理を行う。<p>
     *
     * @exception Exception サービスの破棄処理に失敗した場合
     */
    public void destory() throws Exception{
        initialCtx = null;
        remoteObjCache = null;
        contextEnv = null;
        remoteObjCacheServiceName = null;
        retryExceptionClasses = null;
        daemon = null;
        keepAliveListeners = null;
    }
    
    // JndiFinderのJavaDoc
    public Object lookup() throws NamingException{
        return lookup(C_NONE);
    }
    
    private boolean isRetryException(NamingException e){
        if(retryExceptionClasses != null && retryExceptionClasses.length != 0){
            for(int i = 0; i < retryExceptionClasses.length; i++){
                if(retryExceptionClasses[i].isInstance(e)){
                    return true;
                }
            }
        }
        return false;
    }
    
    // JndiFinderのJavaDoc
    public Object lookup(String name) throws NamingException{
        Object result = null;
        String key = jndiPrefix + name;
        
        //キャッシュサービスが設定されているなら、キャッシュサービスから探す
        if(remoteObjCache != null){
            result = remoteObjCache.get(key);
            if(result != null){
                return result;
            }
        }
        
        result = lookupInternal(key);
        
        // lookupでコンテキストを取得でき、且つキャッシュモードであれば、
        // 取得したContextをキャッシュ
        if(result != null && remoteObjCache != null){
            remoteObjCache.put(key, result);
        }
        
        return result;
    }
    
    private Object lookupInternal(String key) throws NamingException{
        Object result = null;
        
        try{
            result = initialCtx.lookup(key);
        }catch(NamingException e){
            //時間をおいてリトライする。
            if(lookupRetryCount <= 0 || !isRetryException(e)){
                throw e;
            }
        }
        
        if(result == null){
            for(int rcont = 0; rcont < lookupRetryCount; rcont++){
                //リトライ時間sleep
                try{
                    Thread.sleep(retryInterval);
                }catch(InterruptedException e){}
                
                try{
                    result = initialCtx.lookup(key);
                    break;
                }catch(NamingException e){
                    //時間をおいてリトライする。
                    if(rcont == lookupRetryCount - 1
                         || !isRetryException(e)){
                        throw e;
                    }
                }
            }
        }
        
        return result; 
    }
    
    // KeepAliveCheckerのJavaDoc
    public void addKeepAliveListener(KeepAliveListener listener){
        synchronized(keepAliveListeners){
            keepAliveListeners.add(listener);
        }
    }
    
    // KeepAliveCheckerのJavaDoc
    public void removeKeepAliveListener(KeepAliveListener listener){
        synchronized(keepAliveListeners){
            keepAliveListeners.remove(listener);
        }
    }
    
    // KeepAliveCheckerのJavaDoc
    public void clearKeepAliveListener(){
        synchronized(keepAliveListeners){
            keepAliveListeners.clear();
        }
    }
    
    /**
     * デーモンが開始した時に呼び出される。<p>
     * 
     * @return 常にtrueを返す
     */
    public boolean onStart(){
        return true;
    }
    
    /**
     * デーモンが停止した時に呼び出される。<p>
     * 
     * @return 常にtrueを返す
     */
    public boolean onStop(){
        return true;
    }
    
    /**
     * デーモンが中断した時に呼び出される。<p>
     * 
     * @return 常にtrueを返す
     */
    public boolean onSuspend(){
        return true;
    }
    
    /**
     * デーモンが再開した時に呼び出される。<p>
     * 
     * @return 常にtrueを返す
     */
    public boolean onResume(){
        return true;
    }
    
    /**
     * 一定時間sleep後にルートコンテキストをlookupして返す。<p>
     * 
     * @param ctrl DaemonControlオブジェクト
     * @return ルートコンテキストオブジェクトまたはNamingException
     */
    public Object provide(DaemonControl ctrl){
        try{
            ctrl.sleep(aliveCheckJNDIServerInterval, true);
        }catch(InterruptedException e){
            Thread.interrupted();
        }
        if(isAliveCheckJNDIServer){
            try{
                return lookupInternal(ROOT_CONTEXT);
            }catch(NamingException e){
                return e;
            }
        }else{
            return null;
        }
    }
    
    /**
     * 引数lookupedObjで渡されたオブジェクトを消費する。<p>
     * isAliveJNDIServerがtrueの状態で、引数lookupedObjで渡されたオブジェクトがNamingExceptionの場合、JNDIサーバが死んだ旨のエラーログを出力する。<br>
     * isAliveJNDIServerがfalseの状態で、引数lookupedObjで渡されたオブジェクトがNamingExceptionでない場合、JNDIサーバが復帰した旨の通知ログを出力する。<br>
     *
     * @param lookupedObj ルートコンテキストオブジェクト
     * @param ctrl DaemonControlオブジェクト
     */
    public void consume(Object lookupedObj, DaemonControl ctrl){
        if(!isAliveCheckJNDIServer){
            return;
        }
        if(isAliveJNDIServer){
            if(lookupedObj instanceof NamingException){
                isAliveJNDIServer = false;
                clearCache();
                synchronized(keepAliveListeners){
                    final Iterator itr = keepAliveListeners.iterator();
                    while(itr.hasNext()){
                        final KeepAliveListener keepAliveListener
                             = (KeepAliveListener)itr.next();
                        keepAliveListener.onDead(this);
                    }
                }
                // エラーログ出力
                if(isLoggingDeadJNDIServer){
                    getLogger().write(
                        deadJNDIServerLogMessageId,
                        getJNDIServerInfo(),
                        (NamingException)lookupedObj
                    );
                }
            }
        }else{
            if(!(lookupedObj instanceof NamingException)){
                isAliveJNDIServer = true;
                synchronized(keepAliveListeners){
                    final Iterator itr = keepAliveListeners.iterator();
                    while(itr.hasNext()){
                        final KeepAliveListener keepAliveListener
                             = (KeepAliveListener)itr.next();
                        keepAliveListener.onRecover(this);
                    }
                }
                if(isLoggingRecoverJNDIServer){
                    // 通知ログ出力
                    getLogger().write(
                        recoverJNDIServerLogMessageId,
                        getJNDIServerInfo()
                    );
                }
            }
        }
    }
    
    private Object getJNDIServerInfo(){
        Object result = null;
        try{
            result = getEnvironment().get("java.naming.provider.url");
        }catch(NamingException e){
        }
        if(result == null){
            result = "localhost";
        }
        return result;
    }
    
    /**
     * 何もしない。<p>
     */
    public void garbage(){
    }
    
    // CachedJndiFinderServiceMBeanのJavaDoc
    public void setEnvironment(Properties prop){
        contextEnv = prop;
    }
    
    // CachedJndiFinderServiceMBeanのJavaDoc
    public Properties getEnvironment() throws NamingException{
        if(contextEnv != null){
            return contextEnv;
        }else if(initialCtx != null){
            final Properties prop = new Properties();
            prop.putAll(initialCtx.getEnvironment());
            return prop;
        }
        return null;
    }
    
    // CachedJndiFinderServiceMBeanのJavaDoc
    public void setCacheMapServiceName(ServiceName name){
        remoteObjCacheServiceName = name;
    }
    
    // CachedJndiFinderServiceMBeanのJavaDoc
    public ServiceName getCacheMapServiceName(){
        return remoteObjCacheServiceName;
    }
    
    // CachedJndiFinderServiceMBeanのJavaDoc
    public void setPrefix(String prefix){
        jndiPrefix = prefix;
    }
    
    // CachedJndiFinderServiceMBeanのJavaDoc
    public String getPrefix(){
        return jndiPrefix;
    }
    
    // CachedJndiFinderServiceMBeanのJavaDoc
    public void setRetryCount(int num){
        lookupRetryCount = num;
    }
    
    // CachedJndiFinderServiceMBeanのJavaDoc
    public int getRetryCount(){
        return lookupRetryCount;
    }
    
    // CachedJndiFinderServiceMBeanのJavaDoc
    public void setRetryInterval(long interval){
        retryInterval = interval;
    }
    
    // CachedJndiFinderServiceMBeanのJavaDoc
    public long getRetryInterval(){
        return retryInterval;
    }
    
    // CachedJndiFinderServiceMBeanのJavaDoc
    public void setRetryExceptionClassNames(String[] classNames){
        retryExceptionClassNames = classNames;
    }
    
    // CachedJndiFinderServiceMBeanのJavaDoc
    public String[] getRetryExceptionClassNames(){
        return retryExceptionClassNames;
    }
    
    // CachedJndiFinderServiceMBeanのJavaDoc
    public void setAliveCheckJNDIServer(boolean isCheck){
        isAliveCheckJNDIServer = isCheck;
        if(isCheck && getState() == STARTED && !daemon.isRunning()){
            daemon.start();
        }
    }
    
    // CachedJndiFinderServiceMBeanのJavaDoc
    public boolean isAliveCheckJNDIServer(){
        return isAliveCheckJNDIServer;
    }
    
    // CachedJndiFinderServiceMBeanのJavaDoc
    public void setAliveCheckJNDIServerInterval(long interval){
        aliveCheckJNDIServerInterval = interval;
    }
    
    // CachedJndiFinderServiceMBeanのJavaDoc
    public long getAliveCheckJNDIServerInterval(){
        return aliveCheckJNDIServerInterval;
    }
    
    // CachedJndiFinderServiceMBeanのJavaDoc
    public boolean isAliveJNDIServer(){
        if(getState() != STARTED){
            return false;
        }else if(isAliveCheckJNDIServer){
            return isAliveJNDIServer;
        }else{
            try{
                lookupInternal(ROOT_CONTEXT);
                return true;
            }catch(NamingException e){
                return false;
            }
        }
    }
    
    // KeepAliveCheckerのJavaDoc
    public boolean isAlive(){
        return isAliveJNDIServer();
    }
    
    // KeepAliveCheckInvokerのJavaDoc
    public Object getHostInfo() {
        try{
            return initialCtx == null || initialCtx.getEnvironment() == null ? null : initialCtx.getEnvironment().get(Context.PROVIDER_URL);
        }catch(javax.naming.NamingException e){
            return null;
        }
    }
    
    // CachedJndiFinderServiceMBeanのJavaDoc
    public void setLoggingDeadJNDIServer(boolean isOutput){
        isLoggingDeadJNDIServer = isOutput;
    }
    
    // CachedJndiFinderServiceMBeanのJavaDoc
    public boolean isLoggingDeadJNDIServer(){
        return isLoggingDeadJNDIServer;
    }
    
    // CachedJndiFinderServiceMBeanのJavaDoc
    public void setLoggingRecoverJNDIServer(boolean isOutput){
        isLoggingRecoverJNDIServer = isOutput;
    }
    
    // CachedJndiFinderServiceMBeanのJavaDoc
    public boolean isLoggingRecoverJNDIServer(){
        return isLoggingRecoverJNDIServer;
    }
    
    // CachedJndiFinderServiceMBeanのJavaDoc
    public void setDeadJNDIServerLogMessageId(String id){
        deadJNDIServerLogMessageId = id;
    }
    
    // CachedJndiFinderServiceMBeanのJavaDoc
    public String getDeadJNDIServerLogMessageId(){
        return deadJNDIServerLogMessageId;
    }
    
    // CachedJndiFinderServiceMBeanのJavaDoc
    public void setRecoverJNDIServerLogMessageId(String id){
        recoverJNDIServerLogMessageId = id;
    }
    
    // CachedJndiFinderServiceMBeanのJavaDoc
    public String getRecoverJNDIServerLogMessageId(){
        return recoverJNDIServerLogMessageId;
    }
    
    // CachedJndiFinderServiceMBeanのJavaDoc
    public void clearCache(){
        if(remoteObjCache != null){
            remoteObjCache.clear();
        }
    }
    
    // CachedJndiFinderServiceMBeanのJavaDoc
    public void clearCache(String name){
        if(remoteObjCache != null){
            remoteObjCache.remove(name);
        }
    }
    
    // CachedJndiFinderServiceMBeanのJavaDoc
    public String listContext() throws NamingException{
        if(initialCtx == null){
            return null;
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.println("<pre>");
        listContext(pw, initialCtx, "　");
        pw.println("</pre>");
        return sw.toString();
    }
    
    private void listContext(PrintWriter pw, Context context, String indent) throws NamingException{
        NamingEnumeration list = context.listBindings("");
        while(list.hasMore()){
            Binding item = (Binding)list.next();
            String className = item.getClassName();
            String name = item.getName();
            pw.println(indent + className + "　" + name);
            Object o = item.getObject();
            if(o instanceof javax.naming.Context){
                listContext(pw, (Context)o, indent + "　");
            }
        }
    }
}
