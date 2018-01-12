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
package jp.ossc.nimbus.recset;
//インポート
import java.util.*;
import jp.ossc.nimbus.util.*;
/**
 * 行スキーマ管理クラス<p>
 * フィールドスキーマの集合を管理する。
 * @version $Name:  $
 * @author H.Nakano
 * @since 1.0
 */
public class RowSchema implements java.io.Serializable{
	
    private static final long serialVersionUID = 2519505358076142786L;
    
	/** 列名管理ハッシュ */
	private HashMap mFieldHash = new HashMap();
	/** 列順管理リスト */
	private ArrayList mFieldAry = new ArrayList() ;
	/** UniqueKey列管理リスト */
	private ArrayList mUniqueKeyAry = new ArrayList() ;
	/** ハッシュコード */
	private int mHashCode = 0 ;
	
	/**
	 * コンストラクタ
	 */
	public RowSchema(){
	}
	/**
	 * UniqueKeyの数を出力する
	 * @return UniqueKeyの数
	 */
	public int getUniqueKeySize(){
		return this.mUniqueKeyAry.size();
	}
	/**
	 * UniqueKeyの列情報を出力する。
	 * @param index
	 * @return　FieldSchema
	 */
	public FieldSchema getUniqueFieldSchema(int index){
		return (FieldSchema)mUniqueKeyAry.get(index) ;
	}
	/**
	 * 更新用RowSchemaを作成し応答します。
	 * @return 更新用RowSchema
	 */
	public RowSchema makeGoneSchema(){
		RowSchema ret = new RowSchema() ;
		for(int rcnt= 0 ;rcnt<this.mFieldAry.size();rcnt++){
			FieldSchema fs = this.get(rcnt) ;
			FieldSchema newFs = new FieldSchema();
			newFs.setFieldName(fs.getFieldName());
			newFs.setFieldType(fs.getFieldType());
			newFs.setFieldLength(fs.getFieldLength());
			newFs.setFieldKey(fs.getFieldKey());
			newFs.setCrypt(fs.isCrypt());
			if(fs.isUpdateField()){
				ret.add(newFs);
			}
		}
		return ret ;
	}
	
	/**
	 * ハッシュコードを応答します。
	 * @return ハッシュコード
	 */
	public Integer getHashCodeObject() {
		return new Integer(this.mHashCode) ;	
	}

	/**
	 * ハッシュコードを応答します。
	 * @return ハッシュコード
	 * @see java.lang.Object#hashCode()
	 */
	public int	hashCode() {
		return this.mHashCode ;
	}
	
	/**
	 * RowSchemaが同値かどうかを応答します。
	 * RowSchemaのハッシュコードを比較した結果を応答します。
	 * @param another 比較するオブジェクト
	 * @return true 同値<br>
	 * 			false 非道値
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object another){
		if(another == null){
			return false ;
		}else{
			if(this == another){
				return true ;
			}
			if(another instanceof  RowSchema){
				if(this.mHashCode == another.hashCode()){
					return true ;
				}
			}
		}
		return false ;
	}
	
	/**
	 * スキーマ情報を生成する。
	 * スキーマの記述方法は以下の通り。
	 * fieldName,[VARCHAR,CAHR,NUMBER,DATE],FIELDLENGTH,[0:KEY,1:ROWVERSION,2:検索列，3更新列]
	 * 例
	 * REC_ID,CHAR,5,1
	 * SALARY,INT,0,0
	 * INSDATE,DATE,0,0
	 * ROW_VERSIONINT,0,2
	 * @param schema
	 */
	public void initSchema(String schema){
		if(mFieldAry.size() == 0){
			CsvArrayList parser = new CsvArrayList() ;
			parser.splitCL(schema) ;
			for(ListIterator ite = parser.listIterator();ite.hasNext();){
				String fieldStr = (String)ite.next() ; 
				FieldSchema field = makeFieldSchema(fieldStr) ;
				if(field !=null){
					add(field) ;
				}
			}
			this.mHashCode = schema.hashCode() ;
		}
		mFieldAry.trimToSize();
		mUniqueKeyAry.trimToSize();
	}
	
	/**
	 * 文字列指定から列スキーマを生成する。
	 * @param fieldInf　列指定文字列
	 * @return　FieldSchema
	 */
	private FieldSchema makeFieldSchema(String fieldInf){
		if(fieldInf == null || fieldInf.length() == 0){
			return null ;
		}
		CsvArrayList colInf = new CsvArrayList() ;
		final int sz = colInf.split(fieldInf);
		if(sz < 4){
			throw new InvalidSchemaException(fieldInf + "invalid") ; 
		}
		FieldSchema field = new FieldSchema() ;
		// 列名設定
		field.setFieldName(colInf.getStr(0)) ;
		// データ型設定
		String tp = colInf.getStr(1);
		field.setFieldType(FieldSchema.getFieldTypeValue(tp)); 
		//レングス設定
		field.setFieldLength(Integer.parseInt(colInf.getStr(2))) ;
		//ＫＥＹ属性設定
		field.setFieldKey(Integer.parseInt(colInf.getStr(3))) ;
		if(sz > 4){
			field.setCrypt(Integer.parseInt(colInf.getStr(4)) > 0);
		}
		return field ;
	}

	/**
	 * 列サイズを出力する。
	 * @return　列数
	 */
	public int size(){
		if(mFieldAry == null){
			return 0 ;
		}else{
			return mFieldAry.size() ;
		}
	}

	/**
	 * INDEX番号指定で列スキーマを出力する。
	 * @param index
	 * @return　FieldSchema
	 */
	public FieldSchema get(int index){
        if(index < 0 || index >= mFieldAry.size()){
            return null;
        }
		return (FieldSchema)mFieldAry.get(index) ;
	}
	/**
	 * 列名指定で列スキーマを出力する。
	 * @param name
	 * @return　FieldSchema
	 */
	public FieldSchema get(String name){
		return (FieldSchema)mFieldHash.get(name) ;
	}
	
	/**
	 * 列スキーマのリストをListIteratorで応答します。
	 * @return 列スキーマリスト
	 */
	public ListIterator listIterator(){
		return mFieldAry.listIterator();
	}
	
	/**
	 * 列スキーマを追加します。
	 * @param scm
	 */
	private void add(FieldSchema scm){
		if(mFieldHash.containsKey(scm.getFieldName())){
			throw new InvalidSchemaException(scm.getFieldName() + "is duplicate") ;
		}else{
			mFieldHash.put(scm.getFieldName(),scm) ;
			mFieldAry.add(scm) ;
			scm.setIndex(mFieldAry.size() -1) ;
			if(scm.isUniqueKey()){
				this.mUniqueKeyAry.add(scm) ;
			}
		}
	}
	
}
