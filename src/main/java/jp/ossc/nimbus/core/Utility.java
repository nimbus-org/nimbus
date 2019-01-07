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

import java.net.*;
import java.io.File;
import java.lang.reflect.Array;

/**
 * Coreユーティリティ。<p>
 * 
 * @author M.Takata
 */
public class Utility{
    /**
     * システムプロパティ参照開始文字列。<p>
     */
    public static final String SYSTEM_PROPERTY_START = "${";
    /**
     * システムプロパティ参照終了文字列。<p>
     */
    public static final String SYSTEM_PROPERTY_END = "}";
    
    /**
     * デフォルトのサービス定義ファイルのURLをシステムプロパティで指定するためのプロパティ名。<p>
     */
    private static final String DEFAULT_SERVICE_FILE_PROPERTY_KEY
         = "jp.ossc.nimbus.service.url";
    
    /**
     * デフォルトのサービス定義ファイル名。<p>
     */
    private static final String DEFAULT_SERVICE_FILE
         = "nimbus-service.xml";
    
    private static final String ARRAY_CLASS_SUFFIX = "[]";
    
    /**
     * サービス定義URLのデフォルトの値を取得する。<p>
     * デフォルトのURLの決定は、以下の順序で行われる。<br>
     * <ol>
     *   <li>システムプロパティjp.ossc.nimbus.service.urlで指定された値を、{@link #convertServicePathToURL(String)}でURLに変換した値</li>
     *   <li>このクラスのクラスファイルがロードされたクラスパス上から、nimbus-service.xmlを{@link ClassLoader#getResource(String)}でリソースとして取得したURL</li>
     * </ol>
     * 
     * @return デフォルトのURL
     */
    public static URL getDefaultServiceURL(){
        final String urlString = System.getProperty(
            DEFAULT_SERVICE_FILE_PROPERTY_KEY
        );
        
        URL url = null;
        if(urlString != null){
            try{
                url = convertServicePathToURL(urlString);
            }catch(IllegalArgumentException e){
            }
            if(url != null){
                return url;
            }
        }
        
        url = Utility.class.getClassLoader().getResource(
            DEFAULT_SERVICE_FILE
        );
        return url;
    }
    
    /**
     * 指定されたサービス定義のパスをURLに変換する。<p>
     * 以下の順で、変換を行う。<br>
     * <ol>
     *   <li>指定されたパスがnull、または空文字の場合、{@link #getDefaultServiceURL()}で取得されるURL</li>
     *   <li>指定されたパスがローカルファイルとして存在する場合、ローカルパスをURLに変換したURL</li>
     *   <li>指定されたパスがこのクラスをロードしたクラスローダのリソースとして存在する場合、そのURL</li>
     *   <li>上記全てに当てはまらない場合、例外をthrowする。</li>
     * </ol>
     * 
     * @param path サービス定義のパス
     * @return サービス定義のURL
     * @exception IllegalArgumentException 指定されたpathが不正な場合
     */
    public static URL convertServicePathToURL(String path)
     throws IllegalArgumentException{
        if(path == null || path.length() == 0){
            return getDefaultServiceURL();
        }
        
        URL url = null;
        final File localFile = new File(path);
        if(localFile.exists()){
            if(!localFile.isFile()){
                throw new IllegalArgumentException(
                    "ServicePath must be file : " + localFile
                );
            }
            try{
                url = localFile.toURL();
            }catch(MalformedURLException e){
                // この例外は発生しないはず
            }
        }else{
            final ClassLoader classLoader
                 = Thread.currentThread().getContextClassLoader();
            final URL resource = classLoader.getResource(path);
            if(resource != null){
                url = resource;
            }
        }
        if(url == null){
            throw new IllegalArgumentException(
                "ServicePath not found : " + path
            );
        }
        return url;
    }
    
    /**
     * 指定された文字列内のプロパティ参照文字列をシステムプロパティの値に置換する。<p>
     *
     * @param str 文字列
     * @return プロパティ参照文字列をシステムプロパティの値に置換した文字列
     */
    public static String replaceSystemProperty(String str){
        String result = str;
        if(result == null){
            return null;
        }
        final int startIndex = result.indexOf(SYSTEM_PROPERTY_START);
        if(startIndex == -1){
            return result;
        }
        final int endIndex = result.indexOf(SYSTEM_PROPERTY_END, startIndex);
        if(endIndex == -1){
            return result;
        }
        final String propStr = result.substring(
            startIndex + SYSTEM_PROPERTY_START.length(),
            endIndex
        );
        String prop = null;
        if(propStr != null && propStr.length() != 0){
            prop = System.getProperty(propStr);
        }
        if(prop == null){
            return result.substring(0, endIndex + SYSTEM_PROPERTY_END.length())
             + replaceSystemProperty(
                result.substring(endIndex + SYSTEM_PROPERTY_END.length())
             );
        }else{
            result = result.substring(0, startIndex) + prop
                 + result.substring(endIndex + SYSTEM_PROPERTY_END.length());
        }
        if(result.indexOf(SYSTEM_PROPERTY_START) != -1){
            return replaceSystemProperty(result);
        }
        return result;
    }
    
