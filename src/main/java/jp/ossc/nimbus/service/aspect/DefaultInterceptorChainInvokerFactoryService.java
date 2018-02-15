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
package jp.ossc.nimbus.service.aspect;
//インポート
import java.util.*;
import java.io.*;
import java.net.*;
import java.lang.reflect.*;
import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.aspect.interfaces.*;
import jp.ossc.nimbus.service.aspect.metadata.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import jp.ossc.nimbus.service.aspect.util.*;
import jp.ossc.nimbus.service.log.*;
/**
 * インターセプターチェーンインボーカーファクトリーサービスクラス<p>
 * InterceptorChainInvokerを生成する
 * @version $Name:  $
 * @author H.Nakano
 * @since 1.0
 */
public class DefaultInterceptorChainInvokerFactoryService
	extends ServiceBase
	implements DefaultInterceptorChainInvokerFactoryServiceMBean, 
				InterceptorChainInvokerFactory {
	
    private static final long serialVersionUID = 6716744038966389661L;
    
    /** インターセプタリストキャッシュのマップ */
	private Map mInterceptListCacheMap = Collections.synchronizedMap(new HashMap());
	/** インターセプタ定義リスト */
	private List mInterceptConfigList = new ArrayList();
	/** インターセプタ定義ファイル名 */
	private String[] mInterceptConfigFileNames = null;
	/** インターセプタチェインコールバックメソッド */
	private Method mMethod = null;
	/** コールバッククラス名 */
	private String mCallbackClassName = null;
	/** コールバックメソッド名 */
	private String mCallbackMethodName = null;
	/** コールバックメソッドパラーメタクラス名配列 **/
	private String[] mCallbackMethodParamClassNames = null;
	/** InterceptorInvokerクラス名 */
	private String mInterceptorInvokerClassName = "jp.ossc.nimbus.service.aspect.InterceptorChainInvokerAccessImpl";
	/**	InterceptorInvokerクラス */
	private Class mInterceptorPerfomerCls = null ;
	/**	Logger サービス名 */
	private ServiceName mLoggerName = null ;
	/**	Logger サービス */
	private Logger mLogger = null ;
	/**
	 * コンストラクター
	 */
	public DefaultInterceptorChainInvokerFactoryService() {
		super();
	}
	/* (非 Javadoc)
	 * インターセプタコンポーネントの起動<br>
	 * @see jp.ossc.nimbus.core.ServiceBaseSupport#startService()
	 */
	//
	public void startService() throws InvalidConfigurationException{
		if(this.mLoggerName != null){
			mLogger=(Logger)ServiceManagerFactory.getServiceObject(this.mLoggerName) ;
		}else{
			mLogger = getLogger();
		}
		completeMethod();
		loadConfig();
		mInterceptorPerfomerCls = findClazz(this.mInterceptorInvokerClassName) ;
		try {
            mInterceptorPerfomerCls.newInstance() ;
		} catch (InstantiationException e) {
			throw new InvalidConfigurationException(e) ;
		} catch (IllegalAccessException e) {
			throw new InvalidConfigurationException(e) ;
		}
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.aspect.DefaultInterceptorChainInvokerFactoryServiceMBean#setLoggerServiceName(jp.ossc.nimbus.core.ServiceName)
	 */
	public void setLoggerServiceName(ServiceName name) {
		mLoggerName = name ;
	}

	/**
	 * メソッド名を解決する。<br>
	 */
	private void completeMethod() throws InvalidConfigurationException{
		if(this.mLogger != null){
			this.mLogger.write("AOP__00001",mCallbackClassName) ;
		}
		if(mCallbackClassName == null || mCallbackClassName.length() == 0){
			throw new InvalidConfigurationException("CallbackClassName is null");
		}
		if(this.mLogger != null){
			this.mLogger.write("AOP__00002",mCallbackMethodName) ;
		}
		//ルート実行メソッド取得
		if(mCallbackMethodName == null || mCallbackMethodName.length() == 0){
			throw new InvalidConfigurationException("CallbackMethodName is null");
		}
		try{
			//ルート実行クラス取得
			final Class clazz = findClazz(mCallbackClassName);
			Class[] params = null;
			if(mCallbackMethodParamClassNames != null && mCallbackMethodParamClassNames.length != 0){
				params = new Class[mCallbackMethodParamClassNames.length];
				for(int icnt = 0; icnt < mCallbackMethodParamClassNames.length; icnt++){
					final String pClassName = mCallbackMethodParamClassNames[icnt];
					if(pClassName == null || pClassName.length() == 0){
						throw new InvalidConfigurationException("CallbackParameterClassName" + "[" + icnt +"] is null");
					}
					final Class pClazz = findClazz(pClassName);
					params[icnt] = pClazz;
					if(this.mLogger != null){
						this.mLogger.write("AOP__00003",pClassName) ;
					}

				}
			}
			mMethod = clazz.getMethod(mCallbackMethodName, params);
		}catch(NoSuchMethodException ex){
			throw new InvalidConfigurationException(ex);
		}catch(SecurityException es){
			throw new InvalidConfigurationException(es);
		}
		if(this.mLogger != null){
			this.mLogger.write("AOP__00004") ;
		}
	}
	/**
	 * 指定したクラス名のクラスオブジェクトを取得する<br>
	 * @param  String				クラス名
	 * @return Class				クラスオブジェクト
	 */
	private Class findClazz(String className) throws InvalidConfigurationException{
		Class clazz = null;
		try{
			clazz = Class.forName(
				className,
				true,
				NimbusClassLoader.getInstance()
			);
		}catch(ClassNotFoundException ex){
			throw new InvalidConfigurationException(className + " is Invalid Class" , ex) ;
		}
		return clazz ;
	}

    /**
     * Loggerを設定する。
     */
	public void setLogger(Logger logger) {
		mLogger = logger;
	}
	
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.aspect.DefaultInterceptorChainInvokerFactoryServiceMBean#setInterceptorConfigFileNames(java.lang.String[])
	 */
	public void setInterceptorConfigFileNames(String[] fileNames) {
		this.mInterceptConfigFileNames = fileNames ;
	}

	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.aspect.DefaultInterceptorChainInvokerFactoryServiceMBean#getInterceptorConfigFileNames()
	 */
	public String[] getInterceptorConfigFileNames() {
		return this.mInterceptConfigFileNames ;
	}

	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.aspect.DefaultInterceptorChainInvokerFactoryServiceMBean#setCallbackClassName(java.lang.String)
	 */
	public void setCallbackClassName(String callbackClassName) {
		this.mCallbackClassName = callbackClassName ;
	}

	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.aspect.DefaultInterceptorChainInvokerFactoryServiceMBean#getCallbackClassName()
	 */
	public String getCallbackClassName() {
		return this.mCallbackClassName;
	}

	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.aspect.DefaultInterceptorChainInvokerFactoryServiceMBean#setCallbackMethodName(java.lang.String)
	 */
	public void setCallbackMethodName(String callbackMethodName) {
		this.mCallbackMethodName = callbackMethodName ;
	}

	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.aspect.DefaultInterceptorChainInvokerFactoryServiceMBean#getCallbackMethodName()
	 */
	public String getCallbackMethodName() {
		return this.mCallbackMethodName ;
	}

	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.aspect.DefaultInterceptorChainInvokerFactoryServiceMBean#setCallbackMethodParamClassNames(java.lang.String[])
	 */
	public void setCallbackMethodParamClassNames(String[] callbackMethodParamClassNames) {
		this.mCallbackMethodParamClassNames  = callbackMethodParamClassNames ;
	}

	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.aspect.DefaultInterceptorChainInvokerFactoryServiceMBean#getCallbackMethodParamClassNames()
	 */
	public String[] getCallbackMethodParamClassNames() {
		return this.mCallbackMethodParamClassNames ;
	}

	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.aspect.DefaultInterceptorChainInvokerFactoryServiceMBean#setInterceptorInvokerClassName(java.lang.String)
	 */
	public void setInterceptorInvokerClassName(String interceptorInvokerClassName) {
		this.mInterceptorInvokerClassName = interceptorInvokerClassName ;
	}

	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.aspect.DefaultInterceptorChainInvokerFactoryServiceMBean#getInterceptorInvokerClassName()
	 */
	public String getInterceptorInvokerClassName() {
		return this.mInterceptorInvokerClassName;
	}

	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.aspect.DefaultInterceptorChainInvokerFactoryServiceMBean#loadConfig()
	 */
	public void loadConfig() 
		throws InvalidConfigurationException {
		if(mInterceptConfigFileNames == null || mInterceptConfigFileNames.length == 0){
			throw new InvalidConfigurationException("InterceptConfigFileNames is null");
		}
		InputStream fis = null;
		try{
			for(int icnt = 0; icnt < mInterceptConfigFileNames.length; icnt++){
				if(this.mLogger != null){
					this.mLogger.write("AOP__00005",mInterceptConfigFileNames[icnt]) ;
				}
				URL url = Thread.currentThread().getContextClassLoader().getResource(mInterceptConfigFileNames[icnt]);
				if(url == null){
					throw new InvalidConfigurationException("interceptor config " + mInterceptConfigFileNames[icnt] + "is none ") ;
				}
				fis = url.openStream();
				final DocumentBuilderFactory domFactory
					   = DocumentBuilderFactory.newInstance();
				final DocumentBuilder builder = domFactory.newDocumentBuilder();
				final Document doc = builder.parse(fis);
				InterceptorMappingsMetaData imsmd = new InterceptorMappingsMetaData();
				imsmd.importXML(doc.getDocumentElement());
				loadInterceptConfigList(mInterceptConfigList, imsmd);
				fis.close();
				fis = null;
				if(this.mLogger != null){
					this.mLogger.write("AOP__00006",mInterceptConfigFileNames[icnt]) ;
				}
			  }
		//} catch (FileNotFoundException e) {
		//	throw new InvalidConfigurationException(e) ;
		} catch (ParserConfigurationException e) {
			throw new InvalidConfigurationException(e) ;
		} catch (SAXException e) {
			throw new InvalidConfigurationException(e) ;
		} catch (IOException e) {
			throw new InvalidConfigurationException(e) ;
		} catch (DeploymentException e) {
			throw new InvalidConfigurationException(e) ;
		}finally{
			if(fis != null){
				try{
					fis.close();
				}catch(Throwable e){}
				 fis = null;
			  }
		  }
	}
	/**
	 * インターセプタ定義ファイルを読み込む<br>
	 * @param List							定義を格納するリスト
	 * @param InterceptorMappingsMetaData	XMLメタデータ
	 * @exception IOException
	 * @exception ParserConfigurationException
	 * @exception SAXException
	 * @exception DeploymentException
	 * @exception FrameworkException
	 */
	private void loadInterceptConfigList(List list, InterceptorMappingsMetaData imsmd)
	  throws IOException, ParserConfigurationException, SAXException, DeploymentException{
		final List immdList = imsmd.getInterceptorMappingList();
		if(immdList == null || immdList.size() == 0){
			// 正常終了
			return;
		}
		for(int icnt = 0; icnt < immdList.size(); icnt++){
			final InterceptorPaternConfig ic = new InterceptorPaternConfig();
			final InterceptorMappingMetaData immd = (InterceptorMappingMetaData)immdList.get(icnt);
			final InterceptorNameMetaData inmd = immd.getInterceptorName();
			if(inmd == null){
				// フレームワーク例外をスロー
				throw new DeploymentException("<interceptor-name> is not found");
			}
			final String interceptorNameStr = inmd.getInterceptorName();
			final ServiceName interceptorName = UtilTool.convertServiceName(interceptorNameStr);
			if(interceptorName == null){
				// フレームワーク例外をスロー
				throw new DeploymentException("<interceptor-name>[CONTENTS]</interceptor-name> is not found");
			}
			// 該当のコンポーネントがない場合
			if(UtilTool.getInterceptor(interceptorName) == null){
				throw new DeploymentException("<interceptor-name>[CONTENTS]</interceptor-name> is missing");
			}
			ic.setInterceptorServiceName(interceptorName);
			final PatternsMetaData psmd = immd.getPatterns();
			// パターンsがない場合
			if(psmd == null){
				// フレームワーク例外をスロー
				throw new DeploymentException("<patterns>[CONTENTS]</patterns> is not found");
			}
			final List ptList = psmd.getPatternList();
			// パターンがない場合
			if(ptList == null || ptList.size() == 0){
				// フレームワーク例外をスロー
				throw new DeploymentException("<pattern>[CONTENTS]</pattern> is not found");
			}
			final String[] patterns = new String[ptList.size()];
			for(int jcnt = 0; jcnt < ptList.size(); jcnt++){
				final PatternMetaData pmd = (PatternMetaData)ptList.get(jcnt);
				patterns[jcnt] = pmd.getPattern();
			}
			ic.setPatterns(patterns);
			// インターセプタ定義を格納
			list.add(ic);
		}
	}

	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.aspect.InterceptorChainInvokerFactory#createInterceptorInvoker(java.lang.String)
	 */
	public InterceptorChainInvoker createInterceptorInvoker(String chainKey) {
		IntreceptorChainList list = null;
		// エイリアスに対応するインターセプタリストがキャッシュにあるか
		if(mInterceptListCacheMap.containsKey(chainKey)){
			// 存在した場合はキャッシュマップからインターセプタリストを取得
			list = (IntreceptorChainList)mInterceptListCacheMap.get(chainKey);
		}else{
			// 存在しない場合
			// エイリアスおよびサービスからインターセプタリスト作成
			list = findMatchedInterceptorChainList(chainKey);
			// 作成したインターセプタリストをキャッシュマップに格納
			mInterceptListCacheMap.put(chainKey, list);
		}
		// インターセプタリストからインターセプタ実行オブジェクト(インターセプタチェイン)を作成
		final InterceptorChainInvokerAccess ich =  createInterceptorInvokerAccess(list);
		return ich;
	}
	/**
	 * パフォーマーを作成する
	 * @param list	IntreceptorChainList
	 * @return	InterceptorChainInvokerAccess
	 */
	private InterceptorChainInvokerAccess createInterceptorInvokerAccess(IntreceptorChainList list) {
		InterceptorChainInvokerAccess object = null;
		try{
			object = (InterceptorChainInvokerAccess)this.mInterceptorPerfomerCls.newInstance();
		}catch(InstantiationException ex){
			//createServieで実験すみ
		}catch(IllegalAccessException ex){
			//createServieで実験すみ
		}
		object.setLogger(this.mLogger) ;
		object.setInterceptorChainList(list) ;
		object.setCallBackmethod(this.mMethod) ;
		return (InterceptorChainInvokerAccess)object;
	}

	/**
	 * エイリアスに対応するインターセプタのリストを作成し返却<br>
	 * @param  String					エイリアス
	 * @return IntreceptorChainList			インターセプタリスト
	 */
	private IntreceptorChainList findMatchedInterceptorChainList(String key){
		// インターセプタリスト作成
		final IntreceptorChainList list = new IntreceptorChainList();
		// インターセプタ定義リストを順次参照
		for(final Iterator ite = mInterceptConfigList.iterator(); ite.hasNext();){
			// インターセプタ定義を取得
			final InterceptorPaternConfig interceptConfig = (InterceptorPaternConfig)ite.next();
			// パターンマッチング確認
			if(interceptConfig.isMatch(key)){
				// マッチした場合インターセプタをインターセプタリストに格納
				if(this.mLogger != null){
					String ary[] = new String[2] ;
					ary[0] = key ;
					ary[1] = interceptConfig.getInterceptorServiceName().toString() ;
					this.mLogger.write("AOP__00007",ary) ;
				}
				list.add(interceptConfig.getInterceptorServiceName());
			}
		}
		return list;
	}

}
