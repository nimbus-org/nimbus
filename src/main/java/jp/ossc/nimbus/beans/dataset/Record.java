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
package jp.ossc.nimbus.beans.dataset;

import java.util.*;
import java.io.*;

import jp.ossc.nimbus.service.codemaster.CodeMasterUpdateKey;

/**
 * ���R�[�h�B<p>
 * ���R�[�h���X�g�̂P�v�f�ƂȂ镡���̃v���p�e�B������Bean�ŁA�X�L�[�}��`�ɂ���āA�ǂ̂悤��Bean�ɂ���̂��i�v���p�e�B���A�^�Ȃǁj�𓮓I�Ɍ���ł���B<br>
 * �ȉ��ɃT���v���R�[�h�������B<br>
 * <pre>
 *     import jp.ossc.nimbus.beans.dataset.*;
 *     
 *     // ���R�[�h�𐶐�
 *     Record record = new Record();
 *     
 *     // ���R�[�h�̃X�L�[�}���ȉ��̂悤�ɒ�`����
 *     //   �v���p�e�B��  �^
 *     //        A        java.lang.String
 *     //        B        long
 *     record.setSchema(
 *         ":A,java.lang.String\n"
 *             + ":B,long"
 *     );
 *     
 *     // �l��ݒ肷��
 *     record.setProperty("A", "hoge");
 *     record.setProperty("B", 100l);
 * </pre>
 * 
 * @author M.Takata
 */
public class Record implements Externalizable, Cloneable, Map{
    
    private static final long serialVersionUID = -6640296864936227160L;
    
    /**
     * �X�L�[�}������B<p>
     */
    protected String schema;
    
    /**
     * ���R�[�h�X�L�[�}�B<p>
     */
    protected RecordSchema recordSchema;
    
    /**
     * �v���p�e�B�l���i�[����}�b�v�B<p>
     * �L�[�̓v���p�e�B���A�l�̓v���p�e�B�l�B<br>
     */
    protected Object[] values;
    
    /**
     * ���R�[�h���X�g�Ɋi�[�����ۂ̃��R�[�h�̃C���f�b�N�X�B<p>
     */
    protected int index = -1;
    
    /**
     * ���R�[�h���X�g�Ɋi�[�����ۂ̊i�[��̃��X�g�B<p>
     */
    protected RecordList recordList;
    
    /**
     * ����`�̃��R�[�h�𐶐�����B<p>
     */
    public Record(){
    }
    
    /**
     * ���R�[�h�𐶐�����B<p>
     *
     * @param schema �X�L�[�}������
     * @exception PropertySchemaDefineException �v���p�e�B�̃X�L�[�}��`�Ɏ��s�����ꍇ
     */
    public Record(String schema) throws PropertySchemaDefineException{
        this(RecordSchema.getInstance(schema));
    }
    
    /**
     * ���R�[�h�𐶐�����B<p>
     *
     * @param recordSchema �X�L�[�}�����񂩂琶�����ꂽ���R�[�h�X�L�[�}
     * @exception PropertySchemaDefineException �v���p�e�B�̃X�L�[�}��`�Ɏ��s�����ꍇ
     */
    public Record(RecordSchema recordSchema){
        if(recordSchema != null){
            this.schema = recordSchema.getSchema();
            this.recordSchema = recordSchema;
        }
    }
    
    /**
     * ���R�[�h�̃X�L�[�}�������ݒ肷��B<p>
     *
     * @param schema ���R�[�h�̃X�L�[�}������
     * @exception PropertySchemaDefineException �v���p�e�B�̃X�L�[�}��`�Ɏ��s�����ꍇ
     */
    public void setSchema(String schema) throws PropertySchemaDefineException{
        setRecordSchema(RecordSchema.getInstance(schema));
    }
    
    /**
     * ���R�[�h�̃X�L�[�}��������擾����B<p>
     *
     * @return ���R�[�h�̃X�L�[�}������
     */
    public String getSchema(){
        return schema;
    }
    
    /**
     * ���R�[�h�X�L�[�}��ݒ肷��B<p>
     *
     * @param schema ���R�[�h�X�L�[�}
     * @exception PropertySchemaDefineException �v���p�e�B�̃X�L�[�}��`�Ɏ��s�����ꍇ
     */
    public void setRecordSchema(RecordSchema schema) throws PropertySchemaDefineException{
        if(values != null){
            throw new PropertySchemaDefineException("Data already exists.");
        }
        recordSchema = schema;
        this.schema = schema == null ? null : schema.getSchema();
    }
    
    /**
     * ���R�[�h�X�L�[�}���擾����B<p>
     *
     * @return ���R�[�h�X�L�[�}
     */
    public RecordSchema getRecordSchema(){
        return recordSchema;
    }
    
    /**
     * ���R�[�h�X�L�[�}��u������B<p>
     *
     * @param schema ���R�[�h�̃X�L�[�}������
     * @exception PropertySchemaDefineException �v���p�e�B�̃X�L�[�}��`�Ɏ��s�����ꍇ
     */
    public void replaceSchema(String schema) throws PropertySchemaDefineException{
        replaceRecordSchema(RecordSchema.getInstance(schema));
    }
    
    /**
     * ���R�[�h�X�L�[�}��u������B<p>
     *
     * @param schema ���R�[�h�X�L�[�}
     * @exception PropertySchemaDefineException �v���p�e�B�̃X�L�[�}��`�Ɏ��s�����ꍇ
     */
    public void replaceRecordSchema(
        RecordSchema schema
    ) throws PropertySchemaDefineException{
        if(recordSchema != null && schema != null && values != null){
            
            PropertySchema[] props = schema.getPropertySchemata();
            Object[] newValues = new Object[props.length];
            for(int i = 0; i < props.length; i++){
                PropertySchema oldProp = recordSchema.getPropertySchema(
                    props[i].getName()
                );
                if(oldProp != null){
                    Class type = props[i].getType();
                    Class oldType = oldProp.getType();
                    if(type != null
                        && (oldType == null
                             || !type.isAssignableFrom(oldType))
                    ){
                        throw new PropertySchemaDefineException("It is not compatible. old=" + oldProp + ", new=" + props[i]);
                    }
                }
                newValues[i] = getProperty(oldProp.getName());
            }
            values = newValues;
        }
        recordSchema = schema;
        this.schema = schema == null ? null : schema.getSchema();
    }
    
