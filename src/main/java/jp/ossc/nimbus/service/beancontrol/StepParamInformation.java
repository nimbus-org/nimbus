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
// パッケージ
package jp.ossc.nimbus.service.beancontrol;
// インポート
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.w3c.dom.*;
import jp.ossc.nimbus.lang.*;
import java.util.*;
import java.beans.*;
import jp.ossc.nimbus.service.beancontrol.resource.*;
import jp.ossc.nimbus.util.*;
import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.beans.*;
import jp.ossc.nimbus.service.resource.*;
import jp.ossc.nimbus.service.beancontrol.interfaces.*;
import jp.ossc.nimbus.service.journal.*;
//
/**
 *	Stepパラメータ情報を管理します。
 */
public class StepParamInformation {
	
    private static final long serialVersionUID = 2462305701703274968L;
    
    //## クラスメンバー変数宣言 ##
	private static final String C_SETTER = "set";
	private static final String C_THIS = "this";
	/** セッター名　*/
	protected String mSetterName = null ;
	/** value名　*/
	protected String mValue = null ;
	/** モード　*/
	protected int mMode = 0;
	//## メンバー定数宣言 	##
	/** 即値モード　*/
	protected static final int C_VALUE_MOD = 1 ;
	protected static final String C_VALUE_MOD_STR = "value";
	/** サービスモード　*/
	protected static final int C_SERVICE_MOD = 2 ;
	protected static final String C_SERVICE_MOD_STR = "service";
	/** ステップモード */
	protected static final int C_STEP_MOD = 3 ;
	protected static final String C_STEP_MOD_STR = "step" ;
	/** リソースモード */
	protected static final int C_RESOURCE_MOD = 4;
	protected static final String C_RESOURCE_MOD_STR = "resource";
	/** インプットモード　*/
	protected static final int C_INPUT_MOD = 5 ;
	protected static final String C_INPUT_MOD_STR = "input";
	/** name　属性 */
	protected static String C_NAME_ATT = "name";
	/** type 属性 */
	protected static String C_TYPE_ATT = "type";
	/** value 属性 */
	protected static String C_VALUE_ATT = "value";
	protected static String C_VALUE_NULL = "null";
	/** セッターメソッド */
//	protected Method mSetterMethod = null;
	protected HashMap mParamHash = null ; 
	protected boolean mMapFlg = false ;
	/** サービス名Editor */
	protected ServiceNameEditor mEditor;
	/** セッター引数の型がTransanctionResourceかどうか */
	protected boolean mIsTransactionResourceSetter = false;
	/** BeanFlowFactory のコールバック*/
	protected BeanFlowInvokerFactoryCallBack mCallBack = null;
	protected Method mStepMethod = null ;
	protected String mRefStepName = null ;
	protected BeanFlowInvokerAccessImpl invoker;
	/**
	 * Constructor for JclParamInformation.
	 */
	protected StepParamInformation(BeanFlowInvokerAccessImpl impl) {
		super();
		invoker = impl;
		mEditor = new ServiceNameEditor();
		this.mParamHash = new HashMap() ;
	}
	//
	/**
	 * XMLデータから内部属性を抽出する
	 * @param element	DOMのエレメント
	 * @param clazz	セッター存在クラスオブジェクト
	 */
	public void fillParameter(Element element,
								Class clazz, 
								BeanFlowInvokerFactoryCallBack callBack,
								List jobSteps){
		mCallBack = callBack ;
		Method mSetterMethod = null;
		//name属性を取得し、保持する。
		String nameAttr = getAttMustBeSpecified(element,C_NAME_ATT);
		nameAttr = C_SETTER + nameAttr;
		setSetterName(nameAttr);
		//type属性を取得し、保持する。
		String tmp = getAttMustBeSpecified(element,C_TYPE_ATT);
		setSetterMode(tmp);
		//value属性を取得し、保持する。
		String valueAttr = element.getAttribute(C_VALUE_ATT);
		if( (valueAttr == null) || (valueAttr.length() == 0) ){
			String content = MetaData.getElementContent(element);
            if(content == null){
                content = "";
            }
			if(content != null) valueAttr = content;
		}
		if( (valueAttr == null) || (valueAttr.length() == 0) ){
			int mode = getSetterMode();
			//以下のモードでは、必ずvalue属性が指定されていなければならない。
			if((mode == C_SERVICE_MOD) || (mode == C_STEP_MOD) || ( mode == C_RESOURCE_MOD)){
				throw new InvalidConfigurationException(this.getClass().getName()+ " value attr must be specified.");
			}
		}
		if(getSetterMode() == C_STEP_MOD){
			CsvArrayList csvArrayList = new CsvArrayList() ;
			csvArrayList.split(valueAttr,"#");
			mRefStepName = csvArrayList.getStr(0);
			String getterStr = csvArrayList.getStr(1);
			if(!C_THIS.equals(getterStr)){
				//BLのインスタンスを取得する。
				Iterator ite = jobSteps.iterator();
				boolean findFlg = false ;
				while(ite.hasNext()){
					JobStep js = (JobStep)ite.next() ; 
					if(js.getStepName().equals(mRefStepName)){
						Class claz = js.getBeanClass() ;
						Method method = null;
						try {
							method = claz.getMethod(getterStr,(Class[])null);
						} catch (SecurityException e) {
							throw new InvalidConfigurationException("Step Getter is invalid " + valueAttr ,e) ;
						} catch (NoSuchMethodException e) {
							throw new InvalidConfigurationException("Step Getter is invalid " + valueAttr ,e) ;
						}
						findFlg = true ;
						mStepMethod = method ;
						break ;
					}
				}
				if(!findFlg){
					throw new InvalidConfigurationException("Step Getter is none " + valueAttr ) ;
				}
			}
		}
		setValue(valueAttr);
		//メソッドをメンバ変数に確保
		Method[] methods = clazz.getMethods();
		for(int rCnt=0;rCnt<methods.length;rCnt++){
			//名前が一致するものを探す
			if (methods[rCnt].getName().equals(nameAttr)){
				Class[] params = methods[rCnt].getParameterTypes();
				//引数が1個ならばセッターとしてメンバ変数に保持
				if (params.length == 1){
					mSetterMethod = methods[rCnt];
					if(params[0].isAssignableFrom(TransactionResource.class)){
						mIsTransactionResourceSetter = true;
					}
					else{
						mIsTransactionResourceSetter = false;
					}
					if(Byte.TYPE.equals(params[0])){
						params[0] = Byte.class;
					}else if(Character.TYPE.equals(params[0])){
						params[0] = Character.class;
					}else if(Short.TYPE.equals(params[0])){
						params[0] = Short.class;
					}else if(Integer.TYPE.equals(params[0])){
						params[0] = Integer.class;
					}else if(Long.TYPE.equals(params[0])){
						params[0] = Long.class;
					}else if(Float.TYPE.equals(params[0])){
						params[0] = Float.class;
					}else if(Double.TYPE.equals(params[0])){
						params[0] = Double.class;
					}else if(Boolean.TYPE.equals(params[0])){
						params[0] = Boolean.class;
					}
					this.mParamHash.put(params[0],mSetterMethod) ;
				}
			}
		}
		if(clazz.isAssignableFrom(Map.class)){			 			
			this.mMapFlg = true ;
		}
		//セッターが見つからなかったら、ServiceExceptionをスロー
		if(this.mMapFlg==false && mSetterMethod == null){
			throw new InvalidConfigurationException(this.getClass().getName()+ " Not Found " + nameAttr + " method.");
		}
		
	}
	/**
	 * value属性の値をセットする。
	 */
	protected void setValue(String value){
		mValue = value;
	}

