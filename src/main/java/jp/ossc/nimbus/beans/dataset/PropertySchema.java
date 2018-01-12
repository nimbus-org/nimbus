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

/**
 * プロパティのスキーマ定義。<p>
 * 
 * @author M.Takata
 */
public interface PropertySchema{
    
    /**
     * プロパティのスキーマ定義を設定する。<p>
     *
     * @param schema プロパティのスキーマ定義
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    public void setSchema(String schema) throws PropertySchemaDefineException;
    
    /**
     * プロパティのスキーマ文字列を取得する。<p>
     *
     * @return プロパティのスキーマ文字列
     */
    public String getSchema();
    
    /**
     * プロパティの名前を取得する。<p>
     *
     * @return プロパティの名前
     */
    public String getName();
    
    /**
     * プロパティの型を取得する。<p>
     *
     * @return プロパティの型
     */
    public Class getType();
    
    /**
     * 主キーかどうかを判別する。<p>
     *
     * @return trueの場合、主キー
     */
    public boolean isPrimaryKey();
    
    /**
     * プロパティの値を設定する時に呼び出される。<p>
     *
     * @param val 設定しようとしているプロパティの値
     * @return 設定されるプロパティの値
     * @exception PropertySetException プロパティの設定に失敗した場合
     */
    public Object set(Object val) throws PropertySetException;
    
    /**
     * プロパティの値を取得する時に呼び出される。<p>
     *
     * @param val 取得しようとしているプロパティの値
     * @return 取得されるプロパティの値
     * @exception PropertyGetException プロパティの取得に失敗した場合
     */
    public Object get(Object val) throws PropertyGetException;
    
    /**
     * フォーマットされたプロパティの値を取得する時に呼び出される。<p>
     *
     * @param val 取得しようとしているフォーマットされたプロパティの値
     * @return 取得されるフォーマットされたプロパティの値
     * @exception PropertyGetException プロパティの取得に失敗した場合
     */
    public Object format(Object val) throws PropertyGetException;
    
    /**
     * パースしてプロパティの値を設定する時に呼び出される。<p>
     *
     * @param val 設定しようとしているパースするプロパティの値
     * @return 設定されるパースされたプロパティの値
     * @exception PropertySetException プロパティの設定に失敗した場合
     */
    public Object parse(Object val) throws PropertySetException;
    
    /**
     * プロパティの値を検証する時に呼び出される。<p>
     *
     * @param val 検証しようとしているプロパティの値
     * @return 検証結果。trueの場合、検証成功
     * @exception PropertyValidateException プロパティの検証時に例外が発生した場合
     */
    public boolean validate(Object val) throws PropertyValidateException;
}
