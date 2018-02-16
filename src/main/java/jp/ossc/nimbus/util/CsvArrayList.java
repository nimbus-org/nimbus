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
import java.util.ListIterator;
import java.io.*;
/**
*	CSV文字列交換オブジェクト
*	@author		Hirotaka.Nakano
*	@version	1.00 作成：2001.04.04 － H.Nakano<BR>
*				更新：
*/
public class CsvArrayList extends ArrayList implements java.io.Serializable{
	
    private static final long serialVersionUID = -2858942004554521568L;
    
    //## クラスメンバー変数宣言 ##
	/** CSV区切り文字			*/		private String mSeptData ;
	/** エスケープ文字定数		*/		private String mEscapeString ;
	/** 同期用オブジェクト		*/		private Object mObjSync ;
	/** 最終デミリター付加区分	*/		private boolean mAddDemiliter ;
	static public final String ESP_STR = "\u001C"; //$NON-NLS-1$
	static protected final String C_LINESEPT = "line.separator" ; //$NON-NLS-1$
	static protected final String C_COMMMA = "," ; //$NON-NLS-1$
	static protected final String C_NONE ="" ; //$NON-NLS-1$
	static protected final String C_ESCAPE ="\\" ;//$NON-NLS-1$
	protected static final String CR = "\r";
	protected static final String LF = "\n";
	protected static final String CRLF = "\r\n";
	//
	//
	/**
	 *	コンストラクタ<br>
	 *	上位クラスのコンストラクターコール
	 */
	public CsvArrayList() {
		super() ;
		mSeptData = C_COMMMA ;
		mEscapeString = C_ESCAPE ;
		mAddDemiliter = false;
		mObjSync = new String();
	}
	//
	/**
	 *	最終区切り文字付加フラグセッター
	 *	@param		flgAddDelimita	最終区切り文字付加フラグ
	 */
	public void setAddDelimitaFlg(boolean flgAddDelimita )	 {
		synchronized(mObjSync){
			mAddDemiliter = flgAddDelimita ;
		}
	}
	//
	/**
	 *	ECP文字セッター<BR>
	 *	セパレータ文字をエスケープする文字を設定する<br>
	 *	設定なしの場合は\文字になっている。
	 *	@param		strEscape	エスケープ文字列
	 */
	public void setEscapeString(String strEscape )	 {
		synchronized(mObjSync){
			// エスケープ文字設定
			mEscapeString = strEscape ;
		}
	}
	//
	/**
	 *	分割プライベートメソッド<br>
	 *	シンクロは上位publicメソッドでかける事
	 *	@param		strInData	分割対象文字列
	 *	@return		分割項目数
	 */
	private int _splitString(String strInData )	 {
		int lngFindNum ;
		int lngMindNum ;
		StringBuilder subStr1 ;
		StringBuilder subStr2 ;
		// 入力舗?炎
		//== 出力配列初期化 ==
		super.clear() ;
		if (strInData != null && strInData.length()!=0){
			//## 区切り文字検索 ##
			lngMindNum = 0 ;
			subStr1 = new StringBuilder(C_NONE) ;
			subStr2 = new StringBuilder(StringOperator.replaceString(strInData,mEscapeString + mEscapeString,ESP_STR)) ;
			//== 最終デミリター文字削除 ==
			if(mAddDemiliter){
				if(subStr2.substring(subStr2.length()-mSeptData.length()).equals(mSeptData)){
					subStr2 = new StringBuilder(subStr2.substring(0,subStr2.length()-mSeptData.length()));
				}
			}
			while(true){
				//== 文字列検索 ==
				lngFindNum = subStr2.toString().indexOf(mSeptData) ;
				lngMindNum += lngFindNum ;
				//== 検索文字が見つからない場合 ==
				if (lngFindNum == -1) {
					if(subStr1.length()>0){ 
						super.add(subStr1.toString()) ;
					}else{
						super.add(subStr2.toString()) ;
					}
					break;
				//== 検索文字が見つかった場合 ==
				}else {
					// 2文字目以降に発見した場合
					if(lngFindNum >= 0){
						//## ESC文字対応 ##
						if(lngFindNum > 0 && subStr2.substring(lngFindNum-1,lngFindNum).equals(mEscapeString)){
							subStr1 = new StringBuilder(subStr1.append(subStr2.substring(0,lngFindNum-1).toString()).toString());
							subStr1.append(mSeptData);
							final String postStr = subStr2.substring(lngFindNum+mSeptData.length());
							if(postStr.indexOf(mSeptData) != -1){
								subStr2 = new StringBuilder(postStr);
							}else{
								subStr1.append(postStr);
								subStr2 = new StringBuilder(C_NONE);
							}
							continue;
						} else{
							subStr1.append(subStr2.substring(0,lngFindNum)) ;
						}

					// 1文字目に発見した場合
					} else {
						subStr1 = new StringBuilder(C_NONE);
					}
					// 次の検索位置までシークする。
					String tmp = StringOperator.replaceString(subStr1.toString(),ESP_STR, mEscapeString) ;
					super.add(tmp) ;
					lngMindNum= 0 ;
					subStr2 = new StringBuilder(subStr2.substring(lngFindNum+mSeptData.length()));
					subStr1 = new StringBuilder(C_NONE) ;
				}
			}
		}
		//## 分割件数を返す ##
		return super.size();
	}
	//
	/**
	 *	分割メソッド<br>
	 *	";"文字のセパレートを行う。
	 *	@param		strInData	分割対象文字列
	 *	@return		分割項目数
	 */
	public int split(String strInData) {
		int ret = 0;
		synchronized(mObjSync){
			// デフォルト切り文字で分割 /
			mSeptData = C_COMMMA ;
			ret = this._splitString(strInData) ;
		}
		return ret ;
	}
	//
	/**
	 *	分割メソッド<br>
	 *	改行文字のセパレートを行う。
	 *	@param		strInData	分割対象文字列
	 *	@return		分割項目数
	 */
	public int splitCL(String strInData) {
		int ret = 0;
		synchronized(mObjSync){
			// 筑湊拂区切り文字で分割 /
			if(strInData.indexOf(CRLF) != -1){
			    mSeptData = CRLF;
			}else if(strInData.indexOf(LF) != -1){
			    mSeptData = LF;
			}else if(strInData.indexOf(CR) != -1){
			    mSeptData = CR;
			}else{
			    clear();
			    add(strInData);
			    return 1;
			}
			ret = this._splitString(strInData) ;
		}
		return ret ;
	}
	//
	/**
	 *	分割メソッド（指定区切り）
	 *	@param		strInData	分割対象文字列
	 *	@param		strSept		セパレータ文字列
	 *	@return		分割項目数
	 */
	public int split(String strInData,String strSept) {
		int ret = 0 ;
		synchronized(mObjSync){
			mSeptData = strSept ;
			ret = _splitString(strInData) ;
		}
		return ret ;
	}
	//
	/**
	 *	分割メソッド（エクセルCSV形式）<br>
	 *	エクセルCSV形式の分割を行う
	 *	@param		strInData	エクセルCSV形式の文字列
	 *	@return		分割項目数
	 */
	public int splitExcelFile(String strInData) throws IOException {
		clear();
		synchronized(mObjSync){
			int index = 0;
			while((index = getData(strInData, index)) != -1)
				;
		}
		return size() ;
	}
	//
	/**
	 *	文字列取得メソッド<br>
	 *	エクセルCSV形式の文字列からセパレータで区切られた文字列を取得する
	 *	@param		strInData	エクセルCSV形式の文字列
	 *	@param		index			現在のindex
	 *	@return		現在のindex
	 */
	protected int getData(String strInData, int index) {
		if (index > strInData.length()) {
			return -1;
		}

		if (index == strInData.length()) {
			add(C_NONE);
			return -1;
		}
		
		char c = strInData.charAt(index);
		if (c == ',') {
			add(C_NONE);
			return index + 1;
		}
		
		if (c == '"') {
			return _getQuotedData(strInData, index + 1);
		}
		
		int begin = index;
		index = strInData.indexOf(',', index);
		if (index == -1) {
			add(strInData.substring(begin));
			return -1;
		}
		
		add(strInData.substring(begin, index));
		return index + 1;
	}
	//
	/**
	 *	エスケープ文字列の取得メソッド<br>
	 *	エスケープ文字が使用された文字列を取得する
	 *	@return	int			現在のindex
	 *	@param		strInData	エクセルCSV形式の文字列
	 *	@param		int			現在のindex
	 */
	private int _getQuotedData(String strInData, int index) {
		StringBuilder buf = new StringBuilder();
		while (true) {
			int begin = index;
			index = strInData.indexOf('\"', index);
			if (index == -1) {
				buf.append(strInData.substring(begin));
				add(buf.toString());
				return -1;
			}

			if (index == strInData.length() - 1) {
				buf.append(strInData.substring(begin, index));
				add(buf.toString());
				return -1;
			}			
			int c = strInData.charAt(index + 1);
			if (c == '\"') {
				buf.append(strInData.substring(begin, index + 1));
				index += 2;
			} else if (c == ',') {
				buf.append(strInData.substring(begin, index));
				add(buf.toString());
				return index + 2;
			} else {
				buf.append(strInData.substring(begin, index));
				index += 2;
			}
		}
	}

