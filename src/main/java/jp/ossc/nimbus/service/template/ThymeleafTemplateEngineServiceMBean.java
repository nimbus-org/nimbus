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
import java.util.Set;
import java.util.Map;
import java.util.Locale;

import jp.ossc.nimbus.beans.Property;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceBaseMBean;

/**
 * {@link ThymeleafTemplateEngineService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see ThymeleafTemplateEngineService
 */
public interface ThymeleafTemplateEngineServiceMBean extends ServiceBaseMBean{
    
    /**
     * テンプレートファイルを探索するルートディレクトリを設定する。<p>
     * 指定されたパスは、実行ディレクトリからの相対パス、絶対パス、サービス定義ファイルからの相対パスの順で評価され、存在したディレクトリが適用される。また、指定されていない場合は、実行ディレクトリとなる。<br>
     * テンプレートの探索は、以下の順序で行われる。<br>
     * <ol>
     *   <li>指定されたテンプレート名で、{@link #setTemplate(String, String, String)}や{@link #setTemplateFile(String, File, String)}で登録されたテンプレートを探す。</li>
     *   <li>指定されたテンプレート名からテンプレートリソース名に変換した後、クラスパスからテンプレートファイルを探索する。</li>
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
     * メッセージリソースを参照する場合のロケールを設定する。<p>
     * 指定しない場合は、リクエストのロケール、システムの環境変数のロケールの順で適用される。<br>
     *
     * @param lo ロケール
     */
    public void setLocale(Locale lo);
    
    /**
     * メッセージリソースを参照する場合のロケールを設定する。<p>
     *
     * @param lo ロケール
     */
    public Locale getLocale();
    
    /**
     * テンプレート名からテンプレートリソース名に変換する際に、テンプレート名の代わりとなる別名のマッピングを設定する。<p>
     *
     * @param aliases テンプレート名と別名のマッピング
     */
    public void setTemplateAliases(Map aliases);
    
    /**
     * テンプレート名からテンプレートリソース名に変換する際に、テンプレート名の代わりとなる別名のマッピングを取得する。<p>
     *
     * @return テンプレート名と別名のマッピング
     */
    public Map getTemplateAliases();
    
    /**
     * テンプレート名からテンプレートリソース名に変換する際に前置詞として付与される文字列を設定する。<p>
     *
     * @param prefix テンプレートリソース名の前置詞
     */
    public void setPrefix(String prefix);
    
    /**
     * テンプレート名からテンプレートリソース名に変換する際に前置詞として付与される文字列を取得する。<p>
     *
     * @return テンプレートリソース名の前置詞
     */
    public String getPrefix();
    
    /**
     * テンプレート名からテンプレートリソース名に変換する際に拡張子として付与される文字列を設定する。<p>
     *
     * @param suffix テンプレートリソース名の拡張子
     */
    public void setSuffix(String suffix);
    
    /**
     * テンプレート名からテンプレートリソース名に変換する際に拡張子として付与される文字列を取得する。<p>
     *
     * @return テンプレートリソース名の拡張子
     */
    public String getSuffix();
    
    /**
     * 強制的に拡張子を付与するかどうかを設定する。<p>
     * デフォルトは、falseで、有効な拡張子が既に付いている場合は、付与しない。<br>
     *
     * @param isForce 強制的に付与する場合は、true
     */
    public void setForceSuffix(boolean isForce);
    
    /**
     * 強制的に拡張子を付与するかどうかを判定する。<p>
     *
     * @return trueの場合は、強制的に付与する
     */
    public boolean isForceSuffix();
    
    /**
     * テンプレートを解釈するモードを設定する。<p>
     * 指定可能なモードは、org.thymeleaf.templatemode.TemplateModeを参照。<p>
     *
     * @param mode テンプレートを解釈するモード
     */
    public void setTemplateMode(String mode);
    
    /**
     * テンプレートを解釈するモードを設定する。<p>
     *
     * @return テンプレートを解釈するモード
     */
    public String getTemplateMode();
    
    /**
     * テンプレートモードをXMLと判断するテンプレート名のパターンを設定する。<p>
     *
     * @param patterns テンプレート名のパターン文字列の集合
     */
    public void setXmlTemplateModePatterns(Set patterns);
    
