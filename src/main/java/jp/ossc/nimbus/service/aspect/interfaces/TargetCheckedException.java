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
// インポート
package jp.ossc.nimbus.service.aspect.interfaces;

/**
 * ルート実行メソッドチェック例外クラス<p>
 * @version $Name:  $
 * @author H.Nakano
 * @since 1.0
 */
public class TargetCheckedException extends Exception {
    
    private static final long serialVersionUID = -601670536604794153L;
    
	/**
	 * コンストラクター
	 */
	public TargetCheckedException() {
		super();
	}

	/**
	 * コンストラクター
	 * @param arg0 例外メッセージ
	 */
	public TargetCheckedException(String arg0) {
		super(arg0);
	}

	/**
	 * コンストラクター
	 * @param arg0	根本例外
	 */
	public TargetCheckedException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * コンストラクター
	 * @param arg0	例外メッセージ
	 * @param arg1	根本例外	
	 */
	public TargetCheckedException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
