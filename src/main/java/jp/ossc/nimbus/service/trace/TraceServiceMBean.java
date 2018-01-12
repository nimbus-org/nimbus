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
package jp.ossc.nimbus.service.trace;
//インポート
import jp.ossc.nimbus.core.ServiceBaseMBean;
import jp.ossc.nimbus.core.ServiceName;

/**
 * {@link TraceService}サービスMBeanインタフェース。<p>
 *
 * @author K.Nagai
 */
public interface TraceServiceMBean extends ServiceBaseMBean {

	/**トレース用のメッセージレコードキー*/
	//トレース取得要求受付時
	public static final String TRACE_ENTRY_KEY     = "TRC__00001";
	//トレース取得要求終了時
	public static final String TRACE_EXIT_KEY      = "TRC__00002";
	//ネストレベル異常発見時
	public static final String TRACE_NESTLEVEL_ERR = "TRC__00003";
	/**トレース取得レベル：出力なし*/
	public static final int DISABLE_LEVEL   = 21;
	/**トレース取得レベル：PUBLIC*/
	public static final int PUBLIC_LEVEL    = 20;
	/**トレース取得レベル：PROTECTED*/
	public static final int PROTECTED_LEVEL = 10;
	/**トレース取得レベル：PRIVATE*/
	public static final int PRIVATE_LEVEL   = 0;
	
	/** 内部的に使用するログサービス名取得*/
	public ServiceName getLogServiceName();
	/** 内部的に使用するログサービス名設定*/
	public void setLogServiceName(ServiceName name);

	/** 内部的に使用するエディタファインダサービス名取得*/
	public ServiceName getEditorFinderServiceName();
	/** 内部的に使用するエディタファインダサービス名設定*/
	public void setEditorFinderServiceName(ServiceName name);
	/** トレースレベル設定*/
	public void setTraceLevel(int level);
	/** ネストレベル設定*/
	public void setNestedLevel(int level);
	/** セパレタ設定*/
	public void setSeparator(String sep);
}
