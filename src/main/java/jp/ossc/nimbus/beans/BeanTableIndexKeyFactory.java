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
import java.util.Map;

/**
 * {@link BeanTableIndex Beanテーブルインデックス}のインデックスキーのファクトリ。<p>
 *
 * @author M.Takata
 * @see BeanTableIndex
 */
public interface BeanTableIndexKeyFactory{
    
    /**
     * インデックス対象となるプロパティ名の集合を取得する。<p>
     * Beanのプロパティを編集する場合は、本来のプロパティ名と被らない別名を返す必要がある。<br>
     *
     * @return インデックス対象となるプロパティ名の集合
     */
    public Set getPropertyNames();
    
    /**
     * 指定されたBeanからインデックスキーを生成する。<p>
     * 指定されたBeanから、{@link #getPropertyNames()}で返すプロパティに該当する値を取得及び編集して、インデックスのキーとなるオブジェクトを生成する。<br>
     *
     * @param element テーブルの要素となるBean
     * @return インデックスキー
     * @exception IndexPropertyAccessException 指定されたプロパティの取得で例外が発生した場合
     */
    public Object createIndexKey(Object element) throws IndexPropertyAccessException;
    
    /**
     * 指定されたプロパティ名と値のマップからインデックスキーを生成する。<p>
     * 指定されたプロパティ名と値のマップから、{@link #getPropertyNames()}で返すプロパティに該当する値を取得及び編集して、インデックスのキーとなるオブジェクトを生成する。<br>
     *
     * @param props プロパティ名と値のマップ
     * @return インデックスキー
     * @exception IllegalArgumentException 指定されたプロパティ名と値のマップに必要なキーが含まれていない場合
     */
    public Object createIndexKeyByProperties(Map props) throws IllegalArgumentException;
}