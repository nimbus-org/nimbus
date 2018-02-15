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
 * ORプロパティ。<p>
 * ２つのプロパティからNullでない方のプロパティにアクセスするための{@link Property}。<br>
 * 以下のようなプロパティにアクセスするタイプセーフなコードがある。<br>
 * <pre>
 *   Object propValue = bean.getHoge() != null ? bean.getHoge() : bean.getFuga();
 * </pre>
 * ORプロパティを使う事で、このコードを<br>
 * <pre>
 *   OrPropery prop = new OrPropery(new SimpleProperty("hoge"), new SimpleProperty("fuga"));
 *   Object propValue = prop.getProperty(obj);
 * </pre>
 * というコードに置き換える事ができる。<br>
 * このコードは、冗長になっているが、対象となるBeanの型やメソッドをタイプセーフに書かない動的なコードになっている。<br>
 * <p>
 * このネストプロパティでは、以下のようなBeanのプロパティに対するアクセス方法が用意されている。<br>
 * <table border="1">
 *   <tr bgcolor="#CCCCFF"><th rowspan="2">アクセス方法</th><th>Java表現</th><th rowspan="2">プロパティ文字列表現</th></tr>
 *   <tr bgcolor="#CCCCFF"><th>プロパティ取得</th></tr>
 *   <tr><td>ORプロパティ</td><td>bean.getHoge() != null ? bean.getHoge() : bean.getFuga()</td><td>hoge|fuga</td></tr>
 * </table>
 * OR対象の２つのプロパティは、{@link Property}インタフェースを実装していれば良い。<br>
 *
 * @author M.Takata
 */
public class OrProperty implements Property, java.io.Serializable{
    
    private static final long serialVersionUID = 4557829281768367883L;
    private static final String MSG_00001 = "Illegal OrProperty : ";
    private static final String MSG_00002 = "Arguments is null.";
    
    /**
     * 第一プロパティ。<p>
     */
    protected Property firstProperty;
    
    /**
     * 第二プロパティ。<p>
     */
    protected Property secondProperty;
    
    /**
     * null参照のプロパティを取得使用とした場合に、例外をthrowするかどうかのフラグ。<p>
     * trueの場合は、例外をthrowしない。デフォルトは、false。<br>
     */
    protected boolean isIgnoreNullProperty;
    
    /**
     * 空のORプロパティを生成する。<p>
     */
    public OrProperty(){
    }
    
    /**
     * 指定した２つのプロパティのORプロパティを生成する。<p>
     *
     * @param first 第一プロパティ
     * @param second 第二プロパティ
     * @exception IllegalArgumentException 引数にnullを指定した場合
     */
    public OrProperty(Property first, Property second)
     throws IllegalArgumentException{
        if(first == null || second == null){
            throw new IllegalArgumentException(MSG_00002);
        }
        firstProperty = first;
        secondProperty = second;
    }
    
    /**
     * 第一プロパティを設定する。<p>
     * 
     * @param prop 第一プロパティ
     * @exception IllegalArgumentException 引数にnullを指定した場合
     */
    public void setFirstProperty(Property prop) throws IllegalArgumentException{
        if(prop == null){
            throw new IllegalArgumentException(MSG_00002);
        }
        firstProperty = prop;
    }
    
    /**
     * 第一プロパティを取得する。<p>
     * 
     * @return 第一プロパティ
     */
    public Property getFirstProperty(){
        return firstProperty;
    }
    
    /**
     * ネストした第一プロパティを取得する。<p>
     * 
     * @return ネストした第一プロパティ
     */
    public Property getNestedFirstProperty(){
        if(firstProperty instanceof OrProperty){
            return ((OrProperty)firstProperty).getNestedFirstProperty();
        }else{
            return firstProperty;
        }
    }
    
    /**
     * 第二プロパティを設定する。<p>
     * 
     * @param prop 第二プロパティ
     * @exception IllegalArgumentException 引数にnullを指定した場合
     */
    public void setSecondProperty(Property prop)
     throws IllegalArgumentException{
        if(prop == null){
            throw new IllegalArgumentException(MSG_00002);
        }
        secondProperty = prop;
    }
    
    /**
     * 第二プロパティを取得する。<p>
     * 
     * @return 第二プロパティ
     */
    public Property getSecondProperty(){
        return secondProperty;
    }
    
    /**
     * 指定したプロパティ文字列を解析する。<p>
     * ここで指定可能な文字列は、<br>
     * &nbsp;第一プロパティ名|第二プロパティ名<br>
     * である。<br>
     *
     * @param prop プロパティ文字列
     * @exception IllegalArgumentException 指定されたプロパティ文字列をこのプロパティオブジェクトが解析できない場合
     */
    public void parse(String prop) throws IllegalArgumentException{
        final int index = prop.indexOf('|');
        if(index == -1 || index == 0 || index == prop.length() - 1){
            throw new IllegalArgumentException(MSG_00001 + prop);
        }
        firstProperty = PropertyFactory.createProperty(prop.substring(0, index).trim());
        secondProperty = PropertyFactory.createProperty(
            prop.substring(index + 1).trim()
        );
    }
    
