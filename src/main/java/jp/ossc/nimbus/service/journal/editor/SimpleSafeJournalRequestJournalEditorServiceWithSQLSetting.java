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

import jp.ossc.nimbus.service.journal.JournalRecord;
import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;

/**
 * ___クラス<p>
 * ___操作を行う
 * @version $Name:  $
 * @author K.Nagai
 * @since 1.0
 */
public class SimpleSafeJournalRequestJournalEditorServiceWithSQLSetting extends
        SimpleSafeRequestJournalEditorService 
        implements SimpleSafeJournalRequestJournalEditorServiceWithSQLSettingMBean
{
    
    private static final long serialVersionUID = -4112494655642853654L;
    
    /** SQL出力を行うか否か*/
    private boolean bOutputSql;
    /** SQL出力を行うか否か*/
    private String mSqlKeyFormat=null;
    
    public void startService() throws Exception{
        super.startService();
        if( mSqlKeyFormat == null ){
            throw new IllegalArgumentException("SqlOmittingSqlKeyFormat be specified ");
        }
    }
    
    public void stopService() throws Exception{
        mSqlKeyFormat = null;
        super.stopService();
    }

    /* (非 Javadoc)
     * @see jp.ossc.nimbus.service.journal.editor.SimpleSafeJournalRequestJournalEditorServiceWithSQLSettingMBean#setSqlOutput()
     */
    public void setOutputSql(boolean output) {
        bOutputSql = output;       
    }

    /* (非 Javadoc)
     * @see jp.ossc.nimbus.service.journal.editor.SimpleSafeJournalRequestJournalEditorServiceWithSQLSettingMBean#getSqlOutput()
     */
    public boolean getOutputSql() {
        return bOutputSql;
    }
    
    protected StringBuilder makeRecordsFormat(
            EditorFinder finder,
            JournalRecord[] records,
            StringBuilder buf
        ){
            for(int i = 0, max = records.length; i < max; i++){
                 if( bOutputSql || !isSqlRecKey(records[i].getKey())) {
                      buf.append(records[i].toObject());
                      if(i != max - 1){
                          buf.append(getLineSeparator());
                      }
                }
            }
            return buf;
        }

    /**
     * isSqlRecKey<p>
     * SQL文を含むキーであるかを判定する
     * @param recStr
     * @return
     */
    private boolean isSqlRecKey(String recStr) {
        if( recStr == null ) return false;
        if( recStr.indexOf(mSqlKeyFormat) != -1 ) return true;
        return false;
    }

    /* (非 Javadoc)
     * @see jp.ossc.nimbus.service.journal.editor.SimpleSafeJournalRequestJournalEditorServiceWithSQLSettingMBean#setSqlOmittingSqlFormat(java.lang.String)
     */
    public void setSqlOmittingSqlKeyFormat(String format) {
        mSqlKeyFormat = format;        
    }

    /* (非 Javadoc)
     * @see jp.ossc.nimbus.service.journal.editor.SimpleSafeJournalRequestJournalEditorServiceWithSQLSettingMBean#getSqlOmittingSqlFormat()
     */
    public String getSqlOmittingSqlKeyFormat() {
         return mSqlKeyFormat;
    }
}
