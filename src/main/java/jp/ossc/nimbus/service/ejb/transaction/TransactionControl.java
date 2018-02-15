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
package jp.ossc.nimbus.service.ejb.transaction;

/**
 * EJBコンテナ内でのトランザクションコントロールクラス<p>
 * このクラスはEJBコンテナ内のトランザクションマネージャを使い<br>
 * グローバルトランザクションのコントロールを行う。
 * @author   H.Nakano
 * @version  1.00 作成: 2003/11/28 -　H.Nakano
 * @since	1.00
 * @see	javax.transaction.TransactionManager
 **/
public interface TransactionControl {
	/**
	 * グローバルトランザクションを中断する。<p>
	 * すでに中断中ステートである場合は無視される。
	 */
	public void suspend() ;
	/**
	 * グローバルトランザクションを再開する。<p>
	 */
	public void resume() ;
	/**
	 * 新しいグローバルトランザクションを開始する。<p>
	 * このメソッドはカレントのグローバルトランザクションを<br>
	 * サスペンドした状態で有効になる。
	 * @see #suspend()
	 */
	public void beginNewTransaction() ;
	/**
	 * 開始されている新しいトランザクションについてコミットする。<P>
	 */
	public void commitNewTransaction() ;
	/**
	 * 開始されている新しいトランザクションについてロールバックする。<P>
	 */
	public void rollBackNewTransaction() ;
	public void terminateTransactioinControl();
	/**
	 * 現在のトランザクションの状態を出力する。<p>
	 * @return トランザクション状態
	 * @see	#INIT_STATE
	 * @see	#SUSPEND_STATE
	 **/
	public int getState() ;
	/** 初期状態<p>*/
	public static final int INIT_STATE = 0 ;
	/** サスペンド状態<p>*/
	public static final int SUSPEND_STATE = 1 ;
}
