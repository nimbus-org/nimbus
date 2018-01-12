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
import java.net.*;
import java.util.*;
import org.xml.sax.*;

/**
 * Nimbus用EntityResolver。<p>
 * 外部エンティティの公開識別子"-//Nimbus//DTD Nimbus 1.0//JA"に対して、<a href="nimbus-service_1_0.dtd">"jp/ossc/nimbus/core/nimbus-service_1_0.dtd"</a>をマッピングするEntityResolverである。
 *
 * @author M.Takata
 */
public class NimbusEntityResolver implements EntityResolver{
    
    private static Map dtds = new Hashtable();
    
    static{
        registerDTD(
            "-//Nimbus//DTD Nimbus 1.0//JA",
            "jp/ossc/nimbus/core/nimbus-service_1_0.dtd"
        );
    }
    
    /**
     * 指定した外部エンティティの公開識別子に対して、DTDのリソース名を登録する。<p>
     *
     * @param publicId 外部エンティティの公開識別子
     * @param resource DTDのリソース名
     */
    public static void registerDTD(String publicId, String resource){
        dtds.put(publicId, resource);
    }
    
    /**
     * 指定した外部エンティティの公開識別子の登録を解除する。<p>
     *
     * @param publicId 外部エンティティの公開識別子
     */
    public static void unregisterDTD(String publicId){
        dtds.remove(publicId);
    }
    
    /**
     * 指定した外部エンティティの公開識別子が登録されているか調べる。<p>
     *
     * @param publicId 外部エンティティの公開識別子
     * @return 登録されている場合、true
     */
    public static boolean isRegisteredDTD(String publicId){
        return dtds.containsKey(publicId);
    }
    
    /**
     * アプリケーションが外部エンティティを解決できるようにする。<p>
     * 指定された公開識別子が{@link #registerDTD(String, String)}で登録された公開識別子であった場合は、登録されたリソース名でクラスパス上からDTDファイルを解決する。そうでない場合は、システム識別子で指定されたURLでDTDファイルを解決する。<br>
     * 
     * @param publicId 参照される外部エンティティの公開識別子。提供されなかった場合は null
     * @param systemId 参照される外部エンティティのシステム識別子
     */
    public InputSource resolveEntity(String publicId, String systemId){
        if(publicId == null || !dtds.containsKey(publicId)){
            return resolveEntity(systemId);
        }
        final String dtd = (String)dtds.get(publicId);
        if(dtd == null){
            return resolveEntity(systemId);
        }
        final ClassLoader loader
             = Thread.currentThread().getContextClassLoader();
        final InputStream dtdStream = loader.getResourceAsStream(dtd);
        if(dtdStream == null){
            return resolveEntity(systemId);
        }
        final InputSource inputSource = new InputSource(dtdStream);
        return inputSource;
    }
    
    private InputSource resolveEntity(String systemId){
        if(systemId == null){
            return null;
        }
        try{
            final URL url = new URL(systemId);
            final InputStream dtdStream = url.openStream();
            final InputSource inputSource = new InputSource(dtdStream);
            return inputSource;
        }catch(MalformedURLException e){
            // 無視する
        }catch(IOException e){
            // 無視する
        }
        return null;
   }
}
