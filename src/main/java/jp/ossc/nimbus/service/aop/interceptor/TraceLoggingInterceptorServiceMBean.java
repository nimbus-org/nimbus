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
package jp.ossc.nimbus.service.aop.interceptor;

import jp.ossc.nimbus.core.*;

/**
 * {@link TraceLoggingInterceptorService}のMBeanインタフェース。<p>
 * 
 * @author M.Takata
 * @see TraceLoggingInterceptorService
 */
public interface TraceLoggingInterceptorServiceMBean extends ServiceBaseMBean{
    
    public static final String DEFAULT_TRACE_REQUEST_MESSAGE_ID  = "TLIS_00001";
    public static final String DEFAULT_TRACE_RESPONSE_MESSAGE_ID = "TLIS_00002";
    
    /**
     * トレースログ出力を行うかどうかを設定する。<p>
     * デフォルトでは、true。
     *
     * @param enable トレースログ出力を行う場合true
     * @see #isEnabled()
     */
    public void setEnabled(boolean enable);
    
    /**
     * トレースログ出力を行うかどうかを判定する。<p>
     *
     * @return トレースログ出力を行う場合true
     * @see #setEnabled(boolean)
     */
    public boolean isEnabled();
    
    /**
     * 呼び出し時に出力するログのメッセージIDを設定する。<p>
     *
     * @param id 出力するログのメッセージID
     */
    public void setRequestMessageId(String id);
    
    /**
     * 呼び出し時に出力するログのメッセージIDを取得する。<p>
     *
     * @return 出力するログのメッセージID
     */
    public String getRequestMessageId();
    
    /**
     * 応答時に出力するログのメッセージIDを設定する。<p>
     *
     * @param id 出力するログのメッセージID
     */
    public void setResponseMessageId(String id);
    
    /**
     * 応答時に出力するログのメッセージIDを取得する。<p>
     *
     * @return 出力するログのメッセージID
     */
    public String getResponseMessageId();
    
    /**
     * 呼び出し時にログを出力するかどうかを設定する。<p>
     * デフォルトは、trueで出力する。<br>
     *
     * @param isOutput 出力する場合true
     */
    public void setOutputRequestLog(boolean isOutput);
    
    /**
     * 呼び出し時にログを出力するかどうかを判定する。<p>
     *
     * @return trueの場合、出力する
     */
    public boolean  isOutputRequestLog();
    
    /**
     * 応答時にログを出力するかどうかを設定する。<p>
     * デフォルトは、falseで出力しない。<br>
     *
     * @param isOutput 出力する場合true
     */
    public void setOutputResponseLog(boolean isOutput);
    
    /**
     * 応答時にログを出力するかどうかを判定する。<p>
     *
     * @return trueの場合、出力する
     */
    public boolean  isOutputResponseLog();
    
    /**
     * 呼び出し時のログ出力で呼び出し対象を出力するかどうかを設定する。<p>
     * デフォルトは、trueで出力する。<br>
     *
     * @param isOutput 出力する場合true
     */
    public void setOutputTarget(boolean isOutput);
    
    /**
     * 呼び出し時のログ出力で呼び出し対象を出力するかどうかを判定する。<p>
     *
     * @return trueの場合、出力する
     */
    public boolean  isOutputTarget();
    
    /**
     * 呼び出し時のログ出力で呼び出し対象からプロパティを指定して出力するように設定する。<p>
     *
     * @param props 出力する対象のプロパティ配列
     */
    public void setOutputTargetProperties(String[] props);
    
    /**
     * 呼び出し時のログ出力で呼び出し対象からプロパティを指定して出力するように取得する。<p>
     *
     * @return 出力する対象のプロパティ配列
     */
    public String[] getOutputTargetProperties();
    
    /**
     * 呼び出し時のログ出力で呼び出しメソッドを出力するかどうかを設定する。<p>
     * デフォルトは、trueで出力する。<br>
     *
     * @param isOutput 出力する場合true
     */
    public void setOutputMethod(boolean isOutput);
    
    /**
     * 呼び出し時のログ出力で呼び出しメソッドを出力するかどうかを判定する。<p>
     *
     * @return trueの場合、出力する
     */
    public boolean  isOutputMethod();
    
    /**
     * 呼び出し時のログ出力で呼び出しメソッド引数を出力するかどうかを設定する。<p>
     * デフォルトは、falseで出力しない。<br>
     *
     * @param isOutput 出力する場合true
     */
    public void setOutputParameter(boolean isOutput);
    
    /**
     * 呼び出し時のログ出力で呼び出しメソッド引数を出力するかどうかを判定する。<p>
     *
     * @return trueの場合、出力する
     */
    public boolean  isOutputParameter();
    