	/**
	 * value属性を返す
	 */
	protected String getValue() {
		return mValue;
	}

	/**
	 * "セッターモード"のセッター
	 * @param mode String
	 */
	protected void setSetterMode(String mode) {
		if(mode.equals(C_VALUE_MOD_STR)){
			mMode = C_VALUE_MOD;
		}
		else if(mode.equals(C_SERVICE_MOD_STR)){
			mMode = C_SERVICE_MOD;
		}
		else if(mode.equals(C_STEP_MOD_STR)){
			mMode = C_STEP_MOD;
		}
		else if(mode.equals(C_RESOURCE_MOD_STR)){
			mMode = C_RESOURCE_MOD;
		}
		else if(mode.equals(C_INPUT_MOD_STR)){
			mMode = C_INPUT_MOD;
		}
		else{
			throw new ServiceException("StepParamInformation","It's not valid mode as setter [" + mode + "]");
		}
	}
	
	/**
	 * "セッターモード"のゲッター
	 * @return String
	 */
	protected int getSetterMode() {
		return mMode;
	}
	
	
	/**
	 * セッター名のゲッター
	 * Returns the setterName.
	 * @return String
	 */
	protected String getSetterName() {
		return mSetterName;
	}

	/**
	 * セッター名のセッター
	 * @param setterName The setterName to set
	 */
	protected void setSetterName(String setterName) {
		mSetterName = setterName;
	}
	
	
	
	/**
	 * 実行時にコールされる。BLのセッターをinvokeするメソッド
	 * @param invokeInstance
	 * @param execBlInstanceHash
	 * @param rm
	 * @param inputObj
	 */
	
