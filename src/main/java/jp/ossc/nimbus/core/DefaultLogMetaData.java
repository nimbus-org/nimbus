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
package jp.ossc.nimbus.core;

import java.io.*;
import org.w3c.dom.*;


/**
 * &lt;default-log&gt;要素メタデータ。<p>
 * サービス定義ファイルの&lt;server&gt;の子要素&lt;default-log&gt;要素に記述された内容を格納するメタデータコンテナである。<p>
 *
 * @author M.Takata
 * @see <a href="nimbus-service_1_0.dtd">サービス定義ファイルDTD</a>
 */
public class DefaultLogMetaData extends MetaData implements Serializable{
    
    private static final long serialVersionUID = 154759319695696184L;
    
    /**
     * &lt;log&gt;要素の要素名文字列。<p>
     */
    public static final String DEFAULT_LOG_TAG_NAME = "default-log";
    
    /**
     * &lt;debug&gt;要素の要素名文字列。<p>
     */
    private static final String DEBUG_TAG_NAME = "debug";
    
    /**
     * &lt;information&gt;要素の要素名文字列。<p>
     */
    private static final String INFORMATION_TAG_NAME = "information";
    
    /**
     * &lt;warning&gt;要素の要素名文字列。<p>
     */
    private static final String WARNING_TAG_NAME = "warning";
    
    /**
     * &lt;error&gt;要素の要素名文字列。<p>
     */
    private static final String ERROR_TAG_NAME = "error";
    
    /**
     * &lt;fatal&gt;要素の要素名文字列。<p>
     */
    private static final String FATAL_TAG_NAME = "fatal";
    
    /**
     * ログカテゴリ要素のoutput属性の属性名文字列。<p>
     */
    private static final String OUTPUT_ATTRIBUTE_NAME = "output";
    
    /**
     * &lt;debug&gt;要素のメタデータ。<p>
     *
     * @see #getDebug()
     */
    private LogCategoryMetaData debug;
    
    /**
     * &lt;information&gt;要素のメタデータ。<p>
     *
     * @see #getInformation()
     */
    private LogCategoryMetaData information;
    
    /**
     * &lt;warning&gt;要素のメタデータ。<p>
     *
     * @see #getWarning()
     */
    private LogCategoryMetaData warning;
    
    /**
     * &lt;error&gt;要素のメタデータ。<p>
     *
     * @see #getError()
     */
    private LogCategoryMetaData error;
    
    /**
     * &lt;fatal&gt;要素のメタデータ。<p>
     *
     * @see #getFatal()
     */
    private LogCategoryMetaData fatal;
    
    /**
     * 親要素のメタデータを持つインスタンスを生成する。<p>
     * DefaultLogMetaDataの親要素は、&lt;server&gt;要素を表すServerMetaDataである。<br>
     * 
     * @param parent 親要素のメタデータ
     * @see ServerMetaData
     */
    public DefaultLogMetaData(MetaData parent){
        super(parent);
    }
    
    /**
     * &lt;default-log&gt;要素の子要素&lt;debug&gt;要素で指定されたログカテゴリのメタデータを取得する。<p>
     * &lt;debug&gt;要素が指定されていない場合は、nullを返す。<br>
     *
     * @return &lt;debug&gt;要素で指定されたログカテゴリのメタデータ
     */
    public LogCategoryMetaData getDebug(){
        return debug;
    }
    
    /**
     * &lt;default-log&gt;要素の子要素&lt;debug&gt;要素で指定されたログカテゴリのメタデータを設定する。<p>
     *
     * @param data &lt;debug&gt;要素で指定されたログカテゴリのメタデータ
     */
    public void setDebug(LogCategoryMetaData data){
        debug = data;
    }
    
    /**
     * &lt;default-log&gt;要素の子要素&lt;information&gt;要素で指定されたログカテゴリのメタデータを取得する。<p>
     * &lt;information&gt;要素が指定されていない場合は、nullを返す。<br>
     *
     * @return &lt;information&gt;要素で指定されたログカテゴリのメタデータ
     */
    public LogCategoryMetaData getInformation(){
        return information;
    }
    
    /**
     * &lt;default-log&gt;要素の子要素&lt;information&gt;要素で指定されたログカテゴリのメタデータを設定する。<p>
     *
     * @param data &lt;information&gt;要素で指定されたログカテゴリのメタデータ
     */
    public void setInformation(LogCategoryMetaData data){
        information = data;
    }
    
    /**
     * &lt;default-log&gt;要素の子要素&lt;warning&gt;要素で指定されたログカテゴリのメタデータを取得する。<p>
     * &lt;warning&gt;要素が指定されていない場合は、nullを返す。<br>
     *
     * @return &lt;warning&gt;要素で指定されたログカテゴリのメタデータ
     */
    public LogCategoryMetaData getWarning(){
        return warning;
    }
    
