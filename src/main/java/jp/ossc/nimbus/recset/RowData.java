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
// �p�b�P�[�W
package jp.ossc.nimbus.recset;
// �C���|�[�g
import java.util.*;
import java.io.*;
import java.text.*;

import jp.ossc.nimbus.service.codemaster.CodeMasterUpdateKey;

/**
 * �s�f�[�^�Ǘ��N���X<p>
 * �s�f�[�^�̃g�����U�N�V�����Ǘ����s���B
 * @version $Name:  $
 * @author H.Nakano
 * @since 1.0
 */
public class RowData implements Serializable, Comparable, Cloneable{
	
    private static final long serialVersionUID = 1440044638276534717L;
    
    /** ���t�ϊ������� */
	private static final String FORMAT_DATE = "yyyy/MM/dd";
	private static final String FORMAT_TIMESTAMP = "yyyy/MM/dd HH:mm:ss";
	
	/** ���j�[�N�L�[��؂蕶���� */
	static public final char C_KEY_SEPARATOR = '\u001C';
	
	/** �ǂݍ��ݏ�� */
	static public final int E_Record_TypeRead = 0;
	       
	/** �X�V��� */
	static public final  int E_Record_TypeUpdate = 1;
	     
	/** �폜��� */
	static public final  int E_Record_TypeDelete = 2;
	     
	/** �ǉ���� */
	static public final  int E_Record_TypeInsert = 3;
	    
	/** �폜�ǉ���� */
	static public final  int E_Record_TypeDeleteInsert = 4;
	    
	/** ��������� */
	static public final  int E_Record_TypeIgnore = -1;
	     
	/** �g�����U�N�V�������[�h */
	private int mTransactionMode = E_Record_TypeIgnore;
	
	/** �s�X�L�[�} */
	protected RowSchema mRowSchema;
	
	/** ��f�[�^�ێ��z�� */
	private Object[] mFields;
	
	/** �s�z����i�[INDEX */
	private int mRowIndex = -1;
	
	/** ���j�[�NKEY*/
	private String mKey = null;

	
	/**
	 * �R���X�g���N�^ 
	 * @param rs RowSchema
	 */
	public RowData(RowSchema rs) {
		super();
		/** ���[�J���錾 */
		mRowSchema = rs;
		mFields = new Object[rs.size()] ;
	}
	
	/**
	 * �ǉ��E�C�����ꂽ��݂̂�����RowData�𐶐����܂��B 
	 * @param rs �s�X�L�[�}
	 * @return �ǉ��E�C�����ꂽ��݂̂�����RowData
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
	 * �s�X�L�[�}�����Ƀt�B�[���h�𐶐����܂��B
	 */
/* comment out by AVSS Yoshihara 20040510		
	public void createFields() {
		FieldData objNewMember ;
		FieldSchema objSchema ;
		mFields = new Object[mRowSchema.size()];
*/
		/** �t�B�[���h�f�[�^�쐬 */
/*
		for (int rcnt = 0 ;rcnt < mRowSchema.size() ;rcnt++){
			objSchema = mRowSchema.get(rcnt) ;
			objNewMember = new FieldData(objSchema) ;
			mFields.add(objNewMember) ;
		}
	}
*/
	
	/**
	 * KEY���������܂��B
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
	 * ��z���INDEX���������܂��B
	 * @return ��z��INDEX
	 */
	public int getRowIndex() {
		return mRowIndex;
	}

	/**
	 * �s�X�L�[�}���������܂��B
	 * @return�@�s�X�L�[�}
	 */
	public RowSchema getRowSchema() {
		return mRowSchema;
	}

	/**
	 * �g�����U�N�V�������[�h���������܂��B
	 * @return �g�����U�N�V�������[�h
	 */
	public int getTransactionMode() {
		return mTransactionMode;
	}

