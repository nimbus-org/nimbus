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

/**
 * サービスがスローする例外<br>
 * @author   NRI. Hirotaka Nakano
 * @version  1.00 作成: 2003/09/22 -　H.Nakano
 */
public class ServiceException extends BaseRuntimeException {
	
    private static final long serialVersionUID = -6521476442362418922L;
    
    //
	/**
	 *	コンストラクタ<br>
	 */
  	public ServiceException() {
		super() ;
  	}
	//
	/**
	 *	コンストラクタ<br>
	 *	@param	errCode 		エラーコード
	 *	@param	errMsg 			エラーメッセージ
	 *	@param	cause 			エラー原因例外
	 */
  	public ServiceException (	String errCode, 
  								String errMsg,	
  								Throwable cause) {
		super(errCode,errMsg,cause) ;
  	}
	//
	/**
	 *	コンストラクタ<br>
	 *	@param	errCode 		エラーコード
	 *	@param	errMsg 			エラーメッセージ
	 */
  	public ServiceException (	String errCode, 
  								String errMsg ) {
		super(errCode,errMsg) ;
  	}
	/**
	 *	コンストラクタ<br>
	 *	@param	errCode 		エラーコード
	 *	@param	errMsg 			エラーメッセージ
	 *	@param	cause 			エラー原因例外
	 *  @param isRetry			リトライ要請
	 */
  	public ServiceException (	String errCode, 
	  							String errMsg,	
								Throwable cause,
  								boolean isRetry) {
		super(errCode,errMsg,cause,isRetry) ;
  	}
	//
	/**
	 *	コンストラクタ<br>
	 *	@param	errCode 		エラーコード
	 *	@param	errMsg 			エラーメッセージ
	 *  @param isRetry			リトライ要請
	 */
  	public ServiceException (	String errCode, 
  								String errMsg,
  								boolean isRetry ) {
		super(errCode,errMsg,isRetry) ;
  	}
}
