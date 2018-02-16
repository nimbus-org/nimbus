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
package jp.ossc.nimbus.core;

import java.util.*;
import java.net.*;
import java.io.*;

import jp.ossc.nimbus.service.repository.*;
import jp.ossc.nimbus.service.log.*;
import jp.ossc.nimbus.service.message.*;
import jp.ossc.nimbus.util.WaitSynchronizeMonitor;

/**
 * サービス管理ファクトリ。<p>
 * {@link ServiceLoader}にサービス定義のロードを要求して、サービスを管理する
 * {@link ServiceManager}を生成するファクトリである。<br>
 * 生成したServiceManagerは、{@link Repository}で管理し、名前でアクセスできる。<br>
 * <p>
 * また、ServiceManagerに対して、その名前でServiceManagerの各操作にstaticに、
 * アクセスするラッパー的な機能も持つ。<br>
 *
 * @author M.Takata
 * @see <a href="ServiceManagerFactoryUsage.txt">サービス管理ファクトリコマンド使用方法</a>
 */
public class ServiceManagerFactory implements Serializable{
    
    private static final long serialVersionUID = -1120514470640321429L;
    
    private static final String USAGE_RESOURCE
         = "jp/ossc/nimbus/core/ServiceManagerFactoryUsage.txt";
    
    /**
     * デフォルトのログ出力を行う{@link LogService}オブジェクト。<p>
     */
    static final LogService DEFAULT_LOGGER;
    
    /**
     * デフォルトのメッセージ生成を行う{@link MessageRecordFactoryService}オブジェクト。<p>
     */
    static final MessageRecordFactoryService DEFAULT_MESSAGE;
    
    // メッセージID定義
    private static final String SVCMF = "SVCMF";
    private static final String SVCMF0 = SVCMF + 0;
    private static final String SVCMF00 = SVCMF0 + 0;
    private static final String SVCMF000 = SVCMF00 + 0;
    private static final String SVCMF0000 = SVCMF000 + 0;
    private static final String SVCMF00001 = SVCMF0000 + 1;
    private static final String SVCMF00002 = SVCMF0000 + 2;
    private static final String SVCMF00003 = SVCMF0000 + 3;
    private static final String SVCMF00004 = SVCMF0000 + 4;
    private static final String SVCMF00005 = SVCMF0000 + 5;
    private static final String SVCMF00006 = SVCMF0000 + 6;
    private static final String SVCMF00007 = SVCMF0000 + 7;
    private static final String SVCMF00008 = SVCMF0000 + 8;
    private static final String SVCMF00009 = SVCMF0000 + 9;
    private static final String SVCMF00010 = SVCMF000 + 10;
    private static final String SVCMF00011 = SVCMF000 + 11;
    private static final String SVCMF00012 = SVCMF000 + 12;
    private static final String SVCMF00013 = SVCMF000 + 13;
    private static final String SVCMF00014 = SVCMF000 + 14;
    private static final String SVCMF00015 = SVCMF000 + 15;
    private static final String SVCMF00016 = SVCMF000 + 16;
    private static final String SVCMF00017 = SVCMF000 + 17;
    private static final String SVCMF00018 = SVCMF000 + 18;
    private static final String SVCMF00019 = SVCMF000 + 19;
    private static final String SVCMF00020 = SVCMF000 + 20;
    private static final String SVCMF00021 = SVCMF000 + 21;
    private static final String SVCMF00022 = SVCMF000 + 22;
    private static final String SVCMF00023 = SVCMF000 + 23;
    private static final String SVCMF00024 = SVCMF000 + 24;
    private static final String SVCMF00025 = SVCMF000 + 25;
    private static final String SVCMF00026 = SVCMF000 + 26;
    private static final String SVCMF00027 = SVCMF000 + 27;
    private static final String SVCMF00028 = SVCMF000 + 28;
    private static final String SVCMF00029 = SVCMF000 + 29;
    private static final String SVCMF00030 = SVCMF000 + 30;
    private static final String SVCMF00031 = SVCMF000 + 31;
    
    /**
     * {@link ServiceLoader}の実装クラスを指定するシステムプロパティのキー。<p>
     */
    private static final String LOADER_IMPL_CLASS_KEY
         = "jp.ossc.nimbus.core.loader";
    
    /**
     * {@link ServiceManager}の実装クラスを指定するシステムプロパティのキー。<p>
     */
    private static final String MANAGER_IMPL_CLASS_KEY
         = "jp.ossc.nimbus.core.manager";
    
    /**
     * ログ出力を行う{@link Logger}のラッパーオブジェクト。<p>
     */
    private static LoggerWrapper logger;
    
    /**
     * メッセージ生成を行う{@link MessageRecordFactory}のラッパーオブジェクト。<p>
     */
    private static MessageRecordFactoryWrapper message;
    
    /**
     * 改行文字。<p>
     */
    private static final String LINE_SEPARAOTR
         = System.getProperty("line.separator");
    
    /**
     * 待機中のサービスの原因となるサービスを表示する際の接頭辞文字列。<p>
     */
    private static final String CAUSE_SERVICES = " causes ";
    
    /**
     * 起動に失敗したサービスの原因となる例外を表示する際の接頭辞文字列。<p>
     */
    private static final String CAUSE_THROWABLE = " cause ";
    
    /**
     * ServiceLoaderのデフォルト実装クラス。<p>
     */
    private static final Class DEFAULT_SERVICE_LOADER_CLASS
         = DefaultServiceLoaderService.class;
    
    /**
     * サービス定義をロードした{@link ServiceLoader}を管理するマップ。<p>
     * <table border="1">
     *   <tr bgcolor="#CCCCFF"><th colspan="2">キー</th><th colspan="2">値</th></tr>
     *   <tr bgcolor="#CCCCFF"><th>型</th><th>内容</th><th>型</th><th>内容</th></tr>
     *   <tr><td>java.net.URL</td><td>サービス定義のURL</td><td>ServiceLoader</td><td>キーのURLのサービス定義をロードした{@link ServiceLoader}</td></tr>
     * </table>
     */
    private static final Map loaders = Collections.synchronizedMap(new HashMap());
    
    /**
     * {@link ServiceManager}を管理する{@link Repository}。<p>
     * デフォルトでは、Map実装のRepositoty。<br>
     */
    private static Repository repository = new DefaultRepository();
    
    private static class DefaultRepository implements Repository{
        private final Map managerMap = new Hashtable();
        
        public Object get(String name){
            return (Service)managerMap.get(name);
        }
        
        public boolean register(String name, Object manager){
            if(managerMap.containsKey(name)){
                return false;
            }
            managerMap.put(name, manager);
            return true;
        }
        
        public boolean unregister(String name){
            managerMap.remove(name);
            return true;
        }
        
        public boolean isRegistered(String name){
            return managerMap.containsKey(name);
        }
        
        public Set nameSet(){
            return new HashSet(managerMap.keySet());
        }
        
        public Set registeredSet(){
            return new HashSet(managerMap.values());
        }
    };
    
    /**
     * このServiceManagerFactoryに登録された登録状態リスナのリスト。<p>
     */
    private static List registrationListeners = new ArrayList();
    
    /**
     * {@link ServiceLoader}実装クラス。<p>
     */
    private static Class loaderClass = DEFAULT_SERVICE_LOADER_CLASS;
    
    /**
     * {@link ServiceManager}実装クラス。<p>
     */
    private static Class managerClass;
    
    static{
        DEFAULT_LOGGER = new LogService();
        try{
            DEFAULT_LOGGER.create();
            DEFAULT_LOGGER.start();
            DEFAULT_LOGGER.setSystemDebugEnabled(false);
            DEFAULT_LOGGER.setSystemInfoEnabled(true);
            DEFAULT_LOGGER.setSystemWarnEnabled(true);
            DEFAULT_LOGGER.setSystemErrorEnabled(true);
            DEFAULT_LOGGER.setSystemFatalEnabled(true);
        }catch(Exception e){
            e.printStackTrace();
        }
        logger = new LoggerWrapper(DEFAULT_LOGGER);
        
        DEFAULT_MESSAGE = new MessageRecordFactoryService();
        try{
            DEFAULT_MESSAGE.create();
            DEFAULT_MESSAGE.start();
        }catch(Exception e){
            e.printStackTrace();
        }
        message = new MessageRecordFactoryWrapper(DEFAULT_MESSAGE);
    }
    
    private static Properties properties = new Properties();
    
    /**
     * コンストラクタ。<p>
     */
    private ServiceManagerFactory(){
        super();
    }
    
