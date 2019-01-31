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
package jp.ossc.nimbus.service.jmx;

import java.io.IOException;
import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.management.ListenerNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.Notification;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.QueryExp;
import javax.management.remote.JMXConnectionNotification;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.commons.jexl.Expression;
import org.apache.commons.jexl.ExpressionFactory;
import org.apache.commons.jexl.JexlContext;
import org.apache.commons.jexl.JexlHelper;

import jp.ossc.nimbus.beans.PropertyAccess;
import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.daemon.Daemon;
import jp.ossc.nimbus.daemon.DaemonControl;
import jp.ossc.nimbus.daemon.DaemonRunnable;
import jp.ossc.nimbus.service.jndi.JndiFinder;
import jp.ossc.nimbus.service.log.Logger;
import jp.ossc.nimbus.service.writer.Category;
import jp.ossc.nimbus.service.writer.MessageWriteException;
import jp.ossc.nimbus.util.converter.Converter;

/**
 * MBeanを監視するサービス。<p>
 *
 * @author M.Takata
 */
public class MBeanWatcherService extends ServiceBase implements DaemonRunnable, MBeanWatcherServiceMBean{

    private static final long serialVersionUID = -1421073056315791503L;

    private String description;
    private ServiceName jndiFinderServiceName;
    private JndiFinder jndiFinder;
    private String rmiAdaptorName = DEFAULT_JMX_RMI_ADAPTOR_NAME;
    private String serviceURL;
    private Map jmxConnectorEnvironment;
    private ServiceName mBeanServerConnectionFactoryServiceName;
    private MBeanServerConnectionFactory mBeanServerConnectionFactory;

    private long interval;
    private ServiceName categoryServiceName;
    private List targetList;
    private boolean isConnectOnStart;
    private String getValueErrorMessageId = MSG_ID_GET_VALUE_ERROR;
    private String connectErrorMessageId = MSG_ID_CONNECT_ERROR;
    private String writeErrorMessageId = MSG_ID_WRITE_ERROR;
    private boolean isMBeanSet;
    private ObjectName objectName;
    private QueryExp queryExp;
    private boolean isResetOnStart;

    private JMXConnector connector;
    private Category category;
    private Daemon watcher;
    private Map contextMap;
    private boolean isConnectError;
    private PropertyAccess propertyAccess;
    
    // MBeanWatcherServiceMBeanのJavaDoc
    public String getDescription(){
        return description;
    }
    
    // MBeanWatcherServiceMBeanのJavaDoc
    public void setDescription(String desc){
        description = desc;
    }
    
    // MBeanWatcherServiceMBeanのJavaDoc
    public void setMBeanServerConnectionFactoryServiceName(ServiceName name){
        mBeanServerConnectionFactoryServiceName = name;
    }
    // MBeanWatcherServiceMBeanのJavaDoc
    public ServiceName getMBeanServerConnectionFactoryServiceName(){
        return mBeanServerConnectionFactoryServiceName;
    }

    // MBeanWatcherServiceMBeanのJavaDoc
    public void setJndiFinderServiceName(ServiceName name){
        jndiFinderServiceName = name;
    }
    // MBeanWatcherServiceMBeanのJavaDoc
    public ServiceName getJndiFinderServiceName(){
        return jndiFinderServiceName;
    }

    // MBeanWatcherServiceMBeanのJavaDoc
    public void setRMIAdaptorName(String name){
        rmiAdaptorName = name;
    }
    // MBeanWatcherServiceMBeanのJavaDoc
    public String getRMIAdaptorName(){
        return rmiAdaptorName;
    }

    // MBeanWatcherServiceMBeanのJavaDoc
    public void setServiceURL(String url){
        serviceURL = url;
    }
    // MBeanWatcherServiceMBeanのJavaDoc
    public String getServiceURL(){
        return serviceURL;
    }

    // MBeanWatcherServiceMBeanのJavaDoc
    public void setJMXConnectorEnvironment(Map env){
        jmxConnectorEnvironment = env;
    }
    // MBeanWatcherServiceMBeanのJavaDoc
    public Map getJMXConnectorEnvironment(){
        return jmxConnectorEnvironment;
    }

    // MBeanWatcherServiceMBeanのJavaDoc
    public void setInterval(long interval){
        this.interval = interval;
    }
    // MBeanWatcherServiceMBeanのJavaDoc
    public long getInterval(){
        return interval;
    }

    // MBeanWatcherServiceMBeanのJavaDoc
    public void setCategoryServiceName(ServiceName name){
        categoryServiceName = name;
    }
    // MBeanWatcherServiceMBeanのJavaDoc
    public ServiceName getCategoryServiceName(){
        return categoryServiceName;
    }

    // MBeanWatcherServiceMBeanのJavaDoc
    public void setConnectOnStart(boolean isConnect){
        isConnectOnStart = isConnect;
    }
    // MBeanWatcherServiceMBeanのJavaDoc
    public boolean isConnectOnStart(){
        return isConnectOnStart;
    }

    // MBeanWatcherServiceMBeanのJavaDoc
    public void setGetValueErrorMessageId(String id){
        getValueErrorMessageId = id;
    }
    // MBeanWatcherServiceMBeanのJavaDoc
    public String getGetValueErrorMessageId(){
        return getValueErrorMessageId;
    }

    // MBeanWatcherServiceMBeanのJavaDoc
    public void setConnectErrorMessageId(String id){
        connectErrorMessageId = id;
    }
    // MBeanWatcherServiceMBeanのJavaDoc
    public String getConnectErrorMessageId(){
        return connectErrorMessageId;
    }

    // MBeanWatcherServiceMBeanのJavaDoc
    public void setWriteErrorMessageId(String id){
        writeErrorMessageId = id;
    }
    // MBeanWatcherServiceMBeanのJavaDoc
    public String getWriteErrorMessageId(){
        return writeErrorMessageId;
    }

    // MBeanWatcherServiceMBeanのJavaDoc
    public void setMBeanSet(boolean isSet){
        isMBeanSet = isSet;
    }

    // MBeanWatcherServiceMBeanのJavaDoc
    public boolean isMBeanSet(){
        return isMBeanSet;
    }

    // MBeanWatcherServiceMBeanのJavaDoc
    public void setObjectName(String name) throws MalformedObjectNameException{
        objectName = new ObjectName(name);
    }

    // MBeanWatcherServiceMBeanのJavaDoc
    public String getObjectName(){
        return objectName == null ? null : objectName.toString();
    }

    // MBeanWatcherServiceMBeanのJavaDoc
    public void setQueryExp(QueryExp exp){
        queryExp = exp;
    }

    // MBeanWatcherServiceMBeanのJavaDoc
    public QueryExp getQueryExp(){
        return queryExp;
    }

    // MBeanWatcherServiceMBeanのJavaDoc
    public boolean isResetOnStart(){
        return isResetOnStart;
    }

    // MBeanWatcherServiceMBeanのJavaDoc
    public void setResetOnStart(boolean isResetOnStart){
        this.isResetOnStart = isResetOnStart;
    }

    public void addTarget(Target target){
        if(targetList == null){
            targetList = new ArrayList();
        }
        targetList.add(target);
    }

    // MBeanWatcherServiceMBeanのJavaDoc
    public List getTargetList(){
        return targetList;
    }

    // MBeanWatcherServiceMBeanのJavaDoc
    public List getCheckTargetList(){
        if(targetList == null){
            return null;
        }
        List result = new ArrayList();
        for(int i = 0; i < targetList.size(); i++){
            Target target = (Target)targetList.get(i);
            if(target instanceof Check){
                result.add(target);
            }else if(target instanceof WrapTarget){
                do{
                    target = ((WrapTarget)target).getTarget();
                    if(target != null && (target instanceof Check)){
                        result.add(target);
                    }
                }while(target != null && (target instanceof WrapTarget));
            }
        }
        return result;
    }

    // MBeanWatcherServiceMBeanのJavaDoc
    public void reset(){
        for(int i = 0, imax = targetList.size(); i < imax; i++){
            Target target = (Target)targetList.get(i);
            target.reset();
        }
    }

    public void createService() throws Exception{
        contextMap = Collections.synchronizedMap(new HashMap());
        propertyAccess = new PropertyAccess();
        propertyAccess.setIgnoreNullProperty(true);
    }

    public void startService() throws Exception{
        if(mBeanServerConnectionFactoryServiceName != null){
            mBeanServerConnectionFactory = (MBeanServerConnectionFactory)ServiceManagerFactory.getServiceObject(mBeanServerConnectionFactoryServiceName);
            if(isConnectOnStart){
                connector = mBeanServerConnectionFactory.getJMXConnector();
                connector.connect();
            }
        }else if(jndiFinderServiceName != null){
            jndiFinder = (JndiFinder)ServiceManagerFactory.getServiceObject(jndiFinderServiceName);
        }else if(serviceURL != null){
            if(isConnectOnStart){
                connector = JMXConnectorFactory.newJMXConnector(
                    new JMXServiceURL(serviceURL),
                    jmxConnectorEnvironment
                );
                connector.connect();
            }
        }

        if(categoryServiceName != null){
            category = (Category)ServiceManagerFactory.getServiceObject(categoryServiceName);
        }

        for(int i = 0, imax = targetList.size(); i < imax; i++){
            Target target = (Target)targetList.get(i);
            target.setWatcherServiceName(getServiceNameObject());
            target.setWatcherService(this);
            target.setLogger(getLogger());
            if(isResetOnStart){
                target.reset();
            }
            target.start();
        }
        if(interval > 0){
            watcher = new Daemon(this);
            watcher.setName(
                "Nimbus MBeanWatcher " + getServiceNameObject()
            );
            watcher.setDaemon(true);
            if(connector != null){
                JMXConnectorNotificationListener listener = new JMXConnectorNotificationListener();
                connector.addConnectionNotificationListener(listener, listener, watcher);
            }
            watcher.start();
        }
    }

    public void stopService() throws Exception{
        if(watcher != null){
            watcher.stop();
            watcher = null;
        }
        if(connector != null){
            try{
                connector.close();
            }catch(IOException e){}
            connector = null;
        }
        for(int i = 0, imax = targetList.size(); i < imax; i++){
            Target target = (Target)targetList.get(i);
            target.stop();
        }
    }

    public void destroyService() throws Exception{
        contextMap = null;
        propertyAccess = null;
    }

    public Map watch() throws Exception{
        return watch(true);
    }

    protected Map watch(boolean throwConnectError) throws Exception{
        JMXConnector tmpConnector = null;
        try{
            MBeanServerConnection connection = null;
            try{
                if(jndiFinder != null){
                    connection = (MBeanServerConnection)jndiFinder.lookup(rmiAdaptorName);
                }else if(mBeanServerConnectionFactory != null || serviceURL != null){
                    if(connector == null){
                        if(mBeanServerConnectionFactory != null){
                            tmpConnector = mBeanServerConnectionFactory.getJMXConnector();
                        }else{
                            tmpConnector = JMXConnectorFactory.newJMXConnector(
                                new JMXServiceURL(serviceURL),
                                jmxConnectorEnvironment
                            );
                        }
                        tmpConnector.connect();
                        connection = tmpConnector.getMBeanServerConnection();
                    }else{
                        connection = connector.getMBeanServerConnection();
                    }

                }else{
                    connection = ManagementFactory.getPlatformMBeanServer();

                }
                isConnectError = false;
            }catch(MBeanServerConnectionFactoryException e){
                if(!isConnectError && connectErrorMessageId != null){
                    getLogger().write(
                        connectErrorMessageId,
                        new Object[]{getServiceNameObject(), mBeanServerConnectionFactoryServiceName},
                        e
                    );
                }
                isConnectError = true;
                if(throwConnectError){
                    throw e;
                }else{
                    return null;
                }
            }catch(Exception e){
                if(!isConnectError && connectErrorMessageId != null){
                    getLogger().write(
                        connectErrorMessageId,
                        new Object[]{getServiceNameObject(), rmiAdaptorName != null ? rmiAdaptorName : (serviceURL != null ? serviceURL : "PlatformMBeanServer")},
                        e
                    );
                }
                isConnectError = true;
                if(throwConnectError){
                    throw e;
                }else{
                    return null;
                }
            }
            return watch(connection);
        }finally{
            if(tmpConnector != null){
                try{
                    tmpConnector.close();
                }catch(IOException e){}
            }
        }
    }

    protected Map watch(MBeanServerConnection connection) throws Exception{
        Map out = new LinkedHashMap();
        for(int i = 0, imax = targetList.size(); i < imax; i++){
            Target target = (Target)targetList.get(i);
            try{
                Object value = target.getValue(connection);
                if(target.getKey() != null){
                    out.put(target.getKey(), value);
                }
            }catch(Exception e){
                if(getValueErrorMessageId != null){
                    getLogger().write(
                        getValueErrorMessageId,
                        new Object[]{getServiceNameObject(), target},
                        e
                    );
                }
            }
        }
        return out;
    }

    public void write() throws Exception{
        Map out = watch();
        if(out != null){
            write(out);
        }
    }

    protected void write(Object out) throws MessageWriteException{
        if(category != null){
            category.write(out);
        }
    }

    public boolean onStart(){return true;}
    public boolean onStop(){return true;}
    public boolean onSuspend(){return true;}
    public boolean onResume(){return true;}
    public Object provide(DaemonControl ctrl) throws Throwable{
        ctrl.sleep(interval, false);
        if(getState() == STARTED){
            try{
                return watch(false);
            }catch(Throwable th){
                if(getState() == STARTED){
                    throw th;
                } else {
                    return null;
                }
            }
        } else {
            return null;
        }
    }
    public void consume(Object paramObj, DaemonControl ctrl) throws Throwable{
        if(paramObj != null){
            try{
                write(paramObj);
            }catch(Throwable e){
                if(getState() == STARTED && writeErrorMessageId != null && ctrl.isRunning()){
                    getLogger().write(
                        writeErrorMessageId,
                        new Object[]{getServiceNameObject(), paramObj},
                        e
                    );
                }
            }
        }
    }
    public void garbage(){}

