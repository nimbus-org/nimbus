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
package jp.ossc.nimbus.service.keepalive.smtp;

import java.net.*;
import java.io.*;
import java.util.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.daemon.*;
import jp.ossc.nimbus.service.keepalive.KeepAliveListener;

//
/**
 * SMTPサーバチェッカーサービス。<p>
 *
 * @author H.Nakano
 * @version  1.00 作成: 2003/10/09 - H.Nakano
 */
public class SmtpCheckerService extends ServiceBase
 implements SmtpCheckerServiceMBean, DaemonRunnable{
    
    private static final long serialVersionUID = -1543463563116884001L;
    
    protected static final String C_HELLOW = "HELO localhost\r\n" ; //$NON-NLS-1$
    protected static final String C_EOF_KEY = "WOUGN0600002002" ; //$NON-NLS-1$
    protected static final String C_WRONG_SIGN = "2" ; //$NON-NLS-1$
    protected static final String C_ERRSTATE_KEY = "WOUGN0600002003" ; //$NON-NLS-1$
    protected static final String C_NORMALSTATE_KEY = "WOUGN0600002004" ; //$NON-NLS-1$
    protected static final String C_QUITE = "QUIT\r\n" ; //$NON-NLS-1$
    protected static final String C_TIMEOUT_KEY = "WOUGN0600002005" ; //$NON-NLS-1$
    protected static final String C_PROTOCOL_ERROR_KEY = "WOUGN0600002006" ; //$NON-NLS-1$
    protected static final String C_IOERROR_KEY = "WOUGN0600002007" ; //$NON-NLS-1$
    
    protected String mHostName;
    protected InetAddress mIp;
    protected volatile int mPort = 0;
    protected volatile int mConnectionTimeOut = 0;
    protected volatile int mTimeOut = 1000;
    
    protected String eofLogMessageId = C_EOF_KEY;
    protected String errorStateLogMessageId = C_ERRSTATE_KEY;
    protected String normalStateLogMessageId = C_NORMALSTATE_KEY;
    protected String timeoutLogMessageId = C_TIMEOUT_KEY;
    protected String protocolErrorLogMessageId = C_PROTOCOL_ERROR_KEY;
    protected String ioErrorLogMessageId = C_IOERROR_KEY;
    
    protected boolean isOutputEOFLogMessage;
    protected boolean isOutputErrorStateLogMessage;
    protected boolean isOutputNormalStateLogMessage;
    protected boolean isOutputTimeoutLogMessage;
    protected boolean isOutputProtocolErrorLogMessage;
    protected boolean isOutputIOErrorLogMessage;
    protected List keepAliveListeners;
    
    /**
     * JNDIサーバの生存確認をするかどうかのフラグ。<p>
     */
    protected boolean isAliveCheckSMTPServer;
    
    /**
     * SMTPサーバの生存しているかどうかのフラグ。<p>
     */
    protected boolean isAliveSMTPServer;
    
    /**
     * SMTPサーバの生存確認をする間隔[msec]。<p>
     */
    protected long aliveCheckSMTPServerInterval = 60000;
    
    /**
     * {@link Daemon}オブジェクト。<p>
     */
    protected Daemon daemon;
    
    protected boolean isLoggingDeadSMTPServer = true;
    
    protected boolean isLoggingRecoverSMTPServer = true;
    
    protected String deadSMTPServerLogMessageId = SMTP_SERVER_DEAD_MSG_ID;
    
    protected String recoverSMTPServerLogMessageId = SMTP_SERVER_RECOVER_MSG_ID;
    
    // SmtpChekerServiceMBeanのJavaDoc
    public void setHostName(String hostName) throws UnknownHostException{
        mHostName = hostName;
        mIp = InetAddress.getByName(hostName);
    }
    
    // SmtpChekerServiceMBeanのJavaDoc
    public String getHostName(){
        return mHostName;
    }
    
    // SmtpChekerServiceMBeanのJavaDoc
    public void setPort(int port){
        mPort = port;
    }
    
    // SmtpChekerServiceMBeanのJavaDoc
    public int getPort(){
        return mPort;
    }
    
    // SmtpChekerServiceMBeanのJavaDoc
    public void setConnectionTimeoutMillis(int milisec){
        mConnectionTimeOut = milisec;
    }
    
    // SmtpChekerServiceMBeanのJavaDoc
    public int getConnectionTimeoutMillis(){
        return mConnectionTimeOut;
    }
    
    // SmtpChekerServiceMBeanのJavaDoc
    public void setTimeoutMillis(int milisec){
        mTimeOut = milisec;
    }
    
    // SmtpChekerServiceMBeanのJavaDoc
    public int getTimeoutMillis(){
        return mTimeOut;
    }
    
    // SmtpChekerServiceMBeanのJavaDoc
    public void setEOFLogMessageId(String id){
        eofLogMessageId = id;
    }
    
    // SmtpChekerServiceMBeanのJavaDoc
    public String getEOFLogMessageId(){
        return eofLogMessageId;
    }
    
    // SmtpChekerServiceMBeanのJavaDoc
    public void setErrorStateLogMessageId(String id){
        errorStateLogMessageId = id;
    }
    
    // SmtpChekerServiceMBeanのJavaDoc
    public String getErrorStateLogMessageId(){
        return errorStateLogMessageId;
    }
    
    // SmtpChekerServiceMBeanのJavaDoc
    public void setNormalStateLogMessageId(String id){
        normalStateLogMessageId = id;
    }
    
    // SmtpChekerServiceMBeanのJavaDoc
    public String getNormalStateLogMessageId(){
        return normalStateLogMessageId;
    }
    
    // SmtpChekerServiceMBeanのJavaDoc
    public void setTimeoutLogMessageId(String id){
        timeoutLogMessageId = id;
    }
    
    // SmtpChekerServiceMBeanのJavaDoc
    public String getTimeoutLogMessageId(){
        return timeoutLogMessageId;
    }
    
    // SmtpChekerServiceMBeanのJavaDoc
    public void setProtocolErrorLogMessageId(String id){
        protocolErrorLogMessageId = id;
    }
    
    // SmtpChekerServiceMBeanのJavaDoc
    public String getProtocolErrorLogMessageId(){
        return protocolErrorLogMessageId;
    }
    
    // SmtpChekerServiceMBeanのJavaDoc
    public void setIOErrorLogMessageId(String id){
        ioErrorLogMessageId = id;
    }
    
    // SmtpChekerServiceMBeanのJavaDoc
    public String getIOErrorLogMessageId(){
        return ioErrorLogMessageId;
    }
    
    // SmtpChekerServiceMBeanのJavaDoc
    public void setOutputEOFLogMessage(boolean isOutput){
        isOutputEOFLogMessage = isOutput;
    }
    
    // SmtpChekerServiceMBeanのJavaDoc
    public boolean isOutputEOFLogMessage(){
        return isOutputEOFLogMessage;
    }
    
    // SmtpChekerServiceMBeanのJavaDoc
    public void setOutputErrorStateLogMessage(boolean isOutput){
        isOutputErrorStateLogMessage = isOutput;
    }
    
    // SmtpChekerServiceMBeanのJavaDoc
    public boolean isOutputErrorStateLogMessage(){
        return isOutputErrorStateLogMessage;
    }
    
    // SmtpChekerServiceMBeanのJavaDoc
    public void setOutputNormalStateLogMessage(boolean isOutput){
        isOutputNormalStateLogMessage = isOutput;
    }
    
    // SmtpChekerServiceMBeanのJavaDoc
    public boolean isOutputNormalStateLogMessage(){
        return isOutputNormalStateLogMessage;
    }
    
    // SmtpChekerServiceMBeanのJavaDoc
    public void setOutputTimeoutLogMessage(boolean isOutput){
        isOutputTimeoutLogMessage = isOutput;
    }
    
    // SmtpChekerServiceMBeanのJavaDoc
    public boolean isOutputTimeoutLogMessage(){
        return isOutputTimeoutLogMessage;
    }
    
    // SmtpChekerServiceMBeanのJavaDoc
    public void setOutputProtocolErrorLogMessage(boolean isOutput){
        isOutputProtocolErrorLogMessage = isOutput;
    }
    
    // SmtpChekerServiceMBeanのJavaDoc
    public boolean isOutputProtocolErrorLogMessage(){
        return isOutputProtocolErrorLogMessage;
    }
    
    // SmtpChekerServiceMBeanのJavaDoc
    public void setOutputIOErrorLogMessage(boolean isOutput){
        isOutputIOErrorLogMessage = isOutput;
    }
    
    // SmtpChekerServiceMBeanのJavaDoc
    public boolean isOutputIOErrorLogMessage(){
        return isOutputIOErrorLogMessage;
    }
    
    // SmtpChekerServiceMBeanのJavaDoc
    public void setLoggingDeadSMTPServer(boolean isOutput){
        isLoggingDeadSMTPServer = isOutput;
    }
    
    // SmtpChekerServiceMBeanのJavaDoc
    public boolean isLoggingDeadSMTPServer(){
        return isLoggingDeadSMTPServer;
    }
    
    // SmtpChekerServiceMBeanのJavaDoc
    public void setLoggingRecoverSMTPServer(boolean isOutput){
        isLoggingRecoverSMTPServer = isOutput;
    }
    
    // SmtpChekerServiceMBeanのJavaDoc
    public boolean isLoggingRecoverSMTPServer(){
        return isLoggingRecoverSMTPServer;
    }
    
    // SmtpChekerServiceMBeanのJavaDoc
    public void setDeadSMTPServerLogMessageId(String id){
        deadSMTPServerLogMessageId = id;
    }
    
    // SmtpChekerServiceMBeanのJavaDoc
    public String getDeadSMTPServerLogMessageId(){
        return deadSMTPServerLogMessageId;
    }
    
    // SmtpChekerServiceMBeanのJavaDoc
    public void setRecoverSMTPServerLogMessageId(String id){
        recoverSMTPServerLogMessageId = id;
    }
    
    // SmtpChekerServiceMBeanのJavaDoc
    public String getRecoverSMTPServerLogMessageId(){
        return recoverSMTPServerLogMessageId;
    }
    
    // SmtpChekerServiceMBeanのJavaDoc
    public void setAliveCheckSMTPServer(boolean isCheck){
        isAliveCheckSMTPServer = isCheck;
        if(isCheck && getState() == STARTED && !daemon.isRunning()){
            daemon.start();
        }
    }
    
    // SmtpChekerServiceMBeanのJavaDoc
    public boolean isAliveCheckSMTPServer(){
        return isAliveCheckSMTPServer;
    }
    
    // SmtpChekerServiceMBeanのJavaDoc
    public void setAliveCheckSMTPServerInterval(long interval){
        aliveCheckSMTPServerInterval = interval;
    }
    
    // SmtpChekerServiceMBeanのJavaDoc
    public long getAliveCheckSMTPServerInterval(){
        return aliveCheckSMTPServerInterval;
    }
    
    // SmtpChekerServiceMBeanのJavaDoc
    public boolean isAliveSMTPServer(){
        if(getState() != STARTED){
            return false;
        }else if(isAliveCheckSMTPServer){
            return isAliveSMTPServer;
        }else{
            return isAlive();
        }
    }
    
    // SmtpKeepAliveCheckerのJavaDoc
    public String getHostIp(){
        return mIp == null ? null : mIp.getHostAddress();
    }
    
    // SmtpKeepAliveCheckerのJavaDoc
    public int getHostPort(){
        return mPort;
    }
    
    public void createService() throws Exception{
        keepAliveListeners = new ArrayList();
        daemon = new Daemon(this);
        daemon.setName("Nimbus SMTPCheckDaemon " + getServiceNameObject());
    }
    
    public void startService() throws Exception{
        
        isAliveSMTPServer = isAlive();
        
        if(isAliveCheckSMTPServer){
            // デーモン起動
            daemon.start();
        }
    }
    
    public void stopService() throws Exception{
        
        // デーモン停止
        daemon.stop();
    }
    
    public void destroyService() throws Exception{
        keepAliveListeners = null;
    }
    
    // KeepAliveCheckerのJavaDoc
    public boolean isAlive(){
        return isAliveInternal() == null ? true : false;
    }
    protected Object isAliveInternal(){
        Object ret = null;
        Socket sock = null;
        try{
            final int len = 1024;
            // ソケットを生成して読み書きのストリームをオープン
            sock = new Socket();
            sock.connect(new InetSocketAddress(mIp, mPort), mConnectionTimeOut);
            BufferedInputStream in = new BufferedInputStream(
                sock.getInputStream(),
                len
            );
            BufferedOutputStream out = new BufferedOutputStream(
                sock.getOutputStream(),
                len
            );
            
            // HELLO送信
            out.write(C_HELLOW.getBytes(), 0, C_HELLOW.getBytes().length);
            out.flush();
            
            // 応答をタイムアウト待ちにする。
            sock.setSoTimeout(mTimeOut);
            
            // 応答を読む
            byte[] resBuf = new byte[len];
            int readLen = in.read(resBuf, 0, len);
            
            if(readLen == -1){    // 停止中
                if(isOutputEOFLogMessage){
                    getLogger().write(eofLogMessageId, getSMTPServerInfo());
                }
                ret = "Response reading detect EOF.";
            }else {                // 稼動中
                String retCode = new String(resBuf, 0, readLen);
                if(!retCode.startsWith(C_WRONG_SIGN)) {    // 調子悪し
                    if(isOutputErrorStateLogMessage){
                        String[] wd = new String[2];
                        wd[0] = getSMTPServerInfo();
                        wd[1] = retCode;
                        getLogger().write(errorStateLogMessageId, wd);
                    }
                    ret = "Return code is : " + retCode;
                }else{
                    if(isOutputNormalStateLogMessage){
                        getLogger().write(normalStateLogMessageId, getSMTPServerInfo());
                    }
                }
                
                // ソケット切断
                out.write(C_QUITE.getBytes(), 0, C_QUITE.getBytes().length);
                out.flush();
            }
        }catch(InterruptedIOException e){ // タイムアウト
            if(isOutputTimeoutLogMessage){
                getLogger().write(timeoutLogMessageId, getSMTPServerInfo());
            }
            ret = e;
        }catch(SocketException e){ // プロトコルエラー
            if(isOutputProtocolErrorLogMessage){
                getLogger().write(protocolErrorLogMessageId, getSMTPServerInfo(), e);
            }
            ret = e;
        }catch(IOException e){ // ソケット読み書きエラー
            if(isOutputIOErrorLogMessage){
                getLogger().write(ioErrorLogMessageId, getSMTPServerInfo(), e);
            }
            ret = e;
        }finally{
            try{
                if(sock != null){
                    sock.close();
                }
            }catch(IOException ex){
                if(isOutputIOErrorLogMessage){
                    getLogger().write(ioErrorLogMessageId, getSMTPServerInfo(), ex);
                }
                ret = ex;
            }
        }
        // チェック結果更新
        return ret;
    }
    
    // KeepAliveCheckerのJavaDoc
    public void addKeepAliveListener(KeepAliveListener listener){
        synchronized(keepAliveListeners){
            keepAliveListeners.add(listener);
        }
    }
    
    // KeepAliveCheckerのJavaDoc
    public void removeKeepAliveListener(KeepAliveListener listener){
        synchronized(keepAliveListeners){
            keepAliveListeners.remove(listener);
        }
    }
    
    // KeepAliveCheckerのJavaDoc
    public void clearKeepAliveListener(){
        synchronized(keepAliveListeners){
            keepAliveListeners.clear();
        }
    }
    
    /**
     * デーモンが開始した時に呼び出される。<p>
     * 
     * @return 常にtrueを返す
     */
    public boolean onStart(){
        return true;
    }
    
    /**
     * デーモンが停止した時に呼び出される。<p>
     * 
     * @return 常にtrueを返す
     */
    public boolean onStop(){
        return true;
    }
    
    /**
     * デーモンが中断した時に呼び出される。<p>
     * 
     * @return 常にtrueを返す
     */
    public boolean onSuspend(){
        return true;
    }
    
    /**
     * デーモンが再開した時に呼び出される。<p>
     * 
     * @return 常にtrueを返す
     */
    public boolean onResume(){
        return true;
    }
    
    /**
     * 一定時間sleepした後、isAliveInternal()を実行して、その結果を返す。<p>
     * 
     * @param ctrl DaemonControlオブジェクト
     * @return isAlive()の結果
     */
    public Object provide(DaemonControl ctrl){
        try{
            ctrl.sleep(aliveCheckSMTPServerInterval, true);
        }catch(InterruptedException e){
            Thread.interrupted();
        }
        return isAliveInternal();
    }
    
    /**
     * 引数lookupedObjで渡されたオブジェクトを消費する。<p>
     * isAliveSMTPServerがtrueの状態で、lookupedObj != null の場合、SMTPサーバが死んだ旨のエラーログを出力する。<br>
     * isAliveSMTPServerがfalseの状態で、lookupedObj == null の場合、SMTPサーバが復帰した旨の通知ログを出力する。<br>
     *
     * @param lookupedObj isAlive()の結果
     * @param ctrl DaemonControlオブジェクト
     */
    public void consume(Object lookupedObj, DaemonControl ctrl){
        if(!isAliveCheckSMTPServer){
            return;
        }
        if(isAliveSMTPServer){
            if(lookupedObj != null){
                isAliveSMTPServer = false;
                synchronized(keepAliveListeners){
                    final Iterator itr = keepAliveListeners.iterator();
                    while(itr.hasNext()){
                        final KeepAliveListener keepAliveListener
                             = (KeepAliveListener)itr.next();
                        keepAliveListener.onDead(this);
                    }
                }
                // エラーログ出力
                if(isLoggingDeadSMTPServer){
                    if(lookupedObj instanceof Throwable){
                        getLogger().write(
                            deadSMTPServerLogMessageId,
                            new Object[]{
                                getSMTPServerInfo(),
                                ((Throwable)lookupedObj).getMessage()
                            },
                            (Throwable)lookupedObj
                        );
                    }else{
                        getLogger().write(
                            deadSMTPServerLogMessageId,
                            new Object[]{
                                getSMTPServerInfo(),
                                lookupedObj
                            }
                        );
                    }
                }
            }
        }else{
            if(lookupedObj == null){
                isAliveSMTPServer = true;
                synchronized(keepAliveListeners){
                    final Iterator itr = keepAliveListeners.iterator();
                    while(itr.hasNext()){
                        final KeepAliveListener keepAliveListener
                             = (KeepAliveListener)itr.next();
                        keepAliveListener.onRecover(this);
                    }
                }
                if(isLoggingRecoverSMTPServer){
                    // 通知ログ出力
                    getLogger().write(
                        recoverSMTPServerLogMessageId,
                        getSMTPServerInfo()
                    );
                }
            }
        }
    }
    
    protected String getSMTPServerInfo(){
        return getHostName() + ':' + getHostPort();
    }
    
    /**
     * 何もしない。<p>
     */
    public void garbage(){
    }

    public Object getHostInfo() {
        return mIp;
    }
}
