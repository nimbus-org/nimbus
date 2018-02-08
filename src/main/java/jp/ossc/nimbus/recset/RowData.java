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
// インポート
import java.util.*;
import java.io.*;
import java.text.*;

import jp.ossc.nimbus.service.codemaster.CodeMasterUpdateKey;

/**
 * 行データ管理クラス<p>
 * 行データのトランザクション管理を行う。
 * @version $Name:  $
 * @author H.Nakano
 * @since 1.0
 */
public class RowData implements Serializable, Comparable, Cloneable{
	
    private static final long serialVersionUID = 1440044638276534717L;
    
    /** 日付変換文字列 */
	private static final String FORMAT_DATE = "yyyy/MM/dd";
	private static final String FORMAT_TIMESTAMP = "yyyy/MM/dd HH:mm:ss";
	
	/** ユニークキー区切り文字列 */
	static public final char C_KEY_SEPARATOR = '\u001C';
	
	/** 読み込み状態 */
	static public final int E_Record_TypeRead = 0;
	       
	/** 更新状態 */
	static public final  int E_Record_TypeUpdate = 1;
	     
	/** 削除状態 */
	static public final  int E_Record_TypeDelete = 2;
	     
	/** 追加状態 */
	static public final  int E_Record_TypeInsert = 3;
	    
	/** 削除追加状態 */
	static public final  int E_Record_TypeDeleteInsert = 4;
	    
	/** 初期化状態 */
	static public final  int E_Record_TypeIgnore = -1;
	     
	/** トランザクションモード */
	private int mTransactionMode = E_Record_TypeIgnore;
	
	/** 行スキーマ */
	protected RowSchema mRowSchema;
	
	/** 列データ保持配列 */
	private Object[] mFields;
	
	/** 行配列内格納INDEX */
	private int mRowIndex = -1;
	
	/** ユニークKEY*/
	private String mKey = null;

	
	/**
	 * コンストラクタ 
	 * @param rs RowSchema
	 */
	public RowData(RowSchema rs) {
		super();
		/** ローカル宣言 */
		mRowSchema = rs;
		mFields = new Object[rs.size()] ;
	}
	
	/**
	 * 追加・修正された列のみを持つRowDataを生成します。 
	 * @param rs 行スキーマ
	 * @return 追加・修正された列のみを持つRowData
	 */
	protected RowData makeGoneData(RowSchema rs) {
		RowData rd = new RowData(rs);
		ArrayList lst = new ArrayList();
		for (int rcnt=0; rcnt < this.getRowSchema().size(); rcnt++) {
			FieldSchema fs = this.getRowSchema().get(rcnt) ;		
			if (fs.isUpdateField()) {
				lst.add(this.get(rcnt));
			}
		}
		rd.mFields = lst.toArray();
		rd.mTransactionMode = this.getTransactionMode();
		rd.makeUniqueKey();
		return rd ;
	}
	
	/**
	 * 行スキーマを元にフィールドを生成します。
	 */
/* comment out by AVSS Yoshihara 20040510		
	public void createFields() {
		FieldData objNewMember ;
		FieldSchema objSchema ;
		mFields = new Object[mRowSchema.size()];
*/
		/** フィールドデータ作成 */
/*
		for (int rcnt = 0 ;rcnt < mRowSchema.size() ;rcnt++){
			objSchema = mRowSchema.get(rcnt) ;
			objNewMember = new FieldData(objSchema) ;
			mFields.add(objNewMember) ;
		}
	}
*/
	
	/**
	 * KEYを応答します。
	 * @return KEY
	 */
	public String getKey() {
		return mKey;
	}
	
	public String getKey(String[] colNames) {
        if(colNames == null || colNames.length == 0){
	        return getKey();
        }
        if(getRowSchema() == null){
            throw new IllegalArgumentException("Shema not initalize.");
        }
        final int[] colIndexes = new int[colNames.length];
        for(int i = 0; i < colNames.length; i++){
            final FieldSchema field = getRowSchema().get(colNames[i]);
            if(field == null){
                throw new IllegalArgumentException("Field not found : " + colNames[i]);
            }
            colIndexes[i] = field.getIndex();
        }
        return getKey(colIndexes);
	}
	
	public String getKey(int[] colIndexes) {
	    if(colIndexes == null || colIndexes.length == 0){
	        return getKey();
	    }
		StringBuilder ret = new StringBuilder();
		RowSchema rscm = this.getRowSchema() ;
		for (int i = 0; i < colIndexes.length; i++) {
			FieldSchema csm = rscm.get(colIndexes[i]) ;
			if(csm == null){
                throw new IllegalArgumentException("Field not found : " + colIndexes[i]);
			}
			ret.append(this.getString(csm.getIndex()));
			if(i != colIndexes.length - 1){
				ret.append(C_KEY_SEPARATOR);
			}
		}
		return ret.toString();
	}

	/**
	 * 列配列内INDEXを応答します。
	 * @return 列配列INDEX
	 */
	public int getRowIndex() {
		return mRowIndex;
	}

	/**
	 * 行スキーマを応答します。
	 * @return　行スキーマ
	 */
	public RowSchema getRowSchema() {
		return mRowSchema;
	}

	/**
	 * トランザクションモードを応答します。
	 * @return トランザクションモード
	 */
	public int getTransactionMode() {
		return mTransactionMode;
	}

	/**
	 * 行INDEXを設定します。
	 * @param i 行INDEX
	 */
	protected void setRowIndex(int i) {
		mRowIndex = i;
	}

	public void setTransactionModeForce(int trMode) {
		this.mTransactionMode = trMode;
	}
	
	public void remove(){
		setTransactionMode(E_Record_TypeDelete);
	}
	