    protected void setContextValue(String name, Object value){
        contextMap.put(name, value);
    }

    protected Object getContextValue(String name) throws Exception{
        if(contextMap.containsKey(name)){
            return contextMap.get(name);
        }else{
            return propertyAccess.get(contextMap, name);
        }
    }

    protected Map getContextMap(){
        return contextMap;
    }

    /**
     * 監視対象。<p>
     *
     * @author M.Takata
     */
    public static abstract class Target implements Serializable{

        private static final long serialVersionUID = 3184726605262675808L;

        protected String key;
        protected transient Logger logger;
        protected transient MBeanWatcherService watcher;
        protected ServiceName watcherServiceName;
        protected String contextKey;
        protected String description;

        /**
         * {@link MBeanWatcherService}のサービス名を設定する。<p>
         *
         * @param name MBeanWatcherServiceのサービス名
         */
        protected void setWatcherServiceName(ServiceName name){
            this.watcherServiceName = name;
        }

        /**
         * {@link MBeanWatcherService}のサービス名を取得する。<p>
         *
         * @return MBeanWatcherServiceのサービス名
         */
        protected ServiceName getWatcherServiceName(){
            return watcherServiceName;
        }

        /**
         * {@link MBeanWatcherService}を設定する。<p>
         *
         * @param watcher MBeanWatcherService
         */
        protected void setWatcherService(MBeanWatcherService watcher){
            this.watcher = watcher;
        }

        /**
         * {@link MBeanWatcherService}を取得する。<p>
         *
         * @return MBeanWatcherService
         */
        protected MBeanWatcherService getWatcherService(){
            return watcher;
        }

        /**
         * {@link Logger}を設定する。<p>
         *
         * @param logger Logger
         */
        protected void setLogger(Logger logger){
            this.logger = logger;
        }

        /**
         * {@link Logger}を取得する。<p>
         *
         * @return Logger
         */
        protected Logger getLogger(){
            return logger == null ? ServiceManagerFactory.getLogger() : logger;
        }

        /**
         * 出力する際のキーを取得する。<p>
         *
         * @return キー
         */
        public String getKey(){
            return key;
        }

        /**
         * 出力する際のキーを設定する。<p>
         *
         * @param key キー
         */
        public void setKey(String key){
            this.key = key;
        }

        /**
         * コンテキストに出力する際のキーを取得する。<p>
         *
         * @return キー
         */
        public String getContextKey(){
            return contextKey;
        }

        /**
         * コンテキストに出力する際のキーを設定する。<p>
         *
         * @param key キー
         */
        public void setContextKey(String key){
            contextKey = key;
        }

        /**
         * 説明を取得する。<p>
         *
         * @return 説明
         */
        public String getDescription(){
            return description;
        }

        /**
         * 説明を設定する。<p>
         *
         * @param desc 説明
         */
        public void setDescription(String desc){
            description = desc;
        }

        /**
         * 監視対象の値を取得する。<p>
         *
         * @param connection JMX接続
         * @return 監視対象の値
         * @exception Exception 監視対象の値の取得に失敗した場合
         */
        public abstract Object getValue(MBeanServerConnection connection) throws Exception;

        /**
         * 値をBigDecimalに変換する。<p>
         *
         * @param value 値
         * @param isNullToZero 値がnullだった場合にゼロとみなす場合は、true。falseの場合は、値がnullの場合、nullを返す。
         * @return 変換されたBigDecimal値
         * @exception NumberFormatException 変換に失敗した場合
         */
        protected static BigDecimal toBigDecimal(Object value, boolean isNullToZero) throws NumberFormatException{
            BigDecimal result = null;
            if(value == null){
                result = isNullToZero ? new BigDecimal(0.0d) : null;
            }else if(value instanceof BigDecimal){
                result = (BigDecimal)value;
            }else if(value instanceof BigInteger){
                result = new BigDecimal((BigInteger)value);
            }else if(value instanceof Double || value instanceof Float){
                result = new BigDecimal(((Number)value).doubleValue());
            }else if(value instanceof Number){
                result = BigDecimal.valueOf(((Number)value).longValue());
            }else{
                result = new BigDecimal(value.toString());
            }
            return result;
        }

        /**
         * リセットする。<p>
         */
        public void reset(){
        }

        /**
         * 開始する。<p>
         */
        public void start(){
        }

        /**
         * 終了する。<p>
         */
        public void stop(){
        }

        /**
         * この監視対象の文字列表現を取得する。<p>
         *
         * @return 文字列表現
         */
        public String toString(){
            StringBuilder buf = new StringBuilder(super.toString());
            buf.append('{');
            buf.append("key=").append(getKey());
            buf.append(",description=").append(getDescription());
            buf.append('}');
            return buf.toString();
        }
    }

    /**
     * タイムスタンプ。<p>
     * 監視時のタイムスタンプを取得する。<br>
     *
     * @author M.Takata
     */
    public static class Timestamp extends Target{

        private static final long serialVersionUID = 3632167440398869434L;

        /**
         * キーのデフォルト値。<p>
         */
        public static final String DEFAULT_KEY = "Timestamp";

        private String format;

        /**
         * タイムスタンプを文字列に編集する場合のフォーマットを取得する。<p>
         *
         * @return フォーマット
         */
        public String getFormat(){
            return format;
        }

        /**
         * タイムスタンプを文字列に編集する場合のフォーマットを設定する。<p>
         * デフォルトはなしで、タイムスタンプとしてjava.util.Dateオブジェクトを返す。<br>
         *
         * @param format フォーマット
         */
        public void setFormat(String format){
            this.format = format;
        }

        /**
         * 出力する際のキーを取得する。<p>
         * {@link #setKey(String)}で設定していない場合は、{@link #DEFAULT_KEY}を返す。<br>
         *
         * @return キー
         */
        public String getKey(){
            return super.getKey() != null ? super.getKey() : DEFAULT_KEY;
        }

        /**
         * タイムスタンプを取得する。<p>
         * {@link #setFormat(String)}でフォーマットを指定していない場合は、呼び出し時点でのjava.util.Dateオブジェクトを返す。<br>
         * {@link #setFormat(String)}でフォーマットを指定している場合は、呼び出し時点でのjava.util.Dateオブジェクトをフォーマットして文字列を返す。<br>
         *
         * @param connection JMX接続
         * @return タイムスタンプ
         * @exception Exception フォーマットに失敗した場合
         */
        public Object getValue(MBeanServerConnection connection) throws Exception{
            Object value = format == null ? (Object)new Date() : (Object)new SimpleDateFormat(format).format(new Date());
            if(value != null && contextKey != null){
                watcher.setContextValue(contextKey, value);
            }
            return value;
        }

        /**
         * この監視対象の文字列表現を取得する。<p>
         *
         * @return 文字列表現
         */
        public String toString(){
            StringBuilder buf = new StringBuilder(super.toString());
            buf.deleteCharAt(buf.length() - 1);
            buf.append(",format=").append(format);
            buf.append('}');
            return buf.toString();
        }
    }

    /**
     * コンテキスト対象。<p>
     * コンテキストから値を取得する。<br>
     *
     * @author M.Takata
     */
    public static class Context extends Target{

        private static final long serialVersionUID = 566760345569101974L;

        /**
         * コンテキストから値を取得する。<p>
         *
         * @param connection JMX接続
         * @return コンテキストから取得した値
         * @exception Exception フォーマットに失敗した場合
         */
        public Object getValue(MBeanServerConnection connection) throws Exception{
            return watcher.getContextValue(contextKey);
        }

        /**
         * この監視対象の文字列表現を取得する。<p>
         *
         * @return 文字列表現
         */
        public String toString(){
            StringBuilder buf = new StringBuilder(super.toString());
            buf.deleteCharAt(buf.length() - 1);
            buf.append(",contextKey=").append(contextKey);
            buf.append('}');
            return buf.toString();
        }
    }

    /**
     * MBeanWatcher対象。<p>
     * MBeanWatcherから値を取得する。<br>
     *
     * @author M.Takata
     */
    public static class MBeanWatcher extends Target{

        private static final long serialVersionUID = 7924202215978774672L;
        private ServiceName mBeanWatcherServiceName;
        private MBeanWatcherService mBeanWatcherService;
        private boolean isSharedConnection;
        private boolean isThrowConnectError;

        public void setMBeanWatcherServiceName(ServiceName name){
            mBeanWatcherServiceName = name;
        }

        public void setMBeanWatcherService(MBeanWatcherService service){
            mBeanWatcherService = service;
        }

        public void setSharedConnection(boolean isShared){
            isSharedConnection = isShared;
        }

        public void setThrowConnectError(boolean isThrow){
            isThrowConnectError = isThrow;
        }

        /**
         * MBeanWatcherから値を取得する。<p>
         *
         * @param connection JMX接続
         * @return MBeanWatcherから取得した値
         * @exception Exception フォーマットに失敗した場合
         */
        public Object getValue(MBeanServerConnection connection) throws Exception{
            MBeanWatcherService watcherService = mBeanWatcherService;
            if(watcherService == null && mBeanWatcherServiceName != null){
                watcherService = (MBeanWatcherService)ServiceManagerFactory.getServiceObject(mBeanWatcherServiceName);
            }
            Map value = isSharedConnection ? watcherService.watch(connection) : watcherService.watch(isThrowConnectError);
            if(value != null){
                watcherService.write(value);
            }
            return value;
        }

        /**
         * この監視対象の文字列表現を取得する。<p>
         *
         * @return 文字列表現
         */
        public String toString(){
            StringBuilder buf = new StringBuilder(super.toString());
            buf.deleteCharAt(buf.length() - 1);
            buf.append(",mBeanWatcherService=").append(mBeanWatcherService != null ? mBeanWatcherService : mBeanWatcherServiceName);
            buf.append('}');
            return buf.toString();
        }
    }

    /**
     * Managed Bean監視対象。<p>
     * Managed Beanを監視対象とする監視対象基底クラス。<br>
     *
     * @author M.Takata
     */
    public static abstract class MBeanTarget extends Target{
        private static final long serialVersionUID = -5180685937237509600L;
        protected boolean isMBeanSet;
        protected ObjectName objectName;
        protected QueryExp queryExp;

        /**
         * 監視対象のManaged Beanを集合として扱うかどうかを設定する。<p>
         * デフォルトは、falseで、監視対象は、一意なManaged Bean。<br>
         *
         * @param isSet 監視対象のManaged Beanを集合として扱う場合true
         */
        public void setMBeanSet(boolean isSet){
            isMBeanSet = isSet;
        }

        /**
         * 監視対象のManaged Beanを集合として扱うかどうかを判定する。<p>
         *
         * @return trueの場合、監視対象のManaged Beanを集合として扱う
         */
        public boolean isMBeanSet(){
            return isMBeanSet;
        }

        /**
         * 監視対象のManaged Beanの名前を設定する。<p>
         * {@link #setMBeanSet(boolean) setMBeanSet(false)}と設定している場合は、Managed Beanを一意に特定する完全名を指定する。<br>
         * {@link #setMBeanSet(boolean) setMBeanSet(true)}と設定している場合は、Managed Beanの集合を特定するオブジェクト名を指定する。<br>
         *
         * @param name Managed Beanの名前をJMXのオブジェクト名形式で指定する
         * @exception MalformedObjectNameException オブジェクト名が不正な場合
         */
        public void setObjectName(String name) throws MalformedObjectNameException{
            this.objectName = new ObjectName(name);
        }

        /**
         * 監視対象のManaged Beanの名前を取得する。<p>
         *
         * @return Managed Beanの名前をJMXのオブジェクト名形式で指定する
         */
        public String getObjectName(){
            return objectName == null ? null : objectName.toString();
        }

        /**
         * 監視対象のManaged Beanを絞り込む条件式を設定する。<p>
         * {@link #setMBeanSet(boolean) setMBeanSet(true)}の場合のみ有効。<br>
         *
         * @param exp 条件式
         */
        public void setQueryExp(QueryExp exp){
            queryExp = exp;
        }

        /**
         * 監視対象のManaged Beanを絞り込む条件式を取得する。<p>
         *
         * @return 条件式
         */
        public QueryExp getQueryExp(){
            return queryExp;
        }

        /**
         * 監視対象のManaged Beanが持つ監視対象の値を取得する。<p>
         * 監視対象のManaged Beanが集合の場合、キーがオブジェクト名、値が監視対象の値となるマップを返す。<br>
         *
         * @param connection JMX接続
         * @return 監視対象の値
         * @exception Exception 監視対象の値の取得に失敗した場合
         */
        public Object getValue(MBeanServerConnection connection) throws Exception{
            boolean tmpIsMBeanSet = isMBeanSet;
            ObjectName tmpObjectName = objectName;
            QueryExp tmpQueryExp = queryExp;
            if(tmpObjectName == null){
                tmpIsMBeanSet = watcher.isMBeanSet;
                tmpObjectName = watcher.objectName;
                tmpQueryExp = watcher.queryExp;
            }
            Object value = null;
            if(!tmpIsMBeanSet){
                value = getValue(connection, tmpObjectName);
            }else{
                Set objectNameSet = connection.queryNames(tmpObjectName, tmpQueryExp);
                Map map = new LinkedHashMap();
                if(objectNameSet != null && objectNameSet.size() != 0){
                    Iterator itr = objectNameSet.iterator();
                    while(itr.hasNext()){
                        ObjectName name = (ObjectName)itr.next();
                        map.put(name.toString(), getValue(connection, name));
                    }
                }
                value = map;
            }
            if(value != null && contextKey != null){
                watcher.setContextValue(contextKey, value);
            }
            return value;
        }

