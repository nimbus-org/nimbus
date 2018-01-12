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
 * {@link BeanJournalEditorService}のMBeanインタフェース。<p>
 * 
 * @author M.Takata
 * @see BeanJournalEditorService
 */
public interface BeanJournalEditorServiceMBean
 extends BlockJournalEditorServiceBaseMBean{
    
    /**
     * プロパティの型情報を出力するかどうかを設定する。<p>
     * デフォルトはtrue。<br>
     *
     * @param isOutput 出力する場合は、true
     */
    public void setOutputPropertyType(boolean isOutput);
    
    /**
     * プロパティの型情報を出力するかどうかを判定する。<p>
     *
     * @return trueの場合は、出力する
     */
    public boolean isOutputPropertyType();
    
    /**
     * 編集時にBeanのpublicフィールドのみを対象とするかどうかを判定する。<p>
     *
     * @return trueの場合、publicフィールドのみを対象とする
     */
    public boolean isFieldOnly();
    
    /**
     * 編集時にBeanのpublicフィールドのみを対象とするかどうかを設定する。<p>
     * デフォルトは、falseでpublicフィールドのみを対象にはしない。<br>
     *
     * @param isFieldOnly publicフィールドのみを対象とする場合は、true
     */
    public void setFieldOnly(boolean isFieldOnly);
    
    /**
     * 編集時にBeanのpublicなgetterのみを対象とするかどうかを判定する。<p>
     *
     * @return trueの場合、publicなgetterのみを対象とする
     */
    public boolean isAccessorOnly();
    
    /**
     * 編集時にBeanのpublicなgetterのみを対象とするかどうかを設定する。<p>
     * デフォルトは、trueでpublicなgetterのみを対象にする。<br>
     *
     * @param isAccessorOnly publicなgetterのみを対象とする場合、true
     */
    public void setAccessorOnly(boolean isAccessorOnly);
    
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
     * Beanのプロパティをジャーナルに出力する時に、値を隠すプロパティの名前配列を設定する。<p>
     * クラス名まで指定したい場合は、「完全修飾クラス名#プロパティ名」で指定する。<br>
     *
     * @param names 値を隠すプロパティの名前配列
     * @see #getSecretString()
     */
    public void setSecretProperties(String[] names);
    
    /**
     * Beanのプロパティをジャーナルに出力する時に、値を隠すプロパティの名前配列を取得する。<p>
     *
     * @return 値を隠すプロパティの名前配列
     * @see #setSecretProperties(String[])
     */
    public String[] getSecretProperties();
    
    /**
     * ジャーナルに出力するBeanのプロパティの名前配列を設定する。<p>
     * クラス名まで指定したい場合は、「完全修飾クラス名#プロパティ名」で指定する。<br>
     *
     * @param names ジャーナルに出力するプロパティの名前配列
     */
    public void setEnabledProperties(String[] names);
    
    /**
     * ジャーナルに出力するBeanのプロパティの名前配列を取得する。<p>
     *
     * @return ジャーナルに出力するプロパティの名前配列
     */
    public String[] getEnabledProperties();
    
    /**
     * プロパティの型情報を出力する際の開始区切り文字を設定する。<p>
     *
     * @param delimiter 区切り文字
     */
    public void setPropertyTypeStartDelimiter(String delimiter);
    
    /**
     * プロパティの型情報を出力する際の開始区切り文字を取得する。<p>
     *
     * @return 区切り文字
     */
    public String getPropertyTypeStartDelimiter();
    
    /**
     * プロパティの型情報を出力する際の終了区切り文字を設定する。<p>
     *
     * @param delimiter 区切り文字
     */
    public void setPropertyTypeEndDelimiter(String delimiter);
    
    /**
     * プロパティの型情報を出力する際の終了区切り文字を取得する。<p>
     *
     * @return 区切り文字
     */
    public String getPropertyTypeEndDelimiter();
    
    /**
     * プロパティ名と値の区切り文字を設定する。<p>
     *
     * @param delimiter 区切り文字
     */
    public void setPropertyNameValueDelimiter(String delimiter);
    
    /**
     * プロパティ名と値の区切り文字を取得する。<p>
     *
     * @return 区切り文字
     */
    public String getPropertyNameValueDelimiter();
    
    /**
     * プロパティ値の開始区切り文字を設定する。<p>
     *
     * @param delimiter 区切り文字
     */
    public void setStartValueDelimiter(String delimiter);
    
    /**
     * プロパティ値の開始区切り文字を取得する。<p>
     *
     * @return 区切り文字
     */
    public String getStartValueDelimiter();
    
    /**
     * プロパティ値の終了区切り文字を設定する。<p>
     *
     * @param delimiter 区切り文字
     */
    public void setEndValueDelimiter(String delimiter);
    
    /**
     * プロパティ値の終了区切り文字を取得する。<p>
     *
     * @return 区切り文字
     */
    public String getEndValueDelimiter();
    
    /**
     * プロパティの区切り文字を設定する。<p>
     *
     * @param delimiter 区切り文字
     */
    public void setPropertyDelimiter(String delimiter);
    
    /**
     * プロパティの区切り文字を取得する。<p>
     *
     * @return 区切り文字
     */
    public String getPropertyDelimiter();
}