	/**
	 * トランザクションモードを設定します。
	 * @param trMode トランザクションモード
	 */
	public void setTransactionMode(int trMode) {
		boolean wrtFlg = false ;
		switch (this.getTransactionMode()) {
			/** 読み込み状態の場合 */
			case E_Record_TypeRead:
				if (trMode == E_Record_TypeUpdate ||
						trMode == E_Record_TypeDelete || 
						trMode == E_Record_TypeDeleteInsert) {
					this.mTransactionMode = trMode;
				}
				break ;
			/** 更新状態の場合 */
			case E_Record_TypeUpdate:
				if ( trMode == E_Record_TypeDelete || 
						trMode == E_Record_TypeRead  ||
						trMode == E_Record_TypeDeleteInsert) {
					mTransactionMode = trMode;
				}
				break ;
			/** 削除状態の場合 */
			case E_Record_TypeDelete:
				if (trMode == E_Record_TypeInsert) {
					mTransactionMode = E_Record_TypeUpdate;
				} else if (trMode == E_Record_TypeUpdate || 
							trMode == E_Record_TypeRead ||
							trMode == E_Record_TypeDeleteInsert) {
					mTransactionMode = trMode;
				}
				break ;
			/** 追加状態の場合 */
			case E_Record_TypeInsert:
				if (trMode == E_Record_TypeDelete) {
					mTransactionMode = E_Record_TypeIgnore;
				}
				wrtFlg = true;
				break ;
			/** 削除追加状態の場合 */
			case E_Record_TypeDeleteInsert:
				if (trMode == E_Record_TypeDelete ||
						trMode == E_Record_TypeUpdate ||
						trMode == E_Record_TypeRead) {
					mTransactionMode = trMode;
				}
				break ;
			/** 初期状態の場合 */
			default :
				if (trMode == E_Record_TypeInsert) {
					mTransactionMode = trMode;
					wrtFlg = true;
				} else if (trMode == E_Record_TypeUpdate) {
					mTransactionMode = E_Record_TypeInsert;
					wrtFlg = true;
				} else if (trMode == E_Record_TypeDeleteInsert) {
					mTransactionMode = E_Record_TypeInsert;
					wrtFlg = true;
				} else if (trMode == E_Record_TypeRead) {
					mTransactionMode = trMode;
					wrtFlg = true;
				}
				break;
		}
		if (wrtFlg == true) {
			this.makeUniqueKey();
		}
	}
	
	/**
	 * ユニークKEYを生成します。
	 */
	public void makeUniqueKey() {
		StringBuilder ret = new StringBuilder();
		RowSchema rscm = this.getRowSchema() ;
		for (int rcnt = 0, max = rscm.getUniqueKeySize(); rcnt < max; rcnt++) {
			FieldSchema csm = rscm.getUniqueFieldSchema(rcnt) ;
			ret.append(this.getString(csm.getIndex()));
			if(rcnt != max - 1){
				ret.append(C_KEY_SEPARATOR);
			}
		}
		mKey = ret.toString();
	}
	
	/**
	 * 列出力のListIteratorを応答します。
	 * @return　ListIterator
	 */
	public ListIterator listIterator() {
		return Arrays.asList(this.mFields).listIterator();
	}
	
	/**
	 * 列番号指定で列データを応答します。
	 * @param index　配列番号
	 * @return　列データ
	 */
	public Object get(int index) {
		return this.mFields[index];
	}
	
	/**
	 * 列名指定で列データを応答します。
	 * @param key 列名
	 * @return　列データ
	 */
	public Object get(String key) {
		return this.mFields[getFieldSchema(key).getIndex()];
	}
    
    private FieldSchema getFieldSchema(String name) throws InvalidSchemaException{
        if(mRowSchema == null){
            throw new InvalidSchemaException("RowSchema is null.");
        }
        FieldSchema field = mRowSchema.get(name);
        if(field == null){
            final StringBuilder buf = new StringBuilder();
            for(int i = 0, imax = mRowSchema.size(); i < imax; i++){
                buf.append(mRowSchema.get(i).getFieldName());
                if(i != imax - 1){
                    buf.append(',');
                }
            }
            throw new InvalidSchemaException(
                "No such field : name=" + name
                    + ", fieldNames=" + buf
            );
        }
        return field;
    }
    
    private FieldSchema getFieldSchema(int index) throws InvalidSchemaException{
        if(mRowSchema == null){
            throw new InvalidSchemaException("RowSchema is null.");
        }
        FieldSchema field = mRowSchema.get(index);
        if(field == null){
            throw new InvalidSchemaException(
                "No such field : index=" + index
                    + ", fieldLength=" + mRowSchema.size()
            );
        }
        return field;
    }
    
	/**
	 * 列番号指定で列データをString型変換したデータを応答します。
	 * @return　列データ
	 */
	public String getString(int index) {
		int type = getFieldSchema(index).getFieldType();
		Object obj = mFields[index];
		if(obj == null){
			return null;
		}
		String ret = null;
		switch (type) {
			case FieldSchema.C_TYPE_CHAR:
				ret = (String)obj;
				break;
			case FieldSchema.C_TYPE_STRING:
				ret = (String)obj;
				break;
			case FieldSchema.C_TYPE_DATE:
				SimpleDateFormat sf = new SimpleDateFormat(FORMAT_DATE);
				ret = sf.format((Date)obj);
				break;
			case FieldSchema.C_TYPE_TIMESTAMP:
				SimpleDateFormat sf2 = new SimpleDateFormat(FORMAT_TIMESTAMP);
				ret = sf2.format(new Date(((java.sql.Timestamp)obj).getTime()));
				break;
			case FieldSchema.C_TYPE_BLOB:
				ret = new String((byte[])obj);
				break;
			case FieldSchema.C_TYPE_CLOB:
				ret = new String((char[])obj);
				break;
			case FieldSchema.C_TYPE_LONG:
			case FieldSchema.C_TYPE_INT:
			case FieldSchema.C_TYPE_FLOAT:
			case FieldSchema.C_TYPE_DOUBLE:
				ret = obj.toString();
			default:
				break;
		}			
		return ret;
	}
	/**
	 * 列番号指定で列データをSQLタイプ型変換したデータを応答します。
	 * @return　列データ
	 */
	public Object getSqlTypeValue(int index) {
		int type = getFieldSchema(index).getFieldType();
		Object obj = mFields[index];
		if(obj == null){
			return null;
		}
		switch(type){
			case FieldSchema.C_TYPE_DATE:
				final Date dt = (Date)obj;
				obj = new java.sql.Date(dt.getTime());
				break;
			case FieldSchema.C_TYPE_TIMESTAMP:
				final Date dt2 = (Date)obj;
				obj = new java.sql.Timestamp(dt2.getTime());
				break;
			default:
				break;
		}			
		return obj;
	}

