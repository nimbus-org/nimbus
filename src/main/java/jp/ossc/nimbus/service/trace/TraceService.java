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
package jp.ossc.nimbus.service.trace;
//�C���|�[�g
import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.daemon.Daemon;
import jp.ossc.nimbus.lang.ServiceException;
import jp.ossc.nimbus.service.journal.JournalEditor;
import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;
import jp.ossc.nimbus.service.log.Logger;
/**
 * �֐��̓��o�͂��g���[�X����T�[�r�X�N���X<p>
 * @version $Name:  $
 * @author K.Nagai
 * @since 1.0
 */
public class TraceService extends ServiceBase implements TraceServiceMBean , Tracer {
    
    private static final long serialVersionUID = 7534818369420794264L;
    
	/**�f�t�H���g�Z�p���^*/
	private final static String DEFAULT_SEPARATOR= System.getProperty("line.separator");
	/**�f�t�H���g�l�X�g���x��*/
	private final static int    DEFAULT_NESTED_LEVEL=2;
	/**��ԊǗ��pHashMap�Ɋ֐��d�l��Ԃ��i�[����ׂ̃L�[*/
	private final static String ENTRY_STATE_START="1";
	/**@�������u�������镶��(�f�t�H���g)*/
	private final static char ATMARK_REPLACE_CHAR='$';
	
	//�G���[���b�Z�[�W
	/***/
	private final static String ERROR_USE_OF_TRACE_MSG="entry/exit method must be called after using isXXX() trace-level check function.";
    /**
     * �g���[�X���x���B<p>
     */
	private int traceLevel;
    /**
     * �g���[�X�p�����^�̃Z�p���^�B<p>
     */
	private String separator;
    /**
     * �Ăяo�����擾���̃l�X�g���x���B<p>
     */
	private int nestedLevel;

    /**
     * {@link jp.ossc.nimbus.service.log.LogService LogService}�T�[�r�X���B<p>
     */
	private ServiceName logServiceName;
	
    /**
     * {@link EditorFinder}�T�[�r�X���B<p>
     */
	private ServiceName editorFinderServiceName;
	
    /**
     * {@link jp.ossc.nimbus.service.log.LogService LogService}�T�[�r�X���́B<p>
     */
	private Logger mLogService;
	
    /**
     * {@link EditorFinder}�T�[�r�X���́B<p>
     */
	private EditorFinder mEditorFinderService;

	/**
	 * �g�p������ԊǗ�
	 */
	private static ThreadLocal thLocal = new ThreadLocal();
	
	/**
	 * �R���X�g���N�^
	 *
	 */
	public TraceService() {
		separator   = DEFAULT_SEPARATOR;
		nestedLevel = DEFAULT_NESTED_LEVEL;
		traceLevel  = DISABLE_LEVEL;
	}
	
	//#####�Z�b�^�[�E�Q�b�^�[#####
	/* (�� Javadoc)
	 * @see jp.ossc.nimbus.service.trace.TraceServiceMBean#getLogServiceName()
	 */
	public ServiceName getLogServiceName() {
		return logServiceName;
	}

	/* (�� Javadoc)
	 * @see jp.ossc.nimbus.service.trace.TraceServiceMBean#setLogServiceName(jp.ossc.nimbus.core.ServiceName)
	 */
	public void setLogServiceName(ServiceName name) {
		logServiceName = name;		
	}

	/* (�� Javadoc)
	 * @see jp.ossc.nimbus.service.trace.TraceServiceMBean#getEditorFinderServiceName()
	 */
	public ServiceName getEditorFinderServiceName() {
		return editorFinderServiceName;
	}

	/* (�� Javadoc)
	 * @see jp.ossc.nimbus.service.trace.TraceServiceMBean#setEditorFinderServiceName(jp.ossc.nimbus.core.ServiceName)
	 */
	public void setEditorFinderServiceName(ServiceName name) {
		editorFinderServiceName = name;
	}

