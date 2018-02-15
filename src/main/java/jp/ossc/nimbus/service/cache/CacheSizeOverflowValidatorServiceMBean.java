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
 * {@link CacheSizeOverflowValidatorService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see CacheSizeOverflowValidatorService
 */
public interface CacheSizeOverflowValidatorServiceMBean
 extends ServiceBaseMBean{
    
    /**
     * 保持するキャッシュの最大数を設定する。<p>
     * 有効な値の範囲は、0以上。デフォルトは0で、溢れない。<br>
     *
     * @param size 保持するキャッシュの最大数
     */
    public void setMaxSize(int size) throws IllegalArgumentException;
    
    /**
     * 保持するキャッシュの最大数を取得する。<p>
     *
     * @return 保持するキャッシュの最大数
     */
    public int getMaxSize();
    
    /**
     * あふれ閾値を設定する。<p>
     * あふれが発生する場合に、この閾値まであふれさせる。<br>
     * デフォルトは0で、最大数を越えた分だけあふれさせる。<br>
     *
     * @param threshold あふれ閾値
     */
    public void setOverflowThreshold(int threshold);
    
    /**
     * あふれ閾値を取得する。<p>
     *
     * @return あふれ閾値
     */
    public int getOverflowThreshold();
    
    /**
     * キャッシュ数を検証するために保持している情報を初期化する。<p>
     */
    public void reset();
    
    /**
     * あふれ検証を行う。<p>
     *
     * @return あふれ検証を行った結果あふれが発生する場合、あふれ数を返す。あふれない場合は、0を返す
     */
    public int validate();
    
    /**
     * あふれ検証対象になっているキャッシュ数を取得する。<p>
     *
     * @return キャッシュ数
     */
    public int size();
}
