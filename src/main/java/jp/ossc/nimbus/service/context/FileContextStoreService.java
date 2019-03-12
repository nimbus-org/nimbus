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
package jp.ossc.nimbus.service.context;

import java.io.*;
import java.util.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.io.*;

/**
 * ファイルコンテキストストア。<p>
 *
 * @author M.Takata
 */
public class FileContextStoreService extends ServiceBase implements FileContextStoreServiceMBean, ContextStore{
    
    private static final long serialVersionUID = -2901837711123149271L;
    
    protected File rootDirectory;
    protected boolean isSupportByKey;
    protected String keyFileName = "keys";
    protected String entireFileName = "entire";
    protected String valueDirectoryName = "values";
    protected String valueFileNamePrefix = "val";
    protected String valueFileNameSuffix;
    protected ServiceName externalizerServiceName;
    protected boolean isLockOnLoad = false;
    
    protected Externalizer externalizer;
    protected File entireFile;
    protected File valueDirectory;
    protected File keyFile;
    protected Map keyFileMap;
    
    public void setRootDirectory(File dir){
        rootDirectory = dir;
    }
    public File getRootDirectory(){
        return rootDirectory;
    }
    
    public void setSupportByKey(boolean isSupport){
        isSupportByKey = isSupport;
    }
    public boolean isSupportByKey(){
        return isSupportByKey;
    }
    
    public void setKeyFileName(String name){
        keyFileName = name;
    }
    public String getKeyFileName(){
        return keyFileName;
    }
    
    public void setValueDirectoryName(String name){
        valueDirectoryName = name;
    }
    public String getValueDirectoryName(){
        return valueDirectoryName;
    }
    
    public void setValueFileNamePrefix(String name){
        valueFileNamePrefix = name;
    }
    public String getValueFileNamePrefix(){
        return valueFileNamePrefix;
    }
    
    public void setValueFileNameSuffix(String name){
        valueFileNameSuffix = name;
    }
    public String getValueFileNameSuffix(){
        return valueFileNameSuffix;
    }
    
    public void setExternalizerServiceName(ServiceName name){
        externalizerServiceName = name;
    }
    public ServiceName getExternalizerServiceName(){
        return externalizerServiceName;
    }
    
    public void setLockOnLoad(boolean isLock){
        isLockOnLoad = isLock;
    }
    public boolean isLockOnLoad(){
        return isLockOnLoad;
    }
    
    public void setExternalizer(Externalizer ext){
        externalizer = ext;
    }
    
    public void startService() throws Exception{
        if(rootDirectory == null){
            throw new IllegalArgumentException("Directory is null.");
        }
        if(!rootDirectory.exists()){
            if(!rootDirectory.mkdirs()){
                throw new IOException("Directory can not make : " + rootDirectory);
            }
        }
        if(!rootDirectory.isDirectory()){
            throw new IllegalArgumentException("Not Directory : " + rootDirectory);
        }
        entireFile = new File(rootDirectory, entireFileName);
        if(isSupportByKey){
            valueDirectory = new File(rootDirectory, valueDirectoryName);
            if(!valueDirectory.exists()){
                if(!valueDirectory.mkdirs()){
                    throw new IOException("Directory can not make : " + valueDirectory);
                }
            }
            if(!valueDirectory.isDirectory()){
                throw new IllegalArgumentException("Not Directory : " + valueDirectory);
            }
            keyFileMap = new HashMap();
            keyFile = new File(rootDirectory, keyFileName);
        }
        if(externalizerServiceName != null){
            externalizer = (Externalizer)ServiceManagerFactory.getServiceObject(externalizerServiceName);
        }
        if(externalizer == null){
            externalizer = new NimbusExternalizerService();
            ((Service)externalizer).create();
            ((Service)externalizer).start();
        }
    }
    
