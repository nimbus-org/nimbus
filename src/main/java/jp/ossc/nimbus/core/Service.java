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

/**
 * サービスインタフェース。<p>
 * {@link ServiceManager}で制御可能なサービスを定義するインタフェースです。<br>
 * このインタフェースをimplementsしたクラスは、ServiceManagerによって、生成（{@link #create()}）、起動（{@link #start()}）、停止（{@link #stop()}）、廃棄（{@link #destroy()}）の契機を制御する事が可能で、その動作を実装する必要があります。また、４つの動作の制御による、状態の管理を実装する必要があります。<br>
 * 
 * @author M.Takata
 * @see ServiceManager
 */
public interface Service{
    
    /**
     * 状態を表す文字列表現の配列です。<p>
     * <table border=1>
     *   <tr><th>状態</th><th>文字列表現</th></tr>
     *   <tr><td>生成中：{@link #CREATING}</td><td>Creating</td></tr>
     *   <tr><td>生成完了：{@link #CREATED}</td><td>Created</td></tr>
     *   <tr><td>開始中：{@link #STARTING}</td><td>Starting</td></tr>
     *   <tr><td>開始完了：{@link #STARTED}</td><td>Started</td></tr>
     *   <tr><td>停止中：{@link #STOPPING}</td><td>Stopping</td></tr>
     *   <tr><td>停止完了：{@link #STOPPED}</td><td>Stopped</td></tr>
     *   <tr><td>破棄中：{@link #DESTROYING}</td><td>Destorying</td></tr>
     *   <tr><td>破棄完了：{@link #DESTROYED}</td><td>Destroyed</td></tr>
     *   <tr><td>失敗：{@link #FAILED}</td><td>Failed</td></tr>
     *   <tr><td>不明：{@link #UNKNOWN}</td><td>Unknown</td></tr>
     * </table>
     */
    public static final String[] STATES = {
        "Creating", "Created",
        "Starting", "Started",
        "Stopping", "Stopped",
        "Destorying", "Destroyed",
        "Failed", "Unknown"
    };
    
    /**
     * 生成中を表す状態値。<p>
     */
    public static final int CREATING = 0;
    
    /**
     * 生成完了を表す状態値。<p>
     */
    public static final int CREATED = 1;
    
    /**
     * 開始中を表す状態値。<p>
     */
    public static final int STARTING = 2;
    
    /**
     * 開始完了を表す状態値。<p>
     */
    public static final int STARTED  = 3;
    
    /**
     * 停止中を表す状態値。<p>
     */
    public static final int STOPPING = 4;
    
    /**
     * 停止完了を表す状態値。<p>
     */
    public static final int STOPPED  = 5;
    
    /**
     * 破棄中を表す状態値。<p>
     */
    public static final int DESTROYING = 6;
    
    /**
     * 破棄完了を表す状態値。<p>
     */
    public static final int DESTROYED = 7;
    
    /**
     * 失敗を表す状態値。<p>
     */
    public static final int FAILED  = 8;
    
    /**
     * 不明を表す状態値。<p>
     */
    public static final int UNKNOWN  = 9;
    
    /**
     * このサービスの登録先となる{@link ServiceManager}のサービス名を取得します。<p>
     *
     * @return ServiceManagerのサービス名
     * @see #setServiceManagerName(String)
     */
    public String getServiceManagerName();
    
    /**
     * このサービスの登録先となる{@link ServiceManager}のサービス名を設定します。<p>
     *
     * @param name ServiceManagerのサービス名
     * @see #getServiceManagerName()
     */
    public void setServiceManagerName(String name);
    
    /**
     * サービス名を設定します。<p>
     *
     * @param name サービス名
     * @see #getServiceName()
     */
    public void setServiceName(String name);
    
    /**
     * サービス名を取得します。<p>
     *
     * @return サービス名
     * @see #setServiceName(String)
     */
    public String getServiceName();
    
    /**
     * このサービスの登録先となる{@link ServiceManager}のサービス名とこのサービスのサービス名を含んだ{@link ServiceName}を取得します。<p>
     *
     * @return サービス名
     */
    public ServiceName getServiceNameObject();
    
    /**
     * 現在のサービス状態を取得します。<p>
     *
     * @return 状態を示す値
     * @see #CREATING
     * @see #CREATED
     * @see #STARTING
     * @see #STARTED
     * @see #STOPPING
     * @see #STOPPED
     * @see #DESTROYING
     * @see #DESTROYED
     * @see #FAILED
     * @see #UNKNOWN
     */
    public int getState();
    
    /**
     * 現在のサービス状態の文字列表現を取得します。<p>
     *
     * @return サービス状態の文字列表現
     * @see #STATES
     */
    public String getStateString();
    
    /**
     * サービスを生成します。<p>
     * このサービスに必要なオブジェクトの生成などの初期化処理を行います。<br>
     *
     * @exception Exception サービスの生成処理に失敗した場合
     */
    public void create() throws Exception;
    
    /**
     * サービスを開始します。<p>
     * このサービスを利用可能な状態にします。このメソッドの呼び出し後は、このサービスの機能を利用できる事が保証されます。<br>
     *
     * @exception Exception サービスの開始処理に失敗した場合
     */
    public void start() throws Exception;
    
    /**
     * サービスを停止します。<p>
     * このサービスを利用不可能な状態にします。このメソッドの呼び出し後は、このサービスの機能を利用できる事は保証されません。<br>
     */
    public void stop();
    
    /**
     * サービスを破棄します。<p>
     * このサービスで使用するリソースを開放します。このメソッドの呼び出し後は、このサービスの機能を利用できる事は保証されません。<br>
     */
    public void destroy();
}