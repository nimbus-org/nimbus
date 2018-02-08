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

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.Date;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.io.Serializable;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.math.BigDecimal;
import java.math.BigInteger;

import java.math.MathContext;
import java.lang.management.ManagementFactory;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.QueryExp;
import javax.management.MalformedObjectNameException;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.NotificationFilter;
import javax.management.ListenerNotFoundException;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXServiceURL;
import javax.management.remote.JMXConnectionNotification;

import org.apache.commons.jexl.Expression;
import org.apache.commons.jexl.ExpressionFactory;
import org.apache.commons.jexl.JexlContext;
import org.apache.commons.jexl.JexlHelper;

import jp.ossc.nimbus.beans.PropertyAccess;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.daemon.Daemon;
import jp.ossc.nimbus.daemon.DaemonControl;
import jp.ossc.nimbus.daemon.DaemonRunnable;
import jp.ossc.nimbus.service.jndi.JndiFinder;
import jp.ossc.nimbus.service.log.Logger;
import jp.ossc.nimbus.service.writer.Category;
import jp.ossc.nimbus.service.writer.MessageWriteException;
import jp.ossc.nimbus.util.converter.Converter;

/**
 * MBean���Ď�����T�[�r�X�B<p>
 *
 * @author M.Takata
 */
public class MBeanWatcherService extends ServiceBase implements DaemonRunnable, MBeanWatcherServiceMBean{

    private static final long serialVersionUID = -1421073056315791503L;

    protected ServiceName jndiFinderServiceName;
    protected JndiFinder jndiFinder;
    protected String rmiAdaptorName = DEFAULT_JMX_RMI_ADAPTOR_NAME;
    private String serviceURL;
    private Map jmxConnectorEnvironment;
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

    // MBeanWatcherServiceMBean��JavaDoc
    public void setJndiFinderServiceName(ServiceName name){
        jndiFinderServiceName = name;
    }
    // MBeanWatcherServiceMBean��JavaDoc
    public ServiceName getJndiFinderServiceName(){
        return jndiFinderServiceName;
    }

    // MBeanWatcherServiceMBean��JavaDoc
    public void setRMIAdaptorName(String name){
        rmiAdaptorName = name;
    }
    // MBeanWatcherServiceMBean��JavaDoc
    public String getRMIAdaptorName(){
        return rmiAdaptorName;
    }

    // MBeanWatcherServiceMBean��JavaDoc
    public void setServiceURL(String url){
        serviceURL = url;
    }
    // MBeanWatcherServiceMBean��JavaDoc
    public String getServiceURL(){
        return serviceURL;
    }

    // MBeanWatcherServiceMBean��JavaDoc
    public void setJMXConnectorEnvironment(Map env){
        jmxConnectorEnvironment = env;
    }
    // MBeanWatcherServiceMBean��JavaDoc
    public Map getJMXConnectorEnvironment(){
        return jmxConnectorEnvironment;
    }

    // MBeanWatcherServiceMBean��JavaDoc
    public void setInterval(long interval){
        this.interval = interval;
    }
    // MBeanWatcherServiceMBean��JavaDoc
    public long getInterval(){
        return interval;
    }

    // MBeanWatcherServiceMBean��JavaDoc
    public void setCategoryServiceName(ServiceName name){
        categoryServiceName = name;
    }
    // MBeanWatcherServiceMBean��JavaDoc
    public ServiceName getCategoryServiceName(){
        return categoryServiceName;
    }

    // MBeanWatcherServiceMBean��JavaDoc
    public void setConnectOnStart(boolean isConnect){
        isConnectOnStart = isConnect;
    }
    // MBeanWatcherServiceMBean��JavaDoc
    public boolean isConnectOnStart(){
        return isConnectOnStart;
    }

    // MBeanWatcherServiceMBean��JavaDoc
    public void setGetValueErrorMessageId(String id){
        getValueErrorMessageId = id;
    }
    // MBeanWatcherServiceMBean��JavaDoc
    public String getGetValueErrorMessageId(){
        return getValueErrorMessageId;
    }

    // MBeanWatcherServiceMBean��JavaDoc
    public void setConnectErrorMessageId(String id){
        connectErrorMessageId = id;
    }
    // MBeanWatcherServiceMBean��JavaDoc
    public String getConnectErrorMessageId(){
        return connectErrorMessageId;
    }

    // MBeanWatcherServiceMBean��JavaDoc
    public void setWriteErrorMessageId(String id){
        writeErrorMessageId = id;
    }
    // MBeanWatcherServiceMBean��JavaDoc
    public String getWriteErrorMessageId(){
        return writeErrorMessageId;
    }

    // MBeanWatcherServiceMBean��JavaDoc
    public void setMBeanSet(boolean isSet){
        isMBeanSet = isSet;
    }

    // MBeanWatcherServiceMBean��JavaDoc
    public boolean isMBeanSet(){
        return isMBeanSet;
    }

    // MBeanWatcherServiceMBean��JavaDoc
    public void setObjectName(String name) throws MalformedObjectNameException{
        objectName = new ObjectName(name);
    }

    // MBeanWatcherServiceMBean��JavaDoc
    public String getObjectName(){
        return objectName == null ? null : objectName.toString();
    }

    // MBeanWatcherServiceMBean��JavaDoc
    public void setQueryExp(QueryExp exp){
        queryExp = exp;
    }

    // MBeanWatcherServiceMBean��JavaDoc
    public QueryExp getQueryExp(){
        return queryExp;
    }

    // MBeanWatcherServiceMBean��JavaDoc
    public boolean isResetOnStart(){
        return isResetOnStart;
    }

    // MBeanWatcherServiceMBean��JavaDoc
    public void setResetOnStart(boolean isResetOnStart){
        this.isResetOnStart = isResetOnStart;
    }

    public void addTarget(Target target){
        if(targetList == null){
            targetList = new ArrayList();
        }
        targetList.add(target);
    }

    // MBeanWatcherServiceMBean��JavaDoc
    public List getTargetList(){
        return targetList;
    }

