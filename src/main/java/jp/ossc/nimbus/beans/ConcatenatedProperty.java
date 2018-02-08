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
package jp.ossc.nimbus.beans;

import java.lang.reflect.*;

/**
 * �A���v���p�e�B�B<p>
 * �����̃v���p�e�B��A�����ăA�N�Z�X���邽�߂�{@link Property}�B<br>
 * �ȉ��̂悤�ȃR�[�h������B<br>
 * <pre>
 *   String dateStr = obj.getDate();
 *   String timeStr = obj.getTime();
 *   String dateTime = dateStr + timeStr;
 * </pre>
 * �A�����g�����ŁA���̃R�[�h��<br>
 * <pre>
 *   ConcatenationProperty prop = new ConcatenationProperty(new SimpleProperty("date"), new SimpleProperty("time"));
 *   Object propValue = prop.getProperty(obj);
 * </pre>
 * �Ƃ����R�[�h�ɒu�������鎖���ł���B<br>
 * ���̃R�[�h�́A�璷�ɂȂ��Ă��邪�A�ΏۂƂȂ�Bean�̌^�⃁�\�b�h���^�C�v�Z�[�t�ɏ����Ȃ����I�ȃR�[�h�ɂȂ��Ă���B<br>
 * <p>
 * ���̘A���v���p�e�B�ł́A�ȉ��̂悤��Bean�̃v���p�e�B�ɑ΂���A�N�Z�X���@���p�ӂ���Ă���B<br>
 * <table border="1">
 *   <tr bgcolor="#CCCCFF"><th rowspan="3">�A�N�Z�X���@</th><th>Java�\��</th><th rowspan="3">�v���p�e�B������\��</th></tr>
 *   <tr bgcolor="#CCCCFF"><th>�v���p�e�B�擾</th><td>�A��</td><td>bean.getDate() + bean.getTime()</td><td rowspan="2">date+time</td></tr>
 * </table>
 * �A���Ώۂ̂Q�̃v���p�e�B�́A{@link Property}�C���^�t�F�[�X���������Ă���Ηǂ��A�P���v���p�e�B�A�C���f�b�N�X�v���p�e�B�A�}�b�v�v���p�e�B�A�l�X�g�v���p�e�B�̂�����ł��ǂ��B<br>
 *
 * @author M.Takata
 */
public class ConcatenatedProperty implements Property, java.io.Serializable{
    
    private static final long serialVersionUID = -7626632324425762277L;
    
    private static final String MSG_00001 = "Illegal ConcatenatedProperty : ";
    private static final String MSG_00002 = "Arguments is null.";
    
    /**
     * �A�������v���p�e�B�B<p>
     */
    protected Property thisProperty;
    
    /**
     * �A������v���p�e�B�B<p>
     */
    protected Property concatProperty;
    
    /**
     * null�Q�Ƃ̃v���p�e�B���擾�g�p�Ƃ����ꍇ�ɁA��O��throw���邩�ǂ����̃t���O�B<p>
     * true�̏ꍇ�́A��O��throw���Ȃ��B�f�t�H���g�́Afalse�B<br>
     */
    protected boolean isIgnoreNullProperty;
    
    /**
     * ��̘A���v���p�e�B�𐶐�����B<p>
     */
    public ConcatenatedProperty(){
    }
    
    /**
     * �w�肵���Q�̃v���p�e�B���A�������A���v���p�e�B�𐶐�����B<p>
     *
     * @param prop �A�������v���p�e�B
     * @param concatProp �A������v���p�e�B
     * @exception IllegalArgumentException ������null���w�肵���ꍇ
     */
    public ConcatenatedProperty(Property prop, Property concatProp)
     throws IllegalArgumentException{
        if(prop == null || concatProp == null){
            throw new IllegalArgumentException(MSG_00002);
        }
        thisProperty = prop;
        concatProperty = concatProp;
    }
    
    /**
     * �A�������v���p�e�B��ݒ肷��B<p>
     * 
     * @param prop �A�������v���p�e�B
     * @exception IllegalArgumentException ������null���w�肵���ꍇ
     */
    public void setThisProperty(Property prop) throws IllegalArgumentException{
        if(prop == null){
            throw new IllegalArgumentException(MSG_00002);
        }
        thisProperty = prop;
    }
    
