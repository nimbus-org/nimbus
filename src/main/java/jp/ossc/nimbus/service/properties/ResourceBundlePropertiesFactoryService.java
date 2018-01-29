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
package jp.ossc.nimbus.service.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Properties;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.io.ExtentionFileFilter;
import jp.ossc.nimbus.io.RecurciveSearchFile;
import jp.ossc.nimbus.lang.ServiceException;
import jp.ossc.nimbus.util.ArrayProperties;
import jp.ossc.nimbus.util.CsvArrayList;
import jp.ossc.nimbus.util.EncodedProperties;
import jp.ossc.nimbus.util.StringOperator;
//
/**
  * プロパティオブジェクトをキャシュするファクトリ<p>
  * [詳細なクラスの説明。]
  *
  * @version $Name:  $
  * @author H.Nakano
  * @since 1.0
  */
public class ResourceBundlePropertiesFactoryService 
	extends ServiceBase
	implements ResourceBundlePropertiesFactoryServiceMBean,
				PropertiesFactory {
	
    private static final long serialVersionUID = 114536824319044310L;
    
    /** HASH						*/	
	protected Hashtable	mPropHash = null ;
	/** プロパティファイルエンコード	*/	
	protected String	mEncode = EncodedProperties.ENCODE_PORP ;
	/** プロパティファイルエンコード	*/	
	protected CsvArrayList	mRootDir = null ;
	/** 入れ替え予定時刻	*/	
	protected Date	mRefreshPlanTime = null ;
	/** 入れ替え実績時刻	*/	
	protected Date	mRefreshedTime = null ;
	//
	static private final String C_SEPARATOR = "path.separator" ;  //$NON-NLS-1$
	static private final String C_UNDER_SCORE = "_" ; //$NON-NLS-1$
	static private final String C_JAR_EXT = ".JAR" ; //$NON-NLS-1$
	static private final String C_BK_SRASH = "\\" ; //$NON-NLS-1$
	static private final String C_SRASH = "/" ; //$NON-NLS-1$
	static private final String C_PROP_EXT = ".properties" ; //$NON-NLS-1$
	static private final String C_DOT = "." ; //$NON-NLS-1$
	/**
	 *	コンストラクタ。<BR>
	 */
	public ResourceBundlePropertiesFactoryService() throws IOException{
		super() ;
		mPropHash = new Hashtable(1024*3) ;
		mRootDir = new CsvArrayList() ;
		
	}
	//
	/**
	 * @see jp.ossc.nimbus.service.properties.ResourceBundlePropertiesFactoryServiceMBean#getRefreshedTime()
	 */
	public String getRefreshedTime(){
		SimpleDateFormat ft = new SimpleDateFormat(TIME_FORMAT) ;
		synchronized(mPropHash){
			return ft.format(mRefreshedTime);
		}
	}
	//
	/**
	 * @see jp.ossc.nimbus.service.properties.ResourceBundlePropertiesFactoryServiceMBean#getRefreshPlanTime()
	 */
	public String getRefreshPlanTime() {
		SimpleDateFormat ft = new SimpleDateFormat(TIME_FORMAT) ;
		synchronized(mPropHash){
			return ft.format(mRefreshPlanTime);
		}
	}
	//
	/**
	 * @see jp.ossc.nimbus.service.properties.ResourceBundlePropertiesFactoryServiceMBean#setClassPath(String)
	 */
	public void setClassPath(String classPath) {
		synchronized(mPropHash){
			mRootDir.clear() ;
			String sept = System.getProperty(C_SEPARATOR);
			mRootDir.split(classPath,sept) ;
		}
	}
	//
	/**
	 * @see jp.ossc.nimbus.service.properties.ResourceBundlePropertiesFactoryServiceMBean#getClassPath()
	 */
	public String getClassPath(){
		synchronized(mPropHash){
			return mRootDir.join(System.getProperty(C_SEPARATOR)) ;
		}
	}
	//
	/**
	 * @see jp.ossc.nimbus.service.properties.ResourceBundlePropertiesFactoryServiceMBean#refreshNow()
	 */
	public void refreshNow(){
		startService() ;
	}
	//
	/**
	 * @see jp.ossc.nimbus.service.properties.ResourceBundlePropertiesFactoryServiceMBean#setRefreshPlanTime(String)
	 */
	public void setRefreshPlanTime(String time){
		SimpleDateFormat ft = new SimpleDateFormat(TIME_FORMAT) ;
		synchronized(mPropHash){
			try {
				mRefreshPlanTime = ft.parse(time);
			} catch (ParseException e) {
				throw new ServiceException("PROPFACTORY001","ParseException",e) ;				  //$NON-NLS-1$//$NON-NLS-2$
			}
		}
		
	}
	//
	/**
	 *	初期化処理<BR>
	 * @see jp.ossc.nimbus.core.ServiceBaseSupport#startService()
	 */
	public void startService(){
		SimpleDateFormat ft = new SimpleDateFormat(TIME_FORMAT) ;
		synchronized(mPropHash){
			try {
				mRefreshPlanTime = ft.parse(ft.format(new Date())) ;
			} catch (ParseException e) {
				throw new ServiceException("PROPFACTORY011","ParseException",e) ;				 //$NON-NLS-1$ //$NON-NLS-2$
			}
			refresh() ;
		}
	}
	//
	/**
	 *	終了処理<BR>
	 * @see jp.ossc.nimbus.core.ServiceBaseSupport#stopService()
	 */
	public void stopService() {
		synchronized(mPropHash){
			mPropHash.clear();
		}
	}
	//
	/**
	 * @see jp.ossc.nimbus.service.properties.PropertiesFactory#loadProperties(String)
	 */
	public Properties loadProperties(String propName) {
		Locale lo = Locale.getDefault() ;
		return loadProperties(propName,lo) ;
	}
	/**
	 * @see jp.ossc.nimbus.service.properties.PropertiesFactory#loadProperties(String, Locale)
	 */
	public Properties loadProperties(String propName,Locale lo) {
		ArrayProperties retProp = null ;
		synchronized(mPropHash){
			if(this.mRefreshPlanTime.after(this.mRefreshedTime)){
				if(this.mRefreshPlanTime.before(new Date())){
					refresh() ;
				}
			}
			while(true){
				StringBuilder propKey = new StringBuilder(propName) ;
				// 1baseclass + "_" + language1 + "_" + country1 + "_" + variant1 + ".properties" 
				propKey.append(C_UNDER_SCORE).append(lo.getLanguage()).append(C_UNDER_SCORE).append(lo.getCountry()).append(C_UNDER_SCORE).append(lo.getVariant()); 
				retProp = (ArrayProperties)mPropHash.get(propKey.toString());
				if(retProp != null){
					break ;
				}
				//baseclass + "_" + language1 + "_" + country1 + ".properties" 
				propKey = new StringBuilder(propName) ;
				propKey.append(C_UNDER_SCORE).append(lo.getLanguage()).append(C_UNDER_SCORE).append(lo.getCountry()); 
				retProp = (ArrayProperties)mPropHash.get(propKey.toString());
				if(retProp != null){
					break ;
				}
				//baseclass + "_" + language1 + ".properties" 
				propKey = new StringBuilder(propName) ;
				propKey.append(C_UNDER_SCORE).append(lo.getLanguage()); 
				retProp = (ArrayProperties)mPropHash.get(propKey.toString());
				if(retProp != null){
					break ;
				}
				if(!lo.equals(Locale.getDefault())){
					lo = Locale.getDefault() ;
					propKey = new StringBuilder(propName) ;
					// 1baseclass + "_" + language1 + "_" + country1 + "_" + variant1 + ".properties" 
					propKey.append(C_UNDER_SCORE).append(lo.getLanguage()).append(C_UNDER_SCORE).append(lo.getCountry()).append(C_UNDER_SCORE).append(lo.getVariant()); 
					retProp = (ArrayProperties)mPropHash.get(propKey.toString());
					if(retProp != null){
						break ;
					}
					//baseclass + "_" + language1 + "_" + country1 + ".properties" 
					propKey = new StringBuilder(propName) ;
					propKey.append(C_UNDER_SCORE).append(lo.getLanguage()).append(C_UNDER_SCORE).append(lo.getCountry()); 
					retProp = (ArrayProperties)mPropHash.get(propKey.toString());
					if(retProp != null){
						break ;
					}
					//baseclass + "_" + language1 + ".properties" 
					propKey = new StringBuilder(propName) ;
					propKey.append(C_UNDER_SCORE).append(lo.getLanguage()); 
					retProp = (ArrayProperties)mPropHash.get(propKey.toString());
					if(retProp != null){
						break ;
					}
				}
				retProp = (ArrayProperties)mPropHash.get(propName);
				break ;
			}
		}
		return (Properties)retProp ;
	}
	//
	/**
	 * 即時にキャッシュを入れ替える.
	 */
	protected void refresh(){
		SimpleDateFormat ft = new SimpleDateFormat(TIME_FORMAT) ;
		mPropHash.clear() ;
		for(ListIterator iterator = mRootDir.listIterator();iterator.hasNext();){
			String item = (String)iterator.next();
			String upper = item.toUpperCase() ;
			if(upper.endsWith(C_JAR_EXT)){
				//JARファイル内検索
				setupJarPropList(item) ;
			}else{
				//ディレクトリ内検索
				setupDirPropList(item) ;
			}
		}
		try {
			mRefreshedTime = ft.parse(ft.format(new Date())) ;
		} catch (ParseException e) {
			throw new ServiceException("PROPFACTORY021","ParseException",e) ;				  //$NON-NLS-1$//$NON-NLS-2$
		}
	}
	/**
	 * Jarファイルからプロパティの抽出を行う.
	 * @param item
	 */
	protected void setupJarPropList(String item){
		String dirPath = StringOperator.replaceString(item,C_BK_SRASH,C_SRASH) ;
		JarFile jar;
		try {
			jar = new JarFile(dirPath);
		} catch (IOException e) {
			throw new ServiceException("PROPFACTORY005","IOException filename = " +  dirPath ,e) ;				 //$NON-NLS-1$ //$NON-NLS-2$
		}
		for(Enumeration iterator = jar.entries();iterator.hasMoreElements();){ 
			ZipEntry entry = (ZipEntry)iterator.nextElement() ;
			String name = entry.getName() ;
			if(!entry.isDirectory() &&
				name.endsWith(C_PROP_EXT)){
				InputStream is= null;
				try {
					is = jar.getInputStream(entry);
				} catch (IOException e) {
					throw new ServiceException("PROPFACTORY006","IOException filename = " +  dirPath ,e) ;				  //$NON-NLS-1$//$NON-NLS-2$
				}
				ArrayProperties prop = new ArrayProperties(mEncode) ;
				try {
					prop.load(is) ;
				} catch (IOException e) {
					throw new ServiceException("PROPFACTORY007","IOException filename = " +  dirPath ,e) ;				  //$NON-NLS-1$//$NON-NLS-2$
				}
				name = StringOperator.replaceString(name,C_SRASH,C_DOT) ;
				name = name.substring(0,name.length()-C_PROP_EXT.length()) ;
				mPropHash.put(name,prop);
				try {
					is.close() ;
				} catch (IOException e) {
					throw new ServiceException("PROPFACTORY008","IOException filename = " +  dirPath ,e) ;				 //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}
		
	}
	//
	/**
	 * Method ディレクトリ指定でプロパティファイルを抽出する.
	 * @param item
	 */
	protected void setupDirPropList(String item){
		String dirPath = StringOperator.replaceString(item,C_BK_SRASH,C_SRASH) ;
		if(!item.endsWith(C_SRASH)){
			dirPath = dirPath + C_SRASH ;
		}
		File dirPathFile = new File(dirPath) ;
		int pos = dirPathFile.getAbsolutePath().length() ;
		RecurciveSearchFile file= new RecurciveSearchFile(dirPath);
		ExtentionFileFilter filter = new ExtentionFileFilter(C_PROP_EXT,true) ;
		File[] list = file.listAllTreeFiles(filter);
		for(int cnt=0;cnt<list.length;cnt++){
			FileInputStream stream;
			try {
				stream = new FileInputStream(list[cnt]);
			} catch (FileNotFoundException e) {
				throw new ServiceException("PROPFACTORY002","FileNotFoundException name = " + list[cnt],e) ;				 //$NON-NLS-1$ //$NON-NLS-2$
			}
			ArrayProperties prop = new ArrayProperties(mEncode) ;
			try {
				prop.load(stream) ;
			} catch (IOException e) {
				throw new ServiceException("PROPFACTORY003","IOException filename = " + list[cnt] ,e) ;				 //$NON-NLS-1$ //$NON-NLS-2$
			}
			try {
				stream.close() ;
			} catch (IOException e) {
				throw new ServiceException("PROPFACTORY004","IOException filename = " +  list[cnt] ,e) ;				 //$NON-NLS-1$ //$NON-NLS-2$
			}
			String path = list[cnt].getAbsolutePath();
			path = path.substring(pos+1,path.length()-C_PROP_EXT.length()) ;
			path = StringOperator.replaceString(path,C_BK_SRASH,C_DOT) ;
			path = StringOperator.replaceString(path,C_SRASH,C_DOT) ;
			mPropHash.put(path,prop);
		}
	}
	/**
	 * @see jp.ossc.nimbus.service.properties.ResourceBundlePropertiesFactoryServiceMBean#setEncode(String)
	 */
	public void setEncode(String encode){
		this.mEncode = encode;
	}
	/**
	 * Method getEncode.
	 * @return String
	 */
	public String getEncode(){
		return this.mEncode;
	}
}