	/**
	 * 列番号指定でString型の列データを応答します。
	 * @param index　配列番号
	 * @return　データ
	 */
	public String getStringValue(int index) {
	    FieldSchema fs = getFieldSchema(index);
		switch (fs.getFieldType()) {
			case FieldSchema.C_TYPE_CHAR:
			case FieldSchema.C_TYPE_STRING:
        		return (String)mFields[index];
			case FieldSchema.C_TYPE_BLOB:
        		return mFields[index] == null ? null : new String((byte[])mFields[index]);
			case FieldSchema.C_TYPE_CLOB:
        		return mFields[index] == null ? null : new String((char[])mFields[index]);
			default:
				throw new InvalidSchemaException(
				    "Type of " + fs.getFieldName() + " is "
				        + FieldSchema.getFieldTypeString(fs.getFieldType())
				);
		}
	}
	
	/**
	 * 列番号指定でDate型の列データを応答します。
	 * @param index　配列番号
	 * @return　データ
	 */
	public Date getDateValue(int index) {
	    FieldSchema fs = getFieldSchema(index);
		switch (fs.getFieldType()) {
			case FieldSchema.C_TYPE_DATE:
			case FieldSchema.C_TYPE_TIMESTAMP:
				break;
			default:
				throw new InvalidSchemaException(
				    "Type of " + fs.getFieldName() + " is "
				        + FieldSchema.getFieldTypeString(fs.getFieldType())
				);
		}
		return (Date)mFields[index];
	}
	
	/**
	 * 列番号指定でbyte[]型の列データを応答します。
	 * @param index 配列番号
	 * @return データ
	 */
	public byte[] getBytesValue(int index) {
	    FieldSchema fs = getFieldSchema(index);
		switch (fs.getFieldType()) {
			case FieldSchema.C_TYPE_BLOB:
				break;
			default:
				throw new InvalidSchemaException(
				    "Type of " + fs.getFieldName() + " is "
				        + FieldSchema.getFieldTypeString(fs.getFieldType())
				);
		}
		return (byte[])mFields[index];
	}
	
	/**
	 * 列番号指定でInputStream型の列データを応答します。
	 * @param index 配列番号
	 * @return データ
	 */
	public InputStream getInputStreamValue(int index) {
	    FieldSchema fs = getFieldSchema(index);
		switch (fs.getFieldType()) {
			case FieldSchema.C_TYPE_BLOB:
				break;
			default:
				throw new InvalidSchemaException(
				    "Type of " + fs.getFieldName() + " is "
				        + FieldSchema.getFieldTypeString(fs.getFieldType())
				);
		}
		return new ByteArrayInputStream((byte[])mFields[index]);
	}
	
	/**
	 * 列番号指定でReader型の列データを応答します。
	 * @param index 配列番号
	 * @return データ
	 */
	public Reader getReaderValue(int index) {
	    FieldSchema fs = getFieldSchema(index);
		switch (fs.getFieldType()) {
			case FieldSchema.C_TYPE_CLOB:
				break;
			default:
				throw new InvalidSchemaException(
				    "Type of " + fs.getFieldName() + " is "
				        + FieldSchema.getFieldTypeString(fs.getFieldType())
				);
		}
		return new CharArrayReader((char[])mFields[index]);
	}

	/**
	 * 列番号指定でInteger型の列データを応答します。
	 * @param index　配列番号
	 * @return　データ
	 */
	public Integer getIntegerValue(int index) {
	    FieldSchema fs = getFieldSchema(index);
		switch (fs.getFieldType()) {
			case FieldSchema.C_TYPE_INT:
				break;
			default:
				throw new InvalidSchemaException(
				    "Type of " + fs.getFieldName() + " is "
				        + FieldSchema.getFieldTypeString(fs.getFieldType())
				);
		}
		Number tmp = (Number)mFields[index];
		if(tmp == null || tmp instanceof Integer){
			return (Integer)tmp;
		}else{
			return new Integer(tmp.intValue());
		}
	}
	
	/**
	 * 列番号指定でint型の列データを応答します。
	 * @param index　配列番号
	 * @return　データ
	 */
	public int getIntValue(int index) {
	    FieldSchema fs = getFieldSchema(index);
		switch (fs.getFieldType()) {
			case FieldSchema.C_TYPE_INT:
				break;
			default:
				throw new InvalidSchemaException(
				    "Type of " + fs.getFieldName() + " is "
				        + FieldSchema.getFieldTypeString(fs.getFieldType())
				);
		}
		Number tmp = (Number)mFields[index];
		if(tmp == null){
		    throw new NullPointerException(fs.getFieldName() + " is null.");
		}
		return tmp.intValue();
	}
	
	/**
	 * 列番号指定でLong型の列データを応答します。
	 * @param index　配列番号
	 * @return　データ
	 */
	public Long getLongValue(int index) {
	    FieldSchema fs = getFieldSchema(index);
		switch (fs.getFieldType()) {
			case FieldSchema.C_TYPE_LONG:
				break;
			default:
				throw new InvalidSchemaException(
				    "Type of " + fs.getFieldName() + " is "
				        + FieldSchema.getFieldTypeString(fs.getFieldType())
				);
		}
		Number tmp = (Number)mFields[index];
		if(tmp == null || tmp instanceof Long){
			return (Long)tmp;
		}else{
			return new Long(tmp.longValue());
		}
	}
	
