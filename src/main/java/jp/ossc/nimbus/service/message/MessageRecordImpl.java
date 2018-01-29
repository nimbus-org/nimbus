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
package jp.ossc.nimbus.service.message;

import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.util.*;

/**
 * ���b�Z�[�W���R�[�h�̃f�t�H���g�����B<p>
 *
 * @author H.Nakano
 */
public class MessageRecordImpl
 implements MessageRecord, MessageRecordOperator, Serializable{
    
    private static final long serialVersionUID = -1519744660770872465L;
    
    //## �����o�[�ϐ��錾     ##
    
    /** ���b�Z�[�WID */
    protected String mMessageCode;
    
    /** ���P�[�����b�Z�[�W�}�b�v */
    protected HashMap mMessageHash = new HashMap();
    
    /** ���������t���O */
    protected boolean mIsInitialized = false;
    
    /** �ŏI�g�p���� */
    protected long mLastOccur = -1;
    
    /** �閧�����t���O */
    protected boolean mIsSecret = false;
    
    /** �閧���� */
    protected String secretString;
    
    /** �g�p�� */
    protected long mUsedCount;
    
    /** ���[�h�ς݃��P����}�b�v */
    protected Hashtable mLocaleHash = new Hashtable();
    
    /** MessageRecordFactory */
    private transient MessageRecordFactory mFac;
    
    /** MessageRecordFactory�T�[�r�X�� */
    private ServiceName mFacName;
    
    //## �萔��` ##
    /** �f�t�H���g�J�e�S���[ */
    private static final String C_DFAUTL_LOCALE = "default";
    private static final String C_UNDER_SCORE = "_";
    private static final String C_DELIMETER = "," ;
    
    /** ���ߍ��ݕ��� */
    private static final String EMBED_STRING = "@";
    private static final String SECRET_EMBED_STRING = "#";
    private static final String SINGLE_EMBED_STRING = "@0";
    private static final String SINGLE_SECRET_EMBED_STRING = "#0";
    private static final String SECRET_EMBED_SEARCH_STRING = "#[0-9]+";
    private static final String DATE_FORMAT = "yyyy-MM-dd hh-mm-ss";
    
    // MessageRecord��JavaDoc
    public String makeMessage(Locale lo){
        increment() ;
        return this.getMessageTemplate(lo) ;
    }
    
    // MessageRecord��JavaDoc
    public String makeMessage(){
        return this.makeMessage(Locale.getDefault()) ;
    }
    
    // MessageRecord��JavaDoc
    public String makeMessage(Locale lo,Object embed){
        increment();
        String tmp = getMessageTemplate(lo);
        String retStr = StringOperator
            .replaceString(tmp, SINGLE_EMBED_STRING, embed);
        if(mIsSecret){
            if(secretString != null){
                retStr = StringOperator.replaceString(
                    retStr,
                    SINGLE_SECRET_EMBED_STRING,
                    secretString
                );
            }
        }else{
            retStr = StringOperator.replaceString(
                retStr,
                SINGLE_SECRET_EMBED_STRING,
                embed
            );
        }
        return retStr;
    }
    
    // MessageRecord��JavaDoc
    public String makeMessage(Locale lo, byte embed){
        return makeMessage(lo, Byte.toString(embed));
    }
    
    // MessageRecord��JavaDoc
    public String makeMessage(Locale lo, short embed){
        return makeMessage(lo, Short.toString(embed));
    }
    
    // MessageRecord��JavaDoc
    public String makeMessage(Locale lo, char embed){
        return makeMessage(lo, new Character(embed));
    }
    
    // MessageRecord��JavaDoc
    public String makeMessage(Locale lo, int embed){
        return makeMessage(lo, Integer.toString(embed));
    }
    
    // MessageRecord��JavaDoc
    public String makeMessage(Locale lo, long embed){
        return makeMessage(lo, Long.toString(embed));
    }
    
    // MessageRecord��JavaDoc
    public String makeMessage(Locale lo, float embed){
        return makeMessage(lo, Float.toString(embed));
    }
    
    // MessageRecord��JavaDoc
    public String makeMessage(Locale lo, double embed){
        return makeMessage(lo, Double.toString(embed));
    }
    
    // MessageRecord��JavaDoc
    public String makeMessage(Locale lo, boolean embed){
        return makeMessage(lo, Boolean.toString(embed));
    }
    
    // MessageRecord��JavaDoc
    public String makeMessage(Object embed){
        return makeMessage(Locale.getDefault(),embed) ;
    }
    
    // MessageRecord��JavaDoc
    public String makeMessage(byte embed){
        return makeMessage(Byte.toString(embed));
    }
    
    // MessageRecord��JavaDoc
    public String makeMessage(short embed){
        return makeMessage(Short.toString(embed));
    }
    
    // MessageRecord��JavaDoc
    public String makeMessage(char embed){
        return makeMessage(new Character(embed));
    }
    
    // MessageRecord��JavaDoc
    public String makeMessage(int embed){
        return makeMessage(Integer.toString(embed));
    }
    
    // MessageRecord��JavaDoc
    public String makeMessage(long embed){
        return makeMessage(Long.toString(embed));
    }
    
    // MessageRecord��JavaDoc
    public String makeMessage(float embed){
        return makeMessage(Float.toString(embed));
    }
    
    // MessageRecord��JavaDoc
    public String makeMessage(double embed){
        return makeMessage(Double.toString(embed));
    }
    
    // MessageRecord��JavaDoc
    public String makeMessage(boolean embed){
        return makeMessage(Boolean.toString(embed));
    }
    
    // MessageRecord��JavaDoc
    public String makeMessage(Locale lo,Object[] embeds){
        increment();
        String tmp = getMessageTemplate(lo);
        String retStr = StringOperator.replaceString(tmp, EMBED_STRING, embeds);
        if(mIsSecret){
            if(secretString != null){
                retStr = retStr.replaceAll(
                    SECRET_EMBED_SEARCH_STRING,
                    secretString
                );
            }
        }else{
            retStr = StringOperator.replaceString(
                retStr,
                SECRET_EMBED_STRING,
                embeds
            );
        }
        return retStr;
    }
    
    // MessageRecord��JavaDoc
    public String makeMessage(Locale lo, byte[] embeds){
        if(embeds == null){
            return makeMessage(lo, (Object[])null);
        }
        final Object[] strs = new String[embeds.length];
        for(int i = 0, max = strs.length; i < max; i++){
            strs[i] = Byte.toString(embeds[i]);
        }
        return makeMessage(lo, strs);
    }
    
    // MessageRecord��JavaDoc
    public String makeMessage(Locale lo, short[] embeds){
        if(embeds == null){
            return makeMessage(lo, (Object[])null);
        }
        final Object[] strs = new String[embeds.length];
        for(int i = 0, max = strs.length; i < max; i++){
            strs[i] = Short.toString(embeds[i]);
        }
        return makeMessage(lo, strs);
    }
    
    // MessageRecord��JavaDoc
    public String makeMessage(Locale lo, char[] embeds){
        if(embeds == null){
            return makeMessage(lo, (Object[])null);
        }
        final Object[] strs = new String[embeds.length];
        for(int i = 0, max = strs.length; i < max; i++){
            strs[i] = new Character(embeds[i]);
        }
        return makeMessage(lo, strs);
    }
    
    // MessageRecord��JavaDoc
    public String makeMessage(Locale lo, int[] embeds){
        if(embeds == null){
            return makeMessage(lo, (Object[])null);
        }
        final Object[] strs = new String[embeds.length];
        for(int i = 0, max = strs.length; i < max; i++){
            strs[i] = Integer.toString(embeds[i]);
        }
        return makeMessage(lo, strs);
    }
    
    // MessageRecord��JavaDoc
    public String makeMessage(Locale lo, long[] embeds){
        if(embeds == null){
            return makeMessage(lo, (Object[])null);
        }
        final Object[] strs = new String[embeds.length];
        for(int i = 0, max = strs.length; i < max; i++){
            strs[i] = Long.toString(embeds[i]);
        }
        return makeMessage(lo, strs);
    }
    
    // MessageRecord��JavaDoc
    public String makeMessage(Locale lo, float[] embeds){
        if(embeds == null){
            return makeMessage(lo, (Object[])null);
        }
        final Object[] strs = new String[embeds.length];
        for(int i = 0, max = strs.length; i < max; i++){
            strs[i] = Float.toString(embeds[i]);
        }
        return makeMessage(lo, strs);
    }
    
    // MessageRecord��JavaDoc
    public String makeMessage(Locale lo, double[] embeds){
        if(embeds == null){
            return makeMessage(lo, (Object[])null);
        }
        final Object[] strs = new String[embeds.length];
        for(int i = 0, max = strs.length; i < max; i++){
            strs[i] = Double.toString(embeds[i]);
        }
        return makeMessage(lo, strs);
    }
    
    // MessageRecord��JavaDoc
    public String makeMessage(Locale lo, boolean[] embeds){
        if(embeds == null){
            return makeMessage(lo, (Object[])null);
        }
        final Object[] strs = new String[embeds.length];
        for(int i = 0, max = strs.length; i < max; i++){
            strs[i] = Boolean.toString(embeds[i]);
        }
        return makeMessage(lo, strs);
    }
    
    // MessageRecord��JavaDoc
    public String makeMessage(Object[] embeds){
        return makeMessage(Locale.getDefault(),embeds) ;
    }
    
    // MessageRecord��JavaDoc
    public String makeMessage(byte[] embeds){
        if(embeds == null){
            return makeMessage((Object[])null);
        }
        final Object[] strs = new String[embeds.length];
        for(int i = 0, max = strs.length; i < max; i++){
            strs[i] = Byte.toString(embeds[i]);
        }
        return makeMessage(strs);
    }
    
    // MessageRecord��JavaDoc
    public String makeMessage(short[] embeds){
        if(embeds == null){
            return makeMessage((Object[])null);
        }
        final Object[] strs = new String[embeds.length];
        for(int i = 0, max = strs.length; i < max; i++){
            strs[i] = Short.toString(embeds[i]);
        }
        return makeMessage(strs);
    }
    
    // MessageRecord��JavaDoc
    public String makeMessage(char[] embeds){
        if(embeds == null){
            return makeMessage((Object[])null);
        }
        final Object[] strs = new Character[embeds.length];
        for(int i = 0, max = strs.length; i < max; i++){
            strs[i] = new Character(embeds[i]);
        }
        return makeMessage(strs);
    }
    
    // MessageRecord��JavaDoc
    public String makeMessage(int[] embeds){
        if(embeds == null){
            return makeMessage((Object[])null);
        }
        final Object[] strs = new String[embeds.length];
        for(int i = 0, max = strs.length; i < max; i++){
            strs[i] = Integer.toString(embeds[i]);
        }
        return makeMessage(strs);
    }
    
    // MessageRecord��JavaDoc
    public String makeMessage(long[] embeds){
        if(embeds == null){
            return makeMessage((Object[])null);
        }
        final Object[] strs = new String[embeds.length];
        for(int i = 0, max = strs.length; i < max; i++){
            strs[i] = Long.toString(embeds[i]);
        }
        return makeMessage(strs);
    }
    
    // MessageRecord��JavaDoc
    public String makeMessage(float[] embeds){
        if(embeds == null){
            return makeMessage((Object[])null);
        }
        final Object[] strs = new String[embeds.length];
        for(int i = 0, max = strs.length; i < max; i++){
            strs[i] = Float.toString(embeds[i]);
        }
        return makeMessage(strs);
    }
    
    // MessageRecord��JavaDoc
    public String makeMessage(double[] embeds){
        if(embeds == null){
            return makeMessage((Object[])null);
        }
        final Object[] strs = new String[embeds.length];
        for(int i = 0, max = strs.length; i < max; i++){
            strs[i] = Double.toString(embeds[i]);
        }
        return makeMessage(strs);
    }
    
    // MessageRecord��JavaDoc
    public String makeMessage(boolean[] embeds){
        if(embeds == null){
            return makeMessage((Object[])null);
        }
        final Object[] strs = new String[embeds.length];
        for(int i = 0, max = strs.length; i < max; i++){
            strs[i] = Boolean.toString(embeds[i]);
        }
        return makeMessage(strs);
    }
    
    // MessageRecord��JavaDoc
    public String getMessageTemplate(Locale lo){
        return this.getMessage(lo == null ? Locale.getDefault() : lo) ;
    }
    
    // MessageRecord��JavaDoc
    public String getMessageTemplate(){
        return getMessageTemplate(Locale.getDefault()) ;
    }
    
    /**
     * ���b�Z�[�W��`�t�@�C����1�s��ǂݍ��ށB<p>
     * �t�H�[�}�b�g : ���b�Z�[�WID,���b�Z�[�W<br>
     * �G�X�P�[�v���� : \\<br>
     * ���ߍ��ݕ��� : @�A��<br>
     * �V�[�N���b�g���ߍ��ݕ��� : #�A��<br>
     *
     * @param defString ���b�Z�[�W��`�t�@�C����1�s�̕�����
     */
    public void rec2Obj(String defString) throws MessageRecordParseException{
        // �C�j�V�����ς݂�����
        if(!mIsInitialized){
            // �f�t�@�C����������
            CsvArrayList parser = new CsvArrayList();
            parser.split(defString, C_DELIMETER);
            if(parser.size() < 2){
                throw new MessageRecordParseException(
                    "Message define error message is " + defString
                ) ;
            }else{
                // ��{�f�[�^�i�[
                this.mMessageCode = parser.getStr(0);
                this.mMessageHash.put(C_DFAUTL_LOCALE, parser.getStr(1));
            }
        }
    }
    
    // MessageRecordOperator ��JavaDoc
    public String getMessageCode(){
        return this.mMessageCode;
    }
    
    /**
     * ���b�Z�[�WID��ݒ肷��B<p>
     *
     * @param code ���b�Z�[�WID
     */
    public void setMessageCode(String code){
        this.mMessageCode = code;
    }
    
    // MessageRecordOperator ��JavaDoc
    public synchronized long getUsedCount(){
        return this.mUsedCount;
    }
    
    // MessageRecordOperator ��JavaDoc
    public synchronized void clearUsedCount(){
        this.mUsedCount = 0;
    }
    
    /**
     * �g�p�񐔂��J�E���g�A�b�v����B<p>
     */
    protected synchronized void increment(){
        this.mUsedCount++;
        this.mLastOccur = System.currentTimeMillis();
    }
    
    /**
     * ������\�����擾����B<p>
     * 
     * @return ���b�Z�[�WID;���b�Z�[�W;�g�p��;�ŏI�g�p����
     */
    public String toString(){
        StringBuilder ret = new StringBuilder() ;
        ret.append(mMessageCode).append(';');
        ret.append(mMessageHash.get(C_DFAUTL_LOCALE)).append(';');
        ret.append(mUsedCount).append(';');
        if(this.mLastOccur == -1){
            ret.append("NONE") ;
        }else{
            SimpleDateFormat formatter
                 = new SimpleDateFormat(DATE_FORMAT);
            ret.append(formatter.format(new Date(mLastOccur)));
        }
        return ret.toString();
    }
    
    // MessageRecordOperator ��JavaDoc
    public Date getLastUsedDate(){
        return new Date(mLastOccur);
    }
    
    // MessageRecordOperator ��JavaDoc
    public void setSecret(boolean flg){
        mIsSecret = flg ;
    }
    
    // MessageRecordOperator ��JavaDoc
    public void setSecretString(String secret){
        secretString = secret ;
    }
    // MessageRecordOperator ��JavaDoc
    public void addMessage(String message,String locale){
        this.mMessageHash.put(locale,message) ;
        
    }
    // MessageRecordOperator ��JavaDoc
    public void addMessage(String message){
        this.mMessageHash.put(C_DFAUTL_LOCALE,message) ;
    }
    
    /**
     * �w�肵�����P�[���̃��b�Z�[�W���擾����B<p>
     * 
     * @param lo ���P�[��
     * @return �w�肵�����P�[���̃��b�Z�[�W
     */
    protected String getMessage(Locale lo){
        MessageRecordFactory fac = null;
        if(mFac == null && mFacName != null){
            try{
                fac = (MessageRecordFactory)ServiceManagerFactory
                    .getServiceObject(mFacName);
            }catch(ServiceNotFoundException e){
                return null;
            }
        }else{
            fac = mFac;
        }
        if(fac == null){
            return null;
        }
        fac.findLocale(lo);
        String key = (String)mLocaleHash.get(lo);
        if(key == null){
            StringBuilder propKey = new StringBuilder();
            //language1 + "_" + country1 + "_" + variant1 
            propKey.append(lo.getLanguage())
                .append(C_UNDER_SCORE).append(lo.getCountry())
                .append(C_UNDER_SCORE).append(lo.getVariant());
            key = (String)mMessageHash.get(propKey.toString());
            
            if(key == null){
                //language1 + "_" + country1
                propKey.setLength(0);
                propKey.append(lo.getLanguage())
                    .append(C_UNDER_SCORE).append(lo.getCountry());
                key = (String)mMessageHash.get(propKey.toString());
            }
            
            if(key == null){
                // language1  
                propKey.setLength(0);
                propKey.append(lo.getLanguage());
                key = (String)mMessageHash.get(propKey.toString());
            }
            
            if(key == null){
                if(!lo.equals(Locale.getDefault())){
                    
                    Locale loDafault = Locale.getDefault();
                    
                    // language1 + "_" + country1 + "_" + variant1 
                    propKey.setLength(0);
                    propKey.append(loDafault.getLanguage())
                        .append(C_UNDER_SCORE).append(loDafault.getCountry())
                        .append(C_UNDER_SCORE).append(loDafault.getVariant());
                    key = (String)mMessageHash.get(propKey.toString());
                    if(key == null){
                        //language1 + "_" + country1
                        propKey.setLength(0);
                        propKey.append(loDafault.getLanguage())
                            .append(C_UNDER_SCORE).append(loDafault.getCountry()); 
                        key = (String)mMessageHash.get(propKey.toString());
                    }
                    if(key == null){
                        // language1  
                        propKey.setLength(0);
                        propKey.append(loDafault.getLanguage()); 
                        key = (String)mMessageHash.get(propKey.toString());
                    }
                }
                key = (String)mMessageHash.get(C_DFAUTL_LOCALE);
            }
            
            mLocaleHash.put(lo, key);
        }
        return key;
    }
    
    // MessageRecordOperator ��JavaDoc
    public void setFactory(MessageRecordFactory fac){
        mFac = fac;
        if(mFac instanceof ServiceBase){
            mFacName = ((ServiceBase)mFac).getServiceNameObject();
        }else if(mFac instanceof Service){
            final Service service = (Service)mFac;
            mFacName = new ServiceName(
                service.getServiceManagerName(),
                service.getServiceName()
            );
        }
    }
    
    private void writeObject(ObjectOutputStream out) throws IOException{
        out.defaultWriteObject();
        if(mFacName == null && mFac != null){
            out.writeObject(mFac);
        }
    }
    
    private void readObject(ObjectInputStream in)
     throws IOException, ClassNotFoundException{
        in.defaultReadObject();
        if(mFacName == null){
            mFac = (MessageRecordFactory)in.readObject();
        }else{
            try{
                mFac = (MessageRecordFactory)ServiceManagerFactory
                    .getServiceObject(mFacName);
            }catch(ServiceNotFoundException e){
            }
        }
    }
}
