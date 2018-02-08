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
package jp.ossc.nimbus.util.converter;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import jp.ossc.nimbus.beans.dataset.*;
import jp.ossc.nimbus.service.beancontrol.interfaces.*;

/**
 * DataSer��JSON(JavaScript Object Notation)�R���o�[�^�B<p>
 * 
 * @author M.Takata
 */
public class DataSetJSONConverter extends BufferedStreamConverter implements BindingStreamConverter, StreamStringConverter{
    
    private static final String STRING_ENCLOSURE = "\"";
    
    private static final String ARRAY_SEPARATOR = ",";
    private static final String ARRAY_ENCLOSURE_START = "[";
    private static final String ARRAY_ENCLOSURE_END = "]";
    
    private static final String OBJECT_ENCLOSURE_START = "{";
    private static final String OBJECT_ENCLOSURE_END = "}";
    private static final String PROPERTY_SEPARATOR = ":";
    
    private static final String NULL_VALUE = "null";
    private static final String BOOLEAN_VALUE_TRUE = "true";
    private static final String BOOLEAN_VALUE_FALSE = "false";
    
    private static final char ESCAPE = '\\';
    
    private static final char QUOTE = '"';
    private static final char BACK_SLASH = '\\';
    private static final char SLASH = '/';
    private static final char BACK_SPACE = '\b';
    private static final char BACK_SPACE_CHAR = 'b';
    private static final char CHANGE_PAGE = '\f';
    private static final char CHANGE_PAGE_CHAR = 'f';
    private static final char LF = '\n';
    private static final char LF_CHAR = 'n';
    private static final char CR = '\r';
    private static final char CR_CHAR = 'r';
    private static final char TAB = '\t';
    private static final char TAB_CHAR = 't';
    
    private static final String ESCAPE_QUOTE = "\\\"";
    private static final String ESCAPE_BACK_SLASH = "\\\\";
    private static final String ESCAPE_SLASH = "\\/";
    private static final String ESCAPE_BACK_SPACE = "\\b";
    private static final String ESCAPE_CHANGE_PAGE = "\\f";
    private static final String ESCAPE_LF = "\\n";
    private static final String ESCAPE_CR = "\\r";
    private static final String ESCAPE_TAB = "\\t";
    
    private static final String NAME_SCHEMA = "schema";
    private static final String NAME_HEADER = "header";
    private static final String NAME_RECORD_LIST = "recordList";
    private static final String NAME_NESTED_RECORD = "nestedRecord";
    private static final String NAME_NESTED_RECORD_LIST = "nestedRecordList";
    private static final String NAME_VALUE = "value";
    private static final String NAME_INDEX = "index";
    private static final String NAME_TYPE = "type";
    
    private static final String UTF8 = "UTF-8";
    private static final String UTF16 = "UTF-16";
    private static final String UTF16BE = "UTF-16BE";
    private static final String UTF16LE = "UTF-16LE";
    private static String UTF8_BOM;
    private static String UTF16_BOM_LE;
    private static String UTF16_BOM_BE;
    
    static{
        try{
            UTF8_BOM = new String(new byte[]{(byte)0xEF, (byte)0xBB, (byte)0xBF}, "UTF-8");
        }catch(UnsupportedEncodingException e){
        }
        try{
            UTF16_BOM_LE = new String(new byte[]{(byte)0xFF, (byte)0xFE}, "UTF-16");
        }catch(UnsupportedEncodingException e){
        }
        try{
            UTF16_BOM_BE = new String(new byte[]{(byte)0xFE, (byte)0xFF}, "UTF-16");
        }catch(UnsupportedEncodingException e){
        }
    }
    
    /**
     * �f�[�^�Z�b�g��JSON��\���ϊ���ʒ萔�B<p>
     */
    public static final int DATASET_TO_JSON = OBJECT_TO_STREAM;
    
    /**
     * JSON���f�[�^�Z�b�g��\���ϊ���ʒ萔�B<p>
     */
    public static final int JSON_TO_DATASET = STREAM_TO_OBJECT;
    
    /**
     * �ϊ���ʁB<p>
     */
    protected int convertType;
    
    /**
     * �f�[�^�Z�b�g�}�b�s���O�B<p>
     */
    protected Map dataSetMap = new HashMap();
    
    /**
     * BeanFlowInvokerFactory�B<p>
     */
    protected BeanFlowInvokerFactory beanFlowInvokerFactory;
    
    /**
     * DataSet��BeanFlow�Ŏ擾����ꍇ�ɁA���N�G�X�g���ꂽDataSet���̑O�ɂ��̑O�u����t������BeanFlow�������肷��B<p>
     */
    protected String dataSetFlowNamePrefix;
    
    /**
     * �X�L�[�}�����o�͂��邩�ǂ����̃t���O�B<p>
     * �f�[�^�Z�b�g��JSON�ϊ����s���ۂɁAJSON��schema�v�f���o�͂��邩�ǂ���������킷�Btrue�̏ꍇ�A�o�͂���B�f�t�H���g�́Atrue�B<br>
     */
    protected boolean isOutputSchema = true;
    
    /**
     * �f�[�^�Z�b�g��JSON�ϊ����Ɏg�p���镶���G���R�[�f�B���O�B<p>
     */
    protected String characterEncodingToStream = "UTF-8";
    
    /**
     * JSON���f�[�^�Z�b�g�ϊ����Ɏg�p���镶���G���R�[�f�B���O�B<p>
     */
    protected String characterEncodingToObject = "UTF-8";
    
    /**
     * �X�L�[�}���ɑ��݂��Ȃ��v�f�𖳎����邩�ǂ����̃t���O�B<p>
     * �f�t�H���g�́Afalse�ŁA�ϊ��G���[�Ƃ���B<br>
     */
    protected boolean isIgnoreUnknownElement;
    
    /**
     * �w�b�_�̃v���p�e�B�����o�͂��邩�ǂ����̃t���O�B<p>
     * �f�t�H���g�́Atrue�ŁA�o�͂���B<br>
     */
    protected boolean isOutputPropertyNameOfHeader = true;
    
    /**
     * ���R�[�h���X�g�̃v���p�e�B�����o�͂��邩�ǂ����̃t���O�B<p>
     * �f�t�H���g�́Atrue�ŁA�o�͂���B<br>
     */
    protected boolean isOutputPropertyNameOfRecordList = true;
    
    /**
     * null�l�̃v���p�e�B���o�͂��邩�ǂ����̃t���O�B<p>
     * �f�t�H���g�́Atrue�ŁA�o�͂���B<br>
     */
    protected boolean isOutputNullProperty = true;
    
    /**
     * �X�L�[�}����JSON�`���ŏo�͂��邩�ǂ����̃t���O�B<p>
     * �f�t�H���g�́Afalse�ŁAJSON�`���ł͏o�͂��Ȃ��B<br>
     */
    protected boolean isOutputJSONSchema = false;
    
    /**
     * �f�[�^�Z�b�g��JSON�ϊ����ɐ��`����������Ƃ��ďo�͂���ꍇ�ɂQ�o�C�g���������j�R�[�h�G�X�P�[�v���邩�ǂ����̃t���O�B<p>
     * �f�t�H���g�́Atrue�Ń��j�R�[�h�G�X�P�[�v����B<br>
     */
    protected boolean isUnicodeEscape = true;
    
    /**
     * �o�C���h���ꂽDataSet�𕡐����邩�ǂ����̃t���O�B<p>
     * �f�t�H���g�́Atrue�ŕ�������B<br>
     */
    protected boolean isCloneBindingObject = true;
    
    protected boolean isOutputVTLTemplate = false;
    
    /**
     * �f�[�^�Z�b�g��JSON�ϊ����s���R���o�[�^�𐶐�����B<p>
     */
    public DataSetJSONConverter(){
        this(DATASET_TO_JSON);
    }
    
    /**
     * �w�肳�ꂽ�ϊ���ʂ̃R���o�[�^�𐶐�����B<p>
     *
     * @param type �ϊ����
     * @see #DATASET_TO_JSON
     * @see #JSON_TO_DATASET
     */
    public DataSetJSONConverter(int type){
        convertType = type;
    }
    
    /**
     * �ϊ���ʂ�ݒ肷��B<p>
     *
     * @param type �ϊ����
     * @see #getConvertType()
     * @see #DATASET_TO_JSON
     * @see #JSON_TO_DATASET
     */
    public void setConvertType(int type){
        convertType = type;
    }
    
    /**
     * �ϊ���ʂ��擾����B<p>
     *
     * @return �ϊ����
     * @see #setConvertType(int)
     */
    public int getConvertType(){
        return convertType;
    }
    
