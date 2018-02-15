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

import jp.ossc.nimbus.core.*;

/**
 * {@link MailWriterService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see MailWriterService
 */
public interface MailWriterServiceMBean extends ServiceBaseMBean{
    
    /**
     * javax.mail.SessionをJNDIからlookupする時のデフォルトのJNDI名。<p>
     */
    public static final String DEFAULT_MAIL_SESSION_JNDI_NAME = "java:/Mail";
    
    /**
     * javax.mail.Sessionのプロパティを設定する。<p>
     *
     * @param prop javax.mail.Sessionのプロパティ
     */
    public void setSessionProperties(Properties prop);
    
    /**
     * javax.mail.Sessionのプロパティを取得する。<p>
     *
     * @return javax.mail.Sessionのプロパティ
     */
    public Properties getSessionProperties();
    
    /**
     * javax.mail.Authenticatorサービスのサービス名を設定する。<p>
     *
     * @param name javax.mail.Authenticatorサービスのサービス名
     */
    public void setAuthenticatorServiceName(ServiceName name);
    
    /**
     * javax.mail.Authenticatorサービスのサービス名を取得する。<p>
     *
     * @return javax.mail.Authenticatorサービスのサービス名
     */
    public ServiceName getAuthenticatorServiceName();
    
    /**
     * javax.mail.Messageのヘッダを設定する。<p>
     *
     * @param prop javax.mail.Messageのヘッダ
     */
    public void setHeaders(Properties prop);
    
    /**
     * javax.mail.Messageのヘッダを取得する。<p>
     *
     * @return javax.mail.Messageのヘッダ
     */
    public Properties getHeaders();
    
    /**
     * javax.mail.Messageのヘッダを{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を設定する。<p>
     *
     * @param keys WritableRecord内のキー名配列
     */
    public void setHeaderKeys(String[] keys);
    
    /**
     * javax.mail.Messageのヘッダを{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名をs取得する。<p>
     *
     * @return WritableRecord内のキー名配列
     */
    public String[] getHeaderKeys();
    
    /**
     * javax.mail.Sessionのプロパティ"mail.smtp.from"を{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を設定する。<p>
     *
     * @param key WritableRecord内のキー名
     */
    public void setEnvelopeFromAddressKey(String key);
    
    /**
     * javax.mail.Sessionのプロパティ"mail.smtp.from"を{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を取得する。<p>
     *
     * @return WritableRecord内のキー名
     */
    public String getEnvelopeFromAddressKey();
    
    /**
     * javax.mail.Sessionのプロパティ"mail.smtp.from"を設定する。<p>
     *
     * @param address javax.mail.Sessionのプロパティ"mail.smtp.from"
     */
    public void setEnvelopeFromAddress(String address);
    
    /**
     * javax.mail.Sessionのプロパティ"mail.smtp.from"を取得する。<p>
     *
     * @return javax.mail.Sessionのプロパティ"mail.smtp.from"
     */
    public String getEnvelopeFromAddress();
    
    /**
     * javax.mail.Sessionのプロパティ"mail.smtp.from"をメールアドレスとして正しいか検証するかどうかを設定する。<p>
     *
     * @param isValidate 検証する場合true
     */
    public void setEnvelopeFromAddressValidate(boolean isValidate);
    
    /**
     * javax.mail.Sessionのプロパティ"mail.smtp.from"をメールアドレスとして正しいか検証するかどうかを判定する。<p>
     *
     * @return trueの場合、検証する
     */
    public boolean isEnvelopeFromAddressValidate();
    
    /**
     * javax.mail.MessageのFromアドレスを{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を設定する。<p>
     *
     * @param key WritableRecord内のキー名
     */
    public void setFromAddressKey(String key);
    
    /**
     * javax.mail.MessageのFromアドレスを{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を取得する。<p>
     *
     * @return WritableRecord内のキー名
     */
    public String getFromAddressKey();
    
    /**
     * javax.mail.MessageのFromアドレスを設定する。<p>
     *
     * @param address javax.mail.MessageのFromアドレス
     */
    public void setFromAddress(String address);
    
    /**
     * javax.mail.MessageのFromアドレスを取得する。<p>
     *
     * @return javax.mail.MessageのFromアドレス
     */
    public String getFromAddress();
    
    /**
     * javax.mail.MessageのFromアドレスの表示名を{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を設定する。<p>
     *
     * @param key WritableRecord内のキー名
     */
    public void setFromPersonalKey(String key);
    
    /**
     * javax.mail.MessageのFromアドレスの表示名を{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を取得する。<p>
     *
     * @return WritableRecord内のキー名
     */
    public String getFromPersonalKey();
    