    /**
     * デフォルトのサービス定義をロードする。<p>
     * {@link #loadManager(URL)}を引数nullで呼び出す。<br>
     * このメソッドによってロードされるサービス定義ファイルは、{@link Utility#getDefaultServiceURL()}で取得されるURLのサービス定義ファイルである。
     * 
     * @return ロードに成功した場合true。但し、ここで言う成功は、必ずしもサービス定義に定義されたサービスが全て正常に起動した事を保証するものでは、ありません。サービス定義のロードに使用するServiceLoaderが正常に起動された事を示します。
     */
    public static synchronized boolean loadManager(){
        return loadManager((URL)null);
    }
    
    /**
     * 指定したパスのサービス定義をロードする。<p>
     * 指定したパスは、{@link Utility#convertServicePathToURL(String)}でURLに変換され、{@link #loadManager(URL)}を呼び出す。<br>
     *
     * @param path サービス定義ファイルのパス
     * @return ロードに成功した場合true。但し、ここで言う成功は、必ずしもサービス定義に定義されたサービスが全て正常に起動した事を保証するものでは、ありません。サービス定義のロードに使用するServiceLoaderが正常に起動された事を示します。
     * @exception IllegalArgumentException 指定したパスが不正な場合、または存在しない場合
     */
    public static synchronized boolean loadManager(String path){
        return loadManager(path, false, false);
    }
    
    /**
     * 指定したURLのサービス定義をロードする。<p>
     * {@link #loadManager(URL, boolean)}を、loadManager(url, false)で呼び出す。<br>
     *
     * @param url サービス定義ファイルのURL
     * @return ロードに成功した場合true。但し、ここで言う成功は、必ずしもサービス定義に定義されたサービスが全て正常に起動した事を保証するものでは、ありません。サービス定義のロードに使用するServiceLoaderが正常に起動された事を示します。
     */
    public static synchronized boolean loadManager(URL url){
        return loadManager(url, false);
    }
    
    /**
     * 指定したパスのサービス定義をロードする。<p>
     * 指定したパスは、{@link Utility#convertServicePathToURL(String)}でURLに変換され、{@link #loadManager(URL, boolean)}を呼び出す。<br>
     *
     * @param path サービス定義ファイルのパス
     * @param isReload 既にロードしたサービス定義を再ロードする場合には、true
     * @return ロードに成功した場合true。但し、ここで言う成功は、必ずしもサービス定義に定義されたサービスが全て正常に起動した事を保証するものでは、ありません。サービス定義のロードに使用するServiceLoaderが正常に起動された事を示します。
     */
    public static synchronized boolean loadManager(
        String path,
        boolean isReload
    ){
        return loadManager(
            path,
            isReload,
            false
        );
    }
    
    /**
     * 指定したURLのサービス定義をロードする。<p>
     * {@link #loadManager(URL, boolean, boolean)}を、loadManager(url, isReload, false)で呼び出す。<br>
     *
     * @param url サービス定義ファイルのURL
     * @param isReload 既にロードしたサービス定義を再ロードする場合には、true
     * @return ロードに成功した場合true。但し、ここで言う成功は、必ずしもサービス定義に定義されたサービスが全て正常に起動した事を保証するものでは、ありません。サービス定義のロードに使用するServiceLoaderが正常に起動された事を示します。
     */
    public static synchronized boolean loadManager(URL url, boolean isReload){
        return loadManager(url, isReload, false);
    }
    
    /**
     * 指定したパスのサービス定義をロードする。<p>
     * 指定したパスは、{@link Utility#convertServicePathToURL(String)}でURLに変換され、{@link #loadManager(URL, boolean, boolean)}を呼び出す。<br>
     *
     * @param path サービス定義ファイルのパス
     * @param isReload 既にロードしたサービス定義を再ロードする場合には、true
     * @param isValidate サービス定義ファイルを評価するかどうか。評価する場合はtrue
     * @return ロードに成功した場合true。但し、ここで言う成功は、必ずしもサービス定義に定義されたサービスが全て正常に起動した事を保証するものでは、ありません。サービス定義のロードに使用するServiceLoaderが正常に起動された事を示します。
     */
    public static synchronized boolean loadManager(
        String path,
        boolean isReload,
        boolean isValidate
    ){
        URL url = null;
        try{
            url = Utility.convertServicePathToURL(path);
        }catch(IllegalArgumentException e){
            logger.write(SVCMF00030, path, e);
            return false;
        }
        if(url == null){
            logger.write(SVCMF00030, path);
            return false;
        }
        return loadManager(url, isReload, isValidate);
    }
    
    /**
     * 指定したパスのサービス定義をロードする。<p>
     * 指定したパスは、{@link Utility#convertServicePathToURL(String)}でURLに変換され、{@link #loadManager(URL, boolean, boolean)}を呼び出す。<br>
     *
     * @param path サービス定義ファイルのパス
     * @param config サービスローダ構成情報
     * @param isReload 既にロードしたサービス定義を再ロードする場合には、true
     * @param isValidate サービス定義ファイルを評価するかどうか。評価する場合はtrue
     * @return ロードに成功した場合true。但し、ここで言う成功は、必ずしもサービス定義に定義されたサービスが全て正常に起動した事を保証するものでは、ありません。サービス定義のロードに使用するServiceLoaderが正常に起動された事を示します。
     */
    public static synchronized boolean loadManager(
        String path,
        ServiceLoaderConfig config,
        boolean isReload,
        boolean isValidate
    ){
        URL url = null;
        try{
            url = Utility.convertServicePathToURL(path);
        }catch(IllegalArgumentException e){
            logger.write(SVCMF00030, path, e);
            return false;
        }
        if(url == null){
            logger.write(SVCMF00030, path);
            return false;
        }
        return loadManager(url, config, isReload, isValidate);
    }
    
    /**
     * 指定したURLのサービス定義をロードする。<p>
     *
     * @param url サービス定義ファイルのURL
     * @param isReload 既にロードしたサービス定義を再ロードする場合には、true
     * @param isValidate サービス定義ファイルを評価するかどうか。評価する場合はtrue
     * @return ロードに成功した場合true。但し、ここで言う成功は、必ずしもサービス定義に定義されたサービスが全て正常に起動した事を保証するものでは、ありません。サービス定義のロードに使用するServiceLoaderが正常に起動された事を示します。
     */
    public static synchronized boolean loadManager(
        URL url,
        boolean isReload,
        boolean isValidate
    ){
        return loadManager(url, null, isReload, isValidate);
    }
    
    /**
     * 指定したURLのサービス定義をロードする。<p>
     *
     * @param url サービス定義ファイルのURL
     * @param config サービスローダ構成情報
     * @param isReload 既にロードしたサービス定義を再ロードする場合には、true
     * @param isValidate サービス定義ファイルを評価するかどうか。評価する場合はtrue
     * @return ロードに成功した場合true。但し、ここで言う成功は、必ずしもサービス定義に定義されたサービスが全て正常に起動した事を保証するものでは、ありません。サービス定義のロードに使用するServiceLoaderが正常に起動された事を示します。
     */
    public static synchronized boolean loadManager(
        URL url,
        ServiceLoaderConfig config,
        boolean isReload,
        boolean isValidate
    ){
        logger.write(
            SVCMF00001,
            new Object[]{url, isReload ? Boolean.TRUE : Boolean.FALSE}
        );
        
        if(url == null){
            url = Utility.getDefaultServiceURL();
        }
        
        if(url == null){
            return false;
        }
        
        ServiceLoader loader = null;
        if(!loaders.containsKey(url)){
            final String loaderClassName
                 = System.getProperty(LOADER_IMPL_CLASS_KEY);
            if(loaderClassName != null && loaderClassName.length() != 0){
                try{
                    final Class clazz = Class.forName(
                        loaderClassName,
                        true,
                        NimbusClassLoader.getInstance()
                    );
                    setServiceLoaderClass(clazz);
                }catch(ClassNotFoundException e){
                    logger.write(
                        SVCMF00002,
                        new Object[]{ServiceLoader.class, loaderClassName},
                        e
                    );
                }catch(IllegalArgumentException e){
                    logger.write(SVCMF00004, loaderClassName, e);
                }
            }
            try{
                loader = (ServiceLoader)getServiceLoaderClass().newInstance();
            }catch(InstantiationException e){
                logger.write(SVCMF00005, getServiceLoaderClass(), e);
                return false;
            }catch(IllegalAccessException e){
                logger.write(SVCMF00006, getServiceLoaderClass(), e);
                return false;
            }
            String managerClassName
                 = System.getProperty(MANAGER_IMPL_CLASS_KEY);
            if((managerClassName == null || managerClassName.length() == 0)
                && getServiceManagerClass() != null){
                managerClassName = getServiceManagerClass().getName();
            }
            if(managerClassName != null && managerClassName.length() != 0){
                try{
                    loader.setServiceManagerClassName(managerClassName);
                }catch(ClassNotFoundException e){
                    logger.write(
                        SVCMF00002,
                        new Object[]{ServiceManager.class, managerClassName},
                        e
                    );
                }catch(IllegalArgumentException e){
                    logger.write(SVCMF00031, managerClassName, e);
                }
            }
            try{
                loader.setServiceURL(url);
            }catch(IllegalArgumentException e){
                logger.write(SVCMF00007, url, e);
                return false;
            }
        }else if(isReload){
            loader = (ServiceLoader)loaders.get(url);
            loader.stop();
            loader.destroy();
            unregisterLoader(loader);
        }else{
            return true;
        }
        
        loader.setValidate(isValidate);
        loader.setConfig(config);
        
        try{
            loader.create();
            loader.start();
        }catch(Exception e){
            logger.write(SVCMF00008, url, e);
            loader.destroy();
            return false;
        }
        
        registerLoader(loader);
        logger.write(SVCMF00009, url);
        return true;
    }
    
