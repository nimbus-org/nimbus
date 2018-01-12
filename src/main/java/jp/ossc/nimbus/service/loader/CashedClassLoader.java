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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import jp.ossc.nimbus.io.ExtentionFileFilter;
import jp.ossc.nimbus.io.RecurciveSearchFile;
import jp.ossc.nimbus.lang.ServiceException;
import jp.ossc.nimbus.util.StringOperator;
//
/*
 *	ClassCashオブジェクト
 *	@author		NRI Hirotaka Nakano
 *	@version	作成：2003.01.01 － H.Nakano<BR>
 */
public  class CashedClassLoader  {
	//##	メンバー変数宣言		##
	/**	クラスローダー		*/	
	protected URLClassLoader mClassLoader = null ;
	/** クラスタンキングHASH	*/	
	protected	Hashtable	mClassHash;
	/** クラスパスURL配列 */
	protected URL[] mUrls ;
	/** 親のクラスローダーインスタンス */
	protected ClassLoader mParent ;
	//##	定数宣言			##
	static private final String C_JAR_EXTEND = ".JAR" ; //$NON-NLS-1$
	static private final String C_BK_SRASH = "\\" ; //$NON-NLS-1$
	static private final String C_SRASH = "/" ; //$NON-NLS-1$
	static private final String C_DOT = "." ; //$NON-NLS-1$
	static private final String C_CLASS_EXT = ".class" ; //$NON-NLS-1$
	//
	//
	/*
	 *	コンストラクタ
	 */
	public	CashedClassLoader(URL[] urls, ClassLoader parent){
		mClassLoader = new URLClassLoader(urls,parent); 
		mUrls = urls ;
		mParent = parent ;
		mClassHash = new Hashtable() ;
		refresh() ;		
	}
	//
	/**
	 * 即時にキャッシュを入れ替える.
	 */
	protected void refresh(){
		mClassHash.clear() ;
		for(int cnt = 0 ;cnt < mUrls.length;cnt++){
			URL item = mUrls[cnt] ;
			String upper = item.getFile().toUpperCase() ;
			if(upper.endsWith(C_JAR_EXTEND)){
				//JARファイル内検索
				setupJarPropList(item) ;
			}else{
				//ディレクトリ内検索
				setupDirPropList(item) ;
			}
		}
	
	}
	/**
	 * Jarファイルからクラス名の抽出を行う.
	 * @param item
	 */
	protected void setupJarPropList(URL item){
		String dirPath = StringOperator.replaceString(item.getFile(),C_BK_SRASH,C_SRASH) ;
		JarFile jar;
		try {
			jar = new JarFile(dirPath);
		} catch (IOException e) {
			throw new ServiceException("CASHCLASSLODER001","IOException filename = " +  dirPath ,e) ;				  //$NON-NLS-1$//$NON-NLS-2$
		}
		for(Enumeration enumeration = jar.entries();enumeration.hasMoreElements();){ 
			ZipEntry entry = (ZipEntry)enumeration.nextElement() ;
			String name = entry.getName() ;
			if(name.endsWith(C_CLASS_EXT)){
				name = StringOperator.replaceString(name,C_SRASH,C_DOT) ;
				//name = name.substring(1,name.length()-C_CLASS_EXT.length()) ;
				name = name.substring(0,name.length()-C_CLASS_EXT.length()) ;//2003.11.13 Hirokado 1→0変更
				Class cls = null;
				try {
					cls = this.loadClass(name);
				} catch (ClassNotFoundException e) {
					throw new ServiceException("CASHCLASSLODER002","ClassNotFoundException clsename = " +  name ,e) ;				  //$NON-NLS-1$//$NON-NLS-2$
				}
				mClassHash.put(name,cls) ;			
			}
		}
		
	}
	//
	/**
	 * Method ディレクトリ指定でクラス名を抽出する.
	 * @param item
	 */
	protected void setupDirPropList(URL item){
		String dirPath = StringOperator.replaceString(item.getFile(),C_BK_SRASH,C_SRASH) ;
		if(!item.getFile().endsWith(C_SRASH)){
			dirPath = dirPath + C_SRASH ;
		}
		RecurciveSearchFile file= new RecurciveSearchFile(dirPath);
		ExtentionFileFilter filter = new ExtentionFileFilter(C_CLASS_EXT,true) ;
		File[] list = file.listAllTreeFiles(filter);
		for(int cnt=0;cnt<list.length;cnt++){
			String path = list[cnt].getAbsolutePath() ;
			//int pos = item.getFile().length();
			int pos = item.getFile().length() - 1;//2003.11.13 Hirokado -1追加
			path = path.substring(pos,path.length()-C_CLASS_EXT.length()) ;
			path = StringOperator.replaceString(path,C_SRASH,C_DOT) ;
			Class cls = null;
			try {
				cls = this.loadClass(path);
			} catch (ClassNotFoundException e) {
				throw new ServiceException("CASHCLASSLODER003","ClassNotFoundException clsename = " +  path ,e) ;				  //$NON-NLS-1$//$NON-NLS-2$
			}
			mClassHash.put(path,cls) ;			
		}
	}
	//
	/**
	 * @see ClassLauncher#loadClass(String)
	 */
	public  synchronized Class loadClass(String clsName) throws ClassNotFoundException {
		Class cl = null ;
		synchronized(mClassHash){
		 	cl = (Class )mClassHash.get(clsName);
			if(cl == null) {
				//systemclass か判定する。
				cl = this.mClassLoader.loadClass(clsName) ;
				if(cl==null){
					throw new ClassNotFoundException(clsName);
				}
				mClassHash.put(clsName,cl) ;
			}
		}
		return cl;
	}
	//
	/**
	 * Method getCashedClass.
	 * @return Hashtable
	 */
	public Hashtable getCashedClass(){
		return this.mClassHash ;	
	}
	
}