    /**
     * javax.mail.MessageのFromアドレスの表示名を設定する。<p>
     *
     * @param personal javax.mail.MessageのFromアドレスの表示名
     */
    public void setFromPersonal(String personal);
    
    /**
     * javax.mail.MessageのFromアドレスの表示名を取得する。<p>
     *
     * @return javax.mail.MessageのFromアドレスの表示名
     */
    public String getFromPersonal();
    
    /**
     * javax.mail.MessageのFromアドレスの表示名文字エンコーディングを{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を設定する。<p>
     *
     * @param key WritableRecord内のキー名
     */
    public void setFromPersonalEncodingKey(String key);
    
    /**
     * javax.mail.MessageのFromアドレスの表示名文字エンコーディングを{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を取得する。<p>
     *
     * @return WritableRecord内のキー名
     */
    public String getFromPersonalEncodingKey();
    
    /**
     * javax.mail.MessageのFromアドレスの表示名文字エンコーディングを設定する。<p>
     *
     * @param encoding javax.mail.MessageのFromアドレスの表示名文字エンコーディング
     */
    public void setFromPersonalEncoding(String encoding);
    
    /**
     * javax.mail.MessageのFromアドレスの表示名文字エンコーディングを取得する。<p>
     *
     * @return javax.mail.MessageのFromアドレスの表示名文字エンコーディング
     */
    public String getFromPersonalEncoding();
    
    /**
     * javax.mail.MessageのFromアドレスをメールアドレスとして正しいか検証するかどうかを設定する。<p>
     *
     * @param isValidate 検証する場合true
     */
    public void setFromAddressValidate(boolean isValidate);
    
    /**
     * javax.mail.MessageのFromアドレスをメールアドレスとして正しいか検証するかどうかを判定する。<p>
     *
     * @return trueの場合、検証する
     */
    public boolean isFromAddressValidate();
    
    /**
     * javax.mail.MessageのSenderアドレスを{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を設定する。<p>
     *
     * @param key WritableRecord内のキー名
     */
    public void setSenderAddressKey(String key);
    
    /**
     * javax.mail.MessageのSenderアドレスを{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を取得する。<p>
     *
     * @return WritableRecord内のキー名
     */
    public String getSenderAddressKey();
    
    /**
     * javax.mail.MessageのSenderアドレスを設定する。<p>
     *
     * @param address javax.mail.MessageのSenderアドレス
     */
    public void setSenderAddress(String address);
    
    /**
     * javax.mail.MessageのSenderアドレスを取得する。<p>
     *
     * @return javax.mail.MessageのSenderアドレス
     */
    public String getSenderAddress();
    
    /**
     * javax.mail.MessageのSenderアドレスの表示名を{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を設定する。<p>
     *
     * @param key WritableRecord内のキー名
     */
    public void setSenderPersonalKey(String key);
    
    /**
     * javax.mail.MessageのSenderアドレスの表示名を{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を取得する。<p>
     *
     * @return WritableRecord内のキー名
     */
    public String getSenderPersonalKey();
    
    /**
     * javax.mail.MessageのSenderアドレスの表示名を設定する。<p>
     *
     * @param personal javax.mail.MessageのSenderアドレスの表示名
     */
    public void setSenderPersonal(String personal);
    
    /**
     * javax.mail.MessageのSenderアドレスの表示名を取得する。<p>
     *
     * @return javax.mail.MessageのSenderアドレスの表示名
     */
    public String getSenderPersonal();
    
    /**
     * javax.mail.MessageのSenderアドレスの表示名文字エンコーディングを{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を設定する。<p>
     *
     * @param key WritableRecord内のキー名
     */
    public void setSenderPersonalEncodingKey(String key);
    
    /**
     * javax.mail.MessageのSenderアドレスの表示名文字エンコーディングを{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を取得する。<p>
     *
     * @return WritableRecord内のキー名
     */
    public String getSenderPersonalEncodingKey();
    
    /**
     * javax.mail.MessageのSenderアドレスの表示名文字エンコーディングを設定する。<p>
     *
     * @param encoding javax.mail.MessageのSenderアドレスの表示名文字エンコーディング
     */
    public void setSenderPersonalEncoding(String encoding);
    
    /**
     * javax.mail.MessageのSenderアドレスの表示名文字エンコーディングを取得する。<p>
     *
     * @return javax.mail.MessageのSenderアドレスの表示名文字エンコーディング
     */
    public String getSenderPersonalEncoding();
    