    /**
     * �A�������ŏ��̃v���p�e�B���擾����B<p>
     * 
     * @return �A�������ŏ��̃v���p�e�B
     */
    public Property getFirstThisProperty(){
        return getFirstThisProperty(thisProperty);
    }
    
    private Property getFirstThisProperty(Property prop){
        if(prop instanceof ConcatenatedProperty){
            return getFirstThisProperty(
                ((ConcatenatedProperty)prop).getThisProperty()
            );
        }
        return prop;
    }
    
    /**
     * �A�������v���p�e�B���擾����B<p>
     * 
     * @return �A�������v���p�e�B
     */
    public Property getThisProperty(){
        return thisProperty;
    }
    
    /**
     * �A������v���p�e�B��ݒ肷��B<p>
     * 
     * @param concatProp �A������v���p�e�B
     * @exception IllegalArgumentException ������null���w�肵���ꍇ
     */
    public void setConcatenatedProperty(Property concatProp)
     throws IllegalArgumentException{
        if(concatProp == null){
            throw new IllegalArgumentException(MSG_00002);
        }
        concatProperty = concatProp;
    }
    
    /**
     * �A������v���p�e�B���擾����B<p>
     * 
     * @return �A������v���p�e�B
     */
    public Property getConcatenatedProperty(){
        return concatProperty;
    }
    
    /**
     * �w�肵���v���p�e�B���������͂���B<p>
     * �����Ŏw��\�ȕ�����́A<br>
     * &nbsp;�A�������v���p�e�B��.�A������v���p�e�B��<br>
     * �ł���B<br>
     *
     * @param prop �v���p�e�B������
     * @exception IllegalArgumentException �w�肳�ꂽ�v���p�e�B����������̃v���p�e�B�I�u�W�F�N�g����͂ł��Ȃ��ꍇ
     */
    public void parse(String prop) throws IllegalArgumentException{
        final int index = prop.indexOf('+');
        if(index == -1 || index == 0 || index == prop.length() - 1){
            throw new IllegalArgumentException(MSG_00001 + prop);
        }
        thisProperty = PropertyFactory.createProperty(prop.substring(0, index).trim());
        concatProperty = PropertyFactory.createProperty(
            prop.substring(index + 1).trim()
        );
    }
    
    public Class getPropertyType(Class clazz) throws NoSuchPropertyException{
        return String.class;
    }
    
    public Class getPropertyType(Object obj)
     throws NoSuchPropertyException, InvocationTargetException{
        return String.class;
    }
    
    public boolean isReadable(Class clazz){
        if(thisProperty instanceof SimpleProperty){
            String propName = thisProperty.getPropertyName();
            if(propName.length() <= 1 || (propName.charAt(0) != '"' && propName.charAt(0) != '\'')){
                if(!thisProperty.isReadable(clazz)){
                    return false;
                }
            }
        }else{
            if(!thisProperty.isReadable(clazz)){
                return false;
            }
        }
        if(concatProperty instanceof SimpleProperty){
            String propName = concatProperty.getPropertyName();
            if(propName.length() <= 1 || (propName.charAt(0) != '"' && propName.charAt(0) != '\'')){
                if(!concatProperty.isReadable(clazz)){
                    return false;
                }
            }
        }else{
            if(!concatProperty.isReadable(clazz)){
                return false;
            }
        }
        return true;
    }
    
    public boolean isReadable(Object obj){
        if(thisProperty instanceof SimpleProperty){
            String propName = thisProperty.getPropertyName();
            if(propName.length() <= 1 || (propName.charAt(0) != '"' && propName.charAt(0) != '\'')){
                if(!thisProperty.isReadable(obj)){
                    return false;
                }
            }
        }else{
            if(!thisProperty.isReadable(obj)){
                return false;
            }
        }
        if(concatProperty instanceof SimpleProperty){
            String propName = concatProperty.getPropertyName();
            if(propName.length() <= 1 || (propName.charAt(0) != '"' && propName.charAt(0) != '\'')){
                if(!concatProperty.isReadable(obj)){
                    return false;
                }
            }
        }else{
            if(!concatProperty.isReadable(obj)){
                return false;
            }
        }
        return true;
    }
    
