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
import javax.jms.*;
import java.util.*;
import jp.ossc.nimbus.lang.*;
/**
 * @author y-tokuda
 *
 * この生成されたコメントの挿入されるテンプレートを変更するため
 * ウィンドウ > 設定 > Java > コード生成 > コードとコメント
 */
public class MessageResourceImpl implements MessageResourceOperator {
	//メンバ変数
	/** BLフロー保持ハッシュ */
	private HashMap mBlFlowHash = null;
	/** 送信用メッセージフォーマット */
	private MessageFormat mSendMessageFormat = null;
	/** 受信用メッセージフォーマット */
	private MessageFormat mRecvMessageFormat = null;
	/** Display用文字列 */
	private String mDisplayStr = null;
	/** Key属性 */
	private String mKey = null;
	/**
	 * コンストラクタ
	 */
	public MessageResourceImpl(){
		mBlFlowHash = new HashMap();
	}
	/**
	 * BLフローキーを取得する。
	 */
	public String getBLFlow(String pat) {
		return (String)mBlFlowHash.get(pat);
	}
	/**
	 * display用文字列を設定する。
	 */
	public void setDisplayMessage(String msg){
		mDisplayStr = msg;
	}
	/**
	 * Key属性を設定する。
	 */
	public void setKey(String key){
		mKey = key;
	}
	/**
	 * display用文字列を取得する。
	 */
	public String display(){
		return mDisplayStr;
	}
	/**
	 * Key属性を取得する。
	 */
	public String getKey(){
		return mKey;
	}
	
	/**
	 * JMSメッセージを生成する。
	 */ 
	public Message makeMessage(QueueSession session) {
		return mSendMessageFormat.unMarshal(session);
	}
	
	public String toString(Message msg,String kind){
		String ret = null;
		if(kind.equals("send")){
			try{
				if( (msg instanceof BytesMessage) ){
					//送信電文はresetしないと読めない
					((BytesMessage)msg).reset();
				}
				else if(msg instanceof StreamMessage){
					//送信電文はresetしないと読めない
					((StreamMessage)msg).reset();
				}
			}
			catch(JMSException e){
				throw new ServiceException("MESSAGERESOURCEFACTORY006","Bytes(Stream)Message's reset() method failed.");	
			}

			ret = mSendMessageFormat.marshal(msg);
		}
		else if(kind.equals("recv")){
			ret = mRecvMessageFormat.marshal(msg);
		}
		return ret;
	}
	
	/**
	 * BLFlowキーを追加する。
	 * @param key
	 * @param value
	 */
	public void addBLFlowKey(String key,String value){
		mBlFlowHash.put(key,value);
	}
	
	/**
	 * 送信用メッセージフォーマット設定メソッド
	 * @param messageFormat
	 * @param kind
	 */
	public void setMessageFormat(MessageFormat messageFormat,String kind){
		if(kind.equals("send")){
			mSendMessageFormat = messageFormat;
		}
		else if(kind.equals("recv")){
			mRecvMessageFormat = messageFormat;
		}
		
	}
}
