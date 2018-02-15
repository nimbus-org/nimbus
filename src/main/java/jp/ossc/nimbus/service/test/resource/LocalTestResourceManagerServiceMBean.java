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
package jp.ossc.nimbus.service.test.resource;

import java.io.File;

import jp.ossc.nimbus.core.ServiceBaseMBean;
import jp.ossc.nimbus.core.ServiceName;

/**
 * {@link LocalTestResourceManagerService}のMBeanインタフェース<p>
 * 
 * @author M.Aono
 * @see LocalTestResourceManagerService
 */
public interface LocalTestResourceManagerServiceMBean extends ServiceBaseMBean{
    
    public static final String DEFAULT_TEMPLATE_LINK_FILE_EXTENTION = ".tln";
    
    /**
     * テストリソースが存在するディレクトリを取得する。<p>
     *
     * @return ディレクトリ
     */
    public File getTestResourceDirectory();
    
    /**
     * テストリソースが存在するディレクトリを設定する。<p>
     * 指定しない場合は、このサービスのサービス定義ファイルが存在するディレクトリ。<br>
     *
     * @param path ディレクトリ
     */
    public void setTestResourceDirectory(File path);
    
    /**
     * {@link jp.ossc.nimbus.service.test.TemplateEngine TemplateEngine}サービスのサービス名を設定する。<p>
     *
     * @param name TemplateEngineサービスのサービス名
     */
    public void setTemplateEngineServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.test.TemplateEngine TemplateEngine}サービスのサービス名を取得する。<p>
     *
     * @return TemplateEngineサービスのサービス名
     */
    public ServiceName getTemplateEngineServiceName();
    
    /**
     * テンプレートリンクファイルの拡張子を設定する。<p>
     * デフォルトは、{@link #DEFAULT_TEMPLATE_LINK_FILE_EXTENTION}。<br>
     *
     * @param ext 拡張子
     */
    public void setTemplateLinkFileExtention(String ext);
    
    /**
     * テンプレートリンクファイルの拡張子を取得する。<p>
     *
     * @return 拡張子
     */
    public String getTemplateLinkFileExtention();
    
    /**
     * テンプレートリンクファイルの文字エンコーディングを設定する。<p>
     *
     * @param encoding 文字エンコーディング
     */
    public void setTemplateLinkFileEncoding(String encoding);
    
    /**
     * テンプレートリンクファイルの文字エンコーディングを取得する。<p>
     *
     * @return 文字エンコーディング
     */
    public String getTemplateLinkFileEncoding();
}