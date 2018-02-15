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
package jp.ossc.nimbus.beans;

import java.beans.*;

/**
 * char型のPropertyEditorクラス。<p>
 * 文字列をchar型のオブジェクトに変換する。<br>
 * "${"と"}"に囲まれた文字列は、同名のシステムプロパティと置換される。<br>
 * "${\t}"、"${\n}"、"${\r}"、"${\f}"は、エスケープシーケンスとして置換される。<br>
 * "0x"から始まる文字列は、16進文字としてcharに変換される。
 * "￥u"から始まる文字列は、ユニコード文字としてcharに変換される。<br>
 * <p>
 * 例：<br>
 * &nbsp;&nbsp;a<br>
 * <br>
 * のような文字列が<br>
 * <br>
 * &nbsp;&nbsp;'a'<br>
 * <br>
 * のように変換される。<br>
 *
 * @author M.Takata
 */
public class CharacterEditor extends PropertyEditorSupport
 implements java.io.Serializable{
    
    private static final long serialVersionUID = 8830930843268555968L;
    
    private static final String HEX_PREFIX = "0x";
    private static final String UNICODE_PREFIX = "\\u";
    
    /**
     * 指定された文字列を解析してプロパティ値を設定する。<p>
     *
     * @param text 解析される文字列
     */
    public void setAsText(String text){
        String charStr = Utility.replaceSystemProperty(text);
        if(charStr.length() == 1){
            setValue(new Character(charStr.charAt(0)));
        }else if(charStr.startsWith(HEX_PREFIX) && charStr.length() > 2){
            setValue(
                new Character((char)Integer.parseInt(charStr.substring(2), 16))
            );
        }else if(charStr.startsWith(UNICODE_PREFIX)
             && charStr.length() > 2){
            charStr = Utility.unicodeConvert(charStr);
            if(charStr.length() != 1){
                throw new IllegalArgumentException(
                    "Not a character. : " + charStr
                );
            }
            setValue(new Character(charStr.charAt(0)));
        }else{
            throw new IllegalArgumentException(
                "Not a character. : " + charStr
            );
        }
    }
    
    /**
     * プロパティ文字列を取得する。<p>
     *
     * @return プロパティ文字列
     */
    public String getAsText(){
        return String.valueOf(((Character)getValue()).charValue());
    }
}
