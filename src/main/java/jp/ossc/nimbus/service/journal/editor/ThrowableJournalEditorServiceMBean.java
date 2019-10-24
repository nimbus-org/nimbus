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
// パッケージ
// インポート
package jp.ossc.nimbus.service.journal.editor;

import jp.ossc.nimbus.core.ServiceName;

/**
 * ThrowableJournalEditorServiceのMBeanインタフェース。<p>
 * 
 * @version $Name:  $
 * @author K.Nagai
 * @since 1.0
 */
public interface ThrowableJournalEditorServiceMBean 
extends ImmutableJournalEditorServiceBaseMBean {
    
    /**
     * 原因(Cause)を追って出力するかどうかを設定する。<p>
     * 
     * @param output 出力する場合、true
     */
    public void setOutputCause(boolean output);
    /**
     * 原因(Cause)を追って出力するかどうかを判定する。<p>
     * 
     * return trueの場合、出力する
     */
    public boolean getOutputCause();
    
    /**
     * 原因(Cause)を追って出力する際にTabを出力するかどうかを設定する。<p>
     * 
     * @param output 出力する場合、true
     */
    public void setOutputTab(boolean output);
    
    /**
     * 原因(Cause)を追って出力する際にTabを出力するかどうかを判定する。<p>
     * 
     * return trueの場合、出力する
     */
    public boolean getOutputTab();
    
    /**
     * {@link jp.ossc.nimbus.service.context.ThreadContextService ThreadContextService}のサービス名を設定する。<br>
     * 一度出力した例外をスレッドコンテキストに管理して、スタックトレースを出力しないようにする。<br>
     *
     * @param name ThreadContextServiceのサービス名
     */
    public void setThreadContextServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.context.ThreadContextService ThreadContextService}のサービス名を取得する。<br>
     *
     * @return ThreadContextServiceのサービス名
     */
    public ServiceName getThreadContextServiceName();
}
