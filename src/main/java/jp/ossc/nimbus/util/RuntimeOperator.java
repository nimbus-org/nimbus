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

package jp.ossc.nimbus.util;

/**
*	VB命令エミュレーションオブジェクト
*	@author		Hirotaka.Nakano
*	@version	1.00 作成：2001.04.04 － H.Nakano<BR>
*				更新：
*/
public class RuntimeOperator {
	public static final int C_NOAP = -10000 ;
	public static final String C_SPACE = " " ; //$NON-NLS-1$
	/**
	 *	終了待ちEXECメソッド<br>
	 *	@return		int					実行結果
	 *	@param		cmdLine				コマンドライン文字列
	 */
	public static int shellWait(String cmdLine){
		//## ローカル宣言 ##
		int	intRet;
		Process procVM = null;
		Runtime rtEnv = Runtime.getRuntime();
		//== コマンドライン文字配列化 ==
		CsvArrayList perse = new CsvArrayList() ;
		perse.split(cmdLine,C_SPACE) ;
		String[] commands = perse.toStringAry();
		//## Exe実行 ##
		try {
			procVM = rtEnv.exec(commands);
			intRet	= procVM.waitFor();
		} catch(Exception e) {
			intRet = C_NOAP;
		}
		return intRet;
	}
	//
	/**
	 *	即時終了EXECメソッド<br>
	 *	@return		boolean				true 正常終了 false 異常終了
	 *	@param		cmdLine				コマンドライン文字列
	 */
	public static boolean shell(String cmdLine){
		//## ローカル宣言 ##
		Runtime rtEnv = Runtime.getRuntime();
		//== コマンドライン文字配列化 ==
		CsvArrayList perse = new CsvArrayList() ;
		perse.split(cmdLine,C_SPACE) ;
		String[] commands = perse.toStringAry();
		//## Exe実行 ##
		try {
			rtEnv.exec(commands);
		} catch(Exception e) {
			return false;
		}
		return true;
	}
	//
}