	protected void invokeParameter(Object invokeInstance,
									HashMap execBlInstanceHash,
									ResourceManager rm,Object inputObj)
					throws NoSuchMethodException,InvocationTargetException,IllegalAccessException{
		Object retObj = null;

		if(invokeInstance == null){
			throw new ServiceException(this.getClass().getName(),"Target Object is null.") ;			
		}
		//モードによって動作を切り替える。
		switch(mMode){
			case C_VALUE_MOD:
				//測値だったらそのまま設定する。
				//ターゲットBLのセッター実行
				invokeSetter(invokeInstance,mValue);
				break;
			case C_SERVICE_MOD:
				//サービスモードだったらサービスマネージャを使ってサービスを取得する。
				mEditor.setAsText(mValue);
				retObj = ServiceManagerFactory.getServiceObject((ServiceName)mEditor.getValue()) ;
				//ターゲットBLのセッターinvoke
				invokeSetter(invokeInstance,retObj);
				break;
			case C_STEP_MOD:
				//ステップモードだったら
				if(this.mStepMethod != null){
					//BLのインスタンスを取得する。
					Object obj = execBlInstanceHash.get(mRefStepName);
					retObj = mStepMethod.invoke(obj,(Object[])null);
				}else{
					retObj = execBlInstanceHash.get(mRefStepName) ;
				}
				//ターゲットBLのセッター実行
				invokeSetter(invokeInstance,retObj);					
				break;
			case C_RESOURCE_MOD:
				//リソースモードだったらリソースマネージャにリソースをとってこさせる。
				Object tmpObj = rm.getResource(mValue);
				//セッターがトランザクションリソースを期待していたら・・・・
				if( mIsTransactionResourceSetter ){
					retObj = tmpObj;
				}
				else{
					retObj = ((TransactionResource)tmpObj).getObject();
				}
				//ターゲットBLのセッター実行
				invokeSetter(invokeInstance,retObj);
				break;
			case C_INPUT_MOD:
				//インプットモードだったら第4引数をセットする。
				if(this.mValue != null && this.mValue.length()>0){
					if(inputObj.getClass().isAssignableFrom(Map.class)){
						Map tmp =(Map)inputObj ;
						inputObj = tmp.get(mValue) ;
					}else{
						Class clazz1 = inputObj.getClass();
						Method method1 = null;
						method1 = clazz1.getMethod(mValue,(Class[])null);
						inputObj = method1.invoke(inputObj,(Object[])null);
					}
				}
				invokeSetter(invokeInstance,inputObj);
			default:
				//セッターで無効値をはじいているのでここにはこない。
				break;	
		}
	}

	/**
	 * メソッドを実行する。
	 * @param invokeTarget
	 * @param argObj
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private void invokeSetter(Object invokeTarget,Object argObj)
			throws NoSuchMethodException,
					IllegalAccessException,
					InvocationTargetException{	
		//ターゲットBLのセッター実行
		Object objAry[] = new Object[1] ;
		objAry[0] = argObj ;
		Method msd = null ; 
		Journal jnl = this.invoker.getJournal() ;
		if(jnl != null){
			if(argObj != null){
				jnl.addInfo(this.mSetterName ,argObj.toString()) ;
			}else{
				jnl.addInfo(this.mSetterName ,C_VALUE_NULL) ;
			}
		}
		//テキストからセッターが欲しがるクラスへ変換
		if(mMode == C_VALUE_MOD){
			if(this.mParamHash.size()==1){
				Set keys = this.mParamHash.keySet() ;
				for(Iterator ite = keys.iterator();ite.hasNext();){
					Class cls = (Class) ite.next() ;
					PropertyEditor pe = mCallBack.findPropEditor(cls) ;
					pe.setAsText((String)argObj) ;
					objAry[0] = pe.getValue() ;
					msd = (Method)this.mParamHash.get(cls) ;
					break ;
				}
			}
		//INPUTのときは型がわからないので最初に見つかった方で実行するしかない	
		//Primitiveが型不明のため
		}else if(mMode == C_INPUT_MOD){
			Set keys = this.mParamHash.keySet() ;
			for(Iterator ite = keys.iterator();ite.hasNext();){
				Class cls = (Class) ite.next() ;
				msd = (Method)this.mParamHash.get(cls) ;			
				break ;
			}
		}else{
			if(this.mParamHash.size()>0){
				if(argObj!=null){
					msd = (Method)this.mParamHash.get(argObj.getClass()) ;
					if(msd == null){
						Set keys = this.mParamHash.keySet() ;
						for(Iterator ite = keys.iterator();ite.hasNext();){
							Class cls = (Class) ite.next() ;
							if(cls.isAssignableFrom(argObj.getClass())){
								msd = (Method)this.mParamHash.get(cls) ;			
								break ;
							}
						}
					}
				}else{
					Set keys = this.mParamHash.keySet() ;
					for(Iterator ite = keys.iterator();ite.hasNext();){
						Class cls = (Class) ite.next() ;
						msd = (Method)this.mParamHash.get(cls) ;			
						break ;
					}
				}
			}
		}
		if(msd == null && this.mMapFlg){
			Map map = (Map)invokeTarget ;
			map.put(this.mSetterName,argObj) ;
		}else if(msd != null){
			msd.invoke(invokeTarget,objAry);
		}else{
			throw new InvalidConfigurationException("Fail to set Attribute ["
										 + mSetterName + "]" + " param class is " + argObj.getClass());
		}
	}
	/**
	 * 必ず指定されていなければならない属性を取得するメソッド。
	 * 属性取得に失敗すると、ServiceExceptionを投げる。
	 */
	private String getAttMustBeSpecified(Element elem,String attName){
		String ret = elem.getAttribute(attName);
		if (ret != null){
			if(ret.length() > 0){
				return ret;
			}
		}
		throw new InvalidConfigurationException("Fail to get Attribute ["
									 + attName + "] ." + "Tag name is " + elem.getTagName());
	}

}
