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
// パッケージ
package jp.ossc.nimbus.service.scheduler;
//インポート
import jp.ossc.nimbus.core.*;
/**
 * タイムスケジューラークラス<p>
 * 一定時間毎に問い合わせを上げて指定されたコマンドをQueueに入れる
 * @version $Name:  $
 * @author H.Nakano
 * @since 1.0
 */
public interface QueueEntrySchdulerServiceMBean extends ServiceBaseMBean {
	/**
	 * Facade呼び出しサービス名設定
	 * @param name
	 */
	public void setFacadeServiceName(ServiceName name) ;
	/**
	 * Facade呼び出しサービス名出力
	 * @return ServiceName
	 */
	public ServiceName getFacadeServiceName() ;
	/**
	 * 問い合わせインターバル設定
	 * @param msecs ミリ秒
	 */
	public void setInterval(long msecs) ;
	/**
	 * 問い合わせインターバル出力
	 * @return long ミリ秒
	 */
	public long getInterval() ;
	/**
	 * 問い合わせサービス名設定
	 * @param name
	 */
	public void setGetTaskFlowKey(String name) ;
	/**
	 * 問い合わせサービス名出力
	 * @return 問い合わせサービス名
	 */
	public String getGetTaskFlowKey() ;
	/**
	 * ログサービス名セッター
	 * @param name
	 */
	public void setLogServiceName(ServiceName name) ;
	/**
	 * シーケンスサービス名出力
	 * @return　シーケンスサービス名
	 */
	public ServiceName getSequenceServiceName() ;

	/**
	 * ユーザIDゲッター
	 * @return ユーザID
	 */
	public String getUserId() ;

	/**
	 * シーケンスサービス名セっター
	 * @param name
	 */
	public void setSequenceServiceName(ServiceName name);

	/**
	 * ユーザIDゲッター
	 * @param string
	 */
	public void setUserId(String string) ;
}
