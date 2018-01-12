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
package jp.ossc.nimbus.service.log;

import java.util.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.beans.ServiceNameEditor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.LogConfigurationException;

/**
 * Nimbus用のJakarta Commons LoggingのLogFactory拡張クラス。<p>
 * プロパティファイル"commons-logging.properties"に、
 * <pre>
 * org.apache.commons.logging.LogFactory=jp.ossc.nimbus.service.log.NimbusLogFactory
 * </pre>
 * を指定する事で、{@link LogFactory}の実装クラスとして、このクラスが使用可能になる。また、システムプロパティでも同様の指定が可能である。<br>
 * <p>
 * このログファクトリは、{@link CommonsLogFactory}インタフェースを実装したサービスを使用して{@link Log}インスタンスを生成する。<br>
 * そのため、CommonsLogFactoryインタフェースを実装したサービスを定義して、その定義をロードしておく必要がある。CommonsLogFactoryインタフェースを実装したサービスがロードされていない場合、または、起動されていない場合は、デフォルトの{@link LogFactory}を使用してLogインスタンスを生成する。<br>
 * デフォルトのLogFactoryは、org.apache.commons.logging.impl.LogFactoryImplを使用する。但し、デフォルトのLogFactoryを変更する事が可能で、プロパティファイル"commons-logging.properties"に、
 * <pre>
 * jp.ossc.nimbus.service.log.NimbusLogFactory.DefaultLogFactory=org.apache.commons.logging.impl.Log4jFactory
 * </pre>
 * のように指定する事で、変更できる。また、システムプロパティでも同様の指定が可能である。<br>
 * <p>
 * {@link CommonsLogFactory}インタフェースを実装したサービスとして、{@link DefaultCommonsLogFactoryService}が提供されている。<br>
 * DefaultCommonsLogFactoryServiceは、起動時に{@link LogFactory#getFactory()}を呼び出して、このファクトリのインスタンスを取得し、{@link #setCommonsLogFactory(CommonsLogFactory)}で自分自身をこのファクトリに設定する。そのため、前述した"org.apache.commons.logging.LogFactory"プロパティの設定と、DefaultCommonsLogFactoryServiceのサービス定義のみで、使用可能である。<br>
 * 但し、LogFactory.getFactory()で取得できるLogFactoryインスタンスは、呼び出しスレッドに関連付けられたクラスローダ単位で取得される。そのため、DefaultCommonsLogFactoryServiceのロードを行うスレッドに関連付けられたクラスローダと、{@link LogFactory#getLog(String)}を呼び出すスレッドに関連付けられたクラスローダが異なる場合は、上記の設定のみでは、このファクトリがDefaultCommonsLogFactoryServiceの参照を得る事ができない。<br>
 * <p>
 * {@link LogFactory#getLog(Class)}、{@link LogFactory#getLog(String)}を呼び出すスレッドに関連付けられたクラスローダと、CommonsLogFactoryの実装サービスをロードしたスレッドに関連付けられたクラスローダが異なる場合は、プロパティファイル"commons-logging.properties"に、
 * <pre>
 * jp.ossc.nimbus.service.log.NimbusLogFactory.CommonsLogFactoryName=Nimbus#CommonsLog
 * </pre>
 * のように、CommonsLogFactoryの実装サービスのサービス名を指定する必要がある。<br>
 * 
 * @author M.Takata
 * @see CommonsLogFactory
 */
public class NimbusLogFactory extends LogFactory implements java.io.Serializable{
    
    private static final long serialVersionUID = -3343921992875545571L;
    
    /**
     * {@link CommonsLogFactory}が設定されていない時に使用する{@link LogFactory}の実装クラス名を指定するプロパティ名。<p>
     * プロパティファイル"commons-logging.properties"に、このプロパティを指定する。または、システムプロパティで指定する。<br>
     */
    public static final String DEFAULT_FACTORY_PROPERTY =
        "jp.ossc.nimbus.service.log.NimbusLogFactory.DefaultLogFactory";
    
