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
package jp.ossc.nimbus.service.writer;

/**
 * MessageWriterが書き込み可能な要素を表すインタフェース。<p>
 * キーと値の概念を持つ。また、文字列への変換、任意のオブジェクトへの変換を行う機能を持つ。<br>
 * 
 * @author Y.Tokuda
 */
public interface WritableElement {
    
    /**
     * この要素を表すキーを設定する。<p>
     *
     * @param key キー
     */
    public void setKey(Object key);
    
    /**
     * この要素を表すキーを取得する。<p>
     *
     * @return キー
     */
    public Object getKey();
    
    /**
     * この要素の文字列表現を取得する。<p>
     * 
     * @return この要素の文字列表現
     */
    public String toString();
    
    /**
     * この要素の編集後オブジェクトを取得する。<p>
     * 
     * @return この要素の編集後オブジェクト
     */
    public Object toObject();
    
    /**
    * 要素の値を設定する。<p>
    *
    * @param obj 要素の値のオブジェクト
    */
    public void setValue(Object obj);
    
    /**
    * 要素の値を取得する。<p>
    *
    * @return 要素の値のオブジェクト
    */
    public Object getValue();
}
