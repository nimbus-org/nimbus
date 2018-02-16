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
 * メッセージレコード生成サービス。<p>
 * メッセージレコードのファイルからの読み込み及び外部提供を行う。<br>
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
    
    /** ファイルディレクトリ */
    protected CsvArrayList mDir = new CsvArrayList();
    /** レコードキャッシュHASH */
    protected HashMap mMessageMap;
    /** ファイル拡張子 */
    protected String mExtention = "def";
    /** メッセージレコードクラス */
    protected Class mMessageRecordClass = MessageRecordImpl.class;
    /** メッセージコードクラス名 */
    protected String mMessageRecordClassName
         = MessageRecordImpl.class.getName();
    /** 秘密処理フラグ */
    protected boolean mIsSecret;
    /** 秘密文字 */
    protected String secretString;
    /** ロケール文字配列 */
    protected String[] mLocales = new String[0];
    /** 検索済みロケールマップ */
    protected HashSet mSerchedLocale;
    /** 検索済みパスマップ */
    protected HashMap mSerchedPath;
    /** メッセージ定義ファイル名リスト */
    protected CsvArrayList mDefFileNames = new CsvArrayList();
    protected boolean isAllowOverrideMessage;
    protected boolean isLoadNimbusMessageFile = true;
    
    /**
     * コンストラクタ。<p>
     */
    public MessageRecordFactoryService(){
    }
    
    // MessageRecordFactoryServiceMBeanのJavaDoc
    public void setMessageDirPaths(String[] paths){
        mDir.clear();
        if(paths != null){
            for(int i = 0; i < paths.length; i++){
                mDir.add(paths[i]);
            }
        }
    }
    
    // MessageRecordFactoryServiceMBeanのJavaDoc
    public String[] getMessageDirPaths(){
        return (String[])mDir.toArray(new String[mDir.size()]);
    }
    
    // MessageRecordFactoryServiceMBeanのJavaDoc
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
                // ファイルリスト作成
                mDir.add(defFilePath);
                File errDefDir = new File(defFilePath);
                setMessageDef(errDefDir);
                searchMessageDef(errDefDir);
            }
        }
    }
    
    // MessageRecordFactoryServiceMBeanのJavaDoc
    public void setExtentionOfMessageFile(String extention){
        mExtention = extention;
    }
    
    // MessageRecordFactoryServiceMBeanのJavaDoc
    public String getExtentionOfMessageFile(){
        return mExtention;
    }
    
    // MessageRecordFactoryServiceMBeanのJavaDoc
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
    
    // MessageRecordFactoryServiceMBeanのJavaDoc
    public String getMessageRecordClassName(){
        return mMessageRecordClass.getName();
    }
    
    // MessageRecordFactoryServiceMBeanのJavaDoc
    public void setSecretMode(boolean flg){
        mIsSecret = flg;
    }
    
    // MessageRecordFactoryServiceMBeanのJavaDoc
    public boolean isSecretMode(){
        return mIsSecret;
    }
    
    // MessageRecordFactoryServiceMBeanのJavaDoc
    public void setSecretString(String secret){
        secretString = secret;
    }
    
    // MessageRecordFactoryServiceMBeanのJavaDoc
    public String getSecretString(){
        return secretString;
    }
    
    // MessageRecordFactoryServiceMBeanのJavaDoc
    public void setLocaleStrings(String[] locales){
        mLocales = locales;
    }
    
    // MessageRecordFactoryServiceMBeanのJavaDoc
    public String[] getLocaleStrings(){
        return mLocales;
    }
    
    // MessageRecordFactoryServiceMBeanのJavaDoc
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
                    // テーブル作成
                    readStream(url.openStream(), null);
                    for(int j = 0; j < mLocales.length; j++){
                        loadDefFileFromResource(mLocales[j], fileName);
                    }
                }
            }
        }
    }
    
    // MessageRecordFactoryServiceMBeanのJavaDoc
    public void setMessageFiles(String[] files){
        mDefFileNames.clear();
        if(files != null){
            for(int i = 0; i < files.length; i++){
                mDefFileNames.add(files[i]);
            }
        }
    }
    
    // MessageRecordFactoryServiceMBeanのJavaDoc
    public String[] getMessageFiles(){
        return (String[])mDefFileNames.toArray(
            new String[mDefFileNames.size()]
        );
    }
    
    // MessageRecordFactoryServiceMBeanのJavaDoc
    public void setAllowOverrideMessage(boolean isAllow){
        isAllowOverrideMessage = isAllow;
    }
    
    // MessageRecordFactoryServiceMBeanのJavaDoc
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
     * サービスの生成処理を行う。<p>
     *
     * @exception Exception 生成処理に失敗した場合
     */
    public void createService() throws Exception{
        mMessageMap = new HashMap();
        mSerchedLocale = new HashSet();
        mSerchedPath = new HashMap();
    }
    
    /**
     * サービスの開始処理を行う。<p>
     * メッセージ定義ファイルのロードを行う。<br>
     *
     * @exception Exception 開始処理に失敗した場合
     */
    public void startService() throws Exception{
        
        // ディレクトリ指定のメッセージ定義ファイルのロード
        for(Iterator iterator = mDir.iterator();iterator.hasNext();){
            final String defFilePath = (String)iterator.next();
            final File errDefDir = new File(defFilePath);
            
            // デフォルトロケールのメッセージ定義ファイルをロードする
            setMessageDef(errDefDir);
            
            // 設定されたロケールのメッセージ定義ファイルをロードする
            searchMessageDef(errDefDir);
        }
        
        if(isLoadNimbusMessageFile){
            mDefFileNames.add(C_DFAULT_DEF);
        }
        
        // ファイル指定のメッセージ定義ファイルのロード
        for(Iterator iterator = mDefFileNames.iterator(); iterator.hasNext();){
            final String fileName = (String)iterator.next();
            final URL url = Thread.currentThread().getContextClassLoader()
                .getResource(fileName + '.' + mExtention);
            if(url != null){
                // デフォルトロケールのメッセージ定義ファイルをロードする
                readStream(url.openStream(), null);
                
                // 設定されたロケールのメッセージ定義ファイルをロードする
                for(int j = 0; j < mLocales.length; j++){
                    loadDefFileFromResource(mLocales[j], fileName);
                }
            }
        }
    }
    /**
     * サービスの停止処理を行う。<p>
     *
     * @exception Exception 停止処理に失敗した場合
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
     * サービスの破棄処理を行う。<p>
     *
     * @exception Exception 破棄処理に失敗した場合
     */
    public void destroyService() throws Exception{
        mMessageMap = null;
        mSerchedLocale = null;
        mSerchedPath = null;
    }
    
    /**
     * ディレクトリ指定時のResourceBundle読み込みを行う。<p>
     * 
     * @param lo ロケール
     * @param dirPath ディレクトリパス
     * @exception IOException メッセージ定義ファイルの読み込みに失敗した場合
     * @exception MessageRecordParseException メッセージ定義ファイルのパースに失敗した場合
     */
    private void loadDirByLocale(Locale lo, String dirPath)
     throws IOException, MessageRecordParseException{
        //language1 + "_" + country1 + "_" + variant1 
        StringBuilder propKey = new StringBuilder(dirPath);
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
     * クラスパス内から指定DEFファイルのResourceBundle読み込みを行う。<p>
     * 
     * @param lo ロケール
     * @param defFileName 指定ファイル名
     * @exception IOException メッセージ定義ファイルの読み込みに失敗した場合
     * @exception MessageRecordParseException メッセージ定義ファイルのパースに失敗した場合
     */
    private void loadClassPathByLocale(Locale lo, String defFileName)
     throws IOException, MessageRecordParseException{
        StringBuilder propKey = new StringBuilder();
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
     * 指定されたロケールのメッセージ定義ファイルをリソースとして読み込んで格納する。<p>
     *
     * @param loString ロケール文字列
     * @param defName メッセージ定義ファイル名
     * @return 見つかったらtrue
     * @exception IOException メッセージ定義ファイルの読み込みに失敗した場合
     * @exception MessageRecordParseException メッセージ定義ファイルのパースに失敗した場合
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
     * 指定ロケールに対するデータを検索する。<p>
     *
     * @param lo ロケール
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
     * 指定されたディレクトリ配下から、設定されたロケールのロケール名ディレクトリを検索して、そのディレクトリ配下のメッセージ定義ファイルを読み込んで格納する。<p>
     * 
     * @param dirRoot メッセージ定義ファイルを格納したディレクトリ
     * @exception IOException メッセージ定義ファイルの読み込みに失敗した場合
     * @exception MessageRecordParseException メッセージ定義ファイルのパースに失敗した場合
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
     * 指定されたディレクトリ配下のメッセージ定義ファイルを読み込んで、このサービスに格納する。<p>
     * 
     * @param dirRoot メッセージ定義ファイルが格納されたディレクトリ
     * @exception IOException メッセージ定義ファイルの読み込みに失敗した場合
     * @exception MessageRecordParseException メッセージ定義ファイルのパースに失敗した場合
     */
    protected void setMessageDef(File dirRoot)
     throws IOException, MessageRecordParseException{
        ExtentionFileFilter filter = new ExtentionFileFilter(mExtention);
        File[] defFileList = dirRoot.listFiles(filter);
        if(defFileList!=null){
            for(int rCnt = 0; rCnt < defFileList.length; rCnt++){
                if(defFileList[rCnt].isFile()){
                    // ファイルOPEN
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
     * 指定されたディレクトリ配下のメッセージ定義ファイルを読み込んで、このサービスに格納する。<p>
     * 
     * @param dir メッセージ定義ファイルが格納されたディレクトリ
     * @return メッセージ定義ファイルを読み込んだ場合true
     * @exception IOException メッセージ定義ファイルの読み込みに失敗した場合
     * @exception MessageRecordParseException メッセージ定義ファイルのパースに失敗した場合
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
                // ファイルOPEN
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
     * 指定された入力ストリームのメッセージ定義ファイルを、指定されたロケールのメッセージ定義として読み込んで、格納する。<p>
     *
     * @param stream メッセージ定義ファイルの入力ストリーム
     * @param locale ロケール文字列
     * @exception IOException メッセージ定義ファイルの読み込みに失敗した場合
     * @exception MessageRecordParseException メッセージ定義ファイルのパースに失敗した場合
     */
    private void readStream(InputStream stream, String locale)
     throws IOException, MessageRecordParseException{
        final UnicodeHexBufferedReader in = new UnicodeHexBufferedReader(
            new InputStreamReader(stream)
        );
        // テーブル作成
        String record = null;
        try{
            while((record = in.readLine()) != null){
                if(record.length() == 0 || record.charAt(0) == '#'){
                    // コメント行、無効行は読み飛ばし
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
     * 指定したロケールのメッセージ定義レコードを追加する。<p>
     * 
     * @param record メッセージ定義レコード
     * @param locale ロケール
     */
    protected void addDefRec(String record, String locale){
        //Messae Record作成
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
     * メッセージ定義レコードを登録する。<p>
     *
     * @param record メッセージ定義レコード
     * @exception MessageRecordParseException メッセージ定義ファイルのパースに失敗した場合
     */
    protected void putDefRec(String record) throws MessageRecordParseException{
        //Messae Record作成
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
        //管理HASH投入
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
    
    // MessageRecordFactoryServiceMBeanのJavaDoc
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
    
    // MessageRecordFactoryServiceMBeanのJavaDoc
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
    
    // MessageRecordFactoryServiceMBeanのJavaDoc
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
    
    // MessageRecordFactoryのJavaDoc
    public String[] getMessageIds(){
        synchronized(mMessageMap){
            return (String[])mMessageMap.keySet().toArray(new String[mMessageMap.size()]);
        }
    }
    
    // MessageRecordFactoryのJavaDoc
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
    
    // MessageRecordFactoryのJavaDoc
    public String findMessageTemplete(Locale lo, String key){
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.getMessageTemplate(lo);
    }
    
    // MessageRecordFactoryのJavaDoc
    public String findMessageTemplete(String key){
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.getMessageTemplate();
    }
    
    // MessageRecordFactoryのJavaDoc
    public String findMessage(Locale lo,String key) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(lo);
    }
    
    // MessageRecordFactoryのJavaDoc
    public String findMessage(String key) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage();
    }
    
    // MessageRecordFactoryのJavaDoc
    public String findEmbedMessage(Locale lo,String key,Object[] embeds) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(lo,embeds);
    }
    
    // MessageRecordFactoryのJavaDoc
    public String findEmbedMessage(Locale lo,String key,byte[] embeds) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(lo,embeds);
    }
    
    // MessageRecordFactoryのJavaDoc
    public String findEmbedMessage(Locale lo,String key,short[] embeds) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(lo,embeds);
    }
    
    // MessageRecordFactoryのJavaDoc
    public String findEmbedMessage(Locale lo,String key,char[] embeds) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(lo,embeds);
    }
    
    // MessageRecordFactoryのJavaDoc
    public String findEmbedMessage(Locale lo,String key,int[] embeds) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(lo,embeds);
    }
    
    // MessageRecordFactoryのJavaDoc
    public String findEmbedMessage(Locale lo,String key,long[] embeds) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(lo,embeds);
    }
    
    // MessageRecordFactoryのJavaDoc
    public String findEmbedMessage(Locale lo,String key,float[] embeds) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(lo,embeds);
    }
    
    // MessageRecordFactoryのJavaDoc
    public String findEmbedMessage(Locale lo,String key,double[] embeds) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(lo,embeds);
    }
    
    // MessageRecordFactoryのJavaDoc
    public String findEmbedMessage(Locale lo,String key,boolean[] embeds) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(lo,embeds);
    }
    
    // MessageRecordFactoryのJavaDoc
    public String findEmbedMessage(String key,Object[] embeds) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(embeds);
    }
    
    // MessageRecordFactoryのJavaDoc
    public String findEmbedMessage(String key,byte[] embeds) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(embeds);
    }
    
    // MessageRecordFactoryのJavaDoc
    public String findEmbedMessage(String key,short[] embeds) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(embeds);
    }
    
    // MessageRecordFactoryのJavaDoc
    public String findEmbedMessage(String key,char[] embeds) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(embeds);
    }
    
    // MessageRecordFactoryのJavaDoc
    public String findEmbedMessage(String key,int[] embeds) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(embeds);
    }
    
    // MessageRecordFactoryのJavaDoc
    public String findEmbedMessage(String key,long[] embeds) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(embeds);
    }
    
    // MessageRecordFactoryのJavaDoc
    public String findEmbedMessage(String key,float[] embeds) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(embeds);
    }
    
    // MessageRecordFactoryのJavaDoc
    public String findEmbedMessage(String key,double[] embeds) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(embeds);
    }
    
    // MessageRecordFactoryのJavaDoc
    public String findEmbedMessage(String key,boolean[] embeds) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(embeds);
    }
    
    // MessageRecordFactoryのJavaDoc
    public String findEmbedMessage(Locale lo,String key,Object embed) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(lo,embed) ;    
    }
    
    // MessageRecordFactoryのJavaDoc
    public String findEmbedMessage(Locale lo,String key,byte embed) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(lo,embed) ;    
    }
    
    // MessageRecordFactoryのJavaDoc
    public String findEmbedMessage(Locale lo,String key,short embed) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(lo,embed) ;    
    }
    
    // MessageRecordFactoryのJavaDoc
    public String findEmbedMessage(Locale lo,String key,char embed) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(lo,embed) ;    
    }
    
    // MessageRecordFactoryのJavaDoc
    public String findEmbedMessage(Locale lo,String key,int embed) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(lo,embed) ;    
    }
    
    // MessageRecordFactoryのJavaDoc
    public String findEmbedMessage(Locale lo,String key,long embed) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(lo,embed) ;    
    }
    
    // MessageRecordFactoryのJavaDoc
    public String findEmbedMessage(Locale lo,String key,float embed) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(lo,embed) ;    
    }
    
    // MessageRecordFactoryのJavaDoc
    public String findEmbedMessage(Locale lo,String key,double embed) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(lo,embed) ;    
    }
    
    // MessageRecordFactoryのJavaDoc
    public String findEmbedMessage(Locale lo,String key,boolean embed) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(lo,embed) ;    
    }
    
    // MessageRecordFactoryのJavaDoc
    public String findEmbedMessage(String key,Object embed) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(embed) ;    
    }
    
    // MessageRecordFactoryのJavaDoc
    public String findEmbedMessage(String key,byte embed) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(embed) ;    
    }
    
    // MessageRecordFactoryのJavaDoc
   public String findEmbedMessage(String key,short embed) {
       MessageRecord rec = this.findMessageRecord(key);
       return rec == null ? null : rec.makeMessage(embed) ;    
   }
    
    // MessageRecordFactoryのJavaDoc
    public String findEmbedMessage(String key,char embed) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(embed) ;    
    }
    
    // MessageRecordFactoryのJavaDoc
     public String findEmbedMessage(String key,int embed) {
         MessageRecord rec = this.findMessageRecord(key);
         return rec == null ? null : rec.makeMessage(embed) ;    
     }
    
    // MessageRecordFactoryのJavaDoc
    public String findEmbedMessage(String key,long embed) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(embed) ;    
    }
    
    // MessageRecordFactoryのJavaDoc
    public String findEmbedMessage(String key,float embed) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(embed) ;    
    }
    
    // MessageRecordFactoryのJavaDoc
    public String findEmbedMessage(String key,double embed) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(embed) ;    
    }
    
    // MessageRecordFactoryのJavaDoc
    public String findEmbedMessage(String key,boolean embed) {
        MessageRecord rec = this.findMessageRecord(key);
        return rec == null ? null : rec.makeMessage(embed) ;    
    }
}