    /**
     * ���R�[�h�X�L�[�}�������ǉ�����B<p>
     *
     * @param schema ���R�[�h�̃X�L�[�}������
     * @exception PropertySchemaDefineException �v���p�e�B�̃X�L�[�}��`�Ɏ��s�����ꍇ
     */
    public void appendSchema(
        String schema
    ) throws PropertySchemaDefineException{
        if(recordSchema == null){
            setSchema(schema);
        }else{
            replaceRecordSchema(
                recordSchema.appendSchema(schema)
            );
        }
    }
    
    /**
     * �e�ƂȂ郌�R�[�h���X�g��ł̃C���f�b�N�X��ݒ肷��B<p>
     *
     * @param index �C���f�b�N�X
     */
    protected void setIndex(int index){
        this.index = index;
    }
    
    /**
     * �e�ƂȂ郌�R�[�h���X�g��ł̃C���f�b�N�X���擾����B<p>
     *
     * @return �C���f�b�N�X
     */
    public int getIndex(){
        return index;
    }
    
    /**
     * �e�ƂȂ郌�R�[�h���X�g��ݒ肷��B<p>
     *
     * @param list ���R�[�h���X�g
     */
    protected void setRecordList(RecordList list){
        recordList = list;
    }
    
    /**
     * �e�ƂȂ郌�R�[�h���X�g���擾����B<p>
     *
     * @return ���R�[�h���X�g
     */
    public RecordList getRecordList(){
        return recordList;
    }
    
    /**
     * �w�肳�ꂽ���O�̃v���p�e�B��ݒ肷��B<p>
     *
     * @param name �v���p�e�B��
     * @param val �v���p�e�B�̒l
     * @exception PropertySetException �v���p�e�B�̐ݒ�Ɏ��s�����ꍇ
     */
    public void setProperty(String name, Object val)
     throws PropertySetException{
        if(recordSchema == null){
            throw new PropertySetException(null, "Schema is not initialized.");
        }
        final PropertySchema propertySchema
             = recordSchema.getPropertySchema(name);
        if(propertySchema == null){
            throw new PropertySetException(null, "No such property : " + name);
        }
        setProperty(recordSchema.getPropertyIndex(name), val);
    }
    
    /**
     * �w�肳�ꂽ�C���f�b�N�X�̃v���p�e�B��ݒ肷��B<p>
     *
     * @param index �v���p�e�B�̃C���f�b�N�X 
     * @param val �v���p�e�B�̒l
     * @exception PropertySetException �v���p�e�B�̐ݒ�Ɏ��s�����ꍇ
     */
    public void setProperty(int index, Object val)
     throws PropertySetException{
        
        if(recordSchema == null){
            throw new PropertySetException(null, "Schema is not initialized.");
        }
        final PropertySchema propertySchema
             = recordSchema.getPropertySchema(index);
        if(propertySchema == null){
            throw new PropertySetException(null, "No such property : " + index);
        }
        if(recordList != null && recordList.indexManager != null){
            if(recordList.isSynchronized){
                synchronized(recordList.indexManager){
                    recordList.indexManager.remove(this);
                    if(values == null){
                        synchronized(this){
                            if(values == null){
                                values = new Object[recordSchema.getPropertySize()];
                            }
                        }
                    }
                    values[index] = propertySchema.set(val);
                    recordList.indexManager.add(this);
                }
            }else{
                recordList.indexManager.remove(this);
                if(values == null){
                    synchronized(this){
                        if(values == null){
                            values = new Object[recordSchema.getPropertySize()];
                        }
                    }
                }
                values[index] = propertySchema.set(val);
                recordList.indexManager.add(this);
            }
        }else{
            if(values == null){
                synchronized(this){
                    if(values == null){
                        values = new Object[recordSchema.getPropertySize()];
                    }
                }
            }
            values[index] = propertySchema.set(val);
        }
    }
    
    /**
     * �w�肳�ꂽ���O�̃v���p�e�B��ݒ肷��B<p>
     *
     * @param name �v���p�e�B��
     * @param val �v���p�e�B�̒l
     * @exception PropertySetException �v���p�e�B�̐ݒ�Ɏ��s�����ꍇ
     */
    public void setProperty(String name, boolean val)
     throws PropertySetException{
        if(recordSchema == null){
            throw new PropertySetException(null, "Schema is not initialized.");
        }
        setProperty(recordSchema.getPropertyIndex(name), val);
    }
    
    /**
     * �w�肳�ꂽ�C���f�b�N�X�̃v���p�e�B��ݒ肷��B<p>
     *
     * @param index �v���p�e�B�̃C���f�b�N�X 
     * @param val �v���p�e�B�̒l
     * @exception PropertySetException �v���p�e�B�̐ݒ�Ɏ��s�����ꍇ
     */
    public void setProperty(int index, boolean val)
     throws PropertySetException{
        setProperty(index, val ? Boolean.TRUE : Boolean.FALSE);
    }
    
    /**
     * �w�肳�ꂽ���O�̃v���p�e�B��ݒ肷��B<p>
     *
     * @param name �v���p�e�B��
     * @param val �v���p�e�B�̒l
     * @exception PropertySetException �v���p�e�B�̐ݒ�Ɏ��s�����ꍇ
     */
    public void setProperty(String name, byte val)
     throws PropertySetException{
        if(recordSchema == null){
            throw new PropertySetException(null, "Schema is not initialized.");
        }
        setProperty(recordSchema.getPropertyIndex(name), val);
    }
    
    /**
     * �w�肳�ꂽ�C���f�b�N�X�̃v���p�e�B��ݒ肷��B<p>
     *
     * @param index �v���p�e�B�̃C���f�b�N�X 
     * @param val �v���p�e�B�̒l
     * @exception PropertySetException �v���p�e�B�̐ݒ�Ɏ��s�����ꍇ
     */
    public void setProperty(int index, byte val)
     throws PropertySetException{
        setProperty(index, new Byte(val));
    }
    
    /**
     * �w�肳�ꂽ���O�̃v���p�e�B��ݒ肷��B<p>
     *
     * @param name �v���p�e�B��
     * @param val �v���p�e�B�̒l
     * @exception PropertySetException �v���p�e�B�̐ݒ�Ɏ��s�����ꍇ
     */
    public void setProperty(String name, char val)
     throws PropertySetException{
        if(recordSchema == null){
            throw new PropertySetException(null, "Schema is not initialized.");
        }
        setProperty(recordSchema.getPropertyIndex(name), val);
    }
    
    /**
     * �w�肳�ꂽ�C���f�b�N�X�̃v���p�e�B��ݒ肷��B<p>
     *
     * @param index �v���p�e�B�̃C���f�b�N�X 
     * @param val �v���p�e�B�̒l
     * @exception PropertySetException �v���p�e�B�̐ݒ�Ɏ��s�����ꍇ
     */
    public void setProperty(int index, char val)
     throws PropertySetException{
        setProperty(index, new Character(val));
    }
    