    /**
     * デフォルトのサービス定義をアンロードする。<p>
     * {@link #unloadManager(URL)}を引数nullで呼び出す。<br>
     *
     * @return サービス定義のアンロード処理を行った場合true
     */
    public static synchronized boolean unloadManager(){
        return unloadManager((URL)null);
    }
    
    /**
     * 指定されたパスのサービス定義をアンロードする。<p>
     * 指定したパスは、{@link Utility#convertServicePathToURL(String)}でURLに変換され、{@link #unloadManager(URL)}を呼び出す。<br>
     *
     * @param path サービス定義ファイルのパス
     * @return サービス定義のアンロード処理を行った場合true
     */
    public static synchronized boolean unloadManager(String path){
        URL url = null;
        try{
            url = Utility.convertServicePathToURL(path);
        }catch(IllegalArgumentException e){
            try{
                url = new File(path).toURL();
            }catch(MalformedURLException ee){
                // この例外は発生しないはず
                return false;
            }
        }
        return unloadManager(url);
    }
    
    /**
     * 指定されたパスのサービス定義をアンロードする。<p>
     *
     * @param url サービス定義ファイルのURL
     * @return サービス定義のアンロード処理を行った場合true
     */
    public static synchronized boolean unloadManager(URL url){
        
        logger.write(SVCMF00010, url);
        
        if(url == null){
            url = Utility.getDefaultServiceURL();
        }
        
        if(url == null){
            return false;
        }
        
        if(!loaders.containsKey(url)){
            
            logger.write(SVCMF00011, url);
            return false;
        }else{
            Service service = (Service)loaders.get(url);
            service.stop();
            service.destroy();
            
            logger.write(SVCMF00012, url);
        }
        return true;
    }
    
    /**
     * ロードしたサービス定義に定義されたサービスが全て起動されているか調べる。<p>
     * @return 全て起動されている場合true
     */
    public static boolean checkLoadManagerCompleted(){
        return checkLoadManagerCompleted(null);
    }
    
    /**
     * ロードしたサービス定義に定義されたサービスが全て起動されているか調べる。<p>
     * 起動されていないサービス名を{@link ServiceName}として、notStartedに格納して返す。<br>
     * 
     * @param notStarted 起動できなかったサービス名の集合を格納するセット
     * @return 全て起動されている場合true
     */
    public static boolean checkLoadManagerCompleted(Set notStarted){
        
        logger.write(SVCMF00013);
        final Set tmpNotStarted = new HashSet();
        final ServiceManager[] managers = findManagers();
        final StringBuilder message = new StringBuilder();
        for(int i = 0, max = managers.length; i < max; i++){
            final ServiceManager manager = managers[i];
            final String managerName = manager.getServiceName();
            if(manager.existFailedService()){
                final Iterator failedServices
                     = manager.getFailedServices().iterator();
                while(failedServices.hasNext()){
                    final String failedService
                         = (String)failedServices.next();
                    final ServiceName name
                         = new ServiceName(managerName, failedService);
                    tmpNotStarted.add(name);
                    message.append(name);
                    final Throwable cause
                         = manager.getFailedCause(failedService);
                    if(cause != null){
                        message.append(CAUSE_THROWABLE);
                        message.append(cause);
                    }
                    if(failedServices.hasNext()){
                        message.append(LINE_SEPARAOTR);
                    }
                }
                if(i != max - 1){
                    message.append(LINE_SEPARAOTR);
                }
            }
        }
        boolean mustInsertLine = message.length() != 0;
        for(int i = 0, max = managers.length; i < max; i++){
            final ServiceManager manager = managers[i];
            final String managerName = manager.getServiceName();
            if(manager.existWaitingService()){
                final Iterator waitingServices
                     = manager.getWaitingServices().iterator();
                while(waitingServices.hasNext()){
                    final String waitingService
                         = (String)waitingServices.next();
                    final ServiceName name
                         = new ServiceName(managerName, waitingService);
                    if(!tmpNotStarted.contains(name)
                        && !waitingService.equals(managerName)){
                        tmpNotStarted.add(name);
                    }else{
                        continue;
                    }
                    if(mustInsertLine){
                        message.append(LINE_SEPARAOTR);
                        mustInsertLine = false;
                    }
                    message.append(name);
                    final Set causes = manager.getWaitingCauses(waitingService);
                    if(causes != null && causes.size() != 0){
                        message.append(CAUSE_SERVICES);
                        message.append(causes);
                    }
                    if(waitingServices.hasNext()){
                        message.append(LINE_SEPARAOTR);
                    }
                }
                if(i != max - 1){
                    message.append(LINE_SEPARAOTR);
                }
            }
        }
        final boolean isSuccess = tmpNotStarted.size() == 0;
        if(!isSuccess){
            if(notStarted != null){
                notStarted.addAll(tmpNotStarted);
            }
            logger.write(SVCMF00014, message.toString());
        }
        return isSuccess;
    }
    
    /**
     * ロードしたサービス定義に定義されたサービスが全て起動されているか調べる。<p>
     * @param managerNames チェックするマネージャ名の集合
     * @return 全て起動されている場合true
     */
    public static boolean checkLoadManagerCompletedBy(Set managerNames){
        return checkLoadManagerCompletedBy(managerNames, null);
    }
    
    /**
     * ロードしたサービス定義に定義されたサービスが全て起動されているか調べる。<p>
     * 起動されていないサービス名を{@link ServiceName}として、notStartedに格納して返す。<br>
     * 
     * @param managerNames チェックするマネージャ名の集合
     * @param notStarted 起動できなかったサービス名の集合を格納するセット
     * @return 全て起動されている場合true
     */
    public static boolean checkLoadManagerCompletedBy(
        Set managerNames,
        Set notStarted
    ){
        logger.write(SVCMF00013);
        final Set tmpNotStarted = new HashSet();
        final ServiceManager[] managers = findManagers();
        final StringBuilder message = new StringBuilder();
        for(int i = 0, max = managers.length; i < max; i++){
            final ServiceManager manager = managers[i];
            final String managerName = manager.getServiceName();
            if(!managerNames.contains(managerName)){
                continue;
            }
            if(manager.existFailedService()){
                final Iterator failedServices
                     = manager.getFailedServices().iterator();
                while(failedServices.hasNext()){
                    final String failedService
                         = (String)failedServices.next();
                    final ServiceName name
                         = new ServiceName(managerName, failedService);
                    tmpNotStarted.add(name);
                    message.append(name);
                    final Throwable cause
                         = manager.getFailedCause(failedService);
                    if(cause != null){
                        message.append(CAUSE_THROWABLE);
                        message.append(cause);
                    }
                    if(failedServices.hasNext()){
                        message.append(LINE_SEPARAOTR);
                    }
                }
                if(i != max - 1){
                    message.append(LINE_SEPARAOTR);
                }
            }
        }
        boolean mustInsertLine = message.length() != 0;
        for(int i = 0, max = managers.length; i < max; i++){
            final ServiceManager manager = managers[i];
            final String managerName = manager.getServiceName();
            if(!managerNames.contains(managerName)){
                continue;
            }
            if(manager.existWaitingService()){
                final Iterator waitingServices
                     = manager.getWaitingServices().iterator();
                while(waitingServices.hasNext()){
                    final String waitingService
                         = (String)waitingServices.next();
                    final ServiceName name
                         = new ServiceName(managerName, waitingService);
                    if(!tmpNotStarted.contains(name)
                        && !waitingService.equals(managerName)){
                        tmpNotStarted.add(name);
                    }else{
                        continue;
                    }
                    if(mustInsertLine){
                        message.append(LINE_SEPARAOTR);
                        mustInsertLine = false;
                    }
                    message.append(name);
                    final Set causes = manager.getWaitingCauses(waitingService);
                    if(causes != null && causes.size() != 0){
                        message.append(CAUSE_SERVICES);
                        message.append(causes);
                    }
                    if(waitingServices.hasNext()){
                        message.append(LINE_SEPARAOTR);
                    }
                }
                if(i != max - 1){
                    message.append(LINE_SEPARAOTR);
                }
            }
        }
        final boolean isSuccess = tmpNotStarted.size() == 0;
        if(!isSuccess){
            if(notStarted != null){
                notStarted.addAll(tmpNotStarted);
            }
            logger.write(SVCMF00014, message.toString());
        }
        return isSuccess;
    }
    
