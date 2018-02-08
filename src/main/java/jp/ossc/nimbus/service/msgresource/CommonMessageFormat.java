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

import javax.jms.Message;
import javax.jms.QueueSession;
import jp.ossc.nimbus.lang.*;
import org.w3c.dom.*;
import java.util.*;
import jp.ossc.nimbus.service.byteconvert.*;

/**
 *	CommonMessageFormat
 *  Text,Bytes,Stream,Object,MapMessageFormatの
 *  上位クラス。共通するプロパティ部のパースや定数定義をここに移した。
 *	@author	y-tokuda
 *	@version	1.00 作成：2003/11/07－ y-tokuda<BR>
 *				更新：
 */
public abstract class CommonMessageFormat
	implements MessageFormat, MessageResourceDefine {
	//メンバ変数
	/** プロパティ項目保持 */
	protected ArrayList mPropertyItems = null;
	/** バイトコンバータ */
	protected ByteConverter mByteConverter = null;
	/** メッセージインプット */
	protected MessageInput mMessageInput;
	
	//定数定義
	static public final int TYPE_BYTE = 0;
	static public final String TYPE_BYTE_STR = "Byte";
	static public final int TYPE_UBYTE = 1;
	static public final String TYPE_UBYTE_STR = "UnsignedByte";
	static public final int TYPE_BYTES = 2;
	static public final String TYPE_BYTES_STR = "Bytes";
	static public final int TYPE_BOOLEAN = 3;
	static public final String TYPE_BOOLEAN_STR = "Boolean";
	static public final int TYPE_CHAR = 4;
	static public final String TYPE_CHAR_STR = "Char";
	static public final int TYPE_SHORT = 5;
	static public final String TYPE_SHORT_STR = "Short";
	static public final int TYPE_USHORT = 6;
	static public final String TYPE_USHORT_STR = "UnsignedShort";
	static public final int TYPE_INT = 7;
	static public final String TYPE_INT_STR = "Int";
	static public final int TYPE_LONG = 8;
	static public final String TYPE_LONG_STR = "Long";
	static public final int TYPE_FLOAT = 9;
	static public final String TYPE_FLOAT_STR = "Float";
	static public final int TYPE_DOUBLE = 10;
	static public final String TYPE_DOUBLE_STR = "Double";
	static public final int TYPE_UTF = 11;
	static public final String TYPE_UTF_STR = "UTF";
	static public final int TYPE_STRING = 12;
	static public final String TYPE_STRING_STR = "String";
	static public final int TYPE_OBJECT = 13;
	static public final String TYPE_OBJECT_STR = "Object";	

	/**
	 * コンストラクタ
	 */
	public CommonMessageFormat(ByteConverter converter){
		mByteConverter = converter;
		mPropertyItems = new ArrayList();
	}
	
	/**
	 * JMSメッセージが保持する情報をString化する。
	 * 下位のクラスに実装を強制する。
	 */
	public abstract String marshal(Message msg);

	/**
	 * JMSメッセージを生成する。
	 * 下位のクラスに実装を強制する。
	 */
	public abstract Message unMarshal(QueueSession session);

	/**
	 * XML定義をパースする。
	 */
	public void parse(Element elem){
		String tagName = elem.getTagName();
		if(tagName.equals(SENDDATA_TAG_NAME)){
			//inputfile要素の存在を確認する。
			boolean inputFileExists = false;
			NodeList inputElems = elem.getElementsByTagName(INPUT_FILE_TAG);
			if(inputElems.getLength() > 1){
				//inputfile要素を2個以上指定はできない。
				throw new ServiceException("MESSAGERESOURCEFACTORY012","<" +INPUT_FILE_TAG + ">" + 
											"is can be exists only one.");
			}
			for(int rCnt=0;rCnt<inputElems.getLength();rCnt++){
				//inputfile要素定義されていれば1回だけこのループが実行される。
				Element inputElem = (Element)inputElems.item(rCnt);
				String fileName = MessageResourceUtil.getValueMustbeSpecified(inputElem);
				mMessageInput = new FileMessageInput(fileName);
				inputFileExists = true;
				
			}
			//プロパティ部
			propParse(elem,inputFileExists);
			//ペイロード部
			sendPayloadParse(elem,inputFileExists);
			
		}
		else if(tagName.equals(RECVDATA_TAG_NAME)){
			//受信電文定義タグ
			//ペイロード部
			recvPayloadParse(elem);
		}
	}
	/**
	 * 送信ペイロードのパース
	 */
	protected abstract void sendPayloadParse(Element elem,boolean fileExists);
	/**
	 * 受信ペイロード定義のパース
	 */
	protected abstract void recvPayloadParse(Element elem);
	
	
	/**
	 * メンバ変数mPropertyItemsにproperty情報を設定する。
	 */
	protected void propParse(Element elem,boolean fileSpecifiedFlag){
		propKindParse(elem,fileSpecifiedFlag,mPropertyItems,PROP_TAG_NAME);
	}
	/**
	 * propParseと、MapMessageFormatの、sendMessageParseからコールされる。
	 * @param elem
	 * @param fileSpecifiedFlag
	 * @param Items
	 */
	protected void propKindParse(Element elem,boolean fileSpecifiedFlag,
								ArrayList Items,String tagName) {
		NodeList propList = elem.getElementsByTagName(tagName);
		if (propList.getLength() > 1){
			//定義が複数ある
			throw new ServiceException("MESSAGERESOURCEFACTORY013","<" + tagName + 
										"> can be specified only onece.");
		}
		boolean isAProperty = false;
		if(tagName.equals(PROP_TAG_NAME)){
			isAProperty = true;
		}
		for(int rCnt=0;rCnt<propList.getLength();rCnt++){
			//定義が存在すれば、1回だけこのループ内部が実行される。
			Element propElem = (Element)propList.item(rCnt);
			NodeList propItems = propElem.getElementsByTagName(PROP_ITEM);
			for(int rCount=0;rCount<propItems.getLength();rCount++){
				String type = null;
				String wrappedType = null;
				String val = null;
				String name = null;
				boolean itemUseFileFlag = false;
				Element propItem = (Element)propItems.item(rCount);
				name = MessageResourceUtil.getAttMustBeSpecified(propItem,PROP_ITEM_NAME_ATT);
				type = MessageResourceUtil.getAttMustBeSpecified(propItem,PROP_ITEM_TYPE_ATT);
				int wrappedTypeCode = -1;
				if (type.equals("Object")){
					wrappedType = MessageResourceUtil.getAttMustBeSpecified(propItem,PROP_ITEM_WRAPPED_TYPE_ATT);
					wrappedTypeCode = getWrappedTypeCode(wrappedType,isAProperty);
					if(wrappedTypeCode < 0){
						//有効な型指定ではない。
						throw new ServiceException("MESSAGERESOURCEFACTORY019","Invalid Type :" + wrappedType); 										
					}			
				}
				//値を取得
				val = MessageResourceUtil.getValue(propItem);
				//ファイルを参照するかどうかを取得
				String resourceType = propItem.getAttribute(PROP_ITEM_RES_TYPE_ATT);
				if(resourceType.equals(FILE_VAL)){
					if(fileSpecifiedFlag){
						itemUseFileFlag = true;
					}
					else{
						//データファイルが指定されていない場合、ファイル参照するitemは定義できない
						throw new ServiceException("MESSAGERESOURCEFACTORY014","File not specified. But " 
													+ PROP_ITEM_TYPE_ATT + " has " + FILE_VAL + " attribute.");
					}
				}
				int typeCode = getPropertyTypeCode(type,isAProperty);
				if(typeCode < 0){
					//有効な型指定ではない。
					throw new ServiceException("MESSAGERESOURCEFACTORY018","Invalid Type :" + type); 										
				}				
				Items.add(new PropItem(name,typeCode,wrappedTypeCode,val,itemUseFileFlag));	
			}
		}
	}
	/**
	 * BytesMessage,StreamMessageのwriteObject() <BR>
	 * および、MessageのsetObjectProperty<BR>
	 * メソッドで書き込む"型"のコードを返す。<BR>
	 */
	protected int getWrappedTypeCode(String type,boolean propertyMode){
		if(type.equals(TYPE_BYTE_STR)){
			return TYPE_BYTE;
		}
		else if(type.equals(TYPE_BYTES_STR)){
			if(propertyMode == false){
				return TYPE_BYTES;
			}
			else{
				return -1;
			}
		}
		else if(type.equals(TYPE_BOOLEAN_STR)){
			return TYPE_BOOLEAN;
		}
		else if(type.equals(TYPE_CHAR_STR)){
			return TYPE_CHAR;
		}
		else if(type.equals(TYPE_SHORT_STR)){
			return TYPE_SHORT;
		}
		else if(type.equals(TYPE_INT_STR)){
			return TYPE_INT;
		}
		else if(type.equals(TYPE_LONG_STR)){
			return TYPE_LONG;
		}
		else if(type.equals(TYPE_FLOAT_STR)){
			return TYPE_FLOAT;
		}
		else if(type.equals(TYPE_DOUBLE_STR)){
			return TYPE_DOUBLE;
		}
		else if(type.equals(TYPE_STRING_STR)){
			return TYPE_STRING;
		}
		else if(type.equals(TYPE_UTF_STR)){
			return TYPE_UTF;
		}
		return -1;
	}
	
	/**
	 * 型コードから型名を取得する。
	 * @param code
	 * @return
	 */
	protected String getWrappedTypeStr(int code){
		String ret = "";
		switch(code){
			case TYPE_BYTE:
				ret = TYPE_BYTE_STR;
				break;
			case TYPE_BYTES:
				ret = TYPE_BYTES_STR;
				break;
			case TYPE_BOOLEAN:
				ret = TYPE_BOOLEAN_STR;
				break;
			case TYPE_CHAR:
				ret = TYPE_CHAR_STR;
				break;
			case TYPE_SHORT:
				ret = TYPE_SHORT_STR;
				break;
			case TYPE_INT:
				ret = TYPE_INT_STR;
				break;
			case TYPE_LONG:
				ret = TYPE_LONG_STR;
				break;
			case TYPE_FLOAT:
				ret = TYPE_FLOAT_STR;
				break;
			case TYPE_DOUBLE:
				ret = TYPE_DOUBLE_STR;
				break;
			case TYPE_STRING:
				ret = TYPE_STRING_STR;
				break;	
			default:
		}
		return	ret;		
	}
	
	/**
	 * プロパティの型コードを返す。
	 * 
	 * @return
	 */
	protected int getPropertyTypeCode(String type,boolean propertyMode){
		if(type.equals(TYPE_STRING_STR)){
			return TYPE_STRING;
		}
		else if(type.equals(TYPE_INT_STR)){
			return TYPE_INT;
		}
		else if(type.equals(TYPE_BOOLEAN_STR)){
			return TYPE_BOOLEAN;
		}
		else if(type.equals(TYPE_DOUBLE_STR)){
			return TYPE_DOUBLE;
		}
		else if(type.equals(TYPE_FLOAT_STR)){
			return TYPE_FLOAT;
		}
		if(type.equals(TYPE_BYTE_STR)){
			return TYPE_BYTE;
		}
		if(type.equals(TYPE_BYTES_STR)){
			if(propertyMode){
				//プロパティに"Bytes"はない。
				return -1;
			}
			return TYPE_BYTES;
		}
		if(type.equals(TYPE_CHAR_STR)){
			if(propertyMode){
				//プロパティに"Char"はない。
				return -1;
			}
			return TYPE_CHAR;
		}
		else if(type.equals(TYPE_LONG_STR)){
			return TYPE_LONG;
		}
		else if(type.equals(TYPE_SHORT_STR)){
			return TYPE_SHORT;
		}
		else if(type.equals(TYPE_OBJECT_STR)){
			return TYPE_OBJECT;
		}
		return -1;
	}
	
	
	/**
	 * ペイロードの"型"コードを返す
	 */
	protected int getReadTypeCode(String type,String msgType){
		if(type.equals(TYPE_BYTE_STR)){
			return TYPE_BYTE;
		}
		else if(type.equals(TYPE_UBYTE_STR)){
			if(msgType.equals("Bytes")){
				return TYPE_UBYTE;
			}
			else{
				//StreamMessageにreadUnsignedByteはない。
				return -1;
			}			
		}
		else if(type.equals(TYPE_BYTES_STR)){
			return TYPE_BYTES;
		}
		else if(type.equals(TYPE_BOOLEAN_STR)){
			return TYPE_BOOLEAN;
		}
		else if(type.equals(TYPE_CHAR_STR)){
			return TYPE_CHAR;
		}
		else if(type.equals(TYPE_INT_STR)){
			return TYPE_INT;
		}
		else if(type.equals(TYPE_SHORT_STR)){
			return TYPE_SHORT;
		}
		else if(type.equals(TYPE_USHORT_STR)){
			if(msgType.equals("Bytes")){
				return TYPE_USHORT;
			}
			else{
				//StreamMesageにreadUnsignedShortはない。
				return -1;
			}
		}
		else if(type.equals(TYPE_LONG_STR)){
			return TYPE_LONG;
		}
		else if(type.equals(TYPE_FLOAT_STR)){
			return TYPE_FLOAT;
		}
		else if(type.equals(TYPE_DOUBLE_STR)){
			return TYPE_DOUBLE;
		}
		else if(type.equals(TYPE_UTF_STR)){
			if(msgType.equals("Bytes")){
				return TYPE_UTF;
			}
			else{
				//StreamMessageに、readUTFはない。
				return -1;
			}
		}
		else if(type.equals(TYPE_STRING_STR)){
			if(msgType.equals("Bytes")){
				//BytesMessageに、readStringはない。
				return -1;
			}
			else{
				return TYPE_STRING;
			}
		}
		else if(type.equals(TYPE_OBJECT_STR)){
			if(msgType.equals("Bytes")){
				//BytesMessageに、readObjectはない。
				return -1;
			}
			else{
				return TYPE_OBJECT;
			}		
		}
		return -1;
	}
	
	protected int getWriteTypeCode(String type,String msgType){
		if(type.equals(TYPE_BYTE_STR)){
			return TYPE_BYTE;
		}
		else if(type.equals(TYPE_BYTES_STR)){
			return TYPE_BYTES;
		}
		else if(type.equals(TYPE_BOOLEAN_STR)){
			return TYPE_BOOLEAN;
		}
		else if(type.equals(TYPE_CHAR_STR)){
			return TYPE_CHAR;
		}
		else if(type.equals(TYPE_INT_STR)){
			return TYPE_INT;
		}
		else if(type.equals(TYPE_SHORT_STR)){
			return TYPE_SHORT;
		}
		else if(type.equals(TYPE_LONG_STR)){
			return TYPE_LONG;
		}
		else if(type.equals(TYPE_FLOAT_STR)){
			return TYPE_FLOAT;
		}
		else if(type.equals(TYPE_DOUBLE_STR)){
			return TYPE_DOUBLE;
		}
		else if(type.equals(TYPE_UTF_STR)){
			if(msgType.equals("Bytes")){
				return TYPE_UTF;
			}
			return -1;
		}
		else if(type.equals(TYPE_STRING_STR)){
			if(msgType.equals("Bytes")){
				return -1;
			}
			return TYPE_STRING;
		}
		else if(type.equals(TYPE_OBJECT_STR)){
			return TYPE_OBJECT;
		}
		return -1;
	}
	
	/**
	 * JMSメッセージのプロパティ部設定
	 */
	protected void setMessageHeadProperties(Message msg,Properties prop) {
		Iterator Items = mPropertyItems.iterator();
		try{
			while(Items.hasNext()){
				PropItem item = (PropItem)Items.next();
				int type = item.getType();
				String valueStr = null;
				String name = item.getName();
				if(item.useFile()){
					//MessageInput取得したPropertiesオブジェクトから値を引き出す
					valueStr = (String)prop.get(name);
				}
				else{
					//即値
					valueStr = item.getVal();
				}
				//型に応じたプロパティ部のセッターを使う。
				switch(type){
					case TYPE_BYTE:
						byte[] tmp = mByteConverter.hex2byte(valueStr);
						msg.setByteProperty(name,tmp[0]);
						break;
					case TYPE_BOOLEAN:
						Boolean bool = Boolean.valueOf(valueStr);
						msg.setBooleanProperty(name,bool.booleanValue());
						break;
					case TYPE_SHORT:
						msg.setShortProperty(name,Short.parseShort(valueStr));
						break;
					case TYPE_INT:
						msg.setIntProperty(name,Integer.parseInt(valueStr));
						break;
					case TYPE_LONG:
						msg.setLongProperty(name,Long.parseLong(valueStr));
						break;
					case TYPE_FLOAT:
						msg.setFloatProperty(name,Float.parseFloat(valueStr));
						break;
					case TYPE_DOUBLE:
						msg.setDoubleProperty(name,Double.parseDouble(valueStr));
						break;
					case TYPE_STRING:
						msg.setStringProperty(name,valueStr);
						break;
					case TYPE_OBJECT:
						Object obj = createObject(item.getWrappedType(),valueStr);
						msg.setObjectProperty(name,obj);
						break;
					default:
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
			throw new ServiceException("MESSAGERESOURCEFACTORY017",
										"Property Setting failed.",e);
		}
		
	}
	/**
	 * プリミティブのラッパーインスタンスを生成するメソッド
	 */
	protected Object createObject(int type,String valueStr){
		Object ret = null;
		switch(type){
				case TYPE_BYTE:
					byte[] tmp = mByteConverter.hex2byte(valueStr);					
					ret = new Byte(tmp[0]);
					break;
				case TYPE_BYTES:
					ret = mByteConverter.hex2byte(valueStr);
					break;
				case TYPE_BOOLEAN:
					ret = Boolean.valueOf(valueStr);
					break;
				case TYPE_CHAR:
					ret =  new Character(valueStr.charAt(0));
					break;
				case TYPE_SHORT:
					ret = Short.valueOf(valueStr);
					break;
				case TYPE_INT:
					ret = Integer.valueOf(valueStr);
					break;
				case TYPE_LONG:
					ret = Long.valueOf(valueStr);
					break;
				case TYPE_FLOAT:
					ret = Float.valueOf(valueStr);
					break;
				case TYPE_DOUBLE:
					ret = Double.valueOf(valueStr);
					break;
				case TYPE_STRING:
					ret = valueStr;
					break;
				case TYPE_UTF:
					ret = valueStr;
					break;
				default:
		}
		return ret;
	}
	/**
	 * JMSメッセージプロパティ部のダンプ
	 * @param msg
	 * @return
	 */
	protected String dumpProperties(Message msg){
		StringBuffer ret = new StringBuffer();
		try{
			Enumeration propertyNames = msg.getPropertyNames();
			while(propertyNames.hasMoreElements()){
				String name = (String)propertyNames.nextElement();
				Object value = msg.getObjectProperty(name);
				ret.append(name + "=" + value + " ");
			}
			
		}
		catch(Exception e){
			throw new ServiceException("MESSAGERESOURCEFACTORY017",
							"Property Dump Failed",e);
		}
		return ret.toString();
	}

}
