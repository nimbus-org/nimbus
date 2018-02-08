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

import java.beans.PropertyEditor;
import java.io.*;
import java.lang.reflect.*;
import java.math.*;
import java.util.*;

import jp.ossc.nimbus.beans.*;
import jp.ossc.nimbus.beans.dataset.*;
import jp.ossc.nimbus.core.Utility;
import jp.ossc.nimbus.util.ClassMappingTree;

/**
 * Java�I�u�W�F�N�g��JSON(JavaScript Object Notation)�R���o�[�^�B<p>
 * 
 * @author M.Takata
 */
public class BeanJSONConverter extends BufferedStreamConverter implements BindingStreamConverter, StreamStringConverter{
    
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
     * Java�I�u�W�F�N�g��JSON��\���ϊ���ʒ萔�B<p>
     */
    public static final int OBJECT_TO_JSON = OBJECT_TO_STREAM;
    
    /**
     * JSON��Java�I�u�W�F�N�g��\���ϊ���ʒ萔�B<p>
     */
    public static final int JSON_TO_OBJECT = STREAM_TO_OBJECT;
    
    /**
     * �ϊ���ʁB<p>
     */
    protected int convertType;
    
    /**
     * Java�I�u�W�F�N�g��JSON�ϊ����Ɏg�p���镶���G���R�[�f�B���O�B<p>
     */
    protected String characterEncodingToStream;
    
    /**
     * JSON��Java�I�u�W�F�N�g�ϊ����Ɏg�p���镶���G���R�[�f�B���O�B<p>
     */
    protected String characterEncodingToObject;
    
    /**
     * JSON��Java�I�u�W�F�N�g�ϊ����Ɏg�p����PropertyAccess�B<p>
     */
    protected PropertyAccess propertyAccess = new PropertyAccess();
    
    /**
     * �I�u�W�F�N�g�ɑ��݂��Ȃ��v���p�e�B�𖳎����邩�ǂ����̃t���O�B<p>
     * �f�t�H���g�́Afalse�ŁA�ϊ��G���[�Ƃ���B<br>
     */
    protected boolean isIgnoreUnknownProperty;
    
    /**
     * �ϊ���I�u�W�F�N�g�Ƃ��ăo�C���h���ꂽ�I�u�W�F�N�g���N���[�����邩�ǂ����̃t���O�B<p>
     * �f�t�H���g�́Afalse�B<br>
     */
    protected boolean isCloneBindingObject;
    
    /**
     * Java�I�u�W�F�N�g��JSON�ϊ�����ۂɁAJSON�ɏo�͂��Ȃ��N���X���̏W���B<p>
     */
    protected Set disableClassNameSet;
    
    /**
     * JSON��Java�I�u�W�F�N�g�ϊ����ɁA���͂�JSONP�ł��鎖��z�肷�邩�ǂ����̃t���O�B<p>
     * �f�t�H���g�́Afalse�B<br>
     */
    protected boolean isJSONP;
    
    /**
     * Java�I�u�W�F�N�g��JSON�ϊ����ɁA{@link DataSet#getHeader()}�܂���{@link DataSet#getRecordList()}��Ώۂɂ��邩�ǂ����𔻒肷��B<p>
     * �f�t�H���g�́Afalse�B<br>
     */
    protected boolean isWrappedDataSet;
    
    /**
     * Java�I�u�W�F�N�g��JSON�ϊ�����ۂɁABean�̃v���p�e�B���̓�������啶���ɂ��邩�ǂ����̃t���O�B<p>
     * �f�t�H���g�́Afalse�B<br>
     */
    protected boolean isCapitalizeBeanProperty;
    
    /**
     * �ϊ�����ۂɁAJSON�̃L�[��Bean�̃v���p�e�B����啶���ɂ��邩�ǂ����̃t���O�B<p>
     * �f�t�H���g�́Afalse�B<br>
     */
    protected boolean isToUpperCase;
    
    /**
     * �ϊ�����ۂɁAJSON�̃L�[��Bean�̃v���p�e�B�����������ɂ��邩�ǂ����̃t���O�B<p>
     * �f�t�H���g�́Afalse�B<br>
     */
    protected boolean isToLowerCase;
    
    /**
     * null�l�̃v���p�e�B���o�͂��邩�ǂ����̃t���O�B<p>
     * �f�t�H���g�́Atrue�ŁA�o�͂���B<br>
     */
    protected boolean isOutputNullProperty = true;
    
    /**
     * Java�I�u�W�F�N�g��JSON�ϊ����ɐ��`����������Ƃ��ďo�͂��邩�ǂ����̃t���O�B<p>
     * �f�t�H���g�́Afalse�Ő��`���Ȃ��B<br>
     */
    protected boolean isFormat = false;
    
    /**
     * Java�I�u�W�F�N�g��JSON�ϊ����ɐ��`����������Ƃ��ďo�͂���ꍇ�Ɏg�p������s�R�[�h�B<p>
     * �f�t�H���g�́A�V�X�e���v���p�e�B��"line.separator"�B<br>
     */
    protected String lineSeparator = System.getProperty("line.separator");
    
    /**
     * Java�I�u�W�F�N�g��JSON�ϊ����ɐ��`����������Ƃ��ďo�͂���ꍇ�Ɏg�p����C���f���g������B<p>
     * �f�t�H���g�́A�^�u�����B<br>
     */
    protected String indentString = "\t";
    
    /**
     * Java�I�u�W�F�N�g��JSON�ϊ����ɐ��`����������Ƃ��ďo�͂���ꍇ�ɂQ�o�C�g���������j�R�[�h�G�X�P�[�v���邩�ǂ����̃t���O�B<p>
     * �f�t�H���g�́Atrue�Ń��j�R�[�h�G�X�P�[�v����B<br>
     */
    protected boolean isUnicodeEscape = true;
    
    /**
     * Java�I�u�W�F�N�g��JSON�ϊ�����Java�I�u�W�F�N�g��public�t�B�[���h�݂̂�ΏۂƂ��邩�ǂ����̃t���O�B<p>
     * �f�t�H���g�́Afalse��public�t�B�[���h�݂̂�Ώۂɂ͂��Ȃ��B<br>
     */
    protected boolean isFieldOnly = false;
    
    /**
     * Java�I�u�W�F�N�g��JSON�ϊ�����Java�I�u�W�F�N�g��public��getter�݂̂�ΏۂƂ��邩�ǂ����̃t���O�B<p>
     * �f�t�H���g�́Atrue��public��getter�݂̂�Ώۂɂ���B<br>
     */
    protected boolean isAccessorOnly = true;
    
    /**
     * JSON��Java�I�u�W�F�N�g�ϊ����ɁA�l�̌^�ɉ����ĕϊ����s��{@link Converter}�̃}�b�s���O�B<p>
     */
    protected ClassMappingTree parseConverterMap;
    
    /**
     * Java�I�u�W�F�N�g��JSON�ϊ����ɁA�l�̌^�ɉ����ĕϊ����s��{@link Converter}�̃}�b�s���O�B<p>
     */
    protected ClassMappingTree formatConverterMap;
    
    /**
     * Java�I�u�W�F�N�g��JSON�ϊ����ɁA�v���p�e�B�ɂǂ̂悤�ɃA�N�Z�X���邩������{@link PropertyAccessType}�̃}�b�s���O�B<p>
     */
    protected ClassMappingTree propertyAccessTypeMap;
    
    /**
     * Java�I�u�W�F�N�g��JSON�ϊ����s���R���o�[�^�𐶐�����B<p>
     */
    public BeanJSONConverter(){
        this(OBJECT_TO_JSON);
    }
    
    /**
     * �w�肳�ꂽ�ϊ���ʂ̃R���o�[�^�𐶐�����B<p>
     *
     * @param type �ϊ����
     * @see #OBJECT_TO_JSON
     * @see #JSON_TO_OBJECT
     */
    public BeanJSONConverter(int type){
        convertType = type;
        disableClassNameSet = new HashSet();
        disableClassNameSet.add(Class.class.getName());
        disableClassNameSet.add(Method.class.getName());
        disableClassNameSet.add(Field.class.getName());
        disableClassNameSet.add(Constructor.class.getName());
        disableClassNameSet.add(Object.class.getName());
    }
    
