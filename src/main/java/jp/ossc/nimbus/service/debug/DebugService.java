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
// インポート
package jp.ossc.nimbus.service.debug;

import  jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.lang.ServiceException;
import jp.ossc.nimbus.service.journal.JournalEditor;
import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;
import jp.ossc.nimbus.service.log.Logger;

/**
 * Debugクラス<p>
 * Debug出力を行う
 * @version $Name:  $
 * @author K.Nagai
 * @since 1.0
 */
public class DebugService extends ServiceBase 
implements Debug , DebugServiceMBean
{
    
    private static final long serialVersionUID = -2298230444173627934L;
    
    //エラーメッセージ
	/**デフォルトセパレタ*/
	private final static String DEFAULT_SEPARATOR= System.getProperty("line.separator");
	/**isXXXでチェック前にWriteを行おうとした*/
	private final static String ERROR_USE_OF_DEBUG_MSG="write() method shoud be called after using isXXX() debug-level check function.";
	/**デフォルトネストレベル*/
	private final static int    DEFAULT_NESTED_LEVEL=2;
	/**@文字列を置き換える文字(デフォルト)*/
	private final static char ATMARK_REPLACE_CHAR='$';
	
	//isXXXが呼ばれた後に設定される内容
    final static String ENTRY_STATE_START="1";
    //メソッド使用状況管理用スレッドローカル変数
    static ThreadLocal mThreadLocal = new ThreadLocal();
    
    /**デバッグレベル*/
    private int mDebugLevel;
    /**ネストレベル*/
    private int mNestedLevel;
    /**
     * {@link jp.ossc.nimbus.service.log.Logger Logger}サービス名。<p>
     */
	private ServiceName logServiceName;
	/**
	 * EditorFinder名
	 */
	private ServiceName editorFinderServiceName;
    /**
     * {@link jp.ossc.nimbus.service.log.Logger Logger}サービス実体。<p>
     */
	private Logger mLogService;
	/**
	 * JournalEditorFinderサービス
	 */
	private EditorFinder mEditorFinder;
	/**
     * トレースパラメタのセパレタ。<p>
     */
	private String separator;

	/**
	 * コンストラクタ
	 *
	 */
	public DebugService() {
	    //デフォルトでは出力なし
	    mDebugLevel = DEBUG_LEVEL_NOOUTPUT;
	    mNestedLevel = DEFAULT_NESTED_LEVEL;
		separator   = DEFAULT_SEPARATOR;
	}
	//#####AP向けメソッド#####
	/* (非 Javadoc)
     * @see jp.ossc.nimbus.service.debug.Debug#write(java.lang.String, java.lang.Throwable)
     */
    public void write(String str, Throwable e) {
	    //XXXが呼ばれる前にコールされたらException
	    if( !isFlagSetted() ){
	        throw new ServiceException("Tracer00001",ERROR_USE_OF_DEBUG_MSG);
	    }
	    clearFlag();
	    final String stackTrace = getCallerInfo();
        mLogService.write(DEBUG_DEBUG_WRITE_KEY1,new String[]{stackTrace,str},e);
    }

    /* (非 Javadoc)
     * @see jp.ossc.nimbus.service.debug.Debug#write(java.lang.String)
     */
    public void write(String str) {
	    //XXXが呼ばれる前にコールされたらException
	    if( !isFlagSetted() ){
	        throw new ServiceException("Debug00002",ERROR_USE_OF_DEBUG_MSG);
	    }
	    clearFlag();
	    final String stackTrace = getCallerInfo();
	    mLogService.write(DEBUG_DEBUG_WRITE_KEY2,new String[]{stackTrace,str});
    }

    /* (非 Javadoc)
     * @see jp.ossc.nimbus.service.debug.Debug#isDebug()
     */
    public boolean isDebug() {
        boolean b = mDebugLevel <= DEBUG_LEVEL_DEBUG ;                
        if( b ) setFlag();
        return b;
    }

    /* (非 Javadoc)
     * @see jp.ossc.nimbus.service.debug.Debug#isInfo()
     */
    public boolean isInfo() {
        boolean b = mDebugLevel <= DEBUG_LEVEL_INFO;
        if( b ) setFlag();
        return b;
    }

    /* (非 Javadoc)
     * @see jp.ossc.nimbus.service.debug.Debug#isWarn()
     */
    public boolean isWarn() {
        boolean b = mDebugLevel <= DEBUG_LEVEL_WARN;    
        if( b ) setFlag();
        return b;
    }

    /* (非 Javadoc)
     * @see jp.ossc.nimbus.service.debug.Debug#isError()
     */
    public boolean isError() {
        boolean b = mDebugLevel <= DEBUG_LEVEL_ERROR;        
    	if( b ) setFlag();
    	return b;
     }

    /* (非 Javadoc)
     * @see jp.ossc.nimbus.service.debug.Debug#isFatalError()
     */
    public boolean isFatalError() {
        boolean b = mDebugLevel <= DEBUG_LEVEL_FATALERROR;          
        if( b ) setFlag();
    	return b;
    }

    /**
     * EditorFinderを設定する。
     */
	public void setEditorFinder(EditorFinder editorFinder) {
        mEditorFinder = editorFinder;
    }
    
    /**
     * Loggerを設定する。
     */
    public void setLogger(Logger logService) {
        mLogService = logService;
    }
    
    //#####管理用メソッド#####
    /* (非 Javadoc)
     * @see jp.ossc.nimbus.service.debug.DebugServiceMBean#getDebugLevel()
     */
    public int getDebugLevel() {
        return mDebugLevel;
    }
   /* (非 Javadoc)
     * @see jp.ossc.nimbus.service.debug.DebugServiceMBean#setDebugLevel()
     */
    public void setDebugLevel(int level) {
        mDebugLevel = level;
    }
    /* (非 Javadoc)
     * @see jp.ossc.nimbus.service.debug.DebugServiceMBean#getLogServiceName()
     */
    public ServiceName getLogServiceName() {
        return logServiceName;
    }
    /* (非 Javadoc)
     * @see jp.ossc.nimbus.service.debug.DebugServiceMBean#setLogServiceName(jp.ossc.nimbus.core.ServiceName)
     */
    public void setLogServiceName(ServiceName svn) {
        logServiceName = svn;
    }
   
	//#####内部動作用メソッド#####
    /**
     * setFlag<p>
     * スレッドローカル変数にisXXXが呼ばれたことを記録する
     */
    private void setFlag(){
        mThreadLocal.set(ENTRY_STATE_START);
    }
    /**
     * clearFlag<p>
     * writeが呼ばれたのでスレッドローカル変数より状態を削除
     */
    private void clearFlag(){
        mThreadLocal.set(null); 
    }
    /**
     * clearFlag<p>
     * isXXXが呼ばれたかどうか
     */
    private boolean isFlagSetted(){
        return mThreadLocal.get() != null;
    }

    //#####サービス初期化～破棄#####
    /* (非 Javadoc)
     * @see jp.ossc.nimbus.core.ServiceBaseSupport#createService()
     */
    public void createService() throws Exception {
    }
    /* (非 Javadoc)
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
    /* (非 Javadoc)
     * @see jp.ossc.nimbus.core.ServiceBaseSupport#stopService()
     */
    public void stopService() throws Exception {
    }
    /* (非 Javadoc)
     * @see jp.ossc.nimbus.core.ServiceBaseSupport#destroyService()
     */
    public void destroyService() throws Exception {
        mLogService = null;
        logServiceName = null;
        mEditorFinder = null;
        editorFinderServiceName = null;
    }
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.trace.TraceServiceMBean#setNestedLevel(int)
	 */
	public void setNestedLevel(int level) {
		this.mNestedLevel = level;		
	}
	/**
     * 呼び出し元関数名とステップ番号取得。<p>
     * このメソッドには、以下の実装が行われている。<br>
     * <ol>
     *   <li>例外を作成</li>
     *   <li>例外のスタックトレースの2行目の情報より、呼び出し元メソッド名とステップ番号を取得</li>
     * </ol>
     * 
     * @exception Exception 生成処理に失敗した場合。
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
	/* (非 Javadoc)
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
	/* (非 Javadoc)
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
	/* (非 Javadoc)
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
	/* (非 Javadoc)
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
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.debug.DebugServiceMBean#setEditorFinderServiceName(jp.ossc.nimbus.core.ServiceName)
	 */
	public void setEditorFinderServiceName(ServiceName name) {
		editorFinderServiceName = name;
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.debug.DebugServiceMBean#getEditorFinderServiceName()
	 */
	public ServiceName getEditorFinderServiceName() {
		return editorFinderServiceName;
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.trace.TraceServiceMBean#setSeparator(java.lang.String)
	 */
	public void setSeparator(String sep) {
		this.separator = sep;
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.trace.TraceServiceMBean#setSeparator(java.lang.String)
	 */
	public String getSeparator() {
		return separator;
	}

	//表示用ヘルパ関数
	/**
     * Object→String変換を行う。<p>
     * このメソッドには、以下の実装が行われている。<br>
     * <ol>
     *   <li>オブジェクトを取り出す</li>
     *   <li>getParameterStringを呼び出しStringに変換する</li>
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
     * Object→String変換を行う。<p>
     * このメソッドには、以下の実装が行われている。<br>
     * <ol>
     *   <li>対応するエディタを取得する</li>
     *   <li>エディタを使用してStringに変換する</li>
      * </ol>
     * 
     */
	private String getParameterString(Object param){
		if( param != null ) {
			//対応するエディタを取得して文字列に変換する
			final JournalEditor editor = mEditorFinder.findEditor(param.getClass());
			final String  str = (String) editor.toObject(mEditorFinder,null,param);
			//@変換に引っかからないように$に変換する
			if( str != null ) str.replace('@',ATMARK_REPLACE_CHAR);
			return str;
		} else {
			return "null";
		}		
	}
}
