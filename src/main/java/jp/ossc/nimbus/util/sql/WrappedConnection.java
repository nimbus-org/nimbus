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
package jp.ossc.nimbus.util.sql;

import java.sql.*;
import java.util.Date;

import jp.ossc.nimbus.service.journal.Journal;
import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;
import jp.ossc.nimbus.service.context.Context;
import jp.ossc.nimbus.service.sequence.Sequence;

public class WrappedConnection extends ConnectionWrapper{
    
    private static final long serialVersionUID = 2276362914025130990L;
    
    protected static final String DEFAULT_REQUEST_ID_KEY = "REQUEST_ID";
    protected static final String DEFAULT_JOURNAL_OPEN = "Open";
    protected static final String DEFAULT_JOURNAL_COMMIT = "Commit";
    protected static final String DEFAULT_JOURNAL_COMMIT_FAIL = "CommitFail";
    protected static final String DEFAULT_JOURNAL_ROLLBACK = "Rollback";
    protected static final String DEFAULT_JOURNAL_ROLLBACK_FAIL = "RollbackFail";
    protected static final Class DEFAULT_STATEMENT_CLASS
         = WrappedStatement.class;
    protected static final Class DEFAULT_PREPARED_STATEMENT_CLASS
         = WrappedPreparedStatement.class;
    protected static final Class DEFAULT_CALLABLE_STATEMENT_CLASS
         = WrappedCallableStatement.class;
    
    protected Journal journal;
    protected Context threadContext;
    protected String requestIDKey = DEFAULT_REQUEST_ID_KEY;
    protected Sequence sequence;
    protected boolean isStartJournal;
    protected String journalKeyOpen = DEFAULT_JOURNAL_OPEN;
    protected EditorFinder editorFinderForOpen;
    protected String journalKeyCommit = DEFAULT_JOURNAL_COMMIT;
    protected String journalKeyCommitFail = DEFAULT_JOURNAL_COMMIT_FAIL;
    protected String journalKeyRollback = DEFAULT_JOURNAL_ROLLBACK;
    protected String journalKeyRollbackFail = DEFAULT_JOURNAL_ROLLBACK_FAIL;
    
    /**
     * 指定したコネクションをラップするインスタンスを生成する。<p>
     *
     * @param con ラップするコネクション
     */
    public WrappedConnection(Connection con){
        super(con);
        setStatementWrapperClass(DEFAULT_STATEMENT_CLASS);
        setPreparedStatementWrapperClass(DEFAULT_PREPARED_STATEMENT_CLASS);
        setCallableStatementWrapperClass(DEFAULT_CALLABLE_STATEMENT_CLASS);
    }
    
    public void setJournal(Journal jnl){
        journal = jnl;
    }
    
    public void setThreadContext(Context ctx){
        threadContext = ctx;
    }
    
    public void setRequestIDKey(String key){
        requestIDKey = key;
    }
    
    public void setSequence(Sequence seq){
        sequence = seq;
    }
    
    public void setJournalKeyOpen(String key){
        journalKeyOpen = key;
    }
    
    public void setEditorFinderForOpen(EditorFinder finder){
        editorFinderForOpen = finder;
    }
    
    public void setJournalKeyCommit(String key){
        journalKeyCommit = key;
    }
    
    public void setJournalKeyCommitFail(String key){
        journalKeyCommitFail = key;
    }
    
    public void setJournalKeyRollback(String key){
        journalKeyRollback = key;
    }
    
    public void setJournalKeyRollbackFail(String key){
        journalKeyRollbackFail = key;
    }
    
    public void setStartJournal(boolean isStart){
        isStartJournal = isStart;
        if(isStartJournal && journal != null){
            String requestId = null;
            if(threadContext != null && requestIDKey != null){
                requestId = (String)threadContext.get(requestIDKey);
            }
            if(requestId == null && sequence != null){
                requestId = sequence.increment();
                if(threadContext != null && requestIDKey != null){
                    threadContext.put(requestIDKey, requestId);
                }
            }
            journal.startJournal(journalKeyOpen, editorFinderForOpen);
            if(requestId != null){
                journal.setRequestId(requestId);
            }
        }
    }
    
    public void commit() throws SQLException {
        try{
            super.commit();
            if(journal != null){
                journal.addInfo(journalKeyCommit, new Date());
            }
        }catch(SQLException e){
            if(journal != null){
                journal.addInfo(journalKeyCommitFail, e);
            }
            throw e;
        }
    }
    
    public void rollback() throws SQLException {
        try{
            super.rollback();
            if(journal != null){
                journal.addInfo(journalKeyRollback, new Date());
            }
        }catch(SQLException e){
            if(journal != null){
                journal.addInfo(journalKeyRollbackFail, e);
            }
            throw e;
        }
    }
    
    public void close() throws SQLException {
        try{
            super.close();
        }finally{
            if(isStartJournal && journal != null){
                journal.endJournal();
            }
        }
    }
}