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
package jp.ossc.nimbus.service.sequence;

import java.util.*;
import java.io.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.util.CsvArrayList;
//
/**
 * �����񔭔ԃT�[�r�X�B<p>
 *
 * @author H.Nakano
 */
public class StringSequenceService extends ServiceBase
 implements Sequence, StringSequenceServiceMBean {
    
    private static final long serialVersionUID = 7784010436618007574L;
    
    private static final String C_SEMICORON = ";" ;  //$NON-NLS-1$
    
    //## �����o�[�ϐ��錾 ##
    
    /** �V�[�P���X�ԍ�  */
    protected ArrayList mSequenceNo;
    
    /** �t�H�[�}�b�g������ */
    protected String mFormat;
    
    /** �J�n���ԍ� */
    protected String mInitialNumber = "";
    
    /** �J�n�t���O */
    protected boolean mInitialFlag = true;
    
    /** �R���e�L�X�g�T�[�r�X�� */
    protected ServiceName contextServiceName;
    
    /** �i�����t�@�C���� */
    protected String persistFile;
    
    /** ���Ԗ��i�����t���O */
    protected boolean isPersistEveryTime;
    
    // StringSequenceServiceMBean ��JavaDoc
    public void setFormat(String format){
        mFormat = format;
    }
    
    // StringSequenceServiceMBean ��JavaDoc
    public String getFormat(){
        return mFormat;
    }
    
    // StringSequenceServiceMBean ��JavaDoc
    public void setContextServiceName(ServiceName name){
        contextServiceName = name;
    }
    
    // StringSequenceServiceMBean ��JavaDoc
    public ServiceName getContextServiceName(){
        return contextServiceName;
    }
    
    // StringSequenceServiceMBean ��JavaDoc
    public void setPersistFile(String file){
        persistFile = file;
    }
    
    // StringSequenceServiceMBean ��JavaDoc
    public String getPersistFile(){
        return persistFile;
    }
    
    // StringSequenceServiceMBean ��JavaDoc
    public void setPersistEveryTime(boolean isEveryTime){
        isPersistEveryTime = isEveryTime;
    }
    
    // StringSequenceServiceMBean ��JavaDoc
    public boolean isPersistEveryTime(){
        return isPersistEveryTime;
    }
    
    /**
     * �T�[�r�X�̐����������s���B<p>
     * 
     * @exception Exception �T�[�r�X�̐��������Ɏ��s�����ꍇ
     */
    public void createService() throws Exception{
        mSequenceNo = new ArrayList();
    }
    
    /**
     * �T�[�r�X�̊J�n�������s���B<p>
     * 
     * @exception Exception �T�[�r�X�̊J�n�����Ɏ��s�����ꍇ
     */
    public void startService() throws Exception{
        
        mInitialFlag = true;
        mInitialNumber = "";
        
        // format������؂�ŕ�������
        CsvArrayList parser = new CsvArrayList();
        parser.split(mFormat, C_SEMICORON);
        
        CsvArrayList persist = null;
        if(persistFile != null){
            final File file = new File(persistFile);
            if(file.exists()){
                final FileReader fr = new FileReader(file);
                final BufferedReader br = new BufferedReader(fr);
                try{
                    persist = new CsvArrayList();
                    persist.split(br.readLine(),C_SEMICORON);
                    if(parser.size() != persist.size()){
                        persist = null;
                    }
                }finally{
                    fr.close();
                }
            }else if(file.getParentFile() != null
                 && !file.getParentFile().exists()){
                file.mkdirs();
            }
        }
        
        // �e�������C���X�^���V���O���ă��X�g�Ɋi�[����
        for(int i = 0, max = parser.size(); i < max; i++){
            String formatItem = (String)parser.get(i);
            String persistItem = null;
            if(persist != null){
                persistItem = (String)persist.get(i);
            }
            SequenceVariable item = null;
            if(formatItem.startsWith(TimeSequenceVariable.FORMAT_KEY)){
                item = new TimeSequenceVariable(formatItem);
            }else if(formatItem.indexOf(SimpleSequenceVariable.DELIMITER) != -1){
                item = new SimpleSequenceVariable(formatItem, persistItem);
            }else if(formatItem.length() > 2
                && formatItem.charAt(0)
                     == ContextSequenceVariable.DELIMITER
                && formatItem.charAt(formatItem.length() - 1)
                     == ContextSequenceVariable.DELIMITER
            ){
                item = new ContextSequenceVariable(
                    formatItem,
                    contextServiceName
                );
            }else{
                item = new ConstSequenceVariable(formatItem);
            }
            this.mSequenceNo.add(item);
        }
    }
    
    /**
     * �T�[�r�X�̒�~�������s���B<p>
     * 
     * @exception Exception �T�[�r�X�̒�~�����Ɏ��s�����ꍇ
     */
    public void stopService(){
        if(persistFile != null){
            persistSequence();
        }
        mSequenceNo.clear();
    }
    
    /**
     * �T�[�r�X�̔j���������s���B<p>
     * 
     * @exception Exception �T�[�r�X�̔j�������Ɏ��s�����ꍇ
     */
    public void destroyService(){
        mSequenceNo = null;
    }
    
    // Sequence��JavaDoc
    public String increment(){
        StringBuilder retStr = new StringBuilder();
        synchronized(this){
            // �����̐[�����擾����
            int maxCnt = mSequenceNo.size();
            
            // �P�����ڂ���C���N�������g���J�n����
            for(int rCnt = --maxCnt; rCnt >= 0; rCnt--){
                final SequenceVariable item
                     = (SequenceVariable)mSequenceNo.get(rCnt) ;
                //increment
                boolean isOverFlow = item.increment();
                // �I�[�o�[�t���[���Ȃ��ꍇ�͂����オ��Ȃ�
                if(!isOverFlow){
                    break;
                }
            }
            // �J�����g���������������ԕ����𐶐�����
            for(Iterator iterator = mSequenceNo.iterator(); iterator.hasNext();){
                SequenceVariable item = (SequenceVariable)iterator.next();
                retStr.append(item.getCurrent());
            }
            if(mInitialFlag){
                //�J�n�ԍ���ۑ�
                mInitialNumber = retStr.toString();
                mInitialFlag = false;
            }
        }
        if(persistFile != null && isPersistEveryTime){
            persistSequence();
        }
        return retStr.toString();
    }
    
    /**
     * ���ݔ��Ԃ���Ă���Ō�̔ԍ����t�@�C���ɉi��������B<p>
     */
    protected void persistSequence(){
        FileWriter fw = null;
        try{
            fw = new FileWriter(persistFile, false);
            final StringBuilder buf = new StringBuilder();
            for(Iterator itr = mSequenceNo.iterator(); itr.hasNext();){
                SequenceVariable item = (SequenceVariable)itr.next();
                buf.append(item.getCurrent());
                if(itr.hasNext()){
                    buf.append(C_SEMICORON);
                }
            }
            fw.write(buf.toString(), 0, buf.length());
        }catch(IOException e){
        }finally{
            if(fw != null){
                try{
                    fw.close();
                }catch(IOException e){
                }
            }
        }
    }
    
    // Sequence��JavaDoc
    public void reset(){
        synchronized(this){
            /** �J�����g���������������ԕ����𐶐����� */
            for(Iterator iterator = mSequenceNo.iterator(); iterator.hasNext();){
                final SequenceVariable item = (SequenceVariable)iterator.next();
                item.clear();
            }
            //�J�n�t���O��true�ɂ���B
            mInitialFlag = true;
            mInitialNumber = "";
        }
    }
    
    // Sequence��JavaDoc
    public String getInitial(){
        return mInitialNumber;
    }
    
    // Sequence��JavaDoc
    public String getCurrent(){
        StringBuilder retStr = new StringBuilder();
        synchronized(this){
            for(Iterator iterator = mSequenceNo.iterator(); iterator.hasNext();){
                final SequenceVariable item = (SequenceVariable)iterator.next();
                retStr.append(item.getCurrent());
            }
        }
        return retStr.toString();
    }
}
