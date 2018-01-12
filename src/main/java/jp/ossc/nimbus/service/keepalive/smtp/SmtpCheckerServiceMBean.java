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
package jp.ossc.nimbus.service.keepalive.smtp;

import jp.ossc.nimbus.core.*;
//
/**
 * SMTPサーバチェッカーの管理インターフェイス。<p>
 *
 * @author H.Nakano
 * @version  1.00 作成: 2003/10/09 - H.Nakano
 */
public interface SmtpCheckerServiceMBean extends ServiceBaseMBean, SmtpKeepAliveChecker{
    
    /**
     * SMTPサーバ生存チェックで、SMTPサーバが死んだ時に出力されるログのメッセージID。<p>
     */
    public static final String SMTP_SERVER_DEAD_MSG_ID = "SMTP_00001";
    
    /**
     * SMTPサーバ生存チェックで、SMTPサーバが復帰した時に出力されるログのメッセージID。<p>
     */
    public static final String SMTP_SERVER_RECOVER_MSG_ID = "SMTP_00002";
    
    /**
     * SMTPサーバのホスト名を設定する。<p>
     *
     * @param hostName SMTPサーバのホスト名
     */
    public void setHostName(String hostName) throws java.net.UnknownHostException;
    
    /**
     * SMTPサーバのホスト名を取得する。<p>
     *
     * @return SMTPサーバのホスト名
     */
    public String getHostName();
    
    /**
     * SMTPサーバのPort番号を設定する。<p>
     *
     * @param port SMTPサーバのPort番号
     */
    public void setPort(int port);
    
    /**
     * SMTPサーバのPort番号を取得する。<p>
     *
     * @return SMTPサーバのPort番号
     */
    public int getPort();
    
    /**
     * 接続待ちのタイムアウト[ms]を設定する。<p>
     * デフォルトは、0で無限待ち。<br>
     *
     * @param milisec タイムアウト[ms]
     */
    public void setConnectionTimeoutMillis(int milisec);
    
    /**
     * 接続待ちのタイムアウト[ms]を取得する。<p>
     *
     * @return タイムアウト[ms]
     */
    public int getConnectionTimeoutMillis();
    
    /**
     * 応答待ちのタイムアウト[ms]を設定する。<p>
     * デフォルトは、1秒。<br>
     *
     * @param milisec タイムアウト[ms]
     */
    public void setTimeoutMillis(int milisec);
    
    /**
     * 応答待ちのタイムアウト[ms]を取得する。<p>
     *
     * @return タイムアウト[ms]
     */
    public int getTimeoutMillis();
    
    /**
     * EOFを検知した場合に出力するログのメッセージIDを設定する。<p>
     *
     * @param id ログのメッセージID
     */
    public void setEOFLogMessageId(String id);
    
    /**
     * EOFを検知した場合に出力するログのメッセージIDを取得する。<p>
     *
     * @return ログのメッセージID
     */
    public String getEOFLogMessageId();
    
    /**
     * サーバからエラー状態を受信した場合に出力するログのメッセージIDを設定する。<p>
     *
     * @param id ログのメッセージID
     */
    public void setErrorStateLogMessageId(String id);
    
    /**
     * サーバからエラー状態を受信した場合に出力するログのメッセージIDを取得する。<p>
     *
     * @return ログのメッセージID
     */
    public String getErrorStateLogMessageId();
    
    /**
     * サーバから正常状態を受信した場合に出力するログのメッセージIDを設定する。<p>
     *
     * @param id ログのメッセージID
     */
    public void setNormalStateLogMessageId(String id);
    
    /**
     * サーバから正常状態を受信した場合に出力するログのメッセージIDを取得する。<p>
     *
     * @return ログのメッセージID
     */
    public String getNormalStateLogMessageId();
    
    /**
     * サーバからの応答待ちでタイムアウトした場合に出力するログのメッセージIDを設定する。<p>
     *
     * @param id ログのメッセージID
     */
    public void setTimeoutLogMessageId(String id);
    