    /**
     * {@link CommonsLogFactory}のサービス名を指定するプロパティ名。<p>
     * プロパティファイル"commons-logging.properties"に、このプロパティを指定する。または、システムプロパティで指定する。<br>
     * {@link LogFactory#getLog(Class)}、{@link LogFactory#getLog(String)}を呼び出したスレッドのクラスローダと、CommonsLogFactoryの実装サービスをロードしたスレッドのクラスローダが異なる場合は、このプロパティを指定する必要がある。両者のクラスローダが等しい場合は、このプロパティを指定する必要はない。<br>
     */
    public static final String FACTORY_NAME_PROPERTY =
        "jp.ossc.nimbus.service.log.NimbusLogFactory.CommonsLogFactoryName";
    
    /**
     * {@link #DEFAULT_FACTORY_PROPERTY}の指定がない場合に、生成される{@link LogFactory}の実装クラス名。<p>
     */
    public static final String DEFAULT_FACTORY_DEFAULT =
        "org.apache.commons.logging.impl.LogFactoryImpl";
    
    /**
     * {@link Log}を生成する{@link CommonsLogFactory}。<p>
     */
    private CommonsLogFactory logFactory;
    
    /**
     * デフォルトの{@link LogFactory}。<p>
     */
    private LogFactory deafultLogFactory;
    
    /**
     * 属性管理マップ。<p>
     * <table border="1">
     *   <tr bgcolor="#CCCCFF"><th colspan="2">キー</th><th colspan="2">値</th></tr>
     *   <tr bgcolor="#CCCCFF"><th>型</th><th>内容</th><th>型</th><th>内容</th></tr>
     *   <tr><td>Object</td><td>属性名</td><td>Object</td><td>属性値</td></tr>
     * </table>
     */
    private Map attributes = new HashMap();
    
    /**
     * {@link Log}インスタンス管理マップ。<p>
     * <table border="1">
     *   <tr bgcolor="#CCCCFF"><th colspan="2">キー</th><th colspan="2">値</th></tr>
     *   <tr bgcolor="#CCCCFF"><th>型</th><th>内容</th><th>型</th><th>内容</th></tr>
     *   <tr><td>Object</td><td>Logインスタンス識別情報</td><td>Log</td><td>Logインスタンス</td></tr>
     * </table>
     */
    private Map logInstances = new HashMap();
    
    /**
     * {@link CommonsLogFactory}を設定する。<p>
     *
     * @param factory CommonsLogFactoryオブジェクト
     */
    public void setCommonsLogFactory(CommonsLogFactory factory){
        if(logFactory != null && logFactory == factory){
            return;
        }
        logFactory = factory;
        if(factory != null){
            final String[] names = getAttributeNames();
            for(int i = 0; i < names.length; i++){
                final String name = names[i];
                factory.setAttribute(name, getAttribute(name));
            }
        }
        final Iterator logKeys = logInstances.keySet().iterator();
        while(logKeys.hasNext()){
            final Object key = logKeys.next();
            final LogWrapper log = (LogWrapper)logInstances.get(key);
            if(factory != null){
                if(key instanceof Class){
                    log.setRealLog(factory.getInstance((Class)key));
                }else{
                    log.setRealLog(factory.getInstance((String)key));
                }
                log.real();
            }else{
                log.dummy();
            }
        }
    }
    
    /**
     * 指定されたキーに対応するLogインスタンスを取得する。<p>
     * 
     * @param key キー情報
     * @return Logインスタンス
     */
    private Log getInstance(final Object key){
        if(logInstances.containsKey(key)){
            return (Log)logInstances.get(key);
        }
        
        if(deafultLogFactory == null){
            deafultLogFactory = createDefaultLogFactory();
        }
        LogWrapper log = null;
        if(logFactory != null){
            if(key instanceof Class){
                log = new LogWrapper(
                    logFactory.getInstance((Class)key),
                    deafultLogFactory.getInstance((Class)key)
                );
            }else{
                log = new LogWrapper(
                    logFactory.getInstance((String)key),
                    deafultLogFactory.getInstance((String)key)
                );
            }
            logInstances.put(key, log);
            return log;
        }
        
        String factoryName
             = System.getProperty(FACTORY_NAME_PROPERTY);
        if(factoryName == null){
            factoryName = (String)getAttribute(FACTORY_NAME_PROPERTY);
        }
        if(factoryName == null){
            if(key instanceof Class){
                log = new LogWrapper(
                    deafultLogFactory.getInstance((Class)key)
                );
            }else{
                log = new LogWrapper(
                    deafultLogFactory.getInstance((String)key)
                );
            }
            logInstances.put(key, log);
            return log;
        }
        
        final ServiceNameEditor editor = new ServiceNameEditor();
        editor.setAsText(factoryName);
        final ServiceName name = (ServiceName)editor.getValue();
        LogWrapper tmpLog = null;
        if(key instanceof Class){
            tmpLog = new LogWrapper(deafultLogFactory.getInstance((Class)key));
        }else{
            tmpLog = new LogWrapper(deafultLogFactory.getInstance((String)key));
        }
        log = tmpLog;
        logInstances.put(key, log);
        final String managerName = name.getServiceManagerName();
        final String serviceName = name.getServiceName();
        if(!ServiceManagerFactory.isRegisteredManager(managerName)){
            waitRegistrationManager(managerName, serviceName);
            return log;
        }
        final ServiceManager manager
             = ServiceManagerFactory.findManager(managerName);
        if(!manager.isRegisteredService(serviceName)){
            waitRegistrationService(manager, serviceName);
            return log;
        }
        final Service service = manager.getService(serviceName);
        waitStartService(service);
        return log;
    }
    