    /**
     * ロードしたサービス定義に定義されたサービスが全て起動されているか調べる。<p>
     * @param managerName チェックするマネージャ名
     * @return 全て起動されている場合true
     */
    public static boolean checkLoadManagerCompletedBy(String managerName){
        return checkLoadManagerCompletedBy(managerName, null);
    }
    
    /**
     * ロードしたサービス定義に定義されたサービスが全て起動されているか調べる。<p>
     * 起動されていないサービス名を{@link ServiceName}として、notStartedに格納して返す。<br>
     * 
     * @param managerName チェックするマネージャ名
     * @param notStarted 起動できなかったサービス名の集合を格納するセット
     * @return 全て起動されている場合true
     */
    public static boolean checkLoadManagerCompletedBy(
        String managerName,
        Set notStarted
    ){
        logger.write(SVCMF00013);
        final Set tmpNotStarted = new HashSet();
        final ServiceManager manager = findManager(managerName);
        final StringBuilder message = new StringBuilder();
        if(manager.existFailedService()){
            final Iterator failedServices
                 = manager.getFailedServices().iterator();
            while(failedServices.hasNext()){
                final String failedService
                     = (String)failedServices.next();
                final ServiceName name
                     = new ServiceName(managerName, failedService);
                tmpNotStarted.add(name);
                message.append(name);
                final Throwable cause
                     = manager.getFailedCause(failedService);
                if(cause != null){
                    message.append(CAUSE_THROWABLE);
                    message.append(cause);
                }
                if(failedServices.hasNext()){
                    message.append(LINE_SEPARAOTR);
                }
            }
        }
        boolean mustInsertLine = message.length() != 0;
        if(manager.existWaitingService()){
            final Iterator waitingServices
                 = manager.getWaitingServices().iterator();
            while(waitingServices.hasNext()){
                final String waitingService
                     = (String)waitingServices.next();
                final ServiceName name
                     = new ServiceName(managerName, waitingService);
                if(!tmpNotStarted.contains(name)
                    && !waitingService.equals(managerName)){
                    tmpNotStarted.add(name);
                }else{
                    continue;
                }
                if(mustInsertLine){
                    message.append(LINE_SEPARAOTR);
                    mustInsertLine = false;
                }
                message.append(name);
                final Set causes = manager.getWaitingCauses(waitingService);
                if(causes != null && causes.size() != 0){
                    message.append(CAUSE_SERVICES);
                    message.append(causes);
                }
                if(waitingServices.hasNext()){
                    message.append(LINE_SEPARAOTR);
                }
            }
        }
        final boolean isSuccess = tmpNotStarted.size() == 0;
        if(!isSuccess){
            if(notStarted != null){
                notStarted.addAll(tmpNotStarted);
            }
            logger.write(SVCMF00014, message.toString());
        }
        return isSuccess;
    }
    
    /**
     * ロードされた全てのServiceManagerを探す。<p>
     *
     * @return ロードされた全てのServiceManagerの配列。１つもロードされていない場合は、長さ０の配列を返す。
     */
    public static ServiceManager[] findManagers(){
        final Set managerSet = repository.registeredSet();
        final ServiceManager[] managers = new ServiceManager[managerSet.size()];
        managerSet.toArray(managers);
        return managers;
    }
    
    /**
     * デフォルトの名前を持つServiceManagerを探す。<p>
     * ここで言う、デフォルトの名前は、{@link ServiceManager#DEFAULT_NAME}である。<br>
     *
     * @return デフォルトの名前を持つServiceManager。見つからない場合は、nullを返す。
     */
    public static ServiceManager findManager(){
        return findManager(ServiceManager.DEFAULT_NAME);
    }
    
    /**
     * 指定された名前を持つServiceManagerを探す。<p>
     *
     * @param name ServiceManagerの名前
     * @return 指定された名前を持つServiceManager。見つからない場合は、nullを返す。
     */
    public static ServiceManager findManager(String name){
        if(name == null){
            return null;
        }
        return (ServiceManager)repository.get(name);
    }
    
    /**
     * ServiceManagerを登録する。<p>
     * ServiceManagerのデフォルト実装クラスである{@link DefaultServiceManagerService}を使用する。<br>
     *
     * @param name ServiceManagerの登録名
     * @return 登録できた場合true
     * @see #registerManager(String, ServiceManager)
     */
    public static boolean registerManager(String name){
        final DefaultServiceLoaderService loader
             = new DefaultServiceLoaderService();
        final ServerMetaData serverData = new ServerMetaData(loader, null);
        final ManagerMetaData managerData = new ManagerMetaData(loader, serverData);
        managerData.setName(name);
        serverData.addManager(managerData);
        loader.setServerMetaData(serverData);
        try{
            loader.create();
            loader.start();
        }catch(Exception e){
            // 起こらないはず
            loader.destroy();
            return false;
        }
        return true;
    }
    
    /**
     * ServiceManagerを登録する。<p>
     * 上書き登録できるかどうかは、ServiceManagerの管理に用いる{@link Repository}の実装に依存する。デフォルトのRepositoryは、上書き登録は許可しない。<br>
     * <p>
     * 登録できた場合は、{@link #processRegisterd(ServiceManager)}を呼び出して、登録されている{@link RegistrationListener}に登録を通知する。<br>
     *
     * @param name ServiceManagerの登録名
     * @param manager 登録するServiceManagerオブジェクト
     * @return 登録できた場合true
     */
    public static boolean registerManager(String name, ServiceManager manager){
        logger.write(SVCMF00015, new Object[]{name, manager});
        final boolean result = repository.register(name, manager);
        if(result){
            logger.write(SVCMF00016, name);
            if(manager != null){
                processRegisterd(manager);
            }
        }else{
            logger.write(SVCMF00017, name);
        }
        return result;
    }
    
    /**
     * 指定した名前を持つServiceManagerの登録を解除する。<p>
     * 登録を解除できた場合は、{@link #processUnregisterd(ServiceManager)}を呼び出して、登録されている{@link RegistrationListener}に登録解除を通知する。<br>
     *
     * @param name ServiceManagerの登録名
     * @return 登録を解除できた場合true
     */
    public static boolean unregisterManager(String name){
        logger.write(SVCMF00018, name);
        final ServiceManager manager = findManager(name);
        final boolean result = repository.unregister(name);
        if(result){
            logger.write(SVCMF00019, name);
            if(manager != null){
                processUnregisterd(manager);
            }
        }else{
            logger.write(SVCMF00020, name);
        }
        return result;
    }
    
    /**
     * 指定された名前のマネージャが登録されているか調べる。<p>
     *
     * @param name マネージャ名
     * @return 登録されていた場合true
     */
    public static boolean isRegisteredManager(String name){
        return repository.isRegistered(name); 
    }
    
    /**
     * 指定された名前のServiceManagerから、指定されたサービス名のサービスを取得する。<p>
     * ServiceLoaderでロードされたサービスは、{@link Service}インタフェースを実装していなくても、Serviceインタフェースを実装したラッパーでくるまれて登録される。<br>
     * このメソッドは、その特性を生かし、ServiceLoaderでロードされたサービスをServiceオブジェクトとして取得するメソッドである。<br>
     * 通常、サービスの起動、停止などの処理を行いたい場合に、このメソッドでサービスを取得する。<br>
     *
     * @param managerName ServiceManagerの名前
     * @param serviceName サービス名
     * @return サービス
     * @exception ServiceNotFoundException 指定されたサービスが見つからない場合
     */
    public static Service getService(String managerName, String serviceName)
     throws ServiceNotFoundException{
        final ServiceManager manager = findManager(managerName);
        if(manager != null){
            return manager.getService(serviceName);
        }
        throw new ServiceNotFoundException(managerName, serviceName);
    }
    
