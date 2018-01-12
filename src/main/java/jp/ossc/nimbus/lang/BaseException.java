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
package jp.ossc.nimbus.lang;

import java.io.*;

/**
 * 基本例外<br>
 * @author   NRI. Hirotaka Nakano
 * @version  1.00 作成: 2003/09/22 -　H.Nakano
 */
public class BaseException extends Exception {
	
    private static final long serialVersionUID = 2943182462526701811L;
    
    private String mErrorCode;
	private Exception mCause = null;
	private boolean mIsRetry = false;
	static private final String C_NONE = "" ;  //$NON-NLS-1$
	static private final String C_CAUSE = "Caused by: " ;  //$NON-NLS-1$
	//
	/**
	 *	コンストラクタ<br>
	 */
	public BaseException() {
		super();
		mErrorCode = C_NONE;
	}
	//
	/**
	 *	コンストラクタ<br>
	 *	@param	errCode 		エラーコード
	 *	@param	errMsg 			エラーメッセージ
	 *	@param	cause 			エラー原因例外
	 */
	public BaseException(String errCode, 
							String errMsg, 
							Exception cause) {
		super(errMsg);
		mErrorCode = errCode;
		mCause = cause;
	}
	//
	/**
	 *	コンストラクタ<br>
	 *	@param	errCode 		エラーコード
	 *	@param	errMsg 			エラーメッセージ
	 */
	public BaseException(String errCode, String errMsg) {
		super(errMsg);
		mErrorCode = errCode;
	}
	/**
	 *	コンストラクタ<br>
	 *	@param	errCode 		エラーコード
	 *	@param	errMsg 			エラーメッセージ
	 *	@param	cause 			エラー原因例外
	 *	@param isRetry			リトライ要請フラグ
	 */
	public BaseException(String errCode,
							String errMsg,
							Exception cause,
							boolean isRetry) {
		super(errMsg);
		mErrorCode = errCode;
		mCause = cause;
		mIsRetry = isRetry;
	}
	//
	/**
	 *	コンストラクタ<br>
	 * @param errCode		エラーコード
	 * @param errMsg		エラーメッセージ		
	 * @param isRetry		リトライ要請フラグ
	 */
	public BaseException(String errCode,
						  String errMsg, 
						  boolean isRetry) {
		super(errMsg);
		mErrorCode = errCode;
		mIsRetry = isRetry;
	}
	//
	/**
	 *	リトライ要請ゲッター<br>
	 *	@return	boolean リトライ要請
	 */
	public boolean IsRetry() {
		return mIsRetry;
	}
	//
	/**
	 *	エラーコードゲッター<br>
	 *	@return	String エラーコード
	 */
	public String getErrCode() {
		return mErrorCode;
	}
	/**
	 * Method getCause.<BR>
	 * 例外原因を出力する。
	 * @return Exception
	 */
	public Throwable getCause() {
		return (mCause == this ? null : mCause);
	}

	/**
	 * Prints this Exception and its backtrace to the specified print stream.
	 *
	 * @param s <code>PrintStream</code> to use for output
	 */
	public void printStackTrace(PrintWriter s) {
		synchronized (s) {
			super.printStackTrace(s);
			Throwable ourCause = getCause();
			if (ourCause != null){
				s.println(C_CAUSE + ourCause);
				//ourCause.printStackTrace(s);
			}

		}
	}
	//
	/**
	 *	原因例外スタックトレースゲッター<br>
	 *	@return	String スタックトレース
	 */
	public String getStackTraceString() {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		this.printStackTrace(pw);
		pw.close();
		String tmp = sw.toString();
		return tmp;
	}
	/* (非 Javadoc)
	 * @see java.lang.Throwable#printStackTrace()
	 */
	public void printStackTrace() { 
		synchronized (System.err) {
			this.printStackTrace(new PrintWriter(System.err,true)) ;
		}
	}

}