    /**
     * 指定された文字列内のプロパティ参照文字列をマネージャプロパティの値に置換する。<p>
     *
     * @param str 文字列
     * @return プロパティ参照文字列をマネージャプロパティの値に置換した文字列
     */
    public static String replaceManagerProperty(
        ServiceManager manager,
        String str
    ){
        String result = str;
        if(result == null){
            return null;
        }
        final int startIndex = result.indexOf(SYSTEM_PROPERTY_START);
        if(startIndex == -1){
            return result;
        }
        final int endIndex = result.indexOf(SYSTEM_PROPERTY_END, startIndex);
        if(endIndex == -1){
            return result;
        }
        final String propStr = result.substring(
            startIndex + SYSTEM_PROPERTY_START.length(),
            endIndex
        );
        String prop = null;
        if(propStr != null && propStr.length() != 0){
            prop = manager.getProperty(propStr);
        }
        if(prop == null){
            return result.substring(0, endIndex + SYSTEM_PROPERTY_END.length())
             + replaceManagerProperty(
                manager,
                result.substring(endIndex + SYSTEM_PROPERTY_END.length())
             );
        }else{
            result = result.substring(0, startIndex) + prop
                 + result.substring(endIndex + SYSTEM_PROPERTY_END.length());
        }
        if(result.indexOf(SYSTEM_PROPERTY_START) != -1){
            return replaceManagerProperty(manager, result);
        }
        return result;
    }
    
    /**
     * 指定された文字列内のプロパティ参照文字列をマネージャプロパティの値に置換する。<p>
     *
     * @param str 文字列
     * @return プロパティ参照文字列をマネージャプロパティの値に置換した文字列
     */
    public static String replaceManagerProperty(
        ManagerMetaData manager,
        String str
    ){
        String result = str;
        if(result == null){
            return null;
        }
        final int startIndex = result.indexOf(SYSTEM_PROPERTY_START);
        if(startIndex == -1){
            return result;
        }
        final int endIndex = result.indexOf(SYSTEM_PROPERTY_END, startIndex);
        if(endIndex == -1){
            return result;
        }
        final String propStr = result.substring(
            startIndex + SYSTEM_PROPERTY_START.length(),
            endIndex
        );
        String prop = null;
        if(propStr != null && propStr.length() != 0){
            prop = manager.getProperty(propStr);
        }
        if(prop == null){
            return result.substring(0, endIndex + SYSTEM_PROPERTY_END.length())
             + replaceManagerProperty(
                manager,
                result.substring(endIndex + SYSTEM_PROPERTY_END.length())
             );
        }else{
            result = result.substring(0, startIndex) + prop
                 + result.substring(endIndex + SYSTEM_PROPERTY_END.length());
        }
        if(result.indexOf(SYSTEM_PROPERTY_START) != -1){
            return replaceManagerProperty(manager, result);
        }
        return result;
    }
    
    /**
     * 指定された文字列内のプロパティ参照文字列をサービスプロパティの値に置換する。<p>
     *
     * @param str 文字列
     * @return プロパティ参照文字列をサービスプロパティの値に置換した文字列
     */
    public static String replaceServiceProperty(
        ServiceMetaData service,
        String str
    ){
        String result = str;
        if(result == null){
            return null;
        }
        final int startIndex = result.indexOf(SYSTEM_PROPERTY_START);
        if(startIndex == -1){
            return result;
        }
        final int endIndex = result.indexOf(SYSTEM_PROPERTY_END, startIndex);
        if(endIndex == -1){
            return result;
        }
        final String propStr = result.substring(
            startIndex + SYSTEM_PROPERTY_START.length(),
            endIndex
        );
        String prop = null;
        if(propStr != null && propStr.length() != 0){
            prop = service.getProperty(propStr);
        }
        if(prop == null){
            return result.substring(0, endIndex + SYSTEM_PROPERTY_END.length())
             + replaceServiceProperty(
                service,
                result.substring(endIndex + SYSTEM_PROPERTY_END.length())
             );
        }else{
            result = result.substring(0, startIndex) + prop
                 + result.substring(endIndex + SYSTEM_PROPERTY_END.length());
        }
        if(result.indexOf(SYSTEM_PROPERTY_START) != -1){
            return replaceServiceProperty(service, result);
        }
        return result;
    }
    