        /**
         * 指定されたオブジェクト名のManaged Beanが持つ監視対象の値を取得する。<p>
         *
         * @param connection JMX接続
         * @param objectName オブジェクト名
         * @return 監視対象の値
         * @exception Exception 監視対象の値の取得に失敗した場合
         */
        protected abstract Object getValue(MBeanServerConnection connection, ObjectName objectName) throws Exception;

        /**
         * この監視対象の文字列表現を取得する。<p>
         *
         * @return 文字列表現
         */
        public String toString(){
            StringBuilder buf = new StringBuilder(super.toString());
            buf.deleteCharAt(buf.length() - 1);
            boolean tmpIsMBeanSet = isMBeanSet;
            ObjectName tmpObjectName = objectName;
            QueryExp tmpQueryExp = queryExp;
            if(tmpObjectName == null && watcher != null){
                tmpIsMBeanSet = watcher.isMBeanSet;
                tmpObjectName = watcher.objectName;
                tmpQueryExp = watcher.queryExp;
            }
            buf.append(",objectName=").append(tmpObjectName);
            buf.append(",isMBeanSet=").append(tmpIsMBeanSet);
            buf.append(",queryExp=").append(tmpQueryExp);
            buf.append('}');
            return buf.toString();
        }
    }

    /**
     * Managed Beanの属性を監視対象とする{@link MBeanWatcherService.MBeanTarget}の実装クラス。<p>
     *
     * @author M.Takata
     */
    public static class Attribute extends MBeanTarget{

        private static final long serialVersionUID = 253836219685470254L;
        private String name;

        /**
         * 監視対象とするManaged Beanの属性名を設定する。<p>
         *
         * @param name 属性名
         */
        public void setName(String name){
            this.name = name;
        }

        /**
         * 監視対象とするManaged Beanの属性名を取得する。<p>
         *
         * @return 属性名
         */
        public String getName(){
            return name;
        }

        /**
         * 出力する際のキーを取得する。<p>
         * {@link #setKey(String)}で設定していない場合は、オブジェクト名#属性名を返す。<br>
         *
         * @return キー
         */
        public String getKey(){
            return super.getKey() != null ? super.getKey() : getObjectName() + '#' + getName();
        }

        /**
         * 指定されたオブジェクト名のManaged Beanが持つ監視対象の属性値を取得する。<p>
         *
         * @param connection JMX接続
         * @param objectName オブジェクト名
         * @return 監視対象の値
         * @exception Exception 監視対象の値の取得に失敗した場合
         */
        public Object getValue(MBeanServerConnection connection, ObjectName objectName) throws Exception{
            return connection.getAttribute(objectName, name);
        }

        /**
         * この監視対象の文字列表現を取得する。<p>
         *
         * @return 文字列表現
         */
        public String toString(){
            StringBuilder buf = new StringBuilder(super.toString());
            buf.deleteCharAt(buf.length() - 1);
            buf.append(",name=").append(name);
            buf.append('}');
            return buf.toString();
        }
    }

    /**
     * Managed Beanの複数の属性を監視対象とする{@link MBeanWatcherService.MBeanTarget}の実装クラス。<p>
     *
     * @author M.Takata
     */
    public static class Attributes extends MBeanTarget{

        private static final long serialVersionUID = -8344987725214428096L;
        private String[] names;

        /**
         * 監視対象とするManaged Beanの属性名の配列を設定する。<p>
         *
         * @param names 属性名の配列
         */
        public void setNames(String[] names){
            this.names = names;
        }

        /**
         * 監視対象とするManaged Beanの属性名の配列を取得する。<p>
         *
         * @return 属性名の配列
         */
        public String[] getNames(){
            return names;
        }

        /**
         * 指定されたオブジェクト名のManaged Beanが持つ、監視対象の属性名とその値のMapを取得する。<p>
         *
         * @param connection JMX接続
         * @param objectName オブジェクト名
         * @return 監視対象の属性名とその値のMap
         * @exception Exception 監視対象の値の取得に失敗した場合
         */
        public Object getValue(MBeanServerConnection connection, ObjectName objectName) throws Exception{
            List list = connection.getAttributes(objectName, names);
            Map result = new LinkedHashMap();
            if(list != null){
                for(int i = 0; i < list.size(); i++){
                    javax.management.Attribute attribute = (javax.management.Attribute)list.get(i);
                    result.put(attribute.getName(), attribute.getValue());
                }
            }
            return result;
        }

        /**
         * この監視対象の文字列表現を取得する。<p>
         *
         * @return 文字列表現
         */
        public String toString(){
            StringBuilder buf = new StringBuilder(super.toString());
            buf.deleteCharAt(buf.length() - 1);
            buf.append(",names=");
            if(names == null){
                buf.append(names);
            }else{
                buf.append('[');
                for(int i = 0; i < names.length; i++){
                    if(i != 0){
                        buf.append(',');
                    }
                    buf.append(names[i]);
                }
                buf.append(']');
            }
            buf.append('}');
            return buf.toString();
        }
    }

    /**
     * Managed Beanのオペレーションを監視対象とする{@link MBeanWatcherService.MBeanTarget}の実装クラス。<p>
     *
     * @author M.Takata
     */
    public static class Operation extends MBeanTarget{
        private static final long serialVersionUID = 8874947184358756744L;
        private String name;
        private List params;
        private String[] signiture;

        /**
         * 監視対象とするManaged Beanのオペレーション名を設定する。<p>
         *
         * @param name オペレーション名
         */
        public void setName(String name){
            this.name = name;
        }

        /**
         * 監視対象とするManaged Beanのオペレーション名を取得する。<p>
         *
         * @return オペレーション名
         */
        public String getName(){
            return name;
        }

        /**
         * 監視対象とするManaged Beanのオペレーションのシグニチャを設定する。<p>
         *
         * @param sgn オペレーションのシグニチャ
         */
        public void setSigniture(String[] sgn){
            signiture = sgn;
        }

        /**
         * 監視対象とするManaged Beanのオペレーションのシグニチャを取得する。<p>
         *
         * @return オペレーションのシグニチャ
         */
        public String[] getSigniture(){
            return signiture;
        }

        /**
         * 監視対象とするManaged Beanのオペレーションの引数を設定する。<p>
         *
         * @param params オペレーションの引数の配列
         */
        public void setParameters(Object[] params){
            if(this.params == null){
                this.params = new ArrayList();
            }
            for(int i = 0; i < params.length; i++){
                this.params.add(params[i]);
            }
        }

        /**
         * 監視対象とするManaged Beanのオペレーションの引数を追加する。<p>
         *
         * @param param オペレーションの引数
         */
        public void addParameter(Object param){
            if(params == null){
                params = new ArrayList();
            }
            params.add(param);
        }

        /**
         * 監視対象とするManaged Beanのオペレーションの引数リストを取得する。<p>
         *
         * @return オペレーションの引数リスト
         */
        public List getParameterList(){
            return params;
        }

        /**
         * 出力する際のキーを取得する。<p>
         * {@link #setKey(String)}で設定していない場合は、オブジェクト名#オペレーション名([パラメータ1,パラメータ2,....])を返す。<br>
         *
         * @return キー
         */
        public String getKey(){
            return super.getKey() != null ? super.getKey() : getObjectName() + '#' + getName() + '(' + (params == null ? "" : params.toString()) + ')';
        }

        /**
         * 指定されたオブジェクト名のManaged Beanが持つ監視対象のオペレーションの戻り値を取得する。<p>
         *
         * @param connection JMX接続
         * @param objectName オブジェクト名
         * @return 監視対象の値
         * @exception Exception 監視対象の値の取得に失敗した場合
         */
        public Object getValue(MBeanServerConnection connection, ObjectName objectName) throws Exception{
            return connection.invoke(objectName, name, params == null ? null : params.toArray(), signiture);
        }

        /**
         * この監視対象の文字列表現を取得する。<p>
         *
         * @return 文字列表現
         */
        public String toString(){
            StringBuilder buf = new StringBuilder(super.toString());
            buf.deleteCharAt(buf.length() - 1);
            buf.append(",name=").append(name);
            buf.append(",params=").append(params);
            buf.append('}');
            return buf.toString();
        }
    }

    /**
     * ラップ監視対象。<p>
     * 監視対象をラップして特定の処理を付加する監視対象基底クラス。<br>
     *
     * @author M.Takata
     */
    public abstract static class WrapTarget extends Target{

        private static final long serialVersionUID = 6038448327332359108L;
        protected Target target;

        /**
         * {@link MBeanWatcherService}のサービス名を設定する。<p>
         * ラップしている監視対象にもMBeanWatcherServiceのサービス名を設定する。<br>
         *
         * @param name MBeanWatcherServiceのサービス名
         */
        public void setWatcherServiceName(ServiceName name){
            super.setWatcherServiceName(name);
            target.setWatcherServiceName(name);
        }

        /**
         * {@link MBeanWatcherService}を設定する。<p>
         * ラップしている監視対象にもMBeanWatcherServiceを設定する。<br>
         *
         * @param watcher MBeanWatcherService
         */
        protected void setWatcherService(MBeanWatcherService watcher){
            super.setWatcherService(watcher);
            target.setWatcherService(watcher);
        }

        /**
         * {@link Logger}を設定する。<p>
         * ラップしている監視対象にもLoggerを設定する。<br>
         *
         * @param logger Logger
         */
        public void setLogger(Logger logger){
            super.setLogger(logger);
            target.setLogger(logger);
        }

        /**
         * ラップする監視対象{@link Logger}を設定する。<p>
         *
         * @param target 監視対象
         */
        public void setTarget(Target target){
            this.target = target;
        }

        /**
         * ラップする監視対象{@link Logger}を取得する。<p>
         *
         * @return 監視対象
         */
        public Target getTarget(){
            return target;
        }

        /**
         * リセットする。<p>
         * ラップしている監視対象もリセットする。<br>
         */
        public void reset(){
            super.reset();
            target.reset();
        }

        /**
         * 開始する。<p>
         * ラップしている監視対象も開始する。<br>
         */
        public void start(){
            super.start();
            target.start();
        }

        /**
         * 終了する。<p>
         * ラップしている監視対象も終了する。<br>
         */
        public void stop(){
            target.stop();
            super.stop();
        }

        /**
         * この監視対象の文字列表現を取得する。<p>
         *
         * @return 文字列表現
         */
        public String toString(){
            StringBuilder buf = new StringBuilder(super.toString());
            buf.deleteCharAt(buf.length() - 1);
            buf.append(",target=").append(target);
            buf.append('}');
            return buf.toString();
        }
    }

    /**
     * ラップした監視対象の値をチェックする{@link MBeanWatcherService.WrapTarget}の実装クラス。<br>
     *
     * @author M.Takata
     */
    public static class Check extends WrapTarget{

        private static final long serialVersionUID = -8821714143031466189L;
        private boolean isNullToZero;
        private ServiceName loggerServiceName;
        private List checkConditions = new ArrayList();

        /**
         * 監視対象が数値型でnullの場合に、ゼロとみなすかどうかを設定する。<p>
         * デフォルトは、falseでゼロとみなさない。<br>
         *
         * @param isNullToZero ゼロとみなす場合、true
         */
        public void setNullToZero(boolean isNullToZero){
            this.isNullToZero = isNullToZero;
        }

        /**
         * 監視対象が数値型でnullの場合に、ゼロとみなすかどうかを判定する。<p>
         *
         * @return trueの場合、ゼロとみなす
         */
        public boolean isNullToZero(){
            return isNullToZero;
        }

        /**
         * チェックエラーのログを出力する{@link Logger}サービスのサービス名を設定する。<p>
         * 指定しない場合は、このサービスの{@link ServiceBase#getLogger()}で取得されるLoggerでログ出力を行う。<br>
         *
         * @param name Loggerサービスのサービス名
         */
        public void setLoggerServiceName(ServiceName name){
            loggerServiceName = name;
        }

        /**
         * チェックエラーのログを出力する{@link Logger}サービスのサービス名を取得する。<p>
         *
         * @return Loggerサービスのサービス名
         */
        public ServiceName getLoggerServiceName(){
            return loggerServiceName;
        }

        /**
         * {@link Logger}を取得する。<p>
         * {@link #setLoggerServiceName(ServiceName)}が設定されている場合は、そのLoggerを返す。<br>
         *
         * @return Logger
         */
        public Logger getLogger(){
            if(loggerServiceName == null){
                return super.getLogger();
            }else{
                return (Logger)ServiceManagerFactory.getServiceObject(loggerServiceName);
            }
        }

        /**
         * チェックする条件を追加する。<p>
         * 追加された順番にチェックをして、チェックエラーになると後続の条件はチェックせずにリセットする。<br>
         *
         * @param condition チェック条件
         */
        public void addCheckCondition(Condition condition){
            checkConditions.add(condition);
        }

        /**
         * チェックする条件のリストを取得する。<p>
         *
         * @return チェック条件のリスト
         */
        public List getCheckConditionList(){
            return checkConditions;
        }

