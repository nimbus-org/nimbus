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
package jp.ossc.nimbus.service.byteconvert;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.lang.*;
//
//
/**
 *	byte配列のバイナリデータを各型の値に変換する
 *	@author		Hirotaka.Nakano
 *	@version	1.00 作成：2001.06.21 － H.Nakano<BR>
 *				更新：
 */
public class ByteConverterService extends ServiceBase
								  implements ByteConverterServiceMBean,
								  			ByteConverterFactory{
	
    private static final long serialVersionUID = 1999732515319288885L;
	
    /** バイト変換インターフェイス配列 */
	protected ByteConverter[] mInterfaceAry = null ;
	/**
	 * コンストラクタ。<BR>
	 */
	public ByteConverterService(){
		super() ;
		mInterfaceAry = new ByteConverter[2] ;
	}
	//
	/**
	 * byte[] から char に変換する
	 * @param  b   バイト配列
	 * @param  off オフセット
	 * @return 変換された値
	*/
	public void setSameEndianClassName(String clsName) throws ServiceException {
		Class clsObj = null ;
		synchronized(mInterfaceAry){
			try {
				clsObj = Class.forName(
					clsName,
					true,
					NimbusClassLoader.getInstance()
				);
			} catch (ClassNotFoundException e) {
				throw new ServiceException("BYTECONVERTFACTORY001","ClassNotFoundException className = " + clsName,e) ;
			}
			try {
				mInterfaceAry[SAME_ENDIAN] = (ByteConverter)clsObj.newInstance() ;
			} catch (InstantiationException e) {
				throw new ServiceException("BYTECONVERTFACTORY002","InstantiationException" ,e) ;
			} catch (IllegalAccessException e) {
				throw new ServiceException("BYTECONVERTFACTORY003","IllegalAccessException",e) ;
			}
		}
	}
	/**
	 * byte[] から char に変換する
	 * @param  b   バイト配列
	 * @param  off オフセット
	 * @return 変換された値
	*/
	public String getSameEndianClassName(){
		synchronized(mInterfaceAry){
			if(mInterfaceAry[SAME_ENDIAN] == null){
				return "" ;
			}else{
				return mInterfaceAry[SAME_ENDIAN].getClass().getName() ;
			}
		}
	}
	//
	/**
	 * byte[] から char に変換する
	 * @param  b   バイト配列
	 * @param  off オフセット
	 * @return 変換された値
	*/
	public void setDifferentEndianClassName(String clsName) throws ServiceException{
		Class clsObj = null ;
		synchronized(mInterfaceAry){
			try {
				clsObj = Class.forName(
					clsName,
					true,
					NimbusClassLoader.getInstance()
				);
			} catch (ClassNotFoundException e) {
			}
			try {
				mInterfaceAry[DIFFERENT_ENDIAN] = (ByteConverter) clsObj.newInstance() ;
			} catch (InstantiationException e) {
			} catch (IllegalAccessException e) {
			}
			
		}
	}
	/**
	 * byte[] から char に変換する
	 * @param  b   バイト配列
	 * @param  off オフセット
	 * @return 変換された値
	*/
	public String getDifferentEndianClassName(){
		synchronized(mInterfaceAry){
			if(mInterfaceAry[DIFFERENT_ENDIAN] == null){
				return "" ;
			}else{
				return mInterfaceAry[DIFFERENT_ENDIAN].getClass().getName() ;
			}
		}
	}
	
	public ByteConverter findConverter(int type){
		ByteConverter ret = null ;
		synchronized(mInterfaceAry){
			ret = mInterfaceAry[type];
		}
		return ret ;
	}

}
