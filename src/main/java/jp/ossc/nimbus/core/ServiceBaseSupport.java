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
 * サービス基底サポートインタフェース。<p>
 * {@link ServiceBase}クラスを継承しなくても、ServiceBaseの実装を利用できるようにするためのインタフェースである。<br>
 * このインタフェースを実装したクラスは、{@link ServiceManager}に登録する際に、ServiceBaseクラスでラップされて登録される。登録されたこのクラスのインスタンスを、{@link ServiceManager#getService(String)}で、取得して使用する場合には、ラップされたオブジェクトが取得され、ServiceBaseを継承したクラスと同等の機能を使用できる。<br>
 * 
 * @author M.Takata
 * @see ServiceBase
 */
public interface ServiceBaseSupport{
    
    /**
     * このサービスをラップする{@link ServiceBase}を設定する。<p>
     * 
     * @param service このサービスをラップするServiceBase
     */
    public void setServiceBase(ServiceBase service);
    
    /**
     * サービスを生成する。<p>
     * このサービスに必要なオブジェクトの生成などの初期化処理を行う。<br>
     * このインタフェースをimplementsしてサービスを実装するサービス開発者は、サービスの生成処理を、このメソッドに実装すること。<br>
     *
     * @exception Exception サービスの生成処理に失敗した場合
     * @see ServiceBase#create()
     */
    public void createService() throws Exception;
    
    /**
     * サービスを開始する。<p>
     * このサービスを利用可能な状態にする。このメソッドの呼び出し後は、このサービスの機能を利用できる事が保証される。<br>
     * このインタフェースをimplementsしてサービスを実装するサービス開発者は、サービスの開始処理を、このメソッドに実装すること。<br>
     *
     * @exception Exception サービスの開始処理に失敗した場合
     * @see ServiceBase#start()
     */
    public void startService() throws Exception;
    
    /**
     * サービスを停止する。<p>
     * このサービスを利用不可能な状態にする。このメソッドの呼び出し後は、このサービスの機能を利用できる事は保証されない。<br>
     * このインタフェースをimplementsしてサービスを実装するサービス開発者は、サービスの停止処理を、このメソッドに実装すること。<br>
     *
     * @exception Exception サービスの停止処理に失敗した場合。但し、stop()で握り潰されて、処理は続行される。
     * @see ServiceBase#stop()
     */
    public void stopService() throws Exception;
    
    /**
     * サービスを破棄する。<p>
     * このサービスで使用するリソースを開放する。このメソッドの呼び出し後は、このサービスの機能を利用できる事は保証されない。<br>
     * このインタフェースをimplementsしてサービスを実装するサービス開発者は、サービスの破棄処理を、このメソッドに実装すること。<br>
     *
     * @exception Exception サービスの破棄処理に失敗した場合。但し、destroy()で握り潰されて、処理は続行される。
     * @see ServiceBase#destroy()
     */
    public void destroyService() throws Exception;
}