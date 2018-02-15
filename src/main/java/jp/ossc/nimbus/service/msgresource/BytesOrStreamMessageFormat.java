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
import javax.jms.*;
import org.w3c.dom.*;
import jp.ossc.nimbus.lang.*;
import jp.ossc.nimbus.util.*;
import jp.ossc.nimbus.service.byteconvert.*;

/**
 *	Bytes(Stream)メッセージフォーマット
 *	@author	y-tokuda
 *	@version	1.00 作成：2003/10/28－ y-tokuda<BR>
 *				更新：
 */
public class BytesOrStreamMessageFormat extends CommonMessageFormat implements MessageFormat,MessageResourceDefine{
    
    //メンバ変数
	/** 電文ペイロード項目の情報 */
	private ArrayList mPayLoadItems = null;
	/** メッセージインプット */
	//private MessageInput mMessageInput = null;
	/** バイトコンバーターサービス */
	private ByteConverter mByteConverter = null;
	/** 扱うJMSメッセージの種別 (Bytes OR Stream */
	private String mMessageType = null;
	//定数宣言
	/** 型と値のセパレータ（表示時） */
	final String TYPE_VALUE_SEP = ":";
	/** readBytesする時のbyte[]のサイズ */
	final int BUFLEN = 8192;
	/**
	 * コンストラクタ
	 */
	public BytesOrStreamMessageFormat(ByteConverter converter,String msgType){
		super(converter);
		//バイトコンバータサービスの設定
		mByteConverter = converter;
		//ペイロード項目保持ArrayListの初期化
		mPayLoadItems = new ArrayList();
		//プロパティ項目保持ArrayListの初期化
		mPropertyItems = new ArrayList();
		//メッセージ種別 Bytes OR Stream
		if((msgType.equals("Bytes")) || (msgType.equals("Stream"))){
			mMessageType = msgType;
		}
		else{
			throw new ServiceException("MESSAGERESOURCEFACTORY015","Must be Specified Bytes OR Stream");
		}
	}
	/**
	 *JMSメッセージが保持する情報をString化する
	 */
	public String marshal(Message msg) {
		if( (!(msg instanceof BytesMessage)) && (!(msg instanceof StreamMessage)) ){
			return null;
		}
		byte[] readBytesBuffer = new byte[BUFLEN];
		StringBuilder retMsg = new StringBuilder();
		//ラッパーJMSメッセージを設定
		MessageWrapper msgWrapper = new MessageWrapper(msg);
		try{
			//プロパティ部をString化
			retMsg.append(dumpProperties(msg));
			//ペイロード部をString化
			Iterator Items = mPayLoadItems.iterator();
			while(Items.hasNext()){
				PayLoadItem item = (PayLoadItem)Items.next();
				int type = item.getType();
				switch (type){
					case TYPE_BYTE:
						retMsg.append(TYPE_BYTE_STR);
						retMsg.append(TYPE_VALUE_SEP);
						int tmp = (int)msgWrapper.readByte();
						retMsg.append("0x");
						retMsg.append(Integer.toHexString(tmp));
						break;
					case TYPE_UBYTE:
						retMsg.append(TYPE_UBYTE_STR);
						retMsg.append(TYPE_VALUE_SEP);
						tmp = (int)msgWrapper.readUnsignedByte();
						retMsg.append("0x");
						retMsg.append(Integer.toHexString(tmp));
						break;
					case TYPE_BYTES:
						retMsg.append(TYPE_BYTES_STR);
						retMsg.append(TYPE_VALUE_SEP);
						int readNum = -1;
						//データを読み込む					
						while(true){
							readNum = msgWrapper.readBytes(readBytesBuffer);
							if(readNum == -1){
								//これ以上データはない。終了
								break;
							}
							else{
								//データを文字列化する。
								for(int rCnt=0;rCnt<readNum;rCnt++){
									tmp = (int)readBytesBuffer[rCnt];
									retMsg.append("0x");
									retMsg.append(Integer.toHexString(tmp));
									retMsg.append(" ");
								}
								if(readNum == BUFLEN){
									//もう一回読み込む必要あり。
									
								}
								else{
									//終了
									break;//whileループをブレーク
								}
							}
						}
						break;
					case TYPE_BOOLEAN:
						retMsg.append(TYPE_BOOLEAN_STR);
						retMsg.append(TYPE_VALUE_SEP);
						retMsg.append(msgWrapper.readBoolean());
						break;	
					case TYPE_CHAR:
						retMsg.append(TYPE_CHAR_STR);
						retMsg.append(TYPE_VALUE_SEP);
						retMsg.append(msgWrapper.readChar());
						break;	
					case TYPE_SHORT:
						retMsg.append(TYPE_SHORT_STR);
						retMsg.append(TYPE_VALUE_SEP);
						retMsg.append(msgWrapper.readShort());
						break;				
					case TYPE_USHORT:
						retMsg.append(TYPE_USHORT_STR);
						retMsg.append(TYPE_VALUE_SEP);
						retMsg.append(msgWrapper.readUnsignedShort());
						break;
					case TYPE_INT:
						retMsg.append(TYPE_INT_STR);
						retMsg.append(TYPE_VALUE_SEP);
						retMsg.append(msgWrapper.readInt());
						break;	
					case TYPE_LONG:
						retMsg.append(TYPE_LONG_STR);
						retMsg.append(TYPE_VALUE_SEP);
						retMsg.append(msgWrapper.readLong());
						break;
					case TYPE_FLOAT:
						retMsg.append(TYPE_FLOAT_STR);
						retMsg.append(TYPE_VALUE_SEP);
						retMsg.append(msgWrapper.readFloat());
						break;
					case TYPE_DOUBLE:
						retMsg.append(TYPE_DOUBLE_STR);
						retMsg.append(TYPE_VALUE_SEP);
						retMsg.append(msgWrapper.readDouble());
						break;
					case TYPE_UTF:
						retMsg.append(TYPE_UTF_STR);
						retMsg.append(TYPE_VALUE_SEP);
						retMsg.append(msgWrapper.readUTF());
						break;
					case TYPE_STRING:
						retMsg.append(TYPE_STRING_STR);
						retMsg.append(TYPE_VALUE_SEP);
						retMsg.append(msgWrapper.readString());
						break;
					case TYPE_OBJECT:
						retMsg.append(TYPE_OBJECT_STR);
						//wrappedTypeに応じて
						retMsg.append( "(" + getWrappedTypeStr(item.getWrappedType()) + ")" );
						retMsg.append(TYPE_VALUE_SEP);
						retMsg.append(msgWrapper.readObject());
					default:
						break;
				}
			}
		}
		catch(JMSException e){
			e.printStackTrace();
			throw new ServiceException("MESSAGERESOURCEFACTORY016","Fail to read Message",e);
		}
		return retMsg.toString();
	}
	/**
	 * JMSメッセージのペイロード設定
	 */
	protected void setPayload(Message msg,String payload) {
		MessageWrapper msgWrapper = new MessageWrapper(msg);
		Iterator Items = mPayLoadItems.iterator();
		try{
			while(Items.hasNext()){
				PayLoadItem item = (PayLoadItem)Items.next();
				int type = item.getType();
				String valueStr = null;
				if(item.UseFile()){
					//ペイロード文字列(カンマ区切り）から値を表す文字列を取得する。
					CsvArrayList elems = new CsvArrayList();
					elems.split(payload);
					int index = -1;
					try{
						index = Integer.parseInt(item.getValue());
						valueStr = (String)elems.get(index);
					}
					catch(Exception e){
						throw new ServiceException("MESSAGERESOURCEFACTORY017","Invalid column num specified.");
					}
				}
				else{
					//即値
					valueStr = item.getValue();
				}
				//型に応じたwriteメソッドを使う
				switch(type){
						case TYPE_BYTE:
							byte[] tmp = mByteConverter.hex2byte(valueStr);
							msgWrapper.writeByte(tmp[0]);
							break;
						case TYPE_BYTES:
						    byte[] buf = mByteConverter.hex2byte(valueStr);
							msgWrapper.writeBytes(buf);
							break;
						case TYPE_BOOLEAN:
							Boolean bool = Boolean.valueOf(valueStr);
							msgWrapper.writeBoolean(bool.booleanValue());
							break;
						case TYPE_CHAR:
							msgWrapper.writeChar(valueStr.charAt(0));
							break;
						case TYPE_SHORT:
							msgWrapper.writeShort(Short.parseShort(valueStr));
							break;
						case TYPE_INT:
							msgWrapper.writeInt(Integer.parseInt(valueStr));
							break;
						case TYPE_LONG:
							msgWrapper.writeLong(Long.parseLong(valueStr));
							break;
						case TYPE_FLOAT:
							msgWrapper.writeFloat(Float.parseFloat(valueStr));
							break;
						case TYPE_DOUBLE:
							msgWrapper.writeDouble(Double.parseDouble(valueStr));
							break;
						case TYPE_UTF:
							msgWrapper.writeUTF(valueStr);
							break;
						case TYPE_STRING:
							msgWrapper.writeString(valueStr);
							break;
						case TYPE_OBJECT:
							Object obj = createObject(item.getWrappedType(),valueStr);
							msgWrapper.writeObject(obj);
							break;
						default:
				}
			
			}
		}
		catch(Exception e){
			throw new ServiceException("MESSAGERESOURCEFACTORY017",
										"PayLoad Setting failed.",e);
		}
	}
	/**
	 * JMSメッセージ生成メソッド
	 */
	public Message unMarshal(QueueSession session) {
		Message msg = null;
		try{
			if(mMessageType.equals("Bytes")){
				msg = session.createBytesMessage();
			}
			else{
				msg = session.createStreamMessage();
			}
		}
		catch(Exception e){
			throw new ServiceException("MESSAGERESOURCEFACTORY020",
										"PayLoad Setting failed.",e);
		}
		String recordStr = null;
		Properties prop = null;
		if(mMessageInput != null){
			recordStr = mMessageInput.getInputString();
			prop = mMessageInput.getMessageHeadProp();
			mMessageInput.nextLine();
		}
		setMessageHeadProperties(msg,prop);
		setPayload(msg,recordStr);
		return msg;
	}
	
