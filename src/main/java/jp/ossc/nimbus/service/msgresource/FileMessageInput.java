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

import java.util.*;
import java.io.*;
import jp.ossc.nimbus.util.*;
import jp.ossc.nimbus.lang.*;
/**
 *	Fileメッセージインプットクラス
 *	@author	y-tokuda
 *	@version	1.00 作成：2003/11/05－ y-tokuda<BR>
 *				更新：
 */
public class FileMessageInput implements MessageInput,MessageResourceDefine{
	//メンバ変数
	/** ペイロード文字列 */
	private String[] mPayloadArray = null;
	/** プロパティ*/
	private Properties[] mPropertiesArray = null;
	/** 現在行 */
	private int mCurrentline = 0;
	/** レコード数 */
	private int mRecordNum = 0;
	/** プロパティ部とペイロード部のセパレーター */
	private final String SEP = ":";
	/**
	 * コンストラクタ
	 */
	public FileMessageInput(String filename){
		//指定されたファイルを開き、データをキャッシュする。
		ArrayList payloadDefs = new ArrayList();
		ArrayList propertyDefs = new ArrayList();
		try{
			LineNumberReader reader =  new LineNumberReader(new FileReader(filename));
			String line = null;
			mRecordNum = 0;
			String payloadStr = "";
			String propStr = "";
			while( (line = reader.readLine()) != null){
				//改行のみの行は存在しないものとして扱う
				if(line.equals("")){
					continue;
				}
				//コメント行は存在しないものとして扱う
				if(line.charAt(0) == '#'){
					continue;
				}
				//プロパティ部とペイロード部を分割する。
				int pos = line.indexOf(SEP);				
				if(pos == -1){
					//セパレータ無しの場合不正フォーマットとしてServiceExceptionを発生
					throw new ServiceException("MESSAGERESOURCESERVICE901","line no."+reader.getLineNumber() + " is invalid. filename is " + filename+ ".");
				}
				else if(pos == 0){
					//セパレータ先頭の場合、プロパティ指定無しと解釈。
					propStr = "";
					payloadStr=line.substring(1);
					
				}
				//セパレータ文字が最後だったら、ペイロード指定無しと解釈
				else if(pos == (line.length() -1)){
					propStr=line.substring(0,pos);
					payloadStr="";
				}
				else{
					propStr = line.substring(0,pos);
					payloadStr = line.substring(pos+1);
				}
				//プロパティを生成しておく
				Properties jmsProp = new Properties();
				CsvArrayList props = new CsvArrayList();
				props.split(propStr);
				if(propStr.equals("")){
					//何もしない。jmsPropは空のまま
					;
				}
				else{
					for(int rCnt=0;rCnt<props.size();rCnt++){
						String keyAndVal = (String)props.get(rCnt);
						int separatePos = keyAndVal.indexOf("=");
						if( (separatePos>0 ) && (separatePos < keyAndVal.length() -1)){
							String key = keyAndVal.substring(0,separatePos);
							String val = keyAndVal.substring(separatePos + 1);
							jmsProp.put(key,val);
						}
						else{
							throw new ServiceException("MESSAGERESOURCESERVICE902",
														"line no."+reader.getLineNumber() + 
														" has invalid property definition. " + 
														filename+ ".");
						}
						
					}
				}
				propertyDefs.add(jmsProp);
				payloadDefs.add(payloadStr);
				mRecordNum++;
			}
		}
		catch(Exception e){
			//データファイルの読み込みに失敗したらExceptionを上げる。
			//e.printStackTrace();
			throw new ServiceException("MESSAGERESOURCESERVICE900","Exception were thrown while reading "+filename+" .",e);
		}
		//配列に詰めなおす
		mPayloadArray = new String[mRecordNum];
		mPropertiesArray = new Properties[mRecordNum];
		for(int rCnt=0;rCnt<mRecordNum;rCnt++){
			mPayloadArray[rCnt] = (String)payloadDefs.get(rCnt);
			mPropertiesArray[rCnt] = (Properties)propertyDefs.get(rCnt);
		}
	}
	
	/**
	 * 1行進める。最終行に達したら、先頭に戻る。
	 */
	public void nextLine(){
		if( (mRecordNum -1) > mCurrentline){
			mCurrentline++;
		}
		else{
			mCurrentline = 0;
		}
	}
	/**
	 * ペイロードデータ文字列を返す
	 */
	public String getInputString(){
		return mPayloadArray[mCurrentline];
	}
	/**
	 * プロパティデータ文字列を返す
	 *	
	 */
	public Properties getMessageHeadProp(){
		return mPropertiesArray[mCurrentline];
	}
}
