// jp.ossc.nimbus.service.msgresource.TextMessageFormat.java
// Copyright (C) 2002-2005 by Nomura Research Institute,Ltd.  All Rights Reserved.
//
/*****************************************************************************/
/** 更新履歴																**/
/** 																		**/
/*****************************************************************************/
// パッケージ
package jp.ossc.nimbus.service.msgresource;
// インポート
import jp.ossc.nimbus.lang.*;
import javax.jms.*;
import javax.jms.QueueSession;
import org.w3c.dom.*;
import java.util.*;
import jp.ossc.nimbus.service.byteconvert.*;
import jp.ossc.nimbus.util.*;

/**
 *	Textメッセージフォーマット
 *	@author	y-tokuda
 *	@version	1.00 作成：2003/11/06－ y-tokuda<BR>
 *				更新：
 */
public class TextMessageFormat extends CommonMessageFormat
	implements MessageFormat, MessageResourceDefine {
    
    //メンバ変数
	private String mNewLineDefinition = "\\n";
	//private MessageInput mMessageInput = null;
	/** 電文ペイロード即値情報 */
	private String mConstPayloadStr = null;
	/**
	 * コンストラクタ
	 */
	public TextMessageFormat(ByteConverter converter){
		super(converter);
	}

	/**
	 * JMSメッセージが保持している内容をString化する。
	 */
	public String marshal(Message msg) {
		if(!(msg instanceof TextMessage)){
			return null;
		}
		//プロパティ部
		StringBuffer ret = new StringBuffer("[property]");
		ret.append(dumpProperties(msg));
		ret.append("[payload]");
		TextMessage textMsg = (TextMessage)msg;
		try{
			ret.append(textMsg.getText());
		}
		catch(JMSException e){
			throw new ServiceException("MESSAGERESOURCEFACTORY100","getText() Failed",e);
		}
		
		return ret.toString();
	}
	/**
	 * JMSメッセージを生成する。
	 */
	public Message unMarshal(QueueSession session) {
		TextMessage textMsg = null;
		try{
			textMsg = session.createTextMessage();
		}
		catch(Exception e){
			throw new ServiceException("MESSAGERESOURCEFACTOR104",
										"PayLoad Setting failed.",e);
		}
		String recordStr = null;
		Properties prop = null;
		if(mMessageInput != null){
			recordStr = mMessageInput.getInputString();
			prop = mMessageInput.getMessageHeadProp();
			mMessageInput.nextLine();
		}
		setMessageHeadProperties(textMsg,prop);
		if((mConstPayloadStr != null) && (recordStr != null)){
			recordStr = mConstPayloadStr + recordStr;
		}
		//改行文字列を含んでいれば、改行コードに置き換える。
		String lineSep = System.getProperty("line.separator");
		String payloadStr = StringOperator.replaceString(recordStr,mNewLineDefinition,lineSep);
		setPayload(textMsg,payloadStr);
		return textMsg;
	}
	
	protected void setPayload(TextMessage msg,String str){
		if(str == null){
			return;
		}
		try{
			msg.setText(str);
		}
		catch(JMSException e){
			throw new ServiceException("MESSAGERESOURCEFACTOR105",
										"PayLoad Setting failed.",e);
		}
	}
	 
	 protected void sendPayloadParse(Element elem,boolean fileSpecifiedFlag) {
	 	//データファイルにペイロードを記述するときの、改行文字列定義を探す
	 	NodeList newLineDefs = elem.getElementsByTagName("NEWLINEDEF_TAG_NAME");
	 	if(newLineDefs.getLength() > 1){
			throw new ServiceException("MESSAGERESOURCEFACTORY103","<" +NEWLINEDEF_TAG_NAME + ">" + 
										"is can be exists only one.");
	 	}
	 	//改行文字列定義があれば、1回だけこのループが回る。
	 	for(int rCnt=0;rCnt<newLineDefs.getLength();rCnt++){
	 		Element newLineDef = (Element)newLineDefs.item(rCnt);
	 		mNewLineDefinition = MessageResourceUtil.getValue(newLineDef);
	 	}
		NodeList list = elem.getElementsByTagName(PAYLOAD_TAG_NAME);
		if (list.getLength() > 1){
			//ペイロードタグが複数指定されていたらException発生
			throw new ServiceException("MESSAGERESOURCEFACTORY102","<" +PAYLOAD_TAG_NAME + ">" + 
										"is can be exists only one.");
		}
		for(int rCnt=0;rCnt<list.getLength();rCnt++){
			//ペイロードタグが存在する場合、このループは1回だけ実行される。
			Element payLoad = (Element)list.item(rCnt);
			NodeList payLoadItems = payLoad.getElementsByTagName(PAYLOAD_ITEM);
			if (payLoadItems.getLength() > 1){
			   throw new ServiceException("MESSAGERESOURCEFACTORY103","<" +PAYLOAD_ITEM + ">" + 
										   "is can be exists only one.");
			}
			for(int rCount=0;rCount<payLoadItems.getLength();rCount++){
				//アイテムタグが存在する場合、このループは1回だけ実行される。
				Element payLoadItem = (Element)payLoadItems.item(rCount);
				//値を取得
				mConstPayloadStr = MessageResourceUtil.getValue(payLoadItem);
			}
		}
	 }
	 
	 protected void recvPayloadParse(Element elem){
	 	//TextMessageの場合、必要なし。RecvDataエレメントになにか記述されていても無視される
	 	; 	
	 }
	 
	 
}
