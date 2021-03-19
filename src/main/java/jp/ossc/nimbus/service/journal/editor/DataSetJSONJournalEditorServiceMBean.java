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
 * {@link DataSetJSONJournalEditorService}のMBeanインタフェース。<p>
 * 
 * @author M.Takata
 * @see DataSetJSONJournalEditorService
 */
public interface DataSetJSONJournalEditorServiceMBean
 extends JSONJournalEditorServiceMBean{
    
    /**
     * スキーマ情報を出力するかどうかを設定する。<p>
     * JSONにschema要素を出力するかどうかを設定する。trueの場合、出力する。デフォルトは、true。<br>
     *
     * @param isOutput スキーマ情報を出力する場合はtrue
     */
    public void setOutputSchema(boolean isOutput);
    
    /**
     * スキーマ情報を出力するかどうかを判定する。<p>
     *
     * @return trueの場合スキーマ情報を出力する
     */
    public boolean isOutputSchema();
    
    /**
     * ヘッダのプロパティ名を出力するかどうかを設定する。<p>
     * デフォルトは、trueで、出力する。<br>
     * falseにすると、ヘッダがJSONのオブジェクト形式ではなく、配列形式で出力される。<br>
     *
     * @param isOutput ヘッダのプロパティ名を出力する場合は、true
     */
    public void setOutputPropertyNameOfHeader(boolean isOutput);
    
    /**
     * ヘッダのプロパティ名を出力するかどうかを判定する。<p>
     *
     * @return trueの場合、ヘッダのプロパティ名を出力する
     */
    public boolean isOutputPropertyNameOfHeader();
    
    /**
     * レコードリストのプロパティ名を出力するかどうかを設定する。<p>
     * デフォルトは、trueで、出力する。<br>
     * falseにすると、レコードリストがJSONのオブジェクト形式ではなく、配列形式で出力される。<br>
     *
     * @param isOutput レコードリストのプロパティ名を出力する場合は、true
     */
    public void setOutputPropertyNameOfRecordList(boolean isOutput);
    
    /**
     * レコードリストのプロパティ名を出力するかどうかを判定する。<p>
     *
     * @return trueの場合、レコードリストのプロパティ名を出力する
     */
    public boolean isOutputPropertyNameOfRecordList();
}
