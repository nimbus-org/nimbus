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

import java.util.List;

import jp.ossc.nimbus.service.test.bug.BugRecord.BugAttribute;

/**
 * 不具合管理。<p>
 * 不具合情報を管理（登録・更新・削除・取得）するためのインターフェイス。<br>
 *
 * @author M.Ishida
 */
public interface BugManager {

    /**
     * 新しい不具合を追加する。<p>
     * 
     * @param record 不具合情報
     * @return IDが設定された不具合情報
     * @throws BugManageException 不具合追加時に発生する例外
     */
    public BugRecord add(BugRecord record) throws BugManageException ;
    
    /**
     * 不具合を更新する。<p>
     * 
     * @param record 不具合情報
     * @throws BugManageException 不具合更新時に発生する例外
     */
    public void update(BugRecord record) throws BugManageException ;
    
    /**
     * 不具合を削除する。<p>
     * 
     * @param id 不具合のID
     * @throws BugManageException 不具合削除時に発生する例外
     */
    public void delete(String id) throws BugManageException ;
    
    /**
     * 不具合を削除する。<p>
     * 
     * @param record 不具合情報
     * @throws BugManageException 不具合削除時に発生する例外
     */
    public void delete(BugRecord record) throws BugManageException ;
    
    /**
     * 登録されている不具合の一覧を取得する。<p>
     * 
     * @return 不具合情報の一覧
     * @throws BugManageException 不具合一覧取得時に発生する例外
     */
    public List list() throws BugManageException ;
    
    /**
     * 指定されたIDの不具合を取得する。<p>
     * 
     * @param id 取得したい不具合のID
     * @return 不具合情報
     * @throws BugManageException 不具合取得時に発生する例外
     */
    public BugRecord get(String id) throws BugManageException ;
    
    /**
     * 指定されたIDの不具合を取得する。<p>
     * 
     * @param record 不具合情報
     * @return 不具合情報
     * @throws BugManageException 不具合取得時に発生する例外
     */
    public BugRecord get(BugRecord record) throws BugManageException ;
    
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
    
    
}
