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
package jp.ossc.nimbus.service.cui;

import jp.ossc.nimbus.beans.*;
import jp.ossc.nimbus.core.*;
import java.util.*;
import java.io.*;
import jp.ossc.nimbus.io.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import jp.ossc.nimbus.lang.*;
/**
 *	コマンド入力サービス
 *	定義ファイルを読み込み、コマンド入力オブジェクトを生成する。
 *	@author	y-tokuda
 *	@version	1.00 作成：2003/10/20－ y-tokuda<BR>
 *				更新：
 */
public class CuiFactoryService
	extends ServiceBase
	implements CuiFactoryServiceMBean,
				CuiTagDefine,CuiFactory {
	
    private static final long serialVersionUID = -3162235897050844253L;
    
    //メンバ変数
	/** コマンド入力定義ファイル配置ディレクトリ　*/
	private String mDefFileDir = null;
	/** コマンド入力定義ファイル拡張子 */
	private String mDefFileExt = null;
	/** コマンド入力オブジェクトのハッシュ */
	private HashMap mCuiHash = null;
	/** Cui実装クラス名 */
	private String  mImplementClassName = null;
	/**
	 * コンストラクター
	 */
	public CuiFactoryService() {
		super();
	}
	/**
	 * CuiOperator実装クラス名セッター
	 */
	public void setImplementClassName(String name){
		mImplementClassName = name;
	}
	/**
	 * CuiOperator実装クラス名ゲッター
	 */
	public String getImplementClassName(){
		return mImplementClassName;
	}	
	/**
	 * 生成
	 */
	public void createService(){
		mCuiHash = new HashMap();
	}
	/**
	 * 起動
	 * 
	 */
	public void startService(){
		//定義ファイルを探し、ロードする。
		File DefDir = new File(mDefFileDir);
		ExtentionFileFilter filter = new ExtentionFileFilter(mDefFileExt);
		File[] defFileList = DefDir.listFiles(filter);
		//すべての定義ファイルをOpen
		if(defFileList != null){
			for(int rCnt=0;rCnt<defFileList.length;rCnt++){
				//パースする。
				parse(defFileList[rCnt]);
			}
		}
	}
	/**
	 * 停止
	 * 
	 * 
	 */
	public void stopService(){	
	}
	/**
	 * 破棄
	 */
	public void destroy(){
		mCuiHash = null;
	}
    /**
     * 外部APにCuiインスタンスを提供する。
     * @param key
     * @return Cui
     */
	public Cui findInstance(String key){
		return (Cui)mCuiHash.get(key);
	}
	/**
	 * コマンド入力定義ファイルディレクトリセッター
	 *
	 */
	public void setDefFileDir(String dir){
		mDefFileDir = dir;
	}
	/**
	 * コマンド入力定義ファイル拡張子セッター
	 *
	 */
	public void setDefFileExtention(String ext){
		mDefFileExt = ext;
	}
	/**
	 * コマンド入力定義ファイルディレクトリゲッター
	 *
	 */
	public String getDefFileDir(){
		return mDefFileDir;
	}
	/**
	 * コマンド入力定義ファイル拡張子ゲッター
	 *
	 */
	public String getDefFileExtention(){
		return mDefFileExt;
	}
	/**
	 * XML定義ファイルパースメソッド
	 */
	public void parse(File xmlFile) {
		Element root = getRoot(xmlFile);
		// dataInputエレメントを取得
		NodeList dataInputList = root.getElementsByTagName(DATAINPUT_TAG);
		// 定義されているdataInputの数ループする。
		for(int rCnt=0;rCnt<dataInputList.getLength();rCnt++){
			Element dataInputElement = (Element)dataInputList.item(rCnt);
			//keyを取得
			String key = dataInputElement.getAttribute(DATAINPUT_TAG_KEY_ATT);
			if (key.length() < 1){
				//致命的エラー
				throw new ServiceException("CUIFACTORY001","attribute name is none. tag name is"+ dataInputElement.getTagName());
			}
			//CuiOperatorオブジェクトを生成
			CuiOperator cuiObj = createCuiOperator();
			//step要素を取得
			NodeList steps = dataInputElement.getElementsByTagName(STEP_TAG);
			for(int rCount=0;rCount<steps.getLength();rCount++){
				Element stepElement = (Element)steps.item(rCount);
				String stepName = stepElement.getAttribute(STEP_TAG_NAME_ATT);
				if(stepName == null){
					//致命的エラー
					throw new ServiceException("CUIFACTORY002","Tag name is "+ stepElement.getTagName());
				}
				//DataInputStepオブジェクトをnew
				DataInputStep dataInputStep = new DataInputStep(stepName);
				//DataInputStepオブジェクトにDisplayオブジェクトをセット
				setDisplayObject(dataInputStep,stepElement); 
				//DataInputStepオブジェクトにInputCheckerオブジェクトセット
				setInputChecker(dataInputStep,stepElement);
				//DataInputStepオブジェクトに遷移先ハッシュをセット
				setWhereToGoHash(dataInputStep,stepElement);
				NodeList ends = stepElement.getElementsByTagName(END_TAG);
				if(ends.getLength() > 1){
					//一つのstep中にend要素が2個以上あったらExceptionをthrow
					throw new ServiceException("CUIFACTORY003",END_MULTI_DEF_ERR + "Tag name is "+ stepElement.getTagName());		
				}
				if(ends.getLength() == 1){
					//end宣言があるので、DataInputObjectの次Stepに終了をセット
					Element endElem = (Element)ends.item(0);
					String type_att = endElem.getAttribute(END_TAG_TYPE_ATT);
					String endMsg = getValueIfSpecified(endElem);
					if( type_att.equals(END_FORCE)){
						//正常終了設定
						dataInputStep.setNextStepName(INTERRUPT);
					}
					else{
						//強制終了設定
						dataInputStep.setNextStepName(END);
					}
					//終了メッセージ設定
					dataInputStep.setEndMessage(endMsg);
				}
				else{
					//end宣言無し
					//ここまでの処理で、DataInputStepオブジェクトに、次Stepが設定されていなければ以下の処理
					//を行う。
					if(dataInputStep.getNextStepName() == null){
						//最後のstepでなければ次に記述されているステップを次ステップにする。
						if(rCount != (steps.getLength() -1) ){
							Element nextStepElem = (Element)steps.item(rCount+1);
							String name_att = nextStepElem.getAttribute(STEP_TAG_NAME_ATT);
							dataInputStep.setNextStepName(name_att);
						}
						else{
							//最後に記述されているステップには、endタグを記述しなくてよいことにする。
							dataInputStep.setNextStepName(END);
						}
					}
				}
			//DataInputStepオブジェクトをCuiオブジェクトに格納する。
			cuiObj.addStep(stepName,dataInputStep);
			}
		//Cuiオブジェクトをハッシュに格納する。
		mCuiHash.put(key,cuiObj);
		}
	}
	/**
	 * ルートエレメントを返す。
	 * @param xmlFile
	 * @return　Element
	 */
	protected Element getRoot(File xmlFile){
		DocumentBuilderFactory dbfactory;
		DocumentBuilder builder;
		Document doc;
		Element root;
		try{
			// ドキュメントビルダーファクトリを生成
			dbfactory = DocumentBuilderFactory.newInstance();
			// ドキュメントビルダーを生成
			builder = dbfactory.newDocumentBuilder();
			// パースを実行してDocumentオブジェクトを取得
			doc = builder.parse(xmlFile);
			// ルート要素を取得
			root = doc.getDocumentElement();
		}
		catch(Exception e){
			throw new ServiceException("CUIFACTORY021","Fail to get Document Root",e);
		}
		return root;
	}
	
	/**
	 * DataInputStepオブジェクトに、ディスプレイオブジェクトを設定する。
	 * @param step
	 * @param stepElem
	 */
	protected void setDisplayObject(DataInputStep step,
									Element stepElem) {
		//display要素を取得
		NodeList displays = stepElem.getElementsByTagName(DISPLAY_TAG);
		//Display TAGをサーチ
		for(int rCnt=0;rCnt<displays.getLength();rCnt++){
			Element display = (Element)displays.item(rCnt);
			String type = display.getAttribute(DISPLAY_TAG_TYPE_ATT);
			if (type.equals(DISPLAY_TYPE_SERVICE)){
				//サービス検索
				String name = getValueMustSpecified(display);
				ServiceNameEditor edit = new ServiceNameEditor() ;
				edit.setAsText(name) ;
				ServiceName serviceName = (ServiceName)edit.getValue();
				DisplayConstructer displayObject = (DisplayConstructer)ServiceManagerFactory.getServiceObject(serviceName) ;
				if (displayObject == null){
					throw new ServiceException("CUIFACTORY022","Service not found. servicename is "+ name);
				}
				//DataInputStepオブジェクトに設定
				step.addDisplay(displayObject);				
			}else{
				//TextDisplayを生成
				TextDisplay textDisplay = new TextDisplay();
				//表示文字をセット
				textDisplay.setDisplayMenu(getValueIfSpecified(display)) ;
				//DataInputStepオブジェクトに設定
				step.addDisplay(textDisplay);
			}
		}
	}
	/**
	 * DataInputStepオブジェクトに、InputChekerオブジェクトを設定する。
	 * @param step
	 * @param stepElem
	 */
	protected void setInputChecker(DataInputStep step,Element stepElem){
		NodeList checkList = stepElem.getElementsByTagName(INPUT_TAG);
		if(checkList.getLength() > 1){
			throw new ServiceException("CUIFACTORY023","input tag dupulicate define");
		}
		for(int rCnt=0;rCnt<checkList.getLength();rCnt++){
			Element inputElem = (Element)checkList.item(rCnt);
			//属性をチェックする。
			String type = inputElem.getAttribute(INPUT_TAG_TYPE_ATT);
			InputChecker checker = null;
			if(type.equals(INPUT_TYPE_SERVICE)){
				//サービスだった場合
				String name = getValueMustSpecified(inputElem);
				ServiceNameEditor edit = new ServiceNameEditor() ;
				edit.setAsText(name) ;
				ServiceName serviceName = (ServiceName)edit.getValue();
				checker = (InputChecker)ServiceManagerFactory.getServiceObject(serviceName) ;
				if (checker == null){
					throw new ServiceException("CUIFACTORY024","Service not found. servicename is "+ name);
				}
						
			}
			else{
				//サービスではなかった場合、TextInputCheckerを生成
				String inputDefStr = getValueMustSpecified(inputElem);
				checker = new TextInputChecker();
				((TextInputChecker)checker).setValidInput(inputDefStr);
			}
			//DataInputStepオブジェクトに設定	
			step.setChecker(checker);	
		}
		//もしもcheckerが設定されていなかったら、どんな値も有効値として返すチェッカーを設定。
		if(step.getChecker() == null){
			step.setChecker(new AnyValueOkChecker());	
		}
	}
	
	

	/**
	 * DataInputStepオブジェクトに遷移先をセットする。
	 * 
	 */
	protected void setWhereToGoHash(DataInputStep step,Element stepElem) {
		//遷移先ハッシュを設定する。
		NodeList gotolist =stepElem.getElementsByTagName(GOTO_TAG);
		for(int rCnt=0;rCnt<gotolist.getLength();rCnt++){
			Element gotoElem = (Element)gotolist.item(rCnt);
			String value_att = gotoElem.getAttribute(GOTO_TAG_VALUE_ATT);		
			if (value_att.length() < 1){
				String distination = getValueMustSpecified(gotoElem);
				//無条件GOTOは、ハッシュではなく、次STEPを保持するメンバ変数にセットする。
				step.setNextStepName(distination);
			}
			else{
				//条件付きGOTOはハッシュに格納
				String distination = getValueMustSpecified(gotoElem);
				step.addWhereToGo(value_att,distination);
			}
		}
	}
	/**
	 * getFirstChildがnullを返したら、サービスエクセプションを投げるメソッド
	 * 必ず値が設定されているべき
	 * @return
	 */
	protected String getValueMustSpecified(Element elem){
		Node node = elem.getFirstChild();
		if(node == null){
			throw new ServiceException("CUIFACTORY030","must be specified value tag is " + elem.getTagName());
		}
		return node.getNodeValue();
	}
	/**
	 * getFirstChildがnullを返したら、空文字を返すメソッド
	 * 
	 */
	protected String getValueIfSpecified(Element elem){
		Node node = elem.getFirstChild();
		if(node == null){
			return "";
		}
		else{
			return node.getNodeValue();
		}
	}
	/**
	 * 設定された実装クラス名のインスタンスを生成する。
	 * 
	 */
	protected CuiOperator createCuiOperator(){
		CuiOperator cuiOperator = null;
		try{
			cuiOperator = (CuiOperator)Class.forName(
				mImplementClassName,
				true,
				NimbusClassLoader.getInstance()
			).newInstance();
		}
		catch(IllegalAccessException e){
			throw new ServiceException("CUIFACTORY025","IllegalAccess When creatCuiOperator() ",e);
		}
		catch(InstantiationException e){
			throw new ServiceException("CUIFACTORY026","Instanting failed",e);
		}
		catch(ClassNotFoundException e){
			throw new ServiceException("CUIFACTORY027","Class not found. name is "+mImplementClassName);
		}
		return cuiOperator;
	}
		
}