	protected void recvPayloadParse(Element elem) {
		NodeList list = elem.getElementsByTagName(PAYLOAD_TAG_NAME);
		if (list.getLength() < 1){
			//ペイロードの定義無し
			throw new ServiceException("MESSAGERESOURCEFACTORY011","Not Found " + 
										"<" + PAYLOAD_TAG_NAME + ">");
		}
		//最初に定義されているペイロードを使う。仮に複数定義されていても無視する。
		Element payLoad = (Element)list.item(0);
		NodeList payLoadItems = payLoad.getElementsByTagName(PAYLOAD_ITEM);
		for(int rCnt=0;rCnt<payLoadItems.getLength();rCnt++){
			String type = null;
			String wrappedType = null;
			Element payLoadItem = (Element)payLoadItems.item(rCnt);
			type = MessageResourceUtil.getAttMustBeSpecified(payLoadItem,PAYLOAD_ITEM_TYPE_ATT);
			int wrappedTypeCode = -1;
			if (type.equals("Object")){
				wrappedType = MessageResourceUtil.getAttMustBeSpecified(payLoadItem,PAYLOAD_ITEM_WRAPPED_TYPE_ATT);
				wrappedTypeCode = getWrappedTypeCode(wrappedType,false);
				if (wrappedTypeCode < 0){
					throw new ServiceException("MESSAGERESOURCEFACTORY012","Invalid WrappedType :" + wrappedType);
				}
				
			}
			int typeCode = getReadTypeCode(type,mMessageType);
			if(typeCode < 0){
				//有効な型指定ではない。
				throw new ServiceException("MESSAGERESOURCEFACTORY012","Invalid Type :" + type); 										
			}
			mPayLoadItems.add(new PayLoadItem(typeCode,wrappedTypeCode));	
		}
	}
	protected void sendPayloadParse(Element elem,boolean fileSpecifiedFlag) {
		NodeList list = elem.getElementsByTagName(PAYLOAD_TAG_NAME);
		if (list.getLength() < 1){
			//ペイロードの定義無し
			throw new ServiceException("MESSAGERESOURCEFACTORY012","Not Found " + 
										"<" + PAYLOAD_TAG_NAME + ">");
		}
		//最初に定義されているペイロードを使う。仮に複数定義されていても無視する。
		Element payLoad = (Element)list.item(0);
		NodeList payLoadItems = payLoad.getElementsByTagName(PAYLOAD_ITEM);
		for(int rCnt=0;rCnt<payLoadItems.getLength();rCnt++){
			String type = null;
			String wrappedType = null;
			String val = null;
			boolean useFile = false;
			Element payLoadItem = (Element)payLoadItems.item(rCnt);
			type = MessageResourceUtil.getAttMustBeSpecified(payLoadItem,PAYLOAD_ITEM_TYPE_ATT);
			int wrappedTypeCode = -1;
			if (type.equals("Object")){
				wrappedType = MessageResourceUtil.getAttMustBeSpecified(payLoadItem,PAYLOAD_ITEM_WRAPPED_TYPE_ATT);
				wrappedTypeCode = getWrappedTypeCode(wrappedType,false);
				if(wrappedTypeCode < 0){
					//有効な型指定ではない。
					throw new ServiceException("MESSAGERESOURCEFACTORY012","Invalid Wrapped Type :" + wrappedType); 										
				}
			}
			//値を取得
			val = MessageResourceUtil.getValueMustbeSpecified(payLoadItem);
			//ファイルを参照するかどうかを取得
			String resourceType = payLoadItem.getAttribute(PAYLOAD_RES_TYPE_ATT);
			if(resourceType.equals(FILE_VAL)){
				if(fileSpecifiedFlag){
					useFile = true;
				}
				else{
					//データファイルを参照するように指定されていない場合ファイルを参照するitemは定義できない
					throw new ServiceException("MESSAGERESOURCEFACTORY015","File not specified. But " 
											+ PAYLOAD_ITEM_TYPE_ATT + " has " + FILE_VAL + " attribute.");
				}
			}
			int typeCode = getWriteTypeCode(type,mMessageType);
			//型定義属性の値チェック
			if(typeCode < 0){
				//有効な型指定ではない。
				throw new ServiceException("MESSAGERESOURCEFACTORY012","Invalid Type :" + type); 										
			}
			mPayLoadItems.add(new PayLoadItem(typeCode,wrappedTypeCode,val,useFile));	
		}
	}	
	private class PayLoadItem{
		//メンバ変数
		/** int,short,Object等、型情報 */
		int mType;
		/** mTypeがObjectの時、実際の型をこの変数に指定する。 */
		int mWrappedType;
		/** XML定義ファイルに即値が書いてあるとき、この変数に即値を設定する。 */
		String mVal;
		/** リソース種別 "file"か"direct"*/
		boolean mFileRefFlag;
		/** 
		 * コンストラクタ
		 * 値はnull,ファイル参照フラグはfalseにする。
		 */
		public PayLoadItem(int type,int wrappedtype){
			mType = type;
			mWrappedType = wrappedtype;
			mVal = null;
			mFileRefFlag = false;	
		}
		/**
		 * コンストラクタ
		 *	
		 */
		public PayLoadItem(int type,int inctype,String val,boolean fileRefFlag){
			mType = type;
			mWrappedType = inctype;
			mVal = val;
			mFileRefFlag = fileRefFlag;
		}
		/** Typeのゲッター */
		public int getType(){
			return mType;
		}
		/** ラップされている Typeのゲッター */
		public int getWrappedType(){
			return mWrappedType;
		}
		/** XMLファイルに記述された即値のゲッター */
		public String getValue(){
			return mVal;
		}
		/** データファイルを使用するかどうかのフラグ */
		public boolean UseFile(){
			return mFileRefFlag;
		}
	}
	/**
	 * BytesMessageとStreamMessageのラッパー
	 * 
	 */
	private class MessageWrapper{
		//メンバ変数
		Message mMessage;
		/**
		 * コンストラクタ
		 * @return
		 */
		public MessageWrapper(Message msg){
			if( (msg instanceof BytesMessage) || (msg instanceof StreamMessage)){
				mMessage = msg;
			}
			else{
				throw new ServiceException("MESSAGERESOURCEFACTORY300",
											"Must be Specified BytesMessage or StreamMessage");
			}
		}
		/**
		 * readByteのラッパー
		 */
		public byte readByte() throws JMSException{
			byte ret;
			if( mMessage instanceof BytesMessage){
				ret = ((BytesMessage)mMessage).readByte();
			}
			else{
				ret = ((StreamMessage)mMessage).readByte();
			}
			return ret;
		}
		public int readUnsignedByte() throws JMSException{
			int ret;
			if( mMessage instanceof BytesMessage){
				ret = ((BytesMessage)mMessage).readUnsignedByte();
			}
			else{
				throw new ServiceException("MESSAGERESOURCEFACTORY301",
											"readUnsignedByte Method not defined on StreamMessage");
			}
			return ret;
		}
		/**
		 * readBytesのラッパー
		 */	
		public int readBytes(byte[] value) throws JMSException{
			int ret;
			if( mMessage instanceof BytesMessage){
				ret = ((BytesMessage)mMessage).readBytes(value);
			}
			else{
				ret = ((StreamMessage)mMessage).readBytes(value);
			}
			return ret;
		}
		/**
		 * readBooleanのラッパー
		 */
		public boolean readBoolean() throws JMSException{
			boolean ret;
			if( mMessage instanceof BytesMessage){
				ret = ((BytesMessage)mMessage).readBoolean();
			}
			else{
				ret = ((StreamMessage)mMessage).readBoolean();
			}
			return ret;
		}
		/**
		 * readCharのラッパー
		 */
		public char readChar() throws JMSException{
			char ret;
			if( mMessage instanceof BytesMessage){
				ret = ((BytesMessage)mMessage).readChar();
			}
			else{
				ret = ((StreamMessage)mMessage).readChar();
			}
			return ret;
		}
		/**
		 * readShortのラッパー
		 */
		public short readShort() throws JMSException{
			short ret;
			if( mMessage instanceof BytesMessage){
				ret = ((BytesMessage)mMessage).readShort();
			}
			else{
				ret = ((StreamMessage)mMessage).readShort();
			}
			return ret;
		}
		/**
		 * readUnsignedShortのラッパー
		 */
		public int readUnsignedShort() throws JMSException{
			int ret;
			if( mMessage instanceof BytesMessage){
				ret = ((BytesMessage)mMessage).readUnsignedShort();
			}
			else{
				throw new ServiceException("MESSAGERESOURCEFACTORY300",
											"readUnsignedShort Method not defined on StreamMessage");
			}
			return ret;
		}
		/**
		 * readIntのラッパー
		 */
		public int readInt() throws JMSException{
			int ret;
			if( mMessage instanceof BytesMessage){
				ret = ((BytesMessage)mMessage).readInt();
			}
			else{
				ret = ((StreamMessage)mMessage).readInt();
			}
			return ret;
		}
		/**
		 * readLongのラッパー
		 */
		public long readLong() throws JMSException{
			long ret;
			if( mMessage instanceof BytesMessage){
				ret = ((BytesMessage)mMessage).readLong();
			}
			else{
				ret = ((StreamMessage)mMessage).readLong();
			}
			return ret;
		}
		/**
		 * readFloatのラッパー
		 */
		public float readFloat() throws JMSException{
			float ret;
			if( mMessage instanceof BytesMessage){
				ret = ((BytesMessage)mMessage).readFloat();
			}
			else{
				ret = ((StreamMessage)mMessage).readFloat();
			}
			return ret;
		}
		/**
		 * readDoubleのラッパー
		 */
		public double readDouble() throws JMSException{
			double ret;
			if( mMessage instanceof BytesMessage){
				ret = ((BytesMessage)mMessage).readDouble();
			}
			else{
				ret = ((StreamMessage)mMessage).readDouble();
			}
			return ret;
		}
		/**
		 * readUTFのラッパー
		 */
		public String readUTF() throws JMSException{
			String ret;
			if( mMessage instanceof BytesMessage){
				ret = ((BytesMessage)mMessage).readUTF();
			}
			else{
				throw new ServiceException("MESSAGERESOURCEFACTORY300",
											"readUTF Method not defined on StreamMessage");
			}
			return ret;
		}
		/**
		 * readStringのラッパー
		 */
		public String readString() throws JMSException{
			String ret;
			if( mMessage instanceof BytesMessage){
				throw new ServiceException("MESSAGERESOURCEFACTORY300",
											"readString Method not defined on StreamMessage");
			}
			else{
				ret = ((StreamMessage)mMessage).readString();

			}
			return ret;
		}
		
