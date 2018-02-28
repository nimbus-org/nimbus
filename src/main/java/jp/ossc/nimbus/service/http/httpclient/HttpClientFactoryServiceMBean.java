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
package jp.ossc.nimbus.service.http.httpclient;

import java.util.Map;

import jp.ossc.nimbus.core.*;

/**
 * {@link HttpClientFactoryService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see HttpClientFactoryService
 */
public interface HttpClientFactoryServiceMBean extends ServiceBaseMBean{
    
    public static final String JOURNAL_ACCESS = "Access";
    public static final String JOURNAL_ACCESS_EXCEPTION = "Exception";
    public static final String JOURNAL_REQUEST = "Request";
    public static final String JOURNAL_REQUEST_ACTION = "Action";
    public static final String JOURNAL_REQUEST_URI = "URI";
    public static final String JOURNAL_REQUEST_COOKIES = "Cookies";
    public static final String JOURNAL_REQUEST_HEADERS = "Headers";
    public static final String JOURNAL_REQUEST_PARAMS = "Parameters";
    public static final String JOURNAL_REQUEST_OBJECT = "InputObject";
    public static final String JOURNAL_REQUEST_BODY = "Body";
    public static final String JOURNAL_RESPONSE = "Response";
    public static final String JOURNAL_RESPONSE_STATUS = "Status";
    public static final String JOURNAL_RESPONSE_HEADERS = "Headers";
    public static final String JOURNAL_RESPONSE_BODY = "Body";
    public static final String JOURNAL_RESPONSE_OBJECT = "OutputObject";
    
    /**
     * HTTPのソケットを接続する際のタイムアウトを設定する。<p>
     *
     * @param millis タイムアウト[ms]
     */
    public void setConnectionTimeout(int millis);
    
    /**
     * HTTPのソケットを接続する際のタイムアウトを取得する。<p>
     *
     * @return タイムアウト[ms]
     */
    public int getConnectionTimeout();
    
    /**
     * HTTPのソケットが閉じられた場合に、ソケットに残ってるデータの送信を待機するタイムアウトを設定する。<p>
     *
     * @param millis タイムアウト[ms]
     */
    public void setLinger(int millis);
    
    /**
     * HTTPのソケットが閉じられた場合に、ソケットに残ってるデータの送信を待機するタイムアウトを取得する。<p>
     *
     * @return タイムアウト[ms]
     */
    public int getLinger();
    
    /**
     * HTTPのソケットの受信バッファサイズを設定する。<p>
     *
     * @param size HTTPのソケットの受信バッファサイズ
     */
    public void setReceiveBufferSize(int size);
    
    /**
     * HTTPのソケットの受信バッファサイズを取得する。<p>
     *
     * @return HTTPのソケットの受信バッファサイズ
     */
    public int getReceiveBufferSize();
    
    /**
     * HTTPのソケットの送信バッファサイズを設定する。<p>
     *
     * @param size HTTPのソケットの送信バッファサイズ
     */
    public void setSendBufferSize(int size);
    
    /**
     * HTTPのソケットの送信バッファサイズを取得する。<p>
     *
     * @return HTTPのソケットの送信バッファサイズ
     */
    public int getSendBufferSize();
    
    /**
     * HTTPのソケットの受信タイムアウトを設定する。<p>
     *
     * @param millis HTTPのソケットの受信タイムアウト[ms]
     */
    public void setSoTimeout(int millis);
    
    /**
     * HTTPのソケットの受信タイムアウトを取得する。<p>
     *
     * @return HTTPのソケットの受信タイムアウト[ms]
     */
    public int getSoTimeout();
    
    /**
     * リクエストのコンテントタイプを取得する。<p>
     *
     * @return コンテントタイプ
     */
    public String getRequestContentType();
    
    /**
     * リクエストのコンテントタイプを設定する。<p>
     * HTTPリクエストに既に設定されている場合は、そちらが優先される。<br>
     *
     * @param type コンテントタイプ
     */
    public void setRequestContentType(String type);
    
    /**
     * リクエストの文字エンコーディングを取得する。<p>
     *
     * @return 文字エンコーディング
     */
    public String getRequestCharacterEncoding();
    
    /**
     * リクエストの文字エンコーディングを設定する。<p>
     * HTTPリクエストに既に設定されている場合は、そちらが優先される。<br>
     *
     * @param encoding 文字エンコーディング
     */
    public void setRequestCharacterEncoding(String encoding);
    
    /**
     * HTTPのバージョンを取得する。<p>
     *
     * @return HTTPのバージョン
     */
    public String getHttpVersion();
    
    /**
     * HTTPのバージョンを設定する。<p>
     * HTTPリクエストに既に設定されている場合は、そちらが優先される。<br>
     *
     * @param version HTTPのバージョン
     */
    public void setHttpVersion(String version);
    
