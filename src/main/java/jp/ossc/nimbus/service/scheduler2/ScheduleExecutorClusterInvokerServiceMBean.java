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
package jp.ossc.nimbus.service.scheduler2;

import jp.ossc.nimbus.service.proxy.invoker.*;

/**
 * {@link ScheduleExecutorClusterInvokerService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see ScheduleExecutorClusterInvokerService
 */
public interface ScheduleExecutorClusterInvokerServiceMBean
 extends ClusterInvokerServiceMBean{
    
    /**
     * ScheduleExecutorを特定するキーを設定する。<p>
     *
     * @param key キー
     */
    public void setKey(String key);
    
    /**
     * ScheduleExecutorを特定するキーを取得する。<p>
     *
     * @return キー
     */
    public String getKey();
    
    /**
     * ScheduleExecutorが実行し得るスケジュール実行の種類を設定する。<p>
     *
     * @param type スケジュール実行の種類
     */
    public void setType(String type);
    
    /**
     * ScheduleExecutorが実行し得るスケジュール実行の種類を取得する。<p>
     *
     * @return スケジュール実行の種類
     */
    public String getType();
}