    /**
     * 指定されたサービス名のサービスを取得する。<p>
     *
     * @param serviceName サービス名
     * @return サービス
     * @exception ServiceNotFoundException 指定されたサービスが見つからない場合
     * @see #getService(String, String)
     */
    public static Service getService(ServiceName serviceName)
     throws ServiceNotFoundException{
        if(serviceName == null){
            throw new ServiceNotFoundException(null, null);
        }
        return getService(
            serviceName.getServiceManagerName(),
            serviceName.getServiceName()
        );
    }
    
    /**
     * デフォルトの名前のServiceManagerから、指定されたサービス名のサービスを取得する。<p>
     *
     * @param serviceName サービス名
     * @return サービス
     * @exception ServiceNotFoundException 指定されたサービスが見つからない場合
     * @see #getService(String, String)
     */
    public static Service getService(String serviceName)
     throws ServiceNotFoundException{
        final ServiceManager manager = findManager();
        if(manager != null){
            return manager.getService(serviceName);
        }
        throw new ServiceNotFoundException(
            ServiceManager.DEFAULT_NAME,
            serviceName
        );
    }
    
    /**
     * 指定された名前のServiceManagerから、指定されたサービス名のサービスの定義情報を取得する。<p>
     *
     * @param managerName ServiceManagerの名前
     * @param serviceName サービス名
     * @return サービス定義情報
     * @exception ServiceNotFoundException 指定されたサービスが見つからない場合
     */
    public static ServiceMetaData getServiceMetaData(
        String managerName,
        String serviceName
    ) throws ServiceNotFoundException{
        final ServiceManager manager = findManager(managerName);
        if(manager != null){
            return manager.getServiceMetaData(serviceName);
        }
        throw new ServiceNotFoundException(managerName, serviceName);
    }
    
    /**
     * 指定されたサービス名のサービスの定義情報を取得する。<p>
     *
     * @param serviceName サービス名
     * @return サービス定義情報
     * @exception ServiceNotFoundException 指定されたサービスが見つからない場合
     */
    public static ServiceMetaData getServiceMetaData(ServiceName serviceName)
     throws ServiceNotFoundException{
        if(serviceName == null){
            throw new ServiceNotFoundException(null);
        }
        return getServiceMetaData(
            serviceName.getServiceManagerName(),
            serviceName.getServiceName()
        );
    }
    
    /**
     * デフォルトの名前のServiceManagerから、指定されたサービス名のサービス定義情報を取得する。<p>
     *
     * @param serviceName サービス名
     * @return サービス定義情報
     * @exception ServiceNotFoundException 指定されたサービスが見つからない場合
     */
    public static ServiceMetaData getServiceMetaData(
        String serviceName
    ) throws ServiceNotFoundException{
        final ServiceManager manager = findManager();
        if(manager != null){
            return manager.getServiceMetaData(serviceName);
        }
        throw new ServiceNotFoundException(
            ServiceManager.DEFAULT_NAME,
            serviceName
        );
    }
    
    /**
     * 指定された名前のServiceManagerから、指定されたサービス名のサービスオブジェクトを取得する。<p>
     * ServiceLoaderでロードされたサービスは、{@link Service}インタフェースを実装していなくても、Serviceインタフェースを実装したラッパーでくるまれて登録される。<br>
     * {@link #getService(String, String)}メソッドでは、ServiceLoaderでロードされたサービスをServiceオブジェクトとして取得するが、このメソッドは、ServiceLoaderでロードされたサービスオブジェクトそのものを取得する。<br>
     * 通常、サービスのアプリケーション向けの機能を使用する場合に、このメソッドでサービスを取得して、必要なインタフェースにキャストして使用する。<br>
     *
     * @param managerName ServiceManagerの名前
     * @param serviceName サービス名
     * @return サービス
     * @exception ServiceNotFoundException 指定されたサービスが見つからない場合
     */
    public static Object getServiceObject(
        String managerName,
        String serviceName
    ) throws ServiceNotFoundException{
        final ServiceManager manager = findManager(managerName);
        if(manager != null){
            return manager.getServiceObject(serviceName);
        }
        throw new ServiceNotFoundException(managerName, serviceName);
    }
    
    /**
     * 指定されたサービス名のサービスオブジェクトを取得する。<p>
     *
     * @param serviceName サービス名
     * @return サービスオブジェクト
     * @exception ServiceNotFoundException 指定されたサービスが見つからない場合
     * @see #getServiceObject(String, String)
     */
    public static Object getServiceObject(ServiceName serviceName)
     throws ServiceNotFoundException{
        if(serviceName == null){
            throw new ServiceNotFoundException(serviceName);
        }
        return getServiceObject(
            serviceName.getServiceManagerName(),
            serviceName.getServiceName()
        );
    }
    
    /**
     * デフォルトの名前のServiceManagerから、指定されたサービス名のサービスオブジェクトを取得する。<p>
     *
     * @param serviceName サービス名
     * @return サービスオブジェクト
     * @exception ServiceNotFoundException 指定されたサービスが見つからない場合
     * @see #getServiceObject(String, String)
     */
    public static Object getServiceObject(
        String serviceName
    ) throws ServiceNotFoundException{
        final ServiceManager manager = findManager();
        if(manager != null){
            return manager.getServiceObject(serviceName);
        }
        throw new ServiceNotFoundException(
            ServiceManager.DEFAULT_NAME,
            serviceName
        );
    }
    
    /**
     * 指定された名前のServiceManagerから、指定されたサービス名のサービスの状態変更を通知するServiceStateBroadcasterを取得する。<p>
     * ServiceLoaderでロードされたサービスは、ServiceStateBroadcasterを実装していても良い。指定されたサービスがServiceStateBroadcasterを実装している場合は、それを返す。実装していない場合は、nullを返す。<br>
     *
     * @param managerName ServiceManagerの名前
     * @param serviceName サービス名
     * @return ServiceStateBroadcasterオブジェクト
     * @exception ServiceNotFoundException 指定されたサービスが見つからない場合
     */
    public static ServiceStateBroadcaster getServiceStateBroadcaster(
        String managerName,
        String serviceName
    ) throws ServiceNotFoundException{
        final ServiceManager manager = findManager(managerName);
        if(manager != null){
            return manager.getServiceStateBroadcaster(serviceName);
        }
        throw new ServiceNotFoundException(managerName, serviceName);
    }
    
    /**
     * 指定されたサービス名のサービスの状態変更を通知するServiceStateBroadcasterを取得する。<p>
     *
     * @param serviceName サービス名
     * @return ServiceStateBroadcasterオブジェクト
     * @exception ServiceNotFoundException 指定されたサービスが見つからない場合
     * @see #getServiceStateBroadcaster(String, String)
     */
    public static ServiceStateBroadcaster getServiceStateBroadcaster(
        ServiceName serviceName
    ) throws ServiceNotFoundException{
        if(serviceName == null){
            throw new ServiceNotFoundException(null, null);
        }
        return getServiceStateBroadcaster(
            serviceName.getServiceManagerName(),
            serviceName.getServiceName()
        );
    }
    
    /**
     * デフォルトの名前のServiceManagerから、指定されたサービス名のサービスの状態変更を通知するServiceStateBroadcasterを取得する。<p>
     *
     * @param serviceName サービス名
     * @return ServiceStateBroadcasterオブジェクト
     * @exception ServiceNotFoundException 指定されたサービスが見つからない場合
     * @see #getServiceStateBroadcaster(String, String)
     */
    public static ServiceStateBroadcaster getServiceStateBroadcaster(
        String serviceName
    ) throws ServiceNotFoundException{
        final ServiceManager manager = findManager();
        if(manager != null){
            return manager.getServiceStateBroadcaster(serviceName);
        }
        throw new ServiceNotFoundException(
            ServiceManager.DEFAULT_NAME,
            serviceName
        );
    }
    
