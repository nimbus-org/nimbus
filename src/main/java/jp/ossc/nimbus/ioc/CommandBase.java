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
//パッケージ
package jp.ossc.nimbus.ioc;
// インポート
/**
 * コマンドの基本インターフェイス<p>
 * @version $Name:  $
 * @author H.Nakano
 * @since 1.0
 * @see	Command
 * @see	UnitOfWorkImpl 
 */
public interface CommandBase {
	/** コマンドの実行前ステータス */
	static public final int C_STATUS_BEFORE = -1 ;
	/** コマンドの正常終了ステータス */
	static public final int C_STATUS_COMPLETE = 0 ;
	/** コマンドの異常終了ステータス */
	static public final int C_STATUS_ERROR = 1 ;
	/**
	 * コマンド実装か出力する
	 * @return　コマンド実装ならture
	 */
	public boolean isCommand() ;
	/**
	 * 発生した例外件数を出力する
	 * @return　発生した例外件数
	 */
	public int getExceptionCount() ;
	/**
	 * 発生した例外を配列で出力する。
	 * @return　例外配列
	 */
	public Throwable[] getExceptions();
	/**
	 * 例外の発生したコマンドを検索する
	 * @param e　発生した例外オブジェクト
	 * @return	コマンドオブジェクト
	 * @see	Command
	 */
	public Command findErrorCommand(Throwable e) ;
	/**
	 * 格納されているコマンドの総数
	 * @return	コマンド総数
	 */
	public int commandSize() ;
	/**
	 * 実行したコマンドの総数
	 * @return	実行コマンド数
	 */
	public int commandExecuteSize() ;
	
	/**
	 * 実行結果を出力する
	 * 
	 * @return コマンドの実行前ステータス 		C_STATUS_BEFORE <BR> 
	 *   		コマンドの正常終了ステータス 	C_STATUS_COMPLETE <BR>
	 *   		コマンドの異常終了ステータス 	C_STATUS_ERROR<BR> 
	 */
	public int getStatus() ;
	/**
	 * 格納されているユニットオブワークの総数を出力する
	 * @return	格納されているユニットオブワークの総数
	 */
	public int unitOfWorkSize() ;
	/**
	 * 正常実行されたユニットオブワークの総数を出力する
	 * @return	正常実行されたユニットオブワークの総数
	 */
	public int unitOfWorkExecuteSize() ;
}