    public void clear() throws Exception{
        if(keyFile != null){
            synchronized(keyFile){
                if(keyFile != null && keyFile.exists()){
                    keyFile.delete();
                }
                keyFileMap.clear();
                if(valueDirectory != null){
                    File[] files = valueDirectory.listFiles();
                    if(files != null){
                        for(int i = 0; i < files.length; i++){
                            if(files[i].isFile()){
                                files[i].delete();
                            }
                        }
                    }
                }
            }
        }
        if(entireFile != null){
            synchronized(entireFile){
                if(entireFile != null && entireFile.exists()){
                    entireFile.delete();
                }
            }
        }
    }
    
    public void save(Context context) throws Exception{
        synchronized(entireFile){
            FileOutputStream fos = new FileOutputStream(entireFile);
            try{
                Set keySet = context.keySet();
                DataOutputStream dos = new DataOutputStream(fos);
                dos.writeInt(keySet.size());
                Iterator keys = keySet.iterator();
                while(keys.hasNext()){
                    Object key = keys.next();
                    Object value = context.get(key);
                    externalizer.writeExternal(key, fos);
                    externalizer.writeExternal(value, fos);
                }
            }finally{
                fos.close();
            }
        }
    }
    
    public void save(Context context, Object key) throws Exception{
        if(!isSupportByKey){
            throw new UnsupportedOperationException();
        }
        Object value = context.get(key);
        synchronized(keyFile){
            File valueFile = (File)keyFileMap.get(key);
            if(valueFile == null){
                valueFile = File.createTempFile(valueFileNamePrefix, valueFileNameSuffix, valueDirectory);
                FileOutputStream fos = new FileOutputStream(valueFile);
                try{
                    externalizer.writeExternal(value, fos);
                }finally{
                    fos.close();
                }
                keyFileMap.put(key, valueFile);
                if(!keyFile.exists()){
                    keyFile.createNewFile();
                }
                RandomAccessFile raf = new RandomAccessFile(keyFile, "rw");
                try{
                    raf.seek(0);
                    raf.writeInt(keyFileMap.size());
                }finally{
                    raf.close();
                }
                fos = new FileOutputStream(keyFile, true);
                try{
                    externalizer.writeExternal(key, fos);
                    externalizer.writeExternal(valueFile, fos);
                }finally{
                    fos.close();
                }
            }else{
                FileOutputStream fos = new FileOutputStream(valueFile);
                try{
                    externalizer.writeExternal(value, fos);
                }finally{
                    fos.close();
                }
            }
        }
    }
    
    public void load(Context context) throws Exception{
        synchronized(entireFile){
            if(!entireFile.exists()){
                return;
            }
            FileInputStream fis = new FileInputStream(entireFile);
            try{
                DataInputStream dis = new DataInputStream(fis);
                final int size = dis.readInt();
                final boolean isSharedContext = context instanceof SharedContext;
                for(int i = 0; i < size; i++){
                    Object key = externalizer.readExternal(fis);
                    Object value = externalizer.readExternal(fis);
                    try{
                        if(isLockOnLoad && isSharedContext){
                            ((SharedContext)context).lock(key);
                        }
                        context.put(key, value);
                    }finally{
                        if(isLockOnLoad && isSharedContext){
                            ((SharedContext)context).unlock(key);
                        }
                    }
                }
            }finally{
                fis.close();
            }
        }
    }
    