    /**
     * CommonsLogFactoryサービスが登録されるServiceManagerが、ServiceManagerFactoryに登録されるのを待機して、CommonsLogFactoryサービスを設定する。<p>
     * サービス名targetMngのServiceManagerがServiceManagerFactoryに登録されると、サービス名targetServiceのサービスを取得してみる。取得できない場合は、{@link #waitRegistrationService(ServiceManager, String)}を呼び出して、サービスが登録されるのを待機する。取得できる場合は、{@link #waitStartService(Service)}を呼び出して、サービスが開始されるのを待機する。<br>
     *
     * @param targetMng CommonsLogFactoryサービスが登録されるServiceManagerのサービス名
     * @param targetService CommonsLogFactoryのサービス名
     */
    private void waitRegistrationManager(
        final String targetMng,
        final String targetService
    ){
        ServiceManagerFactory.addRegistrationListener(
            new RegistrationListener(){
                public void registered(RegistrationEvent e){
                    final ServiceManager manager
                         = (ServiceManager)e.getRegistration();
                    if(!manager.getServiceName().equals(targetMng)){
                        return;
                    }
                    ServiceManagerFactory.removeRegistrationListener(this);
                    Service service = null;
                    try{
                        service = manager.getService(targetService);
                    }catch(ServiceNotFoundException ex){
                        waitRegistrationService(
                            manager,
                            targetService
                        );
                    }
                    if(service != null){
                        waitStartService(service);
                    }
                }
                public void unregistered(RegistrationEvent e){
                }
            }
        );
    }
    
    /**
     * CommonsLogFactoryサービスがServiceManagerに登録されるのを待機して、CommonsLogFactoryサービスを設定する。<p>
     * サービス名targetServiceのCommonsLogFactoryがServiceManagerに登録されると、{@link #waitStartService(Service)}を呼び出して、サービスが開始されるのを待機する。<br>
     *
     * @param targetMng CommonsLogFactoryサービスが登録されるServiceManager
     * @param targetService CommonsLogFactoryのサービス名
     * @param log LogWrapperオブジェクト
     */
    private void waitRegistrationService(
        final ServiceManager targetMng,
        final String targetService
    ){
        targetMng.addRegistrationListener(
            new RegistrationListener(){
                public void registered(RegistrationEvent e){
                    final Service service
                         = (Service)e.getRegistration();
                    if(!service.getServiceName().equals(targetService)){
                        return;
                    }
                    targetMng.removeRegistrationListener(this);
                    waitStartService(service);
                }
                public void unregistered(RegistrationEvent e){
                }
            }
        );
    }
    
