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
 * Log4J��WriterAppender���g����MessageWriter�T�[�r�X�B<p>
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
     * LoggingEvent�̃L�[�ƂȂ�Logger�I�u�W�F�N�g�B<p>
     */
    protected Logger logger;
    
    /**
     * �t�@�C���o�͂��s�� WriterAppender �I�u�W�F�N�g�B<p>
     */
    protected WriterAppender appender;
    
    /**
     * �t�@�C���o�͂̃��C�A�E�g�����߂�Layout�I�u�W�F�N�g�B<p>
     */
    protected Layout layout = new Layout(){
        private final StringBuffer sbuf = new StringBuffer(128);
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
     * �C���X�^���X�𐶐�����B<p>
     */
    public WriterAppenderWriterService(){
        super();
    }
    
    // WriterAppenderWriterServiceMBean��JavaDoc
    public void setImmediateFlush(boolean flush){
        isImmediateFlush = flush;
    }
    
    // WriterAppenderWriterServiceMBean��JavaDoc
    public boolean isImmediateFlush(){
        return isImmediateFlush;
    }
    
    // WriterAppenderWriterServiceMBean��JavaDoc
    public void setEncoding(String encoding){
        this.encoding = encoding;
    }
    
    // WriterAppenderWriterServiceMBean��JavaDoc
    public String getEncoding(){
        return encoding;
    }
    
    // WriterAppenderWriterServiceMBean��JavaDoc
    public void setHeader(String header){
        this.header = header;
    }
    
    // WriterAppenderWriterServiceMBean��JavaDoc
    public String getHeader(){
        return header;
    }
    
    // WriterAppenderWriterServiceMBean��JavaDoc
    public void setFooter(String footer){
        this.footer = footer;
    }
    
    // WriterAppenderWriterServiceMBean��JavaDoc
    public String getFooter(){
        return footer;
    }
    
    // WriterAppenderWriterServiceMBean��JavaDoc
    public boolean isSynchronized(){
        return isSynchronized;
    }
    // WriterAppenderWriterServiceMBean��JavaDoc
    public void setSynchronized(boolean isSynch){
        isSynchronized = isSynch;
    }
    
    /**
     * �J�n�������s���B<p>
     *
     * @exception Exception �J�n�����Ɏ��s�����ꍇ
     */
    public void startService() throws Exception{
        logger = createLogger();
        appender = createWriterAppender();
        initWriterAppender(appender);
        appender.activateOptions();
    }
    
    /**
     * Logger�C���X�^���X�𐶐�����B<p>
     */
    protected Logger createLogger(){
        return Logger.getLogger(
            getServiceManagerName() + '#' + getServiceName()
        );
    }
    
    /**
     * WriterAppender�C���X�^���X�𐶐�����B<p>
     *
     * @exception Exception WriterAppender�̐����Ɏ��s�����ꍇ
     */
    protected WriterAppender createWriterAppender() throws Exception{
        return new WriterAppender();
    }
    
    /**
     * WriterAppender������������B<p>
     *
     * @exception Exception WriterAppender�̏������Ɏ��s�����ꍇ
     */
    protected void initWriterAppender(WriterAppender appender) throws Exception{
        appender.setLayout(layout);
        appender.setImmediateFlush(isImmediateFlush);
        if(encoding != null){
            appender.setEncoding(encoding);
        }
    }
    
    /**
     * ��~�������s���B<p>
     */
    public void stopService(){
        appender.close();
        appender = null;
    }
    
    /**
     * �o�̓t�@�C���̃��C�A�E�g��ݒ肷��B<p>
     *
     * @param layout ���C�A�E�g
     */
    public void setLayout(Layout layout){
        this.layout = layout;
    }
    
    // MessageWriter��JavaDoc
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