    /**
     * �f�[�^�Z�b�g���ƃf�[�^�Z�b�g�̃}�b�s���O��ݒ肷��B<p>
     * JSON���f�[�^�Z�b�g�ϊ����s���ۂɁAJSON��schema�v�f���Ȃ��ꍇ�ɁA�f�[�^�Z�b�g������f�[�^�Z�b�g����肷��̂Ɏg�p����B<br>
     * 
     * @param dataSet �f�[�^�Z�b�g
     */
    public void setDataSet(DataSet dataSet){
        if(dataSet.getName() == null){
            throw new IllegalArgumentException("DataSet name is null. dataSet=" + dataSet);
        }
        dataSetMap.put(dataSet.getName(), dataSet);
    }
    
    /**
     * �f�[�^�Z�b�g���ƃf�[�^�Z�b�g�̃}�b�s���O��ݒ肷��B<p>
     * JSON���f�[�^�Z�b�g�ϊ����s���ۂɁAJSON��schema�v�f���Ȃ��ꍇ�ɁA�f�[�^�Z�b�g������f�[�^�Z�b�g����肷��̂Ɏg�p����B<br>
     * 
     * @param name �f�[�^�Z�b�g��
     * @param dataSet �f�[�^�Z�b�g
     */
    public void setDataSet(String name, DataSet dataSet){
        if(dataSet.getName() == null){
            dataSet.setName(name);
        }
        dataSetMap.put(name, dataSet);
    }
    
    /**
     * DataSet��BeanFlow�Ŏ擾����ꍇ�Ɏg�p����{@link BeanFlowInvokerFactory}��ݒ肷��B<p>
     *
     * @param factory BeanFlowInvokerFactory
     */
    public void setBeanFlowInvokerFactory(BeanFlowInvokerFactory factory){
        beanFlowInvokerFactory = factory;
    }
    
    /**
     * DataSet��BeanFlow�Ŏ擾����ꍇ�ɁA�Ăяo��BeanFlow���Ƃ��āA���N�G�X�g���ꂽDataSet���̑O�ɕt������v���t�B�N�X��ݒ肷��B<p>
     * �f�t�H���g�́Anull�ŁA�v���t�B�N�X��t�����Ȃ��B<br>
     *
     * @param prefix �v���t�B�N�X
     */
    public void setDataSetFlowNamePrefix(String prefix){
        dataSetFlowNamePrefix = prefix;
    }
    
    /**
     * DataSet��BeanFlow�Ŏ擾����ꍇ�ɁA�Ăяo��BeanFlow���Ƃ��āA���N�G�X�g���ꂽDataSet���̑O�ɕt������v���t�B�N�X���擾����B<p>
     *
     * @return �v���t�B�N�X
     */
    public String getDataSetFlowNamePrefix(){
        return dataSetFlowNamePrefix;
    }
    
    /**
     * �X�L�[�}�����o�͂��邩�ǂ�����ݒ肷��B<p>
     * �f�[�^�Z�b�g��JSON�ϊ����s���ۂɁAJSON��schema�v�f���o�͂��邩�ǂ�����ݒ肷��Btrue�̏ꍇ�A�o�͂���B�f�t�H���g�́Atrue�B<br>
     *
     * @param isOutput �X�L�[�}�����o�͂���ꍇ��true
     */
    public void setOutputSchema(boolean isOutput){
        isOutputSchema = isOutput;
    }
    
    /**
     * �X�L�[�}�����o�͂��邩�ǂ����𔻒肷��B<p>
     *
     * @return true�̏ꍇ�X�L�[�}�����o�͂���
     */
    public boolean isOutputSchema(){
        return isOutputSchema;
    }
    
    /**
     * �w�b�_�̃v���p�e�B�����o�͂��邩�ǂ�����ݒ肷��B<p>
     * �f�t�H���g�́Atrue�ŁA�o�͂���B<br>
     * false�ɂ���ƁA�w�b�_��JSON�̃I�u�W�F�N�g�`���ł͂Ȃ��A�z��`���ŏo�͂����B<br>
     *
     * @param isOutput �w�b�_�̃v���p�e�B�����o�͂���ꍇ�́Atrue
     */
    public void setOutputPropertyNameOfHeader(boolean isOutput){
        isOutputPropertyNameOfHeader = isOutput;
    }
    
    /**
     * �w�b�_�̃v���p�e�B�����o�͂��邩�ǂ����𔻒肷��B<p>
     *
     * @return true�̏ꍇ�A�w�b�_�̃v���p�e�B�����o�͂���
     */
    public boolean isOutputPropertyNameOfHeader(){
        return isOutputPropertyNameOfHeader;
    }
    
    /**
     * ���R�[�h���X�g�̃v���p�e�B�����o�͂��邩�ǂ�����ݒ肷��B<p>
     * �f�t�H���g�́Atrue�ŁA�o�͂���B<br>
     * false�ɂ���ƁA���R�[�h���X�g��JSON�̃I�u�W�F�N�g�`���ł͂Ȃ��A�z��`���ŏo�͂����B<br>
     *
     * @param isOutput ���R�[�h���X�g�̃v���p�e�B�����o�͂���ꍇ�́Atrue
     */
    public void setOutputPropertyNameOfRecordList(boolean isOutput){
        isOutputPropertyNameOfRecordList = isOutput;
    }
    
    /**
     * ���R�[�h���X�g�̃v���p�e�B�����o�͂��邩�ǂ����𔻒肷��B<p>
     *
     * @return true�̏ꍇ�A���R�[�h���X�g�̃v���p�e�B�����o�͂���
     */
    public boolean isOutputPropertyNameOfRecordList(){
        return isOutputPropertyNameOfRecordList;
    }
    
    /**
     * null�l�̃v���p�e�B���o�͂��邩�ǂ�����ݒ肷��B<p>
     * �f�t�H���g�́Atrue�ŁA�o�͂���B<br>
     *
     * @param isOutput �o�͂���ꍇ�Atrue
     */
    public void setOutputNullProperty(boolean isOutput){
        isOutputNullProperty = isOutput;
    }
    
    /**
     * null�l�̃v���p�e�B���o�͂��邩�ǂ����𔻒肷��B<p>
     *
     * @return true�̏ꍇ�A�o�͂���B
     */
    public boolean isOutputNullProperty(){
        return isOutputNullProperty;
    }
    
    /**
     * �f�[�^�Z�b�g��JSON�ϊ��ŁAVTL(Velocity Template Language) ���܂ރe���v���[�g���o�͂��邩�ǂ�����ݒ肷��B<p>
     * �f�t�H���g�́Afalse�ŁA�o�͂��Ȃ��B<br>
     *
     * @param isOutput �o�͂���ꍇ�Atrue
     */
    public void setOutputVTLTemplate(boolean isOutput){
        isOutputVTLTemplate = isOutput;
    }
    
    /**
     * �f�[�^�Z�b�g��JSON�ϊ��ŁAVTL(Velocity Template Language) ���܂ރe���v���[�g���o�͂��邩�ǂ����𔻒肷��B<p>
     *
     * @return true�̏ꍇ�A�o�͂���B
     */
    public boolean isOutputVTLTemplate(){
        return isOutputVTLTemplate;
    }
    
    /**
     * �f�[�^�Z�b�g��JSON�ϊ����Ɏg�p���镶���G���R�[�f�B���O��ݒ肷��B<p>
     * 
     * @param encoding �����G���R�[�f�B���O
     */
    public void setCharacterEncodingToStream(String encoding){
        characterEncodingToStream = encoding;
    }
    
    /**
     * �f�[�^�Z�b�g��JSON�ϊ����Ɏg�p���镶���G���R�[�f�B���O���擾����B<p>
     * 
     * @return �����G���R�[�f�B���O
     */
    public String getCharacterEncodingToStream(){
        return characterEncodingToStream;
    }
    
    /**
     * JSON���f�[�^�Z�b�g�ϊ����Ɏg�p���镶���G���R�[�f�B���O��ݒ肷��B<p>
     * 
     * @param encoding �����G���R�[�f�B���O
     */
    public void setCharacterEncodingToObject(String encoding){
        characterEncodingToObject = encoding;
    }
    
    /**
     * JSON���f�[�^�Z�b�g�ϊ����Ɏg�p���镶���G���R�[�f�B���O���擾����B<p>
     * 
     * @return �����G���R�[�f�B���O
     */
    public String getCharacterEncodingToObject(){
        return characterEncodingToObject;
    }
    