    /**
     * javax.mail.MessageのSenderアドレスをメールアドレスとして正しいか検証するかどうかを設定する。<p>
     *
     * @param isValidate 検証する場合true
     */
    public void setSenderAddressValidate(boolean isValidate);
    
    /**
     * javax.mail.MessageのSenderアドレスをメールアドレスとして正しいか検証するかどうかを判定する。<p>
     *
     * @return trueの場合、検証する
     */
    public boolean isSenderAddressValidate();
    
    /**
     * javax.mail.MessageのToアドレスを{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を設定する。<p>
     *
     * @param key WritableRecord内のキー名
     */
    public void setToAddressKey(String key);
    
    /**
     * javax.mail.MessageのToアドレスを{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を取得する。<p>
     *
     * @return WritableRecord内のキー名
     */
    public String getToAddressKey();
    
    /**
     * javax.mail.MessageのToアドレスを設定する。<p>
     *
     * @param address javax.mail.MessageのToアドレス配列
     */
    public void setToAddress(String[] address);
    
    /**
     * javax.mail.MessageのToアドレスを取得する。<p>
     *
     * @return javax.mail.MessageのToアドレス配列
     */
    public String[] getToAddress();
    
    /**
     * javax.mail.MessageのToアドレスの表示名を{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を設定する。<p>
     *
     * @param key WritableRecord内のキー名
     */
    public void setToPersonalKey(String key);
    
    /**
     * javax.mail.MessageのToアドレスの表示名を{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を取得する。<p>
     *
     * @return WritableRecord内のキー名
     */
    public String getToPersonalKey();
    
    /**
     * javax.mail.MessageのToアドレスの表示名を設定する。<p>
     *
     * @param personal javax.mail.MessageのToアドレスの表示名配列
     */
    public void setToPersonals(String[] personal);
    
    /**
     * javax.mail.MessageのToアドレスの表示名を取得する。<p>
     *
     * @return javax.mail.MessageのToアドレスの表示名配列
     */
    public String[] getToPersonals();
    
    /**
     * javax.mail.MessageのToアドレスの表示名文字エンコーディングを{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を設定する。<p>
     *
     * @param key WritableRecord内のキー名
     */
    public void setToPersonalEncodingKey(String key);
    
    /**
     * javax.mail.MessageのToアドレスの表示名文字エンコーディングを{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を取得する。<p>
     *
     * @return WritableRecord内のキー名
     */
    public String getToPersonalEncodingKey();
    
    /**
     * javax.mail.MessageのToアドレスの表示名文字エンコーディングを設定する。<p>
     *
     * @param encoding javax.mail.MessageのToアドレスの表示名文字エンコーディング配列
     */
    public void setToPersonalEncodings(String[] encoding);
    
    /**
     * javax.mail.MessageのToアドレスの表示名文字エンコーディングを取得する。<p>
     *
     * @return javax.mail.MessageのToアドレスの表示名文字エンコーディング配列
     */
    public String[] getToPersonalEncodings();
    
    /**
     * javax.mail.MessageのToアドレスの表示名文字エンコーディングを設定する。<p>
     *
     * @param encoding javax.mail.MessageのToアドレスの表示名文字エンコーディング
     */
    public void setToPersonalEncoding(String encoding);
    
    /**
     * javax.mail.MessageのToアドレスの表示名文字エンコーディングを取得する。<p>
     *
     * @return javax.mail.MessageのToアドレスの表示名文字エンコーディング
     */
    public String getToPersonalEncoding();
    
    /**
     * javax.mail.MessageのToアドレスをメールアドレスとして正しいか検証するかどうかを設定する。<p>
     *
     * @param isValidate 検証する場合true
     */
    public void setToAddressValidate(boolean isValidate);
    
    /**
     * javax.mail.MessageのToアドレスをメールアドレスとして正しいか検証するかどうかを判定する。<p>
     *
     * @return trueの場合、検証する
     */
    public boolean isToAddressValidate();
    
    /**
     * javax.mail.MessageのCcアドレスを{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を設定する。<p>
     *
     * @param key WritableRecord内のキー名
     */
    public void setCcAddressKey(String key);
    
    /**
     * javax.mail.MessageのCcアドレスを{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を取得する。<p>
     *
     * @return WritableRecord内のキー名
     */
    public String getCcAddressKey();
    
    /**
     * javax.mail.MessageのCcアドレスを設定する。<p>
     *
     * @param address javax.mail.MessageのCcアドレス配列
     */
    public void setCcAddress(String[] address);
    
    /**
     * javax.mail.MessageのCcアドレスを取得する。<p>
     *
     * @return javax.mail.MessageのCcアドレス配列
     */
    public String[] getCcAddress();
    
