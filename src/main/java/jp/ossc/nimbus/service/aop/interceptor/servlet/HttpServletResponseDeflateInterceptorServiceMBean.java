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
 * {@link HttpServletResponseDeflateInterceptorService}のMBeanインタフェース。<p>
 * 
 * @author M.Takata
 * @see HttpServletResponseDeflateInterceptorService
 */
public interface HttpServletResponseDeflateInterceptorServiceMBean
 extends ServletFilterInterceptorServiceMBean{
    
    public void setEnabledContentTypes(String[] contentTypes);
    public String[] getEnabledContentTypes();
    
    public void setDisabledContentTypes(String[] contentTypes);
    public String[] getDisabledContentTypes();
    
    public void setDeflateLength(int length);
    public int getDeflateLength();
    
    /**
     * 圧縮時間を記録する{@link jp.ossc.nimbus.service.performance.PerformanceRecorder PerformanceRecorder}サービスのサービス名を設定する。<p>
     *
     * @param name PerformanceRecorderサービスのサービス名
     */
    public void setPerformanceRecorderServiceName(ServiceName name);
    
    /**
     * 圧縮時間を記録する{@link jp.ossc.nimbus.service.performance.PerformanceRecorder PerformanceRecorder}サービスのサービス名を取得する。<p>
     *
     * @return PerformanceRecorderサービスのサービス名
     */
    public ServiceName getPerformanceRecorderServiceName();
    
    /**
     * 圧縮前バイト数を記録する{@link jp.ossc.nimbus.service.performance.PerformanceRecorder PerformanceRecorder}サービスのサービス名を設定する。<p>
     *
     * @param name PerformanceRecorderサービスのサービス名
     */
    public void setBeforeCompressSizePerformanceRecorderServiceName(ServiceName name);
    
    /**
     * 圧縮前バイト数を記録する{@link jp.ossc.nimbus.service.performance.PerformanceRecorder PerformanceRecorder}サービスのサービス名を取得する。<p>
     *
     * @return PerformanceRecorderサービスのサービス名
     */
    public ServiceName getBeforeCompressSizePerformanceRecorderServiceName();
    
    /**
     * 圧縮後バイト数を記録する{@link jp.ossc.nimbus.service.performance.PerformanceRecorder PerformanceRecorder}サービスのサービス名を設定する。<p>
     *
     * @param name PerformanceRecorderサービスのサービス名
     */
    public void setAfterCompressSizePerformanceRecorderServiceName(ServiceName name);
    
    /**
     * 圧縮後バイト数を記録する{@link jp.ossc.nimbus.service.performance.PerformanceRecorder PerformanceRecorder}サービスのサービス名を取得する。<p>
     *
     * @return PerformanceRecorderサービスのサービス名
     */
    public ServiceName getAfterCompressSizePerformanceRecorderServiceName();
    
    /**
     * 処理した応答回数を取得する。<p>
     *
     * @return 応答回数
     */
    public long getResponseCount();
    
    /**
     * 処理した応答回数のうち、圧縮対象とした回数を取得する。<p>
     *
     * @return 圧縮対象とした回数
     */
    public long getCompressCount();
    
    /**
     * 圧縮対象とした回数のうち、圧縮した回数を取得する。<p>
     *
     * @return 圧縮した回数
     */
    public long getCompressedCount();
    
    /**
     * 処理した応答回数のうち、圧縮対象とした回数の比率を取得する。<p>
     *
     * @return 圧縮対象とした回数の比率
     */
    public double getCompressRate();
    
    /**
     * 圧縮対象とした回数のうち、圧縮した回数の比率を取得する。<p>
     *
     * @return 圧縮した回数の比率
     */
    public double getCompressedRate();
    
    /**
     * 圧縮した時の平均圧縮率を取得する。<p>
     *
     * @return 平均圧縮率
     */
    public double getAverageCompressionRate();
}