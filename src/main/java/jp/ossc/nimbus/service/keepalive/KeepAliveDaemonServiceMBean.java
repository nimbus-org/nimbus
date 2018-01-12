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
package jp.ossc.nimbus.service.keepalive;

import jp.ossc.nimbus.core.*;

/**
 * {@link KeepAliveDaemonService}のMBeanインタフェース。<p>
 *
 * @author H.Nakano
 * @version  1.00 作成: 2003/10/08 - H.Nakano
 */
public interface KeepAliveDaemonServiceMBean
 extends ServiceBaseMBean, QueryKeepAlive{
    
    /**
     * チェック対象となる{@link KeepAliveChecker}サービス名配列を設定する。<p>
     *
     * @param serviceNames チェック対象となるKeepAliveCheckerサービス名配列
     */
    public void setChekerServiceNames(ServiceName[] serviceNames);
    
    /**
     * チェック対象となる{@link KeepAliveChecker}サービス名配列を取得する。<p>
     *
     * @return チェック対象となるKeepAliveCheckerサービス名配列
     */
    public ServiceName[] getChekerServiceNames();
    
    /**
     * 監視インターバル[ms]を設定する。<p>
     *
     * @param miliseconds 監視インターバル[ms]
     */
    public void setIntervalTimeMillis(long miliseconds);
    
    /**
     * 監視インターバル[ms]を取得する。<p>
     *
     * @return 監視インターバル[ms]
     */
    public long getIntervalTimeMillis();
    
    /**
     * ステータス情報を出力する。<p>
     *
     * @return ステータス情報
     */
    public String[] getStatusString();
    
    /**
     * サーバの稼動状態が停止状態から走行状態に変化した時に出力するログのメッセージIDを設定する。<p>
     *
     * @param id ログのメッセージID
     */
    public void setAliveLogMessageId(String id);
    
    /**
     * サーバの稼動状態が停止状態から走行状態に変化した時に出力するログのメッセージIDを取得する。<p>
     *
     * @return ログのメッセージID
     */
    public String getAliveLogMessageId();
    
    /**
     * サーバの稼動状態が走行状態から停止状態に変化した時に出力するログのメッセージIDを設定する。<p>
     *
     * @param id ログのメッセージID
     */
    public void setDeadLogMessageId(String id);
    
    /**
     * サーバの稼動状態が走行状態から停止状態に変化した時に出力するログのメッセージIDを取得する。<p>
     *
     * @return ログのメッセージID
     */
    public String getDeadLogMessageId();
    
    /**
     * サーバの稼動状態が停止状態から走行状態に変化した時にログを出力するかどうかを設定する。<p>
     * デフォルトは、true。<br>
     *
     * @param isOutput 出力する場合は、true
     */
    public void setOutputAliveLogMessage(boolean isOutput);
    
    /**
     * サーバの稼動状態が停止状態から走行状態に変化した時にログを出力するかどうかを判定する。<p>
     *
     * @return trueの場合は、出力する
     */
    public boolean isOutputAliveLogMessage();
    
    /**
     * サーバの稼動状態が走行状態から停止状態に変化した時にログを出力するかどうかを設定する。<p>
     * デフォルトは、true。<br>
     *
     * @param isOutput 出力する場合は、true
     */
    public void setOutputDeadLogMessage(boolean isOutput);
    
    /**
     * サーバの稼動状態が走行状態から停止状態に変化した時にログを出力するかどうかを判定する。<p>
     *
     * @return trueの場合は、出力する
     */
    public boolean isOutputDeadLogMessage();
}
