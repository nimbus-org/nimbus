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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
*	CSV形式Excelファイルオブジェクト
*	@author		s3-ito
*	@version	1.00 作成：2004.01.15 － s3-ito<BR>
*				更新：
*/
public class CsvExcelArrayList extends CsvArrayList {
    
    private static final long serialVersionUID = -6160765413905533998L;
    
	/** 同期用オブジェクト		*/
    private Object mObjSync ;
	
	/**
	 *	コンストラクタ<br>
	 */
	public CsvExcelArrayList() {
		super();
		mObjSync = new Object();
		
	}
	/**
	 *	コンストラクタ<br>
	 *	@param			file	CSV形式Excelファイル
	 */
	public CsvExcelArrayList(FileReader file) throws IOException {
		super();
		mObjSync = new Object();
		readExcelFile(file);
	}
	/**
	 *	Excelファイル読み込みメソッド<br>
	 *	Excelファイルを読み込みCsvArrayListクラスObjectをリストに設定する
	 *	@param			file	CSV形式Excelファイル
	 */
	public void readExcelFile(FileReader file) throws IOException {
		synchronized(mObjSync) {
			BufferedReader buffer = new BufferedReader(file);
			String line = null;
			for(;;) {
				// 行Object生成
				CsvArrayList cols = new CsvArrayList();
				line = buffer.readLine();
				if(line != null) {
					int index = 0;
					while((index = cols.getData(line, index)) != -1)
						;
				}
				else {
					break;
				}
				// リストに行Objectを追加
				this.add(cols);
			}
			buffer.close();
		}
	}
	/**
	 *	文字列取得<br>
	 *	指定位置の文字列を取得する
	 *	@return		String	指定位置の文字列
	 *	@param			line	行
	 *	@param			col		列
	 */
	public String getStr(int line, int col) {
		synchronized(mObjSync) {
			CsvArrayList array = (CsvArrayList)get(line);
			return (String)array.get(col);
		}
	}
	/**
	 *	文字列数取得<br>
	 *	指定行の文字列数を取得する
	 *	@return		int		文字列数
	 *	@param			line	行
	 */
	public int size(int line) {
		synchronized(mObjSync) {
			CsvArrayList array = (CsvArrayList)get(line);		
			return array.size();
		}
	}
}
