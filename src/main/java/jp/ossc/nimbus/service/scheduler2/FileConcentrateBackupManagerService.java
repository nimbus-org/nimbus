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

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.text.SimpleDateFormat;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.io.OperateFile;

/**
 * ファイル集配信バックアップ管理。<p>
 * 集配信バックアップをファイルベースで行う。<br>
 * "バックアップルート/グループ/日付/キー/バックアップファイル"の階層でバックアップファイルを作成する。
 * 各階層は指定されていない場合は、存在しない。<br>
 * また、圧縮モードが指定されている場合は、圧縮してバックアップする。<br>
 * 
 * @author M.Takata
 */
public class FileConcentrateBackupManagerService extends ServiceBase
 implements ConcentrateBackupManager, FileConcentrateBackupManagerServiceMBean{
    
    private static final long serialVersionUID = -8740313948817168475L;
    
    protected File backupDir = new File("backup");
    protected int backupBufferSize = 1024;
    protected int compressMode = COMPRESS_MODE_NONE;
    protected int compressLevel = Deflater.DEFAULT_COMPRESSION;
    protected int compressMethod = ZipOutputStream.DEFLATED;
    protected String dateFormat = DEFAULT_DATE_FORMAT;
    
    public void setDateFormat(String format){
        dateFormat = format;
    }
    public String getDateFormat(){
        return dateFormat;
    }
    
    public void setBackupDirectory(File dir){
        backupDir = dir;
    }
    public File getBackupDirectory(){
        return backupDir;
    }
    
    public void setBufferSize(int size){
        backupBufferSize = size;
    }
    public int getBufferSize(){
        return backupBufferSize;
    }
    
    public void setCompressMode(int mode){
        compressMode = mode;
    }
    public int getCompressMode(){
        return compressMode;
    }
    
    public void setCompressLevel(int level){
        compressLevel = level;
    }
    public int getCompressLevel(){
        return compressLevel;
    }
    
    public void setCompressMethod(int method){
        compressMethod = method;
    }
    public int getCompressMethod(){
        return compressMethod;
    }
    
    public void startService() throws Exception{
        if(backupDir.exists()){
            if(!backupDir.isDirectory()){
                throw new IllegalArgumentException("BackupDirectory is not directory." + backupDir.getPath());
            }
        }else if(!backupDir.mkdirs() && !backupDir.exists()){
            throw new IllegalArgumentException("BackupDirectory can not create." + backupDir.getPath());
        }
    }
    public Object backup(String group, Date date, String key, File file, boolean compressed) throws ConcentrateBackupException{
        return backup(group, date, key, file, compressed, null);
    }
    
    public Object backup(String group, Date date, String key, File file, boolean compressed, Object output) throws ConcentrateBackupException{
        if(group == null && date == null && key == null){
            throw new ConcentrateBackupException("Backup directory can not create.");
        }
        File currentDir = backupDir;
        if(group != null){
            currentDir = new File(currentDir, group);
            if(!currentDir.exists()){
                if(!currentDir.mkdirs() && !currentDir.exists()){
                    throw new ConcentrateBackupException("Directory of group '" + group + "' can not make." + currentDir);
                }
            }
        }
        String dateDirName = null;
        if(date != null){
            dateDirName = new SimpleDateFormat(dateFormat).format(date);
            currentDir = new File(currentDir, dateDirName);
            if(!currentDir.exists()){
                if(!currentDir.mkdirs() && !currentDir.exists()){
                    throw new ConcentrateBackupException("Directory of date '" + dateDirName + "' can not make." + currentDir);
                }
            }
        }
        
        if(key != null){
            currentDir = new File(currentDir, key);
            if(!currentDir.exists()){
                if(!currentDir.mkdirs() && !currentDir.exists()){
                    throw new ConcentrateBackupException("Directory of key '" + key + "' can not make." + currentDir);
                }
            }
        }
        final List result = new ArrayList();
        File backupFile = new File(currentDir, file.getName());
        try{
            backupFile = backupFile(file, compressed, backupFile);
        }catch(IOException e){
            throw new ConcentrateBackupException(e);
        }
        result.add(backupFile);
        return result;
    }
    
    protected File backupFile(File src, boolean compressed, File dest) throws IOException{
        final String destPath = dest.getCanonicalPath();
        if(src.getCanonicalPath().equals(destPath)){
            dest = new File(destPath + ".bk");
        }
        final String destFileName = dest.getName();
        final InputStream in = new FileInputStream(src);
        OutputStream os = null;
        
        
        Deflater deflater = null;
        if(compressed){
            os = new FileOutputStream(dest);
        }else{
            switch(compressMode){
            case COMPRESS_MODE_ZLIB:
                dest = new File(destPath + ".zlib");
                deflater = new Deflater(compressLevel);
                os = new FileOutputStream(dest);
                os = new DeflaterOutputStream(os, deflater, backupBufferSize);
                break;
            case COMPRESS_MODE_ZIP:
                dest = new File(destPath + ".zip");
                os = new FileOutputStream(dest);
                ZipOutputStream zos = new ZipOutputStream(os);
                zos.setLevel(compressLevel);
                zos.setMethod(compressMethod);
                zos.putNextEntry(new ZipEntry(destFileName));
                os = zos;
                break;
            case COMPRESS_MODE_GZIP:
                dest = new File(destPath + ".gz");
                os = new FileOutputStream(dest);
                os = new GZIPOutputStream(os, backupBufferSize);
                break;
            case COMPRESS_MODE_NONE:
                os = new FileOutputStream(dest);
                break;
            default:
                throw new IOException("Unknown compress mode : " + compressMode);
            }
        }
        int len = -1;
        byte[] b = new byte[backupBufferSize];
        try{
            while((len = in.read(b, 0, b.length)) != -1){
                os.write(b, 0, len);
            }
            os.flush();
            if(!compressed){
                if(compressMode == COMPRESS_MODE_ZIP){
                    ((ZipOutputStream)os).closeEntry();
                }
                if(compressMode != COMPRESS_MODE_NONE){
                    ((DeflaterOutputStream)os).finish();
                }
            }
        }finally{
            try{
                in.close();
            }catch(IOException e){}
            try{
                os.close();
            }catch(IOException e){}
            if(deflater != null){
                deflater.end();
            }
        }
        return dest;
    }
    
    public boolean clear() throws ConcentrateBackupException{
        boolean result = true;
        File[] groupFiles = backupDir.listFiles();
        for(int i = 0; i < groupFiles.length; i++){
            if(!groupFiles[i].isDirectory()){
                result &= groupFiles[i].exists() ? OperateFile.deleteAll(groupFiles[i]) : true;
                continue;
            }
            File groupDir = groupFiles[i];
            result &= remove(groupDir.getName());
        }
        return result;
    }
    
    public boolean remove(String group) throws ConcentrateBackupException{
        File groupDir = new File(backupDir, group);
        if(groupDir.exists()){
            return groupDir.exists() ? OperateFile.deleteAll(groupDir) : true;
        }else{
            return true;
        }
    }
    
    public boolean remove(Date date) throws ConcentrateBackupException{
        boolean result = true;
        final String dateDirName = new SimpleDateFormat(dateFormat).format(date);
        File[] groupFiles = backupDir.listFiles();
        for(int i = 0; i < groupFiles.length; i++){
            if(!groupFiles[i].isDirectory()){
                continue;
            }
            File groupDir = groupFiles[i];
            if(groupDir.getName().equals(dateDirName)){
                result &= groupDir.exists() ? OperateFile.deleteAll(groupDir) : true;
                break;
            }
            File[] dateFiles = groupDir.listFiles();
            for(int j = 0; j < dateFiles.length; j++){
                if(!dateFiles[j].isDirectory()){
                    continue;
                }
                File dateDir = dateFiles[j];
                if(dateDir.getName().equals(dateDirName)){
                    result &= dateDir.exists() ? OperateFile.deleteAll(dateDir) : true;
                    break;
                }
            }
        }
        return result;
    }
    
    public boolean remove(String group, Date date) throws ConcentrateBackupException{
        boolean result = true;
        final String dateDirName = new SimpleDateFormat(dateFormat).format(date);
        File[] groupFiles = backupDir.listFiles();
        for(int i = 0; i < groupFiles.length; i++){
            if(!groupFiles[i].isDirectory() || !groupFiles[i].getName().equals(group)){
                continue;
            }
            File groupDir = groupFiles[i];
            File[] dateFiles = groupDir.listFiles();
            for(int j = 0; j < dateFiles.length; j++){
                if(!dateFiles[j].isDirectory()){
                    continue;
                }
                File dateDir = dateFiles[j];
                if(dateDir.getName().equals(dateDirName)){
                    return dateDir.exists() ? OperateFile.deleteAll(dateDir) : true;
                }
            }
            break;
        }
        return result;
    }
    
    public boolean removeTo(Date date) throws ConcentrateBackupException{
        boolean result = true;
        final SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        final String dateDirName = format.format(date);
        File[] groupFiles = backupDir.listFiles();
        for(int i = 0; i < groupFiles.length; i++){
            if(!groupFiles[i].isDirectory()){
                continue;
            }
            File groupDir = groupFiles[i];
            try{
                Date targetDate = format.parse(groupDir.getName());
                if(!targetDate.after(date)){
                    result &= groupDir.exists() ? OperateFile.deleteAll(groupDir) : true;
                }
                continue;
            }catch(java.text.ParseException e){
            }
            File[] dateFiles = groupDir.listFiles();
            for(int j = 0; j < dateFiles.length; j++){
                if(!dateFiles[j].isDirectory()){
                    continue;
                }
                File dateDir = dateFiles[j];
                try{
                    Date targetDate = format.parse(dateDir.getName());
                    if(!targetDate.after(date)){
                        result &= dateDir.exists() ? OperateFile.deleteAll(dateDir) : true;
                    }
                }catch(java.text.ParseException e){
                    continue;
                }
            }
        }
        return result;
    }
    
    public boolean removeTo(String group, Date date) throws ConcentrateBackupException{
        boolean result = true;
        final SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        File[] groupFiles = backupDir.listFiles();
        for(int i = 0; i < groupFiles.length; i++){
            if(!groupFiles[i].isDirectory() || !groupFiles[i].getName().equals(group)){
                continue;
            }
            File groupDir = groupFiles[i];
            File[] dateFiles = groupDir.listFiles();
            for(int j = 0; j < dateFiles.length; j++){
                if(!dateFiles[j].isDirectory()){
                    continue;
                }
                File dateDir = dateFiles[j];
                try{
                    Date targetDate = format.parse(dateDir.getName());
                    if(!targetDate.after(date)){
                        result &=  dateDir.exists() ? OperateFile.deleteAll(dateDir) : true;
                    }
                }catch(java.text.ParseException e){
                    continue;
                }
            }
            break;
        }
        return result;
    }
    
    public boolean remove(String group, Date date, String key) throws ConcentrateBackupException{
        boolean result = true;
        final String dateDirName = new SimpleDateFormat(dateFormat).format(date);
        File[] groupFiles = backupDir.listFiles();
        for(int i = 0; i < groupFiles.length; i++){
            if(!groupFiles[i].isDirectory() || !groupFiles[i].getName().equals(group)){
                continue;
            }
            File groupDir = groupFiles[i];
            File[] dateFiles = groupDir.listFiles();
            for(int j = 0; j < dateFiles.length; j++){
                if(!dateFiles[j].isDirectory() || !dateFiles[j].getName().equals(dateDirName)){
                    continue;
                }
                File dateDir = dateFiles[j];
                File[] keyFiles = dateDir.listFiles();
                for(int k = 0; k < keyFiles.length; k++){
                    if(!keyFiles[k].getName().equals(key)){
                        continue;
                    }
                    File keyFile = keyFiles[k];
                    return keyFile.exists() ? OperateFile.deleteAll(keyFile) : true;
                }
                break;
            }
            break;
        }
        return result;
    }
}