    /**
     * リクエストのヘッダを設定する。<p>
     * HTTPリクエストに既に設定されている場合は、そちらが優先される。<br>
     *
     * @param name ヘッダ名
     * @param values ヘッダ値の配列
     */
    public void setRequestHeaders(String name, String[] values);
    
    /**
     * リクエストのヘッダを取得する。<p>
     *
     * @param name ヘッダ名
     * @return ヘッダ値の配列
     */
    public String[] getRequestHeaders(String name);
    
    /**
     * プロキシのホスト名とポート番号を設定する。<p>
     *
     * @param proxy ホスト名:ポート番号
     */
    public void setProxy(String proxy);
    
    /**
     * プロキシのホスト名とポート番号を取得する。<p>
     *
     * @return ホスト名:ポート番号
     */
    public String getProxy();
    
    /**
     * ローカルアドレスを設定する。<p>
     *
     * @param address ローカルアドレス
     * @exception java.net.UnknownHostException 指定されたアドレスが不正な場合
     */
    public void setLocalAddress(String address)
     throws java.net.UnknownHostException;
    
    /**
     * ローカルアドレスを取得する。<p>
     *
     * @return ローカルアドレス
     */
    public String getLocalAddress();
    
    /**
     * Jakarta HttpClientのパラメータを設定する。<p>
     *
     * @param name パラメータ名
     * @param value 値
     */
    public void setHttpClientParam(String name, Object value);
    
    /**
     * Jakarta HttpClientのパラメータを取得する。<p>
     *
     * @param name パラメータ名
     * @return 値
     */
    public Object getHttpClientParam(String name);
    
    /**
     * Jakarta HttpClientのパラメータのマップを取得する。<p>
     *
     * @return Jakarta HttpClientのパラメータのマップ
     */
    public Map getHttpClientParamMap();
    
    /**
     * リクエストのストリームを圧縮する場合の閾値[byte]を設定する。<p>
     * 設定しない場合は、ストリームのサイズに関わらず圧縮する。<br>
     *
     * @param length 閾値[byte]
     */
    public void setRequestDeflateLength(int length);
    
    /**
     * リクエストのストリームを圧縮する場合の閾値[byte]を取得する。<p>
     *
     * @return 閾値[byte]
     */
    public int getRequestDeflateLength();
    
    /**
     * HTTPリクエストに設定された入力オブジェクトをストリームに変換する{@link jp.ossc.nimbus.util.converter.StreamConverter StreamConverter}サービスのサービス名を設定する。<p>
     * HTTPリクエストに既に設定されている場合は、そちらが優先される。<br>
     *
     * @param name StreamConverterサービスのサービス名
     */
    public void setRequestStreamConverterServiceName(ServiceName name);
    
    /**
     * HTTPリクエストに設定された入力オブジェクトをストリームに変換する{@link jp.ossc.nimbus.util.converter.StreamConverter StreamConverter}サービスのサービス名を取得する。<p>
     *
     * @return StreamConverterサービスのサービス名
     */
    public ServiceName getRequestStreamConverterServiceName();
    
    /**
     * HTTPレスポンスのストリームを出力オブジェクトに変換する{@link jp.ossc.nimbus.util.converter.StreamConverter StreamConverter}サービスのサービス名を設定する。<p>
     * HTTPレスポンスに既に設定されている場合は、そちらが優先される。<br>
     *
     * @param name StreamConverterサービスのサービス名
     */
    public void setResponseStreamConverterServiceName(ServiceName name);
    
    /**
     * HTTPレスポンスのストリームを出力オブジェクトに変換する{@link jp.ossc.nimbus.util.converter.StreamConverter StreamConverter}サービスのサービス名を取得する。<p>
     *
     * @return StreamConverterサービスのサービス名
     */
    public ServiceName getResponseStreamConverterServiceName();
    
    /**
     * ジャーナルを出力する{@link jp.ossc.nimbus.service.journal.Journal Journal}サービスのサービス名を設定する。<p>
     *
     * @param name Journalサービスのサービス名
     */
    public void setJournalServiceName(ServiceName name);
    
    /**
     * ジャーナルを出力する{@link jp.ossc.nimbus.service.journal.Journal Journal}サービスのサービス名を取得する。<p>
     *
     * @return Journalサービスのサービス名
     */
    public ServiceName getJournalServiceName();
    
    /**
     * ジャーナルに設定する通番を発行する{@link jp.ossc.nimbus.service.sequence.Sequence Sequence}サービスのサービス名を設定する。<p>
     *
     * @param name Sequenceサービスのサービス名
     */
    public void setSequenceServiceName(ServiceName name);
    
    /**
     * ジャーナルに設定する通番を発行する{@link jp.ossc.nimbus.service.sequence.Sequence Sequence}サービスのサービス名を取得する。<p>
     *
     * @return Sequenceサービスのサービス名
     */
    public ServiceName getSequenceServiceName();
    
