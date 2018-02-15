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
package jp.ossc.nimbus.util;

import java.io.ByteArrayInputStream;
import java.io.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import jp.ossc.nimbus.io.* ;
//
/**
*	標準のプロパティオブジェクトの文字化けの問題を回避した<BR>
*	日本語可能のプロパティオブジェクトです。<BR>
*	また、String、ファイル名指定のloadが可能です。<BR>
*	@author		Hirotaka.Nakano
*	@version	1.00 作成：2001.06.21 － H.Nakano<BR>
*				更新：
*/
public class EncodedProperties extends Properties {
	
    private static final long serialVersionUID = -3138996569732225373L;
    
    //## メンバー変数宣言 ##
	/** シンクロナイズオブジェクト	*/
	private Boolean	mSyncObj = new Boolean(true) ;
	/** エンコード文字列			*/
	private String	mEncoding  ;
	/** プロパティエンコード	*/
	static public final String ENCODE_PORP = "ISO-8859-1" ; //$NON-NLS-1$
	/** プロパティエンコード	*/
	static public final String ENCODE_UTF8 = "UTF-8" ; //$NON-NLS-1$
	static public final String EQUALS = "=" ; //$NON-NLS-1$
	//
	//
	/**
	 *	コンストラクタ。<BR>
	 */
	public EncodedProperties() {
		super();
		mEncoding = ENCODE_PORP ;
	}
	//
	/**
	 *	コンストラクタ。<BR>
	 *	@param	prop 指定されたデフォルト値を持つ空のプロパティリストを作成します。
	 */
	public EncodedProperties(Properties prop) {
		super((Properties)prop);
		mEncoding = ENCODE_PORP ;
	}
	//
	/**
	 *	コンストラクタ。<BR>
	 *	@param encodeName ファイル読み込みエンコード
	 */
	public EncodedProperties(String encodeName) {
		super();
		mEncoding = encodeName ;
	}

	//	
	/**
	 *	入力ストリームからキーと要素が対になったプロパティリストを読み込みます<BR>
	 *	このロードは文字化けを回避する事ができます。
	 *	@param	inStream 入力ストリーム
	 */
	public void load(InputStream inStream) throws IOException {
		synchronized(mSyncObj){
			try{
				if(ENCODE_PORP.equals(this.getEncoding())){
					super.load(inStream);
				}else{
					this.readStream(inStream) ;
				}
			}finally{
				inStream.close();
			}
		}
	}

	private void makeKey(String rec){
		int index = rec.indexOf(EQUALS) ;
		
		// コメント行
		if(rec.indexOf("#") == 0 ||
			rec.indexOf("!") == 0) {
			return ;
		}
		
		if(index != -1){
			String key = rec.substring(0,index) ;
			key = key.trim();
			String value = rec.substring(index+1) ;
			value = value.trim();
			super.setProperty(key,value) ;
		}
	}

	private void readStream(InputStream stream) throws IOException{
		UnicodeHexBufferedReader in = null;
		in	= new UnicodeHexBufferedReader(new InputStreamReader(stream,this.getEncoding()));

		/** テーブル作成 */
		String rec = "";
		String buf = "";
		try{
			while((buf = in.readLine()) != null) {
				int len = buf.length();
				if(len > 1){
					if(buf.lastIndexOf("\\") == len - 1){
						buf = buf.trim();
						len = buf.length();
						buf = buf.substring(0, len - 1);
						rec += buf;
						continue;
					}
					else{
						buf = buf.trim();
						rec += buf;
					}
					makeKey(rec) ;
				}
				rec = "";
			}
		}finally{
			in.close() ;
			stream.close() ;
		}
	}

	//
	/**
	 *	指定ファイルからキーと要素が対になったプロパティリストを読み込みます<BR>
	 *	このロードは文字化けを回避する事ができます。
	 *	@param	filePath	プロパティファイルフルパス
	 */
	public void loadFromFile(String filePath) throws IOException {
		synchronized(mSyncObj){
			FileInputStream propStream = new FileInputStream(filePath);
			InputStream in = (InputStream)propStream;
			this.load(in);
			in.close();
			propStream.close() ;
		}
	}
	//
	/**
	 *	相対プロパティ指定で要素が対になったプロパティリストを読み込みます<BR>
	 *	このロードは文字化けを回避する事ができます。
	 *	@param	propString リソースバンドルのオブジェクトインスタンス
	 */
	public void loadFromString(String propString) throws IOException {
		synchronized(mSyncObj){
			ByteArrayInputStream propStream;
			
			String buf = getEncoding();
			setEncoding(ENCODE_UTF8);
			propString = UnicodeHexBufferedReader.convertUnicode(propString);
			try {
				propStream =new ByteArrayInputStream(propString.getBytes(this.getEncoding()));
				
			} catch (UnsupportedEncodingException e) {
				throw new IOException("Unsupport Encoding = " + mEncoding);
			}

			InputStream in = (InputStream)propStream;
			this.load(in);
			in.close();
			propStream.close() ;
			setEncoding(buf);
		}
	}
	//
	/**
	 *	エンコーディング情報出力<BR>
	 *	@return	エンコーディング文字列
	 */
	public String getEncoding()  {
		return this.mEncoding ;
	}
	//
	//
	/**
	 *	エンコーディング情報設定<BR>
	 *	@param	encoding エンコーディング文字列
	 */
	public void setEncoding(String encoding)  {
		this.mEncoding = encoding ;
	}
}
