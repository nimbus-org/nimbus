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
import java.lang.reflect.Field;

import org.apache.commons.jexl.*;

import jp.ossc.nimbus.beans.BeanTableIndexManager;
import jp.ossc.nimbus.beans.BeanTableView;
import jp.ossc.nimbus.beans.BeanTableIndexKeyFactory;
import jp.ossc.nimbus.beans.IndexNotFoundException;
import jp.ossc.nimbus.beans.IndexPropertyAccessException;
import jp.ossc.nimbus.beans.NoSuchPropertyException;
import jp.ossc.nimbus.service.codemaster.PartUpdate;
import jp.ossc.nimbus.service.codemaster.PartUpdateRecords;
import jp.ossc.nimbus.service.codemaster.CodeMasterUpdateKey;

/**
 * ���R�[�h���X�g�B<p>
 * �f�[�^�Z�b�g�̌J��Ԃ��\���f�[�^��\������Bean�ŁA{@link Record ���R�[�h}�̃��X�g�ł���B<br>
 * �J��Ԃ��\���̗v�f�ł��郌�R�[�h�́A�X�L�[�}��`�ɂ���āA�ǂ̂悤�ȃ��R�[�h�i�v���p�e�B���A�^�Ȃǁj���J��Ԃ��̂��𓮓I�Ɍ���ł���B<br>
 * �ȉ��ɃT���v���R�[�h�������B<br>
 * <pre>
 *     import jp.ossc.nimbus.beans.dataset.*;
 *     
 *     // ���R�[�h���X�g�𐶐�
 *     RecordList recordList = new RecordList();
 *     
 *     // ���R�[�h���X�g�̃X�L�[�}���ȉ��̂悤�ɒ�`����
 *     //   �v���p�e�B��  �^
 *     //        A        int
 *     //        B        java.lang.String
 *     //        C        java.lang.String
 *     recordList.setSchema(
 *         ":A,int\n"
 *             + ":B,java.lang.String\n"
 *             + ":C,java.lang.String"
 *     );
 *     
 *     // ���R�[�h1�𐶐����āA�l��ݒ肷��
 *     Record record1 = recordList.createRecord();
 *     record1.setProperty("A", 1);
 *     record1.setProperty("B", "hoge1");
 *     record1.setProperty("C", "fuga1");
 *     recordList.addRecord(record1);
 *     // ���R�[�h2�𐶐����āA�l��ݒ肷��
 *     Record record2 = recordList.createRecord();
 *     record2.setProperty("A", 2);
 *     record2.setProperty("B", "hoge2");
 *     record2.setProperty("C", "fuga2");
 *     recordList.addRecord(record2);
 * </pre>
 * 
 * @author M.Takata
 */
public class RecordList implements Externalizable, List, Cloneable, PartUpdate, RandomAccess{
    
    private static final long serialVersionUID = 6399184480196775369L;
    
    /**
     * ��L�[�ɂ��C���f�b�N�X����\���\�񖼁B<p>
     */
    public static final String PRIMARY_KEY_INDEX_NAME = "$PRIMARY_KEY";
    
    /**
     * ���R�[�h���B<p>
     */
    protected String name;
    
    /**
     * �X�L�[�}������B<p>
     */
    protected String schema;
    
    /**
     * ���R�[�h�N���X�B<p>
     */
    protected Class recordClass;
    
    /**
     * ���R�[�h�X�L�[�}�B<p>
     */
    protected RecordSchema recordSchema;
    
    /**
     * ���R�[�h�̃��X�g�B<p>
     */
    protected List records = Collections.synchronizedList(new ArrayList());
    
    protected BeanTableIndexManager indexManager;
    
    /**
     * �X�V�J�E���g�B<p>
     */
    protected int modCount = 0;
    
    protected int[] partUpdateOrderBy;
    
    protected boolean[] partUpdateIsAsc;
    
    protected boolean isSynchronized = true;
    
    /**
     * ����`�̃��R�[�h���X�g�𐶐�����B<p>
     */
    public RecordList(){
        this(true);
    }
    
    /**
     * ����`�̃��R�[�h���X�g�𐶐�����B<p>
     *
     * @param isSynch ����������ꍇtrue
     */
    public RecordList(boolean isSynch){
        this(null, isSynch);
    }
    
    /**
     * ����`�̃��R�[�h���X�g�𐶐�����B<p>
     *
     * @param name ���R�[�h��
     */
    public RecordList(String name){
        this(name, true);
    }
    
    /**
     * ����`�̃��R�[�h���X�g�𐶐�����B<p>
     *
     * @param name ���R�[�h��
     * @param isSynch ����������ꍇtrue
     */
    public RecordList(String name, boolean isSynch){
        this.name = name;
        isSynchronized = isSynch;
        records = isSynchronized ? Collections.synchronizedList(new ArrayList()) : new ArrayList();
        setRecordClass(Record.class);
    }
    
    /**
     * ��̃��R�[�h���X�g�𐶐�����B<p>
     *
     * @param name ���R�[�h��
     * @param schema �X�L�[�}������
     * @exception PropertySchemaDefineException �v���p�e�B�̃X�L�[�}��`�Ɏ��s�����ꍇ
     */
    public RecordList(String name, String schema)
     throws PropertySchemaDefineException{
        this(name, schema, true);
    }
    
    /**
     * ��̃��R�[�h���X�g�𐶐�����B<p>
     *
     * @param name ���R�[�h��
     * @param schema �X�L�[�}������
     * @param isSynch ����������ꍇtrue
     * @exception PropertySchemaDefineException �v���p�e�B�̃X�L�[�}��`�Ɏ��s�����ꍇ
     */
    public RecordList(String name, String schema, boolean isSynch)
     throws PropertySchemaDefineException{
        this(name, RecordSchema.getInstance(schema), isSynch);
    }
    
    /**
     * ��̃��R�[�h���X�g�𐶐�����B<p>
     *
     * @param name ���R�[�h��
     * @param schema �X�L�[�}
     * @exception PropertySchemaDefineException �v���p�e�B�̃X�L�[�}��`�Ɏ��s�����ꍇ
     */
    public RecordList(String name, RecordSchema schema)
     throws PropertySchemaDefineException{
        this(name, schema, true);
    }
    
    /**
     * ��̃��R�[�h���X�g�𐶐�����B<p>
     *
     * @param name ���R�[�h��
     * @param schema �X�L�[�}
     * @param isSynch ����������ꍇtrue
     * @exception PropertySchemaDefineException �v���p�e�B�̃X�L�[�}��`�Ɏ��s�����ꍇ
     */
    public RecordList(String name, RecordSchema schema, boolean isSynch)
     throws PropertySchemaDefineException{
        this.name = name;
        isSynchronized = isSynch;
        records = isSynchronized ? Collections.synchronizedList(new ArrayList()) : new ArrayList();
        setRecordClass(Record.class);
        setRecordSchema(schema);
    }
    
