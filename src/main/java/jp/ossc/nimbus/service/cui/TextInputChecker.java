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
package jp.ossc.nimbus.service.cui;

import jp.ossc.nimbus.util.*;

/**
 *	InputCheckerインターフェイス実装クラス
 *  XMLファイルに即値で入力有効値がかかれている場合
 * （inputタグのtype属性が、"text"のとき生成される。
 *	@author	y-tokuda
 *	@version	1.00 作成：2003/10/31－ y-tokuda<BR>
 *				更新：
 */
public class TextInputChecker implements InputChecker {
	//メンバ変数
	/** 入力可能値 */
	private CsvArrayList mValidValues = null;
	/** 入力可能範囲最大 */
	private int mValidValueMax;
	/** 入力可能範囲最小 */
	private int mValidValueMin;
	/** 範囲指定モード */
	private boolean mMaxMinDefMode = false;
	/** 範囲指定を行う際、最大値と最小値の間に入れる文字列 */
	private String mFromToStr = "-";
	/**
	 * コンストラクタ
	 */
	public TextInputChecker(){
		mValidValues = new CsvArrayList();
	}
	/**
	 * 入力値チェックメソッド
	 */
	public String check(String input) {
		if(mMaxMinDefMode){
			//最大値・最小値が指定されているモード
			int tmp;
			try{
				tmp = Integer.parseInt(input);
			}
			catch(NumberFormatException e){
				return null;
			}
			if( (mValidValueMin <= tmp) && ( tmp <= mValidValueMax) ){
				return input;
			}
			return null;
		}
		else{
			//有効な値が個々に与えられているモード
			if (mValidValues.contains(input)){
				//for debug
				return input;
			}
			return null;
		}
	}
	/**
	 * 有効な入力値のセッター
	 * @param inputdef （XML定義ファイル中の<input>タグの中身）
	 */
	public void setValidInput(String inputdef) throws NumberFormatException{
		mMaxMinDefMode = isMaxMinTypeDefinition(inputdef);
		if(!mMaxMinDefMode){
			//カンマで区切って入力可能値のリストに格納
			mValidValues.split(inputdef);
		}
		
	}
	/**
	 * 有効な入力値を定義する文字列が、最大値、最小値を指定している
	 * タイプかどうか、判定する。最大値、最小値を指定しているタイプで
	 * あれば、メンバ変数に最大値、最小値を格納する。
	 * 
	 */
	protected boolean isMaxMinTypeDefinition(String def) throws NumberFormatException{
		//TODO実装する
		int separatePos = -1;
		if( (separatePos = def.indexOf(mFromToStr)) < 0 ){
			//最大値、最小値を区切る文字が見つからなければfalseを返す
			return false;
		}
		String minStr = def.substring(0,separatePos);
		String maxStr =def.substring(separatePos+mFromToStr.length());
		mValidValueMin = Integer.parseInt(minStr);
		mValidValueMax = Integer.parseInt(maxStr);	
		return true;
	}
	/**
	 * 最大値・最小値の間に入る文字列のセッター
	 * @param fromto
	 */
	public void setFromToString(String fromto){
		mFromToStr = fromto;
	}
	

}