    /**
     * CommonsLogFactoryサービスが開始されるのを待機して、CommonsLogFactoryサービスを設定する。<p>
     * サービスserviceが開始されると、{@link #setCommonsLogFactory(CommonsLogFactory)}を呼び出して、CommonsLogFactoryの参照を設定する。<br>
     * また、サービスserviceが停止されると、{@link #setCommonsLogFactory(CommonsLogFactory)}を呼び出して、CommonsLogFactoryの参照を破棄する。<br>
     *
     * @param targetService CommonsLogFactoryサービス
     */
    private void waitStartService(final Service service){
        Service targetService = null;
        if(!(service instanceof ServiceStateBroadcaster)){
            final String managerName = service.getServiceManagerName();
            final ServiceManager mng = ServiceManagerFactory.findManager(
                managerName
            );
            targetService = mng;
        }else{
            targetService = service;
        }
        final ServiceStateBroadcaster broad = ServiceManagerFactory
            .getServiceStateBroadcaster(
                targetService.getServiceManagerName(),
                targetService.getServiceName()
            );
        if(broad != null){
            broad.addServiceStateListener(
                new ServiceStateListener(){
                    public void stateChanged(ServiceStateChangeEvent e)
                     throws Exception{
                        CommonsLogFactory factory = null;
                        switch(service.getState()){
                        case Service.STARTED:
                            try{
                                factory = (CommonsLogFactory)
                                    ServiceManagerFactory.getServiceObject(
                                        service.getServiceManagerName(),
                                        service.getServiceName()
                                );
                            }catch(ServiceNotFoundException ex){
                                factory = null;
                            }
                            break;
                        case Service.STOPPED:
                            factory = null;
                            break;
                        case Service.DESTROYED:
                            broad.removeServiceStateListener(this);
                            factory = null;
                            break;
                        default:
                        }
                        setCommonsLogFactory(factory);
                    }
                    public boolean isEnabledState(int state){
                        return state == Service.STARTED
                             || state == Service.STOPPED
                             || state == Service.DESTROYED;
                    }
                }
            );
        }
        if(service.getState() == Service.STARTED){
            CommonsLogFactory factory = null;
            try{
                factory = (CommonsLogFactory)ServiceManagerFactory.getServiceObject(
                    service.getServiceManagerName(),
                    service.getServiceName()
                );
            }catch(ServiceNotFoundException ex){
                // 起こり得ない
            }
            setCommonsLogFactory(factory);
        }
    }
    
    /**
     * デフォルトのLogFactoryを生成する。<p>
     *
     * @return デフォルトのLogFactory
     */
    private LogFactory createDefaultLogFactory()
     throws LogConfigurationException {
        LogFactory factory = null;
        try{
            final String factoryClass
                 = System.getProperty(DEFAULT_FACTORY_PROPERTY);
            if(factoryClass != null){
                final ClassLoader classLoader
                     = Thread.currentThread().getContextClassLoader();
                factory = newFactory(factoryClass, classLoader);
            }
        }catch(SecurityException e){
            factory = null;
        }
        
        if(factory == null){
            final String factoryClass = (String)getAttribute(DEFAULT_FACTORY_PROPERTY);
            if(factoryClass != null){
                final ClassLoader classLoader
                     = Thread.currentThread().getContextClassLoader();
                factory = newFactory(factoryClass, classLoader);
            }
        }
        
        if(factory == null){
            final ClassLoader classLoader
                 = Thread.currentThread().getContextClassLoader();
            factory = newFactory(DEFAULT_FACTORY_DEFAULT, classLoader);
        }
        
        if(factory != null){
            final String[] names = getAttributeNames();
            for(int i = 0; i < names.length; i++){
                final String name = names[i];
                factory.setAttribute(name, getAttribute(name));
            }
        }
        
        return factory;
    }
    
    /**
     * 引数で指定したクラスオブジェクトに関連付いた{@link Log}インスタンスを取得する。<p>
     *
     * @param clazz 取得するLogインスタンスを識別するキーとなるクラスオブジェクト
     * @return 引数で指定したクラスオブジェクトに関連付いた{@link Log}インスタンス
     * @exception LogConfigurationException Logインスタンスの作成に失敗した場合
     */
    public Log getInstance(Class clazz) throws LogConfigurationException{
        return getInstance((Object)clazz);
    }
    
    /**
     * 引数で指定した名前に関連付いた{@link Log}インスタンスを取得する。<p>
     *
     * @param name 取得するLogインスタンスを識別する名前
     * @return 引数で指定した名前に関連付いた{@link Log}インスタンス
     * @exception LogConfigurationException Logインスタンスの作成に失敗した場合
     */
    public Log getInstance(String name) throws LogConfigurationException{
        return getInstance((Object)name);
    }
    
