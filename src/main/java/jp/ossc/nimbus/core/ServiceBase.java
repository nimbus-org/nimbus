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

import java.io.*;
import java.util.*;

import jp.ossc.nimbus.service.log.*;
import jp.ossc.nimbus.service.message.*;

/**
 * サービス基底クラス。<p>
 * {@link ServiceManager}で管理、制御可能なサービスの基底クラスである。<br>
 * サービスの開発者は、通常、このクラスを継承して、サービスを実装する。<br>
 * <pre>
 *   public class MyService extends ServiceBase{
 *             ：
 * </pre>
 * このクラスのサブクラスは、ServiceManagerによって、生成（{@link #create()}）、起動（{@link #start()}）、停止（{@link #stop()}）、廃棄（{@link #destroy()}）の契機を制御する事が可能である。それぞれの動作のメソッド（create()、start()、stop()、destroy()）には、実装が行われているので、通常は、オーバーライドしてはいけない。このクラスを使ってサービスの制御の実装を行うには、{@link #createService()}、{@link #startService()}、{@link #stopService()}、{@link #destroyService()}が、空実装となっているので、必要に応じてオーバーライドすること。<br>
 * <pre>
 *   public class MyService extends ServiceBase{
 *             ：
 *       public void createService() throws Exception{
 *                 ：
 *       }
 *       public void startService() throws Exception{
 *                 ：
 *       }
 *       public void stopService() throws Exception{
 *                 ：
 *       }
 *       public void destroyService() throws Exception{
 *                 ：
 *       }
 *             ：
 * </pre>
 * create()、start()、stop()、destroy()の４つの動作の実装で、状態の制御が行われてる。また、create()では、{@link #setServiceManagerName(String)}で設定されたServiceManagerに、{@link #setServiceName(String)}で設定された名前で自分自身を登録する。同様に、destroy()では、ServiceManagerから自分自身を登録解除する。<br>
 * 
 * @author M.Takata
 * @see ServiceManager
 */