    public StreamStringConverter cloneCharacterEncodingToStream(String encoding){
        if((encoding == null && characterEncodingToStream == null)
            || (encoding != null && encoding.equals(characterEncodingToStream))){
            return this;
        }
        try{
            StreamStringConverter clone = (StreamStringConverter)super.clone();
            clone.setCharacterEncodingToStream(encoding);
            return clone;
        }catch(CloneNotSupportedException e){
            return null;
        }
    }
    
    public StreamStringConverter cloneCharacterEncodingToObject(String encoding){
        if((encoding == null && characterEncodingToObject == null)
            || (encoding != null && encoding.equals(characterEncodingToObject))){
            return this;
        }
        try{
            StreamStringConverter clone = (StreamStringConverter)super.clone();
            clone.setCharacterEncodingToObject(encoding);
            return clone;
        }catch(CloneNotSupportedException e){
            return null;
        }
    }
    
    /**
     * �X�L�[�}���ɑ��݂��Ȃ��v�f�𖳎����邩�ǂ�����ݒ肷��B<p>
     * �f�t�H���g�́Afalse�ŁA�ϊ��G���[�ƂȂ�B<br>
     * 
     * @param isIgnore true�̏ꍇ�A��������
     */
    public void setIgnoreUnknownElement(boolean isIgnore){
        isIgnoreUnknownElement = isIgnore;
    }
    
    /**
     * �X�L�[�}���ɑ��݂��Ȃ��v�f�𖳎����邩�ǂ����𔻒肷��B<p>
     * 
     * @return true�̏ꍇ�A��������
     */
    public boolean isIgnoreUnknownElement(){
        return isIgnoreUnknownElement;
    }
    
    /**
     * �X�L�[�}����JSON�`���ŏo�͂��邩�ǂ�����ݒ肷��B<p>
     * �f�t�H���g�́Afalse�ŁAJSON�`���ł͏o�͂��Ȃ��B<br>
     * 
     * @param isOutput JSON�`���ŏo�͂���ꍇ�Atrue
     */
    public void setOutputJSONSchema(boolean isOutput){
        isOutputJSONSchema = isOutput;
    }
    
    /**
     * �X�L�[�}����JSON�`���ŏo�͂��邩�ǂ����𔻒肷��B<p>
     * 
     * @return true�̏ꍇ�AJSON�`���ŏo�͂���
     */
    public boolean isOutputJSONSchema(){
        return isOutputJSONSchema;
    }
    
    /**
     * �f�[�^�Z�b�g��JSON�ϊ����ɐ��`����������Ƃ��ďo�͂���ꍇ�ɂQ�o�C�g���������j�R�[�h�G�X�P�[�v���邩�ǂ����𔻒肷��B<p>
     *
     * @return �G�X�P�[�v����ꍇtrue
     */
    public boolean isUnicodeEscape(){
        return isUnicodeEscape;
    }
    
    /**
     * �f�[�^�Z�b�g��JSON�ϊ����ɐ��`����������Ƃ��ďo�͂���ꍇ�ɂQ�o�C�g���������j�R�[�h�G�X�P�[�v���邩�ǂ�����ݒ肷��B<p>
     * �f�t�H���g�́Atrue�Ń��j�R�[�h�G�X�P�[�v����B<br>
     *
     * @param isEscape �G�X�P�[�v����ꍇtrue
     */
    public void setUnicodeEscape(boolean isEscape){
        isUnicodeEscape = isEscape;
    }
    
    /**
     * �o�C���h���ꂽDataSet�𕡐����邩�ǂ�����ݒ肷��B<p>
     * �f�t�H���g�́Atrue�ŕ�������B<br>
     * 
     * @param isClone ��������ꍇtrue
     */
    public void setCloneBindingObject(boolean isClone){
        isCloneBindingObject = isClone;
    }
    
    /**
     * �o�C���h���ꂽDataSet�𕡐����邩�ǂ����𔻒肷��B<p>
     * 
     * @return true�̏ꍇ�A��������
     */
    public boolean isCloneBindingObject(){
        return isCloneBindingObject;
    }
    
    /**
     * �w�肳�ꂽ�I�u�W�F�N�g��ϊ�����B<p>
     *
     * @param obj �ϊ��Ώۂ̃I�u�W�F�N�g
     * @return �ϊ���̃I�u�W�F�N�g
     * @exception ConvertException �ϊ��Ɏ��s�����ꍇ
     */
    public Object convert(Object obj) throws ConvertException{
        if(obj == null){
            return null;
        }
        switch(convertType){
        case DATASET_TO_JSON:
            return convertToStream(obj);
        case JSON_TO_DATASET:
            if(obj instanceof File){
                return toDataSet((File)obj);
            }else if(obj instanceof InputStream){
                return toDataSet((InputStream)obj);
            }else{
                throw new ConvertException(
                    "Invalid input type : " + obj.getClass()
                );
            }
        default:
            throw new ConvertException(
                "Invalid convert type : " + convertType
            );
        }
    }
    
    /**
     * {@link DataSet}����JSON�o�C�g�z��ɕϊ�����B<p>
     *
     * @param obj DataSet
     * @return JSON�o�C�g�z��
     * @exception ConvertException �ϊ��Ɏ��s�����ꍇ
     */
    protected byte[] convertToByteArray(Object obj) throws ConvertException{
        if(obj instanceof DataSet){
            return toJSON((DataSet)obj);
        }else{
            throw new ConvertException(
                "Invalid input type : " + obj.getClass()
            );
        }
    }
    
    /**
     * JSON�X�g���[������{@link DataSet}�ɕϊ�����B<p>
     *
     * @param is JSON�X�g���[��
     * @return DataSet
     * @exception ConvertException �ϊ��Ɏ��s�����ꍇ
     */
    public Object convertToObject(InputStream is) throws ConvertException{
        return toDataSet(is);
    }
    
    /**
     * �w�肳�ꂽ�I�u�W�F�N�g�֕ϊ�����B<p>
     *
     * @param is ���̓X�g���[��
     * @param returnType �ϊ��Ώۂ̃I�u�W�F�N�g
     * @return �ϊ����ꂽ�I�u�W�F�N�g
     * @throws ConvertException �ϊ��Ɏ��s�����ꍇ
     */
    public Object convertToObject(InputStream is, Object returnType)
     throws ConvertException{
        if(returnType != null && !(returnType instanceof DataSet)){
            throw new ConvertException("ReturnType is not DataSet." + returnType);
        }
        return toDataSet(is, (DataSet)returnType);
    }
    
    protected void fillEmptyRecord(DataSet dataSet, Record record){
        RecordSchema schema = record.getRecordSchema();
        PropertySchema[] propSchemata = schema.getPropertySchemata();
        for(int i = 0; i < propSchemata.length; i++){
            if(record.getProperty(i) == null){
                if(propSchemata[i] instanceof RecordPropertySchema){
                    Record nestedRec = dataSet.createNestedRecord(((RecordPropertySchema)propSchemata[i]).getRecordName());
                    record.setProperty(i, nestedRec);
                    fillEmptyRecord(dataSet, nestedRec);
                }else if(propSchemata[i] instanceof RecordListPropertySchema){
                    RecordList nestedRecList = dataSet.createNestedRecordList(((RecordListPropertySchema)propSchemata[i]).getRecordListName());
                    record.setProperty(i, nestedRecList);
                    nestedRecList.add(nestedRecList.createRecord());
                    fillEmptyRecordList(dataSet, nestedRecList);
                }
            }
        }
    }
    protected void fillEmptyRecordList(DataSet dataSet, RecordList list){
        RecordSchema schema = list.getRecordSchema();
        if(list.size() == 0){
            list.add(list.createRecord());
        }
        Record record = list.getRecord(0);
        fillEmptyRecord(dataSet, record);
    }
    
