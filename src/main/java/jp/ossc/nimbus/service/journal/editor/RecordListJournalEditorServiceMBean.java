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
 * {@link RecordListJournalEditorService}のMBeanインタフェース。<p>
 * 
 * @author M.Takata
 * @see RecordListJournalEditorService
 */
public interface RecordListJournalEditorServiceMBean
 extends BlockJournalEditorServiceBaseMBean{
    
    /**
     * {@link jp.ossc.nimbus.beans.dataset.RecordList RecordList}が持つ名前を出力するかどうかを設定する。<p>
     * デフォルトは、true。<p>
     * 
     * @param isOutput 出力する場合は、true
     */
    public void setOutputRecordListName(boolean isOutput);
    
    /**
     * {@link jp.ossc.nimbus.beans.dataset.RecordList RecordList}が持つ名前を出力するかどうかを判定する。<p>
     * 
     * @return trueの場合は、出力する
     */
    public boolean isOutputRecordListName();
    
    /**
     * {@link jp.ossc.nimbus.beans.dataset.RecordList RecordList}が持つ{@link jp.ossc.nimbus.beans.dataset.RecordSchema RecordSchema}の情報を出力するかどうかを設定する。<p>
     * デフォルトは、false。<p>
     * 
     * @param isOutput 出力する場合は、true
     */
    public void setOutputRecordSchema(boolean isOutput);
    
    /**
     * {@link jp.ossc.nimbus.beans.dataset.RecordList RecordList}が持つ{@link jp.ossc.nimbus.beans.dataset.RecordSchema RecordSchema}の情報を出力するかどうかを判定する。<p>
     * 
     * @return trueの場合は、出力する
     */
    public boolean isOutputRecordSchema();
    
    /**
     * {@link jp.ossc.nimbus.beans.dataset.RecordList RecordList}が持つ{@link jp.ossc.nimbus.beans.dataset.Record Record}の最大出力件数を設定する。<p>
     * デフォルトは、-1で出力件数を制限しない。<p>
     * 
     * @param max 最大出力件数
     */
    public void setMaxSize(int max);
    
    /**
     * {@link jp.ossc.nimbus.beans.dataset.RecordList RecordList}が持つ{@link jp.ossc.nimbus.beans.dataset.Record Record}の最大出力件数を取得する。<p>
     * 
     * @return 最大出力件数
     */
    public int getMaxSize();
}