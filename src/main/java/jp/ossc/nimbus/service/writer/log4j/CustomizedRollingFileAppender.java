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
package jp.ossc.nimbus.service.writer.log4j;

import java.io.*;
import java.util.*;

import org.apache.log4j.*;
import org.apache.log4j.helpers.LogLog;

/**
 * CustomizedRollingFileAppenderクラス
 * <p>
 * ファイル名を指定できるFileAppender
 * 
 * @version $Name:  $
 * @author K.Nagai
 * @since 1.0
 */
public class CustomizedRollingFileAppender extends RollingFileAppender {
    /** Buffer */
    private static final int BUFFER_SIZE = 16 * 1024;
    
    /** 索引 */
    public static final String INDEX = "%INDEX%";
    
    private String indexedFileName;
    
    private List fileNames;
    
    public CustomizedRollingFileAppender(){
        super();
        initFileNames();
    }
    
    private synchronized void initFileNames(){
        if(fileNames == null){
            fileNames = new ArrayList();
        }
        int maxIndex = getMaxBackupIndex();
        if(fileNames.size() < maxIndex){
            for(int i = 0, max = maxIndex - fileNames.size(); i < max; i++){
                fileNames.add(null);
            }
        }
    }
    
    public CustomizedRollingFileAppender(
        Layout layout,
        String filename,
        boolean append
    ) throws IOException{
        super(layout, filename, append);
        initFileNames();
    }
    
    public CustomizedRollingFileAppender(Layout layout, String filename)
     throws IOException{
        super(layout, filename);
        initFileNames();
    }
    
    public void setMaxBackupIndex(int maxBackups){
        super.setMaxBackupIndex(maxBackups);
        initFileNames();
    }
    
    public void setFile(String file){
        final int index = file.indexOf(INDEX);
        String tmpFile = null;
        if(index == -1){
            indexedFileName = null;
            tmpFile = file;
        }else{
            indexedFileName = file;
            if(index + INDEX.length() == indexedFileName.length()){
                tmpFile = indexedFileName.substring(0, index);
            }else{
                tmpFile = indexedFileName.substring(0, index)
                    + indexedFileName.substring(index + INDEX.length());
            }
        }
        
        super.setFile(tmpFile);
    }
    
    public void rollOver(){
        
        if(maxBackupIndex > 0){
            
            String tmpFileName = null;
            File tmpFile = null;
            
            tmpFileName = getNextFileName(maxBackupIndex);
            int index = maxBackupIndex;
            if(tmpFileName == null){
                for(int i = 1; i <= maxBackupIndex; i++){
                    tmpFileName = getNextFileName(i);
                    tmpFile = new File(tmpFileName);
                    if(!tmpFile.exists()){
                        index = i;
                        break;
                    }
                }
            }else{
                tmpFile = new File(tmpFileName);
                tmpFile.delete();
                index = maxBackupIndex;
            }
            for(int i = index; --i >= 1;){
                tmpFileName = getNextFileName(i);
                tmpFile = new File(tmpFileName);
                if(tmpFile.exists()){
                    tmpFileName = getNextFileName(i + 1);
                    tmpFile.renameTo(new File(tmpFileName));
                }
            }
            closeFile();
            try{
                copyFile(new File(fileName), new File(getNextFileName(1)));
            }catch(Exception e){
                LogLog.debug("Renaming IOExcetpion when writing file");
            }
        }
        try{
            setFile(fileName, false, bufferedIO, bufferSize);
        }catch(IOException e){
            LogLog.error(
                "setFile(" + super.fileName + ", false) call failed.",
                e
            );
        }
    }
    
    /**
     * @param file
     * @param target
     * @throws IOException
     */
    private void copyFile(File file, File target) throws IOException {
        InputStream from_stream; // 入力ファイルストリーム・オブジェクト
        OutputStream to_stream; // 出力ファイルストリーム・オブジェクト
        int rcount; // 実際に読み込めたデータの大きさを保持
        byte buffer[]; // バッファ

        // バッファを作る
        buffer = new byte[BUFFER_SIZE];

        from_stream = null;
        to_stream = null;

        try {
            // 複写元のファイルを開く
            from_stream = new FileInputStream(file);
            // 複写先のファイルを開く
            to_stream = new FileOutputStream(target);
            // 複写を行なう
            while ((rcount = from_stream.read(buffer)) >= 0) {
                to_stream.write(buffer, 0, rcount);
            }
        }finally{
            if (from_stream != null)
                from_stream.close();
            if (to_stream != null)
                to_stream.close();
        }

        // 呼び出し元に戻る
    }

    /**
     * @param fileName2
     * @param maxBackupIndex
     * @return
     */
    private String getNextFileName(int backupIndex) {
        if(fileNames.size() < backupIndex){
            initFileNames();
        }
        if(fileNames.get(backupIndex - 1) != null){
            return (String)fileNames.get(backupIndex - 1);
        }
        String result = null;
        if(indexedFileName == null){
            result = fileName + (backupIndex);
        }else{
            final int index = indexedFileName.indexOf(INDEX);
            final StringBuilder buf = new StringBuilder();
            buf.append(indexedFileName.substring(0, index));
            buf.append(backupIndex);
            if(index != indexedFileName.length() - INDEX.length()){
                buf.append(indexedFileName.substring(index + INDEX.length()));
            }
            result = buf.toString();
        }
        fileNames.set(backupIndex - 1, result);
        return result;
    }
}