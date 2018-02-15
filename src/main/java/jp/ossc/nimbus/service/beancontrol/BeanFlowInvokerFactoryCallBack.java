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
// �p�b�P�[�W
package jp.ossc.nimbus.service.beancontrol;
// �C���|�[�g
import java.beans.PropertyEditor;

import jp.ossc.nimbus.service.beancontrol.resource.*;
import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.log.*;
import jp.ossc.nimbus.service.context.*;
import jp.ossc.nimbus.service.journal.*;
import jp.ossc.nimbus.service.journal.editorfinder.*;
import jp.ossc.nimbus.service.beancontrol.interfaces.BeanFlowInvoker;
import jp.ossc.nimbus.service.interpreter.Interpreter;
import jp.ossc.nimbus.service.transaction.TransactionManagerFactory;
import jp.ossc.nimbus.service.queue.QueueHandlerContainer;
import jp.ossc.nimbus.service.template.TemplateEngine;

/**
 * BeanFlowInvoker���g�p����t�@�N�g���[�ւ̃R�[���o�b�N�@�\�̃C���^�[�t�F�C�X
 * @version $Name:  $
 * @author H.Nakano
 * @since 1.0
 * @see jp.ossc.nimbus.service.beancontrol.resource.ResourceManager
 */
public interface BeanFlowInvokerFactoryCallBack {
	/**
	 * ���\�[�X�}�l�[�W�����쐬����
	 * @return	ResourceManager
	 */
	public ResourceManager createResourceManager() ;
	/**
	 * ���K�[���o�͂���B
	 * @return	Logger	
	 */
	public Logger getLogger() ;
	/**
	 * ���s�I���o�^
	 * @param monitor
	 */
	public void removeExecFlow(BeanFlowMonitor monitor);
	/**
	 * ���s����t���[��o�^����
	 * @param monitor
	 */
	public void addExcecFlow(BeanFlowMonitor monitor) ;	
	/**
	 * �t���[���̃X���b�h�R���e�L�X�g�T�[�r�X���o�͂���
	 * @return Context
	 */
	public Context getThreadContext() ;
	/**
	 * �W���[�T���T�[�r�X���o�͂���B
	 * @return	Journal
	 * @see	jp.ossc.nimbus.service.journal.Journal
	 */
	public Journal getJournal(BeanFlowInvokerAccess invoker) ;
	public EditorFinder getEditorFinder();
	/**
	 * property�G�f�B�^����������
	 * @param cls
	 * @return PropertyEditor
	 */
	public PropertyEditor findPropEditor(Class cls);

	public boolean isManageExecBeanFlow();
	
	public BeanFlowInvoker createFlow(String key) ;
    public BeanFlowInvoker createFlow(String key, String caller, boolean isOverwride);
    public boolean containsFlow(String key);

	public ServiceLoader getServiceLoader();
    public ServiceManager getServiceManager();
    
    public Interpreter getInterpreter();
    public Interpreter getTestInterpreter();
    public Interpreter getExpressionInterpreter();
    
    public TransactionManagerFactory getTransactionManagerFactory();
    public TemplateEngine getTemplateEngine();
    
    public QueueHandlerContainer getAsynchInvokeQueueHandlerContainer();
    public String replaceProperty(String textValue);
}
