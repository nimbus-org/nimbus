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
 * 半角全角文字列コンバータの抽象クラス。<p>
 * 
 * @author M.Takata
 */
public abstract class HankakuZenkakuStringConverter
 extends AbstractStringConverter implements HankakuZenkakuConverter{
    
    private static final long serialVersionUID = 2943964370458466319L;
    
    /**
     * 半角→全角変換種別の文字列コンバータを生成する。<p>
     */
    public HankakuZenkakuStringConverter(){
        super(HANKAKU_TO_ZENKAKU);
    }
    
    /**
     * 指定された変換種別の文字列コンバータを生成する。<p>
     *
     * @param type 変換種別
     * @see #HANKAKU_TO_ZENKAKU
     * @see #ZENKAKU_TO_HANKAKU
     */
    public HankakuZenkakuStringConverter(int type){
        super(type);
    }
    
    /**
     * 変換キャラクタ配列を取得する。<p>
     * {@link #getHankakuZenkakuChars()}を呼び出す。<br>
     *
     * @return 変換キャラクタ配列
     */
    protected final char[][] getConvertChars(){
        return getHankakuZenkakuChars();
    }
    
    /**
     * 変換文字列配列を取得する。<p>
     * {@link #getHankakuZenkakuStrings()}を呼び出す。<br>
     *
     * @return 変換文字列配列
     */
    protected final String[][] getConvertStrings(){
        return getHankakuZenkakuStrings();
    }
    
    /**
     * 半角全角変換キャラクタ配列を取得する。<p>
     *
     * @return 半角全角変換キャラクタ配列
     */
    protected abstract char[][] getHankakuZenkakuChars();
    
    /**
     * 半角全角変換文字列配列を取得する。<p>
     *
     * @return 半角全角変換文字列配列
     */
    protected abstract String[][] getHankakuZenkakuStrings();
}