	/* (�� Javadoc)
	 * @see jp.ossc.nimbus.service.trace.TraceServiceMBean#setTraceLevel(int)
	 */
	public void setTraceLevel(int level) {
		traceLevel = level;
	}
	/* (�� Javadoc)
	 * @see jp.ossc.nimbus.service.trace.TraceServiceMBean#setNestedLevel(int)
	 */
	public void setNestedLevel(int level) {
		this.nestedLevel = level;		
	}

	/* (�� Javadoc)
	 * @see jp.ossc.nimbus.service.trace.TraceServiceMBean#setSeparator(java.lang.String)
	 */
	public void setSeparator(String sep) {
		this.separator = sep;
	}
	
	public void setEditorFinder(EditorFinder editorFinder) {
        mEditorFinderService = editorFinder;
    }

    public void setLogger(Logger logger) {
        mLogService = logger;
    }

    //#####�T�[�r�X�������`�j��#####
    /**
     * �����������s���B<p>
     * ���̃��\�b�h�ɂ́A�ȉ��̎������s���Ă���B<br>
     * <ol>
     *   <li>{@link Daemon}�C���X�^���X�𐶐�����B</li>
     * </ol>
     * 
     * @exception Exception ���������Ɏ��s�����ꍇ�B
     */
	public void createService() throws Exception {	    
	}
    /**
     * �J�n�������s���B<p>
     * ���̃��\�b�h�ɂ́A�ȉ��̎������s���Ă���B<br>
     * <ol>
     *   <li>{@link jp.ossc.nimbus.service.log.LogService LogService}�T�[�r�X���擾�B</li>
     *   <li>{@link EditorFinder}�T�[�r�X���擾�B</li>
     * </ol>
     * 
     * @exception Exception ���������Ɏ��s�����ꍇ�B
     */
	public void startService() throws Exception{
        if(logServiceName != null) {
            mLogService = (Logger) ServiceManagerFactory.getServiceObject(logServiceName);
        }
		if(mLogService == null){
            throw new IllegalArgumentException("Cannot resolve LogService.");
		}
        if(editorFinderServiceName != null) {
            mEditorFinderService = (EditorFinder) ServiceManagerFactory.getServiceObject(editorFinderServiceName);
        }
        if(mEditorFinderService == null){
            throw new IllegalArgumentException("Cannot resolve EditorFinderService.");			
		}
	}
    /**
     * ��~�������s���B<p>
     * ���̃��\�b�h�ɂ́A�ȉ��̎������s���Ă���B<br>
     * <ol>
     *   <li>�T�[�r�X���̕ϐ�������������B</li>
     * </ol>
     * 
     * @exception Exception ���������Ɏ��s�����ꍇ�B
     */
	public void stopService() throws Exception {	
		mLogService = null;
		mEditorFinderService = null;
	}
    /**
     * �j���������s���B<p>
     * ���̃��\�b�h�ɂ́A�ȉ��̎������s���Ă���B<br>
     * <ol>
     *   <li>�T�[�r�X����������������B</li>
     * </ol>
     * 
     * @exception Exception ���������Ɏ��s�����ꍇ�B
     */
	public void destroyService() throws Exception {	
		loggerServiceName = null;
		editorFinderServiceName = null;
	}
	
	
	//#####AP�������\�b�h#####
	/**
     * �g���[�X�擾�J�n�v����t���s���B<p>
     * ���̃��\�b�h�ɂ́A�ȉ��̎������s���Ă���B<br>
     * <ol>
     *   <li>�Ή�����p�����^��String�ɕϊ�����</li>
     *   <li>�Ăяo�����̊֐��ƃ��C���ԍ����擾����</li>
     *   <li>���O�ɏ����o�����s��</li>
     * </ol>
     * 
     */
	public void entry(Object[] params){
	    //�G���g���J�n�t���O���L�^
	    //XXX���Ă΂��O�ɃR�[�����ꂽ��Exception
	    if( !isFlagSetted() ){
	        throw new ServiceException("Tracer00001",ERROR_USE_OF_TRACE_MSG);
	    }
	    clearFlag();
		final String param = getParameterStrings(params);
		final String callerInfo = getCallerInfo();
		mLogService.write(TRACE_ENTRY_KEY,new String[]{callerInfo,param});
	}
	/**
     * �g���[�X�擾�I���v����t���s���B<p>
     * ���̃��\�b�h�ɂ́A�ȉ��̎������s���Ă���B<br>
     * <ol>
     *   <li>�Ή�����p�����^��String�ɕϊ�����</li>
     *   <li>�Ăяo�����̊֐��ƃ��C���ԍ����擾����</li>
     *   <li>���O�ɏ����o�����s��</li>
     * </ol>
     * 
     */
	public void exit(Object[] params) {
	    //XXX���Ă΂��O�ɃR�[�����ꂽ��Exception
	    if( !isFlagSetted() ){
	        throw new ServiceException("Tracer00001",ERROR_USE_OF_TRACE_MSG);
	    }
	    clearFlag();
		final String param = getParameterStrings(params);
		final String callerInfo = getCallerInfo();
		mLogService.write(TRACE_EXIT_KEY,new String[]{callerInfo,param});
	}

