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
 * ストリームを文字列として解釈してオブジェクトとの相互変換を行うコンバータのインタフェース。<p>
 * 
 * @author M.Takata
 */
public interface StreamStringConverter extends StreamConverter{
    
    /**
     * オブジェクトからストリームへ変換する際の文字エンコーディングを設定する。<p>
     *
     * @param encoding オブジェクトからストリームへ変換する際の文字エンコーディング
     */
    public void setCharacterEncodingToStream(String encoding);
    
    /**
     * オブジェクトからストリームへ変換する際の文字エンコーディングを取得する。<p>
     *
     * @return オブジェクトからストリームへ変換する際の文字エンコーディング
     */
    public String getCharacterEncodingToStream();
    
    /**
     * ストリームからオブジェクトへ変換する際の文字エンコーディングを設定する。<p>
     *
     * @param encoding ストリームからオブジェクトへ変換する際の文字エンコーディング
     */
    public void setCharacterEncodingToObject(String encoding);
    
    /**
     * ストリームからオブジェクトへ変換する際の文字エンコーディングを取得する。<p>
     *
     * @return ストリームからオブジェクトへ変換する際の文字エンコーディング
     */
    public String getCharacterEncodingToObject();
    
    /**
     * オブジェクトからストリームへ変換する際の文字エンコーディングを設定した複製を作成する。<p>
     *
     * @param encoding オブジェクトからストリームへ変換する際の文字エンコーディング
     * @return 複製。但し、指定したencodingが設定されているエンコーディングと等しい場合には、複製しない。
     */
    public StreamStringConverter cloneCharacterEncodingToStream(String encoding);
    
    /**
     * ストリームからオブジェクトへ変換する際の文字エンコーディングを設定した複製を作成する。。<p>
     *
     * @param encoding ストリームからオブジェクトへ変換する際の文字エンコーディング
     * @return 複製。但し、指定したencodingが設定されているエンコーディングと等しい場合には、複製しない。
     */
    public StreamStringConverter cloneCharacterEncodingToObject(String encoding);
}
