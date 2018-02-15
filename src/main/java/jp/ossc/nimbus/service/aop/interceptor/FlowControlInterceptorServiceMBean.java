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
 * {@link FlowControlInterceptorService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see FlowControlInterceptorService
 */
public interface FlowControlInterceptorServiceMBean extends ServiceBaseMBean{
    
    /**
     * セマフォサービスのサービス名を設定する。<p>
     *
     * @param name セマフォサービスのサービス名
     */
    public void setSemaphoreServiceName(ServiceName name);
    
    /**
     * セマフォサービスのサービス名を取得する。<p>
     * 
     * @return セマフォサービスのサービス名
     */
    public ServiceName getSemaphoreServiceName();
    
    /**
     * セマフォ獲得待ちタイムアウトを設定する。<p>
     * 0以下の値を設定すると無限にセマフォ獲得待ちする。デフォルトは、-1。
     *
     * @param timeout タイムアウト[ms]
     */
    public void setTimeout(long timeout);
    
    /**
     * セマフォ獲得待ちタイムアウトを取得する。<p>
     *
     * @return タイムアウト[ms]
     */
    public long getTimeout();
    
    /**
     * セマフォ獲得待ち最大数を設定する。<p>
     * 0以下の値を設定するとセマフォ獲得待ち数を制限しない。デフォルトは、-1。
     *
     * @param count セマフォ獲得待ち最大数
     */
    public void setMaxWaitingCount(int count);
    
    /**
     * セマフォ獲得待ち最大数を取得する。<p>
     *
     * @return セマフォ獲得待ち最大数
     */
    public int getMaxWaitingCount();
    
    /**
     * セマフォ獲得後の強制セマフォ開放時間[ms]を設定する。<p>
     * 0以下の値を設定すると強制セマフォ開放を行わない。デフォルトは、-1。
     *
     * @param timeout 強制セマフォ開放時間
     */
    public void setForceFreeTimeout(long timeout);
    
    /**
     * セマフォ獲得後の強制セマフォ開放時間[ms]を取得する。<p>
     *
     * @return 強制セマフォ開放時間
     */
    public long getForceFreeTimeout();
    
    /**
     * セマフォ獲得に失敗した場合に例外をthrowするかどうかを設定する。<p>
     * デフォルトはtrue。
     * 
     * @param isThrow セマフォ獲得に失敗した場合に例外をthrowする場合true
     */
    public void setFailToObtainSemaphore(boolean isThrow);
    
    /**
     * セマフォ獲得に失敗した場合に例外をthrowするかどうかを判定する。<p>
     * 
     * @return セマフォ獲得に失敗した場合に例外をthrowする場合true
     */
    public boolean isFailToObtainSemaphore();
}
