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
 * ネストプロパティ。<p>
 * ネストしたプロパティにアクセスするための{@link Property}。<br>
 * 以下のようなプロパティにアクセスするタイプセーフなコードがある。<br>
 * <pre>
 *   Fuga propValue = obj.getHoge().getFuga();
 *   obj.getHoge().setFuga(propValue);
 * </pre>
 * ネストプロパティを使う事で、このコードを<br>
 * <pre>
 *   NestedPropery prop = new NestedPropery(new SimpleProperty("hoge"), new SimpleProperty("fuga"));
 *   Object propValue = prop.getProperty(obj);
 *   prop.setProperty(propValue);
 * </pre>
 * というコードに置き換える事ができる。<br>
 * このコードは、冗長になっているが、対象となるBeanの型やメソッドをタイプセーフに書かない動的なコードになっている。<br>
 * <p>
 * このネストプロパティでは、以下のようなBeanのプロパティに対するアクセス方法が用意されている。<br>
 * <table border="1">
 *   <tr bgcolor="#CCCCFF"><th rowspan="3">アクセス方法</th><th>Java表現</th><th rowspan="3">プロパティ文字列表現</th></tr>
 *   <tr bgcolor="#CCCCFF"><th>プロパティ取得</th></tr>
 *   <tr bgcolor="#CCCCFF"><th>プロパティ設定</th></tr>
 *   <tr><td rowspan="2">ネストプロパティ</td><td>bean.getHoge().getFuga()</td><td rowspan="2">hoge.fuga</td></tr>
 *   <tr><td>bean.getHoge().setFuga(value)</td></tr>
 * </table>
 * ネスト対象の２つのプロパティは、{@link Property}インタフェースを実装していれば良く、単純プロパティ、インデックスプロパティ、マッププロパティ、ネストプロパティのいずれでも良い。<br>
 *
 * @author M.Takata
 */
public class NestedProperty implements Property, java.io.Serializable{
    
    private static final long serialVersionUID = -8976001636216478152L;
    
    private static final String MSG_00001 = "Illegal NestedProperty : ";
    private static final String MSG_00002 = "Arguments is null.";
    
    /**
     * ネストされるプロパティ。<p>
     */
    protected Property thisProperty;
    
    /**
     * ネストするプロパティ。<p>
     */
    protected Property nestProperty;
    
    /**
     * null参照のプロパティを取得使用とした場合に、例外をthrowするかどうかのフラグ。<p>
     * trueの場合は、例外をthrowしない。デフォルトは、false。<br>
     */
    protected boolean isIgnoreNullProperty;
    
    /**
     * 空のネストプロパティを生成する。<p>
     */
    public NestedProperty(){
    }
    
    /**
     * 指定した２つのプロパティがネストしたネストプロパティを生成する。<p>
     *
     * @param prop ネストされるプロパティ
     * @param nestProp ネストするプロパティ
     * @exception IllegalArgumentException 引数にnullを指定した場合
     */
    public NestedProperty(Property prop, Property nestProp)
     throws IllegalArgumentException{
        if(prop == null || nestProp == null){
            throw new IllegalArgumentException(MSG_00002);
        }
        thisProperty = prop;
        nestProperty = nestProp;
    }
    
    /**
     * ネストされるプロパティを設定する。<p>
     * 
     * @param prop ネストされるプロパティ
     * @exception IllegalArgumentException 引数にnullを指定した場合
     */
    public void setThisProperty(Property prop) throws IllegalArgumentException{
        if(prop == null){
            throw new IllegalArgumentException(MSG_00002);
        }
        thisProperty = prop;
    }
    
    /**
     * ネストされる最初のプロパティを取得する。<p>
     * 
     * @return ネストされる最初のプロパティ
     */
    public Property getFirstThisProperty(){
        return getFirstThisProperty(thisProperty);
    }
    
    private Property getFirstThisProperty(Property prop){
        if(prop instanceof NestedProperty){
            return getFirstThisProperty(
                ((NestedProperty)prop).getThisProperty()
            );
        }
        return prop;
    }
    
    /**
     * ネストされるプロパティを取得する。<p>
     * 
     * @return ネストされるプロパティ
     */
    public Property getThisProperty(){
        return thisProperty;
    }
    
    /**
     * ネストするプロパティを設定する。<p>
     * 
     * @param nestProp ネストするプロパティ
     * @exception IllegalArgumentException 引数にnullを指定した場合
     */
    public void setNestedProperty(Property nestProp)
     throws IllegalArgumentException{
        if(nestProp == null){
            throw new IllegalArgumentException(MSG_00002);
        }
        nestProperty = nestProp;
    }
    
