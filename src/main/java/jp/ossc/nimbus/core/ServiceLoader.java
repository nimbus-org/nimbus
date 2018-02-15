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

import java.net.*;
import java.util.*;
import java.beans.PropertyEditor;

/**
 * サービスローダインタフェース。<p>
 * サービス定義を読み込み、サービスを登録するローダのインタフェースである。<br>
 * このインタフェースを実装するサービスローダは、サービス{@link Service}として実装され、サービスの生成、起動と共に、以下の処理を行う。<br>
 * <ol>
 *   <li>サービス定義の読み込み</li>
 *   <li>{@link ServiceManager}のインスタンス生成</li>
 *   <li>ServiceManagerへのServiceLoaderの登録</li>
 *   <li>各Serviceのインスタンス生成</li>
 *   <li>各Serviceのメタデータ生成、管理</li>
 *   <li>各Serviceの依存関係の管理</li>
 *   <li>ServiceManagerへの各Serviceの登録</li>
 *   <li>ServiceManagerのサービスとしての、生成、起動</li>
 * </ol>
 * <p>
 * サービスローダの起動によって、サービス基盤が起動して、そこに配置される各サービスがホスティングされる。従って、サービスローダは、サービス基盤の起点となる。<br>
 * サービスローダの起動方法は、２つ用意されている。<p>
 * <ol>
 *   <li>{@link ServiceManagerFactory#loadManager}<br>ServiceManagerFactory.loadManager()を呼び出すと、ServiceLoaderが生成され、{@link ServiceLoader#create()}、{@link ServiceLoader#start()}が呼び出される。<br>主に、クライアントサイドで使用する場合に用いる起動方法である。<br></li>
 *   <li>{@link DefaultServiceLoaderService}<br>DefaultServiceLoaderServiceをJBossのサービスとして、jboss-service.xmlに定義して、JBossにデプロイする。DefaultServiceLoaderServiceの起動、生成に伴い、ServiceLoaderが生成され、ServiceLoader.create()、ServiceLoader.start()が呼び出される。<br>主に、サーバサイドで使用する場合に用いる起動方法である。DefaultServiceLoaderServiceは、JBossのサービスとしてデプロイ可能であるが、JBossの提供するインタフェースを実装している訳ではない。JBoss以外のアプリケーションサーバでも、サービスという概念があれば、DefaultServiceLoaderServiceをラップ、またはDefaultServiceLoaderService相当の実装を行う事で、サービスローダを起動できる。<br></li>
 * </ol>
 * 
 * @author M.Takata
 * @see DefaultServiceLoaderService
 * @see ServiceManagerFactory
 * @see ServiceManager
 * @see Service
 * @see <a href="nimbus-service_1_0.dtd">サービス定義ファイルDTD</a>
 */
public interface ServiceLoader extends Service{
    
    /**
     * &lt;server&gt;要素のメタデータを取得する。<p>
     *
     * @return サービス定義&lt;server&gt;要素メタデータ
     */
    public ServerMetaData getServerMetaData();
    
    /**
     * &lt;server&gt;要素のメタデータを設定する。<p>
     *
     * @param data サービス定義&lt;server&gt;要素メタデータ
     */
    public void setServerMetaData(ServerMetaData data);
    
    /**
     * &lt;service&gt;要素のメタデータを取得する。<p>
     * 指定したサービスが、このサービスローダがロードしたサービス定義に存在しない場合は、nullを返す。
     *
     * @param managerName サービスが登録されているServiceManagerの名前
     * @param serviceName サービス名
     * @return サービス定義&lt;service&gt;要素メタデータ
     */
    public ServiceMetaData getServiceMetaData(
        String managerName,
        String serviceName
    );
    
    /**
     * 指定したサービスのメタデータを設定する。<p>
     *
     * @param managerName サービスが登録されるServiceManagerの名前
     * @param serviceData サービス定義メタデータ
     */
    public void setServiceMetaData(
        String managerName,
        ServiceMetaData serviceData
    );
    
    /**
     * このサービスローダがロードしたServiceManagerの集合を取得する。<p>
     * ServiceManagerは、サービス定義の&lt;manager&gt;要素に対応する。<br>
     * 複数のサービス定義に跨って定義された&lt;manager&gt;要素に対応するServiceManagerは、どのサービス定義をロードしたサービスローダから取得しても、同じインスタンスが取得できる。<br>
     * &lt;manager&gt;要素が定義されていない場合は、空の集合を返す。<br>
     *
     * @return ServiceManagerの集合
     */
    public Set getServiceManagers();
    
    /**
     * &lt;service&gt;要素の子要素として定義された&lt;depends&gt;要素のメタデータのリストを取得する。<p>
     * 指定されたServiceManagerが、このサービスローダによってロードされていない場合は、nullを返す。また、指定されたサービス名のサービスが、このサービスローダによってロードされていない場合は、nullを返す。また、指定されたサービス名をname属性として持つ&lt;service&gt;要素に、&lt;depends&gt;要素が定義されていない場合は、空のリストを返す。<br>
     *
     * @param managerName サービスが登録されているServiceManagerの名前
     * @param serviceName サービス名
     * @return {@link ServiceMetaData.DependsMetaData}のリスト
     */
    public List getDepends(
        String managerName,
        String serviceName
    );
    
