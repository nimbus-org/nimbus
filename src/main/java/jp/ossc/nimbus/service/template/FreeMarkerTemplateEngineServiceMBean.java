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
import java.util.Locale;

import freemarker.template.Version;

import jp.ossc.nimbus.core.ServiceBaseMBean;

/**
 * {@link FreeMarkerTemplateEngineService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see FreeMarkerTemplateEngineService
 */
public interface FreeMarkerTemplateEngineServiceMBean extends ServiceBaseMBean{
    
    /**
     * テンプレートファイルを探索するルートディレクトリを設定する。<p>
     * 指定されたパスは、実行ディレクトリからの相対パス、絶対パス、サービス定義ファイルからの相対パスの順で評価され、存在したディレクトリが適用される。また、指定されていない場合は、実行ディレクトリとなる。<br>
     * テンプレートの探索は、以下の順序で行われる。<br>
     * <ol>
     *   <li>指定されたテンプレート名で、{@link #setTemplate(String, String, String)}や{@link #setTemplateFile(String, File, String)}で登録されたテンプレートを探す。</li>
     *   <li>指定されたテンプレート名からテンプレートリソース名に変換した後、ルートディレクトリからテンプレートファイルを探索する。</li>
     *   <li>指定されたテンプレート名自体をテンプレート文字列と解釈する。</li>
     * </ol>
     *
     * @param dir ルートディレクトリ
     */
    public void setTemplateFileRootDirectory(File dir);
    
    /**
     * テンプレートファイルを探索するルートディレクトリを取得する。<p>
     *
     * @return ルートディレクトリ
     */
    public File getTemplateFileRootDirectory();
    
    /**
     * テンプレートファイルの文字コードを設定する。<p>
     * 指定しない場合は、システムの環境変数の文字コードが適用される。<br>
     *
     * @param encoding 文字コード
     */
    public void setCharacterEncoding(String encoding);
    
    /**
     * テンプレートファイルの文字コードを取得する。<p>
     *
     * @return 文字コード
     */
    public String getCharacterEncoding();
    
    /**
     * ロケールを設定する。<p>
     * 指定しない場合は、リクエストのロケール、システムの環境変数のロケールの順で適用される。<br>
     *
     * @param lo ロケール
     */
    public void setLocale(Locale lo);
    
    /**
     * ロケールを設定する。<p>
     *
     * @param lo ロケール
     */
    public Locale getLocale();
    
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
     * freemarker.template.Configurationのプロパティを設定する。<p>
     *
     * @param name プロパティ名
     * @param value プロパティ値
     */
    public void setConfigurationProperty(String name, Object value);
    
    /**
     * freemarker.template.Configurationに設定するプロパティ値を取得する。<p>
     *
     * @param name プロパティ名
     * @return プロパティ値
     */
    public Object getConfigurationProperty(String name);
}