    /**
     * javax.mail.MessageのCcアドレスの表示名を{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を設定する。<p>
     *
     * @param key WritableRecord内のキー名
     */
    public void setCcPersonalKey(String key);
    
    /**
     * javax.mail.MessageのCcアドレスの表示名を{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を取得する。<p>
     *
     * @return WritableRecord内のキー名
     */
    public String getCcPersonalKey();
    
    /**
     * javax.mail.MessageのCcアドレスの表示名を設定する。<p>
     *
     * @param personal javax.mail.MessageのCcアドレスの表示名配列
     */
    public void setCcPersonals(String[] personal);
    
    /**
     * javax.mail.MessageのCcアドレスの表示名を取得する。<p>
     *
     * @return javax.mail.MessageのCcアドレスの表示名配列
     */
    public String[] getCcPersonals();
    
    /**
     * javax.mail.MessageのCcアドレスの表示名文字エンコーディングを{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を設定する。<p>
     *
     * @param key WritableRecord内のキー名
     */
    public void setCcPersonalEncodingKey(String key);
    
    /**
     * javax.mail.MessageのCcアドレスの表示名文字エンコーディングを{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を取得する。<p>
     *
     * @return WritableRecord内のキー名
     */
    public String getCcPersonalEncodingKey();
    
    /**
     * javax.mail.MessageのCcアドレスの表示名文字エンコーディングを設定する。<p>
     *
     * @param encoding javax.mail.MessageのCcアドレスの表示名文字エンコーディング配列
     */
    public void setCcPersonalEncodings(String[] encoding);
    
    /**
     * javax.mail.MessageのCcアドレスの表示名文字エンコーディングを取得する。<p>
     *
     * @return javax.mail.MessageのCcアドレスの表示名文字エンコーディング
     */
    public String[] getCcPersonalEncodings();
    
    /**
     * javax.mail.MessageのCcアドレスの表示名文字エンコーディングを設定する。<p>
     *
     * @param encoding javax.mail.MessageのCcアドレスの表示名文字エンコーディング
     */
    public void setCcPersonalEncoding(String encoding);
    
    /**
     * javax.mail.MessageのCcアドレスの表示名文字エンコーディングを取得する。<p>
     *
     * @return javax.mail.MessageのCcアドレスの表示名文字エンコーディング
     */
    public String getCcPersonalEncoding();
    
    /**
     * javax.mail.MessageのCcアドレスをメールアドレスとして正しいか検証するかどうかを設定する。<p>
     *
     * @param isValidate 検証する場合true
     */
    public void setCcAddressValidate(boolean isValidate);
    
    /**
     * javax.mail.MessageのCcアドレスをメールアドレスとして正しいか検証するかどうかを判定する。<p>
     *
     * @return trueの場合、検証する
     */
    public boolean isCcAddressValidate();
    
    /**
     * javax.mail.MessageのBccアドレスを{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を設定する。<p>
     *
     * @param key WritableRecord内のキー名
     */
    public void setBccAddressKey(String key);
    
    /**
     * javax.mail.MessageのBccアドレスを{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を取得する。<p>
     *
     * @return WritableRecord内のキー名
     */
    public String getBccAddressKey();
    
    /**
     * javax.mail.MessageのBccアドレスを設定する。<p>
     *
     * @param address javax.mail.MessageのBccアドレス配列
     */
    public void setBccAddress(String[] address);
    
    /**
     * javax.mail.MessageのBccアドレスを取得する。<p>
     *
     * @return javax.mail.MessageのBccアドレス配列
     */
    public String[] getBccAddress();
    
    /**
     * javax.mail.MessageのBccアドレスの表示名を{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を設定する。<p>
     *
     * @param key WritableRecord内のキー名
     */
    public void setBccPersonalKey(String key);
    
    /**
     * javax.mail.MessageのBccアドレスの表示名を{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を取得する。<p>
     *
     * @return WritableRecord内のキー名
     */
    public String getBccPersonalKey();
    
    /**
     * javax.mail.MessageのBccアドレスの表示名を設定する。<p>
     *
     * @param personal javax.mail.MessageのBccアドレスの表示名配列
     */
    public void setBccPersonals(String[] personal);
    
    /**
     * javax.mail.MessageのBccアドレスの表示名を取得する。<p>
     *
     * @return javax.mail.MessageのBccアドレスの表示名配列
     */
    public String[] getBccPersonals();
    
    /**
     * javax.mail.MessageのBccアドレスの表示名文字エンコーディングを{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を設定する。<p>
     *
     * @param key WritableRecord内のキー名
     */
    public void setBccPersonalEncodingKey(String key);
    