		/**
		 * readObjectのラッパー
		 */
		public Object readObject() throws JMSException{
			Object ret;
			if( mMessage instanceof BytesMessage){
				throw new ServiceException("MESSAGERESOURCEFACTORY300",
											"readString Method not defined on StreamMessage");
			}
			else{
				ret = ((StreamMessage)mMessage).readObject();

			}
			return ret;
		}
		
		/**
		 * writeByteのラッパー
		 */
		public void writeByte(byte value) throws JMSException{
			if( mMessage instanceof BytesMessage){
				((BytesMessage)mMessage).writeByte(value);
			}
			else{
				((StreamMessage)mMessage).writeByte(value);
			}
		}	

		/**
		 * writeBytesのラッパー
		 */
		public void writeBytes(byte[] value) throws JMSException{
			if( mMessage instanceof BytesMessage){
				((BytesMessage)mMessage).writeBytes(value);
			}
			else{
				((StreamMessage)mMessage).writeBytes(value);
			}
		}	
		/**
		 * writeBooleanのラッパー
		 */
		public void writeBoolean(boolean value) throws JMSException{
			if( mMessage instanceof BytesMessage){
				((BytesMessage)mMessage).writeBoolean(value);
			}
			else{
				((StreamMessage)mMessage).writeBoolean(value);
			}
		}	
		/**
		 * writeCharのラッパー
		 */
		public void writeChar(char value) throws JMSException{
			if( mMessage instanceof BytesMessage){
				((BytesMessage)mMessage).writeChar(value);
			}
			else{
				((StreamMessage)mMessage).writeChar(value);
			}
		}	

