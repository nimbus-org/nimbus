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
import java.util.Map;

/**
 * 遺伝情報。<p>
 *
 * @author M.Takata
 */
public interface Genom{
    
    /**
     * 指定した名前の遺伝子を取得する。<p>
     *
     * @param name 遺伝子名
     * @return 遺伝子
     */
    public Gene getGene(String name);
    
    /**
     * 遺伝子マップを取得する。<p>
     *
     * @return 遺伝子マップ。キーが遺伝子名、値が{@link Gene 遺伝子}
     */
    public Map getGeneMap();
    
    /**
     * 遺伝情報を乱数発生させる。<p>
     *
     * @param random 乱数シード
     */
    public void random(Random random);
    
    /**
     * この遺伝情報と指定された遺伝情報を交叉させる。<p>
     *
     * @param random 乱数シード
     * @param genom 交叉対象の遺伝情報
     */
    public void crossover(Random random, Genom genom);
    
    /**
     * この遺伝情報の複製を作成する。<p>
     *
     * @return 複製された遺伝情報
     */
    public Genom cloneGenom();
    
    /**
     * この遺伝情報が交叉されたかどうか。<p>
     *
     * @return trueの場合、交叉されている
     */
    public boolean isCrossover();
    
    /**
     * この遺伝情報が変異されたかどうか。<p>
     *
     * @return trueの場合、変異されている
     */
    public boolean isMutate();
}