    /**
     * ネストするプロパティを取得する。<p>
     * 
     * @return ネストするプロパティ
     */
    public Property getNestedProperty(){
        return nestProperty;
    }
    
    /**
     * 指定したプロパティ文字列を解析する。<p>
     * ここで指定可能な文字列は、<br>
     * &nbsp;ネストされるプロパティ名.ネストするプロパティ名<br>
     * である。<br>
     *
     * @param prop プロパティ文字列
     * @exception IllegalArgumentException 指定されたプロパティ文字列をこのプロパティオブジェクトが解析できない場合
     */
    public void parse(String prop) throws IllegalArgumentException{
        final int index = prop.indexOf('.');
        if(index == -1 || index == 0 || index == prop.length() - 1){
            throw new IllegalArgumentException(MSG_00001 + prop);
        }
        thisProperty = PropertyFactory.createProperty(prop.substring(0, index));
        nestProperty = PropertyFactory.createProperty(
            prop.substring(index + 1)
        );
    }
    
    public Class getPropertyType(Object obj)
     throws NoSuchPropertyException, InvocationTargetException{
        final Object thisObj = thisProperty.getProperty(obj);
        if(thisObj == null){
            Class thisPropType = thisProperty.getPropertyType(obj.getClass());
            return nestProperty.getPropertyType(thisPropType);
        }else{
            return nestProperty.getPropertyType(thisObj);
        }
    }
    
    public Type getPropertyGenericType(Object obj)
     throws NoSuchPropertyException, InvocationTargetException{
        final Object thisObj = thisProperty.getProperty(obj);
        if(thisObj == null){
            Class thisPropType = thisProperty.getPropertyType(obj.getClass());
            return nestProperty.getPropertyGenericType(thisPropType);
        }else{
            return nestProperty.getPropertyGenericType(thisObj);
        }
    }
    
    public Class getPropertyType(Class clazz) throws NoSuchPropertyException{
        return nestProperty.getPropertyType(thisProperty.getPropertyType(clazz));
    }
    
    public Type getPropertyGenericType(Class clazz) throws NoSuchPropertyException{
        Class thisPropType = thisProperty.getPropertyType(clazz);
        return nestProperty.getPropertyGenericType(thisPropType);
    }
    
    public boolean isReadable(Class clazz){
        try{
            Class thisPropType = thisProperty.getPropertyType(clazz);
            return nestProperty.isReadable(thisPropType);
        }catch(NoSuchPropertyException e){
            return false;
        }
    }
    
    public boolean isReadable(Object obj){
        Object thisObj = null;
        try{
            thisObj = thisProperty.getProperty(obj);
        }catch(NoSuchPropertyException e){
            return false;
        }catch(InvocationTargetException e){
        }
        if(thisObj == null){
            try{
                Class thisPropType = thisProperty.getPropertyType(obj);
                return nestProperty.isReadable(thisPropType);
            }catch(NoSuchPropertyException e){
                return false;
            }catch(InvocationTargetException e){
                return isReadable(obj.getClass());
            }
        }else{
            return nestProperty.isReadable(thisObj);
        }
    }
    
    public boolean isWritable(Object obj, Class clazz){
        Object thisObj = null;
        try{
            thisObj = thisProperty.getProperty(obj);
        }catch(NoSuchPropertyException e){
            return false;
        }catch(InvocationTargetException e){
            return false;
        }
        if(thisObj == null){
            return false;
        }else{
            return nestProperty.isWritable(thisObj, clazz);
        }
    }
    
    public boolean isWritable(Class targetClass, Class clazz){
        try{
            Class thisPropType = thisProperty.getPropertyType(clazz);
            return nestProperty.isWritable(thisPropType, clazz);
        }catch(NoSuchPropertyException e){
            return false;
        }
    }
    
    /**
     * 指定したオブジェクトから、このプロパティが表すプロパティ値を取得する。<p>
     *
     * @param obj 対象となるBean
     * @return プロパティ値
     * @exception NullNestPropertyException ネストするプロパティが、nullの場合
     * @exception NoSuchPropertyException 指定されたBeanが、このプロパティが表すアクセス可能なプロパティを持っていない場合
     * @exception InvocationTargetException 指定されたBeanのアクセサを呼び出した結果、例外がthrowされた場合
     */
    public Object getProperty(Object obj)
     throws NoSuchPropertyException, InvocationTargetException{
        final Object thisObj = thisProperty.getProperty(obj);
        if(thisObj == null){
            if(isIgnoreNullProperty){
                return null;
            }else{
                throw new NullNestPropertyException(
                    obj.getClass(),
                    thisProperty.getPropertyName()
                );
            }
        }else{
            return nestProperty.getProperty(thisObj);
        }
    }
    