    public boolean isWritable(Object obj, Class clazz){
        return false;
    }
    
    public boolean isWritable(Class targetClass, Class clazz){
        return false;
    }
    
    /**
     * �w�肵���I�u�W�F�N�g����A���̃v���p�e�B���\���v���p�e�B�l���擾����B<p>
     *
     * @param obj �ΏۂƂȂ�Bean
     * @return �v���p�e�B�l
     * @exception NullNestPropertyException �A������v���p�e�B���Anull�̏ꍇ
     * @exception NoSuchPropertyException �w�肳�ꂽBean���A���̃v���p�e�B���\���A�N�Z�X�\�ȃv���p�e�B�������Ă��Ȃ��ꍇ
     * @exception InvocationTargetException �w�肳�ꂽBean�̃A�N�Z�T���Ăяo�������ʁA��O��throw���ꂽ�ꍇ
     */
    public Object getProperty(Object obj)
     throws NoSuchPropertyException, InvocationTargetException{
        StringBuffer buf = new StringBuffer();
        Object thisObj = null;
        if(thisProperty instanceof SimpleProperty){
            String propName = thisProperty.getPropertyName();
            if(propName.length() > 1 && (propName.charAt(0) == '"' || propName.charAt(0) == '\'')){
                thisObj = propName.substring(1, propName.length() - 1);
            }else{
                thisObj = thisProperty.getProperty(obj);
            }
        }else{
            thisObj = thisProperty.getProperty(obj);
        }
        if(thisObj != null){
            buf.append(thisObj);
        }
        Object concatObj = null;
        if(concatProperty instanceof SimpleProperty){
            String propName = concatProperty.getPropertyName();
            if(propName.length() > 1 && (propName.charAt(0) == '"' || propName.charAt(0) == '\'')){
                concatObj = propName.substring(1, propName.length() - 1);
            }else{
                concatObj = concatProperty.getProperty(obj);
            }
        }else{
            concatObj = concatProperty.getProperty(obj);
        }
        if(concatObj != null){
            buf.append(concatObj);
        }
        return thisObj == null && concatObj == null && isIgnoreNullProperty ? null : buf.toString();
    }
    
    /**
     * �T�|�[�g���Ȃ��B<p>
     *
     * @param obj �ΏۂƂȂ�Bean
     * @param value �ݒ肷��v���p�e�B�l
     * @exception NullNestPropertyException �A������v���p�e�B���Anull�̏ꍇ
     * @exception NoSuchPropertyException �w�肳�ꂽBean���A���̃v���p�e�B���\���A�N�Z�X�\�ȃv���p�e�B�������Ă��Ȃ��ꍇ
     * @exception InvocationTargetException �w�肳�ꂽBean�̃A�N�Z�T���Ăяo�������ʁA��O��throw���ꂽ�ꍇ
     */
    public void setProperty(Object obj, Object value)
     throws NoSuchPropertyException, InvocationTargetException{
        throw new UnsupportedOperationException();
    }
    
    /**
     * �T�|�[�g���Ȃ��B<p>
     *
     * @param obj �ΏۂƂȂ�Bean
     * @param type �v���p�e�B�̌^
     * @param value �ݒ肷��v���p�e�B�l
     * @exception NullNestPropertyException �A������v���p�e�B���Anull�̏ꍇ
     * @exception NoSuchPropertyException �w�肳�ꂽBean���A���̃v���p�e�B���\���A�N�Z�X�\�ȃv���p�e�B�������Ă��Ȃ��ꍇ
     * @exception InvocationTargetException �w�肳�ꂽBean�̃A�N�Z�T���Ăяo�������ʁA��O��throw���ꂽ�ꍇ
     */
    public void setProperty(Object obj, Class type, Object value)
     throws NoSuchPropertyException, InvocationTargetException{
        throw new UnsupportedOperationException();
    }
    
