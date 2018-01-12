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

import java.util.List;

import jp.ossc.nimbus.service.message.MessageRecordImpl;
import jp.ossc.nimbus.service.message.MessageRecordOperator;
import jp.ossc.nimbus.service.message.MessageRecordParseException;
import jp.ossc.nimbus.util.*;

//
//
/**
 *	各種ログの出力インターフェイスを規定する。<BR>
 *	パフォーマンス維持のためのログのキュー管理を行う。<BR>
 *	@author		Hirotaka.Nakano
 *	@version	1.00 作成：2001.06.21 － H.Nakano<BR>
 *				更新：
 */
public class LogMessageRecordImpl extends MessageRecordImpl
								implements LogMessageRecord, MessageRecordOperator, java.io.Serializable{
	
    private static final long serialVersionUID = 6861222398118645636L;
    
    //## メンバー変数宣言 	##
	/**	ログプライオリティ			*/	
	protected int mMessagePriority = 0 ;
	/**	カテゴリー					*/	
	protected CsvArrayList mCategory = null ;
	protected boolean isPrintStackTrace = true;

	//## 定数定義 	##
	/**	デフォルトカテゴリー			*/	
	private static final String C_DFAUTL_CATEGORY = "debug" ;
	/**	デフォルトカテゴリー			*/	
	private static final String C_DFAUTL_LOCALE = "default" ;
	//
	/**
	 *	ログレコード定義文字から内部メンバーへデータをロードする。<br>
	 *	@param		defString	定義文字列<BR>
	 *	LOGCODE,LOGSTR,PRIORITY,CATEGORY0:CATEGORY1...n
	 *	APL0001,エラー発生(@1),100,root
	 */
	public void rec2Obj(String defString) throws MessageRecordParseException{
		/** イニシャル済みか判定 */
		if(!mIsInitialized){
			/** デファイン文字分割 */
			CsvArrayList parser = new CsvArrayList();
			parser.split(defString,",");
			if(parser.size()<2){
				throw new MessageRecordParseException("Message define error message is " + defString ) ;
			}else{
				// 基本データ格納
				this.mMessageCode = parser.getStr(0);
				this.mMessageHash.put(C_DFAUTL_LOCALE,parser.getStr(1)) ;
			}
			if(parser.size()>2){
				this.mMessagePriority = StringOperator.convertInt((String)parser.getStr(2)) ;
			}
            if(mCategory == null){
			    this.mCategory = new CsvArrayList() ;
            }
			if(parser.size()>3){
				this.mCategory.split(parser.getStr(3),":") ;
			}else{
				this.mCategory.add(C_DFAUTL_CATEGORY) ;
			}
			if(parser.size()>4){
				isPrintStackTrace = Boolean.valueOf(parser.getStr(4)).booleanValue();
			}
		}
	}
	public boolean isPrintStackTrace(){
	    return isPrintStackTrace;
	}
	/**
	 *	プライオリティを出力する。<br>
	 *	@return		int		プライオリティー<BR>
	 */
	public int getPriority() {
		return this.mMessagePriority ;
	}
	public void setPriority(int priority) {
		this.mMessagePriority = priority;
	}
	/**
	 *	カテゴリーを取得する。<br>
	 *	@return		CsvArrayList	カテゴリーコード<BR>
	 */
	public List getCategories(){
        if(mCategory == null){
            mCategory = new CsvArrayList();
        }
		return this.mCategory ;
	}
	public void setCategories(List categories){
        if(mCategory == null){
            mCategory = new CsvArrayList();
        }
		mCategory.clear();
		mCategory.addAll(categories);
	}
	public void addCategory(String category){
        if(mCategory == null){
            mCategory = new CsvArrayList();
        }
		if(!this.mCategory.contains(category)){
			this.mCategory.add(category);
		}
	}
	/**
	 *	設定詳細文字列を取得する。<br>
	 *	@return		String		ログコード<BR>
	 *	LOGCODE;LOGSTR;COUNT;OCCUR TIME;PRIORITY;CATEGORY:CATEGORY
	 */
	public String toString(){
		StringBuffer ret = new StringBuffer() ;
		
		ret.append(super.toString());
		ret.append(";") ;
		//PRIORITY
		ret.append(this.mMessagePriority).append(";");
		//CATEGORY
        if(mCategory != null){
            ret.append(this.mCategory.join("#"));
        }
		return ret.toString();
	}
}