        /**
         * ラップした監視対象のオペレーションの戻り値を取得し、チェックを行ってチェックエラーの場合はログを出力する。<p>
         * チェックする条件を追加された順番にチェックして、チェックエラーになると後続の条件はチェックせずにリセットする。<br>
         *
         * @param connection JMX接続
         * @return 監視対象の値
         * @exception Exception 監視対象の値の取得に失敗した場合
         */
        public Object getValue(MBeanServerConnection connection) throws Exception{
            Object value = target.getValue(connection);
            if(isNullToZero){
                value = Target.toBigDecimal(value, isNullToZero);
            }
            boolean checkError = false;
            for(int i = 0, imax = checkConditions.size(); i < imax; i++){
                Condition condition = (Condition)checkConditions.get(i);
                condition.setWatcherService(watcher);
                if(checkError){
                    condition.reset();
                }else if(!condition.check(value, getLogger(), getWatcherServiceName(), getKey())){
                    checkError = true;
                }
            }
            if(value != null && contextKey != null){
                watcher.setContextValue(contextKey, value);
            }
            return value;
        }

        /**
         * リセットする。<p>
         * ラップしている監視対象及び、監視対象のチェックエラー状態もリセットする。<br>
         */
        public void reset(){
            super.reset();
            for(int i = 0, imax = checkConditions.size(); i < imax; i++){
                Condition condition = (Condition)checkConditions.get(i);
                condition.reset();
            }
        }

        /**
         * この監視対象の文字列表現を取得する。<p>
         *
         * @return 文字列表現
         */
        public String toString(){
            StringBuilder buf = new StringBuilder(super.toString());
            buf.deleteCharAt(buf.length() - 1);
            buf.append(",checkConditions=").append(checkConditions);
            buf.append(",isNullToZero=").append(isNullToZero);
            buf.append(",loggerServiceName=").append(loggerServiceName);
            buf.append('}');
            return buf.toString();
        }

        private static class ErrorCountComparator implements Comparator, Serializable{

            private static final long serialVersionUID = -8997521049688710362L;

            public int compare(Object o1, Object o2){
                return ((Comparable)o2).compareTo(o1);
            }
        }

        /**
         * チェック条件。<br>
         *
         * @author M.Takata
         */
        public static class Condition implements Serializable{

            private static final long serialVersionUID = 451166286622455937L;

            public static final String CONTEXT = "context";
            public static final String VALUE = "value";
            
            private String description;
            private transient Expression checkExpression;
            private String expression;
            private Map idMap = new TreeMap(new ErrorCountComparator());
            private int errorCount;
            private List checkTimes;
            private List checkTerms;
            private boolean isOnceOutputLog;
            private ServiceName interpreterServiceName;
            private transient jp.ossc.nimbus.service.interpreter.Interpreter interpreter;
            private transient MBeanWatcherService watcher;
            private boolean lastCheckResult = true;

            /**
             * {@link MBeanWatcherService}を設定する。<p>
             *
             * @param watcher MBeanWatcherService
             */
            protected void setWatcherService(MBeanWatcherService watcher){
                this.watcher = watcher;
            }

            public void setInterpreterServiceName(ServiceName name){
                interpreterServiceName = name;
            }

            public void setInterpreter(jp.ossc.nimbus.service.interpreter.Interpreter interpreter){
                this.interpreter = interpreter;
            }
            
            /**
             * 説明を取得する。<p>
             *
             * @return 説明
             */
            public String getDescription(){
                return description;
            }
            
            /**
             * 説明を設定する。<p>
             *
             * @param desc 説明
             */
            public void setDescription(String desc){
                description = desc;
            }
            
            /**
             * 監視対象をチェックする条件式を設定する。<p>
             * 条件式は、{@link jp.ossc.nimbus.service.interpreter.Interpreter Interpreter}を指定していない場合は、The Apache Jakarta Projectの Commons Jexl(http://jakarta.apache.org/commons/jexl/)で評価する。<br>
             * 監視対象値を表す"value"キーワードを含め、監視対象値を評価してbooleanを返す式とする。<br>
             * 条件式の評価がtrueになると、チェックエラーとみなされる。<br>
             *
             * @param expression 条件式
             */
            public void setCheckExpression(String expression){
                this.expression = expression;
            }

            /**
             * 監視対象をチェックする条件式を取得する。<p>
             *
             * @return 条件式
             */
            public String getCheckExpression(){
                return expression;
            }

            /**
             * チェックエラー時に出力するログのメッセージIDを設定する。<p>
             * {@link #setLogMessageIdByErrorCount(int, String) setLogMessageIdByErrorCount(1, id)}で呼び出すのと等価。<br>
             *
             * @param id ログのメッセージID
             */
            public void setLogMessageId(String id){
                setLogMessageIdByErrorCount(1, id);
            }

            /**
             * チェックエラーが指定した回数連続して発生した時に出力するログのメッセージIDを設定する。<p>
             * チェックエラーの回数は、チェックエラーが連続して発生しなかった場合と、指定されたチェックエラー回数の最大の値の回数まで増えるとリセットされる。<br>
             * ログ出力の際には、サービス名、キー、条件式、監視対象の値、連続して発生したチェックエラーの回数をログ出力の引数として渡す。<br>
             * 汎用のログメッセージIDとして、{@link #MSG_ID_CHECK_WARN}、{@link #MSG_ID_CHECK_ERROR}、{@link #MSG_ID_CHECK_FATAL}を用意している。<br>
             *
             * @param errorCount チェックエラー回数
             * @param id ログのメッセージID
             */
            public void setLogMessageIdByErrorCount(int errorCount, String id){
                idMap.put(new Integer(errorCount), id);
            }

            /**
             * チェックを実行する時刻を設定する。<p>
             *
             * @param times HH:mm:ssまたは、HH:mm:ss.SSSの時刻文字列配列
             * @exception ParseException 指定された時刻文字列のパースに失敗した場合
             */
            public void setCheckTimes(String[] times) throws ParseException{
                if(times == null || times.length == 0){
                    checkTimes = null;
                }else{
                    checkTimes = new ArrayList();
                    for(int i = 0; i < times.length; i++){
                        CheckTime ct = new CheckTime(times[i]);
                        checkTimes.add(ct);
                    }
                    Collections.sort(checkTimes);
                }
            }

            /**
             * チェックを実行する時刻を取得する。<p>
             *
             * @return HH:mm:ssまたは、HH:mm:ss.SSSの時刻文字列配列
             */
            public String[] getCheckTimes(){
                if(checkTimes == null){
                    return null;
                }
                String[] result = new String[checkTimes.size()];
                for(int i = 0; i < checkTimes.size(); i++){
                    result[i] = checkTimes.get(i).toString();
                }
                return result;
            }

            /**
             * チェックを実行する期間を設定する。<p>
             *
             * @param terms HH:mm:ssまたは、HH:mm:ss.SSSの時刻文字列を-で連結した開始時刻-終了時刻の文字列配列。終了時刻は期間に含まれない。
             * @exception ParseException 指定された時刻文字列のパースに失敗した場合
             */
            public void setCheckTerms(String[] terms) throws ParseException{
                if(terms == null || terms.length == 0){
                    checkTerms = null;
                }else{
                    checkTerms = new ArrayList();
                    for(int i = 0; i < terms.length; i++){
                        Term term = new Term(terms[i]);
                        checkTerms.add(term);
                    }
                    Collections.sort(checkTerms);
                }
            }

            /**
             * チェックを実行する期間を取得する。<p>
             *
             * @return HH:mm:ssまたは、HH:mm:ss.SSSの時刻文字列を-で連結した開始時刻-終了時刻の文字列配列。終了時刻は期間に含まれない。
             */
            public String[] getCheckTerms(){
                if(checkTerms == null){
                    return null;
                }
                String[] result = new String[checkTerms.size()];
                for(int i = 0; i < checkTerms.size(); i++){
                    result[i] = checkTerms.get(i).toString();
                }
                return result;
            }

            /**
             * チェックエラーが発生した場合に最初の一回のみログ出力するかを設定する。<p>
             *
             * @param isOnce チェックエラーが発生した場合に最初の一回のみログ出力するか
             */
            public void setOnceOutputLog(boolean isOnce) {
                isOnceOutputLog = isOnce;
            }

            /**
             * チェックエラーが発生した場合に最初の一回のみログ出力するかを取得する。<p>
             *
             * @return チェックエラーが発生した場合に最初の一回のみログ出力するか
             */
            public boolean isOnceOutputLog() {
                return isOnceOutputLog;
            }
            
            /**
             * 直近のチェック結果を取得する。<p>
             *
             * @return 直近のチェック結果
             */
            public boolean getLastCheckResult(){
                return lastCheckResult;
            }
            
            /**
             * 指定された値のチェックを行ってチェックエラーが発生するとログを出力する。<p>
             *
             * @param value チェック対象の値
             * @param logger ログ出力を行うLogger
             * @param watcherServiceName 監視サービスのサービス名
             * @param key 監視対象のキー
             * @return チェック結果。チェックエラーの場合はfalse
             * @exception Exception チェック処理に失敗した場合
             */
            protected boolean check(Object value, Logger logger, ServiceName watcherServiceName, String key) throws Exception{
                if(errorCount == -1){
                    lastCheckResult = true;
                    return true;
                }
                List currentCheckTimes = null;
                Day nowDay = null;
                Time nowTime = null;
                if(checkTimes != null){
                    Calendar now = Calendar.getInstance();
                    nowDay = new Day(now);
                    nowTime = new Time(now);
                    for(int i = 0; i < checkTimes.size(); i++){
                        CheckTime ct = (CheckTime)checkTimes.get(i);
                        if(ct.isChecked(nowDay) || nowTime.compareTo(ct) < 0){
                            continue;
                        }
                        if(currentCheckTimes == null){
                            currentCheckTimes = new ArrayList();
                        }
                        currentCheckTimes.add(ct);
                    }
                    if(currentCheckTimes == null){
                        lastCheckResult = true;
                        return true;
                    }
                }
                if(currentCheckTimes == null && checkTerms != null){
                    if(nowTime == null){
                        nowTime = new Time(Calendar.getInstance());
                    }
                    boolean check = false;
                    for(int i = 0; i < checkTerms.size(); i++){
                        Term term = (Term)checkTerms.get(i);
                        if(!term.contains(nowTime)){
                            continue;
                        }
                        check = true;
                    }
                    if(!check){
                        lastCheckResult = true;
                        return true;
                    }
                }
                jp.ossc.nimbus.service.interpreter.Interpreter itr = interpreter;
                if(itr == null && interpreterServiceName != null){
                    itr = (jp.ossc.nimbus.service.interpreter.Interpreter)ServiceManagerFactory.getServiceObject(interpreterServiceName);
                }
                Boolean error = null;
                if(itr == null){
                    JexlContext jexlContext = JexlHelper.createContext();
                    jexlContext.getVars().put(CONTEXT, watcher.getContextMap());
                    jexlContext.getVars().put(VALUE, value);
                    if(checkExpression == null){
                        checkExpression = ExpressionFactory.createExpression(expression);
                    }
                    error = (Boolean)checkExpression.evaluate(jexlContext);
                }else{
                    Map param = new HashMap();
                    param.put(CONTEXT, watcher.getContextMap());
                    param.put(VALUE, value);
                    error = (Boolean)itr.evaluate(expression, param);
                }
                if(error.booleanValue()){
                    errorCount++;
                    boolean isFirst = true;
                    Iterator entries = idMap.entrySet().iterator();
                    while(entries.hasNext()){
                        Map.Entry entry = (Map.Entry)entries.next();
                        Integer count = (Integer)entry.getKey();
                        if(errorCount >= count.intValue()){
                            if(isFirst || errorCount == count.intValue()){
                                logger.write((String)entry.getValue(), new Object[]{watcherServiceName, key, expression, value, count});
                            }
                            if(isFirst){
                                if(isOnceOutputLog){
                                    errorCount = -1;
                                }else{
                                    errorCount = 0;
                                }
                                if(currentCheckTimes != null){
                                    for(int i = 0; i < currentCheckTimes.size(); i++){
                                        CheckTime ct = (CheckTime)currentCheckTimes.get(i);
                                        ct.setCheckDay(nowDay);
                                    }
                                }
                            }
                            lastCheckResult = false;
                            return false;
                        }
                        isFirst = false;
                    }
                }else{
                    errorCount = 0;
                }
                if(currentCheckTimes != null){
                    for(int i = 0; i < currentCheckTimes.size(); i++){
                        CheckTime ct = (CheckTime)currentCheckTimes.get(i);
                        ct.setCheckDay(nowDay);
                    }
                }
                lastCheckResult = true;
                return true;
            }

            /**
             * 監視対象のチェックエラー回数をリセットする。<br>
             */
            protected void reset(){
                errorCount = 0;
            }

            /**
             * この監視対象の文字列表現を取得する。<p>
             *
             * @return 文字列表現
             */
            public String toString(){
                StringBuilder buf = new StringBuilder();
                buf.append("Condition{");
                buf.append(",description=").append(description);
                buf.append(",expression=").append(expression);
                buf.append(",checkTimes=").append(checkTimes);
                buf.append(",checkTerms=").append(checkTerms);
                buf.append(",isOnceOutputLog=").append(isOnceOutputLog);
                buf.append(",idMap=").append(idMap);
                buf.append(",lastCheckResult=").append(lastCheckResult);
                buf.append(",errorCount=").append(errorCount);
                buf.append('}');
                return buf.toString();
            }

            private class Day implements Serializable, Comparable{

                private static final long serialVersionUID = -3482088973138104434L;
                private int day;

                public Day(Calendar now){
                    day += (now.get(Calendar.YEAR) * 1000);
                    day += now.get(Calendar.DAY_OF_YEAR);
                }

                public int hashCode(){
                    return day;
                }

                public boolean equals(Object obj){
                    if(obj == this){
                        return true;
                    }
                    if(obj == null || !(obj instanceof Day)){
                        return false;
                    }
                    Day cmp = (Day)obj;
                    return day == cmp.day;
                }

                public int compareTo(Object obj){
                    if(obj == null || !(obj instanceof Day)){
                        return 1;
                    }
                    Day cmp = (Day)obj;
                    if(day == cmp.day){
                        return 0;
                    }else{
                        return day > cmp.day ? 1 : -1;
                    }
                }