    protected byte[] toJSON(DataSet dataSet) throws ConvertException{
        if(isOutputVTLTemplate){
            dataSet = dataSet.cloneSchema();
            final String[] headerNames = dataSet.getHeaderNames();
            if(headerNames != null && headerNames.length > 0){
                for(int i = 0, imax = headerNames.length; i < imax; i++){
                    fillEmptyRecord(dataSet, dataSet.getHeader(headerNames[i]));
                }
            }
            String[] recListNames = dataSet.getRecordListNames();
            if(recListNames != null && recListNames.length > 0){
                for(int i = 0, imax = recListNames.length; i < imax; i++){
                    fillEmptyRecordList(dataSet, dataSet.getRecordList(recListNames[i]));
                }
            }
        }
        byte[] result = null;
        try{
            StringBuffer buf = new StringBuffer();
            String dsName = dataSet.getName();
            if(dsName == null){
                dsName = "";
            }
            buf.append(OBJECT_ENCLOSURE_START);
            appendName(buf, dsName);
            buf.append(PROPERTY_SEPARATOR);
            buf.append(OBJECT_ENCLOSURE_START);
            
            boolean isOutput = false;
            // �X�L�[�}�o��
            if(isOutputSchema){
                appendName(buf, NAME_SCHEMA);
                buf.append(PROPERTY_SEPARATOR);
                buf.append(OBJECT_ENCLOSURE_START);
                
                // �w�b�_�̃X�L�[�}�o��
                final String[] headerNames = dataSet.getHeaderNames();
                if(headerNames != null && headerNames.length > 0){
                    appendName(buf, NAME_HEADER);
                    buf.append(PROPERTY_SEPARATOR);
                    buf.append(OBJECT_ENCLOSURE_START);
                    for(int i = 0, imax = headerNames.length; i < imax; i++){
                        final Header header = dataSet.getHeader(headerNames[i]);
                        appendName(
                            buf,
                            headerNames[i] == null ? "" : headerNames[i]
                        );
                        buf.append(PROPERTY_SEPARATOR);
                        if(isOutputJSONSchema){
                            RecordSchema schema = header.getRecordSchema();
                            if(schema == null){
                                appendValue(buf, null, null);
                            }else{
                                appendSchema(buf, schema);
                            }
                        }else{
                            appendValue(buf, null, header.getSchema());
                        }
                        if(i != imax - 1){
                            buf.append(ARRAY_SEPARATOR);
                        }
                    }
                    buf.append(OBJECT_ENCLOSURE_END);
                    isOutput = true;
                }
                
                // ���R�[�h���X�g�̃X�L�[�}�o��
                String[] recListNames = dataSet.getRecordListNames();
                if(recListNames != null && recListNames.length > 0){
                    if(isOutput){
                        buf.append(ARRAY_SEPARATOR);
                    }
                    appendName(buf, NAME_RECORD_LIST);
                    buf.append(PROPERTY_SEPARATOR);
                    buf.append(OBJECT_ENCLOSURE_START);
                    for(int i = 0, imax = recListNames.length; i < imax; i++){
                        final RecordList recList
                             = dataSet.getRecordList(recListNames[i]);
                        appendName(
                            buf,
                            recListNames[i] == null ? "" : recListNames[i]
                        );
                        buf.append(PROPERTY_SEPARATOR);
                        if(isOutputJSONSchema){
                            RecordSchema schema = recList.getRecordSchema();
                            if(schema == null){
                                appendValue(buf, null, null);
                            }else{
                                appendSchema(buf, schema);
                            }
                        }else{
                            appendValue(buf, null, recList.getSchema());
                        }
                        if(i != imax - 1){
                            buf.append(ARRAY_SEPARATOR);
                        }
                    }
                    buf.append(OBJECT_ENCLOSURE_END);
                    isOutput = true;
                }
                
                // �l�X�g���R�[�h�̃X�L�[�}�o��
                String[] recNames = dataSet.getNestedRecordSchemaNames();
                if(recNames != null && recNames.length > 0){
                    if(isOutput){
                        buf.append(ARRAY_SEPARATOR);
                    }
                    appendName(buf, NAME_NESTED_RECORD);
                    buf.append(PROPERTY_SEPARATOR);
                    buf.append(OBJECT_ENCLOSURE_START);
                    for(int i = 0, imax = recNames.length; i < imax; i++){
                        final RecordSchema recSchema
                             = dataSet.getNestedRecordSchema(recNames[i]);
                        appendName(buf, recNames[i]);
                        buf.append(PROPERTY_SEPARATOR);
                        if(isOutputJSONSchema){
                            if(recSchema == null){
                                appendValue(buf, null, null);
                            }else{
                                appendSchema(buf, recSchema);
                            }
                        }else{
                            appendValue(buf, null, recSchema.getSchema());
                        }
                        if(i != imax - 1){
                            buf.append(ARRAY_SEPARATOR);
                        }
                    }
                    buf.append(OBJECT_ENCLOSURE_END);
                    isOutput = true;
                }
                
                // �l�X�g���R�[�h���X�g�̃X�L�[�}�o��
                recListNames = dataSet.getNestedRecordListSchemaNames();
                if(recListNames != null && recListNames.length > 0){
                    if(isOutput){
                        buf.append(ARRAY_SEPARATOR);
                    }
                    appendName(buf, NAME_NESTED_RECORD_LIST);
                    buf.append(PROPERTY_SEPARATOR);
                    buf.append(OBJECT_ENCLOSURE_START);
                    for(int i = 0, imax = recListNames.length; i < imax; i++){
                        final RecordSchema recSchema
                             = dataSet.getNestedRecordListSchema(recListNames[i]);
                        appendName(buf, recListNames[i]);
                        buf.append(PROPERTY_SEPARATOR);
                        if(isOutputJSONSchema){
                            if(recSchema == null){
                                appendValue(buf, null, null);
                            }else{
                                appendSchema(buf, recSchema);
                            }
                        }else{
                            appendValue(buf, null, recSchema.getSchema());
                        }
                        if(i != imax - 1){
                            buf.append(ARRAY_SEPARATOR);
                        }
                    }
                    buf.append(OBJECT_ENCLOSURE_END);
                    isOutput = true;
                }
                
                buf.append(OBJECT_ENCLOSURE_END);
            }
            
            // �w�b�_�o��
            final String[] headerNames = dataSet.getHeaderNames();
            if(headerNames != null && headerNames.length > 0){
                if(isOutput){
                    buf.append(ARRAY_SEPARATOR);
                }
                appendName(buf, NAME_HEADER);
                buf.append(PROPERTY_SEPARATOR);
                buf.append(OBJECT_ENCLOSURE_START);
                for(int i = 0, imax = headerNames.length; i < imax; i++){
                    final Header header = dataSet.getHeader(headerNames[i]);
                    appendName(
                        buf,
                        headerNames[i] == null ? "" : headerNames[i]
                    );
                    buf.append(PROPERTY_SEPARATOR);
                    appendValue(buf, null, header);
                    if(i != imax - 1){
                        buf.append(ARRAY_SEPARATOR);
                    }
                }
                buf.append(OBJECT_ENCLOSURE_END);
                isOutput = true;
            }
            
            // ���R�[�h���X�g�o��
            String[] recListNames = dataSet.getRecordListNames();
            if(recListNames != null && recListNames.length > 0){
                if(isOutput){
                    buf.append(ARRAY_SEPARATOR);
                }
                appendName(buf, NAME_RECORD_LIST);
                buf.append(PROPERTY_SEPARATOR);
                buf.append(OBJECT_ENCLOSURE_START);
                for(int i = 0, imax = recListNames.length; i < imax; i++){
                    final RecordList recList = dataSet.getRecordList(recListNames[i]);
                    appendName(
                        buf,
                        recListNames[i] == null ? "" : recListNames[i]
                    );
                    buf.append(PROPERTY_SEPARATOR);
                    if(isOutputVTLTemplate){
                        buf.append(ARRAY_ENCLOSURE_START);
                        buf.append("#foreach( $record in $").append(recListNames[i]).append(" )");
                        buf.append("#if( $velocityCount != 1 )").append(ARRAY_SEPARATOR).append("#end");
                        appendValue(buf, null, recList.get(0));
                        buf.append("#end");
                        buf.append(ARRAY_ENCLOSURE_END);
                    }else{
                        appendArray(buf, recList);
                    }
                    if(i != imax - 1){
                        buf.append(ARRAY_SEPARATOR);
                    }
                }
                buf.append(OBJECT_ENCLOSURE_END);
                isOutput = true;
            }
            
            buf.append(OBJECT_ENCLOSURE_END);
            buf.append(OBJECT_ENCLOSURE_END);
            
            String str = buf.toString();
            result = characterEncodingToStream == null ? str.getBytes() : str.getBytes(characterEncodingToStream);
        }catch(IOException e){
            throw new ConvertException(e);
        }catch(DataSetException e){
            throw new ConvertException(e);
        }
        return result;
    }
    