    /**
     * javax.mail.MessageのBccアドレスの表示名文字エンコーディングを{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を取得する。<p>
     *
     * @return WritableRecord内のキー名
     */
    public String getBccPersonalEncodingKey();
    
    /**
     * javax.mail.MessageのBccアドレスの表示名文字エンコーディングを設定する。<p>
     *
     * @param encoding javax.mail.MessageのBccアドレスの表示名文字エンコーディング配列
     */
    public void setBccPersonalEncodings(String[] encoding);
    
    /**
     * javax.mail.MessageのBccアドレスの表示名文字エンコーディングを取得する。<p>
     *
     * @return javax.mail.MessageのBccアドレスの表示名文字エンコーディング
     */
    public String[] getBccPersonalEncodings();
    
    /**
     * javax.mail.MessageのBccアドレスの表示名文字エンコーディングを設定する。<p>
     *
     * @param encoding javax.mail.MessageのBccアドレスの表示名文字エンコーディング
     */
    public void setBccPersonalEncoding(String encoding);
    
    /**
     * javax.mail.MessageのBccアドレスの表示名文字エンコーディングを取得する。<p>
     *
     * @return javax.mail.MessageのBccアドレスの表示名文字エンコーディング
     */
    public String getBccPersonalEncoding();
    
    /**
     * javax.mail.MessageのBccアドレスをメールアドレスとして正しいか検証するかどうかを設定する。<p>
     *
     * @param isValidate 検証する場合true
     */
    public void setBccAddressValidate(boolean isValidate);
    
    /**
     * javax.mail.MessageのBccアドレスをメールアドレスとして正しいか検証するかどうかを判定する。<p>
     *
     * @return trueの場合、検証する
     */
    public boolean isBccAddressValidate();
    
    /**
     * javax.mail.MessageのReplyToアドレスを{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を設定する。<p>
     *
     * @param key WritableRecord内のキー名
     */
    public void setReplyToAddressKey(String key);
    
    /**
     * javax.mail.MessageのReplyToアドレスを{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を取得する。<p>
     *
     * @return WritableRecord内のキー名
     */
    public String getReplyToAddressKey();
    
    /**
     * javax.mail.MessageのReplyToアドレスを設定する。<p>
     *
     * @param address javax.mail.MessageのReplyToアドレス配列
     */
    public void setReplyToAddress(String[] address);
    
    /**
     * javax.mail.MessageのReplyToアドレスを取得する。<p>
     *
     * @return javax.mail.MessageのReplyToアドレス配列
     */
    public String[] getReplyToAddress();
    
    /**
     * javax.mail.MessageのReplyToアドレスの表示名を{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を設定する。<p>
     *
     * @param key WritableRecord内のキー名
     */
    public void setReplyToPersonalKey(String key);
    
    /**
     * javax.mail.MessageのReplyToアドレスの表示名を{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を取得する。<p>
     *
     * @return WritableRecord内のキー名
     */
    public String getReplyToPersonalKey();
    
    /**
     * javax.mail.MessageのReplyToアドレスの表示名を設定する。<p>
     *
     * @param personal javax.mail.MessageのReplyToアドレスの表示名配列
     */
    public void setReplyToPersonals(String[] personal);
    
    /**
     * javax.mail.MessageのReplyToアドレスの表示名を取得する。<p>
     *
     * @return javax.mail.MessageのReplyToアドレスの表示名配列
     */
    public String[] getReplyToPersonals();
    
    /**
     * javax.mail.MessageのReplyToアドレスの表示名文字エンコーディングを{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を設定する。<p>
     *
     * @param key WritableRecord内のキー名
     */
    public void setReplyToPersonalEncodingKey(String key);
    
    /**
     * javax.mail.MessageのReplyToアドレスの表示名文字エンコーディングを{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を取得する。<p>
     *
     * @return WritableRecord内のキー名
     */
    public String getReplyToPersonalEncodingKey();
    
    /**
     * javax.mail.MessageのReplyToアドレスの表示名文字エンコーディングを設定する。<p>
     *
     * @param encoding javax.mail.MessageのReplyToアドレスの表示名文字エンコーディング配列
     */
    public void setReplyToPersonalEncodings(String[] encoding);
    
    /**
     * javax.mail.MessageのReplyToアドレスの表示名文字エンコーディングを取得する。<p>
     *
     * @return javax.mail.MessageのReplyToアドレスの表示名文字エンコーディング
     */
    public String[] getReplyToPersonalEncodings();
    
