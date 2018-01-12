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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.LogConfigurationException;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.message.MessageRecordFactory;

/**
 * {@link CommonsLogFactory}のデフォルト実装クラス。<p>
 * The Apache Jakarta ProjectのCommons Loggingのインタフェースで出力したログを、Nimbusのログとして出力するサービスである。<br>
 * また、{@link LogService}のサブクラスなので、Nimbusの{@link Logger}インタフェース経由のログ出力機能も持つ。<br>
 * Commons Loggingのインタフェースで出力したログを、Nimbusのログとして出力する場合は、サービス定義と別に、"commons-logging.properties"ファイルをクラスパス上に置く必要がある。<br>
 * "commons-logging.properties"ファイルに、以下の設定を行う。<br>
 * <ul>
 * <li>org.apache.commons.logging.LogFactory<br>
 * LogFactoryインタフェースの実装クラスを指定する。このサービスを使用する場合は、jp.ossc.nimbus.service.log.NimbusLogFactoryを指定する。 </li>
 * <li>jp.ossc.nimbus.service.log.NimbusLogFactory.DefaultLogFactory<br>
 * このサービスの起動前や停止後に使用するLogFactoryインタフェースの実装クラスを指定する。このプロパティの指定がない場合は、org.apache.commons.logging.impl.LogFactoryImplを使用する。 </li>
 * <li>jp.ossc.nimbus.service.log.NimbusLogFactory.CommonsLogFactoryName<br>
 * このサービスのサービス名を指定する。サービス名は、"マネージャ名#サービス名"で指定する。このサービスを起動するスレッドと、Jakarta Commons LoggingのLogインスタンスを要求するスレッドのコンテキストクラスローダが異なる場合は、このプロパティを指定する必要がある。クラスローダが同じ場合は、指定する必要はない。 </li>
 * </ul>
 * 
 * @author M.Takata
 */