                public String toString(){
                    return Integer.toString(day);
                }
            }

            private class Time implements Serializable, Comparable{

                private static final long serialVersionUID = 5738491787638885636L;
                private static final String TIME_FORMAT1 = "HH:mm:ss";
                private static final String TIME_FORMAT2 = "HH:mm:ss.SSS";

                private int time;

                public Time(Calendar now){
                    time += (now.get(Calendar.HOUR_OF_DAY) * 10000000);
                    time += (now.get(Calendar.MINUTE) * 100000);
                    time += (now.get(Calendar.SECOND) * 1000);
                    time += now.get(Calendar.MILLISECOND);
                }

                public Time(String time) throws ParseException{
                    Date date = time.length() == 8 ? new SimpleDateFormat(TIME_FORMAT1).parse(time) : new SimpleDateFormat(TIME_FORMAT2).parse(time);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    this.time += (cal.get(Calendar.HOUR_OF_DAY) * 10000000);
                    this.time += (cal.get(Calendar.MINUTE) * 100000);
                    this.time += (cal.get(Calendar.SECOND) * 1000);
                    this.time += cal.get(Calendar.MILLISECOND);
                }

                public int hashCode(){
                    return time;
                }

                public boolean equals(Object obj){
                    if(obj == this){
                        return true;
                    }
                    if(obj == null || !(obj instanceof Time)){
                        return false;
                    }
                    Time cmp = (Time)obj;
                    return time == cmp.time;
                }

                public int compareTo(Object obj){
                    if(obj == null || !(obj instanceof Time)){
                        return 1;
                    }
                    Time cmp = (Time)obj;
                    if(time == cmp.time){
                        return 0;
                    }else{
                        return time > cmp.time ? 1 : -1;
                    }
                }

                public String toString(){
                    StringBuilder buf = new StringBuilder();
                    buf.append(time);
                    int length = buf.length();
                    buf.insert(length - 3, '.');
                    buf.insert(length - 5, ':');
                    buf.insert(length - 7, ':');
                    return buf.toString();
                }
            }

            private class Term implements Serializable, Comparable{
                private static final long serialVersionUID = 4874635307721480812L;
                private Time from;
                private Time to;
                public Term(String term) throws ParseException{
                    if(term.indexOf('-') == -1){
                        throw new ParseException("Format is 'from-to' : " + term, term.length());
                    }
                    final String[] times = term.split("-");
                    if(times.length != 2){
                        throw new ParseException("Format is 'from-to' : " + term, term.length());
                    }
                    from = new Time(times[0]);
                    to = new Time(times[1]);
                }

                public boolean contains(Time time){
                    return time.compareTo(from) >= 0 && time.compareTo(to) < 0;
                }

                public int hashCode(){
                    return from.hashCode() + to.hashCode();
                }

                public boolean equals(Object obj){
                    if(obj == this){
                        return true;
                    }
                    if(obj == null || !(obj instanceof Term)){
                        return false;
                    }
                    Term cmp = (Term)obj;
                    return from.equals(cmp.from) && to.equals(cmp.to);
                }

                public int compareTo(Object obj){
                    if(obj == null || !(obj instanceof Term)){
                        return 1;
                    }
                    Term cmp = (Term)obj;
                    return from.compareTo(cmp.from);
                }

                public String toString(){
                    StringBuilder buf = new StringBuilder();
                    return buf.append(from).append('-').append(to).toString();
                }
            }

            private class CheckTime extends Time{
                private static final long serialVersionUID = 5309699914961118492L;
                private Day checkDay;
                public CheckTime(String time) throws ParseException{
                    super(time);
                }

                public boolean isChecked(Day currentDay){
                    if(checkDay == null){
                        return false;
                    }
                    return currentDay.compareTo(checkDay) <= 0;
                }

                public void setCheckDay(Day day){
                    checkDay = day;
                }
            }
        }
    }

    /**
     * 編集監視対象。<p>
     * 監視対象をラップして、監視対象の値に編集処理を行う監視対象基底クラス。<br>
     *
     * @author M.Takata
     */
    public abstract static class EditTarget extends WrapTarget{

        private static final long serialVersionUID = -3292286495172239503L;

        protected boolean isElementEdit;

        /**
         * 監視対象の値が集合や配列の場合に、その各要素に対して編集を行うかどうかを設定する。<p>
         * デフォルトは、falseで要素に対する編集は行わない。<br>
         *
         * @param isElement 各要素に対して編集を行う場合true
         */
        public void setElementEdit(boolean isElement){
            isElementEdit = isElement;
        }

        /**
         * 監視対象の値が集合や配列の場合に、その各要素に対して編集を行うかどうかを判定する。<p>
         *
         * @return trueの場合、各要素に対して編集を行う
         */
        public boolean isElementEdit(){
            return isElementEdit;
        }

        /**
         * ラップした監視対象の値を編集して取得する。<p>
         *
         * @param connection JMX接続
         * @return 監視対象の値
         * @exception Exception 監視対象の値の取得に失敗した場合
         */
        public Object getValue(MBeanServerConnection connection) throws Exception{
            Object value = target.getValue(connection);
            if(isElementEdit){
                Object[] array = null;
                if(value instanceof Map){
                    array = ((Map)value).values().toArray();
                }else if(value instanceof Collection){
                    array = ((Collection)value).toArray();
                }else if(value.getClass().isArray()){
                    Object[] sourceArray = (Object[])value;
                    array = new Object[sourceArray.length];
                    System.arraycopy(sourceArray, 0, array, 0, sourceArray.length);
                }else{
                    value = edit(value);
                }
                if(array != null){
                    List list = new ArrayList();
                    for(int i = 0; i < array.length; i++){
                        list.add(edit(array[i]));
                    }
                    value = list;
                }
            }else{
                value = edit(value);
            }
            if(value != null && contextKey != null){
                watcher.setContextValue(contextKey, value);
            }
            return value;
        }

        /**
         * 指定された値を編集する。<p>
         *
         * @param value 編集対象の値
         * @return 編集結果の値
         * @exception Exception 編集処理に失敗した場合
         */
        protected abstract Object edit(Object value) throws Exception;

        /**
         * この監視対象の文字列表現を取得する。<p>
         *
         * @return 文字列表現
         */
        public String toString(){
            StringBuilder buf = new StringBuilder(super.toString());
            buf.deleteCharAt(buf.length() - 1);
            buf.append(",isElementEdit=").append(isElementEdit);
            buf.append('}');
            return buf.toString();
        }
    }

    /**
     * ラップした監視対象の値を{@link jp.ossc.nimbus.service.interpreter.Interpreter Interpreter}で編集する{@link MBeanWatcherService.EditTarget}の実装クラス。<p>
     *
     * @author M.Takata
     */
    public static class Interpreter extends EditTarget{

        private static final long serialVersionUID = 5874469234835823412L;
        public static final String VALUE = "value";
        public static final String CONTEXT = "context";

        private ServiceName interpreterServiceName;
        private jp.ossc.nimbus.service.interpreter.Interpreter interpreter;
        private String expression;

        public void setInterpreterServiceName(ServiceName name){
            interpreterServiceName = name;
        }
        public void setInterpreter(jp.ossc.nimbus.service.interpreter.Interpreter interpreter){
            this.interpreter = interpreter;
        }

        /**
         * 編集式を設定する。<p>
         * 監視対象値を表す"value"キーワードを含め、監視対象値を編集して返す式とする。<br>
         *
         * @param expression 編集式
         */
        public void setExpression(String expression){
            this.expression = expression;
        }

        /**
         * 指定された値を編集する。<p>
         *
         * @param value 編集対象の値
         * @return 編集結果の値
         * @exception Exception 編集処理に失敗した場合
         */
        protected Object edit(Object value) throws Exception{
            jp.ossc.nimbus.service.interpreter.Interpreter itr = interpreter;
            if(itr == null && interpreterServiceName != null){
                itr = (jp.ossc.nimbus.service.interpreter.Interpreter)ServiceManagerFactory.getServiceObject(interpreterServiceName);
            }
            Map param = new HashMap();
            param.put(CONTEXT, watcher.getContextMap());
            param.put(VALUE, value);
            return itr.evaluate(expression, param);
        }

        /**
         * この監視対象の文字列表現を取得する。<p>
         *
         * @return 文字列表現
         */
        public String toString(){
            StringBuilder buf = new StringBuilder(super.toString());
            buf.deleteCharAt(buf.length() - 1);
            buf.append(",expression=").append(expression);
            buf.append('}');
            return buf.toString();
        }
    }

    /**
     * ラップした監視対象の値のプロパティを取得する{@link MBeanWatcherService.EditTarget}の実装クラス。<p>
     *
     * @author M.Takata
     */
    public static class Property extends EditTarget{

        private static final long serialVersionUID = 5990303499820230369L;
        private jp.ossc.nimbus.beans.Property property;
        private boolean isIgnoreNullProperty;

        /**
         * ラップした監視対象の値から取得したいプロパティを設定する。<p>
         *
         * @param property プロパティ
         */
        public void setProperty(jp.ossc.nimbus.beans.Property property){
            property.setIgnoreNullProperty(isIgnoreNullProperty);
            this.property = property;
        }

        /**
         * ラップした監視対象の値から取得したいプロパティを取得する。<p>
         *
         * @return プロパティ
         */
        public jp.ossc.nimbus.beans.Property getProperty(){
            return property;
        }

        /**
         * null参照のプロパティを取得使用とした場合に、例外をthrowするかどうかを設定する。<p>
         * デフォルトは、false。<br>
         *
         * @param isIgnore null参照の時に例外をthrowしない場合はtrue
         */
        public void setIgnoreNullProperty(boolean isIgnore){
            isIgnoreNullProperty = isIgnore;
            if(property != null){
                property.setIgnoreNullProperty(isIgnoreNullProperty);
            }
        }

        /**
         * null参照のプロパティを取得使用とした場合に、例外をthrowするかどうかを判定する。<p>
         *
         * @return trueの場合、null参照の時に例外をthrowしない
         */
        public boolean isIgnoreNullProperty(){
            return isIgnoreNullProperty;
        }

        /**
         * 指定された値から設定されたプロパティを取得する。<p>
         *
         * @param value 編集対象の値
         * @return 編集結果の値
         * @exception Exception 編集処理に失敗した場合
         */
        protected Object edit(Object value) throws Exception{
            return property.getProperty(value);
        }

        /**
         * この監視対象の文字列表現を取得する。<p>
         *
         * @return 文字列表現
         */
        public String toString(){
            StringBuilder buf = new StringBuilder(super.toString());
            buf.deleteCharAt(buf.length() - 1);
            buf.append(",property=").append(property);
            buf.append(",isIgnoreNullProperty=").append(isIgnoreNullProperty);
            buf.append('}');
            return buf.toString();
        }
    }

    /**
     * ラップした監視対象の値を{@link Converter}で変換する{@link MBeanWatcherService.EditTarget}の実装クラス。<p>
     *
     * @author M.Takata
     */
    public static class Convert extends EditTarget{

        private static final long serialVersionUID = 1737702373890781789L;
        private ServiceName converterServiceName;
        private Converter converter;

        /**
         * ラップした監視対象の値を変換する{@link Converter}を設定する。<p>
         *
         * @param converter Converter
         */
        public void setConverter(Converter converter){
            this.converter = converter;
        }

        /**
         * ラップした監視対象の値を変換する{@link Converter}を取得する。<p>
         *
         * @return Converter
         */
        public Converter getConverter(){
            return converter;
        }

        /**
         * ラップした監視対象の値を変換する{@link Converter}サービスのサービス名を設定する。<p>
         *
         * @param name Converterサービスのサービス名
         */
        public void setConverterServiceName(ServiceName name){
            converterServiceName = name;
        }

        /**
         * ラップした監視対象の値を変換する{@link Converter}サービスのサービス名を取得する。<p>
         *
         * @return Converterサービスのサービス名
         */
        public ServiceName getConverterServiceName(){
            return converterServiceName;
        }

        /**
         * 指定された値を設定された{@link Converter}で変換する。<p>
         *
         * @param value 編集対象の値
         * @return 編集結果の値
         * @exception Exception 編集処理に失敗した場合
         */
        protected Object edit(Object value) throws Exception{
            Converter conv = converter;
            if(converterServiceName != null){
                conv = (Converter)ServiceManagerFactory.getServiceObject(converterServiceName);
            }
            return conv.convert(value);
        }

        /**
         * この監視対象の文字列表現を取得する。<p>
         *
         * @return 文字列表現
         */
        public String toString(){
            StringBuilder buf = new StringBuilder(super.toString());
            buf.deleteCharAt(buf.length() - 1);
            buf.append(",converter=").append(converter);
            buf.append(",converterServiceName=").append(converterServiceName);
            buf.append('}');
            return buf.toString();
        }
    }

    /**
     * ラップした監視対象の値と前回の値の変化を取得する{@link MBeanWatcherService.WrapTarget}の実装クラス。<p>
     *
     * @author M.Takata
     */
    public static class Change extends WrapTarget{

        private static final long serialVersionUID = 315703698395044669L;
        private boolean isNullToZero;
        private Object lastValue;

        /**
         * ラップした監視対象の値が数値型でnullの場合に、ゼロとみなすかどうかを設定する。<p>
         * デフォルトは、falseでゼロとみなさない。<br>
         *
         * @param isNullToZero ゼロとみなす場合、true
         */
        public void setNullToZero(boolean isNullToZero){
            this.isNullToZero = isNullToZero;
        }