    private StringBuffer appendSchema(StringBuffer buf, RecordSchema schema){
        final PropertySchema[] props = schema.getPropertySchemata();
        buf.append(OBJECT_ENCLOSURE_START);
        for(int j = 0; j < props.length; j++){
            appendName(buf, props[j].getName());
            buf.append(PROPERTY_SEPARATOR);
            buf.append(OBJECT_ENCLOSURE_START);
            appendName(buf, NAME_INDEX);
            buf.append(PROPERTY_SEPARATOR);
            buf.append(j);
            buf.append(ARRAY_SEPARATOR);
            
            appendName(buf, NAME_TYPE);
            buf.append(PROPERTY_SEPARATOR);
            String nestedSchemaName = null;
            if(props[j] instanceof RecordListPropertySchema){
                appendValue(buf, null, NAME_NESTED_RECORD_LIST);
                nestedSchemaName = ((RecordListPropertySchema)props[j]).getRecordListName();
            }else if(props[j] instanceof RecordPropertySchema){
                appendValue(buf, null, NAME_NESTED_RECORD);
                nestedSchemaName = ((RecordPropertySchema)props[j]).getRecordName();
            }else{
                appendValue(buf, null, NAME_VALUE);
            }
            
            if(nestedSchemaName != null){
                buf.append(ARRAY_SEPARATOR);
                appendName(buf, NAME_SCHEMA);
                buf.append(PROPERTY_SEPARATOR);
                appendValue(buf, null, nestedSchemaName);
            }
            buf.append(OBJECT_ENCLOSURE_END);
            if(j != props.length - 1){
                buf.append(ARRAY_SEPARATOR);
            }
        }
        buf.append(OBJECT_ENCLOSURE_END);
        return buf;
    }
    
    private StringBuffer appendName(StringBuffer buf, String name){
        buf.append(STRING_ENCLOSURE);
        buf.append(escape(name));
        buf.append(STRING_ENCLOSURE);
        return buf;
    }
    
    private StringBuffer appendValue(StringBuffer buf, Class type, Object value){
        if(type == null && value != null){
            type = value.getClass();
        }
        if(value == null){
            if(type == null){
                buf.append(NULL_VALUE);
            }else if(Number.class.isAssignableFrom(type)
                || (type.isPrimitive()
                    && (Byte.TYPE.equals(type)
                        || Short.TYPE.equals(type)
                        || Integer.TYPE.equals(type)
                        || Long.TYPE.equals(type)
                        || Float.TYPE.equals(type)
                        || Double.TYPE.equals(type)))
            ){
                buf.append('0');
            }else if(Boolean.class.equals(type)
                || Boolean.TYPE.equals(type)
            ){
                buf.append(BOOLEAN_VALUE_FALSE);
            }else{
                buf.append(NULL_VALUE);
            }
        }else if(Boolean.class.equals(type)
            || Boolean.TYPE.equals(type)
        ){
            if(value instanceof Boolean){
                if(((Boolean)value).booleanValue()){
                    buf.append(BOOLEAN_VALUE_TRUE);
                }else{
                    buf.append(BOOLEAN_VALUE_FALSE);
                }
            }else{
                buf.append(escape(value.toString()));
            }
        }else if(Number.class.isAssignableFrom(type)
            || (type.isPrimitive()
                && (Byte.TYPE.equals(type)
                    || Short.TYPE.equals(type)
                    || Integer.TYPE.equals(type)
                    || Long.TYPE.equals(type)
                    || Float.TYPE.equals(type)
                    || Double.TYPE.equals(type)))
        ){
            if((value instanceof Float && (((Float)value).isNaN() || ((Float)value).isInfinite()))
                || (value instanceof Double && (((Double)value).isNaN() || ((Double)value).isInfinite()))
                || ((value instanceof String) && ("-Infinity".equals(value) || "Infinity".equals(value) || "NaN".equals(value)))
            ){
                buf.append(STRING_ENCLOSURE);
                buf.append(escape(value.toString()));
                buf.append(STRING_ENCLOSURE);
            }else{
                buf.append(value);
            }
        }else if(type.isArray() || Collection.class.isAssignableFrom(type)){
            appendArray(buf, value);
        }else if(Record.class.isAssignableFrom(type)){
            Record rec = (Record)value;
            RecordSchema schema = rec.getRecordSchema();
            PropertySchema[] propSchemata = schema.getPropertySchemata();
            boolean isOutputPropertyName = true;
            if((rec instanceof Header && !isOutputPropertyNameOfHeader)
                || (!(rec instanceof Header)
                    && !isOutputPropertyNameOfRecordList)
            ){
                isOutputPropertyName = false;
            }
            if(isOutputPropertyName){
                buf.append(OBJECT_ENCLOSURE_START);
            }else{
                buf.append(ARRAY_ENCLOSURE_START);
            }
            boolean isOutput = false;
            RecordList parentList = rec.getRecordList();
            String headerName = isOutputVTLTemplate && (rec instanceof Header) ? ((Header)rec).getName() : null;
            if(isOutputVTLTemplate && !isOutputNullProperty){
                buf.append("#set( $isOutput = false )");
            }
            for(int i = 0, imax = propSchemata.length; i < imax; i++){
                Object prop = rec.getProperty(i);
                PropertySchema propSchema = propSchemata[i];
                if(isOutputVTLTemplate){
                    if(isOutputNullProperty){
                        if(isOutput){
                            buf.append(ARRAY_SEPARATOR);
                        }
                        if(isOutputPropertyName){
                            appendName(buf, propSchema.getName());
                            buf.append(PROPERTY_SEPARATOR);
                        }
                        if(propSchema instanceof RecordPropertySchema){
                            appendValue(buf, propSchema.getType(), prop);
                        }else if(propSchema instanceof RecordListPropertySchema){
                            buf.append(ARRAY_ENCLOSURE_START);
                            buf.append("#foreach( $record in $").append(propSchema.getName()).append(" )");
                            buf.append("#if( $velocityCount != 1 )").append(ARRAY_SEPARATOR).append("#end");
                            appendValue(buf, null, ((RecordList)prop).get(0));
                            buf.append("#end");
                            buf.append(ARRAY_ENCLOSURE_END);
                        }else{
                            buf.append('$');
                            if(parentList != null){
                                buf.append("record.");
                            }else if(headerName != null){
                                buf.append(headerName).append("[0].");
                            }
                            buf.append(propSchema.getName());
                        }
                        isOutput = true;
                    }else{
                        buf.append("#if( ");
                        buf.append('$');
                        if(parentList != null){
                            buf.append("record.");
                        }else if(headerName != null){
                            buf.append(headerName).append("[0].");
                        }
                        buf.append(propSchema.getName()).append(" )");
                        buf.append("#if( $isOutput )").append(ARRAY_SEPARATOR).append("#end");
                        
                        if(isOutputPropertyName){
                            appendName(buf, propSchema.getName());
                            buf.append(PROPERTY_SEPARATOR);
                        }
                        if(propSchema instanceof RecordPropertySchema){
                            appendValue(buf, propSchema.getType(), prop);
                        }else if(propSchema instanceof RecordListPropertySchema){
                            buf.append(ARRAY_ENCLOSURE_START);
                            buf.append("#foreach( $record in $").append(propSchema.getName()).append(" )");
                            buf.append("#if( $velocityCount != 1 )").append(ARRAY_SEPARATOR).append("#end");
                            appendValue(buf, null, ((RecordList)prop).get(0));
                            buf.append("#end");
                            buf.append(ARRAY_ENCLOSURE_END);
                        }else{
                            buf.append('$');
                            if(parentList != null){
                                buf.append("record.");
                            }else if(headerName != null){
                                buf.append(headerName).append("[0].");
                            }
                            buf.append(propSchema.getName());
                        }
                        buf.append("#set( $isOutput = true )");
                        buf.append("#end");
                    }
                }else{
                    Object formatProp = null;
                    if(isOutputPropertyName){
                        if(!isOutputNullProperty){
                            if(prop == null){
                                continue;
                            }else{
                                formatProp = rec.getFormatProperty(i);
                                if(formatProp == null){
                                    continue;
                                }
                            }
                        }
                        if(isOutput){
                            buf.append(ARRAY_SEPARATOR);
                        }
                        appendName(buf, propSchema.getName());
                        buf.append(PROPERTY_SEPARATOR);
                    }else if(isOutput){
                        buf.append(ARRAY_SEPARATOR);
                    }
                    if(prop == null){
                        appendValue(buf, propSchema.getType(), null);
                    }else{
                        Class propType = propSchema.getType();
                        if(propType == null){
                            propType = prop.getClass();
                        }
                        if(propType.isArray()
                            || Collection.class.isAssignableFrom(propType)){
                            appendArray(buf, prop);
                        }else{
                            if(formatProp == null){
                                formatProp = rec.getFormatProperty(i);
                            }
                            if(Number.class.isAssignableFrom(propType)
                                || (propType.isPrimitive()
                                    && (Byte.TYPE.equals(propType)
                                        || Short.TYPE.equals(propType)
                                        || Integer.TYPE.equals(propType)
                                        || Long.TYPE.equals(propType)
                                        || Float.TYPE.equals(propType)
                                        || Double.TYPE.equals(propType)
                                        || Boolean.TYPE.equals(propType)))
                                || Boolean.class.equals(propType)
                            ){
                                appendValue(
                                    buf,
                                    propType,
                                    formatProp
                                );
                            }else{
                                appendValue(buf, null, formatProp);
                            }
                        }
                    }
                }
                isOutput = true;
            }
            if(isOutputPropertyName){
                buf.append(OBJECT_ENCLOSURE_END);
            }else{
                buf.append(ARRAY_ENCLOSURE_END);
            }
        }else{
            buf.append(STRING_ENCLOSURE);
            buf.append(escape(value.toString()));
            buf.append(STRING_ENCLOSURE);
        }
        return buf;
    }
    
