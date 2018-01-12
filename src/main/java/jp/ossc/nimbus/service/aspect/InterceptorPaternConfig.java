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
import java.util.regex.*;

/**
 * インターセプター毎のキーパターンを管理するクラス<p>
 * @version $Name:  $
 * @author H.Nakano
 * @since 1.0
 */
public class InterceptorPaternConfig {
	// インターセプタ名
	protected ServiceName mInterceptorServiceName = null;
	// パターン配列
	protected Pattern[] mPatterns = null;
	/**
	 * コンストラクタ<br>
	 */
	public InterceptorPaternConfig(){
		super();
	}
	/**
	 * インターセプタ名を格納する<br>
	 * @param interceptorServiceName		インターセプタ名
	 */
	public void setInterceptorServiceName(ServiceName interceptorServiceName){
		mInterceptorServiceName = interceptorServiceName;
	}
	/**
	 * インターセプタ名を返却する<br>
	 * @return interceptorServiceName		インターセプタ名
	 */
	public ServiceName getInterceptorServiceName(){
		return mInterceptorServiceName;
	}
	/**
	 * パターン文字列配列をコンパイルしパターン配列を作成するbr>
	 * @param String[]		パターン文字列配列
	 */
	public void setPatterns(String[] patternStrings){
		mPatterns = null;
		if(patternStrings == null){
			return;
		}
		mPatterns = new Pattern[patternStrings.length];
		for(int icnt = 0; icnt < patternStrings.length; icnt++){
			mPatterns[icnt] = Pattern.compile(patternStrings[icnt]);
		}
	}
	/**
	 * パターン配列を格納する<br>
	 * @param Pattern[]		パターン配列
	 */
	public void setPatterns(Pattern[] patterns){
		mPatterns = patterns;
	}
	/**
	 * パターン配列を返却する<br>
	 * @return Pattern[]		パターン配列
	 */
	public Pattern[] getPatterns(){
		return mPatterns;
	}
	/**
	 * 引数で受け取った文字列とパターン配列のパターンマッチングを行い、<br>
	 * 一つでもマッチした場合trueを返却<br>
	 * @param String			文字列(エイリアス)
	 * @return boolean			true:マッチした/false:マッチしなかった
	 */
	public boolean isMatch(String inString){
		// パターン配列がnullの場合
		if(mPatterns == null){
			// 不一致で返却
			return false;
		}
		// パターン配列でループ
		for(int icnt = 0; icnt < mPatterns.length; icnt++){
			// パターンごとにマッチャを作成
			final Matcher matcher = mPatterns[icnt].matcher(inString);
			// パターンマッチングでマッチした場合
			if(matcher.matches()){
				// 一致で返却
				return true;
			}
		}
		// パターンマッチングでマッチしなかった為不一致で返却
		return false;
	}

}
