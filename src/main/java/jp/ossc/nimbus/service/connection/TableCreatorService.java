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
package jp.ossc.nimbus.service.connection;

import java.io.*;
import java.sql.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.beans.dataset.*;
import jp.ossc.nimbus.io.CSVRecordReader;
import jp.ossc.nimbus.io.FLVRecordReader;
import jp.ossc.nimbus.io.CSVRecordWriter;
import jp.ossc.nimbus.io.FLVRecordWriter;
import jp.ossc.nimbus.util.*;
import jp.ossc.nimbus.util.converter.*;

/**
 * テーブル生成サービス。<p>
 * 設定通りに、テーブルのCREATE、INSERT、DELETE、DROP等を行う。<br>
 * また、テーブルをファイルへバックアップしたり、ファイルからテーブルに復元したりする事もできる。<br>
 *
 * @author M.Takata
 */
public class TableCreatorService extends ServiceBase
 implements TableCreatorServiceMBean{
    
    private static final long serialVersionUID = 3473661138375595733L;
    private ServiceName connectionFactoryServiceName;
    private ConnectionFactory connectionFactory;
    
    private ServiceName recordListConverterServiceName;
    private StreamConverter recordListConverter;
    
    private String tableName;
    private String existsTableQuery;
    private String selectQuery;
    private String createTableQuery;
    private String createTableQueryFilePath;
    private String[] preCreateTableQueries;
    private String[] postCreateTableQueries;
    private String dropTableQuery;
    private String[] preDropTableQueries;
    private String[] postDropTableQueries;
    private String deleteQuery;
    private String insertQuery;
    private String insertRecords;
    private String insertRecordsFilePath;
    private String fileEncoding;
    private String recordListSchema;
    private RecordList recordList;
    private String backupFilePath;
    private RecordList backupRecordList;
    private int insertBatchSize = 0;
    private int fetchSize = 0;
    private boolean isBackupOnStart = false;
    private boolean isRestoreOnStart = false;
    private boolean isDropTableOnStart = false;
    private boolean isCreateTableOnStart = false;
    private boolean isDeleteOnStart = false;
    private boolean isInsertOnStart = false;
    private boolean isDeleteOnStop = false;
    private boolean isInsertOnStop = false;
    private boolean isDropTableOnStop = false;
    private boolean isRestoreOnStop = false;
    private boolean isBackupOnStop = false;
    private int[] ignoreSQLExceptionErrorCodeOnSelect;
    private int[] ignoreSQLExceptionErrorCodeOnDropTable;
    private int[] ignoreSQLExceptionErrorCodeOnDelete;
    private int[] ignoreSQLExceptionErrorCodeOnCreateTable;
    private int[] ignoreSQLExceptionErrorCodeOnInsert;
    private boolean isTransacted = false;
    private ClassMappingTree sqlTypeMap;
    private CSVRecordReader csvReader;
    private FLVRecordReader flvReader;
    private CSVRecordWriter csvWriter;
    private FLVRecordWriter flvWriter;
    
    public void setConnectionFactoryServiceName(ServiceName name){
        connectionFactoryServiceName = name;
    }
    public ServiceName getConnectionFactoryServiceName(){
        return connectionFactoryServiceName;
    }
    
    public void setRecordListConverterServiceName(ServiceName name){
        recordListConverterServiceName = name;
    }
    public ServiceName getRecordListConverterServiceName(){
        return recordListConverterServiceName;
    }
    
    public void setTableName(String name) {
        tableName = name;
    }
    public String getTableName() {
        return tableName;
    }
    
    public void setExistsTableQuery(String query){
        existsTableQuery = query;
    }
    public String getExistsTableQuery(){
        return existsTableQuery;
    }
    
    public void setSelectQuery(String query){
        selectQuery = query;
    }
    public String getSelectQuery(){
        return selectQuery;
    }
    
    public void setCreateTableQuery(String query){
        createTableQuery = query;
    }
    public String getCreateTableQuery(){
        return createTableQuery;
    }
    
    public void setCreateTableQueryFilePath(String filePath) {
        createTableQueryFilePath = filePath;
    }
    public String getCreateTableQueryFilePath() {
        return createTableQueryFilePath;
    }
    
    public void setPreCreateTableQueries(String[] queries){
        preCreateTableQueries = queries;
    }
    public String[] getPreCreateTableQueries(){
        return preCreateTableQueries;
    }
    
    public void setPostCreateTableQueries(String[] queries){
        postCreateTableQueries = queries;
    }
    public String[] getPostCreateTableQueries(){
        return postCreateTableQueries;
    }
    
    public void setDropTableQuery(String query){
        dropTableQuery = query;
    }
    public String getDropTableQuery(){
        return dropTableQuery;
    }
    
    public void setPreDropTableQueries(String[] queries){
        preDropTableQueries = queries;
    }
    public String[] getPreDropTableQueries(){
        return preDropTableQueries;
    }
    
    public void setPostDropTableQueries(String[] queries){
        postDropTableQueries = queries;
    }
    public String[] getPostDropTableQueries(){
        return postDropTableQueries;
    }
    
    public void setDeleteQuery(String query){
        deleteQuery = query;
    }
    public String getDeleteQuery(){
        return deleteQuery;
    }
    
    public void setInsertQuery(String query){
        insertQuery = query;
    }
    public String getInsertQuery(){
        return insertQuery;
    }
    
    public void setInsertRecords(String records){
        insertRecords = records;
    }
    public String getInsertRecords(){
        return insertRecords;
    }
    
    public void setInsertRecordsFilePath(String path){
        insertRecordsFilePath = path;
    }
    public String getInsertRecordsFilePath(){
        return insertRecordsFilePath;
    }
    
    public void setFileEncoding(String enc){
        fileEncoding = enc;
    }
    public String getFileEncoding(){
        return fileEncoding;
    }
    
    public void setRecordListSchema(String schema){
        recordListSchema = schema;
    }
    public String getRecordListSchema(){
        return recordListSchema;
    }
    
    public void setRecordList(RecordList list){
        recordList = list;
    }
    public RecordList getRecordList(){
        return recordList;
    }
    
    public void setInsertBatchSize(int size){
        insertBatchSize = size;
    }
    public int getInsertBatchSize(){
        return insertBatchSize;
    }
    
    public void setFetchSize(int size){
        fetchSize = size;
    }
    public int getFetchSize(){
        return fetchSize;
    }
    
    public void setBackupOnStart(boolean isBackup){
        isBackupOnStart = isBackup;
    }
    public boolean isBackupOnStart(){
        return isBackupOnStart;
    }
    
    public void setRestoreOnStart(boolean isRestore){
        isRestoreOnStart = isRestore;
    }
    public boolean isRestoreOnStart(){
        return isRestoreOnStart;
    }
    
    public void setDropTableOnStart(boolean isDrop){
        isDropTableOnStart = isDrop;
    }
    public boolean isDropTableOnStart(){
        return isDropTableOnStart;
    }
    
    public void setCreateTableOnStart(boolean isCreate){
        isCreateTableOnStart = isCreate;
    }
    public boolean isCreateTableOnStart(){
        return isCreateTableOnStart;
    }
    
    public void setDeleteOnStart(boolean isDelete){
        isDeleteOnStart = isDelete;
    }
    public boolean isDeleteOnStart(){
        return isDeleteOnStart;
    }
    
    public void setInsertOnStart(boolean isInsert){
        isInsertOnStart = isInsert;
    }
    public boolean isInsertOnStart(){
        return isInsertOnStart;
    }
    
    public void setDeleteOnStop(boolean isDelete){
        isDeleteOnStop = isDelete;
    }
    public boolean isDeleteOnStop(){
        return isDeleteOnStop;
    }
    
    public void setInsertOnStop(boolean isInsert){
        isInsertOnStop = isInsert;
    }
    public boolean isInsertOnStop(){
        return isInsertOnStop;
    }
    
    public void setDropTableOnStop(boolean isDrop){
        isDropTableOnStop = isDrop;
    }
    public boolean isDropTableOnStop(){
        return isDropTableOnStop;
    }
    
    public void setRestoreOnStop(boolean isRestore){
        isRestoreOnStop = isRestore;
    }
    public boolean isRestoreOnStop(){
        return isRestoreOnStop;
    }
    
    public void setBackupOnStop(boolean isBackup){
        isBackupOnStop = isBackup;
    }
    public boolean isBackupOnStop(){
        return isBackupOnStop;
    }
    
    public void setIgnoreSQLExceptionErrorCodeOnSelect(int[] code){
        ignoreSQLExceptionErrorCodeOnSelect = code;
    }
    public int[] getIgnoreSQLExceptionErrorCodeOnSelect(){
        return ignoreSQLExceptionErrorCodeOnSelect;
    }
    
    public void setIgnoreSQLExceptionErrorCodeOnDropTable(int[] code){
        ignoreSQLExceptionErrorCodeOnDropTable = code;
    }
    public int[] getIgnoreSQLExceptionErrorCodeOnDropTable(){
        return ignoreSQLExceptionErrorCodeOnDropTable;
    }
    
    public void setIgnoreSQLExceptionErrorCodeOnDelete(int[] code){
        ignoreSQLExceptionErrorCodeOnDelete = code;
    }
    public int[] getIgnoreSQLExceptionErrorCodeOnDelete(){
        return ignoreSQLExceptionErrorCodeOnDelete;
    }
    
    public void setIgnoreSQLExceptionErrorCodeOnCreateTable(int[] code){
        ignoreSQLExceptionErrorCodeOnCreateTable = code;
    }
    public int[] getIgnoreSQLExceptionErrorCodeOnCreateTable(){
        return ignoreSQLExceptionErrorCodeOnCreateTable;
    }
    
    public void setIgnoreSQLExceptionErrorCodeOnInsert(int[] code){
        ignoreSQLExceptionErrorCodeOnInsert = code;
    }
    public int[] getIgnoreSQLExceptionErrorCodeOnInsert(){
        return ignoreSQLExceptionErrorCodeOnInsert;
    }
    
    public void setTransacted(boolean isTransacted){
        this.isTransacted = isTransacted;
    }
    public boolean isTransacted(){
        return isTransacted;
    }
    
    public void setSqlType(Class type, int sqlType){
        sqlTypeMap.add(type, new Integer(sqlType), true);
    }
    
    public void setBackupFilePath(String path){
        backupFilePath = path;
    }
    public String getBackupFilePath(){
        return backupFilePath;
    }
    
    public void setCSVRecordReader(CSVRecordReader reader){
        csvReader = reader;
    }
    
    public void setFLVRecordReader(FLVRecordReader reader){
        flvReader = reader;
    }
    
    public void setCSVRecordWriter(CSVRecordWriter writer){
        csvWriter = writer;
    }
    
    public void setFLVRecordWriter(FLVRecordWriter writer){
        flvWriter = writer;
    }
    
    public void createService() throws Exception{
        sqlTypeMap = new ClassMappingTree(new Integer(Types.JAVA_OBJECT));
        sqlTypeMap.add(Boolean.class, new Integer(Types.BIT));
        sqlTypeMap.add(Byte.class, new Integer(Types.NUMERIC));
        sqlTypeMap.add(Short.class, new Integer(Types.NUMERIC));
        sqlTypeMap.add(Integer.class, new Integer(Types.INTEGER));
        sqlTypeMap.add(Long.class, new Integer(Types.BIGINT));
        sqlTypeMap.add(java.math.BigDecimal.class, new Integer(Types.DECIMAL));
        sqlTypeMap.add(Float.class, new Integer(Types.REAL));
        sqlTypeMap.add(Double.class, new Integer(Types.DOUBLE));
        sqlTypeMap.add(Character.class, new Integer(Types.CHAR));
        sqlTypeMap.add(String.class, new Integer(Types.VARCHAR));
        sqlTypeMap.add(java.sql.Date.class, new Integer(Types.DATE));
        sqlTypeMap.add(java.sql.Time.class, new Integer(Types.TIME));
        sqlTypeMap.add(java.sql.Timestamp.class, new Integer(Types.TIMESTAMP));
    }
    
    public void startService() throws Exception{
        if(connectionFactoryServiceName != null){
            connectionFactory = (ConnectionFactory)ServiceManagerFactory
                .getServiceObject(connectionFactoryServiceName);
        }
        if(connectionFactory == null){
            throw new IllegalArgumentException("ConnectionFactory is null.");
        }
        if(recordListConverterServiceName != null){
            recordListConverter = (StreamConverter)ServiceManagerFactory
                .getServiceObject(recordListConverterServiceName);
        }
        if(recordList != null){
            backupRecordList = recordList.cloneSchema();
        }else if(recordListSchema != null){
            backupRecordList = new RecordList();
            backupRecordList.setSchema(recordListSchema);
        }
        if(createTableQuery == null && createTableQueryFilePath != null) {
            StringWriter sw = new StringWriter();
            InputStreamReader reader = fileEncoding == null ? new InputStreamReader(new FileInputStream(createTableQueryFilePath)) : new InputStreamReader(new FileInputStream(createTableQueryFilePath),fileEncoding);
            try{
                int len = 0;
                char[] buf = new char[1024];
                while((len = reader.read(buf, 0, buf.length)) > 0){
                    sw.write(buf, 0, len);
                }
                createTableQuery = sw.toString();
            }finally{
                reader.close();
            }
        }
        final Connection con = connectionFactory.getConnection();
        if(isTransacted){
            if(con.getAutoCommit()){
                con.setAutoCommit(false);
            }
        }
        try{
            if(isBackupOnStart){
                backupRecords(con);
            }
            if(isDeleteOnStart){
                deleteRecords(con);
            }
            if(isDropTableOnStart){
                dropTable(con);
            }
            if(isCreateTableOnStart){
                createTable(con);
            }
            if(isInsertOnStart){
                insertRecords(con);
            }
            if(isRestoreOnStart){
                restoreRecords(con);
            }
            if(isTransacted){
                con.commit();
            }
        }catch(SQLException e){
            if(isTransacted){
                con.rollback();
            }
            throw e;
        }finally{
            con.close();
        }
    }
    
    public void stopService() throws Exception{
        final Connection con = connectionFactory.getConnection();
        if(isTransacted){
            if(con.getAutoCommit()){
                con.setAutoCommit(false);
            }
        }
        try{
            if(isBackupOnStop){
                backupRecords(con);
            }
            if(isDeleteOnStop){
                deleteRecords(con);
            }
            if(isInsertOnStop){
                insertRecords(con);
            }
            if(isDropTableOnStop){
                dropTable(con);
            }
            if(isRestoreOnStop){
                restoreRecords(con);
            }
            if(isTransacted){
                con.commit();
            }
        }catch(SQLException e){
            if(isTransacted){
                con.rollback();
            }
            throw e;
        }finally{
            con.close();
        }
    }
    
    public void setConnectionFactory(ConnectionFactory factory){
        connectionFactory = factory;
    }
    public ConnectionFactory getConnectionFactory(){
        return connectionFactory;
    }
    
    public void setRecordListConverter(StreamConverter converter){
        recordListConverter = converter;
    }
    public StreamConverter getRecordListConverter(){
        return recordListConverter;
    }
    
    public void executeAllQuery()
     throws ConnectionFactoryException, SQLException,
            ConvertException, IOException{
        final Connection con = connectionFactory.getConnection();
        if(isTransacted){
            if(con.getAutoCommit()){
                con.setAutoCommit(false);
            }
        }
        try{
            dropTable(con);
            deleteRecords(con);
            createTable(con);
            insertRecords(con);
            if(isTransacted){
                con.commit();
            }
        }catch(SQLException e){
            if(isTransacted){
                con.rollback();
            }
            throw e;
        }finally{
            con.close();
        }
    }
    
    protected boolean existsTable(Statement stmt) throws SQLException{
        boolean existsTable = true;
        if(existsTableQuery != null){
            final ResultSet rs = stmt.executeQuery(existsTableQuery);
            rs.next();
            existsTable = rs.getInt(1) != 0;
            rs.close();
        }
        return existsTable;
    }
    
    public void backupRecords()
     throws ConnectionFactoryException, SQLException,
            IOException, ConvertException{
        final Connection con = connectionFactory.getConnection();
        try{
            backupRecords(con);
        }finally{
            con.close();
        }
    }
    
    protected void backupRecords(Connection con)
     throws IOException, ConvertException, SQLException{
        if(selectQuery == null || backupRecordList == null || backupFilePath == null){
            return;
        }
        final File backupFile = new File(backupFilePath);
        if(!backupFile.exists()){
            File dir = backupFile.getParentFile();
            if(dir != null && !dir.exists()){
                dir.mkdirs();
            }
        }
        final FileOutputStream fos
            = new FileOutputStream(backupFilePath);
        Statement stmt = con.createStatement();
        ResultSet rs = null;
        try{
            if(!existsTable(stmt)){
                return;
            }
            if(recordListConverter != null){
                RecordList recList = backupRecordList.cloneSchema();
                if(fetchSize > 0){
                    stmt.setFetchSize(fetchSize);
                }
                rs = stmt.executeQuery(selectQuery);
                final RecordSchema schema = recList.getRecordSchema();
                while(rs.next()){
                    Record rec = recList.createRecord();
                    for(int i = 0, imax = schema.getPropertySize(); i < imax; i++){
                        final String name = schema.getPropertyName(i);
                        rec.setProperty(name, rs.getObject(name));
                    }
                    recList.addRecord(rec);
                }
                rs.close();
                rs = null;
                stmt.close();
                stmt = null;
                final InputStream is = recordListConverter
                    .convertToStream(recList);
                try{
                    byte[] bytes = new byte[1024];
                    int length = 0;
                    while((length = is.read(bytes)) != -1){
                        fos.write(bytes, 0, length);
                    }
                }finally{
                    fos.close();
                    is.close();
                }
            }else if(flvWriter != null){
                flvWriter.setWriter(
                    new BufferedWriter(
                        fileEncoding == null ? new OutputStreamWriter(fos)
                            : new OutputStreamWriter(fos, fileEncoding)
                    )
                );
                Record rec = backupRecordList.createRecord();
                if(fetchSize > 0){
                    stmt.setFetchSize(fetchSize);
                }
                rs = stmt.executeQuery(selectQuery);
                final RecordSchema schema = backupRecordList.getRecordSchema();
                int count = 0;
                while(rs.next()){
                    rec.clear();
                    for(int i = 0, imax = schema.getPropertySize(); i < imax; i++){
                        final String name = schema.getPropertyName(i);
                        rec.setProperty(name, rs.getObject(name));
                    }
                    flvWriter.writeRecord(rec);
                    count++;
                    if(count >= fetchSize){
                        flvWriter.flush();
                        count = 0;
                    }
                }
                flvWriter.flush();
            }else{
                if(csvWriter == null){
                    csvWriter = new CSVRecordWriter();
                }
                csvWriter.setWriter(
                    new BufferedWriter(
                        fileEncoding == null ? new OutputStreamWriter(fos)
                            : new OutputStreamWriter(fos, fileEncoding)
                    )
                );
                Record rec = backupRecordList.createRecord();
                if(fetchSize > 0){
                    stmt.setFetchSize(fetchSize);
                }
                rs = stmt.executeQuery(selectQuery);
                final RecordSchema schema = backupRecordList.getRecordSchema();
                int count = 0;
                while(rs.next()){
                    rec.clear();
                    for(int i = 0, imax = schema.getPropertySize(); i < imax; i++){
                        final String name = schema.getPropertyName(i);
                        rec.setProperty(name, rs.getObject(name));
                    }
                    csvWriter.writeRecord(rec);
                    count++;
                    if(count >= fetchSize){
                        csvWriter.flush();
                        count = 0;
                    }
                }
                csvWriter.flush();
            }
        }catch(SQLException e){
            handleSQLException(e, ignoreSQLExceptionErrorCodeOnSelect);
        }finally{
            if(rs != null){
                rs.close();
            }
            if(stmt != null){
                stmt.close();
            }
            if(fos != null){
                fos.close();
            }
        }
    }
    
    public void dropTable() throws ConnectionFactoryException, SQLException{
        final Connection con = connectionFactory.getConnection();
        try{
            dropTable(con);
        }finally{
            con.close();
        }
    }
    
    protected void dropTable(Connection con) throws SQLException{
        if(dropTableQuery == null){
            return;
        }
        try{
            Statement stmt = con.createStatement();
            if(existsTable(stmt)){
                if(preDropTableQueries != null){
                    for(int i = 0; i < preDropTableQueries.length; i++){
                        try{
                            stmt.executeUpdate(preDropTableQueries[i]);
                        }catch(SQLException e){
                            handleSQLException(e, ignoreSQLExceptionErrorCodeOnDropTable);
                        }
                    }
                }
                try{
                    stmt.executeUpdate(dropTableQuery);
                }catch(SQLException e){
                    handleSQLException(e, ignoreSQLExceptionErrorCodeOnDropTable);
                }
                if(postDropTableQueries != null){
                    for(int i = 0; i < postDropTableQueries.length; i++){
                        try{
                            stmt.executeUpdate(postDropTableQueries[i]);
                        }catch(SQLException e){
                            handleSQLException(e, ignoreSQLExceptionErrorCodeOnDropTable);
                        }
                    }
                }
            }
            stmt.close();
        }catch(SQLException e){
            handleSQLException(e, ignoreSQLExceptionErrorCodeOnDropTable);
        }
    }
    
    protected void handleSQLException(SQLException e, int[] ignoreErrorCodes) throws SQLException{
        if(ignoreErrorCodes != null){
            final int errorCode = e.getErrorCode();
            boolean isIgnore = false;
            for(int i = 0; i < ignoreErrorCodes.length; i++){
                if(ignoreErrorCodes[i] == errorCode){
                    isIgnore = true;
                    break;
                }
            }
            if(!isIgnore){
                throw e;
            }
        }else{
            throw e;
        }
    }
    
    public void deleteRecords() throws ConnectionFactoryException, SQLException{
        final Connection con = connectionFactory.getConnection();
        try{
            deleteRecords(con);
        }finally{
            con.close();
        }
    }
    
    protected void deleteRecords(Connection con) throws SQLException{
        if(deleteQuery == null){
            return;
        }
        try{
            Statement stmt = con.createStatement();
            if(existsTable(stmt)){
                stmt.executeUpdate(deleteQuery);
            }
            stmt.close();
        }catch(SQLException e){
            handleSQLException(e, ignoreSQLExceptionErrorCodeOnDelete);
        }
    }
    
    public void createTable() throws ConnectionFactoryException, SQLException{
        final Connection con = connectionFactory.getConnection();
        try{
            createTable(con);
        }finally{
            con.close();
        }
    }
    
    protected void createTable(Connection con) throws SQLException{
        if(createTableQuery == null){
            return;
        }
        try{
            Statement stmt = con.createStatement();
            if(!existsTable(stmt)){
                if(preCreateTableQueries != null){
                    for(int i = 0; i < preCreateTableQueries.length; i++){
                        try{
                            stmt.executeUpdate(preCreateTableQueries[i]);
                        }catch(SQLException e){
                            handleSQLException(e, ignoreSQLExceptionErrorCodeOnCreateTable);
                        }
                    }
                }
                try{
                    stmt.executeUpdate(createTableQuery);
                }catch(SQLException e){
                    handleSQLException(e, ignoreSQLExceptionErrorCodeOnCreateTable);
                }
                if(postCreateTableQueries != null){
                    for(int i = 0; i < postCreateTableQueries.length; i++){
                        try{
                            stmt.executeUpdate(postCreateTableQueries[i]);
                        }catch(SQLException e){
                            handleSQLException(e, ignoreSQLExceptionErrorCodeOnCreateTable);
                        }
                    }
                }
            }
            stmt.close();
        }catch(SQLException e){
            handleSQLException(e, ignoreSQLExceptionErrorCodeOnCreateTable);
        }
    }
    
    public void insertRecords()
     throws ConnectionFactoryException, IOException,
            ConvertException, SQLException{
        final Connection con = connectionFactory.getConnection();
        try{
            insertRecords(con);
        }finally{
            con.close();
        }
    }
    
    protected void insertRecords(Connection con)
     throws IOException, ConvertException, SQLException{
        if(insertQuery == null){
            return;
        }
        InputStream is = null;
        try{
            Statement stmt = con.createStatement();
            if(existsTable(stmt)){
                stmt.close();
                stmt = null;
                PreparedStatement pstmt = con.prepareStatement(insertQuery);
                if(insertRecords != null){
                    is = new ByteArrayInputStream(insertRecords.getBytes());
                }else if(insertRecordsFilePath != null){
                    if(new File(insertRecordsFilePath).exists()){
                        is = new FileInputStream(insertRecordsFilePath);
                    }
                    if(is == null && getServiceNameObject() != null){
                        ServiceMetaData metaData = ServiceManagerFactory.getServiceMetaData(getServiceNameObject());
                        if(metaData != null){
                            jp.ossc.nimbus.core.ServiceLoader loader = metaData.getServiceLoader();
                            if(loader != null){
                                String filePath = loader.getServiceURL().getFile();
                                if(filePath != null){
                                    File serviceDefDir = new File(filePath).getParentFile();
                                    File file = new File(serviceDefDir, insertRecordsFilePath);
                                    if(file.exists()){
                                        is = new FileInputStream(file);
                                    }
                                }
                            }
                        }
                    }
                    if(is == null){
                        ClassLoader loader = NimbusClassLoader.getInstance();
                        is = loader.getResourceAsStream(insertRecordsFilePath);
                    }
                    if(is == null){
                        throw new IOException("InsertRecordsFilePath not found. path=" + insertRecordsFilePath);
                    }
                }else{
                    return;
                }
                
                RecordList recList = recordList;
                if(recList != null){
                    insertRecords(pstmt, recList);
                }else if(recordListConverter != null){
                    if(recordListSchema != null){
                        recList = new RecordList();
                        recList.setSchema(recordListSchema);
                    }
                    try{
                        if(recList == null){
                            recList = (RecordList)recordListConverter.convertToObject(is);
                        }else{
                            recList = (RecordList)((BindingStreamConverter)recordListConverter).convertToObject(is, recList);
                        }
                    }finally{
                        is.close();
                    }
                    
                    insertRecords(pstmt, recList);
                }else if(flvReader != null){
                    if(recordListSchema != null){
                        flvReader.setRecordSchema(RecordSchema.getInstance(recordListSchema));
                    }
                    flvReader.setReader(
                        fileEncoding == null ? new InputStreamReader(is)
                            : new InputStreamReader(is, fileEncoding)
                    );
                    insertRecords(pstmt, flvReader);
                }else{
                    if(csvReader == null){
                        csvReader = new CSVRecordReader();
                    }
                    if(recordListSchema != null){
                        csvReader.setRecordSchema(RecordSchema.getInstance(recordListSchema));
                    }
                    csvReader.setReader(
                        fileEncoding == null ? new InputStreamReader(is)
                            : new InputStreamReader(is, fileEncoding)
                    );
                    insertRecords(pstmt, csvReader);
                }
                
                pstmt.close();
            }
            if(stmt != null){
                stmt.close();
            }
        }catch(SQLException e){
            handleSQLException(e, ignoreSQLExceptionErrorCodeOnInsert);
        }finally{
            if(is != null){
                try{
                    is.close();
                }catch(IOException e){}
            }
        }
    }
    
    protected void insertRecords(PreparedStatement pstmt, RecordList recList)
     throws SQLException{
        if(recList == null || recList.size() == 0){
            return;
        }
        int batchCount = 0;
        for(int i = 0, imax = recList.size(); i < imax; i++){
            final Record rec = recList.getRecord(i);
            batchCount = insertRecord(pstmt, rec, batchCount);
        }
        if(insertBatchSize > 0){
            pstmt.executeBatch();
        }
    }
    
    protected void insertRecords(PreparedStatement pstmt, CSVRecordReader reader)
     throws SQLException, IOException{
        Record rec = null;
        int batchCount = 0;
        while((rec = reader.readRecord(rec)) != null){
            batchCount = insertRecord(pstmt, rec, batchCount);
        }
        if(insertBatchSize > 0){
            pstmt.executeBatch();
        }
    }
    
    protected void insertRecords(PreparedStatement pstmt, FLVRecordReader reader)
     throws SQLException, IOException{
        Record rec = null;
        int batchCount = 0;
        while((rec = reader.readRecord(rec)) != null){
            batchCount = insertRecord(pstmt, rec, batchCount);
        }
        if(insertBatchSize > 0){
            pstmt.executeBatch();
        }
    }
    
    protected int insertRecord(PreparedStatement pstmt, Record rec, int batchCount) throws SQLException{
        final RecordSchema schema = rec.getRecordSchema();
        for(int j = 0, jmax = rec.size(); j < jmax; j++){
            try{
                Object val = rec.getProperty(j);
                if(val == null){
                    int sqlType = Types.VARCHAR;
                    if(schema != null){
                        Class type = schema.getPropertySchema(j).getType();
                        if(type != null){
                            Integer sqlTypeVal = (Integer)sqlTypeMap.getValue(type);
                            if(sqlTypeVal != null){
                                sqlType = sqlTypeVal.intValue();
                            }
                        }
                    }
                    pstmt.setNull(j + 1, sqlType);
                }else{
                    pstmt.setObject(j + 1, val);
                }
            }catch(SQLException e){
                handleSQLException(e, ignoreSQLExceptionErrorCodeOnInsert);
            }
        }
        try{
            if(insertBatchSize > 0){
                if(batchCount >= insertBatchSize){
                    pstmt.executeBatch();
                    pstmt.clearBatch();
                    batchCount = 0;
                }else{
                    pstmt.addBatch();
                    batchCount++;
                }
            }else{
                pstmt.executeUpdate();
            }
        }catch(SQLException e){
            handleSQLException(e, ignoreSQLExceptionErrorCodeOnInsert);
        }
        return batchCount;
    }
    
    public void restoreRecords()
     throws ConnectionFactoryException, SQLException,
            IOException, ConvertException{
        final Connection con = connectionFactory.getConnection();
        try{
            restoreRecords(con);
        }finally{
            con.close();
        }
    }
    
    protected void restoreRecords(Connection con)
     throws IOException, ConvertException, SQLException{
        if(backupRecordList == null
            || insertQuery == null
        ){
            return;
        }
        if(deleteQuery != null){
            deleteRecords(con);
        }else if(dropTableQuery != null
            && createTableQuery != null){
            dropTable(con);
            createTable(con);
        }
        try{
            Statement stmt = con.createStatement();
            if(existsTable(stmt)){
                stmt.close();
                stmt = null;
                PreparedStatement pstmt = con.prepareStatement(insertQuery);
                
                RecordList recList = backupRecordList;
                if(backupFilePath != null && new File(backupFilePath).exists()){
                    
                    final FileInputStream fis = new FileInputStream(backupFilePath);
                    if(recordListConverter != null){
                        try{
                            recList = backupRecordList.cloneSchema();
                            recList = (RecordList)((BindingStreamConverter)recordListConverter)
                                .convertToObject(fis, recList);
                        }finally{
                            fis.close();
                        }
                        insertRecords(pstmt, recList);
                    }else if(flvReader != null){
                        flvReader.setRecordSchema(backupRecordList.getRecordSchema());
                        flvReader.setReader(
                            fileEncoding == null ? new InputStreamReader(fis)
                                : new InputStreamReader(fis, fileEncoding)
                        );
                        try{
                            insertRecords(pstmt, flvReader);
                        }finally{
                            fis.close();
                        }
                    }else{
                        if(csvReader == null){
                            csvReader = new CSVRecordReader();
                        }
                        csvReader.setRecordSchema(backupRecordList.getRecordSchema());
                        csvReader.setReader(
                            fileEncoding == null ? new InputStreamReader(fis)
                                : new InputStreamReader(fis, fileEncoding)
                        );
                        try{
                            insertRecords(pstmt, csvReader);
                        }finally{
                            fis.close();
                        }
                    }
                }
                pstmt.close();
            }
            if(stmt != null){
                stmt.close();
            }
        }catch(SQLException e){
            handleSQLException(e, ignoreSQLExceptionErrorCodeOnInsert);
        }
     }
}