    private StringBuffer appendArray(StringBuffer buf, Object array){
        buf.append(ARRAY_ENCLOSURE_START);
        if(array.getClass().isArray()){
            for(int i = 0, imax = Array.getLength(array); i < imax; i++){
                appendValue(buf, null, Array.get(array, i));
                if(i != imax - 1){
                    buf.append(ARRAY_SEPARATOR);
                }
            }
        }else if(List.class.isAssignableFrom(array.getClass())){
            List list = (List)array;
            for(int i = 0, imax = list.size(); i < imax; i++){
                appendValue(buf, null, list.get(i));
                if(i != imax - 1){
                    buf.append(ARRAY_SEPARATOR);
                }
            }
        }else if(Collection.class.isAssignableFrom(array.getClass())){
            Iterator itr = ((Collection)array).iterator();
            while(itr.hasNext()){
                appendValue(buf, null, itr.next());
                if(itr.hasNext()){
                    buf.append(ARRAY_SEPARATOR);
                }
            }
        }
        buf.append(ARRAY_ENCLOSURE_END);
        return buf;
    }
    
    private String escape(String str){
        if(str == null || str.length() == 0){
            return str;
        }
        boolean isEscape = false;
        final StringBuffer buf = new StringBuffer();
        for(int i = 0, imax = str.length(); i < imax; i++){
            final char c = str.charAt(i);
            
            switch(c){
            case QUOTE:
                buf.append(ESCAPE_QUOTE);
                isEscape = true;
                break;
            case BACK_SLASH:
                buf.append(ESCAPE_BACK_SLASH);
                isEscape = true;
                break;
            case SLASH:
                buf.append(ESCAPE_SLASH);
                isEscape = true;
                break;
            case BACK_SPACE:
                buf.append(ESCAPE_BACK_SPACE);
                isEscape = true;
                break;
            case CHANGE_PAGE:
                buf.append(ESCAPE_CHANGE_PAGE);
                isEscape = true;
                break;
            case LF:
                buf.append(ESCAPE_LF);
                isEscape = true;
                break;
            case CR:
                buf.append(ESCAPE_CR);
                isEscape = true;
                break;
            case TAB:
                buf.append(ESCAPE_TAB);
                isEscape = true;
                break;
            default:
                if(isUnicodeEscape
                    && !(c == 0x20
                     || c == 0x21
                     || (0x23 <= c && c <= 0x5B)
                     || (0x5D <= c && c <= 0x7E))
                ){
                    isEscape = true;
                    toUnicode(c, buf);
                }else{
                    buf.append(c);
                }
            }
        }
        return isEscape ? buf.toString() : str;
    }
    
    private StringBuffer toUnicode(char c, StringBuffer buf){
        buf.append(ESCAPE);
        buf.append('u');
        int mask = 0xf000;
        for(int i = 0; i < 4; i++){
            mask = 0xf000 >> (i * 4);
            int val = c & mask;
            val = val << (i * 4);
            switch(val){
            case 0x0000:
                buf.append('0');
                break;
            case 0x1000:
                buf.append('1');
                break;
            case 0x2000:
                buf.append('2');
                break;
            case 0x3000:
                buf.append('3');
                break;
            case 0x4000:
                buf.append('4');
                break;
            case 0x5000:
                buf.append('5');
                break;
            case 0x6000:
                buf.append('6');
                break;
            case 0x7000:
                buf.append('7');
                break;
            case 0x8000:
                buf.append('8');
                break;
            case 0x9000:
                buf.append('9');
                break;
            case 0xa000:
                buf.append('a');
                break;
            case 0xb000:
                buf.append('b');
                break;
            case 0xc000:
                buf.append('c');
                break;
            case 0xd000:
                buf.append('d');
                break;
            case 0xe000:
                buf.append('e');
                break;
            case 0xf000:
                buf.append('f');
                break;
            default:
            }
        }
        return buf;
    }
    
    protected DataSet toDataSet(File file) throws ConvertException{
        try{
            return toDataSet(new FileInputStream(file));
        }catch(IOException e){
            throw new ConvertException(e);
        }
    }
    
    protected DataSet toDataSet(InputStream is) throws ConvertException{
        return toDataSet(is, null);
    }
    
    protected DataSet toDataSet(InputStream is, DataSet dataSet)
     throws ConvertException{
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataSet ds = dataSet;
        try{
            int length = 0;
            byte[] buf = new byte[1024];
            while((length = is.read(buf)) != -1){
                baos.write(buf, 0, length);
            }
            String dataStr = new String(
                baos.toByteArray(),
                characterEncodingToObject
            );
            dataStr = removeBOM(dataStr);
            dataStr = fromUnicode(dataStr);
            Map jsonObj = new HashMap();
            readJSONObject(
                new StringReader(dataStr),
                new StringBuffer(),
                jsonObj,
                false
            );
            if(jsonObj.size() == 0){
                return ds;
            }
            Iterator entries = jsonObj.entrySet().iterator();
            Map.Entry entry = (Map.Entry)entries.next();
            final String dsName = (String)entry.getKey();
            jsonObj = (Map)entry.getValue();
            if(ds == null){
                String dsFlowName = dsName;
                if(dataSetFlowNamePrefix != null){
                    dsFlowName = dataSetFlowNamePrefix + dsFlowName;
                }
                if(dataSetMap.containsKey(dsName)){
                    ds = ((DataSet)dataSetMap.get(dsName)).cloneSchema();
                }else if(beanFlowInvokerFactory != null
                            && beanFlowInvokerFactory.containsFlow(dsFlowName)
                ){
                    final BeanFlowInvoker beanFlowInvoker
                        = beanFlowInvokerFactory.createFlow(dsFlowName);
                    Object ret = null;
                    try{
                        ret = beanFlowInvoker.invokeFlow(null);
                    }catch(Exception e){
                        throw new ConvertException("Exception occured in BeanFlow '" + dsFlowName + "'", e);
                    }
                    if(!(ret instanceof DataSet)){
                        throw new ConvertException("Result of BeanFlow '" + dsFlowName + "' is not DataSet.");
                    }
                    ds = (DataSet)ret;
                }else{
                    ds = new DataSet(dsName);
                    
                    // �X�L�[�}��ǂݍ���
                    Object schemaObj = jsonObj.get(NAME_SCHEMA);
                    if(schemaObj == null
                        || !(schemaObj instanceof Map)
                    ){
                        throw new ConvertException(
                            "Dataset is not found. name=" + dsName
                        );
                    }
                    Map schemaMap = (Map)schemaObj;
                    final Object headerObj = schemaMap.get(NAME_HEADER);
                    if(headerObj != null){
                        if(!(headerObj instanceof Map)){
                            throw new ConvertException(
                                "Header schema is not jsonObject." + headerObj
                            );
                        }
                        Map headerMap = (Map)headerObj;
                        entries = headerMap.entrySet().iterator();
                        while(entries.hasNext()){
                            entry = (Map.Entry)entries.next();
                            String name = (String)entry.getKey();
                            if(name.length() == 0){
                                name = null;
                            }
                            final Object schemaStrObj = entry.getValue();
                            if(!(schemaStrObj instanceof String)){
                                throw new ConvertException(
                                    "Header schema '" + name + "' is not string." + schemaStrObj
                                );
                            }
                            ds.setHeaderSchema(name, (String)schemaStrObj);
                        }
                    }
                    final Object recListObj = schemaMap.get(NAME_RECORD_LIST);
                    if(recListObj != null){
                        if(!(recListObj instanceof Map)){
                            throw new ConvertException(
                                "RecordList schema is not jsonObject." + recListObj
                            );
                        }
                        Map recListMap = (Map)recListObj;
                        entries = recListMap.entrySet().iterator();
                        while(entries.hasNext()){
                            entry = (Map.Entry)entries.next();
                            String name = (String)entry.getKey();
                            if(name.length() == 0){
                                name = null;
                            }
                            final Object schemaStrObj = entry.getValue();
                            if(!(schemaStrObj instanceof String)){
                                throw new ConvertException(
                                    "RecordList schema '" + name + "' is not string." + schemaStrObj
                                );
                            }
                            ds.setRecordListSchema(name, (String)schemaStrObj);
                        }
                    }
                    final Object nestedRecObj = schemaMap.get(NAME_NESTED_RECORD);
                    if(nestedRecObj != null){
                        if(!(nestedRecObj instanceof Map)){
                            throw new ConvertException(
                                "NestedRecord schema is not jsonObject." + nestedRecObj
                            );
                        }
                        Map nestedRecMap = (Map)nestedRecObj;
                        entries = nestedRecMap.entrySet().iterator();
                        while(entries.hasNext()){
                            entry = (Map.Entry)entries.next();
                            final String name = (String)entry.getKey();
                            final Object schemaStrObj = entry.getValue();
                            if(!(schemaStrObj instanceof String)){
                                throw new ConvertException(
                                    "NestedRecord schema '" + name + "' is not string." + schemaStrObj
                                );
                            }
                            ds.setNestedRecordSchema(name, (String)schemaStrObj);
                        }
                    }
                    final Object nestedRecListObj = schemaMap.get(NAME_NESTED_RECORD_LIST);
                    if(nestedRecListObj != null){
                        if(!(nestedRecListObj instanceof Map)){
                            throw new ConvertException(
                                "NestedRecordList schema is not jsonObject." + nestedRecListObj
                            );
                        }
                        Map nestedRecListMap = (Map)nestedRecListObj;
                        entries = nestedRecListMap.entrySet().iterator();
                        while(entries.hasNext()){
                            entry = (Map.Entry)entries.next();
                            final String name = (String)entry.getKey();
                            final Object schemaStrObj = entry.getValue();
                            if(!(schemaStrObj instanceof String)){
                                throw new ConvertException(
                                    "NestedRecordList schema '" + name + "' is not string." + schemaStrObj
                                );
                            }
                            ds.setNestedRecordListSchema(name, (String)schemaStrObj);
                        }
                    }
                }
            }else{
                ds = isCloneBindingObject ? ds.cloneSchema() : ds;
            }
            
            // �w�b�_��ǂݍ���
            final Object headerObj = jsonObj.get(NAME_HEADER);
            if(headerObj != null){
                if(!(headerObj instanceof Map)){
                    throw new ConvertException(
                        "Header is not jsonObject." + headerObj
                    );
                }
                final Map headerMap = (Map)headerObj;
                entries = headerMap.entrySet().iterator();
                while(entries.hasNext()){
                    entry = (Map.Entry)entries.next();
                    readHeader(
                        ds,
                        (String)entry.getKey(),
                        entry.getValue()
                    );
                }
            }
            
            // ���R�[�h���X�g��ǂݍ���
            final Object recListObj = jsonObj.get(NAME_RECORD_LIST);
            if(recListObj != null){
                if(!(recListObj instanceof Map)){
                    throw new ConvertException(
                        "RecordList is not jsonObject." + recListObj
                    );
                }
                final Map recListMap = (Map)recListObj;
                entries = recListMap.entrySet().iterator();
                while(entries.hasNext()){
                    entry = (Map.Entry)entries.next();
                    readRecordList(
                        ds,
                        (String)entry.getKey(),
                        entry.getValue()
                    );
                }
            }
        }catch(IOException e){
            throw new ConvertException(e);
        }catch(DataSetException e){
            throw new ConvertException(e);
        }
        return ds;
    }
    
