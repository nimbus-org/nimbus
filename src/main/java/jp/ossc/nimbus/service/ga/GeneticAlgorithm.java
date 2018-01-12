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

/**
 * 遺伝的アルゴリズム。<p>
 *
 * @author M.Takata
 */
public interface GeneticAlgorithm{
    
    /**
     * シード仲人を取得する。<p>
     *
     * @return シード仲人
     */
    public SeedMatchMaker getSeedMatchMaker();
    
    /**
     * 収束条件を取得する。<p>
     *
     * @return 収束条件
     */
    public ConvergenceCondition getConvergenceCondition();
    
    /**
     * 初期世代を生成する。<p>
     *
     * @param random 乱数シード
     * @param seed テンプレートとなるシード
     * @param seedNum 1世代あたりのシード数
     * @param isAsc 適応値の優先順位を昇順にするかどうか
     * @return 初期世代
     */
    public Generation createGeneration(Random random, Seed seed, int seedNum, boolean isAsc);
    
    /**
     * 指定された世代の世代競争を行い、次世代を返す。<p>
     *
     * @param random 乱数シード
     * @param generation 世代
     * @return 次世代。収束条件に到達した場合は、null
     */
    public Generation compete(Random random, Generation generation) throws Exception;
    
    /**
     * 指定された世代数だけ世代競争を行い、最終世代の最適応者を返す。<p>
     *
     * @param random 乱数シード
     * @param seed テンプレートとなるシード
     * @param seedNum 1世代あたりのシード数
     * @param isAsc 適応値の優先順位を昇順にするかどうか
     * @return 生存者たる最適応者
     * @exception Exception 世代競争に失敗した場合
     */
    public Seed compete(Random random, Seed seed, int seedNum, boolean isAsc) throws Exception;
}