    /**
     * 指定されたサービス定義メタデータに従ったサービスを、指定された名前のServiceManagerに、指定されたサービス名でサービスとして登録する。<p>
     *
     * @param managerName ServiceManagerの名前
     * @param serviceData サービス定義メタデータ
     * @return 登録できた場合は、true。指定された名前のServiceManagerが存在しない場合や、ServiceManagerが登録に失敗した場合は、falseを返す。
     * @exception Exception サービスのインスタンス化に失敗した場合
     */
    public static boolean registerService(
        String managerName,
        ServiceMetaData serviceData
    ) throws Exception{
        final ServiceManager manager = findManager(managerName);
        if(manager != null){
            return manager.registerService(serviceData);
        }
        return false;
    }
    
    /**
     * 指定されたオブジェクトを、指定された名前のServiceManagerに、指定されたサービス名でサービスとして登録する。<p>
     * 指定されたオブジェクトがServiceインタフェース実装していない場合は、Serviceインタフェースを実装したラッパーでくるまれて登録される。<br>
     *
     * @param managerName ServiceManagerの名前
     * @param serviceName サービス名
     * @param obj サービスオブジェクト
     * @return 登録できた場合は、true。指定された名前のServiceManagerが存在しない場合や、ServiceManagerが登録に失敗した場合は、falseを返す。
     * @exception Exception サービスの登録に失敗した場合
     */
    public static boolean registerService(
        String managerName,
        String serviceName,
        Object obj
    ) throws Exception{
        final ServiceManager manager = findManager(managerName);
        if(manager != null){
            return manager.registerService(serviceName, obj);
        }
        return false;
    }
    
    /**
     * 指定されたサービスを、指定された名前のServiceManagerに、指定されたサービス名で登録する。<p>
     *
     * @param managerName ServiceManagerの名前
     * @param serviceName サービス名
     * @param service サービス
     * @return 登録できた場合は、true。指定された名前のServiceManagerが存在しない場合や、ServiceManagerが登録に失敗した場合は、falseを返す。
     * @exception Exception サービスの登録に失敗した場合
     */
    public static boolean registerService(
        String managerName,
        String serviceName,
        Service service
    ) throws Exception{
        final ServiceManager manager = findManager(managerName);
        if(manager != null){
            return manager.registerService(serviceName, service);
        }
        return false;
    }
    
    
    /**
     * 指定された名前のServiceManagerから、指定されたサービス名のサービスの登録を解除する。<p>
     *
     * @param managerName ServiceManagerの名前
     * @param serviceName サービス名
     * @return 登録解除できた場合は、true。また、指定された名前のServiceManagerが存在しない場合もtrue。ServiceManagerが登録の解除に失敗した場合は、falseを返す。
     */
    public static boolean unregisterService(
        String managerName,
        String serviceName
    ){
        final ServiceManager manager = findManager(managerName);
        if(manager != null){
            return manager.unregisterService(serviceName);
        }
        return true;
    }
    
    /**
     * 指定された名前のServiceManagerに、指定されたサービス名のサービスが登録されているか調べる。<p>
     *
     * @param managerName マネージャ名
     * @param serviceName サービス名
     * @return 登録されていた場合true
     */
    public static boolean isRegisteredService(
        String managerName,
        String serviceName
    ){
        final ServiceManager manager = findManager(managerName);
        if(manager != null){
            return manager.isRegisteredService(serviceName);
        }
        return false;
    }
    
    /**
     * 指定されたサービス名のサービスが登録されているか調べる。<p>
     *
     * @param serviceName サービス名
     * @return 登録されていた場合true
     */
    public static boolean isRegisteredService(ServiceName serviceName){
        if(serviceName == null){
            return false;
        }
        final ServiceManager manager = findManager(
            serviceName.getServiceManagerName()
        );
        if(manager != null){
            return manager.isRegisteredService(serviceName.getServiceName());
        }
        return false;
    }
    
    /**
     * ServiceManagerの管理に使用するRepositoryサービスを設定する。<p>
     * 現在のRepositoryに登録されているServiceManagerを、全て新しいRepositoryに登録できた場合のみ、Repositoryを変更する。一つでも、登録に失敗したServiceManagerが存在する場合には、元の状態に戻す。<br>
     *
     * @param name ServiceManagerの管理に使用するRepositoryサービスのサービス名
     * @return Repositoryの入れ替えに成功した場合true
     */
    public static boolean setManagerRepository(ServiceName name){
        return setManagerRepository(
            name.getServiceManagerName(),
            name.getServiceName()
        );
    }
    
    /**
     * ServiceManagerの管理に使用するRepositoryサービスを設定する。<p>
     * 現在のRepositoryに登録されているServiceManagerを、全て新しいRepositoryに登録できた場合のみ、Repositoryを変更する。一つでも、登録に失敗したServiceManagerが存在する場合には、元の状態に戻す。<br>
     *
     * @param manager ServiceManagerの管理に使用するRepositoryサービスが登録されているマネージャ名
     * @param service ServiceManagerの管理に使用するRepositoryサービスのサービス名
     * @return Repositoryの入れ替えに成功した場合true
     */
    public static boolean setManagerRepository(
        final String manager,
        final String service
    ){
        if(isRegisteredService(manager, service)
             && getService(manager, service).getState() == Service.STARTED
        ){
            return setManagerRepository(
                (Repository)getServiceObject(manager, service)
            );
        }else{
            addServiceStateListener(
                manager,
                service,
                new ServiceStateListener(){
                    public void stateChanged(ServiceStateChangeEvent e)
                     throws Exception{
                        ServiceManagerFactory.removeServiceStateListener(
                            manager,
                            service,
                            this
                        );
                        ServiceManagerFactory
                            .setManagerRepository(manager, service);
                    }
                    public boolean isEnabledState(int st){
                        return st == Service.STARTED;
                    }
                }
            );
            return false;
        }
    }
    
    /**
     * ServiceManagerの管理に使用するRepositoryを設定する。<p>
     * 現在のRepositoryに登録されているServiceManagerを、全て新しいRepositoryに登録できた場合のみ、Repositoryを変更する。一つでも、登録に失敗したServiceManagerが存在する場合には、元の状態に戻す。<br>
     *
     * @param newRep ServiceManagerの管理に使用するRepositoryオブジェクト
     * @return Repositoryの入れ替えに成功した場合true
     */
    public static boolean setManagerRepository(Repository newRep){
        logger.write(SVCMF00021, newRep);
        synchronized(repository){
            if(newRep == null){
                newRep = new DefaultRepository();
            }
            if(repository.equals(newRep)){
                return true;
            }
            boolean success = true;
            final Set registered = new HashSet();
            Iterator names = repository.nameSet().iterator();
            while(names.hasNext()){
                final String name = (String)names.next();
                final Object manager = repository.get(name);
                if(manager != null){
                    if(!newRep.register(name, manager)){
                        logger.write(SVCMF00022, name);
                        success = false;
                    }else{
                        registered.add(name);
                    }
                }
            }
            if(!success){
                logger.write(SVCMF00023, newRep);
                names = registered.iterator();
                while(names.hasNext()){
                    final String name = (String)names.next();
                    newRep.unregister(name);
                }
                return false;
            }
            logger.write(SVCMF00024, newRep);
            names = newRep.nameSet().iterator();
            while(names.hasNext()){
                final String name = (String)names.next();
                repository.unregister(name);
            }
            repository = newRep;
        }
        return true;
    }
    
    /**
     * サービス定義をロードしたServiceLoaderを登録する。<p>
     * 
     * @param loader 登録するServiceLoaderオブジェクト
     */
    public static void registerLoader(ServiceLoader loader){
        final URL url = loader.getServiceURL();
        if(loaders.size() == 0){
            if(logger != null){
                logger.start();
            }
        }
        if(!loaders.containsKey(url)){
            loaders.put(url, loader);
        }
    }
    
    /**
     * サービス定義をロードしたServiceLoaderの登録を解除する。<p>
     * 
     * @param loader 登録を解除するServiceLoaderオブジェクト
     */
    public static void unregisterLoader(ServiceLoader loader){
        loaders.remove(loader.getServiceURL());
        if(loaders.size() == 0){
            if(logger != null){
                logger.stop();
            }
        }
    }
    
    /**
     * 指定されたURLのサービス定義をロードしたServiceLaoderを取得する。<p>
     *
     * @param url サービス定義のURL
     * @return 指定されたURLのサービス定義をロードしたServiceLaoder。存在しない場合は、nullを返す。
     */
    public static ServiceLoader getLoader(URL url){
        return (ServiceLoader)loaders.get(url);
    }
    
