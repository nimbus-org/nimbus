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
package jp.ossc.nimbus.service.trace;
//インポート
import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.daemon.Daemon;
import jp.ossc.nimbus.lang.ServiceException;
import jp.ossc.nimbus.service.journal.JournalEditor;
import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;
import jp.ossc.nimbus.service.log.Logger;
/**
 * 関数の入出力をトレースするサービスクラス<p>
 * @version $Name:  $
 * @author K.Nagai
 * @since 1.0
 */
public class TraceService extends ServiceBase implements TraceServiceMBean , Tracer {
    
    private static final long serialVersionUID = 7534818369420794264L;
    
	/**デフォルトセパレタ*/
	private final static String DEFAULT_SEPARATOR= System.getProperty("line.separator");
	/**デフォルトネストレベル*/
	private final static int    DEFAULT_NESTED_LEVEL=2;
	/**状態管理用HashMapに関数仕様状態を格納する為のキー*/
	private final static String ENTRY_STATE_START="1";
	/**@文字列を置き換える文字(デフォルト)*/
	private final static char ATMARK_REPLACE_CHAR='$';
	
	//エラーメッセージ
	/***/
	private final static String ERROR_USE_OF_TRACE_MSG="entry/exit method must be called after using isXXX() trace-level check function.";
    /**
     * トレースレベル。<p>
     */
	private int traceLevel;
    /**
     * トレースパラメタのセパレタ。<p>
     */
	private String separator;
    /**
     * 呼び出し元取得時のネストレベル。<p>
     */
	private int nestedLevel;

    /**
     * {@link jp.ossc.nimbus.service.log.LogService LogService}サービス名。<p>
     */
	private ServiceName logServiceName;
	
    /**
     * {@link EditorFinder}サービス名。<p>
     */
	private ServiceName editorFinderServiceName;
	
    /**
     * {@link jp.ossc.nimbus.service.log.LogService LogService}サービス実体。<p>
     */
	private Logger mLogService;
	
    /**
     * {@link EditorFinder}サービス実体。<p>
     */
	private EditorFinder mEditorFinderService;

	/**
	 * 使用した状態管理
	 */
	private static ThreadLocal thLocal = new ThreadLocal();
	
	/**
	 * コンストラクタ
	 *
	 */
	public TraceService() {
		separator   = DEFAULT_SEPARATOR;
		nestedLevel = DEFAULT_NESTED_LEVEL;
		traceLevel  = DISABLE_LEVEL;
	}
	
