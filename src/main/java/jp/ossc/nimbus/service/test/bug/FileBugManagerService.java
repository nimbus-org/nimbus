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
package jp.ossc.nimbus.service.test.bug;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.service.sequence.Sequence;
import jp.ossc.nimbus.util.converter.BeanJSONConverter;
import jp.ossc.nimbus.util.converter.DateFormatConverter;

/**
 * Fileシステムを使用した{@link BugManager}インタフェース実装クラス。
 *
 * @author M.Ishida
 */
public class FileBugManagerService extends ServiceBase implements BugManager, FileBugManagerServiceMBean {
    
    protected ServiceName sequenceServiceName;
    protected Sequence sequence;
    
    protected BugRecord templateRecord;
    protected File rootDir;
    protected BeanJSONConverter converter;
    protected String[] enabledPropertyNames = DEFAULT_CONVERTER_ENABLE_PROP_NAMES;
    
    public ServiceName getSequenceServiceName() {
        return sequenceServiceName;
    }
    
    public void setSequenceServiceName(ServiceName serviceName) {
        sequenceServiceName = serviceName;
    }
    
    public Sequence getSequence() {
        return sequence;
    }
    
    public void setSequence(Sequence sequence) {
        this.sequence = sequence;
    }
    
    public BugRecord getTemplateRecord() {
        return templateRecord;
    }
    
    public void setTemplateRecord(BugRecord record) {
        templateRecord = record;
    }
    
    public File getRootDir() {
        return rootDir;
    }
    
    public void setRootDir(File dir) {
        rootDir = dir;
    }
    
    public BeanJSONConverter getConverter() {
        return converter;
    }
    
    public void setConverter(BeanJSONConverter converter) {
        this.converter = converter;
    }
    
    public String[] getEnabledPropertyNames() {
        return enabledPropertyNames;
    }
    
    public void setEnabledPropertyNames(String[] names) {
        enabledPropertyNames = names;
    }

    /**
     * サービスの開始処理を行う。
     * <p>
     *
     * @exception Exception サービスの開始処理に失敗した場合
     */
    public void startService() throws Exception {
        if (templateRecord == null) {
            throw new IllegalArgumentException("templateRecord is null.");
        }
        if (rootDir == null) {
            throw new IllegalArgumentException("rootDir is null.");
        }
        if (!rootDir.exists()) {
            if (!rootDir.mkdirs()) {
                throw new IllegalArgumentException("rootDir could not create.");
            }
        }
        if (converter == null) {
            converter = new BeanJSONConverter();
            converter.setFormat(true);
            converter.setEnabledPropertyNames(templateRecord.getClass(), enabledPropertyNames);
            DateFormatConverter dfc = new DateFormatConverter();
            dfc.setFormat("yyyy/MM/dd HH:mm:ss.SSS");
            converter.setFormatConverter(java.util.Date.class, dfc);
        }
        if (sequenceServiceName != null) {
            sequence = (Sequence) ServiceManagerFactory.getServiceObject(sequenceServiceName);
        }
    }
    
    public BugRecord add(BugRecord record) throws BugManageException {
        if (record.getId() == null && sequence != null) {
            record.setId(sequence.increment());
        }
        if(record.getId() == null) {
            throw new BugManageException("Id is null.");
        }
        File targetFile = new File(rootDir, record.getId());
        if(targetFile.exists()) {
            throw new BugManageException(record.getId() + " is already exists.");
        }
        FileOutputStream fos = null;
        InputStream is = null;
        try {
            is = converter.convertToStream(record);
            fos = new FileOutputStream(targetFile);
            byte[] bytes = new byte[1024];
            int length = 0;
            while((length = is.read(bytes)) != -1){
                fos.write(bytes, 0, length);
            }
            fos.flush();
        } catch(IOException e){
            throw new BugManageException(e);
        } finally {
            if(is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
            if(fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
        }
        return record;
    }
    
    public void update(BugRecord record) throws BugManageException {
        File targetFile = new File(rootDir, record.getId());
        if(!targetFile.exists()) {
            throw new BugManageException(record.getId() + " is not found.");
        }
        FileOutputStream fos = null;
        OutputStreamWriter osw = null;
        InputStream is = null;
        try {
            is = converter.convertToStream(record);
            fos = new FileOutputStream(targetFile);
            byte[] bytes = new byte[1024];
            int length = 0;
            while((length = is.read(bytes)) != -1){
                fos.write(bytes, 0, length);
            }
            fos.flush();
        } catch(IOException e){
            throw new BugManageException(e);
        } finally {
            if(is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
            if(fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
        }
    }
    
    public void delete(String id) throws BugManageException {
        BugRecord record = new BugRecord();
        record.setId(id);
        delete(record);
    }
    
    public void delete(BugRecord record) throws BugManageException {
        File targetFile = new File(rootDir, record.getId());
        if(!targetFile.exists()) {
            throw new BugManageException(record.getId() + " is not found.");
        }
        targetFile.delete();
    }
    
    public List list() throws BugManageException {
        File[] files = rootDir.listFiles();
        List list = new ArrayList();
        for(int i = 0; i < files.length; i++) {
            if(files[i].isFile()) {
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(files[i]);
                    list.add(converter.convertToObject(fis, templateRecord.cloneBugAttribute()));
                } catch (IOException e) {
                    throw new BugManageException(e);
                } finally {
                    if(fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                        }
                    }
                }
            }
        }
        return list;
    }
    
    public BugRecord get(String id) throws BugManageException {
        BugRecord record = templateRecord.cloneBugAttribute();
        record.setId(id);
        return get(record);
    }
    
    public BugRecord get(BugRecord record) throws BugManageException {
        File targetFile = new File(rootDir, record.getId());
        if(!targetFile.exists()) {
            return null;
        }
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(targetFile);
            return (BugRecord)converter.convertToObject(fis, templateRecord.cloneBugAttribute());
        } catch (IOException e) {
            throw new BugManageException(e);
        } finally {
            if(fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