    /**
     * ��̃��R�[�h���X�g�𐶐�����B<p>
     *
     * @param name ���R�[�h��
     * @param clazz ���R�[�h�N���X
     * @exception PropertySchemaDefineException �v���p�e�B�̃X�L�[�}��`�Ɏ��s�����ꍇ
     */
    public RecordList(String name, Class clazz)
     throws PropertySchemaDefineException{
        this(name, clazz, true);
    }
    
    /**
     * ��̃��R�[�h���X�g�𐶐�����B<p>
     *
     * @param name ���R�[�h��
     * @param clazz ���R�[�h�N���X
     * @param isSynch ����������ꍇtrue
     * @exception PropertySchemaDefineException �v���p�e�B�̃X�L�[�}��`�Ɏ��s�����ꍇ
     */
    public RecordList(String name, Class clazz, boolean isSynch)
     throws PropertySchemaDefineException{
        this.name = name;
        isSynchronized = isSynch;
        records = isSynchronized ? Collections.synchronizedList(new ArrayList()) : new ArrayList();
        setRecordClass(clazz);
    }
    
    /**
     * ���R�[�h�����擾����B<p>
     *
     * @return ���R�[�h��
     */
    public String getName(){
        return name;
    }
    
    /**
     * ���R�[�h����ݒ肷��B<p>
     *
     * @param name ���R�[�h��
     */
    public void setName(String name){
        this.name = name;
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
        if(size() != 0){
            throw new PropertySchemaDefineException("Record already exists.");
        }
        recordSchema = schema;
        this.schema = schema == null ? null : schema.getSchema();
        List primaryKeyNames = null;
        final PropertySchema[] primaryKeys
            = recordSchema.getPrimaryKeyPropertySchemata();
        if(primaryKeys != null){
            for(int i = 0; i < primaryKeys.length; i++){
                if(primaryKeyNames == null){
                    primaryKeyNames = new ArrayList();
                }
                primaryKeyNames.add(primaryKeys[i].getName());
            }
        }
        if(primaryKeyNames == null){
            removeIndex(PRIMARY_KEY_INDEX_NAME);
        }else{
            setIndex(
                PRIMARY_KEY_INDEX_NAME,
                (String[])primaryKeyNames.toArray(
                    new String[primaryKeyNames.size()]
                )
            );
        }
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
    public void replaceRecordSchema(RecordSchema schema) throws PropertySchemaDefineException{
        
        if(recordSchema != null && schema != null && size() != 0){
            for(int i = 0, imax = records.size(); i < imax; i++){
                Record record = (Record)records.get(i);
                record.replaceRecordSchema(schema);
            }
        }
        setRecordSchema(schema);
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
     * ���R�[�h�X�L�[�}���擾����B<p>
     *
     * @return ���R�[�h�X�L�[�}
     */
    public RecordSchema getRecordSchema(){
        return recordSchema;
    }
    
    /**
     * ���R�[�h�̃N���X��ݒ肷��B<p>
     *
     * @param clazz ���R�[�h�̃N���X
     * @exception PropertySchemaDefineException �v���p�e�B�̃X�L�[�}��`�Ɏ��s�����ꍇ
     */
    public void setRecordClass(Class clazz) throws PropertySchemaDefineException{
        if(Record.class.equals(clazz)){
            indexManager = new BeanTableIndexManager(clazz, isSynchronized);
        }else{
            Record record = null;
            try{
                record = (Record)clazz.newInstance();
            }catch(InstantiationException e){
                throw new PropertySchemaDefineException(null, e);
            }catch(IllegalAccessException e){
                throw new PropertySchemaDefineException(null, e);
            }catch(ClassCastException e){
                throw new PropertySchemaDefineException(null, e);
            }
            recordClass = clazz;
            indexManager = new BeanTableIndexManager(clazz, isSynchronized);
            if(record.getRecordSchema() != null){
                setRecordSchema(record.getRecordSchema());
            }
        }
    }
    
    /**
     * ���R�[�h�̃N���X���擾����B<p>
     *
     * @return ���R�[�h�̃N���X
     */
    public Class getRecordClass(){
        return recordClass == null ? Record.class : recordClass;
    }
    
    /**
     * �V�������R�[�h�𐶐�����B<p>
     *
     * @return �V�������R�[�h
     */
    public Record createRecord(){
        if(recordClass == null){
            return new Record(recordSchema);
        }else{
            try{
                return (Record)recordClass.newInstance();
            }catch(Exception e){
                return new Record(recordSchema);
            }
        }
    }
    
    /**
     * �w�肳�ꂽ�C���f�b�N�X�̃��R�[�h���擾����B<p>
     *
     * @param index �C���f�b�N�X
     * @return ���R�[�h
     */
    public Record getRecord(int index){
        return (Record)get(index);
    }
    
    /**
     * ���R�[�h��ǉ�����B<p>
     *
     * @param r ���R�[�h
     */
    public void addRecord(Record r){
        add(r);
    }
    
    /**
     * �w�肳�ꂽ�C���f�b�N�X�Ƀ��R�[�h��}������B<p>
     *
     * @param index �C���f�b�N�X
     * @param r ���R�[�h
     */
    public void addRecord(int index, Record r){
        add(index, r);
    }
    
    /**
     * �w�肳�ꂽ�C���f�b�N�X�̃��R�[�h��u��������B<p>
     * �A���A���̃��\�b�h�Œu��������ꂽ���R�[�h�́A�C���f�b�N�X�^�����̑ΏۂɂȂ�Ȃ��B<br>
     *
     * @param index �C���f�b�N�X
     * @param r ���R�[�h
     * @return �u��������ꂽ�Â����R�[�h
     */
    public Record setRecord(int index, Record r){
        return (Record)set(index, r);
    }
    
    /**
     * �w�肳�ꂽ���R�[�h���폜����B<p>
     *
     * @param r ���R�[�h
     */
    public void removeRecord(Record r){
        remove(r);
    }
    
    /**
     * �w�肳�ꂽ�C���f�b�N�X�̃��R�[�h���폜����B<p>
     *
     * @param index �C���f�b�N�X
     * @return �폜���ꂽ���R�[�h
     */
    public Record removeRecord(int index){
        return (Record)remove(index);
    }
    
    /**
     * �C���f�b�N�X��ǉ�����B<p>
     * �C���f�b�N�ɂ́A�P��̃v���p�e�B�ō\�������P���C���f�b�N�X�ƁA�����̃v���p�e�B�ō\������镡���C���f�b�N�X�����݂���B<br>
     * �����C���f�b�N�X��ǉ������ꍇ�́A�����I�ɂ��̗v�f�ƂȂ�P��v���p�e�B�̒P���C���f�b�N�X�������I�ɐ��������B<p>
     * �A���A�����������ꂽ�P��C���f�b�N�X�́A�C���f�b�N�X���������Ȃ����߁A�C���f�b�N�X���ł͎w��ł����A�v���p�e�B���Ŏw�肵�Ďg�p����B<br>
     * �C���f�b�N�X�̎�ނɂ���āA�g�p�ł��錟���@�\���قȂ�B�P���C���f�b�N�X�́A��v�����Ɣ͈͌����̗������\�����A�����C���f�b�N�X�́A��v�����̂݉\�ł���B<br>
     *
     * @param name �C���f�b�N�X��
     * @param props �C���f�b�N�X�𒣂�Record�̃v���p�e�B���z��
     * @exception PropertyGetException �w�肳�ꂽ�v���p�e�B��Record�ɑ��݂��Ȃ��ꍇ
     */
    public void setIndex(String name, String[] props) throws PropertyGetException{
        if(recordSchema == null){
            throw new DataSetException("Schema not initalize.");
        }
        for(int i = 0; i < props.length; i++){
            if(recordSchema.getPropertySchema(props[i]) == null){
                throw new PropertyGetException(null, "No such property : " + props[i]);
            }
        }
        try{
            indexManager.setIndex(name, props);
        }catch(NoSuchPropertyException e){
            throw new PropertyGetException(null, "No such property", e);
        }
    }
    
    /**
     * �J�X�^�}�C�Y�����C���f�b�N�X��ǉ�����B<p>
     *
     * @param name �C���f�b�N�X��
     * @param keyFactory �C���f�b�N�X�̃L�[�𐶐�����t�@�N�g��
     * @see #setIndex(String, String[])
     */
    public void setIndex(String name, BeanTableIndexKeyFactory keyFactory){
        indexManager.setIndex(name, keyFactory);
    }
    
    /**
     * �C���f�b�N�X���폜����B<p>
     *
     * @param name �C���f�b�N�X��
     */
    public void removeIndex(String name){
        indexManager.removeIndex(name);
    }
    
    /**
     * �C���f�b�N�X���ĉ�͂���B<p>
     */
    public void analyzeIndex(){
        indexManager.clear();
        indexManager.addAll(records);
    }
    
    /**
     * �C���f�b�N�X���g�����������s���r���[���쐬����B<p>
     * 
     * @return �����r���[
     */
    public BeanTableView createView(){
        return new BeanTableView(indexManager);
    }
    
    /**
     * �v���C�}���L�[�Ō�������B<p>
     * �v���C�}���L�[�L�[�������s�����߂ɂ́A�X�L�[�}��`�ɂ����āA���j�[�N�L�[�t���O��ݒ肷��K�v������B<br>
     *
     * @param key �����L�[���R�[�h
     * @return �������ʁB�����ɍ��v�������R�[�h
     * @exception IndexNotFoundException �v���C�}���L�[�ɑ΂���C���f�b�N�X�����݂��Ȃ��ꍇ
     * @exception IndexPropertyAccessException �v���C�}���L�[�̃v���p�e�B�̎擾�ŗ�O�����������ꍇ
     */
    public Record searchByPrimaryKey(Record key) throws IndexNotFoundException, IndexPropertyAccessException{
        return (Record)indexManager.searchByPrimaryElement(key, PRIMARY_KEY_INDEX_NAME, null);
    }
    
    /**
     * ���A���^�L�[�������s���B<p>
     * ���R�[�h���X�g����A�w�肵���v���p�e�B���̒l���A�����L�[���R�[�h�̒l�ƈ�v�������R�[�h����������B<br>
     *
     * @param key �����L�[���R�[�h
     * @param propertyNames �����L�[�ƂȂ�v���p�e�B��
     * @return �������ʁB�����ɍ��v�������R�[�h�̔z��
     */
    public RecordList searchByKey(Record key, String[] propertyNames){
        if(size() == 0){
            return cloneSchema();
        }
        if(recordSchema == null){
            throw new DataSetException("Schema not initalize.");
        }
        final int[] propertyIndexes = new int[propertyNames.length];
        for(int i = 0; i < propertyNames.length; i++){
            propertyIndexes[i] = recordSchema.getPropertyIndex(propertyNames[i]);
            if(propertyIndexes[i] == -1){
                throw new DataSetException("No such property " + propertyNames[i]);
            }
        }
        return searchByKey(key, propertyIndexes);
    }
    
    /**
     * ���A���^�L�[�������s���B<p>
     * ���R�[�h���X�g����A�w�肵���v���p�e�B���̒l���A�����L�[���R�[�h�̒l�ƈ�v�������R�[�h����������B<br>
     *
     * @param key �����L�[���R�[�h
     * @param propertyIndexes �����L�[�ƂȂ�v���p�e�B��
     * @return �������ʁB�����ɍ��v�������R�[�h�̔z��
     */
    public RecordList searchByKey(Record key, int[] propertyIndexes){
        RecordList result = cloneSchema();
        if(size() == 0){
            return result;
        }
        if(recordSchema == null){
            throw new DataSetException("Schema not initalize.");
        }
        for(int i = 0, imax = size(); i < imax; i++){
            Record rd = getRecord(i);
            boolean isMatch = true;
            for(int j = 0; j < propertyIndexes.length; j++){
                Object val1 = key.getProperty(propertyIndexes[j]);
                Object val2 = rd.getProperty(propertyIndexes[j]);
                if(val1 == null && val2 == null){
                    continue;
                }else if(val1 == null && val2 != null
                    || val1 != null && val2 == null
                    || !val1.equals(val2)
                ){
                    isMatch = false;
                    break;
                }
            }
            if(isMatch){
                result.add(rd);
            }
        }
        return result;
    }
    
    /**
     * ���A���^�������s���B<p>
     * ���R�[�h���X�g����A�������ɍ��v���郌�R�[�h����������B<br>
     * �܂��A�����ɂ́A���R�[�h��~�ς���ۂɌ�������C���f�b�N�X�^�����ƁA�~�ς��ꂽ���R�[�h���烊�A���Ɍ������郊�A���^����������B<br>
     * ���A���^�����̗��_�́A���������ɁA���I�ɕς��ϐ����w�肵�A���̕ϐ��l������valueMap�ŗ^���鎖���ł��鎖�ł���B<br>
     * <p>
     * �������́A<a href="http://jakarta.apache.org/commons/jexl/">Jakarta Commons Jexl</a>�̎�������g�p����B<br>
     * ���A���^�����ł́A���R�[�h�̗�̒l���A�񖼂��w�肷�鎖�ŁA�����ŎQ�Ƃ��鎖���ł���̂ɉ����āA�C�ӂ̕ϐ����������ɒ�`���A���̒l������valueMap�ŗ^���鎖���ł���B<br>
     * <pre>
     *  ��FA == '1' and B &gt;= 3
     * </pre>
     *
     * @param condition ������
     * @param valueMap ���������̕ϐ��}�b�v
     * @return �������ʁB�����ɍ��v�������R�[�h�̃��X�g
     * @exception DataSetException ���������s���ȏꍇ
     */
    public RecordList realSearch(String condition, Map valueMap)
     throws DataSetException{
        RecordList result = cloneSchema();
        if(size() == 0){
            return result;
        }
        if(recordSchema == null){
            throw new DataSetException("Schema not initalize.");
        }
        try{
            final Expression exp
                 = ExpressionFactory.createExpression(condition);
            final JexlContext context = JexlHelper.createContext();
            for(int i = 0, imax = size(); i < imax; i++){
                Record rd = getRecord(i);
                for(int j = 0, jmax = recordSchema.getPropertySize();
                        j < jmax; j++){
                    final PropertySchema prop = recordSchema.getPropertySchema(j);
                    final String propName = prop.getName();
                    context.getVars().put(propName, rd.getProperty(propName));
                }
                if(valueMap != null){
                    context.getVars().putAll(valueMap);
                }
                final Boolean ret = (Boolean)exp.evaluate(context);
                if(ret != null && ret.booleanValue()){
                    result.add(rd);
                }
            }
        }catch(Exception e){
            throw new DataSetException(e);
        }
        return result;
    }
    
    /**
     * �w�肳�ꂽ�v���p�e�B���̃v���p�e�B���\�[�g�L�[�ɂ��āA�����Ń\�[�g����B<p>
     *
     * @param orderBy �\�[�g�L�[�ƂȂ�v���p�e�B���z��
     */
    public void sort(String[] orderBy){
        sort(orderBy, null);
    }
    
    /**
     * �w�肳�ꂽ�v���p�e�B���̃v���p�e�B���\�[�g�L�[�ɂ��āA�����Ń\�[�g����B<p>
     *
     * @param records �\�[�g�Ώۂ̃��R�[�h�z��
     * @param orderBy �\�[�g�L�[�ƂȂ�v���p�e�B���z��
     */
    public static void sort(Record[] records, String[] orderBy){
        sort(records, orderBy, null);
    }
    
    /**
     * �w�肳�ꂽ�v���p�e�B���̃v���p�e�B���\�[�g�L�[�ɂ��āA�����Ń\�[�g����B<p>
     *
     * @param records �\�[�g�Ώۂ̃��R�[�h���X�g
     * @param orderBy �\�[�g�L�[�ƂȂ�v���p�e�B���z��
     */
    public static void sort(List records, String[] orderBy){
        sort(records, orderBy, (boolean[])null);
    }
    
    /**
     * �w�肳�ꂽ�v���p�e�B�C���f�b�N�X�̃v���p�e�B���\�[�g�L�[�ɂ��āA�����Ń\�[�g����B<p>
     *
     * @param orderBy �\�[�g�L�[�ƂȂ�v���p�e�B�C���f�b�N�X�z��
     */
    public void sort(int[] orderBy){
        sort(orderBy, null);
    }
    
    /**
     * �w�肳�ꂽ�v���p�e�B�C���f�b�N�X�̃v���p�e�B���\�[�g�L�[�ɂ��āA�����Ń\�[�g����B<p>
     *
     * @param records �\�[�g�Ώۂ̃��R�[�h�z��
     * @param orderBy �\�[�g�L�[�ƂȂ�v���p�e�B�C���f�b�N�X�z��
     */
    public static void sort(Record[] records, int[] orderBy){
        sort(records, orderBy, null);
    }
    
    /**
     * �w�肳�ꂽ�v���p�e�B�C���f�b�N�X�̃v���p�e�B���\�[�g�L�[�ɂ��āA�����Ń\�[�g����B<p>
     *
     * @param records �\�[�g�Ώۂ̃��R�[�h���X�g
     * @param orderBy �\�[�g�L�[�ƂȂ�v���p�e�B�C���f�b�N�X�z��
     */
    public static void sort(List records, int[] orderBy){
        sort(records, orderBy, (boolean[])null);
    }
    
    
    /**
     * �w�肳�ꂽ�v���p�e�B�C���f�b�N�X�̃v���p�e�B���\�[�g�L�[�ɂ��ă\�[�g����B<p>
     *
     * @param orderBy �\�[�g�L�[�ƂȂ�v���p�e�B�C���f�b�N�X�z��
     * @param isAsc �����\�[�g����ꍇ��true�B�~���\�[�g����ꍇ�́Afalse
     */
    public void sort(int[] orderBy, boolean[] isAsc){
        if(records.size() < 2){
            return;
        }
        Collections.sort(records, new RecordComparator(recordSchema, orderBy, isAsc));
    }
    
    /**
     * �w�肳�ꂽ�v���p�e�B�C���f�b�N�X�̃v���p�e�B���\�[�g�L�[�ɂ��ă\�[�g����B<p>
     *
     * @param records �\�[�g�Ώۂ̃��R�[�h�z��
     * @param orderBy �\�[�g�L�[�ƂȂ�v���p�e�B�C���f�b�N�X�z��
     * @param isAsc �����\�[�g����ꍇ��true�B�~���\�[�g����ꍇ�́Afalse
     */
    public static void sort(Record[] records, int[] orderBy, boolean[] isAsc){
        if(records == null || records.length < 2){
            return;
        }
        Arrays.sort(records, new RecordComparator(records[0].getRecordSchema(), orderBy, isAsc));
    }
    
    /**
     * �w�肳�ꂽ�v���p�e�B�C���f�b�N�X�̃v���p�e�B���\�[�g�L�[�ɂ��ă\�[�g����B<p>
     *
     * @param records �\�[�g�Ώۂ̃��R�[�h���X�g
     * @param orderBy �\�[�g�L�[�ƂȂ�v���p�e�B�C���f�b�N�X�z��
     * @param isAsc �����\�[�g����ꍇ��true�B�~���\�[�g����ꍇ�́Afalse
     */
    public static void sort(List records, int[] orderBy, boolean[] isAsc){
        if(records == null || records.size() < 2){
            return;
        }
        Collections.sort(records, new RecordComparator(((Record)records.get(0)).getRecordSchema(), orderBy, isAsc));
    }
    
    /**
     * �w�肳�ꂽ�v���p�e�B���̃v���p�e�B���\�[�g�L�[�ɂ��ă\�[�g����B<p>
     *
     * @param orderBy �\�[�g�L�[�ƂȂ�v���p�e�B���z��
     * @param isAsc �����\�[�g����ꍇ��true�B�~���\�[�g����ꍇ�́Afalse
     */
    public void sort(String[] orderBy, boolean[] isAsc){
        if(records.size() < 2){
            return;
        }
        Collections.sort(records, new RecordComparator(orderBy, isAsc));
    }
    
    /**
     * �w�肳�ꂽ�v���p�e�B���̃v���p�e�B���\�[�g�L�[�ɂ��ă\�[�g����B<p>
     *
     * @param records �\�[�g�Ώۂ̃��R�[�h�z��
     * @param orderBy �\�[�g�L�[�ƂȂ�v���p�e�B���z��
     * @param isAsc �����\�[�g����ꍇ��true�B�~���\�[�g����ꍇ�́Afalse
     */
    public static void sort(Record[] records, String[] orderBy, boolean[] isAsc){
        if(records == null || records.length < 2){
            return;
        }
        Arrays.sort(records, new RecordComparator(orderBy, isAsc));
    }
    
    /**
     * �w�肳�ꂽ�v���p�e�B���̃v���p�e�B���\�[�g�L�[�ɂ��ă\�[�g����B<p>
     *
     * @param records �\�[�g�Ώۂ̃��R�[�h���X�g
     * @param orderBy �\�[�g�L�[�ƂȂ�v���p�e�B���z��
     * @param isAsc �����\�[�g����ꍇ��true�B�~���\�[�g����ꍇ�́Afalse
     */
    public static void sort(List records, String[] orderBy, boolean[] isAsc){
        if(records == null || records.size() < 2){
            return;
        }
        Collections.sort(records, new RecordComparator(orderBy, isAsc));
    }
    
    // java.util.List��JavaDoc
    public int size(){
        return records.size();
    }
    
    // java.util.List��JavaDoc
    public boolean isEmpty(){
        return records.isEmpty();
    }
    
    // java.util.List��JavaDoc
    public boolean contains(Object o){
        return records.contains(o);
    }
    
    // java.util.List��JavaDoc
    public Iterator iterator(){
        return new RecordIterator();
    }
    
    // java.util.List��JavaDoc
    public ListIterator listIterator(){
        return listIterator(0);
    }
    
    // java.util.List��JavaDoc
    public ListIterator listIterator(int index){
        return new RecordListIterator(index);
    }
    
    // java.util.List��JavaDoc
    public List subList(int fromIndex, int toIndex){
        return records.subList(fromIndex, toIndex);
    }
    
    // java.util.List��JavaDoc
    public Object[] toArray(){
        return records.toArray();
    }
    
    // java.util.List��JavaDoc
    public Object[] toArray(Object[] a){
        return records.toArray(a);
    }
    
    // java.util.List��JavaDoc
    public boolean add(Object o){
        if(o == null){
            return false;
        }
        if(!(o instanceof Record)){
            throw new DataSetException("Not record : " + o);
        }
        if(isSynchronized){
            synchronized(records){
                return addInternal((Record)o);
            }
        }else{
            return addInternal((Record)o);
        }
    }
    
    private boolean addInternal(Record rec){
        if(indexManager.getIndex(PRIMARY_KEY_INDEX_NAME) != null){
            if(indexManager.searchByPrimaryElement(rec, PRIMARY_KEY_INDEX_NAME, null) != null){
                throw new DataSetException("Duplicate primary key. " + rec);
            }
        }
        rec.setIndex(size());
        rec.setRecordList(this);
        boolean isAdd = records.add(rec);
        if(isAdd){
            indexManager.add(rec);
            modCount++;
        }
        return isAdd;
    }
    
    // java.util.List��JavaDoc
    public void add(int index, Object element){
        if(element == null){
            return;
        }
        if(!(element instanceof Record)){
            throw new DataSetException("Not record : " + element);
        }
        if(isSynchronized){
            synchronized(records){
                addInternal(index, (Record)element);
            }
        }else{
            addInternal(index, (Record)element);
        }
    }
    
    private void addInternal(int index, Record rec){
        if(indexManager.getIndex(PRIMARY_KEY_INDEX_NAME) != null){
            if(indexManager.searchByPrimaryElement(rec, PRIMARY_KEY_INDEX_NAME, null) != null){
                throw new DataSetException("Duplicate primary key. " + rec);
            }
        }
        rec.setIndex(index);
        rec.setRecordList(this);
        records.add(index, rec);
        for(int i = index + 1, imax = size(); i < imax; i++){
            Record record = (Record)get(i);
            if(record != null){
                record.setIndex(record.getIndex() + 1);
            }
        }
        indexManager.add(rec);
        modCount++;
    }
    
    // java.util.List��JavaDoc
    public Object set(int index, Object element){
        if(element != null && !(element instanceof Record)){
            throw new DataSetException("Not record : " + element);
        }
        if(isSynchronized){
            synchronized(records){
                return setInternal(index, (Record)element);
            }
        }else{
            return setInternal(index, (Record)element);
        }
    }
    
    private Object setInternal(int index, Record rec){
        if(indexManager.getIndex(PRIMARY_KEY_INDEX_NAME) != null){
            Record old = (Record)indexManager.searchByPrimaryElement(rec, PRIMARY_KEY_INDEX_NAME, null);
            if(old != null && old.getIndex() != index){
                throw new DataSetException("Duplicate primary key. " + rec);
            }
        }
        rec.setIndex(index);
        rec.setRecordList(this);
        Record old = (Record)records.set(index, rec);
        indexManager.remove(old);
        indexManager.add(rec);
        old.setIndex(-1);
        old.setRecordList(null);
        return old;
    }
    
    // java.util.List��JavaDoc
    public Object get(int index){
        return records.get(index);
    }
    
    // java.util.List��JavaDoc
    public int indexOf(Object o){
        return records.indexOf(o);
    }
    
    // java.util.List��JavaDoc
    public int lastIndexOf(Object o){
        return records.lastIndexOf(o);
    }
    
    // java.util.List��JavaDoc
    public boolean remove(Object o){
        if(isSynchronized){
            synchronized(records){
                return removeInternal(o);
            }
        }else{
            return removeInternal(o);
        }
    }
    
    private boolean removeInternal(Object o){
        final int index = records.indexOf(o);
        if(index != -1){
            removeInternal(index);
        }
        return index != -1;
    }
    
    // java.util.List��JavaDoc
    public Object remove(int index){
        if(isSynchronized){
            synchronized(records){
                return removeInternal(index);
            }
        }else{
            return removeInternal(index);
        }
    }
    
    private Object removeInternal(int index){
        Object old = records.remove(index);
        if(old != null){
            indexManager.remove(old);
            ((Record)old).setIndex(-1);
            ((Record)old).setRecordList(null);
            for(int i = index, imax = size(); i < imax; i++){
                Record record = (Record)get(i);
                if(record != null){
                    record.setIndex(record.getIndex() - 1);
                }
            }
            modCount++;
        }
        return old;
    }
    
    // java.util.List��JavaDoc
    public boolean containsAll(Collection c){
        return records.containsAll(c);
    }
    
    // java.util.List��JavaDoc
    public boolean addAll(Collection c){
        if(c == null || c.size() == 0){
            return false;
        }
        Object[] vals = c.toArray();
        boolean result = false;
        for(int i = 0; i < vals.length; i++){
            result |= add(vals[i]);
        }
        if(result){
            modCount++;
        }
        return result;
    }
    
    // java.util.List��JavaDoc
    public boolean addAll(int index, Collection c){
        if(c == null || c.size() == 0){
            return false;
        }
        Object[] vals = c.toArray();
        for(int i = vals.length; --i >= 0;){
            add(index, vals[i]);
        }
        modCount++;
        return true;
    }
    
    // java.util.List��JavaDoc
    public boolean removeAll(Collection c){
        boolean isRemoved = false;
        final Iterator itr = c.iterator();
        while(itr.hasNext()){
            isRemoved |= remove(itr.next());
        }
        if(isRemoved){
            modCount++;
        }
        return isRemoved;
    }
    
    // java.util.List��JavaDoc
    public boolean retainAll(Collection c){
        boolean isRemoved = false;
        final Iterator itr = iterator();
        while(itr.hasNext()){
            Object record = itr.next();
            if(!c.contains(record)){
                itr.remove();
                isRemoved = true;
            }
        }
        if(isRemoved){
            modCount++;
        }
        return isRemoved;
    }
    
    /**
     * �S�Ẵ��R�[�h�y�сA�S�Ă̒~�ό^�������ʂ��폜����B<p>
     */
    public void clear(){
        if(isSynchronized){
            synchronized(records){
                clearInternal();
            }
        }else{
            clearInternal();
        }
    }
    private void clearInternal(){
        for(int i = 0, imax = records.size(); i < imax; i++){
            Record record = (Record)records.remove(0);
            if(record != null){
                record.setIndex(-1);
                record.setRecordList(null);
            }
        }
        indexManager.clear();
        modCount++;
    }
    
    /**
     * �T�C�Y�����X�g�̌��݂̃T�C�Y�ɏk������B<p>
     * �A�v���P�[�V�����ł́A���̃I�y���[�V�����ŃT�C�Y���ŏ��ɂ��邱�Ƃ��ł���B <br>
     */
    public void trimToSize(){
        Class clazz = null;
        try{
            clazz = Class.forName("java.util.Collections$SynchronizedCollection");
        }catch(ClassNotFoundException e){
            return;
        }
        Object mutex = records;
        try{
            Field field = clazz.getDeclaredField("mutex");
            field.setAccessible(true);
            mutex = field.get(records);
        }catch(IllegalAccessException e){
        }catch(NoSuchFieldException e){
        }catch(SecurityException e){
        }
        ArrayList list = null;
        try{
            Field field = clazz.getDeclaredField("c");
            field.setAccessible(true);
            list = (ArrayList)field.get(records);
        }catch(IllegalAccessException e){
            return;
        }catch(NoSuchFieldException e){
            return;
        }catch(SecurityException e){
            return;
        }
        synchronized(mutex){
            list.trimToSize();
        }
    }
    
    /**
     * �S�Ẵ��R�[�h�����؂���B<p>
     *
     * @return ���،��ʁBtrue�̏ꍇ�A���ؐ���
     * @exception PropertyGetException �v���p�e�B�̎擾�Ɏ��s�����ꍇ
     * @exception PropertyValidateException �v���p�e�B�̌��؎��ɗ�O�����������ꍇ
     */
    public boolean validate() throws PropertyGetException, PropertyValidateException{
        if(isSynchronized){
            synchronized(records){
                return validateInternal();
            }
        }else{
            return validateInternal();
        }
    }
    
    private boolean validateInternal() throws PropertyGetException, PropertyValidateException{
        for(int i = 0, imax = records.size(); i < imax; i++){
            Record record = (Record)records.get(i);
            if(!record.validate()){
                return false;
            }
        }
        return true;
    }
    
    /**
     * ���R�[�h���X�g�𕡐�����B<p>
     *
     * @return �����������R�[�h���X�g
     */
    public Object clone(){
        return cloneRecordList();
    }
    
    /**
     * �����X�L�[�}�������f�[�^�������Ȃ���̃��R�[�h���X�g�𕡐�����B<p>
     *
     * @return ����������̃��R�[�h���X�g
     */
    public RecordList cloneSchema(){
        RecordList clone = null;
        try{
            clone = (RecordList)super.clone();
            clone.modCount = 0;
            clone.records = isSynchronized ? Collections.synchronizedList(new ArrayList()) : new ArrayList();
            if(partUpdateOrderBy != null){
                clone.partUpdateOrderBy = new int[partUpdateOrderBy.length];
                System.arraycopy(
                    partUpdateOrderBy,
                    0,
                    clone.partUpdateOrderBy,
                    0,
                    partUpdateOrderBy.length
                );
            }
            if(partUpdateIsAsc != null){
                clone.partUpdateIsAsc = new boolean[partUpdateIsAsc.length];
                System.arraycopy(
                    partUpdateIsAsc,
                    0,
                    clone.partUpdateIsAsc,
                    0,
                    partUpdateIsAsc.length
                );
            }
            clone.indexManager = indexManager.cloneEmpty(isSynchronized);
        }catch(CloneNotSupportedException e){
            return null;
        }
        return clone;
    }
    
    /**
     * ���R�[�h���X�g�𕡐�����B<p>
     *
     * @return �����������R�[�h���X�g
     */
    public RecordList cloneRecordList(){
        final RecordList recList = cloneSchema();
        if(size() == 0){
            return recList;
        }
        if(isSynchronized){
            synchronized(records){
                for(int i = 0; i < records.size(); i++){
                    final Record rec = ((Record)records.get(i)).cloneRecord();
                    recList.addRecord(rec);
                }
            }
        }else{
            for(int i = 0; i < records.size(); i++){
                final Record rec = ((Record)records.get(i)).cloneRecord();
                recList.addRecord(rec);
            }
        }
        return recList;
    }
    
    /**
     *  �����X�V���ɁA�w�肳�ꂽ�v���p�e�B���̗���\�[�g�L�[�ɂ��ă\�[�g����悤�ɐݒ肷��B<p>
     *
     * @param orderBy �\�[�g�L�[�ƂȂ�v���p�e�B���z��
     * @param isAsc �����\�[�g����ꍇ��true�B�~���\�[�g����ꍇ�́Afalse
     */
    public void setPartUpdateSort(String[] orderBy, boolean[] isAsc){
        final int[] propertyIndexes = new int[orderBy.length];
        for(int i = 0; i < orderBy.length; i++){
            propertyIndexes[i] = recordSchema.getPropertyIndex(orderBy[i]);
            if(propertyIndexes[i] == -1){
                throw new DataSetException("No such property " + orderBy[i]);
            }
        }
        setPartUpdateSort(propertyIndexes, isAsc);
    }
    
    /**
     *  �����X�V���ɁA�w�肳�ꂽ�C���f�b�N�X�̃v���p�e�B���\�[�g�L�[�ɂ��ă\�[�g����悤�ɐݒ肷��B<p>
     *
     * @param orderBy �\�[�g�L�[�ƂȂ�v���p�e�B�C���f�b�N�X�z��
     * @param isAsc �����\�[�g����ꍇ��true�B�~���\�[�g����ꍇ�́Afalse
     */
    public void setPartUpdateSort(int[] orderBy, boolean[] isAsc){
        if(orderBy == null || orderBy.length == 0){
            throw new DataSetException("Property index array is empty.");
        }
        if(isAsc != null && orderBy.length != isAsc.length){
            throw new DataSetException("Length of property index array and sort flag array is unmatch.");
        }
        
        final int fieldSize = recordSchema.getPropertySize();
        for(int i = 0; i < orderBy.length; i++){
            if(orderBy[i] >= fieldSize){
                throw new DataSetException("No such property " + orderBy[i]);
            }
        }
        
        partUpdateOrderBy = orderBy;
        partUpdateIsAsc = isAsc;
    }
    
    /**
     * �X�V�����i�[�����R�[�h�}�X�^�����X�V���R�[�h�𐶐�����B<p>
     *
     * @param updateType �X�V�^�C�v
     * @param containsValue �X�V���ꂽRecord���R�[�h�}�X�^�����X�V���R�[�h�Ɋ܂߂�ꍇ�́Atrue
     * @return �R�[�h�}�X�^�����X�V���R�[�h
     * @exception DataSetException �R�[�h�}�X�^�����X�V���R�[�h�̐����Ɏ��s�����ꍇ
     */
    public PartUpdateRecords createPartUpdateRecords(
        PartUpdateRecords records,
        int updateType,
        boolean containsValue
    ) throws DataSetException{
        if(isSynchronized){
            synchronized(records){
                return createPartUpdateRecordsInternal(records, updateType, containsValue);
            }
        }else{
            return createPartUpdateRecordsInternal(records, updateType, containsValue);
        }
    }
    
    private PartUpdateRecords createPartUpdateRecordsInternal(
        PartUpdateRecords records,
        int updateType,
        boolean containsValue
    ) throws DataSetException{
        if(records == null){
            records = new PartUpdateRecords();
        }
        for(int i = 0, imax = this.records.size(); i < imax; i++){
            Record record = (Record)this.records.get(i);
            CodeMasterUpdateKey key = record.createCodeMasterUpdateKey();
            key.setUpdateType(updateType);
            if(containsValue){
                records.addRecord(key, record);
            }else{
                records.addRecord(key);
            }
        }
        return records;
    }
    
    /**
     * �w�肳�ꂽ�R�[�h�}�X�^�X�V�L�[�ɊY�����郌�R�[�h���i�[���������X�V�����쐬����B<p>
     *
     * @param key �R�[�h�}�X�^�X�V�L�[
     * @return �X�V���R�[�h���܂񂾕����X�V���
     */
    public PartUpdateRecords fillPartUpdateRecords(CodeMasterUpdateKey key){
        PartUpdateRecords records = new PartUpdateRecords();
        records.addRecord(key);
        return fillPartUpdateRecords(records);
    }
    
    /**
     * �w�肳�ꂽ�����X�V���ɊY�����郌�R�[�h���i�[���������X�V�����쐬����B<p>
     *
     * @param records �����X�V���
     * @return �X�V���R�[�h���܂񂾕����X�V���
     */
    public PartUpdateRecords fillPartUpdateRecords(PartUpdateRecords records){
        if(records == null || records.size() == 0
             || (!records.containsAdd() && !records.containsUpdate())){
            return records;
        }
        records.setFilledRecord(true);
        Record rec = createRecord();
        final CodeMasterUpdateKey[] keys = records.getKeyArray();
        for(int i = 0; i < keys.length; i++){
            CodeMasterUpdateKey key = (CodeMasterUpdateKey)keys[i];
            final int updateType = key.getUpdateType();
            
            records.removeRecord(key);
            
            // �����p��Record�Ɍ����L�[��ݒ肷��
            rec.setCodeMasterUpdateKey(key);
            
            // ����RecordSet�̎�L�[�݂̂�������CodeMasterUpdateKey�ɕϊ�����
            key = rec.createCodeMasterUpdateKey(key);
            key.setUpdateType(updateType);
            
            // �폜�̏ꍇ�́ACodeMasterUpdateKey�����o�^������
            if(key.isRemove()){
                records.addRecord(key);
                continue;
            }
            
            // �ǉ��܂��͍X�V���ꂽRecord����������
            final Record searchRec = searchByPrimaryKey(rec);
            records.addRecord(key, searchRec);
        }
        return records;
    }
    
    /**
     * �����X�V������荞�񂾁A�f�B�[�v�R�s�[�C���X�^���X�𐶐�����B<p>
     *
     * @param records �����X�V���
     * @return �����X�V������荞�񂾁A�f�B�[�v�R�s�[�C���X�^���X
     */
    public PartUpdate cloneAndUpdate(PartUpdateRecords records){
        if(isSynchronized){
            synchronized(records){
                return cloneAndUpdateInternal(records);
            }
        }else{
            return cloneAndUpdateInternal(records);
        }
    }
    
    private PartUpdate cloneAndUpdateInternal(PartUpdateRecords records){
        final RecordList newRecList = cloneSchema();
        CodeMasterUpdateKey tmpKey = new CodeMasterUpdateKey();
        CodeMasterUpdateKey key = null;
        for(int i = 0, imax = this.records.size(); i < imax; i++){
            Record oldRecord = (Record)this.records.get(i);
            tmpKey = oldRecord.createCodeMasterUpdateKey(tmpKey);
            key = records == null ? null : records.getKey(tmpKey);
            Record newRecord = null;
            if(key == null){
                newRecord = oldRecord.cloneRecord();
            }else{
                switch(key.getUpdateType()){
                case CodeMasterUpdateKey.UPDATE_TYPE_ADD:
                case CodeMasterUpdateKey.UPDATE_TYPE_UPDATE:
                    newRecord = (Record)records.removeRecord(key);
                    break;
                case CodeMasterUpdateKey.UPDATE_TYPE_REMOVE:
                default:
                    records.removeRecord(key);
                    continue;
                }
            }
            if(newRecord != null){
                newRecList.addRecord(newRecord);
            }
        }
        if(records != null && records.size() != 0){
            final Iterator itr = records.getRecords().entrySet().iterator();
            while(itr.hasNext()){
                Map.Entry entry = (Map.Entry)itr.next();
                switch(((CodeMasterUpdateKey)entry.getKey()).getUpdateType()){
                case CodeMasterUpdateKey.UPDATE_TYPE_ADD:
                case CodeMasterUpdateKey.UPDATE_TYPE_UPDATE:
                    final Record record = (Record)entry.getValue();
                    if(record != null){
                        newRecList.addRecord(record);
                    }
                    break;
                default:
                }
            }
        }
        if(partUpdateOrderBy != null && partUpdateOrderBy.length != 0){
            newRecList.sort(partUpdateOrderBy, partUpdateIsAsc);
        }
        
        return newRecList;
    }
    
    protected static void writeInt(ObjectOutput out, int val) throws IOException{
        if(val >= Byte.MIN_VALUE && val <= Byte.MAX_VALUE){
            out.writeByte((byte)1);
            out.writeByte((byte)val);
        }else if(val >= Short.MIN_VALUE && val <= Short.MAX_VALUE){
            out.writeByte((byte)2);
            out.writeShort((short)val);
        }else{
            out.writeByte((byte)3);
            out.writeInt(val);
        }
    }
    
    protected static int readInt(ObjectInput in) throws IOException{
        final int type = in.readByte();
        switch(type){
        case 1:
            return in.readByte();
        case 2:
            return in.readShort();
        default:
            return in.readInt();
        }
    }
    
    public void writeExternal(ObjectOutput out) throws IOException{
        if(isSynchronized){
            synchronized(records){
                writeExternalInternal(out);
            }
        }else{
            writeExternalInternal(out);
        }
    }
    
    protected void writeExternalInternal(ObjectOutput out) throws IOException{
        writeSchema(out);
        indexManager.writeExternal(out, false);
        out.writeObject(partUpdateOrderBy);
        out.writeObject(partUpdateIsAsc);
        writeInt(out, records.size());
        for(int i = 0, imax = records.size(); i < imax; i++){
            Record record = (Record)records.get(i);
            record.writeExternalValues(out);
        }
    }
    
    protected void writeSchema(ObjectOutput out) throws IOException{
        out.writeObject(name);
        out.writeObject(schema);
        out.writeObject(recordClass);
        out.writeBoolean(isSynchronized);
    }
    
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException{
        readSchema(in);
        indexManager = new BeanTableIndexManager();
        indexManager.readExternal(in, false);
        if(schema != null){
            recordSchema = RecordSchema.getInstance(schema);
        }
        partUpdateOrderBy = (int[])in.readObject();
        partUpdateIsAsc = (boolean[])in.readObject();
        final int recSize = readInt(in);
        records = isSynchronized ? Collections.synchronizedList(new ArrayList()) : new ArrayList();
        if(recordSchema != null){
            for(int i = 0; i < recSize; i++){
                Record record = createRecord();
                record.readExternalValues(in);
                record.setIndex(i);
                record.setRecordList(this);
                records.add(record);
                indexManager.add(record);
            }
        }
    }
    
    protected void readSchema(ObjectInput in) throws IOException, ClassNotFoundException{
        name = (String)in.readObject();
        schema = (String)in.readObject();
        recordClass = (Class)in.readObject();
        isSynchronized = in.readBoolean();
    }
    
    protected class RecordIterator implements Iterator, Serializable{
        
        private static final long serialVersionUID = 200743372396432511L;
        
        protected int cursor = 0;
        protected int lastRet = -1;
        protected int expectedModCount = modCount;
        
        public boolean hasNext(){
            return cursor != size();
        }
        
        public Object next(){
            checkForComodification();
            try{
                Object next = get(cursor);
                lastRet = cursor++;
                return next;
            }catch(IndexOutOfBoundsException e){
                checkForComodification();
                throw new NoSuchElementException();
            }
        }
        
        public void remove(){
            if(lastRet == -1){
                throw new IllegalStateException();
            }
            checkForComodification();
            
            try{
                RecordList.this.remove(lastRet);
                if(lastRet < cursor){
                    cursor--;
                }
                lastRet = -1;
                expectedModCount = modCount;
            }catch(IndexOutOfBoundsException e){
                throw new ConcurrentModificationException();
            }
        }
        
        final void checkForComodification(){
            if(modCount != expectedModCount){
                throw new ConcurrentModificationException();
            }
        }
    }
    
    protected class RecordListIterator extends RecordIterator
     implements ListIterator{
        
        private static final long serialVersionUID = 1979810413080499078L;
        
        public RecordListIterator(int index){
            cursor = index;
        }
        
        public boolean hasPrevious(){
            return cursor != 0;
        }
        
        public Object previous(){
            checkForComodification();
            try{
                int i = cursor - 1;
                Object previous = get(i);
                lastRet = cursor = i;
                return previous;
            }catch(IndexOutOfBoundsException e){
                checkForComodification();
                throw new NoSuchElementException();
            }
        }
        
        public int nextIndex(){
            return cursor;
        }
        
        public int previousIndex(){
            return cursor - 1;
        }
        
        public void set(Object o){
            if(lastRet == -1){
                throw new IllegalStateException();
            }
            checkForComodification();
            
            try{
                RecordList.this.set(lastRet, o);
                expectedModCount = modCount;
            }catch(IndexOutOfBoundsException e){
                throw new ConcurrentModificationException();
            }
        }
        
        public void add(Object o){
            checkForComodification();
            
            try{
                RecordList.this.add(cursor++, o);
                lastRet = -1;
                expectedModCount = modCount;
            }catch(IndexOutOfBoundsException e){
                throw new ConcurrentModificationException();
            }
        }
    }
    
    public static class RecordComparator implements Comparator{
        
        private String[] propNames;
        private boolean[] isAsc;
        
        public RecordComparator(String[] propNames){
            this(propNames, null);
        }
        
        public RecordComparator(String[] propNames, boolean[] isAsc){
            if(propNames == null || propNames.length == 0){
                throw new IllegalArgumentException("Property name array is empty.");
            }
            if(isAsc != null && propNames.length != isAsc.length){
                throw new IllegalArgumentException("Length of property index array and sort flag array is unmatch.");
            }
            this.propNames = propNames;
            this.isAsc = isAsc;
        }
        
        public RecordComparator(RecordSchema recordSchema, String[] propNames){
            this(recordSchema, propNames, null);
        }
        
        public RecordComparator(RecordSchema recordSchema, String[] propNames, boolean[] isAsc){
            if(recordSchema == null){
                throw new IllegalArgumentException("Schema not initalize.");
            }
            if(propNames == null || propNames.length == 0){
                throw new IllegalArgumentException("Property name array is empty.");
            }
            for(int i = 0; i < propNames.length; i++){
                if(recordSchema.getPropertySchema(propNames[i]) == null){
                    throw new IllegalArgumentException("Property not found : " + propNames[i]);
                }
            }
            if(isAsc != null && propNames.length != isAsc.length){
                throw new IllegalArgumentException("Length of column name array and sort flag array is unmatch.");
            }
            this.isAsc = isAsc;
        }
        
        public RecordComparator(RecordSchema recordSchema, int[] propIndexes){
            this(recordSchema, propIndexes, null);
        }
        
        public RecordComparator(RecordSchema recordSchema, int[] propIndexes, boolean[] isAsc){
            if(recordSchema == null){
                throw new IllegalArgumentException("Schema not initalize.");
            }
            if(propIndexes == null || propIndexes.length == 0){
                throw new IllegalArgumentException("Property name array is empty.");
            }
            if(isAsc != null && propIndexes.length != isAsc.length){
                throw new IllegalArgumentException("Length of column name array and sort flag array is unmatch.");
            }
            propNames = new String[propIndexes.length];
            for(int i = 0; i < propIndexes.length; i++){
                propNames[i] = recordSchema.getPropertyName(propIndexes[i]);
                if(propNames == null){
                    throw new IllegalArgumentException("Property not found : " + propIndexes[i]);
                }
            }
            if(propNames == null || propNames.length == 0){
                throw new IllegalArgumentException("Property name array is empty.");
            }
            this.isAsc = isAsc;
        }
        
        public int compare(Object o1, Object o2){
            final Record rd1 = (Record)o1;
            final Record rd2 = (Record)o2;
            if(rd1 == null && rd2 == null){
                return 0;
            }
            if(rd1 != null && rd2 == null){
                return 1;
            }
            if(rd1 == null && rd2 != null){
                return -1;
            }
            for(int i = 0; i < propNames.length; i++){
                Object val1 = rd1.getProperty(propNames[i]);
                Object val2 = rd2.getProperty(propNames[i]);
                if(val1 != null && val2 == null){
                    return (isAsc == null || isAsc[i]) ? 1 : -1;
                }
                if(val1 == null && val2 != null){
                    return (isAsc == null || isAsc[i]) ? -1 : 1;
                }
                if(val1 != null && val2 != null){
                    int comp = 0;
                    if(val1 instanceof Comparable){
                        comp = ((Comparable)val1).compareTo(val2);
                    }else{
                        comp = val1.hashCode() - val2.hashCode();
                    }
                    if(comp != 0){
                        return (isAsc == null || isAsc[i]) ? comp : -1 * comp;
                    }
                }
            }
            return 0;
        }
    }
}
