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
// �C���|�[�g
package jp.ossc.nimbus.service.debug;

import  jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.lang.ServiceException;
import jp.ossc.nimbus.service.journal.JournalEditor;
import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;
import jp.ossc.nimbus.service.log.Logger;

/**
 * Debug�N���X<p>
 * Debug�o�͂��s��
 * @version $Name:  $
 * @author K.Nagai
 * @since 1.0
 */
public class DebugService extends ServiceBase 
implements Debug , DebugServiceMBean
{
    
    private static final long serialVersionUID = -2298230444173627934L;
    
    //�G���[���b�Z�[�W
	/**�f�t�H���g�Z�p���^*/
	private final static String DEFAULT_SEPARATOR= System.getProperty("line.separator");
	/**isXXX�Ń`�F�b�N�O��Write���s�����Ƃ���*/
	private final static String ERROR_USE_OF_DEBUG_MSG="write() method shoud be called after using isXXX() debug-level check function.";
	/**�f�t�H���g�l�X�g���x��*/
	private final static int    DEFAULT_NESTED_LEVEL=2;
	/**@�������u�������镶��(�f�t�H���g)*/
	private final static char ATMARK_REPLACE_CHAR='$';
	
	//isXXX���Ă΂ꂽ��ɐݒ肳�����e
    final static String ENTRY_STATE_START="1";
    //���\�b�h�g�p�󋵊Ǘ��p�X���b�h���[�J���ϐ�
    static ThreadLocal mThreadLocal = new ThreadLocal();
    
    /**�f�o�b�O���x��*/
    private int mDebugLevel;
    /**�l�X�g���x��*/
    private int mNestedLevel;
    /**
     * {@link jp.ossc.nimbus.service.log.Logger Logger}�T�[�r�X���B<p>
     */
	private ServiceName logServiceName;
	/**
	 * EditorFinder��
	 */
	private ServiceName editorFinderServiceName;
    /**
     * {@link jp.ossc.nimbus.service.log.Logger Logger}�T�[�r�X���́B<p>
     */
	private Logger mLogService;
	/**
	 * JournalEditorFinder�T�[�r�X
	 */
	private EditorFinder mEditorFinder;
	/**
     * �g���[�X�p�����^�̃Z�p���^�B<p>
     */
	private String separator;

	/**
	 * �R���X�g���N�^
	 *
	 */
	public DebugService() {
	    //�f�t�H���g�ł͏o�͂Ȃ�
	    mDebugLevel = DEBUG_LEVEL_NOOUTPUT;
	    mNestedLevel = DEFAULT_NESTED_LEVEL;
		separator   = DEFAULT_SEPARATOR;
	}
	//#####AP�������\�b�h#####
	/* (�� Javadoc)
     * @see jp.ossc.nimbus.service.debug.Debug#write(java.lang.String, java.lang.Throwable)
     */
    public void write(String str, Throwable e) {
	    //XXX���Ă΂��O�ɃR�[�����ꂽ��Exception
	    if( !isFlagSetted() ){
	        throw new ServiceException("Tracer00001",ERROR_USE_OF_DEBUG_MSG);
	    }
	    clearFlag();
	    final String stackTrace = getCallerInfo();
        mLogService.write(DEBUG_DEBUG_WRITE_KEY1,new String[]{stackTrace,str},e);
    }

    /* (�� Javadoc)
     * @see jp.ossc.nimbus.service.debug.Debug#write(java.lang.String)
     */
    public void write(String str) {
	    //XXX���Ă΂��O�ɃR�[�����ꂽ��Exception
	    if( !isFlagSetted() ){
	        throw new ServiceException("Debug00002",ERROR_USE_OF_DEBUG_MSG);
	    }
	    clearFlag();
	    final String stackTrace = getCallerInfo();
	    mLogService.write(DEBUG_DEBUG_WRITE_KEY2,new String[]{stackTrace,str});
    }

    /* (�� Javadoc)
     * @see jp.ossc.nimbus.service.debug.Debug#isDebug()
     */
    public boolean isDebug() {
        boolean b = mDebugLevel <= DEBUG_LEVEL_DEBUG ;                
        if( b ) setFlag();
        return b;
    }

    /* (�� Javadoc)
     * @see jp.ossc.nimbus.service.debug.Debug#isInfo()
     */
    public boolean isInfo() {
        boolean b = mDebugLevel <= DEBUG_LEVEL_INFO;
        if( b ) setFlag();
        return b;
    }

    /* (�� Javadoc)
     * @see jp.ossc.nimbus.service.debug.Debug#isWarn()
     */
    public boolean isWarn() {
        boolean b = mDebugLevel <= DEBUG_LEVEL_WARN;    
        if( b ) setFlag();
        return b;
    }

    /* (�� Javadoc)
     * @see jp.ossc.nimbus.service.debug.Debug#isError()
     */
    public boolean isError() {
        boolean b = mDebugLevel <= DEBUG_LEVEL_ERROR;        
    	if( b ) setFlag();
    	return b;
     }

    /* (�� Javadoc)
     * @see jp.ossc.nimbus.service.debug.Debug#isFatalError()
     */
    public boolean isFatalError() {
        boolean b = mDebugLevel <= DEBUG_LEVEL_FATALERROR;          
        if( b ) setFlag();
    	return b;
    }

    /**
     * EditorFinder��ݒ肷��B
     */
	public void setEditorFinder(EditorFinder editorFinder) {
        mEditorFinder = editorFinder;
    }
    
    /**
     * Logger��ݒ肷��B
     */
    public void setLogger(Logger logService) {
        mLogService = logService;
    }
    
    //#####�Ǘ��p���\�b�h#####
    /* (�� Javadoc)
     * @see jp.ossc.nimbus.service.debug.DebugServiceMBean#getDebugLevel()
     */
    public int getDebugLevel() {
        return mDebugLevel;
    }
   /* (�� Javadoc)
     * @see jp.ossc.nimbus.service.debug.DebugServiceMBean#setDebugLevel()
     */
    public void setDebugLevel(int level) {
        mDebugLevel = level;
    }
    /* (�� Javadoc)
     * @see jp.ossc.nimbus.service.debug.DebugServiceMBean#getLogServiceName()
     */
    public ServiceName getLogServiceName() {
        return logServiceName;
    }
    /* (�� Javadoc)
     * @see jp.ossc.nimbus.service.debug.DebugServiceMBean#setLogServiceName(jp.ossc.nimbus.core.ServiceName)
     */
    public void setLogServiceName(ServiceName svn) {
        logServiceName = svn;
    }
   
	//#####��������p���\�b�h#####
    /**
     * setFlag<p>
     * �X���b�h���[�J���ϐ���isXXX���Ă΂ꂽ���Ƃ��L�^����
     */
    private void setFlag(){
        mThreadLocal.set(ENTRY_STATE_START);
    }
    /**
     * clearFlag<p>
     * write���Ă΂ꂽ�̂ŃX���b�h���[�J���ϐ�����Ԃ��폜
     */
    private void clearFlag(){
        mThreadLocal.set(null); 
    }
    /**
     * clearFlag<p>
     * isXXX���Ă΂ꂽ���ǂ���
     */
    private boolean isFlagSetted(){
        return mThreadLocal.get() != null;
    }

    //#####�T�[�r�X�������`�j��#####
    /* (�� Javadoc)
     * @see jp.ossc.nimbus.core.ServiceBaseSupport#createService()
     */
    public void createService() throws Exception {
    }
    /* (�� Javadoc)
     * @see jp.ossc.nimbus.core.ServiceBaseSupport#startService()
     */
    public void startService() throws Exception {
        if(logServiceName != null) {
            mLogService = (Logger) ServiceManagerFactory.getServiceObject(logServiceName);
        }else if(mLogService == null) {
            throw new IllegalArgumentException("LogServiceName or LogService must not null.");
        }
        
        if(editorFinderServiceName != null) {
            mEditorFinder = (EditorFinder) ServiceManagerFactory.getServiceObject(editorFinderServiceName);
        }else if(mEditorFinder == null) {
            throw new IllegalArgumentException("EditorFinderServiceName or EditorFinderService must not null.");
        }
    }
    /* (�� Javadoc)
     * @see jp.ossc.nimbus.core.ServiceBaseSupport#stopService()
     */
    public void stopService() throws Exception {
    }
    /* (�� Javadoc)
     * @see jp.ossc.nimbus.core.ServiceBaseSupport#destroyService()
     */
    public void destroyService() throws Exception {
        mLogService = null;
        logServiceName = null;
        mEditorFinder = null;
        editorFinderServiceName = null;
    }
	/* (�� Javadoc)
	 * @see jp.ossc.nimbus.service.trace.TraceServiceMBean#setNestedLevel(int)
	 */
	public void setNestedLevel(int level) {
		this.mNestedLevel = level;		
	}
	/**
     * �Ăяo�����֐����ƃX�e�b�v�ԍ��擾�B<p>
     * ���̃��\�b�h�ɂ́A�ȉ��̎������s���Ă���B<br>
     * <ol>
     *   <li>��O���쐬</li>
     *   <li>��O�̃X�^�b�N�g���[�X��2�s�ڂ̏����A�Ăяo�������\�b�h���ƃX�e�b�v�ԍ����擾</li>
     * </ol>
     * 
     * @exception Exception ���������Ɏ��s�����ꍇ�B
     */	
	private String getCallerInfo(){
		String callerClass = null;
		final Exception e = new Exception();
		final StackTraceElement[] elms = e.getStackTrace();
		if( elms.length < mNestedLevel ){
			mLogService.write(DEBUG_NESTLEVEL_ERR_KEY,mNestedLevel);
		} else {
			final StackTraceElement elm = elms[mNestedLevel];
			callerClass = elm.toString(); 
		}
		return callerClass;
	}
	/* (�� Javadoc)
	 * @see jp.ossc.nimbus.service.debug.Debug#dump(java.lang.Object)
	 */
	public void dump(Object object) {
	    if( !isFlagSetted() ){
	        throw new ServiceException("Debug00002",ERROR_USE_OF_DEBUG_MSG);
	    }
	    clearFlag();
		final String param = getParameterString(object);
		final String callerInfo = getCallerInfo();
		mLogService.write(DEBUG_DEBUG_DUMP_KEY1,new String[]{callerInfo,param});
	}
	/* (�� Javadoc)
	 * @see jp.ossc.nimbus.service.debug.Debug#dump(java.lang.Object[])
	 */
	public void dump(Object[] objects) {
	    if( !isFlagSetted() ){
	        throw new ServiceException("Debug00003",ERROR_USE_OF_DEBUG_MSG);
	    }
	    clearFlag();
		final String param = getParameterStrings(objects);
		final String callerInfo = getCallerInfo();
		mLogService.write(DEBUG_DEBUG_DUMP_KEY2,new String[]{callerInfo,param});
	}
	/* (�� Javadoc)
	 * @see jp.ossc.nimbus.service.debug.Debug#dump(java.lang.String, java.lang.Object)
	 */
	public void dump(String msg, Object object) {
	    if( !isFlagSetted() ){
	        throw new ServiceException("Debug00004",ERROR_USE_OF_DEBUG_MSG);
	    }
	    clearFlag();
		final String param = getParameterString(object);
		final String callerInfo = getCallerInfo();
		mLogService.write(DEBUG_DEBUG_MSG_DUMP_KEY1,new String[]{callerInfo,msg,param});
	}
	/* (�� Javadoc)
	 * @see jp.ossc.nimbus.service.debug.Debug#dump(java.lang.String, java.lang.Object[])
	 */
	public void dump(String msg, Object[] objects) {
	    if( !isFlagSetted() ){
	        throw new ServiceException("Debug00005",ERROR_USE_OF_DEBUG_MSG);
	    }
	    clearFlag();
		final String param = getParameterStrings(objects);
		final String callerInfo = getCallerInfo();
		mLogService.write(DEBUG_DEBUG_MSG_DUMP_KEY2,new String[]{callerInfo,msg,param});
	}
	/* (�� Javadoc)
	 * @see jp.ossc.nimbus.service.debug.DebugServiceMBean#setEditorFinderServiceName(jp.ossc.nimbus.core.ServiceName)
	 */
	public void setEditorFinderServiceName(ServiceName name) {
		editorFinderServiceName = name;
	}
	/* (�� Javadoc)
	 * @see jp.ossc.nimbus.service.debug.DebugServiceMBean#getEditorFinderServiceName()
	 */
	public ServiceName getEditorFinderServiceName() {
		return editorFinderServiceName;
	}
	/* (�� Javadoc)
	 * @see jp.ossc.nimbus.service.trace.TraceServiceMBean#setSeparator(java.lang.String)
	 */
	public void setSeparator(String sep) {
		this.separator = sep;
	}
	/* (�� Javadoc)
	 * @see jp.ossc.nimbus.service.trace.TraceServiceMBean#setSeparator(java.lang.String)
	 */
	public String getSeparator() {
		return separator;
	}

	//�\���p�w���p�֐�
	/**
     * Object��String�ϊ����s���B<p>
     * ���̃��\�b�h�ɂ́A�ȉ��̎������s���Ă���B<br>
     * <ol>
     *   <li>�I�u�W�F�N�g�����o��</li>
     *   <li>getParameterString���Ăяo��String�ɕϊ�����</li>
      * </ol>
     * 
     */
	private String getParameterStrings(Object[] params){
		final StringBuffer buff = new StringBuffer();
		if( params == null ) {
			buff.append(params);
			return buff.toString();
		}
		
		for( int i = 0 ; i < params.length ; i++ ){
			buff.append(getParameterString(params[i]));
			if( i != params.length -1 )
				buff.append(separator);
		}
		return buff.toString();
		
	}
	/**
     * Object��String�ϊ����s���B<p>
     * ���̃��\�b�h�ɂ́A�ȉ��̎������s���Ă���B<br>
     * <ol>
     *   <li>�Ή�����G�f�B�^���擾����</li>
     *   <li>�G�f�B�^���g�p����String�ɕϊ�����</li>
      * </ol>
     * 
     */
	private String getParameterString(Object param){
		if( param != null ) {
			//�Ή�����G�f�B�^���擾���ĕ�����ɕϊ�����
			final JournalEditor editor = mEditorFinder.findEditor(param.getClass());
			final String  str = (String) editor.toObject(mEditorFinder,null,param);
			//@�ϊ��Ɉ���������Ȃ��悤��$�ɕϊ�����
			if( str != null ) str.replace('@',ATMARK_REPLACE_CHAR);
			return str;
		} else {
			return "null";
		}		
	}
}
