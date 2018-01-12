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
package jp.ossc.nimbus.service.aop.interceptor.servlet;

import java.util.Map;

import jp.ossc.nimbus.core.ServiceName;

/**
 * {@link ExceptionHandlingInterceptorService}のMBeanインタフェース。<p>
 * 
 * @author M.Takata
 * @see ExceptionHandlingInterceptorService
 */
public interface ExceptionHandlingInterceptorServiceMBean
 extends ServletFilterInterceptorServiceMBean{
    
    /**
     * 例外クラス名と{@link ExceptionHandler}サービスのサービス名のマッピングを設定する。<p>
     * 例外クラスの持つ情報に対して、条件を付けたい場合は、例外クラス名(条件式)の書式で指定できる。<br>
     * 条件式は、The Apache Jakarta Projectの Commons Jexl(http://jakarta.apache.org/commons/jexl/)を使用する。<br>
     * 例外のプロパティを参照する場合は、プロパティを表現する文字列を"@exception."と"@"で囲んで指定する。<br>
     * ServletRequestのプロパティを参照する場合は、プロパティを表現する文字列を"@request."と"@"で囲んで指定する。<br>
     * ServletResponseのプロパティを参照する場合は、プロパティを表現する文字列を"@response."と"@"で囲んで指定する。<br>
     * ここで言う、プロパティの概念は、Java Beansのプロパティの概念より広く、{@link jp.ossc.nimbus.beans.PropertyFactory PropertyFactory}の規約に従う。<br>
     * <pre>
     *  例："java.sql.SQLException(@exception.ErrorCode@ == 1013)"=Nimbus#SQLExceptionHandler
     * </pre>
     *
     * @param map 例外クラス名とExceptionHandlerサービスのサービス名のマッピング。例外クラス名=ExceptionHandlerサービスのサービス名
     */
    public void setExceptionAndHandlerMapping(Map map);
    
    /**
     * 例外クラス名と{@link ExceptionHandler}サービスのサービス名のマッピングを取得する。<p>
     *
     * @return 例外クラス名とExceptionHandlerサービスのサービス名のマッピング
     */
    public Map getExceptionAndHandlerMapping();
    
    /**
     * 発生した例外にマッピングされた{@link ExceptionHandler}サービスがない場合に使用されるExceptionHandlerサービスのサービス名を設定する。<p>
     *
     * @param name ExceptionHandlerサービスのサービス名
     */
    public void setDefaultExceptionHandlerServiceName(ServiceName name);
    
    /**
     * 発生した例外にマッピングされた{@link ExceptionHandler}サービスがない場合に使用されるExceptionHandlerサービスのサービス名を取得する。<p>
     *
     * @return ExceptionHandlerサービスのサービス名
     */
    public ServiceName getDefaultExceptionHandlerServiceName();
}