    /**
     * javax.mail.MessageのReplyToアドレスの表示名文字エンコーディングを設定する。<p>
     *
     * @param encoding javax.mail.MessageのReplyToアドレスの表示名文字エンコーディング
     */
    public void setReplyToPersonalEncoding(String encoding);
    
    /**
     * javax.mail.MessageのReplyToアドレスの表示名文字エンコーディングを取得する。<p>
     *
     * @return javax.mail.MessageのReplyToアドレスの表示名文字エンコーディング
     */
    public String getReplyToPersonalEncoding();
    
    /**
     * javax.mail.MessageのReplyToアドレスをメールアドレスとして正しいか検証するかどうかを設定する。<p>
     *
     * @param isValidate 検証する場合true
     */
    public void setReplyToAddressValidate(boolean isValidate);
    
    /**
     * javax.mail.MessageのReplyToアドレスをメールアドレスとして正しいか検証するかどうかを判定する。<p>
     *
     * @return trueの場合、検証する
     */
    public boolean isReplyToAddressValidate();
    
    /**
     * javax.mail.MessageのSubjectを{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を設定する。<p>
     *
     * @param key WritableRecord内のキー名
     */
    public void setSubjectKey(String key);
    
    /**
     * javax.mail.MessageのSubjectを{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を取得する。<p>
     *
     * @return WritableRecord内のキー名
     */
    public String getSubjectKey();
    
    /**
     * javax.mail.MessageのSubjectを設定する。<p>
     *
     * @param subject javax.mail.MessageのSubject
     */
    public void setSubject(String subject);
    
    /**
     * javax.mail.MessageのSubjectを取得する。<p>
     *
     * @return javax.mail.MessageのSubject
     */
    public String getSubject();
    
    /**
     * javax.mail.MessageのSubjectの文字エンコーディングを{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を設定する。<p>
     *
     * @param key WritableRecord内のキー名
     */
    public void setSubjectEncodingKey(String key);
    
    /**
     * javax.mail.MessageのSubjectの文字エンコーディングを{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を取得する。<p>
     *
     * @return WritableRecord内のキー名
     */
    public String getSubjectEncodingKey();
    
    /**
     * javax.mail.MessageのSubjectの文字エンコーディングを設定する。<p>
     *
     * @param encoding javax.mail.MessageのSubjectの文字エンコーディング
     */
    public void setSubjectEncoding(String encoding);
    
    /**
     * javax.mail.MessageのSubjectの文字エンコーディングを取得する。<p>
     *
     * @return javax.mail.MessageのSubjectの文字エンコーディング
     */
    public String getSubjectEncoding();
    
    /**
     * javax.mail.MessageのContentIDを{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を設定する。<p>
     *
     * @param key WritableRecord内のキー名
     */
    public void setContentIDKey(String key);
    
    /**
     * javax.mail.MessageのContentIDを{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を取得する。<p>
     *
     * @return WritableRecord内のキー名
     */
    public String getContentIDKey();
    
    /**
     * javax.mail.MessageのContentIDを設定する。<p>
     *
     * @param id javax.mail.MessageのContentID
     */
    public void setContentID(String id);
    
    /**
     * javax.mail.MessageのContentIDを取得する。<p>
     *
     * @return javax.mail.MessageのContentID
     */
    public String getContentID();
    
    /**
     * javax.mail.MessageのContentLanguageを{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を設定する。<p>
     *
     * @param key WritableRecord内のキー名
     */
    public void setContentLanguageKey(String key);
    
    /**
     * javax.mail.MessageのContentLanguageを{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を取得する。<p>
     *
     * @return WritableRecord内のキー名
     */
    public String getContentLanguageKey();
    
    /**
     * javax.mail.MessageのContentLanguageを設定する。<p>
     *
     * @param lang javax.mail.MessageのContentLanguage
     */
    public void setContentLanguage(String[] lang);
    
    /**
     * javax.mail.MessageのContentLanguageを取得する。<p>
     *
     * @return javax.mail.MessageのContentLanguage
     */
    public String[] getContentLanguage();
    
    /**
     * javax.mail.MessageのContentMD5を{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を設定する。<p>
     *
     * @param key WritableRecord内のキー名
     */
    public void setContentMD5Key(String key);
    
    /**
     * javax.mail.MessageのContentMD5を{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を取得する。<p>
     *
     * @return WritableRecord内のキー名
     */
    public String getContentMD5Key();
    
    /**
     * javax.mail.MessageのContentMD5を設定する。<p>
     *
     * @param val javax.mail.MessageのContentMD5
     */
    public void setContentMD5(String val);
    
