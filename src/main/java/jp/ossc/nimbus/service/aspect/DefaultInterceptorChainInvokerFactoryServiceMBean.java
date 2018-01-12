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
package jp.ossc.nimbus.service.aspect;
//インポート
import jp.ossc.nimbus.core.*;
/**
 * インターセプターチェーンインボーカーファクトリMBeanインターフェイス
 * @version $Name:  $
 * @author H.Nakano
 * @since 1.0
 */
public interface DefaultInterceptorChainInvokerFactoryServiceMBean
	extends ServiceBaseMBean {
	/** 
	 * インターセプタ定義ファイルを設定<br>
	 * @param fileNames - インターセプタ定義
	 * @see #getInterceptConfigFileName()
	 */
	public void setInterceptorConfigFileNames(String[] fileNames);
	//
	/**
	 * インターセプタ定義ファイルを返却する。<br>
	 * @return String[] - インターセプタ定義ファイル
	 * @see #setInterceptorConfigFileNames(String)
	 */
	public String[] getInterceptorConfigFileNames();
	/**
	 * コールバック対象のクラス名を設定する<br>
	 * @param String			コールバック対象のクラス名
	 */
	public void setCallbackClassName(String callbackClassName);
	/**
	 * コールバック対象のクラス名を返却する<br>
	 * @return String			コールバック対象のクラス名
	 */
	public String getCallbackClassName();
	/**
	 * コールバックメソッド名を設定する<br>
	 * @param String			コールバックメソッド名
	 */
	public void setCallbackMethodName(String callbackMethodName);
	/**
	 * コールバックメソッド名を返却する<br>
	 * @return String			コールバックメソッド名
	 */
	public String getCallbackMethodName();
	/**
	 * コールバックメソッドパラーメタクラス名配列を設定する<br>
	 * @param String[]			コールバックメソッドパラーメタクラス名配列
	 */
	public void setCallbackMethodParamClassNames(String[] callbackMethodParamClassNames);
	/**
	 * コールバックメソッド名を返却する<br>
	 * @return String			コールバックメソッド名
	 */
	public String[] getCallbackMethodParamClassNames();
	/**
	 * インターセプタチェインクラス名を設定する<br>
	 * @param String			インターセプタチェインクラス名
	 */
	public void setInterceptorInvokerClassName(String interceptorInvokerClassName);
	/**
	 * インターセプタチェインクラス名を返却する<br>
	 * @return String			インターセプタチェインクラス名
	 */
	public String getInterceptorInvokerClassName();
	/**
	 * 定義ファイル・変換ファイルを読み込む<br>
	 */
	public void loadConfig() throws InvalidConfigurationException;
	/**
	 * ログ出力のサービス名の設定を行う<br>
	 * @param name サービス名
	 */
	public void setLoggerServiceName(ServiceName name) ;
}