    /**
     * �ϊ���ʂ�ݒ肷��B<p>
     *
     * @param type �ϊ����
     * @see #getConvertType()
     * @see #OBJECT_TO_JSON
     * @see #JSON_TO_OBJECT
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
     * Java�I�u�W�F�N�g��JSON�ϊ����Ɏg�p���镶���G���R�[�f�B���O��ݒ肷��B<p>
     * 
     * @param encoding �����G���R�[�f�B���O
     */
    public void setCharacterEncodingToStream(String encoding){
        characterEncodingToStream = encoding;
    }
    
    /**
     * Java�I�u�W�F�N�g��JSON�ϊ����Ɏg�p���镶���G���R�[�f�B���O���擾����B<p>
     * 
     * @return �����G���R�[�f�B���O
     */
    public String getCharacterEncodingToStream(){
        return characterEncodingToStream;
    }
    
    /**
     * JSON��Java�I�u�W�F�N�g�ϊ����Ɏg�p���镶���G���R�[�f�B���O��ݒ肷��B<p>
     * 
     * @param encoding �����G���R�[�f�B���O
     */
    public void setCharacterEncodingToObject(String encoding){
        characterEncodingToObject = encoding;
    }
    
    /**
     * JSON��Java�I�u�W�F�N�g�ϊ����Ɏg�p���镶���G���R�[�f�B���O���擾����B<p>
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
     * �I�u�W�F�N�g�ɑ��݂��Ȃ��v���p�e�B�𖳎����邩�ǂ�����ݒ肷��B<p>
     * �f�t�H���g�́Afalse�ŁA�ϊ��G���[�ƂȂ�B<br>
     * 
     * @param isIgnore true�̏ꍇ�A��������
     */
    public void setIgnoreUnknownProperty(boolean isIgnore){
        isIgnoreUnknownProperty = isIgnore;
    }
    
    /**
     * �I�u�W�F�N�g�ɑ��݂��Ȃ��v���p�e�B�𖳎����邩�ǂ����𔻒肷��B<p>
     * 
     * @return true�̏ꍇ�A��������
     */
    public boolean isIgnoreUnknownProperty(){
        return isIgnoreUnknownProperty;
    }
    
    /**
     * �ϊ���I�u�W�F�N�g�Ƃ��ăo�C���h���ꂽ�I�u�W�F�N�g���N���[�����邩�ǂ�����ݒ肷��B<p>
     * �o�C���h���ꂽ�I�u�W�F�N�g�́ACloneable���������Apublic��clone()���\�b�h�����K�v������B<br>
     * �f�t�H���g�́Afalse�B<br>
     * 
     * @param isClone �N���[������ꍇ�́Atrue
     */
    public void setCloneBindingObject(boolean isClone){
        isCloneBindingObject = isClone;
    }
    
    /**
     * �ϊ���I�u�W�F�N�g�Ƃ��ăo�C���h���ꂽ�I�u�W�F�N�g���N���[�����邩�ǂ����𔻒肷��B<p>
     * 
     * @return true�̏ꍇ�́A�N���[������
     */
    public boolean isCloneBindingObject(){
        return isCloneBindingObject;
    }
    
    /**
     * Java�I�u�W�F�N�g��JSON�ϊ�����ۂɁAJSON�ɏo�͂��Ȃ��N���X�̃N���X����o�^����B<p>
     *
     * @param className �N���X��
     */
    public void addDisableClassName(String className){
        disableClassNameSet.add(className);
    }
    
    /**
     * Java�I�u�W�F�N�g��JSON�ϊ�����ۂɁAJSON�ɏo�͂��Ȃ��N���X�̃N���X���z���o�^����B<p>
     *
     * @param classNames �N���X���z��
     */
    public void addDisableClassNames(String[] classNames){
        for(int i = 0; i < classNames.length; i++){
            disableClassNameSet.add(classNames[i]);
        }
    }
    
    /**
     * Java�I�u�W�F�N�g��JSON�ϊ�����ۂɁAJSON�ɏo�͂��Ȃ��N���X�̃N���X���̏W�����擾����B<p>
     *
     * @return �N���X���̏W��
     */
    public Set getDisableClassNameSet(){
        return disableClassNameSet;
    }
    
    /**
     * JSON��Java�I�u�W�F�N�g�ϊ����ɁA���͂�JSONP�ł��鎖��z�肷�邩�ǂ�����ݒ肷��B<p>
     * �f�t�H���g�́Afalse�B<br>
     * true�ɐݒ肷��ƁA���͂�"�R�[���o�b�N�֐���(JSON)"�ƂȂ��Ă���Ƃ݂Ȃ��AJSON�̕����݂̂�ǂݎ��B<br>
     *
     * @param isJSONP JSONP�̏ꍇ�Atrue
     */
    public void setJSONP(boolean isJSONP){
        this.isJSONP = isJSONP;
    }
    
    /**
     * JSON��Java�I�u�W�F�N�g�ϊ����ɁA���͂�JSONP�ł��鎖��z�肷�邩�ǂ����𔻒肷��B<p>
     *
     * @return true�̏ꍇ�AJSONP
     */
    public boolean isJSONP(){
        return isJSONP;
    }
    
    /**
     * Java�I�u�W�F�N�g��JSON�ϊ����ɁA{@link DataSet#getHeader()}�܂���{@link DataSet#getRecordList()}��Ώۂɂ��邩�ǂ�����ݒ肷��B<p>
     * �f�t�H���g�́Afalse�B<br>
     *
     * @param isWrapped Java�I�u�W�F�N�g��JSON�ϊ����ɁA{@link DataSet#getHeader()}�܂���{@link DataSet#getRecordList()}��Ώۂɂ���ꍇ�Atrue
     */
    public void setWrappedDataSet(boolean isWrapped){
        isWrappedDataSet = isWrapped;
    }
    
    /**
     * Java�I�u�W�F�N�g��JSON�ϊ����ɁA{@link DataSet#getHeader()}�܂���{@link DataSet#getRecordList()}��Ώۂɂ��邩�ǂ����𔻒肷��B<p>
     * 
     * @return true�̏ꍇ�AJava�I�u�W�F�N�g��JSON�ϊ����ɁA{@link DataSet#getHeader()}�܂���{@link DataSet#getRecordList()}��Ώۂɂ���
     */
    public boolean isWrappedDataSet(){
        return isWrappedDataSet;
    }
    
    /**
     * Java�I�u�W�F�N�g��JSON�ϊ�����ۂɁABean�̃v���p�e�B���̓�������啶���ɂ��邩�ǂ�����ݒ肷��B<p>
     * 
     * @param isCapitalize Java�I�u�W�F�N�g��JSON�ϊ�����ۂɁABean�̃v���p�e�B���̓�������啶���ɂ���ꍇ�Atrue
     */
    public void setCapitalizeBeanProperty(boolean isCapitalize){
        isCapitalizeBeanProperty = isCapitalize;
    }
    
    /**
     * Java�I�u�W�F�N�g��JSON�ϊ�����ۂɁABean�̃v���p�e�B���̓�������啶���ɂ��邩�ǂ����𔻒肷��B<p>
     * 
     * @return true�̏ꍇ�AJava�I�u�W�F�N�g��JSON�ϊ�����ۂɁABean�̃v���p�e�B���̓�������啶���ɂ���
     */
    public boolean isCapitalizeBeanProperty(){
        return isCapitalizeBeanProperty;
    }
    
    /**
     * �ϊ�����ۂɁAJSON�̃L�[��Bean�̃v���p�e�B����啶���ɂ��邩�ǂ�����ݒ肷��B<p>
     * �f�t�H���g�́Afalse�B<br>
     * 
     * @param toUpperCase �ϊ�����ۂɁAJSON�̃L�[��Bean�̃v���p�e�B����啶���ɂ���ꍇ�Atrue
     */
    public void setToUpperCase(boolean toUpperCase){
        isToUpperCase = toUpperCase;
        if(isToUpperCase){
            isToLowerCase = false;
        }
    }
    
    /**
     * �ϊ�����ۂɁAJSON�̃L�[��Bean�̃v���p�e�B����啶���ɂ��邩�ǂ����𔻒肷��B<p>
     * 
     * @return true�̏ꍇ�A�ϊ�����ۂɁAJSON�̃L�[��Bean�̃v���p�e�B����啶���ɂ���
     */
    public boolean isToUpperCase(){
        return isToUpperCase;
    }
    
