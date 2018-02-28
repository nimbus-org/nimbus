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
package jp.ossc.nimbus.ioc;
//インポート

/**
 * EJB実行コマンドインターフェイス<p>
 * @version $Name:  $
 * @author H.Nakano
 * @since 1.0
 */
public interface Command extends CommandBase {
	/**
	 * 入力オブジェクトを設定する
	 * @param obj　入力オブジェクト
	 */
	public void setInputObject(Object obj) ;
	/**
	 * 出力オブジェクトを設定する
	 * @param obj	出力オブジェクト
	 */
	public void setOutObject(Object obj) ;
	/**
	 * 出力オブジェクトを出力する
	 * @return	出力オブジェクト
	 */
	public Object getOutputObject() ;	
	/**
	 * 入力オブジェクトを入力する
	 * @return	入力オブジェクト
	 */
	public Object getInputObject() ;
	/**
	 * 発生例外を設定する
	 * @param e
	 */
	public void setException(Throwable e) ;	
	/**
	 * 発生例外を取得する
	 * @return 発生例外
	 */
	public Throwable getException() ;	
	/**
	 * 実行するBeanフローのフローキーを出力する
	 * @return	フローキー
	 */
	public String getFlowKey() ;
	/**
	 * 実行するBeanフローのフローキーを設定する
	 * @param flowKey	フローキー
	 */
	public void setFlowKey(String flowKey) ;
}