    // MBeanWatcherServiceMBean��JavaDoc
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
        if(jndiFinderServiceName != null){
            jndiFinder = (JndiFinder)ServiceManagerFactory.getServiceObject(jndiFinderServiceName);
        }else if(serviceURL != null){
            if(isConnectOnStart){
                connector = JMXConnectorFactory.newJMXConnector(
                    new JMXServiceURL(serviceURL),
                    jmxConnectorEnvironment
                );
                connector.connect();
            }
/*
        }else{
            throw new IllegalArgumentException("ServiceURL or jndiFinderServiceName must be specified.");
*/
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
/*
                }else{
*/

                }else if(serviceURL != null){

                    if(connector == null){
                        tmpConnector = JMXConnectorFactory.newJMXConnector(
                            new JMXServiceURL(serviceURL),
                            jmxConnectorEnvironment
                        );
                        tmpConnector.connect();
                        connection = tmpConnector.getMBeanServerConnection();
                    }else{
                        connection = connector.getMBeanServerConnection();
                    }

                }else{
                    connection = ManagementFactory.getPlatformMBeanServer();

                }
                isConnectError = false;
            }catch(Exception e){
                if(!isConnectError && connectErrorMessageId != null){
                    getLogger().write(
                        connectErrorMessageId,
/*
                        new Object[]{getServiceNameObject(), rmiAdaptorName != null ? rmiAdaptorName : serviceURL},
*/

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
     * �Ď��ΏہB<p>
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

        /**
         * {@link MBeanWatcherService}�̃T�[�r�X����ݒ肷��B<p>
         *
         * @param name MBeanWatcherService�̃T�[�r�X��
         */
        protected void setWatcherServiceName(ServiceName name){
            this.watcherServiceName = name;
        }

        /**
         * {@link MBeanWatcherService}�̃T�[�r�X�����擾����B<p>
         *
         * @return MBeanWatcherService�̃T�[�r�X��
         */
        protected ServiceName getWatcherServiceName(){
            return watcherServiceName;
        }

        /**
         * {@link MBeanWatcherService}��ݒ肷��B<p>
         *
         * @param watcher MBeanWatcherService
         */
        protected void setWatcherService(MBeanWatcherService watcher){
            this.watcher = watcher;
        }

        /**
         * {@link MBeanWatcherService}���擾����B<p>
         *
         * @return MBeanWatcherService
         */
        protected MBeanWatcherService getWatcherService(){
            return watcher;
        }

        /**
         * {@link Logger}��ݒ肷��B<p>
         *
         * @param logger Logger
         */
        protected void setLogger(Logger logger){
            this.logger = logger;
        }

        /**
         * {@link Logger}���擾����B<p>
         *
         * @return Logger
         */
        protected Logger getLogger(){
            return logger == null ? ServiceManagerFactory.getLogger() : logger;
        }

        /**
         * �o�͂���ۂ̃L�[���擾����B<p>
         *
         * @return �L�[
         */
        public String getKey(){
            return key;
        }

        /**
         * �o�͂���ۂ̃L�[��ݒ肷��B<p>
         *
         * @param key �L�[
         */
        public void setKey(String key){
            this.key = key;
        }

        /**
         * �R���e�L�X�g�ɏo�͂���ۂ̃L�[���擾����B<p>
         *
         * @return �L�[
         */
        public String getContextKey(){
            return contextKey;
        }

        /**
         * �R���e�L�X�g�ɏo�͂���ۂ̃L�[��ݒ肷��B<p>
         *
         * @param key �L�[
         */
        public void setContextKey(String key){
            contextKey = key;
        }

        /**
         * �Ď��Ώۂ̒l���擾����B<p>
         *
         * @param connection JMX�ڑ�
         * @return �Ď��Ώۂ̒l
         * @exception Exception �Ď��Ώۂ̒l�̎擾�Ɏ��s�����ꍇ
         */
        public abstract Object getValue(MBeanServerConnection connection) throws Exception;

        /**
         * �l��BigDecimal�ɕϊ�����B<p>
         *
         * @param value �l
         * @param isNullToZero �l��null�������ꍇ�Ƀ[���Ƃ݂Ȃ��ꍇ�́Atrue�Bfalse�̏ꍇ�́A�l��null�̏ꍇ�Anull��Ԃ��B
         * @return �ϊ����ꂽBigDecimal�l
         * @exception NumberFormatException �ϊ��Ɏ��s�����ꍇ
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
         * ���Z�b�g����B<p>
         */
        public void reset(){
        }

        /**
         * �J�n����B<p>
         */
        public void start(){
        }

        /**
         * �I������B<p>
         */
        public void stop(){
        }

        /**
         * ���̊Ď��Ώۂ̕�����\�����擾����B<p>
         *
         * @return ������\��
         */
        public String toString(){
            StringBuffer buf = new StringBuffer(super.toString());
            buf.append('{');
            buf.append("key=").append(getKey());
            buf.append('}');
            return buf.toString();
        }
    }

    /**
     * �^�C���X�^���v�B<p>
     * �Ď����̃^�C���X�^���v���擾����B<br>
     *
     * @author M.Takata
     */
    public static class Timestamp extends Target{

        private static final long serialVersionUID = 3632167440398869434L;

        /**
         * �L�[�̃f�t�H���g�l�B<p>
         */
        public static final String DEFAULT_KEY = "Timestamp";

        private String format;

        /**
         * �^�C���X�^���v�𕶎���ɕҏW����ꍇ�̃t�H�[�}�b�g���擾����B<p>
         *
         * @return �t�H�[�}�b�g
         */
        public String getFormat(){
            return format;
        }

        /**
         * �^�C���X�^���v�𕶎���ɕҏW����ꍇ�̃t�H�[�}�b�g��ݒ肷��B<p>
         * �f�t�H���g�͂Ȃ��ŁA�^�C���X�^���v�Ƃ���java.util.Date�I�u�W�F�N�g��Ԃ��B<br>
         *
         * @param format �t�H�[�}�b�g
         */
        public void setFormat(String format){
            this.format = format;
        }

        /**
         * �o�͂���ۂ̃L�[���擾����B<p>
         * {@link #setKey(String)}�Őݒ肵�Ă��Ȃ��ꍇ�́A{@link #DEFAULT_KEY}��Ԃ��B<br>
         *
         * @return �L�[
         */
        public String getKey(){
            return super.getKey() != null ? super.getKey() : DEFAULT_KEY;
        }

        /**
         * �^�C���X�^���v���擾����B<p>
         * {@link #setFormat(String)}�Ńt�H�[�}�b�g���w�肵�Ă��Ȃ��ꍇ�́A�Ăяo�����_�ł�java.util.Date�I�u�W�F�N�g��Ԃ��B<br>
         * {@link #setFormat(String)}�Ńt�H�[�}�b�g���w�肵�Ă���ꍇ�́A�Ăяo�����_�ł�java.util.Date�I�u�W�F�N�g���t�H�[�}�b�g���ĕ������Ԃ��B<br>
         *
         * @param connection JMX�ڑ�
         * @return �^�C���X�^���v
         * @exception Exception �t�H�[�}�b�g�Ɏ��s�����ꍇ
         */
        public Object getValue(MBeanServerConnection connection) throws Exception{
            Object value = format == null ? (Object)new Date() : (Object)new SimpleDateFormat(format).format(new Date());
            if(value != null && contextKey != null){
                watcher.setContextValue(contextKey, value);
            }
            return value;
        }

        /**
         * ���̊Ď��Ώۂ̕�����\�����擾����B<p>
         *
         * @return ������\��
         */
        public String toString(){
            StringBuffer buf = new StringBuffer(super.toString());
            buf.deleteCharAt(buf.length() - 1);
            buf.append(",format=").append(format);
            buf.append('}');
            return buf.toString();
        }
    }

    /**
     * �R���e�L�X�g�ΏہB<p>
     * �R���e�L�X�g����l���擾����B<br>
     *
     * @author M.Takata
     */
    public static class Context extends Target{

        private static final long serialVersionUID = 566760345569101974L;

        /**
         * �R���e�L�X�g����l���擾����B<p>
         *
         * @param connection JMX�ڑ�
         * @return �R���e�L�X�g����擾�����l
         * @exception Exception �t�H�[�}�b�g�Ɏ��s�����ꍇ
         */
        public Object getValue(MBeanServerConnection connection) throws Exception{
            return watcher.getContextValue(contextKey);
        }

        /**
         * ���̊Ď��Ώۂ̕�����\�����擾����B<p>
         *
         * @return ������\��
         */
        public String toString(){
            StringBuffer buf = new StringBuffer(super.toString());
            buf.deleteCharAt(buf.length() - 1);
            buf.append(",contextKey=").append(contextKey);
            buf.append('}');
            return buf.toString();
        }
    }

    /**
     * MBeanWatcher�ΏہB<p>
     * MBeanWatcher����l���擾����B<br>
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
         * MBeanWatcher����l���擾����B<p>
         *
         * @param connection JMX�ڑ�
         * @return MBeanWatcher����擾�����l
         * @exception Exception �t�H�[�}�b�g�Ɏ��s�����ꍇ
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
         * ���̊Ď��Ώۂ̕�����\�����擾����B<p>
         *
         * @return ������\��
         */
        public String toString(){
            StringBuffer buf = new StringBuffer(super.toString());
            buf.deleteCharAt(buf.length() - 1);
            buf.append(",mBeanWatcherService=").append(mBeanWatcherService != null ? mBeanWatcherService : mBeanWatcherServiceName);
            buf.append('}');
            return buf.toString();
        }
    }

    /**
     * Managed Bean�Ď��ΏہB<p>
     * Managed Bean���Ď��ΏۂƂ���Ď��Ώۊ��N���X�B<br>
     *
     * @author M.Takata
     */
    public static abstract class MBeanTarget extends Target{
        private static final long serialVersionUID = -5180685937237509600L;
        protected boolean isMBeanSet;
        protected ObjectName objectName;
        protected QueryExp queryExp;

        /**
         * �Ď��Ώۂ�Managed Bean���W���Ƃ��Ĉ������ǂ�����ݒ肷��B<p>
         * �f�t�H���g�́Afalse�ŁA�Ď��Ώۂ́A��ӂ�Managed Bean�B<br>
         *
         * @param isSet �Ď��Ώۂ�Managed Bean���W���Ƃ��Ĉ����ꍇtrue
         */
        public void setMBeanSet(boolean isSet){
            isMBeanSet = isSet;
        }

        /**
         * �Ď��Ώۂ�Managed Bean���W���Ƃ��Ĉ������ǂ����𔻒肷��B<p>
         *
         * @return true�̏ꍇ�A�Ď��Ώۂ�Managed Bean���W���Ƃ��Ĉ���
         */
        public boolean isMBeanSet(){
            return isMBeanSet;
        }

        /**
         * �Ď��Ώۂ�Managed Bean�̖��O��ݒ肷��B<p>
         * {@link #setMBeanSet(boolean) setMBeanSet(false)}�Ɛݒ肵�Ă���ꍇ�́AManaged Bean����ӂɓ��肷�銮�S�����w�肷��B<br>
         * {@link #setMBeanSet(boolean) setMBeanSet(true)}�Ɛݒ肵�Ă���ꍇ�́AManaged Bean�̏W������肷��I�u�W�F�N�g�����w�肷��B<br>
         *
         * @param name Managed Bean�̖��O��JMX�̃I�u�W�F�N�g���`���Ŏw�肷��
         * @exception MalformedObjectNameException �I�u�W�F�N�g�����s���ȏꍇ
         */
        public void setObjectName(String name) throws MalformedObjectNameException{
            this.objectName = new ObjectName(name);
        }

        /**
         * �Ď��Ώۂ�Managed Bean�̖��O���擾����B<p>
         *
         * @return Managed Bean�̖��O��JMX�̃I�u�W�F�N�g���`���Ŏw�肷��
         */
        public String getObjectName(){
            return objectName == null ? null : objectName.toString();
        }

        /**
         * �Ď��Ώۂ�Managed Bean���i�荞�ޏ�������ݒ肷��B<p>
         * {@link #setMBeanSet(boolean) setMBeanSet(true)}�̏ꍇ�̂ݗL���B<br>
         *
         * @param exp ������
         */
        public void setQueryExp(QueryExp exp){
            queryExp = exp;
        }

        /**
         * �Ď��Ώۂ�Managed Bean���i�荞�ޏ��������擾����B<p>
         *
         * @return ������
         */
        public QueryExp getQueryExp(){
            return queryExp;
        }

        /**
         * �Ď��Ώۂ�Managed Bean�����Ď��Ώۂ̒l���擾����B<p>
         * �Ď��Ώۂ�Managed Bean���W���̏ꍇ�A�L�[���I�u�W�F�N�g���A�l���Ď��Ώۂ̒l�ƂȂ�}�b�v��Ԃ��B<br>
         *
         * @param connection JMX�ڑ�
         * @return �Ď��Ώۂ̒l
         * @exception Exception �Ď��Ώۂ̒l�̎擾�Ɏ��s�����ꍇ
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
         * �w�肳�ꂽ�I�u�W�F�N�g����Managed Bean�����Ď��Ώۂ̒l���擾����B<p>
         *
         * @param connection JMX�ڑ�
         * @param objectName �I�u�W�F�N�g��
         * @return �Ď��Ώۂ̒l
         * @exception Exception �Ď��Ώۂ̒l�̎擾�Ɏ��s�����ꍇ
         */
        protected abstract Object getValue(MBeanServerConnection connection, ObjectName objectName) throws Exception;

        /**
         * ���̊Ď��Ώۂ̕�����\�����擾����B<p>
         *
         * @return ������\��
         */
        public String toString(){
            StringBuffer buf = new StringBuffer(super.toString());
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
     * Managed Bean�̑������Ď��ΏۂƂ���{@link MBeanWatcherService.MBeanTarget}�̎����N���X�B<p>
     *
     * @author M.Takata
     */
    public static class Attribute extends MBeanTarget{

        private static final long serialVersionUID = 253836219685470254L;
        private String name;

        /**
         * �Ď��ΏۂƂ���Managed Bean�̑�������ݒ肷��B<p>
         *
         * @param name ������
         */
        public void setName(String name){
            this.name = name;
        }

        /**
         * �Ď��ΏۂƂ���Managed Bean�̑��������擾����B<p>
         *
         * @return ������
         */
        public String getName(){
            return name;
        }

        /**
         * �o�͂���ۂ̃L�[���擾����B<p>
         * {@link #setKey(String)}�Őݒ肵�Ă��Ȃ��ꍇ�́A�I�u�W�F�N�g��#��������Ԃ��B<br>
         *
         * @return �L�[
         */
        public String getKey(){
            return super.getKey() != null ? super.getKey() : getObjectName() + '#' + getName();
        }

        /**
         * �w�肳�ꂽ�I�u�W�F�N�g����Managed Bean�����Ď��Ώۂ̑����l���擾����B<p>
         *
         * @param connection JMX�ڑ�
         * @param objectName �I�u�W�F�N�g��
         * @return �Ď��Ώۂ̒l
         * @exception Exception �Ď��Ώۂ̒l�̎擾�Ɏ��s�����ꍇ
         */
        public Object getValue(MBeanServerConnection connection, ObjectName objectName) throws Exception{
            return connection.getAttribute(objectName, name);
        }

        /**
         * ���̊Ď��Ώۂ̕�����\�����擾����B<p>
         *
         * @return ������\��
         */
        public String toString(){
            StringBuffer buf = new StringBuffer(super.toString());
            buf.deleteCharAt(buf.length() - 1);
            buf.append(",name=").append(name);
            buf.append('}');
            return buf.toString();
        }
    }

    /**
     * Managed Bean�̕����̑������Ď��ΏۂƂ���{@link MBeanWatcherService.MBeanTarget}�̎����N���X�B<p>
     *
     * @author M.Takata
     */
    public static class Attributes extends MBeanTarget{

        private static final long serialVersionUID = -8344987725214428096L;
        private String[] names;

        /**
         * �Ď��ΏۂƂ���Managed Bean�̑������̔z���ݒ肷��B<p>
         *
         * @param names �������̔z��
         */
        public void setNames(String[] names){
            this.names = names;
        }

        /**
         * �Ď��ΏۂƂ���Managed Bean�̑������̔z����擾����B<p>
         *
         * @return �������̔z��
         */
        public String[] getNames(){
            return names;
        }

        /**
         * �w�肳�ꂽ�I�u�W�F�N�g����Managed Bean�����A�Ď��Ώۂ̑������Ƃ��̒l��Map���擾����B<p>
         *
         * @param connection JMX�ڑ�
         * @param objectName �I�u�W�F�N�g��
         * @return �Ď��Ώۂ̑������Ƃ��̒l��Map
         * @exception Exception �Ď��Ώۂ̒l�̎擾�Ɏ��s�����ꍇ
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
         * ���̊Ď��Ώۂ̕�����\�����擾����B<p>
         *
         * @return ������\��
         */
        public String toString(){
            StringBuffer buf = new StringBuffer(super.toString());
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
     * Managed Bean�̃I�y���[�V�������Ď��ΏۂƂ���{@link MBeanWatcherService.MBeanTarget}�̎����N���X�B<p>
     *
     * @author M.Takata
     */
    public static class Operation extends MBeanTarget{
        private static final long serialVersionUID = 8874947184358756744L;
        private String name;
        private List params;
        private String[] signiture;

        /**
         * �Ď��ΏۂƂ���Managed Bean�̃I�y���[�V��������ݒ肷��B<p>
         *
         * @param name �I�y���[�V������
         */
        public void setName(String name){
            this.name = name;
        }

        /**
         * �Ď��ΏۂƂ���Managed Bean�̃I�y���[�V���������擾����B<p>
         *
         * @return �I�y���[�V������
         */
        public String getName(){
            return name;
        }

        /**
         * �Ď��ΏۂƂ���Managed Bean�̃I�y���[�V�����̃V�O�j�`����ݒ肷��B<p>
         *
         * @param sgn �I�y���[�V�����̃V�O�j�`��
         */
        public void setSigniture(String[] sgn){
            signiture = sgn;
        }

        /**
         * �Ď��ΏۂƂ���Managed Bean�̃I�y���[�V�����̃V�O�j�`�����擾����B<p>
         *
         * @return �I�y���[�V�����̃V�O�j�`��
         */
        public String[] getSigniture(){
            return signiture;
        }

        /**
         * �Ď��ΏۂƂ���Managed Bean�̃I�y���[�V�����̈�����ݒ肷��B<p>
         *
         * @param params �I�y���[�V�����̈����̔z��
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
         * �Ď��ΏۂƂ���Managed Bean�̃I�y���[�V�����̈�����ǉ�����B<p>
         *
         * @param param �I�y���[�V�����̈���
         */
        public void addParameter(Object param){
            if(params == null){
                params = new ArrayList();
            }
            params.add(param);
        }

        /**
         * �Ď��ΏۂƂ���Managed Bean�̃I�y���[�V�����̈������X�g���擾����B<p>
         *
         * @return �I�y���[�V�����̈������X�g
         */
        public List getParameterList(){
            return params;
        }

        /**
         * �o�͂���ۂ̃L�[���擾����B<p>
         * {@link #setKey(String)}�Őݒ肵�Ă��Ȃ��ꍇ�́A�I�u�W�F�N�g��#�I�y���[�V������([�p�����[�^1,�p�����[�^2,....])��Ԃ��B<br>
         *
         * @return �L�[
         */
        public String getKey(){
            return super.getKey() != null ? super.getKey() : getObjectName() + '#' + getName() + '(' + (params == null ? "" : params.toString()) + ')';
        }

        /**
         * �w�肳�ꂽ�I�u�W�F�N�g����Managed Bean�����Ď��Ώۂ̃I�y���[�V�����̖߂�l���擾����B<p>
         *
         * @param connection JMX�ڑ�
         * @param objectName �I�u�W�F�N�g��
         * @return �Ď��Ώۂ̒l
         * @exception Exception �Ď��Ώۂ̒l�̎擾�Ɏ��s�����ꍇ
         */
        public Object getValue(MBeanServerConnection connection, ObjectName objectName) throws Exception{
            return connection.invoke(objectName, name, params == null ? null : params.toArray(), signiture);
        }

        /**
         * ���̊Ď��Ώۂ̕�����\�����擾����B<p>
         *
         * @return ������\��
         */
        public String toString(){
            StringBuffer buf = new StringBuffer(super.toString());
            buf.deleteCharAt(buf.length() - 1);
            buf.append(",name=").append(name);
            buf.append(",params=").append(params);
            buf.append('}');
            return buf.toString();
        }
    }

    /**
     * ���b�v�Ď��ΏہB<p>
     * �Ď��Ώۂ����b�v���ē���̏�����t������Ď��Ώۊ��N���X�B<br>
     *
     * @author M.Takata
     */
    public abstract static class WrapTarget extends Target{

        private static final long serialVersionUID = 6038448327332359108L;
        protected Target target;

        /**
         * {@link MBeanWatcherService}�̃T�[�r�X����ݒ肷��B<p>
         * ���b�v���Ă���Ď��Ώۂɂ�MBeanWatcherService�̃T�[�r�X����ݒ肷��B<br>
         *
         * @param name MBeanWatcherService�̃T�[�r�X��
         */
        public void setWatcherServiceName(ServiceName name){
            super.setWatcherServiceName(name);
            target.setWatcherServiceName(name);
        }

        /**
         * {@link MBeanWatcherService}��ݒ肷��B<p>
         * ���b�v���Ă���Ď��Ώۂɂ�MBeanWatcherService��ݒ肷��B<br>
         *
         * @param watcher MBeanWatcherService
         */
        protected void setWatcherService(MBeanWatcherService watcher){
            super.setWatcherService(watcher);
            target.setWatcherService(watcher);
        }

        /**
         * {@link Logger}��ݒ肷��B<p>
         * ���b�v���Ă���Ď��Ώۂɂ�Logger��ݒ肷��B<br>
         *
         * @param logger Logger
         */
        public void setLogger(Logger logger){
            super.setLogger(logger);
            target.setLogger(logger);
        }

        /**
         * ���b�v����Ď��Ώ�{@link Logger}��ݒ肷��B<p>
         *
         * @param target �Ď��Ώ�
         */
        public void setTarget(Target target){
            this.target = target;
        }

        /**
         * ���b�v����Ď��Ώ�{@link Logger}���擾����B<p>
         *
         * @return �Ď��Ώ�
         */
        public Target getTarget(){
            return target;
        }

        /**
         * ���Z�b�g����B<p>
         * ���b�v���Ă���Ď��Ώۂ����Z�b�g����B<br>
         */
        public void reset(){
            super.reset();
            target.reset();
        }

        /**
         * �J�n����B<p>
         * ���b�v���Ă���Ď��Ώۂ��J�n����B<br>
         */
        public void start(){
            super.start();
            target.start();
        }

        /**
         * �I������B<p>
         * ���b�v���Ă���Ď��Ώۂ��I������B<br>
         */
        public void stop(){
            target.stop();
            super.stop();
        }

        /**
         * ���̊Ď��Ώۂ̕�����\�����擾����B<p>
         *
         * @return ������\��
         */
        public String toString(){
            StringBuffer buf = new StringBuffer(super.toString());
            buf.deleteCharAt(buf.length() - 1);
            buf.append(",target=").append(target);
            buf.append('}');
            return buf.toString();
        }
    }

    /**
     * ���b�v�����Ď��Ώۂ̒l���`�F�b�N����{@link MBeanWatcherService.WrapTarget}�̎����N���X�B<br>
     *
     * @author M.Takata
     */
    public static class Check extends WrapTarget{

        private static final long serialVersionUID = -8821714143031466189L;
        private boolean isNullToZero;
        private ServiceName loggerServiceName;
        private List checkConditions = new ArrayList();

        /**
         * �Ď��Ώۂ����l�^��null�̏ꍇ�ɁA�[���Ƃ݂Ȃ����ǂ�����ݒ肷��B<p>
         * �f�t�H���g�́Afalse�Ń[���Ƃ݂Ȃ��Ȃ��B<br>
         *
         * @param isNullToZero �[���Ƃ݂Ȃ��ꍇ�Atrue
         */
        public void setNullToZero(boolean isNullToZero){
            this.isNullToZero = isNullToZero;
        }

        /**
         * �Ď��Ώۂ����l�^��null�̏ꍇ�ɁA�[���Ƃ݂Ȃ����ǂ����𔻒肷��B<p>
         *
         * @return true�̏ꍇ�A�[���Ƃ݂Ȃ�
         */
        public boolean isNullToZero(){
            return isNullToZero;
        }

        /**
         * �`�F�b�N�G���[�̃��O���o�͂���{@link Logger}�T�[�r�X�̃T�[�r�X����ݒ肷��B<p>
         * �w�肵�Ȃ��ꍇ�́A���̃T�[�r�X��{@link ServiceBase#getLogger()}�Ŏ擾�����Logger�Ń��O�o�͂��s���B<br>
         *
         * @param name Logger�T�[�r�X�̃T�[�r�X��
         */
        public void setLoggerServiceName(ServiceName name){
            loggerServiceName = name;
        }

        /**
         * �`�F�b�N�G���[�̃��O���o�͂���{@link Logger}�T�[�r�X�̃T�[�r�X�����擾����B<p>
         *
         * @return Logger�T�[�r�X�̃T�[�r�X��
         */
        public ServiceName getLoggerServiceName(){
            return loggerServiceName;
        }

        /**
         * {@link Logger}���擾����B<p>
         * {@link #setLoggerServiceName(ServiceName)}���ݒ肳��Ă���ꍇ�́A����Logger��Ԃ��B<br>
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
         * �`�F�b�N���������ǉ�����B<p>
         * �ǉ����ꂽ���ԂɃ`�F�b�N�����āA�`�F�b�N�G���[�ɂȂ�ƌ㑱�̏����̓`�F�b�N�����Ƀ��Z�b�g����B<br>
         *
         * @param condition �`�F�b�N����
         */
        public void addCheckCondition(Condition condition){
            checkConditions.add(condition);
        }

        /**
         * �`�F�b�N��������̃��X�g���擾����B<p>
         *
         * @return �`�F�b�N�����̃��X�g
         */
        public List getCheckConditionList(){
            return checkConditions;
        }

        /**
         * ���b�v�����Ď��Ώۂ̃I�y���[�V�����̖߂�l���擾���A�`�F�b�N���s���ă`�F�b�N�G���[�̏ꍇ�̓��O���o�͂���B<p>
         * �`�F�b�N���������ǉ����ꂽ���ԂɃ`�F�b�N���āA�`�F�b�N�G���[�ɂȂ�ƌ㑱�̏����̓`�F�b�N�����Ƀ��Z�b�g����B<br>
         *
         * @param connection JMX�ڑ�
         * @return �Ď��Ώۂ̒l
         * @exception Exception �Ď��Ώۂ̒l�̎擾�Ɏ��s�����ꍇ
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
         * ���Z�b�g����B<p>
         * ���b�v���Ă���Ď��Ώۋy�сA�Ď��Ώۂ̃`�F�b�N�G���[��Ԃ����Z�b�g����B<br>
         */
        public void reset(){
            super.reset();
            for(int i = 0, imax = checkConditions.size(); i < imax; i++){
                Condition condition = (Condition)checkConditions.get(i);
                condition.reset();
            }
        }

        /**
         * ���̊Ď��Ώۂ̕�����\�����擾����B<p>
         *
         * @return ������\��
         */
        public String toString(){
            StringBuffer buf = new StringBuffer(super.toString());
            buf.deleteCharAt(buf.length() - 1);
            buf.append("checkConditions=").append(checkConditions);
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
         * �`�F�b�N�����B<br>
         *
         * @author M.Takata
         */
        public static class Condition implements Serializable{

            private static final long serialVersionUID = 451166286622455937L;

            public static final String CONTEXT = "context";
            public static final String VALUE = "value";

            private Expression checkExpression;
            private String expression;
            private Map idMap = new TreeMap(new ErrorCountComparator());
            private int errorCount;
            private List checkTimes;
            private List checkTerms;
            private boolean isOnceOutputLog;
            private ServiceName interpreterServiceName;
            private jp.ossc.nimbus.service.interpreter.Interpreter interpreter;
            private MBeanWatcherService watcher;

            /**
             * {@link MBeanWatcherService}��ݒ肷��B<p>
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
             * �Ď��Ώۂ��`�F�b�N�����������ݒ肷��B<p>
             * �������́A{@link jp.ossc.nimbus.service.interpreter.Interpreter Interpreter}���w�肵�Ă��Ȃ��ꍇ�́AThe Apache Jakarta Project�� Commons Jexl(http://jakarta.apache.org/commons/jexl/)�ŕ]������B<br>
             * �Ď��Ώےl��\��"value"�L�[���[�h���܂߁A�Ď��Ώےl��]������boolean��Ԃ����Ƃ���B<br>
             * �������̕]����true�ɂȂ�ƁA�`�F�b�N�G���[�Ƃ݂Ȃ����B<br>
             *
             * @param expression ������
             */
            public void setCheckExpression(String expression){
                this.expression = expression;
            }

            /**
             * �Ď��Ώۂ��`�F�b�N������������擾����B<p>
             *
             * @return ������
             */
            public String getCheckExpression(){
                return expression;
            }

            /**
             * �`�F�b�N�G���[���ɏo�͂��郍�O�̃��b�Z�[�WID��ݒ肷��B<p>
             * {@link #setLogMessageIdByErrorCount(int, String) setLogMessageIdByErrorCount(1, id)}�ŌĂяo���̂Ɠ����B<br>
             *
             * @param id ���O�̃��b�Z�[�WID
             */
            public void setLogMessageId(String id){
                setLogMessageIdByErrorCount(1, id);
            }

            /**
             * �`�F�b�N�G���[���w�肵���񐔘A�����Ĕ����������ɏo�͂��郍�O�̃��b�Z�[�WID��ݒ肷��B<p>
             * �`�F�b�N�G���[�̉񐔂́A�`�F�b�N�G���[���A�����Ĕ������Ȃ������ꍇ�ƁA�w�肳�ꂽ�`�F�b�N�G���[�񐔂̍ő�̒l�̉񐔂܂ő�����ƃ��Z�b�g�����B<br>
             * ���O�o�͂̍ۂɂ́A�T�[�r�X���A�L�[�A�������A�Ď��Ώۂ̒l�A�A�����Ĕ��������`�F�b�N�G���[�̉񐔂����O�o�͂̈����Ƃ��ēn���B<br>
             * �ėp�̃��O���b�Z�[�WID�Ƃ��āA{@link #MSG_ID_CHECK_WARN}�A{@link #MSG_ID_CHECK_ERROR}�A{@link #MSG_ID_CHECK_FATAL}��p�ӂ��Ă���B<br>
             *
             * @param errorCount �`�F�b�N�G���[��
             * @param id ���O�̃��b�Z�[�WID
             */
            public void setLogMessageIdByErrorCount(int errorCount, String id){
                idMap.put(new Integer(errorCount), id);
            }

            /**
             * �`�F�b�N�����s���鎞����ݒ肷��B<p>
             *
             * @param times HH:mm:ss�܂��́AHH:mm:ss.SSS�̎���������z��
             * @exception ParseException �w�肳�ꂽ����������̃p�[�X�Ɏ��s�����ꍇ
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
             * �`�F�b�N�����s���鎞�����擾����B<p>
             *
             * @return HH:mm:ss�܂��́AHH:mm:ss.SSS�̎���������z��
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
             * �`�F�b�N�����s������Ԃ�ݒ肷��B<p>
             *
             * @param terms HH:mm:ss�܂��́AHH:mm:ss.SSS�̎����������-�ŘA�������J�n����-�I�������̕�����z��B�I�������͊��ԂɊ܂܂�Ȃ��B
             * @exception ParseException �w�肳�ꂽ����������̃p�[�X�Ɏ��s�����ꍇ
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
             * �`�F�b�N�����s������Ԃ��擾����B<p>
             *
             * @return HH:mm:ss�܂��́AHH:mm:ss.SSS�̎����������-�ŘA�������J�n����-�I�������̕�����z��B�I�������͊��ԂɊ܂܂�Ȃ��B
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
             * �`�F�b�N�G���[�����������ꍇ�ɍŏ��̈��̂݃��O�o�͂��邩��ݒ肷��B<p>
             *
             * @param isOnce �`�F�b�N�G���[�����������ꍇ�ɍŏ��̈��̂݃��O�o�͂��邩
             */
            public void setOnceOutputLog(boolean isOnce) {
                isOnceOutputLog = isOnce;
            }

            /**
             * �`�F�b�N�G���[�����������ꍇ�ɍŏ��̈��̂݃��O�o�͂��邩���擾����B<p>
             *
             * @return �`�F�b�N�G���[�����������ꍇ�ɍŏ��̈��̂݃��O�o�͂��邩
             */
            public boolean isOnceOutputLog() {
                return isOnceOutputLog;
            }

            /**
             * �w�肳�ꂽ�l�̃`�F�b�N���s���ă`�F�b�N�G���[����������ƃ��O���o�͂���B<p>
             *
             * @param value �`�F�b�N�Ώۂ̒l
             * @param logger ���O�o�͂��s��Logger
             * @param watcherServiceName �Ď��T�[�r�X�̃T�[�r�X��
             * @param key �Ď��Ώۂ̃L�[
             * @return �`�F�b�N���ʁB�`�F�b�N�G���[�̏ꍇ��false
             * @exception Exception �`�F�b�N�����Ɏ��s�����ꍇ
             */
            protected boolean check(Object value, Logger logger, ServiceName watcherServiceName, String key) throws Exception{
                if(errorCount == -1){
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
                return true;
            }

            /**
             * �Ď��Ώۂ̃`�F�b�N�G���[�񐔂����Z�b�g����B<br>
             */
            protected void reset(){
                errorCount = 0;
            }

            /**
             * ���̊Ď��Ώۂ̕�����\�����擾����B<p>
             *
             * @return ������\��
             */
            public String toString(){
                StringBuffer buf = new StringBuffer();
                buf.append("Condition{");
                buf.append(",expression=").append(expression);
                buf.append(",idMap=").append(idMap);
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
                    StringBuffer buf = new StringBuffer();
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
                    StringBuffer buf = new StringBuffer();
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
     * �ҏW�Ď��ΏہB<p>
     * �Ď��Ώۂ����b�v���āA�Ď��Ώۂ̒l�ɕҏW�������s���Ď��Ώۊ��N���X�B<br>
     *
     * @author M.Takata
     */
    public abstract static class EditTarget extends WrapTarget{

        private static final long serialVersionUID = -3292286495172239503L;

        protected boolean isElementEdit;

        /**
         * �Ď��Ώۂ̒l���W����z��̏ꍇ�ɁA���̊e�v�f�ɑ΂��ĕҏW���s�����ǂ�����ݒ肷��B<p>
         * �f�t�H���g�́Afalse�ŗv�f�ɑ΂���ҏW�͍s��Ȃ��B<br>
         *
         * @param isElement �e�v�f�ɑ΂��ĕҏW���s���ꍇtrue
         */
        public void setElementEdit(boolean isElement){
            isElementEdit = isElement;
        }

        /**
         * �Ď��Ώۂ̒l���W����z��̏ꍇ�ɁA���̊e�v�f�ɑ΂��ĕҏW���s�����ǂ����𔻒肷��B<p>
         *
         * @return true�̏ꍇ�A�e�v�f�ɑ΂��ĕҏW���s��
         */
        public boolean isElementEdit(){
            return isElementEdit;
        }

        /**
         * ���b�v�����Ď��Ώۂ̒l��ҏW���Ď擾����B<p>
         *
         * @param connection JMX�ڑ�
         * @return �Ď��Ώۂ̒l
         * @exception Exception �Ď��Ώۂ̒l�̎擾�Ɏ��s�����ꍇ
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
         * �w�肳�ꂽ�l��ҏW����B<p>
         *
         * @param value �ҏW�Ώۂ̒l
         * @return �ҏW���ʂ̒l
         * @exception Exception �ҏW�����Ɏ��s�����ꍇ
         */
        protected abstract Object edit(Object value) throws Exception;

        /**
         * ���̊Ď��Ώۂ̕�����\�����擾����B<p>
         *
         * @return ������\��
         */
        public String toString(){
            StringBuffer buf = new StringBuffer(super.toString());
            buf.deleteCharAt(buf.length() - 1);
            buf.append(",isElementEdit=").append(isElementEdit);
            buf.append('}');
            return buf.toString();
        }
    }

    /**
     * ���b�v�����Ď��Ώۂ̒l��{@link jp.ossc.nimbus.service.interpreter.Interpreter Interpreter}�ŕҏW����{@link MBeanWatcherService.EditTarget}�̎����N���X�B<p>
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
         * �ҏW����ݒ肷��B<p>
         * �Ď��Ώےl��\��"value"�L�[���[�h���܂߁A�Ď��Ώےl��ҏW���ĕԂ����Ƃ���B<br>
         *
         * @param expression �ҏW��
         */
        public void setExpression(String expression){
            this.expression = expression;
        }

        /**
         * �w�肳�ꂽ�l��ҏW����B<p>
         *
         * @param value �ҏW�Ώۂ̒l
         * @return �ҏW���ʂ̒l
         * @exception Exception �ҏW�����Ɏ��s�����ꍇ
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
         * ���̊Ď��Ώۂ̕�����\�����擾����B<p>
         *
         * @return ������\��
         */
        public String toString(){
            StringBuffer buf = new StringBuffer(super.toString());
            buf.deleteCharAt(buf.length() - 1);
            buf.append(",expression=").append(expression);
            buf.append('}');
            return buf.toString();
        }
    }

    /**
     * ���b�v�����Ď��Ώۂ̒l�̃v���p�e�B���擾����{@link MBeanWatcherService.EditTarget}�̎����N���X�B<p>
     *
     * @author M.Takata
     */
    public static class Property extends EditTarget{

        private static final long serialVersionUID = 5990303499820230369L;
        private jp.ossc.nimbus.beans.Property property;
        private boolean isIgnoreNullProperty;

        /**
         * ���b�v�����Ď��Ώۂ̒l����擾�������v���p�e�B��ݒ肷��B<p>
         *
         * @param property �v���p�e�B
         */
        public void setProperty(jp.ossc.nimbus.beans.Property property){
            property.setIgnoreNullProperty(isIgnoreNullProperty);
            this.property = property;
        }

        /**
         * ���b�v�����Ď��Ώۂ̒l����擾�������v���p�e�B���擾����B<p>
         *
         * @return �v���p�e�B
         */
        public jp.ossc.nimbus.beans.Property getProperty(){
            return property;
        }

        /**
         * null�Q�Ƃ̃v���p�e�B���擾�g�p�Ƃ����ꍇ�ɁA��O��throw���邩�ǂ�����ݒ肷��B<p>
         * �f�t�H���g�́Afalse�B<br>
         *
         * @param isIgnore null�Q�Ƃ̎��ɗ�O��throw���Ȃ��ꍇ��true
         */
        public void setIgnoreNullProperty(boolean isIgnore){
            isIgnoreNullProperty = isIgnore;
            if(property != null){
                property.setIgnoreNullProperty(isIgnoreNullProperty);
            }
        }

        /**
         * null�Q�Ƃ̃v���p�e�B���擾�g�p�Ƃ����ꍇ�ɁA��O��throw���邩�ǂ����𔻒肷��B<p>
         *
         * @return true�̏ꍇ�Anull�Q�Ƃ̎��ɗ�O��throw���Ȃ�
         */
        public boolean isIgnoreNullProperty(){
            return isIgnoreNullProperty;
        }

        /**
         * �w�肳�ꂽ�l����ݒ肳�ꂽ�v���p�e�B���擾����B<p>
         *
         * @param value �ҏW�Ώۂ̒l
         * @return �ҏW���ʂ̒l
         * @exception Exception �ҏW�����Ɏ��s�����ꍇ
         */
        protected Object edit(Object value) throws Exception{
            return property.getProperty(value);
        }

        /**
         * ���̊Ď��Ώۂ̕�����\�����擾����B<p>
         *
         * @return ������\��
         */
        public String toString(){
            StringBuffer buf = new StringBuffer(super.toString());
            buf.deleteCharAt(buf.length() - 1);
            buf.append(",property=").append(property);
            buf.append(",isIgnoreNullProperty=").append(isIgnoreNullProperty);
            buf.append('}');
            return buf.toString();
        }
    }

    /**
     * ���b�v�����Ď��Ώۂ̒l��{@link Converter}�ŕϊ�����{@link MBeanWatcherService.EditTarget}�̎����N���X�B<p>
     *
     * @author M.Takata
     */
    public static class Convert extends EditTarget{

        private static final long serialVersionUID = 1737702373890781789L;
        private ServiceName converterServiceName;
        private Converter converter;

        /**
         * ���b�v�����Ď��Ώۂ̒l��ϊ�����{@link Converter}��ݒ肷��B<p>
         *
         * @param converter Converter
         */
        public void setConverter(Converter converter){
            this.converter = converter;
        }

        /**
         * ���b�v�����Ď��Ώۂ̒l��ϊ�����{@link Converter}���擾����B<p>
         *
         * @return Converter
         */
        public Converter getConverter(){
            return converter;
        }

        /**
         * ���b�v�����Ď��Ώۂ̒l��ϊ�����{@link Converter}�T�[�r�X�̃T�[�r�X����ݒ肷��B<p>
         *
         * @param name Converter�T�[�r�X�̃T�[�r�X��
         */
        public void setConverterServiceName(ServiceName name){
            converterServiceName = name;
        }

        /**
         * ���b�v�����Ď��Ώۂ̒l��ϊ�����{@link Converter}�T�[�r�X�̃T�[�r�X�����擾����B<p>
         *
         * @return Converter�T�[�r�X�̃T�[�r�X��
         */
        public ServiceName getConverterServiceName(){
            return converterServiceName;
        }

        /**
         * �w�肳�ꂽ�l��ݒ肳�ꂽ{@link Converter}�ŕϊ�����B<p>
         *
         * @param value �ҏW�Ώۂ̒l
         * @return �ҏW���ʂ̒l
         * @exception Exception �ҏW�����Ɏ��s�����ꍇ
         */
        protected Object edit(Object value) throws Exception{
            Converter conv = converter;
            if(converterServiceName != null){
                conv = (Converter)ServiceManagerFactory.getServiceObject(converterServiceName);
            }
            return conv.convert(value);
        }

        /**
         * ���̊Ď��Ώۂ̕�����\�����擾����B<p>
         *
         * @return ������\��
         */
        public String toString(){
            StringBuffer buf = new StringBuffer(super.toString());
            buf.deleteCharAt(buf.length() - 1);
            buf.append(",converter=").append(converter);
            buf.append(",converterServiceName=").append(converterServiceName);
            buf.append('}');
            return buf.toString();
        }
    }

    /**
     * ���b�v�����Ď��Ώۂ̒l�ƑO��̒l�̕ω����擾����{@link MBeanWatcherService.WrapTarget}�̎����N���X�B<p>
     *
     * @author M.Takata
     */
    public static class Change extends WrapTarget{

        private static final long serialVersionUID = 315703698395044669L;
        private boolean isNullToZero;
        private Object lastValue;

        /**
         * ���b�v�����Ď��Ώۂ̒l�����l�^��null�̏ꍇ�ɁA�[���Ƃ݂Ȃ����ǂ�����ݒ肷��B<p>
         * �f�t�H���g�́Afalse�Ń[���Ƃ݂Ȃ��Ȃ��B<br>
         *
         * @param isNullToZero �[���Ƃ݂Ȃ��ꍇ�Atrue
         */
        public void setNullToZero(boolean isNullToZero){
            this.isNullToZero = isNullToZero;
        }

        /**
         * ���b�v�����Ď��Ώۂ̒l�����l�^��null�̏ꍇ�ɁA�[���Ƃ݂Ȃ����ǂ����𔻒肷��B<p>
         *
         * @return true�̏ꍇ�A�[���Ƃ݂Ȃ�
         */
        public boolean isNullToZero(){
            return isNullToZero;
        }

        /**
         * ���b�v�����Ď��Ώۂ̒l����O��̒l���������O�񍷂��擾����B<p>
         *
         * @param connection JMX�ڑ�
         * @return �Ď��Ώۂ̒l
         * @exception Exception �Ď��Ώۂ̒l�̎擾�Ɏ��s�����ꍇ
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
         * ���Z�b�g����B<p>
         * ���b�v���Ă���Ď��Ώۋy�сA�O��̒l�����Z�b�g����B<br>
         */
        public void reset(){
            super.reset();
            lastValue = null;
        }

        /**
         * ���̊Ď��Ώۂ̕�����\�����擾����B<p>
         *
         * @return ������\��
         */
        public String toString(){
            StringBuffer buf = new StringBuffer(super.toString());
            buf.deleteCharAt(buf.length() - 1);
            buf.append(",isNullToZero=").append(isNullToZero);
            buf.append('}');
            return buf.toString();
        }
    }
    /**
     * ���b�v�����Ď��Ώۂ̏W���ƑO��̏W���̕ω����擾����{@link MBeanWatcherService.WrapTarget}�̎����N���X�B<p>
     *
     * @author M.Takata
     */
    public static class ChangeSet extends WrapTarget{

        private static final long serialVersionUID = -7480156060861954682L;
        private Collection lastValue;
        private boolean isChangeAdd = true;

        /**
         * ����/�����̂ǂ�����擾���邩�ݒ肷��B<p>
         * �f�t�H���g�́Atrue�ő������擾����B<br>
         *
         * @param isAdd true�̏ꍇ�́A�����Bfalse�̏ꍇ�́A�������擾����B
         */
        public void setChangeAdd(boolean isAdd){
            isChangeAdd = isAdd;
        }

        /**
         * ���b�v�����Ď��Ώۂ̒l����O��̒l���������O�񍷂��擾����B<p>
         *
         * @param connection JMX�ڑ�
         * @return �Ď��Ώۂ̒l
         * @exception Exception �Ď��Ώۂ̒l�̎擾�Ɏ��s�����ꍇ
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
         * ���Z�b�g����B<p>
         * ���b�v���Ă���Ď��Ώۋy�сA�O��̒l�����Z�b�g����B<br>
         */
        public void reset(){
            super.reset();
            lastValue = null;
        }

        /**
         * ���̊Ď��Ώۂ̕�����\�����擾����B<p>
         *
         * @return ������\��
         */
        public String toString(){
            StringBuffer buf = new StringBuffer(super.toString());
            buf.deleteCharAt(buf.length() - 1);
            buf.append(",isChangeAdd=").append(isChangeAdd);
            buf.append('}');
            return buf.toString();
        }
    }

    /**
     * ���b�v�����Ď��Ώۂ̒l�����Ԋu�Ŏ擾���A���̏W�����擾����{@link MBeanWatcherService.WrapTarget}�̎����N���X�B<p>
     *
     * @author M.Takata
     */
    public static class Period extends WrapTarget implements DaemonRunnable{

        private static final long serialVersionUID = 6105816116626297923L;
        private long interval = 1000l;
        private int count = 60;
        private List valueList = new ArrayList();
        private Daemon periodicGetter;
        private JMXConnectorNotificationListener listener;

        /**
         * ���b�v�����Ď��Ώۂ̒l���擾����Ԋu[ms]��ݒ肷��B<p>
         * �f�t�H���g�́A1000[ms]�B<br>
         *
         * @param millis �l���擾����Ԋu[ms]
         */
        public void setInterval(long millis){
            interval = millis;
        }

        /**
         * ���b�v�����Ď��Ώۂ̒l���擾����Ԋu[ms]���擾����B<p>
         *
         * @return �l���擾����Ԋu[ms]
         */
        public long getInterval(){
            return interval;
        }

        /**
         * ���b�v�����Ď��Ώۂ̒l���擾���錏����ݒ肷��B<p>
         * �f�t�H���g�́A60���B<br>
         *
         * @param cnt �l���擾���錏��
         */
        public void setCount(int cnt){
            count = cnt;
        }

        /**
         * ���b�v�����Ď��Ώۂ̒l���擾���錏�����擾����B<p>
         *
         * @return �l���擾���錏��
         */
        public int getCount(){
            return count;
        }

        /**
         * ���b�v�����Ď��Ώۂ̒l�����Ԋu�Ŏ擾���A���̏W�����擾����B<p>
         *
         * @param connection JMX�ڑ�
         * @return �Ď��Ώۂ̒l
         * @exception Exception �Ď��Ώۂ̒l�̎擾�Ɏ��s�����ꍇ
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
         * �J�n����B<p>
         * �l�̎擾���J�n����B<br>
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
         * �I������B<p>
         * �l�̎擾���~����B<br>
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
         * ���Z�b�g����B<p>
         * ���b�v���Ă���Ď��Ώۋy�сA�擾�����l�����Z�b�g����B<br>
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
/*
                }else{
*/

                }else if(watcher.serviceURL != null){

                    if(watcher.connector == null){
                        tmpConnector = JMXConnectorFactory.newJMXConnector(
                            new JMXServiceURL(watcher.serviceURL),
                            watcher.jmxConnectorEnvironment
                        );
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
                    for(int i = 0, imax = count - valueList.size(); i < imax; i++){
                        valueList.remove(0);
                    }
                }
            }
        }
        public void garbage(){}

        /**
         * ���̊Ď��Ώۂ̕�����\�����擾����B<p>
         *
         * @return ������\��
         */
        public String toString(){
            StringBuffer buf = new StringBuffer(super.toString());
            buf.deleteCharAt(buf.length() - 1);
            buf.append(",interval=").append(interval);
            buf.append('}');
            return buf.toString();
        }
    }

    /**
     * �񍀉��Z�Ď��ΏہB<p>
     * �Q�̊Ď��Ώۂ����b�v���āA�Q�̊Ď��Ώۂ̒l���g���ē񍀉��Z�������s���Ď��Ώۊ��N���X�B<br>
     *
     * @author M.Takata
     */
    public abstract static class BinaryOperation extends WrapTarget{

        private static final long serialVersionUID = 7253736292096568710L;
        protected Target secondTarget;
        protected boolean isNullToZero;

        /**
         * {@link MBeanWatcherService}�̃T�[�r�X����ݒ肷��B<p>
         * ��Q���̊Ď��Ώۂɂ�MBeanWatcherService�̃T�[�r�X����ݒ肷��B<br>
         *
         * @param name MBeanWatcherService�̃T�[�r�X��
         */
        public void setWatcherServiceName(ServiceName name){
            super.setWatcherServiceName(name);
            secondTarget.setWatcherServiceName(name);
        }

        /**
         * {@link MBeanWatcherService}��ݒ肷��B<p>
         * ��Q���̊Ď��Ώۂɂ�MBeanWatcherService��ݒ肷��B<br>
         *
         * @param watcher MBeanWatcherService
         */
        protected void setWatcherService(MBeanWatcherService watcher){
            super.setWatcherService(watcher);
            secondTarget.setWatcherService(watcher);
        }

        /**
         * {@link Logger}��ݒ肷��B<p>
         * ��Q���̊Ď��Ώۂɂ�Logger��ݒ肷��B<br>
         *
         * @param logger Logger
         */
        public void setLogger(Logger logger){
            super.setLogger(logger);
            secondTarget.setLogger(logger);
        }

        /**
         * �񍀉��Z�̑�񍀂ƂȂ�Ď��Ώۂ�ݒ肷��B<p>
         *
         * @param target ��񍀂ƂȂ�Ď��Ώ�
         */
        public void setSecondTarget(Target target){
            secondTarget = target;
        }

        /**
         * �񍀉��Z�̑�񍀂ƂȂ�Ď��Ώۂ��擾����B<p>
         *
         * @return ��񍀂ƂȂ�Ď��Ώ�
         */
        public Target getSecondTarget(){
            return secondTarget;
        }

        /**
         * ���b�v�����Ď��Ώۂ̒l�����l�^��null�̏ꍇ�ɁA�[���Ƃ݂Ȃ����ǂ�����ݒ肷��B<p>
         * �f�t�H���g�́Afalse�Ń[���Ƃ݂Ȃ��Ȃ��B<br>
         *
         * @param isNullToZero �[���Ƃ݂Ȃ��ꍇ�Atrue
         */
        public void setNullToZero(boolean isNullToZero){
            this.isNullToZero = isNullToZero;
        }

        /**
         * ���b�v�����Ď��Ώۂ̒l�����l�^��null�̏ꍇ�ɁA�[���Ƃ݂Ȃ����ǂ����𔻒肷��B<p>
         *
         * @return true�̏ꍇ�A�[���Ƃ݂Ȃ�
         */
        public boolean isNullToZero(){
            return isNullToZero;
        }

        /**
         * ���b�v�����Q�̊Ď��Ώۂ̒l����񍀉��Z���s�������ʂ��擾����B<p>
         *
         * @param connection JMX�ڑ�
         * @return �Ď��Ώۂ̒l
         * @exception Exception �Ď��Ώۂ̒l�̎擾�Ɏ��s�����ꍇ
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
         * �񍀉��Z���s���B<p>
         *
         * @param first ��ꍀ
         * @param second ���
         * @return ���Z����
         * @exception Exception ���Z�Ɏ��s�����ꍇ
         */
        protected abstract BigDecimal calculate(BigDecimal first, BigDecimal second) throws Exception;

        /**
         * ���Z�b�g����B<p>
         * ���b�v���Ă���Ď��Ώۋy�сA��Q���̊Ď��Ώۂ����Z�b�g����B<br>
         */
        public void reset(){
            super.reset();
            secondTarget.reset();
        }

        /**
         * ���̊Ď��Ώۂ̕�����\�����擾����B<p>
         *
         * @return ������\��
         */
        public String toString(){
            StringBuffer buf = new StringBuffer(super.toString());
            buf.deleteCharAt(buf.length() - 1);
            buf.append(",secondTarget=").append(secondTarget);
            buf.append(",isNullToZero=").append(isNullToZero);
            buf.append('}');
            return buf.toString();
        }
    }

    /**
     * ���Z�Ď��ΏہB<p>
     * �Q�̊Ď��Ώۂ̒l�����Z����{@link MBeanWatcherService.BinaryOperation}�̎����N���X�B<br>
     *
     * @author M.Takata
     */
    public static class AddOperation extends BinaryOperation{

        private static final long serialVersionUID = -5086486388715849637L;

        /**
         * ���Z���s���B<p>
         *
         * @param first ��ꍀ
         * @param second ���
         * @return ���Z����
         * @exception Exception ���Z�Ɏ��s�����ꍇ
         */
        protected BigDecimal calculate(BigDecimal first, BigDecimal second) throws Exception{
            return first.add(second);
        }
    }

    /**
     * ���Z�Ď��ΏہB<p>
     * �Q�̊Ď��Ώۂ̒l�����Z����{@link MBeanWatcherService.BinaryOperation}�̎����N���X�B<br>
     *
     * @author M.Takata
     */
    public static class SubtractOperation extends BinaryOperation{

        private static final long serialVersionUID = -2343694151274725683L;

        /**
         * ���Z���s���B<p>
         *
         * @param first ��ꍀ
         * @param second ���
         * @return ���Z����
         * @exception Exception ���Z�Ɏ��s�����ꍇ
         */
        protected BigDecimal calculate(BigDecimal first, BigDecimal second) throws Exception{
            return first.subtract(second);
        }
    }

    /**
     * ��Z�Ď��ΏہB<p>
     * �Q�̊Ď��Ώۂ̒l����Z����{@link MBeanWatcherService.BinaryOperation}�̎����N���X�B<br>
     *
     * @author M.Takata
     */
    public static class MultiplyOperation extends BinaryOperation{

        private static final long serialVersionUID = -5651446587284471517L;

        /**
         * ��Z���s���B<p>
         *
         * @param first ��ꍀ
         * @param second ���
         * @return ���Z����
         * @exception Exception ���Z�Ɏ��s�����ꍇ
         */
        protected BigDecimal calculate(BigDecimal first, BigDecimal second) throws Exception{
            return first.multiply(second);
        }
    }

    /**
     * ���Z�Ď��ΏہB<p>
     * �Q�̊Ď��Ώۂ̒l�����Z����{@link MBeanWatcherService.BinaryOperation}�̎����N���X�B<br>
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
         * ���Z���ʂɓK�p����ۂ߃��[�h��ݒ肷��B<p>
         * �f�t�H���g�́A{@link BigDecimal#ROUND_HALF_EVEN}�B<br>
         *
         * @param mode �ۂ߃��[�h
         */
        public void setRoundingMode(int mode){
            this.roundingMode = mode;
        }

        /**
         * ���Z���ʂɓK�p����ۂ߃��[�h���擾����B<p>
         *
         * @return �ۂ߃��[�h
         */
        public int getRoundingMode(){
            return roundingMode;
        }

        /**
         * ���Z���ʂɓK�p���鏬���_�ȉ�������ݒ肷��B<p>
         * �w�肵�Ȃ��ꍇ�́A��ꍀ�̌����Ɉˑ�����B<br>
         *
         * @param scale �����_�ȉ�����
         */
        public void setScale(int scale){
            this.scale = scale;
        }

        /**
         * ���Z���ʂɓK�p���鏬���_�ȉ��������擾����B<p>
         *
         * @return �����_�ȉ�����
         */
        public int getScale(){
            return scale;
        }

        /**
         * �[�����Z�ƂȂ�ꍇ�ɁAnull��Ԃ����ǂ�����ݒ肷��B<p>
         * �f�t�H���g�́Afalse�ŁA�[�����Z���ɂ͗�O����������B<br>
         *
         * @param isReturnNull null��Ԃ��ꍇtrue
         */
        public void setReturnNullOnZeroDivide(boolean isReturnNull){
            this.isReturnNullOnZeroDivide = isReturnNull;
        }

        /**
         * �[�����Z�ƂȂ�ꍇ�ɁAnull��Ԃ����ǂ����𔻒肷��B<p>
         *
         * @return true�̏ꍇ�Anull��Ԃ�
         */
        public boolean isReturnNullOnZeroDivide(){
            return isReturnNullOnZeroDivide;
        }

        /**
         * �[�����Z�ƂȂ�ꍇ�ɁA0��Ԃ����ǂ�����ݒ肷��B<p>
         * �f�t�H���g�́Afalse�ŁA�[�����Z���ɂ͗�O����������B<br>
         *
         * @param isReturnZero 0��Ԃ��ꍇtrue
         */
        public void setReturnZeroOnZeroDivide(boolean isReturnZero){
            this.isReturnZeroOnZeroDivide = isReturnZero;
        }

        /**
         * �[�����Z�ƂȂ�ꍇ�ɁA0��Ԃ����ǂ����𔻒肷��B<p>
         *
         * @return true�̏ꍇ�A0��Ԃ�
         */
        public boolean isReturnZeroOnZeroDivide(){
            return isReturnZeroOnZeroDivide;
        }


        /**
         * ���Z���s���B<p>
         *
         * @param first ��ꍀ
         * @param second ���
         * @return ���Z����
         * @exception Exception ���Z�Ɏ��s�����ꍇ
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
     * �S�����Ď��ΏہB<p>
     * �Q�̊Ď��Ώۂ̒l�̕S�������v�Z����{@link MBeanWatcherService.BinaryOperation}�̎����N���X�B<br>
     *
     * @author M.Takata
     */
    public static class PercentageOperation extends DivideOperation{

        private static final long serialVersionUID = -828285523729092762L;

        /**
         * �S�����v�Z���s���B<p>
         *
         * @param first ��ꍀ
         * @param second ���
         * @return ���Z����
         * @exception Exception ���Z�Ɏ��s�����ꍇ
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
     * �ő�l�񍀉��Z�Ď��ΏہB<p>
     * �Q�̊Ď��Ώۂ̒l�̍ő�l���擾����{@link MBeanWatcherService.BinaryOperation}�̎����N���X�B<br>
     *
     * @author M.Takata
     */
    public static class MaxOperation extends BinaryOperation{

        private static final long serialVersionUID = 7889957820603303173L;

        /**
         * �ő�l��Ԃ��B<p>
         *
         * @param first ��ꍀ
         * @param second ���
         * @return ���Z����
         * @exception Exception ���Z�Ɏ��s�����ꍇ
         */
        protected BigDecimal calculate(BigDecimal first, BigDecimal second) throws Exception{
            return first.max(second);
        }
    }

    /**
     * �ŏ��l�񍀉��Z�Ď��ΏہB<p>
     * �Q�̊Ď��Ώۂ̒l�̍ŏ��l���擾����{@link MBeanWatcherService.BinaryOperation}�̎����N���X�B<br>
     *
     * @author M.Takata
     */
    public static class MinOperation extends BinaryOperation{

        private static final long serialVersionUID = -4042120812950659730L;

        /**
         * �ŏ��l��Ԃ��B<p>
         *
         * @param first ��ꍀ
         * @param second ���
         * @return ���Z����
         * @exception Exception ���Z�Ɏ��s�����ꍇ
         */
        protected BigDecimal calculate(BigDecimal first, BigDecimal second) throws Exception{
            return first.min(second);
        }
    }

    /**
     * �W�����Z�Ď��ΏہB<p>
     * �Ď��Ώۂ����b�v���āA�Ď��Ώۂ̒l�ɑ΂��ďW�����Z���s���Ď��Ώۊ��N���X�B<br>
     *
     * @author M.Takata
     */
    public static abstract class SetOperation extends EditTarget{

        private static final long serialVersionUID = -7200261696645994257L;

        /**
         * �Ď��Ώۂ̒l���W����z��̏ꍇ�ɁA���̊e�v�f�ɑ΂��ĕҏW���s�����ǂ����𔻒肷��B<p>
         *
         * @return �K��true��Ԃ�
         */
        public boolean isElementEdit(){
            return true;
        }

        /**
         * ���b�v�����Ď��Ώۂ̒l�ɑ΂��ďW�����Z���s�������ʂ��擾����B<p>
         *
         * @param connection JMX�ڑ�
         * @return �Ď��Ώۂ̒l
         * @exception Exception �Ď��Ώۂ̒l�̎擾�Ɏ��s�����ꍇ
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
         * �w�肳�ꂽ�l�ɑ΂��ďW�����Z���s���B<p>
         *
         * @param value �ҏW�Ώۂ̒l
         * @return �ҏW���ʂ̒l
         * @exception Exception �ҏW�����Ɏ��s�����ꍇ
         */
        protected Object edit(Object value) throws Exception{
            List list = (List)value;
            return calculate((BigDecimal[])list.toArray(new BigDecimal[list.size()]));
        }

        /**
         * �w�肳�ꂽ�l�ɑ΂��ďW�����Z���s���B<p>
         *
         * @param numbers �W�����Z�̑ΏۂƂȂ鐔�l�W��
         * @return �W�����Z�̒l
         * @exception Exception �W�����Z�Ɏ��s�����ꍇ
         */
        protected abstract BigDecimal calculate(BigDecimal[] numbers) throws Exception;

        /**
         * ���̊Ď��Ώۂ̕�����\�����擾����B<p>
         *
         * @return ������\��
         */
        public String toString(){
            StringBuffer buf = new StringBuffer(super.toString());
            buf.deleteCharAt(buf.length() - 1);
            buf.append(",isElementEdit=").append(isElementEdit());
            buf.append('}');
            return buf.toString();
        }
    }

    /**
     * ���a�W�����Z���s��{@link MBeanWatcherService.SetOperation}�̎����N���X�B<p>
     *
     * @author M.Takata
     */
    public static class Sum extends SetOperation{

        private static final long serialVersionUID = 2277773295544965565L;

        /**
         * �W���̑��a���s���B<p>
         *
         * @param numbers �W�����Z�̑ΏۂƂȂ鐔�l�W��
         * @return �W�����Z�̒l
         * @exception Exception �W�����Z�Ɏ��s�����ꍇ
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
     * �����l�W�����Z���s��{@link MBeanWatcherService.SetOperation}�̎����N���X�B<p>
     *
     * @author M.Takata
     */
    public static class Median extends SetOperation{

        private static final long serialVersionUID = 4138874599919750663L;

        private int roundingMode = BigDecimal.ROUND_HALF_EVEN;
        private int scale = -1;

        /**
         * 2�l�̕��ς����߂�ۂ̏��Z���ʂɓK�p����ۂ߃��[�h��ݒ肷��B<p>
         * �f�t�H���g�́A{@link BigDecimal#ROUND_HALF_EVEN}�B<br>
         *
         * @param mode �ۂ߃��[�h
         */
        public void setRoundingMode(int mode){
            this.roundingMode = mode;
        }

        /**
         * 2�l�̕��ς����߂�ۂ̏��Z���ʂɓK�p����ۂ߃��[�h���擾����B<p>
         *
         * @return �ۂ߃��[�h
         */
        public int getRoundingMode(){
            return roundingMode;
        }

        /**
         * ���Z���ʂɓK�p���鏬���_�ȉ�������ݒ肷��B<p>
         * �w�肵�Ȃ��ꍇ�́A��ꍀ�̌����Ɉˑ�����B<br>
         *
         * @param scale �����_�ȉ�����
         */
        public void setScale(int scale){
            this.scale = scale;
        }

        /**
         * ���Z���ʂɓK�p���鏬���_�ȉ��������擾����B<p>
         *
         * @return �����_�ȉ�����
         */
        public int getScale(){
            return scale;
        }

        /**
         * �W���̒����l���v�Z����B<p>
         *
         * @param numbers �W�����Z�̑ΏۂƂȂ鐔�l�W��
         * @return �W�����Z�̒l
         * @exception Exception �W�����Z�Ɏ��s�����ꍇ
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
     * ���ϏW�����Z���s��{@link MBeanWatcherService.SetOperation}�̎����N���X�B<p>
     *
     * @author M.Takata
     */
    public static class Average extends Sum{

        private static final long serialVersionUID = -7262525074283633937L;
        protected int roundingMode = BigDecimal.ROUND_HALF_EVEN;
        protected int scale = -1;

        /**
         * ���ς����߂�ۂ̏��Z���ʂɓK�p����ۂ߃��[�h��ݒ肷��B<p>
         * �f�t�H���g�́A{@link BigDecimal#ROUND_HALF_EVEN}�B<br>
         *
         * @param mode �ۂ߃��[�h
         */
        public void setRoundingMode(int mode){
            this.roundingMode = mode;
        }

        /**
         * ���ς����߂�ۂ̏��Z���ʂɓK�p����ۂ߃��[�h���擾����B<p>
         *
         * @return �ۂ߃��[�h
         */
        public int getRoundingMode(){
            return roundingMode;
        }

        /**
         * ���Z���ʂɓK�p���鏬���_�ȉ�������ݒ肷��B<p>
         * �w�肵�Ȃ��ꍇ�́A��ꍀ�̌����Ɉˑ�����B<br>
         *
         * @param scale �����_�ȉ�����
         */
        public void setScale(int scale){
            this.scale = scale;
        }

        /**
         * ���Z���ʂɓK�p���鏬���_�ȉ��������擾����B<p>
         *
         * @return �����_�ȉ�����
         */
        public int getScale(){
            return scale;
        }

        /**
         * �W���̕��ϒl���擾����B<p>
         *
         * @param numbers �W�����Z�̑ΏۂƂȂ鐔�l�W��
         * @return �W�����Z�̒l
         * @exception Exception �W�����Z�Ɏ��s�����ꍇ
         */
        protected BigDecimal calculate(BigDecimal[] numbers) throws Exception{
            if(scale >= 0){
                return super.calculate(numbers).divide(new BigDecimal((double)numbers.length), scale, roundingMode);
            }else{
                return super.calculate(numbers).divide(new BigDecimal((double)numbers.length), roundingMode);
            }
        }

        /**
         * ���̊Ď��Ώۂ̕�����\�����擾����B<p>
         *
         * @return ������\��
         */
        public String toString(){
            StringBuffer buf = new StringBuffer(super.toString());
            buf.deleteCharAt(buf.length() - 1);
            buf.append(",roundingMode=").append(roundingMode);
            buf.append('}');
            return buf.toString();
        }
    }

    /**
     * ���U�W�����Z���s��{@link MBeanWatcherService.SetOperation}�̎����N���X�B<p>
     *
     * @author M.Takata
     */
    public static class Variance extends Average{

        private static final long serialVersionUID = -6250730592729577578L;

        /**
         * �W���̕��U�l���擾����B<p>
         *
         * @param numbers �W�����Z�̑ΏۂƂȂ鐔�l�W��
         * @return �W�����Z�̒l
         * @exception Exception �W�����Z�Ɏ��s�����ꍇ
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
     * �W���΍��W�����Z���s��{@link MBeanWatcherService.SetOperation}�̎����N���X�B<p>
     *
     * @author M.Takata
     */
    public static class StandardDeviation extends Variance{

        private static final long serialVersionUID = 1484688589485075232L;

        /**
         * �W���̕W���΍��l���擾����B<p>
         *
         * @param numbers �W�����Z�̑ΏۂƂȂ鐔�l�W��
         * @return �W�����Z�̒l
         * @exception Exception �W�����Z�Ɏ��s�����ꍇ
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
     * �ő�W�����Z���s��{@link MBeanWatcherService.SetOperation}�̎����N���X�B<p>
     *
     * @author M.Takata
     */
    public static class Max extends SetOperation{

        private static final long serialVersionUID = 3152818165408079349L;

        /**
         * �W���̍ő�l���擾����B<p>
         *
         * @param numbers �W�����Z�̑ΏۂƂȂ鐔�l�W��
         * @return �W�����Z�̒l
         * @exception Exception �W�����Z�Ɏ��s�����ꍇ
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
     * �ŏ��W�����Z���s��{@link MBeanWatcherService.SetOperation}�̎����N���X�B<p>
     *
     * @author M.Takata
     */
    public static class Min extends SetOperation{

        private static final long serialVersionUID = -8356445551875453616L;

        /**
         * �W���̍ŏ��l���擾����B<p>
         *
         * @param numbers �W�����Z�̑ΏۂƂȂ鐔�l�W��
         * @return �W�����Z�̒l
         * @exception Exception �W�����Z�Ɏ��s�����ꍇ
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
     * ���b�v�����Ď��Ώۂ̒l�擾�����ŗ�O�����������ꍇ�ɕԋp�l��ύX����{@link MBeanWatcherService.WrapTarget}�̎����N���X�B<br>
     *
     * @author M.Ishida
     */
    public static class ExceptionHandle extends WrapTarget {

        private List exceptionConditions = new ArrayList();

        /**
         * ��O�������ɕԋp�l��ҏW���������ǉ�����B
         * <p>
         * �ǉ����ꂽ���Ԃɗ�O�̃`�F�b�N�����āA��O����v����ƌ㑱�̏����̓`�F�b�N�����ɏI������B<br>
         *
         * @param condition �`�F�b�N����
         */
        public void addExceptionCondition(Condition condition) {
            exceptionConditions.add(condition);
        }

        /**
         * �`�F�b�N��������̃��X�g���擾����B
         * <p>
         *
         * @return �`�F�b�N�����̃��X�g
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
         * ���̊Ď��Ώۂ̕�����\�����擾����B
         * <p>
         *
         * @return ������\��
         */
        public String toString() {
            StringBuffer buf = new StringBuffer(super.toString());
            buf.deleteCharAt(buf.length() - 1);
            buf.append("exceptionConditions=").append(exceptionConditions);
            buf.append('}');
            return buf.toString();
        }

        /**
         * ��O�����B<br>
         *
         * �Ď��Ώۂ̒l�擾���ɗ�O�����������ꍇ�ɕԋp�l��ҏW�������<br>
         * �Ώۂ̗�O�����������ꍇ�ɁA��O���̂�ԋp����A��O�̃��b�Z�[�W��ԋp����Anull��ԋp����A�����Object��ԋp����AInterpreter�����s����̂����ꂩ���s���B
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
             * �����Ώۂ̗�O��ݒ肷��B<p>
             *
             * @param exception �����Ώۂ̗�O
             */
            public void setTargetException(Exception exception) {
                targetException = exception;
            }

            /**
             * �����Ώۂ̗�O���擾����B<p>
             *
             * @return �����Ώۂ̗�O
             */
            public Exception getTargetException() {
                return targetException;
            }

            /**
             * �Ώۂ̗�O�����������ꍇ�ɗ�O���b�Z�[�W��ԋp���邩�̐ݒ���s���B<p>
             *
             * @param isReturnExceptionMessage true�̏ꍇ�A��O���b�Z�[�W��ԋp����
             */
            public void setReturnExceptionMessage(boolean isReturnExceptionMessage) {
                this.isReturnExceptionMessage = isReturnExceptionMessage;
            }

            /**
             * �Ώۂ̗�O�����������ꍇ�ɗ�O���̂�ԋp���邩�̐ݒ���s���B<p>
             *
             * @param isReturnException true�̏ꍇ�A��O���̂�ԋp����
             */
            public void setReturnException(boolean isReturnException) {
                this.isReturnException = isReturnException;
            }

            /**
             * �Ώۂ̗�O�����������ꍇ��null��ԋp���邩�̐ݒ���s���B<p>
             *
             * @param isReturnNull true�̏ꍇ�Anull��ԋp����
             */
            public void setReturnNull(boolean isReturnNull) {
                this.isReturnNull = isReturnNull;
            }

            /**
             * �Ώۂ̗�O�����������ꍇ�ɕԋp����Object�̐ݒ���s���B<p>
             *
             * @param returnObject �ԋp����Object
             */
            public void setReturnObject(Object returnObject) {
                this.returnObject = returnObject;
            }

            /**
             * �Ώۂ̗�O�����������ꍇ�Ɏ��s����Interpreter���̐ݒ���s���B<p>
             *
             * @param returnObject �ԋp����Object
             */

            /**
             * �Ώۂ̗�O�����������ꍇ�Ɏ��s����Interpreter���̐ݒ���s���B<p>
             *
             * @param expression Interpreter��
             */
            public void setExpression(String expression) {
                this.expression = expression;
            }

            /**
             * �Ώۂ̗�O�����������ꍇ�Ɏ��s����Interpreter�̃T�[�r�X���̐ݒ���s���B<p>
             *
             * @param serviceName Interpreter�̃T�[�r�X��
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
             * ���̊Ď��Ώۂ̕�����\�����擾����B
             * <p>
             *
             * @return ������\��
             */
            public String toString() {
                StringBuffer buf = new StringBuffer();
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
