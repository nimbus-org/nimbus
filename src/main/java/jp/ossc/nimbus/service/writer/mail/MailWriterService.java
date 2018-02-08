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
package jp.ossc.nimbus.service.writer.mail;

import java.util.*;
import java.io.*;
import javax.activation.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.naming.NamingException;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.util.*;
import jp.ossc.nimbus.service.writer.*;
import jp.ossc.nimbus.service.keepalive.KeepAliveCheckerSelector;
import jp.ossc.nimbus.service.keepalive.smtp.SmtpKeepAliveChecker;
import jp.ossc.nimbus.service.jndi.JndiFinder;

/**
 * ���[�����M�T�[�r�X�B<p>
 * Java Mail�ł́ASession�̎擾�y�ѐݒ�AMessage�̐����y�ѐݒ�ASMTP�T�[�o�ւ̑��M�̋@�\������B<br>
 * ���̃T�[�r�X�ł́A�����̋@�\���m���v���O���~���O�ōs�������ł���B<br>
 * <p>
 * Java Mail�ł�Session�̎擾�ɂ́A�Q��ނ̕��@������B<br>
 * {@link Session#getInstance(Properties, Authenticator)}�ŁA�C�ӂ̃Z�b�V�����������w�肵��Session���擾������@�B<br>
 * �A�v���P�[�V�����T�[�o�Őݒ肵��Session��JNDI�o�R�Ŏ擾������@�B<br>
 * ���̃T�[�r�X�ł́A���̑o�����T�|�[�g���܂��B<br>
 * �܂��A�O�҂̕��@�̏ꍇ�́A{@link #setSessionProperties(Properties)}�ŁA�ݒ肵���Z�b�V�����������ꗥ�ɐݒ肷��B<br>
 * �A���A�ȉ��̃Z�b�V���������́A�ꗥ�w�肪�Ȃ��ꍇ�́A���ꂼ��ȉ��̕��@�Őݒ肳���B<br>
 * <table border="1">
 *   <tr bgcolor="#CCCCFF"><th>�Z�b�V��������</th><th>�f�t�H���g�̐ݒ���@</th></tr>
 *   <tr><td>mail.smtp.host</td><td>{@link #setSmtpKeepAliveCheckerSelectorServiceName(ServiceName)}���w�肳��Ă���ꍇ�́A�w�肳�ꂽ{@link KeepAliveCheckerSelector}�ɂ���đI�����ꂽ{@link SmtpKeepAliveChecker}��{@link SmtpKeepAliveChecker#getHostIp()}�Ŏ擾�����l�B<br>�����łȂ��ꍇ�́A{@link #setSmtpHostName(String)}�Ŏw�肳�ꂽ�l�B</td></tr>
 *   <tr><td>mail.smtp.port</td><td>{@link #setSmtpKeepAliveCheckerSelectorServiceName(ServiceName)}���w�肳��Ă���ꍇ�́A�w�肳�ꂽ{@link KeepAliveCheckerSelector}�ɂ���đI�����ꂽ{@link SmtpKeepAliveChecker}��{@link SmtpKeepAliveChecker#getHostPort()}�Ŏ擾�����l�B<br>�����łȂ��ꍇ�́A{@link #setSmtpPort(int)}�Ŏw�肳�ꂽ�l�B</td></tr>
 *   <tr><td>mail.transport.protocol</td><td>smtp</td></tr>
 *   <tr><td>mail.smtp.from</td><td>{@link #setEnvelopeFromAddressKey(String)}�Ŏw�肳�ꂽ�L�[�ŁA{@link #write(WritableRecord)}�̈�����{@link WritableRecord}����擾�����l�B<br>�w�肳��Ă��Ȃ��ꍇ�́A{@link #setEnvelopeFromAddress(String)}�Ŏw�肳�ꂽ�l�B</td></tr>
 * </table>
 * <p>
 * Message�̐����́Ajavax.mail.MimeMessage�𐶐����܂��B<br>
 * MimeMessage�ւ̊e��ݒ�́A���̃T�[�r�X�̊e�����Őݒ�\�ł���B<br>
 * �e�������ɁA�Q��ނ̐ݒ���@���p�ӂ���Ă���B<br>
 * {@link #write(WritableRecord)}�̈�����{@link WritableRecord}����擾���邽�߂̃L�[�����w�肷����@�ƁA�����l���̂��̂�ݒ肷����@�ł���B<br>
 * �O�҂̏ꍇ�́A����MessageWriter���ĂԃN���C�A���g�������ɂ���āA�C�ӂɎw�肷�鎖���\�ŁA��҂̕��@�́A���̃T�[�r�X�̐ݒ�ňꗥ�����l��ݒ肷�鎖���\�ł���B<br>
 * ���鑮���ɑ΂��āA��L�̂Q��ނ̐ݒ���@�������Ƃ��ݒ肳��Ă���ꍇ�ɂ́A�O�҂̕����L���ƂȂ�B<br>
 * �܂��A{@link Message#setSentDate(Date)}�����́A�����I�ɑ��M���̎������ݒ肳���B<br>
 * <p>
 * SMTP�T�[�o�ւ̑��M�@�\�Ƃ��ẮA������SMTP�T�[�o�ɑ΂��ă��[�h�o�����X���Ȃ��瑗�M����@�\�ƁA���M���s���ɁA���̊Ԋu�������āA���g���C����@�\������B<br>
 *
 * @author M.Takata
 */
