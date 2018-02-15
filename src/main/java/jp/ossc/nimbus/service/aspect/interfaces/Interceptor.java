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
package jp.ossc.nimbus.service.aspect.interfaces;


//インポート
//
/**
 * インターセプターインターフェイス<p>
 * インターセプターチェーン内で実行されるインターセプター自身のインターフェイス
 * @version $Name:  $
 * @author H.Nakano
 * @since 1.0
 * @see InterceptorChain
 */
public interface Interceptor{
	/**
	 * チェイン実装用メソッド<br>
	 * 実装する場合、メソッド内でチェインメソッドを実行する必要がある。<br>
	 * @param inputObj			入力オブジェクト
	 * @param InterceptorChain	インターセプタチェーンオブジェクト
	 * @return Object			EJBFacade返却値
	 * @exception InterceptorException		
	 * @exception TargetCheckedException		
	 * @exception	TargetUncheckedException
	 */
	public Object invokeChain(Object inputObj,
	  					       InterceptorChain interceptChain)
	  throws InterceptorException, 
	  		  TargetCheckedException,
	  		  TargetUncheckedException ;
}
