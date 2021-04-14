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
 * 連結プロパティ。<p>
 * 複数のプロパティを連結してアクセスするための{@link Property}。<br>
 * 以下のようなコードがある。<br>
 * <pre>
 *   String dateStr = obj.getDate();
 *   String timeStr = obj.getTime();
 *   String dateTime = dateStr + timeStr;
 * </pre>
 * 連結を使う事で、このコードを<br>
 * <pre>
 *   ConcatenationProperty prop = new ConcatenationProperty(new SimpleProperty("date"), new SimpleProperty("time"));
 *   Object propValue = prop.getProperty(obj);
 * </pre>
 * というコードに置き換える事ができる。<br>
 * このコードは、冗長になっているが、対象となるBeanの型やメソッドをタイプセーフに書かない動的なコードになっている。<br>
 * <p>
 * この連結プロパティでは、以下のようなBeanのプロパティに対するアクセス方法が用意されている。<br>
 * <table border="1">
 *   <tr bgcolor="#CCCCFF"><th rowspan="3">アクセス方法</th><th>Java表現</th><th rowspan="3">プロパティ文字列表現</th></tr>
 *   <tr bgcolor="#CCCCFF"><th>プロパティ取得</th><td>連結</td><td>bean.getDate() + bean.getTime()</td><td rowspan="2">date+time</td></tr>
 * </table>
 * 連結対象の２つのプロパティは、{@link Property}インタフェースを実装していれば良く、単純プロパティ、インデックスプロパティ、マッププロパティ、ネストプロパティのいずれでも良い。<br>
 *
 * @author M.Takata
 */
public class ConcatenatedProperty implements Property, java.io.Serializable{
    
    private static final long serialVersionUID = -7626632324425762277L;
    
    private static final String MSG_00001 = "Illegal ConcatenatedProperty : ";
    private static final String MSG_00002 = "Arguments is null.";
    
    /**
     * 連結されるプロパティ。<p>
     */
    protected Property thisProperty;
    
    /**
     * 連結するプロパティ。<p>
     */
    protected Property concatProperty;
    
    /**
     * null参照のプロパティを取得使用とした場合に、例外をthrowするかどうかのフラグ。<p>
     * trueの場合は、例外をthrowしない。デフォルトは、false。<br>
     */
    protected boolean isIgnoreNullProperty;
    
    /**
     * 空の連結プロパティを生成する。<p>
     */
    public ConcatenatedProperty(){
    }
    
    /**
     * 指定した２つのプロパティが連結した連結プロパティを生成する。<p>
     *
     * @param prop 連結されるプロパティ
     * @param concatProp 連結するプロパティ
     * @exception IllegalArgumentException 引数にnullを指定した場合
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
     * 連結されるプロパティを設定する。<p>
     * 
     * @param prop 連結されるプロパティ
     * @exception IllegalArgumentException 引数にnullを指定した場合
     */
    public void setThisProperty(Property prop) throws IllegalArgumentException{
        if(prop == null){
            throw new IllegalArgumentException(MSG_00002);
        }
        thisProperty = prop;
    }
    
    /**
     * 連結される最初のプロパティを取得する。<p>
     * 
     * @return 連結される最初のプロパティ
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
     * 連結されるプロパティを取得する。<p>
     * 
     * @return 連結されるプロパティ
     */
    public Property getThisProperty(){
        return thisProperty;
    }
    
    /**
     * 連結するプロパティを設定する。<p>
     * 
     * @param concatProp 連結するプロパティ
     * @exception IllegalArgumentException 引数にnullを指定した場合
     */
    public void setConcatenatedProperty(Property concatProp)
     throws IllegalArgumentException{
        if(concatProp == null){
            throw new IllegalArgumentException(MSG_00002);
        }
        concatProperty = concatProp;
    }
    
    /**
     * 連結するプロパティを取得する。<p>
     * 
     * @return 連結するプロパティ
     */
    public Property getConcatenatedProperty(){
        return concatProperty;
    }
    
    /**
     * 指定したプロパティ文字列を解析する。<p>
     * ここで指定可能な文字列は、<br>
     * &nbsp;連結されるプロパティ名.連結するプロパティ名<br>
     * である。<br>
     *
     * @param prop プロパティ文字列
     * @exception IllegalArgumentException 指定されたプロパティ文字列をこのプロパティオブジェクトが解析できない場合
     */
    public void parse(String prop) throws IllegalArgumentException{
        final int index = prop.indexOf('+');
        if(index == -1 || index == 0 || index == prop.length() - 1){
            throw new IllegalArgumentException(MSG_00001 + prop);
        }
        String propStr = prop.substring(0, index).trim();
        if(StringProperty.isEncloused(propStr)){
            StringProperty property = new StringProperty();
            property.parse(propStr);
            thisProperty = property;
        }else{
            thisProperty = PropertyFactory.createProperty(propStr);
        }
        propStr = prop.substring(index + 1).trim();
        if(StringProperty.isEncloused(propStr)){
            StringProperty property = new StringProperty();
            property.parse(propStr);
            concatProperty = property;
        }else{
            concatProperty = PropertyFactory.createProperty(propStr);
        }
    }
    
