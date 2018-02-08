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
package jp.ossc.nimbus.service.cache;

import java.util.*;
import java.io.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.io.Externalizer;

/**
 * �t�@�C���L���b�V���T�[�r�X�B<p>
 * �ȉ��ɁA�L���b�V���I�u�W�F�N�g��JVM�̃e���|�����f�B���N�g���ɒ��񉻂��ăL���b�V������t�@�C���L���b�V���T�[�r�X�̃T�[�r�X��`��������B<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * 
 * &lt;server&gt;
 *     
 *     &lt;manager name="Sample"&gt;
 *         
 *         &lt;service name="FileCache"
 *                  code="jp.ossc.nimbus.service.cache.FileCacheService"/&gt;
 *         
 *     &lt;/manager&gt;
 *     
 * &lt;/server&gt;
 * </pre>
 *
 * @author M.Takata
 */
public class FileCacheService extends AbstractCacheService
 implements java.io.Serializable, FileCacheServiceMBean{
    
    private static final long serialVersionUID = 4587745705534545655L;
    
    private static final String JVM_TMP_DIR = "java.io.tmpdir";
    
    private String outputDirectory;
    private File directory;
    private String prefix;
    private String suffix = DEFAULT_SUFFIX;
    private boolean isDeleteOnExitWithJVM = true;
    private boolean isLoadOnStart;
    private boolean isCheckFileOnLoad;
    private boolean isDeleteOnCheckFileError;
    
    private ServiceName externalizerServiceName;
    private Externalizer externalizer;
    
    // FileCacheServiceMBean��JavaDoc
    public void setOutputDirectory(String outputDirectory){
        this.outputDirectory = outputDirectory;
    }
    
    // FileCacheServiceMBean��JavaDoc
    public String getOutputDirectory(){
        return outputDirectory;
    }
    
    // FileCacheServiceMBean��JavaDoc
    public void setOutputPrefix(String prefix){
        this.prefix = prefix;
    }
    
    // FileCacheServiceMBean��JavaDoc
    public String getOutputPrefix(){
        return prefix;
    }
    
    // FileCacheServiceMBean��JavaDoc
    public void setOutputSuffix(String suffix){
        this.suffix = suffix;
    }
    
    // FileCacheServiceMBean��JavaDoc
    public String getOutputSuffix(){
        return suffix;
    }
    
    // FileCacheServiceMBean��JavaDoc
    public void setLoadOnStart(boolean isLoad){
        isLoadOnStart = isLoad;
    }
    
    // FileCacheServiceMBean��JavaDoc
    public boolean isLoadOnStart(){
        return isLoadOnStart;
    }
    
    // FileCacheServiceMBean��JavaDoc
    public void setCheckFileOnLoad(boolean isCheck){
        isCheckFileOnLoad = isCheck;
    }
    
    // FileCacheServiceMBean��JavaDoc
    public boolean isCheckFileOnLoad(){
        return isCheckFileOnLoad;
    }
    
    // FileCacheServiceMBean��JavaDoc
    public void setDeleteOnCheckFileError(boolean isDelete){
        isDeleteOnCheckFileError = isDelete;
    }
    
    // FileCacheServiceMBean��JavaDoc
    public boolean isDeleteOnCheckFileError(){
        return isDeleteOnCheckFileError;
    }
    
    // FileCacheServiceMBean��JavaDoc
    public void setExternalizerServiceName(ServiceName name){
        externalizerServiceName = name;
    }
    
    // FileCacheServiceMBean��JavaDoc
    public ServiceName getExternalizerServiceName(){
        return externalizerServiceName;
    }
    
    public void setExternalizer(Externalizer ext){
        externalizer = ext;
    }
    
    public Externalizer getExternalizer(){
        return externalizer;
    }
    
    /**
     * �w�肳�ꂽ�L���b�V���I�u�W�F�N�g��ۑ�����t�@�C�����쐬����B<p>
     *
     * @param obj �L���b�V���I�u�W�F�N�g
     * @return �L���b�V���I�u�W�F�N�g��ۑ�����t�@�C��
     * @exception IOException �t�@�C�����쐬�ł��Ȃ������ꍇ
     */
    protected File createFile(Object obj) throws IOException{
        File file = null;
        String prefix = this.prefix;
        if(prefix == null){
            if(obj != null){
                synchronized(obj){
                    prefix = obj.toString();
                }
            }else{
                prefix = "null";
            }
        }
        if(directory != null){
            file = File.createTempFile(
                createPrefix(prefix),
                suffix,
                directory
            );
        }else{
            file = File.createTempFile(
                createPrefix(prefix),
                suffix
            );
        }
        if(isDeleteOnExitWithJVM()){
            file.deleteOnExit();
        }
        return file;
    }
    
    private String createPrefix(String prefix){
        if(prefix.length() > 2){
            return prefix;
        }else{
            final StringBuffer buf = new StringBuffer(prefix);
            for(int i = 0, max = 3 - prefix.length(); i < max; i++){
                buf.append('_');
            }
            return buf.toString();
        }
    }
    
    // FileCacheServiceMBean��JavaDoc
    public void setDeleteOnExitWithJVM(boolean isDeleteOnExit){
        isDeleteOnExitWithJVM = isDeleteOnExit;
    }
    
    // FileCacheServiceMBean��JavaDoc
    public boolean isDeleteOnExitWithJVM(){
        return isDeleteOnExitWithJVM;
    }
    
    /**
     * �T�[�r�X�̊J�n�������s���B<p>
     *
     * @exception Exception �T�[�r�X�̊J�n�����Ɏ��s�����ꍇ
     */
    public void startService() throws Exception{
        if(externalizerServiceName != null){
            externalizer = (Externalizer)ServiceManagerFactory.getServiceObject(externalizerServiceName);
        }
        if(outputDirectory != null){
            final File dir = new File(outputDirectory);
            if(dir.exists()){
                if(!dir.isDirectory()){
                    throw new IllegalArgumentException(
                        "Path is illegal : " + outputDirectory
                    );
                }
            }else{
                if(!dir.mkdirs()){
                    throw new IllegalArgumentException(
                        "Path is illegal : " + outputDirectory
                    );
                }
            }
            directory = dir;
        }
        if(isLoadOnStart()){
            load();
        }
    }
    
    /**
     * �L���b�V���t�@�C���̓ǂݍ��݂��s���B<p>
     *
     * @exception Exception �L���b�V���t�@�C���̓ǂݍ��݂Ɏ��s�����ꍇ
     */
    protected void load() throws Exception{
        File dir = directory;
        if(dir == null){
            final String tmpFileStr = System.getProperty(JVM_TMP_DIR);
            if(tmpFileStr != null){
                final File tmpFile = new File(tmpFileStr);
                if(tmpFile.exists() && tmpFile.isDirectory()){
                    dir = tmpFile;
                }
            }
        }
        if(dir != null){
            final File[] list = dir.listFiles(
                new FilenameFilter(){
                    private final String pre = prefix != null
                         ? createPrefix(prefix) : null;
                    public boolean accept(File dir, String name){
                        if(pre == null){
                            return name.endsWith(suffix);
                        }else{
                            return name.startsWith(pre)
                                 && name.endsWith(suffix);
                        }
                    }
                }
            );
            Arrays.sort(
                list,
                new Comparator(){
                    public int compare(Object o1, Object o2){
                        File f1 = (File)o1;
                        File f2 = (File)o2;
                        long diff = f1.lastModified() - f2.lastModified();
                        return diff == 0 ? 0 : (diff > 0 ? 1 : -1);
                    }
                }
            );
            for(int i = 0; i < list.length; i++){
                if(!containsFile(list[i])){
                    FileCachedReference ref = new FileCachedReference(list[i], externalizer);
                    if(isCheckFileOnLoad){
                        try{
                            ref.deserializeObject();
                            add(ref);
                        }catch(IOException e){
                            if(isDeleteOnCheckFileError){
                                list[i].delete();
                            }
                        }catch(ClassNotFoundException e){
                            if(isDeleteOnCheckFileError){
                                list[i].delete();
                            }
                        }
                    }else{
                        add(ref);
                    }
                }
            }
        }
    }
    
    /**
     * ���̃L���b�V���Ɏw�肳�ꂽ�L���b�V���t�@�C���̃L���b�V���Q�Ƃ��܂܂�Ă��邩���ׂ�B<p>
     *
     * @param file �L���b�V���t�@�C��
     * @return �܂܂�Ă���ꍇtrue
     */
    protected boolean containsFile(File file){
        if(references == null || file == null){
            return false;
        }
        boolean result = false;
        synchronized(references){
            final Iterator refs = references.iterator();
            while(refs.hasNext()){
                FileCachedReference ref = (FileCachedReference)refs.next();
                if(file.equals(ref.getFile())){
                    return true;
                }
            }
        }
        return result;
    }
    
    /**
     * �t�@�C���L���b�V���Q�Ƃ𐶐�����B<p>
     * �t�@�C���L���b�V���Q�Ƃ̐����Ɏ��s�����ꍇ�́Anull��Ԃ��B
     *
     * @param obj �L���b�V���I�u�W�F�N�g
     * @return �t�@�C���L���b�V���Q��
     */
    protected CachedReference createCachedReference(Object obj){
        File file = null;
        try{
            file = createFile(obj);
            return new FileCachedReference(
                file,
                obj,
                externalizer
            );
        }catch(IOException e){
            if(file != null){
                file.delete();
            }
            return null;
        }
    }
}
