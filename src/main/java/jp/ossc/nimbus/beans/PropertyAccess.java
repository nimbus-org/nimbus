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

import java.util.*;
import java.util.concurrent.*;
import java.lang.reflect.InvocationTargetException;

/**
 * プロパティアクセス。<p>
 * 一度使用した{@link Property}をキャッシュして再利用する。<br>
 *
 * @author M.Takata
 */
public class PropertyAccess{
    
    private boolean isIgnoreNullProperty = false;
    
    private ConcurrentMap propertyCache = new ConcurrentHashMap();
    
    private static PropertyAccess instance;
    
    private static PropertyAccess instanceForIgnoreNullProperty;
    
    /**
     * インスタンスを生成する。<p>
     */
    public PropertyAccess(){
    }
    
    /**
     * シングルトンなインスタンスを取得する。<p>
     *
     * @param isIgnoreNullProperty null参照の時に例外をthrowしない場合はtrue
     * @return シングルトンなインスタンス
     */
    public static synchronized PropertyAccess getInstance(boolean isIgnoreNullProperty){
        if(isIgnoreNullProperty){
            if(instance == null){
                instance = new PropertyAccess(){
                    public void setIgnoreNullProperty(boolean isIgnore){
                    }
                };
            }
            return instance;
        }else{
            if(instanceForIgnoreNullProperty == null){
                instanceForIgnoreNullProperty = new PropertyAccess(){
                    public void setIgnoreNullProperty(boolean isIgnore){
                    }
                };
                instanceForIgnoreNullProperty.isIgnoreNullProperty = true;
            }
            return instanceForIgnoreNullProperty;
        }
    }
    
    /**
     * null参照のプロパティを取得使用とした場合に、例外をthrowするかどうかを設定する。<p>
     * デフォルトは、false。<br>
     *
     * @param isIgnore null参照の時に例外をthrowしない場合はtrue
     */
    public void setIgnoreNullProperty(boolean isIgnore){
        if(this == instance || this == instanceForIgnoreNullProperty){
            return;
        }
        isIgnoreNullProperty = isIgnore;
    }
    
    /**
     * null参照のプロパティを取得使用とした場合に、例外をthrowするかどうかを判定する。<p>
     *
     * @return trueの場合、null参照の時に例外をthrowしない
     */
    public boolean isIgnoreNullProperty(){
        return isIgnoreNullProperty;
    }
    
    /**
     * 指定したオブジェクトの指定したプロパティの値を取得する。<p>
     *
     * @param target プロパティの取得対象となるBean
     * @param prop プロパティ名
     * @return プロパティ値
     * @exception IllegalArgumentException 指定されたプロパティ名のフォーマットが正しくない場合
     * @exception NoSuchPropertyException 指定されたBeanが、指定したプロパティが表すアクセス可能なプロパティを持っていない場合
     * @exception InvocationTargetException 指定されたBeanのアクセサを呼び出した結果、例外がthrowされた場合
     */
    public Object get(Object target, String prop) throws IllegalArgumentException, NoSuchPropertyException, InvocationTargetException{
        return getProperty(prop).getProperty(target);
    }
    
    /**
     * 指定したオブジェクトの指定したプロパティに値を設定する。<p>
     *
     * @param target プロパティの設定対象となるBean
     * @param prop プロパティ名
     * @param value 設定するプロパティ値
     * @exception IllegalArgumentException 指定されたプロパティ名のフォーマットが正しくない場合
     * @exception NoSuchPropertyException 指定されたBeanが、指定したプロパティが表すアクセス可能なプロパティを持っていない場合
     * @exception InvocationTargetException 指定されたBeanのアクセサを呼び出した結果、例外がthrowされた場合
     */
    public void set(Object target, String prop, Object value) throws IllegalArgumentException, NoSuchPropertyException, InvocationTargetException{
        getProperty(prop).setProperty(target, value);
    }
    
    /**
     * 指定したプロパティ名のプロパティを取得する。<p>
     *
     * @param prop プロパティ名
     * @return プロパティ
     * @exception IllegalArgumentException 指定されたプロパティ名のフォーマットが正しくない場合
     */
    public Property getProperty(String prop) throws IllegalArgumentException{
        Property property = (Property)propertyCache.get(prop);
        if(property == null){
            property = PropertyFactory.createProperty(prop);
            property.setIgnoreNullProperty(isIgnoreNullProperty);
            Property exists = (Property)propertyCache.putIfAbsent(prop, property);
            if(exists != null){
                property = exists;
            }
        }
        return property;
    }
    
    /**
     * キャッシュしているプロパティをクリアする。<p>
     */
    public void clear(){
        propertyCache.clear();
    }
}