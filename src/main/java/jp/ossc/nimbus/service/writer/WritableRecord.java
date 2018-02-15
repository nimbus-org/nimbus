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

import java.util.*;

/**
 * 出力レコード。<p>
 * {@link WritableElement}を複数持ち、その順序性及びキーとのマッピング性を持つ。<br>
 * 
 * @author Y.Tokuda
 */
public class WritableRecord implements java.io.Serializable{
    
    private static final long serialVersionUID = 7030165706406015562L;
    
    //メンバ変数
    private Map mElements;
    
    /**
     * 空のインスタンスを生成する。<p>
     */
    public WritableRecord(){
        mElements = new LinkedHashMap();
    }
    
    /**
     * 要素を追加する。<p>
     *
     * @param elem 要素
     */
    public void addElement(WritableElement elem){
        mElements.put(elem.getKey() == null ? elem : elem.getKey(), elem);
    }
    
    /**
     * 要素のリストを取得する。<p>
     *
     * @return WritableElementのList
     */
    public List getElements(){
        return new ArrayList(mElements.values());
    }
    
    /**
     * 要素のマッピングを取得する。<p>
     *
     * @return WritableElementのマッピング
     */
    public Map getElementMap(){
        return mElements;
    }
    
    /**
     * 要素の文字列表現を取得する。<p>
     * {@link WritableElement}を追加された順序で、各WritableElementの{@link WritableElement#toString()}を呼び出し連結した文字列を返す。<br>
     *
     * @return 文字列表現
     */
    public String toString(){
        final StringBuilder ret = new StringBuilder();
        final Iterator elements = mElements.values().iterator();
        while(elements.hasNext()){
            ret.append(elements.next().toString());
        }
        return ret.toString();
    }
}
