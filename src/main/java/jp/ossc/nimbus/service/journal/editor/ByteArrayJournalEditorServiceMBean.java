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
package jp.ossc.nimbus.service.journal.editor;

import jp.ossc.nimbus.core.ServiceName;

/**
 * {@link ByteArrayJournalEditorService}のMBeanインタフェース。<p>
 * 
 * @author M.Takata
 * @see ByteArrayJournalEditorService
 */
public interface ByteArrayJournalEditorServiceMBean
 extends ImmutableJournalEditorServiceBaseMBean{
    
    /**
     * 16進文字列に変換するモード。<p>
     */
    public static final String CONVERT_HEX = "HEX";
    
    /**
     * 10進文字列に変換するモード。<p>
     */
    public static final String CONVERT_DECIMAL = "DECIMAL";
    
    /**
     * 8進文字列に変換するモード。<p>
     */
    public static final String CONVERT_OCTAL = "OCTAL";
    
    /**
     * バイト長文字列に変換するモード。<p>
     */
    public static final String CONVERT_LENGTH = "LENGTH";
    
    /**
     * 変換モードを設定する。<p>
     * 8進、10進、16進モード及び、文字列エンコード変換モードが指定できる。<br>
     * 文字列エンコード変換を指定する場合は、変換するエンコードを、変換モードとして指定する。<br>
     *
     * @param mode 変換モード
     * @see #CONVERT_HEX
     * @see #CONVERT_DECIMAL
     * @see #CONVERT_OCTAL
     * @see #CONVERT_LENGTH
     */
    public void setConvertMode(String mode);
    
    /**
     * 変換モードを取得する。<p>
     *
     * @return 変換モード
     */
    public String getConvertMode();
    
    /**
     * 変換モードが、文字列エンコード変換モードの場合に、エンコードした文字列に対して、更に変換を掛ける{@link jp.ossc.nimbus.util.converter.StringConverter StringConverter}のサービス名を設定する。<p>
     *
     * @param name StringConverterのサービス名
     */
    public void setStringConverterServiceName(ServiceName name);
    
    /**
     * 変換モードが、文字列エンコード変換モードの場合に、エンコードした文字列に対して、更に変換を掛ける{@link jp.ossc.nimbus.util.converter.StringConverter StringConverter}のサービス名を取得する。<p>
     *
     * @return StringConverterのサービス名
     */
    public ServiceName getStringConverterServiceName();
}
