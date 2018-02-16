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

import java.util.Random;

import jp.ossc.nimbus.service.queue.QueueHandlerContainer;

/**
 * 世代。<p>
 *
 * @author M.Takata
 */
public interface Generation{
    
    /**
     * 収束条件を指定する。<p>
     *
     * @param condition 収束条件
     */
    public void setConvergenceCondition(ConvergenceCondition condition);
    
    /**
     * 世代競争を並列に行う場合の{@link QueueHandlerContainer}を指定する。<p>
     * 指定しない場合は、{@link #compete(int, long)}の実行毎に内部で生成する。<br>
     *
     * @param qhc 世代競争を並列に行う際に使用するQueueHandlerContainer
     */
    public void setQueueHandlerContainer(QueueHandlerContainer qhc);
    
    /**
     * 世代競争を並列に行う場合の{@link QueueHandlerContainer}を取得する。<p>
     *
     * @return 世代競争を並列に行う際に使用するQueueHandlerContainer
     */
    public QueueHandlerContainer getQueueHandlerContainer();
    
    /**
     * 適応値の並び順を設定する。<p>
     *
     * @param isAsc 昇順の場合、true
     */
    public void setFitnessOrder(boolean isAsc);
    
    /**
     * 適応値の並び順を取得する。<p>
     *
     * @return trueの場合、昇順
     */
    public boolean getFitnessOrder();
    
    /**
     * 世代番号を取得する。<p>
     *
     * @return 世代番号
     */
    public int getGenerationNo();
    
    /**
     * 初期世代を生成する。<p>
     *
     * @param random 乱数シード
     * @param seed テンプレートとなるシード
     * @param num シード数
     */
    public void init(Random random, Seed seed, int num);
    
    /**
     * シードを入れ替える。<p>
     *
     * @param seeds シード配列
     */
    public void setSeeds(Seed[] seeds);
    
    /**
     * この世代が持つ全てのシードを取得する。<p>
     *
     * @return シード配列
     */
    public Seed[] getSeeds();
    
    /**
     * 世代競争する。<p>
     *
     * @exception Exception 世代競争に失敗した場合
     */
    public void compete() throws Exception;
    
    /**
     * 並列処理で世代競争する。<p>
     *
     * @param threadNum 並列度。{@link #setQueueHandlerContainer(QueueHandlerContainer)}でQueueHandlerContainerを指定されている場合は、その設定に従うため無効
     * @param timeout 並列処理をする場合に、全ての並列処理スレッドの終了待ちをするタイムアウト[ms]
     * @exception Exception 世代競争に失敗した場合
     */
    public void compete(int threadNum, long timeout) throws Exception;
    
    /**
     * 次世代を生成する。<p>
     *
     * @param random 乱数シード
     * @param matchMaker シード仲人
     * @return 次世代。収束条件に到達した場合は、null
     */
    public Generation next(Random random, SeedMatchMaker matchMaker);
    
    /**
     * 生存者たる最適応者を取得する。<p>
     *
     * @return シード
     */
    public Seed getSurvivor();
}