    /**
     * サービス定義をロードした全てのServiceLaoderを取得する。<p>
     *
     * @return サービス定義をロードしたServiceLaoderの集合。存在しない場合は、空の集合を返す。
     */
    public static Collection getLoaders(){
        return new HashSet(loaders.values());
    }
    
    /**
     * サービス定義ファイルをロードするServiceLoaderクラスを設定する。<p>
     * 但し、システムプロパティ"jp.ossc.nimbus.core.loader"で指定したServiceLoaderクラスの方が優先される。<br>
     *
     * @param loader サービス定義ファイルをロードするServiceLoaderクラス
     */
    public static void setServiceLoaderClass(Class loader)
     throws IllegalArgumentException{
        if(loader == null){
            loaderClass = DEFAULT_SERVICE_LOADER_CLASS;
        }else if(ServiceLoader.class.isAssignableFrom(loader)){
            loaderClass = loader;
        }else{
            throw new IllegalArgumentException(
                message.findEmbedMessage(SVCMF00003, loader)
            );
        }
        logger.write(SVCMF00025, loaderClass);
    }
    
    /**
     * サービス定義ファイルをロードするServiceLoaderクラスを取得する。<p>
     *
     * @return サービス定義ファイルをロードするServiceLoaderクラス
     */
    public static Class getServiceLoaderClass(){
        return loaderClass;
    }
    
    /**
     * このファクトリで生成するServiceManagerクラスを設定する。<p>
     * 但し、システムプロパティ"jp.ossc.nimbus.core.manager"で指定したServiceManagerクラスの方が優先される。<br>
     *
     * @param manager このファクトリで生成するServiceManagerクラス
     */
    public static void setServiceManagerClass(Class manager)
     throws IllegalArgumentException{
        if(manager == null){
            managerClass = null;
        }else if(ServiceManager.class.isAssignableFrom(manager)){
            managerClass = manager;
        }else{
            throw new IllegalArgumentException(
                message.findEmbedMessage(SVCMF00027, manager)
            );
        }
        logger.write(SVCMF00026, managerClass);
    }
    
    /**
     * サービス定義ファイルをロードするServiceManagerクラスを取得する。<p>
     *
     * @return このファクトリで生成するServiceManagerクラス
     */
    public static Class getServiceManagerClass(){
        return managerClass;
    }
    
    /**
     * ログ出力を行うLoggerを取得する。<p>
     *
     * @return Loggerオブジェクト
     */
    public static Logger getLogger(){
        return logger;
    }
    
    /**
     * ログ出力を行うLoggerサービスを設定する。<p>
     *
     * @param name サービスのサービス名
     */
    public static void setLogger(ServiceName name){
        setLogger(name.getServiceManagerName(), name.getServiceName());
    }
    
    /**
     * ログ出力を行うLoggerサービスを設定する。<p>
     *
     * @param manager Loggerサービスが登録されているServiceManagerの名前
     * @param service Loggerサービスのサービス名
     */
    public static void setLogger(final String manager, final String service){
        if(isRegisteredService(manager, service)
             && getService(manager, service).getState() == Service.STARTED
        ){
            final Logger newLogger = (Logger)getServiceObject(manager, service);
            final Service newLoggerService = getService(manager, service);
            logger.write(SVCMF00028, new Object[]{manager, service});
            logger.setLogger(newLogger, newLoggerService);
        }else{
            addServiceStateListener(
                manager,
                service,
                new ServiceStateListener(){
                    public void stateChanged(ServiceStateChangeEvent e)
                     throws Exception{
                        ServiceManagerFactory.removeServiceStateListener(
                            manager,
                            service,
                            this
                        );
                        ServiceManagerFactory.setLogger(manager, service);
                    }
                    public boolean isEnabledState(int st){
                        return st == Service.STARTED;
                    }
                }
            );
        }
    }
    
    /**
     * メッセージ生成を行うMessageRecordFactoryを取得する。<p>
     *
     * @return MessageRecordFactoryオブジェクト
     */
    public static MessageRecordFactory getMessageRecordFactory(){
        return message;
    }
    
    /**
     * メッセージ生成を行うMessageRecordFactoryサービスを設定する。<p>
     *
     * @param name MessageRecordFactoryサービスのサービス名
     */
    public static void setMessageRecordFactory(ServiceName name){
        setMessageRecordFactory(
            name.getServiceManagerName(),
            name.getServiceName()
        );
    }
    
    /**
     * メッセージ生成を行うMessageRecordFactoryサービスを設定する。<p>
     *
     * @param manager MessageRecordFactoryサービスが登録されているServiceManagerの名前
     * @param service MessageRecordFactoryサービスのサービス名
     */
    public static void setMessageRecordFactory(
        final String manager,
        final String service
    ){
        if(isRegisteredService(manager, service)
             && getService(manager, service).getState() == Service.STARTED
        ){
            final MessageRecordFactory newMessage
                 = (MessageRecordFactory)getServiceObject(manager, service);
            final Service newMessageService = getService(manager, service);
            logger.write(SVCMF00029, new Object[]{manager, service});
            message.setMessageRecordFactory(newMessage, newMessageService);
        }else{
            addServiceStateListener(
                manager,
                service,
                new ServiceStateListener(){
                    public void stateChanged(ServiceStateChangeEvent e)
                     throws Exception{
                        ServiceManagerFactory.removeServiceStateListener(
                            manager,
                            service,
                            this
                        );
                        ServiceManagerFactory.setMessageRecordFactory(
                            manager,
                            service
                        );
                    }
                    public boolean isEnabledState(int st){
                        return st == Service.STARTED;
                    }
                }
            );
        }
    }
    
    /**
     * 登録状態が変更された事を監視するRegistrationListenerを追加する。<p>
     *
     * @param listener RegistrationListenerオブジェクト
     */
    public static void addRegistrationListener(RegistrationListener listener){
        if(!registrationListeners.contains(listener)){
            registrationListeners.add(listener);
        }
    }
    
    /**
     * 登録状態が変更された事を監視するRegistrationListenerを削除する。<p>
     *
     * @param listener RegistrationListenerオブジェクト
     */
    public static void removeRegistrationListener(
        RegistrationListener listener
    ){
        registrationListeners.remove(listener);
    }
    
    /**
     * ServiceManagerが登録された事をRegistrationListenerに通知する。<p>
     * 
     * @param manager 登録されたServiceManager
     * @see RegistrationListener
     * @see RegistrationEvent
     */
    protected static void processRegisterd(ServiceManager manager){
        final Iterator listeners
             = new ArrayList(registrationListeners).iterator();
        while(listeners.hasNext()){
            final RegistrationListener listener
                 = (RegistrationListener)listeners.next();
            listener.registered(new RegistrationEvent(manager));
        }
    }
    
    /**
     * ServiceManagerが削除された事をRegistrationListenerに通知する。<p>
     * 
     * @param manager 削除されたServiceManager
     * @see RegistrationListener
     * @see RegistrationEvent
     */
    protected static void processUnregisterd(ServiceManager manager){
        final Iterator listeners
             = new ArrayList(registrationListeners).iterator();
        while(listeners.hasNext()){
            final RegistrationListener listener
                 = (RegistrationListener)listeners.next();
            listener.unregistered(new RegistrationEvent(manager));
        }
    }
    
    /**
     * サービスの状態が変更された事を監視するServiceStateListenerを追加する。<p>
     * 指定した{@link ServiceManager}が登録されていない場合、RegistrationListenerを登録する。ServiceManagerが登録されると、{@link ServiceManager#addServiceStateListener(String, ServiceStateListener)}で、ServiceStateListenerが登録される。<br>
     *
     * @param managerName マネージャ名
     * @param serviceName サービス名
     * @param listener ServiceStateListenerオブジェクト
     */
    public static void addServiceStateListener(
        final String managerName,
        final String serviceName,
        final ServiceStateListener listener
    ){
        if(isRegisteredManager(managerName)){
            final ServiceManager manager = findManager(managerName);
            manager.addServiceStateListener(serviceName, listener);
            return;
        }
        addRegistrationListener(
            new RegistrationListener(){
                public void registered(RegistrationEvent e){
                    final ServiceManager manager
                         = (ServiceManager)e.getRegistration();
                    if(!manager.getServiceName().equals(managerName)){
                        return;
                    }
                    removeRegistrationListener(this);
                    manager.addServiceStateListener(serviceName, listener);
                }
                public void unregistered(RegistrationEvent e){
                }
            }
        );
    }
    
