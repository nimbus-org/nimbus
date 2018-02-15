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
 * {@link CommandJournalEditorService}のMBeanインタフェース。<p>
 * 
 * @author M.Takata
 * @see CommandJournalEditorService
 */
public interface CommandJournalEditorServiceMBean
 extends BlockJournalEditorServiceBaseMBean{
    
    /**
     * 実行するBeanFlowのキーを出力するかどうかを設定する。<p>
     * デフォルトでは、true。<br>
     *
     * @param isOutput 出力する場合は、true
     */
    public void setOutputFlowKey(boolean isOutput);
    
    /**
     * 実行するBeanFlowのキーを出力するかどうかを判定する。<p>
     *
     * @return trueの場合は、出力する
     */
    public boolean isOutputFlowKey();
    
    /**
     * コマンド入力を出力するかどうかを設定する。<p>
     * デフォルトでは、true。<br>
     *
     * @param isOutput 出力する場合は、true
     */
    public void setOutputInput(boolean isOutput);
    
    /**
     * コマンド入力を出力するかどうかを判定する。<p>
     *
     * @return trueの場合は、出力する
     */
    public boolean isOutputInput();
    
    /**
     * コマンド出力を出力するかどうかを設定する。<p>
     * デフォルトでは、true。<br>
     *
     * @param isOutput 出力する場合は、true
     */
    public void setOutputOutput(boolean isOutput);
    
    /**
     * コマンド出力を出力するかどうかを判定する。<p>
     *
     * @return trueの場合は、出力する
     */
    public boolean isOutputOutput();
    
    /**
     * 実行ステータスを出力するかどうかを設定する。<p>
     * デフォルトでは、true。<br>
     *
     * @param isOutput 出力する場合は、true
     */
    public void setOutputStatus(boolean isOutput);
    
    /**
     * 実行ステータスを出力するかどうかを判定する。<p>
     *
     * @return trueの場合は、出力する
     */
    public boolean isOutputStatus();
    
    /**
     * 発生した例外を出力するかどうかを設定する。<p>
     * デフォルトでは、true。<br>
     *
     * @param isOutput 出力する場合は、true
     */
    public void setOutputException(boolean isOutput);
    
    /**
     * 発生した例外を出力するかどうかを判定する。<p>
     *
     * @return trueの場合は、出力する
     */
    public boolean isOutputException();
}