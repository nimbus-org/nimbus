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
 * &lt;default-log&gt;�v�f���^�f�[�^�B<p>
 * �T�[�r�X��`�t�@�C����&lt;server&gt;�̎q�v�f&lt;default-log&gt;�v�f�ɋL�q���ꂽ���e���i�[���郁�^�f�[�^�R���e�i�ł���B<p>
 *
 * @author M.Takata
 * @see <a href="nimbus-service_1_0.dtd">�T�[�r�X��`�t�@�C��DTD</a>
 */
public class DefaultLogMetaData extends MetaData implements Serializable{
    
    private static final long serialVersionUID = 154759319695696184L;
    
    /**
     * &lt;log&gt;�v�f�̗v�f��������B<p>
     */
    public static final String DEFAULT_LOG_TAG_NAME = "default-log";
    
    /**
     * &lt;debug&gt;�v�f�̗v�f��������B<p>
     */
    private static final String DEBUG_TAG_NAME = "debug";
    
    /**
     * &lt;information&gt;�v�f�̗v�f��������B<p>
     */
    private static final String INFORMATION_TAG_NAME = "information";
    
    /**
     * &lt;warning&gt;�v�f�̗v�f��������B<p>
     */
    private static final String WARNING_TAG_NAME = "warning";
    
    /**
     * &lt;error&gt;�v�f�̗v�f��������B<p>
     */
    private static final String ERROR_TAG_NAME = "error";
    
    /**
     * &lt;fatal&gt;�v�f�̗v�f��������B<p>
     */
    private static final String FATAL_TAG_NAME = "fatal";
    
    /**
     * ���O�J�e�S���v�f��output�����̑�����������B<p>
     */
    private static final String OUTPUT_ATTRIBUTE_NAME = "output";
    
    /**
     * &lt;debug&gt;�v�f�̃��^�f�[�^�B<p>
     *
     * @see #getDebug()
     */
    private LogCategoryMetaData debug;
    
    /**
     * &lt;information&gt;�v�f�̃��^�f�[�^�B<p>
     *
     * @see #getInformation()
     */
    private LogCategoryMetaData information;
    
    /**
     * &lt;warning&gt;�v�f�̃��^�f�[�^�B<p>
     *
     * @see #getWarning()
     */
    private LogCategoryMetaData warning;
    
    /**
     * &lt;error&gt;�v�f�̃��^�f�[�^�B<p>
     *
     * @see #getError()
     */
    private LogCategoryMetaData error;
    
    /**
     * &lt;fatal&gt;�v�f�̃��^�f�[�^�B<p>
     *
     * @see #getFatal()
     */
    private LogCategoryMetaData fatal;
    
    /**
     * �e�v�f�̃��^�f�[�^�����C���X�^���X�𐶐�����B<p>
     * DefaultLogMetaData�̐e�v�f�́A&lt;server&gt;�v�f��\��ServerMetaData�ł���B<br>
     * 
     * @param parent �e�v�f�̃��^�f�[�^
     * @see ServerMetaData
     */
    public DefaultLogMetaData(MetaData parent){
        super(parent);
    }
    
    /**
     * &lt;default-log&gt;�v�f�̎q�v�f&lt;debug&gt;�v�f�Ŏw�肳�ꂽ���O�J�e�S���̃��^�f�[�^���擾����B<p>
     * &lt;debug&gt;�v�f���w�肳��Ă��Ȃ��ꍇ�́Anull��Ԃ��B<br>
     *
     * @return &lt;debug&gt;�v�f�Ŏw�肳�ꂽ���O�J�e�S���̃��^�f�[�^
     */
    public LogCategoryMetaData getDebug(){
        return debug;
    }
    
    /**
     * &lt;default-log&gt;�v�f�̎q�v�f&lt;debug&gt;�v�f�Ŏw�肳�ꂽ���O�J�e�S���̃��^�f�[�^��ݒ肷��B<p>
     *
     * @param data &lt;debug&gt;�v�f�Ŏw�肳�ꂽ���O�J�e�S���̃��^�f�[�^
     */
    public void setDebug(LogCategoryMetaData data){
        debug = data;
    }
    
    /**
     * &lt;default-log&gt;�v�f�̎q�v�f&lt;information&gt;�v�f�Ŏw�肳�ꂽ���O�J�e�S���̃��^�f�[�^���擾����B<p>
     * &lt;information&gt;�v�f���w�肳��Ă��Ȃ��ꍇ�́Anull��Ԃ��B<br>
     *
     * @return &lt;information&gt;�v�f�Ŏw�肳�ꂽ���O�J�e�S���̃��^�f�[�^
     */
    public LogCategoryMetaData getInformation(){
        return information;
    }
    
    /**
     * &lt;default-log&gt;�v�f�̎q�v�f&lt;information&gt;�v�f�Ŏw�肳�ꂽ���O�J�e�S���̃��^�f�[�^��ݒ肷��B<p>
     *
     * @param data &lt;information&gt;�v�f�Ŏw�肳�ꂽ���O�J�e�S���̃��^�f�[�^
     */
    public void setInformation(LogCategoryMetaData data){
        information = data;
    }
    
    /**
     * &lt;default-log&gt;�v�f�̎q�v�f&lt;warning&gt;�v�f�Ŏw�肳�ꂽ���O�J�e�S���̃��^�f�[�^���擾����B<p>
     * &lt;warning&gt;�v�f���w�肳��Ă��Ȃ��ꍇ�́Anull��Ԃ��B<br>
     *
     * @return &lt;warning&gt;�v�f�Ŏw�肳�ꂽ���O�J�e�S���̃��^�f�[�^
     */
    public LogCategoryMetaData getWarning(){
        return warning;
    }
    
    /**
     * &lt;default-log&gt;�v�f�̎q�v�f&lt;warning&gt;�v�f�Ŏw�肳�ꂽ���O�J�e�S���̃��^�f�[�^��ݒ肷��B<p>
     *
     * @param data &lt;warning&gt;�v�f�Ŏw�肳�ꂽ���O�J�e�S���̃��^�f�[�^
     */
    public void setWarning(LogCategoryMetaData data){
        warning = data;
    }
    
    /**
     * &lt;default-log&gt;�v�f�̎q�v�f&lt;error&gt;�v�f�Ŏw�肳�ꂽ���O�J�e�S���̃��^�f�[�^���擾����B<p>
     * &lt;error&gt;�v�f���w�肳��Ă��Ȃ��ꍇ�́Anull��Ԃ��B<br>
     *
     * @return &lt;error&gt;�v�f�Ŏw�肳�ꂽ���O�J�e�S���̃��^�f�[�^
     */
    public LogCategoryMetaData getError(){
        return error;
    }
    
    /**
     * &lt;default-log&gt;�v�f�̎q�v�f&lt;error&gt;�v�f�Ŏw�肳�ꂽ���O�J�e�S���̃��^�f�[�^��ݒ肷��B<p>
     *
     * @param data &lt;error&gt;�v�f�Ŏw�肳�ꂽ���O�J�e�S���̃��^�f�[�^
     */
    public void setError(LogCategoryMetaData data){
        error = data;
    }
    
    /**
     * &lt;default-log&gt;�v�f�̎q�v�f&lt;fatal&gt;�v�f�Ŏw�肳�ꂽ���O�J�e�S���̃��^�f�[�^���擾����B<p>
     * &lt;fatal&gt;�v�f���w�肳��Ă��Ȃ��ꍇ�́Anull��Ԃ��B<br>
     *
     * @return &lt;fatal&gt;�v�f�Ŏw�肳�ꂽ���O�J�e�S���̃��^�f�[�^
     */
    public LogCategoryMetaData getFatal(){
        return fatal;
    }
    
    /**
     * &lt;default-log&gt;�v�f�̎q�v�f&lt;fatal&gt;�v�f�Ŏw�肳�ꂽ���O�J�e�S���̃��^�f�[�^��ݒ肷��B<p>
     *
     * @param data &lt;fatal&gt;�v�f�Ŏw�肳�ꂽ���O�J�e�S���̃��^�f�[�^
     */
    public void setFatal(LogCategoryMetaData data){
        fatal = data;
    }
    
    /**
     * &lt;log&gt;�v�f��Element���p�[�X���āA�������g�̏��������s���B<p>
     *
     * @param element &lt;log&gt;�v�f��Element
     * @exception DeploymentException &lt;log&gt;�v�f�̉�͂Ɏ��s�����ꍇ
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
     * &lt;debug&gt;�v�f�𐶐�����B<p>
     *
     * @return &lt;debug&gt;�v�f�̃��^�f�[�^
     */
    public LogCategoryMetaData createDebugLogCategoryMetaData(){
        return new LogCategoryMetaData(this, DEBUG_TAG_NAME);
    }
    
    /**
     * &lt;information&gt;�v�f�𐶐�����B<p>
     *
     * @return &lt;information&gt;�v�f�̃��^�f�[�^
     */
    public LogCategoryMetaData createInformationLogCategoryMetaData(){
        return new LogCategoryMetaData(this, INFORMATION_TAG_NAME);
    }
    
    /**
     * &lt;warning&gt;�v�f�𐶐�����B<p>
     *
     * @return &lt;warning&gt;�v�f�̃��^�f�[�^
     */
    public LogCategoryMetaData createWarningLogCategoryMetaData(){
        return new LogCategoryMetaData(this, WARNING_TAG_NAME);
    }
    
    /**
     * &lt;error&gt;�v�f�𐶐�����B<p>
     *
     * @return &lt;error&gt;�v�f�̃��^�f�[�^
     */
    public LogCategoryMetaData createErrorLogCategoryMetaData(){
        return new LogCategoryMetaData(this, ERROR_TAG_NAME);
    }
    
    /**
     * &lt;fatal&gt;�v�f�𐶐�����B<p>
     *
     * @return &lt;fatal&gt;�v�f�̃��^�f�[�^
     */
    public LogCategoryMetaData createFatalLogCategoryMetaData(){
        return new LogCategoryMetaData(this, FATAL_TAG_NAME);
    }
    
    /**
     * &lt;default-log&gt;�v�f�̎q�v�f�̃��O�J�e�S���̃��^�f�[�^�B<p>
     * �T�[�r�X��`�t�@�C����&lt;default-log&gt;�v�f�̎q�v�f&lt;debug&gt;�A&lt;information&gt;�A&lt;warning&gt;�A&lt;error&gt;�A&lt;fatal&gt;�v�f�ɋL�q���ꂽ���e���i�[���郁�^�f�[�^�R���e�i�̊��N���X�ł���B<p>
     *
     * @author M.Takata
     * @see <a href="nimbus-service_1_0.dtd">�T�[�r�X��`�t�@�C��DTD</a>
     */
    public static class LogCategoryMetaData
     extends MetaData implements Serializable{
        
        private static final long serialVersionUID = 7428020116195385080L;
        
        /**
         * ���̗v�f�̖��O�B<p>
         */
        private String tagName;
        
        /**
         * output�����̒l�B<p>
         * �Y������J�e�S���̃��O���o�͂��邩�ǂ����������t���O�B<br>
         *
         * @see #isOutput()
         */
        private boolean isOutput;
        
        /**
         * �e�v�f�̃��^�f�[�^�����C���X�^���X�𐶐�����B<p>
         * LogCategoryMetaData�̐e�v�f�́A&lt;log&gt;�v�f��\��DefaultLogMetaData�ł���B<br>
         * 
         * @param parent �e�v�f�̃��^�f�[�^
         * @see DefaultLogMetaData
         */
        public LogCategoryMetaData(MetaData parent){
            super(parent);
        }
        
        /**
         * �e�v�f�̃��^�f�[�^�����C���X�^���X�𐶐�����B<p>
         * LogCategoryMetaData�̐e�v�f�́A&lt;log&gt;�v�f��\��DefaultLogMetaData�ł���B<br>
         * 
         * @param parent �e�v�f�̃��^�f�[�^
         * @param name ���̗v�f�̖��O
         * @see DefaultLogMetaData
         */
        public LogCategoryMetaData(MetaData parent, String name){
            super(parent);
            tagName = name;
        }
        
        /**
         * ���O�J�e�S���v�f��output�����̒l���擾����B<p>
         * output�������ȗ�����Ă����ꍇ�́Afalse��Ԃ��B<br>
         * 
         * @return ���O�J�e�S���v�f��output�����̒l
         */
        public boolean isOutput(){
            return isOutput;
        }
        
        /**
         * ���O�J�e�S���v�f��output�����̒l��ݒ肷��B<p>
         * 
         * @param isOutput ���O�J�e�S���v�f��output�����̒l
         */
        public void setOutput(boolean isOutput){
            this.isOutput = isOutput;
        }
        
        /**
         * ���O�J�e�S���v�f��Element���p�[�X���āA�������g�̏��������s���B<p>
         *
         * @param element ���O�J�e�S���v�f��Element
         * @exception DeploymentException ���O�J�e�S���v�f�̉�͂Ɏ��s�����ꍇ
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
