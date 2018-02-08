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

import jp.ossc.nimbus.core.*;
import java.util.*;
import java.io.*;
import jp.ossc.nimbus.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import jp.ossc.nimbus.lang.*;
import jp.ossc.nimbus.service.byteconvert.*;
import jp.ossc.nimbus.service.cui.*;

/**
 * @author y-tokuda
 * �d�����\�[�X�t�@�N�g���T�[�r�X
 * ���̐������ꂽ�R�����g�̑}�������e���v���[�g��ύX���邽��
 * �E�B���h�E > �ݒ� > Java > �R�[�h���� > �R�[�h�ƃR�����g
 */
public class MessageResourceFactoryService
	extends ServiceBase
	implements MessageResourceFactoryServiceMBean, 
				MessageResourceFactory,MessageResourceDefine,
				DisplayConstructer{
	
    private static final long serialVersionUID = -9078293076843372913L;
    
    //�����o�ϐ�
	/** �d�����\�[�X��`�t�@�C���i�[�f�B���N�g�� */
	private String mDefineFileDir = null;
	/** �d�����\�[�X��`�t�@�C���g���q */
	private String mDefFileExt = null;
	/** �d�����\�[�X�I�u�W�F�N�g��ێ�����n�b�V�� */
	private HashMap mMsgResObjectHash = null;
	/** �f�B�X�v���C���̂�ArrayList */
	private ArrayList mMsgResArrayList = null;
	/** ByteConverterFactory�̖��O */
	private ServiceName mByteConverterFactoryName = null;
    /** ByteConverterFactory */
    private ByteConverterFactory mByteConverterFactory = null;
	/** ByteConverter */
	private ByteConverter mByteConverter = null;
	/**
	 * �R���X�g���N�^
	 */
	public MessageResourceFactoryService(){
		mMsgResObjectHash = new HashMap();
		mMsgResArrayList = new ArrayList();
	}
	/** 
	 * �d�����\�[�X�I�u�W�F�N�g�擾���\�b�h
	 */
	public MessageResource findInstance(String key) {
		return (MessageResource)mMsgResObjectHash.get(key);
	}
	/**
	 * �d�����\�[�X�T�[�r�X����d�����\�[�X�I�u�W�F�N�g���擾����
	 * �ۂ̃L�[�Ƃ��ėL���ł���΁Atrue��Ԃ��B
	 */
	public String check(String val){
		if(mMsgResObjectHash.containsKey(val)){
			return val;
		}
		else return null;
	}
	/**
	 * �d�����\�[�X�T�[�r�X���ێ�����d�����\�[�X�ꗗ�̕\�����\�b�h
	 * @return
	 */
	public String display(){
		StringBuffer ret = new StringBuffer();
		//��������B
		Iterator msgResObjects = mMsgResArrayList.iterator();
		while(msgResObjects.hasNext()){
			MessageResourceOperator msgResObj = (MessageResourceOperator)msgResObjects.next();
			ret.append(msgResObj.getKey());
			ret.append(" ");
			ret.append(msgResObj.display());
			ret.append("\t");
		}
		return ret.toString();
	}
	/**
	 * ��`�t�@�C���i�[�f�B���N�g���̃Z�b�^�[
	 * @param dir
	 */
	public void setDefineFileDir(String dir){
		mDefineFileDir = dir;
	}
	/** 
	 * ��`�t�@�C���i�[�f�B���N�g���̃Q�b�^�[
	 * 
	 * @return
	 */
	public String getDefineFineDir(){
		return mDefineFileDir;
	}
	/**
	 * ��`�t�@�C���g���q�̃Z�b�^�[
	 *
	 */
	public void setDefineFileExt(String ext){
		mDefFileExt = ext;
	}
	/**
	 * ��`�t�@�C���g���q�̃Q�b�^�[
	 *
	 */
	public String getDefineFileExt(){
		return mDefFileExt;
	}
	
	/**
	 * �o�C�g�R���o�[�^�T�[�r�X���̃Z�b�^�[
	 * 
	 */
	public void setByteConverterServiceName(ServiceName name){
		mByteConverterFactoryName = name;
	}
	
    /**
     * ByteConverterFactory��ݒ肵�܂��B
     */
	public void setByteConverterFactory(ByteConverterFactory byteConverterFactory) {
        mByteConverterFactory = byteConverterFactory;
    }
    
    /**
	 * �T�[�r�X�N��
	 * 1.�o�C�g�R���o�[�^�T�[�r�X�擾
	 * 2.�d�����\�[�X��`�t�@�C����ǂݍ���
	 * 
	 */	
	public void startService(){
		//�o�C�g�R���o�[�^�[�T�[�r�X�擾
        if(mByteConverterFactoryName != null) {
            mByteConverterFactory = (ByteConverterFactory)ServiceManagerFactory.getService(mByteConverterFactoryName);
        }
        mByteConverter = mByteConverterFactory.findConverter(0);
		//�t�@�C�����X�g�쐬
		File DefDir = new File(mDefineFileDir);
		ExtentionFileFilter filter = new ExtentionFileFilter(mDefFileExt);
		File[] defFileList = DefDir.listFiles(filter);
		//���ׂĂ̒�`�t�@�C����Open
		if(defFileList != null){
			for(int rCnt=0;rCnt<defFileList.length;rCnt++){
					loadXMLDefinition(defFileList[rCnt]);
			}
		}
	}
	/**
	 * XML��`�t�@�C����ǂݍ���
	 * @param xmlfile
	 */
	protected void loadXMLDefinition(File xmlfile){
		//root�G�������g���擾
		Element root = null;
		try{
			root = getRoot(xmlfile);
		}
		catch(Exception e){
			e.printStackTrace();
			throw new ServiceException("MESSAGERESOURCEFACTORY001","Fail to get Root Element.",e) ;				
		}
		// Message�G�������g���擾
		NodeList MessageList = root.getElementsByTagName(MESSAGE_TAG_NAME);
		// ��`����Ă��郁�b�Z�[�W�̐����[�v����B
		for(int rCnt=0;rCnt<MessageList.getLength();rCnt++){
			Element msgElement = (Element)MessageList.item(rCnt);
			//display�������擾
			String disp = MessageResourceUtil.getAttMustBeSpecified(msgElement,DISP_ATT);
			//selectKey�������擾
			String key = MessageResourceUtil.getAttMustBeSpecified(msgElement,SELECT_KEY_ATT);
			//���b�Z�[�W���\�[�X�I�u�W�F�N�g�𐶐�		
			MessageResourceOperator msgResource = new MessageResourceImpl();
			//�L�[������ݒ�
			msgResource.setKey(key);
			//�f�B�X�v���C�p�������ݒ�
			msgResource.setDisplayMessage(disp);
			//���b�Z�[�W���\�[�X�I�u�W�F�N�g�ɁABL�t���[�L�[��ݒ�
			setMsgResBLFlowKeys(msgResource,msgElement);
			//SendData�ݒ�
			setMsgResFormatData(msgResource,msgElement,"send");
			//RecvData�ݒ�
			setMsgResFormatData(msgResource,msgElement,"recv");
			//�d�����\�[�X�I�u�W�F�N�g���n�b�V���ɓo�^
			mMsgResObjectHash.put(key,msgResource);
			//Display���̃n�b�V���ɁAdisplay���̂�o�^
			mMsgResArrayList.add(msgResource);		
		}
	}

	

	/**
	 * ���[�g�G�������g���擾���郁�\�b�h
	 */
	protected Element getRoot(File xmlfile) throws Exception{
		// �h�L�������g�r���_�[�t�@�N�g���𐶐�
		DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
		// �h�L�������g�r���_�[�𐶐�
		DocumentBuilder builder = dbfactory.newDocumentBuilder();
		// �p�[�X�����s����Document�I�u�W�F�N�g���擾
		Document doc = builder.parse(xmlfile);
		// ���[�g�v�f���擾
		return doc.getDocumentElement();
	}
	
	
	protected void setMsgResBLFlowKeys(MessageResourceOperator msg,Element element){
		//BLFlow�G�������g���擾
		NodeList blFlowList = element.getElementsByTagName(MessageResourceDefine.BLFLOW_TAG_NAME);
		for(int rCnt=0;rCnt<blFlowList.getLength();rCnt++){
			Element blFlow = (Element)blFlowList.item(rCnt);
			//�n�b�V���ւ̓o�^�L�[���擾
			String name = MessageResourceUtil.getAttMustBeSpecified(blFlow,BLFLOW_ATT_NAME);
			//BL�t���[�L�[���擾
			String value = MessageResourceUtil.getValueMustbeSpecified(blFlow);
			//�n�b�V���ɒǉ�
			msg.addBLFlowKey(name,value);
		}
	}
	
	protected void setMsgResFormatData(MessageResourceOperator msg,
										Element element,
										String kind){
		//����M����
		String tag = null;
		if(kind.equals("send")){
			tag = SENDDATA_TAG_NAME;
		}
		else if(kind.equals("recv")){
			tag = RECVDATA_TAG_NAME;
		}
		NodeList DataList = element.getElementsByTagName(tag);
		int definedNum = DataList.getLength();
		
		if ( definedNum > 1){
			//2�ȏ��`������̂͂�������
			throw new ServiceException("MESSAGERESOURCEFACTORY005",tag + 
											" must be specified only one or not be specified.");
		}	
		
		if (definedNum != 0){
			Element Data = (Element)DataList.item(0);
			String JmsMsgType = MessageResourceUtil.getAttMustBeSpecified(Data,DATATAG_ATT_NAME);
			MessageFormat messageFormat = null;
			if(JmsMsgType.equals(JMSTEXTMSG)){
				//TextMessageFormat�̃C���X�^���X�𐶐�	
				messageFormat = new TextMessageFormat(mByteConverter);
			}
			else if(JmsMsgType.equals(JMSBYTESMSG)){
				//BytesMessageFormat�̃C���X�^���X�𐶐�
				messageFormat = new BytesOrStreamMessageFormat(mByteConverter,"Bytes");
			}
			else if(JmsMsgType.equals(JMSOBJECTMSG)){
				//ObjectMessageFormat�̃C���X�^���X�𐶐�
				messageFormat = new ObjectMessageFormat(mByteConverter);
			}
			else if(JmsMsgType.equals(JMSSTREAMMSG)){
				//StreamMessageFormat�̃C���X�^���X�𐶐�
				messageFormat = new BytesOrStreamMessageFormat(mByteConverter,"Stream");
			}
			else if(JmsMsgType.equals(JMSMAPMSG)){
				///MapMessageFormat�̃C���X�^���X�𐶐�
				messageFormat = new MapMessageFormat(mByteConverter);
			}
			else{
				//�s���ȑ����w��
				throw new ServiceException("MESSAGERESOURCEFACTORY004","[" + JmsMsgType + "]  is invalid as JMS Message Type.");

			}
			//��������MessageFormat�����N���X�̃C���X�^���X�Ƀp�[�X������B
			messageFormat.parse(Data);
			//MessageFormat�����N���X��d�����\�[�X�I�u�W�F�N�g��add����B
			msg.setMessageFormat(messageFormat,kind);
		}
		

	}
	
	public ServiceName getByteConverterServiceName(){
		return mByteConverterFactoryName;
	}
	


}