    /**
     * �w�肳�ꂽ���O�̃v���p�e�B��ݒ肷��B<p>
     *
     * @param name �v���p�e�B��
     * @param val �v���p�e�B�̒l
     * @exception PropertySetException �v���p�e�B�̐ݒ�Ɏ��s�����ꍇ
     */
    public void setProperty(String name, short val)
     throws PropertySetException{
        if(recordSchema == null){
            throw new PropertySetException(null, "Schema is not initialized.");
        }
        setProperty(recordSchema.getPropertyIndex(name), val);
    }
    
    /**
     * �w�肳�ꂽ�C���f�b�N�X�̃v���p�e�B��ݒ肷��B<p>
     *
     * @param index �v���p�e�B�̃C���f�b�N�X 
     * @param val �v���p�e�B�̒l
     * @exception PropertySetException �v���p�e�B�̐ݒ�Ɏ��s�����ꍇ
     */
    public void setProperty(int index, short val)
     throws PropertySetException{
        setProperty(index, new Short(val));
    }
    
    /**
     * �w�肳�ꂽ���O�̃v���p�e�B��ݒ肷��B<p>
     *
     * @param name �v���p�e�B��
     * @param val �v���p�e�B�̒l
     * @exception PropertySetException �v���p�e�B�̐ݒ�Ɏ��s�����ꍇ
     */
    public void setProperty(String name, int val)
     throws PropertySetException{
        if(recordSchema == null){
            throw new PropertySetException(null, "Schema is not initialized.");
        }
        setProperty(recordSchema.getPropertyIndex(name), val);
    }
    
    /**
     * �w�肳�ꂽ�C���f�b�N�X�̃v���p�e�B��ݒ肷��B<p>
     *
     * @param index �v���p�e�B�̃C���f�b�N�X 
     * @param val �v���p�e�B�̒l
     * @exception PropertySetException �v���p�e�B�̐ݒ�Ɏ��s�����ꍇ
     */
    public void setProperty(int index, int val)
     throws PropertySetException{
        setProperty(index, new Integer(val));
    }
    
    /**
     * �w�肳�ꂽ���O�̃v���p�e�B��ݒ肷��B<p>
     *
     * @param name �v���p�e�B��
     * @param val �v���p�e�B�̒l
     * @exception PropertySetException �v���p�e�B�̐ݒ�Ɏ��s�����ꍇ
     */
    public void setProperty(String name, long val)
     throws PropertySetException{
        if(recordSchema == null){
            throw new PropertySetException(null, "Schema is not initialized.");
        }
        setProperty(recordSchema.getPropertyIndex(name), val);
    }
    
    /**
     * �w�肳�ꂽ�C���f�b�N�X�̃v���p�e�B��ݒ肷��B<p>
     *
     * @param index �v���p�e�B�̃C���f�b�N�X 
     * @param val �v���p�e�B�̒l
     * @exception PropertySetException �v���p�e�B�̐ݒ�Ɏ��s�����ꍇ
     */
    public void setProperty(int index, long val)
     throws PropertySetException{
        setProperty(index, new Long(val));
    }
    
    /**
     * �w�肳�ꂽ���O�̃v���p�e�B��ݒ肷��B<p>
     *
     * @param name �v���p�e�B��
     * @param val �v���p�e�B�̒l
     * @exception PropertySetException �v���p�e�B�̐ݒ�Ɏ��s�����ꍇ
     */
    public void setProperty(String name, float val)
     throws PropertySetException{
        if(recordSchema == null){
            throw new PropertySetException(null, "Schema is not initialized.");
        }
        setProperty(recordSchema.getPropertyIndex(name), val);
    }
    
    /**
     * �w�肳�ꂽ�C���f�b�N�X�̃v���p�e�B��ݒ肷��B<p>
     *
     * @param index �v���p�e�B�̃C���f�b�N�X 
     * @param val �v���p�e�B�̒l
     * @exception PropertySetException �v���p�e�B�̐ݒ�Ɏ��s�����ꍇ
     */
    public void setProperty(int index, float val)
     throws PropertySetException{
        setProperty(index, new Float(val));
    }
    
    /**
     * �w�肳�ꂽ���O�̃v���p�e�B��ݒ肷��B<p>
     *
     * @param name �v���p�e�B��
     * @param val �v���p�e�B�̒l
     * @exception PropertySetException �v���p�e�B�̐ݒ�Ɏ��s�����ꍇ
     */
    public void setProperty(String name, double val)
     throws PropertySetException{
        if(recordSchema == null){
            throw new PropertySetException(null, "Schema is not initialized.");
        }
        setProperty(recordSchema.getPropertyIndex(name), val);
    }
    
    /**
     * �w�肳�ꂽ�C���f�b�N�X�̃v���p�e�B��ݒ肷��B<p>
     *
     * @param index �v���p�e�B�̃C���f�b�N�X 
     * @param val �v���p�e�B�̒l
     * @exception PropertySetException �v���p�e�B�̐ݒ�Ɏ��s�����ꍇ
     */
    public void setProperty(int index, double val)
     throws PropertySetException{
        setProperty(index, new Double(val));
    }
    
    /**
     * �w�肳�ꂽ���O�̃v���p�e�B���擾����B<p>
     *
     * @param name �v���p�e�B��
     * @return �v���p�e�B�̒l
     * @exception PropertyGetException �v���p�e�B�̎擾�Ɏ��s�����ꍇ
     */
    public Object getProperty(String name) throws PropertyGetException{
        if(recordSchema == null){
            throw new PropertyGetException(null, "Schema is not initialized.");
        }
        final PropertySchema propertySchema
             = recordSchema.getPropertySchema(name);
        if(propertySchema == null){
            throw new PropertyGetException(null, "No such property : " + name);
        }
        return getProperty(recordSchema.getPropertyIndex(name));
    }
    
    /**
     * �w�肳�ꂽ�C���f�b�N�X�̃v���p�e�B���擾����B<p>
     *
     * @param index �v���p�e�B�̃C���f�b�N�X 
     * @return �v���p�e�B�̒l
     * @exception PropertyGetException �v���p�e�B�̎擾�Ɏ��s�����ꍇ
     */
    public Object getProperty(int index) throws PropertyGetException{
        if(recordSchema == null){
            throw new PropertyGetException(null, "Schema is not initialized.");
        }
        final PropertySchema propertySchema
             = recordSchema.getPropertySchema(index);
        if(propertySchema == null){
            throw new PropertyGetException(null, "No such property : " + index);
        }
        return propertySchema.get(values == null ? null : values[index]);
    }
    