    /**
     * サーバからの応答待ちでタイムアウトした場合に出力するログのメッセージIDを取得する。<p>
     *
     * @return ログのメッセージID
     */
    public String getTimeoutLogMessageId();
    
    /**
     * TCPレベルでのプロトコルエラーが発生した場合に出力するログのメッセージIDを設定する。<p>
     *
     * @param id ログのメッセージID
     */
    public void setProtocolErrorLogMessageId(String id);
    
    /**
     * TCPレベルでのプロトコルエラーが発生した場合に出力するログのメッセージIDを取得する。<p>
     *
     * @return ログのメッセージID
     */
    public String getProtocolErrorLogMessageId();
    
    /**
     * 入出力エラーが発生した場合に出力するログのメッセージIDを設定する。<p>
     *
     * @param id ログのメッセージID
     */
    public void setIOErrorLogMessageId(String id);
    
    /**
     * 入出力エラーが発生した場合に出力するログのメッセージIDを取得する。<p>
     *
     * @return ログのメッセージID
     */
    public String getIOErrorLogMessageId();
    
    /**
     * EOFを検知した場合にログを出力するかどうかを設定する。<p>
     * デフォルトでは、false。<br>
     *
     * @param isOutput 出力する場合は、true
     */
    public void setOutputEOFLogMessage(boolean isOutput);
    
    /**
     * EOFを検知した場合にログを出力するかどうかを判定する。<p>
     *
     * @return trueの場合は、出力する
     */
    public boolean isOutputEOFLogMessage();
    
    /**
     * サーバからエラー状態を受信した場合にログを出力するかどうかを設定する。<p>
     * デフォルトでは、false。<br>
     *
     * @param isOutput 出力する場合は、true
     */
    public void setOutputErrorStateLogMessage(boolean isOutput);
    
    /**
     * サーバからエラー状態を受信した場合にログを出力するかどうかを判定する。<p>
     *
     * @return trueの場合は、出力する
     */
    public boolean isOutputErrorStateLogMessage();
    
    /**
     * サーバから正常状態を受信した場合にログを出力するかどうかを設定する。<p>
     * デフォルトでは、false。<br>
     *
     * @param isOutput 出力する場合は、true
     */
    public void setOutputNormalStateLogMessage(boolean isOutput);
    
    /**
     * サーバから正常状態を受信した場合にログを出力するかどうかを判定する。<p>
     *
     * @return trueの場合は、出力する
     */
    public boolean isOutputNormalStateLogMessage();
    
    /**
     * サーバからの応答待ちでタイムアウトした場合にログを出力するかどうかを設定する。<p>
     * デフォルトでは、false。<br>
     *
     * @param isOutput 出力する場合は、true
     */
    public void setOutputTimeoutLogMessage(boolean isOutput);
    
    /**
     * サーバからの応答待ちでタイムアウトした場合にログを出力するかどうかを判定する。<p>
     *
     * @return trueの場合は、出力する
     */
    public boolean isOutputTimeoutLogMessage();
    
    /**
     * TCPレベルでのプロトコルエラーが発生した場合にログを出力するかどうかを設定する。<p>
     * デフォルトでは、false。<br>
     *
     * @param isOutput 出力する場合は、true
     */
    public void setOutputProtocolErrorLogMessage(boolean isOutput);
    
    /**
     * TCPレベルでのプロトコルエラーが発生した場合にログを出力するかどうかを判定する。<p>
     *
     * @return trueの場合は、出力する
     */
    public boolean isOutputProtocolErrorLogMessage();
    
    /**
     * 入出力エラーが発生した場合にログを出力するかどうかを設定する。<p>
     * デフォルトでは、false。<br>
     *
     * @param isOutput 出力する場合は、true
     */
    public void setOutputIOErrorLogMessage(boolean isOutput);
    
    /**
     * 入出力エラーが発生した場合にログを出力するかどうかを判定する。<p>
     *
     * @return trueの場合は、出力する
     */
    public boolean isOutputIOErrorLogMessage();
    
