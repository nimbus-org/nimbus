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

/**
 * サービス基底クラスMBeanインタフェース。<p>
 * {@link ServiceBase}クラスをMBeanとして実装するためのインタフェースである。<br>
 * このインタフェースを実装したクラスは、{@link Service}インタフェースがMBeanインタフェースとして認識される。<br>
 * 
 * @author M.Takata
 * @see ServiceBase
 */
public interface ServiceBaseMBean extends Service{
    
    /**
     * サービスを再起動する。<p>
     *
     * @exception IllegalStateException サービス状態チェックに失敗した場合
     * @exception Exception start()、stop()で例外が発生した場合
     * @see #start()
     * @see #stop()
     */
    public void restart() throws Exception;
    
    /**
     * Service内のログ出力に使用する{@link jp.ossc.nimbus.service.log.Logger}サービスの名前を設定する。<p>
     *
     * @param name Service内のログ出力に使用する{@link jp.ossc.nimbus.service.log.Logger}サービスの名前
     * @see #getSystemLoggerServiceName()
     */
    public void setSystemLoggerServiceName(ServiceName name);
    
    /**
     * Service内のログ出力に使用する{@link jp.ossc.nimbus.service.log.Logger}サービスの名前を取得する。<p>
     *
     * @return Service内のログ出力に使用する{@link jp.ossc.nimbus.service.log.Logger}サービスの名前
     * @see #setSystemLoggerServiceName(ServiceName)
     */
    public ServiceName getSystemLoggerServiceName();
    
    /**
     * Service内でのメッセージ取得に使用する{@link jp.ossc.nimbus.service.message.MessageRecordFactory}サービスの名前を設定する。<p>
     *
     * @param name Service内でのメッセージ取得に使用する{@link jp.ossc.nimbus.service.message.MessageRecordFactory}サービスの名前
     * @see #getSystemMessageRecordFactoryServiceName()
     */
    public void setSystemMessageRecordFactoryServiceName(ServiceName name);
    
    /**
     * Service内でのメッセージ取得に使用する{@link jp.ossc.nimbus.service.message.MessageRecordFactory}サービスの名前を取得する。<p>
     *
     * @return Service内でのメッセージ取得に使用する{@link jp.ossc.nimbus.service.message.MessageRecordFactory}サービスの名前
     * @see #setSystemMessageRecordFactoryServiceName(ServiceName)
     */
    public ServiceName getSystemMessageRecordFactoryServiceName();
}