public class DefaultCommonsLogFactoryService extends LogService
 implements CommonsLogFactory, DefaultCommonsLogFactoryServiceMBean{
    
    private static final long serialVersionUID = 7172007959847003109L;
    
    // メッセージID定義
    private static final String DCLF_ = "DCLF_";
    private static final String DCLF_0 = DCLF_ + 0;
    private static final String DCLF_00 = DCLF_0 + 0;
    private static final String DCLF_000 = DCLF_00 + 0;
    private static final String DCLF_0000 = DCLF_000 + 0;
    private static final String DCLF_00001 = DCLF_0000 + 1;
    
    /**
     * 有効なログインスタンスのキー名セット
     */
    private Set enabledClientSet = new HashSet();
    
    /** {@link #CATEGORY_COMMONS_TRACE}カテゴリのログ出力フラグ */
    private boolean isCommonsTraceEnabled = false;
    
    /** {@link #CATEGORY_COMMONS_DEBUG}カテゴリのログ出力フラグ */
    private boolean isCommonsDebugEnabled = false;
    
    /** {@link #CATEGORY_COMMONS_INFO}カテゴリのログ出力フラグ */
    private boolean isCommonsInfoEnabled = true;
    
    /** {@link #CATEGORY_COMMONS_WARN}カテゴリのログ出力フラグ */
    private boolean isCommonsWarnEnabled = true;
    
    /** {@link #CATEGORY_COMMONS_ERROR}カテゴリのログ出力フラグ */
    private boolean isCommonsErrorEnabled = true;
    
    /** {@link #CATEGORY_COMMONS_FATAL}カテゴリのログ出力フラグ */
    private boolean isCommonsFatalEnabled = true;
    
    /**
     * {@link #CATEGORY_COMMONS_TRACE}カテゴリのログ出力を行う{@link jp.ossc.nimbus.service.writer.MessageWriter MessageWriter}サービス名。<p>
     */
    private ServiceName commonsTraceMessageWriterServiceName;
    
    /**
     * {@link #CATEGORY_COMMONS_DEBUG}カテゴリのログ出力を行う{@link jp.ossc.nimbus.service.writer.MessageWriter MessageWriter}サービス名。<p>
     */
    private ServiceName commonsDebugMessageWriterServiceName;
    
    /**
     * {@link #CATEGORY_COMMONS_INFO}カテゴリのログ出力を行う{@link jp.ossc.nimbus.service.writer.MessageWriter MessageWriter}サービス名。<p>
     */
    private ServiceName commonsInfoMessageWriterServiceName;
    
    /**
     * {@link #CATEGORY_COMMONS_WARN}カテゴリのログ出力を行う{@link jp.ossc.nimbus.service.writer.MessageWriter MessageWriter}サービス名。<p>
     */
    private ServiceName commonsWarnMessageWriterServiceName;
    
    /**
     * {@link #CATEGORY_COMMONS_ERROR}カテゴリのログ出力を行う{@link jp.ossc.nimbus.service.writer.MessageWriter MessageWriter}サービス名。<p>
     */
    private ServiceName commonsErrorMessageWriterServiceName;
    
    /**
     * {@link #CATEGORY_COMMONS_FATAL}カテゴリのログ出力を行う{@link jp.ossc.nimbus.service.writer.MessageWriter MessageWriter}サービス名。<p>
     */
    private ServiceName commonsFatalMessageWriterServiceName;
    
    /**
     * {@link #CATEGORY_COMMONS_TRACE}カテゴリのログ出力フォーマットを行う{@link jp.ossc.nimbus.service.writer.WritableRecordFactory WritableRecordFactory}サービス名。<p>
     */
    private ServiceName commonsTraceRecordFactoryServiceName;
    
    /**
     * {@link #CATEGORY_COMMONS_DEBUG}カテゴリのログ出力フォーマットを行う{@link jp.ossc.nimbus.service.writer.WritableRecordFactory WritableRecordFactory}サービス名。<p>
     */
    private ServiceName commonsDebugRecordFactoryServiceName;
    
    /**
     * {@link #CATEGORY_COMMONS_INFO}カテゴリのログ出力フォーマットを行う{@link jp.ossc.nimbus.service.writer.WritableRecordFactory WritableRecordFactory}サービス名。<p>
     */
    private ServiceName commonsInfoRecordFactoryServiceName;
    
    /**
     * {@link #CATEGORY_COMMONS_WARN}カテゴリのログ出力フォーマットを行う{@link jp.ossc.nimbus.service.writer.WritableRecordFactory WritableRecordFactory}サービス名。<p>
     */
    private ServiceName commonsWarnRecordFactoryServiceName;
    
    /**
     * {@link #CATEGORY_COMMONS_ERROR}カテゴリのログ出力フォーマットを行う{@link jp.ossc.nimbus.service.writer.WritableRecordFactory WritableRecordFactory}サービス名。<p>
     */
    private ServiceName commonsErrorRecordFactoryServiceName;
    
    /**
     * {@link #CATEGORY_COMMONS_FATAL}カテゴリのログ出力フォーマットを行う{@link jp.ossc.nimbus.service.writer.WritableRecordFactory WritableRecordFactory}サービス名。<p>
     */
    private ServiceName commonsFatalRecordFactoryServiceName;
    
    /**
     * {@link Log}インスタンス管理マップ。<p>
     * <table border="1">
     *   <tr bgcolor="#CCCCFF"><th colspan="2">キー</th><th colspan="2">値</th></tr>
     *   <tr bgcolor="#CCCCFF"><th>型</th><th>内容</th><th>型</th><th>内容</th></tr>
     *   <tr><td>Object</td><td>Logインスタンス識別情報</td><td>Log</td><td>Logインスタンス</td></tr>
     * </table>
     */
    private Map logInstances;
    
    /**
     * 属性管理マップ。<p>
     * <table border="1">
     *   <tr bgcolor="#CCCCFF"><th colspan="2">キー</th><th colspan="2">値</th></tr>
     *   <tr bgcolor="#CCCCFF"><th>型</th><th>内容</th><th>型</th><th>内容</th></tr>
     *   <tr><td>Object</td><td>属性名</td><td>Object</td><td>属性値</td></tr>
     * </table>
     */
    private Map attributes;
    
    /**
     * 生成処理を行う。<p>
     * このメソッドには、以下の実装が行われている。<br>
     * <ol>
     *   <li>super.createService()呼び出し。</li>
     *   <li>Logインスタンス管理用Mapを生成する。</li>
     *   <li>属性管理用Mapを生成する。</li>
     * </ol>
     * 
     * @exception Exception 生成処理に失敗した場合
     */
    public void createService() throws Exception{
        super.createService();
        
        logInstances = Collections.synchronizedMap(new HashMap());
        attributes = Collections.synchronizedMap(new HashMap());
    }
    
    /**
     * 開始処理を行う。<p>
     * このメソッドには、以下の実装が行われている。<br>
     * <ol>
     *   <li>super.startService()呼び出し。</li>
     *   <li>Commonsログカテゴリをデフォルトカテゴリとして登録する。</li>
     *   <li>ログ出力フォーマットを{@link DefaultCommonsLogFactoryServiceMBean#DEFAULT_FORMAT}に変更する。</li>
     *   <li>{@link NimbusLogFactory#setCommonsLogFactory(CommonsLogFactory)}に自分自身を設定する。</li>
     * </ol>
     * 
     * @exception Exception 開始処理に失敗した場合
     */
    public void startService() throws Exception{
        defaultFormat = DefaultCommonsLogFactoryServiceMBean.DEFAULT_FORMAT;
        super.startService();
        
        // NimbusLogFactoryに登録
        final LogFactory logFactory = LogFactory.getFactory();
        if(logFactory instanceof NimbusLogFactory){
            ((NimbusLogFactory)logFactory).setCommonsLogFactory(this);
        }
    }
    
    protected void initDefaultCategory() throws Exception{
        super.initDefaultCategory();
        
        // Commonsログカテゴリの登録
        addDefaultCategory(
            getDefaultMessageWriterService(),
            getDefaultWritableRecordFactoryService(),
            getCommonsTraceMessageWriterServiceName(),
            getCommonsTraceWritableRecordFactoryServiceName(),
            CATEGORY_COMMONS_TRACE,
            PRIORITY_COMMONS_TRACE_MIN,
            PRIORITY_COMMONS_TRACE_MAX,
            LABEL_COMMONS_TRACE,
            isCommonsTraceEnabled()
        );
        addDefaultCategory(
            getDefaultMessageWriterService(),
            getDefaultWritableRecordFactoryService(),
            getCommonsDebugMessageWriterServiceName(),
            getCommonsDebugWritableRecordFactoryServiceName(),
            CATEGORY_COMMONS_DEBUG,
            PRIORITY_COMMONS_DEBUG_MIN,
            PRIORITY_COMMONS_DEBUG_MAX,
            LABEL_COMMONS_DEBUG,
            isCommonsDebugEnabled()
        );
        addDefaultCategory(
            getDefaultMessageWriterService(),
            getDefaultWritableRecordFactoryService(),
            getCommonsInfoMessageWriterServiceName(),
            getCommonsInfoWritableRecordFactoryServiceName(),
            CATEGORY_COMMONS_INFO,
            PRIORITY_COMMONS_INFO_MIN,
            PRIORITY_COMMONS_INFO_MAX,
            LABEL_COMMONS_INFO,
            isCommonsInfoEnabled()
        );
        addDefaultCategory(
            getDefaultMessageWriterService(),
            getDefaultWritableRecordFactoryService(),
            getCommonsWarnMessageWriterServiceName(),
            getCommonsWarnWritableRecordFactoryServiceName(),
            CATEGORY_COMMONS_WARN,
            PRIORITY_COMMONS_WARN_MIN,
            PRIORITY_COMMONS_WARN_MAX,
            LABEL_COMMONS_WARN,
            isCommonsWarnEnabled()
        );
        addDefaultCategory(
            getDefaultMessageWriterService(),
            getDefaultWritableRecordFactoryService(),
            getCommonsErrorMessageWriterServiceName(),
            getCommonsErrorWritableRecordFactoryServiceName(),
            CATEGORY_COMMONS_ERROR,
            PRIORITY_COMMONS_ERROR_MIN,
            PRIORITY_COMMONS_ERROR_MAX,
            LABEL_COMMONS_ERROR,
            isCommonsErrorEnabled()
        );
        addDefaultCategory(
            getDefaultMessageWriterService(),
            getDefaultWritableRecordFactoryService(),
            getCommonsFatalMessageWriterServiceName(),
            getCommonsFatalWritableRecordFactoryServiceName(),
            CATEGORY_COMMONS_FATAL,
            PRIORITY_COMMONS_FATAL_MIN,
            PRIORITY_COMMONS_FATAL_MAX,
            LABEL_COMMONS_FATAL,
            isCommonsFatalEnabled()
        );
    }
    
    /**
     * 停止処理を行う。<p>
     * このメソッドには、以下の実装が行われている。<br>
     * <ol>
     *   <li>super.stopService()呼び出し。</li>
     *   <li>{@link #release()}を呼び出す。</li>
     *   <li>{@link NimbusLogFactory#setCommonsLogFactory(CommonsLogFactory)}にnullを設定する。</li>
     * </ol>
     */
    public void stopService(){
        super.stopService();
        release();
        final LogFactory logFactory = LogFactory.getFactory();
        if(logFactory instanceof NimbusLogFactory){
            ((NimbusLogFactory)logFactory).setCommonsLogFactory(null);
        }
    }
    
    // CommonsLogFactoryのJavaDoc
    public Log getInstance(Class clazz) throws LogConfigurationException{
        if(logInstances == null){
            final MessageRecordFactory message = getMessageRecordFactory();
            throw new LogConfigurationException(
                message.findMessage(DCLF_00001)
            );
        }
        if(logInstances.containsKey(clazz)){
            return (Log)logInstances.get(clazz);
        }
        final CommonsLog log = new CommonsLog(clazz);
        logInstances.put(clazz, log);
        if(!enabledClientSet.isEmpty()){
            log.setEnabled(containsEnabledClient(log));
        }
        return log;
    }
    
    // CommonsLogFactoryのJavaDoc
    public Log getInstance(String name) throws LogConfigurationException{
        if(logInstances == null){
            final MessageRecordFactory message = getMessageRecordFactory();
            throw new LogConfigurationException(
                message.findMessage(DCLF_00001)
            );
        }
        if(logInstances.containsKey(name)){
            return (Log)logInstances.get(name);
        }
        final CommonsLog log = new CommonsLog(name);
        logInstances.put(name, log);
        if(!enabledClientSet.isEmpty()){
            log.setEnabled(containsEnabledClient(log));
        }
        return log;
    }
    
    // CommonsLogFactoryのJavaDoc
    public void release(){
        if(logInstances != null){
            logInstances.clear();
        }
    }
    
    // CommonsLogFactoryのJavaDoc
    public Object getAttribute(String name){
        if(attributes == null){
            return null;
        }
        return attributes.get(name);
    }
    
    // CommonsLogFactoryのJavaDoc
    public String[] getAttributeNames(){
        if(attributes == null){
            return new String[0];
        }
        return (String[])attributes.keySet()
            .toArray(new String[attributes.size()]);
    }
    
    // CommonsLogFactoryのJavaDoc
    public void removeAttribute(String name){
        if(attributes == null){
            return;
        }
        attributes.remove(name);
    }
    
    // CommonsLogFactoryのJavaDoc
    public void setAttribute(String name, Object value){
        if(attributes == null){
            return;
        }
        attributes.put(name, value);
    }
    
    /**
     * ログのキュー取り出し後の処理を行う。<p>
     * super呼び出しを行い処理を親に委譲する。<br>
     * その後、dequeuedRecordから取り出したLogMessageRecordのインスタンスが、CommonsLogMessageRecordであった場合、{@link #FORMAT_CLIENT_KEY}に対応するクライアント識別文字を取得して、{@link LogEnqueuedRecord#addWritableElement(Object, Object)}で{@link jp.ossc.nimbus.service.writer.WritableElement WritableElement}として追加する。<br>
     *
     * @param dequeuedRecord LogEnqueuedRecordオブジェクト
     */
    protected void postDequeue(LogEnqueuedRecord dequeuedRecord){
        super.postDequeue(dequeuedRecord);
        
        final LogMessageRecord messageRecord
             = dequeuedRecord.getLogMessageRecord();
        if(messageRecord instanceof CommonsLogMessageRecord){
            dequeuedRecord.addWritableElement(
                FORMAT_CLIENT_KEY,
                ((CommonsLogMessageRecord)messageRecord).getShortClientKey()
            );
        }
    }
    
    // DefaultCommonsLogServiceMBeanのJavaDoc
    public void setEnabledClients(String[] clients){
        if(clients != null){
            enabledClientSet.clear();
            for(int i = 0, max = clients.length; i < max; i++){
                enabledClientSet.add(clients[i]);
            }
        }
        if(logInstances != null){
            final Iterator keys = logInstances.keySet().iterator();
            while(keys.hasNext()){
                final Object key = keys.next();
                final CommonsLog log = (CommonsLog)logInstances.get(key);
                if(enabledClientSet.isEmpty()){
                    log.setEnabled(true);
                }else{
                    log.setEnabled(
                        containsEnabledClient(log)
                    );
                }
            }
        }
    }
    
    private boolean containsEnabledClient(CommonsLog log){
        final String key = log.getClientKey();
        if(enabledClientSet.contains(key)){
            return true;
        }
        final Iterator enabledClients = enabledClientSet.iterator();
        while(enabledClients.hasNext()){
            final String enabledClient = (String)enabledClients.next();
            final int length = enabledClient.length();
            if(length == 0){
                continue;
            }
            if(enabledClient.charAt(length - 1) == '*'){
                final String match = enabledClient.substring(0, length - 1);
                if(key.startsWith(match)){
                    return true;
                }
            }
        }
        return false;
    }
    
    // DefaultCommonsLogServiceMBeanのJavaDoc
    public String[] getEnabledClients(){
        return (String[])enabledClientSet.toArray(new String[enabledClientSet.size()]);
    }
    
    // DefaultCommonsLogServiceMBeanのJavaDoc
    public void setCommonsTraceEnabled(boolean isEnabled){
        isCommonsTraceEnabled = isEnabled;
        setEnabled(
            CATEGORY_COMMONS_TRACE,
            isEnabled
        );
    }
    
    // DefaultCommonsLogServiceMBeanのJavaDoc
    public boolean isCommonsTraceEnabled(){
        return isEnabled(
            CATEGORY_COMMONS_TRACE,
            isCommonsTraceEnabled
        );
    }
    
    // DefaultCommonsLogServiceMBeanのJavaDoc
    public void setCommonsDebugEnabled(boolean isEnabled){
        isCommonsDebugEnabled = isEnabled;
        setEnabled(
            CATEGORY_COMMONS_DEBUG,
            isEnabled
        );
    }
    
    // DefaultCommonsLogServiceMBeanのJavaDoc
    public boolean isCommonsDebugEnabled(){
        return isEnabled(
            CATEGORY_COMMONS_DEBUG,
            isCommonsDebugEnabled
        );
    }
    
    // DefaultCommonsLogServiceMBeanのJavaDoc
    public void setCommonsInfoEnabled(boolean isEnabled){
        isCommonsInfoEnabled = isEnabled;
        setEnabled(
            CATEGORY_COMMONS_INFO,
            isEnabled
        );
    }
    
    // DefaultCommonsLogServiceMBeanのJavaDoc
    public boolean isCommonsInfoEnabled(){
        return isEnabled(
            CATEGORY_COMMONS_INFO,
            isCommonsInfoEnabled
        );
    }
    
    // DefaultCommonsLogServiceMBeanのJavaDoc
    public void setCommonsWarnEnabled(boolean isEnabled){
        isCommonsWarnEnabled = isEnabled;
        setEnabled(
            CATEGORY_COMMONS_WARN,
            isEnabled
        );
    }
    
    // DefaultCommonsLogServiceMBeanのJavaDoc
    public boolean isCommonsWarnEnabled(){
        return isEnabled(
            CATEGORY_COMMONS_WARN,
            isCommonsWarnEnabled
        );
    }
    
    // DefaultCommonsLogServiceMBeanのJavaDoc
    public void setCommonsErrorEnabled(boolean isEnabled){
        isCommonsErrorEnabled = isEnabled;
        setEnabled(
            CATEGORY_COMMONS_ERROR,
            isEnabled
        );
    }
    
    // DefaultCommonsLogServiceMBeanのJavaDoc
    public boolean isCommonsErrorEnabled(){
        return isEnabled(
            CATEGORY_COMMONS_ERROR,
            isCommonsErrorEnabled
        );
    }
    
    // DefaultCommonsLogServiceMBeanのJavaDoc
    public void setCommonsFatalEnabled(boolean isEnabled){
        isCommonsFatalEnabled = isEnabled;
        setEnabled(
            CATEGORY_COMMONS_FATAL,
            isEnabled
        );
    }
    
    // DefaultCommonsLogServiceMBeanのJavaDoc
    public boolean isCommonsFatalEnabled(){
        return isEnabled(
            CATEGORY_COMMONS_FATAL,
            isCommonsFatalEnabled
        );
    }
    
    // DefaultCommonsLogServiceMBeanのJavaDoc
    public void setCommonsTraceMessageWriterServiceName(ServiceName name){
        commonsTraceMessageWriterServiceName = name;
    }
    
    // DefaultCommonsLogServiceMBeanのJavaDoc
    public ServiceName getCommonsTraceMessageWriterServiceName(){
        return commonsTraceMessageWriterServiceName;
    }
    
    // DefaultCommonsLogServiceMBeanのJavaDoc
    public void setCommonsDebugMessageWriterServiceName(ServiceName name){
        commonsDebugMessageWriterServiceName = name;
    }
    
    // DefaultCommonsLogServiceMBeanのJavaDoc
    public ServiceName getCommonsDebugMessageWriterServiceName(){
        return commonsDebugMessageWriterServiceName;
    }
    
    // DefaultCommonsLogServiceMBeanのJavaDoc
    public void setCommonsInfoMessageWriterServiceName(ServiceName name){
        commonsInfoMessageWriterServiceName = name;
    }
    
    // DefaultCommonsLogServiceMBeanのJavaDoc
    public ServiceName getCommonsInfoMessageWriterServiceName(){
        return commonsInfoMessageWriterServiceName;
    }
    
    // DefaultCommonsLogServiceMBeanのJavaDoc
    public void setCommonsWarnMessageWriterServiceName(ServiceName name){
        commonsWarnMessageWriterServiceName = name;
    }
    
    // DefaultCommonsLogServiceMBeanのJavaDoc
    public ServiceName getCommonsWarnMessageWriterServiceName(){
        return commonsWarnMessageWriterServiceName;
    }
    
    // DefaultCommonsLogServiceMBeanのJavaDoc
    public void setCommonsErrorMessageWriterServiceName(ServiceName name){
        commonsErrorMessageWriterServiceName = name;
    }
    
    // DefaultCommonsLogServiceMBeanのJavaDoc
    public ServiceName getCommonsErrorMessageWriterServiceName(){
        return commonsErrorMessageWriterServiceName;
    }
    
    // DefaultCommonsLogServiceMBeanのJavaDoc
    public void setCommonsFatalMessageWriterServiceName(ServiceName name){
        commonsFatalMessageWriterServiceName = name;
    }
    
    // DefaultCommonsLogServiceMBeanのJavaDoc
    public ServiceName getCommonsFatalMessageWriterServiceName(){
        return commonsFatalMessageWriterServiceName;
    }
    
    // DefaultCommonsLogServiceMBeanのJavaDoc
    public void setCommonsTraceWritableRecordFactoryServiceName(
        ServiceName name
    ){
        commonsTraceRecordFactoryServiceName = name;
    }
    
    // DefaultCommonsLogServiceMBeanのJavaDoc
    public ServiceName getCommonsTraceWritableRecordFactoryServiceName(){
        return commonsTraceRecordFactoryServiceName;
    }
    
    // DefaultCommonsLogServiceMBeanのJavaDoc
    public void setCommonsDebugWritableRecordFactoryServiceName(
        ServiceName name
    ){
        commonsDebugRecordFactoryServiceName = name;
    }
    
    // DefaultCommonsLogServiceMBeanのJavaDoc
    public ServiceName getCommonsDebugWritableRecordFactoryServiceName(){
        return commonsDebugRecordFactoryServiceName;
    }
    
    // DefaultCommonsLogServiceMBeanのJavaDoc
    public void setCommonsInfoWritableRecordFactoryServiceName(
        ServiceName name
    ){
        commonsInfoRecordFactoryServiceName = name;
    }
    
    // DefaultCommonsLogServiceMBeanのJavaDoc
    public ServiceName getCommonsInfoWritableRecordFactoryServiceName(){
        return commonsInfoRecordFactoryServiceName;
    }
    
    // DefaultCommonsLogServiceMBeanのJavaDoc
    public void setCommonsWarnWritableRecordFactoryServiceName(
        ServiceName name
    ){
        commonsWarnRecordFactoryServiceName = name;
    }
    
    // DefaultCommonsLogServiceMBeanのJavaDoc
    public ServiceName getCommonsWarnWritableRecordFactoryServiceName(){
        return commonsWarnRecordFactoryServiceName;
    }
    
    // DefaultCommonsLogServiceMBeanのJavaDoc
    public void setCommonsErrorWritableRecordFactoryServiceName(
        ServiceName name
    ){
        commonsErrorRecordFactoryServiceName = name;
    }
    
    // DefaultCommonsLogServiceMBeanのJavaDoc
    public ServiceName getCommonsErrorWritableRecordFactoryServiceName(){
        return commonsErrorRecordFactoryServiceName;
    }
    
    // DefaultCommonsLogServiceMBeanのJavaDoc
    public void setCommonsFatalWritableRecordFactoryServiceName(
        ServiceName name
    ){
        commonsFatalRecordFactoryServiceName = name;
    }
    
    // DefaultCommonsLogServiceMBeanのJavaDoc
    public ServiceName getCommonsFatalWritableRecordFactoryServiceName(){
        return commonsFatalRecordFactoryServiceName;
    }
    
    private static class CommonsLogMessageRecord extends LogMessageRecordImpl
     implements java.io.Serializable{
        
        private static final long serialVersionUID = 7967745897491812488L;
        
        private final String clientKey;
        private final String shortClientKey;
        
        public CommonsLogMessageRecord(CommonsLog logger){
            clientKey = logger.getClientKey();
            shortClientKey = logger.getShortClientKey();
        }
        
        public String getClientKey(){
            return clientKey;
        }
        
        public String getShortClientKey(){
            return shortClientKey;
        }
    }
    
    private class CommonsLog
     implements org.apache.commons.logging.Log, java.io.Serializable{
        
        private static final long serialVersionUID = 6075471555520523752L;
        
        private final String clientKey;
        private String shortClientKey;
        private boolean isEnabled = true;
        
        public CommonsLog(){
            this(DefaultCommonsLogFactoryService.class);
        }
        
        public CommonsLog(Class clazz){
            this(clazz.getName());
            final String className = clazz.getName();
            final int index = className.lastIndexOf('.');
            shortClientKey
                 = index == -1 ? className : className.substring(index + 1);
        }
        
        public CommonsLog(String name){
            clientKey = name;
        }
        
        public String getClientKey(){
            return clientKey;
        }
        
        public String getShortClientKey(){
            return shortClientKey == null ? clientKey : shortClientKey;
        }
        
        public void setEnabled(boolean enable){
            isEnabled = enable;
        }
        
        public boolean isEnabled(){
            return isEnabled;
        }
        
        private LogMessageRecord createLogMessageRecord(
            String category,
            int priority,
            Object message,
            CommonsLog logger
        ){
            final CommonsLogMessageRecord record
                 = new CommonsLogMessageRecord(logger);
            record.addCategory(category);
            record.setPriority(priority);
            record.setMessageCode(EMPTY_STRING);
            record.addMessage(message != null ? message.toString() : null);
            record.setFactory(getMessageRecordFactoryService());
            return record;
        }
        
        private void write(String category, int priority, Object message){
            if(!isEnabled()){
                return;
            }
            final LogMessageRecord messageRecord = createLogMessageRecord(
                category,
                priority,
                message,
                this
            );
            if(!isWrite(messageRecord)){
                return;
            }
            DefaultCommonsLogFactoryService.this.write(
                messageRecord,
                null,
                (String)null,
                null
            );
        }
        
        private void write(
            String category,
            int priority,
            Object message,
            Throwable t
        ){
            if(!isEnabled()){
                return;
            }
            final LogMessageRecord messageRecord = createLogMessageRecord(
                category,
                priority,
                message,
                this
            );
            if(!isWrite(messageRecord)){
                return;
            }
            DefaultCommonsLogFactoryService.this.write(messageRecord, null, (String)null, t);
        }
        
        public void trace(Object message){
            write(CATEGORY_COMMONS_TRACE, PRIORITY_COMMONS_TRACE, message);
        }
        
        public void trace(Object message, Throwable t){
            write(CATEGORY_COMMONS_TRACE, PRIORITY_COMMONS_TRACE, message, t);
        }
        
        public void debug(Object message){
            write(CATEGORY_COMMONS_DEBUG, PRIORITY_COMMONS_DEBUG, message);
        }
        
        public void debug(Object message, Throwable t){
            write(CATEGORY_COMMONS_DEBUG, PRIORITY_COMMONS_DEBUG, message, t);
        }
        
        public void info(Object message){
            write(CATEGORY_COMMONS_INFO, PRIORITY_COMMONS_INFO, message);
        }
        
        public void info(Object message, Throwable t){
            write(CATEGORY_COMMONS_INFO, PRIORITY_COMMONS_INFO, message, t);
        }
        
        public void warn(Object message){
            write(CATEGORY_COMMONS_WARN, PRIORITY_COMMONS_WARN, message);
        }
        
        public void warn(Object message, Throwable t){
            write(CATEGORY_COMMONS_WARN, PRIORITY_COMMONS_WARN, message, t);
        }
        
        public void error(Object message){
            write(CATEGORY_COMMONS_ERROR, PRIORITY_COMMONS_ERROR, message);
        }
        
        public void error(Object message, Throwable t){
            write(CATEGORY_COMMONS_ERROR, PRIORITY_COMMONS_ERROR, message, t);
        }
        
        public void fatal(Object message){
            write(CATEGORY_COMMONS_FATAL, PRIORITY_COMMONS_FATAL, message);
        }
        
        public void fatal(Object message, Throwable t) {
            write(CATEGORY_COMMONS_FATAL, PRIORITY_COMMONS_FATAL, message, t);
        }
        
        public boolean isTraceEnabled() {
            return isCommonsTraceEnabled();
        }
        
        public boolean isDebugEnabled() {
            return isCommonsDebugEnabled();
        }
        
        public boolean isInfoEnabled() {
            return isCommonsInfoEnabled();
        }
        
        public boolean isWarnEnabled() {
            return isCommonsWarnEnabled();
        }
        
        public boolean isErrorEnabled() {
            return isCommonsErrorEnabled();
        }
        
        public boolean isFatalEnabled() {
            return isCommonsFatalEnabled();
        }
    }
}