    /**
     * サービスの状態が変更された事を監視するServiceStateListenerを追加する。<p>
     * 指定した{@link ServiceManager}が登録されていない場合、RegistrationListenerを登録する。ServiceManagerが登録されると、{@link ServiceManager#addServiceStateListener(String, ServiceStateListener)}で、ServiceStateListenerが登録される。<br>
     *
     * @param serviceName サービス名
     * @param listener ServiceStateListenerオブジェクト
     */
    public static void addServiceStateListener(
        ServiceName serviceName,
        ServiceStateListener listener
    ){
        addServiceStateListener(
            serviceName.getServiceManagerName(),
            serviceName.getServiceName(),
            listener
        );
    }
    
    /**
     * サービスの状態が変更された事を監視するServiceStateListenerを追加する。<p>
     * デフォルトの{@link ServiceManager}が登録されていない場合、RegistrationListenerを登録する。デフォルトのServiceManagerが登録されると、{@link ServiceManager#addServiceStateListener(String, ServiceStateListener)}で、ServiceStateListenerが登録される。<br>
     *
     * @param serviceName サービス名
     * @param listener ServiceStateListenerオブジェクト
     */
    public static void addServiceStateListener(
        final String serviceName,
        final ServiceStateListener listener
    ){
        final ServiceManager manager = findManager(serviceName);
        if(manager != null){
            manager.addServiceStateListener(serviceName, listener);
            return;
        }
        addRegistrationListener(
            new RegistrationListener(){
                public void registered(RegistrationEvent e){
                    final ServiceManager manager
                         = (ServiceManager)e.getRegistration();
                    if(!manager.getServiceName()
                        .equals(ServiceManager.DEFAULT_NAME)){
                        return;
                    }
                    removeRegistrationListener(this);
                    manager.addServiceStateListener(serviceName, listener);
                }
                public void unregistered(RegistrationEvent e){
                }
            }
        );
    }
    
    /**
     * サービスの状態が変更された事を監視するServiceStateListenerを削除する。<p>
     *
     * @param managerName マネージャ名
     * @param serviceName サービス名
     * @param listener ServiceStateListenerオブジェクト
     */
    public static void removeServiceStateListener(
        String managerName,
        String serviceName,
        ServiceStateListener listener
    ){
        if(isRegisteredManager(managerName)){
            final ServiceManager manager = findManager(managerName);
            manager.removeServiceStateListener(serviceName, listener);
        }
    }
    
    /**
     * サービスの状態が変更された事を監視するServiceStateListenerを削除する。<p>
     *
     * @param serviceName サービス名
     * @param listener ServiceStateListenerオブジェクト
     */
    public static void removeServiceStateListener(
        ServiceName serviceName,
        ServiceStateListener listener
    ){
        removeServiceStateListener(
            serviceName.getServiceManagerName(),
            serviceName.getServiceName(),
            listener
        );
    }
    
    /**
     * サービスの状態が変更された事を監視するServiceStateListenerを削除する。<p>
     *
     * @param serviceName サービス名
     * @param listener ServiceStateListenerオブジェクト
     */
    public static void removeServiceStateListener(
        String serviceName,
        ServiceStateListener listener
    ){
        final ServiceManager manager = findManager();
        if(manager != null){
            manager.removeServiceStateListener(serviceName, listener);
        }
    }
    
    /**
     * サーバプロパティを取得する。<p>
     * 
     * @param name プロパティ名
     * @return サーバプロパティ
     */
    public static String getProperty(String name){
        return properties.getProperty(name);
    }
    
    /**
     * サーバプロパティを設定する。<p>
     * 
     * @param name プロパティ名
     * @param value サーバプロパティ
     */
    public static void setProperty(String name, String value){
        properties.setProperty(name, value);
    }
    
    /**
     * 使用方法を標準出力に表示する。<p>
     */
    private static void usage(){
        try{
            System.out.println(
                getResourceString(USAGE_RESOURCE)
            );
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    /**
     * リソースを文字列として読み込む。<p>
     *
     * @param name リソース名
     * @exception IOException リソースが存在しない場合
     */
    private static String getResourceString(String name) throws IOException{
        
        // リソースの入力ストリームを取得
        InputStream is = ServiceManagerFactory.class.getClassLoader()
            .getResourceAsStream(name);
        
        // メッセージの読み込み
        StringBuilder buf = new StringBuilder();
        BufferedReader reader = null;
        final String separator = System.getProperty("line.separator");
        try{
            reader = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while((line = reader.readLine()) != null){
                buf.append(line).append(separator);
            }
        }finally{
            if(reader != null){
                try{
                    reader.close();
                }catch(IOException e){
                }
            }
        }
        return unicodeConvert(buf.toString());
    }
    
    /**
     * ユニコードエスケープ文字列を含んでいる可能性のある文字列をデフォルトエンコーディングの文字列に変換する。<p>
     *
     * @param str ユニコードエスケープ文字列を含んでいる可能性のある文字列
     * @return デフォルトエンコーディングの文字列
     */
    private static String unicodeConvert(String str){
        char c;
        int len = str.length();
        StringBuilder buf = new StringBuilder(len);
        
        for(int i = 0; i < len; ){
            c = str.charAt(i++);
            if(c == '\\'){
                c = str.charAt(i++);
                if(c == 'u'){
                    int value = 0;
                    for(int j = 0; j < 4; j++){
                        c = str.charAt(i++);
                        switch(c){
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            value = (value << 4) + (c - '0');
                            break;
                        case 'a':
                        case 'b':
                        case 'c':
                        case 'd':
                        case 'e':
                        case 'f':
                            value = (value << 4) + 10 + (c - 'a');
                            break;
                        case 'A':
                        case 'B':
                        case 'C':
                        case 'D':
                        case 'E':
                        case 'F':
                            value = (value << 4) + 10 + (c - 'A');
                            break;
                        default:
                            throw new IllegalArgumentException(
                                "Failed to convert unicode : " + c
                            );
                        }
                    }
                    buf.append((char)value);
                }else{
                    switch(c){
                    case 't':
                        c = '\t';
                        break;
                    case 'r':
                        c = '\r';
                        break;
                    case 'n':
                        c = '\n';
                        break;
                    case 'f':
                        c = '\f';
                        break;
                    default:
                    }
                    buf.append(c);
                }
            }else{
                buf.append(c);
            }
        }
        return buf.toString();
    }
    
    /**
     * コンパイルコマンドを実行する。<p>
     * <pre>
     * コマンド使用方法：
     *  java jp.ossc.nimbus.core.ServiceManagerFactory [options] [paths]
     * 
     * [options]
     * 
     *  [-validate]
     *   サービス定義をDTDで検証する。
     * 
     *  [-server]
     *   メインスレッドを待機させて、サーバとして動かす。
     * 
     *  [-help]
     *   ヘルプを表示します。
     * 
     * [paths]
     *  ロードするサービス定義ファイルのパス
     * 
     * 使用例 : 
     *    java -classpath classes;lib/nimbus.jar jp.ossc.nimbus.core.ServiceManagerFactory service-definition.xml
     * </pre>
     *
     * @param args コマンド引数
     * @exception Exception コンパイル中に問題が発生した場合
     */
    public static void main(String[] args) throws Exception{
        
        if(args.length != 0 && args[0].equals("-help")){
            usage();
            return;
        }
        
        final List servicePaths = new ArrayList();
        boolean validate = false;
        boolean server = false;
        for(int i = 0; i < args.length; i++){
            if(args[i].equals("-server")){
                server = true;
            }else if(args[i].equals("-validate")){
                validate = true;
            }else{
                servicePaths.add(args[i]);
            }
        }
        
        if(servicePaths.size() == 0){
            usage();
            return;
        }
        
        Runtime.getRuntime().addShutdownHook(
            new Thread(
                new Runnable(){
                    public void run(){
                        for(int i = servicePaths.size(); --i >= 0;){
                            ServiceManagerFactory.unloadManager((String)servicePaths.get(i));
                        }
                    }
                }
            )
        );
        
        for(int i = 0, max = servicePaths.size(); i < max; i++){
            if(!ServiceManagerFactory.loadManager((String)servicePaths.get(i), false, validate)){
                Thread.sleep(1000);
                System.exit(-1);
            }
        }
        if(!ServiceManagerFactory.checkLoadManagerCompleted()){
            Thread.sleep(1000);
            System.exit(-1);
        }
        if(server){
            WaitSynchronizeMonitor lock = new WaitSynchronizeMonitor();
            synchronized(lock){
               lock.initMonitor();
                try{
                   lock.waitMonitor();
                }catch(InterruptedException ignore){}
            }
        }
    }
}