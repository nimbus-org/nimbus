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
package jp.ossc.nimbus.service.log;

import java.util.*;

import jp.ossc.nimbus.lang.*;
import jp.ossc.nimbus.daemon.*;
import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.queue.*;
import jp.ossc.nimbus.service.queue.Queue;
import jp.ossc.nimbus.service.context.*;
import jp.ossc.nimbus.service.message.*;
import jp.ossc.nimbus.service.writer.*;

/**
 * ���O�T�[�r�X�B<p>
 * 
 * @author H.Nakano
 */
public class LogService extends ServiceBase
 implements DaemonRunnable, Logger, LogServiceMBean {
    
    private static final long serialVersionUID = -4145738242582933541L;
    
    /** �󕶎��萔 */
    protected static final String EMPTY_STRING = "";
    /** ���ʏ��Context�L�[���w�肳��Ă��Ȃ��ꍇ�̎��ʏ�񕶎��� */
    protected static final String NONE_ID = "NONE";
    
    private static final String LINE_SEP = System.getProperty("line.separator");
    private static final String MSG_CAUSE = "Caused by: ";
    private static final String SERVLET_EXCEPTION_NAME = "javax.servlet.ServletException";
    private static final String GET_ROOT_CAUSE_METHOD = "getRootCause";
    private static final String JMS_EXCEPTION_NAME = "javax.jms.JMSException";
    private static final String GET_LINKED_EXCEPTION_METHOD = "getLinkedException";
    
    /**
     * �J�e�S������{@link LogCategory}�T�[�r�X�̃}�b�s���O�B<p>
     * <table border="1">
     *   <tr bgcolor="#CCCCFF"><th colspan="2">�L�[</th><th colspan="2">�l</th></tr>
     *   <tr bgcolor="#CCCCFF"><th>�^</th><th>���e</th><th>�^</th><th>���e</th></tr>
     *   <tr><td>String</td><td>�J�e�S����</td><td>LogCategory</td><td>�J�e�S���T�[�r�X</td></tr>
     * </table>
     */
    private Map categoryMap;
    
    /**
     * �f�t�H���g�̃J�e�S������{@link LogCategory}�T�[�r�X�̃}�b�s���O�B<p>
     * <table border="1">
     *   <tr bgcolor="#CCCCFF"><th colspan="2">�L�[</th><th colspan="2">�l</th></tr>
     *   <tr bgcolor="#CCCCFF"><th>�^</th><th>���e</th><th>�^</th><th>���e</th></tr>
     *   <tr><td>String</td><td>�J�e�S����</td><td>LogCategory</td><td>�J�e�S���T�[�r�X</td></tr>
     * </table>
     */
    private Map defaultCategoryMap;
    
    /**
     * �J�e�S���T�[�r�X���z��B<p>
     */
    private ServiceName[] categoryNames;
    
    /**
     * {@link Queue}�T�[�r�X���B<p>
     */
    private ServiceName queueServiceName;
    
    /**
     * {@link #getQueueServiceName()}��null�̏ꍇ�A�f�t�H���g��{@link Queue}�T�[�r�X�Ƃ��Đ�������{@link DefaultQueueService}�T�[�r�X�B<p>
     */
    private DefaultQueueService defaultQueue;
    
    /**
     * {@link Queue}�I�u�W�F�N�g�B<p>
     */
    private Queue queue;
    
    /**
     * {@link MessageRecordFactory}�T�[�r�X���B<p>
     */
    private ServiceName messageFactoryServiceName;
    
    /**
     * {@link MessageRecordFactory}�T�[�r�X�B<p>
     */
    private MessageRecordFactory messageFactory;
    
    /**
     * {@link #getMessageRecordFactoryServiceName()}��null�̏ꍇ�A�f�t�H���g��{@link MessageRecordFactory}�T�[�r�X�Ƃ��Đ�������{@link MessageRecordFactoryService}�T�[�r�X�B<p>
     */
    private MessageRecordFactoryService defaultMessageFactory;
    
    /**
     * {@link Context}�T�[�r�X���B<p>
     */
    private ServiceName contextServiceName;
    
    /**
     * {@link Context}�T�[�r�X�B<p>
     */
    private Context context;
    
    /**
     * �f�t�H���g��{@link MessageWriter}�T�[�r�X���B<p>
     */
    private ServiceName defaultMessageWriterServiceName;
    
    /**
     * �f�t�H���g��{@link MessageWriter}�T�[�r�X�B<p>
     */
    private MessageWriter defaultMessageWriter;
    
    /**
     * {@link #getDefaultMessageWriterServiceName()}��null�̏ꍇ�A�f�t�H���g��{@link MessageWriter}�T�[�r�X�Ƃ��Đ�������{@link ConsoleWriterService}�T�[�r�X�B<p>
     */
    private ConsoleWriterService consoleWriter;
    
    /**
     * �f�t�H���g��{@link WritableRecordFactory}�T�[�r�X���B<p>
     */
    private ServiceName defaultWritableRecordFactoryServiceName;
    
    /**
     * �f�t�H���g��{@link WritableRecordFactory}�T�[�r�X�B<p>
     */
    private WritableRecordFactory defaultWritableRecordFactory;
    
    /**
     * {@link #getDefaultWritableRecordFactoryServiceName()}��null�̏ꍇ�A�f�t�H���g��{@link WritableRecordFactory}�T�[�r�X�Ƃ��Đ�������{@link LogWritableRecordFactoryService}�T�[�r�X�B<p>
     */
    private LogWritableRecordFactoryService logWritableRecordFactory;
    
    /**
     * {@link #DEBUG_METHOD_CATEGORY}�J�e�S���̃��O�o�͂��s��{@link MessageWriter}�T�[�r�X���B<p>
     */
    private ServiceName debugMessageWriterServiceName;
    
    /**
     * {@link #SYSTEM_DEBUG_CATEGORY}�J�e�S���̃��O�o�͂��s��{@link MessageWriter}�T�[�r�X���B<p>
     */
    private ServiceName systemDebugMessageWriterServiceName;
    
    /**
     * {@link #SYSTEM_INFO_CATEGORY}�J�e�S���̃��O�o�͂��s��{@link MessageWriter}�T�[�r�X���B<p>
     */
    private ServiceName systemInfoMessageWriterServiceName;
    
    /**
     * {@link #SYSTEM_WARN_CATEGORY}�J�e�S���̃��O�o�͂��s��{@link MessageWriter}�T�[�r�X���B<p>
     */
    private ServiceName systemWarnMessageWriterServiceName;
    
    /**
     * {@link #SYSTEM_ERROR_CATEGORY}�J�e�S���̃��O�o�͂��s��{@link MessageWriter}�T�[�r�X���B<p>
     */
    private ServiceName systemErrorMessageWriterServiceName;
    
    /**
     * {@link #SYSTEM_FATAL_CATEGORY}�J�e�S���̃��O�o�͂��s��{@link MessageWriter}�T�[�r�X���B<p>
     */
    private ServiceName systemFatalMessageWriterServiceName;
    
    /**
     * {@link #DEBUG_METHOD_CATEGORY}�J�e�S���̃��O�o�̓t�H�[�}�b�g���s��{@link WritableRecordFactory}�T�[�r�X���B<p>
     */
    private ServiceName debugRecordFactoryServiceName;
    
    /**
     * {@link #SYSTEM_DEBUG_CATEGORY}�J�e�S���̃��O�o�̓t�H�[�}�b�g���s��{@link WritableRecordFactory}�T�[�r�X���B<p>
     */
    private ServiceName systemDebugRecordFactoryServiceName;
    
    /**
     * {@link #SYSTEM_INFO_CATEGORY}�J�e�S���̃��O�o�̓t�H�[�}�b�g���s��{@link WritableRecordFactory}�T�[�r�X���B<p>
     */
    private ServiceName systemInfoRecordFactoryServiceName;
    
    /**
     * {@link #SYSTEM_WARN_CATEGORY}�J�e�S���̃��O�o�̓t�H�[�}�b�g���s��{@link WritableRecordFactory}�T�[�r�X���B<p>
     */
    private ServiceName systemWarnRecordFactoryServiceName;
    
    /**
     * {@link #SYSTEM_ERROR_CATEGORY}�J�e�S���̃��O�o�̓t�H�[�}�b�g���s��{@link WritableRecordFactory}�T�[�r�X���B<p>
     */
    private ServiceName systemErrorRecordFactoryServiceName;
    
    /**
     * {@link #SYSTEM_FATAL_CATEGORY}�J�e�S���̃��O�o�̓t�H�[�}�b�g���s��{@link WritableRecordFactory}�T�[�r�X���B<p>
     */
    private ServiceName systemFatalRecordFactoryServiceName;
    
    /**
     * {@link Daemon}�I�u�W�F�N�g�B<p>
     */
    private Daemon daemon;
    
    /**
     * �t�H�[�}�b�g���Context�L�[�����i�[����W���B<p>
     */
    private Set contextKeys = new HashSet();
    
    /** {@link #debug(Object)}���\�b�h�̃��O�o�̓t���O */
    private boolean isDebugEnabled = false;
    
    /** �V�X�e�����O��DEBUG���O�o�̓t���O */
    private boolean isSystemDebugEnabled = false;
    
    /** �V�X�e�����O��INFO���O�o�̓t���O */
    private boolean isSystemInfoEnabled = true;
    
    /** �V�X�e�����O��WARN���O�o�̓t���O */
    private boolean isSystemWarnEnabled = true;
    
    /** �V�X�e�����O��ERROR���O�o�̓t���O */
    private boolean isSystemErrorEnabled = true;
    
    /** �V�X�e�����O��FATAL���O�o�̓t���O */
    private boolean isSystemFatalEnabled = true;
    
    protected String defaultFormat = DEFAULT_FORMAT;
    
    private boolean isDaemon = true;
    
    /**
     * �����������s���B<p>
     * ���̃��\�b�h�ɂ́A�ȉ��̎������s���Ă���B<br>
     * <ol>
     *   <li>{@link Daemon}�C���X�^���X�𐶐�����B</li>
     *   <li>�J�e�S���Ǘ��pMap�C���X�^���X�𐶐�����B</li>
     * </ol>
     * 
     * @exception Exception ���������Ɏ��s�����ꍇ�B
     */
    public void createService() throws Exception{
        daemon = new Daemon(this);
        daemon.setName("Nimbus LogWriteDaemon " + getServiceNameObject());
        categoryMap = new HashMap();
        defaultCategoryMap = new HashMap();
    }
    
    /**
     * ���̃��O�T�[�r�X�Ńf�t�H���g�Ŏ���{@link LogCategory}�𐶐����ēo�^����B<p>
     *
     * @param defaultMessageWriter �f�t�H���g��MessageWriter�BmessageWriterName�ɗL���ȃT�[�r�X�����w�肳��Ă��Ȃ��ꍇ��LogCategory�Ŏg�p�����B
     * @param defaultRecordFactory �f�t�H���g��WritableRecordFactory�BrecordFactoryName�ɗL���ȃT�[�r�X�����w�肳��Ă��Ȃ��ꍇ��LogCategory�Ŏg�p�����B
     * @param messageWriterName LogCategory�Ŏg�p�����MessageWriter�T�[�r�X��
     * @param recordFactoryName LogCategory�Ŏg�p�����WritableRecordFactory�T�[�r�X��
     * @param categoryName �J�e�S����
     * @param priorityMin �D�揇�ʔ͈͂̍ŏ��l
     * @param priorityMax �D�揇�ʔ͈͂̍ő�l
     * @param label �J�e�S���̗D�揇�ʔ͈͂̃��x��
     * @param isEnabled �o�͂�L���ɂ��邩�ǂ����̃t���O�B�o�͂����Ԃɂ������ꍇ�� true
     * @exception Exception �J�e�S���T�[�r�X�̐����E�J�n�Ɏ��s�����ꍇ
     */
    protected void addDefaultCategory(
        MessageWriter defaultMessageWriter,
        WritableRecordFactory defaultRecordFactory,
        ServiceName messageWriterName,
        ServiceName recordFactoryName,
        String categoryName,
        int priorityMin,
        int priorityMax,
        String label,
        boolean isEnabled
    ) throws Exception{
        MessageWriter messageWriter = defaultMessageWriter;
        WritableRecordFactory recordFactory = defaultRecordFactory;
        if(messageWriterName != null){
            messageWriter = (MessageWriter)ServiceManagerFactory
                .getServiceObject(messageWriterName);
        }
        if(recordFactoryName != null){
            recordFactory = (WritableRecordFactory)ServiceManagerFactory
                .getServiceObject(recordFactoryName);
        }
        final SimpleCategoryService category = new SimpleCategoryService();
        category.setCategoryName(categoryName);
        category.setPriorityRangeValue(priorityMin, priorityMax);
        category.setLabel(priorityMin, priorityMax, label);
        category.create();
        category.start();
        category.setMessageWriterService(messageWriter);
        category.setWritableRecordFactoryService(recordFactory);
        
        addCategoryService(category);
        addDefaultCategoryService(category);
        
        setEnabled(categoryName, isEnabled);
    }
    
    /**
     * �J�n�������s���B<p>
     * ���̃��\�b�h�ɂ́A�ȉ��̎������s���Ă���B<br>
     * <ol>
     *   <li>{@link #setDefaultMessageWriterServiceName(ServiceName)}�Ńf�t�H���g��{@link MessageWriter}�T�[�r�X�̖��O���ݒ肳��Ă���ꍇ�́A{@link ServiceManager}����MessageWriter���擾����{@link #setDefaultMessageWriterService(MessageWriter)}�Őݒ肷��B�܂��A�f�t�H���g��MessageWriter�T�[�r�X�̖��O���ݒ肳��Ă��Ȃ��ꍇ�́A{@link ConsoleWriterService}�𐶐�����{@link #setDefaultMessageWriterService(MessageWriter)}�Őݒ肷��B</li>
     *   <li>{@link #setDefaultWritableRecordFactoryServiceName(ServiceName)}�Ńf�t�H���g��{@link WritableRecordFactory}�T�[�r�X�̖��O���ݒ肳��Ă���ꍇ�́A{@link ServiceManager}����WritableRecordFactory���擾����{@link #setDefaultWritableRecordFactoryService(WritableRecordFactory)}�Őݒ肷��B�܂��A�f�t�H���g��WritableRecordFactory�T�[�r�X�̖��O���ݒ肳��Ă��Ȃ��ꍇ�́A{@link LogWritableRecordFactoryService}�𐶐�����{@link #setDefaultWritableRecordFactoryService(WritableRecordFactory)}�Őݒ肷��B</li>
     *   <li>�V�X�e���J�e�S���𐶐����ēo�^����B</li>
     *   <li>{@link #setCategoryServiceNames(ServiceName[])}�Őݒ肳�ꂽ�J�e�S����o�^����B</li>
     *   <li>{@link #setQueueServiceName(ServiceName)}��{@link Queue}�T�[�r�X�̖��O���ݒ肳��Ă���ꍇ�́A{@link ServiceManager}����Queue���擾����{@link #setQueueService(Queue)}�Őݒ肷��B�܂��AQueue�T�[�r�X�̖��O���ݒ肳��Ă��Ȃ��ꍇ�́A{@link DefaultQueueService}�𐶐�����{@link #setQueueService(Queue)}�Őݒ肷��B</li>
     *   <li>{@link #setMessageRecordFactoryServiceName(ServiceName)}��{@link MessageRecordFactory}�T�[�r�X�̖��O���ݒ肳��Ă���ꍇ�́A{@link ServiceManager}����MessageRecordFactory���擾����{@link #setMessageRecordFactoryService(MessageRecordFactory)}�Őݒ肷��B�܂��AMessageRecordFactory�T�[�r�X�̖��O���ݒ肳��Ă��Ȃ��ꍇ�́A{@link MessageRecordFactoryService}�𐶐�����{@link #setMessageRecordFactoryService(MessageRecordFactory)}�Őݒ肷��B</li>
     *   <li>{@link #setContextServiceName(ServiceName)}��{@link Context}�T�[�r�X�̖��O���ݒ肳��Ă���ꍇ�́A{@link ServiceManager}����Context���擾����{@link #setContextService(Context)}�Őݒ肷��B</li>
     *   <li>{@link Daemon}���N������B</li>
     * </ol>
     * 
     * @exception Exception �J�n�����Ɏ��s�����ꍇ�B
     */
    public void startService() throws Exception{
        
        // �f�t�H���gMessageWriter�T�[�r�X�̐����܂��͎擾
        if(getDefaultMessageWriterServiceName() == null){
            if(getConsoleWriterService() == null){
                final ConsoleWriterService consoleWriter
                     = new ConsoleWriterService();
                consoleWriter.setOutput(ConsoleWriterService.OUTPUT_STDOUT);
                consoleWriter.create();
                consoleWriter.start();
                setConsoleWriterService(consoleWriter);
            }else{
                getConsoleWriterService().start();
            }
            setDefaultMessageWriterService(getConsoleWriterService());
        }else{
            setDefaultMessageWriterService(
                (MessageWriter)ServiceManagerFactory
                    .getServiceObject(getDefaultMessageWriterServiceName())
            );
        }
        
        // �f�t�H���gWritableRecordFactory�T�[�r�X�̐����܂��͎擾
        if(getDefaultWritableRecordFactoryServiceName() == null){
            if(getLogWritableRecordFactoryService() == null){
                final LogWritableRecordFactoryService recordFactory
                     = new LogWritableRecordFactoryService();
                recordFactory.setFormat(getDefaultFormat());
                recordFactory.create();
                recordFactory.start();
                setLogWritableRecordFactoryService(recordFactory);
            }else{
                getLogWritableRecordFactoryService().start();
            }
            setDefaultWritableRecordFactoryService(
                getLogWritableRecordFactoryService()
            );
        }else{
            setDefaultWritableRecordFactoryService(
                (WritableRecordFactory)ServiceManagerFactory.getServiceObject(
                    getDefaultWritableRecordFactoryServiceName()
                )
            );
        }
        
        // �V�X�e���J�e�S���̓o�^
        initDefaultCategory();
        
        // ���[�U��`�J�e�S���̓o�^
        final ServiceName[] categoryNames = getCategoryServiceNames();
        if(categoryNames != null){
            for(int i = 0; i < categoryNames.length; i++){
                final ServiceName categoryName = categoryNames[i];
                final LogCategory category = (LogCategory)ServiceManagerFactory
                    .getServiceObject(categoryName);
                addCategoryService(category);
            }
        }
        
        // Queue�T�[�r�X�̐����܂��͎擾
        if(getQueueServiceName() == null){
            if(getDefaultQueueService() == null){
                final DefaultQueueService defaultQueue
                     = new DefaultQueueService();
                defaultQueue.create();
                defaultQueue.start();
                setDefaultQueueService(defaultQueue);
            }else{
                getDefaultQueueService().start();
            }
            setQueueService(getDefaultQueueService());
        }else{
            setQueueService((Queue)ServiceManagerFactory
                    .getServiceObject(getQueueServiceName())
            );
        }
        
        // MessageRecordFactory�T�[�r�X�̐����܂��͎擾
        if(getMessageRecordFactoryServiceName() == null){
            if(getDefaultMessageRecordFactoryService() == null){
                final MessageRecordFactoryService defaultMessageFactory
                     = new MessageRecordFactoryService();
                defaultMessageFactory.setMessageRecordClassName(
                    LogMessageRecordImpl.class.getName()
                );
                defaultMessageFactory.create();
                defaultMessageFactory.start();
                setDefaultMessageRecordFactoryService(defaultMessageFactory);
            }else{
                getDefaultMessageRecordFactoryService().start();
            }
            setMessageRecordFactoryService(defaultMessageFactory);
        }else{
            setMessageRecordFactoryService(
                (MessageRecordFactory)ServiceManagerFactory
                    .getServiceObject(getMessageRecordFactoryServiceName())
            );
        }
        
        // Context�T�[�r�X�̎擾
        if(getContextServiceName() != null){
            setContextService((Context)ServiceManagerFactory
                    .getServiceObject(getContextServiceName())
            );
        }
        
        // �L���[�擾�҂����J�n����
        queue.accept();
        
        daemon.setDaemon(isDaemon);
        
        // �f�[�����N��
        daemon.start();
    }
    
    protected void initDefaultCategory() throws Exception{
        addDefaultCategory(
            getDefaultMessageWriterService(),
            getDefaultWritableRecordFactoryService(),
            getDebugMessageWriterServiceName(),
            getDebugWritableRecordFactoryServiceName(),
            DEBUG_METHOD_CATEGORY,
            DEBUG_METHOD_CATEGORY_PRIORITY_MIN,
            DEBUG_METHOD_CATEGORY_PRIORITY_MAX,
            DEBUG_METHOD_CATEGORY_LABEL,
            isDebugEnabled()
        );
        addDefaultCategory(
            getDefaultMessageWriterService(),
            getDefaultWritableRecordFactoryService(),
            getSystemDebugMessageWriterServiceName(),
            getSystemDebugWritableRecordFactoryServiceName(),
            SYSTEM_DEBUG_CATEGORY,
            SYSTEM_DEBUG_CATEGORY_PRIORITY_MIN,
            SYSTEM_DEBUG_CATEGORY_PRIORITY_MAX,
            SYSTEM_DEBUG_CATEGORY_LABEL,
            isSystemDebugEnabled()
        );
        addDefaultCategory(
            getDefaultMessageWriterService(),
            getDefaultWritableRecordFactoryService(),
            getSystemInfoMessageWriterServiceName(),
            getSystemInfoWritableRecordFactoryServiceName(),
            SYSTEM_INFO_CATEGORY,
            SYSTEM_INFO_CATEGORY_PRIORITY_MIN,
            SYSTEM_INFO_CATEGORY_PRIORITY_MAX,
            SYSTEM_INFO_CATEGORY_LABEL,
            isSystemInfoEnabled()
        );
        addDefaultCategory(
            getDefaultMessageWriterService(),
            getDefaultWritableRecordFactoryService(),
            getSystemWarnMessageWriterServiceName(),
            getSystemWarnWritableRecordFactoryServiceName(),
            SYSTEM_WARN_CATEGORY,
            SYSTEM_WARN_CATEGORY_PRIORITY_MIN,
            SYSTEM_WARN_CATEGORY_PRIORITY_MAX,
            SYSTEM_WARN_CATEGORY_LABEL,
            isSystemWarnEnabled()
        );
        addDefaultCategory(
            getDefaultMessageWriterService(),
            getDefaultWritableRecordFactoryService(),
            getSystemErrorMessageWriterServiceName(),
            getSystemErrorWritableRecordFactoryServiceName(),
            SYSTEM_ERROR_CATEGORY,
            SYSTEM_ERROR_CATEGORY_PRIORITY_MIN,
            SYSTEM_ERROR_CATEGORY_PRIORITY_MAX,
            SYSTEM_ERROR_CATEGORY_LABEL,
            isSystemErrorEnabled()
        );
        addDefaultCategory(
            getDefaultMessageWriterService(),
            getDefaultWritableRecordFactoryService(),
            getSystemFatalMessageWriterServiceName(),
            getSystemFatalWritableRecordFactoryServiceName(),
            SYSTEM_FATAL_CATEGORY,
            SYSTEM_FATAL_CATEGORY_PRIORITY_MIN,
            SYSTEM_FATAL_CATEGORY_PRIORITY_MAX,
            SYSTEM_FATAL_CATEGORY_LABEL,
            isSystemFatalEnabled()
        );
    }
    
    /**
     * ��~�������s���B<p>
     * ���̃��\�b�h�ɂ́A�ȉ��̎������s���Ă���B<br>
     * <ol>
     *   <li>�f�t�H���g��MessageWriter�T�[�r�X�𖳖��T�[�r�X�Ƃ��Đ������Ă���ꍇ�A���̃T�[�r�X���~����B</li>
     *   <li>�f�t�H���g��WritableRecordFactory�T�[�r�X�𖳖��T�[�r�X�Ƃ��Đ������Ă���ꍇ�A���̃T�[�r�X���~����B</li>
     *   <li>Queue�T�[�r�X�𖳖��T�[�r�X�Ƃ��Đ������Ă���ꍇ�A
        ���̃T�[�r�X���~����B</li>
     *   <li>MessageRecordFactory�T�[�r�X�𖳖��T�[�r�X�Ƃ��Đ������Ă���ꍇ�A
        ���̃T�[�r�X���~����B</li>
     *   <li>�J�e�S�����폜����B</li>
     *   <li>{@link Daemon}���~����B</li>
     * </ol>
     * 
     * @exception Exception ��~�����Ɏ��s�����ꍇ�B
     */
    public void stopService(){
        
        // �f�[������~
        daemon.stop();
        
        // �L���[�擾�҂����J������
        queue.release();
        
        // �f�t�H���g��MessageWriter�T�[�r�X�𖳖��T�[�r�X�Ƃ��Đ�������
        // ����ꍇ�A���̃T�[�r�X���~����
        if(getDefaultMessageWriterService() == getConsoleWriterService()){
            getConsoleWriterService().stop();
        }
        
        // �f�t�H���g��WritableRecordFactory�T�[�r�X�𖳖��T�[�r�X�Ƃ���
        // �������Ă���ꍇ�A���̃T�[�r�X���~����
        if(getDefaultWritableRecordFactoryService()
             == getLogWritableRecordFactoryService()){
            getLogWritableRecordFactoryService().stop();
        }
        
        // Queue�T�[�r�X�𖳖��T�[�r�X�Ƃ��Đ������Ă���ꍇ�A
        // ���̃T�[�r�X���~����
        if(getQueueService() == getDefaultQueueService()){
            getDefaultQueueService().stop();
        }
        
        // MessageRecordFactory�T�[�r�X�𖳖��T�[�r�X�Ƃ��Đ������Ă���ꍇ�A
        // ���̃T�[�r�X���~����
        if(getMessageRecordFactoryService()
             == getDefaultMessageRecordFactoryService()){
            getDefaultMessageRecordFactoryService().stop();
        }
        
        // �J�e�S�����폜����
        categoryMap.clear();
        defaultCategoryMap.clear();
    }
    
    /**
     * �j���������s���B<p>
     * ���̃��\�b�h�ɂ́A�ȉ��̎������s���Ă���B<br>
     * <ol>
     *   <li>�f�t�H���g��MessageWriter�T�[�r�X�𖳖��T�[�r�X�Ƃ��Đ������Ă���ꍇ�A���̃T�[�r�X��j������B</li>
     *   <li>�f�t�H���g��WritableRecordFactory�T�[�r�X�𖳖��T�[�r�X�Ƃ��Đ������Ă���ꍇ�A���̃T�[�r�X��j������B</li>
     *   <li>Queue�T�[�r�X�𖳖��T�[�r�X�Ƃ��Đ������Ă���ꍇ�A
        ���̃T�[�r�X��j������B</li>
     *   <li>MessageRecordFactory�T�[�r�X�𖳖��T�[�r�X�Ƃ��Đ������Ă���ꍇ�A
        ���̃T�[�r�X��j������B</li>
     *   <li>�J�e�S����j������B</li>
     *   <li>{@link Daemon}��j������B</li>
     * </ol>
     * 
     * @exception Exception �j�������Ɏ��s�����ꍇ�B
     */
    public void destroyService(){
        
        // �f�t�H���g��MessageWriter�T�[�r�X�𖳖��T�[�r�X�Ƃ��Đ�������
        // ����ꍇ�A���̃T�[�r�X��j������
        if(getDefaultMessageWriterService() == getConsoleWriterService()
            && getConsoleWriterService() != null){
            getConsoleWriterService().destroy();
            setConsoleWriterService(null);
        }
        
        // �f�t�H���g��WritableRecordFactory�T�[�r�X�𖳖��T�[�r�X�Ƃ���
        // �������Ă���ꍇ�A���̃T�[�r�X��j������
        if(getDefaultWritableRecordFactoryService()
             == getLogWritableRecordFactoryService()
             && getLogWritableRecordFactoryService() != null){
            getLogWritableRecordFactoryService().destroy();
            setLogWritableRecordFactoryService(null);
        }
        
        // Queue�T�[�r�X�𖳖��T�[�r�X�Ƃ��Đ������Ă���ꍇ�A
        // ���̃T�[�r�X��j������
        if(getQueueService() == getDefaultQueueService()
            && getDefaultQueueService() != null){
            getDefaultQueueService().destroy();
            setDefaultQueueService(null);
        }
        
        // MessageRecordFactory�T�[�r�X�𖳖��T�[�r�X�Ƃ��Đ������Ă���ꍇ�A
        // ���̃T�[�r�X��j������
        if(getMessageRecordFactoryService()
             == getDefaultMessageRecordFactoryService()
            && getDefaultMessageRecordFactoryService() != null){
            getDefaultMessageRecordFactoryService().destroy();
            setDefaultMessageRecordFactoryService(null);
        }
        
        // �J�e�S���Ǘ�Map��j������
        categoryMap = null;
        defaultCategoryMap = null;
        
        // �f�[������j������
        daemon = null;
    }
    
    /**
     * �J�e�S������{@link LogCategory}�̃}�b�s���O���擾����B<p>
     *
     * @return �J�e�S������{@link LogCategory}�̃}�b�s���O
     */
    protected Map getCategoryMap(){
        return categoryMap;
    }
    
    // LogServiceMBean��JavaDoc
    public void setDefaultMessageWriterServiceName(ServiceName name){
        defaultMessageWriterServiceName = name;
    }
    
    // LogServiceMBean��JavaDoc
    public ServiceName getDefaultMessageWriterServiceName(){
        return defaultMessageWriterServiceName;
    }
    
    // LogServiceMBean��JavaDoc
    public void setDefaultMessageWriterService(MessageWriter writer){
        defaultMessageWriter = writer;
    }
    
    // LogServiceMBean��JavaDoc
    public MessageWriter getDefaultMessageWriterService(){
        return defaultMessageWriter;
    }
    
    /**
     * �f�t�H���g��MessageWriter���w�肳��Ă��Ȃ��ꍇ�Ɏg�p����{@link ConsoleWriterService}���擾����B<p>
     * ����ConsoleWriterService�́A�����T�[�r�X�Ƃ��Đ��������B�܂��A{@link #setDefaultMessageWriterServiceName(ServiceName)}�Ńf�t�H���g��MessageWriter���w�肳��Ă���ꍇ�́Anull��Ԃ��ꍇ������B<br>
     *
     * @return ConsoleWriterService�I�u�W�F�N�g�B��������Ă��Ȃ��ꍇ�́Anull��Ԃ��B
     */
    protected ConsoleWriterService getConsoleWriterService(){
        return consoleWriter;
    }
    
    /**
     * �f�t�H���g��MessageWriter���w�肳��Ă��Ȃ��ꍇ�Ɏg�p����{@link ConsoleWriterService}��ݒ肷��B<p>
     *
     * @param consoleWriter ConsoleWriterService�I�u�W�F�N�g
     */
    protected void setConsoleWriterService(ConsoleWriterService consoleWriter){
        this.consoleWriter = consoleWriter;
    }
    
    // LogServiceMBean��JavaDoc
    public void setDefaultWritableRecordFactoryServiceName(ServiceName name){
        defaultWritableRecordFactoryServiceName = name;
    }
    
    // LogServiceMBean��JavaDoc
    public ServiceName getDefaultWritableRecordFactoryServiceName(){
        return defaultWritableRecordFactoryServiceName;
    }
    
    // LogServiceMBean��JavaDoc
    public void setDefaultWritableRecordFactoryService(
        WritableRecordFactory recordFactory
    ){
        defaultWritableRecordFactory = recordFactory;
    }
    
    // LogServiceMBean��JavaDoc
    public WritableRecordFactory getDefaultWritableRecordFactoryService(){
        return defaultWritableRecordFactory;
    }
    
    /**
     * �f�t�H���g��WritableRecordFactory���w�肳��Ă��Ȃ��ꍇ�Ɏg�p����{@link LogWritableRecordFactoryService}���擾����B<p>
     * ����LogWritableRecordFactory�́A�����T�[�r�X�Ƃ��Đ��������B�܂��A{@link #setDefaultWritableRecordFactoryServiceName(ServiceName)}�Ńf�t�H���g��WritableRecordFactory���w�肳��Ă���ꍇ�́Anull��Ԃ��ꍇ������B<br>
     *
     * @return LogWritableRecordFactory�I�u�W�F�N�g�B��������Ă��Ȃ��ꍇ�́Anull��Ԃ��B
     */
    protected LogWritableRecordFactoryService getLogWritableRecordFactoryService(){
        return logWritableRecordFactory;
    }
    
    /**
     * �f�t�H���g��WritableRecordFactory���w�肳��Ă��Ȃ��ꍇ�Ɏg�p����{@link LogWritableRecordFactoryService}��ݒ肷��B<p>
     *
     * @param logRecordFactory LogWritableRecordFactory�I�u�W�F�N�g
     */
    protected void setLogWritableRecordFactoryService(
        LogWritableRecordFactoryService logRecordFactory
    ){
        this.logWritableRecordFactory = logRecordFactory;
    }
    
    // LogServiceMBean��JavaDoc
    public void setCategoryServiceNames(ServiceName[] names){
        categoryNames = names;
    }
    
    // LogServiceMBean��JavaDoc
    public ServiceName[] getCategoryServiceNames(){
        return categoryNames;
    }
    
    // LogServiceMBean��JavaDoc
    public void setCategoryServices(LogCategory[] categories){
        if(categoryMap != null){
            
            // �f�t�H���g�J�e�S���̖��O�W���𐶐�
            final Set defaultCategoryNames = defaultCategoryMap.keySet();
            
            // ���ݕێ����Ă���J�e�S������A�V�X�e���J�e�S���ȊO���폜����
            final Set categoryNames = categoryMap.keySet();
            categoryNames.retainAll(defaultCategoryNames);
            
            // �w�肳�ꂽ�J�e�S����o�^����
            if(categories != null){
                for(int i = 0; i < categories.length; i++){
                    final LogCategory category = categories[i];
                    if(category != null){
                        addCategoryService(category);
                    }
                }
            }
        }
    }
    
    // LogServiceMBean��JavaDoc
    public LogCategory[] getCategoryServices(){
        if(categoryMap != null){
            return (LogCategory[])categoryMap.values().toArray(
                new LogCategory[categoryMap.size()]
            );
        }
        return new LogCategory[0];
    }
    
    // LogServiceMBean��JavaDoc
    public void addCategoryService(LogCategory category){
        if(categoryMap != null && category != null){
            categoryMap.put(category.getCategoryName(), category);
        }
    }
    
    /**
     * �f�t�H���g��{@link LogCategory}�T�[�r�X��ǉ�����B<p>
     *
     * @parma category LogCategory�T�[�r�X
     */
    private void addDefaultCategoryService(LogCategory category){
        if(defaultCategoryMap != null && category != null){
            defaultCategoryMap.put(category.getCategoryName(), category);
        }
    }
    
    // LogServiceMBean��JavaDoc
    public LogCategory getCategoryService(String name){
        if(categoryMap != null && name != null){
            return (LogCategory)categoryMap.get(name);
        }
        return null;
    }
    
    // LogServiceMBean��JavaDoc
    public void setQueueServiceName(ServiceName name){
        queueServiceName = name;
    }
    
    // LogServiceMBean��JavaDoc
    public void setQueueService(Queue queue){
        this.queue = queue;
    }
    
    // LogServiceMBean��JavaDoc
    public ServiceName getQueueServiceName(){
        return queueServiceName;
    }
    
    // LogServiceMBean��JavaDoc
    public Queue getQueueService(){
        return queue;
    }
    
    /**
     * Queue���w�肳��Ă��Ȃ��ꍇ�Ɏg�p����{@link DefaultQueueService}���擾����B<p>
     * ����DefaultQueueService�́A�����T�[�r�X�Ƃ��Đ��������B�܂��A{@link #setQueueServiceName(ServiceName)}��Queue���w�肳��Ă���ꍇ�́Anull��Ԃ��ꍇ������B<br>
     *
     * @return DefaultQueueService�I�u�W�F�N�g�B��������Ă��Ȃ��ꍇ��null��Ԃ��B
     */
    protected DefaultQueueService getDefaultQueueService(){
        return defaultQueue;
    }
    
    /**
     * Queue���w�肳��Ă��Ȃ��ꍇ�Ɏg�p����{@link DefaultQueueService}��ݒ肷��B<p>
     *
     * @param queue DefaultQueueService�I�u�W�F�N�g
     */
    protected void setDefaultQueueService(DefaultQueueService queue){
        defaultQueue = queue;
    }
    
    // LogServiceMBean��JavaDoc
    public void setMessageRecordFactoryServiceName(ServiceName name){
        messageFactoryServiceName = name;
    }
    
    // LogServiceMBean��JavaDoc
    public void setMessageRecordFactoryService(MessageRecordFactory message){
        messageFactory = message;
    }
    
    // LogServiceMBean��JavaDoc
    public ServiceName getMessageRecordFactoryServiceName(){
        return messageFactoryServiceName;
    }
    
    // LogServiceMBean��JavaDoc
    public MessageRecordFactory getMessageRecordFactoryService(){
        return messageFactory;
    }
    
    /**
     * MessageRecordFactory���w�肳��Ă��Ȃ��ꍇ�Ɏg�p����{@link MessageRecordFactoryService}���擾����B<p>
     * ����MessageRecordFactoryService�́A�����T�[�r�X�Ƃ��Đ��������B�܂��A{@link #setMessageRecordFactoryServiceName(ServiceName)}��MessageRecordFactory���w�肳��Ă���ꍇ�́Anull��Ԃ��ꍇ������B<br>
     *
     * @return MessageRecordFactoryService�I�u�W�F�N�g�B��������Ă��Ȃ��ꍇ��null��Ԃ��B
     */
    protected MessageRecordFactoryService getDefaultMessageRecordFactoryService(){
        return defaultMessageFactory;
    }
    
    /**
     * MessageRecordFactory���w�肳��Ă��Ȃ��ꍇ�Ɏg�p����{@link jp.ossc.nimbus.service.message.MessageRecordFactoryService MessageRecordFactoryService}��ݒ肷��B<p>
     *
     * @param message MessageRecordFactoryService�I�u�W�F�N�g
     */
    protected void setDefaultMessageRecordFactoryService(
        MessageRecordFactoryService message
    ){
        defaultMessageFactory = message;
    }
    
    // LogServiceMBean��JavaDoc
    public void setContextServiceName(ServiceName name){
        contextServiceName = name;
    }
    
    // LogServiceMBean��JavaDoc
    public void setContextService(Context context){
        this.context = context;
    }
    
    // LogServiceMBean��JavaDoc
    public ServiceName getContextServiceName(){
        return contextServiceName;
    }
    
    // LogServiceMBean��JavaDoc
    public Context getContextService(){
        return context;
    }
    
    // LogServiceMBean��JavaDoc
    public void setContextFormatKeys(String[] keys){
        if(keys != null){
            for(int i = 0; i < keys.length; i++){
                if(keys[i] != null){
                    contextKeys.add(keys[i]);
                }
            }
        }
    }
    
    // LogServiceMBean��JavaDoc
    public void addContextFormatKey(String key){
        if(key != null){
            contextKeys.add(key);
        }
    }
    
    // LogServiceMBean��JavaDoc
    public void removeContextFormatKey(String key){
        if(key != null){
            contextKeys.remove(key);
        }
    }
    
    // LogServiceMBean��JavaDoc
    public void clearContextFormatKeys(){
        contextKeys.clear();
    }
    
    // LogServiceMBean��JavaDoc
    public String[] getContextFormatKeys(){
        return (String[])contextKeys.toArray(new String[contextKeys.size()]);
    }
    
    protected String getDefaultFormat(){
        return defaultFormat;
    }
    
    /**
     * �w�肳�ꂽ��������L�[��{@link Context}�T�[�r�X����l���擾����B<p>
     *
     * @param key �L�[
     * @return {@link Context}�T�[�r�X����擾�����l
     */
    protected Object getContextFormatValue(String key){
        final Context context = getContextService();
        if(context != null){
            return context.get(key);
        }
        return null;
    }
    
    /**
     * �w�肳�ꂽ{@link LogMessageRecord}���o�͂���邩���肷��B<p>
     *
     * @param messageRecord LogMessageRecord
     * @return �o�͂����ꍇ true
     */
    protected boolean isWrite(LogMessageRecord messageRecord){
        final int priority = messageRecord.getPriority();
        final Iterator categoryNames = messageRecord.getCategories().iterator();
        while(categoryNames.hasNext()){
            final String categoryName = (String)categoryNames.next();
            final LogCategory category = getCategoryService(categoryName);
            if(category == null){
                continue;
            }
            if(category.isEnabled() && category.isValidPriorityRange(priority)){
                return true;
            }
        }
        return false;
    }
    
    /**
     * ���O�̃L���[�ɑ}������{@link LogEnqueuedRecord}�𐶐�����B<p>
     * {@link #write(LogMessageRecord, Locale, String, Throwable)}����Ăяo�����B<br>
     *
     * @param messageRecord ���O�o�͗v���̂�����LogMessageRecord�I�u�W�F�N�g
     * @param locale ���O�o�͂Ɏg�p����郁�b�Z�[�W�̃��P�[��
     * @param embed ���O�o�͂̃��b�Z�[�W�Ɏg�p����閄�ߍ��ݕ�����B���ߍ��݂̂Ȃ����b�Z�[�W�̏ꍇ�́Anull�B
     * @param throwable ���O�o�͂Ɏg�p������O
     * @return ���O�̃L���[�ɑ}������LogEnqueuedRecord
     */
    protected LogEnqueuedRecord createLogEnqueuedRecord(
        LogMessageRecord messageRecord,
        Locale locale,
        String embed,
        Throwable throwable
    ){
        return new LogEnqueuedRecord(messageRecord, locale, embed, throwable);
    }
    
    /**
     * ���O�̃L���[�ɑ}������{@link LogEnqueuedRecord}�𐶐�����B<p>
     * {@link #write(LogMessageRecord, Locale, String[], Throwable)}����Ăяo�����B<br>
     *
     * @param messageRecord ���O�o�͗v���̂�����LogMessageRecord�I�u�W�F�N�g
     * @param locale ���O�o�͂Ɏg�p����郁�b�Z�[�W�̃��P�[��
     * @param embeds ���O�o�͂̃��b�Z�[�W�Ɏg�p����閄�ߍ��ݕ�����B���ߍ��݂̂Ȃ����b�Z�[�W�̏ꍇ�́Anull�B
     * @param throwable ���O�o�͂Ɏg�p������O�B��O���b�Z�[�W�łȂ��ꍇ�́Anull�B
     * @return ���O�̃L���[�ɑ}������LogEnqueuedRecord
     */
    protected LogEnqueuedRecord createLogEnqueuedRecord(
        LogMessageRecord messageRecord,
        Locale locale,
        String[] embeds,
        Throwable throwable
    ){
        return new LogEnqueuedRecord(messageRecord, locale, embeds, throwable);
    }
    
    /**
     * �f�o�b�O���O�p��{@link LogMessageRecord}�𐶐�����B<p>
     * {@link #debug(Object)}�A{@link #debug(Object, Throwable)}����Ăяo����A{@link MessageRecordFactory}�ɒ�`����Ă��Ȃ����b�Z�[�W�p��LogMessageRecord�𐶐�����B<br>
     *
     * @param category �J�e�S����
     * @param priority �D�揇��
     * @param message ���b�Z�[�W
     * @return �f�o�b�O���O�p��LogMessageRecord
     */
    protected LogMessageRecord createDebugLogMessageRecord(
        String category,
        int priority,
        Object message
    ){
        final LogMessageRecordImpl record = new LogMessageRecordImpl();
        record.addCategory(category);
        record.setPriority(priority);
        record.setMessageCode(EMPTY_STRING);
        record.addMessage(message != null ? message.toString() : null);
        record.setFactory(getMessageRecordFactoryService());
        return record;
    }
    
    /**
     * ���O�̃L���[�ɑ}������{@link LogEnqueuedRecord}�𐶐����ăL���[�ɑ}������B<p>
     * LogEnqueuedRecord�̐����́A{@link #createLogEnqueuedRecord(LogMessageRecord, Locale, String, Throwable)}���Ăяo���čs���B<br>
     * �L���[�ւ̑}���́A{@link #enqueue(LogEnqueuedRecord)}���Ăяo���čs���B<br>
     *
     * @param messageRecord �o�͂���LogMessageRecord
     * @param locale ���O�o�͂Ɏg�p����郁�b�Z�[�W�̃��P�[��
     * @param embed ���O�o�͂̃��b�Z�[�W�Ɏg�p����閄�ߍ��ݕ�����B���ߍ��݂̂Ȃ����b�Z�[�W�̏ꍇ�́Anull�B
     * @param throwable ���O�o�͂Ɏg�p������O�B��O���b�Z�[�W�łȂ��ꍇ�́Anull�B
     */
    protected void write(
        LogMessageRecord messageRecord,
        Locale locale,
        String embed,
        Throwable throwable
    ){
        if(getState() != STARTED){
            return;
        }
        final LogEnqueuedRecord enqueuedRecord = createLogEnqueuedRecord(
            messageRecord,
            locale,
            embed,
            throwable
        );
        enqueue(enqueuedRecord);
    }
    
    /**
     * ���O�̃L���[�ɑ}������{@link LogEnqueuedRecord}�𐶐����ăL���[�ɑ}������B<p>
     * LogEnqueuedRecord�̐����́A{@link #createLogEnqueuedRecord(LogMessageRecord, Locale, String, Throwable)}���Ăяo���čs���B<br>
     * �L���[�ւ̑}���́A{@link #enqueue(LogEnqueuedRecord)}���Ăяo���čs���B<br>
     *
     * @param messageRecord �o�͂���LogMessageRecord
     * @param locale ���O�o�͂Ɏg�p����郁�b�Z�[�W�̃��P�[��
     * @param embeds ���O�o�͂̃��b�Z�[�W�Ɏg�p����閄�ߍ��ݕ�����B���ߍ��݂̂Ȃ����b�Z�[�W�̏ꍇ�́Anull�B
     * @param throwable ���O�o�͂Ɏg�p������O�B��O���b�Z�[�W�łȂ��ꍇ�́Anull�B
     */
    protected void write(
        LogMessageRecord messageRecord,
        Locale locale,
        String[] embeds,
        Throwable throwable
    ){
        final LogEnqueuedRecord enqueuedRecord = createLogEnqueuedRecord(
            messageRecord,
            locale,
            embeds,
            throwable
        );
        enqueue(enqueuedRecord);
    }
    
    /**
     * ���O�̃L���[�ɑ}������O�������s���B<p>
     * {@link #FORMAT_DATE_KEY}�ɑΉ�����{@link Date}�I�u�W�F�N�g�𐶐����āA{@link LogEnqueuedRecord#addWritableElement(Object, Object)}��{@link jp.ossc.nimbus.service.writer.WritableElement WritableElement}�Ƃ��Ēǉ�����B<br>
     * �܂��A{@link #setContextFormatKeys(String[])}�Őݒ肳�ꂽ�L�[���g���āA{@link #setContextServiceName(ServiceName)}�Ŏw�肳�ꂽ{@link Context}�T�[�r�X����I�u�W�F�N�g���擾����B���̃I�u�W�F�N�g���A�R���e�L�X�g�t�H�[�}�b�g���Ƃ���{@link LogEnqueuedRecord#addWritableElement(Object, Object)}��{@link jp.ossc.nimbus.service.writer.WritableElement WritableElement}�Ƃ��Ēǉ�����B<br>
     *
     * @param enqueuedRecord �L���[�ɑ}������LogEnqueuedRecord
     */
    protected void preEnqueue(LogEnqueuedRecord enqueuedRecord){
        enqueuedRecord.addWritableElement(
            FORMAT_DATE_KEY,
            new Date()
        );
        final String[] keys = getContextFormatKeys();
        if(keys != null){
            for(int i = 0; i < keys.length; i++){
                if(keys[i] != null){
                    final Object val = getContextFormatValue(keys[i]);
                    if(val != null){
                        enqueuedRecord.addWritableElement(
                            keys[i],
                            val
                        );
                    }
                }
            }
        }
    }
    
    /**
     * ���O�̃L���[�ɑ}������B<p>
     * �L���[�}���O�ɁA{@link #preEnqueue(LogEnqueuedRecord)}���Ăяo���B<br>
     * 
     * @param enqueuedRecord LogEnqueuedRecord�I�u�W�F�N�g
     */
    protected void enqueue(LogEnqueuedRecord enqueuedRecord){
        preEnqueue(enqueuedRecord);
        queue.push(enqueuedRecord);
    }
    
    /**
     * ���O�̃L���[���o����̏������s���B<p>
     * {@link LogMessageRecord#makeMessage(Locale, Object[])}�ŏo�̓��b�Z�[�W�𐶐�����B�����������b�Z�[�W��{@link #FORMAT_MESSAGE_KEY}�ɑΉ����郁�b�Z�[�W�Ƃ��āA{@link LogEnqueuedRecord#addWritableElement(Object, Object)}��{@link jp.ossc.nimbus.service.writer.WritableElement WritableElement}�Ƃ��Ēǉ�����B<br>
     * �܂��A{@link #FORMAT_CODE_KEY}�ɑΉ����郁�b�Z�[�W�R�[�h���擾���āA{@link LogEnqueuedRecord#addWritableElement(Object, Object)}��{@link jp.ossc.nimbus.service.writer.WritableElement WritableElement}�Ƃ��Ēǉ�����B<br>
     *
     * @param dequeuedRecord LogEnqueuedRecord�I�u�W�F�N�g
     */
    protected void postDequeue(LogEnqueuedRecord dequeuedRecord){
        final LogMessageRecord messageRecord
             = dequeuedRecord.getLogMessageRecord();
        final Locale locale = dequeuedRecord.getLocale();
        final String[] embeds = dequeuedRecord.getEmbedStringArray();
        final Throwable throwable = dequeuedRecord.getThrowable();
        String message = messageRecord.makeMessage(locale, embeds);
        if(throwable != null && messageRecord.isPrintStackTrace()){
            final StringBuffer buf = new StringBuffer(message);
            buf.append(LINE_SEP);
            buf.append(getStackTraceString(throwable));
            message = buf.toString();
        }
        dequeuedRecord.addWritableElement(
            FORMAT_CODE_KEY,
            messageRecord.getMessageCode()
        );
        dequeuedRecord.addWritableElement(
            FORMAT_MESSAGE_KEY,
            message
        );
    }
    
    /**
     * �w�肳�ꂽ{@link LogMessageRecord}���o�͂����{@link LogCategory}���擾����B<p>
     *
     * @param messageRecord LogMessageRecord
     * @return �o�͂����LogCategory�̔z��
     */
    protected LogCategory[] getWriteCategories(LogMessageRecord messageRecord){
        final List result = new ArrayList();
        final int priority = messageRecord.getPriority();
        final Iterator categoryNames = messageRecord.getCategories().iterator();
        while(categoryNames.hasNext()){
            final String categoryName = (String)categoryNames.next();
            final LogCategory category = getCategoryService(categoryName);
            if(category != null && category.isEnabled()
                && category.isValidPriorityRange(priority)){
                result.add(category);
            }
        }
        return (LogCategory[])result.toArray(new LogCategory[result.size()]);
    }
    
    /**
     * �L���[���o����ɁA�J�e�S������{@link WritableRecord}�𐶐�����B<p>
     * {@link #dequeue(LogEnqueuedRecord)}����Ăяo�����B<br>
     *
     * @param dequeuedRecord �L���[������o����LogEnqueuedRecord�I�u�W�F�N�g
     * @param category LogCategory�I�u�W�F�N�g
     */
    protected Map createWritableElementMap(
        LogEnqueuedRecord dequeuedRecord,
        LogCategory category
    ){
        final LogMessageRecord messageRecord
             = dequeuedRecord.getLogMessageRecord();
        dequeuedRecord.addWritableElement(
            FORMAT_PRIORITY_KEY,
            category.getLabel(messageRecord.getPriority())
        );
        dequeuedRecord.addWritableElement(
            FORMAT_CATEGORY_KEY,
            category.getCategoryName()
        );
        return dequeuedRecord.getWritableElements();
    }
    
    /**
     * �L���[������o���ꂽ{@link LogEnqueuedRecord}����J�e�S������{@link WritableRecord}�𐶐�����{@link MessageWriter}�ɏo�͂��˗�����B<p>
     * {@link #postDequeue(LogEnqueuedRecord)}���Ăяo���āA�L���[���o����̏������s���B<br>
     * �܂��A{@link #getWriteCategories(LogMessageRecord)}�ŏo�͂��ׂ�{@link LogCategory}���擾���āA�J�e�S������{@link #createWritableElementMap(LogEnqueuedRecord, LogCategory)}��Map�𐶐�����B����Map���J�e�S����{@link LogCategory#write(int, Map)}���g���āA�o�͂��˗�����B<br>
     *
     * @param dequeuedRecord �L���[������o����LogEnqueuedRecord�I�u�W�F�N�g
     */
    protected void dequeue(LogEnqueuedRecord dequeuedRecord){
        postDequeue(dequeuedRecord);
        final LogMessageRecord messageRecord
             = dequeuedRecord.getLogMessageRecord();
        final LogCategory[] categories = getWriteCategories(messageRecord);
        for(int i = 0; i < categories.length; i++){
            final LogCategory category = categories[i];
            try{
                category.write(
                    messageRecord.getPriority(),
                    createWritableElementMap(dequeuedRecord, category)
                );
            }catch(MessageWriteException e){
                // ��������
            }
        }
    }
    
    /**
     * �f�[�������J�n�������ɌĂяo�����B<p>
     * 
     * @return ���true��Ԃ�
     */
    public boolean onStart() {
        return true;
    }
    
    /**
     * �f�[��������~�������ɌĂяo�����B<p>
     * 
     * @return ���true��Ԃ�
     */
    public boolean onStop() {
        return true;
    }
    
    /**
     * �f�[���������f�������ɌĂяo�����B<p>
     * 
     * @return ���true��Ԃ�
     */
    public boolean onSuspend() {
        return true;
    }
    
    /**
     * �f�[�������ĊJ�������ɌĂяo�����B<p>
     * 
     * @return ���true��Ԃ�
     */
    public boolean onResume() {
        return true;
    }
    
    /**
     * �L���[����P���o���ĕԂ��B<p>
     * 
     * @param ctrl DaemonControl�I�u�W�F�N�g
     * @return {@link LogEnqueuedRecord}�I�u�W�F�N�g
     */
    public Object provide(DaemonControl ctrl){
        return queue.get(5000);
    }
    
    /**
     * ����dequeued�œn���ꂽ�I�u�W�F�N�g�������B<p>
     * ����dequeued�œn���ꂽ�I�u�W�F�N�g��{@link LogEnqueuedRecord}�ɃL���X�g����{@link #dequeue(LogEnqueuedRecord)}���Ăяo���B<br>
     *
     * @param dequeued �L���[������o���ꂽ�I�u�W�F�N�g
     * @param ctrl DaemonControl�I�u�W�F�N�g
     */
    public void consume(Object dequeued, DaemonControl ctrl){
        if(dequeued == null){
            return;
        }
        try{
            dequeue((LogEnqueuedRecord)dequeued);
        }catch(Throwable e){
            e.printStackTrace();
        }
    }
    
    /**
     * �L���[�̒��g��f���o���B<p>
     */
    public void garbage(){
        if(queue != null){
            while(queue.size() > 0){
                consume(queue.get(0), daemon);
            }
        }
    }
    
    // Logger��JavaDoc
    public void debug(Object msg){
        final LogMessageRecord messageRecord = createDebugLogMessageRecord(
            DEBUG_METHOD_CATEGORY,
            DEBUG_METHOD_CATEGORY_PRIORITY_MIN,
            msg
        );
        if(!isWrite(messageRecord)){
            return;
        }
        write(messageRecord, null, (String)null, null);
    }
    
    // Logger��JavaDoc
    public void debug(Object msg, Throwable oException){
        final LogMessageRecord messageRecord = createDebugLogMessageRecord(
            DEBUG_METHOD_CATEGORY,
            DEBUG_METHOD_CATEGORY_PRIORITY_MIN,
            msg
        );
        if(!isWrite(messageRecord)){
            return;
        }
        write(messageRecord, null, (String)null, oException);
    }
    
    // Logger��JavaDoc
    public void write(String logCode, Object embed){
        write(Locale.getDefault(), logCode, embed);
    }
    
    // Logger��JavaDoc
    public void write(String logCode, byte embed){
        write(Locale.getDefault(), logCode, embed);
    }
    
    // Logger��JavaDoc
    public void write(String logCode, short embed){
        write(Locale.getDefault(), logCode, embed);
    }
    
    // Logger��JavaDoc
    public void write(String logCode, char embed){
        write(Locale.getDefault(), logCode, embed);
    }
    
    // Logger��JavaDoc
    public void write(String logCode, int embed){
        write(Locale.getDefault(), logCode, embed);
    }
    
    // Logger��JavaDoc
    public void write(String logCode, long embed){
        write(Locale.getDefault(), logCode, embed);
    }
    
    // Logger��JavaDoc
    public void write(String logCode, float embed){
        write(Locale.getDefault(), logCode, embed);
    }
    
    // Logger��JavaDoc
    public void write(String logCode, double embed){
        write(Locale.getDefault(), logCode, embed);
    }
    
    // Logger��JavaDoc
    public void write(String logCode, boolean embed){
        write(Locale.getDefault(), logCode, embed);
    }
    
    // Logger��JavaDoc
    public void write(Locale lo, String logCode, Object embed){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(
            messageRecord,
            lo,
            embed != null ? embed.toString() : (String)null,
            null
        );
    }
    
    // Logger��JavaDoc
    public void write(Locale lo, String logCode, byte embed){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, Byte.toString(embed), null);
    }
    
    // Logger��JavaDoc
    public void write(Locale lo, String logCode, short embed){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, Short.toString(embed), null);
    }
    
    // Logger��JavaDoc
    public void write(Locale lo, String logCode, char embed){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, new Character(embed).toString(), null);
    }
    
    // Logger��JavaDoc
    public void write(Locale lo, String logCode, int embed){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, Integer.toString(embed), null);
    }
    
    // Logger��JavaDoc
    public void write(Locale lo, String logCode, long embed){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, Long.toString(embed), null);
    }
    
    // Logger��JavaDoc
    public void write(Locale lo, String logCode, float embed){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, Float.toString(embed), null);
    }
    
    // Logger��JavaDoc
    public void write(Locale lo, String logCode, double embed){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, Double.toString(embed), null);
    }
    
    // Logger��JavaDoc
    public void write(Locale lo, String logCode, boolean embed){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, Boolean.toString(embed), null);
    }
    
    // Logger��JavaDoc
    public void write(String logCode, Object[] embeds) {
        write(Locale.getDefault(), logCode, embeds);
    }
    
    // Logger��JavaDoc
    public void write(String logCode, byte[] embeds){
        write(Locale.getDefault(), logCode, embeds);
    }
    
    // Logger��JavaDoc
    public void write(String logCode, short[] embeds){
        write(Locale.getDefault(), logCode, embeds);
    }
    
    // Logger��JavaDoc
    public void write(String logCode, char[] embeds){
        write(Locale.getDefault(), logCode, embeds);
    }
    
    // Logger��JavaDoc
    public void write(String logCode, int[] embeds){
        write(Locale.getDefault(), logCode, embeds);
    }
    
    // Logger��JavaDoc
    public void write(String logCode, long[] embeds){
        write(Locale.getDefault(), logCode, embeds);
    }
    
    // Logger��JavaDoc
    public void write(String logCode, float[] embeds){
        write(Locale.getDefault(), logCode, embeds);
    }
    
    // Logger��JavaDoc
    public void write(String logCode, double[] embeds){
        write(Locale.getDefault(), logCode, embeds);
    }
    
    // Logger��JavaDoc
    public void write(String logCode, boolean[] embeds){
        write(Locale.getDefault(), logCode, embeds);
    }
    
    /**
     * Object�z���String�z��ɕϊ�����B<p>
     *
     * @param vals Object�z��
     * @return String�z��
     */
    protected static String[] convertStringArray(Object[] vals){
        String[] strings = null;
        if(vals != null){
            strings = new String[vals.length];
            for(int i = 0; i < vals.length; i++){
                if(vals[i] != null){
                    strings[i] = vals[i].toString();
                }
            }
        }
        return strings;
    }
    
    /**
     * byte�z���String�z��ɕϊ�����B<p>
     *
     * @param vals byte�z��
     * @return String�z��
     */
    protected static String[] convertStringArray(byte[] vals){
        String[] strings = null;
        if(vals != null){
            strings = new String[vals.length];
            for(int i = 0; i < vals.length; i++){
                strings[i] = Byte.toString(vals[i]);
            }
        }
        return strings;
    }
    
    /**
     * short�z���String�z��ɕϊ�����B<p>
     *
     * @param vals short�z��
     * @return String�z��
     */
    protected static String[] convertStringArray(short[] vals){
        String[] strings = null;
        if(vals != null){
            strings = new String[vals.length];
            for(int i = 0; i < vals.length; i++){
                strings[i] = Short.toString(vals[i]);
            }
        }
        return strings;
    }
    
    /**
     * char�z���String�z��ɕϊ�����B<p>
     *
     * @param vals char�z��
     * @return String�z��
     */
    protected static String[] convertStringArray(char[] vals){
        String[] strings = null;
        if(vals != null){
            strings = new String[vals.length];
            for(int i = 0; i < vals.length; i++){
                strings[i] = new Character(vals[i]).toString();
            }
        }
        return strings;
    }
    
    /**
     * int�z���String�z��ɕϊ�����B<p>
     *
     * @param vals int�z��
     * @return String�z��
     */
    protected static String[] convertStringArray(int[] vals){
        String[] strings = null;
        if(vals != null){
            strings = new String[vals.length];
            for(int i = 0; i < vals.length; i++){
                strings[i] = Integer.toString(vals[i]);
            }
        }
        return strings;
    }
    
    /**
     * long�z���String�z��ɕϊ�����B<p>
     *
     * @param vals long�z��
     * @return String�z��
     */
    protected static String[] convertStringArray(long[] vals){
        String[] strings = null;
        if(vals != null){
            strings = new String[vals.length];
            for(int i = 0; i < vals.length; i++){
                strings[i] = Long.toString(vals[i]);
            }
        }
        return strings;
    }
    
    /**
     * float�z���String�z��ɕϊ�����B<p>
     *
     * @param vals float�z��
     * @return String�z��
     */
    protected static String[] convertStringArray(float[] vals){
        String[] strings = null;
        if(vals != null){
            strings = new String[vals.length];
            for(int i = 0; i < vals.length; i++){
                strings[i] = Float.toString(vals[i]);
            }
        }
        return strings;
    }
    
    /**
     * double�z���String�z��ɕϊ�����B<p>
     *
     * @param vals double�z��
     * @return String�z��
     */
    protected static String[] convertStringArray(double[] vals){
        String[] strings = null;
        if(vals != null){
            strings = new String[vals.length];
            for(int i = 0; i < vals.length; i++){
                strings[i] = Double.toString(vals[i]);
            }
        }
        return strings;
    }
    
    /**
     * boolean�z���String�z��ɕϊ�����B<p>
     *
     * @param vals boolean�z��
     * @return String�z��
     */
    protected static String[] convertStringArray(boolean[] vals){
        String[] strings = null;
        if(vals != null){
            strings = new String[vals.length];
            for(int i = 0; i < vals.length; i++){
                strings[i] = Boolean.toString(vals[i]);
            }
        }
        return strings;
    }
    
    // Logger��JavaDoc
    public void write(Locale lo, String logCode, Object[] embeds){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, convertStringArray(embeds), null);
    }
    
    // Logger��JavaDoc
    public void write(Locale lo, String logCode, byte[] embeds){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, convertStringArray(embeds), null);
    }
    
    // Logger��JavaDoc
    public void write(Locale lo,String logCode,short[] embeds){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, convertStringArray(embeds), null);
    }
    
    // Logger��JavaDoc
    public void write(Locale lo,String logCode,char[] embeds){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, convertStringArray(embeds), null);
    }
    
    // Logger��JavaDoc
    public void write(Locale lo,String logCode,int[] embeds){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, convertStringArray(embeds), null);
    }
    
    // Logger��JavaDoc
    public void write(Locale lo,String logCode,long[] embeds){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, convertStringArray(embeds), null);
    }
    
    // Logger��JavaDoc
    public void write(Locale lo,String logCode,float[] embeds){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, convertStringArray(embeds), null);
    }
    
    // Logger��JavaDoc
    public void write(Locale lo, String logCode, double[] embeds){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, convertStringArray(embeds), null);
    }
    
    // Logger��JavaDoc
    public void write(Locale lo, String logCode, boolean[] embeds){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, convertStringArray(embeds), null);
    }
    
    // Logger��JavaDoc
    public void write(String logCode){
        write(Locale.getDefault(), logCode);
    }
    
    // Logger��JavaDoc
    public void write(Locale lo, String logCode){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, (String)null, null);
    }
    
    // Logger��JavaDoc
    public void write(String logCode, Throwable oException) {
        write(Locale.getDefault(), logCode, oException);
    }
    
    // Logger��JavaDoc
    public void write(Locale lo, String logCode, Throwable oException){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, (String)null, oException);
    }
    
    // Logger��JavaDoc
    public void write(String logCode, Object embed, Throwable oException){
        write(Locale.getDefault(), logCode, embed, oException);
    }
    
    // Logger��JavaDoc
    public void write(String logCode, byte embed, Throwable oException){
        write(Locale.getDefault(), logCode, embed, oException);
    }
    
    // Logger��JavaDoc
    public void write(String logCode, short embed, Throwable oException){
        write(Locale.getDefault(), logCode, embed, oException);
    }
    
    // Logger��JavaDoc
    public void write(String logCode, char embed, Throwable oException){
        write(Locale.getDefault(), logCode, embed, oException);
    }
    
    // Logger��JavaDoc
    public void write(String logCode, int embed, Throwable oException){
        write(Locale.getDefault(), logCode, embed, oException);
    }
    
    // Logger��JavaDoc
    public void write(String logCode, long embed, Throwable oException){
        write(Locale.getDefault(), logCode, embed, oException);
    }
    
    // Logger��JavaDoc
    public void write(String logCode, float embed, Throwable oException){
        write(Locale.getDefault(), logCode, embed, oException);
    }
    
    // Logger��JavaDoc
    public void write(String logCode, double embed, Throwable oException){
        write(Locale.getDefault(), logCode, embed, oException);
    }
    
    // Logger��JavaDoc
    public void write(String logCode, boolean embed, Throwable oException){
        write(Locale.getDefault(), logCode, embed, oException);
    }
    
    // Logger��JavaDoc
    public void write(
        Locale lo,
        String logCode,
        Object embed,
        Throwable oException
    ){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(
            messageRecord,
            lo,
            embed != null ? embed.toString() : (String)null,
            oException
        );
    }
    
    // Logger��JavaDoc
    public void write(
        Locale lo,
        String logCode,
        byte embed,
        Throwable oException
    ){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, Byte.toString(embed), oException);
    }
    
    // Logger��JavaDoc
    public void write(
        Locale lo,
        String logCode,
        short embed,
        Throwable oException
    ){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, Short.toString(embed), oException);
    }
    
    // Logger��JavaDoc
    public void write(
        Locale lo,
        String logCode,
        char embed,
        Throwable oException
    ){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, new Character(embed).toString(), oException);
    }
    
    // Logger��JavaDoc
    public void write(
        Locale lo,
        String logCode,
        int embed,
        Throwable oException
    ){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, Integer.toString(embed), oException);
    }
    
    // Logger��JavaDoc
    public void write(
        Locale lo,
        String logCode,
        long embed,
        Throwable oException
    ){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, Long.toString(embed), oException);
    }
    
    // Logger��JavaDoc
    public void write(
        Locale lo,
        String logCode,
        float embed,
        Throwable oException
    ){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, Float.toString(embed), oException);
    }
    
    // Logger��JavaDoc
    public void write(
        Locale lo,
        String logCode,
        double embed,
        Throwable oException
    ){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, Double.toString(embed), oException);
    }
    
    // Logger��JavaDoc
    public void write(
        Locale lo,
        String logCode,
        boolean embed,
        Throwable oException
    ){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, Boolean.toString(embed), oException);
    }
    
    // Logger��JavaDoc
    public void write(String logCode, Object[] embeds, Throwable oException) {
        write(Locale.getDefault(), logCode, embeds, oException);
    }
    
    // Logger��JavaDoc
    public void write(String logCode, byte[] embeds, Throwable oException){
        write(Locale.getDefault(), logCode, embeds, oException);
    }
    
    // Logger��JavaDoc
    public void write(String logCode, short[] embeds, Throwable oException){
        write(Locale.getDefault(), logCode, embeds, oException);
    }
    
    // Logger��JavaDoc
    public void write(String logCode, char[] embeds, Throwable oException){
        write(Locale.getDefault(), logCode, embeds, oException);
    }
    
    // Logger��JavaDoc
    public void write(String logCode, int[] embeds, Throwable oException){
        write(Locale.getDefault(), logCode, embeds, oException);
    }
    
    // Logger��JavaDoc
    public void write(String logCode, long[] embeds, Throwable oException){
        write(Locale.getDefault(), logCode, embeds, oException);
    }
    
    // Logger��JavaDoc
    public void write(String logCode, float[] embeds, Throwable oException){
        write(Locale.getDefault(), logCode, embeds, oException);
    }
    
    // Logger��JavaDoc
    public void write(String logCode, double[] embeds, Throwable oException){
        write(Locale.getDefault(), logCode, embeds, oException);
    }
    
    // Logger��JavaDoc
    public void write(String logCode, boolean[] embeds, Throwable oException){
        write(Locale.getDefault(), logCode, embeds, oException);
    }
    
    // Logger��JavaDoc
    public void write(
        Locale lo,
        String logCode,
        Object[] embeds,
        Throwable oException
    ){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, convertStringArray(embeds), oException);
    }
    
    // Logger��JavaDoc
    public void write(
        Locale lo,
        String logCode,
        byte[] embeds,
        Throwable oException
    ){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, convertStringArray(embeds), oException);
    }
    
    // Logger��JavaDoc
    public void write(
        Locale lo,
        String logCode,
        short[] embeds,
        Throwable oException
    ){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, convertStringArray(embeds), oException);
    }
    
    // Logger��JavaDoc
    public void write(
        Locale lo,
        String logCode,
        char[] embeds,
        Throwable oException
    ){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, convertStringArray(embeds), oException);
    }
    
    // Logger��JavaDoc
    public void write(
        Locale lo,
        String logCode,
        int[] embeds,
        Throwable oException
    ){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, convertStringArray(embeds), oException);
    }
    
    // Logger��JavaDoc
    public void write(
        Locale lo,
        String logCode,
        long[] embeds,
        Throwable oException
    ){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, convertStringArray(embeds), oException);
    }
    
    // Logger��JavaDoc
    public void write(
        Locale lo,
        String logCode,
        float[] embeds,
        Throwable oException
    ){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, convertStringArray(embeds), oException);
    }
    
    // Logger��JavaDoc
    public void write(
        Locale lo,
        String logCode,
        double[] embeds,
        Throwable oException
    ){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, convertStringArray(embeds), oException);
    }
    
    // Logger��JavaDoc
    public void write(
        Locale lo,
        String logCode,
        boolean[] embeds,
        Throwable oException
    ){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, convertStringArray(embeds), oException);
    }
    
    // Logger��JavaDoc
    public void write(AppException e){
        write(Locale.getDefault(), e);
    }
    
    // Logger��JavaDoc
    public void write(Locale lo, AppException e) {
        final MessageRecord  tmp = (MessageRecord)e.getMessageRecord();
        LogMessageRecord  messageRecord = null;
        if(tmp instanceof LogMessageRecord){
            messageRecord = (LogMessageRecord)tmp;
        }else{
            // TODO �ǂ�����H
        }
        if(messageRecord == null || !isWrite(messageRecord)){
            return;
        }
        write(messageRecord, lo, (String)null, e);
    }
    
    // Logger��JavaDoc
    public boolean isWrite(String logCode){
        final LogMessageRecord  messageRecord
             = (LogMessageRecord)messageFactory.findMessageRecord(logCode);
        if(messageRecord == null || !isWrite(messageRecord)){
            return false;
        }
        return true;
    }
    
    // Logger��JavaDoc
    public boolean isDebugWrite(){
        return isDebugEnabled;
    }
    
    /**
     * ��O�̃X�^�b�N�g���[�X��������擾����B<p>
     *
     * @param e ��O
     * @return �X�^�b�N�g���[�X������
     */
    protected static String getStackTraceString(Throwable e){
        final StringBuffer buf = new StringBuffer();
        buf.append(e).append(LINE_SEP);
        final StackTraceElement[] elemss = e.getStackTrace();
        if(elemss != null){
            for(int i = 0, max = elemss.length; i < max; i++){
                buf.append('\t');
                buf.append(elemss[i]);
                if(i != max - 1){
                    buf.append(LINE_SEP);
                }
            }
        }
        for(Throwable ee = getCause(e); ee != null; ee = getCause(ee)){
            buf.append(LINE_SEP).append(MSG_CAUSE)
                .append(ee).append(LINE_SEP);
            final StackTraceElement[] elems = ee.getStackTrace();
            if(elems != null){
                for(int i = 0, max = elems.length; i < max; i++){
                    buf.append('\t');
                    buf.append(elems[i]);
                    if(i != max - 1){
                        buf.append(LINE_SEP);
                    }
                }
            }
        }
        return buf.toString();
    }
    
    private static Throwable getCause(Throwable th){
        Throwable cause = null;
        String thClassName = th.getClass().getName();
        if(thClassName.equals(SERVLET_EXCEPTION_NAME)){
            // ��O��ServletException�̏ꍇ�́A���[�g�̌������擾
            try{
                cause = (Throwable)th.getClass()
                    .getMethod(GET_ROOT_CAUSE_METHOD, (Class[])null).invoke(th, (Object[])null);
            }catch(NoSuchMethodException e){
            }catch(IllegalAccessException e){
            }catch(java.lang.reflect.InvocationTargetException e){
            }
        }else if(thClassName.equals(JMS_EXCEPTION_NAME)){
            // ��O��JMSException�̏ꍇ�́A�����N��O���擾
            try{
                cause = (Exception)th.getClass()
                    .getMethod(GET_LINKED_EXCEPTION_METHOD, (Class[])null).invoke(th, (Object[])null);
            }catch(NoSuchMethodException e){
            }catch(IllegalAccessException e){
            }catch(java.lang.reflect.InvocationTargetException e){
            }
        }else{
            cause = th.getCause();
        }
        return cause == th ? null : cause;
    }
    
    /**
     * �w��J�e�S���̗D�揇�ʔ͈̗͂L��/������ݒ肷��B<p>
     *
     * @param categoryName �J�e�S����
     * @param isEnabled �L���ɂ���ꍇ true
     */
    protected void setEnabled(
        String categoryName,
        boolean isEnabled
    ){
        final LogCategory category = getCategoryService(categoryName);
        if(category == null){
            return;
        }
        category.setEnabled(isEnabled);
    }
    
    /**
     * �w�肳�ꂽ�J�e�S���̗D�揇�ʔ͈͂��L�����������𒲂ׂ�B<p>
     *
     * @param categoryName �J�e�S����
     * @param defaultEnabled �J�e�S�������݂��Ȃ��ꍇ�̖߂�l
     */
    protected boolean isEnabled(
        String categoryName,
        boolean defaultEnabled
    ){
        final LogCategory category = getCategoryService(categoryName);
        if(category == null){
            return defaultEnabled;
        }
        return category.isEnabled();
    }
    
    // LogServiceMBean��JavaDoc
    public void setDebugEnabled(boolean isEnabled){
        isDebugEnabled = isEnabled;
        setEnabled(
            DEBUG_METHOD_CATEGORY,
            isEnabled
        );
    }
    
    // LogServiceMBean��JavaDoc
    public boolean isDebugEnabled(){
        return isEnabled(
            DEBUG_METHOD_CATEGORY,
            isDebugEnabled
        );
    }
    
    // LogServiceMBean��JavaDoc
    public void setSystemDebugEnabled(boolean isEnabled){
        isSystemDebugEnabled = isEnabled;
        setEnabled(
            SYSTEM_DEBUG_CATEGORY,
            isEnabled
        );
    }
    
    // LogServiceMBean��JavaDoc
    public boolean isSystemDebugEnabled(){
        return isEnabled(
            SYSTEM_DEBUG_CATEGORY,
            isSystemDebugEnabled
        );
    }
    
    // LogServiceMBean��JavaDoc
    public void setSystemInfoEnabled(boolean isEnabled){
        isSystemInfoEnabled = isEnabled;
        setEnabled(
            SYSTEM_INFO_CATEGORY,
            isEnabled
        );
    }
    
    // LogServiceMBean��JavaDoc
    public boolean isSystemInfoEnabled(){
        return isEnabled(
            SYSTEM_INFO_CATEGORY,
            isSystemInfoEnabled
        );
    }
    
    // LogServiceMBean��JavaDoc
    public void setSystemWarnEnabled(boolean isEnabled){
        isSystemWarnEnabled = isEnabled;
        setEnabled(
            SYSTEM_WARN_CATEGORY,
            isEnabled
        );
    }
    
    // LogServiceMBean��JavaDoc
    public boolean isSystemWarnEnabled(){
        return isEnabled(
            SYSTEM_WARN_CATEGORY,
            isSystemWarnEnabled
        );
    }
    
    // LogServiceMBean��JavaDoc
    public void setSystemErrorEnabled(boolean isEnabled){
        isSystemErrorEnabled = isEnabled;
        setEnabled(
            SYSTEM_ERROR_CATEGORY,
            isEnabled
        );
    }
    
    // LogServiceMBean��JavaDoc
    public boolean isSystemErrorEnabled(){
        return isEnabled(
            SYSTEM_ERROR_CATEGORY,
            isSystemErrorEnabled
        );
    }
    
    // LogServiceMBean��JavaDoc
    public void setSystemFatalEnabled(boolean isEnabled){
        isSystemFatalEnabled = isEnabled;
        setEnabled(
            SYSTEM_FATAL_CATEGORY,
            isEnabled
        );
    }
    
    // LogServiceMBean��JavaDoc
    public boolean isSystemFatalEnabled(){
        return isEnabled(
            SYSTEM_FATAL_CATEGORY,
            isSystemFatalEnabled
        );
    }
    
    // LogServiceMBean��JavaDoc
    public void setDebugMessageWriterServiceName(ServiceName name){
        debugMessageWriterServiceName = name;
    }
    
    // LogServiceMBean��JavaDoc
    public ServiceName getDebugMessageWriterServiceName(){
        return debugMessageWriterServiceName;
    }
    
    // LogServiceMBean��JavaDoc
    public void setSystemDebugMessageWriterServiceName(ServiceName name){
        systemDebugMessageWriterServiceName = name;
    }
    
    // LogServiceMBean��JavaDoc
    public ServiceName getSystemDebugMessageWriterServiceName(){
        return systemDebugMessageWriterServiceName;
    }
    
    // LogServiceMBean��JavaDoc
    public void setSystemInfoMessageWriterServiceName(ServiceName name){
        systemInfoMessageWriterServiceName = name;
    }
    
    // LogServiceMBean��JavaDoc
    public ServiceName getSystemInfoMessageWriterServiceName(){
        return systemInfoMessageWriterServiceName;
    }
    
    // LogServiceMBean��JavaDoc
    public void setSystemWarnMessageWriterServiceName(ServiceName name){
        systemWarnMessageWriterServiceName = name;
    }
    
    // LogServiceMBean��JavaDoc
    public ServiceName getSystemWarnMessageWriterServiceName(){
        return systemWarnMessageWriterServiceName;
    }
    
    // LogServiceMBean��JavaDoc
    public void setSystemErrorMessageWriterServiceName(ServiceName name){
        systemErrorMessageWriterServiceName = name;
    }
    
    // LogServiceMBean��JavaDoc
    public ServiceName getSystemErrorMessageWriterServiceName(){
        return systemErrorMessageWriterServiceName;
    }
    
    // LogServiceMBean��JavaDoc
    public void setSystemFatalMessageWriterServiceName(ServiceName name){
        systemFatalMessageWriterServiceName = name;
    }
    
    // LogServiceMBean��JavaDoc
    public ServiceName getSystemFatalMessageWriterServiceName(){
        return systemFatalMessageWriterServiceName;
    }
    
    // LogServiceMBean��JavaDoc
    public void setDebugWritableRecordFactoryServiceName(
        ServiceName name
    ){
        debugRecordFactoryServiceName = name;
    }
    
    // LogServiceMBean��JavaDoc
    public ServiceName getDebugWritableRecordFactoryServiceName(){
        return debugRecordFactoryServiceName;
    }
    
    // LogServiceMBean��JavaDoc
    public void setSystemDebugWritableRecordFactoryServiceName(
        ServiceName name
    ){
        systemDebugRecordFactoryServiceName = name;
    }
    
    // LogServiceMBean��JavaDoc
    public ServiceName getSystemDebugWritableRecordFactoryServiceName(){
        return systemDebugRecordFactoryServiceName;
    }
    
    // LogServiceMBean��JavaDoc
    public void setSystemInfoWritableRecordFactoryServiceName(ServiceName name){
        systemInfoRecordFactoryServiceName = name;
    }
    
    // LogServiceMBean��JavaDoc
    public ServiceName getSystemInfoWritableRecordFactoryServiceName(){
        return systemInfoRecordFactoryServiceName;
    }
    
    // LogServiceMBean��JavaDoc
    public void setSystemWarnWritableRecordFactoryServiceName(ServiceName name){
        systemWarnRecordFactoryServiceName = name;
    }
    
    // LogServiceMBean��JavaDoc
    public ServiceName getSystemWarnWritableRecordFactoryServiceName(){
        return systemWarnRecordFactoryServiceName;
    }
    
    // LogServiceMBean��JavaDoc
    public void setSystemErrorWritableRecordFactoryServiceName(
        ServiceName name
    ){
        systemErrorRecordFactoryServiceName = name;
    }
    
    // LogServiceMBean��JavaDoc
    public ServiceName getSystemErrorWritableRecordFactoryServiceName(){
        return systemErrorRecordFactoryServiceName;
    }
    
    // LogServiceMBean��JavaDoc
    public void setSystemFatalWritableRecordFactoryServiceName(
        ServiceName name
    ){
        systemFatalRecordFactoryServiceName = name;
    }
    
    // LogServiceMBean��JavaDoc
    public ServiceName getSystemFatalWritableRecordFactoryServiceName(){
        return systemFatalRecordFactoryServiceName;
    }
    
    // LogServiceMBean��JavaDoc
    public void setDaemon(boolean isDaemon){
        this.isDaemon = isDaemon;
    }
    // LogServiceMBean��JavaDoc
    public boolean isDaemon(){
        return isDaemon;
    }
}