        /**
         * ラップした監視対象の値が数値型でnullの場合に、ゼロとみなすかどうかを判定する。<p>
         *
         * @return trueの場合、ゼロとみなす
         */
        public boolean isNullToZero(){
            return isNullToZero;
        }

        /**
         * ラップした監視対象の値から前回の値を引いた前回差を取得する。<p>
         *
         * @param connection JMX接続
         * @return 監視対象の値
         * @exception Exception 監視対象の値の取得に失敗した場合
         */
        public Object getValue(MBeanServerConnection connection) throws Exception{
            Object value = target.getValue(connection);
            Object changeValue = null;
            if(value instanceof Map){
                Map valueMap = (Map)value;
                Map lastValueMap = (lastValue instanceof Map) ? (Map)lastValue : null;
                Map changeMap = new HashMap();
                Iterator itr = valueMap.entrySet().iterator();
                while(itr.hasNext()){
                    Entry entry = (Entry)itr.next();
                    BigDecimal newValue = Target.toBigDecimal(entry.getValue(), isNullToZero);
                    if(lastValueMap == null){
                        changeMap.put(entry.getKey(), isNullToZero ? BigDecimal.ZERO : null);
                    }else if(lastValueMap.containsKey(entry.getKey())){
                        BigDecimal oldValue = Target.toBigDecimal(lastValueMap.remove(entry.getKey()), isNullToZero);
                        if(isNullToZero || (newValue != null && oldValue != null)){
                            changeMap.put(entry.getKey(), newValue.subtract(oldValue));
                        }else if(newValue == null && oldValue == null){
                            changeMap.put(entry.getKey(), null);
                        }else if(newValue != null && oldValue == null){
                            changeMap.put(entry.getKey(), newValue);
                        }else if(newValue == null && oldValue != null){
                            changeMap.put(entry.getKey(), BigDecimal.ZERO.subtract(oldValue));
                        }
                    }else{
                        changeMap.put(entry.getKey(), newValue);
                    }
                }
                if(lastValueMap != null && !lastValueMap.isEmpty()){
                    itr = lastValueMap.entrySet().iterator();
                    while(itr.hasNext()){
                        Entry entry = (Entry)itr.next();
                        BigDecimal oldValue = Target.toBigDecimal(entry.getValue(), isNullToZero);
                        if(isNullToZero || oldValue != null){
                            changeMap.put(entry.getKey(), BigDecimal.ZERO.subtract(oldValue));
                        }else{
                            changeMap.put(entry.getKey(), null);
                        }
                    }
                }
                changeValue = changeMap;
                lastValue = new HashMap((Map)value);
            }else{
                value = Target.toBigDecimal(value, isNullToZero);
                if(lastValue == null){
                    changeValue = isNullToZero ? BigDecimal.ZERO : null;
                } else {
                    changeValue = ((BigDecimal)value).subtract((BigDecimal)lastValue);
                }
                lastValue = value;
            }
            if(contextKey != null){
                watcher.setContextValue(contextKey, changeValue);
            }
            return changeValue;
        }

        /**
         * リセットする。<p>
         * ラップしている監視対象及び、前回の値もリセットする。<br>
         */
        public void reset(){
            super.reset();
            lastValue = null;
        }

        /**
         * この監視対象の文字列表現を取得する。<p>
         *
         * @return 文字列表現
         */
        public String toString(){
            StringBuilder buf = new StringBuilder(super.toString());
            buf.deleteCharAt(buf.length() - 1);
            buf.append(",isNullToZero=").append(isNullToZero);
            buf.append('}');
            return buf.toString();
        }
    }
    /**
     * ラップした監視対象の集合と前回の集合の変化を取得する{@link MBeanWatcherService.WrapTarget}の実装クラス。<p>
     *
     * @author M.Takata
     */
    public static class ChangeSet extends WrapTarget{

        private static final long serialVersionUID = -7480156060861954682L;
        private Collection lastValue;
        private boolean isChangeAdd = true;

        /**
         * 増分/減分のどちらを取得するか設定する。<p>
         * デフォルトは、trueで増分を取得する。<br>
         *
         * @param isAdd trueの場合は、増分。falseの場合は、減分を取得する。
         */
        public void setChangeAdd(boolean isAdd){
            isChangeAdd = isAdd;
        }

        /**
         * ラップした監視対象の値から前回の値を引いた前回差を取得する。<p>
         *
         * @param connection JMX接続
         * @return 監視対象の値
         * @exception Exception 監視対象の値の取得に失敗した場合
         */
        public Object getValue(MBeanServerConnection connection) throws Exception{
            Collection value = (Collection)target.getValue(connection);
            if(lastValue == null){
                lastValue = value;
                if(!isChangeAdd){
                    value = new HashSet();
                }
            }else{
                Collection tmpLastValue = new HashSet(value);
                if(isChangeAdd){
                    value = new HashSet(value);
                    value.removeAll(lastValue);
                }else{
                    lastValue.removeAll(value);
                    value = lastValue;
                }
                lastValue = tmpLastValue;
            }
            if(value != null && contextKey != null){
                watcher.setContextValue(contextKey, value);
            }
            return value;
        }

        /**
         * リセットする。<p>
         * ラップしている監視対象及び、前回の値もリセットする。<br>
         */
        public void reset(){
            super.reset();
            lastValue = null;
        }

        /**
         * この監視対象の文字列表現を取得する。<p>
         *
         * @return 文字列表現
         */
        public String toString(){
            StringBuilder buf = new StringBuilder(super.toString());
            buf.deleteCharAt(buf.length() - 1);
            buf.append(",isChangeAdd=").append(isChangeAdd);
            buf.append('}');
            return buf.toString();
        }
    }

    /**
     * ラップした監視対象の値を一定間隔で取得し、その集合を取得する{@link MBeanWatcherService.WrapTarget}の実装クラス。<p>
     *
     * @author M.Takata
     */
    public static class Period extends WrapTarget implements DaemonRunnable{

        private static final long serialVersionUID = 6105816116626297923L;
        private long interval = 1000l;
        private int count = 60;
        private List valueList = new LinkedList();
        private Daemon periodicGetter;
        private JMXConnectorNotificationListener listener;

        /**
         * ラップした監視対象の値を取得する間隔[ms]を設定する。<p>
         * デフォルトは、1000[ms]。<br>
         *
         * @param millis 値を取得する間隔[ms]
         */
        public void setInterval(long millis){
            interval = millis;
        }

        /**
         * ラップした監視対象の値を取得する間隔[ms]を取得する。<p>
         *
         * @return 値を取得する間隔[ms]
         */
        public long getInterval(){
            return interval;
        }

        /**
         * ラップした監視対象の値を取得する件数を設定する。<p>
         * デフォルトは、60件。<br>
         *
         * @param cnt 値を取得する件数
         */
        public void setCount(int cnt){
            count = cnt;
        }

        /**
         * ラップした監視対象の値を取得する件数を取得する。<p>
         *
         * @return 値を取得する件数
         */
        public int getCount(){
            return count;
        }

        /**
         * ラップした監視対象の値を一定間隔で取得し、その集合を取得する。<p>
         *
         * @param connection JMX接続
         * @return 監視対象の値
         * @exception Exception 監視対象の値の取得に失敗した場合
         */
        public Object getValue(MBeanServerConnection connection) throws Exception{
            List value = null;
            synchronized(valueList){
                value = new ArrayList(valueList);
            }
            if(value != null && contextKey != null){
                watcher.setContextValue(contextKey, value);
            }
            return value;
        }

        /**
         * 開始する。<p>
         * 値の取得を開始する。<br>
         */
        public void start(){
            super.start();
            periodicGetter = new Daemon(this);
            periodicGetter.setName(
                "Nimbus MBeanWatcher periodic value getter " + getWatcherServiceName() + target
            );
            periodicGetter.setDaemon(true);
            if(watcher.connector != null){
                listener = new JMXConnectorNotificationListener();
                watcher.connector.addConnectionNotificationListener(listener, listener, periodicGetter);
            }
            periodicGetter.start();
        }

        /**
         * 終了する。<p>
         * 値の取得を停止する。<br>
         */
        public void stop(){
            if(listener != null && watcher.connector != null){
                try{
                    watcher.connector.removeConnectionNotificationListener(listener);
                }catch(ListenerNotFoundException e){}
            }
            if(periodicGetter != null){
                periodicGetter.stopNoWait();
                periodicGetter = null;
            }
            synchronized(valueList){
                valueList.clear();
            }
            super.stop();
        }

        /**
         * リセットする。<p>
         * ラップしている監視対象及び、取得した値もリセットする。<br>
         */
        public void reset(){
            super.reset();
            synchronized(valueList){
                valueList.clear();
            }
        }
        public boolean onStart(){return true;}
        public boolean onStop(){return true;}
        public boolean onSuspend(){return true;}
        public boolean onResume(){return true;}
        public Object provide(DaemonControl ctrl) throws Throwable{
            ctrl.sleep(interval, false);
            JMXConnector tmpConnector = null;
            try{
                MBeanServerConnection connection = null;
                if(watcher.jndiFinder != null){
                    connection = (MBeanServerConnection)watcher.jndiFinder.lookup(watcher.rmiAdaptorName);

                }else if(watcher.mBeanServerConnectionFactory != null || watcher.serviceURL != null){

                    if(watcher.connector == null){
                        if(watcher.mBeanServerConnectionFactory != null){
                            tmpConnector = watcher.mBeanServerConnectionFactory.getJMXConnector();
                        }else{
                            tmpConnector = JMXConnectorFactory.newJMXConnector(
                                new JMXServiceURL(watcher.serviceURL),
                                watcher.jmxConnectorEnvironment
                            );
                        }
                        tmpConnector.connect();
                        connection = tmpConnector.getMBeanServerConnection();
                    }else{
                        connection = watcher.connector.getMBeanServerConnection();
                    }

                }else{
                    connection = ManagementFactory.getPlatformMBeanServer();

                }
                return target.getValue(connection);
            }catch(Exception e){
                return null;
            }finally{
                if(tmpConnector != null){
                    try{
                        tmpConnector.close();
                    }catch(IOException e){}
                }
            }
        }
        public void consume(Object paramObj, DaemonControl ctrl) throws Throwable{
            synchronized(valueList){
                valueList.add(paramObj);
                if(valueList.size() > count){
                    for(int i = 0, imax = valueList.size() - count; i < imax; i++){
                        valueList.remove(0);
                    }
                }
            }
        }
        public void garbage(){}

        /**
         * この監視対象の文字列表現を取得する。<p>
         *
         * @return 文字列表現
         */
        public String toString(){
            StringBuilder buf = new StringBuilder(super.toString());
            buf.deleteCharAt(buf.length() - 1);
            buf.append(",interval=").append(interval);
            buf.append('}');
            return buf.toString();
        }
    }

    /**
     * 二項演算監視対象。<p>
     * ２つの監視対象をラップして、２つの監視対象の値を使って二項演算処理を行う監視対象基底クラス。<br>
     *
     * @author M.Takata
     */
    public abstract static class BinaryOperation extends WrapTarget{

        private static final long serialVersionUID = 7253736292096568710L;
        protected Target secondTarget;
        protected boolean isNullToZero;

        /**
         * {@link MBeanWatcherService}のサービス名を設定する。<p>
         * 第２項の監視対象にもMBeanWatcherServiceのサービス名を設定する。<br>
         *
         * @param name MBeanWatcherServiceのサービス名
         */
        public void setWatcherServiceName(ServiceName name){
            super.setWatcherServiceName(name);
            secondTarget.setWatcherServiceName(name);
        }

        /**
         * {@link MBeanWatcherService}を設定する。<p>
         * 第２項の監視対象にもMBeanWatcherServiceを設定する。<br>
         *
         * @param watcher MBeanWatcherService
         */
        protected void setWatcherService(MBeanWatcherService watcher){
            super.setWatcherService(watcher);
            secondTarget.setWatcherService(watcher);
        }

        /**
         * {@link Logger}を設定する。<p>
         * 第２項の監視対象にもLoggerを設定する。<br>
         *
         * @param logger Logger
         */
        public void setLogger(Logger logger){
            super.setLogger(logger);
            secondTarget.setLogger(logger);
        }

        /**
         * 二項演算の第二項となる監視対象を設定する。<p>
         *
         * @param target 第二項となる監視対象
         */
        public void setSecondTarget(Target target){
            secondTarget = target;
        }

        /**
         * 二項演算の第二項となる監視対象を取得する。<p>
         *
         * @return 第二項となる監視対象
         */
        public Target getSecondTarget(){
            return secondTarget;
        }

        /**
         * ラップした監視対象の値が数値型でnullの場合に、ゼロとみなすかどうかを設定する。<p>
         * デフォルトは、falseでゼロとみなさない。<br>
         *
         * @param isNullToZero ゼロとみなす場合、true
         */
        public void setNullToZero(boolean isNullToZero){
            this.isNullToZero = isNullToZero;
        }

        /**
         * ラップした監視対象の値が数値型でnullの場合に、ゼロとみなすかどうかを判定する。<p>
         *
         * @return trueの場合、ゼロとみなす
         */
        public boolean isNullToZero(){
            return isNullToZero;
        }

        /**
         * ラップした２つの監視対象の値から二項演算を行った結果を取得する。<p>
         *
         * @param connection JMX接続
         * @return 監視対象の値
         * @exception Exception 監視対象の値の取得に失敗した場合
         */
        public Object getValue(MBeanServerConnection connection) throws Exception{
            BigDecimal first = Target.toBigDecimal(target.getValue(connection), isNullToZero);
            BigDecimal second = Target.toBigDecimal(secondTarget.getValue(connection), isNullToZero);
            if(first == null || second == null){
                return null;
            }
            Object value = calculate(first, second);
            if(value != null && contextKey != null){
                watcher.setContextValue(contextKey, value);
            }
            return value;
        }

