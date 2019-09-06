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
package jp.ossc.nimbus.service.template;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

/**
 * テンプレートエンジン。<p>
 *
 * @author M.Takata
 */
public interface TemplateEngine{
    
    /**
     * テンプレートを登録する。<p>
     *
     * @param name テンプレート名
     * @param template テンプレート文字列
     */
    public void setTemplate(String name, String template);
    
    /**
     * テンプレートを登録する。<p>
     *
     * @param name テンプレート名
     * @param template テンプレート文字列
     * @param encoding テンプレート文字列をバイト配列にする場合の文字コード
     */
    public void setTemplate(String name, String template, String encoding);
    
    /**
     * テンプレートを登録する。<p>
     *
     * @param name テンプレート名
     * @param templateFile テンプレートファイル
     */
    public void setTemplateFile(String name, File templateFile);
    
    /**
     * テンプレートを登録する。<p>
     *
     * @param name テンプレート名
     * @param templateFile テンプレートファイル
     * @param encoding テンプレートファイルの文字コード
     */
    public void setTemplateFile(String name, File templateFile, String encoding);
    
    /**
     * テンプレートにデータを適用して、変換結果を返す。<p>
     *
     * @param name テンプレート名
     * @param dataMap データマップ
     * @return 変換結果
     * @exception TemplateTransformException 変換に失敗した場合
     */
    public String transform(String name, Map dataMap) throws TemplateTransformException;
    
    /**
     * テンプレートにデータを適用して、変換結果を書き込む。<p>
     *
     * @param name テンプレート名
     * @param dataMap データマップ
     * @param writer 変換結果を書き込むWriter
     * @exception TemplateTransformException 変換に失敗した場合
     * @exception IOException 書き込みに失敗した場合
     */
    public void transform(String name, Map dataMap, Writer writer) throws TemplateTransformException, IOException;
}