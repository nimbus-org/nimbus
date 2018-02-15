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
package jp.ossc.nimbus.service.jndi;

import java.util.*;
import javax.naming.NamingException;

import jp.ossc.nimbus.core.*;

/**
 * {@link CachedJndiFinderService}のMBeanインタフェース<p>
 * 
 * @author Y.Tokuda
 * @see CachedJndiFinderService
 */
public interface CachedJndiFinderServiceMBean extends ServiceBaseMBean{
    
    /**
     * JNDIサーバ生存チェックで、JNDIサーバが死んだ時に出力されるログのメッセージID。<p>
     */
    public static final String JNDI_SERVER_DEAD_MSG_ID = "CJF__00001";
    
    /**
     * JNDIサーバ生存チェックで、JNDIサーバが復帰した時に出力されるログのメッセージID。<p>
     */
    public static final String JNDI_SERVER_RECOVER_MSG_ID = "CJF__00002";
    
    /**
     * リトライの対象とする例外のデフォルト値。<p>
     * <ul>
     *     <li>javax.naming.CommunicationException</li>
     *     <li>javax.naming.InsufficientResourcesException</li>
     *     <li>javax.naming.InterruptedNamingException</li>
     *     <li>javax.naming.TimeLimitExceededException</li>
     *     <li>javax.naming.ServiceUnavailableException</li>
     * </ul>
     */
    public static final String[] DEFAULT_RETRY_EXCXEPTION_NAME = new String[]{
        javax.naming.CommunicationException.class.getName(),
        javax.naming.InsufficientResourcesException.class.getName(),
        javax.naming.InterruptedNamingException.class.getName(),
        javax.naming.TimeLimitExceededException.class.getName(),
        javax.naming.ServiceUnavailableException.class.getName()
    };
    
    /**
     * InitialContextの初期化に使用するJNDI環境変数を設定する。<p>
     * 
     * @param prop JNDI環境変数を格納したプロパティ
     */
    public void setEnvironment(Properties prop);
    
    /**
     * InitialContextの初期化に使用するJNDI環境変数を取得する。<p>
     * 
     * @return JNDI環境変数を格納したプロパティ
     * @exception NamingException JNDI環境変数を取得できなかった場合
     */
    public Properties getEnvironment() throws NamingException;
    
    /**
     * lookup時に使用するJNDIプレフィックスを設定する。<p>
     * デフォルトは、空文字。<br>
     *
     * @param prefix JNDIプレフィックス
     */
    public void setPrefix(String prefix);
    
    /**
     * lookup時に使用するJNDIプレフィックスを取得する。<p>
     *
     * @return JNDIプレフィックス
     */
    public String getPrefix();
    
    /**
     * lookupしたリモートオブジェクトをキャッシュするキャッシュサービス名を設定する。<p>
     * この属性を設定しない場合は、リモートオブジェクトをキャッシュしない。<br>
     *
     * @param name キャッシュサービス名
     */
    public void setCacheMapServiceName(ServiceName name);
    
    /**
     * lookupしたリモートオブジェクトをキャッシュするキャッシュサービス名を取得する。<p>
     *
     * @return キャッシュサービス名
     */
    public ServiceName getCacheMapServiceName();
    
    /** 
     * lookup時にリトライ例外が発生した場合にリトライする回数を設定する。<p>
     * デフォルトは、0でリトライしない。<br>
     *
     * @param num リトライ回数
     * @see #setRetryExceptionClassNames(String[])
     */
    public void setRetryCount(int num);
    
    /** 
     * lookup時にリトライ例外が発生した場合にリトライする回数を取得する。<p>
     *
     * @return リトライ回数
     */
    public int getRetryCount();
    
    /** 
     * lookup時にリトライ例外が発生した場合にリトライする間隔[msec]を設定する。<p>
     * デフォルトは、1000。<br>
     *
     * @param interval リトライ間隔
     * @see #setRetryExceptionClassNames(String[])
     */
    public void setRetryInterval(long interval);
    
    /** 
     * lookup時にリトライ例外が発生した場合にリトライする間隔[msec]を取得する。<p>
     *
     * @return リトライ間隔
     */
    public long getRetryInterval();
    
    /** 
     * lookup時にjavax.naming.NamingExceptionが発生した場合に、リトライする例外クラス名を設定する。<p>
     * デフォルトは、{@link #DEFAULT_RETRY_EXCXEPTION_NAME}。<br>
     *
     * @param classNames リトライする例外クラス名配列
     */
    public void setRetryExceptionClassNames(String[] classNames);
    
