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
package jp.ossc.nimbus.service.aop.interceptor;

import java.util.Properties;

import jp.ossc.nimbus.core.*;

/**
 * {@link MethodMappingInterceptorService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see MethodMappingInterceptorService
 */
public interface MethodMappingInterceptorServiceMBean extends ServiceBaseMBean{
    
    /**
     * メソッドとインターセプタサービス名のマッピングを設定する。<p>
     * 指定されたメソッドが呼び出された場合に、対応するインターセプタサービスを呼び出すように設定する。<br>
     * クラス名#メソッド名(引数型,引数型,…)=インターセプタサービス名<br>
     * クラス名、メソッド名、引数型には、正規表現を指定する事ができる。また、引数が一致するか比較しない場合は、*を指定する。<br>
     * 一致するマッピングが複数ある場合の動作は保証しない。<br>
     *
     * @param mapping メソッドとインターセプタサービス名のマッピング
     */
    public void setTargetMethodMapping(Properties mapping);
    
    /**
     * メソッドとインターセプタサービス名のマッピングを取得する。<p>
     *
     * @return メソッドとインターセプタサービス名のマッピング
     */
    public Properties getTargetMethodMapping();
    
    /**
     * メソッドとコンテキストキー名のマッピングを設定する。<p>
     * 指定されたメソッドが呼び出された場合に、対応するコンテキストキー名の値をコンテキストから取得して返すように設定する。<br>
     * クラス名#メソッド名(引数型,引数型,…)=コンテキストキー名<br>
     * クラス名、メソッド名、引数型には、正規表現を指定する事ができる。また、引数が一致するか比較しない場合は、*を指定する。<br>
     * 一致するマッピングが複数ある場合の動作は保証しない。<br>
     *
     * @param mapping メソッドとコンテキストキー名のマッピング
     */
    public void setTargetMethodReturnMapping(Properties mapping);
    
    /**
     * メソッドとコンテキストキー名のマッピングを取得する。<p>
     *
     * @return メソッドとコンテキストキー名のマッピング
     */
    public Properties getTargetMethodReturnMapping();
    
    /**
     * コンテキストサービス名を設定する。<p>
     *
     * @param name コンテキストサービス名
     */
    public void setContextServiceName(ServiceName name);
    
    /**
     * コンテキストサービス名を取得する。<p>
     *
     * @return コンテキストサービス名
     */
    public ServiceName getContextServiceName();
}
