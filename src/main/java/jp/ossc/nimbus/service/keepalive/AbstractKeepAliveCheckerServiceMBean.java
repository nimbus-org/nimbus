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
 * {@link AbstractKeepAliveCheckerService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see AbstractKeepAliveCheckerService
 */
public interface AbstractKeepAliveCheckerServiceMBean extends ServiceBaseMBean{
    
    public static final String DEFAULT_ALIVE_LOG_MSG_ID = "KACS_00001";
    public static final String DEFAULT_DEAD_LOG_MSG_ID = "KACS_00002";
    
    /**
     * 定期的に生存確認を行う間隔[ms]を設定する。<p>
     * この属性を設定しない場合は、毎回問い合わせを行う。<br>
     * デフォルトでは、毎回問い合わせを行う。<br>
     *
     * @param millis KeepAliveCheckerに生存確認を行う間隔
     */
    public void setCheckInterval(long millis);
    
    /**
     * 定期的に生存確認を行う間隔[ms]を取得する。<p>
     *
     * @return KeepAliveCheckerに生存確認を行う間隔
     */
    public long getCheckInterval();
    
    /**
     * 稼動状態が停止状態から走行状態に変化した時に出力するログのメッセージIDを設定する。<p>
     *
     * @param id ログのメッセージID
     */
    public void setAliveLogMessageId(String id);
    
    /**
     * 稼動状態が停止状態から走行状態に変化した時に出力するログのメッセージIDを取得する。<p>
     *
     * @return ログのメッセージID
     */
    public String getAliveLogMessageId();
    
    /**
     * 稼動状態が走行状態から停止状態に変化した時に出力するログのメッセージIDを設定する。<p>
     *
     * @param id ログのメッセージID
     */
    public void setDeadLogMessageId(String id);
    
    /**
     * 稼動状態が走行状態から停止状態に変化した時に出力するログのメッセージIDを取得する。<p>
     *
     * @return ログのメッセージID
     */
    public String getDeadLogMessageId();
    
    /**
     * 稼動状態が停止状態から走行状態に変化した時にログを出力するかどうかを設定する。<p>
     * デフォルトは、true。<br>
     *
     * @param isOutput 出力する場合は、true
     */
    public void setOutputAliveLogMessage(boolean isOutput);
    
    /**
     * 稼動状態が停止状態から走行状態に変化した時にログを出力するかどうかを判定する。<p>
     *
     * @return trueの場合は、出力する
     */
    public boolean isOutputAliveLogMessage();
    
    /**
     * 稼動状態が走行状態から停止状態に変化した時にログを出力するかどうかを設定する。<p>
     * デフォルトは、true。<br>
     *
     * @param isOutput 出力する場合は、true
     */
    public void setOutputDeadLogMessage(boolean isOutput);
    
    /**
     * 稼動状態が走行状態から停止状態に変化した時にログを出力するかどうかを判定する。<p>
     *
     * @return trueの場合は、出力する
     */
    public boolean isOutputDeadLogMessage();
    
    /**
     * サーバの稼動状態を判定する。<p>
     *
     * @return 稼動状態（true:走行中、false:停止中）
     */
    public boolean isAlive();
    
    /**
     * サーバの稼動状態をチェックする。<p>
     *
     * @return 稼動状態（true:走行中、false:停止中）
     * @exception Exception チェック時に例外が発生した場合
     */
    public boolean checkAlive() throws Exception;
}
