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
package jp.ossc.nimbus.service.beancontrol;

import jp.ossc.nimbus.core.*;
import java.util.*;

/**
 * {@link DefaultBeanFlowInvokerFactoryService}��MBean�C���^�t�F�[�X�B<p>
 * 
 * @author H.Nakano
 */
public interface DefaultBeanFlowInvokerFactoryServiceMBean
 extends ServiceBaseMBean{
    
    /** �X�V�����t�H�[�}�b�g������ */
    public static final String TIME_FORMAT = "yyyy.MM.dd hh:mm:ss";
    
    /**
     * {@link jp.ossc.nimbus.service.beancontrol.resource.ResourceManagerFactory ResourceManagerFactory}�T�[�r�X�̃T�[�r�X����ݒ肷��B<p>
     *
     * @param name ResourceManagerFactory�T�[�r�X�̃T�[�r�X��
     */
    public void setResourceManagerFactoryServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.beancontrol.resource.ResourceManagerFactory ResourceManagerFactory}�T�[�r�X�̃T�[�r�X�����擾����B<p>
     *
     * @return ResourceManagerFactory�T�[�r�X�̃T�[�r�X��
     */
    public ServiceName getResourceManagerFactoryServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.context.Context Context}�T�[�r�X�̃T�[�r�X����ݒ肷��B<p>
     * 
     * @param name Context�T�[�r�X�̃T�[�r�X��
     */
    public void setThreadContextServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.context.Context Context}�T�[�r�X�̃T�[�r�X�����擾����B<p>
     * 
     * @return Context�T�[�r�X�̃T�[�r�X��
     */
    public ServiceName getThreadContextServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.log.Logger Logger}�T�[�r�X�̃T�[�r�X����ݒ肷��B<p>
     * 
     * @param name Logger�T�[�r�X�̃T�[�r�X��
     */
    public void setLogServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.log.Logger Logger}�T�[�r�X�̃T�[�r�X�����擾����B<p>
     * 
     * @return Logger�T�[�r�X�̃T�[�r�X��
     */
    public ServiceName getLogServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.journal.Journal Journal}�T�[�r�X�̃T�[�r�X����ݒ肷��B<p>
     *
     * @param name Journal�T�[�r�X�̃T�[�r�X��
     */
    public void setJournalServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.journal.Journal Journal}�T�[�r�X�̃T�[�r�X�����擾����B<p>
     *
     * @return Journal�T�[�r�X�̃T�[�r�X��
     */
    public ServiceName getJournalServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}�T�[�r�X�̃T�[�r�X����ݒ肷��B<p>
     *
     * @param name EditorFinder�T�[�r�X�̃T�[�r�X��
     */
    public void setEditorFinderServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}�T�[�r�X�̃T�[�r�X�����擾����B<p>
     *
     * @return EditorFinder�T�[�r�X�̃T�[�r�X��
     */
    public ServiceName getEditorFinderServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.interpreter.Interpreter Interpreter}�T�[�r�X�̃T�[�r�X����ݒ肷��B<p>
     * 
     * @param name Interpreter�T�[�r�X�̃T�[�r�X��
     */
    public void setInterpreterServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.interpreter.Interpreter Interpreter}�T�[�r�X�̃T�[�r�X�����擾����B<p>
     * 
     * @return Interpreter�T�[�r�X�̃T�[�r�X��
     */
    public ServiceName getInterpreterServiceName();
    
    /**
     * test�����]���p��{@link jp.ossc.nimbus.service.interpreter.Interpreter Interpreter}�T�[�r�X�̃T�[�r�X����ݒ肷��B<p>
     * 
     * @param name Interpreter�T�[�r�X�̃T�[�r�X��
     */
    public void setTestInterpreterServiceName(ServiceName name);
    
    /**
     * test�����]���p��{@link jp.ossc.nimbus.service.interpreter.Interpreter Interpreter}�T�[�r�X�̃T�[�r�X�����擾����B<p>
     * 
     * @return Interpreter�T�[�r�X�̃T�[�r�X��
     */
    public ServiceName getTestInterpreterServiceName();
    
    /**
     * expression�v�f�]���p��{@link jp.ossc.nimbus.service.interpreter.Interpreter Interpreter}�T�[�r�X�̃T�[�r�X����ݒ肷��B<p>
     * 
     * @param name Interpreter�T�[�r�X�̃T�[�r�X��
     */
    public void setExpressionInterpreterServiceName(ServiceName name);
    
    /**
     * expression�v�f�]���p��{@link jp.ossc.nimbus.service.interpreter.Interpreter Interpreter}�T�[�r�X�̃T�[�r�X�����擾����B<p>
     * 
     * @return Interpreter�T�[�r�X�̃T�[�r�X��
     */
    public ServiceName getExpressionInterpreterServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.template.TemplateEngine TemplateEngine}�T�[�r�X�̃T�[�r�X����ݒ肷��B<p>
     * 
     * @param name TemplateEngine�T�[�r�X�̃T�[�r�X��
     */
    public void setTemplateEngineServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.template.TemplateEngine TemplateEngine}�T�[�r�X�̃T�[�r�X�����擾����B<p>
     * 
     * @return TemplateEngine�T�[�r�X�̃T�[�r�X��
     */
    public ServiceName getTemplateEngineServiceName();
    
    /**
     * ���s����BeanFlow���Ǘ����邩�ǂ�����ݒ肷��B<p>
     * �f�t�H���g�́Atrue�B<br>
     *
     * @param isManage �Ǘ�����ꍇ�́Atrue
     */
    public void setManageExecBeanFlow(boolean isManage);
    
    /**
     * ���s����BeanFlow���Ǘ����邩�ǂ����𔻒肷��B<p>
     *
     * @return true�̏ꍇ�A�Ǘ�����
     */
    public boolean isManageExecBeanFlow();
    
    /**
     * {@link BeanFlowInvokerAccess}�C���^�t�F�[�X�̎����N���X��ݒ肷��B<p>
     * �f�t�H���g�́A{@link BeanFlowInvokerAccessImpl}�B<br>
     *
     * @param clazz BeanFlowInvokerAccess�C���^�t�F�[�X�̎����N���X
     */
    public void setBeanFlowInvokerAccessClass(Class clazz);
    
    /**
     * {@link BeanFlowInvokerAccess}�C���^�t�F�[�X�̎����N���X���擾����B<p>
     *
     * @return BeanFlowInvokerAccess�C���^�t�F�[�X�̎����N���X
     */
    public Class getBeanFlowInvokerAccessClass();
    
    /**
     * �t���[��`XML���ADTD�Ō��؂��邩�ǂ�����ݒ肷��B<p>
     * �f�t�H���g�́Afalse�B<br>
     *
     * @param validate ���؂���ꍇ�Atrue
     */
    public void setValidate(boolean validate);
    
    /**
     * �t���[��`XML���ADTD�Ō��؂��邩�ǂ����𔻒肷��B<p>
     *
     * @return true�̏ꍇ�A���؂���
     */
    public boolean isValidate();
    
    /**
     * {@link jp.ossc.nimbus.service.aop.InterceptorChainFactory InterceptorChainFactory}�T�[�r�X�̃T�[�r�X����ݒ肷��B<p>
     *
     * @param name InterceptorChainFactory�T�[�r�X�̃T�[�r�X��
     */
    public void setInterceptorChainFactoryServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.aop.InterceptorChainFactory InterceptorChainFactory}�T�[�r�X�̃T�[�r�X�����擾����B<p>
     *
     * @return InterceptorChainFactory�T�[�r�X�̃T�[�r�X��
     */
    public ServiceName getInterceptorChainFactoryServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.transaction.TransactionManagerFactory TransactionManagerFactory}�T�[�r�X�̃T�[�r�X����ݒ肷��B<p>
     * �ݒ肵�Ȃ��ꍇ�́A{@link jp.ossc.nimbus.service.transaction.JndiTransactionManagerFactoryService JndiTransactionManagerFactoryService}���K�p�����B<br>
     *
     * @param name TransactionManagerFactory�T�[�r�X�̃T�[�r�X��
     */
    public void setTransactionManagerFactoryServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.transaction.TransactionManagerFactory TransactionManagerFactory}�T�[�r�X�̃T�[�r�X�����擾����B<p>
     *
     * @return TransactionManagerFactory�T�[�r�X�̃T�[�r�X��
     */
    public ServiceName getTransactionManagerFactoryServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.queue.QueueHandlerContainer QueueHandlerContainer}�T�[�r�X�̃T�[�r�X����ݒ肷��B<p>
     * �ݒ肵�Ȃ��ꍇ�́A{@link jp.ossc.nimbus.service.beancontrol.interfaces.BeanFlowInvoker#invokeAsynchFlow(Object, jp.ossc.nimbus.service.beancontrol.BeanFlowMonitor, boolean, int)}���T�|�[�g���Ȃ��B<br>
     *
     * @param name QueueHandlerContainer�T�[�r�X�̃T�[�r�X��
     */
    public void setAsynchInvokeQueueHandlerContainerServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.queue.QueueHandlerContainer QueueHandlerContainer}�T�[�r�X�̃T�[�r�X�����擾����B<p>
     *
     * @return QueueHandlerContainer�T�[�r�X�̃T�[�r�X��
     */
    public ServiceName getAsynchInvokeQueueHandlerContainerServiceName();
    
    /**
     * �t���[��`XML�̑��݂���f�B���N�g����ݒ肷��B<p>
     * 
     * @param dirPaths �t���[��`XML�̑��݂���f�B���N�g���p�X�z��
     */
    public void setDirPaths(String dirPaths[]);
    
    /**
     * �t���[��`�t�@�C���̑��݂���f�B���N�g�����擾����B<p>
     * 
     * @return �t���[��`�t�@�C���̃f�B���N�g���p�X�z��
     */
    public String[] getDirPaths();
    
    /**
     * �t���[��`XML�̃p�X��ݒ肷��B<p>
     * 
     * @param paths �t���[��`XML�̑��݂���p�X�z��
     */
    public void setPaths(String paths[]);
    
    /**
     * �t���[��`�t�@�C���̃p�X���擾����B<p>
     * 
     * @return �t���[��`�t�@�C���̃p�X�z��
     */
    public String[] getPaths();
    
    /**
     * �n���h�����O���ɃG���[�����������ꍇ�ɏo�͂��郍�O�̃��b�Z�[�WID��ݒ肷��B<p>
     * �f�t�H���g�́Anull�ŁA���O���o�͂��Ȃ��B<br>
     *
     * @param id ���O�̃��b�Z�[�WID
     */
    public void setAsynchInvokeErrorLogMessageId(String id);
    
    /**
     * �n���h�����O���ɃG���[�����������ꍇ�ɏo�͂��郍�O�̃��b�Z�[�WID���擾����B<p>
     * 
     * @return ���O�̃��b�Z�[�WID
     */
    public String getAsynchInvokeErrorLogMessageId();
    
    /**
     * �n���h�����O���ɃG���[���������A�K��̃��g���C�񐔂��z�����ꍇ�ɏo�͂��郍�O�̃��b�Z�[�WID��ݒ肷��B<p>
     * �f�t�H���g�́Anull�ŁA���O���o�͂��Ȃ��B<br>
     *
     * @param id ���O�̃��b�Z�[�WID
     */
    public void setAsynchInvokeRetryOverErrorLogMessageId(String id);
    
    /**
     * �n���h�����O���ɃG���[���������A�K��̃��g���C�񐔂��z�����ꍇ�ɏo�͂��郍�O�̃��b�Z�[�WID���擾����B<p>
     * 
     * @return ���O�̃��b�Z�[�WID
     */
    public String getAsynchInvokeRetryOverErrorLogMessageId();
    
    /**
     * �W���[�i���̏o�̓T�C�Y���L�^����{@link jp.ossc.nimbus.service.performance.PerformanceRecorder PerformanceRecorder}�T�[�r�X�̃T�[�r�X����ݒ肷��B<p>
     *
     * @param name PerformanceRecorder�T�[�r�X�̃T�[�r�X��
     */
    public void setJournalPerformanceRecorderServiceName(ServiceName name);
    
    /**
     * �W���[�i���̏o�̓T�C�Y���L�^����{@link jp.ossc.nimbus.service.performance.PerformanceRecorder PerformanceRecorder}�T�[�r�X�̃T�[�r�X�����擾����B<p>
     *
     * @return PerformanceRecorder�T�[�r�X�̃T�[�r�X��
     */
    public ServiceName getJournalPerformanceRecorderServiceName();
    
    /**
     * �W���[�i���̏o�̓T�C�Y�̓��v�������W���邩�ǂ�����ݒ肷��B<p>
     * �f�t�H���g�́Afalse�Ŏ��W���Ȃ��B<br>
     *
     * @param isCollect ���W����ꍇtrue
     */
    public void setCollectJournalMetrics(boolean isCollect);
    
    /**
     * �W���[�i���̏o�̓T�C�Y�̓��v�������W���邩�ǂ����𔻒肷��B<p>
     *
     * @return true�̏ꍇ�A���W����
     */
    public boolean isCollectJournalMetrics();
    
    /**
     * �W���[�i���̏o�̓T�C�Y�̓��v���ŁA�o�͉񐔂��o�͂��邩�ǂ�����ݒ肷��B<p>
     * �f�t�H���g�́Atrue�ŏo�͂���B<br>
     *
     * @param isOutput �o�͂���ꍇtrue
     */
    public void setOutputJournalMetricsCount(boolean isOutput);
    
    /**
     * �W���[�i���̏o�̓T�C�Y�̓��v���ŁA�o�͉񐔂��o�͂��邩�ǂ����𔻒肷��B<p>
     *
     * @return true�̏ꍇ�A�o�͂���
     */
    public boolean isOutputJournalMetricsCount();
    
    /**
     * �W���[�i���̏o�̓T�C�Y�̓��v���ŁA�ŏI�������o�͂��邩�ǂ�����ݒ肷��B<p>
     * �f�t�H���g�́Afalse�ŏo�͂��Ȃ��B<br>
     *
     * @param isOutput �o�͂���ꍇtrue
     */
    public void setOutputJournalMetricsLastTime(boolean isOutput);
    
    /**
     * �W���[�i���̏o�̓T�C�Y�̓��v���ŁA�ŏI�������o�͂��邩�ǂ����𔻒肷��B<p>
     *
     * @return true�̏ꍇ�A�o�͂���
     */
    public boolean isOutputJournalMetricsLastTime();
    
    /**
     * �W���[�i���̏o�̓T�C�Y�̓��v���ŁA�ŏ��T�C�Y���o�͂��邩�ǂ�����ݒ肷��B<p>
     * �f�t�H���g�́Atrue�ŏo�͂���B<br>
     *
     * @param isOutput �o�͂���ꍇtrue
     */
    public void setOutputJournalMetricsBestSize(boolean isOutput);
    
    /**
     * �W���[�i���̏o�̓T�C�Y�̓��v���ŁA�ŏ��T�C�Y���o�͂��邩�ǂ����𔻒肷��B<p>
     *
     * @return true�̏ꍇ�A�o�͂���
     */
    public boolean isOutputJournalMetricsBestSize();
    
    /**
     * �W���[�i���̏o�̓T�C�Y�̓��v���ŁA�ŏ��T�C�Y�������o�͂��邩�ǂ�����ݒ肷��B<p>
     * �f�t�H���g�́Afalse�ŏo�͂��Ȃ��B<br>
     *
     * @param isOutput �o�͂���ꍇtrue
     */
    public void setOutputJournalMetricsBestSizeTime(boolean isOutput);
    
    /**
     * �W���[�i���̏o�̓T�C�Y�̓��v���ŁA�ŏ��T�C�Y�������o�͂��邩�ǂ����𔻒肷��B<p>
     *
     * @return true�̏ꍇ�A�o�͂���
     */
    public boolean isOutputJournalMetricsBestSizeTime();
    
    /**
     * �W���[�i���̏o�̓T�C�Y�̓��v���ŁA�ő�T�C�Y���o�͂��邩�ǂ�����ݒ肷��B<p>
     * �f�t�H���g�́Atrue�ŏo�͂���B<br>
     *
     * @param isOutput �o�͂���ꍇtrue
     */
    public void setOutputJournalMetricsWorstSize(boolean isOutput);
    
    /**
     * �W���[�i���̏o�̓T�C�Y�̓��v���ŁA�ő�T�C�Y���o�͂��邩�ǂ����𔻒肷��B<p>
     *
     * @return true�̏ꍇ�A�o�͂���
     */
    public boolean isOutputJournalMetricsWorstSize();
    
    /**
     * �W���[�i���̏o�̓T�C�Y�̓��v���ŁA�ő�T�C�Y�������o�͂��邩�ǂ�����ݒ肷��B<p>
     * �f�t�H���g�́Afalse�ŏo�͂��Ȃ��B<br>
     *
     * @param isOutput �o�͂���ꍇtrue
     */
    public void setOutputJournalMetricsWorstSizeTime(boolean isOutput);
    
    /**
     * �W���[�i���̏o�̓T�C�Y�̓��v���ŁA�ő�T�C�Y�������o�͂��邩�ǂ����𔻒肷��B<p>
     *
     * @return true�̏ꍇ�A�o�͂���
     */
    public boolean isOutputJournalMetricsWorstSizeTime();
    
    /**
     * �W���[�i���̏o�̓T�C�Y�̓��v���ŁA���σT�C�Y���o�͂��邩�ǂ�����ݒ肷��B<p>
     * �f�t�H���g�́Atrue�ŏo�͂���B<br>
     *
     * @param isOutput �o�͂���ꍇtrue
     */
    public void setOutputJournalMetricsAverageSize(boolean isOutput);
    
    /**
     * �W���[�i���̏o�̓T�C�Y�̓��v���ŁA���σT�C�Y�������o�͂��邩�ǂ����𔻒肷��B<p>
     *
     * @return true�̏ꍇ�A�o�͂���
     */
    public boolean isOutputJournalMetricsAverageSize();
    
    /**
     * �W���[�i���̏o�̓T�C�Y�̓��v���ŁA���v���o�͎������o�͂��邩�ǂ�����ݒ肷��B<p>
     * �f�t�H���g�́Afalse�ŏo�͂��Ȃ��B<br>
     *
     * @param isOutput �o�͂���ꍇtrue
     */
    public void setOutputJournalMetricsTimestamp(boolean isOutput);
    
    /**
     * �W���[�i���̏o�̓T�C�Y�̓��v���ŁA���v���o�͎������o�͂��邩�ǂ����𔻒肷��B<p>
     *
     * @return true�̏ꍇ�A�o�͂���
     */
    public boolean isOutputJournalMetricsTimestamp();
    
    /**
     * �W���[�i���̏o�̓T�C�Y�̓��v��������������B<p>
     */
    public void resetJournalMetrics();
    
    /**
     * �W���[�i���̏o�̓T�C�Y�̓��v����\������B<p>
     *
     * @return ���v��񕶎���
     */
    public String displayJournalMetricsInfo();
    
    /**
     * �t���[��`XML���ēǂݍ��݂���B<p>
     */
    public void reload();
    
    /**
     * �w�肳�ꂽ�t���[���T�X�y���h����B<p>
     * �t���[���s�C���X�^���X�͕ԋp���邪�A���s����ƃT�X�y���h��Ԃő҂������B<br>
     *
     * @param key �t���[�L�[
     */
    public void suspend(String key);
    
    /**
     * �w�肳�ꂽ�t���[���T�X�y���h��������B<p>
     * 
     * @param key �t���[�L�[
     */
    public void resume(String key);
    
    /**
     * �w�肳�ꂽ�t���[�������I������B<p>
     * 
     * @param key �t���[�L�[
     */
    public void stop(String key);
    
    /**
     * �w�肳�ꂽ�t���[�̎��s�C���X�^���X���擾�s�\�ɂ���B<p>
     * 
     * @param key �t���[�L�[
     */
    public void ignore(String key);
    
    /**
     * �����Ώۂɂ����t���[�ɑ΂��ĕ������L���ɂ���B<p>
     * 
     * @param key �t���[�L�[
     */
    public void unIgnore(String key);
    
    /**
     * �T�X�y���h���̃t���[�L�[�̃��X�g���擾����B<p>
     * 
     * @return �T�X�y���h���̃t���[�L�[�̃��X�g
     */
    public ArrayList getSuspendList();
    
    /**
     * �����Ώۂɂ����t���[�L�[�̃��X�g���擾����B<p>
     * 
     * @return �����Ώۂɂ����t���[�L�[�̃��X�g
     */
    public ArrayList getIgnoreList();
    
    /**
     * ���s���t���[�̃t���[�L�[�̃��X�g���擾����B<p>
     * 
     * @return ���s���t���[�̃t���[�L�[�̃��X�g
     */
    public ArrayList getExecFlowList();
    
    /**
     * �t���[��`XML���ēǂݍ��݂��鎞����ݒ肷��B<p>
     * 
     * @param time �t���[��`XML���ēǂݍ��݂��鎞���Byyyy.MM.dd hh:mm:ss
     */
    public void setRefreshTime(String time);
    
    /**
     * �t���[��`XML��ǂݍ��񂾍ŏI�������擾����B<p>
     * 
     * @return �t���[��`XML��ǂݍ��񂾍ŏI�����Byyyy.MM.dd hh:mm:ss
     */
    public String getLastRrefreshTime();
    
    /**
     * �t���[��`XML������ēǂݍ��݂��鎞�����擾����B<p>
     * 
     * @return �t���[��`XML������ēǂݍ��݂��鎞��
     */
    public String getNextRefreshTime();
}
