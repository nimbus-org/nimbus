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
 * {@link RecordJournalEditorService}のMBeanインタフェース。<p>
 * 
 * @author M.Takata
 * @see RecordJournalEditorService
 */
public interface RecordJournalEditorServiceMBean
 extends BlockJournalEditorServiceBaseMBean{
    
    /**
     * {@link jp.ossc.nimbus.beans.dataset.Record Record}が持つ{@link jp.ossc.nimbus.beans.dataset.RecordSchema RecordSchema}の情報を出力するかどうかを設定する。<p>
     * デフォルトは、false。<p>
     * 
     * @param isOutput 出力する場合は、true
     */
    public void setOutputRecordSchema(boolean isOutput);
    
    /**
     * {@link jp.ossc.nimbus.beans.dataset.Record Record}が持つ{@link jp.ossc.nimbus.beans.dataset.RecordSchema RecordSchema}の情報を出力するかどうかを判定する。<p>
     * 
     * @return trueの場合は、出力する
     */
    public boolean isOutputRecordSchema();
    
    /**
     * {@link jp.ossc.nimbus.beans.dataset.Record Record}が持つプロパティの情報を出力するかどうかを設定する。<p>
     * デフォルトは、true。<p>
     * 
     * @param isOutput 出力する場合は、true
     */
    public void setOutputProperties(boolean isOutput);
    
    /**
     * {@link jp.ossc.nimbus.beans.dataset.Record Record}が持つプロパティの情報を出力するかどうかを判定する。<p>
     * 
     * @return trueの場合は、出力する
     */
    public boolean isOutputProperties();
    
    /**
     * ジャーナルに出力する時に、値を隠すための文字列を設定する。<p>
     *
     * @param str 値を隠すための文字列
     * @see #getSecretString()
     */
    public void setSecretString(String str);
    
    /**
     * ジャーナルに出力する時に、値を隠すための文字列を取得する。<p>
     *
     * @return 値を隠すための文字列
     * @see #setSecretString(String)
     */
    public String getSecretString();
    
    /**
     * {@link jp.ossc.nimbus.beans.dataset.Record Record}のプロパティをジャーナルに出力する時に、値を隠すプロパティの名前配列を設定する。<p>
     *
     * @param names 値を隠すプロパティの名前配列
     * @see #getSecretString()
     */
    public void setSecretProperties(String[] names);
    
    /**
     * {@link jp.ossc.nimbus.beans.dataset.Record Record}のプロパティをジャーナルに出力する時に、値を隠すプロパティの名前配列を取得する。<p>
     *
     * @return 値を隠すプロパティの名前配列
     * @see #setSecretProperties(String[])
     */
    public String[] getSecretProperties();
    
    /**
     * {@link jp.ossc.nimbus.beans.dataset.Record Record}のプロパティをジャーナルに出力する時に、値を出力するプロパティの名前配列を設定する。<p>
     *
     * @param names 値を出力するプロパティの名前配列
     * @see #getEnabledProperties()
     */
    public void setEnabledProperties(String[] names);
    
    /**
     * {@link jp.ossc.nimbus.beans.dataset.Record Record}のプロパティをジャーナルに出力する時に、値を出力するプロパティの名前配列を取得する。<p>
     *
     * @return 値を出力するプロパティの名前配列
     * @see #setEnabledProperties(String[])
     */
    public String[] getEnabledProperties();
    
    /**
     * {@link jp.ossc.nimbus.beans.dataset.Record Record}のプロパティをCSV形式に出力するかどうかを設定する。<p>
     * デフォルトは、true。<p>
     * 
     * @param isOutput CSV形式で出力する場合は、true
     * @see #isOutputCSVProperties()
     */
    public void setOutputCSVProperties(boolean isOutput);
    
    /**
     * {@link jp.ossc.nimbus.beans.dataset.Record Record}のプロパティをCSV形式に出力するかどうかを判定する。<p>
     * 
     * @return trueの場合、CSV形式で出力する
     * @see #setOutputCSVProperties(boolean)
     */
    public boolean isOutputCSVProperties();
}