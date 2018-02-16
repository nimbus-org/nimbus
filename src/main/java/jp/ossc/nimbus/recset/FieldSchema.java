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
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
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
package jp.ossc.nimbus.recset;
// インポート
import java.sql.Types;
/**
 * 列スキーマクラス<p>
 * 列スキーマ情報を管理する。
 * @version $Name:  $
 * @author H.Nakano
 * @since 1.0
 */
public class FieldSchema implements java.io.Serializable{
	
    private static final long serialVersionUID = 750474574072043812L;
    
    /** データ型指定文字定数　VARCHAR　*/
	public static final String C_FIELD_TYPE_VARCHAR = "VARCHAR" ;
	/** データ型指定文字定数　CHAR　*/
	public static final String C_FIELD_TYPE_CHAR = "CHAR" ;
	/** データ型指定文字定数　NUMBER　*/
	public static final String C_FIELD_TYPE_LONG = "LONG" ;
	/** データ型指定文字定数　NUMBER　*/
	public static final String C_FIELD_TYPE_INT = "INT" ;
	/** データ型指定文字定数　NUMBER　*/
	public static final String C_FIELD_TYPE_FLOAT = "FLOAT" ;
	/** データ型指定文字定数　NUMBER　*/
	public static final String C_FIELD_TYPE_DOUBLE = "DOUBLE" ;
	/** データ型指定文字定数　DATE　*/
	public static final String C_FIELD_TYPE_DATE = "DATE" ;
	/** データ型指定文字定数　TIMESTAMP　*/
	public static final String C_FIELD_TYPE_TIMESTAMP = "TIMESTAMP" ;
	/** データ型指定文字定数　BLOB　*/
	public static final String C_FIELD_TYPE_BLOB = "BLOB" ;
	/** データ型指定文字定数　CLOB　*/
	public static final String C_FIELD_TYPE_CLOB = "CLOB" ;
	
    /** フィールドKEY定数 */
	static public final int C_KEY_UNIQUE = 0;			// ユニーク列キー
	static public final int C_KEY_ROW_VERSION = 1;		// Rowバージョン列キー
	static public final int C_KEY_READ = 2; 			// 参照列キー
	static public final int C_KEY_UPDATE = 3;			// 更新列キー
	static public final int C_KEY_DUMMY = 4;			// ダミー列キー
	/** 列型定数 */
	static public final int C_TYPE_NONE = -1;
	static public final int C_TYPE_INT = 1;
	static public final int C_TYPE_LONG = 8;
	static public final int C_TYPE_STRING = 2;
	static public final int C_TYPE_CHAR = 3;
	static public final int C_TYPE_DATE = 4;
	static public final int C_TYPE_FLOAT = 5;
	static public final int C_TYPE_DOUBLE = 7;
	static public final int C_TYPE_TIMESTAMP = 9;
	static public final int C_TYPE_BLOB = 10;
	static public final int C_TYPE_CLOB = 11;
	/** ﾌｨｰﾙﾄﾞ名 */
	private String mFieldName = null;
	/** SQL物理名 */
	private String mPysicalName = null ;
	/** 変数型 */
	private int mFieldType = C_TYPE_NONE;
	/** 変数長 */
	private int mFieldLength =-1;
	/** 列順位INDEX */
	private int mIndex =-1;
	/** 列順位INDEX */
	private boolean mIsCrypt = false;
	/** 列順位INDEX */
	private int mSqlType = -1;
	/** フィールドKEY */
	private int mFieldKey = -1;
	/**
	 * 列長を出力する。
	 * @return 列長
	 */
	public int getFieldLength(){
		return mFieldLength;
	}
	/**
	 * 列名を出力する。
	 * @return 列名
	 */
	public String getFieldName(){
		return mFieldName;
	}
	/**
	 * 列名を出力する。
	 * @return 列名
	 */
	public String getPysicalName(){
		return mPysicalName;
	}
	/**
	 * 列型を出力する。
	 * @return　列型
	 */
	public int getFieldType(){
		return mFieldType;
	}
	/**
	 * 列のキー属性を出力する。
	 * @return 列のキー属性
	 */
	public int getFieldKey(){
		return mFieldKey;
	}
	/**
	 * 行内INDEXを出力する。
	 * @return 行内INDEX
	 */
	public int getIndex(){
		return mIndex;
	}
	/**
	 * 暗号化有無を出力する。
	 * @return 暗号化有無
	 */
	public boolean isCrypt(){
		return mIsCrypt && (mFieldType == C_TYPE_STRING || mFieldType == C_TYPE_CHAR);
	}
	/**
	 * ユニーク列か出力する。
	 * @return　ユニーク列対象boolean
	 */
	public boolean isUniqueKey(){
		return (mFieldKey == C_KEY_UNIQUE);
	}
	/**
	 * 更新対象従属列か出力する。
	 * @return　ユニーク列対象boolean
	 */
	public boolean isUpdateField(){
		return ((mFieldKey == C_KEY_UNIQUE) ||
				(mFieldKey == C_KEY_ROW_VERSION) ||
				(mFieldKey == C_KEY_UPDATE));
	}
	/**
	 * Rowバージョン列か出力する。
	 * @return　ユニーク列対象boolean
	 */
	public boolean isRowVersionField(){
		return (mFieldKey == C_KEY_ROW_VERSION);
	}
	/**
	 * Field長を設定する。
	 * @param l　Field長
	 */
	public void setFieldLength(int l){
		if(mFieldLength == -1){
			mFieldLength = l;
		}
	}
	/**
	 * 列名を設定する。
	 * @param name　列名
	 */
	public void setFieldName(String name){
		if(mFieldName == null){
		    final int index = name.lastIndexOf(' ');
		    if(index == -1 || index == name.length() - 1){
		        mFieldName = name;
		    }else{
		        mPysicalName = name.substring(0, index);
			    mFieldName = name.substring(index + 1);
		    }
		}
	}
	/**
	 * 列タイプを設定する。
	 * @param type
	 */
	public void setFieldType(int type){
		if(mFieldType != C_TYPE_NONE){
			return;
		}
		switch(type){
			case C_TYPE_INT:
				mSqlType = Types.INTEGER;
				break;
			case C_TYPE_LONG:
				mSqlType = Types.INTEGER;
				break;
			case C_TYPE_STRING:
				mSqlType = Types.VARCHAR;
				break;
			case C_TYPE_CHAR:
				mSqlType = Types.CHAR;
				break;
			case C_TYPE_DATE:
				mSqlType = Types.DATE;
				break;
			case C_TYPE_TIMESTAMP:
				mSqlType = Types.TIMESTAMP;
				break;
			case C_TYPE_FLOAT:
				mSqlType = Types.FLOAT;
				break;
			case C_TYPE_DOUBLE:
				mSqlType = Types.DOUBLE;
				break;
			case C_TYPE_BLOB:
				mSqlType = Types.BLOB;
				break;
			case C_TYPE_CLOB:
				mSqlType = Types.CLOB;
				break;
			default:
				mFieldType = C_TYPE_NONE;
				return;
		}
		mFieldType = type;
	}
	/**
	 * 列キーを設定する。
	 * @param key
	 */
	public void setFieldKey(int key){
		if(mFieldKey ==-1){
			switch(key){
				case C_KEY_UNIQUE:
				case C_KEY_ROW_VERSION:
				case C_KEY_READ:
				case C_KEY_UPDATE:
				case C_KEY_DUMMY:
					mFieldKey = key;
					break;
				default:
			}
		}
	}
	/**
	 * 列番号を設定する。
	 * @param l
	 */
	public void setIndex(int l){
		if(mIndex ==-1){
			mIndex = l;
		}
	}
	/**
	 * 暗号化有無を設定する。
	 * @param val 暗号化有無
	 */
	public void setCrypt(boolean val){
		mIsCrypt = val;
	}
	/**
	 *	setSqlType
	 *	@param	arg
	 */
	public void setSqlType(int arg){
		mSqlType = arg;
	}
	/**
	 *	getSqlType
	 *	@return	int
	 */
	public int getSqlType(){
		return mSqlType;
	}
    