    /**
     * ジャーナルに設定する通番を取得する{@link jp.ossc.nimbus.service.context.Context Context}サービスのサービス名を設定する。<p>
     *
     * @param name Contextサービスのサービス名
     */
    public void setThreadContextServiceName(ServiceName name);
    
    /**
     * ジャーナルに設定する通番を取得する{@link jp.ossc.nimbus.service.context.Context Context}サービスのサービス名を取得する。<p>
     *
     * @return Contextサービスのサービス名
     */
    public ServiceName getThreadContextServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.http.HttpClient HttpClient}を生成する際に使用する{@link jp.ossc.nimbus.service.semaphore.Semaphore Semaphore}サービスのサービス名を設定する。<p>
     *
     * @param name Semaphoreサービスのサービス名
     */
    public void setSemaphoreServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.http.HttpClient HttpClient}を生成する際に使用する{@link jp.ossc.nimbus.service.semaphore.Semaphore Semaphore}サービスのサービス名を取得する。<p>
     *
     * @return Semaphoreサービスのサービス名
     */
    public ServiceName getSemaphoreServiceName();
    
    /**
     * パフォーマンスを記録する{@link jp.ossc.nimbus.service.performance.PerformanceRecorder PerformanceRecorder}サービスのサービス名を設定する。<p>
     *
     * @param name パフォーマンスを記録するPerformanceRecorderサービスのサービス名
     */
    public void setPerformanceRecorderServiceName(ServiceName name);
    
    /**
     * パフォーマンスを記録する{@link jp.ossc.nimbus.service.performance.PerformanceRecorder PerformanceRecorder}サービスのサービス名を取得する。<p>
     *
     * @return パフォーマンスを記録するPerformanceRecorderサービスのサービス名
     */
    public ServiceName getPerformanceRecorderServiceName();
    
    /**
     * org.apache.commons.httpclient.HttpConnectionManagerのクラスを設定する。<p>
     *
     * @param clazz HttpConnectionManagerのクラス
     */
    public void setHttpConnectionManagerClass(Class clazz);
    
    /**
     * org.apache.commons.httpclient.HttpConnectionManagerのクラスを取得する。<p>
     *
     * @return HttpConnectionManagerのクラス
     */
    public Class getHttpConnectionManagerClass();
    
    /**
     * org.apache.commons.httpclient.HttpConnectionManagerを使ってコネクションをプールする場合に、待機状態のコネクションを閉じるまでのタイムアウト[ms]を設定する。<p>
     * デフォルトは、0で待機状態のコネクションは閉じない。<br>
     *
     * @param timeout タイムアウト[ms]
     */
    public void setIdleConnectionTimeout(long timeout);
    
    /**
     * org.apache.commons.httpclient.HttpConnectionManagerを使ってコネクションをプールする場合に、待機状態のコネクションを閉じるまでのタイムアウト[ms]を取得する。<p>
     *
     * @return タイムアウト[ms]
     */
    public long getIdleConnectionTimeout();
    
    /**
     * org.apache.commons.httpclient.HttpConnectionManagerを使ってコネクションをプールする場合に、待機状態のコネクションの待機時間をチェックする間隔[ms]を設定する。<p>
     * デフォルトは、0でorg.apache.commons.httpclient.util.IdleConnectionTimeoutThreadのデフォルト値に準じる。<br>
     *
     * @param interval チェック間隔[ms]
     */
    public void setIdleConnectionCheckInterval(long interval);
    
    /**
     * org.apache.commons.httpclient.HttpConnectionManagerを使ってコネクションをプールする場合に、待機状態のコネクションの待機時間をチェックする間隔[ms]を取得する。<p>
     *
     * @return チェック間隔[ms]
     */
    public long getIdleConnectionCheckInterval();
    
    /**
     * ジャーナルに{@link jp.ossc.nimbus.service.http.HttpResponse#getObject()}を出力するかどうかを設定する。<p>
     * デフォルトは、trueで出力する。{@link jp.ossc.nimbus.service.http.HttpResponse#getObject(Object)}を使用する場合は、falseにする必要がある。<br>
     *
     * @param isOutput 出力する場合、true
     */
    public void setOutputJournalResponseObject(boolean isOutput);
    
    /**
     * ジャーナルに{@link jp.ossc.nimbus.service.http.HttpResponse#getObject()}を出力するかどうかを判定する。<p>
     *
     * @return trueの場合、出力する
     */
    public boolean isOutputJournalResponseObject();
    
    /**
     * org.apache.commons.httpclient.MultiThreadedHttpConnectionManagerを使用している場合の現在のコネクションプール数を取得する。<p>
     *
     * @return コネクションプール数
     */
    public int getConnectionsInPool();
    
    /**
     * org.apache.commons.httpclient.MultiThreadedHttpConnectionManagerを使用している場合の現在のコネクションプール使用数を取得する。<p>
     *
     * @return コネクションプール使用数
     */
    public int getConnectionsInUse();
}