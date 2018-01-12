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

import java.util.Date;


/**
 * {@link java.sql.Date}型のPropertyEditorクラス。<p>
 * 日付文字列（yyyy/MM/dd HH:mm:ss SSS）をjava.sql.Date型のオブジェクトに変換する。<br>
 * "${"と"}"に囲まれた文字列は、同名のシステムプロパティと置換される。<br>
 * <p>
 * 例：<br>
 * &nbsp;&nbsp;2006/08/15 15:20:11 100<br>
 * <br>
 * のような文字列が<br>
 * <br>
 * &nbsp;&nbsp;new java.sql.Date(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss SSS").parse("2006/08/15 15:20:11 100").getTime())<br>
 * <br>
 * のように変換される。<br>
 * また、設定する必要のないフィールドは空にすると、そのフィールドの最小値に設定される。<br>
 * 例：<br>
 * &nbsp;&nbsp;//15 15::11<br>
 * <br>
 * のような文字列が<br>
 * <br>
 * &nbsp;&nbsp;new java.sql.Date(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss SSS").parse("1970/01/15 15:00:11 000").getTime())<br>
 * <br>
 * のように変換される。<br>
 * また、現在時刻から設定したい場合は、各フィールドに"NOW"を設定する。<br>
 * 例：<br>
 * &nbsp;&nbsp;NOW/NOW/15 15:NOW:11 NOW<br>
 * <br>
 * のような文字列が、現在日付を2006/09/01 13:59:40 150とすると<br>
 * <br>
 * &nbsp;&nbsp;new java.sql.Date(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss SSS").parse("2006/09/15 15:59:11 150").getTime())<br>
 * <br>
 * のように変換される。<br>
 * また、単純に現在時刻を設定したい場合は、"NOW"を設定する。<br>
 * 例：<br>
 * &nbsp;&nbsp;NOW<br>
 * <br>
 * のような文字列が、<br>
 * <br>
 * &nbsp;&nbsp;new java.sql.Date(System.currentTimeMillis())<br>
 * <br>
 * のように変換される。<br>
 *
 * @author M.Takata
 */
public class SQLDateEditor extends DateEditor
 implements java.io.Serializable{
    
    private static final long serialVersionUID = 4216221057481182615L;

    /**
     * 指定された文字列を解析してプロパティ値を設定する。<p>
     *
     * @param text 解析される文字列
     */
    public void setAsText(String text){
        super.setAsText(text);
        Date date = (Date)super.getValue();
        if(date != null){
            super.setValue(new java.sql.Date(date.getTime()));
        }
    }
}
