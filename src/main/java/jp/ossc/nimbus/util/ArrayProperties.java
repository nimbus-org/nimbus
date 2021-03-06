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

import java.util.Properties;
//
/**
 *	標準のプロパティオブジェクトの文字化けの問題を回避した<BR>
 *	日本語可能のプロパティオブジェクトです。<BR>
 *	またprop0...propnのキーの場合配列として出力できます。<BR>
 *	@author		Hirotaka.Nakano
 *	@version	1.00 作成：2001.06.21 － H.Nakano<BR>
 *				更新：
 */
public class ArrayProperties extends EncodedProperties {
	
    private static final long serialVersionUID = -5640322744765219842L;
    
    //
	//
	/**
	 *	コンストラクタ。<BR>
	 */
	public ArrayProperties() {
		super();
	}
	//
	/**
	 *	コンストラクタ。<BR>
	 *	@param	prop 指定されたデフォルト値を持つ空のプロパティリストを作成します。
	 */
	public ArrayProperties(EncodedProperties prop) {
		super((EncodedProperties)prop);
	}
	//
	/**
	 *	コンストラクタ。<BR>
	 *	@param	prop 指定されたデフォルト値を持つ空のプロパティリストを作成します。
	 */
	public ArrayProperties(Properties prop) {
		super(prop);
	}
	/**
	 *	コンストラクタ。<BR>
	 *	@param encodeName ファイル読み込みエンコード
	 */
	public ArrayProperties(String encodeName) {
		super(encodeName);
	}
	//
	/**
	 *	キー指定配列プロパティ取得<BR>
	 *	キーに添え数字があれば添え字なしのキーで配列を返す。
	 *	@param	key			キー
	 *	@return	配列オブジェクト
	 */
	public CsvArrayList getAryProperty(String key) {
		CsvArrayList retObj = new CsvArrayList();
		String mngBuf = null;
		int	rcnt = 0;
		while(true){
			String keyval = key + rcnt ;
			mngBuf = this.getProperty(keyval);
			if(mngBuf==null){
				break ;
			}
			retObj.add(mngBuf);
			rcnt++;
		}
		return retObj ;
	}
}