    public Class getPropertyType(Class clazz){
        return String.class;
    }
    
    public Class getPropertyGenericType(Class clazz){
        return String.class;
    }
    
    public Class getPropertyType(Object obj){
        return String.class;
    }
    
    public Class getPropertyGenericType(Object obj){
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
     * 指定したオブジェクトから、このプロパティが表すプロパティ値を取得する。<p>
     *
     * @param obj 対象となるBean
     * @return プロパティ値
     * @exception NullNestPropertyException 連結するプロパティが、nullの場合
     * @exception NoSuchPropertyException 指定されたBeanが、このプロパティが表すアクセス可能なプロパティを持っていない場合
     * @exception InvocationTargetException 指定されたBeanのアクセサを呼び出した結果、例外がthrowされた場合
     */
    public Object getProperty(Object obj)
     throws NoSuchPropertyException, InvocationTargetException{
        StringBuilder buf = new StringBuilder();
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
     * サポートしない。<p>
     *
     * @param obj 対象となるBean
     * @param value 設定するプロパティ値
     * @exception NullNestPropertyException 連結するプロパティが、nullの場合
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
     * @exception NullNestPropertyException 連結するプロパティが、nullの場合
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
     * @return 連結されるプロパティ名.連結するプロパティ名
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
     * この連結プロパティの文字列表現を取得する。<p>
     *
     * @return ConcatenatedProperty{プロパティ名.連結するプロパティ名}
     */
    public String toString(){
        return "ConcatenatedProperty{"
            + (thisProperty == null ? "null" : thisProperty.toString())
            + '+' + (concatProperty == null ? "null" : concatProperty.toString()) + '}';
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
     * ハッシュ値を取得する。<p>
     *
     * @return ハッシュ値
     */
    public int hashCode(){
        return (thisProperty == null ? 0 : (thisProperty.hashCode() * 2)) + (concatProperty == null ? 0 : (concatProperty.hashCode() * 3));
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
    
    public static class StringProperty implements Property, java.io.Serializable{
        
        private String string;
        private boolean isIgnoreNullProperty;
        
        public StringProperty(){
        }
        
        public StringProperty(String str){
            string = str;
        }
        
        public String getPropertyName(){
            return string;
        }
        
        public Class getPropertyType(Class clazz) throws NoSuchPropertyException{
            return String.class;
        }
        
        public Type getPropertyGenericType(Class clazz) throws NoSuchPropertyException{
            return getPropertyType(clazz);
        }
        
        public Class getPropertyType(Object obj) throws NoSuchPropertyException, InvocationTargetException{
            return getPropertyType((Class)null);
        }
        
        public Type getPropertyGenericType(Object obj) throws NoSuchPropertyException, InvocationTargetException{
            return getPropertyType(obj);
        }
        
        public Object getProperty(Object obj) throws NoSuchPropertyException, InvocationTargetException{
            return string;
        }
        
        public void setProperty(Object obj, Object value) throws NoSuchPropertyException, InvocationTargetException{
            throw new UnsupportedOperationException();
        }
        
        public void setProperty(Object obj, Class type, Object value) throws NoSuchPropertyException, InvocationTargetException{
            throw new UnsupportedOperationException();
        }
        
        public void parse(String prop) throws IllegalArgumentException{
            String propStr = prop.trim();
            if(isEncloused(prop)){
                string = propStr.substring(1, propStr.length() - 1);
            }else{
                string = prop;
            }
        }
        
        public static boolean isEncloused(String prop){
            return prop.length() > 2
                && ((prop.indexOf(0) == '\'' && prop.indexOf(prop.length() - 1) == '\'')
                    || (prop.indexOf(0) == '"' && prop.indexOf(prop.length() - 1) == '"'));
        }
        
        public boolean isReadable(Class clazz){
            return true;
        }
        
        public boolean isReadable(Object obj){
            return true;
        }
        
        public boolean isWritable(Class targetClass, Class clazz){
            return false;
        }
        
        public boolean isWritable(Object obj, Class clazz){
            return false;
        }
        
        public void setIgnoreNullProperty(boolean isIgnore){
            isIgnoreNullProperty = isIgnore;
        }
        
        public boolean isIgnoreNullProperty(){
            return isIgnoreNullProperty;
        }
        
        public String toString(){
            return "ConcatenatedProperty$StringProperty{'" + string + "'}";
        }
    }
}