        /**
         * 二項演算を行う。<p>
         *
         * @param first 第一項
         * @param second 第二項
         * @return 演算結果
         * @exception Exception 演算に失敗した場合
         */
        protected abstract BigDecimal calculate(BigDecimal first, BigDecimal second) throws Exception;

        /**
         * リセットする。<p>
         * ラップしている監視対象及び、第２項の監視対象もリセットする。<br>
         */
        public void reset(){
            super.reset();
            secondTarget.reset();
        }

        /**
         * この監視対象の文字列表現を取得する。<p>
         *
         * @return 文字列表現
         */
        public String toString(){
            StringBuilder buf = new StringBuilder(super.toString());
            buf.deleteCharAt(buf.length() - 1);
            buf.append(",secondTarget=").append(secondTarget);
            buf.append(",isNullToZero=").append(isNullToZero);
            buf.append('}');
            return buf.toString();
        }
    }

    /**
     * 加算監視対象。<p>
     * ２つの監視対象の値を加算する{@link MBeanWatcherService.BinaryOperation}の実装クラス。<br>
     *
     * @author M.Takata
     */
    public static class AddOperation extends BinaryOperation{

        private static final long serialVersionUID = -5086486388715849637L;

        /**
         * 加算を行う。<p>
         *
         * @param first 第一項
         * @param second 第二項
         * @return 演算結果
         * @exception Exception 演算に失敗した場合
         */
        protected BigDecimal calculate(BigDecimal first, BigDecimal second) throws Exception{
            return first.add(second);
        }
    }

    /**
     * 減算監視対象。<p>
     * ２つの監視対象の値を減算する{@link MBeanWatcherService.BinaryOperation}の実装クラス。<br>
     *
     * @author M.Takata
     */
    public static class SubtractOperation extends BinaryOperation{

        private static final long serialVersionUID = -2343694151274725683L;

        /**
         * 減算を行う。<p>
         *
         * @param first 第一項
         * @param second 第二項
         * @return 演算結果
         * @exception Exception 演算に失敗した場合
         */
        protected BigDecimal calculate(BigDecimal first, BigDecimal second) throws Exception{
            return first.subtract(second);
        }
    }

    /**
     * 乗算監視対象。<p>
     * ２つの監視対象の値を乗算する{@link MBeanWatcherService.BinaryOperation}の実装クラス。<br>
     *
     * @author M.Takata
     */
    public static class MultiplyOperation extends BinaryOperation{

        private static final long serialVersionUID = -5651446587284471517L;

        /**
         * 乗算を行う。<p>
         *
         * @param first 第一項
         * @param second 第二項
         * @return 演算結果
         * @exception Exception 演算に失敗した場合
         */
        protected BigDecimal calculate(BigDecimal first, BigDecimal second) throws Exception{
            return first.multiply(second);
        }
    }

    /**
     * 除算監視対象。<p>
     * ２つの監視対象の値を除算する{@link MBeanWatcherService.BinaryOperation}の実装クラス。<br>
     *
     * @author M.Takata
     */
    public static class DivideOperation extends BinaryOperation{
        private static final long serialVersionUID = -5331257610256153532L;
        private static final BigDecimal ZERO = new BigDecimal(0.0d);
        private int roundingMode = BigDecimal.ROUND_HALF_EVEN;
        private int scale = -1;
        private boolean isReturnNullOnZeroDivide = false;
        private boolean isReturnZeroOnZeroDivide = false;

        /**
         * 除算結果に適用する丸めモードを設定する。<p>
         * デフォルトは、{@link BigDecimal#ROUND_HALF_EVEN}。<br>
         *
         * @param mode 丸めモード
         */
        public void setRoundingMode(int mode){
            this.roundingMode = mode;
        }

        /**
         * 除算結果に適用する丸めモードを取得する。<p>
         *
         * @return 丸めモード
         */
        public int getRoundingMode(){
            return roundingMode;
        }

        /**
         * 除算結果に適用する小数点以下桁数を設定する。<p>
         * 指定しない場合は、第一項の桁数に依存する。<br>
         *
         * @param scale 小数点以下桁数
         */
        public void setScale(int scale){
            this.scale = scale;
        }

        /**
         * 除算結果に適用する小数点以下桁数を取得する。<p>
         *
         * @return 小数点以下桁数
         */
        public int getScale(){
            return scale;
        }

        /**
         * ゼロ除算となる場合に、nullを返すかどうかを設定する。<p>
         * デフォルトは、falseで、ゼロ除算時には例外が発生する。<br>
         *
         * @param isReturnNull nullを返す場合true
         */
        public void setReturnNullOnZeroDivide(boolean isReturnNull){
            this.isReturnNullOnZeroDivide = isReturnNull;
        }

        /**
         * ゼロ除算となる場合に、nullを返すかどうかを判定する。<p>
         *
         * @return trueの場合、nullを返す
         */
        public boolean isReturnNullOnZeroDivide(){
            return isReturnNullOnZeroDivide;
        }

        /**
         * ゼロ除算となる場合に、0を返すかどうかを設定する。<p>
         * デフォルトは、falseで、ゼロ除算時には例外が発生する。<br>
         *
         * @param isReturnZero 0を返す場合true
         */
        public void setReturnZeroOnZeroDivide(boolean isReturnZero){
            this.isReturnZeroOnZeroDivide = isReturnZero;
        }

        /**
         * ゼロ除算となる場合に、0を返すかどうかを判定する。<p>
         *
         * @return trueの場合、0を返す
         */
        public boolean isReturnZeroOnZeroDivide(){
            return isReturnZeroOnZeroDivide;
        }


        /**
         * 除算を行う。<p>
         *
         * @param first 第一項
         * @param second 第二項
         * @return 演算結果
         * @exception Exception 演算に失敗した場合
         */
        protected BigDecimal calculate(BigDecimal first, BigDecimal second) throws Exception{
            if(ZERO.equals(second)){
                if(isReturnZeroOnZeroDivide){
                    return ZERO;
                }else if(isReturnNullOnZeroDivide){
                    return null;
                }
            }
            return scale >= 0 ? first.divide(second, scale, roundingMode) : first.divide(second, roundingMode);
        }
    }

    /**
     * 百分率監視対象。<p>
     * ２つの監視対象の値の百分率を計算する{@link MBeanWatcherService.BinaryOperation}の実装クラス。<br>
     *
     * @author M.Takata
     */
    public static class PercentageOperation extends DivideOperation{

        private static final long serialVersionUID = -828285523729092762L;

        /**
         * 百分率計算を行う。<p>
         *
         * @param first 第一項
         * @param second 第二項
         * @return 演算結果
         * @exception Exception 演算に失敗した場合
         */
        protected BigDecimal calculate(BigDecimal first, BigDecimal second) throws Exception{
            BigDecimal divided = super.calculate(first, second);
            if(divided == null){
                return null;
            }
            return divided.multiply(new BigDecimal(100.0d));
        }
    }

    /**
     * 最大値二項演算監視対象。<p>
     * ２つの監視対象の値の最大値を取得する{@link MBeanWatcherService.BinaryOperation}の実装クラス。<br>
     *
     * @author M.Takata
     */
    public static class MaxOperation extends BinaryOperation{

        private static final long serialVersionUID = 7889957820603303173L;

        /**
         * 最大値を返す。<p>
         *
         * @param first 第一項
         * @param second 第二項
         * @return 演算結果
         * @exception Exception 演算に失敗した場合
         */
        protected BigDecimal calculate(BigDecimal first, BigDecimal second) throws Exception{
            return first.max(second);
        }
    }

    /**
     * 最小値二項演算監視対象。<p>
     * ２つの監視対象の値の最小値を取得する{@link MBeanWatcherService.BinaryOperation}の実装クラス。<br>
     *
     * @author M.Takata
     */
    public static class MinOperation extends BinaryOperation{

        private static final long serialVersionUID = -4042120812950659730L;

        /**
         * 最小値を返す。<p>
         *
         * @param first 第一項
         * @param second 第二項
         * @return 演算結果
         * @exception Exception 演算に失敗した場合
         */
        protected BigDecimal calculate(BigDecimal first, BigDecimal second) throws Exception{
            return first.min(second);
        }
    }

    /**
     * 集合演算監視対象。<p>
     * 監視対象をラップして、監視対象の値に対して集合演算を行う監視対象基底クラス。<br>
     *
     * @author M.Takata
     */
    public static abstract class SetOperation extends EditTarget{

        private static final long serialVersionUID = -7200261696645994257L;

        /**
         * 監視対象の値が集合や配列の場合に、その各要素に対して編集を行うかどうかを判定する。<p>
         *
         * @return 必ずtrueを返す
         */
        public boolean isElementEdit(){
            return true;
        }

        /**
         * ラップした監視対象の値に対して集合演算を行った結果を取得する。<p>
         *
         * @param connection JMX接続
         * @return 監視対象の値
         * @exception Exception 監視対象の値の取得に失敗した場合
         */
        public Object getValue(MBeanServerConnection connection) throws Exception{
            Object value = target.getValue(connection);
            if(value == null){
                value = new BigDecimal(0.0d);
            }else{
                Object[] array = null;
                if(value instanceof Map){
                    array = ((Map)value).values().toArray();
                }else if(value instanceof Collection){
                    array = ((Collection)value).toArray();
                }else if(value.getClass().isArray()){
                    Object[] sourceArray = (Object[])value;
                    array = new Object[sourceArray.length];
                    System.arraycopy(sourceArray, 0, array, 0, sourceArray.length);
                }
                if(array != null){
                    if(array.length == 0){
                        value = new BigDecimal(0.0d);
                    }else{
                        List list = new ArrayList();
                        for(int i = 0; i < array.length; i++){
                            if(array[i] != null && array[i] instanceof Number){
                                BigDecimal target = Target.toBigDecimal(array[i], true);
                                list.add(target);
                            }
                        }
                        if(list.size() == 0){
                            value = new BigDecimal(0.0d);
                        }else{
                            value = edit(list);
                        }
                    }
                }
            }
            if(value != null && contextKey != null){
                watcher.setContextValue(contextKey, value);
            }
            return value;
        }

        /**
         * 指定された値に対して集合演算を行う。<p>
         *
         * @param value 編集対象の値
         * @return 編集結果の値
         * @exception Exception 編集処理に失敗した場合
         */
        protected Object edit(Object value) throws Exception{
            List list = (List)value;
            return calculate((BigDecimal[])list.toArray(new BigDecimal[list.size()]));
        }

        /**
         * 指定された値に対して集合演算を行う。<p>
         *
         * @param numbers 集合演算の対象となる数値集合
         * @return 集合演算の値
         * @exception Exception 集合演算に失敗した場合
         */
        protected abstract BigDecimal calculate(BigDecimal[] numbers) throws Exception;

        /**
         * この監視対象の文字列表現を取得する。<p>
         *
         * @return 文字列表現
         */
        public String toString(){
            StringBuilder buf = new StringBuilder(super.toString());
            buf.deleteCharAt(buf.length() - 1);
            buf.append(",isElementEdit=").append(isElementEdit());
            buf.append('}');
            return buf.toString();
        }
    }

    /**
     * 総和集合演算を行う{@link MBeanWatcherService.SetOperation}の実装クラス。<p>
     *
     * @author M.Takata
     */
    public static class Sum extends SetOperation{

        private static final long serialVersionUID = 2277773295544965565L;

        /**
         * 集合の総和を行う。<p>
         *
         * @param numbers 集合演算の対象となる数値集合
         * @return 集合演算の値
         * @exception Exception 集合演算に失敗した場合
         */
        protected BigDecimal calculate(BigDecimal[] numbers) throws Exception{
            BigDecimal result = new BigDecimal(0.0d);
            for(int i = 0; i < numbers.length; i++){
                result = result.add(numbers[i]);
            }
            return result;
        }
    }

    /**
     * 中央値集合演算を行う{@link MBeanWatcherService.SetOperation}の実装クラス。<p>
     *
     * @author M.Takata
     */
    public static class Median extends SetOperation{

        private static final long serialVersionUID = 4138874599919750663L;

        private int roundingMode = BigDecimal.ROUND_HALF_EVEN;
        private int scale = -1;

        /**
         * 2値の平均を求める際の除算結果に適用する丸めモードを設定する。<p>
         * デフォルトは、{@link BigDecimal#ROUND_HALF_EVEN}。<br>
         *
         * @param mode 丸めモード
         */
        public void setRoundingMode(int mode){
            this.roundingMode = mode;
        }

        /**
         * 2値の平均を求める際の除算結果に適用する丸めモードを取得する。<p>
         *
         * @return 丸めモード
         */
        public int getRoundingMode(){
            return roundingMode;
        }

        /**
         * 除算結果に適用する小数点以下桁数を設定する。<p>
         * 指定しない場合は、第一項の桁数に依存する。<br>
         *
         * @param scale 小数点以下桁数
         */
        public void setScale(int scale){
            this.scale = scale;
        }

        /**
         * 除算結果に適用する小数点以下桁数を取得する。<p>
         *
         * @return 小数点以下桁数
         */
        public int getScale(){
            return scale;
        }

        /**
         * 集合の中央値を計算する。<p>
         *
         * @param numbers 集合演算の対象となる数値集合
         * @return 集合演算の値
         * @exception Exception 集合演算に失敗した場合
         */
        protected BigDecimal calculate(BigDecimal[] numbers) throws Exception{
            Arrays.sort(numbers);
            if((numbers.length + 1) % 2 == 0){
                return numbers[(numbers.length + 1) / 2 - 1];
            }else{
                BigDecimal result = new BigDecimal(0.0d);
                result = result.add(numbers[numbers.length / 2 - 1]);
                result = result.add(numbers[numbers.length / 2]);
                if(scale >= 0){
                    result.divide(new BigDecimal((double)2.0), scale, roundingMode);
                }else{
                    result.divide(new BigDecimal((double)2.0), roundingMode);
                }
                return result;
            }
        }
    }

