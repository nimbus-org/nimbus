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
package jp.ossc.nimbus.service.scheduler2;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * 集配信レスポンス。<p>
 * 
 * @author M.Takata
 */
public class ConcentrateResponse implements Serializable{
    
    private static final long serialVersionUID = 7100038841123910324L;
    
    private String group;
    private Date date;
    private String key;
    private List files;
    private Map compressed;
    private Map keyMap;
    private Map dateMap;
    private boolean isClean = true;
    private String rootDirectory;
    private Object output;
    
    public ConcentrateResponse(){
    }
    
    public String getGroup(){
        return group;
    }
    
    public void setGroup(String group){
        this.group = group;
    }
    
    public Date getDate(){
        return date;
    }
    
    public void setDate(Date date){
        this.date = date;
    }
    
    public String getKey(){
        return key;
    }
    
    public void setKey(String key){
        this.key = key;
    }
    
    public File[] getFiles(){
        return files == null ? null : (File[])files.toArray(new File[files.size()]);
    }
    
    public Date getDate(File file){
        if(dateMap == null){
            return getDate();
        }
        Date date = (Date)dateMap.get(file);
        return date == null ? getDate() : date;
    }
    
    public String getKey(File file){
        if(keyMap == null){
            return getKey();
        }
        String key = (String)keyMap.get(file);
        return key == null ? getKey() : key;
    }
    
    public boolean getFileCompressed(File file){
        if(compressed == null){
            return false;
        }
        Boolean flg = (Boolean)compressed.get(file);
        return flg == null ? false : flg.booleanValue();
    }
    
    public void addFile(File file){
        addFile(file, false);
    }
    
    public void addFiles(File[] files){
        for(int i = 0; i < files.length; i++){
            addFile(files[i]);
        }
    }
    
    public void addFile(File file, boolean isCompressed){
        addFile(file, null, null, isCompressed);
    }
    
    public void addFiles(File[] files, boolean[] isCompressed){
        for(int i = 0; i < files.length; i++){
            addFile(files[i], isCompressed[i]);
        }
    }
    
    public void addFile(File file, Date date, String key){
        addFile(file, date, key, false);
    }
    
    public void addFile(File file, Date date, String key, boolean isCompressed){
        if(files == null){
            files = new ArrayList();
            keyMap = new HashMap();
            dateMap = new HashMap();
            compressed = new HashMap();
        }
        files.add(file);
        keyMap.put(file, key);
        dateMap.put(file, date);
        compressed.put(file, isCompressed ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public void addFiles(File[] files, Date[] dates, String[] keys){
        for(int i = 0; i < files.length; i++){
            addFile(files[i], dates[i], keys[i]);
        }
    }
    
    public void addFiles(File[] files, Date[] dates, String[] keys, boolean[] isCompressed){
        for(int i = 0; i < files.length; i++){
            addFile(files[i], dates[i], keys[i], isCompressed[i]);
        }
    }
    
    /**
     * 収集したファイルのルートディレクトリを設定する。<p>
     *
     * @param path 収集したファイルのルートディレクトリ
     */
    public void setRootDirectory(String path){
        rootDirectory = path;
    }
    
    /**
     * 収集したファイルのルートディレクトリを取得する。<p>
     *
     * @return 収集したファイルのルートディレクトリ
     */
    public String getRootDirectory(){
        return rootDirectory;
    }
    
    public void setClean(boolean isClean){
        this.isClean = isClean;
    }
    public boolean isClean(){
        return isClean;
    }
    
    /**
     * スケジュールの処理結果を取得する。<p>
     *
     * @return 処理結果
     */
    public Object getOutput(){
        return output;
    }
    
    /**
     * スケジュールの処理結果を設定する。<p>
     *
     * @param out 処理結果
     */
    public void setOutput(Object out){
        output = out;
    }
    
    public String toString(){
        final StringBuilder buf = new StringBuilder(super.toString());
        buf.append("{group=").append(group);
        buf.append(",date=").append(date);
        buf.append(",key=").append(key);
        buf.append(",files=");
        if(files == null || files.size() == 0){
            buf.append("null");
        }else{
            buf.append('[');
            for(int i = 0, imax = files.size(); i < imax; i++){
                File file = (File)files.get(i);
                buf.append("{file=" + file);
                buf.append(",date=" + dateMap.get(file));
                buf.append(",key=" + keyMap.get(file));
                buf.append(",compressed=" + compressed.get(file)).append('}');
                if(i != imax - 1){
                    buf.append(',');
                }
            }
            buf.append(']');
        }
        buf.append(",isClean=").append(isClean);
        buf.append(",rootDirectory=").append(rootDirectory);
        buf.append(",output=").append(output).append('}');
        return buf.toString();
    }
}