    /**
     * �w�肳�ꂽ���O�̃v���p�e�B��boolean�Ƃ��Ď擾����B<p>
     *
     * @param name �v���p�e�B��
     * @return �v���p�e�B�̒l
     * @exception PropertyGetException �v���p�e�B�̎擾�Ɏ��s�����ꍇ
     */
    public boolean getBooleanProperty(String name) throws PropertyGetException{
        if(recordSchema == null){
            throw new PropertyGetException(null, "Schema is not initialized.");
        }
        return getBooleanProperty(recordSchema.getPropertyIndex(name));
    }
    
    /**
     * �w�肳�ꂽ�C���f�b�N�X�̃v���p�e�B��boolean�Ƃ��Ď擾����B<p>
     *
     * @param index �v���p�e�B�̃C���f�b�N�X 
     * @return �v���p�e�B�̒l
     * @exception PropertyGetException �v���p�e�B�̎擾�Ɏ��s�����ꍇ
     */
    public boolean getBooleanProperty(int index) throws PropertyGetException{
        final Object ret = getProperty(index);
        if(ret == null){
            return false;
        }else if(ret instanceof Boolean){
            return ((Boolean)ret).booleanValue();
        }else if(ret instanceof String){
            try{
                return Integer.parseInt((String)ret) == 0 ? false : true;
            }catch(NumberFormatException e){
                return Boolean.valueOf((String)ret).booleanValue();
            }
        }else if(ret instanceof Number){
            return ((Number)ret).intValue() == 0 ? false : true;
        }
        throw new PropertyGetException(
            recordSchema.getPropertySchema(index),
            "The type is unmatch. value=" + ret
        );
    }
    
    /**
     * �w�肳�ꂽ���O�̃v���p�e�B��byte�Ƃ��Ď擾����B<p>
     *
     * @param name �v���p�e�B��
     * @return �v���p�e�B�̒l
     * @exception PropertyGetException �v���p�e�B�̎擾�Ɏ��s�����ꍇ
     */
    public byte getByteProperty(String name) throws PropertyGetException{
        if(recordSchema == null){
            throw new PropertyGetException(null, "Schema is not initialized.");
        }
        return getByteProperty(recordSchema.getPropertyIndex(name));
    }
    
    /**
     * �w�肳�ꂽ�C���f�b�N�X�̃v���p�e�B��byte�Ƃ��Ď擾����B<p>
     *
     * @param index �v���p�e�B�̃C���f�b�N�X 
     * @return �v���p�e�B�̒l
     * @exception PropertyGetException �v���p�e�B�̎擾�Ɏ��s�����ꍇ
     */
    public byte getByteProperty(int index) throws PropertyGetException{
        final Object ret = getProperty(index);
        if(ret == null){
            return (byte)0;
        }else if(ret instanceof Number){
            return ((Number)ret).byteValue();
        }else if(ret instanceof Boolean){
            return ((Boolean)ret).booleanValue() ? (byte)1 : (byte)0;
        }else if(ret instanceof String){
            try{
                return Byte.parseByte((String)ret);
            }catch(NumberFormatException e){
            }
        }
        throw new PropertyGetException(
            recordSchema.getPropertySchema(index),
            "The type is unmatch. value=" + ret
        );
    }
    
    /**
     * �w�肳�ꂽ���O�̃v���p�e�B��short�Ƃ��Ď擾����B<p>
     *
     * @param name �v���p�e�B��
     * @return �v���p�e�B�̒l
     * @exception PropertyGetException �v���p�e�B�̎擾�Ɏ��s�����ꍇ
     */
    public short getShortProperty(String name) throws PropertyGetException{
        if(recordSchema == null){
            throw new PropertyGetException(null, "Schema is not initialized.");
        }
        return getShortProperty(recordSchema.getPropertyIndex(name));
    }
    
    /**
     * �w�肳�ꂽ�C���f�b�N�X�̃v���p�e�B��short�Ƃ��Ď擾����B<p>
     *
     * @param index �v���p�e�B�̃C���f�b�N�X 
     * @return �v���p�e�B�̒l
     * @exception PropertyGetException �v���p�e�B�̎擾�Ɏ��s�����ꍇ
     */
    public short getShortProperty(int index) throws PropertyGetException{
        final Object ret = getProperty(index);
        if(ret == null){
            return (short)0;
        }else if(ret instanceof Number){
            return ((Number)ret).shortValue();
        }else if(ret instanceof Boolean){
            return ((Boolean)ret).booleanValue() ? (short)1 : (short)0;
        }else if(ret instanceof String){
            try{
                return Short.parseShort((String)ret);
            }catch(NumberFormatException e){
            }
        }
        throw new PropertyGetException(
            recordSchema.getPropertySchema(index),
            "The type is unmatch. value=" + ret
        );
    }
    
    /**
     * �w�肳�ꂽ���O�̃v���p�e�B��int�Ƃ��Ď擾����B<p>
     *
     * @param name �v���p�e�B��
     * @return �v���p�e�B�̒l
     * @exception PropertyGetException �v���p�e�B�̎擾�Ɏ��s�����ꍇ
     */
    public int getIntProperty(String name) throws PropertyGetException{
        if(recordSchema == null){
            throw new PropertyGetException(null, "Schema is not initialized.");
        }
        return getIntProperty(recordSchema.getPropertyIndex(name));
    }
    
    /**
     * �w�肳�ꂽ�C���f�b�N�X�̃v���p�e�B��int�Ƃ��Ď擾����B<p>
     *
     * @param index �v���p�e�B�̃C���f�b�N�X 
     * @return �v���p�e�B�̒l
     * @exception PropertyGetException �v���p�e�B�̎擾�Ɏ��s�����ꍇ
     */
    public int getIntProperty(int index) throws PropertyGetException{
        final Object ret = getProperty(index);
        if(ret == null){
            return (int)0;
        }else if(ret instanceof Number){
            return ((Number)ret).intValue();
        }else if(ret instanceof Boolean){
            return ((Boolean)ret).booleanValue() ? (int)1 : (int)0;
        }else if(ret instanceof String){
            try{
                return Integer.parseInt((String)ret);
            }catch(NumberFormatException e){
            }
        }
        throw new PropertyGetException(
            recordSchema.getPropertySchema(index),
            "The type is unmatch. value=" + ret
        );
    }
    
    /**
     * �w�肳�ꂽ���O�̃v���p�e�B��long�Ƃ��Ď擾����B<p>
     *
     * @param name �v���p�e�B��
     * @return �v���p�e�B�̒l
     * @exception PropertyGetException �v���p�e�B�̎擾�Ɏ��s�����ꍇ
     */
    public long getLongProperty(String name) throws PropertyGetException{
        if(recordSchema == null){
            throw new PropertyGetException(null, "Schema is not initialized.");
        }
        return getLongProperty(recordSchema.getPropertyIndex(name));
    }
    