		/**
		 * writeShortのラッパー
		 */
		public void writeShort(short value) throws JMSException{
			if( mMessage instanceof BytesMessage){
				((BytesMessage)mMessage).writeShort(value);
			}
			else{
				((StreamMessage)mMessage).writeShort(value);
			}
		}	

		/**
		 * writeIntのラッパー
		 */
		public void writeInt(int value) throws JMSException{
			if( mMessage instanceof BytesMessage){
				((BytesMessage)mMessage).writeInt(value);
			}
			else{
				((StreamMessage)mMessage).writeInt(value);
			}
		}	

		/**
		 * writeLongのラッパー
		 */
		public void writeLong(long value) throws JMSException{
			if( mMessage instanceof BytesMessage){
				((BytesMessage)mMessage).writeLong(value);
			}
			else{
				((StreamMessage)mMessage).writeLong(value);
			}
		}	

		/**
		 * writeFloatのラッパー
		 */
		public void writeFloat(float value) throws JMSException{
			if( mMessage instanceof BytesMessage){
				((BytesMessage)mMessage).writeFloat(value);
			}
			else{
				((StreamMessage)mMessage).writeFloat(value);
			}
		}	

		/**
		 * writeDoubleのラッパー
		 */
		public void writeDouble(double value) throws JMSException{
			if( mMessage instanceof BytesMessage){
				((BytesMessage)mMessage).writeDouble(value);
			}
			else{
				((StreamMessage)mMessage).writeDouble(value);
			}
		}	

		/**
		 * writeUTFのラッパー
		 */
		public void writeUTF(String value) throws JMSException{
			if( mMessage instanceof BytesMessage){
				((BytesMessage)mMessage).writeUTF(value);
			}
			else{
				throw new ServiceException("MESSAGERESOURCEFACTORY300",
							"writeUTF Method not defined on StreamMessage");
			}
		}	
		/**
		 * writeStringのラッパー
		 */
		public void writeString(String value) throws JMSException{
			if( mMessage instanceof BytesMessage){
				throw new ServiceException("MESSAGERESOURCEFACTORY300",
							"writeString Method not defined on BytesMessage");
			}
			else{
				((StreamMessage)mMessage).writeString(value);
			}
		}	
		/**
		 * writeObjectのラッパー
		 */
		public void writeObject(Object obj) throws JMSException{
			if( mMessage instanceof BytesMessage){
				((BytesMessage)mMessage).writeObject(obj);
			}
			else{
				((StreamMessage)mMessage).writeObject(obj);
			}
		}	


		
				
	}
}