    /**
     * javax.mail.MessageのContentMD5を取得する。<p>
     *
     * @return javax.mail.MessageのContentMD5
     */
    public String getContentMD5();
    
    /**
     * javax.mail.MessageのDescriptionを{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を設定する。<p>
     *
     * @param key WritableRecord内のキー名
     */
    public void setDescriptionKey(String key);
    
    /**
     * javax.mail.MessageのDescriptionを{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を取得する。<p>
     *
     * @return WritableRecord内のキー名
     */
    public String getDescriptionKey();
    
    /**
     * javax.mail.MessageのDescriptionを設定する。<p>
     *
     * @param val javax.mail.MessageのDescription
     */
    public void setDescription(String val);
    
    /**
     * javax.mail.MessageのDescriptionを取得する。<p>
     *
     * @return javax.mail.MessageのDescription
     */
    public String getDescription();
    
    /**
     * javax.mail.MessageのDescriptionの文字エンコーディングを{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を設定する。<p>
     *
     * @param key WritableRecord内のキー名
     */
    public void setDescriptionEncodingKey(String key);
    
    /**
     * javax.mail.MessageのDescriptionの文字エンコーディングを{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を取得する。<p>
     *
     * @return WritableRecord内のキー名
     */
    public String getDescriptionEncodingKey();
    
    /**
     * javax.mail.MessageのDescriptionの文字エンコーディングを設定する。<p>
     *
     * @param encoding javax.mail.MessageのDescriptionの文字エンコーディング
     */
    public void setDescriptionEncoding(String encoding);
    
    /**
     * javax.mail.MessageのDescriptionの文字エンコーディングを取得する。<p>
     *
     * @return javax.mail.MessageのDescriptionの文字エンコーディング
     */
    public String getDescriptionEncoding();
    
    /**
     * javax.mail.MessageのDispositionを{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を設定する。<p>
     *
     * @param key WritableRecord内のキー名
     */
    public void setDispositionKey(String key);
    
    /**
     * javax.mail.MessageのDispositionを{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を取得する。<p>
     *
     * @return WritableRecord内のキー名
     */
    public String getDispositionKey();
    
    /**
     * javax.mail.MessageのDispositionを設定する。<p>
     *
     * @param val javax.mail.MessageのDisposition
     */
    public void setDisposition(String val);
    
    /**
     * javax.mail.MessageのDispositionを取得する。<p>
     *
     * @return javax.mail.MessageのDisposition
     */
    public String getDisposition();
    
    /**
     * 添付ファイルを{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を設定する。<p>
     *
     * @param key WritableRecord内のキー名
     */
    public void setFilePartKey(String key);
    
    /**
     * 添付ファイルを{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を取得する。<p>
     *
     * @return WritableRecord内のキー名
     */
    public String getFilePartKey();
    
    /**
     * 添付ファイルのファイル名の文字コードを{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を設定する。<p>
     *
     * @param key WritableRecord内のキー名
     */
    public void setFileCharsetKey(String key);
    
    /**
     * 添付ファイルのファイル名の文字コードを{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を取得する。<p>
     *
     * @return WritableRecord内のキー名
     */
    public String getFileCharsetKey();
    
    /**
     * 添付ファイルのファイル名の文字コードをを設定する。<p>
     *
     * @param charset 文字コード
     */
    public void setFileCharset(String charset);
    
    /**
     * 添付ファイルのファイル名の文字コードを取得する。<p>
     *
     * @return 文字コード
     */
    public String getFileCharset();
    
    /**
     * 添付ファイルのファイル名の言語コードを{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を設定する。<p>
     *
     * @param key WritableRecord内のキー名
     */
    public void setFileLanguageKey(String key);
    
    /**
     * 添付ファイルのファイル名の言語コードを{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内のキー名を取得する。<p>
     *
     * @return WritableRecord内のキー名
     */
    public String getFileLanguageKey();
    
    /**
     * 添付ファイルのファイル名の言語コードをを設定する。<p>
     *
     * @param lang 言語コード
     */
    public void setFileLanguage(String lang);
    
    /**
     * 添付ファイルのファイル名の言語コードを取得する。<p>
     *
     * @return 言語コード
     */
    public String getFileLanguage();
    
    /**
     * メールの本文を{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内の開始インデックスを指定する{@link jp.ossc.nimbus.service.writer.WritableElement WritableElement}のキー名を設定する。<p>
     *
     * @param key WritableRecord内の開始インデックスを指定するWritableElementのキー名
     */
    public void setBodyIndexKey(String key);
    