    /**
     * �w�肳�ꂽ�C���f�b�N�X�̃v���p�e�B��long�Ƃ��Ď擾����B<p>
     *
     * @param index �v���p�e�B�̃C���f�b�N�X 
     * @return �v���p�e�B�̒l
     * @exception PropertyGetException �v���p�e�B�̎擾�Ɏ��s�����ꍇ
     */
    public long getLongProperty(int index) throws PropertyGetException{
        final Object ret = getProperty(index);
        if(ret == null){
            return (long)0;
        }else if(ret instanceof Number){
            return ((Number)ret).longValue();
        }else if(ret instanceof Boolean){
            return ((Boolean)ret).booleanValue() ? 1l : 0l;
        }else if(ret instanceof String){
            try{
                return Long.parseLong((String)ret);
            }catch(NumberFormatException e){
            }
        }
        throw new PropertyGetException(
            recordSchema.getPropertySchema(index),
            "The type is unmatch. value=" + ret
        );
    }
    
    /**
     * �w�肳�ꂽ���O�̃v���p�e�B��float�Ƃ��Ď擾����B<p>
     *
     * @param name �v���p�e�B��
     * @return �v���p�e�B�̒l
     * @exception PropertyGetException �v���p�e�B�̎擾�Ɏ��s�����ꍇ
     */
    public float getFloatProperty(String name) throws PropertyGetException{
        if(recordSchema == null){
            throw new PropertyGetException(null, "Schema is not initialized.");
        }
        return getFloatProperty(recordSchema.getPropertyIndex(name));
    }
    
    /**
     * �w�肳�ꂽ�C���f�b�N�X�̃v���p�e�B��float�Ƃ��Ď擾����B<p>
     *
     * @param index �v���p�e�B�̃C���f�b�N�X 
     * @return �v���p�e�B�̒l
     * @exception PropertyGetException �v���p�e�B�̎擾�Ɏ��s�����ꍇ
     */
    public float getFloatProperty(int index) throws PropertyGetException{
        final Object ret = getProperty(index);
        if(ret == null){
            return (float)0;
        }else if(ret instanceof Number){
            return ((Number)ret).floatValue();
        }else if(ret instanceof Boolean){
            return ((Boolean)ret).booleanValue() ? 1.0f : 0.0f;
        }else if(ret instanceof String){
            try{
                return Float.parseFloat((String)ret);
            }catch(NumberFormatException e){
            }
        }
        throw new PropertyGetException(
            recordSchema.getPropertySchema(index),
            "The type is unmatch. value=" + ret
        );
    }
    
    /**
     * �w�肳�ꂽ���O�̃v���p�e�B��double�Ƃ��Ď擾����B<p>
     *
     * @param name �v���p�e�B��
     * @return �v���p�e�B�̒l
     * @exception PropertyGetException �v���p�e�B�̎擾�Ɏ��s�����ꍇ
     */
    public double getDoubleProperty(String name) throws PropertyGetException{
        if(recordSchema == null){
            throw new PropertyGetException(null, "Schema is not initialized.");
        }
        return getDoubleProperty(recordSchema.getPropertyIndex(name));
    }
    
    /**
     * �w�肳�ꂽ�C���f�b�N�X�̃v���p�e�B��double�Ƃ��Ď擾����B<p>
     *
     * @param index �v���p�e�B�̃C���f�b�N�X 
     * @return �v���p�e�B�̒l
     * @exception PropertyGetException �v���p�e�B�̎擾�Ɏ��s�����ꍇ
     */
    public double getDoubleProperty(int index) throws PropertyGetException{
        final Object ret = getProperty(index);
        if(ret == null){
            return (double)0;
        }else if(ret instanceof Number){
            return ((Number)ret).doubleValue();
        }else if(ret instanceof Boolean){
            return ((Boolean)ret).booleanValue() ? 1.0d : 0.0d;
        }else if(ret instanceof String){
            try{
                return Double.parseDouble((String)ret);
            }catch(NumberFormatException e){
            }
        }
        throw new PropertyGetException(
            recordSchema.getPropertySchema(index),
            "The type is unmatch. value=" + ret
        );
    }
    
    /**
     * �w�肳�ꂽ���O�̃v���p�e�B�𕶎���Ƃ��Ď擾����B<p>
     *
     * @param name �v���p�e�B��
     * @return �v���p�e�B�̒l
     * @exception PropertyGetException �v���p�e�B�̎擾�Ɏ��s�����ꍇ
     */
    public String getStringProperty(String name) throws PropertyGetException{
        if(recordSchema == null){
            throw new PropertyGetException(null, "Schema is not initialized.");
        }
        return getStringProperty(recordSchema.getPropertyIndex(name));
    }
    
    /**
     * �w�肳�ꂽ�C���f�b�N�X�̃v���p�e�B�𕶎���Ƃ��Ď擾����B<p>
     *
     * @param index �v���p�e�B�̃C���f�b�N�X 
     * @return �v���p�e�B�̒l
     * @exception PropertyGetException �v���p�e�B�̎擾�Ɏ��s�����ꍇ
     */
    public String getStringProperty(int index) throws PropertyGetException{
        final Object ret = getProperty(index);
        if(ret == null){
            return null;
        }else if(ret instanceof String){
            return (String)ret;
        }else{
            return ret.toString();
        }
    }
    
    /**
     * �w�肳�ꂽ���O�̃v���p�e�B���t�H�[�}�b�g���Ď擾����B<p>
     *
     * @param name �v���p�e�B��
     * @return �t�H�[�}�b�g���ꂽ�v���p�e�B�̒l
     * @exception PropertyGetException �v���p�e�B�̎擾�Ɏ��s�����ꍇ
     */
    public Object getFormatProperty(String name) throws PropertyGetException{
        if(recordSchema == null){
            throw new PropertyGetException(null, "Schema is not initialized.");
        }
        final PropertySchema propertySchema
             = recordSchema.getPropertySchema(name);
        if(propertySchema == null){
            throw new PropertyGetException(null, "No such property : " + name);
        }
        return getFormatProperty(recordSchema.getPropertyIndex(name));
    }
    
