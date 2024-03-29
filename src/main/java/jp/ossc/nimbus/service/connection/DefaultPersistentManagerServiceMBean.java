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
package jp.ossc.nimbus.service.connection;

import java.util.Map;

import jp.ossc.nimbus.core.*;

/**
 * {@link DefaultPersistentManagerService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see DefaultPersistentManagerService
 */
public interface DefaultPersistentManagerServiceMBean
 extends ServiceBaseMBean{
    public void setIgnoreNullProperty(boolean isIgnore);
    public boolean isIgnoreNullProperty();
    
    /**
     * java.sql.Typesの型と、Javaのクラスのマッピングを設定する。<p>
     * 設定しない場合、デフォルトのマッピングが生成される。
     *
     * @param mapping java.sql.Typesの型のIntegerとJavaのクラスのマッピング
     */
    public void setResultSetJDBCTypeMap(Map mapping);
    
    /**
     * java.sql.Typesの型と、Javaのクラスのマッピングを取得する。<p>
     *
     * @return java.sql.Typesの型のIntegerとJavaのクラスのマッピング
     */
    public Map getResultSetJDBCTypeMap();
    
    /**
     * java.sql.Typesの型と、Javaのクラスのマッピングを設定する。<p>
     *
     * @param jdbcType java.sql.Typesの型名
     * @param javaType Javaのクラス
     */
    public void setResultSetJDBCType(String jdbcType, Class javaType) throws IllegalArgumentException;
    
    /**
     * ARRAY型の要素のデータベースの固有型名と、Javaのクラスのマッピングを設定する。<p>
     *
     * @param mapping ARRAY型の要素のデータベースの固有型名とJavaのクラスのマッピング
     */
    public void setArrayTypeMap(Map mapping);
    
    /**
     * ARRAY型の要素のデータベースの固有型名と、Javaのクラスのマッピングを取得する。<p>
     *
     * @return ARRAY型の要素のデータベースの固有型名とJavaのクラスのマッピング
     */
    public Map getArrayTypeMap();
    
    /**
     * ARRAY型の要素のデータベースの固有型名と、Javaのクラスのマッピングを取得する。<p>
     *
     * @param typeName ARRAY型の要素のデータベースの固有型名
     * @param javaType Javaのクラス
     */
    public void setArrayType(String typeName, Class javaType);
}