    /**
     * �ϊ�����ۂɁAJSON�̃L�[��Bean�̃v���p�e�B�����������ɂ��邩�ǂ�����ݒ肷��B<p>
     * �f�t�H���g�́Afalse�B<br>
     * 
     * @param toLowerCase �ϊ�����ۂɁAJSON�̃L�[��Bean�̃v���p�e�B�����������ɂ���ꍇ�Atrue
     */
    public void setToLowerCase(boolean toLowerCase){
        isToLowerCase = toLowerCase;
        if(isToLowerCase){
            isToUpperCase = false;
        }
    }
    
    /**
     * �ϊ�����ۂɁAJSON�̃L�[��Bean�̃v���p�e�B�����������ɂ��邩�ǂ����𔻒肷��B<p>
     * 
     * @return true�̏ꍇ�A�ϊ�����ۂɁAJSON�̃L�[��Bean�̃v���p�e�B�����������ɂ���
     */
    public boolean isToLowerCase(){
        return isToLowerCase;
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
     * JSON��Java�I�u�W�F�N�g�ϊ����ɁA�l�̌^�ɉ����Ďw�肳�ꂽ{@link Converter}�ŕϊ����s���悤�ɐݒ肷��B<p>
     *
     * @param className �l�̃N���X��
     * @param converter �l��ϊ�����Converter
     * @exception ClassNotFoundException �w�肳�ꂽ�N���X�����݂��Ȃ��ꍇ
     */
    public void setParseConverter(String className, Converter converter) throws ClassNotFoundException{
        setParseConverter(Utility.convertStringToClass(className), converter);
    }
    
    /**
     * JSON��Java�I�u�W�F�N�g�ϊ����ɁA�l�̌^�ɉ����Ďw�肳�ꂽ{@link Converter}�ŕϊ����s���悤�ɐݒ肷��B<p>
     *
     * @param type �l�̌^
     * @param converter �l��ϊ�����Converter
     */
    public void setParseConverter(Class type, Converter converter){
        if(parseConverterMap == null){
            parseConverterMap = new ClassMappingTree();
        }
        parseConverterMap.add(type, converter);
    }
    
    /**
     * Java�I�u�W�F�N�g��JSON�ϊ����ɁA�l�̌^�ɉ����Ďw�肳�ꂽ{@link Converter}�ŕϊ����s���悤�ɐݒ肷��B<p>
     *
     * @param className �l�̃N���X��
     * @param converter �l��ϊ�����Converter
     * @exception ClassNotFoundException �w�肳�ꂽ�N���X�����݂��Ȃ��ꍇ
     */
    public void setFormatConverter(String className, Converter converter) throws ClassNotFoundException{
        setFormatConverter(Utility.convertStringToClass(className), converter);
    }
    
    /**
     * Java�I�u�W�F�N�g��JSON�ϊ����ɁA�l�̌^�ɉ����Ďw�肳�ꂽ{@link Converter}�ŕϊ����s���悤�ɐݒ肷��B<p>
     *
     * @param type �l�̌^
     * @param converter �l��ϊ�����Converter
     */
    public void setFormatConverter(Class type, Converter converter){
        if(formatConverterMap == null){
            formatConverterMap = new ClassMappingTree();
        }
        formatConverterMap.add(type, converter);
    }
    
    /**
     * Java�I�u�W�F�N�g��JSON�ϊ����ɐ��`����������Ƃ��ďo�͂��邩�ǂ����𔻒肷��B<p>
     *
     * @return true�̏ꍇ�A���`����
     */
    public boolean isFormat(){
        return isFormat;
    }
    
    /**
     * Java�I�u�W�F�N�g��JSON�ϊ����ɐ��`����������Ƃ��ďo�͂��邩�ǂ�����ݒ肷��B<p>
     * �f�t�H���g�́Afalse�Ő��`���Ȃ��B<br>
     *
     * @param isFormat ���`����ꍇtrue
     */
    public void setFormat(boolean isFormat){
        this.isFormat = isFormat;
    }
    
    /**
     * Java�I�u�W�F�N�g��JSON�ϊ����ɐ��`����������Ƃ��ďo�͂���ꍇ�Ɏg�p������s�R�[�h���擾����B<p>
     * 
     * @return ���s�R�[�h������
     */
    public String getLineSeparator(){
        return lineSeparator;
    }
    
    /**
     * Java�I�u�W�F�N�g��JSON�ϊ����ɐ��`����������Ƃ��ďo�͂���ꍇ�Ɏg�p������s�R�[�h��ݒ肷��B<p>
     * �f�t�H���g�́A�V�X�e���v���p�e�B��"line.separator"�B<br>
     * 
     * @param ls ���s�R�[�h������
     */
    public void setLineSeparator(String ls){
        lineSeparator = ls;
    }
    
    /**
     * Java�I�u�W�F�N�g��JSON�ϊ����ɐ��`����������Ƃ��ďo�͂���ꍇ�Ɏg�p����C���f���g��������擾����B<p>
     *
     * @return �C���f���g������
     */
    public String getIndent(){
        return indentString;
    }
    
    /**
     * Java�I�u�W�F�N�g��JSON�ϊ����ɐ��`����������Ƃ��ďo�͂���ꍇ�Ɏg�p����C���f���g�������ݒ肷��B<p>
     * �f�t�H���g�́A�^�u�����B<br>
     *
     * @param indent �C���f���g������
     */
    public void setIndent(String indent){
        indentString = indent;
    }
    
    /**
     * Java�I�u�W�F�N�g��JSON�ϊ����ɐ��`����������Ƃ��ďo�͂���ꍇ�ɂQ�o�C�g���������j�R�[�h�G�X�P�[�v���邩�ǂ����𔻒肷��B<p>
     *
     * @return �G�X�P�[�v����ꍇtrue
     */
    public boolean isUnicodeEscape(){
        return isUnicodeEscape;
    }
    
    /**
     * Java�I�u�W�F�N�g��JSON�ϊ����ɐ��`����������Ƃ��ďo�͂���ꍇ�ɂQ�o�C�g���������j�R�[�h�G�X�P�[�v���邩�ǂ�����ݒ肷��B<p>
     * �f�t�H���g�́Atrue�Ń��j�R�[�h�G�X�P�[�v����B<br>
     *
     * @param isEscape �G�X�P�[�v����ꍇtrue
     */
    public void setUnicodeEscape(boolean isEscape){
        isUnicodeEscape = isEscape;
    }
    
    /**
     * Java�I�u�W�F�N�g��JSON�ϊ�����Java�I�u�W�F�N�g��public�t�B�[���h�݂̂�ΏۂƂ��邩�ǂ����𔻒肷��B<p>
     *
     * @return true�̏ꍇ�Apublic�t�B�[���h�݂̂�ΏۂƂ���
     */
    public boolean isFieldOnly(){
        return isFieldOnly;
    }
    
    /**
     * Java�I�u�W�F�N�g��JSON�ϊ�����Java�I�u�W�F�N�g��public�t�B�[���h�݂̂�ΏۂƂ��邩�ǂ�����ݒ肷��B<p>
     * �f�t�H���g�́Afalse��public�t�B�[���h�݂̂�Ώۂɂ͂��Ȃ��B<br>
     *
     * @param isFieldOnly public�t�B�[���h�݂̂�ΏۂƂ���ꍇ�́Atrue
     */
    public void setFieldOnly(boolean isFieldOnly){
        this.isFieldOnly = isFieldOnly;
    }
    
    /**
     * Java�I�u�W�F�N�g��JSON�ϊ�����Java�I�u�W�F�N�g��public�t�B�[���h�݂̂�ΏۂƂ��邩�ǂ�����ݒ肷��B<p>
     * �f�t�H���g�́Afalse��public�t�B�[���h�݂̂�Ώۂɂ͂��Ȃ��B<br>
     *
     * @param type �Ώۂ̃N���X
     * @param isFieldOnly public�t�B�[���h�݂̂�ΏۂƂ���ꍇ�́Atrue
     */
    public void setFieldOnly(Class type, boolean isFieldOnly){
        if(propertyAccessTypeMap == null){
            propertyAccessTypeMap = new ClassMappingTree();
        }
        PropertyAccessType pat = (PropertyAccessType)propertyAccessTypeMap.getValueOf(type);
        if(pat == null){
            pat = new PropertyAccessType();
            propertyAccessTypeMap.add(type, pat);
        }
        pat.isFieldOnly = isFieldOnly;
    }
    
    /**
     * Java�I�u�W�F�N�g��JSON�ϊ�����Java�I�u�W�F�N�g��public�t�B�[���h�݂̂�ΏۂƂ��邩�ǂ����𔻒肷��B<p>
     *
     * @param type �Ώۂ̃N���X
     * @return true�̏ꍇ�Apublic�t�B�[���h�݂̂�ΏۂƂ���
     */
    public boolean isFieldOnly(Class type){
        PropertyAccessType pat = propertyAccessTypeMap == null ? null : (PropertyAccessType)propertyAccessTypeMap.getValue(type);
        return pat == null ? isFieldOnly : pat.isFieldOnly;
    }
    
    /**
     * Java�I�u�W�F�N�g��JSON�ϊ�����Java�I�u�W�F�N�g��public��getter�݂̂�ΏۂƂ��邩�ǂ�����ݒ肷��B<p>
     * �f�t�H���g�́Atrue��public��getter�݂̂�Ώۂɂ���B<br>
     *
     * @param isAccessorOnly public��getter�݂̂�ΏۂƂ���ꍇ�Atrue
     */
    public void setAccessorOnly(boolean isAccessorOnly){
        this.isAccessorOnly = isAccessorOnly;
    }
    
    /**
     * Java�I�u�W�F�N�g��JSON�ϊ�����Java�I�u�W�F�N�g��public��getter�݂̂�ΏۂƂ��邩�ǂ����𔻒肷��B<p>
     *
     * @return true�̏ꍇ�Apublic��getter�݂̂�ΏۂƂ���
     */
    public boolean isAccessorOnly(){
        return isAccessorOnly;
    }
    
    /**
     * Java�I�u�W�F�N�g��JSON�ϊ�����Java�I�u�W�F�N�g��public��getter�݂̂�ΏۂƂ��邩�ǂ�����ݒ肷��B<p>
     * �f�t�H���g�́Atrue��public��getter�݂̂�Ώۂɂ���B<br>
     *
     * @param type �Ώۂ̃N���X
     * @param isAccessorOnly public��getter�݂̂�ΏۂƂ���ꍇ�Atrue
     */
    public void setAccessorOnly(Class type, boolean isAccessorOnly){
        if(propertyAccessTypeMap == null){
            propertyAccessTypeMap = new ClassMappingTree();
        }
        PropertyAccessType pat = (PropertyAccessType)propertyAccessTypeMap.getValueOf(type);
        if(pat == null){
            pat = new PropertyAccessType();
            propertyAccessTypeMap.add(type, pat);
        }
        pat.isAccessorOnly = isAccessorOnly;
    }
    
    /**
     * Java�I�u�W�F�N�g��JSON�ϊ�����Java�I�u�W�F�N�g��public��getter�݂̂�ΏۂƂ��邩�ǂ����𔻒肷��B<p>
     *
     * @param type �Ώۂ̃N���X
     * @return true�̏ꍇ�Apublic��getter�݂̂�ΏۂƂ���
     */
    public boolean isAccessorOnly(Class type){
        PropertyAccessType pat = propertyAccessTypeMap == null ? null : (PropertyAccessType)propertyAccessTypeMap.getValue(type);
        return pat == null ? isAccessorOnly : pat.isAccessorOnly;
    }
    
    /**
     * Java�I�u�W�F�N�g��JSON�ϊ����ɏo�͂��Ȃ��v���p�e�B����ݒ肷��B<p>
     *
     * @param type �Ώۂ̃N���X
     * @param names �v���p�e�B���̔z��
     */
    public void setDisabledPropertyNames(Class type, String[] names){
        if(propertyAccessTypeMap == null){
            propertyAccessTypeMap = new ClassMappingTree();
        }
        PropertyAccessType pat = (PropertyAccessType)propertyAccessTypeMap.getValueOf(type);
        if(pat == null){
            pat = new PropertyAccessType();
            propertyAccessTypeMap.add(type, pat);
        }
        if(names == null || names.length == 0){
            pat.disabledPropertyNames = null;
        }else{
            if(pat.disabledPropertyNames == null){
                pat.disabledPropertyNames = new HashSet();
            }else{
                pat.disabledPropertyNames.clear();
            }
            for(int i = 0; i < names.length; i++){
                pat.disabledPropertyNames.add(names[i]);
            }
        }
    }
    
    /**
     * Java�I�u�W�F�N�g��JSON�ϊ����ɏo�͂���v���p�e�B����ݒ肷��B<p>
     *
     * @param type �Ώۂ̃N���X
     * @param names �v���p�e�B���̔z��
     */
    public void setEnabledPropertyNames(Class type, String[] names){
        if(propertyAccessTypeMap == null){
            propertyAccessTypeMap = new ClassMappingTree();
        }
        PropertyAccessType pat = (PropertyAccessType)propertyAccessTypeMap.getValueOf(type);
        if(pat == null){
            pat = new PropertyAccessType();
            propertyAccessTypeMap.add(type, pat);
        }
        if(names == null || names.length == 0){
            pat.enabledPropertyNames = null;
        }else{
            if(pat.enabledPropertyNames == null){
                pat.enabledPropertyNames = new HashSet();
            }else{
                pat.enabledPropertyNames.clear();
            }
            for(int i = 0; i < names.length; i++){
                pat.enabledPropertyNames.add(names[i]);
            }
        }
    }
    
    /**
     * Java�I�u�W�F�N�g��JSON�ϊ����ɏo�͂���v���p�e�B�����ǂ����𔻒肷��B<p>
     *
     * @param type �Ώۂ̃N���X
     * @param name �v���p�e�B��
     * @return �o�͂���ꍇtrue
     */
    public boolean isEnabledPropertyName(Class type, String name){
        PropertyAccessType pat = propertyAccessTypeMap == null ? null : (PropertyAccessType)propertyAccessTypeMap.getValue(type);
        if(pat == null){
            return true;
        }
        if(pat.disabledPropertyNames != null && pat.disabledPropertyNames.contains(name)){
            return false;
        }
        if(pat.enabledPropertyNames != null){
            return pat.enabledPropertyNames.contains(name);
        }
        return true;
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
        case OBJECT_TO_JSON:
            return convertToStream(obj);
        case JSON_TO_OBJECT:
            if(obj instanceof File){
                return toObject((File)obj);
            }else if(obj instanceof InputStream){
                return toObject((InputStream)obj);
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
     * �I�u�W�F�N�g����JSON�o�C�g�z��ɕϊ�����B<p>
     *
     * @param obj �I�u�W�F�N�g
     * @return JSON�o�C�g�z��
     * @exception ConvertException �ϊ��Ɏ��s�����ꍇ
     */
    protected byte[] convertToByteArray(Object obj) throws ConvertException{
        return toJSON(obj);
    }
    
    /**
     * JSON�X�g���[������I�u�W�F�N�g�ɕϊ�����B<p>
     *
     * @param is JSON�X�g���[��
     * @return �I�u�W�F�N�g
     * @exception ConvertException �ϊ��Ɏ��s�����ꍇ
     */
    public Object convertToObject(InputStream is) throws ConvertException{
        return toObject(is);
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
        return toObject(is, returnType);
    }
    
    protected byte[] toJSON(Object obj) throws ConvertException{
        byte[] result = null;
        try{
            StringBuffer buf = new StringBuffer();
            appendValue(buf, null, obj, new HashSet(), 0);
            
            String str = buf.toString();
            result = characterEncodingToStream == null ? str.getBytes() : str.getBytes(characterEncodingToStream);
        }catch(IOException e){
            throw new ConvertException(e);
        }
        return result;
    }
    
    private StringBuffer appendIndent(StringBuffer buf, int indent){
        if(indent <= 0){
            return buf;
        }
        for(int i = 0; i < indent; i++){
            buf.append(getIndent());
        }
        return buf;
    }
    
    private StringBuffer appendName(StringBuffer buf, String name, int indent){
        if(isFormat()){
            buf.append(getLineSeparator());
            appendIndent(buf, indent);
        }
        buf.append(STRING_ENCLOSURE);
        if(isToUpperCase){
            name = name == null ? null : name.toUpperCase();
        }else if(isToLowerCase){
            name = name == null ? null : name.toLowerCase();
        }
        buf.append(escape(name));
        buf.append(STRING_ENCLOSURE);
        return buf;
    }
    
    private StringBuffer appendValue(StringBuffer buf, Class type, Object value, Set instanceSet, int indent){
        if(type == null && value != null){
            type = value.getClass();
        }
        if(type != null){
            Converter converter = formatConverterMap == null ? null : (Converter)formatConverterMap.getValue(type);
            if(converter != null){
                value = converter.convert(value);
                if(Number.class.isAssignableFrom(type) || type.isPrimitive()){
                    if(value == null){
                        buf.append(NULL_VALUE);
                    }else{
                        buf.append(value);
                    }
                }else{
                    if(value == null){
                        buf.append(NULL_VALUE);
                    }else{
                        buf.append(STRING_ENCLOSURE);
                        buf.append(escape(value.toString()));
                        buf.append(STRING_ENCLOSURE);
                    }
                }
                return buf;
            }
        }
        
        if(value == null){
            if(type == null){
                buf.append(NULL_VALUE);
            }else if(type.isPrimitive()
                    && (Byte.TYPE.equals(type)
                        || Short.TYPE.equals(type)
                        || Integer.TYPE.equals(type)
                        || Long.TYPE.equals(type)
                        || Float.TYPE.equals(type)
                        || Double.TYPE.equals(type))
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
            appendArray(buf, value, instanceSet, indent);
        }else if(DataSet.class.isAssignableFrom(type)){
            if(instanceSet.contains(value)){
                return buf;
            }
            DataSet dataSet = (DataSet)value;
            if(isWrappedDataSet){
                Header header = dataSet.getHeader();
                if(header != null){
                    appendValue(buf, null, header, instanceSet, indent + 1);
                }else{
                    RecordList list = dataSet.getRecordList();
                    if(list != null){
                        appendValue(buf, null, list, instanceSet, indent + 1);
                    }
                }
            }else{
                instanceSet.add(value);
                buf.append(OBJECT_ENCLOSURE_START);
                String[] names = dataSet.getHeaderNames();
                boolean isAppend = names.length != 0;
                for(int i = 0, imax = names.length; i < imax; i++){
                    String headerName = names[i];
                    Header header = dataSet.getHeader(headerName);
                    appendName(buf, headerName, indent + 1);
                    buf.append(PROPERTY_SEPARATOR);
                    appendValue(buf, null, header, instanceSet, indent + 1);
                    if(i != imax - 1){
                        buf.append(ARRAY_SEPARATOR);
                    }
                }
                names = dataSet.getRecordListNames();
                if(isAppend && names.length != 0){
                    buf.append(ARRAY_SEPARATOR);
                }
                for(int i = 0, imax = names.length; i < imax; i++){
                    String recListName = names[i];
                    RecordList recList = dataSet.getRecordList(recListName);
                    appendName(buf, recListName, indent + 1);
                    buf.append(PROPERTY_SEPARATOR);
                    appendValue(buf, null, recList, instanceSet, indent + 1);
                    if(i != imax - 1){
                        buf.append(ARRAY_SEPARATOR);
                    }
                }
                if(isFormat()){
                    buf.append(getLineSeparator());
                    appendIndent(buf, indent);
                }
                buf.append(OBJECT_ENCLOSURE_END);
                instanceSet.remove(value);
            }
        }else if(Record.class.isAssignableFrom(type)){
            if(instanceSet.contains(value)){
                return buf;
            }
            instanceSet.add(value);
            Record record = (Record)value;
            RecordSchema schema = record.getRecordSchema();
            if(schema == null){
                throw new ConvertException("Schema is null.");
            }
            PropertySchema[] propSchemata = schema.getPropertySchemata();
            boolean isOutput = false;
            buf.append(OBJECT_ENCLOSURE_START);
            for(int i = 0, imax = propSchemata.length; i < imax; i++){
                String key = propSchemata[i].getName();
                Object val = record.getProperty(key);
                if(val == null && !isOutputNullProperty){
                    continue;
                }
                if(isOutput){
                    buf.append(ARRAY_SEPARATOR);
                }
                appendName(buf, key == null ? (String)key : key.toString(), indent + 1);
                buf.append(PROPERTY_SEPARATOR);
                if(val == null){
                    appendValue(buf, propSchemata[i].getType(), null, instanceSet, indent + 1);
                }else{
                    Class propType = propSchemata[i].getType();
                    if(propType == null){
                        propType = val.getClass();
                    }
                    if(propType.isArray()
                        || Collection.class.isAssignableFrom(propType)){
                        appendArray(buf, val, instanceSet, indent + 1);
                    }else{
                        Object formatProp = record.getFormatProperty(i);
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
                            appendValue(buf, propType, formatProp, instanceSet, indent + 1);
                        }else{
                            appendValue(buf, null, formatProp, instanceSet, indent + 1);
                        }
                    }
                }
                isOutput = true;
            }
            if(isFormat()){
                buf.append(getLineSeparator());
                appendIndent(buf, indent);
            }
            buf.append(OBJECT_ENCLOSURE_END);
            instanceSet.remove(value);
        }else if(Map.class.isAssignableFrom(type)){
            if(instanceSet.contains(value)){
                return buf;
            }
            instanceSet.add(value);
            Map map = (Map)value;
            Object[] keys = map.keySet().toArray();
            boolean isOutput = false;
            buf.append(OBJECT_ENCLOSURE_START);
            for(int i = 0, imax = keys.length; i < imax; i++){
                Object key = keys[i];
                Object val = map.get(key);
                if(val != null && disableClassNameSet.contains(val.getClass().getName())){
                    continue;
                }
                if(val == null && !isOutputNullProperty){
                    continue;
                }
                if(isOutput){
                    buf.append(ARRAY_SEPARATOR);
                }
                appendName(buf, key == null ? (String)key : key.toString(), indent + 1);
                buf.append(PROPERTY_SEPARATOR);
                if(val == null){
                    appendValue(buf, null, null, instanceSet, indent + 1);
                }else{
                    Class propType = val.getClass();
                    if(propType.isArray()
                        || Collection.class.isAssignableFrom(propType)){
                        appendArray(buf, val, instanceSet, indent + 1);
                    }else if(Number.class.isAssignableFrom(propType)
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
                            val,
                            instanceSet,
                            indent + 1
                        );
                    }else{
                        appendValue(buf, null, val, instanceSet, indent + 1);
                    }
                }
                isOutput = true;
            }
            if(isFormat()){
                buf.append(getLineSeparator());
                appendIndent(buf, indent);
            }
            buf.append(OBJECT_ENCLOSURE_END);
            instanceSet.remove(value);
        }else if(String.class.isAssignableFrom(type)){
            buf.append(STRING_ENCLOSURE);
            buf.append(escape(value.toString()));
            buf.append(STRING_ENCLOSURE);
        }else{
            if(instanceSet.contains(value)){
                return buf;
            }
            instanceSet.add(value);
            buf.append(OBJECT_ENCLOSURE_START);
            final Property[] props = isFieldOnly(type)
                ? SimpleProperty.getFieldProperties(value)
                    : SimpleProperty.getProperties(value, !isAccessorOnly(type));
            try{
                boolean isOutput = false;
                for(int i = 0, imax = props.length; i < imax; i++){
                    if(!isEnabledPropertyName(type, props[i].getPropertyName())){
                        continue;
                    }
                    if(!props[i].isReadable(value)){
                        continue;
                    }
                    Object propValue = props[i].getProperty(value);
                    if(propValue == value){
                        continue;
                    }
                    if(propValue != null && disableClassNameSet.contains(propValue.getClass().getName())){
                        continue;
                    }
                    if(propValue == null && !isOutputNullProperty){
                        continue;
                    }
                    if(isOutput){
                        buf.append(ARRAY_SEPARATOR);
                    }
                    if(isCapitalizeBeanProperty){
                        String propName = props[i].getPropertyName();
                        if(propName.length() != 0 && Character.isLowerCase(propName.charAt(0))){
                            char firstChar = propName.charAt(0);
                            char uppercaseChar =  Character.toUpperCase(firstChar);
                            if(firstChar != uppercaseChar){
                                propName = uppercaseChar + propName.substring(1);
                            }
                        }
                        appendName(buf, propName, indent + 1);
                    }else{
                        appendName(buf, props[i].getPropertyName(), indent + 1);
                    }
                    buf.append(PROPERTY_SEPARATOR);
                    Class propType = props[i].getPropertyType(value);
                    appendValue(
                        buf,
                        propValue == null? propType : propValue.getClass(),
                        propValue,
                        instanceSet,
                        indent + 1
                    );
                    isOutput = true;
                }
            }catch(NoSuchPropertyException e){
                throw new ConvertException(e);
            }catch(InvocationTargetException e){
                throw new ConvertException(e);
            }
            if(isFormat()){
                buf.append(getLineSeparator());
                appendIndent(buf, indent);
            }
            buf.append(OBJECT_ENCLOSURE_END);
            instanceSet.remove(value);
        }
        return buf;
    }
    
    private StringBuffer appendArray(StringBuffer buf, Object array, Set instanceSet, int indent){
        if(instanceSet.contains(array)){
            return buf;
        }
        instanceSet.add(array);
        buf.append(ARRAY_ENCLOSURE_START);
        if(array.getClass().isArray()){
            boolean isOutput = false;
            for(int i = 0, imax = Array.getLength(array); i < imax; i++){
                Object element = Array.get(array, i);
                if(element != null && disableClassNameSet.contains(element.getClass().getName())){
                    continue;
                }
                if(isOutput){
                    buf.append(ARRAY_SEPARATOR);
                }
                if(isFormat()){
                    buf.append(getLineSeparator());
                    appendIndent(buf, indent + 1);
                }
                appendValue(
                    buf,
                    element == null ? array.getClass().getComponentType() : element.getClass(),
                    element,
                    instanceSet,
                    indent + 1
                );
                isOutput = true;
            }
        }else if(List.class.isAssignableFrom(array.getClass())){
            List list = (List)array;
            boolean isOutput = false;
            for(int i = 0, imax = list.size(); i < imax; i++){
                Object val = list.get(i);
                if(val != null && disableClassNameSet.contains(val.getClass().getName())){
                    continue;
                }
                if(isOutput){
                    buf.append(ARRAY_SEPARATOR);
                }
                if(isFormat()){
                    buf.append(getLineSeparator());
                    appendIndent(buf, indent + 1);
                }
                appendValue(buf, null, val, instanceSet, indent + 1);
                isOutput = true;
            }
        }else if(Collection.class.isAssignableFrom(array.getClass())){
            Iterator itr = ((Collection)array).iterator();
            boolean isOutput = false;
            while(itr.hasNext()){
                Object val = itr.next();
                if(val != null && disableClassNameSet.contains(val.getClass().getName())){
                    continue;
                }
                if(isOutput){
                    buf.append(ARRAY_SEPARATOR);
                }
                if(isFormat()){
                    buf.append(getLineSeparator());
                    appendIndent(buf, indent + 1);
                }
                appendValue(buf, null, val, instanceSet, indent + 1);
                isOutput = true;
            }
        }
        if(isFormat()){
            buf.append(getLineSeparator());
            appendIndent(buf, indent);
        }
        buf.append(ARRAY_ENCLOSURE_END);
        instanceSet.remove(array);
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
    
    protected Object toObject(File file) throws ConvertException{
        try{
            return toObject(new FileInputStream(file));
        }catch(IOException e){
            throw new ConvertException(e);
        }
    }
    
    protected Object toObject(InputStream is) throws ConvertException{
        return toObject(is, null);
    }
    
    protected Object toObject(InputStream is, Object returnType)
     throws ConvertException{
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Object jsonObj = null;
        try{
            int length = 0;
            byte[] buf = new byte[1024];
            while((length = is.read(buf)) != -1){
                baos.write(buf, 0, length);
            }
            String dataStr = characterEncodingToObject == null ? new String(baos.toByteArray())
                : new String(baos.toByteArray(), characterEncodingToObject);
            dataStr = removeBOM(dataStr);
            dataStr = fromUnicode(dataStr);
            if(isJSONP){
                int startIndex = dataStr.indexOf('(');
                int endIndex = dataStr.lastIndexOf(')');
                if(startIndex != -1 && endIndex != -1 && startIndex < endIndex){
                    dataStr = dataStr.substring(startIndex + 1, endIndex);
                }
            }
            Class componentType = null;
            if(returnType != null){
                if(returnType instanceof Class){
                    Class returnClass = (Class)returnType;
                    if(returnClass.isArray()){
                        jsonObj = new ArrayList();
                        componentType = returnClass.getComponentType();
                    }else{
                        try{
                            jsonObj = returnClass.newInstance();
                        }catch(InstantiationException e){
                            throw new ConvertException(e);
                        }catch(IllegalAccessException e){
                            throw new ConvertException(e);
                        }
                    }
                }else{
                    jsonObj = returnType;
                    if(isCloneBindingObject){
                        if(jsonObj instanceof DataSet){
                            jsonObj = ((DataSet)jsonObj).cloneSchema();
                        }else if(jsonObj instanceof RecordList){
                            jsonObj = ((RecordList)jsonObj).cloneSchema();
                        }else if(jsonObj instanceof Record){
                            jsonObj = ((Record)jsonObj).cloneSchema();
                        }else if(jsonObj instanceof Cloneable){
                            try{
                                jsonObj = jsonObj.getClass().getMethod("clone", (Class[])null).invoke(jsonObj, (Object[])null);
                            }catch(NoSuchMethodException e){
                                throw new ConvertException(e);
                            }catch(IllegalAccessException e){
                                throw new ConvertException(e);
                            }catch(InvocationTargetException e){
                                throw new ConvertException(e);
                            }
                        }
                    }
                }
            }
            StringReader reader = new StringReader(dataStr);
            int c = skipWhitespace(reader);
            if(c == -1){
                throw new ConvertException("It reached EOF on the way.");
            }
            switch(c){
            case '{':
                if(jsonObj == null){
                    jsonObj = new HashMap();
                }
                readJSONObject(
                    reader,
                    new StringBuffer(),
                    ((jsonObj instanceof DataSet) && isWrappedDataSet) ? ((DataSet)jsonObj).getHeader() : jsonObj,
                    null,
                    jsonObj instanceof DataSet ? (DataSet)jsonObj : null
                );
                break;
            case '[':
                if(jsonObj == null){
                    jsonObj = new ArrayList();
                }
                readJSONArray(
                    reader,
                    new StringBuffer(),
                    componentType,
                    ((jsonObj instanceof DataSet) && isWrappedDataSet) ? ((DataSet)jsonObj).getRecordList() : (List)jsonObj,
                    jsonObj instanceof DataSet ? (DataSet)jsonObj : null
                );
                if(componentType != null){
                    jsonObj = ((List)jsonObj).toArray(
                        (Object[])Array.newInstance(componentType, ((List)jsonObj).size())
                    );
                }
                break;
            default:
                throw new ConvertException("Not json." + dataStr);
            }
        }catch(IOException e){
            throw new ConvertException(e);
        }
        return jsonObj;
    }
    
    private int readJSONObject(
        Reader reader,
        StringBuffer buf,
        Object jsonObj,
        MappedProperty mappedProp,
        DataSet dataSet
    ) throws ConvertException, IOException{
        int c = 0;
        do{
            c = readJSONProperty(reader, buf, jsonObj, mappedProp, dataSet);
            if(c == -1){
                throw new ConvertException("It reached EOF on the way.");
            }
        }while(c == ',');
        
        return c;
    }
    
    private int readJSONArray(
        Reader reader,
        StringBuffer buf,
        Class componentType,
        List array,
        DataSet dataSet
    ) throws ConvertException, IOException{
        buf.setLength(0);
        int c = 0;
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
                if(((String)value).length() != 0 && componentType != null && !String.class.equals(componentType)){
                    value = toPrimitive((String)value, componentType);
                }
                if(c == -1){
                    throw new ConvertException("It reached EOF on the way.");
                }else{
                    c = skipWhitespace(reader);
                }
                break;
            case '{':
                if(array instanceof RecordList){
                    value = ((RecordList)array).createRecord();
                }else if(componentType == null){
                    value = new HashMap();
                }else{
                    try{
                        value = componentType.newInstance();
                    }catch(InstantiationException e){
                        throw new ConvertException(e);
                    }catch(IllegalAccessException e){
                        throw new ConvertException(e);
                    }
                }
                c = readJSONObject(reader, buf, value, null, dataSet);
                if(c == -1){
                    throw new ConvertException("It reached EOF on the way.");
                }else if(c != '}'){
                    do{
                        c = reader.read();
                        if(c != -1
                            && c != '}'
                            && !Character.isWhitespace((char)c)){
                            throw new ConvertException("Expected '}' but '" + (char)c + "' appeared.");
                        }
                    }while(c != -1 && c != '}');
                }
                c = skipWhitespace(reader);
                break;
            case '[':
                value = new ArrayList();
                Class nestComponentType = null;
                if(componentType != null){
                    if(componentType.isArray()){
                        nestComponentType = componentType.getComponentType();
                    }else{
                        throw new ConvertException("ComponentType is not multidimentional array. " + componentType);
                    }
                }
                c = readJSONArray(reader, buf, nestComponentType, (List)value, dataSet);
                if(nestComponentType != null){
                    value = ((List)value).toArray((Object[])Array.newInstance(nestComponentType, ((List)value).size()));
                }
                if(c == -1){
                    throw new ConvertException("It reached EOF on the way.");
                }else if(c != ']'){
                    do{
                        c = reader.read();
                        if(c != -1
                            && c != ']'
                            && !Character.isWhitespace((char)c)){
                            throw new ConvertException("Expected ']' but '" + (char)c + "' appeared.");
                        }
                    }while(c != -1 && c != ']');
                }
                c = skipWhitespace(reader);
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
                    value = toPrimitive(str, componentType);
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
    
    private int readJSONProperty(Reader reader, StringBuffer buf, Object jsonObj, MappedProperty mappedProp, DataSet dataSet)
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
        }else{
            if(c == '}'){
                return c;
            }
            throw new ConvertException("JSON name must be enclosed '\"'.");
        }
        String name = unescape(buf.toString());
        if(isToUpperCase){
            name = name == null ? null : name.toUpperCase();
        }else if(isToLowerCase){
            name = name == null ? null : name.toLowerCase();
        }
        buf.setLength(0);
        
        c = reader.read();
        if(c != ':'){
            throw new ConvertException("JSON name and value must be separated ':'.");
        }
        c = skipWhitespace(reader);
        Class propType = null;
        boolean isUnknownProperty = false;
        
        Object value = null;
        switch(c){
        case '"':
            if(jsonObj instanceof DataSet || jsonObj instanceof RecordList){
                if(!isIgnoreUnknownProperty){
                    throw new ConvertException("Unknown property : " + name);
                }
                isUnknownProperty = true;
            }
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
            if(jsonObj instanceof Record){
                Record record = (Record)jsonObj;
                RecordSchema schema = record.getRecordSchema();
                PropertySchema propSchema = schema.getPropertySchema(name);
                if(propSchema == null){
                    if(!isIgnoreUnknownProperty){
                        throw new ConvertException("Unknown property : " + name);
                    }
                    isUnknownProperty = true;
                }else{
                    propType = propSchema.getType();
                }
            }else if(!(jsonObj instanceof Map)){
                Property property = mappedProp != null ? mappedProp : propertyAccess.getProperty(name);
                try{
                    propType = property.getPropertyType(jsonObj);
                }catch(NoSuchPropertyException e){
                    if(!isIgnoreUnknownProperty){
                        throw new ConvertException(e);
                    }
                    isUnknownProperty = true;
                }catch(InvocationTargetException e){
                    throw new ConvertException(e);
                }
            }
            if(!isUnknownProperty && propType != null){
                Converter converter = parseConverterMap == null ? null : (Converter)parseConverterMap.getValue(propType);
                if(converter != null){
                    value = converter.convert(value);
                }else if(!(jsonObj instanceof Record) && ((String)value).length() != 0 && !String.class.equals(propType)){
                    value = toPrimitive((String)value, propType);
                }
            }
            if(c == -1){
                throw new ConvertException("It reached EOF on the way.");
            }else{
                c = skipWhitespace(reader);
            }
            break;
        case '{':
            Object objectValue = null;
            MappedProperty mappedProperty = null;
            if(jsonObj instanceof DataSet){
                DataSet ds = (DataSet)jsonObj;
                objectValue = ds.getHeader(name);
                if(objectValue == null){
                    if(!isIgnoreUnknownProperty){
                        throw new ConvertException("Unknown header : " + name);
                    }
                    isUnknownProperty = true;
                }
            }else if(jsonObj instanceof Record){
                Record record = (Record)jsonObj;
                RecordSchema schema = record.getRecordSchema();
                PropertySchema propSchema = schema.getPropertySchema(name);
                if(propSchema == null){
                    if(!isIgnoreUnknownProperty){
                        throw new ConvertException("Unknown nested record : " + name);
                    }
                    isUnknownProperty = true;
                }else{
                    if(propSchema instanceof RecordPropertySchema){
                        RecordPropertySchema recPropSchema = (RecordPropertySchema)propSchema;
                        if(dataSet != null){
                            objectValue = dataSet.createNestedRecord(recPropSchema.getRecordName());
                            value = objectValue;
                        }else{
                            objectValue = record.getProperty(name);
                        }
                    }else{
                        propType = propSchema.getType();
                    }
                }
            }else if(!(jsonObj instanceof Map)){
                Property property = mappedProp != null ? mappedProp : propertyAccess.getProperty(name);
                try{
                    propType = property.getPropertyType(jsonObj);
                    if(property.isReadable(jsonObj)){
                        objectValue = property.getProperty(jsonObj);
                    }
                }catch(NoSuchPropertyException e){
                    MappedProperty[] props = MappedProperty.getMappedProperties(jsonObj.getClass(), name);
                    if(props != null){
                        for(int i = 0; i < props.length; i++){
                            if(props[i].isWritable(jsonObj, null)){
                                mappedProperty = props[i];
                                objectValue = jsonObj;
                                value = objectValue;
                                break;
                            }
                        }
                    }
                    if(mappedProperty == null){
                        if(!isIgnoreUnknownProperty){
                            throw new ConvertException(e);
                        }
                        isUnknownProperty = true;
                    }
                }catch(InvocationTargetException e){
                    throw new ConvertException(e);
                }
            }
            if(propType != null){
                if(objectValue == null){
                    if(!propType.isInterface() && !Modifier.isAbstract(propType.getModifiers())){
                        try{
                            objectValue = propType.newInstance();
                            value = objectValue;
                        }catch(InstantiationException e){
                            throw new ConvertException(e);
                        }catch(IllegalAccessException e){
                            throw new ConvertException(e);
                        }
                    }else if(!Map.class.isAssignableFrom(propType)){
                        if(!isIgnoreUnknownProperty){
                            throw new ConvertException("Unknown property : " + name);
                        }
                        isUnknownProperty = true;
                    }
                }else{
                    value = objectValue;
                }
            }
            if(objectValue == null){
                objectValue = new HashMap();
                value = objectValue;
            }
            c = readJSONObject(reader, buf, objectValue, mappedProperty, dataSet);
            if(c == -1){
                throw new ConvertException("It reached EOF on the way.");
            }else if(c != '}'){
                do{
                    c = reader.read();
                    if(c != -1
                        && c != '}'
                        && !Character.isWhitespace((char)c)){
                        throw new ConvertException("Expected '}' but '" + (char)c + "' appeared.");
                    }
                }while(c != -1 && c != '}');
            }
            c = skipWhitespace(reader);
            if(mappedProperty != null){
                return c;
            }
            break;
        case '[':
            Object arrayValue = null;
            Class componentType = null;
            if(jsonObj instanceof DataSet){
                DataSet ds = (DataSet)jsonObj;
                arrayValue = ds.getRecordList(name);
                if(arrayValue == null){
                    if(!isIgnoreUnknownProperty){
                        throw new ConvertException("Unknown recordList : " + name);
                    }
                    isUnknownProperty = true;
                }
            }else if(jsonObj instanceof Record){
                Record record = (Record)jsonObj;
                RecordSchema schema = record.getRecordSchema();
                PropertySchema propSchema = schema.getPropertySchema(name);
                if(propSchema == null){
                    if(!isIgnoreUnknownProperty){
                        throw new ConvertException("Unknown nested recordList : " + name);
                    }
                    isUnknownProperty = true;
                }else{
                    if(propSchema instanceof RecordListPropertySchema){
                        RecordListPropertySchema recListPropSchema = (RecordListPropertySchema)propSchema;
                        if(dataSet != null){
                            arrayValue = dataSet.createNestedRecordList(recListPropSchema.getRecordListName());
                            value = arrayValue;
                        }else{
                            arrayValue = record.getProperty(name);
                        }
                    }else{
                        propType = propSchema.getType();
                    }
                }
            }else if(!(jsonObj instanceof Map)){
                Property property = mappedProp != null ? mappedProp : propertyAccess.getProperty(name);
                try{
                    propType = property.getPropertyType(jsonObj);
                }catch(NoSuchPropertyException e){
                    if(!isIgnoreUnknownProperty){
                        throw new ConvertException(e);
                    }
                    isUnknownProperty = true;
                }catch(InvocationTargetException e){
                    throw new ConvertException(e);
                }
            }
            if(propType != null){
                if(!propType.isArray() && !List.class.isAssignableFrom(propType)){
                    if(!isIgnoreUnknownProperty){
                        throw new ConvertException("Unknown property : " + name);
                    }
                    isUnknownProperty = true;
                }else if(propType.isArray()){
                    componentType = propType.getComponentType();
                }else{
                    if(!propType.isInterface() && !Modifier.isAbstract(propType.getModifiers())){
                        try{
                            objectValue = propType.newInstance();
                            value = objectValue;
                        }catch(InstantiationException e){
                            throw new ConvertException(e);
                        }catch(IllegalAccessException e){
                            throw new ConvertException(e);
                        }
                    }
                }
            }
            if(arrayValue == null){
                arrayValue = new ArrayList();
                value = arrayValue;
            }
            c = readJSONArray(reader, buf, componentType, (List)arrayValue, dataSet);
            if(!isUnknownProperty && propType != null && propType.isArray()){
                if(componentType.isPrimitive()){
                    List list = (List)arrayValue;
                    value = Array.newInstance(componentType, list.size());
                    IndexedProperty indexdProp = new IndexedProperty("");
                    try{
                        for(int i = 0, imax = list.size(); i < imax; i++){
                            indexdProp.setIndex(i);
                            indexdProp.setProperty(value, list.get(i));
                        }
                    }catch(NoSuchPropertyException e){
                        throw new ConvertException(e);
                    }catch(InvocationTargetException e){
                        throw new ConvertException(e);
                    }
                }else{
                    value = ((List)arrayValue).toArray((Object[])Array.newInstance(componentType, ((List)arrayValue).size()));
                }
            }
            if(c == -1){
                throw new ConvertException("It reached EOF on the way.");
            }else if(c != ']'){
                do{
                    c = reader.read();
                    if(c != -1
                        && c != ']'
                        && !Character.isWhitespace((char)c)){
                        throw new ConvertException("Expected ']' but '" + (char)c + "' appeared.");
                    }
                }while(c != -1 && c != ']');
            }
            c = skipWhitespace(reader);
            break;
        default:
            if(jsonObj instanceof DataSet || jsonObj instanceof RecordList){
                if(!isIgnoreUnknownProperty){
                    throw new ConvertException("Unknown property : " + name);
                }
                isUnknownProperty = true;
            }
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
            if(str.length() == 0){
                return c;
            }else{
                if(jsonObj instanceof Record){
                    Record record = (Record)jsonObj;
                    RecordSchema schema = record.getRecordSchema();
                    PropertySchema propSchema = schema.getPropertySchema(name);
                    if(propSchema == null){
                        if(!isIgnoreUnknownProperty){
                            throw new ConvertException("Unknown property : " + name);
                        }
                        isUnknownProperty = true;
                    }else{
                        propType = propSchema.getType();
                    }
                }else if(!(jsonObj instanceof Map)){
                    Property property = mappedProp != null ? mappedProp : propertyAccess.getProperty(name);
                    try{
                        propType = property.getPropertyType(jsonObj);
                    }catch(NoSuchPropertyException e){
                        if(!isIgnoreUnknownProperty){
                            throw new ConvertException(e);
                        }
                        isUnknownProperty = true;
                    }catch(InvocationTargetException e){
                        throw new ConvertException(e);
                    }
                }
                if(!isUnknownProperty){
                    Converter converter = null;
                    if(propType != null){
                        converter = parseConverterMap == null ? null : (Converter)parseConverterMap.getValue(propType);
                    }
                    if(NULL_VALUE.equals(str)){
                        value = null;
                        if(converter != null){
                            value = converter.convert(value);
                        }
                    }else{
                        if(converter != null){
                            value = converter.convert(str);
                        }else{
                            value = jsonObj instanceof Record ? str : toPrimitive(str, propType);
                        }
                    }
                }
            }
        }
        if(!isUnknownProperty && value != null){
            if(mappedProp != null){
                try{
                    mappedProp.setKey(name);
                    mappedProp.setProperty(jsonObj, value);
                }catch(NoSuchPropertyException e){
                    if(!isIgnoreUnknownProperty){
                        throw new ConvertException(e);
                    }
                }catch(InvocationTargetException e){
                    throw new ConvertException(e);
                }
            }else if(jsonObj instanceof Record){
                Record rec = (Record)jsonObj;
                if(isIgnoreUnknownProperty){
                    RecordSchema schema = rec.getRecordSchema();
                    if(schema != null && schema.getPropertyIndex(name) == -1){
                        return c;
                    }
                }
                rec.setParseProperty(name, value);
            }else if(jsonObj instanceof Map){
                ((Map)jsonObj).put(name, value);
            }else if(!isUnknownProperty){
                try{
                    propertyAccess.set(jsonObj, name, value);
                }catch(NoSuchPropertyException e){
                    if(!isIgnoreUnknownProperty){
                        throw new ConvertException(e);
                    }
                }catch(InvocationTargetException e){
                    throw new ConvertException(e);
                }
            }
        }
        return c;
    }
    
    private Object toPrimitive(String str, Class propType) throws ConvertException{
        if(propType == null){
            if(str.toLowerCase().equals("true") || str.toLowerCase().equals("false")){
                return new Boolean(str);
            }else if(str.indexOf('.') == -1){
                try{
                    return new BigInteger(str);
                }catch(NumberFormatException e){
                    throw new ConvertException(e);
                }
            }else{
                try{
                    return new BigDecimal(str);
                }catch(NumberFormatException e){
                    throw new ConvertException(e);
                }
            }
        }else{
            if(propType.isPrimitive() || Number.class.isAssignableFrom(propType) || Boolean.class.isAssignableFrom(propType)){
                PropertyEditor editor = NimbusPropertyEditorManager.findEditor(propType);
                if(editor == null){
                    throw new ConvertException("PropertyEditor not found : " + propType);
                }
                try{
                    editor.setAsText(str);
                    return editor.getValue();
                }catch(Exception e){
                    throw new ConvertException(e);
                }
            }else{
                throw new ConvertException("Not number type : " + propType);
            }
        }
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
    
    protected class PropertyAccessType{
        
        /**
         * Java�I�u�W�F�N�g��JSON�ϊ�����Java�I�u�W�F�N�g��public�t�B�[���h�݂̂�ΏۂƂ��邩�ǂ����̃t���O�B<p>
         * �f�t�H���g�́Afalse��public�t�B�[���h�݂̂�Ώۂɂ͂��Ȃ��B<br>
         */
        public boolean isFieldOnly = false;
        
        /**
         * Java�I�u�W�F�N�g��JSON�ϊ�����Java�I�u�W�F�N�g��public��getter�݂̂�ΏۂƂ��邩�ǂ����̃t���O�B<p>
         * �f�t�H���g�́Atrue��public��getter�݂̂�Ώۂɂ���B<br>
         */
        public boolean isAccessorOnly = true;
        
        /**
         * �o�͂��Ȃ��v���p�e�B���̏W���B<p>
         */
        public Set disabledPropertyNames;
        
        /**
         * �o�͂���v���p�e�B���̏W���B<p>
         */
        public Set enabledPropertyNames;
    }
}