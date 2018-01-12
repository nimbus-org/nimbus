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
package jp.ossc.nimbus.service.transaction;

import java.lang.reflect.*;

import jp.ossc.nimbus.core.*;

/**
 * {@link ReflectionTransactionManagerFactoryService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see ReflectionTransactionManagerFactoryService
 */
public interface ReflectionTransactionManagerFactoryServiceMBean
 extends ServiceBaseMBean{
    
    /**
     * TransactionManagerを生成するファクトリクラスのコンストラクタを設定する。<p>
     * ファクトリクラスのstaticメソッドを呼ぶ場合は、指定する必要はない。<br>
     * 
     * @param c ファクトリクラスのコンストラクタ
     */
    public void setFactoryConstructor(Constructor c);
    
    /**
     * TransactionManagerを生成するファクトリクラスのコンストラクタを取得する。<p>
     * 
     * @return ファクトリクラスのコンストラクタ
     */
    public Constructor getFactoryConstructor();
    
    /**
     * TransactionManagerを生成するファクトリクラスのコンストラクタの引数を設定する。<p>
     * 
     * @param params ファクトリクラスのコンストラクタの引数
     */
    public void setFactoryConstructorParameters(Object[] params);
    
    /**
     * TransactionManagerを生成するファクトリクラスのコンストラクタの引数を取得する。<p>
     * 
     * @return ファクトリクラスのコンストラクタの引数
     */
    public Object[] getFactoryConstructorParameters();
    
    /**
     * TransactionManagerを生成するファクトリクラスのファクトリメソッドを設定する。<p>
     * 
     * @param m ファクトリクラスのファクトリメソッド
     */
    public void setFactoryMethod(Method m);
    
    /**
     * TransactionManagerを生成するファクトリクラスのファクトリメソッドを取得する。<p>
     * 
     * @return ファクトリクラスのファクトリメソッド
     */
    public Method getFactoryMethod();
    
    /**
     * TransactionManagerを生成するファクトリクラスのファクトリメソッドの引数を設定する。<p>
     * 
     * @param params ファクトリクラスのファクトリメソッドの引数
     */
    public void setFactoryMethodParameters(Object[] params);
    
    /**
     * TransactionManagerを生成するファクトリクラスのファクトリメソッドの引数を取得する。<p>
     * 
     * @return ファクトリクラスのファクトリメソッドの引数
     */
    public Object[] getFactoryMethodParameters();
    
    /**
     * ファクトリのインスタンスを設定する。<p>
     *
     * @param fac ファクトリ
     */
    public void setFactory(Object fac);
    
    /**
     * ファクトリのインスタンスを取得する。<p>
     *
     * @return ファクトリ
     */
    public Object getFactory();
    
    /**
     * TransactionManagerクラスのコンストラクタを設定する。<p>
     * staticメソッドを呼ぶ場合は、指定する必要はない。<br>
     * 
     * @param c コンストラクタ
     */
    public void setTransactionManagerConstructor(Constructor c);
    
    /**
     * TransactionManagerクラスのコンストラクタを取得する。<p>
     * 
     * @return コンストラクタ
     */
    public Constructor getTransactionManagerConstructor();
    
    /**
     * TransactionManagerクラスのコンストラクタの引数を設定する。<p>
     * 
     * @param params コンストラクタの引数
     */
    public void setTransactionManagerConstructorParameters(Object[] params);
    
    /**
     * TransactionManagerクラスのコンストラクタの引数を取得する。<p>
     * 
     * @return コンストラクタの引数
     */
    public Object[] getTransactionManagerConstructorParameters();
}