    /**
     * 指定したオブジェクトに、このプロパティが表すプロパティ値を設定する。<p>
     *
     * @param obj 対象となるBean
     * @param value 設定するプロパティ値
     * @exception NullNestPropertyException ネストするプロパティが、nullの場合
     * @exception NoSuchPropertyException 指定されたBeanが、このプロパティが表すアクセス可能なプロパティを持っていない場合
     * @exception InvocationTargetException 指定されたBeanのアクセサを呼び出した結果、例外がthrowされた場合
     */
    public void setProperty(Object obj, Object value)
     throws NoSuchPropertyException, InvocationTargetException{
        setProperty(obj, null, value);
    }
    
    /**
     * 指定したオブジェクトに、このプロパティが表すプロパティ値を設定する。<p>
     *
     * @param obj 対象となるBean
     * @param type プロパティの型
     * @param value 設定するプロパティ値
     * @exception NullNestPropertyException ネストするプロパティが、nullの場合
     * @exception NoSuchPropertyException 指定されたBeanが、このプロパティが表すアクセス可能なプロパティを持っていない場合
     * @exception InvocationTargetException 指定されたBeanのアクセサを呼び出した結果、例外がthrowされた場合
     */
    public void setProperty(Object obj, Class type, Object value)
     throws NoSuchPropertyException, InvocationTargetException{
        final Object thisObj = thisProperty.getProperty(obj);
        if(thisObj == null){
            throw new NullNestPropertyException(
                obj.getClass(),
                thisProperty.getPropertyName()
            );
        }else{
            nestProperty.setProperty(thisObj, type, value);
        }
    }
    
    /**
     * このプロパティが表すプロパティ名を取得する。<p>
     *
     * @return ネストされるプロパティ名.ネストするプロパティ名
     */
    public String getPropertyName(){
        return thisProperty.getPropertyName()
            + '.' + nestProperty.getPropertyName();
    }
    
    public void setIgnoreNullProperty(boolean isIgnore){
        isIgnoreNullProperty = isIgnore;
        if(thisProperty != null){
            thisProperty.setIgnoreNullProperty(isIgnoreNullProperty);
        }
        if(nestProperty != null){
            nestProperty.setIgnoreNullProperty(isIgnoreNullProperty);
        }
    }
    
    public boolean isIgnoreNullProperty(){
        return isIgnoreNullProperty;
    }
    
    /**
     * このネストプロパティの文字列表現を取得する。<p>
     *
     * @return NestedProperty{プロパティ名.ネストするプロパティ名}
     */
    public String toString(){
        return "NestedProperty{"
            + (thisProperty == null ? "null" : thisProperty.toString())
            + '.' + (nestProperty == null ? "null" : nestProperty.toString()) + '}';
    }
    
    /**
     * このオブジェクトと他のオブジェクトが等しいかどうかを示します。 <p>
     *
     * @param obj 比較対象のオブジェクト
     * @return 引数に指定されたオブジェクトとこのオブジェクトが等しい場合は true、そうでない場合は false。
     */
    public boolean equals(Object obj){
        if(obj == null){
            return false;
        }
        if(!(obj instanceof NestedProperty)){
            return false;
        }
        final NestedProperty comp = (NestedProperty)obj;
        if(thisProperty == null && comp.thisProperty != null
            || thisProperty != null && comp.thisProperty == null){
            return false;
        }else if(thisProperty != null && comp.thisProperty != null
            && !thisProperty.equals(comp.thisProperty)){
            return false;
        }
        if(nestProperty == null && comp.nestProperty == null){
            return true;
        }else if(nestProperty == null){
            return false;
        }else{
            return nestProperty.equals(comp.nestProperty);
        }
    }
    
    /**
     * ハッシュ値を取得する。<p>
     *
     * @return ハッシュ値
     */
    public int hashCode(){
        return (thisProperty == null ? 0 : (thisProperty.hashCode() * 2)) + (nestProperty == null ? 0 : (nestProperty.hashCode() * 3));
    }
    
    /**
     * このオブジェクトと指定されたオブジェクトの順序を比較する。<p>
     *
     * @param obj 比較対象のオブジェクト
     * @return このオブジェクトが指定されたオブジェクトより小さい場合は負の整数、等しい場合はゼロ、大きい場合は正の整数
     */
    public int compareTo(Object obj){
        if(obj == null){
            return 1;
        }
        if(!(obj instanceof NestedProperty)){
            return 1;
        }
        final NestedProperty comp = (NestedProperty)obj;
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
        if(nestProperty == null && comp.nestProperty == null){
            return 0;
        }else if(nestProperty == null){
            return -1;
        }else{
            if(nestProperty instanceof Comparable){
                return ((Comparable)nestProperty).compareTo(comp.nestProperty);
            }else{
                return -1;
            }
        }
    }
}
