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
package jp.ossc.nimbus.service.resource.datasource;

import jp.ossc.nimbus.service.resource.TransactionResource;
import java.sql.*;
// インポート
//
/**
 *  
 * @author   nakano
 * @version  1.00 作成: 2003/11/30 -　H.Nakano
 */
public class JdbcConnectionTransactionResource
	implements TransactionResource {
	private Connection mCon = null ; 	
	public	JdbcConnectionTransactionResource(Connection con){
		this.mCon = con ;
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.resource.TransactionResource#commit()
	 */
	public void commit() throws Exception {
		if(this.mCon.isClosed() == false){
			if(this.mCon.getAutoCommit() == false){
				this.mCon.commit();
			}
		}
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.resource.TransactionResource#rollback()
	 */
	public void rollback() throws Exception {
		if(this.mCon.isClosed() == false){
			this.mCon.rollback() ;
		}
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.resource.TransactionResource#close()
	 */
	public void close() throws Exception {
		if(this.mCon.isClosed() == false)
		{
			if(mCon instanceof NimbusJdbcConnection){
				final NimbusJdbcConnection tmp =(NimbusJdbcConnection)mCon;		
				tmp.trueClose();
			}else{
				this.mCon.close() ;
			}
		}
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.resource.TransactionResource#getObject()
	 */
	public Object getObject() {
		return this.mCon;
	}
}
