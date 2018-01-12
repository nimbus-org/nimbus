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
package jp.ossc.nimbus.service.beancontrol.resource;
//インポート
import java.util.*;
import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.service.resource.*;
import jp.ossc.nimbus.service.beancontrol.interfaces.*;
//
/**
 * リソースマネージャ実装クラス 
 * @author   nakano
 * @version  1.00 作成: 2003/11/29 -　H.Nakano
 */
public class ResourceManagerImpl
	extends ServiceBase
	implements ResourceManager {
	
    private static final long serialVersionUID = 1692609765851402548L;
    
    /** リソース管理Hash */
	private HashMap mResourceMap= null ;
	/**
	 * コンストラクタ
	 */
	public  ResourceManagerImpl(){
		mResourceMap = new HashMap() ;
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.beancontrol.resource.ResourceManager#terminateResourceManager()
	 */
	public void terminateResourceManager() {
		//コントロールが必要か判定する
		HashMap map = mResourceMap ;
		boolean bControl = false ;
		for(Iterator iterator = map.keySet().iterator();iterator.hasNext();){
			String key =(String)iterator.next() ;
			ResourceRecord rec = (ResourceRecord)map.get(key) ;
			bControl = rec.isTransactionControl() ;
			if(bControl){
				try{
					rec.getTransanctionResource().rollback();
				}catch(Exception e){
				}
			}
			try {
				rec.getTransanctionResource().close();
			} catch (Exception e) {
			}
		}
		map.clear() ;
		mResourceMap=null ;
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.beanflow.resource.ResourceManager#addResource(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public void addResource(
		String key,
		String resourceKey,
		ServiceName serviceName,
		boolean isTranControl,
		boolean isTranClose) {
		HashMap map = this.mResourceMap ;
/*		ResourceFactory rf = 
			(ResourceFactory)ServiceManagerFactory.getServiceObject(serviceName) ;
		TransactionResource rc;
		try {
			rc = rf.makeResource(resourceKey);
		} catch (Exception e) {
			throw new BeanControlUncheckedException("makeResource exception",e) ;
		}
*/		
		ResourceRecord rec = new ResourceRecord() ;
//		rec.setTransanctionResource(rc) ;
		rec.setServiceName(serviceName) ;
		rec.setResourceKey(resourceKey) ;
		rec.setTransactionControl(isTranControl) ;
		rec.setTransactionClose(isTranClose);
		map.put(key,rec) ;
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.beancontrol.resource.ResourceManager#commitResource(java.lang.String, boolean)
	 */
	public void commitResource(String key, boolean isClose) {
		//コントロールが必要か判定する
		boolean bControl =  false ;
		if(bControl==false && isClose== false){
			return ;
		}
		//Hashからリソースを取り出す
		HashMap map = (HashMap)this.mResourceMap ;
		ResourceRecord rec = (ResourceRecord)map.get(key) ;
		if(rec != null){
			//使用されているか
			if(rec.isGet()){
				bControl = rec.isTransactionControl() ;
				if(bControl){
					try{
						rec.getTransanctionResource().commit() ;
					}catch(Exception e){
						throw new BeanControlUncheckedException("resource commit error",e) ; //$NON-NLS-1$ //$NON-NLS-2$
					}
				}
				if(isClose){
					try {
						rec.getTransanctionResource().close();
					} catch (Exception e) {
						throw new BeanControlUncheckedException("resource close error",e) ;  //$NON-NLS-1$//$NON-NLS-2$
					}
					map.remove(key);
				}
			}
		}
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.beancontrol.resource.ResourceManager#rollBackResource(java.lang.String, boolean)
	 */
	public void rollBackResource(String key, boolean isClose) {
		//コントロールが必要か判定する
		boolean bControl =  false ;
		if(bControl==false && isClose== false){
			return ;
		}
		//Hashからリソースを取り出す
		HashMap map = this.mResourceMap ;
		ResourceRecord rec = (ResourceRecord)map.get(key) ;
		if(rec != null){
			//使用されているか
			if(rec.isGet()){
				bControl = rec.isTransactionControl() ;
				if(bControl){
					try{
						rec.getTransanctionResource().rollback() ;
					}catch(Exception e){
						throw new BeanControlUncheckedException("resource rollback error",e) ;  //$NON-NLS-1$//$NON-NLS-2$
					}
				}
				if(isClose){
					try {
						rec.getTransanctionResource().close();
					} catch (Exception e) {
						throw new BeanControlUncheckedException("resource close error",e) ;  //$NON-NLS-1$//$NON-NLS-2$
					}
					rec.setGet(false) ;
					map.remove(key);
				}
			}
		}
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.beancontrol.resource.ResourceManager#commitAllResources()
	 */
	public void commitAllResources() {
		//コントロールが必要か判定する
		HashMap map = this.mResourceMap ;
		boolean bControl = false ;
		boolean bClose = false;
		for (Iterator iterator = map.keySet().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			ResourceRecord rec = (ResourceRecord) map.get(key);
			bControl = rec.isTransactionControl();
			if (bControl) {
				if (rec.isGet()) {
					try {
						rec.getTransanctionResource().commit();
					} catch (Exception e) {
						throw new BeanControlUncheckedException("resource commit error", e); //$NON-NLS-1$//$NON-NLS-2$
					}
				}
			}
			bClose = rec.isTransactionClose();
			if (bClose && rec.isGet()) {
				try {
					rec.getTransanctionResource().close();
				} catch (Exception e) {
					throw new BeanControlUncheckedException("resource close error", e); //$NON-NLS-1$//$NON-NLS-2$
				}
				rec.setGet(false);
			}
		}
		map.clear();
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.beancontrol.resource.ResourceManager#rollbbackAllResources()
	 */
	public void rollbbackAllResources() {
		//コントロールが必要か判定する
		HashMap map = this.mResourceMap ;
		boolean bControl =  false ;
		boolean bClose = false;
		for(Iterator iterator = map.keySet().iterator();iterator.hasNext();){
			String key =(String)iterator.next() ;
			ResourceRecord rec = (ResourceRecord)map.get(key) ;
			bControl = rec.isTransactionControl() ;
			if(bControl){
				if(rec.isGet()){
					try{
						rec.getTransanctionResource().rollback();
					}catch(Exception e){
						throw new BeanControlUncheckedException("resource rollback error",e) ;  //$NON-NLS-1$//$NON-NLS-2$
					}
				}
			}
			bClose = rec.isTransactionClose();
			if(bClose && rec.isGet()){
				try {
					rec.getTransanctionResource().close();
				} catch (Exception e) {
					throw new BeanControlUncheckedException("resource close error",e) ;  //$NON-NLS-1$//$NON-NLS-2$
				}
				rec.setGet(false) ;
			}
		}
		map.clear();
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.beancontrol.resource.ResourceManager#getResource(java.lang.String)
	 */
	public Object getResource(String key) {
		Object ret = null ;
		HashMap map = this.mResourceMap ;
		ResourceRecord rec =(ResourceRecord)map.get(key) ;
		if(rec!=null){
			ret = rec.getTransanctionResource();
			rec.setGet(true) ;
		}
		return ret ;
	}
	//
	/**
	 * リソース管理レコード 
	 * @author   nakano
	 * @version  1.00 作成: 2003/11/29 -　H.Nakano
	 */
	private class ResourceRecord {
		private TransactionResource mTransanctionResource = null;
		private boolean mIsGet = false ;
		private boolean mIsTransactionControl = false ;
		private boolean mIsTransactionClose = true;
		private String mResourceKey= null ;
		private ServiceName mServiceName = null ;
		/**
		 * サービス名を設定する
		 * @param name
		 */
		public void setServiceName(ServiceName name){
			mServiceName = name ;
		}
		/**
		 * リソースキーを設定する
		 * @param key
		 */
		public void setResourceKey(String key) {
			mResourceKey = key ;
		}
		/**
		 * リソースを取得されたか
		 * @return
		 */
		public boolean isGet() {
			return mIsGet;
		}
		/**
		 * getTransanctionResource
		 * @return
		 */
		public TransactionResource getTransanctionResource() {
			if(this.isGet()){
				return mTransanctionResource;
			}else{
				ResourceFactory rf = 
				(ResourceFactory)ServiceManagerFactory.getServiceObject(this.mServiceName) ;
				TransactionResource rc=null;
				try {
					rc = rf.makeResource(this.mResourceKey);
				} catch (Exception e) {
					throw new BeanControlUncheckedException("makeResource exception",e) ;
				}
				this.setTransanctionResource(rc) ;
				return rc;
			}
		}

		/**
		 * setGet
		 * @param b
		 */
		public void setGet(boolean b) {
			mIsGet = b;
		}

		/**
		 * setTransanctionResource
		 * @param resource
		 */
		public void setTransanctionResource(TransactionResource resource) {
			mTransanctionResource = resource;
		}

		/**
		 * 指定ファイルにコピーする。
		 */
		public boolean isTransactionControl() {
			return mIsTransactionControl;
		}
		
		/**
		 * @return
		 */
		public boolean isTransactionClose(){
			return mIsTransactionClose;
		}

		/**
		 * トランザクションをコントロールするか否かを設定する。
		 */
		public void setTransactionControl(boolean b) {
			mIsTransactionControl = b;
		}
		/**
		 * コネクションをクローズするか否かを設定する。
		 */
		public void setTransactionClose(boolean b){
			mIsTransactionClose = b;
		}

	}
}