public abstract class ServiceBase
 implements ServiceBaseMBean, ServiceProxy,
            ServiceStateBroadcaster, Serializable{
    
    private static final long serialVersionUID = -2021965433743797247L;
    
    // メッセージID定義
    private static final String SVC__ = "SVC__";
    private static final String SVC__0 = SVC__ + 0;
    private static final String SVC__00 = SVC__0 + 0;
    private static final String SVC__000 = SVC__00 + 0;
    private static final String SVC__0000 = SVC__000 + 0;
    private static final String SVC__00001 = SVC__0000 + 1;
    private static final String SVC__00002 = SVC__0000 + 2;
    private static final String SVC__00003 = SVC__0000 + 3;
    private static final String SVC__00004 = SVC__0000 + 4;
    private static final String SVC__00005 = SVC__0000 + 5;
    private static final String SVC__00006 = SVC__0000 + 6;
    private static final String SVC__00007 = SVC__0000 + 7;
    private static final String SVC__00008 = SVC__0000 + 8;
    private static final String SVC__00009 = SVC__0000 + 9;
    private static final String SVC__00010 = SVC__000 + 10;
    private static final String SVC__00011 = SVC__000 + 11;
    private static final String SVC__00012 = SVC__000 + 12;
    private static final String SVC__00013 = SVC__000 + 13;
    private static final String SVC__00014 = SVC__000 + 14;
    private static final String SVC__00015 = SVC__000 + 15;
    private static final String SVC__00016 = SVC__000 + 16;
    private static final String SVC__00017 = SVC__000 + 17;
    private static final String SVC__00018 = SVC__000 + 18;
    private static final String SVC__00019 = SVC__000 + 19;
    private static final String SVC__00020 = SVC__000 + 20;
    private static final String SVC__00021 = SVC__000 + 21;
    private static final String SVC__00022 = SVC__000 + 22;
    private static final String SVC__00023 = SVC__000 + 23;
    private static final String SVC__00024 = SVC__000 + 24;
    private static final String SVC__00025 = SVC__000 + 25;
    private static final String SVC__00026 = SVC__000 + 26;
    private static final String SVC__00027 = SVC__000 + 27;
    private static final String SVC__00028 = SVC__000 + 28;
    private static final String SVC__00029 = SVC__000 + 29;
    private static final String SVC__00030 = SVC__000 + 30;
    private static final String SVC__00031 = SVC__000 + 31;
    private static final String SVC__00032 = SVC__000 + 32;
    private static final String SVC__00033 = SVC__000 + 33;
    private static final String SVC__00034 = SVC__000 + 34;
    private static final String SVC__00035 = SVC__000 + 35;
    private static final String SVC__00036 = SVC__000 + 36;
    private static final String SVC__00037 = SVC__000 + 37;
    private static final String SVC__00038 = SVC__000 + 38;
    private static final String SVC__00039 = SVC__000 + 39;
    private static final String SVC__00040 = SVC__000 + 40;
    private static final String SVC__00041 = SVC__000 + 41;
    private static final String SVC__00042 = SVC__000 + 42;
    private static final String SVC__00043 = SVC__000 + 43;
    private static final String SVC__00044 = SVC__000 + 44;
    private static final String SVC__00045 = SVC__000 + 45;
    private static final String SVC__00046 = SVC__000 + 46;
    private static final String SVC__00047 = SVC__000 + 47;
    private static final String SVC__00048 = SVC__000 + 48;
    private static final String SVC__00049 = SVC__000 + 49;
    private static final String SVC__00050 = SVC__000 + 50;
    private static final String SVC__00051 = SVC__000 + 51;
    private static final String SVC__00052 = SVC__000 + 52;
    private static final String SVC__00053 = SVC__000 + 53;
    private static final String SVC__00054 = SVC__000 + 54;
    private static final String SVC__00055 = SVC__000 + 55;
    private static final String SVC__00056 = SVC__000 + 56;
    private static final String SVC__00057 = SVC__000 + 57;
    private static final String SVC__00058 = SVC__000 + 58;
    private static final String SVC__00059 = SVC__000 + 59;
    private static final String SVC__00060 = SVC__000 + 60;
    private static final String SVC__00061 = SVC__000 + 61;
    private static final String SVC__00062 = SVC__000 + 62;
    private static final String SVC__00063 = SVC__000 + 63;
    private static final String SVC__00064 = SVC__000 + 64;
    private static final String SVC__00065 = SVC__000 + 65;
    private static final String SVC__00066 = SVC__000 + 66;
    private static final String SVC__00067 = SVC__000 + 67;
    private static final String SVC__00068 = SVC__000 + 68;
    private static final String SVC__00069 = SVC__000 + 69;
    private static final String SVC__00070 = SVC__000 + 70;
    private static final String SVC__00071 = SVC__000 + 71;
    private static final String SVC__00072 = SVC__000 + 72;
    private static final String SVC__00073 = SVC__000 + 73;
    private static final String SVC__00074 = SVC__000 + 74;
    private static final String SVC__00075 = SVC__000 + 75;
    private static final String SVC__00076 = SVC__000 + 76;
    private static final String SVC__00077 = SVC__000 + 77;
    private static final String SVC__00078 = SVC__000 + 78;
    private static final String SVC__00079 = SVC__000 + 79;
    private static final String SVC__00080 = SVC__000 + 80;
    private static final String SVC__00081 = SVC__000 + 81;
    private static final String SVC__00082 = SVC__000 + 82;
    private static final String SVC__00083 = SVC__000 + 83;
    private static final String SVC__00084 = SVC__000 + 84;
    private static final String SVC__00085 = SVC__000 + 85;
    private static final String SVC__00086 = SVC__000 + 86;
    private static final String SVC__00087 = SVC__000 + 87;
    private static final String SVC__00088 = SVC__000 + 88;
    
    /**
     * サービスの状態を表す値。<p>
     * 初期値は、{@link #DESTROYED}である。
     * 
     * @see #getState()
     */
    protected volatile int state = DESTROYED;
    
    /**
     * サービスの名前。<p>
     *
     * @see #setServiceName(String)
     * @see #getServiceName()
     */
    protected String name;
    
    /**
     * サービスの名前。<p>
     *
     * @see #getServiceNameObject()
     */
    protected ServiceName nameObj;
    
    /**
     * このサービスが登録されるServiceManager。<p>
     */
    protected transient ServiceManager manager;
    
    /**
     * このサービスが登録されるServiceManagerのサービス名。<p>
     *
     * @see #setServiceManagerName(String)
     * @see #getServiceManagerName()
     */
    protected String managerName;
    
    /**
     * ラップする{@link ServiceBaseSupport}オブジェクト。<p>
     * ServiceBaseSupportインタフェースを実装したオブジェクトをコンストラクタの引数に渡す事で、このクラスを継承しなくても、このクラスの実装を利用できるようにするためのもの。
     */
    protected ServiceBaseSupport support;
    
    /**
     * このサービスに登録されたサービス状態リスナのリスト。<p>
     * 
     * @see #addServiceStateListener(ServiceStateListener)
     * @see #removeServiceStateListener(ServiceStateListener)
     */
    protected transient List serviceStateListeners = new ArrayList();
    
    /**
     * Service内のログ出力に使用する{@link jp.ossc.nimbus.service.log.Logger}サービスの名前。<p>
     * 
     * @see #getSystemLoggerServiceName()
     * @see #setSystemLoggerServiceName(ServiceName)
     */
    protected ServiceName loggerServiceName;
    
    /**
     * Service内のログ出力に使用する{@link jp.ossc.nimbus.service.log.Logger}サービス。<p>
     */
    protected transient LoggerWrapper logger
         = new LoggerWrapper(ServiceManagerFactory.getLogger());
    
    /**
     * Service内でのメッセージ取得に使用する{@link jp.ossc.nimbus.service.message.MessageRecordFactory}サービスの名前。<p>
     * 
     * @see #getSystemMessageRecordFactoryServiceName()
     * @see #setSystemMessageRecordFactoryServiceName(ServiceName)
     */
    protected ServiceName messageServiceName;
    
    /**
     * Service内でのメッセージ取得に使用する{@link jp.ossc.nimbus.service.message.MessageRecordFactory}サービス。<p>
     */
    protected transient MessageRecordFactoryWrapper message
         = new MessageRecordFactoryWrapper(
            ServiceManagerFactory.getMessageRecordFactory()
         );
    
    /**
     * コンストラクタ。<p>
     */
    public ServiceBase(){
    }
    
    /**
     * {@link ServiceBaseSupport}インタフェースを実装したサービスクラスをラップして、ServiceBaseの実装を利用できるようにするためのコンストラクタ。<p>
     * 
     * @param support ServiceBaseSupportインタフェースを実装したクラスのインスタンス
     */
    protected ServiceBase(ServiceBaseSupport support){
        this();
        this.support = support;
        if(support != null){
            try{
                support.setServiceBase(this);
            }catch(AbstractMethodError e){
                // 互換性担保のため例外発生時は無視
            }
        }
    }
    
    /**
     * このサービスを管理する{@link ServiceManager}を設定する。<p>
     * ServiceManagerをインスタンス変数に格納すると同時に、{@link ServiceManager#getLogger()}で取得した{@link Logger}を、このサービスの持つ{@link LoggerWrapper}のデフォルトのLoggerに設定する。また、{@link #setSystemLoggerServiceName(ServiceName)}でLoggerサービスが設定されていない場合は、LoggerWrapperのカレントのLoggerにも設定する。<br>
     *
     * @param mng このサービスを管理するServiceManager
     */
    protected void setServiceManager(ServiceManager mng){
        if(manager != null && manager.equals(mng)){
            return;
        }
        manager = mng;
        if(manager != null){
            logger.setDefaultLogger(manager.getLogger());
            if(loggerServiceName == null){
                logger.setLogger(manager.getLogger());
            }
            message.setDefaultMessageRecordFactory(
                manager.getMessageRecordFactory()
            );
            if(messageServiceName == null){
                message.setMessageRecordFactory(
                    manager.getMessageRecordFactory()
                );
            }
        }
    }
    
    /**
     * このサービスを管理する{@link ServiceManager}を取得する。<p>
     * 
     * @return このサービスを管理するServiceManager
     */
    public ServiceManager getServiceManager(){
        return manager;
    }
    
    /**
     * このサービスをロードした{@link ServiceLoader}を取得する。<p>
     *
     * @return このサービスをロードした{@link ServiceLoader}
     */
    public ServiceLoader getServiceLoader(){
        if(manager == null){
            return null;
        }
        ServiceMetaData metaData = null;
        try{
            metaData = manager.getServiceMetaData(name);
        }catch(ServiceNotFoundException e){
            return null;
        }
        return metaData.getServiceLoader();
    }
    
    /**
     * サービスを生成する。<p>
     * <b><i>このメソッドは、通常、オーバーライドしてはいけない。</i></b><br>
     * このメソッドには、以下の実装が行われている。<br>
     * <ol>
     *   <li>{@link #isNecessaryToCreate()}の呼び出し。戻り値がfalseの場合は、生成処理を中止する。</li>
     *   <li>{@link #preCreateService()}の呼び出し。例外が発生した場合は、{@link #FAILED}に遷移する。</li>
     *   <li>{@link #createService()}の呼び出し。例外が発生した場合は、FAILEDに遷移する。</li>
     *   <li>{@link #postCreateService()}の呼び出し。例外が発生した場合は、FAILEDに遷移する。</li>
     * </ol>
     *
     * @exception Exception preCreateService()、createService()、postCreateService()で例外が発生した場合
     * @see #isNecessaryToCreate()
     * @see #preCreateService()
     * @see #createService()
     * @see #postCreateService()
     */
    public synchronized void create() throws Exception{
        
        if(manager == null && managerName != null){
            setServiceManager(ServiceManagerFactory.findManager(managerName));
        }
        
        Object[] serviceName = null;
        if(managerName != null){
            serviceName = new Object[]{managerName, name};
            logger.write(SVC__00001, serviceName);
        }else{
            if(name == null){
                setServiceName(toString());
            }
            logger.write(SVC__00002, name);
        }
        
        try{
            if(!isNecessaryToCreate()){
                if(managerName != null){
                    logger.write(
                        SVC__00003,
                        new Object[]{managerName, name, getStateString()}
                    );
                }else{
                    logger.write(
                        SVC__00004,
                        new Object[]{name, getStateString()}
                    );
                }
                return;
            }
            
            if(managerName != null){
                logger.write(SVC__00005, serviceName);
            }else{
                logger.write(SVC__00006, name);
            }
            preCreateService();
            if(managerName != null){
                logger.write(SVC__00007, serviceName);
            }else{
                logger.write(SVC__00008, name);
            }
            
            if(managerName != null){
                logger.write(SVC__00009, serviceName);
            }else{
                logger.write(SVC__00010, name);
            }
            createService();
            if(managerName != null){
                logger.write(SVC__00011, serviceName);
            }else{
                logger.write(SVC__00012, name);
            }
            
            if(managerName != null){
                logger.write(SVC__00013, serviceName);
            }else{
                logger.write(SVC__00014, name);
            }
            postCreateService();
            if(managerName != null){
                logger.write(SVC__00015, serviceName);
            }else{
                logger.write(SVC__00016, name);
            }
        }catch(Exception e){
            if(managerName != null){
                logger.write(SVC__00017, serviceName, e);
            }else{
                logger.write(SVC__00018, name, e);
            }
            state = FAILED;
            processStateChanged(FAILED);
            throw e;
        }
        
        if(managerName != null){
            logger.write(SVC__00019, serviceName);
        }else{
            logger.write(SVC__00020, name);
        }
    }
    
    /**
     * サービスを生成する必要があるか調べる。<p>
     * サービス状態が{@link #CREATING}または、{@link #CREATED}、{@link #STARTED}の場合、サービスを生成する必要がないと判断してfalse返す。<br>
     *
     * @return サービスを生成する必要がある場合true、そうでない場合false
     * @exception Exception 不正な状態でサービスを生成しようとした場合。ここでは、throwされない。オーバーライドした場合に、必要ならば例外をthrowできる。
     * @see #create()
     */
    protected boolean isNecessaryToCreate() throws Exception{
        return !(state == CREATED || state == CREATING || state == STARTED);
    }
    
    /**
     * サービスを生成する前処理を行う。<p>
     * <b><i>このメソッドは、通常、オーバーライドしてはいけない。</i></b><br>
     * このメソッドには、以下の実装が行われている。<br>
     * <ol>
     *   <li>サービス状態の遷移。{@link #CREATING}に遷移する。</li>
     * </ol>
     *
     * @exception Exception {@link #processStateChanged(int)}で例外が発生した場合
     * @see #create()
     */
    protected void preCreateService() throws Exception{
        
        state = CREATING;
        processStateChanged(CREATING);
    }
    
    /**
     * サービスを生成する後処理を行う。<p>
     * <b><i>このメソッドは、通常、オーバーライドしてはいけない。</i></b><br>
     * このメソッドには、以下の実装が行われている。<br>
     * <ol>
     *   <li>ServiceManagerへの登録。{@link #getServiceManagerName()}で取得できるサービス名のServiceManagerに、{@link #getServiceName()}で取得できるサービス名で、自分自身を登録する。どちらかがnullの場合、登録されない。</li>
     *   <li>サービス状態の遷移。createService()の呼び出しが正常に行われると、{@link #CREATED}に遷移する。</li>
     * </ol>
     *
     * @exception Exception {@link #processStateChanged(int)}で例外が発生した場合
     * @see #create()
     */
    protected void postCreateService() throws Exception{
        
        if(manager == null && managerName != null){
            setServiceManager(ServiceManagerFactory.findManager(managerName));
        }
        if(manager != null && getServiceName() != null){
            if(manager.isRegisteredService(getServiceName())){
                final Service registeredService
                     = manager.getService(getServiceName());
                if(registeredService != this){
                    logger.write(
                        SVC__00088,
                        new Object[]{managerName, name}
                    );
                    manager.stopService(getServiceName());
                    manager.destroyService(getServiceName());
                    if(manager.isRegisteredService(getServiceName())){
                        manager.unregisterService(getServiceName());
                    }
                    manager.registerService(getServiceName(), this);
                }
            }else{
                manager.registerService(getServiceName(), this);
            }
        }
        
        state = CREATED;
        processStateChanged(CREATED);
    }
    
    // ServiceBaseMBean のJavaDoc
    public synchronized void restart() throws Exception{
        stop();
        start();
    }
    
    /**
     * サービスを開始する。<p>
     * <b><i>このメソッドは、通常、オーバーライドしてはいけない。</i></b><br>
     * このメソッドには、以下の実装が行われている。<br>
     * <ol>
     *   <li>{@link #isNecessaryToStart()}の呼び出し。戻り値がfalseの場合は、開始処理を中止する。</li>
     *   <li>{@link #preStartService()}の呼び出し。例外が発生した場合は、{@link #FAILED}に遷移する。</li>
     *   <li>{@link #startService()}の呼び出し。例外が発生した場合は、FAILEDに遷移する。</li>
     *   <li>{@link #postStartService()}の呼び出し。例外が発生した場合は、FAILEDに遷移する。</li>
     * </ol>
     *
     * @exception IllegalStateException サービス状態チェックに失敗した場合
     * @exception Exception preStartService()、startService()、postStartService()で例外が発生した場合
     * @see #preStartService()
     * @see #startService()
     * @see #postStartService()
     */
    public synchronized void start() throws Exception{
        
        Object[] serviceName = null;
        if(managerName != null){
            serviceName = new Object[]{managerName, name};
            logger.write(SVC__00021, serviceName);
        }else{
            logger.write(SVC__00022, name);
        }
        
        try{
            if(!isNecessaryToStart()){
                if(managerName != null){
                    logger.write(
                        SVC__00023,
                        new Object[]{managerName, name, getStateString()}
                    );
                }else{
                    logger.write(
                        SVC__00024,
                        new Object[]{name, getStateString()}
                    );
                }
                return;
            }
            
            if(managerName != null){
                logger.write(SVC__00025, serviceName);
            }else{
                logger.write(SVC__00026, name);
            }
            preStartService();
            if(managerName != null){
                logger.write(SVC__00027, serviceName);
            }else{
                logger.write(SVC__00028, name);
            }
            
            if(managerName != null){
                logger.write(SVC__00029, serviceName);
            }else{
                logger.write(SVC__00030, name);
            }
            startService();
            if(managerName != null){
                logger.write(SVC__00031, serviceName);
            }else{
                logger.write(SVC__00032, name);
            }
            
            if(managerName != null){
                logger.write(SVC__00033, serviceName);
            }else{
                logger.write(SVC__00034, name);
            }
            postStartService();
            if(managerName != null){
                logger.write(SVC__00035, serviceName);
            }else{
                logger.write(SVC__00036, name);
            }
        }catch(Exception e){
            if(managerName != null){
                logger.write(SVC__00037, serviceName, e);
            }else{
                logger.write(SVC__00038, name, e);
            }
            state = FAILED;
            processStateChanged(FAILED);
            throw e;
        }
        
        if(managerName != null){
            logger.write(SVC__00039, serviceName);
        }else{
            logger.write(SVC__00040, name);
        }
    }
    
    /**
     * サービスを開始する必要があるか調べる。<p>
     * サービス状態が{@link #STARTING}または、{@link #STARTED}の場合、サービスを開始する必要がないと判断してfalse返す。<br>
     *
     * @return サービスを開始する必要がある場合true、そうでない場合false
     * @exception Exception サービス状態が{@link #DESTROYED}または、{@link #FAILED}で、サービスを開始しようとした場合
     * @see #start()
     */
    protected boolean isNecessaryToStart() throws Exception{
        if(state == DESTROYED){
            throw new IllegalStateException(
                message.findEmbedMessage(
                    SVC__00041,
                    new Object[]{STATES[STARTING], getStateString()}
                )
            );
        }
        if(state == FAILED){
            // TODO 待機させる？
            return false;
        }
        if(state == STARTED || state == STARTING){
            return false;
        }
        return true;
    }
    
    /**
     * サービスを開始する前処理を行う。<p>
     * <b><i>このメソッドは、通常、オーバーライドしてはいけない。</i></b><br>
     * このメソッドには、以下の実装が行われている。<br>
     * <ol>
     *   <li>サービス状態の遷移。{@link #STARTING}に遷移する。</li>
     * </ol>
     *
     * @exception Exception {@link #processStateChanged(int)}で例外が発生した場合
     * @see #start()
     */
    protected void preStartService() throws Exception{
        state = STARTING;
        processStateChanged(STARTING);
    }
    
    /**
     * サービスを開始する。<p>
     * <b><i>このメソッドは、通常、オーバーライドしてはいけない。</i></b><br>
     * このメソッドには、以下の実装が行われている。<br>
     * <ol>
     *   <li>サービス状態の遷移。startService()の呼び出しが正常に行われると、{@link #STARTED}に遷移する。</li>
     * </ol>
     *
     * @exception Exception {@link #processStateChanged(int)}で例外が発生した場合
     * @see #start()
     */
    protected void postStartService() throws Exception{
        state = STARTED;
        processStateChanged(STARTED);
    }
    
    /**
     * サービスを停止する。<p>
     * <b><i>このメソッドは、通常、オーバーライドしてはいけない。</i></b><br>
     * このメソッドには、以下の実装が行われている。<br>
     * <ol>
     *   <li>{@link #isNecessaryToStop()}の呼び出し。戻り値がfalseの場合は、停止処理を中止する。</li>
     *   <li>{@link #preStopService()}の呼び出し。例外が発生した場合は、{@link #FAILED}に遷移する。</li>
     *   <li>{@link #stopService()}の呼び出し。例外が発生した場合は、FAILEDに遷移する。</li>
     *   <li>{@link #postStopService()}の呼び出し。例外が発生した場合は、FAILEDに遷移する。</li>
     * </ol>
     *
     * @see #preStopService()
     * @see #stopService()
     * @see #postStopService()
     */
    public synchronized void stop(){
        
        Object[] serviceName = null;
        if(managerName != null){
            serviceName = new Object[]{managerName, name};
            logger.write(SVC__00042, serviceName);
        }else{
            logger.write(SVC__00043, name);
        }
        
        try{
            if(!isNecessaryToStop()){
                if(managerName != null){
                    logger.write(
                        SVC__00044,
                        new Object[]{managerName, name, getStateString()}
                    );
                }else{
                    logger.write(
                        SVC__00045,
                        new Object[]{name, getStateString()}
                    );
                }
                return;
            }
            
            if(managerName != null){
                logger.write(SVC__00046, serviceName);
            }else{
                logger.write(SVC__00047, name);
            }
            preStopService();
            if(managerName != null){
                logger.write(SVC__00048, serviceName);
            }else{
                logger.write(SVC__00049, name);
            }
            
            if(managerName != null){
                logger.write(SVC__00050, serviceName);
            }else{
                logger.write(SVC__00051, name);
            }
            stopService();
            if(managerName != null){
                logger.write(SVC__00052, serviceName);
            }else{
                logger.write(SVC__00053, name);
            }
            
            if(managerName != null){
                logger.write(SVC__00054, serviceName);
            }else{
                logger.write(SVC__00055, name);
            }
            postStopService();
            if(managerName != null){
                logger.write(SVC__00056, serviceName);
            }else{
                logger.write(SVC__00057, name);
            }
        }catch(Exception e){
            if(managerName != null){
                logger.write(SVC__00058, serviceName, e);
            }else{
                logger.write(SVC__00059, name, e);
            }
            state = FAILED;
            try{
                processStateChanged(FAILED);
            }catch(Exception ex){
                if(managerName != null){
                    logger.write(
                        SVC__00060,
                        new Object[]{managerName, name, getStateString()},
                        ex
                    );
                }else{
                    logger.write(
                        SVC__00061,
                        new Object[]{name, getStateString()},
                        ex
                    );
                }
                state = FAILED;
                return;
            }
            return;
        }
        
        if(managerName != null){
            logger.write(SVC__00062, serviceName);
        }else{
            logger.write(SVC__00063, name);
        }
    }
    
    /**
     * サービスを停止する必要があるか調べる。<p>
     * サービス状態が{@link #STARTED}の場合、サービスを停止する必要があると判断してtrue返す。<br>
     *
     * @return サービスを停止する必要がある場合true、そうでない場合false
     * @exception Exception 不正な状態でサービスを停止しようとした場合。ここでは、throwされない。オーバーライドした場合に、必要ならば例外をthrowできる。
     * @see #stop()
     */
    protected boolean isNecessaryToStop() throws Exception{
        return state == STARTED;
    }
    
    /**
     * サービスを停止する前処理を行う。<p>
     * <b><i>このメソッドは、通常、オーバーライドしてはいけない。</i></b><br>
     * このメソッドには、以下の実装が行われている。<br>
     * <ol>
     *   <li>サービス状態チェック。サービス状態が{@link #STARTED}でない場合、処理を行わずに返す。</li>
     *   <li>サービス状態の遷移。サービス状態チェックを通過すると、{@link #STOPPING}に遷移する。</li>
     * </ol>
     *
     * @exception Exception {@link #processStateChanged(int)}で例外が発生した場合
     * @see #stop()
     */
    protected void preStopService() throws Exception{
        state = STOPPING;
        processStateChanged(STOPPING);
    }
    
    /**
     * サービスを停止する後処理を行う。<p>
     * <b><i>このメソッドは、通常、オーバーライドしてはいけない。</i></b><br>
     * このメソッドには、以下の実装が行われている。<br>
     * <ol>
     *   <li>サービス状態の遷移。stopService()の呼び出しが正常に行われると、{@link #STOPPED}に遷移する。</li>
     * </ol>
     *
     * @exception Exception {@link #processStateChanged(int)}で例外が発生した場合
     * @see #stop()
     */
    protected void postStopService() throws Exception{
        state = STOPPED;
        processStateChanged(STOPPED);
    }
    
    /**
     * サービスを破棄する。<p>
     * <b><i>このメソッドは、通常、オーバーライドしてはいけない。</i></b><br>
     * このメソッドには、以下の実装が行われている。<br>
     * <ol>
     *   <li>{@link #isNecessaryToDestroy()}の呼び出し。戻り値がfalseの場合は、破棄処理を中止する。</li>
     *   <li>{@link #preDestroyService()}の呼び出し。例外が発生した場合は、{@link #FAILED}に遷移する。</li>
     *   <li>{@link #destroyService()}の呼び出し。例外が発生した場合は、FAILEDに遷移する。</li>
     *   <li>{@link #postDestroyService()}の呼び出し。例外が発生した場合は、FAILEDに遷移する。</li>
     * </ol>
     *
     * @see #preDestroyService()
     * @see #destroyService()
     * @see #postDestroyService()
     */
    public synchronized void destroy(){
        
        Object[] serviceName = null;
        if(managerName != null){
            serviceName = new Object[]{managerName, name};
            logger.write(SVC__00064, serviceName);
        }else{
            logger.write(SVC__00065, name);
        }
        
        try{
            if(!isNecessaryToDestroy()){
                if(managerName != null){
                    logger.write(
                        SVC__00066,
                        new Object[]{managerName, name, getStateString()}
                    );
                }else{
                    logger.write(
                        SVC__00067,
                        new Object[]{name, getStateString()}
                    );
                }
                return;
            }
            
            if(managerName != null){
                logger.write(SVC__00068, serviceName);
            }else{
                logger.write(SVC__00069, name);
            }
            preDestroyService();
            if(managerName != null){
                logger.write(SVC__00070, serviceName);
            }else{
                logger.write(SVC__00071, name);
            }
            
            if(managerName != null){
                logger.write(SVC__00072, serviceName);
            }else{
                logger.write(SVC__00073, name);
            }
            destroyService();
            if(managerName != null){
                logger.write(SVC__00074, serviceName);
            }else{
                logger.write(SVC__00075, name);
            }
            
            if(managerName != null){
                logger.write(SVC__00076, serviceName);
            }else{
                logger.write(SVC__00077, name);
            }
            postDestroyService();
            if(managerName != null){
                logger.write(SVC__00078, serviceName);
            }else{
                logger.write(SVC__00079, name);
            }
        }catch(Exception e){
            if(managerName != null){
                logger.write(SVC__00080, serviceName, e);
            }else{
                logger.write(SVC__00081, name, e);
            }
            state = FAILED;
            try{
                processStateChanged(FAILED);
            }catch(Exception ex){
                if(managerName != null){
                    logger.write(
                        SVC__00060,
                        new Object[]{managerName, name, getStateString()},
                        ex
                    );
                }else{
                    logger.write(
                        SVC__00061,
                        new Object[]{name, getStateString()},
                        ex
                    );
                }
                state = FAILED;
                return;
            }
            return;
        }
        
        if(managerName != null){
            logger.write(SVC__00082, serviceName);
        }else{
            logger.write(SVC__00083, name);
        }
    }
    
    /**
     * サービスを破棄する必要があるか調べる。<p>
     * サービス状態が{@link #DESTROYING}または{@link #DESTROYED}の場合、サービスを破棄する必要がないと判断してfalse返す。<br>
     *
     * @return サービスを破棄する必要がある場合true、そうでない場合false
     * @exception Exception 不正な状態でサービスを破棄しようとした場合。ここでは、throwされない。オーバーライドした場合に、必要ならば例外をthrowできる。
     * @see #stop()
     */
    protected boolean isNecessaryToDestroy() throws Exception{
        return !(state == DESTROYED || state == DESTROYING);
    }
    
    /**
     * サービスを破棄する前処理を行う。<p>
     * <b><i>このメソッドは、通常、オーバーライドしてはいけない。</i></b><br>
     * このメソッドには、以下の実装が行われている。<br>
     * <ol>
     *   <li>サービス状態が{@link #STOPPED}でない場合、{@link #stop()}を呼び出す。</li>
     *   <li>サービス状態の遷移。{@link #DESTROYING}に遷移する。</li>
     *   <li>ServiceManagerからの削除。{@link #getServiceManagerName()}で取得できるサービス名のServiceManagerから、{@link #getServiceName()}で取得できるサービス名で、自分自身を削除する。どちらかがnullの場合、削除されない。</li>
     * </ol>
     *
     * @exception Exception {@link #processStateChanged(int)}で例外が発生した場合
     * @see #destroy()
     */
    protected void preDestroyService() throws Exception{
        if(state != STOPPED){
            stop();
        }
        
        state = DESTROYING;
        processStateChanged(DESTROYING);
        
        if(manager == null && managerName != null){
            setServiceManager(ServiceManagerFactory.findManager(managerName));
        }
        if(manager != null && getServiceName() != null){
            Service service = null;
            try{
                service = manager.getService(getServiceName());
            }catch(ServiceNotFoundException e){
            }
            if(service == this){
                manager.unregisterService(getServiceName());
            }
        }
    }
    
    /**
     * サービスを破棄する後処理を行う。<p>
     * <b><i>このメソッドは、通常、オーバーライドしてはいけない。</i></b><br>
     * このメソッドには、以下の実装が行われている。<br>
     * <ol>
     *   <li>サービス状態の遷移。destroyService()の呼び出しが正常に行われると、{@link #DESTROYED}に遷移する。</li>
     * </ol>
     *
     * @exception Exception {@link #processStateChanged(int)}で例外が発生した場合
     * @see #destroy()
     */
    protected void postDestroyService() throws Exception{
        state = DESTROYED;
        processStateChanged(DESTROYED);
    }
    
    /**
     * サービス名を設定する。<p>
     * {@link ServiceLoader}でサービスをロードする場合は、ServiceLoaderが設定する。<br>
     *
     * @param name サービス名
     * @see #getServiceName()
     */
    public void setServiceName(String name){
        if(name != null && name.length() != 0){
            this.name = name;
            if(managerName != null){
                nameObj = new ServiceName(managerName, name);
            }
        }
    }
    
    // ServiceのJavaDoc
    public String getServiceName(){
        return name;
    }
    
    // ServiceのJavaDoc
    public ServiceName getServiceNameObject(){
        return nameObj;
    }
    
    // ServiceのJavaDoc
    public int getState(){
        return state;
    }
    
    // ServiceのJavaDoc
    public String getStateString(){
        return STATES[state];
    }
    
    /**
     * サービスを生成する。<p>
     * このサービスに必要なオブジェクトの生成などの初期化処理を行う。<br>
     * このクラスを継承してサービスを実装するサービス開発者は、サービスの生成処理を、このメソッドをオーバーライドして実装すること。デフォルト実装は、空である。<br>
     *
     * @exception Exception サービスの生成処理に失敗した場合
     * @see #create()
     */
    public void createService() throws Exception{
        if(support != null){
            support.createService();
        }
    }
    
    /**
     * サービスを開始する。<p>
     * このサービスを利用可能な状態にする。このメソッドの呼び出し後は、このサービスの機能を利用できる事が保証される。<br>
     * このクラスを継承してサービスを実装するサービス開発者は、サービスの開始処理を、このメソッドをオーバーライドして実装すること。デフォルト実装は、空である。<br>
     *
     * @exception Exception サービスの開始処理に失敗した場合
     * @see #start()
     */
    public void startService() throws Exception{
        if(support != null){
            support.startService();
        }
    }
    
    /**
     * サービスを停止する。<p>
     * このサービスを利用不可能な状態にする。このメソッドの呼び出し後は、このサービスの機能を利用できる事は保証されない。<br>
     * このクラスを継承してサービスを実装するサービス開発者は、サービスの停止処理を、このメソッドをオーバーライドして実装すること。デフォルト実装は、空である。<br>
     *
     * @exception Exception サービスの停止処理に失敗した場合。但し、stop()で握り潰されて、処理は続行される。
     * @see #stop()
     */
    public void stopService() throws Exception{
        if(support != null){
            support.stopService();
        }
    }
    
    /**
     * サービスを破棄する。<p>
     * このサービスで使用するリソースを開放する。このメソッドの呼び出し後は、このサービスの機能を利用できる事は保証されない。<br>
     * このクラスを継承してサービスを実装するサービス開発者は、サービスの破棄処理を、このメソッドをオーバーライドして実装すること。デフォルト実装は、空である。<br>
     *
     * @exception Exception サービスの破棄処理に失敗した場合。但し、destroy()で握り潰されて、処理は続行される。
     * @see #destroy()
     */
    public void destroyService() throws Exception{
        if(support != null){
            support.destroyService();
        }
    }
    
    // ServiceBaseMBeanのJavaDoc
    public void setSystemLoggerServiceName(final ServiceName name){
        if(ServiceManagerFactory.isRegisteredService(name)
             && ServiceManagerFactory.getService(name).getState()
                 == Service.STARTED
        ){
            loggerServiceName = name;
            if(logger != null){
                if(managerName != null){
                    logger.write(
                        SVC__00084,
                        new Object[]{managerName, this.name, loggerServiceName}
                    );
                }else{
                    logger.write(
                        SVC__00085,
                        new Object[]{this.name, loggerServiceName}
                    );
                }
                logger.setLogger(
                    (Logger)ServiceManagerFactory
                        .getServiceObject(loggerServiceName),
                    ServiceManagerFactory.getService(loggerServiceName)
                );
            }
        }else{
            ServiceManagerFactory.addServiceStateListener(
                name,
                new ServiceStateListener(){
                    public void stateChanged(ServiceStateChangeEvent e)
                     throws Exception{
                        ServiceManagerFactory
                            .removeServiceStateListener(name, this);
                        setSystemLoggerServiceName(name);
                    }
                    public boolean isEnabledState(int st){
                        return st == Service.STARTED;
                    }
                }
            );
        }
    }
    
    // ServiceBaseMBeanのJavaDoc
    public ServiceName getSystemLoggerServiceName(){
        return loggerServiceName;
    }
    
    // ServiceBaseMBeanのJavaDoc
    public void setSystemMessageRecordFactoryServiceName(
        final ServiceName name
    ){
        if(ServiceManagerFactory.isRegisteredService(name)
             && ServiceManagerFactory.getService(name).getState()
                 == Service.STARTED
        ){
            messageServiceName = name;
            if(message != null){
                if(managerName != null){
                    logger.write(
                        SVC__00086,
                        new Object[]{managerName, this.name, messageServiceName}
                    );
                }else{
                    logger.write(
                        SVC__00087,
                        new Object[]{this.name, messageServiceName}
                    );
                }
                message.setMessageRecordFactory(
                    (MessageRecordFactory)ServiceManagerFactory
                        .getServiceObject(messageServiceName),
                    ServiceManagerFactory.getService(messageServiceName)
                );
            }
        }else{
            ServiceManagerFactory.addServiceStateListener(
                name,
                new ServiceStateListener(){
                    public void stateChanged(ServiceStateChangeEvent e)
                     throws Exception{
                        ServiceManagerFactory
                            .removeServiceStateListener(name, this);
                        setSystemMessageRecordFactoryServiceName(name);
                    }
                    public boolean isEnabledState(int st){
                        return st == Service.STARTED;
                    }
                }
            );
        }
    }
    
    // ServiceBaseMBeanのJavaDoc
    public ServiceName getSystemMessageRecordFactoryServiceName(){
        return messageServiceName;
    }
    
    /**
     * Service内のログ出力に使用する{@link jp.ossc.nimbus.service.log.Logger}を取得する。<p>
     *
     * @return Service内のログ出力に使用する{@link jp.ossc.nimbus.service.log.Logger}
     */
    public Logger getLogger(){
        return logger;
    }
    
    /**
     * Service内のログ出力に使用する{@link jp.ossc.nimbus.service.log.Logger}を設定する。<p>
     *
     * @param log Service内のログ出力に使用する{@link jp.ossc.nimbus.service.log.Logger}
     */
    public void setLogger(Logger log){
        if(log != null){
            if(log instanceof Service){
                final Service logService = (Service)log;
                final String managerName = logService.getServiceManagerName();
                final String serviceName = logService.getServiceName();
                if(managerName != null && serviceName != null){
                    setSystemLoggerServiceName(
                        new ServiceName(managerName, serviceName)
                    );
                    return;
                }
            }
            if(managerName != null){
                logger.write(
                    SVC__00084,
                    new Object[]{managerName, this.name, null}
                );
            }else{
                logger.write(
                    SVC__00085,
                    new Object[]{this.name, null}
                );
            }
            logger.setLogger(
                log,
                (log instanceof Service) ? (Service)log : null
            );
        }
    }
    
    /**
     * Service内でのメッセージ取得に使用する{@link jp.ossc.nimbus.service.message.MessageRecordFactory}サービスを取得する。<p>
     *
     * @return Service内でのメッセージ取得に使用する{@link jp.ossc.nimbus.service.message.MessageRecordFactory}サービス
     */
    public MessageRecordFactory getMessageRecordFactory(){
        return message;
    }
    
    /**
     * Service内でのメッセージ取得に使用する{@link jp.ossc.nimbus.service.message.MessageRecordFactory}を設定する。<p>
     *
     * @param msg Service内でのメッセージ取得に使用する{@link jp.ossc.nimbus.service.message.MessageRecordFactory}
     */
    public void setMessageRecordFactory(MessageRecordFactory msg){
        if(msg != null){
            if(msg instanceof Service){
                final Service msgService = (Service)msg;
                final String managerName = msgService.getServiceManagerName();
                final String serviceName = msgService.getServiceName();
                if(managerName != null && serviceName != null){
                    setSystemMessageRecordFactoryServiceName(
                        new ServiceName(managerName, serviceName)
                    );
                    return;
                }
            }
            if(managerName != null){
                logger.write(
                    SVC__00086,
                    new Object[]{managerName, this.name, null}
                );
            }else{
                logger.write(
                    SVC__00087,
                    new Object[]{this.name, null}
                );
            }
            message.setMessageRecordFactory(
                msg,
                (msg instanceof Service) ? (Service)msg : null
            );
        }
    }
    
    /**
     * このサービスを登録する{@link ServiceManager}のサービス名を取得する。<p>
     *
     * @return ServiceManagerのサービス名
     * @see #setServiceManagerName(String)
     */
    public String getServiceManagerName(){
        return managerName;
    }
    
    /**
     * このサービスを登録する{@link ServiceManager}のサービス名を設定する。<p>
     * {@link ServiceLoader}でサービスをロードする場合は、ServiceLoaderが、登録すべきServiceManagerを知っており、該当するServiceManagerに登録する時に、登録するServiceManagerによって設定される。<br>
     *
     * @param name ServiceManagerのサービス名
     * @see #getServiceManagerName()
     */
    public void setServiceManagerName(String name){
        managerName = name;
        if(name != null){
            if(getServiceName() != null){
                nameObj = new ServiceName(managerName, getServiceName());
            }
        }
    }
    
    /**
     * {@link ServiceBaseSupport}のラップとして生成された場合に、ラップするServiceBaseSupportのインスタンスを取得する。<p>
     *
     * @see #ServiceBase(ServiceBaseSupport)
     */
    public Object getTarget(){
        if(support != null){
            return support;
        }
        return this;
    }
    
    /**
     * サービスの状態が変更された事を監視するServiceStateListenerを追加する。<p>
     *
     * @param listener ServiceStateListenerオブジェクト
     */
    public void addServiceStateListener(ServiceStateListener listener){
        if(!serviceStateListeners.contains(listener)){
            serviceStateListeners.add(listener);
        }
    }
    
    /**
     * サービスの状態が変更された事を監視するServiceStateListenerを削除する。<p>
     *
     * @param listener ServiceStateListenerオブジェクト
     */
    public void removeServiceStateListener(ServiceStateListener listener){
        serviceStateListeners.remove(listener);
    }
    
    /**
     * サービスの状態が変更された事をServiceStateListenerに通知する。<p>
     * サービスの状態が変更された場合に、呼び出されます。但し、オーバーライドする場合は、必ずsuper.processStateChanged(int)を呼び出すこと。<br>
     * 
     * @param state 変更後のサービスの状態
     * @see ServiceStateListener
     */
    protected void processStateChanged(int state) throws Exception{
        final Iterator listeners
             = new ArrayList(serviceStateListeners).iterator();
        while(listeners.hasNext()){
            final ServiceStateListener listener
                 = (ServiceStateListener)listeners.next();
            if(listener.isEnabledState(state)){
                listener.stateChanged(new ServiceStateChangeEvent(this));
            }
        }
    }
    
    /**
     * デシリアライズを行う。<p>
     *
     * @param in デシリアライズの元情報となるストリーム
     * @exception IOException 読み込みに失敗した場合
     * @exception ClassNotFoundException デシリアライズしようとしたオブジェクトのクラスが見つからない場合
     */
    private void readObject(java.io.ObjectInputStream in)
     throws IOException, ClassNotFoundException{
        in.defaultReadObject();
        logger = new LoggerWrapper(ServiceManagerFactory.getLogger());
        message = new MessageRecordFactoryWrapper(
            ServiceManagerFactory.getMessageRecordFactory()
        );
        serviceStateListeners = new ArrayList();
        if(manager == null && managerName != null){
            setServiceManager(ServiceManagerFactory.findManager(managerName));
        }
        logger = new LoggerWrapper(ServiceManagerFactory.getLogger());
        if(loggerServiceName != null){
            setSystemLoggerServiceName(loggerServiceName);
        }
        message = new MessageRecordFactoryWrapper(
            ServiceManagerFactory.getMessageRecordFactory()
        );
        if(messageServiceName != null){
            setSystemMessageRecordFactoryServiceName(messageServiceName);
        }
    }
    
    /**
     * このオブジェクトと他のオブジェクトが等しいかどうかを示す。<p>
     * 以下の順で、等価比較を行う。<br>
     * <ol>
     *   <li>objがnullの場合、false</li>
     *   <li>objの参照がこのインスタンスの参照と等しい場合、true</li>
     *   <li>{@link #getServiceName()}がnullの場合、{Object#equals(Object)}に委譲</li>
     *   <li>objがServiceのインスタンスでない場合、false</li>
     *   <li>objがServiceのインスタンスで、その{@link Service#getServiceManagerName()}とこのインスタンスの{@link #getServiceManagerName()}が等しく、その{@link Service#getServiceName()}とこのインスタンスの{@link #getServiceName()}が等しい場合、true。そうでない場合、false</li>
     * </ol>
     *
     * @param obj 比較対象の参照オブジェクト
     * @return obj 引数に指定されたオブジェクトとこのオブジェクトが等しい場合はtrue、そうでない場合はfalse
     */
    public boolean equals(Object obj){
        if(obj == null){
            return false;
        }
        if(this == obj){
            return true;
        }
        if(name == null){
            return super.equals(obj);
        }
        if(obj instanceof Service){
            final Service service = (Service)obj;
            final String mngName = service.getServiceManagerName();
            if(managerName == null){
                return mngName != null
                     ? false : super.equals(obj);
            }else{
                if(managerName.equals(mngName)
                     && name.equals(service.getServiceName())){
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * オブジェクトのハッシュコード値を返す。<p>
     *
     * @return ハッシュコード値
     */
    public int hashCode(){
        if(name == null){
            return super.hashCode();
        }else{
            return managerName != null
                 ? managerName.hashCode() + name.hashCode() : name.hashCode();
        }
    }
    
    
    /**
     * このインスタンスの文字列表現を返す。<p>
     *
     * @return このインスタンスの文字列表現
     */
    public String toString(){
        StringBuilder buf = new StringBuilder();
        if(support != null){
            buf.append(support.toString());
        }else{
            buf.append(super.toString());
        }
        if(managerName != null || name != null){
            buf.append(':');
        }
        if(managerName != null){
            buf.append(managerName);
        }
        if(managerName != null){
            buf.append('#');
        }
        if(name != null){
            buf.append(name);
        }
        return buf.toString();
    }
    
    /**
     * サービスが停止及び破棄されずにガベージされようとした場合に、停止処理及び破棄処理を行う。<p>
     *
     * @exception Throwable 停止、及び破棄処理に失敗した場合
     */
    protected void finalize() throws Throwable{
        try{
            final int state = getState();
            if(state <= STARTED){
                final String name = getServiceName();
                final ServiceManager manager = getServiceManager();
                if(name == null && manager == null){
                    if(isNecessaryToStop()) stop();
                    if(isNecessaryToDestroy()) destroy();
                }else{
                    Service service = null;
                    try{
                        service = manager.getService(name);
                    }catch(ServiceNotFoundException e){
                    }
                    if(service == this){
                        manager.stopService(name);
                        manager.destroyService(name);
                    }
                }
            }
        }catch(Throwable th){
        }
        super.finalize();
    }
}