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
import javax.jms.QueueSession;
import jp.ossc.nimbus.service.byteconvert.*;
import jp.ossc.nimbus.lang.*;
import org.w3c.dom.*;
import java.util.*;
import jp.ossc.nimbus.util.*;
/**
 *	Map���b�Z�[�W�t�H�[�}�b�g
 *  JMS MapMessage��String���AMapMessage�C���X�^���X�̐������s���B
 *	@author	y-tokuda
 *	@version	1.00 �쐬�F2003/11/07�| y-tokuda<BR>
 *				�X�V�F
 */
public class MapMessageFormat
	extends CommonMessageFormat
	implements MessageFormat, MessageResourceDefine {
	//�����o�ϐ�
	/** ���b�Z�[�W�C���v�b�g */
	//CommonMessageFormat�Œ�`����B
	//private MessageInput mMessageInput = null; 
	/** �y�C���[�h���ێ� */
	private ArrayList mPayloadItems;
	
	/**
	 * �R���X�g���N�^
	 */
	public MapMessageFormat(ByteConverter converter){
		super(converter);
		mPayloadItems = new ArrayList();
	}

	/* (�� Javadoc)
	 * @see jp.ossc.nimbus.service.msgresource.MessageFormat#marshal(javax.jms.Message)
	 */
	public String marshal(Message msg) {
		if(!(msg instanceof MapMessage)){
			return null;
		}	
		StringBuilder ret = new StringBuilder("[property]");
		//�v���p�e�B����String��
		ret.append(dumpProperties(msg));
		//��؂������
		ret.append("[payload]");
		//�y�C���[�h����String��
		try{
			MapMessage mapMsg = (MapMessage)msg;
			Enumeration names = mapMsg.getMapNames();
			while(names.hasMoreElements()){
				String name = (String)names.nextElement();
				ret.append(name);
				ret.append("=");
				Object value = mapMsg.getObject(name);
				ret.append(value) ;
				ret.append(" ");
			}
		}
		catch(Exception e){
			throw new ServiceException("MESSAGERESOURCEFACTORY201","Fail to marshal PayLoad.");
		}
		return ret.toString();
	}

	/**
	 * JMS���b�Z�[�W�������\�b�h
	 */
	public Message unMarshal(QueueSession session) {
		MapMessage mapMsg = null;
		try{
			mapMsg = session.createMapMessage();
		}
		catch(Exception e){
			throw new ServiceException("MESSAGERESOURCEFACTORY202",
										"PayLoad Setting failed.",e);
		}
		String recordStr = null;
		Properties prop = null;
		if(mMessageInput != null){
			recordStr = mMessageInput.getInputString();
			prop = mMessageInput.getMessageHeadProp();
			mMessageInput.nextLine();
		}
		setMessageHeadProperties(mapMsg,prop);
		setPayload(mapMsg,recordStr);
		return mapMsg;
	}

	protected void recvPayloadParse(Element elem){
		//�Ȃɂ����Ȃ�
		;
	}
	
	protected void sendPayloadParse(Element elem,boolean inputFileExists){
		propKindParse(elem,inputFileExists,	mPayloadItems,PAYLOAD_TAG_NAME);
	}
	
	/**
	 * JMS MAP���b�Z�[�W�̃y�C���[�h�ݒ�
	 * MAP���b�Z�[�W�̃y�C���[�h���̍���(item)�́A�v���p�e�B��item�Ɠ����^�̃C���X�^���X�ł���B
	 */
	protected void setPayload(MapMessage msg,String payloadStr) {
		//�y�C���[�h��String","��split���āA��Properties�̃C���X�^���X�ɋl�߂�
		Properties payloadProp = new Properties();
		CsvArrayList payloadArrayList = new CsvArrayList();
		payloadArrayList.split(payloadStr);
		for(int rCnt=0;rCnt<payloadArrayList.size();rCnt++){
			String keyAndVal = payloadArrayList.getStr(rCnt);
			int pos = keyAndVal.indexOf("=");
			if((pos>0)&&(pos<keyAndVal.length()-1)){
				String key = keyAndVal.substring(0,pos);
				String val = keyAndVal.substring(pos+1);
				payloadProp.put(key,val);				
			}
			else{
				throw new ServiceException("MESSAGERESOURCEFACTORY203",
											"Invalid description on data file. [" + keyAndVal + "]");
			}
			

		}
		
		Iterator Items = mPayloadItems.iterator();
		try{
			while(Items.hasNext()){
				PropItem item = (PropItem)Items.next();
				int type = item.getType();
				String valueStr = null;
				String name = item.getName();
				if(item.useFile()){
					//MessageInput����擾����Properties�I�u�W�F�N�g����l�������o��
					valueStr = (String)payloadProp.get(name);
				}
				else{
					//���l
					valueStr = item.getVal();
				}
				//�^�ɉ������v���p�e�B���̃Z�b�^�[���g���B
				switch(type){
					case TYPE_BYTE:
						byte[] tmp = mByteConverter.hex2byte(valueStr);
						msg.setByte(name,tmp[0]);
						break;
					case TYPE_BYTES:
						tmp = mByteConverter.hex2byte(valueStr);
						msg.setBytes(name,tmp);
						break;
					case TYPE_CHAR:
						msg.setChar(name,valueStr.charAt(0));
						break;
					case TYPE_BOOLEAN:
						Boolean bool = Boolean.valueOf(valueStr);
						msg.setBoolean(name,bool.booleanValue());
						break;
					case TYPE_SHORT:
						msg.setShort(name,Short.parseShort(valueStr));
						break;
					case TYPE_INT:
						msg.setInt(name,Integer.parseInt(valueStr));
						break;
					case TYPE_LONG:
						msg.setLong(name,Long.parseLong(valueStr));
						break;
					case TYPE_FLOAT:
						msg.setFloat(name,Float.parseFloat(valueStr));
						break;
					case TYPE_DOUBLE:
						msg.setDouble(name,Double.parseDouble(valueStr));
						break;
					case TYPE_STRING:
						msg.setString(name,valueStr);
						break;
					case TYPE_OBJECT:
						Object obj = createObject(item.getWrappedType(),valueStr);
						msg.setObject(name,obj);
						break;
					default:
				}
			}
		}
		catch(Exception e){
			throw new ServiceException("MESSAGERESOURCEFACTORY204",
										"Property Setting failed.",e);
		}
		
	}

}