    /**
     * テンプレートモードをXMLと判断するテンプレート名のパターンを取得する。<p>
     *
     * @return テンプレート名のパターン文字列の集合
     */
    public Set getXmlTemplateModePatterns();
    
    /**
     * テンプレートモードをHTMLと判断するテンプレート名のパターンを設定する。<p>
     *
     * @param patterns テンプレート名のパターン文字列の集合
     */
    public void setHtmlTemplateModePatterns(Set patterns);
    
    /**
     * テンプレートモードをHTMLと判断するテンプレート名のパターンを取得する。<p>
     *
     * @return テンプレート名のパターン文字列の集合
     */
    public Set getHtmlTemplateModePatterns();
    
    /**
     * テンプレートモードをTEXTと判断するテンプレート名のパターンを設定する。<p>
     *
     * @param patterns テンプレート名のパターン文字列の集合
     */
    public void setTextTemplateModePatterns(Set patterns);
    
    /**
     * テンプレートモードをTEXTと判断するテンプレート名のパターンを取得する。<p>
     *
     * @return テンプレート名のパターン文字列の集合
     */
    public Set getTextTemplateModePatterns();
    
    /**
     * テンプレートモードをJavaScriptと判断するテンプレート名のパターンを設定する。<p>
     *
     * @param patterns テンプレート名のパターン文字列の集合
     */
    public void setJavaScriptTemplateModePatterns(Set patterns);
    
    /**
     * テンプレートモードをJavaScriptと判断するテンプレート名のパターンを取得する。<p>
     *
     * @return テンプレート名のパターン文字列の集合
     */
    public Set getJavaScriptTemplateModePatterns();
    
    /**
     * テンプレートモードをCSSと判断するテンプレート名のパターンを設定する。<p>
     *
     * @param patterns テンプレート名のパターン文字列の集合
     */
    public void setCssTemplateModePatterns(Set patterns);
    
    /**
     * テンプレートモードをCSSと判断するテンプレート名のパターンを取得する。<p>
     *
     * @return テンプレート名のパターン文字列の集合
     */
    public Set getCssTemplateModePatterns();
    
    /**
     * テンプレートモードをRAWと判断するテンプレート名のパターンを設定する。<p>
     *
     * @param patterns テンプレート名のパターン文字列の集合
     */
    public void setRawTemplateModePatterns(Set patterns);
    
    /**
     * テンプレートモードをRAWと判断するテンプレート名のパターンを取得する。<p>
     *
     * @return テンプレート名のパターン文字列の集合
     */
    public Set getRawTemplateModePatterns();
    
    /**
     * テンプレートリソース名から適切なテンプレートモードを解釈するかどうかを設定する。<p>
     * デフォルトは、falseで、テンプレートリソース名からテンプレートモードを解釈しない。<br>
     * 
     * @param isForce テンプレートリソース名から適切なテンプレートモードを解釈する場合は、true
     */
    public void setForceTemplateMode(boolean isForce);
    
    /**
     * テンプレートリソース名から適切なテンプレートモードを解釈するかどうかを判定する。<p>
     * 
     * @return trueの場合は、テンプレートリソース名から適切なテンプレートモードを解釈する
     */
    public boolean isForceTemplateMode();
    
    /**
     * テンプレートファイルのキャッシュを有効化するかどうかを設定する。<p>
     * デフォルトは、trueで、キャッシュを有効化する。<br>
     *
     * @param isCacheable 有効化する場合は、true
     */
    public void setCacheable(boolean isCacheable);
    
    /**
     * テンプレートファイルのキャッシュを有効化するかどうかを判定する。<p>
     *
     * @return trueの場合、有効化する
     */
    public boolean isCacheable();
    
    /**
     * キャッシュの有効期間[ms]を設定する。<p>
     * デフォルトは、0で、有効期間を指定しない。<br>
     *
     * @param millis 有効期間[ms]
     */
    public void setCacheTTL(long millis);
    
    /**
     * キャッシュの有効期間[ms]を取得する。<p>
     *
     * @return 有効期間[ms]
     */
    public long getCacheTTL();
    
    /**
     * キャッシュを有効化するテンプレート名のパターンを設定する。<p>
     *
     * @param patterns テンプレート名のパターン文字列の集合
     */
    public void setCacheablePatterns(Set patterns);
    
