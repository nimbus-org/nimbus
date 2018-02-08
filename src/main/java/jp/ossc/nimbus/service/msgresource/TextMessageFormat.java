// jp.ossc.nimbus.service.msgresource.TextMessageFormat.java
// Copyright (C) 2002-2005 by Nomura Research Institute,Ltd.  All Rights Reserved.
//
/*****************************************************************************/
/** �X�V����																**/
/** 																		**/
/*****************************************************************************/
// �p�b�P�[�W
package jp.ossc.nimbus.service.msgresource;
// �C���|�[�g
import jp.ossc.nimbus.lang.*;
import javax.jms.*;
import javax.jms.QueueSession;
import org.w3c.dom.*;
import java.util.*;
import jp.ossc.nimbus.service.byteconvert.*;
import jp.ossc.nimbus.util.*;

/**
 *	Text���b�Z�[�W�t�H�[�}�b�g
 *	@author	y-tokuda
 *	@version	1.00 �쐬�F2003/11/06�| y-tokuda<BR>
 *				�X�V�F
 */
public class TextMessageFormat extends CommonMessageFormat
	implements MessageFormat, MessageResourceDefine {
    
    //�����o�ϐ�
	private String mNewLineDefinition = "\\n";
	//private MessageInput mMessageInput = null;
	/** �d���y�C���[�h���l��� */
	private String mConstPayloadStr = null;
	/**
	 * �R���X�g���N�^
	 */
	public TextMessageFormat(ByteConverter converter){
		super(converter);
	}

	/**
	 * JMS���b�Z�[�W���ێ����Ă�����e��String������B
	 */
	public String marshal(Message msg) {
		if(!(msg instanceof TextMessage)){
			return null;
		}
		//�v���p�e�B��
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
	 * JMS���b�Z�[�W�𐶐�����B
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
		//���s��������܂�ł���΁A���s�R�[�h�ɒu��������B
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
	 	//�f�[�^�t�@�C���Ƀy�C���[�h���L�q����Ƃ��́A���s�������`��T��
	 	NodeList newLineDefs = elem.getElementsByTagName("NEWLINEDEF_TAG_NAME");
	 	if(newLineDefs.getLength() > 1){
			throw new ServiceException("MESSAGERESOURCEFACTORY103","<" +NEWLINEDEF_TAG_NAME + ">" + 
										"is can be exists only one.");
	 	}
	 	//���s�������`������΁A1�񂾂����̃��[�v�����B
	 	for(int rCnt=0;rCnt<newLineDefs.getLength();rCnt++){
	 		Element newLineDef = (Element)newLineDefs.item(rCnt);
	 		mNewLineDefinition = MessageResourceUtil.getValue(newLineDef);
	 	}
		NodeList list = elem.getElementsByTagName(PAYLOAD_TAG_NAME);
		if (list.getLength() > 1){
			//�y�C���[�h�^�O�������w�肳��Ă�����Exception����
			throw new ServiceException("MESSAGERESOURCEFACTORY102","<" +PAYLOAD_TAG_NAME + ">" + 
										"is can be exists only one.");
		}
		for(int rCnt=0;rCnt<list.getLength();rCnt++){
			//�y�C���[�h�^�O�����݂���ꍇ�A���̃��[�v��1�񂾂����s�����B
			Element payLoad = (Element)list.item(rCnt);
			NodeList payLoadItems = payLoad.getElementsByTagName(PAYLOAD_ITEM);
			if (payLoadItems.getLength() > 1){
			   throw new ServiceException("MESSAGERESOURCEFACTORY103","<" +PAYLOAD_ITEM + ">" + 
										   "is can be exists only one.");
			}
			for(int rCount=0;rCount<payLoadItems.getLength();rCount++){
				//�A�C�e���^�O�����݂���ꍇ�A���̃��[�v��1�񂾂����s�����B
				Element payLoadItem = (Element)payLoadItems.item(rCount);
				//�l���擾
				mConstPayloadStr = MessageResourceUtil.getValue(payLoadItem);
			}
		}
	 }
	 
	 protected void recvPayloadParse(Element elem){
	 	//TextMessage�̏ꍇ�A�K�v�Ȃ��BRecvData�G�������g�ɂȂɂ��L�q����Ă��Ă����������
	 	; 	
	 }
	 
	 
}