	//#####セッター・ゲッター#####
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.trace.TraceServiceMBean#getLogServiceName()
	 */
	public ServiceName getLogServiceName() {
		return logServiceName;
	}

	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.trace.TraceServiceMBean#setLogServiceName(jp.ossc.nimbus.core.ServiceName)
	 */
	public void setLogServiceName(ServiceName name) {
		logServiceName = name;		
	}

	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.trace.TraceServiceMBean#getEditorFinderServiceName()
	 */
	public ServiceName getEditorFinderServiceName() {
		return editorFinderServiceName;
	}

	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.trace.TraceServiceMBean#setEditorFinderServiceName(jp.ossc.nimbus.core.ServiceName)
	 */
	public void setEditorFinderServiceName(ServiceName name) {
		editorFinderServiceName = name;
	}

	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.trace.TraceServiceMBean#setTraceLevel(int)
	 */
	public void setTraceLevel(int level) {
		traceLevel = level;
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.trace.TraceServiceMBean#setNestedLevel(int)
	 */
	public void setNestedLevel(int level) {
		this.nestedLevel = level;		
	}

	/* (非 Javadoc)
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

    //#####サービス初期化～破棄#####
    /**
     * 生成処理を行う。<p>
     * このメソッドには、以下の実装が行われている。<br>
     * <ol>
     *   <li>{@link Daemon}インスタンスを生成する。</li>
     * </ol>
     * 
     * @exception Exception 生成処理に失敗した場合。
     */
	public void createService() throws Exception {	    
	}
    /**
     * 開始処理を行う。<p>
     * このメソッドには、以下の実装が行われている。<br>
     * <ol>
     *   <li>{@link jp.ossc.nimbus.service.log.LogService LogService}サービスを取得。</li>
     *   <li>{@link EditorFinder}サービスを取得。</li>
     * </ol>
     * 
     * @exception Exception 生成処理に失敗した場合。
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
     * 停止処理を行う。<p>
     * このメソッドには、以下の実装が行われている。<br>
     * <ol>
     *   <li>サービス実体変数を初期化する。</li>
     * </ol>
     * 
     * @exception Exception 生成処理に失敗した場合。
     */
	public void stopService() throws Exception {	
		mLogService = null;
		mEditorFinderService = null;
	}
    /**
     * 破棄処理を行う。<p>
     * このメソッドには、以下の実装が行われている。<br>
     * <ol>
     *   <li>サービス属性を初期化する。</li>
     * </ol>
     * 
     * @exception Exception 生成処理に失敗した場合。
     */
	public void destroyService() throws Exception {	
		loggerServiceName = null;
		editorFinderServiceName = null;
	}
	
	
	//#####AP向けメソッド#####
	/**
     * トレース取得開始要求受付を行う。<p>
     * このメソッドには、以下の実装が行われている。<br>
     * <ol>
     *   <li>対応するパラメタをStringに変換する</li>
     *   <li>呼び出し元の関数とライン番号を取得する</li>
     *   <li>ログに書き出しを行う</li>
     * </ol>
     * 
     */
	public void entry(Object[] params){
	    //エントリ開始フラグを記録
	    //XXXが呼ばれる前にコールされたらException
	    if( !isFlagSetted() ){
	        throw new ServiceException("Tracer00001",ERROR_USE_OF_TRACE_MSG);
	    }
	    clearFlag();
		final String param = getParameterStrings(params);
		final String callerInfo = getCallerInfo();
		mLogService.write(TRACE_ENTRY_KEY,new String[]{callerInfo,param});
	}
	/**
     * トレース取得終了要求受付を行う。<p>
     * このメソッドには、以下の実装が行われている。<br>
     * <ol>
     *   <li>対応するパラメタをStringに変換する</li>
     *   <li>呼び出し元の関数とライン番号を取得する</li>
     *   <li>ログに書き出しを行う</li>
     * </ol>
     * 
     */
	public void exit(Object[] params) {
	    //XXXが呼ばれる前にコールされたらException
	    if( !isFlagSetted() ){
	        throw new ServiceException("Tracer00001",ERROR_USE_OF_TRACE_MSG);
	    }
	    clearFlag();
		final String param = getParameterStrings(params);
		final String callerInfo = getCallerInfo();
		mLogService.write(TRACE_EXIT_KEY,new String[]{callerInfo,param});
	}

	//#####表示レベル問い合わせ#####
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.trace.Tracer#isPublic()
	 */
	public boolean isPublic() {
	    boolean b =  traceLevel <= PUBLIC_LEVEL ;
	    if( b ) setFlag();
	    return b;
	}

	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.trace.Tracer#isProtected()
	 */
	public boolean isProtected() {
		boolean b =  traceLevel <= PROTECTED_LEVEL;
	    if( b ) setFlag();
	    return b;
	}

	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.trace.Tracer#isPrivate()
	 */
	public boolean isPrivate() {
	    boolean b =  traceLevel <= PRIVATE_LEVEL;
	    if( b ) setFlag();
	    return b;
	}
	//#####内部管理用#####
	/**
	 * clearFlag
	 * スレッドローカル変数に記録されたフラグをクリアする
	 */
	protected void clearFlag(){
	    thLocal.set(null);
	}
	/**
	 * isFlagCleared
	 * @return クリア済み
	 */
	protected boolean isFlagSetted(){
	    return thLocal.get() != null;
	}
	/**
	 * トレースエントリ状態であることを設定する
	 *  Exitの際にフラグがクリアされていないとExceptionが発生する
	 */
	protected void setFlag(){
	    thLocal.set(ENTRY_STATE_START);
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
		if( elms.length < nestedLevel ){
			mLogService.write(TRACE_NESTLEVEL_ERR,nestedLevel);
		} else {
			final StackTraceElement elm = elms[nestedLevel];
			callerClass = elm.toString(); 
		}
		return callerClass;
	}
	//表示用ヘルパ関数
	/**
     * Object→String変換を行う。<p>
     * このメソッドには、以下の実装が行われている。<br>
     * <ol>
     *   <li>対応するエディタを取得する</li>
     *   <li>呼び出し元の関数とライン番号を取得する</li>
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
				//対応するエディタを取得して文字列に変換する
				final JournalEditor editor = mEditorFinderService.findEditor(param.getClass());
				String str =(String) editor.toObject(mEditorFinderService,null,param) ;
				//@変換に引っかからないように$に変換する
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
