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

import jp.ossc.nimbus.core.ServiceName;

/**
 * {@link HttpServletRequestTransferInterceptorService}のMBeanインタフェース。
 * <p>
 *
 * @author M.Ishida
 * @see HttpServletRequestTransferInterceptorService
 */
public interface HttpServletRequestURLConvertInterceptorServiceMBean extends ServletFilterInterceptorServiceMBean {

    /**
     * ThreadContextサービスのサービス名を設定する。
     * <p>
     *
     * @param name ThreadContextサービスのサービス名
     */
    public void setThreadContextServiceName(ServiceName name);

    /**
     * ThreadContextサービスのサービス名を取得する。
     * <p>
     *
     * @return ThreadContextサービスのサービス名
     */
    public ServiceName getThreadContextServiceName();

    /**
     * ジャーナルサービス名を設定する。
     * <p>
     *
     * @param name ジャーナルサービス名
     */
    public void setJournalServiceName(ServiceName name);

    /**
     * ジャーナルサービス名を取得する。
     * <p>
     *
     * @return ジャーナルサービス名
     */
    public ServiceName getJournalServiceName();

    /**
     * パスの一部をパラメータとして扱うためのパス文字列定義を設定する。<p>
     * パス文字列として{}で括られているパスをパラメータとして判断し、<br>
     * 設定されているThreadContext、Journaalに設定する。<br>
     *
     * @param paths パス文字列定義
     */
    public void setResourcePaths(String[] paths);

    /**
     * パス文字列定義を取得する。<p>
     *
     * @return パス文字列定義
     */
    public String[] getResourcePaths();
}