    /**
     * 指定された文字列内のプロパティ参照文字列をサービスロード構成プロパティの値に置換する。<p>
     *
     * @param str 文字列
     * @return プロパティ参照文字列をサービスロード構成プロパティの値に置換した文字列
     */
    public static String replaceServiceLoderConfig(
        String str,
        ServiceLoaderConfig config
    ){
        if(config == null){
            return str;
        }
        String result = str;
        if(result == null){
            return null;
        }
        final int startIndex = result.indexOf(SYSTEM_PROPERTY_START);
        if(startIndex == -1){
            return result;
        }
        final int endIndex = result.indexOf(SYSTEM_PROPERTY_END, startIndex);
        if(endIndex == -1){
            return result;
        }
        final String propStr = result.substring(
            startIndex + SYSTEM_PROPERTY_START.length(),
            endIndex
        );
        String prop = null;
        if(propStr != null && propStr.length() != 0){
            prop = config.getProperty(propStr);
        }
        if(prop == null){
            return result.substring(0, endIndex + SYSTEM_PROPERTY_END.length())
             + replaceServiceLoderConfig(
                result.substring(endIndex + SYSTEM_PROPERTY_END.length()),
                config
             );
        }else{
            result = result.substring(0, startIndex) + prop
                 + result.substring(endIndex + SYSTEM_PROPERTY_END.length());
        }
        if(result.indexOf(SYSTEM_PROPERTY_START) != -1){
            return replaceServiceLoderConfig(result, config);
        }
        return result;
    }
    
    /**
     * 指定された文字列内のプロパティ参照文字列をサーバプロパティの値に置換する。<p>
     *
     * @param str 文字列
     * @return プロパティ参照文字列をサーバプロパティの値に置換した文字列
     */
    public static String replaceServerProperty(String str){
        String result = str;
        if(result == null){
            return null;
        }
        final int startIndex = result.indexOf(SYSTEM_PROPERTY_START);
        if(startIndex == -1){
            return result;
        }
        final int endIndex = result.indexOf(SYSTEM_PROPERTY_END, startIndex);
        if(endIndex == -1){
            return result;
        }
        final String propStr = result.substring(
            startIndex + SYSTEM_PROPERTY_START.length(),
            endIndex
        );
        String prop = null;
        if(propStr != null && propStr.length() != 0){
            prop = ServiceManagerFactory.getProperty(propStr);
        }
        if(prop == null){
            return result.substring(0, endIndex + SYSTEM_PROPERTY_END.length())
             + replaceServerProperty(
                result.substring(endIndex + SYSTEM_PROPERTY_END.length())
             );
        }else{
            result = result.substring(0, startIndex) + prop
                 + result.substring(endIndex + SYSTEM_PROPERTY_END.length());
        }
        if(result.indexOf(SYSTEM_PROPERTY_START) != -1){
            return replaceServerProperty(result);
        }
        return result;
    }
    
    /**
     * 環境変数プロパティを取得する。<p>
     * {@link System#getProperty(String)} &gt; {@link ServiceLoaderConfig#getProperty(String)} &gt; {@link ServiceManager#getProperty(String)} &gt; {@link ServiceManagerFactory#getProperty(String)}
     *
     * @param name プロパティ名
     * @param config ServiceLoaderConfig
     * @param manager ServiceManager
     * @param metaData メタデータ
     */
    public static String getProperty(
        String name,
        ServiceLoaderConfig config,
        ServiceManager manager,
        MetaData metaData
    ){
        String prop = System.getProperty(name);
        if(prop != null){
            return prop;
        }
        if(config != null){
            prop = config.getProperty(name);
            if(prop != null){
                return prop;
            }
        }
        if(manager != null){
            prop = manager.getProperty(name);
            if(prop != null){
                return prop;
            }
        }
        if(metaData != null){
            ServerMetaData serverData = null;
            ManagerMetaData mngData = null;
            ServiceMetaData serviceData = null;
            MetaData parent = metaData;
            do{
                if(parent == null){
                    break;
                }else if(serviceData == null
                     && parent instanceof ServiceMetaData){
                    serviceData = (ServiceMetaData)parent;
                }else if(mngData == null
                     && parent instanceof ManagerMetaData){
                    mngData = (ManagerMetaData)parent;
                }else if(serverData == null
                     && parent instanceof ServerMetaData){
                    serverData = (ServerMetaData)parent;
                    break;
                }
            }while((parent = parent.getParent()) != null);
            if(serviceData != null){
                prop = serviceData.getProperty(name);
                if(prop != null){
                    return prop;
                }
            }
            if(mngData != null){
                prop = mngData.getProperty(name);
                if(prop != null){
                    return prop;
                }
            }
            if(serverData != null){
                prop = serverData.getProperty(name);
                if(prop != null){
                    return prop;
                }
            }
        }
        return ServiceManagerFactory.getProperty(name);
    }
    
