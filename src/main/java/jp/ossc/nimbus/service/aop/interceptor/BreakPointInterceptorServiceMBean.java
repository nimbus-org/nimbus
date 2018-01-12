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
import jp.ossc.nimbus.util.SynchronizeMonitor;

/**
 * {@link BreakPointInterceptorService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see BreakPointInterceptorService
 */
public interface BreakPointInterceptorServiceMBean extends ServiceBaseMBean, BreakPoint{
    
    /**
     * ブレイクポイントを入口側で貼る事を示す定数。<p>
     */
    public static final int BREAK_POINT_IN = 1;
    
    /**
     * ブレイクポイントを出口側で貼る事を示す定数。<p>
     */
    public static final int BREAK_POINT_OUT = 2;
    
    /**
     * ブレイクポイントを貼る場所を設定する。<p>
     * デフォルトは、{@link #BREAK_POINT_IN}。<br>
     *
     * @param breakPoint ブレイクポイントを貼る場所
     * @see #BREAK_POINT_IN
     * @see #BREAK_POINT_OUT
     */
    public void setBreakPoint(int breakPoint);
    
    /**
     * ブレイクポイントを貼る場所を取得する。<p>
     *
     * @return ブレイクポイントを貼る場所
     */
    public int getBreakPoint();
    
    /**
     * ブレイクポイントで、wait()する際のモニターを設定する。<p>
     *
     * @param monitor wait()のモニター
     */
    public void setMonitor(SynchronizeMonitor monitor);
    
    /**
     * ブレイクポイントで、wait()する際のモニターを取得する。<p>
     *
     * @return wait()のモニター
     */
    public SynchronizeMonitor getMonitor();
    
    /**
     * ブレイクポイントで待機する最大時間[ms]を設定する。<p>
     * デフォルトは、0で無限に待つ。<br>
     * 最大時間まで待機した後は、自動的に再開される。<br>
     *
     * @param timeout ブレイクポイントで待機する最大時間[ms]
     */
    public void setTimeout(long timeout);
    
    /**
     * ブレイクポイントで待機する最大時間[ms]を取得する。<p>
     *
     * @return ブレイクポイントで待機する最大時間[ms]
     */
    public long getTimeout();
}