    private DataSet readHeader(
        DataSet dataSet,
        String headerName,
        Object headerValue
    ) throws ConvertException{
        Header header = dataSet.getHeader(headerName);
        if(header == null && headerName != null && headerName.length() == 0){
            header = dataSet.getHeader();
        }
        if(header == null){
            if(isIgnoreUnknownElement){
                return dataSet;
            }else{
                throw new ConvertException("Unknown header : " + headerName);
            }
        }
        return readRecord(dataSet, header, headerValue);
    }
    
    private DataSet readRecord(
        DataSet dataSet,
        Record record,
        Object recordValue
    ) throws ConvertException{
        final RecordSchema schema = record.getRecordSchema();
        if(recordValue instanceof Map){
            final Map propertyMap = (Map)recordValue;
            final Iterator entries = propertyMap.entrySet().iterator();
            while(entries.hasNext()){
                final Map.Entry entry = (Map.Entry)entries.next();
                final String propName = (String)entry.getKey();
                PropertySchema propSchema = schema.getPropertySchema(propName);
                if(propSchema == null && isIgnoreUnknownElement){
                    continue;
                }
                Object propValue = entry.getValue();
                if(propSchema instanceof RecordPropertySchema){
                    if(propValue != null){
                        RecordPropertySchema recPropSchema
                             = (RecordPropertySchema)propSchema;
                        Record rec = dataSet.createNestedRecord(
                            recPropSchema.getRecordName()
                        );
                        readRecord(dataSet, rec, propValue);
                        record.setProperty(propName, rec);
                    }
                }else if(propSchema instanceof RecordListPropertySchema){
                    if(propValue != null){
                        RecordListPropertySchema recListPropSchema
                             = (RecordListPropertySchema)propSchema;
                        RecordList recList = dataSet.createNestedRecordList(
                            recListPropSchema.getRecordListName()
                        );
                        readRecordList(dataSet, recList, (List)propValue);
                        record.setProperty(propName, recList);
                    }
                }else{
                    if(propValue instanceof List){
                        propValue = ((List)propValue).toArray(
                            new String[((List)propValue).size()]
                        );
                    }
                    record.setParseProperty(propName, propValue);
                }
            }
        }else if(recordValue instanceof List){
            final PropertySchema[] propSchemata = schema.getPropertySchemata();
            final List propertyList = (List)recordValue;
            if(propSchemata.length != propertyList.size()){
                if(!isIgnoreUnknownElement){
                    throw new ConvertException("Unmatch record property size. " + propertyList.size());
                }
            }
            for(int i = 0, imax = propSchemata.length; i < imax; i++){
                if(i >= propertyList.size()){
                    break;
                }
                final PropertySchema propSchema = propSchemata[i];
                Object propValue = propertyList.get(i);
                if(propValue == null){
                    continue;
                }
                if(propSchema instanceof RecordPropertySchema){
                    RecordPropertySchema recPropSchema
                         = (RecordPropertySchema)propSchema;
                    Record rec = dataSet.createNestedRecord(
                        recPropSchema.getRecordName()
                    );
                    readRecord(dataSet, rec, propValue);
                    record.setProperty(i, rec);
                }else if(propSchema instanceof RecordListPropertySchema){
                    RecordListPropertySchema recListPropSchema
                         = (RecordListPropertySchema)propSchema;
                    RecordList recList = dataSet.createNestedRecordList(
                        recListPropSchema.getRecordListName()
                    );
                    readRecordList(dataSet, recList, propValue);
                    record.setProperty(i, recList);
                }else{
                    if(propValue instanceof List){
                        propValue = ((List)propValue).toArray(
                            new String[((List)propValue).size()]
                        );
                    }
                    record.setParseProperty(i, propValue);
                }
            }
        }else{
            throw new ConvertException(
                "Record is neither jsonObject nor array." + recordValue
            );
        }
        return dataSet;
    }
    
    private DataSet readRecordList(
        DataSet dataSet,
        String recListName,
        Object recordListValue
    ) throws ConvertException{
        if(recordListValue == null){
            return dataSet;
        }
        RecordList recList = dataSet.getRecordList(recListName);
        if(recList == null && recListName != null && recListName.length() == 0){
            recList = dataSet.getRecordList();
        }
        if(recList == null){
            if(isIgnoreUnknownElement){
                return dataSet;
            }else{
                throw new ConvertException("Unknown recordList : " + recListName);
            }
        }
        return readRecordList(dataSet, recList, recordListValue);
    }
    
    private DataSet readRecordList(
        DataSet dataSet,
        RecordList recordList,
        Object recordListValue
    ) throws ConvertException{
        if(!(recordListValue instanceof List)){
            throw new ConvertException(
                "RecordList must be json array." + recordListValue
            );
        }
        final List recListValue = (List)recordListValue;
        for(int i = 0, imax = recListValue.size(); i < imax; i++){
            Record record = recordList.createRecord();
            readRecord(dataSet, record, recListValue.get(i));
            recordList.addRecord(record);
        }
        return dataSet;
    }
    