    /**
     * 環境変数プロパティを取得する。<p>
     * System.getProperties().containsKey(String) &gt; {@link ServiceLoaderConfig#existsProperty(String)} &gt; {@link ServiceManager#existsProperty(String)} &gt; {@link ServiceManagerFactory#existsProperty(String)}
     *
     * @param name プロパティ名
     * @param config ServiceLoaderConfig
     * @param manager ServiceManager
     * @param metaData メタデータ
     */
    public static boolean existsProperty(
        String name,
        ServiceLoaderConfig config,
        ServiceManager manager,
        MetaData metaData
    ){
        boolean exists = System.getProperties().containsKey(name);
        if(exists){
            return exists;
        }
        if(config != null){
            exists = config.existsProperty(name);
            if(exists){
                return exists;
            }
        }
        if(manager != null){
            exists = manager.existsProperty(name);
            if(exists){
                return exists;
            }
        }
        if(metaData != null){
            ServerMetaData serverData = null;
            ManagerMetaData mngData = null;
            ServiceMetaData serviceData = null;
            MetaData parent = metaData;
            do{
                if(parent == null){
                    break;
                }else if(serviceData == null
                     && parent instanceof ServiceMetaData){
                    serviceData = (ServiceMetaData)parent;
                }else if(mngData == null
                     && parent instanceof ManagerMetaData){
                    mngData = (ManagerMetaData)parent;
                }else if(serverData == null
                     && parent instanceof ServerMetaData){
                    serverData = (ServerMetaData)parent;
                    break;
                }
            }while((parent = parent.getParent()) != null);
            if(serviceData != null){
                exists = serviceData.existsProperty(name);
                if(exists){
                    return exists;
                }
            }
            if(mngData != null){
                exists = mngData.existsProperty(name);
                if(exists){
                    return exists;
                }
            }
            if(serverData != null){
                exists = serverData.existsProperty(name);
                if(exists){
                    return exists;
                }
            }
        }
        return ServiceManagerFactory.existsProperty(name);
    }
    
    public static Class convertStringToClass(String typeStr)
     throws ClassNotFoundException{
        return Utility.convertStringToClass(typeStr, false);
    }
    
    /**
     * 文字列からクラスに変換する。<p>
     *
     * @param typeStr 完全修飾クラス名
     * @param isWrapp プリミティブ型のラッパに変換するかのフラグ
     */
    public static Class convertStringToClass(
        String typeStr,
        boolean isWrapp
    ) throws ClassNotFoundException{
        Class type = null;
        if(typeStr != null){
            if(Byte.TYPE.getName().equals(typeStr)){
                type = isWrapp ? Byte.class : Byte.TYPE;
            }else if(Character.TYPE.getName().equals(typeStr)){
                type = isWrapp ? Character.class : Character.TYPE;
            }else if(Short.TYPE.getName().equals(typeStr)){
                type = isWrapp ? Short.class : Short.TYPE;
            }else if(Integer.TYPE.getName().equals(typeStr)){
                type = isWrapp ? Integer.class : Integer.TYPE;
            }else if(Long.TYPE.getName().equals(typeStr)){
                type = isWrapp ? Long.class : Long.TYPE;
            }else if(Float.TYPE.getName().equals(typeStr)){
                type = isWrapp ? Float.class : Float.TYPE;
            }else if(Double.TYPE.getName().equals(typeStr)){
                type = isWrapp ? Double.class : Double.TYPE;
            }else if(Boolean.TYPE.getName().equals(typeStr)){
                type = isWrapp ? Boolean.class : Boolean.TYPE;
            }else{
                if(typeStr.endsWith(ARRAY_CLASS_SUFFIX)
                    && typeStr.length() > 2){
                    final Class elementType = convertStringToClass(
                        typeStr.substring(0, typeStr.length() - 2),
                        false
                    );
                    type = Array.newInstance(elementType, 0).getClass();
                }else{
                    type = Class.forName(
                        typeStr,
                        true,
                        NimbusClassLoader.getInstance()
                    );
                }
            }
        }
        return type;
    }
}
