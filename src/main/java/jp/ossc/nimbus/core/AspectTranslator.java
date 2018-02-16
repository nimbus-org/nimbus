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

import java.security.*;

/**
 * アスペクト変換。<p>
 * アスペクト指向の概念に則って、クラスファイルを変換する変換者の実装すべきインタフェースである。<br>
 *
 * @author M.Takata
 * @see NimbusClassLoader#addAspectTranslator(AspectTranslator)
 */
public interface AspectTranslator{
    
    /**
     * このアスペクト変換を識別するアスペクトのキーを取得する。<p>
     * 同じキーを持つアスペクトは、重複してアスペクトされない。<br>
     *
     * @return アスペクトのキー
     */
    public String getAspectKey();
    
    /**
     * クラスファイルを変換する。<p>
     *
     * @param loader クラスファイルをロードするクラスローダ
     * @param className クラス名
     * @param domain クラスのドメイン
     * @param bytecode クラスファイルのバイト配列
     * @return 変換後のクラスファイルのバイト配列。変換対象でない場合は、nullを返す。
     */
    public byte[] transform(
        ClassLoader loader,
        String className,
        ProtectionDomain domain,
        byte[] bytecode
    );
}
