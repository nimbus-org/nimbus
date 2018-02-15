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

import java.util.Map;

import jp.ossc.nimbus.core.*;

/**
 * {@link RequestProcessCheckInterceptorService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see RequestProcessCheckInterceptorService
 */
public interface RequestProcessCheckInterceptorServiceMBean
 extends ServiceBaseMBean{
    
    public static final String DEBUG_MESSAGE_ID = "RPC__00001";
    public static final String INFO_MESSAGE_ID  = "RPC__00002";
    public static final String ERROR_MESSAGE_ID = "RPC__00003";
    public static final String FATAL_MESSAGE_ID = "RPC__00004";
    
    /**
     * ログ出力する{@link jp.ossc.nimbus.service.log.Logger Logger}サービスのサービス名を設定する。<p>
     * デフォルトでは、{@link RequestProcessCheckInterceptorService#getLogger()}で取得できるLoggerを使用する。<br>
     *
     * @param name Loggerサービス名
     */
    public void setReportingLoggerServiceName(ServiceName name);
    
    /**
     * ログ出力する{@link jp.ossc.nimbus.service.log.Logger Logger}サービスのサービス名を取得する。<p>
     *
     * @return Loggerサービス名
     */
    public ServiceName getReportingLoggerServiceName();
    
    /**
     * 処理時間の閾値と出力するログのメッセージIDのマッピングを設定する。<p>
     * 処理時間の閾値[ms]=出力するログのメッセージID,ログの出力間隔[ms]<br>
     * ログの出力間隔を指定しない場合、または0を指定した場合は、チェックする毎に出力する。負の数値を指定した場合は、１回だけ出力する。0より大きい数値を指定した場合には、その間隔[ms]でログを出力する。<br>
     *
     * @param threshold 処理時間の閾値と出力するログのメッセージIDのマッピング
     */
    public void setThreshold(Map threshold);
    
    /**
     * 処理時間の閾値と出力するログのメッセージIDのマッピングを取得する。<p>
     *
     * @return 処理時間の閾値と出力するログのメッセージIDのマッピング
     */
    public Map getThreshold();
    
    /**
     * 処理時間のチェックを行う間隔[ms]を設定する。<p>
     *
     * @param interval チェック間隔
     */
    public void setCheckInterval(long interval);
    
    /**
     * 処理時間のチェックを行う間隔[ms]を取得する。<p>
     *
     * @return チェック間隔
     */
    public long getCheckInterval();
    
    /**
     * 現在のリクエストのレポートを出力する。<p>
     * 
     * @return 現在のリクエストのレポート
     */
    public String displayCurrentReport();
    
    /**
     * 処理時間のチェックを中断する。<p>
     */
    public void suspendChecker();
    
    /**
     * 処理時間のチェックを再開する。<p>
     */
    public void resumeChecker();
    
    /**
     * 指定されたリクエスト中スレッドをインターラプトする。<p>
     *
     * @param groupName スレッドのグループ名
     * @param threadName スレッド名
     * @return インターラプトできた場合は、true
     */
    public boolean interruptRequest(String groupName, String threadName);
    
    /**
     * 指定されたリクエスト中スレッドをチェック対象から削除する。<p>
     *
     * @param groupName スレッドのグループ名
     * @param threadName スレッド名
     * @return 削除できた場合は、true
     */
    public boolean removeRequest(String groupName, String threadName);
    
    /**
     * リクエスト中スレッドをチェック対象から全て削除する。<p>
     */
    public void clearRequest();
    
    /**
     * リクエスト中のスレッド数を取得する。<p>
     *
     * @return リクエスト中のスレッド数
     */
    public int getRequestCount();
}