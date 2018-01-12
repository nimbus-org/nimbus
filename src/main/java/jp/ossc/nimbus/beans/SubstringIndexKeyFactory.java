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

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.lang.reflect.InvocationTargetException;
import java.io.Externalizable;
import java.io.ObjectOutput;
import java.io.ObjectInput;
import java.io.IOException;

/**
 * 文字列型プロパティの部分文字列をインデックスとする{@link BeanTableIndexKeyFactory}実装クラス。<p>
 *
 * @author M.Takata
 */
public class SubstringIndexKeyFactory implements BeanTableIndexKeyFactory, Externalizable{
    
    protected Property property;
    protected Set propertyNames;
    protected int beginIndex;
    protected int endIndex;
    protected boolean reverse;
    
    public SubstringIndexKeyFactory(){}
    
    /**
     * 文字列の後方一致を行うインデックスを生成するインスタンスを生成する。<p>
     *
     * @param elementClass テーブルの要素となるBeanのクラスオブジェクト
     * @param propName 対象となる文字列型プロパティの名前
     * @param aliasName 部分文字列となったプロパティの別名
     * @param beginIndex 部分文字列の開始インデックス
     */
    public SubstringIndexKeyFactory(
        Class elementClass,
        String propName,
        String aliasName,
        int beginIndex
    ) throws NoSuchPropertyException, IllegalArgumentException{
        this(elementClass, propName, aliasName, beginIndex, -1);
    }
    
    /**
     * 文字列の前方一致及び部分一致を行うインデックスを生成するインスタンスを生成する。<p>
     *
     * @param elementClass テーブルの要素となるBeanのクラスオブジェクト
     * @param propName 対象となる文字列型プロパティの名前
     * @param aliasName 部分文字列となったプロパティの別名
     * @param beginIndex 部分文字列の開始インデックス。このインデックスの文字を含む
     * @param endIndex 部分文字列の終了インデックス。このインデックスの文字は含まない
     */
    public SubstringIndexKeyFactory(
        Class elementClass,
        String propName,
        String aliasName,
        int beginIndex,
        int endIndex
    ) throws NoSuchPropertyException, IllegalArgumentException{
        this(elementClass, propName, aliasName, beginIndex, endIndex, false);
    }
    
    /**
     * 文字列の前方一致及び部分一致を行うインデックスを生成するインスタンスを生成する。<p>
     *
     * @param elementClass テーブルの要素となるBeanのクラスオブジェクト
     * @param propName 対象となる文字列型プロパティの名前
     * @param aliasName 部分文字列となったプロパティの別名
     * @param beginIndex 部分文字列の開始インデックス。このインデックスの文字を含む
     * @param endIndex 部分文字列の終了インデックス。このインデックスの文字は含まない
     * @param reverse trueの場合、文字列の後ろからインデックスを評価する
     */
    public SubstringIndexKeyFactory(
        Class elementClass,
        String propName,
        String aliasName,
        int beginIndex,
        int endIndex,
        boolean reverse
    ) throws NoSuchPropertyException, IllegalArgumentException{
        if(beginIndex < 0){
            throw new IllegalArgumentException("beginIndex >= 0 : " + beginIndex);
        }
        if(endIndex == 0){
            throw new IllegalArgumentException("endIndex != 0 : " + endIndex);
        }
        if(endIndex > 0 && beginIndex >= endIndex){
            throw new IllegalArgumentException("beginIndex < endIndex : beginIndex="  + beginIndex + ", endIndex=" + endIndex);
        }
        SimpleProperty prop = new SimpleProperty(propName);
        if(!prop.isReadable(elementClass)){
            throw new NoSuchPropertyException(elementClass, propName);
        }
        property = prop;
        propertyNames = new HashSet();
        propertyNames.add(aliasName);
        this.beginIndex = beginIndex;
        this.endIndex = endIndex;
        this.reverse = reverse;
    }
    
    public Set getPropertyNames(){
        return propertyNames;
    }
    
    public Object createIndexKey(Object element) throws IndexPropertyAccessException{
        String strProp = null;
        try{
            Object prop = property.getProperty(element);
            if(prop == null){
                return null;
            }
            if(!(prop instanceof String)){
                throw new IndexPropertyAccessException(prop.getClass(), property.getPropertyName(), "Type is not String. type=" + prop.getClass().getName());
            }
            strProp = (String)prop;
        }catch(NoSuchPropertyException e){
            throw new IndexPropertyAccessException(
                element.getClass(),
                property.getPropertyName(),
                e
            );
        }catch(InvocationTargetException e){
            throw new IndexPropertyAccessException(
                element.getClass(),
                property.getPropertyName(),
                ((InvocationTargetException)e).getTargetException()
            );
        }
        return substring(strProp);
    }
    
    protected String substring(String str){
        final int len = str.length();
        if(len == 0){
            return str;
        }
        if(beginIndex > len){
            return "";
        }
        if(reverse){
            if(endIndex > 0){
                return str.substring(endIndex > len ? 0 : len - endIndex, len - beginIndex);
            }else{
                return str.substring(0, len - beginIndex);
            }
        }else{
            if(endIndex > 0){
                return str.substring(beginIndex, endIndex > len ? len : endIndex);
            }else{
                return str.substring(beginIndex);
            }
        }
    }
    
    public Object createIndexKeyByProperties(Map keys) throws IllegalArgumentException{
        final String aliasName = (String)propertyNames.iterator().next();
        if(keys.containsKey(aliasName)){
            return keys.get(aliasName);
        }else if(keys.containsKey(property.getPropertyName())){
            Object prop = keys.get(property.getPropertyName());
            if(prop == null){
                return null;
            }
            if(!(prop instanceof String)){
                throw new IllegalArgumentException("Type is not String. type=" + prop.getClass().getName());
            }
            return substring((String)prop);
        }else{
            throw new IllegalArgumentException("keys are insufficient. keys=" + keys);
        }
    }
    public void writeExternal(ObjectOutput out) throws IOException{
        out.writeObject(property.getPropertyName());
        out.writeObject(propertyNames.iterator().next());
        out.writeInt(beginIndex);
        out.writeInt(endIndex);
        out.writeBoolean(reverse);
    }
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException{
        String propertyName = (String)in.readObject();
        if(propertyName != null){
            property = new SimpleProperty(propertyName);
        }
        String aliasName = (String)in.readObject();
        if(aliasName != null){
            propertyNames = new HashSet();
            propertyNames.add(aliasName);
        }
        beginIndex = in.readInt();
        endIndex = in.readInt();
        reverse = in.readBoolean();
    }
}