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

import jp.ossc.nimbus.service.log.Logger;
import jp.ossc.nimbus.service.repository.*;
import jp.ossc.nimbus.service.message.MessageRecordFactory;
/**
 * サービス管理インタフェース。<p>
 * {@link Service}インスタンスに名前を付けて管理し、提供するインタフェースである。<br>
 * サービスの登録、登録解除、取得などのサービスを提供するメソッドと、サービスの登録先となる{@link Repository}を設定するメソッドを持つ。<br>
 * また、{@link Service}のサブインタフェースとなっているため、実装クラスはサービスとして実装される。更に、{@link RegistrationBroadcaster}のサブインタフェースとなっており、実装クラスはサービスの登録・登録解除を{@link RegistrationListener}に通知する責任を負う。<br>
 *
 * @author M.Takata
 */
public interface ServiceManager
 extends ServiceBaseMBean, RegistrationBroadcaster{
    
    /**
     * このサービスのデフォルトのサービス名。<p>
     */
    public static final String DEFAULT_NAME = "Nimbus";
    
    /**
     * 指定した名前のサービスを取得する。<p>
     * このメソッドで取得できるオブジェクトは、サービスを提供するインタフェースにキャストできるとは限らない。<br>
     * サービスの生成・起動・停止・破棄などの操作を行いたい場合に、このメソッドでインスタンスを取得する。<br>
     *
     * @param name サービスの名前
     * @return Serviceオブジェクト
     * @exception ServiceNotFoundException サービスが見つからない場合
     */
    public Service getService(String name) throws ServiceNotFoundException;
    
    /**
     * 指定した名前のサービスを提供するオブジェクトを取得する。<p>
     * このメソッドで取得できるオブジェクトは、サービスを提供するインタフェースにキャストできる事を保証する。<br>
     * サービスの生成・起動・停止・破棄などの操作を行いたい場合は、{@link #getService(String)}でインスタンスを取得する。<br>
     *
     * @param name サービスの名前
     * @return Serviceオブジェクト
     * @exception ServiceNotFoundException サービスが見つからない場合
     */
    public Object getServiceObject(String name) throws ServiceNotFoundException;
    
    /**
     * 指定した名前のサービスの状態変更を通知する{@link ServiceStateBroadcaster}を取得する。<p>
     * 登録されていないサービスのServiceStateBroadcasterは取得できない。<br>
     * また、{@link Service}インタフェースを直接実装したサービスを登録した場合、そのクラスがServiceStateBroadcasterを実装していないと取得できない。<br>
     *
     * @param name サービスの名前
     * @return ServiceStateBroadcasterオブジェクト
     * @exception ServiceNotFoundException サービスが見つからない場合
     */
    public ServiceStateBroadcaster getServiceStateBroadcaster(String name)
     throws ServiceNotFoundException;
    
    /**
     * 指定した名前のサービスの定義情報を取得する。<p>
     * {@link ServiceLoader}でロードしたサービスの定義情報を取得する。<br>
     * 
     * @param name サービスの名前
     * @return サービス定義情報
     * @exception ServiceNotFoundException サービスが見つからない場合
     */
    public ServiceMetaData getServiceMetaData(String name)
     throws ServiceNotFoundException;
    
    /**
     * サービスを登録する。<p>
     * objに指定するオブジェクトが{@link Service}インタフェースを実装していない場合は、Serviceインタフェースを実装したオブジェクトにラップされて登録される。これにより、任意のオブジェクトをサービスと同様に扱う事ができる。<br>
     * 但し、サービスの実装に依存して動作的な制限は発生する。以下に、サービスとして登録するオブジェクトを、推奨する実装方法の順で、説明する。<br>
     * <ol>
     *   <li>{@link ServiceBase}のサブクラス<br>引数なしのコンストラクタで生成される。ロード時に、{@link ServiceBase#setServiceName(String)}、{@link ServiceBase#createService()}、{@link ServiceBase#startService()}を呼び出す。また、{@link ServiceManager#create()}、{@link ServiceManager#start()}、{@link ServiceManager#stop()}、{@link ServiceManager#destroy()}を呼び出すと、登録されているServiceの対応するメソッドを呼び出す。ServiceBaseは、JMXに対応しており{@link ServiceBaseMBean}インタフェースをMBeanインタフェースとして持っている。</li>
     *   <li>{@link FactoryServiceBase}のサブクラス<br>FactoryServiceBaseは、ServiceBaseのサブクラスであるため、上記のServiceBaseのサブクラスに準拠する。但し、{@link ServiceManager#getServiceObject(String)}を呼び出された場合、FactoryServiceBaseの実装クラスのインスタンスは返さずに、{@link FactoryService#newInstance()}で生成されるオブジェクトを返す。</li>
     *  <li>{@link ServiceBaseSupport}インタフェース実装クラス<br>引数なしのコンストラクタで生成され、ServiceBaseクラスでラップされて、ServiceManagerに登録される。ロード時に、{@link ServiceBase#createService()}、{@link ServiceBase#startService()}を呼び出す。また、ServiceManager.create()、ServiceManager.start()、ServiceManager.stop()、ServiceManager.destroy()を呼び出すと、ServiceManagerに登録されているServiceBaseオブジェクトを通して、{@link ServiceBaseSupport#createService()}、{@link ServiceBaseSupport#startService()}、{@link ServiceBaseSupport#stopService()}、{@link ServiceBaseSupport#destroyService()}を呼び出す。ラッパーであるServiceBaseが、JMXに対応しているため、ServiceManagerに登録されるオブジェクトは、ServiceBaseMBeanインタフェースをMBeanインタフェースとして持つ。また、{@link FactoryService}インタフェースも実装すれば、上記のFactoryServiceBaseのサブクラスに準拠した動作をする。</li>
     *   <li>{@link Service}インタフェース実装クラス<br>引数なしのコンストラクタで生成される。ロード時に、{@link Service#setServiceName(String)}、{@link Service#create()}、{@link Service#start()}を呼び出す。また、ServiceManager.create()、ServiceManager.start()、ServiceManager.stop()、ServiceManager.destroy()を呼び出すと、Serviceの対応するメソッドを呼び出す。但し、{@link Service#getState()}に対する実装の責任を負う必要がある。JMXには、対応していないので、対応するためには、独自にMBean実装を行う必要がある。</li>
     *   <li>上記以外のクラス<br>引数なしのコンストラクタで生成される。ServiceBaseSupportインタフェースでラップされ、更にServiceBaseクラスにラップされて、ServiceManagerに登録される。Service.create()、Service.start()、Service.stop()、Service.destroy()と同じシグニチャのメソッドを持っている場合は、上記のServiceBaseのサブクラスと同じ動作をする。それ以外の場合は、ServiceManager.create()を呼び出すと、ServiceManagerへ自分自身を登録する事と、ServiceManager.destroy()を呼び出すと、ServiceManagerから自分自身を削除する。また、Service#getState()は、{@link Service#CREATED}、{@link Service#DESTROYED}以外の状態は{@link Service#UNKNOWN}となる。</li>
     * </ol>
     *
     * @param name サービスの名前
     * @param obj サービスを提供するオブジェクト
     * @exception Exception サービスの登録処理に失敗した場合
     * @return 登録できた場合true
     */
    public boolean registerService(String name, Object obj) throws Exception;
    
    /**
     * 指定されたサービスを指定されたサービス名で登録する。<p>
     *
     * @param name サービスの名前
     * @param service サービスを提供するサービスオブジェクト
     * @exception Exception サービスの登録処理に失敗した場合
     * @return 登録できた場合true
     */
    public boolean registerService(String name, Service service) throws Exception;
    
    /**
     * 指定されたサービス定義メタデータに従ったサービスを、指定されたサービス名でサービスとして登録する。<p>
     *
     * @param serviceData サービス定義メタデータ
     * @return 登録できた場合true
     * @exception Exception サービスのインスタンス化に失敗した場合
     */
    public boolean registerService(ServiceMetaData serviceData) throws Exception;
    
    /**
     * 指定されたサービス名のサービスを登録解除する。<p>
     *
     * @param name サービス名
     * @return 登録解除できた場合true
     */
    public boolean unregisterService(String name);
    
    /**
     * 指定されたサービス名のサービスが登録されているか調べる。<p>
     *
     * @param name サービス名
     * @return 登録されていた場合true
     */
    public boolean isRegisteredService(String name);
    
    /**
     * 登録されているサービス名前の集合を取得する。<p>
     *
     * @return 登録されているサービス名の集合
     */
    public Set serviceNameSet();
    
    /**
     * 登録されている{@link Service}の集合を取得する。<p>
     *
     * @return 登録されているServiceの集合
     */
    public Set serviceSet();
    
    /**
     * 登録されているサービスオブジェクトの集合を取得する。<p>
     *
     * @return 登録されているサービスオブジェクトの集合
     */
    public Set serviceObjectSet();
    
    /**
     * このマネージャで管理するサービスの登録先の{@link Repository}を設定する。<p>
     * Repositoryサービスの停止時に、自動的にデフォルトのリポジトリに切り替えます。<br>
     *
     * @param manager Repositoryインタフェースを実装したサービスが登録されているマネージャ名
     * @param service Repositoryインタフェースを実装したサービスのサービス名
     * @return 登録しているサービスを指定されたRepositoryに全て登録し直せた場合、true
     */
    public boolean setServiceRepository(String manager, String service);
    
    /**
     * このマネージャで管理するサービスの登録先の{@link Repository}を設定する。<p>
     *
     * @param repository Repositoryインタフェースを実装したオブジェクト
     * @return 登録しているサービスを指定されたRepositoryに全て登録し直せた場合、true
     */
    public boolean setServiceRepository(Repository repository);
    
    /**
     * 指定されたオブジェクトを{@link Service}インタフェースを実装したオブジェクトに変換する。<p>
     * objで指定されたオブジェクトが、Serviceインタフェースを実装する場合には、そのまま返す。<br>
     *
     * @param name サービス名
     * @param obj Serviceに変換したいオブジェクト
     * @return Serviceインタフェースを実装したオブジェクト
     */
    public Service convertObjectToService(String name, Object obj);
    
    /**
     * このマネージャに管理されるサービスをロードした{@link ServiceLoader}を登録する。<p>
     *
     * @param loader このマネージャに管理されるサービスをロードしたServiceLoader
     */
    public void addServiceLoader(ServiceLoader loader);
    
    /**
     * このマネージャに管理されるサービスをロードした{@link ServiceLoader}を削除する。<p>
     *
     * @param loader このマネージャに管理されるサービスをロードしたServiceLoader
     */
    public void removeServiceLoader(ServiceLoader loader);
    
    /**
     * このマネージャに管理されるサービスをロードした{@link ServiceLoader}の集合を取得する。<p>
     * 
     * @return このマネージャに管理されるサービスをロードした{@link ServiceLoader}の集合
     */
    public Set getServiceLoaders();
    
    /**
     * このマネージャの{@link ManagerMetaData}の集合を取得する。<p>
     * 
     * @return このマネージャの{@link ManagerMetaData}の集合
     */
    public Set getManagerMetaDatas();
    
    /**
     * マネージャプロパティを取得する。<p>
     *
     * @param name プロパティ名
     * @return プロパティ値
     */
    public String getProperty(String name);
    
    /**
     * 指定されたサービスをインスタンス化する。<p>
     *
     * @param data サービス定義情報
     * @return インスタンス化したサービス
     * @exception Exception 生成処理に失敗した場合
     */
    public Service instanciateService(ServiceMetaData data) throws Exception;
    
    /**
     * 指定されたオブジェクトを生成する。<p>
     *
     * @param data サービス定義情報
     * @return 生成したオブジェクト
     * @exception Exception 生成処理に失敗した場合
     */
    public Object createObject(ServiceMetaData data) throws Exception;
    
    /**
     * 指定されたサービスを生成する。<p>
     *
     * @param name サービス名
     * @exception Exception 生成処理に失敗した場合
     */
    public void createService(String name) throws Exception;
    
    /**
     * 指定されたサービスを生成する。<p>
     *
     * @param name サービス名
     * @param completed 生成されたサービス名の集合。依存関係により生成されたサービスを含む。
     * @exception Exception 生成処理に失敗した場合
     */
    public void createService(String name, Set completed) throws Exception;
    
    /**
     * 指定されたサービス集合を生成する。<p>
     *
     * @param names サービス名の集合
     */
    public void createService(Set names);
    
    /**
     * 指定された登録されていないサービスを生成する。<p>
     * 依存関係の解決は行わない。
     *
     * @param service 登録されていないサービス
     * @param serviceData サービスの定義情報
     * @exception Exception サービスの開始に失敗した場合
     */
    public void createService(Service service, ServiceMetaData serviceData)
     throws Exception;
    
    /**
     * 登録されている全てのサービスを生成する。<p>
     * 但し、既に生成されているサービスは、生成されない。<br>
     */
    public void createAllService();
    
    /**
     * 指定されたサービスを開始する。<p>
     *
     * @param name サービス名
     * @exception Exception 開始処理に失敗した場合
     */
    public void startService(String name) throws Exception;
    
    /**
     * 指定されたサービスを開始する。<p>
     *
     * @param name サービス名
     * @param completed 開始されたサービス名の集合。依存関係により開始されたサービスを含む。
     * @exception Exception 開始処理に失敗した場合
     */
    public void startService(String name, Set completed) throws Exception;
    
    /**
     * 指定されたサービス集合を開始する。<p>
     *
     * @param names サービス名の集合
     */
    public void startService(Set names);
    
    /**
     * 指定された登録されていないサービスを開始する。<p>
     * 依存関係の解決は行わない。
     *
     * @param service 登録されていないサービス
     * @param serviceData サービスの定義情報
     * @exception Exception サービスの開始に失敗した場合
     */
    public void startService(Service service, ServiceMetaData serviceData)
     throws Exception;
    
    /**
     * 登録されている全てのサービスを開始する。<p>
     * 但し、既に開始されているサービスは、開始されない。<br>
     */
    public void startAllService();
    
    /**
     * 指定されたサービスを再開始する。<p>
     *
     * @param name サービス名
     * @exception Exception 再開始処理に失敗した場合
     */
    public void restartService(String name) throws Exception;
    
    /**
     * 指定されたサービスを再開始する。<p>
     *
     * @param name サービス名
     * @param completed 再開始されたサービス名の集合。依存関係により再開始されたサービスを含む。
     * @exception Exception 再開始処理に失敗した場合
     */
    public void restartService(String name, Set completed) throws Exception;
    
    /**
     * 指定されたサービス集合を再開始する。<p>
     *
     * @param names サービス名の集合
     */
    public void restartService(Set names);
    
    /**
     * 指定された登録されていないサービスを再開始する。<p>
     * 依存関係の解決は行わない。
     *
     * @param service 登録されていないサービス
     * @param serviceData サービスの定義情報
     * @exception Exception サービスの再開始に失敗した場合
     */
    public void restartService(Service service, ServiceMetaData serviceData)
     throws Exception;
    
    /**
     * 登録されている全てのサービスを再開始する。<p>
     */
    public void restartAllService();
    
    /**
     * 指定されたサービスを停止する。<p>
     *
     * @param name サービス名
     */
    public void stopService(String name);
    
    /**
     * 指定されたサービスを停止する。<p>
     *
     * @param name サービス名
     * @param completed 停止されたサービス名の集合。依存関係により停止されたサービスを含む。
     */
    public void stopService(String name, Set completed);
    
    /**
     * 指定されたサービス集合を停止する。<p>
     *
     * @param names サービス名の集合
     */
    public void stopService(Set names);
    
    /**
     * 指定された登録されていないサービスを停止する。<p>
     * 依存関係の解決は行わない。
     *
     * @param service 登録されていないサービス
     * @param serviceData サービスの定義情報
     */
    public void stopService(Service service, ServiceMetaData serviceData);
    
    /**
     * 登録されている全てのサービスを停止する。<p>
     */
    public void stopAllService();
    
    /**
     * 指定されたサービスを破棄する。<p>
     *
     * @param name サービス名
     */
    public void destroyService(String name);
    
    /**
     * 指定されたサービスを破棄する。<p>
     *
     * @param name サービス名
     * @param completed 破棄されたサービス名の集合。依存関係により破棄されたサービスなどを含む。
     */
    public void destroyService(String name, Set completed);
    
    /**
     * 指定されたサービス集合を破棄する。<p>
     *
     * @param names サービス名の集合
     */
    public void destroyService(Set names);
    
    /**
     * 指定された登録されていないサービスを破棄する。<p>
     * 依存関係の解決は行わない。
     *
     * @param service 登録されていないサービス
     * @param serviceData サービスの定義情報
     */
    public void destroyService(Service service, ServiceMetaData serviceData);
    
    /**
     * 登録されている全てのサービスを破棄する。<p>
     */
    public void destroyAllService();
    
    /**
     * 指定したデプロイ待機中のサービスが、待機している原因となっているサービスの集合を取得する。<p>
     *
     * @param waitService 待機中のサービス名
     * @return 待機している原因となっているサービスの集合
     */
    public Set getWaitingCauses(String waitService);
    
    /**
     * 管理している待機中のサービスをクリアする。<p>
     */
    public void clearWaitingServices();
    
    /**
     * 待機中のサービスが存在するか調べる。<p>
     *
     * @return 待機中のサービスが存在する場合true
     */
    public boolean existWaitingService();
    
    /**
     * 待機中のサービスの名前の集合を取得する。<p>
     *
     * @return 待機中のサービスの名前の集合
     */
    public Set getWaitingServices();
    
    /**
     * 指定したデプロイに失敗したサービスが、デプロイできなかった原因となっている例外を取得する。<p>
     *
     * @param failedService デプロイに失敗したサービス名
     * @return デプロイできなかった原因となっている例外
     */
    public Throwable getFailedCause(String failedService);
    
    /**
     * 管理しているデプロイに失敗したサービスの集合をクリアする。<p>
     */
    public void clearFailedServices();
    
    /**
     * デプロイに失敗したサービスが存在するか調べる。<p>
     *
     * @return デプロイに失敗したサービスが存在する場合true
     */
    public boolean existFailedService();
    
    /**
     * デプロイに失敗したサービスの名前の集合を取得する。<p>
     *
     * @return デプロイに失敗したサービスの名前の集合
     */
    public Set getFailedServices();
     
    /**
     * 登録されたService内のログ出力に使用する{@link jp.ossc.nimbus.service.log.Logger}サービスを取得する。<p>
     *
     * @return 登録されたService内のログ出力に使用する{@link jp.ossc.nimbus.service.log.Logger}サービス
     */
    public Logger getLogger();
    
    /**
     * Service内でのメッセージ取得に使用する{@link jp.ossc.nimbus.service.message.MessageRecordFactory}サービスを取得する。<p>
     *
     * @return Service内でのメッセージ取得に使用する{@link jp.ossc.nimbus.service.message.MessageRecordFactory}サービス
     */
    public MessageRecordFactory getMessageRecordFactory();
    
    /**
     * サービスの状態が変更された事を監視するServiceStateListenerを追加する。<p>
     * 指定したサービスが登録されていない場合、RegistrationListenerを登録する。サービスが登録されると、ServiceStateListenerが登録される。<br>
     * また、指定されたサービスが{@link ServiceStateBroadcaster}を実装していない場合、ServiceStateListenerを登録できないため何もしない。<br>
     *
     * @param name サービス名
     * @param listener ServiceStateListenerオブジェクト
     */
    public void addServiceStateListener(
        String name,
        ServiceStateListener listener
    );
    
    /**
     * サービスの状態が変更された事を監視するServiceStateListenerを削除する。<p>
     *
     * @param name サービス名
     * @param listener ServiceStateListenerオブジェクト
     */
    public void removeServiceStateListener(
        String name,
        ServiceStateListener listener
    );
}