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

import jp.ossc.nimbus.service.journal.Journal;
import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;
import jp.ossc.nimbus.service.context.Context;
import jp.ossc.nimbus.service.sequence.Sequence;
import jp.ossc.nimbus.service.connection.SQLMetricsCollector;
import jp.ossc.nimbus.service.performance.PerformanceRecorder;

public class WrappedStatement extends StatementWrapper{
    
    private static final long serialVersionUID = -9161665514224368780L;
    
    protected static final String DEFAULT_REQUEST_ID_KEY = "REQUEST_ID";
    protected static final String DEFAULT_JOURNAL_EXECUTE = "Execute";
    protected static final String DEFAULT_JOURNAL_SQL = "SQL";
    protected static final String SQL_SEPARATOR = ";";
    
    protected Journal journal;
    protected SQLMetricsCollector collector;
    protected PerformanceRecorder performanceRecorder;
    protected Context threadContext;
    protected String requestIDKey = DEFAULT_REQUEST_ID_KEY;
    protected Sequence sequence;
    protected String journalKeyExecute = DEFAULT_JOURNAL_EXECUTE;
    protected String journalKeySQL = DEFAULT_JOURNAL_SQL;
    protected EditorFinder editorFinderForExecute;
    protected String requestId;
    protected StringBuilder batchSQL;
    protected int batchSQLSize;
    protected int maxJournalBatchSize = -1;
    
    /**
     * 指定したStatementをラップするインスタンスを生成する。<p>
     *
     * @param st ラップするStatement
     */
    public WrappedStatement(Statement st){
        super(st);
    }
    
    /**
     * 指定したStatementをラップするインスタンスを生成する。<p>
     *
     * @param con このStatementを生成したConnection
     * @param st ラップするStatement
     */
    public WrappedStatement(Connection con, Statement st){
        super(con, st);
    }
    
    public void setJournal(Journal jnl){
        journal = jnl;
    }
    
    public void setMaxJournalBatchSize(int max){
        maxJournalBatchSize = max;
    }
    
    public void setSQLMetricsCollector(SQLMetricsCollector collector){
        this.collector = collector;
    }
    
