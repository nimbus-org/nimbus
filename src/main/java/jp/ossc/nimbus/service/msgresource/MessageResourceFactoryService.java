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
 * 電文リソースファクトリサービス
 * この生成されたコメントの挿入されるテンプレートを変更するため
 * ウィンドウ > 設定 > Java > コード生成 > コードとコメント
 */
public class MessageResourceFactoryService
	extends ServiceBase
	implements MessageResourceFactoryServiceMBean, 
				MessageResourceFactory,MessageResourceDefine,
				DisplayConstructer{
	
    private static final long serialVersionUID = -9078293076843372913L;
    
    //メンバ変数
	/** 電文リソース定義ファイル格納ディレクトリ */
	private String mDefineFileDir = null;
	/** 電文リソース定義ファイル拡張子 */
	private String mDefFileExt = null;
	/** 電文リソースオブジェクトを保持するハッシュ */
	private HashMap mMsgResObjectHash = null;
	/** ディスプレイ名称のArrayList */
	private ArrayList mMsgResArrayList = null;
	/** ByteConverterFactoryの名前 */
	private ServiceName mByteConverterFactoryName = null;
    /** ByteConverterFactory */
    private ByteConverterFactory mByteConverterFactory = null;
	/** ByteConverter */
	private ByteConverter mByteConverter = null;
	/**
	 * コンストラクタ
	 */
	public MessageResourceFactoryService(){
		mMsgResObjectHash = new HashMap();
		mMsgResArrayList = new ArrayList();
	}
	/** 
	 * 電文リソースオブジェクト取得メソッド
	 */
	public MessageResource findInstance(String key) {
		return (MessageResource)mMsgResObjectHash.get(key);
	}
	/**
	 * 電文リソースサービスから電文リソースオブジェクトを取得する
	 * 際のキーとして有効であれば、trueを返す。
	 */
	public String check(String val){
		if(mMsgResObjectHash.containsKey(val)){
			return val;
		}
		else return null;
	}
	/**
	 * 電文リソースサービスが保持する電文リソース一覧の表示メソッド
	 * @return
	 */
	public String display(){
		StringBuilder ret = new StringBuilder();
		//実装する。
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
	 * 定義ファイル格納ディレクトリのセッター
	 * @param dir
	 */
	public void setDefineFileDir(String dir){
		mDefineFileDir = dir;
	}
	/** 
	 * 定義ファイル格納ディレクトリのゲッター
	 * 
	 * @return
	 */
	public String getDefineFineDir(){
		return mDefineFileDir;
	}
	/**
	 * 定義ファイル拡張子のセッター
	 *
	 */
	public void setDefineFileExt(String ext){
		mDefFileExt = ext;
	}
	/**
	 * 定義ファイル拡張子のゲッター
	 *
	 */
	public String getDefineFileExt(){
		return mDefFileExt;
	}
	
	/**
	 * バイトコンバータサービス名のセッター
	 * 
	 */
	public void setByteConverterServiceName(ServiceName name){
		mByteConverterFactoryName = name;
	}
	
    /**
     * ByteConverterFactoryを設定します。
     */
	public void setByteConverterFactory(ByteConverterFactory byteConverterFactory) {
        mByteConverterFactory = byteConverterFactory;
    }
    
    /**
	 * サービス起動
	 * 1.バイトコンバータサービス取得
	 * 2.電文リソース定義ファイルを読み込む
	 * 
	 */	
	public void startService(){
		//バイトコンバーターサービス取得
        if(mByteConverterFactoryName != null) {
            mByteConverterFactory = (ByteConverterFactory)ServiceManagerFactory.getService(mByteConverterFactoryName);
        }
        mByteConverter = mByteConverterFactory.findConverter(0);
		//ファイルリスト作成
		File DefDir = new File(mDefineFileDir);
		ExtentionFileFilter filter = new ExtentionFileFilter(mDefFileExt);
		File[] defFileList = DefDir.listFiles(filter);
		//すべての定義ファイルをOpen
		if(defFileList != null){
			for(int rCnt=0;rCnt<defFileList.length;rCnt++){
					loadXMLDefinition(defFileList[rCnt]);
			}
		}
	}
	/**
	 * XML定義ファイルを読み込む
	 * @param xmlfile
	 */
	protected void loadXMLDefinition(File xmlfile){
		//rootエレメントを取得
		Element root = null;
		try{
			root = getRoot(xmlfile);
		}
		catch(Exception e){
			e.printStackTrace();
			throw new ServiceException("MESSAGERESOURCEFACTORY001","Fail to get Root Element.",e) ;				
		}
		// Messageエレメントを取得
		NodeList MessageList = root.getElementsByTagName(MESSAGE_TAG_NAME);
		// 定義されているメッセージの数ループする。
		for(int rCnt=0;rCnt<MessageList.getLength();rCnt++){
			Element msgElement = (Element)MessageList.item(rCnt);
			//display属性を取得
			String disp = MessageResourceUtil.getAttMustBeSpecified(msgElement,DISP_ATT);
			//selectKey属性を取得
			String key = MessageResourceUtil.getAttMustBeSpecified(msgElement,SELECT_KEY_ATT);
			//メッセージリソースオブジェクトを生成		
			MessageResourceOperator msgResource = new MessageResourceImpl();
			//キー属性を設定
			msgResource.setKey(key);
			//ディスプレイ用文字列を設定
			msgResource.setDisplayMessage(disp);
			//メッセージリソースオブジェクトに、BLフローキーを設定
			setMsgResBLFlowKeys(msgResource,msgElement);
			//SendData設定
			setMsgResFormatData(msgResource,msgElement,"send");
			//RecvData設定
			setMsgResFormatData(msgResource,msgElement,"recv");
			//電文リソースオブジェクトをハッシュに登録
			mMsgResObjectHash.put(key,msgResource);
			//Display名称ハッシュに、display名称を登録
			mMsgResArrayList.add(msgResource);		
		}
	}

	

	/**
	 * ルートエレメントを取得するメソッド
	 */
	protected Element getRoot(File xmlfile) throws Exception{
		// ドキュメントビルダーファクトリを生成
		DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
		// ドキュメントビルダーを生成
		DocumentBuilder builder = dbfactory.newDocumentBuilder();
		// パースを実行してDocumentオブジェクトを取得
		Document doc = builder.parse(xmlfile);
		// ルート要素を取得
		return doc.getDocumentElement();
	}
	
	
	protected void setMsgResBLFlowKeys(MessageResourceOperator msg,Element element){
		//BLFlowエレメントを取得
		NodeList blFlowList = element.getElementsByTagName(MessageResourceDefine.BLFLOW_TAG_NAME);
		for(int rCnt=0;rCnt<blFlowList.getLength();rCnt++){
			Element blFlow = (Element)blFlowList.item(rCnt);
			//ハッシュへの登録キーを取得
			String name = MessageResourceUtil.getAttMustBeSpecified(blFlow,BLFLOW_ATT_NAME);
			//BLフローキーを取得
			String value = MessageResourceUtil.getValueMustbeSpecified(blFlow);
			//ハッシュに追加
			msg.addBLFlowKey(name,value);
		}
	}
	
	protected void setMsgResFormatData(MessageResourceOperator msg,
										Element element,
										String kind){
		//送受信判定
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
			//2個以上定義があるのはおかしい
			throw new ServiceException("MESSAGERESOURCEFACTORY005",tag + 
											" must be specified only one or not be specified.");
		}	
		
		if (definedNum != 0){
			Element Data = (Element)DataList.item(0);
			String JmsMsgType = MessageResourceUtil.getAttMustBeSpecified(Data,DATATAG_ATT_NAME);
			MessageFormat messageFormat = null;
			if(JmsMsgType.equals(JMSTEXTMSG)){
				//TextMessageFormatのインスタンスを生成	
				messageFormat = new TextMessageFormat(mByteConverter);
			}
			else if(JmsMsgType.equals(JMSBYTESMSG)){
				//BytesMessageFormatのインスタンスを生成
				messageFormat = new BytesOrStreamMessageFormat(mByteConverter,"Bytes");
			}
			else if(JmsMsgType.equals(JMSOBJECTMSG)){
				//ObjectMessageFormatのインスタンスを生成
				messageFormat = new ObjectMessageFormat(mByteConverter);
			}
			else if(JmsMsgType.equals(JMSSTREAMMSG)){
				//StreamMessageFormatのインスタンスを生成
				messageFormat = new BytesOrStreamMessageFormat(mByteConverter,"Stream");
			}
			else if(JmsMsgType.equals(JMSMAPMSG)){
				///MapMessageFormatのインスタンスを生成
				messageFormat = new MapMessageFormat(mByteConverter);
			}
			else{
				//不正な属性指定
				throw new ServiceException("MESSAGERESOURCEFACTORY004","[" + JmsMsgType + "]  is invalid as JMS Message Type.");

			}
			//生成したMessageFormat実装クラスのインスタンスにパースさせる。
			messageFormat.parse(Data);
			//MessageFormat実装クラスを電文リソースオブジェクトにaddする。
			msg.setMessageFormat(messageFormat,kind);
		}
		

	}
	
	public ServiceName getByteConverterServiceName(){
		return mByteConverterFactoryName;
	}
	


}