    /**
     * �w�肳�ꂽ�C���f�b�N�X�̃v���p�e�B���t�H�[�}�b�g���Ď擾����B<p>
     *
     * @param index �v���p�e�B�̃C���f�b�N�X
     * @return �t�H�[�}�b�g���ꂽ�v���p�e�B�̒l
     * @exception PropertyGetException �v���p�e�B�̎擾�Ɏ��s�����ꍇ
     */
    public Object getFormatProperty(int index) throws PropertyGetException{
        if(recordSchema == null){
            throw new PropertyGetException(null, "Schema is not initialized.");
        }
        final PropertySchema propertySchema
             = recordSchema.getPropertySchema(index);
        if(propertySchema == null){
            throw new PropertyGetException(null, "No such property : " + index);
        }
        return propertySchema.format(getProperty(index));
    }
    
    /**
     * �w�肳�ꂽ���O�̃v���p�e�B�ɁA�w�肳�ꂽ�l���p�[�X���Đݒ肷��B<p>
     *
     * @param name �v���p�e�B��
     * @param val �v���p�e�B�̒l
     * @exception PropertySetException �v���p�e�B�̐ݒ�Ɏ��s�����ꍇ
     */
    public void setParseProperty(String name, Object val) throws PropertySetException{
        if(recordSchema == null){
            throw new PropertySetException(null, "Schema is not initialized.");
        }
        final PropertySchema propertySchema
             = recordSchema.getPropertySchema(name);
        if(propertySchema == null){
            throw new PropertySetException(null, "No such property : " + name);
        }
        setParseProperty(recordSchema.getPropertyIndex(name), val);
    }
    
    /**
     * �w�肳�ꂽ�C���f�b�N�X�̃v���p�e�B�ɁA�w�肳�ꂽ�l���p�[�X���Đݒ肷��B<p>
     *
     * @param index �v���p�e�B�̃C���f�b�N�X
     * @param val �v���p�e�B�̒l
     * @exception PropertySetException �v���p�e�B�̐ݒ�Ɏ��s�����ꍇ
     */
    public void setParseProperty(int index, Object val) throws PropertySetException{
        if(recordSchema == null){
            throw new PropertySetException(null, "Schema is not initialized.");
        }
        final PropertySchema propertySchema
             = recordSchema.getPropertySchema(index);
        if(propertySchema == null){
            throw new PropertySetException(null, "No such property : " + index);
        }
        setProperty(index, propertySchema.parse(val));
    }
    
    /**
     * �S�Ẵv���p�e�B�̒l�����؂���B<p>
     *
     * @return ���،��ʁBtrue�̏ꍇ�A���ؐ���
     * @exception PropertyGetException �v���p�e�B�̎擾�Ɏ��s�����ꍇ
     * @exception PropertyValidateException �v���p�e�B�̌��؎��ɗ�O�����������ꍇ
     */
    public boolean validate() throws PropertyGetException, PropertyValidateException{
        if(recordSchema == null){
            throw new PropertyGetException(null, "Schema is not initialized.");
        }
        final PropertySchema[] schemata = recordSchema.getPropertySchemata();
        for(int i = 0; i < schemata.length; i++){
            if(!schemata[i].validate(getProperty(i))){
                return false;
            }
        }
        return true;
    }
    
    /**
     * �w�肳�ꂽ���O�̃v���p�e�B�̒l�����؂���B<p>
     *
     * @param name �v���p�e�B��
     * @return ���،��ʁBtrue�̏ꍇ�A���ؐ���
     * @exception PropertyGetException �v���p�e�B�̎擾�Ɏ��s�����ꍇ
     * @exception PropertyValidateException �v���p�e�B�̌��؎��ɗ�O�����������ꍇ
     */
    public boolean validateProperty(String name) throws PropertyGetException, PropertyValidateException{
        if(recordSchema == null){
            throw new PropertyGetException(null, "Schema is not initialized.");
        }
        final PropertySchema propertySchema
             = recordSchema.getPropertySchema(name);
        if(propertySchema == null){
            throw new PropertyGetException(null, "No such property : " + name);
        }
        return validateProperty(recordSchema.getPropertyIndex(name));
    }
    
    /**
     * �w�肳�ꂽ�C���f�b�N�X�̃v���p�e�B�̒l�����؂���B<p>
     *
     * @param index �v���p�e�B�̃C���f�b�N�X
     * @return ���،��ʁBtrue�̏ꍇ�A���ؐ���
     * @exception PropertyGetException �v���p�e�B�̎擾�Ɏ��s�����ꍇ
     * @exception PropertyValidateException �v���p�e�B�̌��؎��ɗ�O�����������ꍇ
     */
    public boolean validateProperty(int index) throws PropertyGetException, PropertyValidateException{
        if(recordSchema == null){
            throw new PropertyGetException(null, "Schema is not initialized.");
        }
        final PropertySchema propertySchema
             = recordSchema.getPropertySchema(index);
        if(propertySchema == null){
            throw new PropertyGetException(null, "No such property : " + index);
        }
        return propertySchema.validate(getProperty(index));
    }
    
    /**
     * �S�Ẵv���p�e�B���N���A����B<p>
     */
    public void clear(){
        if(values != null){
            for(int i = 0; i < values.length; i++){
                values[i] = null;
            }
        }
    }
    
    /**
     * ���R�[�h�𕡐�����B<p>
     *
     * @return �����������R�[�h
     */
    public Object clone(){
        return cloneRecord();
    }
    
    /**
     * �����X�L�[�}�������f�[�^�������Ȃ���̃��R�[�h�𕡐�����B<p>
     *
     * @return ����������̃��R�[�h
     */
    public Record cloneSchema(){
        Record clone = null;
        try{
            clone = (Record)super.clone();
            clone.values = null;
            clone.index = -1;
            clone.recordList = null;
        }catch(CloneNotSupportedException e){
            return null;
        }
        return clone;
    }
    
    /**
     * ���R�[�h�𕡐�����B<p>
     *
     * @return �����������R�[�h
     */
    public Record cloneRecord(){
        final Record record = cloneSchema();
        if(values != null){
            record.values = new Object[values.length];
            System.arraycopy(values, 0, record.values, 0, values.length);
        }
        return record;
    }
    
    /**
     * ���̃��R�[�h�̕�����\�����擾����B<p>
     *
     * @return ������\��
     */
    public String toString(){
        final StringBuilder buf = new StringBuilder();
        buf.append('{');
        if(values != null){
            for(int i = 0; i < values.length; i++){
                if(recordSchema != null){
                    buf.append(recordSchema.getPropertyName(i));
                    buf.append('=');
                }
                buf.append(values[i]);
                if(i != values.length - 1){
                    buf.append(',');
                }
            }
        }
        buf.append('}');
        return buf.toString();
    }
    
    // java.util.Map��JavaDoc
    public int size(){
        return recordSchema == null ? 0 : recordSchema.getPropertySize();
    }
    
