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

import java.util.ArrayList;
/**
*	ストリング操作クラス
*	@author		Hirotaka.Nakano
*	@version	1.00 作成：2001.04.04 － H.Nakano<BR>
*				更新：
*/
public class StringOperator {
	public static final int C_NOAP = -10000 ;
	public static final String C_SPACE = " " ; //$NON-NLS-1$
	//
	/**
	 *	空白文字列返却メソッド<br>
	 *	指定された数分のスペース文字を連結して出力する。
	 *	@param		spaceNum	文字数
	 *	@return		空白連結文字列
	 */
	public static String makeSpace(int spaceNum)	{
		//## 返り値初期化 ##
		StringBuilder strRet = new StringBuilder() ;
		int rCnt;
		//## 文字列作成 ##
		for(rCnt=0; rCnt<spaceNum;rCnt++){
			strRet.append(C_SPACE);
		}
		//## 返り値リターン ##
		return strRet.toString()  ;
	}
	//
	/**
	 *	指定文字列返却メソッド
	 *	@param		strElement	作成単位文字列
	 *	@param		strNum		文字数
	 *	@return		連結文字列
	 */
	public static String makeString(String strElement,int strNum){
		//## 返り値初期化 ##
		StringBuilder strRet = new StringBuilder() ;
		int rCnt;
		//## 文字列作成 ##
		for(rCnt=0; rCnt<strNum;rCnt++){
			strRet.append(strElement) ;
		}
		//## 返り値リターン ##
		return strRet.toString()  ;
	}
	
	public static String replaceString(String inDataBuff, String targetChr, Object replace){
		//## ローカル宣言 ##
		StringBuilder strRet = new StringBuilder();
		String inStr = new String(inDataBuff);
		int lngFindNum ;
		int lngStartCnt ;
		lngStartCnt = 0 ;
		//## 置き換え処理 ##
		while(true){
			//== 対象部分文字列検索 ==
			lngFindNum = inStr.indexOf(targetChr) ;
			//発見なしならブレイク
			if (lngFindNum == -1){
				strRet.append(inStr);
				break ;
			//発見した場合は置き換え
			}else{
				strRet.append(inStr.substring(0, lngFindNum ));
				strRet.append(replace);
				lngStartCnt = lngFindNum + targetChr.length();
				inStr=inStr.substring(lngStartCnt);
			}
		}
		return strRet.toString();
	}
	//
	/**
	 *	指定文字列置き換えメソッド<br>
	 *	変換元の文字列（xxxppqq)上の部分文字列（pp）を<br>
	 *	任意の文字列（test）に置き換える場合変換後文字列（xxxtestqq）を出力する。
	 *	@param		inDataBuff	変換元入力文字列
	 *	@param		targetChr	置き換え対象文字列
	 *	@param		replaceChr	置き換え文字列
	 *	@return		置き換え
	 */
	public static String replaceString(String inDataBuff, String targetChr, String replaceChr){
	    return replaceString(inDataBuff, targetChr, (Object)replaceChr);
	}
	
