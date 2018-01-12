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
package jp.ossc.nimbus.service.journal;

import jp.ossc.nimbus.core.*;

/**
 * {@link ThreadManagedJournalService}のMBeanインタフェース。<p>
 * 
 * @author H.Nakano
 * @see ThreadManagedJournalService
 */
public interface ThreadManagedJournalServiceMBean extends ServiceBaseMBean{
    
    /**
     * ジャーナルされたオブジェクトを編集する{@link JournalEditor}が登録されている{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}のサービス名を設定する。<p>
     *
     * @param name EditorFinderのサービス名
     */
    public void setEditorFinderName(ServiceName name);
    
    /**
     * ジャーナルされたオブジェクトを編集する{@link JournalEditor}が登録されている{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}のサービス名を取得する。<p>
     *
     * @return EditorFinderのサービス名
     */
    public ServiceName getEditorFinderName();
    
    /**
     * ジャーナルの通番を払い出す{@link jp.ossc.nimbus.service.sequence.Sequence Sequence}サービスのサービス名を設定する。<p>
     *
     * @param name Sequenceサービスのサービス名
     */
    public void setSequenceServiceName(ServiceName name);
    
    /**
     * ジャーナルの通番を払い出す{@link jp.ossc.nimbus.service.sequence.Sequence Sequence}サービスのサービス名を取得する。<p>
     *
     * @return Sequenceサービスのサービス名
     */
    public ServiceName getSequenceServiceName();
    
    /**
     * ジャーナルを非同期に出力するためのキューを生成する{@link jp.ossc.nimbus.service.queue.Queue Queue}サービスのサービス名を設定する。<p>
     *
     * @param name Queueサービスのサービス名
     */
    public void setQueueServiceName(ServiceName name);
    
    /**
     * ジャーナルを非同期に出力するためのキューを生成する{@link jp.ossc.nimbus.service.queue.Queue Queue}サービスのサービス名を取得する。<p>
     *
     * @return Queueサービスのサービス名
     */
    public ServiceName getQueueServiceName();
    
    /**
     * ジャーナルの出力情報を{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}に追加する時のキー名を設定する。<p>
     *
     * @param key ジャーナルの出力情報をWritableRecordに追加する時のキー名
     */
    public void setWritableElementKey(String key);
    
    /**
     * ジャーナルの出力情報を{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}に追加する時のキー名を取得する。<p>
     *
     * @return ジャーナルの出力情報をWritableRecordに追加する時のキー名
     */
    public String getWritableElementKey();
    
    /**
     * 出力するジャーナルのカテゴリを定義する{@link jp.ossc.nimbus.service.writer.Category Category}サービスの名前を設定する。<p>
     * 
     * @param names Categoryサービス名の配列
     */
    public void setCategoryServiceNames(ServiceName[] names);
    
    /**
     * 出力するジャーナルのカテゴリを定義する{@link jp.ossc.nimbus.service.writer.Category Category}サービスの名前を取得する。<p>
     * 
     * @return Categoryサービス名の配列
     */
    public ServiceName[] getCategoryServiceNames();
    
    /**
     * Journal出力レベルを設定する。<p>
     * ここで定義されたレベル以上のジャーナルのみ出力する。
     * 
     * @param level 出力レベル
     * @see Journal#JOURNAL_LEVEL_DEBUG
     * @see Journal#JOURNAL_LEVEL_INFO
     * @see Journal#JOURNAL_LEVEL_WARN
     * @see Journal#JOURNAL_LEVEL_ERROR
     * @see Journal#JOURNAL_LEVEL_FATAL
     */
    public void setJournalLevel(int level);
    
    /**
     * Journal出力レベルを取得する。<p>
     * 
     * @return Journal出力レベル
     */
    public int getJournalLevel();
    
    public void setWriteDaemonSize(int size);
    public int getWriteDaemonSize();
}