    /**
     * SMTPサーバの生存チェックを行うかどうかを設定する。<p>
     * trueに設定された場合、{@link #setAliveCheckSMTPServerInterval(long)}で設定された間隔で、"HELO"メッセージを送信して、SMTPサーバの生存チェックを行う。<br>
     * "HELO"メッセージの応答が正常でない場合、エラーログを出力する。また、正常応答できるようになった場合、通知ログを出力する。<br>
     *
     * @param isCheck 生存チェックを行う場合はtrue
     */
    public void setAliveCheckSMTPServer(boolean isCheck);
    
    /**
     * SMTPサーバの生存チェックを行うかどうかを設定する。<p>
     *
     * @return 生存チェックを行う場合はtrue
     */
    public boolean isAliveCheckSMTPServer();
    
    /**
     * SMTPサーバの生存チェックを行う間隔[msec]を設定する。<p>
     * デフォルトは、60000[msec]。
     * 
     * @param interval SMTPサーバの生存チェックを行う間隔[msec]
     */
    public void setAliveCheckSMTPServerInterval(long interval);
    
    /**
     * SMTPサーバの生存チェックを行う間隔[msec]を取得する。<p>
     * 
     * @return SMTPサーバの生存チェックを行う間隔[msec]
     */
    public long getAliveCheckSMTPServerInterval();
    
    /**
     * SMTPサーバが生存しているかどうかを調べる。<p>
     * {@link #isAliveCheckSMTPServer()}がtrueを返す場合は、最後にチェックした時の状態を返す。<br>
     * isAliveCheckSMTPServer()がfalseを返す場合は、即時にチェックして結果を返す。但し、サービスが開始していない場合は、falseを返す。<br>
     * 
     * @return SMTPサーバが生存している場合true
     */
    public boolean isAliveSMTPServer();
    
    /**
     * SMTPサーバがダウンした事を検知した旨のログを出力するかどうかを設定する。<p>
     * デフォルトは、true。<br>
     *
     * @param isOutput ログを出力する場合true
     */
    public void setLoggingDeadSMTPServer(boolean isOutput);
    
    /**
     * SMTPサーバがダウンした事を検知した旨のログを出力するかどうかを判定する。<p>
     *
     * @return ログを出力する場合true
     */
    public boolean isLoggingDeadSMTPServer();
    
    /**
     * SMTPサーバが復帰した事を検知した旨のログを出力するかどうかを設定する。<p>
     * デフォルトは、true。<br>
     *
     * @param isOutput ログを出力する場合true
     */
    public void setLoggingRecoverSMTPServer(boolean isOutput);
    
    /**
     * SMTPサーバが復帰した事を検知した旨のログを出力するかどうかを判定する。<p>
     *
     * @return ログを出力する場合true
     */
    public boolean isLoggingRecoverSMTPServer();
    
    /**
     * SMTPサーバがダウンした事を検知した旨のログ出力のメッセージIDを設定する。<p>
     * デフォルトは、{@link #SMTP_SERVER_DEAD_MSG_ID}。<br>
     *
     * @param id ログ出力のメッセージID
     */
    public void setDeadSMTPServerLogMessageId(String id);
    
    /**
     * SMTPサーバがダウンした事を検知した旨のログ出力のメッセージIDを取得する。<p>
     *
     * @return ログ出力のメッセージID
     */
    public String getDeadSMTPServerLogMessageId();
    
    /**
     * SMTPサーバが復帰した事を検知した旨のログ出力のメッセージIDを設定する。<p>
     * デフォルトは、{@link #SMTP_SERVER_RECOVER_MSG_ID}。<br>
     *
     * @param id ログ出力のメッセージID
     */
    public void setRecoverSMTPServerLogMessageId(String id);
    
    /**
     * SMTPサーバが復帰した事を検知した旨のログ出力のメッセージIDを取得する。<p>
     *
     * @return ログ出力のメッセージID
     */
    public String getRecoverSMTPServerLogMessageId();
}