    /** 
     * lookup時にjavax.naming.NamingExceptionが発生した場合に、リトライする例外クラス名を取得する。<p>
     *
     * @return リトライする例外クラス名配列
     */
    public String[] getRetryExceptionClassNames();
    
    /**
     * JNDIサーバの生存チェックを行うかどうかを設定する。<p>
     * trueに設定された場合、{@link #setAliveCheckJNDIServerInterval(long)}で設定された間隔で、ルートコンテキスト（"/"）をlookupして、JNDIサーバの生存チェックを行う。<br>
     * ルートコンテキストが取得できなくなった場合、エラーログを出力して、キャッシュをクリアする。また、ルートコンテキストが取得できるようになった場合、通知ログを出力する。<br>
     *
     * @param isCheck 生存チェックを行う場合はtrue
     */
    public void setAliveCheckJNDIServer(boolean isCheck);
    
    /**
     * JNDIサーバの生存チェックを行うかどうかを設定する。<p>
     *
     * @return 生存チェックを行う場合はtrue
     */
    public boolean isAliveCheckJNDIServer();
    
    /**
     * JNDIサーバの生存チェックを行う間隔[msec]を設定する。<p>
     * デフォルトは、60000[msec]。
     * 
     * @param interval JNDIサーバの生存チェックを行う間隔[msec]
     */
    public void setAliveCheckJNDIServerInterval(long interval);
    
    /**
     * JNDIサーバの生存チェックを行う間隔[msec]を取得する。<p>
     * 
     * @return JNDIサーバの生存チェックを行う間隔[msec]
     */
    public long getAliveCheckJNDIServerInterval();
    
    /**
     * JNDIサーバが生存しているかどうかを調べる。<p>
     * {@link #isAliveCheckJNDIServer()}がtrueを返す場合は、最後にチェックした時の状態を返す。<br>
     * isAliveCheckJNDIServer()がfalseを返す場合は、即時にチェックして結果を返す。但し、サービスが開始していない場合は、falseを返す。<br>
     * 
     * @return JNDIサーバが生存している場合true
     */
    public boolean isAliveJNDIServer();
    
    /**
     * JNDIサーバがダウンした事を検知した旨のログを出力するかどうかを設定する。<p>
     * デフォルトは、true。<br>
     *
     * @param isOutput ログを出力する場合true
     */
    public void setLoggingDeadJNDIServer(boolean isOutput);
    
    /**
     * JNDIサーバがダウンした事を検知した旨のログを出力するかどうかを判定する。<p>
     *
     * @return ログを出力する場合true
     */
    public boolean isLoggingDeadJNDIServer();
    
    /**
     * JNDIサーバが復帰した事を検知した旨のログを出力するかどうかを設定する。<p>
     * デフォルトは、true。<br>
     *
     * @param isOutput ログを出力する場合true
     */
    public void setLoggingRecoverJNDIServer(boolean isOutput);
    
    /**
     * JNDIサーバが復帰した事を検知した旨のログを出力するかどうかを判定する。<p>
     *
     * @return ログを出力する場合true
     */
    public boolean isLoggingRecoverJNDIServer();
    
    /**
     * JNDIサーバがダウンした事を検知した旨のログ出力のメッセージIDを設定する。<p>
     * デフォルトは、{@link #JNDI_SERVER_DEAD_MSG_ID}。<br>
     *
     * @param id ログ出力のメッセージID
     */
    public void setDeadJNDIServerLogMessageId(String id);
    
    /**
     * JNDIサーバがダウンした事を検知した旨のログ出力のメッセージIDを取得する。<p>
     *
     * @return ログ出力のメッセージID
     */
    public String getDeadJNDIServerLogMessageId();
    
    /**
     * JNDIサーバが復帰した事を検知した旨のログ出力のメッセージIDを設定する。<p>
     * デフォルトは、{@link #JNDI_SERVER_RECOVER_MSG_ID}。<br>
     *
     * @param id ログ出力のメッセージID
     */
    public void setRecoverJNDIServerLogMessageId(String id);
    
    /**
     * JNDIサーバが復帰した事を検知した旨のログ出力のメッセージIDを取得する。<p>
     *
     * @return ログ出力のメッセージID
     */
    public String getRecoverJNDIServerLogMessageId();
    
    /**
     * キャッシュしたリモートオブジェクトを全てクリアする。<p>
     */
    public void clearCache();
    
    /**
     * 指定したJNDI名のしたリモートオブジェクトのキャッシュをクリアする。<p>
     * 
     * @param jndiName キャッシュから削除するリモートオブジェクトのJNDI名
     */
    public void clearCache(String jndiName);
    
    public String listContext() throws NamingException;
}