    // java.util.Map��JavaDoc
    public boolean isEmpty(){
        return size() == 0;
    }
    
    // java.util.Map��JavaDoc
    public boolean containsKey(Object key){
        return recordSchema == null ? false : recordSchema.getPropertySchema(
            key == null ? (String)key : key.toString()
        ) != null;
    }
    
    // java.util.Map��JavaDoc
    public boolean containsValue(Object value){
        if(values == null){
            return false;
        }
        for(int i = 0; i < values.length; i++){
            if(value == null &&  values[i] == null){
                return true;
            }else if(value != null && value.equals(values[i])){
                return true;
            }
        }
        return false;
    }
    
    // java.util.Map��JavaDoc
    public Object get(Object key){
        return getProperty(key == null ? (String)key : key.toString());
    }
    
    // java.util.Map��JavaDoc
    public Object put(Object key, Object value){
        final Object old = get(key);
        setProperty(key == null ? (String)key : key.toString(), value);
        return old;
    }
    
    // java.util.Map��JavaDoc
    public Object remove(Object key){
        if(!containsKey(key)){
            return null;
        }
        final Object old = get(key);
        if(old != null){
            setProperty(key == null ? (String)key : key.toString(), null);
        }
        return old;
    }
    
    // java.util.Map��JavaDoc
    public void putAll(Map t){
        if(t == null){
            return;
        }
        final Iterator entries = t.entrySet().iterator();
        while(entries.hasNext()){
            final Map.Entry entry = (Map.Entry)entries.next();
            put(entry.getKey(), entry.getValue());
        }
    }
    
    // java.util.Map��JavaDoc
    public Set keySet(){
        return new KeySet();
    }
    
    // java.util.Map��JavaDoc
    public Collection values(){
        return new Values();
    }
    
    // java.util.Map��JavaDoc
    public Set entrySet(){
        return new EntrySet();
    }
    
    public CodeMasterUpdateKey createCodeMasterUpdateKey() throws DataSetException{
        return createCodeMasterUpdateKey(new CodeMasterUpdateKey());
    }
    
    public CodeMasterUpdateKey createCodeMasterUpdateKey(CodeMasterUpdateKey key) throws DataSetException{
        final PropertySchema[] primaryKeys
            = recordSchema.getPrimaryKeyPropertySchemata();
        if(primaryKeys == null || primaryKeys.length == 0){
            throw new DataSetException("Primary key is not defined.");
        }
        key.clear();
        for(int i = 0; i < primaryKeys.length; i++){
            PropertySchema primaryKey = primaryKeys[i];
            key.addKey(primaryKey.getName(), getProperty(primaryKey.getName()));
        }
        return key;
    }
    
    public void setCodeMasterUpdateKey(CodeMasterUpdateKey key){
        final Iterator itr = key.getKeyMap().entrySet().iterator();
        while(itr.hasNext()){
            Map.Entry entry = (Map.Entry)itr.next();
            final PropertySchema propSchema = recordSchema.getPropertySchema(
                (String)entry.getKey()
            );
            if(propSchema == null){
                continue;
            }
            setProperty(propSchema.getName(), entry.getValue());
        }
    }
    
    // java.util.Map��JavaDoc
    public boolean equals(Object o){
        if(o == null){
            return false;
        }
        if(o == this){
            return true;
        }
        if(!(o instanceof Record)){
            return false;
        }
        final Record record = (Record)o;
        
        if(recordSchema != record.recordSchema){
            return false;
        }
        
        if(values == record.values){
            return true;
        }
        
        if((values == null && record.values != null)
            || (values != null && record.values == null)
            || values.length != record.values.length){
            return false;
        }
        for(int i = 0; i < values.length; i++){
            if(values[i] == null && record.values[i] != null
                || values[i] != null && record.values[i] == null){
                return false;
            }else if(values[i] != null && !values[i].equals(record.values[i])){
                return false;
            }
        }
        return true;
    }
    
    // java.util.Map��JavaDoc
    public int hashCode(){
        int hashCode = 0;
        if(schema != null){
            hashCode += schema.hashCode();
        }
        if(values != null){
            for(int i = 0; i < values.length; i++){
                if(values[i] != null){
                    hashCode += values[i].hashCode();
                }
            }
        }
        return hashCode;
    }
    
    protected class KeySet implements Set, Serializable{
        
        private static final long serialVersionUID = 810743353037210495L;
        
        protected List keys;
        
        public KeySet(){
            keys = new ArrayList();
            if(recordSchema != null){
                final PropertySchema[] schemata
                     = recordSchema.getPropertySchemata();
                for(int i = 0; i < schemata.length; i++){
                    keys.add(schemata[i].getName());
                }
            }
        }
        
        public int size(){
            return keys.size();
        }
        public boolean isEmpty(){
            return keys.isEmpty();
        }
        public boolean contains(Object o){
            return keys.contains(o);
        }
        public Iterator iterator(){
            return new KeySetIterator();
        }
        public Object[] toArray(){
            return keys.toArray();
        }
        public Object[] toArray(Object[] a){
            return keys.toArray(a);
        }
        public boolean add(Object o){
            throw new UnsupportedOperationException();
        }
        public boolean remove(Object o){
            return Record.this.remove(o) != null;
        }
        public boolean containsAll(Collection c){
            return keys.containsAll(c);
        }
        public boolean addAll(Collection c){
            throw new UnsupportedOperationException();
        }
        public boolean retainAll(Collection c){
            boolean result = false;
            final Iterator itr = keys.iterator();
            while(itr.hasNext()){
                final Object key = itr.next();
                if(!c.contains(key)){
                    result |= remove(key);
                }
            }
            return result;
        }
        public boolean removeAll(Collection c){
            boolean result = false;
            final Iterator itr = keys.iterator();
            while(itr.hasNext()){
                final Object key = itr.next();
                if(c.contains(key)){
                    result |= remove(key);
                }
            }
            return result;
        }
        public void clear(){
            Record.this.clear();
        }
        public boolean equals(Object o){
            return keys.equals(o);
        }
        public int hashCode(){
            return keys.hashCode();
        }
        
        protected class KeySetIterator implements Iterator, Serializable{
            
            private static final long serialVersionUID = -1219165095772883511L;
            
            protected int index;
            public boolean hasNext(){
                return keys.size() > index;
            }
            public Object next(){
                return hasNext() ? keys.get(index++) : null;
            }
            public void remove(){
                if(keys.size() > index){
                    Record.this.remove(keys.get(index));
                }
            }
        }
    }
    
    protected class Values implements Collection, Serializable{
        
        private static final long serialVersionUID = 4612582373933630957L;
        