	//
	/**
	 *	合成メソッド（プライベート）<br>
	 *	シンクロは上位publicメソッドでかける事
	 *	@return		String		合成文字列
	 */
	private String _joinString() {
		StringBuilder mngBuf = new StringBuilder() ;
		for( ListIterator iterator = super.listIterator(); iterator.hasNext();) {
			String tmpBuf = (String)iterator.next() ;
			tmpBuf = StringOperator.replaceString(tmpBuf,mEscapeString,mEscapeString+mEscapeString );
			tmpBuf = StringOperator.replaceString(tmpBuf,mSeptData,mEscapeString + mSeptData);
			mngBuf.append(tmpBuf).append(mSeptData) ;
		}
		//最終デミリター削除
		if(mAddDemiliter==false){
			if( mngBuf.length() > mSeptData.length()){
				mngBuf = new StringBuilder(mngBuf.toString().substring(0,mngBuf.length() - mSeptData.length())) ;
			}
		}
		return mngBuf.toString() ;
	}
	//
	/**
	 *	合成メソッド<BR>
	 *	";"をデミリター文字にして合成文字列を作成する。
	 *	@return		String		合成文字列
	 */
	public String join() {
		synchronized(mObjSync){
			/** 筑湊拂区切り文字で分割 **/
			mSeptData = C_COMMMA ;
			return _joinString() ;
		}
	}
	//
	/**
	 *	合成メソッド<BR>
	 *	改行コードをデミリター文字にして合成文字列を作成する。
	 *	@return		String		合成文字列
	 */
	public String joinCL() {
		/** 筑湊拂区切り文字で分割 **/
		mSeptData = System.getProperty(C_LINESEPT) ;
		return _joinString() ;
	}
	//
	/**
	 *	合成メソッド（指定区切り）
	 *	指定引数をデミリター文字にして合成文字列を作成する。
	 *	@return		String		合成文字列
	 *	@param		strSept		デミリター文字列
	 */
	public String join(String strSept) {
		mSeptData = strSept ;
		return _joinString() ;
	}
	//
	/**
	 *	文字配列作成メソッド
	 *	@return		String[]		合成文字列
	 */
	public String[] toStringAry() {
		String result[] = null;
		Object aryObj[] = null;
		aryObj = super.toArray() ;
		if(aryObj != null){
			result = new String[aryObj.length];
			for(int rCnt=0 ;rCnt<aryObj.length;rCnt++){
				result[rCnt] = (String)aryObj[rCnt];
			}
		}
		return result ;
	}
	//
	/**
	 *	文字配列設定メソッド
	 *	@param		inStrAry		入力文字配列
	 */
	public void setStringAry(String inStrAry[]) {
		if(inStrAry != null){
			for (int i = 0 ; i < inStrAry.length ; i ++ ) {
				this.add(inStrAry[i]) ;
			}
		}
	}
	//
	/**
	 *	文字列取得メソッド
	 *	@param		index		配列番号
	 *	@return		INDX指定文字列
	 */
	public String getStr(int index) {
		String result ;
		result = (String)super.get(index);
		return result ;
	}
	//
}