	//#####�\�����x���₢���킹#####
	/* (�� Javadoc)
	 * @see jp.ossc.nimbus.service.trace.Tracer#isPublic()
	 */
	public boolean isPublic() {
	    boolean b =  traceLevel <= PUBLIC_LEVEL ;
	    if( b ) setFlag();
	    return b;
	}

	/* (�� Javadoc)
	 * @see jp.ossc.nimbus.service.trace.Tracer#isProtected()
	 */
	public boolean isProtected() {
		boolean b =  traceLevel <= PROTECTED_LEVEL;
	    if( b ) setFlag();
	    return b;
	}

	/* (�� Javadoc)
	 * @see jp.ossc.nimbus.service.trace.Tracer#isPrivate()
	 */
	public boolean isPrivate() {
	    boolean b =  traceLevel <= PRIVATE_LEVEL;
	    if( b ) setFlag();
	    return b;
	}
	//#####�����Ǘ��p#####
	/**
	 * clearFlag
	 * �X���b�h���[�J���ϐ��ɋL�^���ꂽ�t���O���N���A����
	 */
	protected void clearFlag(){
	    thLocal.set(null);
	}
	/**
	 * isFlagCleared
	 * @return �N���A�ς�
	 */
	protected boolean isFlagSetted(){
	    return thLocal.get() != null;
	}
	/**
	 * �g���[�X�G���g����Ԃł��邱�Ƃ�ݒ肷��
	 *  Exit�̍ۂɃt���O���N���A����Ă��Ȃ���Exception����������
	 */
	protected void setFlag(){
	    thLocal.set(ENTRY_STATE_START);
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
		if( elms.length < nestedLevel ){
			mLogService.write(TRACE_NESTLEVEL_ERR,nestedLevel);
		} else {
			final StackTraceElement elm = elms[nestedLevel];
			callerClass = elm.toString(); 
		}
		return callerClass;
	}
	//�\���p�w���p�֐�
	/**
     * Object��String�ϊ����s���B<p>
     * ���̃��\�b�h�ɂ́A�ȉ��̎������s���Ă���B<br>
     * <ol>
     *   <li>�Ή�����G�f�B�^���擾����</li>
     *   <li>�Ăяo�����̊֐��ƃ��C���ԍ����擾����</li>
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
			Object param = params[i];
			if( param != null ) {
				//�Ή�����G�f�B�^���擾���ĕ�����ɕϊ�����
				final JournalEditor editor = mEditorFinderService.findEditor(param.getClass());
				String str =(String) editor.toObject(mEditorFinderService,null,param) ;
				//@�ϊ��Ɉ���������Ȃ��悤��$�ɕϊ�����
				if( str != null ) str.replace('@',ATMARK_REPLACE_CHAR);
				buff.append(str);
				if( i != params.length -1 )
					buff.append(separator);
			} else {
				buff.append(param);
			}
			if( i != params.length -1 )
				buff.append(separator);
		}
		return buff.toString();
		
	}
}
