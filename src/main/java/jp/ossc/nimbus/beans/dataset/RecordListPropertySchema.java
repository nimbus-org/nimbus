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

import java.util.List;

/**
 * �l�X�g����{@link RecordList}�̃v���p�e�B�̃X�L�[�}��`�B<p>
 * ���̃N���X�ɂ́A�v���p�e�B�̃X�L�[�}���Ƃ��āA�ȉ��̏�񂪒�`�ł���B<br>
 * <ul>
 *   <li>���O</li>
 *   <li>�l�X�g���R�[�h���X�g��</li>
 *   <li>�^</li>
 * </ul>
 * �v���p�e�B�X�L�[�}��`�̃t�H�[�}�b�g�́A<br>
 * <pre>
 *    ���O,�l�X�g���R�[�h���X�g��,�^
 * </pre>
 * �ƂȂ��Ă���A�^�ȊO�͑S�ĕK�{�ł���B<br>
 * <p>
 * ���ɁA�e���ڂ̏ڍׂ��������B<br>
 * <p>
 * ���O�́A�v���p�e�B�̖��O���Ӗ����A{@link Record ���R�[�h}����v���p�e�B�l���擾����ۂ̃L�[�ƂȂ�B<br>
 * <p>
 * �l�X�g���R�[�h���X�g���́A�l�X�g���ꂽRecordList�̖��O�ŁA{@link DataSet#setNestedRecordListSchema(String, String)}�Őݒ肵�����R�[�h���X�g�����w�肷��B<br>
 * <p>
 * �^�́A�v���p�e�B�̌^���Ӗ����AJava�̊��S�C���N���X���Ŏw�肷��B<br>
 * 
 * @author M.Takata
 */
public class RecordListPropertySchema implements PropertySchema, java.io.Serializable{
    
    private static final long serialVersionUID = -4263284765094524721L;
    
    /**
     * �X�L�[�}������B<p>
     */
    protected String schema;
    
    /**
     * �v���p�e�B�̖��O�B<p>
     */
    protected String name;
    
    /**
     * �v���p�e�B�̌^�B<p>
     */
    protected Class type = RecordList.class;
    
    /**
     * �l�X�g�������R�[�h���X�g���B<p>
     */
    protected String recordListName;
    
    /**
     * ��̃v���p�e�B�X�L�[�}�𐶐�����B<p>
     */
    public RecordListPropertySchema(){
    }
    
    /**
     * �v���p�e�B�X�L�[�}�𐶐�����B<p>
     *
     * @param schema �v���p�e�B�̃X�L�[�}��`
     * @exception PropertySchemaDefineException �v���p�e�B�̃X�L�[�}��`�Ɏ��s�����ꍇ
     */
    public RecordListPropertySchema(String schema) throws PropertySchemaDefineException{
        setSchema(schema);
    }
    
    /**
     * �v���p�e�B�̃X�L�[�}��`��ݒ肷��B<p>
     *
     * @param schema �v���p�e�B�̃X�L�[�}��`
     * @exception PropertySchemaDefineException �v���p�e�B�̃X�L�[�}��`�Ɏ��s�����ꍇ
     */
    public void setSchema(String schema) throws PropertySchemaDefineException{
        final List schemata = DefaultPropertySchema.parseCSV(schema);
        if(schemata.size() < 2){
            throw new PropertySchemaDefineException("Name and Schema must be specified.");
        }
        this.schema = schema;
        name = (String)schemata.get(0);
        recordListName = (String)schemata.get(1);
        if(schemata.size() > 2){
            parseType(schema, (String)schemata.get(2));
        }
    }
    
    /**
     * �v���p�e�B�X�L�[�}�̌^�̍��ڂ��p�[�X����B<p>
     *
     * @param schema �v���p�e�B�X�L�[�}�S��
     * @param val �X�L�[�}����
     * @exception PropertySchemaDefineException �v���p�e�B�̃X�L�[�}��`�Ɏ��s�����ꍇ
     */
    protected void parseType(String schema, String val)
     throws PropertySchemaDefineException{
        if(val != null && val.length() != 0){
            try{
                type = jp.ossc.nimbus.core.Utility.convertStringToClass(val, false);
            }catch(ClassNotFoundException e){
                throw new PropertySchemaDefineException(
                    schema,
                    "The type is illegal.",
                    e
                );
            }
        }
    }
    
    // PropertySchema��JavaDoc
    public String getSchema(){
        return schema;
    }
    
    // PropertySchema��JavaDoc
    public String getName(){
        return name;
    }
    
    // PropertySchema��JavaDoc
    public Class getType(){
        return type;
    }
    
    // PropertySchema��JavaDoc
    public boolean isPrimaryKey(){
        return false;
    }
    
    // PropertySchema��JavaDoc
    public Object set(Object val) throws PropertySetException{
        if(val == null){
            return null;
        }
        if(!(val instanceof RecordList)){
            throw new PropertySchemaCheckException(
                this,
                "The type is unmatch. type=" + val.getClass().getName()
            );
        }
        RecordList list = (RecordList)val;
        if(!recordListName.equals(list.getName())){
            throw new PropertySchemaCheckException(
                this,
                "Name of RecordList is unmatch. name=" + list.getName()
            );
        }
        return val;
    }
    
    // PropertySchema��JavaDoc
    public Object get(Object val) throws PropertyGetException{
        return val;
    }
    
    // PropertySchema��JavaDoc
    public Object format(Object val) throws PropertyGetException{
        return val;
    }
    
    // PropertySchema��JavaDoc
    public Object parse(Object val) throws PropertySetException{
        return val;
    }
    
    // PropertySchema��JavaDoc
    public boolean validate(Object val) throws PropertyValidateException{
        if(val != null && val instanceof RecordList){
            return ((RecordList)val).validate();
        }
        return true;
    }
    
    /**
     * �l�X�g�������R�[�h���X�g�����擾����B<p>
     *
     * @return �l�X�g�������R�[�h���X�g��
     */
    public String getRecordListName(){
        return recordListName;
    }
    
    /**
     * ���̃X�L�[�}�̕�����\�����擾����B<p>
     *
     * @return ������\��
     */
    public String toString(){
        final StringBuffer buf = new StringBuffer(getClass().getName());
        buf.append('{');
        buf.append("name=").append(name);
        buf.append(",recordListName=").append(recordListName);
        buf.append(",type=").append(type);
        buf.append('}');
        return buf.toString();
    }
}
