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
package jp.ossc.nimbus.service.context;

/**
 * 共有コンテキスト値差分情報サポート。<p>
 * 共有コンテキストの値として差分更新可能なBeanがサポートすべきインタフェース。<br>
 * 
 * @author M.Takata
 */
public interface SharedContextValueDifferenceSupport{
    
    /**
     * 更新バージョンを設定する。<p>
     *
     * @param version 更新バージョン
     */
    public void setUpdateVersion(int version);
    
    /**
     * 更新バージョンを取得する。<p>
     *
     * @return 更新バージョン
     */
    public int getUpdateVersion();
    
    /**
     * 指定された差分情報を受けて更新する。<p>
     *
     * @param diff 差分情報
     * @return 全て更新された場合、1。更新されたものと、更新する必要がなかったものが存在する場合、0。整合性が取れずに、更新できないものが存在する場合、-1。
     * @exception SharedContextUpdateException 差分の更新に失敗した場合
     */
    public int update(SharedContextValueDifference diff) throws SharedContextUpdateException;
    
    /**
     * このオブジェクトの複製を作成する。<p>
     *
     * @return このオブジェクトの複製
     */
    public Object clone();
    
    /**
     * このオブジェクトの更新を行うためのテンプレートとなるオブジェクトを取得する。<p>
     *
     * @return このオブジェクトの更新を行うためのテンプレートとなるオブジェクト
     */
    public Object getUpdateTemplate();
}