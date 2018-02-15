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
package jp.ossc.nimbus.service.jms;

import java.lang.reflect.*;

import jp.ossc.nimbus.core.*;

/**
 * {@link ReflectionJMSConnectionFactoryService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see ReflectionJMSConnectionFactoryService
 */
public interface ReflectionJMSConnectionFactoryServiceMBean
 extends ServiceBaseMBean{
    
    /**
     * 自動再接続モード：再接続しない。<p>
     */
    public static int AUTO_RECONNECT_MODE_NON = 0;
    
    /**
     * 自動再接続モード：JNDIサーバ回復検知時に再接続する。<p>
     */
    public static int AUTO_RECONNECT_MODE_ON_RECOVER = ReconnectableConnection.RECONNECT_MODE_ON_RECOVER;
    
    /**
     * 自動再接続モード：JNDIサーバダウン検知時に再接続する。<p>
     */
    public static int AUTO_RECONNECT_MODE_ON_DEAD = ReconnectableConnection.RECONNECT_MODE_ON_DEAD;
    
    /**
     * ConnectionFactoryを生成するファクトリクラスのコンストラクタを設定する。<p>
     * ファクトリクラスのstaticメソッドを呼ぶ場合は、指定する必要はない。<br>
     * 
     * @param c ファクトリクラスのコンストラクタ
     */
    public void setFactoryConstructor(Constructor c);
    
    /**
     * ConnectionFactoryを生成するファクトリクラスのコンストラクタを取得する。<p>
     * 
     * @return ファクトリクラスのコンストラクタ
     */
    public Constructor getFactoryConstructor();
    
    /**
     * ConnectionFactoryを生成するファクトリクラスのコンストラクタの引数を設定する。<p>
     * 
     * @param params ファクトリクラスのコンストラクタの引数
     */
    public void setFactoryConstructorParameters(Object[] params);
    
    /**
     * ConnectionFactoryを生成するファクトリクラスのコンストラクタの引数を取得する。<p>
     * 
     * @return ファクトリクラスのコンストラクタの引数
     */
    public Object[] getFactoryConstructorParameters();
    
    /**
     * ConnectionFactoryを生成するファクトリクラスのファクトリメソッドを設定する。<p>
     * 
     * @param m ファクトリクラスのファクトリメソッド
     */
    public void setFactoryMethod(Method m);
    
    /**
     * ConnectionFactoryを生成するファクトリクラスのファクトリメソッドを取得する。<p>
     * 
     * @return ファクトリクラスのファクトリメソッド
     */
    public Method getFactoryMethod();
    
    /**
     * ConnectionFactoryを生成するファクトリクラスのファクトリメソッドの引数を設定する。<p>
     * 
     * @param params ファクトリクラスのファクトリメソッドの引数
     */
    public void setFactoryMethodParameters(Object[] params);
    
    /**
     * ConnectionFactoryを生成するファクトリクラスのファクトリメソッドの引数を取得する。<p>
     * 
     * @return ファクトリクラスのファクトリメソッドの引数
     */
    public Object[] getFactoryMethodParameters();
    
    /**
     * ファクトリのインスタンスを設定する。<p>
     *
     * @param fac ファクトリ
     */
    public void setFactory(Object fac);
    
    /**
     * ファクトリのインスタンスを取得する。<p>
     *
     * @return ファクトリ
     */
    public Object getFactory();
    
    /**
     * ConnectionFactoryクラスのコンストラクタを設定する。<p>
     * staticメソッドを呼ぶ場合は、指定する必要はない。<br>
     * 
     * @param c コンストラクタ
     */
    public void setConnectionFactoryConstructor(Constructor c);
    
    /**
     * ConnectionFactoryクラスのコンストラクタを取得する。<p>
     * 
     * @return コンストラクタ
     */
    public Constructor getConnectionFactoryConstructor();
    
    /**
     * ConnectionFactoryクラスのコンストラクタの引数を設定する。<p>
     * 
     * @param params コンストラクタの引数
     */
    public void setConnectionFactoryConstructorParameters(Object[] params);
    
    /**
     * ConnectionFactoryクラスのコンストラクタの引数を取得する。<p>
     * 
     * @return コンストラクタの引数
     */
    public Object[] getConnectionFactoryConstructorParameters();
    
    /**
     * JMSコネクションのインスタンスを１つだけ生成するかどうかを設定する。<p>
     * JMSコネクションは、物理的な接続先を表すオブジェクトであるため、通常インスタンスは、１つだけ生成して使用すべきである。<br>
     * デフォルトは、true。<br>
     *
     * @param isSingle JMSコネクションのインスタンスを１つだけ生成する場合はtrue
     */
    public void setSingleConnection(boolean isSingle);
    
    /**
     * JMSコネクションのインスタンスを１つだけ生成するかどうかを判定する。<p>
     *
     * @return trueの場合、JMSコネクションのインスタンスを１つだけ生成する
     */
    public boolean isSingleConnection();
    
    /**
     * 生成したJMSコネクションを管理するかどうかを設定する。<p>
     * trueを設定した場合、生成したJMSコネクションは、このサービスによって保持されており、サービスの停止と共にJMSコネクションの終了処理が行われる。
     * リソースの開放漏れを防ぐための機能である。<br>
     * 但し、SingleConnection属性をtrueに設定している場合は、この属性をtrueにしなくても同様の処理が行われる。<br>
     * デフォルトは、false。<br>
     *
     * @param isManaged 生成したJMSコネクションを管理する場合true
     */
    public void setConnectionManagement(boolean isManaged);
    
    /**
     * 生成したJMSコネクションを管理するかどうかを判定する。<p>
     *
     * @return trueの場合、生成したJMSコネクションを管理する
     */
    public boolean isConnectionManagement();
    
    /**
     * Connectionを生成する時にConnectionの開始処理をするかどうかを設定する。<p>
     * デフォルトは、false。<br>
     *
     * @param isStart Connectionの開始処理をする場合true
     */
    public void setStartConnection(boolean isStart);
    
    /**
     * Connectionの開始処理をするかどうかを判定する。<p>
     *
     * @return trueの場合、Connectionの開始処理をする
     */
    public boolean isStartConnection();
    
    /**
     * JMS接続ユーザ名を設定する。<p>
     * J2EEコンテナ側でJMS接続に対してセキュリティ設定を行っている場合に、設定する。<br>
     *
     * @param name JMS接続ユーザ名
     */
    public void setUserName(String name);
    
    /**
     * JMS接続ユーザ名を取得する。<p>
     *
     * @return JMS接続ユーザ名
     */
    public String getUserName();
    
    /**
     * JMS接続パスワードを設定する。<p>
     * J2EEコンテナ側でJMS接続に対してセキュリティ設定を行っている場合に、設定する。<br>
     *
     * @param passwd JMS接続パスワード
     */
    public void setPassword(String passwd);
    
    /**
     * JMS接続パスワードを取得する。<p>
     *
     * @return JMS接続パスワード
     */
    public String getPassword();
    
    /**
     * 自動再接続モードを設定する。<p>
     * デフォルトは、{@link #AUTO_RECONNECT_MODE_NON}で、再接続しない。<br>
     * 再接続を行うモードに設定した場合は、JMSサーバの生死を検知して、自動再接続を行う。<br>
     * またその場合は、{@link #setKeepAliveCheckerServiceName(ServiceName)}で、JMSサーバの生死を検知する{@link jp.ossc.nimbus.service.keepalive.KeepAliveChecker KeepAliveChecker}サービスを設定しなければならない。<br>
     *
     * @param mode 自動再接続モード
     * @see #AUTO_RECONNECT_MODE_NON
     * @see #AUTO_RECONNECT_MODE_ON_RECOVER
     * @see #AUTO_RECONNECT_MODE_ON_DEAD
     * @see #setKeepAliveCheckerServiceName(ServiceName)
     */
    public void setAutoReconnectMode(int mode);
    
    /**
     * 自動再接続モードを設定する。<p>
     *
     * @return 自動再接続モード
     */
    public int getAutoReconnectMode();
    
    /**
     * JMSサーバの生死を検知する{@link jp.ossc.nimbus.service.keepalive.KeepAliveChecker KeepAliveChecker}サービスのサービス名を設定する。<p>
     *
     * @param name KeepAliveCheckerサービスのサービス名
     */
    public void setKeepAliveCheckerServiceName(ServiceName name);
    
    /**
     * JMSサーバの生死を検知する{@link jp.ossc.nimbus.service.keepalive.KeepAliveChecker KeepAliveChecker}サービスのサービス名を取得する。<p>
     *
     * @return KeepAliveCheckerサービスのサービス名
     */
    public ServiceName getKeepAliveCheckerServiceName();
    
    /**
     * 自動再接続時に、接続に失敗した場合に出力するログのメッセージIDを設定する。<p>
     * デフォルトは、nullで、ログ出力は行われない。<br>
     * 
     * @param id ログのメッセージID
     */
    public void setAutoReconnectErrorLogMessageId(String id);
    
    /**
     * 自動再接続時に、接続に失敗した場合に出力するログのメッセージIDを取得する。<p>
     * 
     * @return ログのメッセージID
     */
    public String getAutoReconnectErrorLogMessageId();
    
    /**
     * 自動再接続時に、接続に失敗した場合にリトライ処理を行う回数を設定する。<p>
     * デフォルトは、0でリトライしない。<br>
     *
     * @param count リトライ回数
     */
    public void setAutoReconnectMaxRetryCount(int count);
    
    /**
     * 自動再接続時に、接続に失敗した場合にリトライ処理を行う回数を取得する。<p>
     *
     * @return リトライ回数
     */
    public int getAutoReconnectMaxRetryCount();
    
    /**
     * 自動再接続時に、接続に失敗した場合にリトライ処理を行う間隔[ms]を設定する。<p>
     * デフォルトは、1000[ms]。<br>
     *
     * @param interval リトライ間隔
     */
    public void setAutoReconnectRetryInterval(long interval);
    
    /**
     * 自動再接続時に、接続に失敗した場合にリトライ処理を行う間隔[ms]を取得する。<p>
     *
     * @return リトライ間隔
     */
    public long getAutoReconnectRetryInterval();
}
