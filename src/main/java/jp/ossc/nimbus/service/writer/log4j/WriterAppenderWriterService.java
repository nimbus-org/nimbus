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
package jp.ossc.nimbus.service.writer.log4j;

import org.apache.log4j.*;
import org.apache.log4j.spi.LoggingEvent;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.service.writer.*;

/**
 * Log4JのWriterAppenderを使ったMessageWriterサービス。<p>
 * 
 * @author M.Takata
 */
public class WriterAppenderWriterService extends ServiceBase
 implements WriterAppenderWriterServiceMBean{
    
    private static final long serialVersionUID = -6684161299188208252L;
    
    private boolean isImmediateFlush;
    private String encoding;
    private String header;
    private String footer;
    private boolean isSynchronized;
    
    /**
     * LoggingEventのキーとなるLoggerオブジェクト。<p>
     */
    protected Logger logger;
    
    /**
     * ファイル出力を行う WriterAppender オブジェクト。<p>
     */
    protected WriterAppender appender;
    
    /**
     * ファイル出力のレイアウトを決めるLayoutオブジェクト。<p>
     */
    protected Layout layout = new Layout(){
        private final StringBuilder sbuf = new StringBuilder(128);
        public String format(LoggingEvent event){
            sbuf.setLength(0);
            sbuf.append(event.getRenderedMessage());
            sbuf.append(LINE_SEP);
            return sbuf.toString();
        }
        public boolean ignoresThrowable(){
            return true;
        }
        public void activateOptions(){
        }
        public String getHeader() {
            return header;
        }
        public String getFooter() {
            return footer;
        }
    };
    
    /**
     * インスタンスを生成する。<p>
     */
    public WriterAppenderWriterService(){
        super();
    }
    
    // WriterAppenderWriterServiceMBeanのJavaDoc
    public void setImmediateFlush(boolean flush){
        isImmediateFlush = flush;
    }
    
    // WriterAppenderWriterServiceMBeanのJavaDoc
    public boolean isImmediateFlush(){
        return isImmediateFlush;
    }
    
    // WriterAppenderWriterServiceMBeanのJavaDoc
    public void setEncoding(String encoding){
        this.encoding = encoding;
    }
    
    // WriterAppenderWriterServiceMBeanのJavaDoc
    public String getEncoding(){
        return encoding;
    }
    
    // WriterAppenderWriterServiceMBeanのJavaDoc
    public void setHeader(String header){
        this.header = header;
    }
    
    // WriterAppenderWriterServiceMBeanのJavaDoc
    public String getHeader(){
        return header;
    }
    
    // WriterAppenderWriterServiceMBeanのJavaDoc
    public void setFooter(String footer){
        this.footer = footer;
    }
    
    // WriterAppenderWriterServiceMBeanのJavaDoc
    public String getFooter(){
        return footer;
    }
    
    // WriterAppenderWriterServiceMBeanのJavaDoc
    public boolean isSynchronized(){
        return isSynchronized;
    }
    // WriterAppenderWriterServiceMBeanのJavaDoc
    public void setSynchronized(boolean isSynch){
        isSynchronized = isSynch;
    }
    
    /**
     * 開始処理を行う。<p>
     *
     * @exception Exception 開始処理に失敗した場合
     */
    public void startService() throws Exception{
        logger = createLogger();
        appender = createWriterAppender();
        initWriterAppender(appender);
        appender.activateOptions();
    }
    
    /**
     * Loggerインスタンスを生成する。<p>
     */
    protected Logger createLogger(){
        return Logger.getLogger(
            getServiceManagerName() + '#' + getServiceName()
        );
    }
    
    /**
     * WriterAppenderインスタンスを生成する。<p>
     *
     * @exception Exception WriterAppenderの生成に失敗した場合
     */
    protected WriterAppender createWriterAppender() throws Exception{
        return new WriterAppender();
    }
    
    /**
     * WriterAppenderを初期化する。<p>
     *
     * @exception Exception WriterAppenderの初期化に失敗した場合
     */
    protected void initWriterAppender(WriterAppender appender) throws Exception{
        appender.setLayout(layout);
        appender.setImmediateFlush(isImmediateFlush);
        if(encoding != null){
            appender.setEncoding(encoding);
        }
    }
    
    /**
     * 停止処理を行う。<p>
     */
    public void stopService(){
        appender.close();
        appender = null;
    }
    
    /**
     * 出力ファイルのレイアウトを設定する。<p>
     *
     * @param layout レイアウト
     */
    public void setLayout(Layout layout){
        this.layout = layout;
    }
    
    // MessageWriterのJavaDoc
    public void write(WritableRecord rec){
        if(isSynchronized){
            synchronized(this){
                appender.append(
                    new LoggingEvent(
                        getClass().getName(),
                        logger,
                        Level.INFO,
                        rec.toString(),
                        null
                    )
                );
            }
        }else{
            appender.append(
                new LoggingEvent(
                    getClass().getName(),
                    logger,
                    Level.INFO,
                    rec.toString(),
                    null
                )
            );
        }
    }
}