	public static int getFieldTypeValue(String type){
		if(C_FIELD_TYPE_VARCHAR.equals(type)){
			return C_TYPE_STRING; 
		}else if(C_FIELD_TYPE_CHAR.equals(type)){
			return C_TYPE_CHAR;
		}else if(C_FIELD_TYPE_LONG.equals(type)){
			return C_TYPE_LONG;
		}else if(C_FIELD_TYPE_INT.equals(type)){
			return C_TYPE_INT;
		}else if(C_FIELD_TYPE_DOUBLE.equals(type)){
			return C_TYPE_DOUBLE;
		}else if(C_FIELD_TYPE_FLOAT.equals(type)){
			return C_TYPE_FLOAT;
		}else if(C_FIELD_TYPE_DATE.equals(type)){
			return C_TYPE_DATE;
		}else if(C_FIELD_TYPE_TIMESTAMP.equals(type)){
			return C_TYPE_TIMESTAMP;
		}else if(C_FIELD_TYPE_BLOB.equals(type)){
			return C_TYPE_BLOB;
		}else if(C_FIELD_TYPE_CLOB.equals(type)){
			return C_TYPE_CLOB;
		}else{
			throw new InvalidSchemaException("Unknown type : " + type);
		}
	}
	
	public static String getFieldTypeString(int type){
		switch(type){
		case C_TYPE_STRING:
			return C_FIELD_TYPE_VARCHAR; 
		case C_TYPE_CHAR:
			return C_FIELD_TYPE_CHAR;
		case C_TYPE_LONG:
			return C_FIELD_TYPE_LONG;
		case C_TYPE_INT:
			return C_FIELD_TYPE_INT;
		case C_TYPE_DOUBLE:
			return C_FIELD_TYPE_DOUBLE;
		case C_TYPE_FLOAT:
			return C_FIELD_TYPE_FLOAT;
		case C_TYPE_DATE:
			return C_FIELD_TYPE_DATE;
		case C_TYPE_TIMESTAMP:
			return C_FIELD_TYPE_TIMESTAMP;
		case C_TYPE_BLOB:
			return C_FIELD_TYPE_BLOB;
		case C_TYPE_CLOB:
			return C_FIELD_TYPE_CLOB;
		default:
			throw new InvalidSchemaException("Unknown type : " + type);
		}
	}
}