	/**
	 * 列番号指定でlong型の列データを応答します。
	 * @param index　配列番号
	 * @return　データ
	 */
	public long getPrimitiveLongValue(int index) {
	    FieldSchema fs = getFieldSchema(index);
		switch (fs.getFieldType()) {
			case FieldSchema.C_TYPE_LONG:
				break;
			default:
				throw new InvalidSchemaException(
				    "Type of " + fs.getFieldName() + " is "
				        + FieldSchema.getFieldTypeString(fs.getFieldType())
				);
		}
		Number tmp = (Number)mFields[index];
		if(tmp == null){
		    throw new NullPointerException(fs.getFieldName() + " is null.");
		}
		return tmp.longValue();
	}
	
	/**
	 * 列番号指定でFloat型の列データを応答します。
	 * @param index　配列番号
	 * @return　データ
	 */
	public Float getFloatValue(int index) {
	    FieldSchema fs = getFieldSchema(index);
		switch (fs.getFieldType()) {
			case FieldSchema.C_TYPE_FLOAT:
				break;
			default:
				throw new InvalidSchemaException(
				    "Type of " + fs.getFieldName() + " is "
				        + FieldSchema.getFieldTypeString(fs.getFieldType())
				);
		}
		Number tmp = (Number)mFields[index];
		if(tmp == null || tmp instanceof Float){
			return (Float)tmp;
		}else{
			return new Float(tmp.floatValue());
		}
	}
	
	/**
	 * 列番号指定でfloat型の列データを応答します。
	 * @param index　配列番号
	 * @return　データ
	 */
	public float getPrimitiveFloatValue(int index) {
	    FieldSchema fs = getFieldSchema(index);
		switch (fs.getFieldType()) {
			case FieldSchema.C_TYPE_FLOAT:
				break;
			default:
				throw new InvalidSchemaException(
				    "Type of " + fs.getFieldName() + " is "
				        + FieldSchema.getFieldTypeString(fs.getFieldType())
				);
		}
		Number tmp = (Number)mFields[index];
		if(tmp == null){
		    throw new NullPointerException(fs.getFieldName() + " is null.");
		}
		return tmp.floatValue();
	}
	
	/**
	 * 列番号指定でDouble型の列データを応答します。
	 * @param index　配列番号
	 * @return　データ
	 */
	public Double getDoubleValue(int index) {
	    FieldSchema fs = getFieldSchema(index);
		switch (fs.getFieldType()) {
			case FieldSchema.C_TYPE_DOUBLE:
				break;
			default:
				throw new InvalidSchemaException(
				    "Type of " + fs.getFieldName() + " is "
				        + FieldSchema.getFieldTypeString(fs.getFieldType())
				);
		}
		Number tmp = (Number)mFields[index];
		if(tmp == null || tmp instanceof Double){
			return (Double)tmp;
		}else{
			return new Double(tmp.doubleValue());
		}
	}
	
	/**
	 * 列番号指定でdouble型の列データを応答します。
	 * @param index　配列番号
	 * @return　データ
	 */
	public double getPrimitiveDoubleValue(int index) {
	    FieldSchema fs = getFieldSchema(index);
		switch (fs.getFieldType()) {
			case FieldSchema.C_TYPE_DOUBLE:
				break;
			default:
				throw new InvalidSchemaException(
				    "Type of " + fs.getFieldName() + " is "
				        + FieldSchema.getFieldTypeString(fs.getFieldType())
				);
		}
		Number tmp = (Number)mFields[index];
		if(tmp == null){
		    throw new NullPointerException(fs.getFieldName() + " is null.");
		}
		return tmp.doubleValue();
	}

	/**
	 * 列名指定で列データをSQLタイプ型変換したデータを応答します。
	 * @return　列データ
	 */
	public Object getSqlTypeValue(String key) {
		return this.getSqlTypeValue(getFieldSchema(key).getIndex());
	}
	/**
	 * 列名指定で列データをString型変換したデータを応答します。
	 * @return　列データ
	 */
	public String getString(String key) {
		return this.getString(getFieldSchema(key).getIndex());
	}
	
	/**
	 * 列名指定でString型の列データを応答します。
	 * @param key 列名
	 * @return　列データ
	 */
	public String getStringValue(String key) {
		return this.getStringValue(getFieldSchema(key).getIndex());
	}
	
	/**
	 * 列名指定でDate型の列データを応答します。
	 * @param key 列名
	 * @return　列データ
	 */
	public Date getDateValue(String key) {
		return this.getDateValue(getFieldSchema(key).getIndex());
	}
	
	/**
	 * 列番号指定でbyte[]型の列データを応答します。
	 * @param key 列名
	 * @return 列データ
	 */
	public byte[] getBytesValue(String key) {
		return this.getBytesValue(getFieldSchema(key).getIndex());
	}
	
	/**
	 * 列番号指定でInputStream型の列データを応答します。
	 * @param key 列名
	 * @return 列データ
	 */
	public InputStream getInputStreamValue(String key) {
		return this.getInputStreamValue(getFieldSchema(key).getIndex());
	}
	
	/**
	 * 列番号指定でReader型の列データを応答します。
	 * @param key 列名
	 * @return 列データ
	 */
	public Reader getReaderValue(String key) {
		return this.getReaderValue(getFieldSchema(key).getIndex());
	}

	/**
	 * 列名指定でInteger型の列データを応答します。
	 * @param key 列名
	 * @return　列データ
	 */
	public Integer getIntegerValue(String key) {
		return this.getIntegerValue(getFieldSchema(key).getIndex());
	}
	
	/**
	 * 列名指定でint型の列データを応答します。
	 * @param key 列名
	 * @return　列データ
	 */
	public int getIntValue(String key) {
		return this.getIntValue(getFieldSchema(key).getIndex());
	}
	