    /**
     * &lt;default-log&gt;要素の子要素&lt;warning&gt;要素で指定されたログカテゴリのメタデータを設定する。<p>
     *
     * @param data &lt;warning&gt;要素で指定されたログカテゴリのメタデータ
     */
    public void setWarning(LogCategoryMetaData data){
        warning = data;
    }
    
    /**
     * &lt;default-log&gt;要素の子要素&lt;error&gt;要素で指定されたログカテゴリのメタデータを取得する。<p>
     * &lt;error&gt;要素が指定されていない場合は、nullを返す。<br>
     *
     * @return &lt;error&gt;要素で指定されたログカテゴリのメタデータ
     */
    public LogCategoryMetaData getError(){
        return error;
    }
    
    /**
     * &lt;default-log&gt;要素の子要素&lt;error&gt;要素で指定されたログカテゴリのメタデータを設定する。<p>
     *
     * @param data &lt;error&gt;要素で指定されたログカテゴリのメタデータ
     */
    public void setError(LogCategoryMetaData data){
        error = data;
    }
    
    /**
     * &lt;default-log&gt;要素の子要素&lt;fatal&gt;要素で指定されたログカテゴリのメタデータを取得する。<p>
     * &lt;fatal&gt;要素が指定されていない場合は、nullを返す。<br>
     *
     * @return &lt;fatal&gt;要素で指定されたログカテゴリのメタデータ
     */
    public LogCategoryMetaData getFatal(){
        return fatal;
    }
    
    /**
     * &lt;default-log&gt;要素の子要素&lt;fatal&gt;要素で指定されたログカテゴリのメタデータを設定する。<p>
     *
     * @param data &lt;fatal&gt;要素で指定されたログカテゴリのメタデータ
     */
    public void setFatal(LogCategoryMetaData data){
        fatal = data;
    }
    
    /**
     * &lt;log&gt;要素のElementをパースして、自分自身の初期化を行う。<p>
     *
     * @param element &lt;log&gt;要素のElement
     * @exception DeploymentException &lt;log&gt;要素の解析に失敗した場合
     */
    public void importXML(Element element) throws DeploymentException{
        super.importXML(element);
        
        if(!element.getTagName().equals(DEFAULT_LOG_TAG_NAME)){
            throw new DeploymentException(
                "Tag must be " + DEFAULT_LOG_TAG_NAME + " : "
                 + element.getTagName()
            );
        }
        final Element debugElement
             = getOptionalChild(element, DEBUG_TAG_NAME);
        if(debugElement != null){
            debug = new LogCategoryMetaData(this);
            debug.importXML(debugElement);
        }
        final Element informationElement
             = getOptionalChild(element, INFORMATION_TAG_NAME);
        if(informationElement != null){
            information = new LogCategoryMetaData(this);
            information.importXML(informationElement);
        }
        final Element warningElement
             = getOptionalChild(element, WARNING_TAG_NAME);
        if(warningElement != null){
            warning = new LogCategoryMetaData(this);
            warning.importXML(warningElement);
        }
        final Element errorElement
             = getOptionalChild(element, ERROR_TAG_NAME);
        if(errorElement != null){
            error = new LogCategoryMetaData(this);
            error.importXML(errorElement);
        }
        final Element fatalElement
             = getOptionalChild(element, FATAL_TAG_NAME);
        if(fatalElement != null){
            fatal = new LogCategoryMetaData(this);
            fatal.importXML(fatalElement);
        }
    }
    
    public StringBuilder toXML(StringBuilder buf){
        appendComment(buf);
        buf.append('<').append(DEFAULT_LOG_TAG_NAME).append('>');
        if(debug != null){
            buf.append(LINE_SEPARATOR);
            buf.append(
                addIndent(debug.toXML(new StringBuilder()))
            );
        }
        if(information != null){
            buf.append(LINE_SEPARATOR);
            buf.append(
                addIndent(information.toXML(new StringBuilder()))
            );
        }
        if(warning != null){
            buf.append(LINE_SEPARATOR);
            buf.append(
                addIndent(warning.toXML(new StringBuilder()))
            );
        }
        if(error != null){
            buf.append(LINE_SEPARATOR);
            buf.append(
                addIndent(error.toXML(new StringBuilder()))
            );
        }
        if(fatal != null){
            buf.append(LINE_SEPARATOR);
            buf.append(
                addIndent(fatal.toXML(new StringBuilder()))
            );
        }
        buf.append(LINE_SEPARATOR);
        buf.append("</").append(DEFAULT_LOG_TAG_NAME).append('>');
        return buf;
    }
    
