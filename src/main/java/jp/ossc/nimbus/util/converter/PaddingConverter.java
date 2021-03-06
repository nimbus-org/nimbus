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
package jp.ossc.nimbus.util.converter;

/**
 * パディングコンバータのインタフェース。<p>
 * 
 * @author M.Takata
 */
public interface PaddingConverter extends ReversibleConverter{
    
    /**
     * パディング変換を表す変換種別定数。<p>
     */
    public static final int PADDING = POSITIVE_CONVERT;
    
    /**
     * パース変換を表す変換種別定数。<p>
     */
    public static final int PARSE = REVERSE_CONVERT;
    
    /**
     * 左寄せを表すパディング方向定数。<p>
     */
    public static final int DIRECTION_LEFT = 1;
    
    /**
     * 右寄せを表すパディング方向定数。<p>
     */
    public static final int DIRECTION_RIGHT = 2;
    
    /**
     * 中央寄せを表すパディング方向定数。<p>
     */
    public static final int DIRECTION_CENTER = 3;
    
    /**
     * パディング長を設定する。<p>
     *
     * @param length パディング長
     */
    public void setPaddingLength(int length);
    
    /**
     * パディング長を取得する。<p>
     *
     * @return パディング長
     */
    public int getPaddingLength();
    
    /**
     * パディング方向を設定する。<p>
     *
     * @param direct パディング方向
     */
    public void setPaddingDirection(int direct);
    
    /**
     * パディング方向を取得する。<p>
     *
     * @return パディング方向
     */
    public int getPaddingDirection();
}
