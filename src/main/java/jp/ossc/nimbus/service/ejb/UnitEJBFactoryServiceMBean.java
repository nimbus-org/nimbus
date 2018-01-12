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
package jp.ossc.nimbus.service.ejb;

/**
 * {@link UnitEJBFactoryService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see UnitEJBFactoryService
 */
public interface UnitEJBFactoryServiceMBean extends InvocationEJBFactoryServiceMBean{
    
    /**
     * このEJBファクトリで生成するEJBのEJBHomeの完全修飾クラス名を取得する。<p>
     * 設定されてない場合は、javax.ejb.EJBHomeを返す。
     *
     * @return EJBHomeの完全修飾クラス名
     * @see #setHomeType(String)
     */
    public String getHomeType();
    
    /**
     * このEJBファクトリで生成するEJBのEJBHomeの完全修飾クラス名を設定する。<p>
     *
     * @param className EJBHomeの完全修飾クラス名
     * @see #getHomeType()
     */
    public void setHomeType(String className);
    
    /**
     * このEJBファクトリで生成するEJBのEJBLocalHomeの完全修飾クラス名を取得する。<p>
     * 設定されてない場合は、javax.ejb.EJBLocalHomeを返す。
     *
     * @return EJBLocalHomeの完全修飾クラス名
     * @see #setLocalHomeType(String)
     */
    public String getLocalHomeType();
    
    /**
     * このEJBファクトリで生成するEJBのEJBLocalHomeの完全修飾クラス名を設定する。<p>
     *
     * @param className EJBLocalHomeの完全修飾クラス名
     * @see #getLocalHomeType()
     */
    public void setLocalHomeType(String className);
    
    /**
     * このEJBファクトリで生成するEJBのEJBObjectの完全修飾クラス名を取得する。<p>
     * 設定されてない場合は、javax.ejb.EJBObjectを返す。
     *
     * @return EJBObjectの完全修飾クラス名
     * @see #setRemoteType(String)
     */
    public String getRemoteType();
    
    /**
     * このEJBファクトリで生成するEJBのEJBObjectの完全修飾クラス名を設定する。<p>
     *
     * @param className EJBObjectの完全修飾クラス名
     * @see #getRemoteType()
     */
    public void setRemoteType(String className);
    
    /**
     * このEJBファクトリで生成するEJBのEJBLocalObjectの完全修飾クラス名を取得する。<p>
     * 設定されてない場合は、javax.ejb.EJBLocalObjectを返す。
     *
     * @return EJBObjectの完全修飾クラス名
     * @see #setLocalType(String)
     */
    public String getLocalType();
    
    /**
     * このEJBファクトリで生成するEJBのEJBLocalObjectの完全修飾クラス名を設定する。<p>
     *
     * @param className EJBLocalObjectの完全修飾クラス名
     * @see #getLocalType()
     */
    public void setLocalType(String className);
    
    /**
     * このEJBファクトリで生成するEJBのEJBHome及びEJBLocalHomeのcreateメソッドに渡す引数の完全修飾クラス名を文字列配列として取得する。<p>
     * 設定されてない場合は、nullを返す。
     *
     * @return EJBHome及びEJBLocalHomeのcreateメソッドに渡す引数の完全修飾クラス名の文字列配列
     * @see #setCreateMethodParamTypes(String[])
     */
    public String[] getCreateMethodParamTypes();
    
    /**
     * このEJBファクトリで生成するEJBのEJBHome及びEJBLocalHomeのcreateメソッドに渡す引数の完全修飾クラス名を文字列配列として設定する。<p>
     * 引数がない場合は、設定する必要はない。
     *
     * @param params EJBHome及びEJBLocalHomeのcreateメソッドに渡す引数の完全修飾クラス名の文字列配列
     * @see #getCreateMethodParamTypes()
     */
    public void setCreateMethodParamTypes(String[] params);
}
