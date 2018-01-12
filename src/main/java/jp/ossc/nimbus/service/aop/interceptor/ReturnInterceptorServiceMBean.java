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

import jp.ossc.nimbus.core.*;

/**
 * {@link ReturnInterceptorService}のMBeanインタフェース。<p>
 * 
 * @author M.Takata
 * @see ReturnInterceptorService
 */
public interface ReturnInterceptorServiceMBean
 extends ServiceBaseMBean{
    
    /**
     * 有効/無効を設定する。<p>
     *
     * @param enabled 有効にする場合は、true
     */
    public void setEnabled(boolean enabled);
    
    /**
     * 有効/無効を判定する。<p>
     *
     * @return trueの場合、有効
     */
    public boolean isEnabled();
    
    /**
     * 呼び出し情報{@link jp.ossc.nimbus.service.aop.InvocationContext InvocationContext}に対する条件と戻り値を設定する。<p>
     * 条件式は、The Apache Jakarta Projectの Commons Jexl(http://jakarta.apache.org/commons/jexl/)を使用する。<br>
     * InvocationContextのプロパティを参照する場合は、プロパティを表現する文字列を"@"で囲んで指定する。ここで言う、プロパティの概念は、Java Beansのプロパティの概念より広く、{@link jp.ossc.nimbus.beans.PropertyFactory PropertyFactory}の規約に従う。<br>
     *
     * @param condition 条件式
     * @param value 戻り値
     */
    public void setReturnValue(String condition, Object value);
    
    /**
     * 呼び出し情報{@link jp.ossc.nimbus.service.aop.InvocationContext InvocationContext}に対する条件に対応する戻り値を取得する。<p>
     *
     * @param condition 条件式
     * @return 戻り値
     */
    public Object getReturnValue(String condition);
    
    /**
     * 戻り値を設定する。<p>
     *
     * @param value 戻り値
     */
    public void setReturnValue(Object value);
    
    /**
     * 戻り値を取得する。<p>
     *
     * @return 戻り値
     */
    public Object getReturnValue();
    
    /**
     * 呼び出し情報{@link jp.ossc.nimbus.service.aop.InvocationContext InvocationContext}に対する条件と戻りサービス名を設定する。<p>
     * 条件式は、The Apache Jakarta Projectの Commons Jexl(http://jakarta.apache.org/commons/jexl/)を使用する。<br>
     * InvocationContextのプロパティを参照する場合は、プロパティを表現する文字列を"@"で囲んで指定する。ここで言う、プロパティの概念は、Java Beansのプロパティの概念より広く、{@link jp.ossc.nimbus.beans.PropertyFactory PropertyFactory}の規約に従う。<br>
     *
     * @param condition 条件式
     * @param name 戻りサービス名
     */
    public void setReturnServiceName(String condition, ServiceName name);
    
    /**
     * 呼び出し情報{@link jp.ossc.nimbus.service.aop.InvocationContext InvocationContext}に対する条件に対応する戻りサービス名を取得する。<p>
     *
     * @param condition 条件式
     * @return 戻りサービス名
     */
    public ServiceName getReturnServiceName(String condition);
    
    /**
     * 戻りサービス名を設定する。<p>
     *
     * @param name 戻りサービス名
     */
    public void setReturnServiceName(ServiceName name);
    
    /**
     * 戻りサービス名を取得する。<p>
     *
     * @return 戻りサービス名
     */
    public ServiceName getReturnServiceName();
    
    /**
     * 戻り値をラップする際のインターフェースを設定する。<p>
     *
     * @param clazz インターフェース
     */
    public void setReturnInterfaceClass(Class clazz);
    
    /**
     * 戻り値をラップする際のインターフェースを取得する。<p>
     *
     * @return インターフェース
     */
    public Class getReturnInterfaceClass();
    
    /**
     * 戻り値をラップしたプロキシに挟み込む{@link jp.ossc.nimbus.service.aop.InterceptorChainList InterceptorChainList}サービスのサービス名を設定する。<p>
     *
     * @param name InterceptorChainListサービスのサービス名
     */
    public void setInterceptorChainListServiceName(ServiceName name);
    
    /**
     * 戻り値をラップしたプロキシに挟み込む{@link jp.ossc.nimbus.service.aop.InterceptorChainList InterceptorChainList}サービスのサービス名を取得する。<p>
     *
     * @return InterceptorChainListサービスのサービス名
     */
    public ServiceName getInterceptorChainListServiceName();
}