	/**
	 * 列名指定でLong型の列データを応答します。
	 * @param key 列名
	 * @return　列データ
	 */
	public Long getLongValue(String key) {
		return this.getLongValue(getFieldSchema(key).getIndex());
	}
	
	/**
	 * 列名指定でlong型の列データを応答します。
	 * @param key 列名
	 * @return　列データ
	 */
	public long getPrimitiveLongValue(String key) {
		return this.getPrimitiveLongValue(getFieldSchema(key).getIndex());
	}
	
	/**
	 * 列名指定でFloat型の列データを応答します。
	 * @param key 列名
	 * @return　列データ
	 */
	public Float getFloatValue(String key) {
		return this.getFloatValue(getFieldSchema(key).getIndex());
	}
	
	/**
	 * 列名指定でfloat型の列データを応答します。
	 * @param key 列名
	 * @return　列データ
	 */
	public float getPrimitiveFloatValue(String key) {
		return this.getPrimitiveFloatValue(getFieldSchema(key).getIndex());
	}
	
	/**
	 * 列名指定でDouble型の列データを応答します。
	 * @param key 列名
	 * @return　列データ
	 */
	public Double getDoubleValue(String key) {
		return this.getDoubleValue(getFieldSchema(key).getIndex());
	}
	
	/**
	 * 列名指定でdouble型の列データを応答します。
	 * @param key 列名
	 * @return　列データ
	 */
	public double getPrimitiveDoubleValue(String key) {
		return this.getPrimitiveDoubleValue(getFieldSchema(key).getIndex());
	}

	/**
	 * 列番号指定でデータを設定します。
	 * @param index 列番号
	 * @param value データ
	 */
	public void setValueNative(int index, Object value){
		FieldSchema fs = getFieldSchema(index); 
		if (fs.isUniqueKey()) {
			switch (getTransactionMode()) {
				case RowData.E_Record_TypeRead:
				case RowData.E_Record_TypeUpdate:
				case RowData.E_Record_TypeDelete:
					if (mFields[index] != null) {
						return;
					}
					break;
				default:
					break;
			}
		}
		
		mFields[index] = value;
		
		if (fs.isUniqueKey()) {
			this.makeUniqueKey();
		}
	}

	/**
	 * 列番号指定でString型のデータを設定します。
	 * @param index 列番号
	 * @param value データ
	 */
	public void setValue(int index, String value) {
		FieldSchema fs = getFieldSchema(index); 
		switch (fs.getFieldType()) {
			case FieldSchema.C_TYPE_STRING:
			case FieldSchema.C_TYPE_CHAR:
				setValueNative(index, value);
				break;
			case FieldSchema.C_TYPE_BLOB:
				if(value == null){
    				setValueNative(index, null);
				}else{
    				setValueNative(index, value.getBytes());
				}
				break;
			case FieldSchema.C_TYPE_CLOB:
				if(value == null){
    				setValueNative(index, null);
				}else{
    				setValueNative(index, value.toCharArray());
				}
				break;
			default :
				throw new InvalidSchemaException(
				    "Type of " + fs.getFieldName() + " is "
				        + FieldSchema.getFieldTypeString(fs.getFieldType())
				);
		}
	}

	/**
	 * 列番号指定でDate型のデータを設定します。
	 * @param index 列番号
	 * @param value データ
	 */
	public void setValue(int index, Date value){
		FieldSchema fs = getFieldSchema(index); 
		switch (fs.getFieldType()) {
			case FieldSchema.C_TYPE_DATE:
			case FieldSchema.C_TYPE_TIMESTAMP:
				break;
			default :
				throw new InvalidSchemaException(
				    "Type of " + fs.getFieldName() + " is "
				        + FieldSchema.getFieldTypeString(fs.getFieldType())
				);
		}
		setValueNative(index, value);		
	}

	/**
	 * 列番号指定でbyte[]型のデータを設定します。
	 * @param index 列番号
	 * @param value データ
	 */
	public void setValue(int index, byte[] value){
		FieldSchema fs = getFieldSchema(index); 
		switch (fs.getFieldType()) {
			case FieldSchema.C_TYPE_BLOB:
				break;
			default :
				throw new InvalidSchemaException(
				    "Type of " + fs.getFieldName() + " is "
				        + FieldSchema.getFieldTypeString(fs.getFieldType())
				);
		}
		if (mRowSchema.get(index).getFieldLength() > 0) {
			if (value != null) {
				if (value.length > mRowSchema.get(index).getFieldLength()) {
    				throw new InvalidSchemaException(
    				    "Length is over. Length of "
    				        + fs.getFieldName() + " is " + fs.getFieldLength()
    				);
				}
			}
		}
		setValueNative(index, value);
	}

