package jp.ossc.nimbus.service.resource.datasource;
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
//�C���|�[�g
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * Statement���b�p�[�N���X<p>
 * ���g���쐬�������U���g�Z�b�g�܂ŐӔC�������ăN���[�Y����B
 * @version $Name:  $
 * @author K.Nagai
 * @since 1.0
 */
public class NimbusStatement extends AbstNimbusStatement implements Statement {

    //Open���Ă��邩�ǂ���
    protected boolean mIsOpen = true ;
    //BatchSQL�̃Z�p���^
    final String BATCH_SEPARATOR=System.getProperty("line.separator");
    //[ms]�\��
    final protected String PF_FOOTER = "[ms]";
    //�L�[�ɕt��������l
    final protected String PF_KEY_HEADER = ":Performance";
    
 
	//�o�b�`�u���������SQL�ۑ��p
	protected ArrayList mBatchArray = new ArrayList();
		
	/**
	 * 
	 */
	public NimbusStatement(Statement st) {
		super(st); 
		mStatement = st ;
	}


	/* (�� Javadoc)
	 * @see java.sql.Statement#cancel()
	 */
	public void cancel() throws SQLException {
		mStatement.cancel() ;
	}

	/* (�� Javadoc)
	 * @see java.sql.Statement#clearBatch()
	 */
	public void clearBatch() throws SQLException {
		mStatement.clearBatch() ;
		//�o�b�`�p�ɕۑ����Ă��������e���N���A
		if(journalService != null){		    
		    mBatchArray.clear();
		}
	}

	/* (�� Javadoc)
	 * @see java.sql.Statement#close()
	 */
	public void close() throws SQLException {
		//�z����ResultSet�����
		for(int cnt = 0; cnt<this.mResultSetList.size();cnt++){
			ResultSet tmp = (ResultSet)mResultSetList.get(cnt) ;
			tmp.close() ;
		}
		
		mResultSetList.clear() ;
		//���g���Ƃ��Ă��Ȃ���Ε���B
		if(this.mIsOpen){
			this.mStatement.close() ;
			mIsOpen = false; 
		}
		//Batch�pSQL�̌㏈��
		if( journalService != null ){
		    mBatchArray.clear();
		}
	}

	

	/* (�� Javadoc)
	 * @see java.sql.Statement#executeBatch()
	 */
	public int[] executeBatch() throws SQLException {
	    
	    long startTime = 0;
	    long endTime = 0;
	    String sqlID=null;
	    if( journalService != null ){
	        //SQL�P�ʂ̒ʔԂ��擾
	        sqlID = getSequenceNo();
            //���s����SQL���������Ă���
            final StringBuffer buff = new StringBuffer();
	        for( int i = 0 ; i < mBatchArray.size() ; i++ ){
	            final String sql = (String) mBatchArray.get(i); 
	            buff.append(sql);
	            buff.append(BATCH_SEPARATOR);
	        }
	        //�W���[�i���ɓo�^
	        journalService.addInfo(sqlID,buff.toString(),journalLevel);
	        //�o�b�`�o�^���e������
	        mBatchArray.clear();
	    }
	    int[] ret = null;
	    startTime = System.currentTimeMillis();
	    try {
	        ret = mStatement.executeBatch() ;
	    } finally {
            endTime = System.currentTimeMillis();
	        if( performanceService != null ) {
	            performanceService.entry(makeBatchAllSqlKey(mBatchArray),endTime-startTime);
	        }	
	        if( journalService != null ){
	            journalService.addInfo(sqlID+PF_KEY_HEADER,new Long(endTime-startTime)+PF_FOOTER,journalLevel);
	        }
	    }
		return ret;
	}
	
	protected String makeBatchAllSqlKey(ArrayList arr){
	    final StringBuffer buff = new StringBuffer();
	    for( int i= 0 ; i < arr.size() ; i++ ){
	        buff.append(arr.get(i));
	    }
	    return buff.toString();
	}

	/* (�� Javadoc)
	 * @see java.sql.Statement#executeUpdate(java.lang.String)
	 */
	public int executeUpdate(String arg0) throws SQLException {
	    long startTime = 0;
	    long endTime = 0;
	    String sqlID = null;
	    if( journalService != null ){
	        //SQL�����L�^
	        sqlID = getSequenceNo();
	        journalService.addInfo(sqlID,arg0,journalLevel);
	    }
	    startTime = System.currentTimeMillis();
	    try {
	        return mStatement.executeUpdate(arg0);
	    } finally {
	        endTime = System.currentTimeMillis();
	        if( performanceService != null ) {
	            performanceService.entry(arg0,endTime-startTime);
	        }	        	        
	        if( journalService != null ){
	            journalService.addInfo(sqlID+PF_KEY_HEADER,new Long(endTime-startTime)+PF_FOOTER,journalLevel);
	        }
	    }
	}

	/* (�� Javadoc)
	 * @see java.sql.Statement#addBatch(java.lang.String)
	 */
	public void addBatch(String arg0) throws SQLException {
	    if( journalService != null ){
	        mBatchArray.add(arg0);
	    }
		mStatement.addBatch(arg0) ;
	}
	/* (�� Javadoc)
	 * @see java.sql.Statement#execute(java.lang.String)
	 */
	public boolean execute(String arg0) throws SQLException {
	    long startTime = 0;
	    long endTime = 0;
	    String sqlID=null;
	    if( journalService != null ){
	        //SQL�����L�^
	        sqlID = getSequenceNo();
	        journalService.addInfo(sqlID,arg0,journalLevel);
	    }

        startTime = System.currentTimeMillis();
	    try {
	        return mStatement.execute(arg0);
	    } finally {
	        endTime = System.currentTimeMillis();
	        if( performanceService != null ) {
	            performanceService.entry(arg0,endTime-startTime);
	        }	        
	        if( journalService != null ){
	            journalService.addInfo(sqlID+PF_KEY_HEADER,new Long(endTime-startTime)+PF_FOOTER,journalLevel);
	        }
	    }
	}

	/* (�� Javadoc)
	 * @see java.sql.Statement#executeUpdate(java.lang.String, int)
	 */
	public int executeUpdate(String arg0, int arg1) throws SQLException {
	    long startTime = 0;
	    long endTime = 0;
	    String sqlID = null;
	    if( journalService != null ){
	        sqlID = getSequenceNo();
	        journalService.addInfo(sqlID,arg0,journalLevel);
	    }
	    startTime = System.currentTimeMillis();
	    try {
	        return mStatement.executeUpdate(arg0,arg1);
	    } finally {
	        endTime = System.currentTimeMillis();
	        if( performanceService != null ) {
	            performanceService.entry(arg0,endTime-startTime);
	        }	        	        
	        if( journalService != null ){
	            journalService.addInfo(sqlID+PF_KEY_HEADER,new Long(endTime-startTime)+PF_FOOTER,journalLevel);
	        }
	    }
	}

	/* (�� Javadoc)
	 * @see java.sql.Statement#execute(java.lang.String, int)
	 */
	public boolean execute(String arg0, int arg1) throws SQLException {
	    
	    long startTime = 0;
	    long endTime = 0;
	    String sqlID = null;
	    if( journalService != null ){
	        //SQL�����L�^
	        sqlID = getSequenceNo();
	        journalService.addInfo(sqlID,arg0,journalLevel);
	    }
        if( performanceService != null ) startTime = System.currentTimeMillis();
	    try {
			return mStatement.execute(arg0,arg1);
	    }finally {
	        endTime = System.currentTimeMillis();
	        if( performanceService != null ) {
	            performanceService.entry(arg0,endTime-startTime);
	        }	        	        
	        if( journalService != null ){
	            journalService.addInfo(sqlID+PF_KEY_HEADER,new Long(endTime-startTime)+PF_FOOTER,journalLevel);
	        }
	    }
	}

	/* (�� Javadoc)
	 * @see java.sql.Statement#executeUpdate(java.lang.String, int[])
	 */
	public int executeUpdate(String arg0, int[] arg1) throws SQLException {
	    long startTime = 0;
	    long endTime = 0;
	    String sqlID = null;
	    if( journalService != null ){
	        //SQL�����L�^
	        sqlID = getSequenceNo();
	        journalService.addInfo(sqlID,arg0,journalLevel);
	    }
        startTime = System.currentTimeMillis();
	    try {
	        return mStatement.executeUpdate(arg0,arg1);
	    } finally {
	        endTime = System.currentTimeMillis();
	        if( performanceService != null ) {
	            performanceService.entry(arg0,endTime-startTime);
	        }	        	        
	        if( journalService != null ){
	            journalService.addInfo(sqlID+PF_KEY_HEADER,new Long(endTime-startTime)+PF_FOOTER,journalLevel);
	        }
	    }
	}

	/* (�� Javadoc)
	 * @see java.sql.Statement#execute(java.lang.String, int[])
	 */
	public boolean execute(String arg0, int[] arg1) throws SQLException {
	    long startTime = 0;
	    long endTime = 0;
	    String sqlID = null;
	    if( journalService != null ){
		    sqlID = getSequenceNo();
	        //SQL�����L�^
	        journalService.addInfo(sqlID,arg0,journalLevel);
	    }
	    startTime = System.currentTimeMillis();
	    try {
	        return this.mStatement.execute(arg0,arg1);
	    } finally {
		    endTime = System.currentTimeMillis();
		    if( performanceService != null ){
		        	performanceService.entry(arg0,endTime - startTime);
		    }
	        if( journalService != null ){
	            journalService.addInfo(sqlID+PF_KEY_HEADER,new Long(endTime-startTime)+PF_FOOTER,journalLevel);
	        }
		    
	    }
	}

	/* (�� Javadoc)
	 * @see java.sql.Statement#executeUpdate(java.lang.String, java.lang.String[])
	 */
	public int executeUpdate(String arg0, String[] arg1) throws SQLException {
	    long startTime = 0;
	    long endTime = 0;
	    String sqlID = null;
	    if( journalService != null ){
	        //SQL�����L�^
	        sqlID = getSequenceNo();
	        journalService.addInfo(sqlID,arg0,journalLevel);
	    }
        startTime = System.currentTimeMillis();
	    try {
	        return mStatement.executeUpdate(arg0,arg1);
	    } finally {
	        endTime = System.currentTimeMillis();
	        if( performanceService != null ) {
	            performanceService.entry(arg0,endTime-startTime);
	        }	        	        
	        if( journalService != null ){
	            journalService.addInfo(sqlID+PF_KEY_HEADER,new Long(endTime-startTime)+PF_FOOTER,journalLevel);
	        }
	    }
	}

	/* (�� Javadoc)
	 * @see java.sql.Statement#execute(java.lang.String, java.lang.String[])
	 */
	public boolean execute(String arg0, String[] arg1) throws SQLException {
	    long startTime = 0;
	    long endTime = 0;
	    String sqlID = null;
		if( journalService != null ){
		    sqlID = getSequenceNo();
	        //SQL�����L�^
	        journalService.addInfo(sqlID,arg0,journalLevel);
	    }
	    startTime = System.currentTimeMillis();
	    try {
	        return this.mStatement.execute(arg0,arg1);
	    }finally{
	        endTime = System.currentTimeMillis();
	        if( performanceService != null ) {
	            performanceService.entry(arg0,endTime-startTime);
	        }	        
	        if( journalService != null ){
	            journalService.addInfo(sqlID+PF_KEY_HEADER,new Long(endTime-startTime)+PF_FOOTER,journalLevel);
	        }
		}
	}

	/* (�� Javadoc)
	 * @see java.sql.Statement#executeQuery(java.lang.String)
	 */
	public ResultSet executeQuery(String arg0) throws SQLException {
	    long startTime = 0;
	    long endTime = 0;
	    String sqlID = null;
	    if( journalService != null ){
	        sqlID = getSequenceNo();
	        journalService.addInfo(sqlID,arg0,journalLevel);
	    }
		// ���N�G�X�g�J�n�����擾
		startTime = System.currentTimeMillis();
	    NimbusResultSet nret=null;
	    try {
	        ResultSet ret =  mStatement.executeQuery(arg0);
	        nret = new NimbusResultSet(ret);
		    //ResultSet���Ǘ�
		    mResultSetList.add(nret);
	    } finally {
			endTime = System.currentTimeMillis();
		    if( performanceService != null ){
				performanceService.entry(arg0,endTime-startTime);
		    }
	        if( journalService != null ){
	            journalService.addInfo(sqlID+PF_KEY_HEADER,new Long(endTime-startTime)+PF_FOOTER,journalLevel);
	        }
	    }
	    return nret;
	} 
    

    public boolean isPoolable() throws SQLException{
        return mStatement.isPoolable();
    }
    
    public void setPoolable(boolean isPoolable) throws SQLException{
        mStatement.setPoolable(isPoolable);
    }
    
    public boolean isClosed() throws SQLException{
        return mStatement.isClosed();
    }
    
    public boolean isWrapperFor(Class<?> iface) throws SQLException{
        return mStatement.isWrapperFor(iface);
    }
    
    public <T> T unwrap(Class<T> iface) throws SQLException{
        return mStatement.unwrap(iface);
    }


    
    public void closeOnCompletion() throws SQLException{
        mStatement.closeOnCompletion();
    }
    
    public boolean isCloseOnCompletion() throws SQLException{
        return mStatement.isCloseOnCompletion();
    }

}
