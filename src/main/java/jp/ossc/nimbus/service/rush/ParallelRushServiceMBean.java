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
package jp.ossc.nimbus.service.rush;

import jp.ossc.nimbus.core.*;

/**
 * {@link ParallelRushService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see ParallelRushService
 */
public interface ParallelRushServiceMBean extends ServiceBaseMBean{
    
    /**
     * シナリオ名を設定する。<p>
     * 他のラッシュサービスとの通信で使用するサブジェクトとしても使用する。<p>
     * デフォルトは、マネージャ名#サービス名。<br>
     *
     * @param name シナリオ名
     */
    public void setScenarioName(String name);
    
    /**
     * シナリオ名を取得する。<p>
     *
     * @return シナリオ名
     */
    public String getScenarioName();
    
    /**
     * {@link Rush}サービスのサービス名の配列を設定する。<p>
     *
     * @param names Rushサービスのサービス名の配列
     */
    public void setRushServiceNames(ServiceName[] names);
    
    /**
     * {@link Rush}サービスのサービス名の配列を取得する。<p>
     *
     * @return Rushサービスのサービス名の配列
     */
    public ServiceName[] getRushServiceNames();
    
    /**
     * サービスの開始時に、ラッシュを開始するかどうかを判定する。<p>
     *
     * @return trueの場合、サービスの開始時に、ラッシュを開始する
     */
    public boolean isStartRushOnStart();
    
    /**
     * サービスの開始時に、ラッシュを開始するかどうかを設定する。<p>
     *
     * @param isStart サービスの開始時に、ラッシュを開始する場合は、true
     */
    public void setStartRushOnStart(boolean isStart);
    
    /**
     * サービスの開始時に、ラッシュを開始する時に、ラッシュが終了するのを待つかどうかを判定する。<p>
     * デフォルトは、trueで、待たない。<br>
     *
     * @return falseの場合は、待つ
     */
    public boolean isNoWait();
    
    /**
     * サービスの開始時に、ラッシュを開始する時に、ラッシュが終了するのを待つかどうかを設定する。<p>
     * デフォルトは、trueで、待たない。<br>
     *
     * @param noWait 待つ場合は、false
     */
    public void setNoWait(boolean noWait);
    
    /**
     * ラッシュを開始する。<p>
     *
     * @param noWait ラッシュが終了するのを待つ場合、false
     * @exception Exception ラッシュの開始に失敗した場合
     */
    public void startRush(boolean noWait) throws Exception;
    
    /**
     * ラッシュを停止する。<p>
     */
    public void stopRush();
}