    /**
     * 作成した{@link Log}インスタンスを開放する。<p>
     */
    public void release(){
        logInstances.clear();
        if(logFactory != null){
            logFactory.release();
        }
    }
    
    /**
     * 属性値を取得する。<p>
     * "commons-logging.properties"で設定したプロパティが属性として格納される。<p>
     *
     * @param name 属性名
     * @return 属性値
     * @see #getAttributeNames()
     * @see #removeAttribute(String)
     * @see #setAttribute(String, Object)
     */
    public Object getAttribute(String name){
        if(logFactory != null){
            return logFactory.getAttribute(name);
        }
        return attributes.get(name);
    }
    
    /**
     * 属性名の配列を取得する。<p>
     * "commons-logging.properties"で設定したプロパティが属性として格納される。<p>
     *
     * @return 属性名の配列
     * @see #getAttribute(String)
     * @see #removeAttribute(String)
     * @see #setAttribute(String, Object)
     */
    public String[] getAttributeNames(){
        if(logFactory != null){
            return logFactory.getAttributeNames();
        }
        return (String[])attributes.keySet()
            .toArray(new String[attributes.size()]);
    }
    
    /**
     * 属性を削除する。<p>
     * "commons-logging.properties"で設定したプロパティが属性として格納される。<p>
     *
     * @param name 属性名
     * @see #getAttribute(String)
     * @see #getAttributeNames()
     * @see #setAttribute(String, Object)
     */
    public void removeAttribute(String name){
        attributes.remove(name);
        if(logFactory != null){
            logFactory.removeAttribute(name);
        }
    }
    
    /**
     * 属性を設定する。<p>
     * "commons-logging.properties"で設定したプロパティが属性として格納される。<p>
     *
     * @param name 属性名
     * @param value 属性値
     * @see #getAttribute(String)
     * @see #getAttributeNames()
     * @see #removeAttribute(String)
     */
    public void setAttribute(String name, Object value){
        attributes.put(name, value);
        if(logFactory != null){
            logFactory.setAttribute(name, value);
        }
    }
    
    private class LogWrapper implements org.apache.commons.logging.Log{
        
        private Log logger;
        private Log dummyLogger;
        private Log currentLogger;
        
        public LogWrapper(Log dummyLogger){
            this(null, dummyLogger);
        }
        
        public LogWrapper(Log logger, Log dummyLogger){
            this.logger = logger;
            this.dummyLogger = dummyLogger;
            if(logger != null){
                currentLogger = logger;
            }else{
                currentLogger = dummyLogger;
            }
        }
        
        public void setRealLog(Log logger){
            LogWrapper.this.logger = logger;
        }
        
        public void real(){
            currentLogger = logger;
        }
        
        public void dummy(){
            currentLogger = dummyLogger;
        }
        
        public void trace(Object message){
            currentLogger.trace(message);
        }
        
        public void trace(Object message, Throwable t){
            currentLogger.trace(message, t);
        }
        
        public void debug(Object message){
            currentLogger.debug(message);
        }
        
        public void debug(Object message, Throwable t){
            currentLogger.debug(message, t);
        }
        
        public void info(Object message){
            currentLogger.info(message);
        }
        
        public void info(Object message, Throwable t){
            currentLogger.info(message, t);
        }
        
        public void warn(Object message){
            currentLogger.warn(message);
        }
        
        public void warn(Object message, Throwable t){
            currentLogger.warn(message, t);
        }
        
        public void error(Object message){
            currentLogger.error(message);
        }
        
        public void error(Object message, Throwable t){
            currentLogger.error(message, t);
        }
        
        public void fatal(Object message){
            currentLogger.fatal(message);
        }
        
        public void fatal(Object message, Throwable t) {
            currentLogger.fatal(message, t);
        }
        
        public boolean isTraceEnabled() {
            return currentLogger.isTraceEnabled();
        }
        
        public boolean isDebugEnabled() {
            return currentLogger.isDebugEnabled();
        }
        
        public boolean isInfoEnabled() {
            return currentLogger.isInfoEnabled();
        }
        
        public boolean isWarnEnabled() {
            return currentLogger.isWarnEnabled();
        }
        
        public boolean isErrorEnabled() {
            return currentLogger.isErrorEnabled();
        }
        
        public boolean isFatalEnabled() {
            return currentLogger.isFatalEnabled();
        }
    }
}