    public void loadKey(Context context) throws Exception{
        if(!isSupportByKey){
            throw new UnsupportedOperationException();
        }
        Map map = new HashMap();
        synchronized(keyFile){
            if(keyFile.exists()){
                FileInputStream fis = new FileInputStream(keyFile);
                try{
                    DataInputStream dis = new DataInputStream(fis);
                    final int size = dis.readInt();
                    for(int i = 0; i < size; i++){
                        Object key = externalizer.readExternal(fis);
                        Object valueFile = externalizer.readExternal(fis);
                        keyFileMap.put(key, valueFile);
                        map.put(key, null);
                    }
                }finally{
                    fis.close();
                }
            }else{
                synchronized(entireFile){
                    if(!entireFile.exists()){
                        return;
                    }
                    FileInputStream fis = new FileInputStream(entireFile);
                    DataInputStream dis = new DataInputStream(fis);
                    final int size = dis.readInt();
                    for(int i = 0; i < size; i++){
                        Object key = externalizer.readExternal(fis);
                        Object value = externalizer.readExternal(fis);
                        File valueFile = File.createTempFile(valueFileNamePrefix, valueFileNameSuffix, valueDirectory);
                        FileOutputStream fos = new FileOutputStream(valueFile);
                        try{
                            externalizer.writeExternal(value, fos);
                        }finally{
                            fos.close();
                        }
                        keyFileMap.put(key, valueFile);
                        map.put(key, null);
                    }
                    FileOutputStream fos = new FileOutputStream(keyFile);
                    try{
                        DataOutputStream dos = new DataOutputStream(fos);
                        dos.writeInt(keyFileMap.size());
                        Iterator entries = keyFileMap.entrySet().iterator();
                        while(entries.hasNext()){
                            Map.Entry entry = (Map.Entry)entries.next();
                            externalizer.writeExternal(entry.getKey(), fos);
                            externalizer.writeExternal(entry.getValue(), fos);
                        }
                    }finally{
                        fos.close();
                    }
                }
            }
        }
        final boolean isSharedContext = context instanceof SharedContext;
        try{
            if(isLockOnLoad && isSharedContext){
                ((SharedContext)context).locks(map.keySet());
            }
            context.putAll(map);
        }finally{
            if(isLockOnLoad && isSharedContext){
                ((SharedContext)context).unlocks(map.keySet());
            }
        }
    }
    
    public void load(Context context, Object key) throws Exception{
        if(!isSupportByKey){
            throw new UnsupportedOperationException();
        }
        Object value = null;
        synchronized(keyFile){
            File valueFile = (File)keyFileMap.get(key);
            if(valueFile == null && keyFileMap.size() == 0){
                synchronized(entireFile){
                    if(!entireFile.exists()){
                        return;
                    }
                    FileInputStream fis = new FileInputStream(entireFile);
                    DataInputStream dis = new DataInputStream(fis);
                    final int size = dis.readInt();
                    for(int i = 0; i < size; i++){
                        Object k = externalizer.readExternal(fis);
                        Object val = externalizer.readExternal(fis);
                        valueFile = File.createTempFile(valueFileNamePrefix, valueFileNameSuffix, valueDirectory);
                        FileOutputStream fos = new FileOutputStream(valueFile);
                        try{
                            externalizer.writeExternal(val, fos);
                        }finally{
                            fos.close();
                        }
                        keyFileMap.put(k, valueFile);
                    }
                    FileOutputStream fos = new FileOutputStream(keyFile);
                    try{
                        DataOutputStream dos = new DataOutputStream(fos);
                        dos.writeInt(keyFileMap.size());
                        Iterator entries = keyFileMap.entrySet().iterator();
                        while(entries.hasNext()){
                            Map.Entry entry = (Map.Entry)entries.next();
                            externalizer.writeExternal(entry.getKey(), fos);
                            externalizer.writeExternal(entry.getValue(), fos);
                        }
                    }finally{
                        fos.close();
                    }
                }
            }
            valueFile = (File)keyFileMap.get(key);
            if(valueFile != null){
                FileInputStream fis = new FileInputStream(valueFile);
                try{
                    value = externalizer.readExternal(fis);
                }finally{
                    fis.close();
                }
            }
        }
        if(value != null){
            final boolean isSharedContext = context instanceof SharedContext;
            try{
                if(isLockOnLoad && isSharedContext){
                    ((SharedContext)context).lock(key);
                }
                context.put(key, value);
            }finally{
                if(isLockOnLoad && isSharedContext){
                    ((SharedContext)context).unlock(key);
                }
            }
        }
    }
    
    public boolean isSupportSaveByKey(){
        return isSupportByKey;
    }
    
    public boolean isSupportLoadByKey(){
        return isSupportByKey;
    }
}
