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
 * メール送信サービス。<p>
 * Java Mailでは、Sessionの取得及び設定、Messageの生成及び設定、SMTPサーバへの送信の機能がある。<br>
 * このサービスでは、それらの機能をノンプログラミングで行う事ができる。<br>
 * <p>
 * Java MailでのSessionの取得には、２種類の方法がある。<br>
 * {@link Session#getInstance(Properties, Authenticator)}で、任意のセッション属性を指定したSessionを取得する方法。<br>
 * アプリケーションサーバで設定したSessionをJNDI経由で取得する方法。<br>
 * このサービスでは、その双方をサポートします。<br>
 * また、前者の方法の場合は、{@link #setSessionProperties(Properties)}で、設定したセッション属性を一律に設定する。<br>
 * 但し、以下のセッション属性は、一律指定がない場合は、それぞれ以下の方法で設定される。<br>
 * <table border="1">
 *   <tr bgcolor="#CCCCFF"><th>セッション属性</th><th>デフォルトの設定方法</th></tr>
 *   <tr><td>mail.smtp.host</td><td>{@link #setSmtpKeepAliveCheckerSelectorServiceName(ServiceName)}が指定されている場合は、指定された{@link KeepAliveCheckerSelector}によって選択された{@link SmtpKeepAliveChecker}の{@link SmtpKeepAliveChecker#getHostIp()}で取得した値。<br>そうでない場合は、{@link #setSmtpHostName(String)}で指定された値。</td></tr>
 *   <tr><td>mail.smtp.port</td><td>{@link #setSmtpKeepAliveCheckerSelectorServiceName(ServiceName)}が指定されている場合は、指定された{@link KeepAliveCheckerSelector}によって選択された{@link SmtpKeepAliveChecker}の{@link SmtpKeepAliveChecker#getHostPort()}で取得した値。<br>そうでない場合は、{@link #setSmtpPort(int)}で指定された値。</td></tr>
 *   <tr><td>mail.transport.protocol</td><td>smtp</td></tr>
 *   <tr><td>mail.smtp.from</td><td>{@link #setEnvelopeFromAddressKey(String)}で指定されたキーで、{@link #write(WritableRecord)}の引数の{@link WritableRecord}から取得した値。<br>指定されていない場合は、{@link #setEnvelopeFromAddress(String)}で指定された値。</td></tr>
 * </table>
 * <p>
 * Messageの生成は、javax.mail.MimeMessageを生成します。<br>
 * MimeMessageへの各種設定は、このサービスの各属性で設定可能である。<br>
 * 各属性毎に、２種類の設定方法が用意されている。<br>
 * {@link #write(WritableRecord)}の引数の{@link WritableRecord}から取得するためのキー名を指定する方法と、属性値そのものを設定する方法である。<br>
 * 前者の場合は、このMessageWriterを呼ぶクライアントが引数によって、任意に指定する事が可能で、後者の方法は、このサービスの設定で一律同じ値を設定する事が可能である。<br>
 * ある属性に対して、上記の２種類の設定方法が両方とも設定されている場合には、前者の方が有効となる。<br>
 * また、{@link Message#setSentDate(Date)}だけは、自動的に送信時の時刻が設定される。<br>
 * <p>
 * SMTPサーバへの送信機能としては、複数のSMTPサーバに対してロードバランスしながら送信する機能と、送信失敗時に、一定の間隔をあけて、リトライする機能がある。<br>
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
    
    // MailWriterServiceMBeanのJavaDoc
    public void setSessionProperties(Properties prop){
        sessionProperties = prop;
    }
    // MailWriterServiceMBeanのJavaDoc
    public Properties getSessionProperties(){
        return sessionProperties;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setAuthenticatorServiceName(ServiceName name){
        authenticatorServiceName = name;
    }
    // MailWriterServiceMBeanのJavaDoc
    public ServiceName getAuthenticatorServiceName(){
        return authenticatorServiceName;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setHeaders(Properties prop){
        headers = prop;
    }
    // MailWriterServiceMBeanのJavaDoc
    public Properties getHeaders(){
        return headers;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setHeaderKeys(String[] keys){
        headerKeys = keys;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String[] getHeaderKeys(){
        return headerKeys;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setEnvelopeFromAddressKey(String key){
        envelopeFromAddressKey = key;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String getEnvelopeFromAddressKey(){
        return envelopeFromAddressKey;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setEnvelopeFromAddress(String address){
        envelopeFromAddress = address;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String getEnvelopeFromAddress(){
        return envelopeFromAddress;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setEnvelopeFromAddressValidate(boolean isValidate){
        isEnvelopeFromAddressValidate = isValidate;
    }
    // MailWriterServiceMBeanのJavaDoc
    public boolean isEnvelopeFromAddressValidate(){
        return isEnvelopeFromAddressValidate;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setFromAddressKey(String key){
        fromAddressKey = key;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String getFromAddressKey(){
        return fromAddressKey;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setFromAddress(String address){
        fromAddress = address;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String getFromAddress(){
        return fromAddress;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setFromPersonalKey(String key){
        fromPersonalKey = key;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String getFromPersonalKey(){
        return fromPersonalKey;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setFromPersonal(String personal){
        fromPersonal = personal;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String getFromPersonal(){
        return fromPersonal;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setFromPersonalEncodingKey(String key){
        fromPersonalEncodingKey = key;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String getFromPersonalEncodingKey(){
        return fromPersonalEncodingKey;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setFromPersonalEncoding(String encoding){
        fromPersonalEncoding = encoding;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String getFromPersonalEncoding(){
        return fromPersonalEncoding;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setFromAddressValidate(boolean isValidate){
        isFromAddressValidate = isValidate;
    }
    // MailWriterServiceMBeanのJavaDoc
    public boolean isFromAddressValidate(){
        return isFromAddressValidate;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setSenderAddressKey(String key){
        senderAddressKey = key;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String getSenderAddressKey(){
        return senderAddressKey;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setSenderAddress(String address){
        senderAddress = address;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String getSenderAddress(){
        return senderAddress;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setSenderPersonalKey(String key){
        senderPersonalKey = key;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String getSenderPersonalKey(){
        return senderPersonalKey;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setSenderPersonal(String personal){
        senderPersonal = personal;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String getSenderPersonal(){
        return senderPersonal;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setSenderPersonalEncodingKey(String key){
        senderPersonalEncodingKey = key;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String getSenderPersonalEncodingKey(){
        return senderPersonalEncodingKey;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setSenderPersonalEncoding(String encoding){
        senderPersonalEncoding = encoding;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String getSenderPersonalEncoding(){
        return senderPersonalEncoding;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setSenderAddressValidate(boolean isValidate){
        isSenderAddressValidate = isValidate;
    }
    // MailWriterServiceMBeanのJavaDoc
    public boolean isSenderAddressValidate(){
        return isSenderAddressValidate;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setToAddressKey(String key){
        toAddressKey = key;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String getToAddressKey(){
        return toAddressKey;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setToAddress(String[] address){
        toAddress = address;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String[] getToAddress(){
        return toAddress;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setToPersonalKey(String key){
        toPersonalKey = key;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String getToPersonalKey(){
        return toPersonalKey;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setToPersonals(String[] personal){
        toPersonals = personal;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String[] getToPersonals(){
        return toPersonals;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setToPersonalEncodingKey(String key){
        toPersonalEncodingKey = key;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String getToPersonalEncodingKey(){
        return toPersonalEncodingKey;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setToPersonalEncodings(String[] encoding){
        toPersonalEncodings = encoding;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String[] getToPersonalEncodings(){
        return toPersonalEncodings;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setToPersonalEncoding(String encoding){
        toPersonalEncoding = encoding;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String getToPersonalEncoding(){
        return toPersonalEncoding;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setToAddressValidate(boolean isValidate){
        isToAddressValidate = isValidate;
    }
    // MailWriterServiceMBeanのJavaDoc
    public boolean isToAddressValidate(){
        return isToAddressValidate;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setCcAddressKey(String key){
        ccAddressKey = key;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String getCcAddressKey(){
        return ccAddressKey;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setCcAddress(String[] address){
        ccAddress = address;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String[] getCcAddress(){
        return ccAddress;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setCcPersonalKey(String key){
        ccPersonalKey = key;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String getCcPersonalKey(){
        return ccPersonalKey;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setCcPersonals(String[] personal){
        ccPersonals = personal;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String[] getCcPersonals(){
        return ccPersonals;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setCcPersonalEncodingKey(String key){
        ccPersonalEncodingKey = key;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String getCcPersonalEncodingKey(){
        return ccPersonalEncodingKey;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setCcPersonalEncodings(String[] encoding){
        ccPersonalEncodings = encoding;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String[] getCcPersonalEncodings(){
        return ccPersonalEncodings;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setCcPersonalEncoding(String encoding){
        ccPersonalEncoding = encoding;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String getCcPersonalEncoding(){
        return ccPersonalEncoding;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setCcAddressValidate(boolean isValidate){
        isCcAddressValidate = isValidate;
    }
    // MailWriterServiceMBeanのJavaDoc
    public boolean isCcAddressValidate(){
        return isCcAddressValidate;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setBccAddressKey(String key){
        bccAddressKey = key;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String getBccAddressKey(){
        return bccAddressKey;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setBccAddress(String[] address){
        bccAddress = address;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String[] getBccAddress(){
        return bccAddress;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setBccPersonalKey(String key){
        bccPersonalKey = key;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String getBccPersonalKey(){
        return bccPersonalKey;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setBccPersonals(String[] personal){
        bccPersonals = personal;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String[] getBccPersonals(){
        return bccPersonals;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setBccPersonalEncodingKey(String key){
        bccPersonalEncodingKey = key;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String getBccPersonalEncodingKey(){
        return bccPersonalEncodingKey;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setBccPersonalEncodings(String[] encoding){
        bccPersonalEncodings = encoding;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String[] getBccPersonalEncodings(){
        return bccPersonalEncodings;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setBccPersonalEncoding(String encoding){
        bccPersonalEncoding = encoding;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String getBccPersonalEncoding(){
        return bccPersonalEncoding;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setBccAddressValidate(boolean isValidate){
        isBccAddressValidate = isValidate;
    }
    // MailWriterServiceMBeanのJavaDoc
    public boolean isBccAddressValidate(){
        return isBccAddressValidate;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setReplyToAddressKey(String key){
        replyToAddressKey = key;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String getReplyToAddressKey(){
        return replyToAddressKey;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setReplyToAddress(String[] address){
        replyToAddress = address;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String[] getReplyToAddress(){
        return replyToAddress;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setReplyToPersonalKey(String key){
        replyToPersonalKey = key;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String getReplyToPersonalKey(){
        return replyToPersonalKey;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setReplyToPersonals(String[] personal){
        replyToPersonals = personal;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String[] getReplyToPersonals(){
        return replyToPersonals;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setReplyToPersonalEncodingKey(String key){
        replyToPersonalEncodingKey = key;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String getReplyToPersonalEncodingKey(){
        return replyToPersonalEncodingKey;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setReplyToPersonalEncodings(String[] encoding){
        replyToPersonalEncodings = encoding;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String[] getReplyToPersonalEncodings(){
        return replyToPersonalEncodings;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setReplyToPersonalEncoding(String encoding){
        replyToPersonalEncoding = encoding;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String getReplyToPersonalEncoding(){
        return replyToPersonalEncoding;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setReplyToAddressValidate(boolean isValidate){
        isReplyToAddressValidate = isValidate;
    }
    // MailWriterServiceMBeanのJavaDoc
    public boolean isReplyToAddressValidate(){
        return isReplyToAddressValidate;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setSubjectKey(String key){
        subjectKey = key;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String getSubjectKey(){
        return subjectKey;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setSubject(String subject){
        this.subject = subject;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String getSubject(){
        return subject;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setSubjectEncodingKey(String key){
        subjectEncodingKey = key;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String getSubjectEncodingKey(){
        return subjectEncodingKey;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setSubjectEncoding(String encoding){
        subjectEncoding = encoding;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String getSubjectEncoding(){
        return subjectEncoding;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setContentIDKey(String key){
        contentIDKey = key;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String getContentIDKey(){
        return contentIDKey;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setContentID(String id){
        this.contentID = id;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String getContentID(){
        return contentID;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setContentLanguageKey(String key){
        contentLanguageKey = key;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String getContentLanguageKey(){
        return contentLanguageKey;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setContentLanguage(String[] lang){
        contentLanguage = lang;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String[] getContentLanguage(){
        return contentLanguage;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setContentMD5Key(String key){
        contentMD5Key = key;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String getContentMD5Key(){
        return contentMD5Key;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setContentMD5(String val){
        contentMD5 = val;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String getContentMD5(){
        return contentMD5;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setDescriptionKey(String key){
        descriptionKey = key;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String getDescriptionKey(){
        return descriptionKey;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setDescription(String val){
        description = val;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String getDescription(){
        return description;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setDescriptionEncodingKey(String key){
        descriptionEncodingKey = key;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String getDescriptionEncodingKey(){
        return descriptionEncodingKey;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setDescriptionEncoding(String encoding){
        descriptionEncoding = encoding;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String getDescriptionEncoding(){
        return descriptionEncoding;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setDispositionKey(String key){
        dispositionKey = key;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String getDispositionKey(){
        return dispositionKey;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setDisposition(String val){
        disposition = val;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String getDisposition(){
        return disposition;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setFilePartKey(String key){
        filePartKey = key;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String getFilePartKey(){
        return filePartKey;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setFileCharsetKey(String key){
        fileCharsetKey = key;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String getFileCharsetKey(){
        return fileCharsetKey;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setFileCharset(String charset){
        fileCharset = charset;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String getFileCharset(){
        return fileCharset;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setFileLanguageKey(String key){
        fileLanguageKey = key;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String getFileLanguageKey(){
        return fileLanguageKey;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setFileLanguage(String lang){
        fileLanguage = lang;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String getFileLanguage(){
        return fileLanguage;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setBodyIndex(int index){
        bodyIndex = index;
    }
    // MailWriterServiceMBeanのJavaDoc
    public int getBodyIndex(){
        return bodyIndex;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setBodyIndexKey(String key){
        bodyIndexKey = key;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String getBodyIndexKey(){
        return bodyIndexKey;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setBodyText(String text){
        bodyText = text;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String getBodyText(){
        return bodyText;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setBodyEncoding(String encoding){
        bodyEncoding = encoding;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String getBodyEncoding(){
        return bodyEncoding;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setSmtpHostName(String name){
        smtpHostName = name;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String getSmtpHostName(){
        return smtpHostName;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setSmtpPort(int port){
        smtpPort = port;
    }
    // MailWriterServiceMBeanのJavaDoc
    public int getSmtpPort(){
        return smtpPort;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setRetryCount(int count){
        retryCount = count;
    }
    // MailWriterServiceMBeanのJavaDoc
    public int getRetryCount(){
        return retryCount;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setRetryInterval(long millis){
        retryInterval = millis;
    }
    // MailWriterServiceMBeanのJavaDoc
    public long getRetryInterval(){
        return retryInterval;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setSmtpKeepAliveCheckerSelectorServiceName(ServiceName name){
        smtpKeepAliveCheckerSelectorServiceName = name;
    }
    // MailWriterServiceMBeanのJavaDoc
    public ServiceName getSmtpKeepAliveCheckerSelectorServiceName(){
        return smtpKeepAliveCheckerSelectorServiceName;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setJndiFinderServiceName(ServiceName name){
        jndiFinderServiceName = name;
    }
    // MailWriterServiceMBeanのJavaDoc
    public ServiceName getJndiFinderServiceName(){
        return jndiFinderServiceName;
    }
    
    // MailWriterServiceMBeanのJavaDoc
    public void setMailSessionJndiName(String name){
        mailSessionJndiName = name;
    }
    // MailWriterServiceMBeanのJavaDoc
    public String getMailSessionJndiName(){
        return mailSessionJndiName;
    }
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始処理に失敗した場合
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
     * 指定されたレコードをメール送信する。<p>
     *
     * @param rec メール送信するレコード
     * @exception MessageWriteException メール送信に失敗した場合
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
     * 指定されたレコードをメール送信する。<p>
     *
     * @param rec メール送信するレコード
     * @exception MessageWriteException javax.mail.Messageに設定する値が不正な場合、またはSMTPサーバが死んでいる場合
     * @exception IllegalWriteException javax.mail.Messageの読み取り専用属性に書き込みが行われた場合
     * @exception MessagingException メール送信に失敗した場合
     * @exception NamingException javax.mail.SessionをJNDIからlookupするのに失敗した場合
     */
    public void sendMail(WritableRecord rec)
     throws MessageWriteException, IllegalWriteException, MessagingException,
            NamingException{
        
        final Map elementMap = rec.getElementMap();
        
        // Fromアドレスを作成する
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
        
        // Senderアドレスを作成する
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
        
        // Toアドレスを作成する
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
        
        // Ccアドレスを作成する
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
        
        // Bccアドレスを作成する
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
        
        // ReplyToアドレスを作成する
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
        
        // 本文を作成する
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
                // SMTPサーバのホスト名を設定する
                props.setProperty(
                    SESSION_PROPERTY_NAME_HOST,
                    checker.getHostIp()
                );
                // SMTPサーバのポート番号を設定する
                props.setProperty(
                    SESSION_PROPERTY_NAME_PORT,
                    String.valueOf(checker.getHostPort())
                );
            }else{
                // SMTPサーバのホスト名を設定する
                props.setProperty(SESSION_PROPERTY_NAME_HOST, smtpHostName);
                // SMTPサーバのポート番号を設定する
                props.setProperty(
                    SESSION_PROPERTY_NAME_PORT,
                    String.valueOf(smtpPort)
                );
            }
            
            // 送信プロトコルを設定する
            props.setProperty(
                SESSION_PROPERTY_NAME_TRANSPORT_PROTOCOL,
                SESSION_PROPERTY_VALUE_TRANSPORT_PROTOCOL
            );
            
            // envelope-fromアドレスを作成する
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
                // envelope-fromアドレスを設定する
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
        
        // Content-IDを設定する
        String val = getStringValue(elementMap, contentID, contentIDKey);
        if(val != null){
            message.setContentID(val);
        }
        
        // Content-Languageを設定する
        String[] vals = getStringArrayValue(
            elementMap,
            contentLanguage,
            contentLanguageKey
        );
        if(vals != null){
            message.setContentLanguage(vals);
        }
        
        // Content-MD5を設定する
        val = getStringValue(elementMap, contentMD5, contentMD5Key);
        if(val != null){
            message.setContentMD5(val);
        }
        
        // Content-Descriptionを設定する
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
        
        // Content-Dispositionを設定する
        val = getStringValue(elementMap, disposition, dispositionKey);
        if(val != null){
            message.setDisposition(val);
        }

        // Fromアドレスを設定する
        message.setFrom(fromInternetAddress);
        
        // Senderアドレスを設定する
        if(senderInternetAddress != null){
            message.setSender(senderInternetAddress);
        }
        
        // Toアドレスを設定する
        if(toInternetAddress != null){
            for(int i = 0; i < toInternetAddress.length; i++){
                message.addRecipient(
                    Message.RecipientType.TO,
                    toInternetAddress[i]
                );
            }
        }
        
        // Ccアドレスを設定する
        if(ccInternetAddress != null){
            for(int i = 0; i < ccInternetAddress.length; i++){
                message.addRecipient(
                    Message.RecipientType.CC,
                    ccInternetAddress[i]
                );
            }
        }
        
        // Bccアドレスを設定する
        if(bccInternetAddress != null){
            for(int i = 0; i < bccInternetAddress.length; i++){
                message.addRecipient(
                    Message.RecipientType.BCC,
                    bccInternetAddress[i]
                );
            }
        }
        
        // ReplyToアドレスを設定する
        if(replyToInternetAddress != null){
            message.setReplyTo(replyToInternetAddress);
        }
        
        // 題名を設定する
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
            
            // 本文を設定する
            final MimeBodyPart textPart = new MimeBodyPart();
            if(bodyEncoding == null){
                textPart.setText(body);
            }else{
                textPart.setText(body, bodyEncoding);
            }
            mp.addBodyPart(textPart);
            
            // 添付ファイルを添付する
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
            // 本文を設定する
            if(bodyEncoding == null){
                message.setText(body);
            }else{
                message.setText(body, bodyEncoding);
            }
        }
        
        // ヘッダを設定する
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

        // 送信時刻を設定する
        message.setSentDate(new Date());
        
        // 設定を有効にする
        message.saveChanges();
        
        // メールを送信する
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
