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
package jp.ossc.nimbus.service.aop.javassist;

import jp.ossc.nimbus.core.*;

/**
 * {@link MethodInterceptorAspectService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see MethodInterceptorAspectService
 */
public interface MethodInterceptorAspectServiceMBean extends ServiceBaseMBean{
    
    /**
     * Interceptorを入れる対象のクラスのクラス修飾子を指定する。<p>
     * ここで指定されたクラス修飾子のクラスがInterceptorを入れる対象となる。指定しない場合は、クラス修飾子は限定されない。<br>
     * 修飾子を否定する場合は、各修飾子の前に"!"を付与すること。
     *
     * @param modifiers クラス修飾子文字列
     */
    public void setTargetClassModifiers(String modifiers);
    
    /**
     * Interceptorを入れる対象のクラスのクラス修飾子を取得する。<p>
     *
     * @return クラス修飾子文字列
     */
    public String getTargetClassModifiers();
    
    /**
     * Interceptorを入れる対象のクラス名を指定する。<p>
     * ここで指定されたクラス名のクラスがInterceptorを入れる対象となる。指定しない場合は、クラス名は限定されない。また、正規表現を指定する事も可能である。
     *
     * @param name パッケージ名を含む完全修飾クラス名。正規表現も可。
     */
    public void setTargetClassName(String name);
    
    /**
     * Interceptorを入れる対象のクラス名を取得する。<p>
     *
     * @return パッケージ名を含む完全修飾クラス名。正規表現も可。
     */
    public String getTargetClassName();
    
    /**
     * Interceptorを入れない対象のクラス名を指定する。<p>
     * ここで指定されたクラス名のクラスがInterceptorを入れる対象とならない。また、正規表現を指定する事も可能である。
     *
     * @param name パッケージ名を含む完全修飾クラス名。正規表現も可。
     */
    public void setNoTargetClassName(String name);
    
    /**
     * Interceptorを入れない対象のクラス名を取得する。<p>
     *
     * @return パッケージ名を含む完全修飾クラス名。正規表現も可。
     */
    public String getNoTargetClassName();
    
    /**
     * Interceptorを入れる対象のインスタンスのクラス名を指定する。<p>
     * ここで指定されたクラス名のインスタンスがInterceptorを入れる対象となる。指定しない場合は、インスタンスは限定されない。
     *
     * @param name パッケージ名を含む完全修飾クラス名
     */
    public void setTargetInstanceClassName(String name);
    
    /**
     * Interceptorを入れる対象のインスタンスのクラス名を取得する。<p>
     *
     * @return パッケージ名を含む完全修飾クラス名。
     */
    public String getTargetInstanceClassName();
    
    /**
     * Interceptorを入れる対象のメソッドのメソッド修飾子を指定する。<p>
     * ここで指定されたメソッド修飾子のメソッドがInterceptorを入れる対象となる。指定しない場合は、メソッド修飾子は限定されない。<br>
     * 修飾子を否定する場合は、各修飾子の前に"!"を付与すること。
     *
     * @param modifiers メソッド修飾子文字列
     */
    public void setTargetMethodModifiers(String modifiers);
    
    /**
     * Interceptorを入れる対象のメソッドのメソッド修飾子を取得する。<p>
     *
     * @return メソッド修飾子文字列
     */
    public String getTargetMethodModifiers();
    
    /**
     * Interceptorを入れる対象のメソッド名を指定する。<p>
     * ここで指定されたメソッド名のメソッドがInterceptorを入れる対象となる。指定しない場合は、メソッド名は限定されない。また、正規表現を指定する事も可能である。
     *
     * @param name メソッド名。正規表現も可。
     */
    public void setTargetMethodName(String name);
    
    /**
     * Interceptorを入れる対象のメソッド名を取得する。<p>
     *
     * @return メソッド名。正規表現も可。
     */
    public String getTargetMethodName();
    
    /**
     * Interceptorを入れる対象のメソッドの引数の型を表すクラス名を指定する。<p>
     * ここで指定された引数型を持つメソッドがInterceptorを入れる対象となる。指定しない場合は、引数型は限定されない。また、正規表現を指定する事も可能である。
     *
     * @param paramTypes メソッドの引数の型を表すクラス名の配列。正規表現も可。
     */
    public void setTargetParameterTypes(String[] paramTypes);
    
    /**
     * Interceptorを入れる対象のメソッドの引数の型を表すクラス名を取得する。<p>
     *
     * @return メソッドの引数の型を表すクラス名の配列。正規表現も可。
     */
    public String[] getTargetParameterTypes();
    
    /**
     * 挿入するInterceptorチェーンのリストである{@link jp.ossc.nimbus.service.aop.InterceptorChainList InterceptorChainList}サービスのサービス名を指定する。<p>
     *
     * @param name InterceptorChainListサービスのサービス名
     */
    public void setInterceptorChainListServiceName(ServiceName name);
    
    /**
     * 挿入するInterceptorチェーンのリストである{@link jp.ossc.nimbus.service.aop.InterceptorChainList InterceptorChainList}サービスのサービス名を取得する。<p>
     *
     * @return InterceptorChainListサービスのサービス名
     */
    public ServiceName getInterceptorChainListServiceName();
    
    /**
     * インターセプトした対象を呼び出す{@link jp.ossc.nimbus.service.aop.Invoker Invoker}サービスのサービス名を設定する。<p>
     * デフォルトでは、{@link WrappedMethodReflectionCallInvokerService}が自動的に生成され使用される。<br>
     *
     * @param name InterceptorChainListサービスのサービス名
     */
    public void setInvokerServiceName(ServiceName name);
    
    /**
     * インターセプトした対象を呼び出す{@link jp.ossc.nimbus.service.aop.Invoker Invoker}サービスのサービス名を取得する。<p>
     *
     * @return Invokerサービスのサービス名
     */
    public ServiceName getInvokerServiceName();
    
    /**
     * このサービスを静的コンパイルに使用するかどうかを設定する。<p>
     * デフォルトは、false。<br>
     *
     * @param isStatic 静的コンパイルに使用する場合は、true
     */
    public void setStaticCompile(boolean isStatic);
    
    /**
     * このサービスを静的コンパイルに使用するかどうかを判定設定する。<p>
     *
     * @return trueの場合、静的コンパイルに使用する
     */
    public boolean isStaticCompile();
    
    /**
     * このアスペクト変換を識別するアスペクトのキーを設定する。<p>
     * 同じキーを持つアスペクトは、重複してアスペクトされない。<br>
     * デフォルトでは、サービス名が使用される。<br>
     *
     * @param key アスペクトのキー
     */
    public void setAspectKey(String key);
    
    /**
     * このアスペクト変換を識別するアスペクトのキーを取得する。<p>
     *
     * @return アスペクトのキー
     */
    public String getAspectKey();
    
    /**
     * NimbusClassLoaderにVMレベルでAspectTranslatorを登録するかどうかを設定する。<p>
     * デフォルトはtrueで、VMレベルでAspectTranslatorを登録する。falseに設定すると、ThreadContextレベルでAspectTranslatorを登録する。
     *
     * @param isRegister VMレベルでAspectTranslatorを登録する場合は、true
     */
    public void setRegisterVMClassLoader(boolean isRegister);
    
    /**
     * NimbusClassLoaderにVMレベルでAspectTranslatorを登録するかどうかを判定する。<p>
     *
     * @return trueの場合は、VMレベルでAspectTranslatorを登録する。falseの場合は、ThreadContextレベルでAspectTranslatorを登録する
     */
    public boolean isRegisterVMClassLoader();
}
