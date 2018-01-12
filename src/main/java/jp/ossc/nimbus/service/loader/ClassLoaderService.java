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
package jp.ossc.nimbus.service.loader;

import java.net.*;
import java.io.*;
import java.util.*;
import java.text.*;
import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.util.*;
import jp.ossc.nimbus.lang.*;

/*
 *	ClassFactoryオブジェクト
 *	@author		NRI Hirotaka Nakano
 *	@version	作成：2003.01.01 － H.Nakano<BR>
 */
public class ClassLoaderService extends ServiceBase 
								implements ClassLauncher,ClassLoaderServiceMBean
								{
	
    private static final long serialVersionUID = -6862375386956303362L;
    
    /** クラスパスURL配列	*/	
	protected	URL[]	mUrlAry ;
	/** CashLoder			*/	
	protected	CashedClassLoader	mBlLoder ;
	/** クラスパス文字列　*/
	protected String mClassPath ;
	/** リフレッシュ日時 */
	protected Date mReshreshedDate = null ;
	/** リフレッシュ予定日時 */
	protected Date mReshreshPlanDate = null ;
	//
	static private final String C_SEMICORON = ";" ; //$NON-NLS-1$
	static private final String C_JAR_EXT = ".jar" ; //$NON-NLS-1$
	static private final String C_UPER_JAR_EXT = ".JAR" ; //$NON-NLS-1$
	static private final String C_SLASH = "/" ; //$NON-NLS-1$
	//
	/**
	 *	コンストラクタ
	 */
	public ClassLoaderService(){
		
	}
		/**
	 * ClassPathセッター.
	 * @param paths
	 */
	public void setClassPath(String paths){
		mClassPath = paths ;
		CsvArrayList ps = new CsvArrayList() ;
		ps.split(paths,C_SEMICORON) ;
		mUrlAry = new URL[ps.size()] ;
		for(int i= 0;i<ps.size();i++){
			String	filePath = ps.getStr(i);
			//指定がJARファイルでない場合
			if	(filePath.endsWith(C_JAR_EXT) == false 
				&& filePath.endsWith(C_UPER_JAR_EXT) == false)	{
				if(!filePath.endsWith(C_SLASH)){
					// /を最後に付加する。
					filePath = filePath + C_SLASH;
				}
			}
			try{
				//URL配列を作ってみる
				mUrlAry[i]	=	new File(filePath).toURL();
			}catch(MalformedURLException	e)	{
				throw new ServiceException("CLASSLODER001","URLERROR " + filePath  ,e) ;  //$NON-NLS-1$//$NON-NLS-2$
			}
		}
	}
	/**
	 * ClassPathゲッター.
	 * @return String
	 */
	public String getClassPath() {
		return this.mClassPath ;		
	}
	
//
	/*
	 *	クラスロードメソッド
	 *	@param		name	クラス名
	 *	@return		Class：正常<BR>
	 *	throws		ClassNotFoundException	 
	 */
	public	Class	loadClass(String name) throws ClassNotFoundException{
		synchronized(this){
			if(this.mReshreshPlanDate.after(this.mReshreshedDate) &&
				this.mReshreshPlanDate.before(new Date())){
				startService();
			}	
			return mBlLoder.loadClass(name) ;
		}
	} 
	//
	/*
	 *	クラスロードメソッド
	 *	@param		name	クラス名
	 *	@return		Class：正常<BR>
	 *	throws		ClassNotFoundException	 
	 */
	public	Object	loadNewInstance(String name) throws ClassNotFoundException, InstantiationException, IllegalAccessException  {
		Class cls = null ;
		synchronized(this){
			if(this.mReshreshPlanDate.after(this.mReshreshedDate) &&
				this.mReshreshPlanDate.before(new Date())){
				startService();
			}	
			cls = mBlLoder.loadClass(name) ;
		}
		return cls.newInstance();
	}
	//
	/**
	 *	シングルトンファクトリー初期化。<br>
	 */
	public  void startService() {
		synchronized(this){
			/** リフレッシュ予定日時 */
			mReshreshPlanDate = new Date() ;
			mBlLoder = new CashedClassLoader(mUrlAry,Thread.currentThread().getContextClassLoader()) ;		
			/** リフレッシュ日時 */
			mReshreshedDate = new Date() ;
		}
	}
	//
	/**
	 *	シングルトンファクトリー後処理。<br>
	 */
	public void stopService() {
		mBlLoder = null ;	
	}
		/**
	 * Refresh予定時刻を設定する。.
	 * @param time
	 */
	public void setRefreshTime(String time) {
		SimpleDateFormat ft = new SimpleDateFormat(TIMEFORMAT) ;
		synchronized(this){
			try {
				this.mReshreshPlanDate = ft.parse(time);
			} catch (ParseException e) {
				throw new ServiceException("CLASSLODER002","ParseException time is" + time  ,e) ; //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}
	/**
	 * 即時リフレッシュ要請.
	 */
	public void refreshNow() {
		startService() ;
	}
	/**
	 * 最終リフレッシュ時刻ゲッター.
	 * @return String
	 */
	public String getLastRrefreshTime() {
		SimpleDateFormat ft = new SimpleDateFormat(TIMEFORMAT) ;
		synchronized(this){
			return ft.format(this.mReshreshedDate);
		}
	}
	/**
	 * 次回リフレッシュ時刻ゲッター.
	 * @return String
	 */
	public String getNextRefreshTime() {
		SimpleDateFormat ft = new SimpleDateFormat(TIMEFORMAT) ;
		synchronized(this){
			return ft.format(this.mReshreshPlanDate);
		}
	}
	/**
	 * Method getCashedBlList.
	 * @return String[]
	 */
	public String[] getCashedBlList(){
		Hashtable hash = null ;
		synchronized(this){
			hash= this.mBlLoder.getCashedClass() ;
		}
		int size = hash.size() ;
		String ret[] = new String[size] ;
		int cnt = 0 ;
		for(Enumeration enumeration = hash.elements();enumeration.hasMoreElements();cnt++){
			Class cls = (Class)enumeration.nextElement();
			ret[cnt] = cls.getName() ;
		}
		return ret ;		
	}
	
	//
}
