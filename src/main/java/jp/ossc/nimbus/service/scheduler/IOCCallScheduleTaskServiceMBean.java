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
package jp.ossc.nimbus.service.scheduler;

import jp.ossc.nimbus.core.*;

/**
 * {@link IOCCallScheduleTaskService}サービスのMBeanインタフェース。<p>
 * 
 * @author M.Takata
 */
public interface IOCCallScheduleTaskServiceMBean extends ServiceBaseMBean{
    
    /**
     * IOC呼び出し種別キー 同期一括。<p>
     */
    public static final String IOC_CALL_TYPE_SYNCH = "Synch";
    
    /**
     * IOC呼び出し種別キー 同期並列。<p>
     */
    public static final String IOC_CALL_TYPE_SYNCH_PARALLEL = "SynchParallel";
    
    /**
     * IOC呼び出し種別キー 同期直列。<p>
     */
    public static final String IOC_CALL_TYPE_SYNCH_SEQUENCE = "SynchSequence";
    
    /**
     * IOC呼び出し種別キー 非同期一括。<p>
     */
    public static final String IOC_CALL_TYPE_ASYNCH = "Asynch";
    
    /**
     * IOC呼び出し種別キー 非同期直列。<p>
     */
    public static final String IOC_CALL_TYPE_ASYNCH_SEQUENCE = "AsynchSequence";
    
    /**
     * {@link jp.ossc.nimbus.service.ioccall.FacadeCaller FacadeCaller}サービスのサービス名を設定する。<p>
     *
     * @param name FacadeCallerサービスのサービス名
     */
    public void setFacadeCallerServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.ioccall.FacadeCaller FacadeCaller}サービスのサービス名を取得する。<p>
     *
     * @return FacadeCallerサービスのサービス名
     */
    public ServiceName getFacadeCallerServiceName();
    
    /**
     * 実行する業務フロー名を設定する。<p>
     *
     * @param names 業務フロー名
     */
    public void setBeanFlowNames(String[] names);
    
    /**
     * 実行する業務フロー名を取得する。<p>
     *
     * @return 業務フロー名
     */
    public String[] getBeanFlowNames();
    
    /**
     * 実行する業務フローへの入力オブジェクトを設定する。<p>
     *
     * @param in 業務フローへの入力オブジェクト
     */
    public void setBeanFlowInputs(Object[] in);
    
    /**
     * 実行する業務フローへの入力オブジェクトを取得する。<p>
     *
     * @return 業務フローへの入力オブジェクト
     */
    public Object[] getBeanFlowInputs();
    
    /**
     * IOC呼び出し種別を設定する。<p>
     * デフォルトでは、{@link #IOC_CALL_TYPE_SYNCH}。<br>
     *
     * @param type IOC呼び出し種別キー
     * @see #IOC_CALL_TYPE_SYNCH
     * @see #IOC_CALL_TYPE_SYNCH_PARALLEL
     * @see #IOC_CALL_TYPE_SYNCH_SEQUENCE
     * @see #IOC_CALL_TYPE_ASYNCH
     * @see #IOC_CALL_TYPE_ASYNCH_SEQUENCE
     */
    public void setIOCCallType(String type);
    
    /**
     * IOC呼び出し種別を取得する。<p>
     *
     * @return IOC呼び出し種別キー
     */
    public String getIOCCallType();
}