public class MailWriterService extends ServiceBase
 implements MessageWriter, MailWriterServiceMBean{
    
    private static final long serialVersionUID = 8479884337523286206L;
    
    private static final String SESSION_PROPERTY_NAME_HOST = "mail.smtp.host";
    private static final String SESSION_PROPERTY_NAME_PORT = "mail.smtp.port";
    private static final String SESSION_PROPERTY_NAME_TRANSPORT_PROTOCOL = "mail.transport.protocol";
    private static final String SESSION_PROPERTY_VALUE_TRANSPORT_PROTOCOL = "smtp";
    private static final String SESSION_PROPERTY_NAME_FROM = "mail.smtp.from";
    
    private Properties sessionProperties;
    
    private ServiceName authenticatorServiceName;
    private Authenticator authenticator;
    
    private Properties headers;
    private String[] headerKeys;
    
    private String envelopeFromAddressKey;
    private String envelopeFromAddress;
    private boolean isEnvelopeFromAddressValidate;
    
    private String fromAddressKey;
    private String fromAddress;
    private String fromPersonalKey;
    private String fromPersonal;
    private String fromPersonalEncodingKey;
    private String fromPersonalEncoding;
    private boolean isFromAddressValidate;
    
    private String senderAddressKey;
    private String senderAddress;
    private String senderPersonalKey;
    private String senderPersonal;
    private String senderPersonalEncodingKey;
    private String senderPersonalEncoding;
    private boolean isSenderAddressValidate;
    
    private String toAddressKey;
    private String[] toAddress;
    private String toPersonalKey;
    private String[] toPersonals;
    private String toPersonalEncodingKey;
    private String[] toPersonalEncodings;
    private String toPersonalEncoding;
    private boolean isToAddressValidate;
    
    private String ccAddressKey;
    private String[] ccAddress;
    private String ccPersonalKey;
    private String[] ccPersonals;
    private String ccPersonalEncodingKey;
    private String[] ccPersonalEncodings;
    private String ccPersonalEncoding;
    private boolean isCcAddressValidate;
    
    private String bccAddressKey;
    private String[] bccAddress;
    private String bccPersonalKey;
    private String[] bccPersonals;
    private String bccPersonalEncodingKey;
    private String[] bccPersonalEncodings;
    private String bccPersonalEncoding;
    private boolean isBccAddressValidate;
    
    private String replyToAddressKey;
    private String[] replyToAddress;
    private String replyToPersonalKey;
    private String[] replyToPersonals;
    private String replyToPersonalEncodingKey;
    private String[] replyToPersonalEncodings;
    private String replyToPersonalEncoding;
    private boolean isReplyToAddressValidate;
    
    private String subjectKey;
    private String subject;
    private String subjectEncodingKey;
    private String subjectEncoding;
    
    private String contentIDKey;
    private String contentID;
    
    private String contentLanguageKey;
    private String[] contentLanguage;
    
    private String contentMD5Key;
    private String contentMD5;
    
    private String descriptionKey;
    private String description;
    
    private String descriptionEncodingKey;
    private String descriptionEncoding;
    
    private String dispositionKey;
    private String disposition;
    
    private String filePartKey;
    private String fileCharset = MimeUtility.getDefaultJavaCharset();
    private String fileCharsetKey;
    private String fileLanguage;
    private String fileLanguageKey;
    
    private String bodyText;
    private String bodyIndexKey;
    private int bodyIndex = -1;
    private String bodyEncoding;
    
    private String smtpHostName;
    private int smtpPort = 25;
    private ServiceName smtpKeepAliveCheckerSelectorServiceName;
    private KeepAliveCheckerSelector smtpKeepAliveCheckerSelector;
    
    private int retryCount = -1;
    private long retryInterval = -1;
    
    private ServiceName jndiFinderServiceName;
    private JndiFinder jndiFinder;
    private String mailSessionJndiName = DEFAULT_MAIL_SESSION_JNDI_NAME;
    
    // MailWriterServiceMBean��JavaDoc
    public void setSessionProperties(Properties prop){
        sessionProperties = prop;
    }
    // MailWriterServiceMBean��JavaDoc
    public Properties getSessionProperties(){
        return sessionProperties;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setAuthenticatorServiceName(ServiceName name){
        authenticatorServiceName = name;
    }
    // MailWriterServiceMBean��JavaDoc
    public ServiceName getAuthenticatorServiceName(){
        return authenticatorServiceName;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setHeaders(Properties prop){
        headers = prop;
    }
    // MailWriterServiceMBean��JavaDoc
    public Properties getHeaders(){
        return headers;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setHeaderKeys(String[] keys){
        headerKeys = keys;
    }
    // MailWriterServiceMBean��JavaDoc
    public String[] getHeaderKeys(){
        return headerKeys;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setEnvelopeFromAddressKey(String key){
        envelopeFromAddressKey = key;
    }
    // MailWriterServiceMBean��JavaDoc
    public String getEnvelopeFromAddressKey(){
        return envelopeFromAddressKey;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setEnvelopeFromAddress(String address){
        envelopeFromAddress = address;
    }
    // MailWriterServiceMBean��JavaDoc
    public String getEnvelopeFromAddress(){
        return envelopeFromAddress;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setEnvelopeFromAddressValidate(boolean isValidate){
        isEnvelopeFromAddressValidate = isValidate;
    }
    // MailWriterServiceMBean��JavaDoc
    public boolean isEnvelopeFromAddressValidate(){
        return isEnvelopeFromAddressValidate;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setFromAddressKey(String key){
        fromAddressKey = key;
    }
    // MailWriterServiceMBean��JavaDoc
    public String getFromAddressKey(){
        return fromAddressKey;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setFromAddress(String address){
        fromAddress = address;
    }
    // MailWriterServiceMBean��JavaDoc
    public String getFromAddress(){
        return fromAddress;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setFromPersonalKey(String key){
        fromPersonalKey = key;
    }
    // MailWriterServiceMBean��JavaDoc
    public String getFromPersonalKey(){
        return fromPersonalKey;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setFromPersonal(String personal){
        fromPersonal = personal;
    }
    // MailWriterServiceMBean��JavaDoc
    public String getFromPersonal(){
        return fromPersonal;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setFromPersonalEncodingKey(String key){
        fromPersonalEncodingKey = key;
    }
    // MailWriterServiceMBean��JavaDoc
    public String getFromPersonalEncodingKey(){
        return fromPersonalEncodingKey;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setFromPersonalEncoding(String encoding){
        fromPersonalEncoding = encoding;
    }
    // MailWriterServiceMBean��JavaDoc
    public String getFromPersonalEncoding(){
        return fromPersonalEncoding;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setFromAddressValidate(boolean isValidate){
        isFromAddressValidate = isValidate;
    }
    // MailWriterServiceMBean��JavaDoc
    public boolean isFromAddressValidate(){
        return isFromAddressValidate;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setSenderAddressKey(String key){
        senderAddressKey = key;
    }
    // MailWriterServiceMBean��JavaDoc
    public String getSenderAddressKey(){
        return senderAddressKey;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setSenderAddress(String address){
        senderAddress = address;
    }
    // MailWriterServiceMBean��JavaDoc
    public String getSenderAddress(){
        return senderAddress;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setSenderPersonalKey(String key){
        senderPersonalKey = key;
    }
    // MailWriterServiceMBean��JavaDoc
    public String getSenderPersonalKey(){
        return senderPersonalKey;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setSenderPersonal(String personal){
        senderPersonal = personal;
    }
    // MailWriterServiceMBean��JavaDoc
    public String getSenderPersonal(){
        return senderPersonal;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setSenderPersonalEncodingKey(String key){
        senderPersonalEncodingKey = key;
    }
    // MailWriterServiceMBean��JavaDoc
    public String getSenderPersonalEncodingKey(){
        return senderPersonalEncodingKey;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setSenderPersonalEncoding(String encoding){
        senderPersonalEncoding = encoding;
    }
    // MailWriterServiceMBean��JavaDoc
    public String getSenderPersonalEncoding(){
        return senderPersonalEncoding;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setSenderAddressValidate(boolean isValidate){
        isSenderAddressValidate = isValidate;
    }
    // MailWriterServiceMBean��JavaDoc
    public boolean isSenderAddressValidate(){
        return isSenderAddressValidate;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setToAddressKey(String key){
        toAddressKey = key;
    }
    // MailWriterServiceMBean��JavaDoc
    public String getToAddressKey(){
        return toAddressKey;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setToAddress(String[] address){
        toAddress = address;
    }
    // MailWriterServiceMBean��JavaDoc
    public String[] getToAddress(){
        return toAddress;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setToPersonalKey(String key){
        toPersonalKey = key;
    }
    // MailWriterServiceMBean��JavaDoc
    public String getToPersonalKey(){
        return toPersonalKey;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setToPersonals(String[] personal){
        toPersonals = personal;
    }
    // MailWriterServiceMBean��JavaDoc
    public String[] getToPersonals(){
        return toPersonals;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setToPersonalEncodingKey(String key){
        toPersonalEncodingKey = key;
    }
    // MailWriterServiceMBean��JavaDoc
    public String getToPersonalEncodingKey(){
        return toPersonalEncodingKey;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setToPersonalEncodings(String[] encoding){
        toPersonalEncodings = encoding;
    }
    // MailWriterServiceMBean��JavaDoc
    public String[] getToPersonalEncodings(){
        return toPersonalEncodings;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setToPersonalEncoding(String encoding){
        toPersonalEncoding = encoding;
    }
    // MailWriterServiceMBean��JavaDoc
    public String getToPersonalEncoding(){
        return toPersonalEncoding;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setToAddressValidate(boolean isValidate){
        isToAddressValidate = isValidate;
    }
    // MailWriterServiceMBean��JavaDoc
    public boolean isToAddressValidate(){
        return isToAddressValidate;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setCcAddressKey(String key){
        ccAddressKey = key;
    }
    // MailWriterServiceMBean��JavaDoc
    public String getCcAddressKey(){
        return ccAddressKey;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setCcAddress(String[] address){
        ccAddress = address;
    }
    // MailWriterServiceMBean��JavaDoc
    public String[] getCcAddress(){
        return ccAddress;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setCcPersonalKey(String key){
        ccPersonalKey = key;
    }
    // MailWriterServiceMBean��JavaDoc
    public String getCcPersonalKey(){
        return ccPersonalKey;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setCcPersonals(String[] personal){
        ccPersonals = personal;
    }
    // MailWriterServiceMBean��JavaDoc
    public String[] getCcPersonals(){
        return ccPersonals;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setCcPersonalEncodingKey(String key){
        ccPersonalEncodingKey = key;
    }
    // MailWriterServiceMBean��JavaDoc
    public String getCcPersonalEncodingKey(){
        return ccPersonalEncodingKey;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setCcPersonalEncodings(String[] encoding){
        ccPersonalEncodings = encoding;
    }
    // MailWriterServiceMBean��JavaDoc
    public String[] getCcPersonalEncodings(){
        return ccPersonalEncodings;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setCcPersonalEncoding(String encoding){
        ccPersonalEncoding = encoding;
    }
    // MailWriterServiceMBean��JavaDoc
    public String getCcPersonalEncoding(){
        return ccPersonalEncoding;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setCcAddressValidate(boolean isValidate){
        isCcAddressValidate = isValidate;
    }
    // MailWriterServiceMBean��JavaDoc
    public boolean isCcAddressValidate(){
        return isCcAddressValidate;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setBccAddressKey(String key){
        bccAddressKey = key;
    }
    // MailWriterServiceMBean��JavaDoc
    public String getBccAddressKey(){
        return bccAddressKey;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setBccAddress(String[] address){
        bccAddress = address;
    }
    // MailWriterServiceMBean��JavaDoc
    public String[] getBccAddress(){
        return bccAddress;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setBccPersonalKey(String key){
        bccPersonalKey = key;
    }
    // MailWriterServiceMBean��JavaDoc
    public String getBccPersonalKey(){
        return bccPersonalKey;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setBccPersonals(String[] personal){
        bccPersonals = personal;
    }
    // MailWriterServiceMBean��JavaDoc
    public String[] getBccPersonals(){
        return bccPersonals;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setBccPersonalEncodingKey(String key){
        bccPersonalEncodingKey = key;
    }
    // MailWriterServiceMBean��JavaDoc
    public String getBccPersonalEncodingKey(){
        return bccPersonalEncodingKey;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setBccPersonalEncodings(String[] encoding){
        bccPersonalEncodings = encoding;
    }
    // MailWriterServiceMBean��JavaDoc
    public String[] getBccPersonalEncodings(){
        return bccPersonalEncodings;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setBccPersonalEncoding(String encoding){
        bccPersonalEncoding = encoding;
    }
    // MailWriterServiceMBean��JavaDoc
    public String getBccPersonalEncoding(){
        return bccPersonalEncoding;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setBccAddressValidate(boolean isValidate){
        isBccAddressValidate = isValidate;
    }
    // MailWriterServiceMBean��JavaDoc
    public boolean isBccAddressValidate(){
        return isBccAddressValidate;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setReplyToAddressKey(String key){
        replyToAddressKey = key;
    }
    // MailWriterServiceMBean��JavaDoc
    public String getReplyToAddressKey(){
        return replyToAddressKey;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setReplyToAddress(String[] address){
        replyToAddress = address;
    }
    // MailWriterServiceMBean��JavaDoc
    public String[] getReplyToAddress(){
        return replyToAddress;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setReplyToPersonalKey(String key){
        replyToPersonalKey = key;
    }
    // MailWriterServiceMBean��JavaDoc
    public String getReplyToPersonalKey(){
        return replyToPersonalKey;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setReplyToPersonals(String[] personal){
        replyToPersonals = personal;
    }
    // MailWriterServiceMBean��JavaDoc
    public String[] getReplyToPersonals(){
        return replyToPersonals;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setReplyToPersonalEncodingKey(String key){
        replyToPersonalEncodingKey = key;
    }
    // MailWriterServiceMBean��JavaDoc
    public String getReplyToPersonalEncodingKey(){
        return replyToPersonalEncodingKey;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setReplyToPersonalEncodings(String[] encoding){
        replyToPersonalEncodings = encoding;
    }
    // MailWriterServiceMBean��JavaDoc
    public String[] getReplyToPersonalEncodings(){
        return replyToPersonalEncodings;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setReplyToPersonalEncoding(String encoding){
        replyToPersonalEncoding = encoding;
    }
    // MailWriterServiceMBean��JavaDoc
    public String getReplyToPersonalEncoding(){
        return replyToPersonalEncoding;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setReplyToAddressValidate(boolean isValidate){
        isReplyToAddressValidate = isValidate;
    }
    // MailWriterServiceMBean��JavaDoc
    public boolean isReplyToAddressValidate(){
        return isReplyToAddressValidate;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setSubjectKey(String key){
        subjectKey = key;
    }
    // MailWriterServiceMBean��JavaDoc
    public String getSubjectKey(){
        return subjectKey;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setSubject(String subject){
        this.subject = subject;
    }
    // MailWriterServiceMBean��JavaDoc
    public String getSubject(){
        return subject;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setSubjectEncodingKey(String key){
        subjectEncodingKey = key;
    }
    // MailWriterServiceMBean��JavaDoc
    public String getSubjectEncodingKey(){
        return subjectEncodingKey;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setSubjectEncoding(String encoding){
        subjectEncoding = encoding;
    }
    // MailWriterServiceMBean��JavaDoc
    public String getSubjectEncoding(){
        return subjectEncoding;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setContentIDKey(String key){
        contentIDKey = key;
    }
    // MailWriterServiceMBean��JavaDoc
    public String getContentIDKey(){
        return contentIDKey;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setContentID(String id){
        this.contentID = id;
    }
    // MailWriterServiceMBean��JavaDoc
    public String getContentID(){
        return contentID;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setContentLanguageKey(String key){
        contentLanguageKey = key;
    }
    // MailWriterServiceMBean��JavaDoc
    public String getContentLanguageKey(){
        return contentLanguageKey;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setContentLanguage(String[] lang){
        contentLanguage = lang;
    }
    // MailWriterServiceMBean��JavaDoc
    public String[] getContentLanguage(){
        return contentLanguage;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setContentMD5Key(String key){
        contentMD5Key = key;
    }
    // MailWriterServiceMBean��JavaDoc
    public String getContentMD5Key(){
        return contentMD5Key;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setContentMD5(String val){
        contentMD5 = val;
    }
    // MailWriterServiceMBean��JavaDoc
    public String getContentMD5(){
        return contentMD5;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setDescriptionKey(String key){
        descriptionKey = key;
    }
    // MailWriterServiceMBean��JavaDoc
    public String getDescriptionKey(){
        return descriptionKey;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setDescription(String val){
        description = val;
    }
    // MailWriterServiceMBean��JavaDoc
    public String getDescription(){
        return description;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setDescriptionEncodingKey(String key){
        descriptionEncodingKey = key;
    }
    // MailWriterServiceMBean��JavaDoc
    public String getDescriptionEncodingKey(){
        return descriptionEncodingKey;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setDescriptionEncoding(String encoding){
        descriptionEncoding = encoding;
    }
    // MailWriterServiceMBean��JavaDoc
    public String getDescriptionEncoding(){
        return descriptionEncoding;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setDispositionKey(String key){
        dispositionKey = key;
    }
    // MailWriterServiceMBean��JavaDoc
    public String getDispositionKey(){
        return dispositionKey;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setDisposition(String val){
        disposition = val;
    }
    // MailWriterServiceMBean��JavaDoc
    public String getDisposition(){
        return disposition;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setFilePartKey(String key){
        filePartKey = key;
    }
    // MailWriterServiceMBean��JavaDoc
    public String getFilePartKey(){
        return filePartKey;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setFileCharsetKey(String key){
        fileCharsetKey = key;
    }
    // MailWriterServiceMBean��JavaDoc
    public String getFileCharsetKey(){
        return fileCharsetKey;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setFileCharset(String charset){
        fileCharset = charset;
    }
    // MailWriterServiceMBean��JavaDoc
    public String getFileCharset(){
        return fileCharset;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setFileLanguageKey(String key){
        fileLanguageKey = key;
    }
    // MailWriterServiceMBean��JavaDoc
    public String getFileLanguageKey(){
        return fileLanguageKey;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setFileLanguage(String lang){
        fileLanguage = lang;
    }
    // MailWriterServiceMBean��JavaDoc
    public String getFileLanguage(){
        return fileLanguage;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setBodyIndex(int index){
        bodyIndex = index;
    }
    // MailWriterServiceMBean��JavaDoc
    public int getBodyIndex(){
        return bodyIndex;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setBodyIndexKey(String key){
        bodyIndexKey = key;
    }
    // MailWriterServiceMBean��JavaDoc
    public String getBodyIndexKey(){
        return bodyIndexKey;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setBodyText(String text){
        bodyText = text;
    }
    // MailWriterServiceMBean��JavaDoc
    public String getBodyText(){
        return bodyText;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setBodyEncoding(String encoding){
        bodyEncoding = encoding;
    }
    // MailWriterServiceMBean��JavaDoc
    public String getBodyEncoding(){
        return bodyEncoding;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setSmtpHostName(String name){
        smtpHostName = name;
    }
    // MailWriterServiceMBean��JavaDoc
    public String getSmtpHostName(){
        return smtpHostName;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setSmtpPort(int port){
        smtpPort = port;
    }
    // MailWriterServiceMBean��JavaDoc
    public int getSmtpPort(){
        return smtpPort;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setRetryCount(int count){
        retryCount = count;
    }
    // MailWriterServiceMBean��JavaDoc
    public int getRetryCount(){
        return retryCount;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setRetryInterval(long millis){
        retryInterval = millis;
    }
    // MailWriterServiceMBean��JavaDoc
    public long getRetryInterval(){
        return retryInterval;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setSmtpKeepAliveCheckerSelectorServiceName(ServiceName name){
        smtpKeepAliveCheckerSelectorServiceName = name;
    }
    // MailWriterServiceMBean��JavaDoc
    public ServiceName getSmtpKeepAliveCheckerSelectorServiceName(){
        return smtpKeepAliveCheckerSelectorServiceName;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setJndiFinderServiceName(ServiceName name){
        jndiFinderServiceName = name;
    }
    // MailWriterServiceMBean��JavaDoc
    public ServiceName getJndiFinderServiceName(){
        return jndiFinderServiceName;
    }
    
    // MailWriterServiceMBean��JavaDoc
    public void setMailSessionJndiName(String name){
        mailSessionJndiName = name;
    }
    // MailWriterServiceMBean��JavaDoc
    public String getMailSessionJndiName(){
        return mailSessionJndiName;
    }
    
    /**
     * �T�[�r�X�̊J�n�������s���B<p>
     *
     * @exception Exception �T�[�r�X�̊J�n�����Ɏ��s�����ꍇ
     */
    public void startService() throws Exception{
        if(authenticatorServiceName != null){
            authenticator = (Authenticator)ServiceManagerFactory
                .getServiceObject(authenticatorServiceName);
        }
        
        if(fromAddressKey == null && fromAddress == null){
            throw new IllegalArgumentException("It is necessary to set any of FromAddressKey and FromAddress.");
        }
        if(toAddress != null && toAddress.length != 0){
            if(toPersonals != null && toPersonals.length != toAddress.length){
                throw new IllegalArgumentException("It is necessary to set toAddress and toPersonals to the same length.");
            }
            if(toPersonalEncodings != null && toPersonalEncodings.length != toAddress.length){
                throw new IllegalArgumentException("It is necessary to set toAddress and toPersonalEncodings to the same length.");
            }
        }
        if(ccAddress != null && ccAddress.length != 0){
            if(ccPersonals != null && ccPersonals.length != ccAddress.length){
                throw new IllegalArgumentException("It is necessary to set ccAddress and ccPersonals to the same length.");
            }
            if(ccPersonalEncodings != null && ccPersonalEncodings.length != ccAddress.length){
                throw new IllegalArgumentException("It is necessary to set ccAddress and ccPersonalEncodings to the same length.");
            }
        }
        if(bccAddress != null && bccAddress.length != 0){
            if(bccPersonals != null && bccPersonals.length != bccAddress.length){
                throw new IllegalArgumentException("It is necessary to set bccAddress and bccPersonals to the same length.");
            }
            if(bccPersonalEncodings != null && bccPersonalEncodings.length != bccAddress.length){
                throw new IllegalArgumentException("It is necessary to set bccAddress and bccPersonalEncodings to the same length.");
            }
        }
        
        if(smtpKeepAliveCheckerSelectorServiceName != null){
            smtpKeepAliveCheckerSelector
                 = (KeepAliveCheckerSelector)ServiceManagerFactory
                    .getServiceObject(smtpKeepAliveCheckerSelectorServiceName);
        }
        if(smtpHostName == null && smtpKeepAliveCheckerSelector == null){
            throw new IllegalArgumentException("It is necessary to set any of SmtpHostName and SmtpKeepAliveCheckerSelectorServiceName.");
        }
        
        if(jndiFinderServiceName != null){
            jndiFinder = (JndiFinder)ServiceManagerFactory
                .getServiceObject(jndiFinderServiceName);
        }
    }
    
    /**
     * �w�肳�ꂽ���R�[�h�����[�����M����B<p>
     *
     * @param rec ���[�����M���郌�R�[�h
     * @exception MessageWriteException ���[�����M�Ɏ��s�����ꍇ
     */
    public void write(WritableRecord rec) throws MessageWriteException{
        
        int maxCount = retryCount > 0 ? retryCount : 0;
        Exception exception = null;
        for(int count = 0; count <= maxCount; count++){
            try{
                sendMail(rec);
                return;
            }catch(NamingException e){
                exception = e;
            }catch(IllegalWriteException e){
                exception = e;
            }catch(MessagingException e){
                exception = e;
            }
            if(retryInterval > 0){
                try{
                    Thread.sleep(retryInterval);
                }catch(InterruptedException e){
                }
            }
        }
        throw new MessageWriteException(exception);
    }
    
    /**
     * �w�肳�ꂽ���R�[�h�����[�����M����B<p>
     *
     * @param rec ���[�����M���郌�R�[�h
     * @exception MessageWriteException javax.mail.Message�ɐݒ肷��l���s���ȏꍇ�A�܂���SMTP�T�[�o������ł���ꍇ
     * @exception IllegalWriteException javax.mail.Message�̓ǂݎ���p�����ɏ������݂��s��ꂽ�ꍇ
     * @exception MessagingException ���[�����M�Ɏ��s�����ꍇ
     * @exception NamingException javax.mail.Session��JNDI����lookup����̂Ɏ��s�����ꍇ
     */
    public void sendMail(WritableRecord rec)
     throws MessageWriteException, IllegalWriteException, MessagingException,
            NamingException{
        
        final Map elementMap = rec.getElementMap();
        
        // From�A�h���X���쐬����
        final InternetAddress fromInternetAddress = createAddress(
            elementMap,
            fromAddress,
            fromPersonal,
            fromPersonalEncoding,
            fromAddressKey,
            fromPersonalKey,
            fromPersonalEncodingKey,
            isFromAddressValidate
        );
        if(fromInternetAddress == null){
            throw new MessageWriteException("The From address is null.");
        }
        
        // Sender�A�h���X���쐬����
        final InternetAddress senderInternetAddress = createAddress(
            elementMap,
            senderAddress,
            senderPersonal,
            senderPersonalEncoding,
            senderAddressKey,
            senderPersonalKey,
            senderPersonalEncodingKey,
            isSenderAddressValidate
        );
        
        // To�A�h���X���쐬����
        final InternetAddress[] toInternetAddress = createAddressArray(
            elementMap,
            toAddress,
            toPersonals,
            toPersonalEncodings,
            toPersonalEncoding,
            toAddressKey,
            toPersonalKey,
            toPersonalEncodingKey,
            isToAddressValidate
        );
        
        // Cc�A�h���X���쐬����
        final InternetAddress[] ccInternetAddress = createAddressArray(
            elementMap,
            ccAddress,
            ccPersonals,
            ccPersonalEncodings,
            ccPersonalEncoding,
            ccAddressKey,
            ccPersonalKey,
            ccPersonalEncodingKey,
            isCcAddressValidate
        );
        
        // Bcc�A�h���X���쐬����
        final InternetAddress[] bccInternetAddress = createAddressArray(
            elementMap,
            bccAddress,
            bccPersonals,
            bccPersonalEncodings,
            bccPersonalEncoding,
            bccAddressKey,
            bccPersonalKey,
            bccPersonalEncodingKey,
            isBccAddressValidate
        );
        if((toInternetAddress == null || toInternetAddress.length == 0)
            && (ccInternetAddress == null || ccInternetAddress.length == 0)
            && (bccInternetAddress == null || bccInternetAddress.length == 0)
        ){
            throw new MessageWriteException("The destination address is null.");
        }
        
        // ReplyTo�A�h���X���쐬����
        final InternetAddress[] replyToInternetAddress = createAddressArray(
            elementMap,
            replyToAddress,
            replyToPersonals,
            replyToPersonalEncodings,
            replyToPersonalEncoding,
            replyToAddressKey,
            replyToPersonalKey,
            replyToPersonalEncodingKey,
            isReplyToAddressValidate
        );
        
        // �{�����쐬����
        String body = bodyText;
        final int bIndex = getIntValue(elementMap, bodyIndex, bodyIndexKey);
        if(bIndex >= 0){
            List elements = rec.getElements();
            if(elements.size() > bIndex){
                final StringBuffer buf = new StringBuffer();
                for(int i = bIndex, imax = elements.size(); i < imax; i++){
                    WritableElement element = (WritableElement)elements.get(i);
                    if(element != null){
                        buf.append(element);
                    }
                }
                body = buf.toString();
            }
        }
        Session session = null;
        if(jndiFinder != null){
            session = (Session)jndiFinder.lookup(mailSessionJndiName);
        }else{
            
            final Properties props = new Properties(System.getProperties());
            
            if(smtpKeepAliveCheckerSelector != null){
                final SmtpKeepAliveChecker checker
                     = (SmtpKeepAliveChecker)smtpKeepAliveCheckerSelector
                        .selectChecker();
                if(checker == null){
                    throw new MessageWriteException("All smtp server is dead.");
                }
                // SMTP�T�[�o�̃z�X�g����ݒ肷��
                props.setProperty(
                    SESSION_PROPERTY_NAME_HOST,
                    checker.getHostIp()
                );
                // SMTP�T�[�o�̃|�[�g�ԍ���ݒ肷��
                props.setProperty(
                    SESSION_PROPERTY_NAME_PORT,
                    String.valueOf(checker.getHostPort())
                );
            }else{
                // SMTP�T�[�o�̃z�X�g����ݒ肷��
                props.setProperty(SESSION_PROPERTY_NAME_HOST, smtpHostName);
                // SMTP�T�[�o�̃|�[�g�ԍ���ݒ肷��
                props.setProperty(
                    SESSION_PROPERTY_NAME_PORT,
                    String.valueOf(smtpPort)
                );
            }
            
            // ���M�v���g�R����ݒ肷��
            props.setProperty(
                SESSION_PROPERTY_NAME_TRANSPORT_PROTOCOL,
                SESSION_PROPERTY_VALUE_TRANSPORT_PROTOCOL
            );
            
            // envelope-from�A�h���X���쐬����
            final InternetAddress envelopeFromInternetAddress = createAddress(
                elementMap,
                envelopeFromAddress,
                null,
                null,
                envelopeFromAddressKey,
                null,
                null,
                isEnvelopeFromAddressValidate
            );
            if(envelopeFromInternetAddress != null){
                // envelope-from�A�h���X��ݒ肷��
                props.setProperty(
                    SESSION_PROPERTY_NAME_FROM,
                    envelopeFromInternetAddress.getAddress()
                );
            }
            
            if(sessionProperties != null){
                props.putAll(sessionProperties);
            }
            
            session = Session.getInstance(props, authenticator);
        }
        final MimeMessage message = new MimeMessage(session);
        
        // Content-ID��ݒ肷��
        String val = getStringValue(elementMap, contentID, contentIDKey);
        if(val != null){
            message.setContentID(val);
        }
        
        // Content-Language��ݒ肷��
        String[] vals = getStringArrayValue(
            elementMap,
            contentLanguage,
            contentLanguageKey
        );
        if(vals != null){
            message.setContentLanguage(vals);
        }
        
        // Content-MD5��ݒ肷��
        val = getStringValue(elementMap, contentMD5, contentMD5Key);
        if(val != null){
            message.setContentMD5(val);
        }
        
        // Content-Description��ݒ肷��
        val = getStringValue(elementMap, description, descriptionKey);
        if(val != null){
            final String enc = getStringValue(
                elementMap,
                descriptionEncoding,
                descriptionEncodingKey
            );
            if(enc == null){
                message.setDescription(val);
            }else{
                message.setDescription(val, enc);
            }
        }
        
        // Content-Disposition��ݒ肷��
        val = getStringValue(elementMap, disposition, dispositionKey);
        if(val != null){
            message.setDisposition(val);
        }

        // From�A�h���X��ݒ肷��
        message.setFrom(fromInternetAddress);
        
        // Sender�A�h���X��ݒ肷��
        if(senderInternetAddress != null){
            message.setSender(senderInternetAddress);
        }
        
        // To�A�h���X��ݒ肷��
        if(toInternetAddress != null){
            for(int i = 0; i < toInternetAddress.length; i++){
                message.addRecipient(
                    Message.RecipientType.TO,
                    toInternetAddress[i]
                );
            }
        }
        
        // Cc�A�h���X��ݒ肷��
        if(ccInternetAddress != null){
            for(int i = 0; i < ccInternetAddress.length; i++){
                message.addRecipient(
                    Message.RecipientType.CC,
                    ccInternetAddress[i]
                );
            }
        }
        
        // Bcc�A�h���X��ݒ肷��
        if(bccInternetAddress != null){
            for(int i = 0; i < bccInternetAddress.length; i++){
                message.addRecipient(
                    Message.RecipientType.BCC,
                    bccInternetAddress[i]
                );
            }
        }
        
        // ReplyTo�A�h���X��ݒ肷��
        if(replyToInternetAddress != null){
            message.setReplyTo(replyToInternetAddress);
        }
        
        // �薼��ݒ肷��
        String enc =
            getStringValue(elementMap, subjectEncoding, subjectEncodingKey);
        if(enc != null){
            message.setSubject(
                getStringValue(elementMap, subject, subjectKey),
                enc
            );
        }else{
            message.setSubject(
                getStringValue(elementMap, subject, subjectKey)
            );
        }
        
        File[] files = null;
        if(filePartKey != null){
            files = getFileArrayValue(elementMap, filePartKey);
        }
        if(files != null && files.length != 0){
            
            final MimeMultipart mp = new MimeMultipart();
            
            // �{����ݒ肷��
            final MimeBodyPart textPart = new MimeBodyPart();
            if(bodyEncoding == null){
                textPart.setText(body);
            }else{
                textPart.setText(body, bodyEncoding);
            }
            mp.addBodyPart(textPart);
            
            // �Y�t�t�@�C����Y�t����
            for(int i = 0; i < files.length; i++){
                final MimeBodyPart filePart = new MimeBodyPart();
                filePart.setDataHandler(
                    new DataHandler(new FileDataSource(files[i]))
                );
                setFileName(
                    filePart,
                    files[i].getName(),
                    getStringValue(elementMap, fileCharset, fileCharsetKey),
                    getStringValue(elementMap, fileLanguage, fileLanguageKey)
                );
                mp.addBodyPart(filePart);
            }
            
            message.setContent(mp);
        }else{
            // �{����ݒ肷��
            if(bodyEncoding == null){
                message.setText(body);
            }else{
                message.setText(body, bodyEncoding);
            }
        }
        
        // �w�b�_��ݒ肷��
        if(headers != null){
            final Iterator names = headers.keySet().iterator();
            while(names.hasNext()){
                final String name = (String)names.next();
                message.setHeader(name, headers.getProperty(name));
            }
        }
        if(headerKeys != null){
            for(int i = 0; i < headerKeys.length; i++){
                final String header = getStringValue(
                    elementMap,
                    null,
                    headerKeys[i]
                );
                if(header != null){
                    message.setHeader(headerKeys[i], header);
                }
            }
        }

        // ���M������ݒ肷��
        message.setSentDate(new Date());
        
        // �ݒ��L���ɂ���
        message.saveChanges();
        
        // ���[���𑗐M����
        Transport.send(message);
    }
    
    private void setFileName(
        Part part,
        String filename,
        String charset,
        String lang
    ) throws MessagingException{
        
        ContentDisposition disposition;
        String[] strings = part.getHeader("Content-Disposition");
        if(strings == null || strings.length < 1){
            disposition = new ContentDisposition(Part.ATTACHMENT);
        }else{
            disposition = new ContentDisposition(strings[0]);
            disposition.getParameterList().remove("filename");
        }
        
        part.setHeader(
            "Content-Disposition",
            disposition.toString()
                + encodeParameter("filename", filename, charset, lang)
        );
        
        ContentType cType;
        strings = part.getHeader("Content-Type");
        if(strings == null || strings.length < 1){
            cType = new ContentType(part.getDataHandler().getContentType());
        }else{
            cType = new ContentType(strings[0]);
        }
        
        try{
            // I want to public the MimeUtility#doEncode()!!!
            String mimeString = MimeUtility.encodeWord(filename, charset, "B");
            // cut <CRLF>...
            StringBuffer sb = new StringBuffer();
            int i;
            while((i = mimeString.indexOf('\r')) != -1){
                sb.append(mimeString.substring(0, i));
                mimeString = mimeString.substring(i + 2);
            }
            sb.append(mimeString);
            
            cType.setParameter("name", new String(sb));
        }catch(UnsupportedEncodingException e){
            throw new MessagingException("Encoding error", e);
        }
        part.setHeader("Content-Type", cType.toString());
    }
    
    private String encodeParameter(
        String name,
        String value,
        String encoding,
        String lang
    ){
        StringBuffer result = new StringBuffer();
        StringBuffer encodedPart = new StringBuffer();
        
        boolean needWriteCES = !isAllAscii(value);
        boolean CESWasWritten = false;
        boolean encoded;
        boolean needFolding = false;
        int sequenceNo = 0;
        int column;
        
        while(value.length() > 0){
            // index of boundary of ascii/non ascii
            int lastIndex;
            boolean isAscii = value.charAt(0) < 0x80;
            for(lastIndex = 1; lastIndex < value.length(); lastIndex++){
                if(value.charAt(lastIndex) < 0x80){
                    if(!isAscii) break;
                }else{
                    if(isAscii) break;
                }
            }
            if(lastIndex != value.length()) needFolding = true;
            
            RETRY:
            while(true){
                encodedPart.delete(0, encodedPart.length());
                String target = value.substring(0, lastIndex);
                
                byte[] bytes;
                try{
                    if(isAscii){
                        bytes = target.getBytes("us-ascii");
                    }else{
                        bytes = target.getBytes(encoding);
                    }
                }catch(UnsupportedEncodingException e){
                    bytes = target.getBytes(); // use default encoding
                    encoding = MimeUtility.mimeCharset(
                        MimeUtility.getDefaultJavaCharset()
                    );
                }
                
                encoded = false;
                // It is not strict.
                column = name.length() + 7; // size of " " and "*nn*=" and ";"
                
                for(int i = 0; i < bytes.length; i++){
                    if(bytes[i] > ' ' && bytes[i] < 'z'
                         && HeaderTokenizer.MIME.indexOf((char)bytes[i]) < 0){
                        encodedPart.append((char)bytes[i]);
                        column++;
                    }else{
                        encoded = true;
                        encodedPart.append('%');
                        String hex  = Integer.toString(bytes[i] & 0xff, 16);
                        if(hex.length() == 1){
                            encodedPart.append('0');
                        }
                        encodedPart.append(hex);
                        column += 3;
                    }
                    if(column > 76){
                        needFolding = true;
                        lastIndex /= 2;
                        continue RETRY;
                    }
                }
                
                result.append(";\r\n ").append(name);
                if(needFolding){
                    result.append('*').append(sequenceNo);
                    sequenceNo++;
                }
                if(!CESWasWritten && needWriteCES){
                    result.append("*=");
                    CESWasWritten = true;
                    result.append(encoding).append('\'');
                    if(lang != null) result.append(lang);
                    result.append('\'');
                }else if(encoded){
                    result.append("*=");
                }else{
                    result.append('=');
                }
                result.append(new String(encodedPart));
                value = value.substring(lastIndex);
                break;
            }
        }
        return result.toString();
    }
    
    private boolean isAllAscii(String text){
        for(int i = 0; i < text.length(); i++){
            if(text.charAt(i) > 0x7f){ // non-ascii
                return false;
            }
        }
        return true;
    }
    
    private String[] toStringArray(Object obj){
        if(obj == null){
            return null;
        }
        String[] result = null;
        if(obj instanceof String[]){
            result = (String[])obj;
        }else if(obj instanceof Collection){
            final Collection col = (Collection)obj;
            if(col.size() != 0){
                result = new String[col.size()];
                int index = 0;
                final Iterator itr = col.iterator();
                while(itr.hasNext()){
                    result[index++] = itr.next().toString();
                }
            }
        }else{
            final CsvArrayList list = new CsvArrayList();
            list.split(obj.toString());
            result = list.toStringAry();
        }
        return result;
    }
    
    private InternetAddress[] createAddressArray(
        Map elementMap,
        String[] defaultAddress,
        String[] defaultPersonals,
        String[] defaultPersonalEncodings,
        String defaultPersonalEncoding,
        String addressKey,
        String personalKey,
        String personalEncodingKey,
        boolean isAddressValidate
    ) throws MessageWriteException{
        final String[] address = getStringArrayValue(
            elementMap,
            defaultAddress,
            addressKey
        );
        if(address == null || address.length == 0){
            return null;
        }
        String[] personals = null;
        String[] personalEncodings = null;
        if(address == defaultAddress){
            personals = defaultPersonals;
            personalEncodings = defaultPersonalEncodings;
        }else{
            personals = getStringArrayValue(
                elementMap,
                null,
                personalKey
            );
            if(personals != null && personals.length != address.length){
                throw new MessageWriteException("It is necessary to set address and personals to the same length.");
            }
            
            personalEncodings = getStringArrayValue(
                elementMap,
                null,
                personalEncodingKey
            );
            if(personalEncodings != null && personalEncodings.length != address.length){
                throw new MessageWriteException("It is necessary to set address and personalEncodings to the same length.");
            }
        }
        final InternetAddress[] internetAddress
             = new InternetAddress[address.length];
        for(int i = 0; i < address.length; i++){
            internetAddress[i] = createAddress(
                address[i],
                personals == null ? null : personals[i],
                personalEncodings == null ? defaultPersonalEncoding : personalEncodings[i],
                isAddressValidate
            );
        }
        return internetAddress;
    }
    
    private InternetAddress createAddress(
        Map elementMap,
        String defaultAddress,
        String defaultPersonal,
        String defaultPersonalEncoding,
        String addressKey,
        String personalKey,
        String personalEncodingKey,
        boolean isAddressValidate
    ) throws MessageWriteException{
        final String address = getStringValue(
            elementMap,
            defaultAddress,
            addressKey
        );
        if(address == null){
            return null;
        }
        String personal = getStringValue(
            elementMap,
            defaultPersonal,
            personalKey
        );
        String personalEncoding = getStringValue(
            elementMap,
            defaultPersonalEncoding,
            personalEncodingKey
        );
        return createAddress(
            address,
            personal,
            personalEncoding,
            isAddressValidate
        );
    }
    
    private InternetAddress createAddress(
        String address,
        String personal,
        String personalEncoding,
        boolean isAddressValidate
    ) throws MessageWriteException{
        InternetAddress internetAddress = null;
        try{
            if(personal != null && personalEncoding != null){
                internetAddress = new InternetAddress(
                    address,
                    personal,
                    personalEncoding
                );
            }else if(personal != null){
                internetAddress = new InternetAddress(
                    address,
                    personal
                );
            }else{
                internetAddress = new InternetAddress(address);
            }
            if(isAddressValidate){
                internetAddress.validate();
            }
        }catch(UnsupportedEncodingException e){
            throw new MessageWriteException(e);
        }catch(AddressException e){
            throw new MessageWriteException(e);
        }
        return internetAddress;
    }
    
    private String[] getStringArrayValue(Map elementMap, String[] defaultVal, String key){
        String[] val = defaultVal;
        if(key != null){
            final WritableElement tmpElement
                 = (WritableElement)elementMap.get(key);
            if(tmpElement != null){
                Object tmpVal = tmpElement.toObject();
                if(tmpVal != null){
                    final String[] tmpArray = toStringArray(tmpVal);
                    if(tmpArray != null && tmpArray.length != 0){
                        val = tmpArray;
                    }
                }
            }
        }
        return val;
    }
    
    private String getStringValue(Map elementMap, String defaultVal, String key){
        Object val = getValue(elementMap, defaultVal, key);
        return val == null ? null : val.toString();
    }
    
    private int getIntValue(Map elementMap, int defaultVal, String key){
        Integer val = (Integer)getValue(elementMap, null, key);
        return val == null ? defaultVal : val.intValue();
    }
    
    private Object getValue(Map elementMap, Object defaultVal, String key){
        Object val = defaultVal;
        if(key != null){
            final WritableElement tmpElement
                 = (WritableElement)elementMap.get(key);
            if(tmpElement != null){
                Object tmpVal = tmpElement.toObject();
                if(tmpVal != null){
                    val = tmpVal;
                }
            }
        }
        return val;
    }
    
    private File[] getFileArrayValue(Map elementMap, String key){
        File[] val = null;
        if(key != null){
            final WritableElement tmpElement
                 = (WritableElement)elementMap.get(key);
            if(tmpElement != null){
                Object tmpVal = tmpElement.toObject();
                if(tmpVal != null){
                    if(tmpVal instanceof File[]){
                        val = (File[])tmpVal;
                    }else if(tmpVal instanceof Collection){
                        final Collection col = (Collection)tmpVal;
                        if(col.size() != 0){
                            val = new File[col.size()];
                            int index = 0;
                            final Iterator itr = col.iterator();
                            while(itr.hasNext()){
                                val[index++] = (File)itr.next();
                            }
                        }
                    }
                }
            }
        }
        return val;
    }
}
