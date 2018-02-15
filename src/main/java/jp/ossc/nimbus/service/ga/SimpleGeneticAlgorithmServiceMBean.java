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
package jp.ossc.nimbus.service.ga;

import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceBaseMBean;

/**
 * {@link SimpleGeneticAlgorithmService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see SimpleGeneticAlgorithmService
 */
public interface SimpleGeneticAlgorithmServiceMBean extends ServiceBaseMBean{
    
    /**
     * {@link SeedMatchMaker}サービスのサービス名を設定する。<p>
     *
     * @param name SeedMatchMakerサービスのサービス名
     */
    public void setSeedMatchMakerServiceName(ServiceName name);
    
    /**
     * {@link SeedMatchMaker}サービスのサービス名を取得する。<p>
     *
     * @return SeedMatchMakerサービスのサービス名
     */
    public ServiceName getSeedMatchMakerServiceName();
    
    /**
     * 世代競争時の各シードの適応値計算を並列に行う場合に使用する{@link jp.ossc.nimbus.service.queue.QueueHandlerContainer QueueHandlerContainer}サービスのサービス名を設定する。<p>
     *
     * @param name QueueHandlerContainerサービスのサービス名
     */
    public void setQueueHandlerContainerServiceName(ServiceName name);
    
    /**
     * 世代競争時の各シードの適応値計算を並列に行う場合に使用する{@link jp.ossc.nimbus.service.queue.QueueHandlerContainer QueueHandlerContainer}サービスのサービス名を取得する。<p>
     *
     * @return QueueHandlerContainerサービスのサービス名
     */
    public ServiceName getQueueHandlerContainerServiceName();
    
    /**
     * 世代競争時の各シードの適応値計算を並列に行う場合のスレッド数を設定する。<p>
     * {@link #setQueueHandlerContainerServiceName(ServiceName)}が指定されている場合は、その{@link jp.ossc.nimbus.service.queue.QueueHandlerContainer QueueHandlerContainer}サービスの{@link jp.ossc.nimbus.service.queue.QueueHandlerContainer#getQueueHandlerSize() getQueueHandlerSize()}が優先される。<br>
     * デフォルトは、0で直列に適応値計算を行う。<br>
     *
     * @param num シードの適応値計算の並列度
     */
    public void setParallelThreadNum(int num);
    
    /**
     * 世代競争時の各シードの適応値計算を並列に行う場合のスレッド数を取得する。<p>
     *
     * @return シードの適応値計算の並列度
     */
    public int getParallelThreadNum();
    
    /**
     * 世代競争時の各シードの適応値計算を並列に行う場合の応答待ち時間[ms]を設定する。<p>
     * デフォルトは、-1で無限待ち。<br>
     *
     * @param timeout 応答待ち時間[ms]
     */
    public void setParallelResponseTimout(long timeout);
    
    /**
     * 世代競争時の各シードの適応値計算を並列に行う場合の応答待ち時間[ms]を取得する。<p>
     *
     * @return 応答待ち時間[ms]
     */
    public long getParallelResponseTimout();
    
    /**
     * 次世代を生成した後に、同じシードを排除し、新しいシードと入れ替える淘汰を行うかどうかを設定する。<p>
     * デフォルトは、falseで淘汰は行わない。<br>
     *
     * @param isSelection 淘汰する場合、true
     */
    public void setSeedSelection(boolean isSelection);
    
    /**
     * 次世代を生成した後に、同じシードを排除し、新しいシードと入れ替える淘汰を行うかどうかを判定する。<p>
     *
     * @return trueの場合、淘汰する
     */
    public boolean isSeedSelection();
}
