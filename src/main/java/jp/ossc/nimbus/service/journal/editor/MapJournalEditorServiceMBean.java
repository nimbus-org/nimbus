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

/**
 * {@link MapJournalEditorService}のMBeanインタフェース。<p>
 * 
 * @author M.Takata
 * @see MapJournalEditorService
 */
public interface MapJournalEditorServiceMBean
 extends ImmutableJournalEditorServiceBaseMBean{
    
    /**
     * 開始区切り文字を設定する。<p>
     *
     * @param delim 区切り文字
     */
    public void setStartDelimiter(String delim);
    
    /**
     * 開始区切り文字を取得する。<p>
     *
     * @return 区切り文字
     */
    public String getStartDelimiter();
    
    /**
     * 終了区切り文字を設定する。<p>
     *
     * @param delim 区切り文字
     */
    public void setEndDelimiter(String delim);
    
    /**
     * 終了区切り文字を取得する。<p>
     *
     * @return 区切り文字
     */
    public String getEndDelimiter();
    
    /**
     * マップのエントリの区切り文字を設定する。<p>
     *
     * @param delim 区切り文字
     */
    public void setDelimiter(String delim);
    
    /**
     * マップのエントリの区切り文字を取得する。<p>
     *
     * @return 区切り文字
     */
    public String getDelimiter();
    
    /**
     * マップのキーと値の区切り文字を設定する。<p>
     *
     * @param delim 区切り文字
     */
    public void setKeyValueDelimiter(String delim);
    
    /**
     * マップのキーと値の区切り文字を取得する。<p>
     *
     * @return 区切り文字
     */
    public String getKeyValueDelimiter();
    
    /**
     * マップの値の開始区切り文字を設定する。<p>
     *
     * @param delim 区切り文字
     */
    public void setStartValueDelimiter(String delim);
    
    /**
     * マップの値の開始区切り文字を取得する。<p>
     *
     * @return 区切り文字
     */
    public String getStartValueDelimiter();
    
    /**
     * マップの値の終了区切り文字を設定する。<p>
     *
     * @param delim 区切り文字
     */
    public void setEndValueDelimiter(String delim);
    
    /**
     * マップの値の終了区切り文字を取得する。<p>
     *
     * @return 区切り文字
     */
    public String getEndValueDelimiter();
    
    /**
     * 値をマスクする際のマスク文字列を設定する。<p>
     *
     * @param str マスク文字列
     */
    public void setSecretString(String str);
    
    /**
     * 値をマスクする際のマスク文字列を取得する。<p>
     *
     * @return マスク文字列
     */
    public String getSecretString();
    
    /**
     * 値をマスクするキー名の配列を設定する。<p>
     *
     * @param keys キー名の配列
     */
    public void setSecretKeys(String[] keys);
    
    /**
     * 値をマスクするキー名の配列を取得する。<p>
     *
     * @return キー名の配列
     */
    public String[] getSecretKeys();
    
    /**
     * 値を出力するキー名の配列を設定する。<p>
     * この属性を設定しない場合は、全てのキーが出力される。<br>
     *
     * @param keys キー名の配列
     */
    public void setEnabledKeys(String[] keys);
    
    /**
     * 値を出力するキー名の配列を取得する。<p>
     *
     * @return キー名の配列
     */
    public String[] getEnabledKeys();
}
