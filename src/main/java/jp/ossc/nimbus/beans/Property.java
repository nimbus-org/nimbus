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
 * Beanのプロパティに汎用的にアクセスするためのインタフェース。<p>
 *
 * @author M.Takata
 */
public interface Property{
    
    /**
     * このプロパティが表すプロパティ名を取得する。<p>
     *
     * @return プロパティ名
     */
    public String getPropertyName();
    
    /**
     * 指定したオブジェクトのクラスから、このプロパティが表すプロパティ型を取得する。<p>
     *
     * @param clazz 対象となるBeanのクラス
     * @return プロパティ型
     * @exception NoSuchPropertyException 指定されたBeanが、このプロパティが表すアクセス可能なプロパティを持っていない場合
     * @exception InvocationTargetException 指定されたBeanのアクセサを呼び出した結果、例外がthrowされた場合
     */
    public Class getPropertyType(Class clazz) throws NoSuchPropertyException;
    
    /**
     * 指定したオブジェクトから、このプロパティが表すプロパティ総称型を取得する。<p>
     *
     * @param clazz 対象となるBeanのクラス
     * @return プロパティ総称型
     * @exception NoSuchPropertyException 指定されたBeanのクラスが、このプロパティが表すアクセス可能なプロパティを持っていない場合
     */
    public Type getPropertyGenericType(Class clazz) throws NoSuchPropertyException;
    
    /**
     * 指定したオブジェクトから、このプロパティが表すプロパティ型を取得する。<p>
     *
     * @param obj 対象となるBean
     * @return プロパティ型
     * @exception NoSuchPropertyException 指定されたBeanが、このプロパティが表すアクセス可能なプロパティを持っていない場合
     * @exception InvocationTargetException 指定されたBeanのアクセサを呼び出した結果、例外がthrowされた場合
     */
    public Class getPropertyType(Object obj)
     throws NoSuchPropertyException, InvocationTargetException;
    
    /**
     * 指定したオブジェクトから、このプロパティが表すプロパティ総称型を取得する。<p>
     *
     * @param obj 対象となるBean
     * @return プロパティ総称型
     * @exception NoSuchPropertyException 指定されたBeanが、このプロパティが表すアクセス可能なプロパティを持っていない場合
     * @exception InvocationTargetException 指定されたBeanのアクセサを呼び出した結果、例外がthrowされた場合
     */
    public Type getPropertyGenericType(Object obj)
     throws NoSuchPropertyException, InvocationTargetException;
    
    /**
     * 指定したオブジェクトから、このプロパティが表すプロパティ値を取得する。<p>
     *
     * @param obj 対象となるBean
     * @return プロパティ値
     * @exception NoSuchPropertyException 指定されたBeanが、このプロパティが表すアクセス可能なプロパティを持っていない場合
     * @exception InvocationTargetException 指定されたBeanのアクセサを呼び出した結果、例外がthrowされた場合
     */
    public Object getProperty(Object obj)
     throws NoSuchPropertyException, InvocationTargetException;
    
    /**
     * 指定したオブジェクトに、このプロパティが表すプロパティ値を設定する。<p>
     *
     * @param obj 対象となるBean
     * @param value 設定するプロパティ値
     * @exception NoSuchPropertyException 指定されたBeanが、このプロパティが表すアクセス可能なプロパティを持っていない場合
     * @exception InvocationTargetException 指定されたBeanのアクセサを呼び出した結果、例外がthrowされた場合
     */
    public void setProperty(Object obj, Object value)
     throws NoSuchPropertyException, InvocationTargetException;
    
    /**
     * 指定したオブジェクトに、このプロパティが表すプロパティ値を設定する。<p>
     *
     * @param obj 対象となるBean
     * @param type プロパティの型
     * @param value 設定するプロパティ値
     * @exception NoSuchPropertyException 指定されたBeanが、このプロパティが表すアクセス可能なプロパティを持っていない場合
     * @exception InvocationTargetException 指定されたBeanのアクセサを呼び出した結果、例外がthrowされた場合
     */
    public void setProperty(Object obj, Class type, Object value)
     throws NoSuchPropertyException, InvocationTargetException;
    
    /**
     * 指定したプロパティ文字列を解析する。<p>
     *
     * @param prop プロパティ文字列
     * @exception IllegalArgumentException 指定されたプロパティ文字列をこのプロパティオブジェクトが解析できない場合
     */
    public void parse(String prop) throws IllegalArgumentException;
    
    /**
     * 読み込み可能かどうか判定する。<p>
     *
     * @param clazz 対象となるBeanのクラス
     * @return 読み込み可能な場合true
     */
    public boolean isReadable(Class clazz);
    
    /**
     * 読み込み可能かどうか判定する。<p>
     *
     * @param obj 対象となるBean
     * @return 読み込み可能な場合true
     */
    public boolean isReadable(Object obj);
    
    /**
     * 書き込み可能かどうか判定する。<p>
     *
     * @param targetClass 対象となるBeanのクラス
     * @param clazz 設定するプロパティの型
     * @return 書き込み可能な場合true
     */
    public boolean isWritable(Class targetClass, Class clazz);
    
    /**
     * 書き込み可能かどうか判定する。<p>
     *
     * @param obj 対象となるBean
     * @param clazz 設定するプロパティの型
     * @return 書き込み可能な場合true
     */
    public boolean isWritable(Object obj, Class clazz);
    
    /**
     * null参照のプロパティを取得使用とした場合に、例外をthrowするかどうかを設定する。<p>
     * デフォルトは、false。<br>
     *
     * @param isIgnore null参照の時に例外をthrowしない場合はtrue
     */
    public void setIgnoreNullProperty(boolean isIgnore);
    
    /**
     * null参照のプロパティを取得使用とした場合に、例外をthrowするかどうかを判定する。<p>
     *
     * @return trueの場合、null参照の時に例外をthrowしない
     */
    public boolean isIgnoreNullProperty();
}
