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

import java.io.*;
import java.util.*;
import java.net.*;

import jp.ossc.nimbus.io.ExtentionFileFilter;
import jp.ossc.nimbus.lang.*;
import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.util.*;
import jp.ossc.nimbus.io.*;

/**
 * ���b�Z�[�W���R�[�h�����T�[�r�X�B<p>
 * ���b�Z�[�W���R�[�h�̃t�@�C������̓ǂݍ��݋y�ъO���񋟂��s���B<br>
 *
 * @author H.Nakano
 */
public class MessageRecordFactoryService extends ServiceBase
 implements MessageRecordFactory, MessageRecordFactoryServiceMBean{
    
    private static final long serialVersionUID = 2051325927530427511L;
    
    private static final String C_DFAULT_DEF ="jp/ossc/nimbus/resource/Nimbus";
    private static final String C_UNDER_SCORE = "_";
    private static final String C_SLUSH = "/";
    private static final String C_BKSLA = "\\";
    private static final String C_PATH_DELIMETER = ";";
    private static final String C_RECORD_DELIMETER = ",";
    private static final String C_FOUND_DEF = "1";
    private static final String C_NOT_FOUND_DEF = "0";
    
    /** �t�@�C���f�B���N�g�� */
    protected CsvArrayList mDir = new CsvArrayList();
    /** ���R�[�h�L���b�V��HASH */
    protected HashMap mMessageMap;
    /** �t�@�C���g���q */
    protected String mExtention = "def";
    /** ���b�Z�[�W���R�[�h�N���X */
    protected Class mMessageRecordClass = MessageRecordImpl.class;
    /** ���b�Z�[�W�R�[�h�N���X�� */
    protected String mMessageRecordClassName
         = MessageRecordImpl.class.getName();
    /** �閧�����t���O */
    protected boolean mIsSecret;
    /** �閧���� */
    protected String secretString;
    /** ���P�[�������z�� */
    protected String[] mLocales = new String[0];
    /** �����ς݃��P�[���}�b�v */
    protected HashSet mSerchedLocale;
    /** �����ς݃p�X�}�b�v */
    protected HashMap mSerchedPath;
    /** ���b�Z�[�W��`�t�@�C�������X�g */
    protected CsvArrayList mDefFileNames = new CsvArrayList();
    protected boolean isAllowOverrideMessage;
    protected boolean isLoadNimbusMessageFile = true;
    
    /**
     * �R���X�g���N�^�B<p>
     */
    public MessageRecordFactoryService(){
    }
    
    // MessageRecordFactoryServiceMBean��JavaDoc
    public void setMessageDirPaths(String[] paths){
        mDir.clear();
        if(paths != null){
            for(int i = 0; i < paths.length; i++){
                mDir.add(paths[i]);
            }
        }
    }
    
    // MessageRecordFactoryServiceMBean��JavaDoc
    public String[] getMessageDirPaths(){
        return (String[])mDir.toArray(new String[mDir.size()]);
    }
    
    // MessageRecordFactoryServiceMBean��JavaDoc
    public void addMessageDirPaths(String path) throws Exception{
        final CsvArrayList tmpAry = new CsvArrayList();
        tmpAry.split(path, C_PATH_DELIMETER);
        if(tmpAry.size() <= 0){
            return;
        }
        for(Iterator iterator = tmpAry.iterator(); iterator.hasNext();){
            String defFilePath = (String)iterator.next();
            boolean bFlg = false;
            for(int cnt = 0; cnt < mDir.size(); cnt++){
                if(mDir.getStr(cnt).equals(defFilePath)){
                    bFlg = true;
                    break;
                }
            }
            if(!bFlg){
                // �t�@�C�����X�g�쐬
                mDir.add(defFilePath);
                File errDefDir = new File(defFilePath);
                setMessageDef(errDefDir);
                searchMessageDef(errDefDir);
            }
        }
    }
    
    // MessageRecordFactoryServiceMBean��JavaDoc
    public void setExtentionOfMessageFile(String extention){
        mExtention = extention;
    }
    
    // MessageRecordFactoryServiceMBean��JavaDoc
    public String getExtentionOfMessageFile(){
        return mExtention;
    }
    
    // MessageRecordFactoryServiceMBean��JavaDoc
    public void setMessageRecordClassName(String className)
     throws ServiceException{
        mMessageRecordClassName = className;
        try{
            mMessageRecordClass = Class.forName(
                className,
                true,
                NimbusClassLoader.getInstance()
            );
        }catch(ClassNotFoundException ce){
            throw new ServiceException(
                "MSG000030",
                "MessageRecordClassName is invalid class name is "
                     + mMessageRecordClassName,
                ce
            );
        }
    }
    
    // MessageRecordFactoryServiceMBean��JavaDoc
    public String getMessageRecordClassName(){
        return mMessageRecordClass.getName();
    }
    
    // MessageRecordFactoryServiceMBean��JavaDoc
    public void setSecretMode(boolean flg){
        mIsSecret = flg;
    }
    
    // MessageRecordFactoryServiceMBean��JavaDoc
    public boolean isSecretMode(){
        return mIsSecret;
    }
    
    // MessageRecordFactoryServiceMBean��JavaDoc
    public void setSecretString(String secret){
        secretString = secret;
    }
    
    // MessageRecordFactoryServiceMBean��JavaDoc
    public String getSecretString(){
        return secretString;
    }
    
    // MessageRecordFactoryServiceMBean��JavaDoc
    public void setLocaleStrings(String[] locales){
        mLocales = locales;
    }
    
    // MessageRecordFactoryServiceMBean��JavaDoc
    public String[] getLocaleStrings(){
        return mLocales;
    }
    
    // MessageRecordFactoryServiceMBean��JavaDoc
    public void addMessageFiles(String files) throws Exception {
        CsvArrayList tmp = new CsvArrayList();
        tmp.split(files, C_PATH_DELIMETER);
        if(tmp.size() <= 0){
            return;
        }
        for(Iterator iterator = tmp.iterator(); iterator.hasNext();){
            String fileName = (String)iterator.next();
            boolean bFlg = false ;
            for(int cnt = 0; cnt < mDir.size(); cnt++){
                if(mDefFileNames.getStr(cnt).equals(fileName)){
                    bFlg = true;
                    break;
                }
            }
            if(!bFlg){
                URL url = Thread.currentThread().getContextClassLoader()
                    .getResource(fileName + '.' + mExtention);
                if(url != null && url.openStream() != null){
                    // �e�[�u���쐬
                    readStream(url.openStream(), null);
                    for(int j = 0; j < mLocales.length; j++){
                        loadDefFileFromResource(mLocales[j], fileName);
                    }
                }
            }
        }
    }
    
    // MessageRecordFactoryServiceMBean��JavaDoc
    public void setMessageFiles(String[] files){
        mDefFileNames.clear();
        if(files != null){
            for(int i = 0; i < files.length; i++){
                mDefFileNames.add(files[i]);
            }
        }
    }
    
    // MessageRecordFactoryServiceMBean��JavaDoc
    public String[] getMessageFiles(){
        return (String[])mDefFileNames.toArray(
            new String[mDefFileNames.size()]
        );
    }
    
    // MessageRecordFactoryServiceMBean��JavaDoc
    public void setAllowOverrideMessage(boolean isAllow){
        isAllowOverrideMessage = isAllow;
    }
    
    // MessageRecordFactoryServiceMBean��JavaDoc
    public boolean isAllowOverrideMessage(){
        return isAllowOverrideMessage;
    }
    
    public void setLoadNimbusMessageFile(boolean isLoad){
        isLoadNimbusMessageFile = isLoad;
    }
    public boolean isLoadNimbusMessageFile(){
        return isLoadNimbusMessageFile;
    }
    
    /**
     * �T�[�r�X�̐����������s���B<p>
     *
     * @exception Exception ���������Ɏ��s�����ꍇ
     */
    public void createService() throws Exception{
        mMessageMap = new HashMap();
        mSerchedLocale = new HashSet();
        mSerchedPath = new HashMap();
    }
    
    /**
     * �T�[�r�X�̊J�n�������s���B<p>
     * ���b�Z�[�W��`�t�@�C���̃��[�h���s���B<br>
     *
     * @exception Exception �J�n�����Ɏ��s�����ꍇ
     */
    public void startService() throws Exception{
        
        // �f�B���N�g���w��̃��b�Z�[�W��`�t�@�C���̃��[�h
        for(Iterator iterator = mDir.iterator();iterator.hasNext();){
            final String defFilePath = (String)iterator.next();
            final File errDefDir = new File(defFilePath);
            
            // �f�t�H���g���P�[���̃��b�Z�[�W��`�t�@�C�������[�h����
            setMessageDef(errDefDir);
            
            // �ݒ肳�ꂽ���P�[���̃��b�Z�[�W��`�t�@�C�������[�h����
            searchMessageDef(errDefDir);
        }
        
        if(isLoadNimbusMessageFile){
            mDefFileNames.add(C_DFAULT_DEF);
        }
        
        // �t�@�C���w��̃��b�Z�[�W��`�t�@�C���̃��[�h
        for(Iterator iterator = mDefFileNames.iterator(); iterator.hasNext();){
            final String fileName = (String)iterator.next();
            final URL url = Thread.currentThread().getContextClassLoader()
                .getResource(fileName + '.' + mExtention);
            if(url != null){
                // �f�t�H���g���P�[���̃��b�Z�[�W��`�t�@�C�������[�h����
                readStream(url.openStream(), null);
                
                // �ݒ肳�ꂽ���P�[���̃��b�Z�[�W��`�t�@�C�������[�h����
                for(int j = 0; j < mLocales.length; j++){
                    loadDefFileFromResource(mLocales[j], fileName);
                }
            }
        }
    }
    /**
     * �T�[�r�X�̒�~�������s���B<p>
     *
     * @exception Exception ��~�����Ɏ��s�����ꍇ
     */
    public void stopService() throws Exception{
        mMessageMap.clear();
        mSerchedLocale.clear();
        mSerchedPath.clear();
        if(isLoadNimbusMessageFile){
            mDefFileNames.remove(C_DFAULT_DEF);
        }
    }
    
    /**
     * �T�[�r�X�̔j���������s���B<p>
     *
     * @exception Exception �j�������Ɏ��s�����ꍇ
     */
    public void destroyService() throws Exception{
        mMessageMap = null;
        mSerchedLocale = null;
        mSerchedPath = null;
    }
    
    /**
     * �f�B���N�g���w�莞��ResourceBundle�ǂݍ��݂��s���B<p>
     * 
     * @param lo ���P�[��
     * @param dirPath �f�B���N�g���p�X
     * @exception IOException ���b�Z�[�W��`�t�@�C���̓ǂݍ��݂Ɏ��s�����ꍇ
     * @exception MessageRecordParseException ���b�Z�[�W��`�t�@�C���̃p�[�X�Ɏ��s�����ꍇ
     */
    private void loadDirByLocale(Locale lo, String dirPath)
     throws IOException, MessageRecordParseException{
        //language1 + "_" + country1 + "_" + variant1 
        StringBuffer propKey = new StringBuffer(dirPath);
        propKey.append(C_SLUSH).append(lo.getLanguage())
            .append(C_UNDER_SCORE).append(lo.getCountry())
            .append(C_UNDER_SCORE).append(lo.getVariant());
        String key = StringOperator.replaceString(
            propKey.toString(),
            C_BKSLA,
            C_SLUSH
        );
        String result = (String)mSerchedPath.get(key);
        File dirFile = null;
        if(result== null){
            dirFile = new File(key);
            if(addMessageDef(dirFile)){
                return;
            }
        }
        
        //language1 + "_" + country1
        propKey.setLength(0);
        propKey.append(dirPath)
            .append(C_SLUSH).append(lo.getLanguage())
            .append(C_UNDER_SCORE).append(lo.getCountry()); 
        key = StringOperator.replaceString(
            propKey.toString(),
            C_BKSLA,
            C_SLUSH
        );
        result = (String)mSerchedPath.get(key);
        if(result == null){
            dirFile = new File(key);
            if(addMessageDef(dirFile)){
                return;
            }
        }
        // language1  
        propKey.setLength(0);
        propKey.append(dirPath)
            .append(C_SLUSH).append(lo.getLanguage());
        key = StringOperator.replaceString(
            propKey.toString(),
            C_BKSLA,
            C_SLUSH
        );
        result = (String)mSerchedPath.get(key);
        if(result == null){
            dirFile = new File(key);
            if(addMessageDef(dirFile)){
                return;
            }
        }
        
        if(lo.equals(Locale.getDefault())){
            return;
        }
        
        // language1 + "_" + country1 + "_" + variant1 
        Locale loDafault = Locale.getDefault();
        propKey.setLength(0);
        propKey.append(dirPath)
            .append(C_SLUSH).append(loDafault.getLanguage())
            .append(C_UNDER_SCORE).append(loDafault.getCountry())
            .append(C_UNDER_SCORE).append(loDafault.getVariant());
        key = StringOperator.replaceString(
            propKey.toString(),
            C_BKSLA,
            C_SLUSH
        );
        result = (String)mSerchedPath.get(key);
        if(result== null){
            dirFile = new File(key);
            if(addMessageDef(dirFile)){
                return;
            }
        }
        //language1 + "_" + country1
        propKey.setLength(0);
        propKey.append(dirPath)
            .append(C_SLUSH).append(loDafault.getLanguage())
            .append(C_UNDER_SCORE).append(loDafault.getCountry()); 
        key = StringOperator.replaceString(
            propKey.toString(),
            C_BKSLA,
            C_SLUSH
        );
        result = (String)mSerchedPath.get(key);
        if(result== null){
            dirFile = new File(key);
            if(addMessageDef(dirFile)){
                return;
            }
        }
        // language1  
        propKey.setLength(0);
        propKey.append(dirPath)
            .append(C_SLUSH).append(loDafault.getLanguage()); 
        key = StringOperator.replaceString(
            propKey.toString(),
            C_BKSLA,
            C_SLUSH
        );
        result = (String)mSerchedPath.get(key);
        if(result== null){
            dirFile = new File(key);
            if(addMessageDef(dirFile)){
                return;
            }
        }
    }
    
    /**
     * �N���X�p�X������w��DEF�t�@�C����ResourceBundle�ǂݍ��݂��s���B<p>
     * 
     * @param lo ���P�[��
     * @param defFileName �w��t�@�C����
     * @exception IOException ���b�Z�[�W��`�t�@�C���̓ǂݍ��݂Ɏ��s�����ꍇ
     * @exception MessageRecordParseException ���b�Z�[�W��`�t�@�C���̃p�[�X�Ɏ��s�����ꍇ
     */
    private void loadClassPathByLocale(Locale lo, String defFileName)
     throws IOException, MessageRecordParseException{
        StringBuffer propKey = new StringBuffer();
        //language1 + "_" + country1 + "_" + variant1 
        propKey.append(lo.getLanguage())
            .append(C_UNDER_SCORE).append(lo.getCountry())
            .append(C_UNDER_SCORE).append(lo.getVariant());
        String fileName = defFileName + '_' + propKey.toString()
             +  '.' + mExtention;
        String result = (String)mSerchedPath.get(fileName);
        if(result == null){
            if(loadDefFileFromResource(propKey.toString(), defFileName)){
                return;
            }
        }
        //language1 + "_" + country1  
        propKey.setLength(0);
        propKey.append(lo.getLanguage())
            .append(C_UNDER_SCORE).append(lo.getCountry());
        fileName = defFileName + '_' + propKey.toString() +  '.' + mExtention;
        result = (String)mSerchedPath.get(fileName);
        if(result == null){
            if(loadDefFileFromResource(propKey.toString(), defFileName)){
                return;
            }
        }
        // language1  
        propKey.setLength(0);
        propKey.append(lo.getLanguage());
        fileName = defFileName + '_' + propKey.toString() +  '.' + mExtention;
        result = (String)mSerchedPath.get(fileName);
        if(result == null){
            if(loadDefFileFromResource(propKey.toString(), defFileName)){
                return;
            }
        }
        
        if(lo.equals(Locale.getDefault())){
            return;
        }
        
        // language1 + "_" + country1 + "_" + variant1 
        Locale loDafault = Locale.getDefault();
        propKey.setLength(0);
        propKey.append(loDafault.getLanguage())
            .append(C_UNDER_SCORE).append(loDafault.getCountry())
            .append(C_UNDER_SCORE).append(loDafault.getVariant()); 
        fileName = defFileName + '_' + propKey.toString() +  '.' + mExtention;
        result = (String)mSerchedPath.get(fileName);
        if(result == null){
            if(loadDefFileFromResource(propKey.toString(), defFileName)){
                return;
            }
        }
        //language1 + "_" + country1
        propKey.setLength(0);
        propKey.append(loDafault.getLanguage())
            .append(C_UNDER_SCORE).append(loDafault.getCountry());
        fileName = defFileName + '_' + propKey.toString() +  '.' + mExtention;
        result = (String)mSerchedPath.get(fileName);
        if(result == null){
            if(loadDefFileFromResource(propKey.toString(), defFileName)){
                return;
            }
        }
        // language1  
        propKey.setLength(0);
        propKey.append(loDafault.getLanguage()); 
        fileName = defFileName + '_' + propKey.toString() +  '.' + mExtention;
        result = (String)mSerchedPath.get(fileName);
        if(result == null){
            if(loadDefFileFromResource(propKey.toString(), defFileName)){
                return;
            }
        }
    }
    
    /**
     * �w�肳�ꂽ���P�[���̃��b�Z�[�W��`�t�@�C�������\�[�X�Ƃ��ēǂݍ���Ŋi�[����B<p>
     *
     * @param loString ���P�[��������
     * @param defName ���b�Z�[�W��`�t�@�C����
     * @return ����������true
     * @exception IOException ���b�Z�[�W��`�t�@�C���̓ǂݍ��݂Ɏ��s�����ꍇ
     * @exception MessageRecordParseException ���b�Z�[�W��`�t�@�C���̃p�[�X�Ɏ��s�����ꍇ
     */
    private boolean loadDefFileFromResource(String loString, String defName)
     throws IOException, MessageRecordParseException{
        String fileName = defName + '_' + loString +  '.' + mExtention;
        boolean bret = false;
        final ClassLoader classLoader
             = Thread.currentThread().getContextClassLoader();
        final URL url = classLoader.getResource(fileName);
        try{
            if(url != null){
                readStream(url.openStream(), loString);
                mSerchedPath.put(fileName, C_FOUND_DEF);
                bret = true;
            }else{
                mSerchedPath.put(fileName,C_NOT_FOUND_DEF);
            }
        }catch(IOException e){
            mSerchedPath.put(fileName, C_NOT_FOUND_DEF) ;
        }
        return bret;
    }
    
    
    /**
     * �w�胍�P�[���ɑ΂���f�[�^����������B<p>
     *
     * @param lo ���P�[��
     */
    public void findLocale(Locale lo){
        synchronized(mSerchedLocale){
            Locale tmpLo = null;
            if(lo == null){
                tmpLo = Locale.getDefault();
            }else{
                tmpLo = lo;
            }
            if(!mSerchedLocale.contains(tmpLo)){
                boolean isLoad = false;
                for(Iterator iterator = mDir.iterator(); iterator.hasNext();){
                    final String defDirPath = (String)iterator.next();
                    try{
                        loadDirByLocale(tmpLo, defDirPath);
                        isLoad = true;
                    }catch(Exception e){
                    }
                }
                for(Iterator iterator = mDefFileNames.iterator(); iterator.hasNext();){
                    final String fileName = (String)iterator.next();
                    try{
                        loadClassPathByLocale(tmpLo, fileName);
                        isLoad = true;
                    }catch(Exception e){
                    }
                }
                if(isLoad){
                    mSerchedLocale.add(tmpLo);
                }
            }
        }
    }
    
    /**
     * �w�肳�ꂽ�f�B���N�g���z������A�ݒ肳�ꂽ���P�[���̃��P�[�����f�B���N�g�����������āA���̃f�B���N�g���z���̃��b�Z�[�W��`�t�@�C����ǂݍ���Ŋi�[����B<p>
     * 
     * @param dirRoot ���b�Z�[�W��`�t�@�C�����i�[�����f�B���N�g��
     * @exception IOException ���b�Z�[�W��`�t�@�C���̓ǂݍ��݂Ɏ��s�����ꍇ
     * @exception MessageRecordParseException ���b�Z�[�W��`�t�@�C���̃p�[�X�Ɏ��s�����ꍇ
     */
    protected void searchMessageDef(File dirRoot)
     throws IOException, MessageRecordParseException{
        if(mLocales == null || mLocales.length == 0){
            return;
        }
        File[] dirs = dirRoot.listFiles();
        if(dirs != null){
            for(int cnt = 0; cnt < dirs.length; cnt++){
                if(dirs[cnt].isDirectory()){
                    name = dirs[cnt].getName() ;
                    for(int j = 0; j < mLocales.length; j++){
                        if(name.equals(mLocales[j])){
                            addMessageDef(dirs[cnt]);
                            break;
                        }
                    }
                }
            }
        }
    }
    
    /**
     * �w�肳�ꂽ�f�B���N�g���z���̃��b�Z�[�W��`�t�@�C����ǂݍ���ŁA���̃T�[�r�X�Ɋi�[����B<p>
     * 
     * @param dirRoot ���b�Z�[�W��`�t�@�C�����i�[���ꂽ�f�B���N�g��
     * @exception IOException ���b�Z�[�W��`�t�@�C���̓ǂݍ��݂Ɏ��s�����ꍇ
     * @exception MessageRecordParseException ���b�Z�[�W��`�t�@�C���̃p�[�X�Ɏ��s�����ꍇ
     */
    protected void setMessageDef(File dirRoot)
     throws IOException, MessageRecordParseException{
        ExtentionFileFilter filter = new ExtentionFileFilter(mExtention);
        File[] defFileList = dirRoot.listFiles(filter);
        if(defFileList!=null){
            for(int rCnt = 0; rCnt < defFileList.length; rCnt++){
                if(defFileList[rCnt].isFile()){
                    // �t�@�C��OPEN
                    FileInputStream stream = null;
                    try{
                        stream = new FileInputStream(defFileList[rCnt]);
                    }catch(FileNotFoundException e){
                        continue;
                    }
                    readStream(stream, null);
                }
            }
        }
    }
    
    /**
     * �w�肳�ꂽ�f�B���N�g���z���̃��b�Z�[�W��`�t�@�C����ǂݍ���ŁA���̃T�[�r�X�Ɋi�[����B<p>
     * 
     * @param dir ���b�Z�[�W��`�t�@�C�����i�[���ꂽ�f�B���N�g��
     * @return ���b�Z�[�W��`�t�@�C����ǂݍ��񂾏ꍇtrue
     * @exception IOException ���b�Z�[�W��`�t�@�C���̓ǂݍ��݂Ɏ��s�����ꍇ
     * @exception MessageRecordParseException ���b�Z�[�W��`�t�@�C���̃p�[�X�Ɏ��s�����ꍇ
     */
    protected boolean addMessageDef(File dir)
     throws IOException, MessageRecordParseException{
        ExtentionFileFilter filter = new ExtentionFileFilter(mExtention);
        File[] defFileList = dir.listFiles(filter);
        boolean bret = false ;
        if(defFileList!=null){
            for( int rCnt = 0 ; rCnt < defFileList.length ; rCnt++) {
                if(defFileList[rCnt].isDirectory()){
                    continue;
                }
                // �t�@�C��OPEN
                FileInputStream stream = null ;
                try{
                    stream = new FileInputStream(defFileList[rCnt]);
                }catch(FileNotFoundException e){
                    continue;
                }
                readStream(stream,dir.getName());
            }
            mSerchedPath.put(dir.getAbsolutePath(),C_FOUND_DEF) ;
            bret = true;
        }else{
            mSerchedPath.put(dir.getAbsolutePath(),C_NOT_FOUND_DEF) ;
        }
        return bret;
    }
    
    /**
     * �w�肳�ꂽ���̓X�g���[���̃��b�Z�[�W��`�t�@�C�����A�w�肳�ꂽ���P�[���̃��b�Z�[�W��`�Ƃ��ēǂݍ���ŁA�i�[����B<p>
     *
     * @param stream ���b�Z�[�W��`�t�@�C���̓��̓X�g���[��
     * @param locale ���P�[��������
     * @exception IOException ���b�Z�[�W��`�t�@�C���̓ǂݍ��݂Ɏ��s�����ꍇ
     * @exception MessageRecordParseException ���b�Z�[�W��`�t�@�C���̃p�[�X�Ɏ��s�����ꍇ
     */
    private void readStream(InputStream stream, String locale)
     throws IOException, MessageRecordParseException{
        final UnicodeHexBufferedReader in = new UnicodeHexBufferedReader(
            new InputStreamReader(stream)
        );
        // �e�[�u���쐬
        String record = null;
        try{
            while((record = in.readLine()) != null){
                if(record.length() == 0 || record.charAt(0) == '#'){
                    // �R�����g�s�A�����s�͓ǂݔ�΂�
                    continue;
                }
                if(locale == null){
                    putDefRec(record);
                }else{
                    addDefRec(record, locale);
                }
            }
        }finally{
            in.close();
            stream.close();
        }
    }
    
    /**
     * �w�肵�����P�[���̃��b�Z�[�W��`���R�[�h��ǉ�����B<p>
     * 
     * @param record ���b�Z�[�W��`���R�[�h
     * @param locale ���P�[��
     */
    protected void addDefRec(String record, String locale){
        //Messae Record�쐬
        CsvArrayList ps = new CsvArrayList();
        ps.split(record, C_RECORD_DELIMETER);
        if(ps.size() < 2){
            throw new ServiceException(
                "MESSAGERECORDSERVICEA00",
                "record format error record is " + record
            );
        }
        MessageRecordOperator messageRec
             = (MessageRecordOperator)mMessageMap.get(ps.getStr(0));
        if(messageRec == null){
            throw new ServiceException(
                "MESSAGERECORDSERVICEA01",
                "record id invalid. record is " + record
            );
        }
        messageRec.addMessage(ps.getStr(1), locale);
    }
    
    /**
     * ���b�Z�[�W��`���R�[�h��o�^����B<p>
     *
     * @param record ���b�Z�[�W��`���R�[�h
     * @exception MessageRecordParseException ���b�Z�[�W��`�t�@�C���̃p�[�X�Ɏ��s�����ꍇ
     */
    protected void putDefRec(String record) throws MessageRecordParseException{
        //Messae Record�쐬
        MessageRecordOperator messageRec = null;
        try{
            messageRec = (MessageRecordOperator)mMessageRecordClass
                .newInstance();
        }catch(InstantiationException e){
            throw new ServiceException(
                "MESSAGERECORDSERVICE030",
                "newInstance() failed." + "Class name is "
                     + mMessageRecordClass.getName(),
                 e
            );
        }catch(IllegalAccessException e){
            throw new ServiceException(
                "MESSAGERECORDSERVICE040",
                "newInstance() failed." + "Class name is "
                     + mMessageRecordClass.getName(),
                e
            );
        }
        messageRec.rec2Obj(record);
        messageRec.setFactory(this);
        MessageRecord rec = (MessageRecord)messageRec;
        //�Ǘ�HASH����
        final MessageRecordOperator tmpRec
             = (MessageRecordOperator)mMessageMap.get(rec.getMessageCode());
        if(tmpRec == null){
            mMessageMap.put(rec.getMessageCode(), messageRec);
        }else if(!isAllowOverrideMessage){
            throw new ServiceException(
                "MESSAGERECORDSERVICE041",
                "message code duplicate." + "recode is " + record
            );
        }else{
            mMessageMap.put(rec.getMessageCode(), messageRec);
        }
    }
    
    // MessageRecordFactoryServiceMBean��JavaDoc
    public ArrayList getMessgaeList(){
        MessageRecordOperator rec = null;
        ArrayList retAry = new ArrayList();
        synchronized(mMessageMap){
            Collection c = mMessageMap.values();
            for(Iterator i = c.iterator(); i.hasNext();){
                rec = (MessageRecordOperator)i.next();
                retAry.add(rec.toString());
            }
        }
        return retAry;
    }
    
    // MessageRecordFactoryServiceMBean��JavaDoc
    public ArrayList getUsedMessgaeList() {
        MessageRecordOperator container;
        ArrayList retAry = new ArrayList();
        synchronized(mMessageMap){
            Collection c = mMessageMap.values();
            for(Iterator i = c.iterator(); i.hasNext();){
                container = (MessageRecordOperator)i.next();
                if(container.getUsedCount() > 0){
                    retAry.add(container.toString());
                }
            }
        }
        return retAry;
    }
    
    // MessageRecordFactoryServiceMBean��JavaDoc
    public void initUsedCount(){
        MessageRecordOperator container;
        synchronized(mMessageMap){
            Collection c = mMessageMap.values();
            for(Iterator i = c.iterator(); i.hasNext();){
                container = (MessageRecordOperator)i.next();
                container.clearUsedCount();
            }
        }
    }
    
    // MessageRecordFactory��JavaDoc
    public String[] getMessageIds(){
        synchronized(mMessageMap){
            return (String[])mMessageMap.keySet().toArray(new String[mMessageMap.size()]);
        }
    }
    
    // MessageRecordFactory��JavaDoc
    public MessageRecord findMessageRecord(String key){
        MessageRecord eif;
        synchronized(mMessageMap){
            eif = (MessageRecord)mMessageMap.get(key);
            if(eif != null){
                MessageRecordOperator op = (MessageRecordOperator)eif;
                op.setSecret(mIsSecret);
                op.setSecretString(secretString);
            }
        }
        return eif;
    }
    
    // MessageRecordFactory��JavaDoc
    public String findMessageTemplete(Locale lo, String key){
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.getMessageTemplate(lo);
    }
    
    // MessageRecordFactory��JavaDoc
    public String findMessageTemplete(String key){
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.getMessageTemplate();
    }
    
    // MessageRecordFactory��JavaDoc
    public String findMessage(Locale lo,String key) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(lo);
    }
    
    // MessageRecordFactory��JavaDoc
    public String findMessage(String key) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage();
    }
    
    // MessageRecordFactory��JavaDoc
    public String findEmbedMessage(Locale lo,String key,Object[] embeds) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(lo,embeds);
    }
    
    // MessageRecordFactory��JavaDoc
    public String findEmbedMessage(Locale lo,String key,byte[] embeds) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(lo,embeds);
    }
    
    // MessageRecordFactory��JavaDoc
    public String findEmbedMessage(Locale lo,String key,short[] embeds) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(lo,embeds);
    }
    
    // MessageRecordFactory��JavaDoc
    public String findEmbedMessage(Locale lo,String key,char[] embeds) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(lo,embeds);
    }
    
    // MessageRecordFactory��JavaDoc
    public String findEmbedMessage(Locale lo,String key,int[] embeds) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(lo,embeds);
    }
    
    // MessageRecordFactory��JavaDoc
    public String findEmbedMessage(Locale lo,String key,long[] embeds) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(lo,embeds);
    }
    
    // MessageRecordFactory��JavaDoc
    public String findEmbedMessage(Locale lo,String key,float[] embeds) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(lo,embeds);
    }
    
    // MessageRecordFactory��JavaDoc
    public String findEmbedMessage(Locale lo,String key,double[] embeds) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(lo,embeds);
    }
    
    // MessageRecordFactory��JavaDoc
    public String findEmbedMessage(Locale lo,String key,boolean[] embeds) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(lo,embeds);
    }
    
    // MessageRecordFactory��JavaDoc
    public String findEmbedMessage(String key,Object[] embeds) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(embeds);
    }
    
    // MessageRecordFactory��JavaDoc
    public String findEmbedMessage(String key,byte[] embeds) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(embeds);
    }
    
    // MessageRecordFactory��JavaDoc
    public String findEmbedMessage(String key,short[] embeds) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(embeds);
    }
    
    // MessageRecordFactory��JavaDoc
    public String findEmbedMessage(String key,char[] embeds) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(embeds);
    }
    
    // MessageRecordFactory��JavaDoc
    public String findEmbedMessage(String key,int[] embeds) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(embeds);
    }
    
    // MessageRecordFactory��JavaDoc
    public String findEmbedMessage(String key,long[] embeds) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(embeds);
    }
    
    // MessageRecordFactory��JavaDoc
    public String findEmbedMessage(String key,float[] embeds) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(embeds);
    }
    
    // MessageRecordFactory��JavaDoc
    public String findEmbedMessage(String key,double[] embeds) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(embeds);
    }
    
    // MessageRecordFactory��JavaDoc
    public String findEmbedMessage(String key,boolean[] embeds) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(embeds);
    }
    
    // MessageRecordFactory��JavaDoc
    public String findEmbedMessage(Locale lo,String key,Object embed) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(lo,embed) ;    
    }
    
    // MessageRecordFactory��JavaDoc
    public String findEmbedMessage(Locale lo,String key,byte embed) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(lo,embed) ;    
    }
    
    // MessageRecordFactory��JavaDoc
    public String findEmbedMessage(Locale lo,String key,short embed) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(lo,embed) ;    
    }
    
    // MessageRecordFactory��JavaDoc
    public String findEmbedMessage(Locale lo,String key,char embed) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(lo,embed) ;    
    }
    
    // MessageRecordFactory��JavaDoc
    public String findEmbedMessage(Locale lo,String key,int embed) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(lo,embed) ;    
    }
    
    // MessageRecordFactory��JavaDoc
    public String findEmbedMessage(Locale lo,String key,long embed) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(lo,embed) ;    
    }
    
    // MessageRecordFactory��JavaDoc
    public String findEmbedMessage(Locale lo,String key,float embed) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(lo,embed) ;    
    }
    
    // MessageRecordFactory��JavaDoc
    public String findEmbedMessage(Locale lo,String key,double embed) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(lo,embed) ;    
    }
    
    // MessageRecordFactory��JavaDoc
    public String findEmbedMessage(Locale lo,String key,boolean embed) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(lo,embed) ;    
    }
    
    // MessageRecordFactory��JavaDoc
    public String findEmbedMessage(String key,Object embed) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(embed) ;    
    }
    
    // MessageRecordFactory��JavaDoc
    public String findEmbedMessage(String key,byte embed) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(embed) ;    
    }
    
    // MessageRecordFactory��JavaDoc
   public String findEmbedMessage(String key,short embed) {
       MessageRecord rec = this.findMessageRecord(key);
       return rec == null ? null : rec.makeMessage(embed) ;    
   }
    
    // MessageRecordFactory��JavaDoc
    public String findEmbedMessage(String key,char embed) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(embed) ;    
    }
    
    // MessageRecordFactory��JavaDoc
     public String findEmbedMessage(String key,int embed) {
         MessageRecord rec = this.findMessageRecord(key);
         return rec == null ? null : rec.makeMessage(embed) ;    
     }
    
    // MessageRecordFactory��JavaDoc
    public String findEmbedMessage(String key,long embed) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(embed) ;    
    }
    
    // MessageRecordFactory��JavaDoc
    public String findEmbedMessage(String key,float embed) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(embed) ;    
    }
    
    // MessageRecordFactory��JavaDoc
    public String findEmbedMessage(String key,double embed) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(embed) ;    
    }
    
    // MessageRecordFactory��JavaDoc
    public String findEmbedMessage(String key,boolean embed) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(embed) ;    
    }
}
