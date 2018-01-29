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
 *	Bytes(Stream)���b�Z�[�W�t�H�[�}�b�g
 *	@author	y-tokuda
 *	@version	1.00 �쐬�F2003/10/28�| y-tokuda<BR>
 *				�X�V�F
 */
public class BytesOrStreamMessageFormat extends CommonMessageFormat implements MessageFormat,MessageResourceDefine{
    
    //�����o�ϐ�
	/** �d���y�C���[�h���ڂ̏�� */
	private ArrayList mPayLoadItems = null;
	/** ���b�Z�[�W�C���v�b�g */
	//private MessageInput mMessageInput = null;
	/** �o�C�g�R���o�[�^�[�T�[�r�X */
	private ByteConverter mByteConverter = null;
	/** ����JMS���b�Z�[�W�̎�� (Bytes OR Stream */
	private String mMessageType = null;
	//�萔�錾
	/** �^�ƒl�̃Z�p���[�^�i�\�����j */
	final String TYPE_VALUE_SEP = ":";
	/** readBytes���鎞��byte[]�̃T�C�Y */
	final int BUFLEN = 8192;
	/**
	 * �R���X�g���N�^
	 */
	public BytesOrStreamMessageFormat(ByteConverter converter,String msgType){
		super(converter);
		//�o�C�g�R���o�[�^�T�[�r�X�̐ݒ�
		mByteConverter = converter;
		//�y�C���[�h���ڕێ�ArrayList�̏�����
		mPayLoadItems = new ArrayList();
		//�v���p�e�B���ڕێ�ArrayList�̏�����
		mPropertyItems = new ArrayList();
		//���b�Z�[�W��� Bytes OR Stream
		if((msgType.equals("Bytes")) || (msgType.equals("Stream"))){
			mMessageType = msgType;
		}
		else{
			throw new ServiceException("MESSAGERESOURCEFACTORY015","Must be Specified Bytes OR Stream");
		}
	}
	/**
	 *JMS���b�Z�[�W���ێ��������String������
	 */
	public String marshal(Message msg) {
		if( (!(msg instanceof BytesMessage)) && (!(msg instanceof StreamMessage)) ){
			return null;
		}
		byte[] readBytesBuffer = new byte[BUFLEN];
		StringBuilder retMsg = new StringBuilder();
		//���b�p�[JMS���b�Z�[�W��ݒ�
		MessageWrapper msgWrapper = new MessageWrapper(msg);
		try{
			//�v���p�e�B����String��
			retMsg.append(dumpProperties(msg));
			//�y�C���[�h����String��
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
						//�f�[�^��ǂݍ���					
						while(true){
							readNum = msgWrapper.readBytes(readBytesBuffer);
							if(readNum == -1){
								//����ȏ�f�[�^�͂Ȃ��B�I��
								break;
							}
							else{
								//�f�[�^�𕶎��񉻂���B
								for(int rCnt=0;rCnt<readNum;rCnt++){
									tmp = (int)readBytesBuffer[rCnt];
									retMsg.append("0x");
									retMsg.append(Integer.toHexString(tmp));
									retMsg.append(" ");
								}
								if(readNum == BUFLEN){
									//�������ǂݍ��ޕK�v����B
									
								}
								else{
									//�I��
									break;//while���[�v���u���[�N
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
						//wrappedType�ɉ�����
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
	 * JMS���b�Z�[�W�̃y�C���[�h�ݒ�
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
					//�y�C���[�h������(�J���}��؂�j����l��\����������擾����B
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
					//���l
					valueStr = item.getValue();
				}
				//�^�ɉ�����write���\�b�h���g��
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
	 * JMS���b�Z�[�W�������\�b�h
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
			//�y�C���[�h�̒�`����
			throw new ServiceException("MESSAGERESOURCEFACTORY011","Not Found " + 
										"<" + PAYLOAD_TAG_NAME + ">");
		}
		//�ŏ��ɒ�`����Ă���y�C���[�h���g���B���ɕ�����`����Ă��Ă���������B
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
				//�L���Ȍ^�w��ł͂Ȃ��B
				throw new ServiceException("MESSAGERESOURCEFACTORY012","Invalid Type :" + type); 										
			}
			mPayLoadItems.add(new PayLoadItem(typeCode,wrappedTypeCode));	
		}
	}
	protected void sendPayloadParse(Element elem,boolean fileSpecifiedFlag) {
		NodeList list = elem.getElementsByTagName(PAYLOAD_TAG_NAME);
		if (list.getLength() < 1){
			//�y�C���[�h�̒�`����
			throw new ServiceException("MESSAGERESOURCEFACTORY012","Not Found " + 
										"<" + PAYLOAD_TAG_NAME + ">");
		}
		//�ŏ��ɒ�`����Ă���y�C���[�h���g���B���ɕ�����`����Ă��Ă���������B
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
					//�L���Ȍ^�w��ł͂Ȃ��B
					throw new ServiceException("MESSAGERESOURCEFACTORY012","Invalid Wrapped Type :" + wrappedType); 										
				}
			}
			//�l���擾
			val = MessageResourceUtil.getValueMustbeSpecified(payLoadItem);
			//�t�@�C�����Q�Ƃ��邩�ǂ������擾
			String resourceType = payLoadItem.getAttribute(PAYLOAD_RES_TYPE_ATT);
			if(resourceType.equals(FILE_VAL)){
				if(fileSpecifiedFlag){
					useFile = true;
				}
				else{
					//�f�[�^�t�@�C�����Q�Ƃ���悤�Ɏw�肳��Ă��Ȃ��ꍇ�t�@�C�����Q�Ƃ���item�͒�`�ł��Ȃ�
					throw new ServiceException("MESSAGERESOURCEFACTORY015","File not specified. But " 
											+ PAYLOAD_ITEM_TYPE_ATT + " has " + FILE_VAL + " attribute.");
				}
			}
			int typeCode = getWriteTypeCode(type,mMessageType);
			//�^��`�����̒l�`�F�b�N
			if(typeCode < 0){
				//�L���Ȍ^�w��ł͂Ȃ��B
				throw new ServiceException("MESSAGERESOURCEFACTORY012","Invalid Type :" + type); 										
			}
			mPayLoadItems.add(new PayLoadItem(typeCode,wrappedTypeCode,val,useFile));	
		}
	}	
	private class PayLoadItem{
		//�����o�ϐ�
		/** int,short,Object���A�^��� */
		int mType;
		/** mType��Object�̎��A���ۂ̌^�����̕ϐ��Ɏw�肷��B */
		int mWrappedType;
		/** XML��`�t�@�C���ɑ��l�������Ă���Ƃ��A���̕ϐ��ɑ��l��ݒ肷��B */
		String mVal;
		/** ���\�[�X��� "file"��"direct"*/
		boolean mFileRefFlag;
		/** 
		 * �R���X�g���N�^
		 * �l��null,�t�@�C���Q�ƃt���O��false�ɂ���B
		 */
		public PayLoadItem(int type,int wrappedtype){
			mType = type;
			mWrappedType = wrappedtype;
			mVal = null;
			mFileRefFlag = false;	
		}
		/**
		 * �R���X�g���N�^
		 *	
		 */
		public PayLoadItem(int type,int inctype,String val,boolean fileRefFlag){
			mType = type;
			mWrappedType = inctype;
			mVal = val;
			mFileRefFlag = fileRefFlag;
		}
		/** Type�̃Q�b�^�[ */
		public int getType(){
			return mType;
		}
		/** ���b�v����Ă��� Type�̃Q�b�^�[ */
		public int getWrappedType(){
			return mWrappedType;
		}
		/** XML�t�@�C���ɋL�q���ꂽ���l�̃Q�b�^�[ */
		public String getValue(){
			return mVal;
		}
		/** �f�[�^�t�@�C�����g�p���邩�ǂ����̃t���O */
		public boolean UseFile(){
			return mFileRefFlag;
		}
	}
	/**
	 * BytesMessage��StreamMessage�̃��b�p�[
	 * 
	 */
	private class MessageWrapper{
		//�����o�ϐ�
		Message mMessage;
		/**
		 * �R���X�g���N�^
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
		 * readByte�̃��b�p�[
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
		 * readBytes�̃��b�p�[
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
		 * readBoolean�̃��b�p�[
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
		 * readChar�̃��b�p�[
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
		 * readShort�̃��b�p�[
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
		 * readUnsignedShort�̃��b�p�[
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
		 * readInt�̃��b�p�[
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
		 * readLong�̃��b�p�[
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
		 * readFloat�̃��b�p�[
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
		 * readDouble�̃��b�p�[
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
		 * readUTF�̃��b�p�[
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
		 * readString�̃��b�p�[
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
		 * readObject�̃��b�p�[
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
		 * writeByte�̃��b�p�[
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
		 * writeBytes�̃��b�p�[
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
		 * writeBoolean�̃��b�p�[
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
		 * writeChar�̃��b�p�[
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
		 * writeShort�̃��b�p�[
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
		 * writeInt�̃��b�p�[
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
		 * writeLong�̃��b�p�[
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
		 * writeFloat�̃��b�p�[
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
		 * writeDouble�̃��b�p�[
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
		 * writeUTF�̃��b�p�[
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
		 * writeString�̃��b�p�[
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
		 * writeObject�̃��b�p�[
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