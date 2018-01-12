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
 * {@link RetryInterceptorService}のMBeanインタフェース。<p>
 * 
 * @author M.Takata
 * @see RetryInterceptorService
 */
public interface RetryInterceptorServiceMBean
 extends ServiceBaseMBean{
    
    public static final String DEFAULT_RETRY_COUNT_ATTRIBUTE_NAME
         = RetryInterceptorService.class.getName().replaceAll("\\.", "_") + "_RETRY_COUNT";
    
    /**
     * リトライする戻り値の条件を設定する。<p>
     * 条件式は、The Apache Jakarta Projectの Commons Jexl(http://jakarta.apache.org/commons/jexl/)を使用する。<br>
     * 戻り値自体を参照する場合は、"value"という予約語を使用する。<br>
     * 戻り値のプロパティを参照する場合は、プロパティを表現する文字列を"@"で囲んで指定する。ここで言う、プロパティの概念は、Java Beansのプロパティの概念より広く、{@link jp.ossc.nimbus.beans.PropertyFactory PropertyFactory}の規約に従う。<br>
     *
     * @param conditions 条件式配列
     */
    public void setReturnConditions(String[] conditions);
    
    /**
     * リトライする戻り値の条件を取得する。<p>
     *
     * @return 条件式配列
     */
    public String[] getReturnConditions();
    
    /**
     * リトライする例外のクラス名とその条件を設定する。<p>
     * 条件式は、The Apache Jakarta Projectの Commons Jexl(http://jakarta.apache.org/commons/jexl/)を使用する。<br>
     * 例外のプロパティを参照する場合は、プロパティを表現する文字列を"@"で囲んで指定する。ここで言う、プロパティの概念は、Java Beansのプロパティの概念より広く、{@link jp.ossc.nimbus.beans.PropertyFactory PropertyFactory}の規約に従う。<br>
     *
     * @param conditions 例外クラス名:条件式（条件が必要ない場合は、:以下を省略可能）の文字列配列
     */
    public void setExceptionConditions(String[] conditions);
    
    /**
     * リトライする例外のクラス名とその条件を取得する。<p>
     *
     * @return 例外クラス名:条件式（条件が必要ない場合は、:以下を省略可能）の文字列配列
     */
    public String[] getExceptionConditions();
    
    /**
     * リトライする回数を設定する。<p>
     * デフォルトは、1。<br>
     *
     * @param count リトライする回数
     */
    public void setMaxRetryCount(int count);
    
    /**
     * リトライする回数を取得する。<p>
     *
     * @return リトライする回数
     */
    public int getMaxRetryCount();
    
    /**
     * 現在のリトライ回数を保持する{@link jp.ossc.nimbus.service.aop.InvocationContext InvocationContext}の属性名を設定する。<p>
     * デフォルトは、{@link #DEFAULT_RETRY_COUNT_ATTRIBUTE_NAME}。<br>
     *
     * @param name 現在のリトライ回数を保持するInvocationContextの属性名
     */
    public void setRetryCountAttributeName(String name);
    
    /**
     * 現在のリトライ回数を保持する{@link jp.ossc.nimbus.service.aop.InvocationContext InvocationContext}の属性名を取得する。<p>
     *
     * @return 現在のリトライ回数を保持するInvocationContextの属性名
     */
    public String getRetryCountAttributeName();
    
    /**
     * リトライする間隔[ms]を設定する。<p>
     * デフォルトは、0で間隔をあけずにリトライする。<br>
     *
     * @param millis リトライする間隔[ms]
     */
    public void setRetryInterval(long millis);
    
    /**
     * リトライする間隔[ms]を取得する。<p>
     *
     * @return リトライする間隔[ms]
     */
    public long getRetryInterval();
}