    /**
     * ���̃v���p�e�B���\���v���p�e�B�����擾����B<p>
     *
     * @return �A�������v���p�e�B��.�A������v���p�e�B��
     */
    public String getPropertyName(){
        return thisProperty != null && concatProperty != null
            ? (thisProperty.getPropertyName() + '+' + concatProperty.getPropertyName()) : null;
    }
    
    public void setIgnoreNullProperty(boolean isIgnore){
        isIgnoreNullProperty = isIgnore;
        if(thisProperty != null){
            thisProperty.setIgnoreNullProperty(isIgnoreNullProperty);
        }
        if(concatProperty != null){
            concatProperty.setIgnoreNullProperty(isIgnoreNullProperty);
        }
    }
    
    public boolean isIgnoreNullProperty(){
        return isIgnoreNullProperty;
    }
    
    /**
     * ���̘A���v���p�e�B�̕�����\�����擾����B<p>
     *
     * @return ConcatenatedProperty{�v���p�e�B��.�A������v���p�e�B��}
     */
    public String toString(){
        return "ConcatenatedProperty{"
            + (thisProperty == null ? "null" : thisProperty.toString())
            + '+' + (concatProperty == null ? "null" : concatProperty.toString()) + '}';
    }
    
    /**
     * ���̃I�u�W�F�N�g�Ƒ��̃I�u�W�F�N�g�����������ǂ����������܂��B <p>
     *
     * @param obj ��r�Ώۂ̃I�u�W�F�N�g
     * @return �����Ɏw�肳�ꂽ�I�u�W�F�N�g�Ƃ��̃I�u�W�F�N�g���������ꍇ�� true�A�����łȂ��ꍇ�� false�B
     */
    public boolean equals(Object obj){
        if(obj == null){
            return false;
        }
        if(!(obj instanceof ConcatenatedProperty)){
            return false;
        }
        final ConcatenatedProperty comp = (ConcatenatedProperty)obj;
        if(thisProperty == null && comp.thisProperty != null
            || thisProperty != null && comp.thisProperty == null){
            return false;
        }else if(thisProperty != null && comp.thisProperty != null
            && !thisProperty.equals(comp.thisProperty)){
            return false;
        }
        if(concatProperty == null && comp.concatProperty == null){
            return true;
        }else if(concatProperty == null){
            return false;
        }else{
            return concatProperty.equals(comp.concatProperty);
        }
    }
    
    /**
     * �n�b�V���l���擾����B<p>
     *
     * @return �n�b�V���l
     */
    public int hashCode(){
        return (thisProperty == null ? 0 : (thisProperty.hashCode() * 2)) + (concatProperty == null ? 0 : (concatProperty.hashCode() * 3));
    }
    
    /**
     * ���̃I�u�W�F�N�g�Ǝw�肳�ꂽ�I�u�W�F�N�g�̏������r����B<p>
     *
     * @param obj ��r�Ώۂ̃I�u�W�F�N�g
     * @return ���̃I�u�W�F�N�g���w�肳�ꂽ�I�u�W�F�N�g��菬�����ꍇ�͕��̐����A�������ꍇ�̓[���A�傫���ꍇ�͐��̐���
     */
    public int compareTo(Object obj){
        if(obj == null){
            return 1;
        }
        if(!(obj instanceof ConcatenatedProperty)){
            return 1;
        }
        final ConcatenatedProperty comp = (ConcatenatedProperty)obj;
        if(thisProperty == null && comp.thisProperty != null){
            return -1;
        }else if(thisProperty != null && comp.thisProperty == null){
            return 1;
        }else if(thisProperty != null && comp.thisProperty != null){
            if(thisProperty instanceof Comparable){
                final int val = ((Comparable)thisProperty).compareTo(comp.thisProperty);
                if(val != 0){
                    return val;
                }
            }else{
                return -1;
            }
        }
        if(concatProperty == null && comp.concatProperty == null){
            return 0;
        }else if(concatProperty == null){
            return -1;
        }else{
            if(concatProperty instanceof Comparable){
                return ((Comparable)concatProperty).compareTo(comp.concatProperty);
            }else{
                return -1;
            }
        }
    }
}
