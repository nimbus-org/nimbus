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
package jp.ossc.nimbus.service.sequence;

import jp.ossc.nimbus.core.ServiceBaseMBean;

/**
 * {@link NumericSequenceService}のMBeanインタフェース。<p>
 * 
 * @author H.Nakano
 */
public interface NumericSequenceServiceMBean extends ServiceBaseMBean {
    
    /**
     * 発番する番号のフォーマットを設定する。<p>
     * フォーマットの指定方法は、"最小値;最大値"で指定する。
     * 
     * @param format 発番する番号のフォーマット文字列
     */
    public void setFormat(String format);
    
    /**
     * 発番する番号のフォーマットを取得する。<p>
     * 
     * @return 発番する番号のフォーマット文字列
     */
    public String getFormat();
    
    /**
     * 発番される番号の最小値を取得する。<p>
     * 
     * @return 最小値
     */
    public String getMinValue();
    
    /**
     * 発番される番号の最大値を取得する。<p>
     * 
     * @return 最大値
     */
    public String getMaxValue();
}