    private int readJSONObject(
        Reader reader,
        StringBuffer buf,
        Map jsonObj,
        boolean isStart
    ) throws ConvertException, IOException{
        int c = 0;
        if(!isStart){
            c = skipWhitespace(reader);
            if(c == -1){
                throw new ConvertException("It reached EOF on the way.");
            }
            if(c != '{'){
                throw new ConvertException(
                    "JSON object must be enclosed '{' and '}'"
                );
            }
        }
        do{
            c = readJSONProperty(reader, buf, jsonObj);
            if(c == -1){
                throw new ConvertException("It reached EOF on the way.");
            }
        }while(c == ',');
        return c;
    }
    
    private int readJSONArray(
        Reader reader,
        StringBuffer buf,
        List array,
        boolean isStart
    ) throws ConvertException, IOException{
        buf.setLength(0);
        int c = 0;
        if(!isStart){
            c = skipWhitespace(reader);
            if(c == -1){
                throw new ConvertException("It reached EOF on the way.");
            }
            if(c != '['){
                throw new ConvertException(
                    "JSON array must be enclosed '[' and ']'"
                );
            }
        }
        do{
            c = skipWhitespace(reader);
            Object value = null;
            switch(c){
            case '"':
                do{
                    c = reader.read();
                    if(c != -1 && c != '"'){
                        if(c == '\\'){
                            buf.append((char)c);
                            c = reader.read();
                            if(c == -1){
                                break;
                            }
                        }
                        buf.append((char)c);
                    }else{
                        break;
                    }
                }while(true);
                value = unescape(buf.toString());
                if(c == -1){
                    throw new ConvertException("It reached EOF on the way.");
                }else{
                    c = skipWhitespace(reader);
                }
                break;
            case '{':
                value = new LinkedHashMap();
                c = readJSONObject(reader, buf, (Map)value, true);
                if(c == -1){
                    throw new ConvertException("It reached EOF on the way.");
                }else{
                    c = skipWhitespace(reader);
                }
                break;
            case '[':
                value = new ArrayList();
                c = readJSONArray(reader, buf, (List)value, true);
                if(c == -1){
                    throw new ConvertException("It reached EOF on the way.");
                }else{
                    c = skipWhitespace(reader);
                }
                break;
            default:
                while(c != -1
                    && c != ','
                    && c != ']'
                    && c != '}'
                    && !Character.isWhitespace((char)c)
                ){
                    buf.append((char)c);
                    c = reader.read();
                }
                if(c == -1){
                    throw new ConvertException("It reached EOF on the way.");
                }
                String str = unescape(buf.toString());
                if(NULL_VALUE.equals(str)){
                    value = null;
                }else if(str.length() != 0){
                    value = str;
                }else{
                    buf.setLength(0);
                    continue;
                }
            }
            array.add(value);
            buf.setLength(0);
        }while(c == ',');
        return c;
    }
    
    private int readJSONProperty(Reader reader, StringBuffer buf, Map jsonObj)
     throws ConvertException, IOException{
        buf.setLength(0);
        int c = skipWhitespace(reader);
        if(c == '"'){
            do{
                c = reader.read();
                if(c != -1 && c != '"'){
                    if(c == '\\'){
                        buf.append((char)c);
                        c = reader.read();
                        if(c == -1){
                            break;
                        }
                    }
                    buf.append((char)c);
                }else{
                    break;
                }
            }while(true);
        }else if(c == '}'){
            return c;
        }else{
            throw new ConvertException("JSON name must be enclosed '\"'.");
        }
        final String name = unescape(buf.toString());
        buf.setLength(0);
        
        c = reader.read();
        if(c != ':'){
            throw new ConvertException("JSON name and value must be separated ':'.");
        }
        c = reader.read();
        
        Object value = null;
        switch(c){
        case '"':
            do{
                c = reader.read();
                if(c != -1 && c != '"'){
                    if(c == '\\'){
                        buf.append((char)c);
                        c = reader.read();
                        if(c == -1){
                            break;
                        }
                    }
                    buf.append((char)c);
                }else{
                    break;
                }
            }while(true);
            value = unescape(buf.toString());
            if(c == -1){
                throw new ConvertException("It reached EOF on the way.");
            }else{
                c = skipWhitespace(reader);
            }
            break;
        case '{':
            value = new LinkedHashMap();
            c = readJSONObject(reader, buf, (Map)value, true);
            if(c == -1){
                throw new ConvertException("It reached EOF on the way.");
            }else{
                c = skipWhitespace(reader);
            }
            break;
        case '[':
            value = new ArrayList();
            c = readJSONArray(reader, buf, (List)value, true);
            if(c == -1){
                throw new ConvertException("It reached EOF on the way.");
            }else{
                c = skipWhitespace(reader);
            }
            break;
        default:
            while(c != -1
                && c != ','
                && c != ']'
                && c != '}'
                && !Character.isWhitespace((char)c)
            ){
                buf.append((char)c);
                c = reader.read();
            }
            if(c == -1){
                throw new ConvertException("It reached EOF on the way.");
            }
            String str = unescape(buf.toString());
            if(NULL_VALUE.equals(str)){
                value = null;
            }else if(str.length() != 0){
                value = str;
            }else{
                return c;
            }
        }
        jsonObj.put(name, value);
        return c;
    }
    
    private String removeBOM(String str){
        if(characterEncodingToObject != null){
            if(UTF8.equals(characterEncodingToObject)){
                if(UTF8_BOM != null && str.startsWith(UTF8_BOM)){
                    str = str.substring(UTF8_BOM.length());
                }
            }else if(UTF16.equals(characterEncodingToObject)){
                if(UTF16_BOM_LE != null && str.startsWith(UTF16_BOM_LE)){
                    str = str.substring(UTF16_BOM_LE.length());
                }else if(UTF16_BOM_BE != null && str.startsWith(UTF16_BOM_BE)){
                    str = str.substring(UTF16_BOM_BE.length());
                }
            }else if(UTF16LE.equals(characterEncodingToObject)){
                if(UTF16_BOM_LE != null && str.startsWith(UTF16_BOM_LE)){
                    str = str.substring(UTF16_BOM_LE.length());
                }
            }else if(UTF16BE.equals(characterEncodingToObject)){
                if(UTF16_BOM_BE != null && str.startsWith(UTF16_BOM_BE)){
                    str = str.substring(UTF16_BOM_BE.length());
                }
            }
        }
        return str;
    }
    
    private String fromUnicode(String unicodeStr){
        String str = null;
        if(unicodeStr != null){
            final int length = unicodeStr.length();
            final StringBuffer buf = new StringBuffer(length);
            for(int i = 0; i < length;){
                //�������؂���
                char c = unicodeStr.charAt(i++);
                //�G�X�P�[�v�Ȃ�
                if(c == ESCAPE && (length - 1) > i){
                    c = unicodeStr.charAt(i++);
                    //UNICODE�}�[�N
                    if(c == 'u'){
                        int value = 0;
                        //�S�����ǂݍ���
                        for(int j=0;j<4;j++){
                            c = unicodeStr.charAt(i++);
                            switch(c){
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + (c - '0');
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + (c - 'a');
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + (c - 'A');
                                break;
                            default:
                                throw new IllegalArgumentException(
                                    "Failed to convert unicode char is " + c
                                );
                            }
                        }
                        buf.append((char)value);
                    }else{
                        buf.append('\\');
                        buf.append((char)c);
                    }
                }else{
                    buf.append((char)c);
                }
            }
            str = buf.toString();
        }
        return str;
    }
    
    private String unescape(String str){
        if(str != null){
            final int length = str.length();
            final StringBuffer buf = new StringBuffer(length);
            boolean isUnescape = false;
            for(int i = 0; i < length;){
                //�������؂���
                char c = str.charAt(i++);
                //�G�X�P�[�v�Ȃ�
                if(c == '\\' && length > i){
                    isUnescape = true;
                    c = str.charAt(i++);
                    switch(c){
                    case BACK_SPACE_CHAR:
                        c = BACK_SPACE;
                        break;
                    case CHANGE_PAGE_CHAR:
                        c = CHANGE_PAGE;
                        break;
                    case LF_CHAR:
                        c = LF;
                        break;
                    case CR_CHAR:
                        c = CR;
                        break;
                    case TAB_CHAR:
                        c = TAB;
                        break;
                    case QUOTE:
                    case BACK_SLASH:
                    case SLASH:
                    default:
                    }
                }
                buf.append(c);
            }
            if(isUnescape){
                str = buf.toString();
            }
        }
        return str;
    }
    
    private int skipWhitespace(Reader reader) throws IOException{
        int c = 0;
        do{
            c = reader.read();
        }while(c != -1 && Character.isWhitespace((char)c));
        return c;
    }
}