    /**
     * 平均集合演算を行う{@link MBeanWatcherService.SetOperation}の実装クラス。<p>
     *
     * @author M.Takata
     */
    public static class Average extends Sum{

        private static final long serialVersionUID = -7262525074283633937L;
        protected int roundingMode = BigDecimal.ROUND_HALF_EVEN;
        protected int scale = -1;

        /**
         * 平均を求める際の除算結果に適用する丸めモードを設定する。<p>
         * デフォルトは、{@link BigDecimal#ROUND_HALF_EVEN}。<br>
         *
         * @param mode 丸めモード
         */
        public void setRoundingMode(int mode){
            this.roundingMode = mode;
        }

        /**
         * 平均を求める際の除算結果に適用する丸めモードを取得する。<p>
         *
         * @return 丸めモード
         */
        public int getRoundingMode(){
            return roundingMode;
        }

        /**
         * 除算結果に適用する小数点以下桁数を設定する。<p>
         * 指定しない場合は、第一項の桁数に依存する。<br>
         *
         * @param scale 小数点以下桁数
         */
        public void setScale(int scale){
            this.scale = scale;
        }

        /**
         * 除算結果に適用する小数点以下桁数を取得する。<p>
         *
         * @return 小数点以下桁数
         */
        public int getScale(){
            return scale;
        }

        /**
         * 集合の平均値を取得する。<p>
         *
         * @param numbers 集合演算の対象となる数値集合
         * @return 集合演算の値
         * @exception Exception 集合演算に失敗した場合
         */
        protected BigDecimal calculate(BigDecimal[] numbers) throws Exception{
            if(scale >= 0){
                return super.calculate(numbers).divide(new BigDecimal((double)numbers.length), scale, roundingMode);
            }else{
                return super.calculate(numbers).divide(new BigDecimal((double)numbers.length), roundingMode);
            }
        }

        /**
         * この監視対象の文字列表現を取得する。<p>
         *
         * @return 文字列表現
         */
        public String toString(){
            StringBuilder buf = new StringBuilder(super.toString());
            buf.deleteCharAt(buf.length() - 1);
            buf.append(",roundingMode=").append(roundingMode);
            buf.append('}');
            return buf.toString();
        }
    }

    /**
     * 分散集合演算を行う{@link MBeanWatcherService.SetOperation}の実装クラス。<p>
     *
     * @author M.Takata
     */
    public static class Variance extends Average{

        private static final long serialVersionUID = -6250730592729577578L;

        /**
         * 集合の分散値を取得する。<p>
         *
         * @param numbers 集合演算の対象となる数値集合
         * @return 集合演算の値
         * @exception Exception 集合演算に失敗した場合
         */
        protected BigDecimal calculate(BigDecimal[] numbers) throws Exception{
            BigDecimal average = super.calculate(numbers);
            BigDecimal sum = BigDecimal.ZERO;
            for(int i = 0; i < numbers.length; i++){
/*
                BigDecimal val = numbers[i].subtract(average);
                sum = sum.add(val.multiply(val));
*/

                sum = sum.add(numbers[i].subtract(average).pow(2));

            }
            if(scale >= 0){
                return sum.divide(new BigDecimal(numbers.length), scale, roundingMode);
            }else{
                return sum.divide(new BigDecimal(numbers.length), roundingMode);
            }
        }
    }

    /**
     * 標準偏差集合演算を行う{@link MBeanWatcherService.SetOperation}の実装クラス。<p>
     *
     * @author M.Takata
     */
    public static class StandardDeviation extends Variance{

        private static final long serialVersionUID = 1484688589485075232L;

        /**
         * 集合の標準偏差値を取得する。<p>
         *
         * @param numbers 集合演算の対象となる数値集合
         * @return 集合演算の値
         * @exception Exception 集合演算に失敗した場合
         */
        protected BigDecimal calculate(BigDecimal[] numbers) throws Exception{
            BigDecimal variance = super.calculate(numbers);

/*
            BigDecimal sd = new BigDecimal(Math.sqrt(variance.doubleValue()));
            if(scale >= 0){
                return sd.setScale(scale, roundingMode);
            }else{
                return sd;
            }
*/

            BigDecimal sd = new BigDecimal(Math.sqrt(variance.doubleValue()), MathContext.DECIMAL64);
            if(scale >= 0){
                if(scale < 17){
                    return sd;
                }
                BigDecimal b2 = new BigDecimal(2);
                for(int tempScale = 16; tempScale < scale; tempScale *= 2){
                    if(scale >= 0){
                        sd = sd.subtract(sd.multiply(sd).subtract(variance).divide(sd.multiply(b2), scale, roundingMode));
                    }else{
                        sd = sd.subtract(sd.multiply(sd).subtract(variance).divide(sd.multiply(b2), roundingMode));
                    }
                }
                return sd;
            }else{
                return sd;
            }

        }
    }

    /**
     * 最大集合演算を行う{@link MBeanWatcherService.SetOperation}の実装クラス。<p>
     *
     * @author M.Takata
     */
    public static class Max extends SetOperation{

        private static final long serialVersionUID = 3152818165408079349L;

        /**
         * 集合の最大値を取得する。<p>
         *
         * @param numbers 集合演算の対象となる数値集合
         * @return 集合演算の値
         * @exception Exception 集合演算に失敗した場合
         */
        protected BigDecimal calculate(BigDecimal[] numbers) throws Exception{
            BigDecimal result = null;
            for(int i = 0; i < numbers.length; i++){
                if(result == null){
                    result = numbers[i];
                }else{
                    result = result.max(numbers[i]);
                }
            }
            return result;
        }
    }

    /**
     * 最小集合演算を行う{@link MBeanWatcherService.SetOperation}の実装クラス。<p>
     *
     * @author M.Takata
     */
    public static class Min extends SetOperation{

        private static final long serialVersionUID = -8356445551875453616L;

        /**
         * 集合の最小値を取得する。<p>
         *
         * @param numbers 集合演算の対象となる数値集合
         * @return 集合演算の値
         * @exception Exception 集合演算に失敗した場合
         */
        protected BigDecimal calculate(BigDecimal[] numbers) throws Exception{
            BigDecimal result = null;
            for(int i = 0; i < numbers.length; i++){
                if(result == null){
                    result = numbers[i];
                }else{
                    result = result.min(numbers[i]);
                }
            }
            return result;
        }
    }

    /**
     * ラップした監視対象の値取得処理で例外が発生した場合に返却値を変更する{@link MBeanWatcherService.WrapTarget}の実装クラス。<br>
     *
     * @author M.Ishida
     */
    public static class ExceptionHandle extends WrapTarget {

        private List exceptionConditions = new ArrayList();

        /**
         * 例外発生時に返却値を編集する条件を追加する。
         * <p>
         * 追加された順番に例外のチェックをして、例外が一致すると後続の条件はチェックせずに終了する。<br>
         *
         * @param condition チェック条件
         */
        public void addExceptionCondition(Condition condition) {
            exceptionConditions.add(condition);
        }

        /**
         * チェックする条件のリストを取得する。
         * <p>
         *
         * @return チェック条件のリスト
         */
        public List getExceptionConditionList() {
            return exceptionConditions;
        }

        public Object getValue(MBeanServerConnection connection) throws Exception {
            try{
                Object value = target.getValue(connection);
                if(value != null && target.getContextKey() != null){
                    watcher.setContextValue(target.getContextKey(), value);
                }
                return value;
            } catch(Exception e){
                for(int i = 0; i < exceptionConditions.size(); i++){
                    Condition condition = (Condition)exceptionConditions.get(i);
                    Exception targetException = condition.getTargetException();
                    if(targetException != null && targetException.getClass().isAssignableFrom(e.getClass())){
                        Map out = new LinkedHashMap();
                        Object value = condition.handleException(e, watcher);
                        if(target.getKey() != null){
                            out.put(target.getKey(), value);
                        }
                        if(value != null && target.getContextKey() != null && watcher != null){
                            watcher.setContextValue(target.getContextKey(), value);
                        }
                        return out;
                    }
                }
                throw e;
            }
        }

        /**
         * この監視対象の文字列表現を取得する。
         * <p>
         *
         * @return 文字列表現
         */
        public String toString() {
            StringBuilder buf = new StringBuilder(super.toString());
            buf.deleteCharAt(buf.length() - 1);
            buf.append(",exceptionConditions=").append(exceptionConditions);
            buf.append('}');
            return buf.toString();
        }

        /**
         * 例外条件。<br>
         *
         * 監視対象の値取得時に例外が発生した場合に返却値を編集する条件<br>
         * 対象の例外が発生した場合に、例外自体を返却する、例外のメッセージを返却する、nullを返却する、特定のObjectを返却する、Interpreterを実行するのいずれかを行う。
         *
         * @author M.Ishida
         */
        public static class Condition implements Serializable {

            public static final String CONTEXT = "context";

            private Exception targetException;
            private boolean isReturnExceptionMessage;
            private boolean isReturnException;
            private boolean isReturnNull;
            private Object returnObject;
            private String expression;

            private ServiceName interpreterServiceName;

            /**
             * 発生対象の例外を設定する。<p>
             *
             * @param exception 発生対象の例外
             */
            public void setTargetException(Exception exception) {
                targetException = exception;
            }

            /**
             * 発生対象の例外を取得する。<p>
             *
             * @return 発生対象の例外
             */
            public Exception getTargetException() {
                return targetException;
            }

            /**
             * 対象の例外が発生した場合に例外メッセージを返却するかの設定を行う。<p>
             *
             * @param isReturnExceptionMessage trueの場合、例外メッセージを返却する
             */
            public void setReturnExceptionMessage(boolean isReturnExceptionMessage) {
                this.isReturnExceptionMessage = isReturnExceptionMessage;
            }

            /**
             * 対象の例外が発生した場合に例外自体を返却するかの設定を行う。<p>
             *
             * @param isReturnException trueの場合、例外自体を返却する
             */
            public void setReturnException(boolean isReturnException) {
                this.isReturnException = isReturnException;
            }

            /**
             * 対象の例外が発生した場合にnullを返却するかの設定を行う。<p>
             *
             * @param isReturnNull trueの場合、nullを返却する
             */
            public void setReturnNull(boolean isReturnNull) {
                this.isReturnNull = isReturnNull;
            }

            /**
             * 対象の例外が発生した場合に返却するObjectの設定を行う。<p>
             *
             * @param returnObject 返却するObject
             */
            public void setReturnObject(Object returnObject) {
                this.returnObject = returnObject;
            }

            /**
             * 対象の例外が発生した場合に実行するInterpreter式の設定を行う。<p>
             *
             * @param returnObject 返却するObject
             */

            /**
             * 対象の例外が発生した場合に実行するInterpreter式の設定を行う。<p>
             *
             * @param expression Interpreter式
             */
            public void setExpression(String expression) {
                this.expression = expression;
            }

            /**
             * 対象の例外が発生した場合に実行するInterpreterのサービス名の設定を行う。<p>
             *
             * @param serviceName Interpreterのサービス名
             */
            public void setInterpreterServiceName(ServiceName serviceName) {
                interpreterServiceName = serviceName;
            }

            protected Object handleException(Exception e, MBeanWatcherService watcher) throws Exception {
                if(isReturnException){
                    return e;
                }
                if(isReturnExceptionMessage){
                    return e.getMessage();
                }
                if(isReturnNull){
                    return null;
                }
                if(returnObject != null){
                    return returnObject;
                }
                if(expression != null){
                    jp.ossc.nimbus.service.interpreter.Interpreter itr = null;
                    if(interpreterServiceName != null){
                        itr = (jp.ossc.nimbus.service.interpreter.Interpreter) ServiceManagerFactory.getServiceObject(interpreterServiceName);
                        Map param = new HashMap();
                        param.put(CONTEXT, watcher.getContextMap());
                        return itr.evaluate(expression, param);
                    } else {
                        JexlContext jexlContext = JexlHelper.createContext();
                        jexlContext.getVars().put(CONTEXT, watcher.getContextMap());
                        Expression ex = ExpressionFactory.createExpression(expression);
                        return ex.evaluate(jexlContext);
                    }
                }
                throw e;
            }

            /**
             * この監視対象の文字列表現を取得する。
             * <p>
             *
             * @return 文字列表現
             */
            public String toString() {
                StringBuilder buf = new StringBuilder();
                buf.append("Condition{");
                buf.append(",targetException=").append(targetException.getClass().getName());
                buf.append(",isReturnException=").append(isReturnException);
                buf.append(",isReturnExceptionMessage=").append(isReturnExceptionMessage);
                buf.append(",isReturnNull=").append(isReturnNull);
                buf.append(",returnObject=").append(returnObject);
                buf.append(",interpreterServiceName=").append(interpreterServiceName);
                buf.append(",expression=").append(expression);
                buf.append('}');
                return buf.toString();
            }
        }
    }

    protected static class JMXConnectorNotificationListener implements NotificationListener, NotificationFilter{
        private static final long serialVersionUID = 5262799493133917779L;
        public void handleNotification(Notification notification, Object handback){
            Daemon daemon = (Daemon)handback;
            if(JMXConnectionNotification.OPENED.equals(notification.getType())){
                daemon.resume();
            }else if(JMXConnectionNotification.CLOSED.equals(notification.getType())){
                daemon.suspend();
            }
        }
        public boolean isNotificationEnabled(Notification notification){
            return notification instanceof JMXConnectionNotification;
        }
    }
}
