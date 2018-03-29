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
package jp.ossc.nimbus.service.test.bug;

import java.io.File;

import jp.ossc.nimbus.core.ServiceBaseMBean;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.service.connection.ConnectionFactory;
import jp.ossc.nimbus.service.connection.PersistentManager;
import jp.ossc.nimbus.service.sequence.Sequence;
import jp.ossc.nimbus.service.test.bug.BugRecord.BugAttribute;
import jp.ossc.nimbus.util.converter.BeanJSONConverter;

/**
 * {@link FileBugManagerService}のMBeanインタフェース<p>
 * 
 * @author M.Ishida
 * @see FileBugManagerService
 */
public interface FileBugManagerServiceMBean extends ServiceBaseMBean {
    
    /**
     * JSONコンバータが設定されて無い場合に使用するデフォルトの有効プロパティ名の配列
     * 
     */
    public static final String[] DEFAULT_CONVERTER_ENABLE_PROP_NAMES = {"bugAttributes", "id", "entryDate", "updateDate", "scenarioGroupId", "scenarioId", "testCaseId"};
    
    /**
     * Sequenceサービスのサービス名を取得する。<p>
     * 
     * @return Sequenceサービスのサービス名
     */
    public ServiceName getSequenceServiceName();

    /**
     * Sequenceサービスのサービス名を設定する。<p>
     * 
     * @param serviceName Sequenceサービスのサービス名
     */
    public void setSequenceServiceName(ServiceName serviceName);

    /**
     * Sequenceを取得する。<p>
     * 
     * @return Sequence
     */
    public Sequence getSequence();

    /**
     * Sequenceを設定する。<p>
     * 
     * @param sequence Sequence
     */
    public void setSequence(Sequence sequence);

    /**
     * 取得した不具合情報を格納するための{@link BugRecord}のテンプレートを取得する。<p>
     * 
     * @return BugRecordのテンプレート
     */
    public BugRecord getTemplateRecord();
    
    /**
     * 取得した不具合情報を格納するための{@link BugRecord}のテンプレートを設定する。<p>
     * テンプレートには必要な{@link BugAttribute}が追加されている状態で設定する必要がある。<p>
     * 
     * @param record BugRecordのテンプレート
     */
    public void setTemplateRecord(BugRecord record);

    /**
     * 不具合情報を管理するファイルのルートディレクトリを取得する。<p>
     * 
     * @return ルートディレクトリ
     */
    public File getRootDir();
    
    /**
     * 不具合情報を管理するファイルのルートディレクトリを設定する。<p>
     * 
     * @param dir ルートディレクトリ
     */
    public void setRootDir(File dir);
    
    /**
     * {@link BugRecord}をファイルに保存する際に使用するJSONコンバーターを取得する。<p>
     * 
     * @return JSONコンバーター
     */
    public BeanJSONConverter getConverter();
    
    /**
     * {@link BugRecord}をファイルに保存する際に使用するJSONコンバーターを設定する。<p>
     * 
     * @param converter JSONコンバーター
     */
    public void setConverter(BeanJSONConverter converter);
    
    /**
     * JSONコンバータが設定されて無い場合に使用する有効プロパティ名の配列を取得する。<p>
     * 
     * @return 有効プロパティ名の配列
     */
    public String[] getEnabledPropertyNames();

    /**
     * JSONコンバータが設定されて無い場合に使用する有効プロパティ名の配列を設定する。<p>
     * 
     * @param names 有効プロパティ名の配列
     */
    public void setEnabledPropertyNames(String[] names);
}