	public static String replaceString(String inDataBuff, String targetStr, Object[] replaces){
		/** 入力文字配列がnullならリターン */
		if(replaces==null){
			return new String(inDataBuff) ;
		}
		String retStr = new String(inDataBuff) ;
		/** 配列内文字の置き換えをする。*/
		for(int rCnt = replaces.length -1;rCnt>=0;rCnt--){
			//ターゲット文字＋配列INDEXで置き換えを行う。
			String cntStr = targetStr + new Integer(rCnt).toString()  ;
			retStr = replaceString(retStr,cntStr,replaces[rCnt]);
		}
		return retStr ;
	}
	//
	/**
	 *	指定埋め込み文字配列置き換えメソッド<br>
	 *	@param		inDataBuff	変換元入力文字列
	 *	@param		targetStr	置き換え対象文字列
	 *	@param		replaceStrs	置き換え文字配列
	 *	@return		置き換え文字
	 */
	public static String replaceString(String inDataBuff, String targetStr, String[] replaceStrs){
	    return replaceString(inDataBuff, targetStr, (Object[])replaceStrs);
	}
	//
	/**
	 *	指定埋め込み文字配列置き換えメソッド<br>
	 *	@param		inDataBuff	変換元入力文字列
	 *	@param		targetStr	置き換え対象文字列
	 *	@param		replaceStrs	置き換え文字配列
	 *	@return		置き換え文字
	 */
	public static String replaceString(String inDataBuff, String targetStr, ArrayList replaceStrs){
		/** 入力文字配列がnullならリターン */
		if(replaceStrs==null){
			return inDataBuff ;
		}
		String retStr = new String(inDataBuff) ;
		/** 配列内文字の置き換えをする。*/
		for(int rCnt = replaceStrs.size() -1;
			rCnt>=0;rCnt--){
			//ターゲット文字＋配列INDEXで置き換えを行う。
			String cntStr = targetStr + new Integer(rCnt).toString()  ;
			retStr = replaceString(retStr,cntStr,(String)replaceStrs.get(rCnt));
		}
		return retStr ;
	}
	//
	/**
	 *	文字列、数値変換メソッド<br>
	 *	@param		inStr		数値文字列
	 *	@return		文字列が表す数値
	 */
	public static int convertInt(String inStr){
		//## ローカル宣言 ##
		int findPriod = inStr.indexOf(".");
		String mngBuf = inStr ;
		if(findPriod > -1){
			mngBuf = inStr.substring(0,findPriod);
		}
		Integer intRet = null;
		try{
			intRet = new Integer(mngBuf);
		}catch(NumberFormatException e){
			intRet = new Integer(0) ;
		}
		return intRet.intValue();
	}
	//
	/**
	 *	文字列、数値変換メソッド<br>
	 *	@param		inStr		数値文字列
	 *	@return		文字列が表す数値
	 */
	public static long convertLong(String inStr){
		//## ローカル宣言 ##
		int findPriod = inStr.indexOf(".");
		String mngBuf = inStr ;
		if(findPriod > -1){
			mngBuf = inStr.substring(0,findPriod);
		}
		Long lngRet = null;
		try{
			lngRet = new Long(mngBuf);
		}catch(NumberFormatException e){
			lngRet = new Long(0) ;
		}
		return lngRet.longValue();
	}
	/**
	 *	入力文字がASCII文字であるか判定する。<BR>
	 * @param inStr 入力文字
	 *	@return		チェック結果
	 */
	public static boolean isAscii (String inStr) {
		for(int cnt = 0; cnt < inStr.length(); cnt++){
			char valtmp = inStr.charAt(cnt);
			if(valtmp < ' ' || valtmp > '~'){
				return false;
			}
		}
		return true ;
	}
	//
	/**
	 *	入力文字が数字であるか判定する。<BR>
	 * @param inStr 入力文字
	 *	@return	チェック結果
	 */
	public static boolean isNumeric (String inStr) {
		for(int cnt = 0; cnt < inStr.length(); cnt++){
			char valtmp = inStr.charAt(cnt);
			if(valtmp < '0' || valtmp > '9'){
				return false;
			}
		}
		return true ;
	}
	//
	/**
	 *	小数チェックメソッド<BR>
	 *	小数の整数部、小数点以下の桁数チェックを行う。<BR>
	 * @param getData チェック文字
	 *	@return		チェック結果
	 */
	public static boolean isDecimal (String getData) {
		int checkInt = 0 ;
		int checkDec = 0 ;
		for(int cnt = 0; cnt < getData.length(); cnt++){
			char valtmp = getData.charAt(cnt);
			if(valtmp < '0' || valtmp > '9') {
				if(valtmp != '.' && valtmp != ',') {
					return false ;
				} else if(checkDec > 0) {
					return false ;
				} else if(cnt == getData.length() - 1) {
					return false ;
				} else if(valtmp == '.') {
					checkDec++ ;
				}
			} else if(checkDec > 0) {
				checkDec++ ;
			} else {
				checkInt++ ;
			}
		}
		return true ;
	}
	//
	//
	/**
	 *	全角，半角スペース削除メソッド<BR>
	 *	文字列の両側から全角スペース,半角スペースを削除する。<BR>
	 *	@param		getData		削除対象文字列
	 *	@return		削除した文字列
	 */
	public static String trimSpace (String getData) {
		StringBuilder retBuff = new StringBuilder() ;
		char [] cValtmp = getData.toCharArray();
		for(int k = 0 ; k < 2 ; k++) {
			for(int i=0 ; i < cValtmp.length ; i++) {
				if(cValtmp[i]!='　' && cValtmp[i]!=' ') {
					try {
						if(cValtmp[i]==65377 && cValtmp[i+1]==65377){
							i++ ;
							continue;
						}
					} catch(ArrayIndexOutOfBoundsException e) {
						e.printStackTrace() ;
					}
					for(int j=i ; j < cValtmp.length ; j++) {
						retBuff.append(cValtmp[j]) ;
						i=j;
					}
				}
			}
			if(k==0) {
				cValtmp = new String(retBuff.reverse()).toCharArray() ;
				retBuff.append(retBuff.delete(0,retBuff.length())) ;
			} 
		}
		String retData = retBuff.reverse().toString() ;
		return retData ;
	}
	//
	/**
	 *	全角，半角スペース,改行コード削除メソッド<BR>
	 *	文字列の両側から全角スペース,半角スペースを削除し、<BR>
	 *	文字列から全ての改行コードを削除する。<BR>
	 *	@param		getData		削除対象文字列
	 *	@param dummy オーバーロード用ダミー引数
	 *	@return		削除した文字列
	 */
	public static String trimSpace (String getData,int dummy) {
		String removeStr = removeReturn(getData) ;
		StringBuilder retBuff = new StringBuilder() ;
		char [] cValtmp = removeStr.toCharArray();
		for(int k = 0 ; k < 2 ; k++) {
			for(int i=0 ; i < cValtmp.length ; i++) {
				if(cValtmp[i]!='　' && cValtmp[i]!=' ') {
					try {
						if(cValtmp[i]==65377 && cValtmp[i+1]==65377){
							i++ ;
							continue;
						}
					} catch(ArrayIndexOutOfBoundsException e) {
						e.printStackTrace() ;
					}
					for(int j=i ; j < cValtmp.length ; j++) {
						retBuff.append(cValtmp[j]) ;
						i=j;
					}
				}
			}
			if(k==0) {
				cValtmp = new String(retBuff.reverse()).toCharArray() ;
				retBuff.append(retBuff.delete(0,retBuff.length())) ;
			} 
		}
		String retData = retBuff.reverse().toString() ;
		return retData ;
	}
	//
	/**
	 *	全角スペース,半角スペース,※,タブ削除メソッド<BR>
	 *	文字列の両側から全角スペース,半角スペース,※,タブ<BR>
	 *	を削除する。<BR>
	 *	@param		getData		削除対象文字列
	 *	@return		削除した文字列
	 */
	public static String trimNeedlessChara (String getData) {
		StringBuilder retBuff = new StringBuilder() ;
		char [] cValtmp = getData.toCharArray();
		for(int k = 0 ; k < 2 ; k++) {
			for(int i=0 ; i < cValtmp.length ; i++) {
				if(cValtmp[i]!='　' 
					&& cValtmp[i]!=' ' 
					&& cValtmp[i]!='＊' && cValtmp[i]!='\t') {
					try {
						if(cValtmp[i]==65377 && cValtmp[i+1]==65377){
							i++ ;
							continue;
						}
						if(k == 0) {
							if(cValtmp[i]==65378 && cValtmp[i+1]==65384){
								i++ ;
								continue;
							}
						} else {
							if(cValtmp[i]==65384 && cValtmp[i+1]==65378){
								i++ ;
								continue;
							}
						}
					} catch(ArrayIndexOutOfBoundsException e) {
						e.printStackTrace() ;
					}
					for(int j=i ; j < cValtmp.length ; j++) {
						retBuff.append(cValtmp[j]) ;
						i=j;
					}
				}
			}
			if(k==0) {
				cValtmp = new String(retBuff.reverse()).toCharArray() ;
				retBuff.append(retBuff.delete(0,retBuff.length())) ;
			} 
		}
		String retData = retBuff.reverse().toString() ;
		return retData ;
	}
	//
	/**
	 *	全角スペース,半角スペース,※,タブ,改行コード 削除メソッド<BR>
	 *	文字列の両側から全角スペース,半角スペース,※,タブを削除し、<BR>
	 *	文字列の中から全ての改行コードを削除する。<BR>
	 *	@param		getData		削除対象文字列
	 *	@param		dummy			オーバーロード用ダミー引数
	 *	@return		削除した文字列
	 */
	public static String trimNeedlessChara (String getData,int dummy) {
		String removeStr = removeReturn(getData) ;
		StringBuilder retBuff = new StringBuilder() ;
		char [] cValtmp = removeStr.toCharArray();
		for(int k = 0 ; k < 2 ; k++) {
			for(int i=0 ; i < cValtmp.length ; i++) {
				if(cValtmp[i]!='　' 
					&& cValtmp[i]!=' ' 
					&& cValtmp[i]!='＊' 
					&& cValtmp[i]!='\t') {
					try {
						if(cValtmp[i]==65377 && cValtmp[i+1]==65377){
							i++ ;
							continue;
						}
						if(k == 0) {
							if(cValtmp[i]==65378 && cValtmp[i+1]==65384){
								i++ ;
								continue;
							}
						} else {
							if(cValtmp[i]==65384 && cValtmp[i+1]==65378){
								i++ ;
								continue;
							}
						}
					} catch(ArrayIndexOutOfBoundsException e) {
						e.printStackTrace() ;
					}
					for(int j=i ; j < cValtmp.length ; j++) {
						retBuff.append(cValtmp[j]) ;
						i=j;
					}
				}
			}
			if(k==0) {
				cValtmp = new String(retBuff.reverse()).toCharArray() ;
				retBuff.append(retBuff.delete(0,retBuff.length())) ;
			} 
		}
		String retData = retBuff.reverse().toString() ;
		return retData ;
	}
	//
		//
	/**
	 *	全角スペース,半角スペース,タブ,改行コード 削除メソッド<BR>
	 *	文字列の両側から全角スペース,半角スペース,タブを削除し、<BR>
	 *	文字列の中から全ての改行コードを削除する。<BR>
	 *	@param		getData		削除対象文字列
	 *	@return		削除した文字列
	 */
	public static String trimNeedlessChara2 (String getData) {
		String removeStr = removeReturn(getData) ;
		StringBuilder retBuff = new StringBuilder() ;
		char [] cValtmp = removeStr.toCharArray();
		for(int k = 0 ; k < 2 ; k++) {
			for(int i=0 ; i < cValtmp.length ; i++) {
				if(cValtmp[i]!='　' 
					&& cValtmp[i]!=' ' 
					&& cValtmp[i]!='\t') {
					try {
						if(cValtmp[i]==65377 && cValtmp[i+1]==65377){
							i++ ;
							continue;
						}
						if(k == 0) {
							if(cValtmp[i]==65378 && cValtmp[i+1]==65384){
								i++ ;
								continue;
							}
						} else {
							if(cValtmp[i]==65384 && cValtmp[i+1]==65378){
								i++ ;
								continue;
							}
						}
					} catch(ArrayIndexOutOfBoundsException e) {
						e.printStackTrace() ;
					}
					for(int j=i ; j < cValtmp.length ; j++) {
						retBuff.append(cValtmp[j]) ;
						i=j;
					}
				}
			}
			if(k==0) {
				cValtmp = new String(retBuff.reverse()).toCharArray() ;
				retBuff.append(retBuff.delete(0,retBuff.length())) ;
			} 
		}
		String retData = retBuff.reverse().toString() ;
		return retData ;
	}
	//
	/**
	 *	全角スペース,半角スペース,※,タブ,改行コード 削除メソッド<BR>
	 *	全角スペース,半角スペース,※,タブ,改行コード<BR>
	 *	を文字列の両側から削除する。<BR>
	 *	@param		getData		削除対象文字列
	 *	@return		削除した文字列
	 */
	public static String trimNeedless (String getData) {
		StringBuilder retBuff = new StringBuilder() ;
		char [] cValtmp = getData.toCharArray();
		for(int k = 0 ; k < 2 ; k++) {
			for(int i=0 ; i < cValtmp.length ; i++) {
				if(cValtmp[i]!='　' 
				&& cValtmp[i]!=' ' 
				&& cValtmp[i]!='＊' 
				&& cValtmp[i]!='\t') {
					try {
						if(cValtmp[i]==65377 && cValtmp[i+1]==65377){
							i++ ;
							continue;
						}
						if(k == 0) {
							if(cValtmp[i]==65378 && cValtmp[i+1]==65384){
								i++ ;
								continue;
							}
						} else {
							if(cValtmp[i]==65384 && cValtmp[i+1]==65378){
								i++ ;
								continue;
							}
						}
					} catch(ArrayIndexOutOfBoundsException e) {
						e.printStackTrace() ;
					}
					if(cValtmp[i]!='\r' && cValtmp[i]!='\n' ){
						for(int j=i ; j < cValtmp.length ; j++) {
							retBuff.append(cValtmp[j]) ;
							i=j;
						}
					}else{
					//	i++ ;
					}
				}
			}
			if(k==0) {
				cValtmp = new String(retBuff.reverse()).toCharArray() ;
				retBuff.append(retBuff.delete(0,retBuff.length())) ;
			} 
		}
		String retData = retBuff.reverse().toString() ;
		return retData ;
	}
	//
	/**
	 *	全角スペース,半角スペース,※,タブ,改行コード 削除メソッド<BR>
	 *	文字列から全ての全角スペース,半角スペース,※,タブ,改行コード<BR>
	 *	を削除する。<BR>
	 *	@param		getData		削除対象文字列
	 *	@return		削除した文字列
	 */
	public static String removeNeedlessChara (String getData) {
		String removeStr = removeReturn(getData) ;
		StringBuilder retBuff = new StringBuilder() ;
		char [] cValtmp = removeStr.toCharArray();
		for(int i=0 ; i < cValtmp.length ; i++) {
			if(cValtmp[i]!='　' 
			&& cValtmp[i]!=' ' 
			&& cValtmp[i]!='＊' 
			&& cValtmp[i]!='\t') {
				try {
					if(cValtmp[i]==65377 && cValtmp[i+1]==65377){
						i++ ;
						continue;
					}
					if(cValtmp[i]==65378 && cValtmp[i+1]==65384){
						i++ ;
						continue;
					}
				} catch(ArrayIndexOutOfBoundsException e) {
					e.printStackTrace() ;
				}
				retBuff.append(cValtmp[i]) ;
			}
		}
//		if(k==0) {
//			cValtmp = new String(retBuff.reverse()).toCharArray() ;
//			retBuff.append(retBuff.delete(0,retBuff.length())) ;
//		} 
//		String retData = retBuff.reverse().toString() ;
		String retData = retBuff.toString() ;
		return retData ;
	}
	//2001/11/21 Add K.Nakamura ※を削除しないメソッドを追加
	/**
	 *	全角スペース,半角スペース,タブ,改行コード 削除メソッド<BR>
	 *	文字列から全ての全角スペース,半角スペース,タブ,改行コード<BR>
	 *	を削除する。<BR>
	 *	@param getData 削除対象文字列
	 *	@param dummy オーバーロード用ダミー引数
	 *	@return 削除した文字列
	 */
	public static String removeNeedlessChara (String getData,int dummy) {
		String removeStr = removeReturn(getData) ;
		StringBuilder retBuff = new StringBuilder() ;
		char [] cValtmp = removeStr.toCharArray();
		for(int i=0 ; i < cValtmp.length ; i++) {
			if(cValtmp[i]!='　' 
			&& cValtmp[i]!=' ' 
			&& cValtmp[i]!='\t') {
				try {
					if(cValtmp[i]==65377 && cValtmp[i+1]==65377){
						i++ ;
						continue;
					}
					if(cValtmp[i]==65378 && cValtmp[i+1]==65384){
						i++ ;
						continue;
					}
				} catch(ArrayIndexOutOfBoundsException e) {
					e.printStackTrace() ;
				}
				retBuff.append(cValtmp[i]) ;
			}
		}
		String retData = retBuff.toString() ;
		return retData ;
	}
	//
	/**
	 *	改行コード削除メソッド<BR>
	 *	文字列から改行コードを削除する。<BR>
	 *	@param getData 削除対象文字列
	 *	@return 削除した文字列
	 */
	public static String removeReturn (String getData) {
		String retStr = new String(getData);
		if(retStr.indexOf("\r\n") != -1){
			retStr = StringOperator.replaceString(retStr,"\r\n","");
		}
		if(retStr.indexOf("\n") != -1){
			retStr = StringOperator.replaceString(retStr,"\n","");
		}
		if(retStr.indexOf("\r") != -1){
			retStr = StringOperator.replaceString(retStr,"\r","");
		}
		return retStr ;
	}
	//
	//2002/02/15 Add K.Nakamura
	/** 文字列を指定された長さ毎に挿入していく。
	 *	@param	argStr		対象文字列
	 *	@param	argLen		区切り長
	 *	@param	argLinefeed	挿入文字列
	 *	@param	argUnfeed	区切り対象外文字列の羅列文字 例: "、。）)"　など
	 *	@return 結果文字列
	 */
	public static String setLinefeed(String argStr, int argLen, String argLinefeed, String argUnfeed){
		// 引数チェック
		if (argStr == null || argStr.equals("")){
			return argStr;
		}
		if (argLen <= 0){
			return argStr;
		}
		if (argLinefeed == null || argLinefeed.equals("")){
			return argStr;
		}
		if (argStr.length() <= argLen){
			return argStr;
		}
		// 返却文字列
		StringBuilder retStr = new StringBuilder();
		// 作業用文字列
		String targetStr = argStr;
		int begine = 0;
		int end = argLen;
		while(true){
			// 指定長だけ抽出する。
			retStr.append(targetStr.substring(begine,end));
			// 次のポイントに進める
			begine = end;
			end = end + argLen;
			if (argStr.length() <= begine){
				// 開始点がENDまできたので終了する
				break;
			}else{
				// 句読点などはその行に含める
				if (argUnfeed != null && !argUnfeed.equals("")){
					if(argUnfeed.indexOf(targetStr.substring(begine,begine + 1)) > -1 ){
						// 区切り対象外なのでその行に追加
						retStr.append(targetStr.substring(begine,begine + 1));
						begine ++;
						end ++;
						if (argStr.length() <= begine){
							// 開始点がENDまできたので終了する
							break;
						}
					}
				}
				// 指定文字を挿入する
				retStr.append(argLinefeed);
			}
			// 終了点が対象文字列長を超えた場合は対象文字列のENDにする。
			if (argStr.length() < end){
				end = argStr.length();
			}
		}
		return retStr.toString();
	}
	//
}
