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

import jp.ossc.nimbus.service.writer.log4j.FileAppenderWriterService;

import org.apache.log4j.*;

/**
 * Log4JのRollingFileAppenderを使ったMessageWriterサービス。<p>
 * 
 * @author M.Takata
 */
public class RollingFileAppenderWriterService extends FileAppenderWriterService
 implements RollingFileAppenderWriterServiceMBean {
    
    private static final long serialVersionUID = 360683732716933676L;
    
    /**ローテーションのトリガーとなるファイルサイズ*/
    private long maxFileSize = -1;
    /**最大管理履歴数*/
    private int maxBackups = -1;
    
    /**
     * インスタンスを生成する。<p>
     */
    public RollingFileAppenderWriterService(){
        super();
    }
    
    // RollingFileAppenderWriterServiceMBeanのJavaDoc
    public void setMaximumFileSize(long maxFileSize){
        this.maxFileSize = maxFileSize;
    }
    
    // RollingFileAppenderWriterServiceMBeanのJavaDoc
    public long getMaximumFileSize(){
        return maxFileSize;
    }
    
    // RollingFileAppenderWriterServiceMBeanのJavaDoc
    public void setMaxBackupIndex(int maxBackups){
        this.maxBackups = maxBackups;
    }
    
    // RollingFileAppenderWriterServiceMBeanのJavaDoc
    public int getMaxBackupIndex(){
        return maxBackups;
    }
    
    /**
     * RollingFileAppenderインスタンスを生成して、インスタンス変数の{@link #appender}を初期化する。<p>
     *
     * @exception Exception RollingFileAppenderの生成に失敗した場合
     */
    protected WriterAppender createWriterAppender() throws Exception{
        return new RollingFileAppender();
    }
    
    /**
     * RollingFileAppenderを初期化する。<p>
     *
     * @exception Exception RollingFileAppenderの初期化に失敗した場合
     */
    protected void initWriterAppender(WriterAppender appender) throws Exception{
        super.initWriterAppender(appender);
        final RollingFileAppender rolling = (RollingFileAppender)appender;
        if(maxFileSize > 0){
            rolling.setMaximumFileSize(maxFileSize);
        }
        if(maxBackups > 0){
            rolling.setMaxBackupIndex(maxBackups);
        }
    }
}