    /**
     * 呼び出し時のログ出力で呼び出しメソッド引数からプロパティを指定して出力するように設定する。<p>
     *
     * @param props 出力するメソッド引数のプロパティ配列
     */
    public void setOutputParameterProperties(String[] props);
    
    /**
     * 呼び出し時のログ出力で呼び出しメソッド引数からプロパティを指定して出力するように取得する。<p>
     *
     * @return 出力するメソッド引数のプロパティ配列
     */
    public String[] getOutputParameterProperties();
    
    /**
     * 呼び出し時のログ出力で呼び出しスタックを出力するかどうかを設定する。<p>
     * デフォルトは、falseで出力しない。<br>
     *
     * @param isOutput 出力する場合true
     */
    public void setOutputCallStackTrace(boolean isOutput);
    
    /**
     * 呼び出し時のログ出力で呼び出しメソッド引数を出力するかどうかを判定する。<p>
     *
     * @return trueの場合、出力する
     */
    public boolean  isOutputCallStackTrace();
    
    /**
     * 応答時のログ出力で戻り値を出力するかどうかを設定する。<p>
     * デフォルトは、trueで出力する。<br>
     *
     * @param isOutput 出力する場合true
     */
    public void setOutputReturn(boolean isOutput);
    
    /**
     * 応答時のログ出力で戻り値を出力するかどうかを判定する。<p>
     *
     * @return trueの場合、出力する
     */
    public boolean  isOutputReturn();
    
    /**
     * 応答時のログ出力で処理時間[ms]を出力するかどうかを設定する。<p>
     * デフォルトは、falseで出力しない。<br>
     *
     * @param isOutput 出力する場合true
     */
    public void setOutputPerformance(boolean isOutput);
    
    /**
     * 応答時のログ出力で処理時間[ms]を出力するかどうかを判定する。<p>
     *
     * @return trueの場合、出力する
     */
    public boolean  isOutputPerformance();
    
    /**
     * 応答時のログ出力で呼び出し対象を出力するかどうかを設定する。<p>
     * デフォルトは、falseで出力しない。<br>
     *
     * @param isOutput 出力する場合true
     */
    public void setOutputTargetOnResponse(boolean isOutput);
    
    /**
     * 応答時のログ出力で呼び出し対象を出力するかどうかを判定する。<p>
     *
     * @return trueの場合、出力する
     */
    public boolean  isOutputTargetOnResponse();
    
    /**
     * 応答時のログ出力で呼び出しメソッドを出力するかどうかを設定する。<p>
     * デフォルトは、falseで出力しない。<br>
     *
     * @param isOutput 出力する場合true
     */
    public void setOutputMethodOnResponse(boolean isOutput);
    
    /**
     * 応答時のログ出力で呼び出しメソッドを出力するかどうかを判定する。<p>
     *
     * @return trueの場合、出力する
     */
    public boolean  isOutputMethodOnResponse();
    
    /**
     * 応答時のログ出力で呼び出しメソッド引数を出力するかどうかを設定する。<p>
     * デフォルトは、falseで出力しない。<br>
     *
     * @param isOutput 出力する場合true
     */
    public void setOutputParameterOnResponse(boolean isOutput);
    
    /**
     * 応答時のログ出力で呼び出しメソッド引数を出力するかどうかを判定する。<p>
     *
     * @return trueの場合、出力する
     */
    public boolean  isOutputParameterOnResponse();
    
    /**
     * 呼び出し時のログ出力で戻り値からプロパティを指定して出力するように設定する。<p>
     *
     * @param props 出力する戻り値のプロパティ配列
     */
    public void setOutputReturnProperties(String[] props);
    
    /**
     * 呼び出し時のログ出力で戻り値からプロパティを指定して出力するように取得する。<p>
     *
     * @return 出力する戻り値のプロパティ配列
     */
    public String[] getOutputReturnProperties();
    
    /**
     * 応答時のログ出力で発生した例外を出力するかどうかを設定する。<p>
     * デフォルトは、falseで出力しない。<br>
     *
     * @param isOutput 出力する場合true
     */
    public void setOutputThrowable(boolean isOutput);
    
    /**
     * 応答時のログ出力で発生した例外を出力するかどうかを判定する。<p>
     *
     * @return trueの場合、出力する
     */
    public boolean  isOutputThrowable();
    
    /**
     * トレースログに付与する連番を発番する{@link jp.ossc.nimbus.service.sequence.Sequence Sequence}サービスのサービス名を設定する。<p>
     *
     * @param name Sequenceサービスのサービス名
     */
    public void setSequenceServiceName(ServiceName name);
    
    /**
     * トレースログに付与する連番を発番する{@link jp.ossc.nimbus.service.sequence.Sequence Sequence}サービスのサービス名を取得する。<p>
     *
     * @return Sequenceサービスのサービス名
     */
    public ServiceName getSequenceServiceName();
}