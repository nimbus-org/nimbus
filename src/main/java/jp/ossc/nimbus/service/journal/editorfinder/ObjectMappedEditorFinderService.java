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
package jp.ossc.nimbus.service.journal.editorfinder;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Properties;
import java.lang.reflect.Array;

import jp.ossc.nimbus.beans.ServiceNameEditor;
import jp.ossc.nimbus.core.NimbusClassLoader;
import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.service.journal.JournalEditor;
import jp.ossc.nimbus.util.ClassMappingTree;

/**
 * エディターファインダーサービスの実装クラス 
 * @author   nakano
 * @version  1.00 作成: 2003/11/07 -　H.Nakano
 */
public class ObjectMappedEditorFinderService
	extends ServiceBase
	implements EditorFinder, ObjectMappedEditorFinderServiceMBean, java.io.Serializable {
	
    private static final long serialVersionUID = 1524875427185813794L;
    
    private static final String ARRAY_CLASS_SUFFIX = "[]";
	
	/** 上位リポジトリ */
	private EditorFinder mParentFinder  ;
	/** 上位リポジトリサービス名  */
	private ServiceName mFinderServiceName ;
	/** エディタメンバー指定プロパティ */
	private Properties mEditorMap;
	/** エディタツリー管理 */
	private ClassMappingTree mEditorRepository ;
    
	/**
	 * ObjectMappedEditorFinderService
	 */
	public ObjectMappedEditorFinderService(){
		super() ;
		mParentFinder = null ;
		mFinderServiceName = null ;
		mParentFinder = null ;
		mEditorRepository = new ClassMappingTree() ;
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.core.ServiceBaseSupport#startService()
	 */
	public void startService() throws Exception{
		//上位リポジトリの取得
		if(mFinderServiceName != null){
			this.mParentFinder = (EditorFinder)ServiceManagerFactory.getServiceObject(this.mFinderServiceName) ;
		}
		ServiceNameEditor editor = new ServiceNameEditor() ;
		editor.setServiceManagerName(getServiceManagerName());
		//自分が管理するエディターサービスの設定
        Iterator iterator = mEditorMap.keySet().iterator();
		while(iterator.hasNext()) {
		    String classAndKey = (String)iterator.next();
			String key = null;
			String clsName = null;
			final int index = classAndKey.indexOf(',');
		    if(index == -1){
		        clsName = classAndKey;
		    }else{
		        clsName = classAndKey.substring(0, index);
		        if(index != classAndKey.length() - 1){
		            key = classAndKey.substring(index + 1);
		        }
		    }
			
			Class cls = convertStringToClass(clsName);
            
            JournalEditor journalEditor = null;
            Object value = mEditorMap.get(classAndKey);
            if(value instanceof String) {
                String serviceName = (String)value;
                editor.setAsText(serviceName);
                ServiceName name = (ServiceName)editor.getValue();
                journalEditor = (JournalEditor)ServiceManagerFactory.getServiceObject(name);
            }else if(value instanceof JournalEditor) {
                journalEditor = (JournalEditor)value;
            }
            Map keyEditorMap = (Map)mEditorRepository.getValueOf(cls);
            if(keyEditorMap == null){
                keyEditorMap = new HashMap();
                mEditorRepository.add(cls, keyEditorMap);
            }
            keyEditorMap.put(key, journalEditor);
		}
		
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.core.ServiceBaseSupport#stopService()
	 */
	public void stopService() throws Exception{
		mEditorRepository.clear();
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.core.ServiceBaseSupport#destroyService()
	 */
	public void destroyService() throws Exception{
		mParentFinder = null ;
		mFinderServiceName = null ;
		mParentFinder = null ;
		mEditorRepository = null ;
	}
	
    private Class convertStringToClass(String typeStr)
     throws ClassNotFoundException{
        Class type = null;
        if(typeStr != null){
            if(Byte.TYPE.getName().equals(typeStr)){
                type = Byte.TYPE;
            }else if(Character.TYPE.getName().equals(typeStr)){
                type = Character.TYPE;
            }else if(Short.TYPE.getName().equals(typeStr)){
                type = Short.TYPE;
            }else if(Integer.TYPE.getName().equals(typeStr)){
                type = Integer.TYPE;
            }else if(Long.TYPE.getName().equals(typeStr)){
                type = Long.TYPE;
            }else if(Float.TYPE.getName().equals(typeStr)){
                type = Float.TYPE;
            }else if(Double.TYPE.getName().equals(typeStr)){
                type = Double.TYPE;
            }else if(Boolean.TYPE.getName().equals(typeStr)){
                type = Boolean.TYPE;
            }else{
                if(typeStr.endsWith(ARRAY_CLASS_SUFFIX)
                    && typeStr.length() > 2){
                    final Class elementType = convertStringToClass(
                        typeStr.substring(0, typeStr.length() - 2)
                    );
                    type = Array.newInstance(elementType, 0).getClass();
                }else{
                    type = Class.forName(
                        typeStr,
                        true,
                        NimbusClassLoader.getInstance()
                    );
                }
            }
        }
        return type;
    }
	
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.journal.editorfinder.EditorFinder#findEditor(java.lang.Class)
	 */
	public JournalEditor findEditor(Class paramClass) {
		return this.findEditor(null, paramClass) ;
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.journal.editorfinder.EditorFinder#findEditor(java.lang.Object)
	 */
	public JournalEditor findEditor(Object paramObj){
		return this.findEditor(null, paramObj) ;
	}
	
	public JournalEditor findEditor(Object key, Class paramClass) {
		Map keyEditorMap = (Map)mEditorRepository.getValue(paramClass);
		if(keyEditorMap == null){
		    return mParentFinder != null ? mParentFinder.findEditor(key, paramClass) : null;
		}
		JournalEditor ret = (JournalEditor)keyEditorMap.get(key);
		if(ret == null && key != null){
		    ret = (JournalEditor)keyEditorMap.get(null);
		}
		if(ret==null && mParentFinder != null){
			ret = mParentFinder.findEditor(key, paramClass);
		}
		return ret;
	}
	
	public JournalEditor findEditor(Object key, Object paramObj){
		Class cls = paramObj != null ? paramObj.getClass() : null;
		return this.findEditor(key, cls) ;
	}

	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.journal.editorfinder.ObjectMappedEditorFinderServiceMBean#setEditorProperties(java.util.Properties)
	 */
	public void setEditorProperties(Properties prop) {
		this.mEditorMap = prop ;
	}
	public Properties getEditorProperties(){
		return this.mEditorMap;
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.journal.editorfinder.ObjectMappedEditorFinderServiceMBean#setParentEditorfinderServiceName(jp.ossc.nimbus.core.ServiceName)
	 */
	public void setParentEditorfinderServiceName(ServiceName name){
		this.mFinderServiceName = name ;
	}
	public ServiceName getParentEditorfinderServiceName(){
		return this.mFinderServiceName;
	}
    
    /**
     * EditorFinderを設定する。
     */
    public void setEditorFinder(EditorFinder parentFinder) {
        mParentFinder = parentFinder;
    }
 
}