	/**
	 * 列番号指定でストリーム型のデータを設定します。
	 * @param index 列番号
	 * @param value データ
	 */
	public void setValue(int index, InputStream value) throws IOException{
		FieldSchema fs = getFieldSchema(index); 
		switch (fs.getFieldType()) {
			case FieldSchema.C_TYPE_BLOB:
				break;
			default :
				throw new InvalidSchemaException(
				    "Type of " + fs.getFieldName() + " is "
				        + FieldSchema.getFieldTypeString(fs.getFieldType())
				);
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int length = 0;
		byte[] buf = new byte[1024];
		while((length = value.read(buf)) != -1){
		    baos.write(buf, 0, length);
		}
		byte[] bytes = baos.toByteArray();
		if (mRowSchema.get(index).getFieldLength() > 0) {
			if (bytes != null) {
				if (bytes.length > mRowSchema.get(index).getFieldLength()) {
    				throw new InvalidSchemaException(
    				    "Length is over. Length of "
    				        + fs.getFieldName() + " is " + fs.getFieldLength()
    				);
				}
			}
		}
		setValueNative(index, bytes);
	}

	/**
	 * 列番号指定でchar[]型のデータを設定します。
	 * @param index 列番号
	 * @param value データ
	 */
	public void setValue(int index, char[] value){
		FieldSchema fs = getFieldSchema(index); 
		switch (fs.getFieldType()) {
			case FieldSchema.C_TYPE_CLOB:
				break;
			default :
				throw new InvalidSchemaException(
				    "Type of " + fs.getFieldName() + " is "
				        + FieldSchema.getFieldTypeString(fs.getFieldType())
				);
		}
		if (mRowSchema.get(index).getFieldLength() > 0) {
			if (value != null) {
				if (value.length > mRowSchema.get(index).getFieldLength()) {
    				throw new InvalidSchemaException(
    				    "Length is over. Length of "
    				        + fs.getFieldName() + " is " + fs.getFieldLength()
    				);
				}
			}
		}
		setValueNative(index, value);
	}

	/**
	 * 列番号指定で文字ストリーム型のデータを設定します。
	 * @param index 列番号
	 * @param value データ
	 */
	public void setValue(int index, Reader value) throws IOException{
		FieldSchema fs = getFieldSchema(index); 
		switch (fs.getFieldType()) {
			case FieldSchema.C_TYPE_BLOB:
				break;
			default :
				throw new InvalidSchemaException(
				    "Type of " + fs.getFieldName() + " is "
				        + FieldSchema.getFieldTypeString(fs.getFieldType())
				);
		}
		CharArrayWriter caw = new CharArrayWriter();
		int length = 0;
		char[] buf = new char[1024];
		while((length = value.read(buf)) != -1){
		    caw.write(buf, 0, length);
		}
		char[] chars = caw.toCharArray();
		if (mRowSchema.get(index).getFieldLength() > 0) {
			if (chars != null) {
				if (chars.length > mRowSchema.get(index).getFieldLength()) {
    				throw new InvalidSchemaException(
    				    "Length is over. Length of "
    				        + fs.getFieldName() + " is " + fs.getFieldLength()
    				);
				}
			}
		}
		setValueNative(index, chars);
	}

	/**
	 * 列番号指定でInteger型のデータを設定します。
	 * @param index 列番号
	 * @param value データ
	 */
	public void setValue(int index, Integer value) {
		FieldSchema fs = getFieldSchema(index); 
		switch (fs.getFieldType()) {
			case FieldSchema.C_TYPE_INT:
				break;
			default :
				throw new InvalidSchemaException(
				    "Type of " + fs.getFieldName() + " is "
				        + FieldSchema.getFieldTypeString(fs.getFieldType())
				);
		}
		if (value != null && mRowSchema.get(index).getFieldLength() > 0) {
			if (value.intValue() != 0) {
				String tmp = value.toString();
				tmp = tmp.replaceAll("-","");
				if (tmp.length() > mRowSchema.get(index).getFieldLength()) {
    				throw new InvalidSchemaException(
    				    "Length is over. Length of "
    				        + fs.getFieldName() + " is " + fs.getFieldLength()
    				);
				}
			}
		}
		setValueNative(index, value);		
	}

	/**
	 * 列番号指定でint型のデータを設定します。
	 * @param index 列番号
	 * @param value データ
	 */
	public void setValue(int index, int value) {
		FieldSchema fs = getFieldSchema(index); 
		switch (fs.getFieldType()) {
			case FieldSchema.C_TYPE_INT:
				break;
			default:
				throw new InvalidSchemaException(
				    "Type of " + fs.getFieldName() + " is "
				        + FieldSchema.getFieldTypeString(fs.getFieldType())
				);
		}
		if (mRowSchema.get(index).getFieldLength() > 0) {
			if (value != 0) {
				String tmp = Integer.toString(value);
				tmp = tmp.replaceAll("-","");
				if (tmp.length() > mRowSchema.get(index).getFieldLength()) {
    				throw new InvalidSchemaException(
    				    "Length is over. Length of "
    				        + fs.getFieldName() + " is " + fs.getFieldLength()
    				);
				}
			}
		}
		setValueNative(index, new Integer(value));		
	}

	/**
	 * 列番号指定でLong型のデータを設定します。
	 * @param index 列番号
	 * @param value データ
	 */
	public void setValue(int index, Long value) {
		FieldSchema fs = getFieldSchema(index); 
		switch (fs.getFieldType()) {
			case FieldSchema.C_TYPE_LONG:
				break;
			default:
				throw new InvalidSchemaException(
				    "Type of " + fs.getFieldName() + " is "
				        + FieldSchema.getFieldTypeString(fs.getFieldType())
				);
		}
	 	if (value != null && mRowSchema.get(index).getFieldLength() > 0){
			if (value.longValue()!=0) {
				String tmp = value.toString();
				tmp = tmp.replaceAll("-","");
				if (tmp.length() > mRowSchema.get(index).getFieldLength()) {
    				throw new InvalidSchemaException(
    				    "Length is over. Length of "
    				        + fs.getFieldName() + " is " + fs.getFieldLength()
    				);
				}
			}
		}
		setValueNative(index, value);
	}
	
	/**
	 * 列番号指定でlong型のデータを設定します。
	 * @param index 列番号
	 * @param value データ
	 */
	public void setValue(int index, long value) {
		FieldSchema fs = getFieldSchema(index); 
		switch (fs.getFieldType()) {
			case FieldSchema.C_TYPE_LONG :
				break ;
			default :
				throw new InvalidSchemaException(
				    "Type of " + fs.getFieldName() + " is "
				        + FieldSchema.getFieldTypeString(fs.getFieldType())
				);
		}
		if (mRowSchema.get(index).getFieldLength() > 0) {
			if (value != 0) {
				String tmp = Long.toString(value);
				tmp = tmp.replaceAll("-","");
				if (tmp.length() > mRowSchema.get(index).getFieldLength()) {
    				throw new InvalidSchemaException(
    				    "Length is over. Length of "
    				        + fs.getFieldName() + " is " + fs.getFieldLength()
    				);
				}
			}
		}
		setValueNative(index, new Long(value));
	}

	/**
	 * 列番号指定でFloat型のデータを設定します。
	 * @param index 列番号
	 * @param value データ
	 */
	public void setValue(int index, Float value) {
		FieldSchema fs = getFieldSchema(index); 
		switch (fs.getFieldType()) {
			case FieldSchema.C_TYPE_FLOAT:
				break;
			default:
				throw new InvalidSchemaException(
				    "Type of " + fs.getFieldName() + " is "
				        + FieldSchema.getFieldTypeString(fs.getFieldType())
				);
		}
		setValueNative(index, value);
	}
	
	/**
	 * 列番号指定でfloat型のデータを設定します。
	 * @param index 列番号
	 * @param value データ
	 */
	public void setValue(int index, float value) {
		FieldSchema fs = getFieldSchema(index); 
		switch (fs.getFieldType()) {
			case FieldSchema.C_TYPE_FLOAT:
				break;
			default:
				throw new InvalidSchemaException(
				    "Type of " + fs.getFieldName() + " is "
				        + FieldSchema.getFieldTypeString(fs.getFieldType())
				);
		}
		setValueNative(index, new Float(value));
	}

	/**
	 * 列番号指定でDouble型のデータを設定します。
	 * @param index 列番号
	 * @param value データ
	 */
	public void setValue(int index, Double value) {
		FieldSchema fs = getFieldSchema(index); 
		switch (fs.getFieldType()) {
			case FieldSchema.C_TYPE_DOUBLE:
				break;
			default:
				throw new InvalidSchemaException(
				    "Type of " + fs.getFieldName() + " is "
				        + FieldSchema.getFieldTypeString(fs.getFieldType())
				);
		}
		setValueNative(index, value);
	}

	/**
	 * 列番号指定でdouble型のデータを設定します。
	 * @param index 列番号
	 * @param value データ
	 */
	public void setValue(int index, double value) {
		FieldSchema fs = getFieldSchema(index); 
		switch (fs.getFieldType()) {
			case FieldSchema.C_TYPE_DOUBLE :
				break;
			default:
				throw new InvalidSchemaException(
				    "Type of " + fs.getFieldName() + " is "
				        + FieldSchema.getFieldTypeString(fs.getFieldType())
				);
		}
		setValueNative(index, new Double(value));
	}

	/**
	 * 列名指定でString型の列データを設定します。
	 * @param key 列名
	 * @param value データ
	 */
	public void setValue(String key, String value) {
		this.setValue(getFieldSchema(key).getIndex(), value);
	}

	/**
	 * 列名指定でDate型の列データを設定します。
	 * @param key 列名
	 * @param value データ
	 */
	public void setValue(String key, Date value) {
		this.setValue(getFieldSchema(key).getIndex(), value);
	}

	/**
	 * 列名指定でbyte[]型の列データを設定します。
	 * @param key 列名
	 * @param value データ
	 */
	public void setValue(String key, byte[] value) {
		this.setValue(getFieldSchema(key).getIndex(), value);
	}

	/**
	 * 列名指定でInputStream型の列データを設定します。
	 * @param key 列名
	 * @param value データ
	 */
	public void setValue(String key, InputStream value) throws IOException{
		this.setValue(getFieldSchema(key).getIndex(), value);
	}

	/**
	 * 列名指定でchar[]型の列データを設定します。
	 * @param key 列名
	 * @param value データ
	 */
	public void setValue(String key, char[] value) {
		this.setValue(getFieldSchema(key).getIndex(), value);
	}

	/**
	 * 列名指定でReader型の列データを設定します。
	 * @param key 列名
	 * @param value データ
	 */
	public void setValue(String key, Reader value) throws IOException{
		this.setValue(getFieldSchema(key).getIndex(), value);
	}

	/**
	 * 列名指定でInteger型の列データを設定します。
	 * @param key 列名
	 * @param value データ
	 */
	public void setValue(String key, Integer value) {
		this.setValue(getFieldSchema(key).getIndex(), value);
	}

	/**
	 * 列名指定でint型の列データを設定します。
	 * @param key 列名
	 * @param value データ
	 */
	public void setValue(String key, int value) {
		this.setValue(getFieldSchema(key).getIndex(), value);
	}

	/**
	 * 列名指定でLong型の列データを設定します。
	 * @param key 列名
	 * @param value データ
	 */
	public void setValue(String key, Long value) {
		this.setValue(getFieldSchema(key).getIndex(), value);
	}

	/**
	 * 列名指定でlong型の列データを設定します。
	 * @param key 列名
	 * @param value データ
	 */
	public void setValue(String key, long value) {
		this.setValue(getFieldSchema(key).getIndex(), value);
	}

	/**
	 * 列名指定でFloat型の列データを設定します。
	 * @param key 列名
	 * @param value データ
	 */
	public void setValue(String key, Float value) {
		this.setValue(getFieldSchema(key).getIndex(), value);
	}

	/**
	 * 列名指定でfloat型の列データを設定します。
	 * @param key 列名
	 * @param value データ
	 */
	public void setValue(String key, float value) {
		this.setValue(getFieldSchema(key).getIndex(), value);
	}

	/**
	 * 列名指定でDouble型の列データを設定します。
	 * @param key 列名
	 * @param value データ
	 */
	public void setValue(String key, Double value) {
		this.setValue(getFieldSchema(key).getIndex(), value);
	}

	/**
	 * 列名指定でdouble型の列データを設定します。
	 * @param key 列名
	 * @param value データ
	 */
	public void setValue(String key, double value) {
		this.setValue(getFieldSchema(key).getIndex(), value);
	}

	/**
	 * 列数を応答します。
	 * @return　列数
	 */
	public int size() {
		return this.mFields.length;
	}
	
	/**
	 * 行データをコピーして応答します。
	 * @return コピーしたRowData
	 */
	public RowData cloneRowData() {
        RowData rd = null;
        try{
            rd = (RowData)clone();
            rd.mFields = new Object[mFields.length];
            // 互換性維持のため、トランザクションモードは、複製しない
            rd.mTransactionMode = E_Record_TypeIgnore;
        }catch(CloneNotSupportedException e){
            //起こらない
            throw new RuntimeException(e);
        }
		for (int rcnt = 0; rcnt < mFields.length; rcnt++) {
			rd.mFields[rcnt] = cloneFieldData(rcnt);
		}
		return rd;
	}

	/**
	 * 列データをコピーして応答します。
	 * @param index 列番号
	 * @return　コピーしたObject
	 */
	private Object cloneFieldData(int index){
		FieldSchema schema = getFieldSchema(index);
		Object ret = mFields[index];
		switch (schema.getFieldType()) {
			case FieldSchema.C_TYPE_DATE:
			case FieldSchema.C_TYPE_TIMESTAMP:
				if (ret != null) {
					Date dt = (Date)ret;
					ret = new Date(dt.getTime());
				}
				break;
			case FieldSchema.C_TYPE_BLOB:
				if (ret != null) {
				    byte[] bytes = (byte[])ret;
				    byte[] newBytes = new byte[bytes.length];
				    System.arraycopy(bytes, 0, newBytes, 0, bytes.length);
				    ret = newBytes;
				}
				break;
			case FieldSchema.C_TYPE_CLOB:
				if (ret != null) {
				    char[] chars = (char[])ret;
				    char[] newChars = new char[chars.length];
				    System.arraycopy(chars, 0, newChars, 0, chars.length);
				    ret = newChars;
				}
				break;
			default:
				break;
		}			
		return ret;
	}

	/**
	 * 列格納配列を応答します。
	 * @return　List
	 */
	private List getFields() {
		return Arrays.asList(mFields);
	}

    public void clear(){
        mTransactionMode = E_Record_TypeIgnore;
        if(mFields != null){
            for(int i = 0; i < mFields.length; i++){
                mFields[i] = null;
            }
        }
        mRowIndex = -1;
        mKey = null;
    }

	/**
	 * データ内容が同値か判定します。
	 * @param rd 比較したいRowData
	 * @return	<code>true</code> 同値
     *				<code>false</code> 非同値
	 */
	public boolean equals(RowData rd){
		boolean ret = true ;
		if (this.getRowSchema() == rd.getRowSchema()) {
			for (int rcnt =0 ; rcnt < this.getFields().size() ;rcnt++) {
				Object obj0 = this.get(rcnt) ;	
				Object obj1 = rd.get(rcnt) ;	
				if (obj0 == null && 
					obj1 == null) {
					continue ;
				} else if (obj0 == null || 
					obj1 == null) {
					ret = false ;
					break ;
				} else if (!obj0.equals(obj1)) {
					ret = false ;
					break ;
				}
			}
		} else {
			ret = false ;
		}
		return ret ;
	}
	
    public int compareTo(Object o){
        if(o == null){
            return 1;
        }
        if(!(o instanceof RowData)){
            return 1;
        }
        if(o == this){
            return 0;
        }
        RowData comp = (RowData)o;
        RowSchema mySchema = getRowSchema();
        RowSchema compSchema = comp.getRowSchema();
        if(mySchema == null && compSchema == null){
            return hashCode() - comp.hashCode();
        }else if(mySchema != null && compSchema == null){
            return 1;
        }else if(mySchema == null && compSchema != null){
            return -1;
        }
        if(!mySchema.equals(compSchema)
            || mySchema.getUniqueKeySize() == 0){
            return hashCode() - comp.hashCode();
        }
        final String myKey = getKey();
        final String compKey = comp.getKey();
        if(myKey == null && compKey == null){
            return hashCode() - comp.hashCode();
        }else if(myKey != null && compKey == null){
            return 1;
        }else if(myKey == null && compKey != null){
            return -1;
        }
        return myKey.compareTo(compKey);
    }
	
    public CodeMasterUpdateKey createCodeMasterUpdateKey(){
        CodeMasterUpdateKey key = new CodeMasterUpdateKey();
        return createCodeMasterUpdateKey(key);
    }
    
    public CodeMasterUpdateKey createCodeMasterUpdateKey(CodeMasterUpdateKey key){
        key.clear();
        RowSchema rowSchema = getRowSchema();
        for(int i = 0, imax = rowSchema.getUniqueKeySize(); i < imax; i++){
            FieldSchema fieldSchema = rowSchema.getUniqueFieldSchema(i);
            key.addKey(
                fieldSchema.getFieldName(),
                get(fieldSchema.getIndex())
            );
        }
        switch(getTransactionMode()){
        case E_Record_TypeUpdate:
        case E_Record_TypeDeleteInsert:
            key.update();
            break;
        case E_Record_TypeDelete:
            key.remove();
            break;
        case E_Record_TypeInsert:
            key.add();
            break;
        case E_Record_TypeRead:
        default:
        }
        return key;
    }
    
    public void setCodeMasterUpdateKey(CodeMasterUpdateKey key){
        setTransactionModeForce(E_Record_TypeIgnore);
        final RowSchema rowSchema = getRowSchema();
        Iterator entries = key.getKeyMap().entrySet().iterator();
        while(entries.hasNext()){
            final Map.Entry entry = (Map.Entry)entries.next();
            final FieldSchema fieldSchema = rowSchema.get((String)entry.getKey());
            if(fieldSchema == null){
                continue;
            }
            setValueNative(fieldSchema.getIndex(), entry.getValue());
        }
        switch(key.getUpdateType()){
        case CodeMasterUpdateKey.UPDATE_TYPE_ADD:
            setTransactionModeForce(E_Record_TypeInsert);
            break;
        case CodeMasterUpdateKey.UPDATE_TYPE_UPDATE:
            setTransactionModeForce(E_Record_TypeUpdate);
            break;
        case CodeMasterUpdateKey.UPDATE_TYPE_REMOVE:
            setTransactionModeForce(E_Record_TypeDelete);
            break;
        default:
            setTransactionModeForce(E_Record_TypeRead);
            break;
        }
    }
}
