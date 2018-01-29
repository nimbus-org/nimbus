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
import java.lang.reflect.*;
import java.beans.*;
import org.w3c.dom.*;

import javax.jms.*;
import javax.jms.QueueSession;

import jp.ossc.nimbus.lang.*;
import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.byteconvert.*;
import jp.ossc.nimbus.util.*;

/**
 *	Object���b�Z�[�W�t�H�[�}�b�g
 *	@author	y-tokuda
 *	@version	1.00 �쐬�F2003/11/08�| y-tokuda<BR>
 *				�X�V�F
 */
public class ObjectMessageFormat
	extends CommonMessageFormat
	implements MessageResourceDefine {
    
    //�����o�ϐ�
	/** ���b�Z�[�W�C���v�b�g */
	//CommonMessageFormat�Œ�`
	//private MessageInput mMessageInput;
	/** �N���X�� */
	private String mClassName;
	/** �y�C���[�h�̑����ێ� */
	private ArrayList mPayloadObjAttributes;
	/** ���\�b�h�}�b�v */
	private HashMap mMethodMap;
	/** �Z�p���[�^��Escape���� */
	private String mEscapeChar = null;

	/* (�� Javadoc)
	 * @see jp.ossc.nimbus.service.msgresource.MessageFormat#marshal(javax.jms.Message)
	 */
	public ObjectMessageFormat(ByteConverter converter){
		super(converter);
		mPayloadObjAttributes = new ArrayList();
		mMethodMap = new HashMap();
	}
	public String marshal(Message msg) {
		if(!(msg instanceof ObjectMessage)){
			return null;
		}
		//�v���p�e�B��
		StringBuilder ret = new StringBuilder("[property] ");
		ret.append(dumpProperties(msg));
		ret.append(" [payload] ");
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		Object obj = null;
		ObjectMessage objMsg = (ObjectMessage)msg;
		try{
			obj = objMsg.getObject();
		}
		catch(JMSException e){
			throw new ServiceException();
		}
		ret.append(obj.toString());
		return ret.toString();
	}

	/* (�� Javadoc)
	 * @see jp.ossc.nimbus.service.msgresource.MessageFormat#unMarshal(javax.jms.QueueSession)
	 */
	 public Message unMarshal(QueueSession session) {
		 Message msg = null;
		 try{
			msg = session.createObjectMessage();
		 }
		 catch(Exception e){
			 throw new ServiceException("MESSAGERESOURCEFACTORY400",
										 "createObjectMessage() failed.",e);
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
	
	protected void setPayload(Message msg,String recordStr){
		//�C���X�^���X�𐶐�
		ObjectMessage objMsg = (ObjectMessage)msg;
		Object obj = null;
		Class clazz = null;
		try{
			clazz = Class.forName(
				mClassName,
				true,
				NimbusClassLoader.getInstance()
			);
		}
		catch(Exception e){
			throw new ServiceException("MESSAGERESOURCEFACTORY401",
										"setPayload() failed. Class not Found.",e);
		}	
		try{
			obj = clazz.newInstance();
		}
		catch(Exception e){
			throw new ServiceException("MESSAGERESOURCEFACTORY402",
										"setPayload() failed. Instancing failed.",e);
		}	
		//�Z�b�^�[���N��
		try{
			//System.out.println("now invoke settter methods.");
			invokeSetterMethods(clazz,obj,recordStr);
		}
		catch(Exception e){
			throw new ServiceException("MESSAGERESOURCEFACTORY402",
										"settter invocation failed.",e);
		}	
		//JMSObjectMessage�ɃZ�b�g
		try{
			objMsg.setObject((Serializable)obj);
		}
		catch(Exception e){
			throw new ServiceException("MESSAGERESOURCEFACTORY402",
										"ObjectMessage's setObject() Method failed.",e);
		}
	}
	
	
	private void invokeSetterMethods(Class clazz,Object obj,String recordStr)
		throws InvocationTargetException,IllegalAccessException{
		final Iterator attrs = mPayloadObjAttributes.iterator();
		while(attrs.hasNext()){
			final PayloadAttribute attr = (PayloadAttribute)attrs.next();
			final String name = attr.getName();
			String valStr = attr.getValue();
			if(attr.useFile()){
				//�t�@�C���w��
				CsvArrayList elems = new CsvArrayList();
				if(mEscapeChar != null){
					elems.setEscapeString(mEscapeChar);
				}
				elems.split(recordStr);
				int index = 0;
				try{
					index = Integer.parseInt(valStr);
					valStr = (String)elems.get(index);
				}
				catch(IndexOutOfBoundsException e){
					throw new ServiceException("MESSAGERESOURCEFACTORY403","Data File does not have column["
													+ index + "]");
				}
				catch(NumberFormatException e){
					throw new ServiceException("MESSAGERESOURCEFACTORY404",valStr + " is not recognized as number.");
				}
				
			}
			final String setterName = "set" + name.toUpperCase().charAt(0)
				 + name.substring(1);
			final Method method = (Method)mMethodMap.get(setterName);
			Class[] paramTypes = method.getParameterTypes();
			Object val = convertStringToObject(paramTypes[0],valStr);
			if(method != null){
				//�Z�b�^�[��invoke
				method.invoke(obj,new Object[]{val});
			}
		}
	}
	
	private Object convertStringToObject(Class clazz,String valStr){
		final PropertyEditor editor = PropertyEditorManager.findEditor(clazz);
		if(editor == null){
			throw new ServiceException("MESSAGERESOURCEFACTORY016","Not Found PropertyEditor Class is "
										 +clazz.getName()); 
		}
		editor.setAsText(valStr);
		return editor.getValue();
	}
	
	protected void sendPayloadParse(Element elem,boolean fileSpecifiedFlag) {
		NodeList escapeChars = elem.getElementsByTagName(ESCAPECHAR_TAG_NAME);
		if(escapeChars.getLength() > 1){
			throw new ServiceException("MESSAGERESOURCEFACTORY012","<" + ESCAPECHAR_TAG_NAME + "> must be specified at least one (and only).");
		}
		for(int rCnt=0;rCnt<escapeChars.getLength();rCnt++){
			Element escapeChar = (Element)escapeChars.item(rCnt);
			mEscapeChar = MessageResourceUtil.getValue(escapeChar);
		}
		NodeList list = elem.getElementsByTagName(PAYLOAD_TAG_NAME);
		if (list.getLength() != 1){
			//�y�C���[�h�̒�`����
			throw new ServiceException("MESSAGERESOURCEFACTORY012","<" + PAYLOAD_TAG_NAME + "> must be specified at least one (and only one).");
		}
		Element payLoad = (Element)list.item(0);
		//�N���X���̎擾
		NodeList classNames= payLoad.getElementsByTagName(PAYLOAD_CLASS_NAME);
		if( classNames.getLength() !=1 ){
			throw new ServiceException("MESSAGERESOURCEFACTORY012",
										"<" + PAYLOAD_CLASS_NAME + "> Must be specifiled at least one (and only only one).");
		}
		Element className = (Element)classNames.item(0);
		mClassName = MessageResourceUtil.getValueMustbeSpecified(className);
		setMethodMap(mClassName);
		//
		NodeList payLoadAttributes = payLoad.getElementsByTagName(PAYLOAD_ATTRIBUTE);
		for(int rCnt=0;rCnt<payLoadAttributes.getLength();rCnt++){
			String name = null;
			String val = null;
			boolean useFile = false;
			Element payloadAttribute = (Element)payLoadAttributes.item(rCnt);
			//���O���擾
			name = MessageResourceUtil.getAttMustBeSpecified(payloadAttribute,PAYLOAD_ATTRIBUTE_NAME_ATT);
			//�l���擾
			val = MessageResourceUtil.getValueMustbeSpecified(payloadAttribute);
			//�t�@�C�����Q�Ƃ��邩�ǂ������擾
			String resourceType = payloadAttribute.getAttribute(PAYLOAD_ATTRIBUTE_RESTYPE_ATT);
			if(resourceType.equals(FILE_VAL)){
				if(fileSpecifiedFlag){
					useFile = true;
				}
				else{
					//�f�[�^�t�@�C�����Q�Ƃ���悤�Ɏw�肳��Ă��Ȃ��ꍇ�t�@�C�����Q�Ƃ���item�͒�`�ł��Ȃ�
					throw new ServiceException("MESSAGERESOURCEFACTORY015","File not specified. But " 
											+ PAYLOAD_ITEM_TYPE_ATT + " has " + FILE_VAL + "attribute.");
				}
			}
			mPayloadObjAttributes.add(new PayloadAttribute(name,val,useFile));	
		}
	}
	
	protected void setMethodMap(String className){
		Class clazz;
		try{
			clazz = Class.forName(
				className,
				true,
				NimbusClassLoader.getInstance()
			);
		}
		catch(ClassNotFoundException e){
			throw new ServiceException("MESSAGERESOURCEFACTORY016","Class Not Found. Class name is " + className);
		}
		Method[] methods = clazz.getMethods();
		for(int i = 0; i < methods.length; i++){
			mMethodMap.put(methods[i].getName(), methods[i]);
		}
			
	}
	
	protected void recvPayloadParse(Element elem){
		//�Ȃɂ����Ȃ�
		;
	}
	private class PayloadAttribute{
		//�����o�ϐ�
		private String mName;
		private String mVal;
		private boolean mUseFile;
		/**
		 * �R���X�g���N�^
		 *	
		 */
		public PayloadAttribute(String name,String val,boolean useFile){
			mName = name;
			mVal = val;
			mUseFile = useFile;
			
		}
		/** 
		 * ���O�̃Q�b�^�[
		 *	
		 */
		public String getName(){
			return mName;
		}
		/**
		 * �l�̃Q�b�^�[
		 *	
		 */
		public String getValue(){
			return mVal;
		}
		/**
		 * �t�@�C���̒l���Q�Ƃ��邩�ǂ���
		 *	
		 */
		public boolean useFile(){
			return mUseFile;
		}
	}
	

}
