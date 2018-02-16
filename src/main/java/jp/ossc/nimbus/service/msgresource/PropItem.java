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
package jp.ossc.nimbus.service.msgresource;



/**
 *	プロパティ項目
 *	@author	y-tokuda
 *	@version	1.00 作成：2003/11/05－ y-tokuda<BR>
 *				更新：
 */
public class PropItem {
	
	//メンバ変数
	/** 名前 */
	private String mName = null;
	/** 型 */
	private int mType = -1;
	/** 型がObjectの場合、含まれる型 */
	private int mWrappedType = -1;
	/** 値 */
	private String mVal = null;
	/** ファイルを参照するか否か */
	private boolean mUseFileFlag = false;
	
	/**
	 * コンストラクタ
	 *	
	 */
	public PropItem(String name,int type,int wrappedType ){
		mName = name;
		mType = type;
		mWrappedType = wrappedType;
		mVal = null;
		mUseFileFlag = false;
	}
	/**
	 * コンストラクタ
	 *	
	 */
	public PropItem(String name,int type,int wrappedType,String val,boolean useFileFlag){
		mName = name;
		mType = type;
		mWrappedType = wrappedType;
		mVal = val;
		mUseFileFlag = useFileFlag;
	}
	/**
	 * Nameのゲッター
	 *	
	 */
	public String getName(){
		return mName;
	}
	/**
	 * Typeのゲッター
	 *	
	 */
	public int getType(){
		return mType;
	}
	/**
	 * WrappedTypeのゲッター
	 * @return
	 */
	public int getWrappedType(){
		return mWrappedType;
	}
	/**
	 * 値のゲッター
	 *	
	 */
	public String getVal(){
		return mVal;
	}
	/**
	 * ファイルを使用するか否か取得する。
	 */
	public boolean useFile(){
		return mUseFileFlag;
	}
}