    public void setPerformanceRecorder(PerformanceRecorder recorder){
        this.performanceRecorder = recorder;
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
    
    public void setJournalKeyExecute(String key){
        journalKeyExecute = key;
    }
    
    public void setEditorFinderForExecute(EditorFinder finder){
        editorFinderForExecute = finder;
    }
    
    public void setJournalKeySQL(String key){
        journalKeySQL = key;
    }
    
    protected void setRequestID(){
        if(journal == null){
            return;
        }
        if(requestId == null){
            if(threadContext != null && requestIDKey != null){
                requestId = (String)threadContext.get(requestIDKey);
            }
            if(requestId == null && sequence != null){
                requestId = sequence.increment();
                if(threadContext != null && requestIDKey != null){
                    threadContext.put(requestIDKey, requestId);
                }
            }
        }
        if(requestId != null){
            journal.setRequestId(requestId);
        }
    }
    
    public boolean execute(String arg0) throws SQLException {
        long start = 0;
        boolean isException = false;
        boolean isError = false;
        try{
            if(journal != null){
                setRequestID();
                journal.startJournal(journalKeyExecute, editorFinderForExecute);
                journal.addInfo(journalKeySQL, arg0);
            }
            if(collector != null || performanceRecorder != null){
                start = System.currentTimeMillis();
            }
            return super.execute(arg0);
        }catch(SQLException e){
            isException = true;
            throw e;
        }catch(RuntimeException e){
            isException = true;
            throw e;
        }catch(Error err){
            isError = true;
            throw err;
        }finally{
            if(collector != null){
                if(isException){
                    collector.registerException(arg0, System.currentTimeMillis() - start);
                }else if(isError){
                    collector.registerError(arg0, System.currentTimeMillis() - start);
                }else{
                    collector.register(arg0, System.currentTimeMillis() - start);
                }
            }
            if(performanceRecorder != null){
                performanceRecorder.record(start, System.currentTimeMillis());
            }
            if(journal != null){
                journal.endJournal();
            }
        }
    }
    
    public boolean execute(String arg0, int arg1) throws SQLException {
        long start = 0;
        boolean isException = false;
        boolean isError = false;
        try{
            if(journal != null){
                setRequestID();
                journal.startJournal(journalKeyExecute, editorFinderForExecute);
                journal.addInfo(journalKeySQL, arg0);
            }
            if(collector != null || performanceRecorder != null){
                start = System.currentTimeMillis();
            }
            return super.execute(arg0, arg1);
        }catch(SQLException e){
            isException = true;
            throw e;
        }catch(RuntimeException e){
            isException = true;
            throw e;
        }catch(Error err){
            isError = true;
            throw err;
        }finally{
            if(collector != null){
                if(isException){
                    collector.registerException(arg0, System.currentTimeMillis() - start);
                }else if(isError){
                    collector.registerError(arg0, System.currentTimeMillis() - start);
                }else{
                    collector.register(arg0, System.currentTimeMillis() - start);
                }
            }
            if(performanceRecorder != null){
                performanceRecorder.record(start, System.currentTimeMillis());
            }
            if(journal != null){
                journal.endJournal();
            }
        }
    }
    
    public boolean execute(String arg0, int[] arg1) throws SQLException {
        long start = 0;
        boolean isException = false;
        boolean isError = false;
        try{
            if(journal != null){
                setRequestID();
                journal.startJournal(journalKeyExecute, editorFinderForExecute);
                journal.addInfo(journalKeySQL, arg0);
            }
            if(collector != null || performanceRecorder != null){
                start = System.currentTimeMillis();
            }
            return super.execute(arg0, arg1);
        }catch(SQLException e){
            isException = true;
            throw e;
        }catch(RuntimeException e){
            isException = true;
            throw e;
        }catch(Error err){
            isError = true;
            throw err;
        }finally{
            if(collector != null){
                if(isException){
                    collector.registerException(arg0, System.currentTimeMillis() - start);
                }else if(isError){
                    collector.registerError(arg0, System.currentTimeMillis() - start);
                }else{
                    collector.register(arg0, System.currentTimeMillis() - start);
                }
            }
            if(performanceRecorder != null){
                performanceRecorder.record(start, System.currentTimeMillis());
            }
            if(journal != null){
                journal.endJournal();
            }
        }
    }
    
    public boolean execute(String arg0, String[] arg1) throws SQLException {
        long start = 0;
        boolean isException = false;
        boolean isError = false;
        try{
            if(journal != null){
                setRequestID();
                journal.startJournal(journalKeyExecute, editorFinderForExecute);
                journal.addInfo(journalKeySQL, arg0);
            }
            if(collector != null || performanceRecorder != null){
                start = System.currentTimeMillis();
            }
            return super.execute(arg0, arg1);
        }catch(SQLException e){
            isException = true;
            throw e;
        }catch(RuntimeException e){
            isException = true;
            throw e;
        }catch(Error err){
            isError = true;
            throw err;
        }finally{
            if(collector != null){
                if(isException){
                    collector.registerException(arg0, System.currentTimeMillis() - start);
                }else if(isError){
                    collector.registerError(arg0, System.currentTimeMillis() - start);
                }else{
                    collector.register(arg0, System.currentTimeMillis() - start);
                }
            }
            if(performanceRecorder != null){
                performanceRecorder.record(start, System.currentTimeMillis());
            }
            if(journal != null){
                journal.endJournal();
            }
        }
    }
    
    public int executeUpdate(String arg0) throws SQLException {
        long start = 0;
        boolean isException = false;
        boolean isError = false;
        try{
            if(journal != null){
                setRequestID();
                journal.startJournal(journalKeyExecute, editorFinderForExecute);
                journal.addInfo(journalKeySQL, arg0);
            }
            if(collector != null || performanceRecorder != null){
                start = System.currentTimeMillis();
            }
            return super.executeUpdate(arg0);
        }catch(SQLException e){
            isException = true;
            throw e;
        }catch(RuntimeException e){
            isException = true;
            throw e;
        }catch(Error err){
            isError = true;
            throw err;
        }finally{
            if(collector != null){
                if(isException){
                    collector.registerException(arg0, System.currentTimeMillis() - start);
                }else if(isError){
                    collector.registerError(arg0, System.currentTimeMillis() - start);
                }else{
                    collector.register(arg0, System.currentTimeMillis() - start);
                }
            }
            if(performanceRecorder != null){
                performanceRecorder.record(start, System.currentTimeMillis());
            }
            if(journal != null){
                journal.endJournal();
            }
        }
    }
    
    public int executeUpdate(String arg0, int arg1) throws SQLException {
        long start = 0;
        boolean isException = false;
        boolean isError = false;
        try{
            if(journal != null){
                setRequestID();
                journal.startJournal(journalKeyExecute, editorFinderForExecute);
                journal.addInfo(journalKeySQL, arg0);
            }
            if(collector != null || performanceRecorder != null){
                start = System.currentTimeMillis();
            }
            return super.executeUpdate(arg0, arg1);
        }catch(SQLException e){
            isException = true;
            throw e;
        }catch(RuntimeException e){
            isException = true;
            throw e;
        }catch(Error err){
            isError = true;
            throw err;
        }finally{
            if(collector != null){
                if(isException){
                    collector.registerException(arg0, System.currentTimeMillis() - start);
                }else if(isError){
                    collector.registerError(arg0, System.currentTimeMillis() - start);
                }else{
                    collector.register(arg0, System.currentTimeMillis() - start);
                }
            }
            if(performanceRecorder != null){
                performanceRecorder.record(start, System.currentTimeMillis());
            }
            if(journal != null){
                journal.endJournal();
            }
        }
    }
    
    public int executeUpdate(String arg0, int[] arg1) throws SQLException {
        long start = 0;
        boolean isException = false;
        boolean isError = false;
        try{
            if(journal != null){
                setRequestID();
                journal.startJournal(journalKeyExecute, editorFinderForExecute);
                journal.addInfo(journalKeySQL, arg0);
            }
            if(collector != null || performanceRecorder != null){
                start = System.currentTimeMillis();
            }
            return super.executeUpdate(arg0, arg1);
        }catch(SQLException e){
            isException = true;
            throw e;
        }catch(RuntimeException e){
            isException = true;
            throw e;
        }catch(Error err){
            isError = true;
            throw err;
        }finally{
            if(collector != null){
                if(isException){
                    collector.registerException(arg0, System.currentTimeMillis() - start);
                }else if(isError){
                    collector.registerError(arg0, System.currentTimeMillis() - start);
                }else{
                    collector.register(arg0, System.currentTimeMillis() - start);
                }
            }
            if(performanceRecorder != null){
                performanceRecorder.record(start, System.currentTimeMillis());
            }
            if(journal != null){
                journal.endJournal();
            }
        }
    }
    
    public int executeUpdate(String arg0, String[] arg1) throws SQLException {
        long start = 0;
        boolean isException = false;
        boolean isError = false;
        try{
            if(journal != null){
                setRequestID();
                journal.startJournal(journalKeyExecute, editorFinderForExecute);
                journal.addInfo(journalKeySQL, arg0);
            }
            if(collector != null || performanceRecorder != null){
                start = System.currentTimeMillis();
            }
            return super.executeUpdate(arg0, arg1);
        }catch(SQLException e){
            isException = true;
            throw e;
        }catch(RuntimeException e){
            isException = true;
            throw e;
        }catch(Error err){
            isError = true;
            throw err;
        }finally{
            if(collector != null){
                if(isException){
                    collector.registerException(arg0, System.currentTimeMillis() - start);
                }else if(isError){
                    collector.registerError(arg0, System.currentTimeMillis() - start);
                }else{
                    collector.register(arg0, System.currentTimeMillis() - start);
                }
            }
            if(performanceRecorder != null){
                performanceRecorder.record(start, System.currentTimeMillis());
            }
            if(journal != null){
                journal.endJournal();
            }
        }
    }
    
    public ResultSet executeQuery(String arg0) throws SQLException {
        long start = 0;
        boolean isException = false;
        boolean isError = false;
        try{
            if(journal != null){
                setRequestID();
                journal.startJournal(journalKeyExecute, editorFinderForExecute);
                journal.addInfo(journalKeySQL, arg0);
            }
            if(collector != null || performanceRecorder != null){
                start = System.currentTimeMillis();
            }
            return super.executeQuery(arg0);
        }catch(SQLException e){
            isException = true;
            throw e;
        }catch(RuntimeException e){
            isException = true;
            throw e;
        }catch(Error err){
            isError = true;
            throw err;
        }finally{
            if(collector != null){
                if(isException){
                    collector.registerException(arg0, System.currentTimeMillis() - start);
                }else if(isError){
                    collector.registerError(arg0, System.currentTimeMillis() - start);
                }else{
                    collector.register(arg0, System.currentTimeMillis() - start);
                }
            }
            if(performanceRecorder != null){
                performanceRecorder.record(start, System.currentTimeMillis());
            }
            if(journal != null){
                journal.endJournal();
            }
        }
    }
    
    public void addBatch(String arg0) throws SQLException {
        super.addBatch(arg0);
        if(journal != null && batchSQL == null){
            batchSQL = new StringBuilder();
            batchSQLSize = 0;
        }
        if(batchSQL != null
            && (maxJournalBatchSize < 0 || batchSQLSize < maxJournalBatchSize)){
            batchSQL.append(arg0).append(SQL_SEPARATOR);
            batchSQLSize++;
        }
    }
    
    public int[] executeBatch() throws SQLException {
        long start = 0;
        boolean isException = false;
        boolean isError = false;
        String sql = null;
        String collectorKey = getBatchQueryForCollectorKey();
        if(batchSQL != null){
            sql = batchSQL.toString();
            batchSQL.setLength(0);
            batchSQLSize = 0;
        }
        try{
            if(journal != null){
                setRequestID();
                journal.startJournal(journalKeyExecute, editorFinderForExecute);
                if(sql != null){
                    journal.addInfo(journalKeySQL, sql);
                }
            }
            if(collector != null || performanceRecorder != null){
                start = System.currentTimeMillis();
            }
            return super.executeBatch();
        }catch(SQLException e){
            isException = true;
            throw e;
        }catch(RuntimeException e){
            isException = true;
            throw e;
        }catch(Error err){
            isError = true;
            throw err;
        }finally{
            if(collector != null){
                if(isException){
                    collector.registerException(collectorKey, System.currentTimeMillis() - start);
                }else if(isError){
                    collector.registerError(collectorKey, System.currentTimeMillis() - start);
                }else{
                    collector.register(collectorKey, System.currentTimeMillis() - start);
                }
            }
            if(performanceRecorder != null){
                performanceRecorder.record(start, System.currentTimeMillis());
            }
            if(journal != null){
                journal.endJournal();
            }
        }
    }
    
    protected String getBatchQueryForCollectorKey(){
        return batchSQL == null ? "" : batchSQL.toString();
    }
    
    public void clearBatch() throws SQLException {
        super.clearBatch();
        if(batchSQL != null){
            batchSQL.setLength(0);
            batchSQLSize = 0;
        }
    }
}