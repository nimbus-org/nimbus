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
package jp.ossc.nimbus.service.aop.interceptor;

import java.util.Map;

import jp.ossc.nimbus.core.*;

/**
 * {@link MethodJournalInterceptorService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see MethodJournalInterceptorService
 */
public interface MethodJournalInterceptorServiceMBean extends ServiceBaseMBean{
    
    /**
     * ジャーナル開始時のデフォルトのジャーナルキー。<p>
     */
    public static final String DEFAULT_REQUEST_JOURNAL_KEY = "Request";
    
    /**
     * メソッド呼び出し時のデフォルトのジャーナルキー。<p>
     */
    public static final String DEFAULT_METHOD_CALL_JOURNAL_KEY
         = "MethodCall";
    
    /**
     * メソッド戻り時のデフォルトのジャーナルキー。<p>
     */
    public static final String DEFAULT_METHOD_RETURN_JOURNAL_KEY
         = "MethodReturn";
    
    /**
     * リクエストIDを取得する{@link jp.ossc.nimbus.service.context.Context}サービスのサービス名を設定する。<p>
     *
     * @param name Contextサービスのサービス名
     * @see #getThreadContextServiceName()
     */
    public void setThreadContextServiceName(ServiceName name);
    
    /**
     * リクエストIDを取得する{@link jp.ossc.nimbus.service.context.Context}サービスのサービス名を取得する。<p>
     *
     * @return Contextサービスのサービス名
     * @see #setThreadContextServiceName(ServiceName)
     */
    public ServiceName getThreadContextServiceName();
    
    /**
     * Contextサービスに設定されたリクエストIDのキー名を設定する。<p>
     *
     * @param key リクエストIDのキー名
     * @see #getRequestIdKey()
     */
    public void setRequestIdKey(String key);
    
    /**
     * Contextサービスに設定されたリクエストIDのキー名を取得する。<p>
     *
     * @return リクエストIDのキー名
     * @see #setRequestIdKey(String)
     */
    public String getRequestIdKey();
    