    /**
     * &lt;debug&gt;要素を生成する。<p>
     *
     * @return &lt;debug&gt;要素のメタデータ
     */
    public LogCategoryMetaData createDebugLogCategoryMetaData(){
        return new LogCategoryMetaData(this, DEBUG_TAG_NAME);
    }
    
    /**
     * &lt;information&gt;要素を生成する。<p>
     *
     * @return &lt;information&gt;要素のメタデータ
     */
    public LogCategoryMetaData createInformationLogCategoryMetaData(){
        return new LogCategoryMetaData(this, INFORMATION_TAG_NAME);
    }
    
    /**
     * &lt;warning&gt;要素を生成する。<p>
     *
     * @return &lt;warning&gt;要素のメタデータ
     */
    public LogCategoryMetaData createWarningLogCategoryMetaData(){
        return new LogCategoryMetaData(this, WARNING_TAG_NAME);
    }
    
    /**
     * &lt;error&gt;要素を生成する。<p>
     *
     * @return &lt;error&gt;要素のメタデータ
     */
    public LogCategoryMetaData createErrorLogCategoryMetaData(){
        return new LogCategoryMetaData(this, ERROR_TAG_NAME);
    }
    
    /**
     * &lt;fatal&gt;要素を生成する。<p>
     *
     * @return &lt;fatal&gt;要素のメタデータ
     */
    public LogCategoryMetaData createFatalLogCategoryMetaData(){
        return new LogCategoryMetaData(this, FATAL_TAG_NAME);
    }
    
    /**
     * &lt;default-log&gt;要素の子要素のログカテゴリのメタデータ。<p>
     * サービス定義ファイルの&lt;default-log&gt;要素の子要素&lt;debug&gt;、&lt;information&gt;、&lt;warning&gt;、&lt;error&gt;、&lt;fatal&gt;要素に記述された内容を格納するメタデータコンテナの基底クラスである。<p>
     *
     * @author M.Takata
     * @see <a href="nimbus-service_1_0.dtd">サービス定義ファイルDTD</a>
     */
    public static class LogCategoryMetaData
     extends MetaData implements Serializable{
        
        private static final long serialVersionUID = 7428020116195385080L;
        
        /**
         * この要素の名前。<p>
         */
        private String tagName;
        
        /**
         * output属性の値。<p>
         * 該当するカテゴリのログを出力するかどうかを示すフラグ。<br>
         *
         * @see #isOutput()
         */
        private boolean isOutput;
        
        /**
         * 親要素のメタデータを持つインスタンスを生成する。<p>
         * LogCategoryMetaDataの親要素は、&lt;log&gt;要素を表すDefaultLogMetaDataである。<br>
         * 
         * @param parent 親要素のメタデータ
         * @see DefaultLogMetaData
         */
        public LogCategoryMetaData(MetaData parent){
            super(parent);
        }
        
        /**
         * 親要素のメタデータを持つインスタンスを生成する。<p>
         * LogCategoryMetaDataの親要素は、&lt;log&gt;要素を表すDefaultLogMetaDataである。<br>
         * 
         * @param parent 親要素のメタデータ
         * @param name この要素の名前
         * @see DefaultLogMetaData
         */
        public LogCategoryMetaData(MetaData parent, String name){
            super(parent);
            tagName = name;
        }
        
        /**
         * ログカテゴリ要素のoutput属性の値を取得する。<p>
         * output属性が省略されていた場合は、falseを返す。<br>
         * 
         * @return ログカテゴリ要素のoutput属性の値
         */
        public boolean isOutput(){
            return isOutput;
        }
        
        /**
         * ログカテゴリ要素のoutput属性の値を設定する。<p>
         * 
         * @param isOutput ログカテゴリ要素のoutput属性の値
         */
        public void setOutput(boolean isOutput){
            this.isOutput = isOutput;
        }
        
        /**
         * ログカテゴリ要素のElementをパースして、自分自身の初期化を行う。<p>
         *
         * @param element ログカテゴリ要素のElement
         * @exception DeploymentException ログカテゴリ要素の解析に失敗した場合
         */
        public void importXML(Element element) throws DeploymentException{
            super.importXML(element);
            tagName = element.getTagName();
            final String output = getOptionalAttribute(
                element,
                OUTPUT_ATTRIBUTE_NAME
            );
            if(output != null){
                isOutput = Boolean.valueOf(output).booleanValue();
            }
        }
        
        public StringBuilder toXML(StringBuilder buf){
            appendComment(buf);
            buf.append('<').append(tagName);
            buf.append(' ').append(OUTPUT_ATTRIBUTE_NAME)
                .append("=\"").append(isOutput).append("\"");
            buf.append("/>");
            return buf;
        }
    }
}