    /**
     * キャッシュを有効化するテンプレート名のパターンを取得する。<p>
     *
     * @return テンプレート名のパターン文字列の集合
     */
    public Set getCacheablePatterns();
    
    /**
     * キャッシュを無効化するテンプレート名のパターンを設定する。<p>
     *
     * @param patterns テンプレート名のパターン文字列の集合
     */
    public void setNonCacheablePatterns(Set patterns);
    
    /**
     * キャッシュを無効化するテンプレート名のパターンを取得する。<p>
     *
     * @return テンプレート名のパターン文字列の集合
     */
    public Set getNonCacheablePatterns();
    
    /**
     * {@link TemplateEngine#transform(String, Map) transform(name, dataMap)}のdataMapからjavax.servlet.ServletContextを取得するプロパティを設定する。<p>
     * org.thymeleaf.context.IContextの実装として、org.thymeleaf.context.WebContextを使用する場合は、設定する必要がある。設定しない場合、または、設定しても取得できない場合は、org.thymeleaf.context.Contextが使用される。<br>
     * ただし、設定されていない場合で、{@link #setHttpServletRequestProperty(Property)}が設定されている場合は、javax.servlet.ServletContextをjavax.servlet.http.HttpServletRequestからgetServletContext()で取得する。<br>
     *
     * @param prop javax.servlet.ServletContextを取得するプロパティ
     */
    public void setServletContextProperty(Property prop);
    
    /**
     * {@link TemplateEngine#transform(String, Map) transform(name, dataMap)}のdataMapからjavax.servlet.ServletContextを取得するプロパティを取得する。<p>
     *
     * @return javax.servlet.ServletContextを取得するプロパティ
     */
    public Property getServletContextProperty();
    
    /**
     * {@link TemplateEngine#transform(String, Map) transform(name, dataMap)}のdataMapからjavax.servlet.http.HttpServletRequestを取得するプロパティを設定する。<p>
     * org.thymeleaf.context.IContextの実装として、org.thymeleaf.context.WebContextを使用する場合は、設定する必要がある。設定しない場合、または、設定しても取得できない場合は、org.thymeleaf.context.Contextが使用される。<br>
     *
     * @param prop javax.servlet.http.HttpServletRequestを取得するプロパティ
     */
    public void setHttpServletRequestProperty(Property prop);
    
    /**
     * {@link TemplateEngine#transform(String, Map) transform(name, dataMap)}のdataMapからjavax.servlet.http.HttpServletRequestを取得するプロパティを取得する。<p>
     *
     * @return javax.servlet.http.HttpServletRequestを取得するプロパティ
     */
    public Property getHttpServletRequestProperty();
    
    /**
     * {@link TemplateEngine#transform(String, Map) transform(name, dataMap)}のdataMapからjavax.servlet.http.HttpServletResponseを取得するプロパティを設定する。<p>
     * org.thymeleaf.context.IContextの実装として、org.thymeleaf.context.WebContextを使用する場合は、設定する必要がある。設定しない場合、または、設定しても取得できない場合は、org.thymeleaf.context.Contextが使用される。<br>
     *
     * @param prop javax.servlet.http.HttpServletResponseを取得するプロパティ
     */
    public void setHttpServletResponseProperty(Property prop);
    
    /**
     * {@link TemplateEngine#transform(String, Map) transform(name, dataMap)}のdataMapからjavax.servlet.http.HttpServletResponseを取得するプロパティを取得する。<p>
     *
     * @return javax.servlet.http.HttpServletResponseを取得するプロパティ
     */
    public Property getHttpServletResponseProperty();
    
    /**
     * {@link jp.ossc.nimbus.service.message.MessageRecordFactory MessageRecordFactory}サービスのサービス名を設定する。<p>
     *
     * @param name MessageRecordFactoryサービスのサービス名
     */
    public void setMessageRecordFactoryServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.message.MessageRecordFactory MessageRecordFactory}サービスのサービス名を取得する。<p>
     *
     * @return MessageRecordFactoryサービスのサービス名
     */
    public ServiceName getMessageRecordFactoryServiceName();
    
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
     * テンプレートのキャッシュをクリアする。<p>
     */
    public void clearTemplateCache();
    
    /**
     * 指定したテンプレートのキャッシュをクリアする。<p>
     *
     * @param name テンプレート名
     */
    public void clearTemplateCacheFor(String name);
}