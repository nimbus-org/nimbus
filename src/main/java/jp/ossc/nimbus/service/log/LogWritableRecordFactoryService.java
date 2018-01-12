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
package jp.ossc.nimbus.service.log;

import jp.ossc.nimbus.service.writer.DateElement;
import jp.ossc.nimbus.service.writer.WritableElement;
import jp.ossc.nimbus.service.writer.WritableRecordFactoryService;

/**
 *	LogWritableRecordファクトリ
 *  postCreateElementメソッドをオーバーライドし、
 *  日付型のWritableElementに対して、フォーマット
 *  文字列を設定するようにする。日付型要素のsetFormat(String)
 *  メソッドを叩くので、日付型の要素として、DateElement
 *  以外のクラスを使う場合には、setFormat(String)メソッドを実装
 *  すること。
 * 
 *	@author	y-tokuda
 *	@version	1.00 作成：2003/11/02－ y-tokuda<BR>
 *				更新：
 */
public class LogWritableRecordFactoryService extends WritableRecordFactoryService {
	
    private static final long serialVersionUID = -4184045351990790021L;
    
    //メンバ変数
	/** 日付フォーマット文字列 */
	private String mDateFormat;
	/** postCreateElementメソッドの処理対象であるか（日付型かどうか）識別するキー */
	static final String DATE_KEY = LogServiceMBean.FORMAT_DATE_KEY;
	/**
	 * 日付フォーマットのセッター
	 *	
	 */
	public void setDateFormat(String fmt){
		mDateFormat = fmt;
	}
	/**
	 * フォーマットのセッター <BR>
	 * キーワードは%で囲む。<BR>
	 * キーワード<BR>
	 * 日付:D <BR>
	 * メッセージコード:CODE <BR>
	 * 優先度(DEBUG,INFO等):PRIO <BR>
	 * メッセージ:MSG <BR>
	 * アプリケーション指定通番:SEQ <BR>
	 * カテゴリ:CAT <BR>
	 * 例 <BR>
	 * "%D%,%CODE%,%PRIO%,%MSG%,%SEQ%,%CAT%"
	 */
	public void setFormat(String fmt){
		super.setFormat(fmt);
	}
	
	public void startService() throws Exception {
		super.startService();
		if(getImplementClass(DATE_KEY) == null){
			setImplementClass(
				DATE_KEY,
				jp.ossc.nimbus.service.writer.DateElement.class.getName()
			);
		}
	}
	
	/**
	 *  createElementの直後にコールされるメソッド
	 *  WritableRecordFactoryの空実装をオーバーライド
	 *  日付型が生成された場合、日付型のsetFormat(String)メソッドを実行する。
	 */
	protected void postCreateElement(WritableElement elem){
		if(mDateFormat != null && elem != null && DATE_KEY.equals(elem.getKey())){
			//DateElement型にキャストして、setFormatする。
			((DateElement)elem).setFormat(mDateFormat);
		}
	}
}