	/**
	 * �sINDEX��ݒ肵�܂��B
	 * @param i �sINDEX
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
	 * �g�����U�N�V�������[�h��ݒ肵�܂��B
	 * @param trMode �g�����U�N�V�������[�h
	 */
	public void setTransactionMode(int trMode) {
		boolean wrtFlg = false ;
		switch (this.getTransactionMode()) {
			/** �ǂݍ��ݏ�Ԃ̏ꍇ */
			case E_Record_TypeRead:
				if (trMode == E_Record_TypeUpdate ||
						trMode == E_Record_TypeDelete || 
						trMode == E_Record_TypeDeleteInsert) {
					this.mTransactionMode = trMode;
				}
				break ;
			/** �X�V��Ԃ̏ꍇ */
			case E_Record_TypeUpdate:
				if ( trMode == E_Record_TypeDelete || 
						trMode == E_Record_TypeRead  ||
						trMode == E_Record_TypeDeleteInsert) {
					mTransactionMode = trMode;
				}
				break ;
			/** �폜��Ԃ̏ꍇ */
			case E_Record_TypeDelete:
				if (trMode == E_Record_TypeInsert) {
					mTransactionMode = E_Record_TypeUpdate;
				} else if (trMode == E_Record_TypeUpdate || 
							trMode == E_Record_TypeRead ||
							trMode == E_Record_TypeDeleteInsert) {
					mTransactionMode = trMode;
				}
				break ;
			/** �ǉ���Ԃ̏ꍇ */
			case E_Record_TypeInsert:
				if (trMode == E_Record_TypeDelete) {
					mTransactionMode = E_Record_TypeIgnore;
				}
				wrtFlg = true;
				break ;
			/** �폜�ǉ���Ԃ̏ꍇ */
			case E_Record_TypeDeleteInsert:
				if (trMode == E_Record_TypeDelete ||
						trMode == E_Record_TypeUpdate ||
						trMode == E_Record_TypeRead) {
					mTransactionMode = trMode;
				}
				break ;
			/** ������Ԃ̏ꍇ */
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
	 * ���j�[�NKEY�𐶐����܂��B
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
	 * ��o�͂�ListIterator���������܂��B
	 * @return�@ListIterator
	 */
	public ListIterator listIterator() {
		return Arrays.asList(this.mFields).listIterator();
	}
	
	/**
	 * ��ԍ��w��ŗ�f�[�^���������܂��B
	 * @param index�@�z��ԍ�
	 * @return�@��f�[�^
	 */
	public Object get(int index) {
		return this.mFields[index];
	}
	
	/**
	 * �񖼎w��ŗ�f�[�^���������܂��B
	 * @param key ��
	 * @return�@��f�[�^
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
	 * ��ԍ��w��ŗ�f�[�^��String�^�ϊ������f�[�^���������܂��B
	 * @return�@��f�[�^
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
	 * ��ԍ��w��ŗ�f�[�^��SQL�^�C�v�^�ϊ������f�[�^���������܂��B
	 * @return�@��f�[�^
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
	 * ��ԍ��w���String�^�̗�f�[�^���������܂��B
	 * @param index�@�z��ԍ�
	 * @return�@�f�[�^
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
	 * ��ԍ��w���Date�^�̗�f�[�^���������܂��B
	 * @param index�@�z��ԍ�
	 * @return�@�f�[�^
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
	 * ��ԍ��w���byte[]�^�̗�f�[�^���������܂��B
	 * @param index �z��ԍ�
	 * @return �f�[�^
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
	 * ��ԍ��w���InputStream�^�̗�f�[�^���������܂��B
	 * @param index �z��ԍ�
	 * @return �f�[�^
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
	 * ��ԍ��w���Reader�^�̗�f�[�^���������܂��B
	 * @param index �z��ԍ�
	 * @return �f�[�^
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
	 * ��ԍ��w���Integer�^�̗�f�[�^���������܂��B
	 * @param index�@�z��ԍ�
	 * @return�@�f�[�^
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
	 * ��ԍ��w���int�^�̗�f�[�^���������܂��B
	 * @param index�@�z��ԍ�
	 * @return�@�f�[�^
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
	 * ��ԍ��w���Long�^�̗�f�[�^���������܂��B
	 * @param index�@�z��ԍ�
	 * @return�@�f�[�^
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
	 * ��ԍ��w���long�^�̗�f�[�^���������܂��B
	 * @param index�@�z��ԍ�
	 * @return�@�f�[�^
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
	 * ��ԍ��w���Float�^�̗�f�[�^���������܂��B
	 * @param index�@�z��ԍ�
	 * @return�@�f�[�^
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
	 * ��ԍ��w���float�^�̗�f�[�^���������܂��B
	 * @param index�@�z��ԍ�
	 * @return�@�f�[�^
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
	 * ��ԍ��w���Double�^�̗�f�[�^���������܂��B
	 * @param index�@�z��ԍ�
	 * @return�@�f�[�^
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
	 * ��ԍ��w���double�^�̗�f�[�^���������܂��B
	 * @param index�@�z��ԍ�
	 * @return�@�f�[�^
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
	 * �񖼎w��ŗ�f�[�^��SQL�^�C�v�^�ϊ������f�[�^���������܂��B
	 * @return�@��f�[�^
	 */
	public Object getSqlTypeValue(String key) {
		return this.getSqlTypeValue(getFieldSchema(key).getIndex());
	}
	/**
	 * �񖼎w��ŗ�f�[�^��String�^�ϊ������f�[�^���������܂��B
	 * @return�@��f�[�^
	 */
	public String getString(String key) {
		return this.getString(getFieldSchema(key).getIndex());
	}
	
	/**
	 * �񖼎w���String�^�̗�f�[�^���������܂��B
	 * @param key ��
	 * @return�@��f�[�^
	 */
	public String getStringValue(String key) {
		return this.getStringValue(getFieldSchema(key).getIndex());
	}
	
	/**
	 * �񖼎w���Date�^�̗�f�[�^���������܂��B
	 * @param key ��
	 * @return�@��f�[�^
	 */
	public Date getDateValue(String key) {
		return this.getDateValue(getFieldSchema(key).getIndex());
	}
	
	/**
	 * ��ԍ��w���byte[]�^�̗�f�[�^���������܂��B
	 * @param key ��
	 * @return ��f�[�^
	 */
	public byte[] getBytesValue(String key) {
		return this.getBytesValue(getFieldSchema(key).getIndex());
	}
	
	/**
	 * ��ԍ��w���InputStream�^�̗�f�[�^���������܂��B
	 * @param key ��
	 * @return ��f�[�^
	 */
	public InputStream getInputStreamValue(String key) {
		return this.getInputStreamValue(getFieldSchema(key).getIndex());
	}
	
	/**
	 * ��ԍ��w���Reader�^�̗�f�[�^���������܂��B
	 * @param key ��
	 * @return ��f�[�^
	 */
	public Reader getReaderValue(String key) {
		return this.getReaderValue(getFieldSchema(key).getIndex());
	}

	/**
	 * �񖼎w���Integer�^�̗�f�[�^���������܂��B
	 * @param key ��
	 * @return�@��f�[�^
	 */
	public Integer getIntegerValue(String key) {
		return this.getIntegerValue(getFieldSchema(key).getIndex());
	}
	
	/**
	 * �񖼎w���int�^�̗�f�[�^���������܂��B
	 * @param key ��
	 * @return�@��f�[�^
	 */
	public int getIntValue(String key) {
		return this.getIntValue(getFieldSchema(key).getIndex());
	}
	
	/**
	 * �񖼎w���Long�^�̗�f�[�^���������܂��B
	 * @param key ��
	 * @return�@��f�[�^
	 */
	public Long getLongValue(String key) {
		return this.getLongValue(getFieldSchema(key).getIndex());
	}
	
	/**
	 * �񖼎w���long�^�̗�f�[�^���������܂��B
	 * @param key ��
	 * @return�@��f�[�^
	 */
	public long getPrimitiveLongValue(String key) {
		return this.getPrimitiveLongValue(getFieldSchema(key).getIndex());
	}
	
	/**
	 * �񖼎w���Float�^�̗�f�[�^���������܂��B
	 * @param key ��
	 * @return�@��f�[�^
	 */
	public Float getFloatValue(String key) {
		return this.getFloatValue(getFieldSchema(key).getIndex());
	}
	
	/**
	 * �񖼎w���float�^�̗�f�[�^���������܂��B
	 * @param key ��
	 * @return�@��f�[�^
	 */
	public float getPrimitiveFloatValue(String key) {
		return this.getPrimitiveFloatValue(getFieldSchema(key).getIndex());
	}
	
	/**
	 * �񖼎w���Double�^�̗�f�[�^���������܂��B
	 * @param key ��
	 * @return�@��f�[�^
	 */
	public Double getDoubleValue(String key) {
		return this.getDoubleValue(getFieldSchema(key).getIndex());
	}
	
	/**
	 * �񖼎w���double�^�̗�f�[�^���������܂��B
	 * @param key ��
	 * @return�@��f�[�^
	 */
	public double getPrimitiveDoubleValue(String key) {
		return this.getPrimitiveDoubleValue(getFieldSchema(key).getIndex());
	}

	/**
	 * ��ԍ��w��Ńf�[�^��ݒ肵�܂��B
	 * @param index ��ԍ�
	 * @param value �f�[�^
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
	 * ��ԍ��w���String�^�̃f�[�^��ݒ肵�܂��B
	 * @param index ��ԍ�
	 * @param value �f�[�^
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
	 * ��ԍ��w���Date�^�̃f�[�^��ݒ肵�܂��B
	 * @param index ��ԍ�
	 * @param value �f�[�^
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
	 * ��ԍ��w���byte[]�^�̃f�[�^��ݒ肵�܂��B
	 * @param index ��ԍ�
	 * @param value �f�[�^
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
	 * ��ԍ��w��ŃX�g���[���^�̃f�[�^��ݒ肵�܂��B
	 * @param index ��ԍ�
	 * @param value �f�[�^
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
	 * ��ԍ��w���char[]�^�̃f�[�^��ݒ肵�܂��B
	 * @param index ��ԍ�
	 * @param value �f�[�^
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
	 * ��ԍ��w��ŕ����X�g���[���^�̃f�[�^��ݒ肵�܂��B
	 * @param index ��ԍ�
	 * @param value �f�[�^
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
	 * ��ԍ��w���Integer�^�̃f�[�^��ݒ肵�܂��B
	 * @param index ��ԍ�
	 * @param value �f�[�^
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
	 * ��ԍ��w���int�^�̃f�[�^��ݒ肵�܂��B
	 * @param index ��ԍ�
	 * @param value �f�[�^
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
	 * ��ԍ��w���Long�^�̃f�[�^��ݒ肵�܂��B
	 * @param index ��ԍ�
	 * @param value �f�[�^
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
	 * ��ԍ��w���long�^�̃f�[�^��ݒ肵�܂��B
	 * @param index ��ԍ�
	 * @param value �f�[�^
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
	 * ��ԍ��w���Float�^�̃f�[�^��ݒ肵�܂��B
	 * @param index ��ԍ�
	 * @param value �f�[�^
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
	 * ��ԍ��w���float�^�̃f�[�^��ݒ肵�܂��B
	 * @param index ��ԍ�
	 * @param value �f�[�^
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
	 * ��ԍ��w���Double�^�̃f�[�^��ݒ肵�܂��B
	 * @param index ��ԍ�
	 * @param value �f�[�^
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
	 * ��ԍ��w���double�^�̃f�[�^��ݒ肵�܂��B
	 * @param index ��ԍ�
	 * @param value �f�[�^
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
	 * �񖼎w���String�^�̗�f�[�^��ݒ肵�܂��B
	 * @param key ��
	 * @param value �f�[�^
	 */
	public void setValue(String key, String value) {
		this.setValue(getFieldSchema(key).getIndex(), value);
	}

	/**
	 * �񖼎w���Date�^�̗�f�[�^��ݒ肵�܂��B
	 * @param key ��
	 * @param value �f�[�^
	 */
	public void setValue(String key, Date value) {
		this.setValue(getFieldSchema(key).getIndex(), value);
	}

	/**
	 * �񖼎w���byte[]�^�̗�f�[�^��ݒ肵�܂��B
	 * @param key ��
	 * @param value �f�[�^
	 */
	public void setValue(String key, byte[] value) {
		this.setValue(getFieldSchema(key).getIndex(), value);
	}

	/**
	 * �񖼎w���InputStream�^�̗�f�[�^��ݒ肵�܂��B
	 * @param key ��
	 * @param value �f�[�^
	 */
	public void setValue(String key, InputStream value) throws IOException{
		this.setValue(getFieldSchema(key).getIndex(), value);
	}

	/**
	 * �񖼎w���char[]�^�̗�f�[�^��ݒ肵�܂��B
	 * @param key ��
	 * @param value �f�[�^
	 */
	public void setValue(String key, char[] value) {
		this.setValue(getFieldSchema(key).getIndex(), value);
	}

	/**
	 * �񖼎w���Reader�^�̗�f�[�^��ݒ肵�܂��B
	 * @param key ��
	 * @param value �f�[�^
	 */
	public void setValue(String key, Reader value) throws IOException{
		this.setValue(getFieldSchema(key).getIndex(), value);
	}

	/**
	 * �񖼎w���Integer�^�̗�f�[�^��ݒ肵�܂��B
	 * @param key ��
	 * @param value �f�[�^
	 */
	public void setValue(String key, Integer value) {
		this.setValue(getFieldSchema(key).getIndex(), value);
	}

	/**
	 * �񖼎w���int�^�̗�f�[�^��ݒ肵�܂��B
	 * @param key ��
	 * @param value �f�[�^
	 */
	public void setValue(String key, int value) {
		this.setValue(getFieldSchema(key).getIndex(), value);
	}

	/**
	 * �񖼎w���Long�^�̗�f�[�^��ݒ肵�܂��B
	 * @param key ��
	 * @param value �f�[�^
	 */
	public void setValue(String key, Long value) {
		this.setValue(getFieldSchema(key).getIndex(), value);
	}

	/**
	 * �񖼎w���long�^�̗�f�[�^��ݒ肵�܂��B
	 * @param key ��
	 * @param value �f�[�^
	 */
	public void setValue(String key, long value) {
		this.setValue(getFieldSchema(key).getIndex(), value);
	}

	/**
	 * �񖼎w���Float�^�̗�f�[�^��ݒ肵�܂��B
	 * @param key ��
	 * @param value �f�[�^
	 */
	public void setValue(String key, Float value) {
		this.setValue(getFieldSchema(key).getIndex(), value);
	}

	/**
	 * �񖼎w���float�^�̗�f�[�^��ݒ肵�܂��B
	 * @param key ��
	 * @param value �f�[�^
	 */
	public void setValue(String key, float value) {
		this.setValue(getFieldSchema(key).getIndex(), value);
	}

	/**
	 * �񖼎w���Double�^�̗�f�[�^��ݒ肵�܂��B
	 * @param key ��
	 * @param value �f�[�^
	 */
	public void setValue(String key, Double value) {
		this.setValue(getFieldSchema(key).getIndex(), value);
	}

	/**
	 * �񖼎w���double�^�̗�f�[�^��ݒ肵�܂��B
	 * @param key ��
	 * @param value �f�[�^
	 */
	public void setValue(String key, double value) {
		this.setValue(getFieldSchema(key).getIndex(), value);
	}

	/**
	 * �񐔂��������܂��B
	 * @return�@��
	 */
	public int size() {
		return this.mFields.length;
	}
	
	/**
	 * �s�f�[�^���R�s�[���ĉ������܂��B
	 * @return �R�s�[����RowData
	 */
	public RowData cloneRowData() {
        RowData rd = null;
        try{
            rd = (RowData)clone();
            rd.mFields = new Object[mFields.length];
            // �݊����ێ��̂��߁A�g�����U�N�V�������[�h�́A�������Ȃ�
            rd.mTransactionMode = E_Record_TypeIgnore;
        }catch(CloneNotSupportedException e){
            //�N����Ȃ�
            throw new RuntimeException(e);
        }
		for (int rcnt = 0; rcnt < mFields.length; rcnt++) {
			rd.mFields[rcnt] = cloneFieldData(rcnt);
		}
		return rd;
	}

	/**
	 * ��f�[�^���R�s�[���ĉ������܂��B
	 * @param index ��ԍ�
	 * @return�@�R�s�[����Object
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
	 * ��i�[�z����������܂��B
	 * @return�@List
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
	 * �f�[�^���e�����l�����肵�܂��B
	 * @param rd ��r������RowData
	 * @return	<code>true</code> ���l
     *				<code>false</code> �񓯒l
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
