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
package jp.ossc.nimbus.service.cache;

import jp.ossc.nimbus.core.ServiceBaseMBean;

/**
 * {@link LRUOverflowAlgorithmService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see LRUOverflowAlgorithmService
 */
public interface LRUOverflowAlgorithmServiceMBean
 extends ServiceBaseMBean{
    
    /**
     * あふれアルゴリズムを実行するために保持している情報を初期化する。<p>
     */
    public void reset();
    
    /**
     * あふれアルゴリズム対象になっているキャッシュ数を取得する。<p>
     *
     * @return キャッシュ数
     */
    public int size();
    
    /**
     * あふれ回数を取得する。<p>
     *
     * @return あふれ回数
     */
    public long getOverflowCount();
    
    /**
     * あふれたキャッシュの平均キャッシュ時間[ms]を取得する。<p>
     *
     * @return 平均キャッシュ時間[ms]
     */
    public long getAverageOverflowCachedTime();
    
    /**
     * キャッシュの最終アクセス時間グラフを表示する。<p>
     *
     * @return 最終アクセス時間グラフ
     */
    public String displayReferenceTimes();
}