    public Class getPropertyType(Class clazz) throws NoSuchPropertyException{
        return firstProperty.getPropertyType(clazz);
    }
    
    public Class getPropertyType(Object obj)
     throws NoSuchPropertyException, InvocationTargetException{
        Object firstObj = null;
        try{
            firstObj = firstProperty.getProperty(obj);
        }catch(NoSuchPropertyException e){}
        if(firstObj != null){
            return firstProperty.getPropertyType(obj);
        }else{
            return secondProperty.getPropertyType(obj);
        }
    }
    
    public boolean isReadable(Class clazz){
        return firstProperty.isReadable(clazz) || secondProperty.isReadable(clazz);
    }
    
    public boolean isReadable(Object obj){
        return firstProperty.isReadable(obj) || secondProperty.isReadable(obj);
    }
    
    public boolean isWritable(Object obj, Class clazz){
        return false;
    }
    
    public boolean isWritable(Class targetClass, Class clazz){
        return false;
    }
    
    /**
     * 指定したオブジェクトから、このプロパティが表すプロパティ値を取得する。<p>
     *
     * @param obj 対象となるBean
     * @return プロパティ値
     * @exception NoSuchPropertyException 指定されたBeanが、このプロパティが表すアクセス可能なプロパティを持っていない場合
     * @exception InvocationTargetException 指定されたBeanのアクセサを呼び出した結果、例外がthrowされた場合
     */
    public Object getProperty(Object obj)
     throws NoSuchPropertyException, InvocationTargetException{
        Object firstObj = null;
        try{
            firstObj = firstProperty.getProperty(obj);
        }catch(NoSuchPropertyException e){}
        if(firstObj == null){
            return secondProperty.getProperty(obj);
        }else{
            return firstObj;
        }
    }
    
    /**
     * サポートしない。<p>
     *
     * @param obj 対象となるBean
     * @param value 設定するプロパティ値
     * @exception NoSuchPropertyException 指定されたBeanが、このプロパティが表すアクセス可能なプロパティを持っていない場合
     * @exception InvocationTargetException 指定されたBeanのアクセサを呼び出した結果、例外がthrowされた場合
     */
    public void setProperty(Object obj, Object value)
     throws NoSuchPropertyException, InvocationTargetException{
        throw new UnsupportedOperationException();
    }
    
    /**
     * サポートしない。<p>
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
        throw new UnsupportedOperationException();
    }
    
    /**
     * このプロパティが表すプロパティ名を取得する。<p>
     *
     * @return 第一プロパティ名|第二プロパティ名
     */
    public String getPropertyName(){
        return firstProperty != null && secondProperty != null
            ? (firstProperty.getPropertyName() + '|' + secondProperty.getPropertyName()) : null;
    }
    
    public void setIgnoreNullProperty(boolean isIgnore){
        isIgnoreNullProperty = isIgnore;
        if(firstProperty != null){
            firstProperty.setIgnoreNullProperty(isIgnoreNullProperty);
        }
        if(secondProperty != null){
            secondProperty.setIgnoreNullProperty(isIgnoreNullProperty);
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
        return "OrProperty{"
            + (firstProperty == null ? "null" : firstProperty.toString())
            + '|' + (secondProperty == null ? "null" : secondProperty.toString()) + '}';
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
        if(!(obj instanceof OrProperty)){
            return false;
        }
        final OrProperty comp = (OrProperty)obj;
        if(firstProperty == null && comp.firstProperty != null
            || firstProperty != null && comp.firstProperty == null){
            return false;
        }else if(firstProperty != null && comp.firstProperty != null
            && !firstProperty.equals(comp.firstProperty)){
            return false;
        }
        if(secondProperty == null && comp.secondProperty == null){
            return true;
        }else if(secondProperty == null){
            return false;
        }else{
            return secondProperty.equals(comp.secondProperty);
        }
    }
    
    /**
     * ハッシュ値を取得する。<p>
     *
     * @return ハッシュ値
     */
    public int hashCode(){
        return (firstProperty == null ? 0 : (firstProperty.hashCode() * 2)) + (secondProperty == null ? 0 : (secondProperty.hashCode() * 3));
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
        if(!(obj instanceof OrProperty)){
            return 1;
        }
        final OrProperty comp = (OrProperty)obj;
        if(firstProperty == null && comp.firstProperty != null){
            return -1;
        }else if(firstProperty != null && comp.firstProperty == null){
            return 1;
        }else if(firstProperty != null && comp.firstProperty != null){
            if(firstProperty instanceof Comparable){
                final int val = ((Comparable)firstProperty).compareTo(comp.firstProperty);
                if(val != 0){
                    return val;
                }
            }else{
                return -1;
            }
        }
        if(secondProperty == null && comp.secondProperty == null){
            return 0;
        }else if(secondProperty == null){
            return -1;
        }else{
            if(secondProperty instanceof Comparable){
                return ((Comparable)secondProperty).compareTo(comp.secondProperty);
            }else{
                return -1;
            }
        }
    }
}