    /**
     * メールの本文を{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内の開始インデックスを指定する{@link jp.ossc.nimbus.service.writer.WritableElement WritableElement}のキー名を取得する。<p>
     *
     * @return WritableRecord内の開始インデックスを指定するWritableElementのキー名
     */
    public String getBodyIndexKey();
    
    /**
     * メールの本文を{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内の開始インデックスを設定する。<p>
     *
     * @param index WritableRecord内の開始インデックス
     */
    public void setBodyIndex(int index);
    
    /**
     * メールの本文を{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}から取得して設定する際の、WritableRecord内の開始インデックスを取得する。<p>
     *
     * @return WritableRecord内の開始インデックス
     */
    public int getBodyIndex();
    
    /**
     * メールの本文を設定する。<p>
     *
     * @param text メールの本文
     */
    public void setBodyText(String text);
    
    /**
     * メールの本文を取得する。<p>
     *
     * @return メールの本文
     */
    public String getBodyText();
    
    /**
     * メールの本文の文字エンコーディングを設定する。<p>
     *
     * @param encoding メールの本文の文字エンコーディング
     */
    public void setBodyEncoding(String encoding);
    
    /**
     * メールの本文の文字エンコーディングを取得する。<p>
     *
     * @return メールの本文の文字エンコーディング
     */
    public String getBodyEncoding();
    
    /**
     * SMTPサーバのホスト名を設定する。<p>
     *
     * @param name SMTPサーバのホスト名
     */
    public void setSmtpHostName(String name);
    
    /**
     * SMTPサーバのホスト名を取得する。<p>
     *
     * @return SMTPサーバのホスト名
     */
    public String getSmtpHostName();
    
    /**
     * SMTPサーバのポート番号を設定する。<p>
     *
     * @param port SMTPサーバのポート番号
     */
    public void setSmtpPort(int port);
    
    /**
     * SMTPサーバのポート番号を取得する。<p>
     *
     * @return SMTPサーバのポート番号
     */
    public int getSmtpPort();
    
    /**
     * {@link jp.ossc.nimbus.service.keepalive.smtp.SmtpKeepAliveChecker SmtpKeepAliveChecker}を選択する{@link jp.ossc.nimbus.service.keepalive.KeepAliveCheckerSelector KeepAliveCheckerSelector}サービスのサービス名を設定する。<p>
     *
     * @param name KeepAliveCheckerSelectorサービスのサービス名
     */
    public void setSmtpKeepAliveCheckerSelectorServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.keepalive.smtp.SmtpKeepAliveChecker SmtpKeepAliveChecker}を選択する{@link jp.ossc.nimbus.service.keepalive.KeepAliveCheckerSelector KeepAliveCheckerSelector}サービスのサービス名を取得する。<p>
     *
     * @return KeepAliveCheckerSelectorサービスのサービス名
     */
    public ServiceName getSmtpKeepAliveCheckerSelectorServiceName();
    
    /**
     * メール送信リトライ回数を設定する。<p>
     *
     * @param count メール送信リトライ回数
     */
    public void setRetryCount(int count);
    
    /**
     * メール送信リトライ回数を取得する。<p>
     *
     * @return メール送信リトライ回数
     */
    public int getRetryCount();
    
    /**
     * メール送信リトライ間隔[ms]を設定する。<p>
     *
     * @param millis メール送信リトライ間隔[ms]
     */
    public void setRetryInterval(long millis);
    
    /**
     * メール送信リトライ間隔[ms]を取得する。<p>
     *
     * @return メール送信リトライ間隔[ms]
     */
    public long getRetryInterval();
    
    /**
     * javax.mail.SessionをJNDIからlookupする際に使用する{@link jp.ossc.nimbus.service.jndi.JndiFinder JndiFinder}サービスのサービス名を設定する。<p>
     *
     * @param name JndiFinderサービスのサービス名
     */
    public void setJndiFinderServiceName(ServiceName name);
    
    /**
     * javax.mail.SessionをJNDIからlookupする際に使用する{@link jp.ossc.nimbus.service.jndi.JndiFinder JndiFinder}サービスのサービス名を取得する。<p>
     *
     * @return JndiFinderサービスのサービス名
     */
    public ServiceName getJndiFinderServiceName();
    
    /**
     * javax.mail.SessionをJNDIからlookupする際に使用するJNDI名を設定する。<p>
     *
     * @param name javax.mail.SessionのJNDI名
     */
    public void setMailSessionJndiName(String name);
    
    /**
     * javax.mail.SessionをJNDIからlookupする際に使用するJNDI名を取得する。<p>
     *
     * @return javax.mail.SessionのJNDI名
     */
    public String getMailSessionJndiName();
}