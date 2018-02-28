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
package jp.ossc.nimbus.service.writer;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.beans.*;
import jp.ossc.nimbus.lang.*;
import java.util.*;
import java.io.Serializable;

/**
 * java.util.MapをWritableRecordに変換する{@link WritableRecordFactory}サービス。<p>
 *
 * @author Y.Tokuda
 */
public class WritableRecordFactoryService extends ServiceBase
 implements WritableRecordFactory, WritableRecordFactoryServiceMBean,
            Serializable{
    
    private static final long serialVersionUID = 5249532509800152052L;
    
    //メンバ変数
    /** シンプルエレメント実装クラス名 */
    protected static final String SIMPLE_ELEMENT_NAME
         = SimpleElement.class.getName();
    /** フォーマット定義文字列 */
    protected String mFormat;
    /** フォーマット定義文字列から生成されるParsedElement型のArrayList */
    protected List mParsedElements;
    /** WritableElementの実装クラス名ハッシュ */
    protected Properties mImplClasses;
    /** WritableElementの実装サービス名ハッシュ */
    protected Properties mImplServiceNames;
    protected Map mImplServiceNameMap;
    
    // WritableRecordFactoryServiceMBeanのJavaDoc
    public void setImplementClasses(Properties prop){
        mImplClasses = prop;
    }
    
    // WritableRecordFactoryServiceMBeanのJavaDoc
    public Properties getImplementClasses(){
        return mImplClasses;
    }
    
    // WritableRecordFactoryServiceMBeanのJavaDoc
    public void setImplementServiceNames(Properties prop){
        mImplServiceNames = prop;
    }
    
    // WritableRecordFactoryServiceMBeanのJavaDoc
    public Properties getImplementServiceNames(){
        return mImplServiceNames;
    }
    
    // WritableRecordFactoryServiceMBeanのJavaDoc
    public void setFormat(String fmt){
        mFormat = fmt;
    }
    
    // WritableRecordFactoryServiceMBeanのJavaDoc
    public String getFormat(){
        return mFormat;
    }
    
    /**
     * 生成処理を行う。<p>
     * このメソッドには、以下の実装が行われている。<br>
     * <ol>
     * <li> セッターでキーワード,WritableElementの実装クラス名の組を格納したPropertiesが与えられなかった場合、空のPropertiesをメンバ変数mImplClassesにセットする。<br>
     * </ol>
     *
     * @exception Exception サービスの生成処理に失敗した場合
     */
    public void createService() throws Exception {
        if(mImplClasses == null){
            mImplClasses = new Properties();
        }
        mImplServiceNameMap = new HashMap();
    }
    
    /**
     * 開始処理を行う。<p>
     * このメソッドには、以下の実装がおこなわれている。<br>
     * <ol>
     * <li>フォーマット文字列をパースする。</li>
     * <li>WritableElementの実装クラスがインスタンス化可能かチェックする。</li>
     * </ol>
     *
     * @exception Exception サービスの開始処理に失敗した場合
     */    
    public void startService()throws Exception {
        try{
            mParsedElements = parseFormat(mFormat);
        }
        catch(IllegalArgumentException ex){
            //不正フォーマットの場合
            getLogger().write("SSWRF00001",mFormat,ex);
            throw ex;        
        }
        //与えられた実装クラス名でインスタンスを生成できるかどうか確認
        Collection implClasses = mImplClasses.values();
        Iterator it = implClasses.iterator();
        while(it.hasNext()){
            String implClassName = (String)it.next();
            try{
                getInstance(implClassName);    
            }
            catch(ServiceException e){
                //不正実装クラス名の場合
                getLogger().write("SSWRF00002",implClassName,e);
                throw e;    
            }
        }
        
        if(mImplServiceNames != null){
            mImplServiceNameMap.clear();
            final ServiceNameEditor editor = new ServiceNameEditor();
            editor.setServiceManagerName(getServiceManagerName());
            final Iterator keys = mImplServiceNames.keySet().iterator();
            while(keys.hasNext()){
                final String key = (String)keys.next();
                editor.setAsText(mImplServiceNames.getProperty(key));
                mImplServiceNameMap.put(
                    key,
                    editor.getValue()
                );
            }
        }
    }
    
    /**
     * 停止処理を行う。<p>
     * このメソッドには、以下の実装がおこなわれている。<br>
     * <ol>
     * <li>mParsedElementを破棄する</li>
     * </ol>
     *
     * @exception Exception サービスの停止処理に失敗した場合
     */    
    public void stopService() throws Exception {
        mParsedElements = null;
    }
    /**
     * 破棄処理を行う。<p>
     * このメソッドには、以下の実装がおこなわれている。<br>
     * <ol>
     * <li>mImplClassesを破棄する</li>
     * </ol>
     *
     * @exception Exception サービスの破棄処理に失敗した場合
     */            
    public void destroyService() throws Exception {
        mImplClasses = null;
    }
    
    /**
     * 指定された出力要素のMapから{@link WritableRecord}を生成する。<p>
     * {@link #setFormat(String)}で指定されたフォーマットに従って、{@link WritableElement}を生成し、それを順次格納したWritableRecordを返す。<br>
     * setFormat(String)で指定されたフォーマット内に、キーが指定されている場合は、出力要素のMapから値を取り出して、{@link #setImplementClasses(Properties)}または{@link #setImplementServiceNames(Properties)}でマッピングされたWritableElementインスタンスに格納する。<br>
     * 
     * @param elements 出力要素のMapオブジェクト
     * @return MessageWriterサービスの入力オブジェクトとなるWritableRecord
     */
    public WritableRecord createRecord(Object elements){
        if(getState() != STARTED){
            throw new IllegalServiceStateException(this);
        }
        WritableRecord writableRec = new WritableRecord();
        if(mFormat == null || mFormat.length() == 0){
            //単にelementsの内容をWritableRecordにadd
            final Iterator keys = getElementKeys(elements).iterator();
            while(keys.hasNext()){
                final String key = keys.next().toString();
                WritableElement elem = createElement(
                    key,
                    getElementValue(key, elements)
                );
                writableRec.addElement(elem);
            }
        }
        else{
            //フォーマットに従ってWritableRecordを生成
            for(int rCnt=0, max = mParsedElements.size();rCnt < max;rCnt++){
                ParsedElement parsedElem = (ParsedElement)mParsedElements.get(rCnt);
                if(parsedElem.isKeyElement()){
                    //キーなので、該当キーをもつ項目をelementsから探し、WritableRecordにaddする。
                    final String key = parsedElem.getValue();
                    WritableElement elem = createElement(
                        key,
                        getElementValue(key, elements)
                    );
                    if(elem != null){
                        writableRec.addElement(elem);
                    }
                }
                else{
                    //表示用文字列要素をwritableRecordにadd
                    SimpleElement simpleElem = new SimpleElement();
                    simpleElem.setKey(simpleElem);
                    simpleElem.setValue(parsedElem.getValue());
                    writableRec.addElement(simpleElem);
                }
            }
        }
        return writableRec;
    }
    
    /**
     * 指定された出力要素のMapが持つキー名の集合を取得する。<p>
     *
     * @param elements 出力要素のマップ
     * @return キー名の集合
     */
    protected Set getElementKeys(Object elements){
        return ((Map)elements).keySet();
    }
    
    /**
     * 出力要素から指定されたキーの値を取り出して、対応する{@link WritableElement}を生成する。<p>
     * {@link #getElementValue(String, Object)}で、出力要素から指定されたキーの値を取り出す。{@link #getImplementClass(String)}または{@link #getImplementServiceName(String)}で、キーに該当する{@link WritableElement}を特定し、そのインスタンスを取得して、キーと値を設定して返す。<br>
     * キーに該当する{@link WritableElement}が特定できない場合は、{@link SimpleElement}クラスを使用する。<br>
     * 
     * @param key キー
     * @param val 出力要素
     * @return WritableElementインスタンス
     */
    protected WritableElement createElement(String key, Object val){
        WritableElement writableElem = null;
        if(getImplementClass(key) == null){
            if(getImplementServiceName(key) == null){
                //実装クラス名が取得できない時はSimpleElementにする。
                writableElem = getInstance(SIMPLE_ELEMENT_NAME);
            }else{
                try{
                    writableElem = (WritableElement)ServiceManagerFactory.getServiceObject(getImplementServiceName(key));
                }catch(ServiceNotFoundException e){
                    writableElem = getInstance(SIMPLE_ELEMENT_NAME);
                }
            }
        }
        else{
            String implClassName = getImplementClass(key);
            writableElem = getInstance(implClassName);
        }
        //キーと値を設定
        writableElem.setKey(key);
        writableElem.setValue(val);
        postCreateElement(writableElem);
        return writableElem;
    }
    
    /**
     * 指定されたキーに対して指定されたクラス名の{@link WritableElement}実装クラスをマッピングする。<p>
     *
     * @param key キー
     * @param className WritableElement実装クラス名
     */
    protected void setImplementClass(String key, String className){
        mImplClasses.put(key, className);
    }
    
    /**
     * 指定されたキーに対する{@link WritableElement}実装クラス名を取得する。<p>
     *
     * @param key キー
     * @return WritableElement実装クラス名
     */
    protected String getImplementClass(String key){
        return (String)mImplClasses.get(key);
    }
    
    /**
     * 指定されたキーに対する{@link WritableElement}実装サービス名を取得する。<p>
     *
     * @param key キー
     * @return WritableElement実装サービス名
     */
    protected ServiceName getImplementServiceName(String key){
        return (ServiceName)mImplServiceNameMap.get(key);
    }
    
    /**
     * {@link WritableElement}を生成した後の処理を行う。<p>
     * {@link #createElement(String, Object)}で生成したWritableElementに対して任意の処理を行う。<br>
     * このクラスを継承するクラスで必要な実装を行う。ここでは空実装。<br>
     * 
     * @param elem 処理対象のWritableElement
     */
    protected void postCreateElement(WritableElement elem){
        //空実装
        ;
    }
    
    /**
     * 指定された出力要素のマップから、指定されたキーの値を取得する。<p>
     *
     * @param key 出力要素マップ内のキー
     * @param elements 出力要素マップ
     * @return 出力要素内のキーに該当する値
     */
    protected Object getElementValue(String key, Object elements){
        return ((Map)elements).get(key);
    }
    
    /**
     * 指定したクラスのインスタンスを取得する。<p>
     * 
     * @param className インスタンス化するクラスの名前
     * @return インスタンス
     */
    protected WritableElement getInstance(String className){
        WritableElement writableElem = null;
        try{
            Class clazz = Class.forName(
                className,
                true,
                NimbusClassLoader.getInstance()
            );
            writableElem = (WritableElement)clazz.newInstance();
        }catch(IllegalAccessException e){
            throw new ServiceException(
                "WRITABLERECORDFACTORY002",
                "IllegalAccess When creatCuiOperator() ",
                e
            );
        }catch(InstantiationException e){
            throw new ServiceException(
                "WRITABLERECORDFACTORY003",
                "Instanting failed",
                e
            );
        }catch(ClassNotFoundException e){
            throw new ServiceException(
                "WRITABLERECORDFACTORY004",
                "Class not found. name is " + className,
                e
            );
        }
        return writableElem;
    }
    
    /**
     * フォーマット文字列パースメソッド。<p>
     * setFormat(String)で、セットされたフォーマット文字列mFormatをパースし、ParsedElementのListを返す
     * <ol>
     * <li>'%'で囲んだ文字列をキーワードと認識する。</li>
     * <li>'\%'記述すると、文字列の'%'として認識される。</li>
     * <li>'\\'と記述すると、文字列の'\'として認識される。</li>
     * <li>フォーマット文字列がnullまたは空文字のとき、nullを返す。</li>
     * <li>フォーマット文字列において、"%"で始めたキーワードの記述が%で閉じられていない場合、IllegalArgumentExceptionをthrowする。</li>
     * <li>(例) フォーマット文字列が"今日は %D% です。"の場合、3つのParsedElementを含むListが返却される。</li> 
     *         <ul>
     *            <li>最初のParsedElementのmValueは"今日は "、mIsKeyWordはfalse</li>
     *            <li>2番目のParsedElementのmValueは"D"、mIsKeyWordはtrue</li>
     *            <li>3番目のParsedElementのmValueは" です。"、mIsKeyWordはfalse</li>
     *         </ul>
     * <li>フォーマット文字列がnullもしくは空文字の場合、nullを返す。</li>
     * </ol>
     * 
     * @return ParsedElementのList
     */
    protected List parseFormat(String format){
        //初期化
        if(format == null || format.length() == 0 ){
            //nullを返す
            return null;
        }
        List result = new ArrayList();
        StringBuilder word = new StringBuilder("");
        boolean isStartKey = false;
        boolean isEscape = false;
        for(int i = 0, max = format.length(); i < max; i++){
            final char c = format.charAt(i);
            switch(c){
            case '%':
                if(isEscape){
                    word.append(c);
                    isEscape = false;
                }else if(isStartKey){
                    if(word.length() != 0){
                        //キーワードとして追加
                        final ParsedElement elem = new ParsedElement(
                            word.toString(),
                            true
                        );
                        result.add(elem);
                        word.setLength(0);
                        isStartKey = false;
                    }else{
                        throw new IllegalArgumentException(
                            "Keyword must not be null. : " + format
                        );
                    }
                }else{
                    if(word.length() > 0){
                        //固定メッセージとして追加
                        final ParsedElement elem = new ParsedElement(
                            word.toString(),
                            false
                        );
                        result.add(elem);
                        word.setLength(0);
                    }
                    isStartKey = true;
                }
                break;
            case '\\':
                if(isEscape){
                    word.append(c);
                    isEscape = false;
                }else{
                    isEscape = true;
                }
                break;
            default:
                if(isEscape){
                    throw new IllegalArgumentException(
                        "'\' is escape character. : " + format
                    );
                }else{
                    word.append(c);
                }
                break;
            }
        }
        if(isEscape){
            throw new IllegalArgumentException(
                "'\' is escape character. : " + format
            );
        }
        if(isStartKey){
            throw new IllegalArgumentException(
                "'%' is key separator character. : " + format
            );
        }
        if(word.length() > 0){
            //固定メッセージとして追加
            final ParsedElement elem = new ParsedElement(
                word.toString(),
                false
            );
            result.add(elem);
            word.setLength(0);
        }
        return result;
    }
    
    /**
     * パースされたフォーマットの要素を表すクラス。<p>
     * Stringの値と、keyか否かの識別(boolean)値を持つ。
     * 
     * @author Y.Tokuda
     */
    protected static class ParsedElement implements Serializable{
        
        private static final long serialVersionUID = 6554326776504636150L;
        
        //メンバ変数
        protected String mVal;
        protected boolean mIsKeyWord;
        
        /**
         * コンストラクタ。<p>
         *
         * @param val フォーマットの要素文字列
         * @param isKey キーかどうかを示すフラグ
         */
        public ParsedElement(String val, boolean isKey){
            mVal = val;
            mIsKeyWord = isKey;
        }
        
        /**
         * フォーマットの要素文字列を取得する。<p>
         *
         * @return フォーマットの要素文字列
         */
        public String getValue(){
            return mVal;
        }
        
        /**
         * この要素がキーかどうかを判定する。<p>
         *
         * @return この要素がキーの場合true
         */
        public boolean isKeyElement(){
            return mIsKeyWord ;
        }
    }
}