    /**
     * 指定されたサービスを&lt;depends&gt;要素に持つ&lt;service&gt;要素のメタデータのリストを取得する。<p>
     * 但し、このサービスローダにロードされたサービスのみが対象となる。<br>
     * 指定されたサービスを&lt;depends&gt;要素に持つサービスが定義されていない場合は、空のリストを返す。<br>
     *
     * @param managerName サービスが登録されているServiceManagerの名前
     * @param serviceName サービス名
     * @return {@link ServiceMetaData}のリスト
     */
    public List getDependedServices(
        String managerName,
        String serviceName
    );
    
    /**
     * サービス定義ファイルのURLを設定する。<p>
     *
     * @param url サービス定義ファイルのURL
     * @exception IllegalArgumentException 指定されたURLが有効なURLでない場合
     * @see #getServiceURL()
     */
    public void setServiceURL(URL url) throws IllegalArgumentException;
    
    /**
     * サービス定義ファイルのURLを取得する。<p>
     *
     * @return サービス定義ファイルのURL
     * @see #setServiceURL(URL)
     */
    public URL getServiceURL();
    
    /**
     * サービス定義ファイルのパスを設定する。<p>
     * ここで指定されたパスは、以下の手順でURLに変更されて、{@link #setServiceURL(URL)}される。<br>
     * パス→URL変換は、以下の順序で行われる。<br>
     * <ol>
     *   <li>指定されたパスがnull、または空文字の場合、デフォルトURL（後述）</li>
     *   <li>指定されたパスがローカルファイルとして存在する場合、ローカルパスをURLに変換したURL</li>
     *   <li>指定されたパスがこのクラスをロードしたクラスローダのリソースとして存在する場合、そのURL</li>
     *   <li>上記全てに当てはまらない場合、例外をthrowする。</li>
     * </ol>
     * デフォルトURLの決定は、以下の順序で行われる。<br>
     * <ol>
     *   <li>システムプロパティjp.ossc.nimbus.service.urlで指定された値を、上記のパス→URL変換でURLに変換した値</li>
     *   <li>このクラスのクラスファイルがロードされたクラスパス上から、nimbus-service.xmlを{@link ClassLoader#getResource(String)}でリソースとして取得したURL。このクラスのクラスファイルがJarファイルに格納されている場合は、そのJarファイルと同じパス上のnimbus-service.xmlのURL</li>
     * </ol>
     *
     * @param path サービス定義ファイルのパス
     * @exception IllegalArgumentException 指定されたパスが有効なパスでない場合
     * @see #setServiceURL(URL)
     */ 
    public void setServicePath(String path) throws IllegalArgumentException;
    
    /**
     * &lt;manager&gt;要素を表す{@link ServiceManager}インタフェースの実装クラス名を設定する。<p>
     * クラス名は、完全修飾名で設定する。<br>
     * また、設定しない場合は、デフォルトの実装クラスが使用される。デフォルトは、jp.ossc.nimbus.core.ServiceManagerImplである。<br>
     *
     * @param className ServiceManagerインタフェースの実装クラス名
     * @exception ClassNotFoundException 指定したクラス名のクラスが見つからない場合
     * @exception IllegalArgumentException 指定したクラス名のクラスがServiceManagerインタフェースを実装していない場合
     * @see #getServiceManagerClassName()
     * @see ServiceManager
     */
    public void setServiceManagerClassName(String className)
     throws ClassNotFoundException, IllegalArgumentException;
    
    /**
     * &lt;manager&gt;要素を表す{@link ServiceManager}インタフェースの実装クラス名を取得する。<p>
     *
     * @return ServiceManagerインタフェースの実装クラス名
     * @see #setServiceManagerClassName(String)
     * @see ServiceManager
     */
    public String getServiceManagerClassName();
    
    /**
     * 指定したサービスをロードする。<p>
     * 但し、引数で指定するサービスは、このローダーがデプロイしたサービスでなければならない。<br>
     *
     * @param managerName サービスが登録されているServiceManagerの名前
     * @param serviceName サービス名
     * @exception DeploymentException ロードに失敗した場合
     */
    public void loadService(String managerName, String serviceName)
     throws DeploymentException;
    
    /**
     * 指定したサービス定義&lt;service&gt;要素メタデータをデプロイする。<p>
     * 但し、引数で指定するserviceDataは、このローダーがロードしたServiceManagerに登録されるものでなければならない。<br>
     *
     * @param serviceData サービス定義&lt;service&gt;要素メタデータ
     * @exception DeploymentException デプロイに失敗した場合
     */
    public void deployService(ServiceMetaData serviceData)
     throws DeploymentException;
    
    /**
     * 指定した型のBean属性の編集を行うPropertyEditorを取得する。<p>
     * 存在しない場合は、nullを返す。
     * 
     * @param type 編集を行うBean属性の型
     * @return PropertyEditorオブジェクト
     */
    public PropertyEditor findEditor(Class type);
    
    /**
     * サービス定義ファイルを評価するかどうかを指定する。<p>
     * デフォルトでは、評価しない。<br>
     *
     * @param validate 評価する場合true。
     */
    public void setValidate(boolean validate);
    
    /**
     * サービス定義ファイルを評価するかどうかを調べる。<p>
     *
     * @return 評価する場合true。
     */
    public boolean isValidate();
    
    /**
     * サービス定義ファイルのロード時に使用する構成情報を設定する。<p>
     * 
     * @param config サービスロード構成情報
     */
    public void setConfig(ServiceLoaderConfig config);
    
    /**
     * サービス定義ファイルのロード時に使用する構成情報を取得する。<p>
     * 
     * @return サービスロード構成情報
     */
    public ServiceLoaderConfig getConfig();
}