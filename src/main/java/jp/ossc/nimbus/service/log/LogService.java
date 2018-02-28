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

import jp.ossc.nimbus.lang.*;
import jp.ossc.nimbus.daemon.*;
import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.queue.*;
import jp.ossc.nimbus.service.queue.Queue;
import jp.ossc.nimbus.service.context.*;
import jp.ossc.nimbus.service.message.*;
import jp.ossc.nimbus.service.writer.*;

/**
 * ログサービス。<p>
 * 
 * @author H.Nakano
 */
public class LogService extends ServiceBase
 implements DaemonRunnable, Logger, LogServiceMBean {
    
    private static final long serialVersionUID = -4145738242582933541L;
    
    /** 空文字定数 */
    protected static final String EMPTY_STRING = "";
    /** 識別情報Contextキーが指定されていない場合の識別情報文字列 */
    protected static final String NONE_ID = "NONE";
    
    private static final String LINE_SEP = System.getProperty("line.separator");
    private static final String MSG_CAUSE = "Caused by: ";
    private static final String SERVLET_EXCEPTION_NAME = "javax.servlet.ServletException";
    private static final String GET_ROOT_CAUSE_METHOD = "getRootCause";
    private static final String JMS_EXCEPTION_NAME = "javax.jms.JMSException";
    private static final String GET_LINKED_EXCEPTION_METHOD = "getLinkedException";
    
    /**
     * カテゴリ名と{@link LogCategory}サービスのマッピング。<p>
     * <table border="1">
     *   <tr bgcolor="#CCCCFF"><th colspan="2">キー</th><th colspan="2">値</th></tr>
     *   <tr bgcolor="#CCCCFF"><th>型</th><th>内容</th><th>型</th><th>内容</th></tr>
     *   <tr><td>String</td><td>カテゴリ名</td><td>LogCategory</td><td>カテゴリサービス</td></tr>
     * </table>
     */
    private Map categoryMap;
    
    /**
     * デフォルトのカテゴリ名と{@link LogCategory}サービスのマッピング。<p>
     * <table border="1">
     *   <tr bgcolor="#CCCCFF"><th colspan="2">キー</th><th colspan="2">値</th></tr>
     *   <tr bgcolor="#CCCCFF"><th>型</th><th>内容</th><th>型</th><th>内容</th></tr>
     *   <tr><td>String</td><td>カテゴリ名</td><td>LogCategory</td><td>カテゴリサービス</td></tr>
     * </table>
     */
    private Map defaultCategoryMap;
    
    /**
     * カテゴリサービス名配列。<p>
     */
    private ServiceName[] categoryNames;
    
    /**
     * {@link Queue}サービス名。<p>
     */
    private ServiceName queueServiceName;
    
    /**
     * {@link #getQueueServiceName()}がnullの場合、デフォルトの{@link Queue}サービスとして生成する{@link DefaultQueueService}サービス。<p>
     */
    private DefaultQueueService defaultQueue;
    
    /**
     * {@link Queue}オブジェクト。<p>
     */
    private Queue queue;
    
    /**
     * {@link MessageRecordFactory}サービス名。<p>
     */
    private ServiceName messageFactoryServiceName;
    
    /**
     * {@link MessageRecordFactory}サービス。<p>
     */
    private MessageRecordFactory messageFactory;
    
    /**
     * {@link #getMessageRecordFactoryServiceName()}がnullの場合、デフォルトの{@link MessageRecordFactory}サービスとして生成する{@link MessageRecordFactoryService}サービス。<p>
     */
    private MessageRecordFactoryService defaultMessageFactory;
    
    /**
     * {@link Context}サービス名。<p>
     */
    private ServiceName contextServiceName;
    
    /**
     * {@link Context}サービス。<p>
     */
    private Context context;
    
    /**
     * デフォルトの{@link MessageWriter}サービス名。<p>
     */
    private ServiceName defaultMessageWriterServiceName;
    
    /**
     * デフォルトの{@link MessageWriter}サービス。<p>
     */
    private MessageWriter defaultMessageWriter;
    
    /**
     * {@link #getDefaultMessageWriterServiceName()}がnullの場合、デフォルトの{@link MessageWriter}サービスとして生成する{@link ConsoleWriterService}サービス。<p>
     */
    private ConsoleWriterService consoleWriter;
    
    /**
     * デフォルトの{@link WritableRecordFactory}サービス名。<p>
     */
    private ServiceName defaultWritableRecordFactoryServiceName;
    
    /**
     * デフォルトの{@link WritableRecordFactory}サービス。<p>
     */
    private WritableRecordFactory defaultWritableRecordFactory;
    
    /**
     * {@link #getDefaultWritableRecordFactoryServiceName()}がnullの場合、デフォルトの{@link WritableRecordFactory}サービスとして生成する{@link LogWritableRecordFactoryService}サービス。<p>
     */
    private LogWritableRecordFactoryService logWritableRecordFactory;
    
    /**
     * {@link #DEBUG_METHOD_CATEGORY}カテゴリのログ出力を行う{@link MessageWriter}サービス名。<p>
     */
    private ServiceName debugMessageWriterServiceName;
    
    /**
     * {@link #SYSTEM_DEBUG_CATEGORY}カテゴリのログ出力を行う{@link MessageWriter}サービス名。<p>
     */
    private ServiceName systemDebugMessageWriterServiceName;
    
    /**
     * {@link #SYSTEM_INFO_CATEGORY}カテゴリのログ出力を行う{@link MessageWriter}サービス名。<p>
     */
    private ServiceName systemInfoMessageWriterServiceName;
    
    /**
     * {@link #SYSTEM_WARN_CATEGORY}カテゴリのログ出力を行う{@link MessageWriter}サービス名。<p>
     */
    private ServiceName systemWarnMessageWriterServiceName;
    
    /**
     * {@link #SYSTEM_ERROR_CATEGORY}カテゴリのログ出力を行う{@link MessageWriter}サービス名。<p>
     */
    private ServiceName systemErrorMessageWriterServiceName;
    
    /**
     * {@link #SYSTEM_FATAL_CATEGORY}カテゴリのログ出力を行う{@link MessageWriter}サービス名。<p>
     */
    private ServiceName systemFatalMessageWriterServiceName;
    
    /**
     * {@link #DEBUG_METHOD_CATEGORY}カテゴリのログ出力フォーマットを行う{@link WritableRecordFactory}サービス名。<p>
     */
    private ServiceName debugRecordFactoryServiceName;
    
    /**
     * {@link #SYSTEM_DEBUG_CATEGORY}カテゴリのログ出力フォーマットを行う{@link WritableRecordFactory}サービス名。<p>
     */
    private ServiceName systemDebugRecordFactoryServiceName;
    
    /**
     * {@link #SYSTEM_INFO_CATEGORY}カテゴリのログ出力フォーマットを行う{@link WritableRecordFactory}サービス名。<p>
     */
    private ServiceName systemInfoRecordFactoryServiceName;
    
    /**
     * {@link #SYSTEM_WARN_CATEGORY}カテゴリのログ出力フォーマットを行う{@link WritableRecordFactory}サービス名。<p>
     */
    private ServiceName systemWarnRecordFactoryServiceName;
    
    /**
     * {@link #SYSTEM_ERROR_CATEGORY}カテゴリのログ出力フォーマットを行う{@link WritableRecordFactory}サービス名。<p>
     */
    private ServiceName systemErrorRecordFactoryServiceName;
    
    /**
     * {@link #SYSTEM_FATAL_CATEGORY}カテゴリのログ出力フォーマットを行う{@link WritableRecordFactory}サービス名。<p>
     */
    private ServiceName systemFatalRecordFactoryServiceName;
    
    /**
     * {@link Daemon}オブジェクト。<p>
     */
    private Daemon daemon;
    
    /**
     * フォーマット情報Contextキー情報を格納する集合。<p>
     */
    private Set contextKeys = new HashSet();
    
    /** {@link #debug(Object)}メソッドのログ出力フラグ */
    private boolean isDebugEnabled = false;
    
    /** システムログのDEBUGログ出力フラグ */
    private boolean isSystemDebugEnabled = false;
    
    /** システムログのINFOログ出力フラグ */
    private boolean isSystemInfoEnabled = true;
    
    /** システムログのWARNログ出力フラグ */
    private boolean isSystemWarnEnabled = true;
    
    /** システムログのERRORログ出力フラグ */
    private boolean isSystemErrorEnabled = true;
    
    /** システムログのFATALログ出力フラグ */
    private boolean isSystemFatalEnabled = true;
    
    protected String defaultFormat = DEFAULT_FORMAT;
    
    private boolean isDaemon = true;
    
    /**
     * 生成処理を行う。<p>
     * このメソッドには、以下の実装が行われている。<br>
     * <ol>
     *   <li>{@link Daemon}インスタンスを生成する。</li>
     *   <li>カテゴリ管理用Mapインスタンスを生成する。</li>
     * </ol>
     * 
     * @exception Exception 生成処理に失敗した場合。
     */
    public void createService() throws Exception{
        daemon = new Daemon(this);
        daemon.setName("Nimbus LogWriteDaemon " + getServiceNameObject());
        categoryMap = new HashMap();
        defaultCategoryMap = new HashMap();
    }
    
    /**
     * このログサービスでデフォルトで持つ{@link LogCategory}を生成して登録する。<p>
     *
     * @param defaultMessageWriter デフォルトのMessageWriter。messageWriterNameに有効なサービス名が指定されていない場合にLogCategoryで使用される。
     * @param defaultRecordFactory デフォルトのWritableRecordFactory。recordFactoryNameに有効なサービス名が指定されていない場合にLogCategoryで使用される。
     * @param messageWriterName LogCategoryで使用されるMessageWriterサービス名
     * @param recordFactoryName LogCategoryで使用されるWritableRecordFactoryサービス名
     * @param categoryName カテゴリ名
     * @param priorityMin 優先順位範囲の最小値
     * @param priorityMax 優先順位範囲の最大値
     * @param label カテゴリの優先順位範囲のラベル
     * @param isEnabled 出力を有効にするかどうかのフラグ。出力する状態にしたい場合は true
     * @exception Exception カテゴリサービスの生成・開始に失敗した場合
     */
    protected void addDefaultCategory(
        MessageWriter defaultMessageWriter,
        WritableRecordFactory defaultRecordFactory,
        ServiceName messageWriterName,
        ServiceName recordFactoryName,
        String categoryName,
        int priorityMin,
        int priorityMax,
        String label,
        boolean isEnabled
    ) throws Exception{
        MessageWriter messageWriter = defaultMessageWriter;
        WritableRecordFactory recordFactory = defaultRecordFactory;
        if(messageWriterName != null){
            messageWriter = (MessageWriter)ServiceManagerFactory
                .getServiceObject(messageWriterName);
        }
        if(recordFactoryName != null){
            recordFactory = (WritableRecordFactory)ServiceManagerFactory
                .getServiceObject(recordFactoryName);
        }
        final SimpleCategoryService category = new SimpleCategoryService();
        category.setCategoryName(categoryName);
        category.setPriorityRangeValue(priorityMin, priorityMax);
        category.setLabel(priorityMin, priorityMax, label);
        category.create();
        category.start();
        category.setMessageWriterService(messageWriter);
        category.setWritableRecordFactoryService(recordFactory);
        
        addCategoryService(category);
        addDefaultCategoryService(category);
        
        setEnabled(categoryName, isEnabled);
    }
    
    /**
     * 開始処理を行う。<p>
     * このメソッドには、以下の実装が行われている。<br>
     * <ol>
     *   <li>{@link #setDefaultMessageWriterServiceName(ServiceName)}でデフォルトの{@link MessageWriter}サービスの名前が設定されている場合は、{@link ServiceManager}からMessageWriterを取得して{@link #setDefaultMessageWriterService(MessageWriter)}で設定する。また、デフォルトのMessageWriterサービスの名前が設定されていない場合は、{@link ConsoleWriterService}を生成して{@link #setDefaultMessageWriterService(MessageWriter)}で設定する。</li>
     *   <li>{@link #setDefaultWritableRecordFactoryServiceName(ServiceName)}でデフォルトの{@link WritableRecordFactory}サービスの名前が設定されている場合は、{@link ServiceManager}からWritableRecordFactoryを取得して{@link #setDefaultWritableRecordFactoryService(WritableRecordFactory)}で設定する。また、デフォルトのWritableRecordFactoryサービスの名前が設定されていない場合は、{@link LogWritableRecordFactoryService}を生成して{@link #setDefaultWritableRecordFactoryService(WritableRecordFactory)}で設定する。</li>
     *   <li>システムカテゴリを生成して登録する。</li>
     *   <li>{@link #setCategoryServiceNames(ServiceName[])}で設定されたカテゴリを登録する。</li>
     *   <li>{@link #setQueueServiceName(ServiceName)}で{@link Queue}サービスの名前が設定されている場合は、{@link ServiceManager}からQueueを取得して{@link #setQueueService(Queue)}で設定する。また、Queueサービスの名前が設定されていない場合は、{@link DefaultQueueService}を生成して{@link #setQueueService(Queue)}で設定する。</li>
     *   <li>{@link #setMessageRecordFactoryServiceName(ServiceName)}で{@link MessageRecordFactory}サービスの名前が設定されている場合は、{@link ServiceManager}からMessageRecordFactoryを取得して{@link #setMessageRecordFactoryService(MessageRecordFactory)}で設定する。また、MessageRecordFactoryサービスの名前が設定されていない場合は、{@link MessageRecordFactoryService}を生成して{@link #setMessageRecordFactoryService(MessageRecordFactory)}で設定する。</li>
     *   <li>{@link #setContextServiceName(ServiceName)}で{@link Context}サービスの名前が設定されている場合は、{@link ServiceManager}からContextを取得して{@link #setContextService(Context)}で設定する。</li>
     *   <li>{@link Daemon}を起動する。</li>
     * </ol>
     * 
     * @exception Exception 開始処理に失敗した場合。
     */
    public void startService() throws Exception{
        
        // デフォルトMessageWriterサービスの生成または取得
        if(getDefaultMessageWriterServiceName() == null){
            if(getConsoleWriterService() == null){
                final ConsoleWriterService consoleWriter
                     = new ConsoleWriterService();
                consoleWriter.setOutput(ConsoleWriterService.OUTPUT_STDOUT);
                consoleWriter.create();
                consoleWriter.start();
                setConsoleWriterService(consoleWriter);
            }else{
                getConsoleWriterService().start();
            }
            setDefaultMessageWriterService(getConsoleWriterService());
        }else{
            setDefaultMessageWriterService(
                (MessageWriter)ServiceManagerFactory
                    .getServiceObject(getDefaultMessageWriterServiceName())
            );
        }
        
        // デフォルトWritableRecordFactoryサービスの生成または取得
        if(getDefaultWritableRecordFactoryServiceName() == null){
            if(getLogWritableRecordFactoryService() == null){
                final LogWritableRecordFactoryService recordFactory
                     = new LogWritableRecordFactoryService();
                recordFactory.setFormat(getDefaultFormat());
                recordFactory.create();
                recordFactory.start();
                setLogWritableRecordFactoryService(recordFactory);
            }else{
                getLogWritableRecordFactoryService().start();
            }
            setDefaultWritableRecordFactoryService(
                getLogWritableRecordFactoryService()
            );
        }else{
            setDefaultWritableRecordFactoryService(
                (WritableRecordFactory)ServiceManagerFactory.getServiceObject(
                    getDefaultWritableRecordFactoryServiceName()
                )
            );
        }
        
        // システムカテゴリの登録
        initDefaultCategory();
        
        // ユーザ定義カテゴリの登録
        final ServiceName[] categoryNames = getCategoryServiceNames();
        if(categoryNames != null){
            for(int i = 0; i < categoryNames.length; i++){
                final ServiceName categoryName = categoryNames[i];
                final LogCategory category = (LogCategory)ServiceManagerFactory
                    .getServiceObject(categoryName);
                addCategoryService(category);
            }
        }
        
        // Queueサービスの生成または取得
        if(getQueueServiceName() == null){
            if(getDefaultQueueService() == null){
                final DefaultQueueService defaultQueue
                     = new DefaultQueueService();
                defaultQueue.create();
                defaultQueue.start();
                setDefaultQueueService(defaultQueue);
            }else{
                getDefaultQueueService().start();
            }
            setQueueService(getDefaultQueueService());
        }else{
            setQueueService((Queue)ServiceManagerFactory
                    .getServiceObject(getQueueServiceName())
            );
        }
        
        // MessageRecordFactoryサービスの生成または取得
        if(getMessageRecordFactoryServiceName() == null){
            if(getDefaultMessageRecordFactoryService() == null){
                final MessageRecordFactoryService defaultMessageFactory
                     = new MessageRecordFactoryService();
                defaultMessageFactory.setMessageRecordClassName(
                    LogMessageRecordImpl.class.getName()
                );
                defaultMessageFactory.create();
                defaultMessageFactory.start();
                setDefaultMessageRecordFactoryService(defaultMessageFactory);
            }else{
                getDefaultMessageRecordFactoryService().start();
            }
            setMessageRecordFactoryService(defaultMessageFactory);
        }else{
            setMessageRecordFactoryService(
                (MessageRecordFactory)ServiceManagerFactory
                    .getServiceObject(getMessageRecordFactoryServiceName())
            );
        }
        
        // Contextサービスの取得
        if(getContextServiceName() != null){
            setContextService((Context)ServiceManagerFactory
                    .getServiceObject(getContextServiceName())
            );
        }
        
        // キュー取得待ちを開始する
        queue.accept();
        
        daemon.setDaemon(isDaemon);
        
        // デーモン起動
        daemon.start();
    }
    
    protected void initDefaultCategory() throws Exception{
        addDefaultCategory(
            getDefaultMessageWriterService(),
            getDefaultWritableRecordFactoryService(),
            getDebugMessageWriterServiceName(),
            getDebugWritableRecordFactoryServiceName(),
            DEBUG_METHOD_CATEGORY,
            DEBUG_METHOD_CATEGORY_PRIORITY_MIN,
            DEBUG_METHOD_CATEGORY_PRIORITY_MAX,
            DEBUG_METHOD_CATEGORY_LABEL,
            isDebugEnabled()
        );
        addDefaultCategory(
            getDefaultMessageWriterService(),
            getDefaultWritableRecordFactoryService(),
            getSystemDebugMessageWriterServiceName(),
            getSystemDebugWritableRecordFactoryServiceName(),
            SYSTEM_DEBUG_CATEGORY,
            SYSTEM_DEBUG_CATEGORY_PRIORITY_MIN,
            SYSTEM_DEBUG_CATEGORY_PRIORITY_MAX,
            SYSTEM_DEBUG_CATEGORY_LABEL,
            isSystemDebugEnabled()
        );
        addDefaultCategory(
            getDefaultMessageWriterService(),
            getDefaultWritableRecordFactoryService(),
            getSystemInfoMessageWriterServiceName(),
            getSystemInfoWritableRecordFactoryServiceName(),
            SYSTEM_INFO_CATEGORY,
            SYSTEM_INFO_CATEGORY_PRIORITY_MIN,
            SYSTEM_INFO_CATEGORY_PRIORITY_MAX,
            SYSTEM_INFO_CATEGORY_LABEL,
            isSystemInfoEnabled()
        );
        addDefaultCategory(
            getDefaultMessageWriterService(),
            getDefaultWritableRecordFactoryService(),
            getSystemWarnMessageWriterServiceName(),
            getSystemWarnWritableRecordFactoryServiceName(),
            SYSTEM_WARN_CATEGORY,
            SYSTEM_WARN_CATEGORY_PRIORITY_MIN,
            SYSTEM_WARN_CATEGORY_PRIORITY_MAX,
            SYSTEM_WARN_CATEGORY_LABEL,
            isSystemWarnEnabled()
        );
        addDefaultCategory(
            getDefaultMessageWriterService(),
            getDefaultWritableRecordFactoryService(),
            getSystemErrorMessageWriterServiceName(),
            getSystemErrorWritableRecordFactoryServiceName(),
            SYSTEM_ERROR_CATEGORY,
            SYSTEM_ERROR_CATEGORY_PRIORITY_MIN,
            SYSTEM_ERROR_CATEGORY_PRIORITY_MAX,
            SYSTEM_ERROR_CATEGORY_LABEL,
            isSystemErrorEnabled()
        );
        addDefaultCategory(
            getDefaultMessageWriterService(),
            getDefaultWritableRecordFactoryService(),
            getSystemFatalMessageWriterServiceName(),
            getSystemFatalWritableRecordFactoryServiceName(),
            SYSTEM_FATAL_CATEGORY,
            SYSTEM_FATAL_CATEGORY_PRIORITY_MIN,
            SYSTEM_FATAL_CATEGORY_PRIORITY_MAX,
            SYSTEM_FATAL_CATEGORY_LABEL,
            isSystemFatalEnabled()
        );
    }
    
    /**
     * 停止処理を行う。<p>
     * このメソッドには、以下の実装が行われている。<br>
     * <ol>
     *   <li>デフォルトのMessageWriterサービスを無名サービスとして生成している場合、そのサービスを停止する。</li>
     *   <li>デフォルトのWritableRecordFactoryサービスを無名サービスとして生成している場合、そのサービスを停止する。</li>
     *   <li>Queueサービスを無名サービスとして生成している場合、
        そのサービスを停止する。</li>
     *   <li>MessageRecordFactoryサービスを無名サービスとして生成している場合、
        そのサービスを停止する。</li>
     *   <li>カテゴリを削除する。</li>
     *   <li>{@link Daemon}を停止する。</li>
     * </ol>
     * 
     * @exception Exception 停止処理に失敗した場合。
     */
    public void stopService(){
        
        // デーモン停止
        daemon.stop();
        
        // キュー取得待ちを開放する
        queue.release();
        
        // デフォルトのMessageWriterサービスを無名サービスとして生成して
        // いる場合、そのサービスを停止する
        if(getDefaultMessageWriterService() == getConsoleWriterService()){
            getConsoleWriterService().stop();
        }
        
        // デフォルトのWritableRecordFactoryサービスを無名サービスとして
        // 生成している場合、そのサービスを停止する
        if(getDefaultWritableRecordFactoryService()
             == getLogWritableRecordFactoryService()){
            getLogWritableRecordFactoryService().stop();
        }
        
        // Queueサービスを無名サービスとして生成している場合、
        // そのサービスを停止する
        if(getQueueService() == getDefaultQueueService()){
            getDefaultQueueService().stop();
        }
        
        // MessageRecordFactoryサービスを無名サービスとして生成している場合、
        // そのサービスを停止する
        if(getMessageRecordFactoryService()
             == getDefaultMessageRecordFactoryService()){
            getDefaultMessageRecordFactoryService().stop();
        }
        
        // カテゴリを削除する
        categoryMap.clear();
        defaultCategoryMap.clear();
    }
    
    /**
     * 破棄処理を行う。<p>
     * このメソッドには、以下の実装が行われている。<br>
     * <ol>
     *   <li>デフォルトのMessageWriterサービスを無名サービスとして生成している場合、そのサービスを破棄する。</li>
     *   <li>デフォルトのWritableRecordFactoryサービスを無名サービスとして生成している場合、そのサービスを破棄する。</li>
     *   <li>Queueサービスを無名サービスとして生成している場合、
        そのサービスを破棄する。</li>
     *   <li>MessageRecordFactoryサービスを無名サービスとして生成している場合、
        そのサービスを破棄する。</li>
     *   <li>カテゴリを破棄する。</li>
     *   <li>{@link Daemon}を破棄する。</li>
     * </ol>
     * 
     * @exception Exception 破棄処理に失敗した場合。
     */
    public void destroyService(){
        
        // デフォルトのMessageWriterサービスを無名サービスとして生成して
        // いる場合、そのサービスを破棄する
        if(getDefaultMessageWriterService() == getConsoleWriterService()
            && getConsoleWriterService() != null){
            getConsoleWriterService().destroy();
            setConsoleWriterService(null);
        }
        
        // デフォルトのWritableRecordFactoryサービスを無名サービスとして
        // 生成している場合、そのサービスを破棄する
        if(getDefaultWritableRecordFactoryService()
             == getLogWritableRecordFactoryService()
             && getLogWritableRecordFactoryService() != null){
            getLogWritableRecordFactoryService().destroy();
            setLogWritableRecordFactoryService(null);
        }
        
        // Queueサービスを無名サービスとして生成している場合、
        // そのサービスを破棄する
        if(getQueueService() == getDefaultQueueService()
            && getDefaultQueueService() != null){
            getDefaultQueueService().destroy();
            setDefaultQueueService(null);
        }
        
        // MessageRecordFactoryサービスを無名サービスとして生成している場合、
        // そのサービスを破棄する
        if(getMessageRecordFactoryService()
             == getDefaultMessageRecordFactoryService()
            && getDefaultMessageRecordFactoryService() != null){
            getDefaultMessageRecordFactoryService().destroy();
            setDefaultMessageRecordFactoryService(null);
        }
        
        // カテゴリ管理Mapを破棄する
        categoryMap = null;
        defaultCategoryMap = null;
        
        // デーモンを破棄する
        daemon = null;
    }
    
    /**
     * カテゴリ名と{@link LogCategory}のマッピングを取得する。<p>
     *
     * @return カテゴリ名と{@link LogCategory}のマッピング
     */
    protected Map getCategoryMap(){
        return categoryMap;
    }
    
    // LogServiceMBeanのJavaDoc
    public void setDefaultMessageWriterServiceName(ServiceName name){
        defaultMessageWriterServiceName = name;
    }
    
    // LogServiceMBeanのJavaDoc
    public ServiceName getDefaultMessageWriterServiceName(){
        return defaultMessageWriterServiceName;
    }
    
    // LogServiceMBeanのJavaDoc
    public void setDefaultMessageWriterService(MessageWriter writer){
        defaultMessageWriter = writer;
    }
    
    // LogServiceMBeanのJavaDoc
    public MessageWriter getDefaultMessageWriterService(){
        return defaultMessageWriter;
    }
    
    /**
     * デフォルトのMessageWriterが指定されていない場合に使用する{@link ConsoleWriterService}を取得する。<p>
     * このConsoleWriterServiceは、無名サービスとして生成される。また、{@link #setDefaultMessageWriterServiceName(ServiceName)}でデフォルトのMessageWriterが指定されている場合は、nullを返す場合がある。<br>
     *
     * @return ConsoleWriterServiceオブジェクト。生成されていない場合は、nullを返す。
     */
    protected ConsoleWriterService getConsoleWriterService(){
        return consoleWriter;
    }
    
    /**
     * デフォルトのMessageWriterが指定されていない場合に使用する{@link ConsoleWriterService}を設定する。<p>
     *
     * @param consoleWriter ConsoleWriterServiceオブジェクト
     */
    protected void setConsoleWriterService(ConsoleWriterService consoleWriter){
        this.consoleWriter = consoleWriter;
    }
    
    // LogServiceMBeanのJavaDoc
    public void setDefaultWritableRecordFactoryServiceName(ServiceName name){
        defaultWritableRecordFactoryServiceName = name;
    }
    
    // LogServiceMBeanのJavaDoc
    public ServiceName getDefaultWritableRecordFactoryServiceName(){
        return defaultWritableRecordFactoryServiceName;
    }
    
    // LogServiceMBeanのJavaDoc
    public void setDefaultWritableRecordFactoryService(
        WritableRecordFactory recordFactory
    ){
        defaultWritableRecordFactory = recordFactory;
    }
    
    // LogServiceMBeanのJavaDoc
    public WritableRecordFactory getDefaultWritableRecordFactoryService(){
        return defaultWritableRecordFactory;
    }
    
    /**
     * デフォルトのWritableRecordFactoryが指定されていない場合に使用する{@link LogWritableRecordFactoryService}を取得する。<p>
     * このLogWritableRecordFactoryは、無名サービスとして生成される。また、{@link #setDefaultWritableRecordFactoryServiceName(ServiceName)}でデフォルトのWritableRecordFactoryが指定されている場合は、nullを返す場合がある。<br>
     *
     * @return LogWritableRecordFactoryオブジェクト。生成されていない場合は、nullを返す。
     */
    protected LogWritableRecordFactoryService getLogWritableRecordFactoryService(){
        return logWritableRecordFactory;
    }
    
    /**
     * デフォルトのWritableRecordFactoryが指定されていない場合に使用する{@link LogWritableRecordFactoryService}を設定する。<p>
     *
     * @param logRecordFactory LogWritableRecordFactoryオブジェクト
     */
    protected void setLogWritableRecordFactoryService(
        LogWritableRecordFactoryService logRecordFactory
    ){
        this.logWritableRecordFactory = logRecordFactory;
    }
    
    // LogServiceMBeanのJavaDoc
    public void setCategoryServiceNames(ServiceName[] names){
        categoryNames = names;
    }
    
    // LogServiceMBeanのJavaDoc
    public ServiceName[] getCategoryServiceNames(){
        return categoryNames;
    }
    
    // LogServiceMBeanのJavaDoc
    public void setCategoryServices(LogCategory[] categories){
        if(categoryMap != null){
            
            // デフォルトカテゴリの名前集合を生成
            final Set defaultCategoryNames = defaultCategoryMap.keySet();
            
            // 現在保持しているカテゴリから、システムカテゴリ以外を削除する
            final Set categoryNames = categoryMap.keySet();
            categoryNames.retainAll(defaultCategoryNames);
            
            // 指定されたカテゴリを登録する
            if(categories != null){
                for(int i = 0; i < categories.length; i++){
                    final LogCategory category = categories[i];
                    if(category != null){
                        addCategoryService(category);
                    }
                }
            }
        }
    }
    
    // LogServiceMBeanのJavaDoc
    public LogCategory[] getCategoryServices(){
        if(categoryMap != null){
            return (LogCategory[])categoryMap.values().toArray(
                new LogCategory[categoryMap.size()]
            );
        }
        return new LogCategory[0];
    }
    
    // LogServiceMBeanのJavaDoc
    public void addCategoryService(LogCategory category){
        if(categoryMap != null && category != null){
            categoryMap.put(category.getCategoryName(), category);
        }
    }
    
    /**
     * デフォルトの{@link LogCategory}サービスを追加する。<p>
     *
     * @parma category LogCategoryサービス
     */
    private void addDefaultCategoryService(LogCategory category){
        if(defaultCategoryMap != null && category != null){
            defaultCategoryMap.put(category.getCategoryName(), category);
        }
    }
    
    // LogServiceMBeanのJavaDoc
    public LogCategory getCategoryService(String name){
        if(categoryMap != null && name != null){
            return (LogCategory)categoryMap.get(name);
        }
        return null;
    }
    
    // LogServiceMBeanのJavaDoc
    public void setQueueServiceName(ServiceName name){
        queueServiceName = name;
    }
    
    // LogServiceMBeanのJavaDoc
    public void setQueueService(Queue queue){
        this.queue = queue;
    }
    
    // LogServiceMBeanのJavaDoc
    public ServiceName getQueueServiceName(){
        return queueServiceName;
    }
    
    // LogServiceMBeanのJavaDoc
    public Queue getQueueService(){
        return queue;
    }
    
    /**
     * Queueが指定されていない場合に使用する{@link DefaultQueueService}を取得する。<p>
     * このDefaultQueueServiceは、無名サービスとして生成される。また、{@link #setQueueServiceName(ServiceName)}でQueueが指定されている場合は、nullを返す場合がある。<br>
     *
     * @return DefaultQueueServiceオブジェクト。生成されていない場合はnullを返す。
     */
    protected DefaultQueueService getDefaultQueueService(){
        return defaultQueue;
    }
    
    /**
     * Queueが指定されていない場合に使用する{@link DefaultQueueService}を設定する。<p>
     *
     * @param queue DefaultQueueServiceオブジェクト
     */
    protected void setDefaultQueueService(DefaultQueueService queue){
        defaultQueue = queue;
    }
    
    // LogServiceMBeanのJavaDoc
    public void setMessageRecordFactoryServiceName(ServiceName name){
        messageFactoryServiceName = name;
    }
    
    // LogServiceMBeanのJavaDoc
    public void setMessageRecordFactoryService(MessageRecordFactory message){
        messageFactory = message;
    }
    
    // LogServiceMBeanのJavaDoc
    public ServiceName getMessageRecordFactoryServiceName(){
        return messageFactoryServiceName;
    }
    
    // LogServiceMBeanのJavaDoc
    public MessageRecordFactory getMessageRecordFactoryService(){
        return messageFactory;
    }
    
    /**
     * MessageRecordFactoryが指定されていない場合に使用する{@link MessageRecordFactoryService}を取得する。<p>
     * このMessageRecordFactoryServiceは、無名サービスとして生成される。また、{@link #setMessageRecordFactoryServiceName(ServiceName)}でMessageRecordFactoryが指定されている場合は、nullを返す場合がある。<br>
     *
     * @return MessageRecordFactoryServiceオブジェクト。生成されていない場合はnullを返す。
     */
    protected MessageRecordFactoryService getDefaultMessageRecordFactoryService(){
        return defaultMessageFactory;
    }
    
    /**
     * MessageRecordFactoryが指定されていない場合に使用する{@link jp.ossc.nimbus.service.message.MessageRecordFactoryService MessageRecordFactoryService}を設定する。<p>
     *
     * @param message MessageRecordFactoryServiceオブジェクト
     */
    protected void setDefaultMessageRecordFactoryService(
        MessageRecordFactoryService message
    ){
        defaultMessageFactory = message;
    }
    
    // LogServiceMBeanのJavaDoc
    public void setContextServiceName(ServiceName name){
        contextServiceName = name;
    }
    
    // LogServiceMBeanのJavaDoc
    public void setContextService(Context context){
        this.context = context;
    }
    
    // LogServiceMBeanのJavaDoc
    public ServiceName getContextServiceName(){
        return contextServiceName;
    }
    
    // LogServiceMBeanのJavaDoc
    public Context getContextService(){
        return context;
    }
    
    // LogServiceMBeanのJavaDoc
    public void setContextFormatKeys(String[] keys){
        if(keys != null){
            for(int i = 0; i < keys.length; i++){
                if(keys[i] != null){
                    contextKeys.add(keys[i]);
                }
            }
        }
    }
    
    // LogServiceMBeanのJavaDoc
    public void addContextFormatKey(String key){
        if(key != null){
            contextKeys.add(key);
        }
    }
    
    // LogServiceMBeanのJavaDoc
    public void removeContextFormatKey(String key){
        if(key != null){
            contextKeys.remove(key);
        }
    }
    
    // LogServiceMBeanのJavaDoc
    public void clearContextFormatKeys(){
        contextKeys.clear();
    }
    
    // LogServiceMBeanのJavaDoc
    public String[] getContextFormatKeys(){
        return (String[])contextKeys.toArray(new String[contextKeys.size()]);
    }
    
    protected String getDefaultFormat(){
        return defaultFormat;
    }
    
    /**
     * 指定された文字列をキーに{@link Context}サービスから値を取得する。<p>
     *
     * @param key キー
     * @return {@link Context}サービスから取得した値
     */
    protected Object getContextFormatValue(String key){
        final Context context = getContextService();
        if(context != null){
            return context.get(key);
        }
        return null;
    }
    
    /**
     * 指定された{@link LogMessageRecord}が出力されるか判定する。<p>
     *
     * @param messageRecord LogMessageRecord
     * @return 出力される場合 true
     */
    protected boolean isWrite(LogMessageRecord messageRecord){
        final int priority = messageRecord.getPriority();
        final Iterator categoryNames = messageRecord.getCategories().iterator();
        while(categoryNames.hasNext()){
            final String categoryName = (String)categoryNames.next();
            final LogCategory category = getCategoryService(categoryName);
            if(category == null){
                continue;
            }
            if(category.isEnabled() && category.isValidPriorityRange(priority)){
                return true;
            }
        }
        return false;
    }
    
    /**
     * ログのキューに挿入する{@link LogEnqueuedRecord}を生成する。<p>
     * {@link #write(LogMessageRecord, Locale, String, Throwable)}から呼び出される。<br>
     *
     * @param messageRecord ログ出力要求のあったLogMessageRecordオブジェクト
     * @param locale ログ出力に使用されるメッセージのロケール
     * @param embed ログ出力のメッセージに使用される埋め込み文字列。埋め込みのないメッセージの場合は、null。
     * @param throwable ログ出力に使用される例外
     * @return ログのキューに挿入するLogEnqueuedRecord
     */
    protected LogEnqueuedRecord createLogEnqueuedRecord(
        LogMessageRecord messageRecord,
        Locale locale,
        String embed,
        Throwable throwable
    ){
        return new LogEnqueuedRecord(messageRecord, locale, embed, throwable);
    }
    
    /**
     * ログのキューに挿入する{@link LogEnqueuedRecord}を生成する。<p>
     * {@link #write(LogMessageRecord, Locale, String[], Throwable)}から呼び出される。<br>
     *
     * @param messageRecord ログ出力要求のあったLogMessageRecordオブジェクト
     * @param locale ログ出力に使用されるメッセージのロケール
     * @param embeds ログ出力のメッセージに使用される埋め込み文字列。埋め込みのないメッセージの場合は、null。
     * @param throwable ログ出力に使用される例外。例外メッセージでない場合は、null。
     * @return ログのキューに挿入するLogEnqueuedRecord
     */
    protected LogEnqueuedRecord createLogEnqueuedRecord(
        LogMessageRecord messageRecord,
        Locale locale,
        String[] embeds,
        Throwable throwable
    ){
        return new LogEnqueuedRecord(messageRecord, locale, embeds, throwable);
    }
    
    /**
     * デバッグログ用の{@link LogMessageRecord}を生成する。<p>
     * {@link #debug(Object)}、{@link #debug(Object, Throwable)}から呼び出され、{@link MessageRecordFactory}に定義されていないメッセージ用のLogMessageRecordを生成する。<br>
     *
     * @param category カテゴリ名
     * @param priority 優先順位
     * @param message メッセージ
     * @return デバッグログ用のLogMessageRecord
     */
    protected LogMessageRecord createDebugLogMessageRecord(
        String category,
        int priority,
        Object message
    ){
        final LogMessageRecordImpl record = new LogMessageRecordImpl();
        record.addCategory(category);
        record.setPriority(priority);
        record.setMessageCode(EMPTY_STRING);
        record.addMessage(message != null ? message.toString() : null);
        record.setFactory(getMessageRecordFactoryService());
        return record;
    }
    
    /**
     * ログのキューに挿入する{@link LogEnqueuedRecord}を生成してキューに挿入する。<p>
     * LogEnqueuedRecordの生成は、{@link #createLogEnqueuedRecord(LogMessageRecord, Locale, String, Throwable)}を呼び出して行う。<br>
     * キューへの挿入は、{@link #enqueue(LogEnqueuedRecord)}を呼び出して行う。<br>
     *
     * @param messageRecord 出力するLogMessageRecord
     * @param locale ログ出力に使用されるメッセージのロケール
     * @param embed ログ出力のメッセージに使用される埋め込み文字列。埋め込みのないメッセージの場合は、null。
     * @param throwable ログ出力に使用される例外。例外メッセージでない場合は、null。
     */
    protected void write(
        LogMessageRecord messageRecord,
        Locale locale,
        String embed,
        Throwable throwable
    ){
        if(getState() != STARTED){
            return;
        }
        final LogEnqueuedRecord enqueuedRecord = createLogEnqueuedRecord(
            messageRecord,
            locale,
            embed,
            throwable
        );
        enqueue(enqueuedRecord);
    }
    
    /**
     * ログのキューに挿入する{@link LogEnqueuedRecord}を生成してキューに挿入する。<p>
     * LogEnqueuedRecordの生成は、{@link #createLogEnqueuedRecord(LogMessageRecord, Locale, String, Throwable)}を呼び出して行う。<br>
     * キューへの挿入は、{@link #enqueue(LogEnqueuedRecord)}を呼び出して行う。<br>
     *
     * @param messageRecord 出力するLogMessageRecord
     * @param locale ログ出力に使用されるメッセージのロケール
     * @param embeds ログ出力のメッセージに使用される埋め込み文字列。埋め込みのないメッセージの場合は、null。
     * @param throwable ログ出力に使用される例外。例外メッセージでない場合は、null。
     */
    protected void write(
        LogMessageRecord messageRecord,
        Locale locale,
        String[] embeds,
        Throwable throwable
    ){
        final LogEnqueuedRecord enqueuedRecord = createLogEnqueuedRecord(
            messageRecord,
            locale,
            embeds,
            throwable
        );
        enqueue(enqueuedRecord);
    }
    
    /**
     * ログのキューに挿入する前処理を行う。<p>
     * {@link #FORMAT_DATE_KEY}に対応する{@link Date}オブジェクトを生成して、{@link LogEnqueuedRecord#addWritableElement(Object, Object)}で{@link jp.ossc.nimbus.service.writer.WritableElement WritableElement}として追加する。<br>
     * また、{@link #setContextFormatKeys(String[])}で設定されたキーを使って、{@link #setContextServiceName(ServiceName)}で指定された{@link Context}サービスからオブジェクトを取得する。そのオブジェクトを、コンテキストフォーマット情報として{@link LogEnqueuedRecord#addWritableElement(Object, Object)}で{@link jp.ossc.nimbus.service.writer.WritableElement WritableElement}として追加する。<br>
     *
     * @param enqueuedRecord キューに挿入するLogEnqueuedRecord
     */
    protected void preEnqueue(LogEnqueuedRecord enqueuedRecord){
        enqueuedRecord.addWritableElement(
            FORMAT_DATE_KEY,
            new Date()
        );
        final String[] keys = getContextFormatKeys();
        if(keys != null){
            for(int i = 0; i < keys.length; i++){
                if(keys[i] != null){
                    final Object val = getContextFormatValue(keys[i]);
                    if(val != null){
                        enqueuedRecord.addWritableElement(
                            keys[i],
                            val
                        );
                    }
                }
            }
        }
    }
    
    /**
     * ログのキューに挿入する。<p>
     * キュー挿入前に、{@link #preEnqueue(LogEnqueuedRecord)}を呼び出す。<br>
     * 
     * @param enqueuedRecord LogEnqueuedRecordオブジェクト
     */
    protected void enqueue(LogEnqueuedRecord enqueuedRecord){
        preEnqueue(enqueuedRecord);
        queue.push(enqueuedRecord);
    }
    
    /**
     * ログのキュー取り出し後の処理を行う。<p>
     * {@link LogMessageRecord#makeMessage(Locale, Object[])}で出力メッセージを生成する。生成したメッセージを{@link #FORMAT_MESSAGE_KEY}に対応するメッセージとして、{@link LogEnqueuedRecord#addWritableElement(Object, Object)}で{@link jp.ossc.nimbus.service.writer.WritableElement WritableElement}として追加する。<br>
     * また、{@link #FORMAT_CODE_KEY}に対応するメッセージコードを取得して、{@link LogEnqueuedRecord#addWritableElement(Object, Object)}で{@link jp.ossc.nimbus.service.writer.WritableElement WritableElement}として追加する。<br>
     *
     * @param dequeuedRecord LogEnqueuedRecordオブジェクト
     */
    protected void postDequeue(LogEnqueuedRecord dequeuedRecord){
        final LogMessageRecord messageRecord
             = dequeuedRecord.getLogMessageRecord();
        final Locale locale = dequeuedRecord.getLocale();
        final String[] embeds = dequeuedRecord.getEmbedStringArray();
        final Throwable throwable = dequeuedRecord.getThrowable();
        String message = messageRecord.makeMessage(locale, embeds);
        if(throwable != null && messageRecord.isPrintStackTrace()){
            final StringBuilder buf = new StringBuilder(message);
            buf.append(LINE_SEP);
            buf.append(getStackTraceString(throwable));
            message = buf.toString();
        }
        dequeuedRecord.addWritableElement(
            FORMAT_CODE_KEY,
            messageRecord.getMessageCode()
        );
        dequeuedRecord.addWritableElement(
            FORMAT_MESSAGE_KEY,
            message
        );
    }
    
    /**
     * 指定された{@link LogMessageRecord}が出力される{@link LogCategory}を取得する。<p>
     *
     * @param messageRecord LogMessageRecord
     * @return 出力されるLogCategoryの配列
     */
    protected LogCategory[] getWriteCategories(LogMessageRecord messageRecord){
        final List result = new ArrayList();
        final int priority = messageRecord.getPriority();
        final Iterator categoryNames = messageRecord.getCategories().iterator();
        while(categoryNames.hasNext()){
            final String categoryName = (String)categoryNames.next();
            final LogCategory category = getCategoryService(categoryName);
            if(category != null && category.isEnabled()
                && category.isValidPriorityRange(priority)){
                result.add(category);
            }
        }
        return (LogCategory[])result.toArray(new LogCategory[result.size()]);
    }
    
    /**
     * キュー取り出し後に、カテゴリ毎の{@link WritableRecord}を生成する。<p>
     * {@link #dequeue(LogEnqueuedRecord)}から呼び出される。<br>
     *
     * @param dequeuedRecord キューから取り出したLogEnqueuedRecordオブジェクト
     * @param category LogCategoryオブジェクト
     */
    protected Map createWritableElementMap(
        LogEnqueuedRecord dequeuedRecord,
        LogCategory category
    ){
        final LogMessageRecord messageRecord
             = dequeuedRecord.getLogMessageRecord();
        dequeuedRecord.addWritableElement(
            FORMAT_PRIORITY_KEY,
            category.getLabel(messageRecord.getPriority())
        );
        dequeuedRecord.addWritableElement(
            FORMAT_CATEGORY_KEY,
            category.getCategoryName()
        );
        return dequeuedRecord.getWritableElements();
    }
    
    /**
     * キューから取り出された{@link LogEnqueuedRecord}からカテゴリ毎に{@link WritableRecord}を生成して{@link MessageWriter}に出力を依頼する。<p>
     * {@link #postDequeue(LogEnqueuedRecord)}を呼び出して、キュー取り出し後の処理を行う。<br>
     * また、{@link #getWriteCategories(LogMessageRecord)}で出力すべき{@link LogCategory}を取得して、カテゴリ毎に{@link #createWritableElementMap(LogEnqueuedRecord, LogCategory)}でMapを生成する。そのMapをカテゴリの{@link LogCategory#write(int, Map)}を使って、出力を依頼する。<br>
     *
     * @param dequeuedRecord キューから取り出したLogEnqueuedRecordオブジェクト
     */
    protected void dequeue(LogEnqueuedRecord dequeuedRecord){
        postDequeue(dequeuedRecord);
        final LogMessageRecord messageRecord
             = dequeuedRecord.getLogMessageRecord();
        final LogCategory[] categories = getWriteCategories(messageRecord);
        for(int i = 0; i < categories.length; i++){
            final LogCategory category = categories[i];
            try{
                category.write(
                    messageRecord.getPriority(),
                    createWritableElementMap(dequeuedRecord, category)
                );
            }catch(MessageWriteException e){
                // 無視する
            }
        }
    }
    
    /**
     * デーモンが開始した時に呼び出される。<p>
     * 
     * @return 常にtrueを返す
     */
    public boolean onStart() {
        return true;
    }
    
    /**
     * デーモンが停止した時に呼び出される。<p>
     * 
     * @return 常にtrueを返す
     */
    public boolean onStop() {
        return true;
    }
    
    /**
     * デーモンが中断した時に呼び出される。<p>
     * 
     * @return 常にtrueを返す
     */
    public boolean onSuspend() {
        return true;
    }
    
    /**
     * デーモンが再開した時に呼び出される。<p>
     * 
     * @return 常にtrueを返す
     */
    public boolean onResume() {
        return true;
    }
    
    /**
     * キューから１つ取り出して返す。<p>
     * 
     * @param ctrl DaemonControlオブジェクト
     * @return {@link LogEnqueuedRecord}オブジェクト
     */
    public Object provide(DaemonControl ctrl){
        return queue.get(5000);
    }
    
    /**
     * 引数dequeuedで渡されたオブジェクトを消費する。<p>
     * 引数dequeuedで渡されたオブジェクトを{@link LogEnqueuedRecord}にキャストして{@link #dequeue(LogEnqueuedRecord)}を呼び出す。<br>
     *
     * @param dequeued キューから取り出されたオブジェクト
     * @param ctrl DaemonControlオブジェクト
     */
    public void consume(Object dequeued, DaemonControl ctrl){
        if(dequeued == null){
            return;
        }
        try{
            dequeue((LogEnqueuedRecord)dequeued);
        }catch(Throwable e){
            e.printStackTrace();
        }
    }
    
    /**
     * キューの中身を吐き出す。<p>
     */
    public void garbage(){
        if(queue != null){
            while(queue.size() > 0){
                consume(queue.get(0), daemon);
            }
        }
    }
    
    // LoggerのJavaDoc
    public void debug(Object msg){
        final LogMessageRecord messageRecord = createDebugLogMessageRecord(
            DEBUG_METHOD_CATEGORY,
            DEBUG_METHOD_CATEGORY_PRIORITY_MIN,
            msg
        );
        if(!isWrite(messageRecord)){
            return;
        }
        write(messageRecord, null, (String)null, null);
    }
    
    // LoggerのJavaDoc
    public void debug(Object msg, Throwable oException){
        final LogMessageRecord messageRecord = createDebugLogMessageRecord(
            DEBUG_METHOD_CATEGORY,
            DEBUG_METHOD_CATEGORY_PRIORITY_MIN,
            msg
        );
        if(!isWrite(messageRecord)){
            return;
        }
        write(messageRecord, null, (String)null, oException);
    }
    
    // LoggerのJavaDoc
    public void write(String logCode, Object embed){
        write(Locale.getDefault(), logCode, embed);
    }
    
    // LoggerのJavaDoc
    public void write(String logCode, byte embed){
        write(Locale.getDefault(), logCode, embed);
    }
    
    // LoggerのJavaDoc
    public void write(String logCode, short embed){
        write(Locale.getDefault(), logCode, embed);
    }
    
    // LoggerのJavaDoc
    public void write(String logCode, char embed){
        write(Locale.getDefault(), logCode, embed);
    }
    
    // LoggerのJavaDoc
    public void write(String logCode, int embed){
        write(Locale.getDefault(), logCode, embed);
    }
    
    // LoggerのJavaDoc
    public void write(String logCode, long embed){
        write(Locale.getDefault(), logCode, embed);
    }
    
    // LoggerのJavaDoc
    public void write(String logCode, float embed){
        write(Locale.getDefault(), logCode, embed);
    }
    
    // LoggerのJavaDoc
    public void write(String logCode, double embed){
        write(Locale.getDefault(), logCode, embed);
    }
    
    // LoggerのJavaDoc
    public void write(String logCode, boolean embed){
        write(Locale.getDefault(), logCode, embed);
    }
    
    // LoggerのJavaDoc
    public void write(Locale lo, String logCode, Object embed){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(
            messageRecord,
            lo,
            embed != null ? embed.toString() : (String)null,
            null
        );
    }
    
    // LoggerのJavaDoc
    public void write(Locale lo, String logCode, byte embed){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, Byte.toString(embed), null);
    }
    
    // LoggerのJavaDoc
    public void write(Locale lo, String logCode, short embed){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, Short.toString(embed), null);
    }
    
    // LoggerのJavaDoc
    public void write(Locale lo, String logCode, char embed){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, new Character(embed).toString(), null);
    }
    
    // LoggerのJavaDoc
    public void write(Locale lo, String logCode, int embed){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, Integer.toString(embed), null);
    }
    
    // LoggerのJavaDoc
    public void write(Locale lo, String logCode, long embed){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, Long.toString(embed), null);
    }
    
    // LoggerのJavaDoc
    public void write(Locale lo, String logCode, float embed){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, Float.toString(embed), null);
    }
    
    // LoggerのJavaDoc
    public void write(Locale lo, String logCode, double embed){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, Double.toString(embed), null);
    }
    
    // LoggerのJavaDoc
    public void write(Locale lo, String logCode, boolean embed){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, Boolean.toString(embed), null);
    }
    
    // LoggerのJavaDoc
    public void write(String logCode, Object[] embeds) {
        write(Locale.getDefault(), logCode, embeds);
    }
    
    // LoggerのJavaDoc
    public void write(String logCode, byte[] embeds){
        write(Locale.getDefault(), logCode, embeds);
    }
    
    // LoggerのJavaDoc
    public void write(String logCode, short[] embeds){
        write(Locale.getDefault(), logCode, embeds);
    }
    
    // LoggerのJavaDoc
    public void write(String logCode, char[] embeds){
        write(Locale.getDefault(), logCode, embeds);
    }
    
    // LoggerのJavaDoc
    public void write(String logCode, int[] embeds){
        write(Locale.getDefault(), logCode, embeds);
    }
    
    // LoggerのJavaDoc
    public void write(String logCode, long[] embeds){
        write(Locale.getDefault(), logCode, embeds);
    }
    
    // LoggerのJavaDoc
    public void write(String logCode, float[] embeds){
        write(Locale.getDefault(), logCode, embeds);
    }
    
    // LoggerのJavaDoc
    public void write(String logCode, double[] embeds){
        write(Locale.getDefault(), logCode, embeds);
    }
    
    // LoggerのJavaDoc
    public void write(String logCode, boolean[] embeds){
        write(Locale.getDefault(), logCode, embeds);
    }
    
    /**
     * Object配列をString配列に変換する。<p>
     *
     * @param vals Object配列
     * @return String配列
     */
    protected static String[] convertStringArray(Object[] vals){
        String[] strings = null;
        if(vals != null){
            strings = new String[vals.length];
            for(int i = 0; i < vals.length; i++){
                if(vals[i] != null){
                    strings[i] = vals[i].toString();
                }
            }
        }
        return strings;
    }
    
    /**
     * byte配列をString配列に変換する。<p>
     *
     * @param vals byte配列
     * @return String配列
     */
    protected static String[] convertStringArray(byte[] vals){
        String[] strings = null;
        if(vals != null){
            strings = new String[vals.length];
            for(int i = 0; i < vals.length; i++){
                strings[i] = Byte.toString(vals[i]);
            }
        }
        return strings;
    }
    
    /**
     * short配列をString配列に変換する。<p>
     *
     * @param vals short配列
     * @return String配列
     */
    protected static String[] convertStringArray(short[] vals){
        String[] strings = null;
        if(vals != null){
            strings = new String[vals.length];
            for(int i = 0; i < vals.length; i++){
                strings[i] = Short.toString(vals[i]);
            }
        }
        return strings;
    }
    
    /**
     * char配列をString配列に変換する。<p>
     *
     * @param vals char配列
     * @return String配列
     */
    protected static String[] convertStringArray(char[] vals){
        String[] strings = null;
        if(vals != null){
            strings = new String[vals.length];
            for(int i = 0; i < vals.length; i++){
                strings[i] = new Character(vals[i]).toString();
            }
        }
        return strings;
    }
    
    /**
     * int配列をString配列に変換する。<p>
     *
     * @param vals int配列
     * @return String配列
     */
    protected static String[] convertStringArray(int[] vals){
        String[] strings = null;
        if(vals != null){
            strings = new String[vals.length];
            for(int i = 0; i < vals.length; i++){
                strings[i] = Integer.toString(vals[i]);
            }
        }
        return strings;
    }
    
    /**
     * long配列をString配列に変換する。<p>
     *
     * @param vals long配列
     * @return String配列
     */
    protected static String[] convertStringArray(long[] vals){
        String[] strings = null;
        if(vals != null){
            strings = new String[vals.length];
            for(int i = 0; i < vals.length; i++){
                strings[i] = Long.toString(vals[i]);
            }
        }
        return strings;
    }
    
    /**
     * float配列をString配列に変換する。<p>
     *
     * @param vals float配列
     * @return String配列
     */
    protected static String[] convertStringArray(float[] vals){
        String[] strings = null;
        if(vals != null){
            strings = new String[vals.length];
            for(int i = 0; i < vals.length; i++){
                strings[i] = Float.toString(vals[i]);
            }
        }
        return strings;
    }
    
    /**
     * double配列をString配列に変換する。<p>
     *
     * @param vals double配列
     * @return String配列
     */
    protected static String[] convertStringArray(double[] vals){
        String[] strings = null;
        if(vals != null){
            strings = new String[vals.length];
            for(int i = 0; i < vals.length; i++){
                strings[i] = Double.toString(vals[i]);
            }
        }
        return strings;
    }
    
    /**
     * boolean配列をString配列に変換する。<p>
     *
     * @param vals boolean配列
     * @return String配列
     */
    protected static String[] convertStringArray(boolean[] vals){
        String[] strings = null;
        if(vals != null){
            strings = new String[vals.length];
            for(int i = 0; i < vals.length; i++){
                strings[i] = Boolean.toString(vals[i]);
            }
        }
        return strings;
    }
    
    // LoggerのJavaDoc
    public void write(Locale lo, String logCode, Object[] embeds){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, convertStringArray(embeds), null);
    }
    
    // LoggerのJavaDoc
    public void write(Locale lo, String logCode, byte[] embeds){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, convertStringArray(embeds), null);
    }
    
    // LoggerのJavaDoc
    public void write(Locale lo,String logCode,short[] embeds){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, convertStringArray(embeds), null);
    }
    
    // LoggerのJavaDoc
    public void write(Locale lo,String logCode,char[] embeds){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, convertStringArray(embeds), null);
    }
    
    // LoggerのJavaDoc
    public void write(Locale lo,String logCode,int[] embeds){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, convertStringArray(embeds), null);
    }
    
    // LoggerのJavaDoc
    public void write(Locale lo,String logCode,long[] embeds){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, convertStringArray(embeds), null);
    }
    
    // LoggerのJavaDoc
    public void write(Locale lo,String logCode,float[] embeds){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, convertStringArray(embeds), null);
    }
    
    // LoggerのJavaDoc
    public void write(Locale lo, String logCode, double[] embeds){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, convertStringArray(embeds), null);
    }
    
    // LoggerのJavaDoc
    public void write(Locale lo, String logCode, boolean[] embeds){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, convertStringArray(embeds), null);
    }
    
    // LoggerのJavaDoc
    public void write(String logCode){
        write(Locale.getDefault(), logCode);
    }
    
    // LoggerのJavaDoc
    public void write(Locale lo, String logCode){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, (String)null, null);
    }
    
    // LoggerのJavaDoc
    public void write(String logCode, Throwable oException) {
        write(Locale.getDefault(), logCode, oException);
    }
    
    // LoggerのJavaDoc
    public void write(Locale lo, String logCode, Throwable oException){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, (String)null, oException);
    }
    
    // LoggerのJavaDoc
    public void write(String logCode, Object embed, Throwable oException){
        write(Locale.getDefault(), logCode, embed, oException);
    }
    
    // LoggerのJavaDoc
    public void write(String logCode, byte embed, Throwable oException){
        write(Locale.getDefault(), logCode, embed, oException);
    }
    
    // LoggerのJavaDoc
    public void write(String logCode, short embed, Throwable oException){
        write(Locale.getDefault(), logCode, embed, oException);
    }
    
    // LoggerのJavaDoc
    public void write(String logCode, char embed, Throwable oException){
        write(Locale.getDefault(), logCode, embed, oException);
    }
    
    // LoggerのJavaDoc
    public void write(String logCode, int embed, Throwable oException){
        write(Locale.getDefault(), logCode, embed, oException);
    }
    
    // LoggerのJavaDoc
    public void write(String logCode, long embed, Throwable oException){
        write(Locale.getDefault(), logCode, embed, oException);
    }
    
    // LoggerのJavaDoc
    public void write(String logCode, float embed, Throwable oException){
        write(Locale.getDefault(), logCode, embed, oException);
    }
    
    // LoggerのJavaDoc
    public void write(String logCode, double embed, Throwable oException){
        write(Locale.getDefault(), logCode, embed, oException);
    }
    
    // LoggerのJavaDoc
    public void write(String logCode, boolean embed, Throwable oException){
        write(Locale.getDefault(), logCode, embed, oException);
    }
    
    // LoggerのJavaDoc
    public void write(
        Locale lo,
        String logCode,
        Object embed,
        Throwable oException
    ){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(
            messageRecord,
            lo,
            embed != null ? embed.toString() : (String)null,
            oException
        );
    }
    
    // LoggerのJavaDoc
    public void write(
        Locale lo,
        String logCode,
        byte embed,
        Throwable oException
    ){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, Byte.toString(embed), oException);
    }
    
    // LoggerのJavaDoc
    public void write(
        Locale lo,
        String logCode,
        short embed,
        Throwable oException
    ){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, Short.toString(embed), oException);
    }
    
    // LoggerのJavaDoc
    public void write(
        Locale lo,
        String logCode,
        char embed,
        Throwable oException
    ){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, new Character(embed).toString(), oException);
    }
    
    // LoggerのJavaDoc
    public void write(
        Locale lo,
        String logCode,
        int embed,
        Throwable oException
    ){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, Integer.toString(embed), oException);
    }
    
    // LoggerのJavaDoc
    public void write(
        Locale lo,
        String logCode,
        long embed,
        Throwable oException
    ){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, Long.toString(embed), oException);
    }
    
    // LoggerのJavaDoc
    public void write(
        Locale lo,
        String logCode,
        float embed,
        Throwable oException
    ){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, Float.toString(embed), oException);
    }
    
    // LoggerのJavaDoc
    public void write(
        Locale lo,
        String logCode,
        double embed,
        Throwable oException
    ){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, Double.toString(embed), oException);
    }
    
    // LoggerのJavaDoc
    public void write(
        Locale lo,
        String logCode,
        boolean embed,
        Throwable oException
    ){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, Boolean.toString(embed), oException);
    }
    
    // LoggerのJavaDoc
    public void write(String logCode, Object[] embeds, Throwable oException) {
        write(Locale.getDefault(), logCode, embeds, oException);
    }
    
    // LoggerのJavaDoc
    public void write(String logCode, byte[] embeds, Throwable oException){
        write(Locale.getDefault(), logCode, embeds, oException);
    }
    
    // LoggerのJavaDoc
    public void write(String logCode, short[] embeds, Throwable oException){
        write(Locale.getDefault(), logCode, embeds, oException);
    }
    
    // LoggerのJavaDoc
    public void write(String logCode, char[] embeds, Throwable oException){
        write(Locale.getDefault(), logCode, embeds, oException);
    }
    
    // LoggerのJavaDoc
    public void write(String logCode, int[] embeds, Throwable oException){
        write(Locale.getDefault(), logCode, embeds, oException);
    }
    
    // LoggerのJavaDoc
    public void write(String logCode, long[] embeds, Throwable oException){
        write(Locale.getDefault(), logCode, embeds, oException);
    }
    
    // LoggerのJavaDoc
    public void write(String logCode, float[] embeds, Throwable oException){
        write(Locale.getDefault(), logCode, embeds, oException);
    }
    
    // LoggerのJavaDoc
    public void write(String logCode, double[] embeds, Throwable oException){
        write(Locale.getDefault(), logCode, embeds, oException);
    }
    
    // LoggerのJavaDoc
    public void write(String logCode, boolean[] embeds, Throwable oException){
        write(Locale.getDefault(), logCode, embeds, oException);
    }
    
    // LoggerのJavaDoc
    public void write(
        Locale lo,
        String logCode,
        Object[] embeds,
        Throwable oException
    ){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, convertStringArray(embeds), oException);
    }
    
    // LoggerのJavaDoc
    public void write(
        Locale lo,
        String logCode,
        byte[] embeds,
        Throwable oException
    ){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, convertStringArray(embeds), oException);
    }
    
    // LoggerのJavaDoc
    public void write(
        Locale lo,
        String logCode,
        short[] embeds,
        Throwable oException
    ){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, convertStringArray(embeds), oException);
    }
    
    // LoggerのJavaDoc
    public void write(
        Locale lo,
        String logCode,
        char[] embeds,
        Throwable oException
    ){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, convertStringArray(embeds), oException);
    }
    
    // LoggerのJavaDoc
    public void write(
        Locale lo,
        String logCode,
        int[] embeds,
        Throwable oException
    ){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, convertStringArray(embeds), oException);
    }
    
    // LoggerのJavaDoc
    public void write(
        Locale lo,
        String logCode,
        long[] embeds,
        Throwable oException
    ){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, convertStringArray(embeds), oException);
    }
    
    // LoggerのJavaDoc
    public void write(
        Locale lo,
        String logCode,
        float[] embeds,
        Throwable oException
    ){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, convertStringArray(embeds), oException);
    }
    
    // LoggerのJavaDoc
    public void write(
        Locale lo,
        String logCode,
        double[] embeds,
        Throwable oException
    ){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, convertStringArray(embeds), oException);
    }
    
    // LoggerのJavaDoc
    public void write(
        Locale lo,
        String logCode,
        boolean[] embeds,
        Throwable oException
    ){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, convertStringArray(embeds), oException);
    }
    
    // LoggerのJavaDoc
    public void write(AppException e){
        write(Locale.getDefault(), e);
    }
    
    // LoggerのJavaDoc
    public void write(Locale lo, AppException e) {
        final MessageRecord  tmp = (MessageRecord)e.getMessageRecord();
        LogMessageRecord  messageRecord = null;
        if(tmp instanceof LogMessageRecord){
            messageRecord = (LogMessageRecord)tmp;
        }else{
            // TODO どうする？
        }
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, (String)null, e);
    }
    
    // LoggerのJavaDoc
    public boolean isWrite(String logCode){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return false;
        }
        return true;
    }
    
    // LoggerのJavaDoc
    public boolean isDebugWrite(){
        return isDebugEnabled;
    }
    
    /**
     * 例外のスタックトレース文字列を取得する。<p>
     *
     * @param e 例外
     * @return スタックトレース文字列
     */
    protected static String getStackTraceString(Throwable e){
        final StringBuilder buf = new StringBuilder();
        buf.append(e).append(LINE_SEP);
        final StackTraceElement[] elemss = e.getStackTrace();
        if(elemss != null){
            for(int i = 0, max = elemss.length; i < max; i++){
                buf.append('\t');
                buf.append(elemss[i]);
                if(i != max - 1){
                    buf.append(LINE_SEP);
                }
            }
        }
        for(Throwable ee = getCause(e); ee != null; ee = getCause(ee)){
            buf.append(LINE_SEP).append(MSG_CAUSE)
                .append(ee).append(LINE_SEP);
            final StackTraceElement[] elems = ee.getStackTrace();
            if(elems != null){
                for(int i = 0, max = elems.length; i < max; i++){
                    buf.append('\t');
                    buf.append(elems[i]);
                    if(i != max - 1){
                        buf.append(LINE_SEP);
                    }
                }
            }
        }
        return buf.toString();
    }
    
    private static Throwable getCause(Throwable th){
        Throwable cause = null;
        String thClassName = th.getClass().getName();
        if(thClassName.equals(SERVLET_EXCEPTION_NAME)){
            // 例外がServletExceptionの場合は、ルートの原因を取得
            try{
                cause = (Throwable)th.getClass()
                    .getMethod(GET_ROOT_CAUSE_METHOD, (Class[])null).invoke(th, (Object[])null);
            }catch(NoSuchMethodException e){
            }catch(IllegalAccessException e){
            }catch(java.lang.reflect.InvocationTargetException e){
            }
        }else if(thClassName.equals(JMS_EXCEPTION_NAME)){
            // 例外がJMSExceptionの場合は、リンク例外を取得
            try{
                cause = (Exception)th.getClass()
                    .getMethod(GET_LINKED_EXCEPTION_METHOD, (Class[])null).invoke(th, (Object[])null);
            }catch(NoSuchMethodException e){
            }catch(IllegalAccessException e){
            }catch(java.lang.reflect.InvocationTargetException e){
            }
        }else{
            cause = th.getCause();
        }
        return cause == th ? null : cause;
    }
    
    /**
     * 指定カテゴリの優先順位範囲の有効/無効を設定する。<p>
     *
     * @param categoryName カテゴリ名
     * @param isEnabled 有効にする場合 true
     */
    protected void setEnabled(
        String categoryName,
        boolean isEnabled
    ){
        final LogCategory category = getCategoryService(categoryName);
        if(category == null){
            return;
        }
        category.setEnabled(isEnabled);
    }
    
    /**
     * 指定されたカテゴリの優先順位範囲が有効か無効かを調べる。<p>
     *
     * @param categoryName カテゴリ名
     * @param defaultEnabled カテゴリが存在しない場合の戻り値
     */
    protected boolean isEnabled(
        String categoryName,
        boolean defaultEnabled
    ){
        final LogCategory category = getCategoryService(categoryName);
        if(category == null){
            return defaultEnabled;
        }
        return category.isEnabled();
    }
    
    // LogServiceMBeanのJavaDoc
    public void setDebugEnabled(boolean isEnabled){
        isDebugEnabled = isEnabled;
        setEnabled(
            DEBUG_METHOD_CATEGORY,
            isEnabled
        );
    }
    
    // LogServiceMBeanのJavaDoc
    public boolean isDebugEnabled(){
        return isEnabled(
            DEBUG_METHOD_CATEGORY,
            isDebugEnabled
        );
    }
    
    // LogServiceMBeanのJavaDoc
    public void setSystemDebugEnabled(boolean isEnabled){
        isSystemDebugEnabled = isEnabled;
        setEnabled(
            SYSTEM_DEBUG_CATEGORY,
            isEnabled
        );
    }
    
    // LogServiceMBeanのJavaDoc
    public boolean isSystemDebugEnabled(){
        return isEnabled(
            SYSTEM_DEBUG_CATEGORY,
            isSystemDebugEnabled
        );
    }
    
    // LogServiceMBeanのJavaDoc
    public void setSystemInfoEnabled(boolean isEnabled){
        isSystemInfoEnabled = isEnabled;
        setEnabled(
            SYSTEM_INFO_CATEGORY,
            isEnabled
        );
    }
    
    // LogServiceMBeanのJavaDoc
    public boolean isSystemInfoEnabled(){
        return isEnabled(
            SYSTEM_INFO_CATEGORY,
            isSystemInfoEnabled
        );
    }
    
    // LogServiceMBeanのJavaDoc
    public void setSystemWarnEnabled(boolean isEnabled){
        isSystemWarnEnabled = isEnabled;
        setEnabled(
            SYSTEM_WARN_CATEGORY,
            isEnabled
        );
    }
    
    // LogServiceMBeanのJavaDoc
    public boolean isSystemWarnEnabled(){
        return isEnabled(
            SYSTEM_WARN_CATEGORY,
            isSystemWarnEnabled
        );
    }
    
    // LogServiceMBeanのJavaDoc
    public void setSystemErrorEnabled(boolean isEnabled){
        isSystemErrorEnabled = isEnabled;
        setEnabled(
            SYSTEM_ERROR_CATEGORY,
            isEnabled
        );
    }
    
    // LogServiceMBeanのJavaDoc
    public boolean isSystemErrorEnabled(){
        return isEnabled(
            SYSTEM_ERROR_CATEGORY,
            isSystemErrorEnabled
        );
    }
    
    // LogServiceMBeanのJavaDoc
    public void setSystemFatalEnabled(boolean isEnabled){
        isSystemFatalEnabled = isEnabled;
        setEnabled(
            SYSTEM_FATAL_CATEGORY,
            isEnabled
        );
    }
    
    // LogServiceMBeanのJavaDoc
    public boolean isSystemFatalEnabled(){
        return isEnabled(
            SYSTEM_FATAL_CATEGORY,
            isSystemFatalEnabled
        );
    }
    
    // LogServiceMBeanのJavaDoc
    public void setDebugMessageWriterServiceName(ServiceName name){
        debugMessageWriterServiceName = name;
    }
    
    // LogServiceMBeanのJavaDoc
    public ServiceName getDebugMessageWriterServiceName(){
        return debugMessageWriterServiceName;
    }
    
    // LogServiceMBeanのJavaDoc
    public void setSystemDebugMessageWriterServiceName(ServiceName name){
        systemDebugMessageWriterServiceName = name;
    }
    
    // LogServiceMBeanのJavaDoc
    public ServiceName getSystemDebugMessageWriterServiceName(){
        return systemDebugMessageWriterServiceName;
    }
    
    // LogServiceMBeanのJavaDoc
    public void setSystemInfoMessageWriterServiceName(ServiceName name){
        systemInfoMessageWriterServiceName = name;
    }
    
    // LogServiceMBeanのJavaDoc
    public ServiceName getSystemInfoMessageWriterServiceName(){
        return systemInfoMessageWriterServiceName;
    }
    
    // LogServiceMBeanのJavaDoc
    public void setSystemWarnMessageWriterServiceName(ServiceName name){
        systemWarnMessageWriterServiceName = name;
    }
    
    // LogServiceMBeanのJavaDoc
    public ServiceName getSystemWarnMessageWriterServiceName(){
        return systemWarnMessageWriterServiceName;
    }
    
    // LogServiceMBeanのJavaDoc
    public void setSystemErrorMessageWriterServiceName(ServiceName name){
        systemErrorMessageWriterServiceName = name;
    }
    
    // LogServiceMBeanのJavaDoc
    public ServiceName getSystemErrorMessageWriterServiceName(){
        return systemErrorMessageWriterServiceName;
    }
    
    // LogServiceMBeanのJavaDoc
    public void setSystemFatalMessageWriterServiceName(ServiceName name){
        systemFatalMessageWriterServiceName = name;
    }
    
    // LogServiceMBeanのJavaDoc
    public ServiceName getSystemFatalMessageWriterServiceName(){
        return systemFatalMessageWriterServiceName;
    }
    
    // LogServiceMBeanのJavaDoc
    public void setDebugWritableRecordFactoryServiceName(
        ServiceName name
    ){
        debugRecordFactoryServiceName = name;
    }
    
    // LogServiceMBeanのJavaDoc
    public ServiceName getDebugWritableRecordFactoryServiceName(){
        return debugRecordFactoryServiceName;
    }
    
    // LogServiceMBeanのJavaDoc
    public void setSystemDebugWritableRecordFactoryServiceName(
        ServiceName name
    ){
        systemDebugRecordFactoryServiceName = name;
    }
    
    // LogServiceMBeanのJavaDoc
    public ServiceName getSystemDebugWritableRecordFactoryServiceName(){
        return systemDebugRecordFactoryServiceName;
    }
    
    // LogServiceMBeanのJavaDoc
    public void setSystemInfoWritableRecordFactoryServiceName(ServiceName name){
        systemInfoRecordFactoryServiceName = name;
    }
    
    // LogServiceMBeanのJavaDoc
    public ServiceName getSystemInfoWritableRecordFactoryServiceName(){
        return systemInfoRecordFactoryServiceName;
    }
    
    // LogServiceMBeanのJavaDoc
    public void setSystemWarnWritableRecordFactoryServiceName(ServiceName name){
        systemWarnRecordFactoryServiceName = name;
    }
    
    // LogServiceMBeanのJavaDoc
    public ServiceName getSystemWarnWritableRecordFactoryServiceName(){
        return systemWarnRecordFactoryServiceName;
    }
    
    // LogServiceMBeanのJavaDoc
    public void setSystemErrorWritableRecordFactoryServiceName(
        ServiceName name
    ){
        systemErrorRecordFactoryServiceName = name;
    }
    
    // LogServiceMBeanのJavaDoc
    public ServiceName getSystemErrorWritableRecordFactoryServiceName(){
        return systemErrorRecordFactoryServiceName;
    }
    
    // LogServiceMBeanのJavaDoc
    public void setSystemFatalWritableRecordFactoryServiceName(
        ServiceName name
    ){
        systemFatalRecordFactoryServiceName = name;
    }
    
    // LogServiceMBeanのJavaDoc
    public ServiceName getSystemFatalWritableRecordFactoryServiceName(){
        return systemFatalRecordFactoryServiceName;
    }
    
    // LogServiceMBeanのJavaDoc
    public void setDaemon(boolean isDaemon){
        this.isDaemon = isDaemon;
    }
    // LogServiceMBeanのJavaDoc
    public boolean isDaemon(){
        return isDaemon;
    }
}