        protected List valueList;
        
        public Values(){
            valueList = new ArrayList();
            if(recordSchema != null){
                final PropertySchema[] schemata
                     = recordSchema.getPropertySchemata();
                for(int i = 0; i < schemata.length; i++){
                    valueList.add(Record.this.getProperty(schemata[i].getName()));
                }
            }
        }
        
        public int size(){
            return valueList.size();
        }
        public boolean isEmpty(){
            return valueList.isEmpty();
        }
        public boolean contains(Object o){
            return valueList.contains(o);
        }
        public Iterator iterator(){
            return new ValuesIterator();
        }
        public Object[] toArray(){
            return valueList.toArray();
        }
        public Object[] toArray(Object[] a){
            return valueList.toArray(a);
        }
        public boolean add(Object o){
            throw new UnsupportedOperationException();
        }
        public boolean remove(Object o){
            final int index = valueList.indexOf(o);
            if(index == -1){
                return false;
            }
            return Record.this.remove(recordSchema.getPropertyName(index)) != null;
        }
        public boolean containsAll(Collection c){
            return valueList.containsAll(c);
        }
        public boolean addAll(Collection c){
            throw new UnsupportedOperationException();
        }
        public boolean retainAll(Collection c){
            boolean result = false;
            final Iterator itr = valueList.iterator();
            while(itr.hasNext()){
                final Object val = itr.next();
                if(!c.contains(val)){
                    result |= remove(val);
                }
            }
            return result;
        }
        public boolean removeAll(Collection c){
            boolean result = false;
            final Iterator itr = valueList.iterator();
            while(itr.hasNext()){
                final Object val = itr.next();
                if(c.contains(val)){
                    result |= remove(val);
                }
            }
            return result;
        }
        public void clear(){
            Record.this.clear();
        }
        public boolean equals(Object o){
            return valueList.equals(o);
        }
        public int hashCode(){
            return valueList.hashCode();
        }
        
        protected class ValuesIterator implements Iterator, Serializable{
            
            private static final long serialVersionUID = 167532200775957747L;
            
            protected int index;
            public boolean hasNext(){
                return valueList.size() > index;
            }
            public Object next(){
                return hasNext() ? valueList.get(index++) : null;
            }
            public void remove(){
                if(valueList.size() > index){
                    Record.Values.this.remove(valueList.get(index));
                }
            }
        }
    }
    
    protected class EntrySet implements Set, Serializable{
        
        private static final long serialVersionUID = -4696386214482898985L;
        
        protected List entries;
        
        public EntrySet(){
            entries = new ArrayList();
            if(recordSchema != null){
                final PropertySchema[] schemata
                     = recordSchema.getPropertySchemata();
                for(int i = 0; i < schemata.length; i++){
                    entries.add(new Entry(schemata[i].getName()));
                }
            }
        }
        
        public int size(){
            return entries.size();
        }
        public boolean isEmpty(){
            return entries.isEmpty();
        }
        public boolean contains(Object o){
            return entries.contains(o);
        }
        public Iterator iterator(){
            return new EntrySetIterator();
        }
        public Object[] toArray(){
            return entries.toArray();
        }
        public Object[] toArray(Object[] a){
            return entries.toArray(a);
        }
        public boolean add(Object o){
            throw new UnsupportedOperationException();
        }
        public boolean remove(Object o){
            if(!(o instanceof Map.Entry)){
                return false;
            }
            return Record.this.remove(((Map.Entry)o).getKey()) != null;
        }
        public boolean containsAll(Collection c){
            return entries.containsAll(c);
        }
        public boolean addAll(Collection c){
            throw new UnsupportedOperationException();
        }
        public boolean retainAll(Collection c){
            boolean result = false;
            final Iterator itr = entries.iterator();
            while(itr.hasNext()){
                final Object key = ((Map.Entry)itr.next()).getKey();
                if(!c.contains(key)){
                    result |= remove(key);
                }
            }
            return result;
        }
        public boolean removeAll(Collection c){
            boolean result = false;
            final Iterator itr = entries.iterator();
            while(itr.hasNext()){
                final Object key = ((Map.Entry)itr.next()).getKey();
                if(c.contains(key)){
                    result |= remove(key);
                }
            }
            return result;
        }
        public void clear(){
            Record.this.clear();
        }
        public boolean equals(Object o){
            return entries.equals(o);
        }
        public int hashCode(){
            return entries.hashCode();
        }
        
        protected class Entry implements Map.Entry, Serializable{
            
            private static final long serialVersionUID = 5572280646230618952L;
            
            protected String key;
            public Entry(String key){
                this.key = key;
            }
            public Object getKey(){
                return key;
            }
            public Object getValue(){
                return Record.this.getProperty(key);
            }
            public Object setValue(Object value){
                return Record.this.put(key, value);
            }
            public boolean equals(Object o){
                if(o == null){
                    return false;
                }
                if(o == this){
                    return true;
                }
                if(!(o instanceof Map.Entry)){
                    return false;
                }
                final Map.Entry entry = (Map.Entry)o;
                return (getKey() == null ? entry.getKey() == null : getKey().equals(entry.getKey())) && (getValue() == null ? entry.getValue() == null : getValue().equals(entry.getValue()));
            }
            public int hashCode(){
                return (getKey() == null ? 0 : getKey().hashCode()) ^ (getValue() == null ? 0 : getValue().hashCode());
            }
        }
        
        protected class EntrySetIterator implements Iterator, Serializable{
            
            private static final long serialVersionUID = -8153119352044048534L;
            
            protected int index;
            public boolean hasNext(){
                return entries.size() > index;
            }
            public Object next(){
                return hasNext() ? entries.get(index++) : null;
            }
            public void remove(){
                if(entries.size() > index){
                    Record.this.remove(((Entry)entries.get(index)).getKey());
                }
            }
        }
    }
    
    public void writeExternal(ObjectOutput out) throws IOException{
        writeSchema(out);
        writeExternalValues(out);
    }
    
    protected void writeSchema(ObjectOutput out) throws IOException{
        out.writeObject(schema);
    }
    
    protected void writeExternalValues(ObjectOutput out) throws IOException{
        out.writeObject(values);
    }
    
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException{
        readSchema(in);
        readExternalValues(in);
    }
    
    protected void readSchema(ObjectInput in) throws IOException, ClassNotFoundException{
        schema = (String)in.readObject();
        if(schema != null){
            recordSchema = RecordSchema.getInstance(schema);
        }
    }
    
    protected void readExternalValues(ObjectInput in) throws IOException, ClassNotFoundException{
        values = (Object[])in.readObject();
    }
}