    /**
     * {@link jp.ossc.nimbus.service.journal.Journal Journal}サービスのサービス名を設定する。<p>
     *
     * @param name Journalサービスのサービス名
     * @see #getJournalServiceName()
     */
    public void setJournalServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.journal.Journal Journal}サービスのサービス名を取得する。
     *
     * @return Journalサービスのサービス名
     * @see #setJournalServiceName(ServiceName)
     */
    public ServiceName getJournalServiceName();
    
    /**
     * ジャーナル開始のジャーナル編集に使用する{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービスのサービス名を設定する。<p>
     *
     * @param name EditorFinderサービスのサービス名
     * @see #getRequestEditorFinderServiceName()
     */
    public void setRequestEditorFinderServiceName(ServiceName name);
    
    /**
     * ジャーナル開始のジャーナル編集に使用する{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービスのサービス名を取得する。<p>
     *
     * @return EditorFinderサービスのサービス名
     * @see #setRequestEditorFinderServiceName(ServiceName)
     */
    public ServiceName getRequestEditorFinderServiceName();
    
    /**
     * メソッド呼び出しのジャーナル編集に使用する{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービスのサービス名を設定する。<p>
     *
     * @param name EditorFinderサービスのサービス名
     * @see #getMethodCallEditorFinderServiceName()
     */
    public void setMethodCallEditorFinderServiceName(ServiceName name);
    
    /**
     * メソッド呼び出しのジャーナル編集に使用する{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービスのサービス名を取得する。<p>
     *
     * @return EditorFinderサービスのサービス名
     * @see #setMethodCallEditorFinderServiceName(ServiceName)
     */
    public ServiceName getMethodCallEditorFinderServiceName();
    
    /**
     * メソッド戻りのジャーナル編集に使用する{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービスのサービス名を設定する。<p>
     *
     * @param name EditorFinderサービスのサービス名
     * @see #getMethodReturnEditorFinderServiceName()
     */
    public void setMethodReturnEditorFinderServiceName(ServiceName name);
    
    /**
     * メソッド戻りのジャーナル編集に使用する{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービスのサービス名を取得する。<p>
     *
     * @return EditorFinderサービスのサービス名
     * @see #setMethodReturnEditorFinderServiceName(ServiceName)
     */
    public ServiceName getMethodReturnEditorFinderServiceName();
    
    /**
     * 出力されるジャーナルのルート要素のキーを設定する。<p>
     * ここで指定されたキーは、{@link jp.ossc.nimbus.service.journal.Journal#startJournal(String)}の引数として使用される。<br>
     * また、指定しない場合は、"Request"が使用される。<br>
     *
     * @param key 出力されるジャーナルのルート要素のキー
     * @see #getRequestJournalKey()
     */
    public void setRequestJournalKey(String key);
    
    /**
     * 出力されるジャーナルのルート要素のキーを取得する。<p>
     *
     * @return 出力されるジャーナルのルート要素のキー
     * @see #setRequestJournalKey(String)
     */
    public String getRequestJournalKey();
        
    /**
     * 出力されるジャーナルのメソッド呼び出し要素のキーを設定する。<p>
     * ここで指定されたキーは、{@link jp.ossc.nimbus.service.journal.Journal#addInfo(String, Object)}の第一引数として使用される。<br>
     * また、指定しない場合は、"MethodCall"が使用される。<br>
     *
     * @param key 出力されるジャーナルのメソッド呼び出し要素のキー
     * @see #getMethodCallJournalKey()
     */
    public void setMethodCallJournalKey(String key);
    
    /**
     * 出力されるジャーナルのメソッド呼び出し要素のキーを取得する。<p>
     *
     * @return 出力されるジャーナルのメソッド呼び出し要素のキー
     * @see #setMethodCallJournalKey(String)
     */
    public String getMethodCallJournalKey();
    
    /**
     * 出力されるジャーナルのメソッド戻り要素のキーを設定する。<p>
     * ここで指定されたキーは、{@link jp.ossc.nimbus.service.journal.Journal#addInfo(String, Object)}の第一引数として使用される。<br>
     * また、指定しない場合は、"MethodReturn"が使用される。<br>
     *
     * @param key 出力されるジャーナルのメソッド戻り要素のキー
     * @see #getMethodReturnJournalKey()
     */
    public void setMethodReturnJournalKey(String key);
    
    /**
     * 出力されるジャーナルのメソッド戻り要素のキーを取得する。<p>
     *
     * @return 出力されるジャーナルのメソッド戻り要素のキー
     * @see #setMethodReturnJournalKey(String)
     */
    public String getMethodReturnJournalKey();
    
    /**
     * ジャーナル出力を行うかどうかを設定する。<p>
     * デフォルトでは、true。
     *
     * @param enable ジャーナル出力を行う場合true
     * @see #isEnabled()
     */
    public void setEnabled(boolean enable);
    
    /**
     * ジャーナル出力を行うかどうかを判定する。<p>
     *
     * @return ジャーナル出力を行う場合true
     * @see #setEnabled(boolean)
     */
    public boolean isEnabled();
    
    /**
     * メソッド呼び出し中に複数回通過した場合に、2回目以降のジャーナルを記録しないかどうかを設定する。<p>
     * デフォルトは、false。<br>
     *
     * @param isBlock 記録しない場合は、true
     */
    public void setBushingCallBlock(boolean isBlock);
    
    /**
     * メソッド呼び出し中に複数回通過した場合に、2回目以降のジャーナルを記録しないかどうかを判定する。<p>
     *
     * @return trueの場合は、記録しない
     */
    public boolean isBushingCallBlock();
    
    /**
     * Contextサービスから指定されたキーで値を取得して、指定されたキーでジャーナルに出力するように設定する。<p>
     *
     * @param contextKey Contextサービスから取得するキー
     * @param journalKey ジャーナルに出力するキー
     */
    public void setContextJournalMapping(String contextKey, String journalKey);
    
    /**
     * 指定されたContextサービスから取得するキーで、ジャーナルに出力するキーを取得する。<p>
     *
     * @param contextKey Contextサービスから取得するキー
     * @return ジャーナルに出力するキー
     */
    public String getContextJournalMapping(String contextKey);
    
    /**
     * Contextサービスから取得するキーと、ジャーナルに出力するキーのマッピングを取得する。<p>
     *
     * @return Contextサービスから取得するキーと、ジャーナルに出力するキーのマッピング
     */
    public Map getContextJournalMap();
    
    /**
     * InvocationContextから指定された属性名で値を取得して、指定されたキーでジャーナルに出力するように設定する。<p>
     *
     * @param attributeName InvocationContextから取得する属性名
     * @param journalKey ジャーナルに出力するキー
     */
    public void setInvocationContextJournalMapping(String attributeName, String journalKey);
    
    /**
     * 指定されたInvocationContextから取得する属性名で、ジャーナルに出力するキーを取得する。<p>
     *
     * @param attributeName InvocationContextから取得する属性名
     * @return ジャーナルに出力するキー
     */
    public String getInvocationContextJournalMapping(String attributeName);
    
    /**
     * InvocationContextから取得する属性名と、ジャーナルに出力するキーのマッピングを取得する。<p>
     *
     * @return InvocationContextから取得する属性名と、ジャーナルに出力するキーのマッピング
     */
    public Map getInvocationContextJournalMap();
}
