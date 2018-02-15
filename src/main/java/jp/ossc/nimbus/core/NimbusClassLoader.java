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

import java.util.*;
import java.net.*;
import java.io.*;
import java.security.*;
import java.security.cert.Certificate;

/**
 * Nimbusクラスローダ。<p>
 * Nimbusのサービスクラスのロードを行うクラスローダである。<br>
 * このクラスローダのインスタンスは、{@link NimbusClassLoader#getInstance()}で取得することができる。そのインスタンスはスレッドコンテキストクラスローダ単位に生成される。生成されたインスタンスは、スレッドコンテキストクラスローダをキーに弱参照で保持されるため、スレッドコンテキストクラスローダが破棄されると自動的に破棄される。<br>
 * <p>
 * また、このクラスローダには、{@link AspectTranslator}を登録する事が可能である。AspectTranslatorが登録された状態で、クラスのロードを依頼されると、AspectTranslatorによるクラスファイルの変換が行われて、クラスがロードされる。<br>
 * 但し、クラスのロードが行われるのは、クラスファイルの変換対象になっているクラスだけで、ロードされたクラス内で参照される別のクラスは、クラスファイルの変換対象でなければ、このクラスローダでロードが行われない。<br>
 * これは、アプリケーションサーバなどの複雑なクラスローダ構成を持つコンテナに対する配慮である。スタンドアローンのアプリケーションでは、[@link #setLoadNotTransformClass(boolean) setLoadNotTransformClass(true)}を呼び出す事で、変換対象外のクラスのロードも、このクラスローダで行われるようになる。<br>
 * 
 * @author M.Takata
 * @see AspectTranslator
 */
public class NimbusClassLoader extends ClassLoader{
    
    /**
     * スレッドコンテキストクラスローダに関連付けて、このクラスローダのインスタンスを保持する弱参照マップ。<p>
     */
    protected static final Map classLoader = new WeakHashMap();
    
    /**
     * VMレベルで登録された{@link AspectTranslator}のリスト。<p>
     */
    protected static final Map vmTranslators = new HashMap();
    
    /**
     * ThreadContextレベルで登録された{@link AspectTranslator}のリスト。<p>
     */
    protected final Map translators = new HashMap();
    
    private static final String CLASS_EXTEND = ".class";
    private boolean isLoadNotTransformClass = false;
    
    /**
     * 指定されたクラスローダを親に持つインスタンスを生成する。<p>
     *
     * @param parent 親クラスローダ
     */
    protected NimbusClassLoader(ClassLoader parent){
        super(parent);
    }
    
    /**
     * クラスローダのインスタンスを取得する。<p>
     * このメソッドで取得されるクラスローダは、スレッドコンテキストクラスローダを親に持つ。<br>
     * 
     * @return スレッドコンテキストクラスローダに関連付けられたNimbusクラスローダのインスタンス
     */
    public static synchronized NimbusClassLoader getInstance(){
        final ClassLoader contextLoader
             = Thread.currentThread().getContextClassLoader();
        if(contextLoader instanceof NimbusClassLoader){
            return (NimbusClassLoader)contextLoader;
        }
        NimbusClassLoader loader
             = (NimbusClassLoader)classLoader.get(contextLoader);
        if(loader == null){
            loader = new NimbusClassLoader(contextLoader);
            classLoader.put(contextLoader, loader);
        }
        return loader;
    }
    
    /**
     * VMレベルでクラスロード時にクラスファイル変換を行うAspectTranslatorを登録する。<p>
     *
     * @param translator 登録するAspectTranslator
     */
    public static void addVMAspectTranslator(AspectTranslator translator){
        synchronized(vmTranslators){
            List list = null;
            if(vmTranslators.containsKey(translator.getAspectKey())){
                list = (List)vmTranslators.get(translator.getAspectKey());
            }else{
                list = new ArrayList();
                vmTranslators.put(translator.getAspectKey(), list);
            }
            if(!list.contains(translator)){
                list.add(translator);
            }
        }
    }
    
    /**
     * VMレベルでクラスロード時にクラスファイル変換を行うAspectTranslatorを登録解除する。<p>
     *
     * @param translator 登録解除するAspectTranslator
     */
    public static void removeVMAspectTranslator(AspectTranslator translator){
        synchronized(vmTranslators){
            if(vmTranslators.containsKey(translator.getAspectKey())){
                final List list = (List)vmTranslators
                    .get(translator.getAspectKey());
                list.remove(translator);
                if(list.size() == 0){
                    vmTranslators.remove(translator.getAspectKey());
                }
            }
        }
    }
    
    /**
     * VMレベルで登録されているAspectTranslatorを取得する。<p>
     *
     * @return AspectTranslatorの配列
     */
    public static AspectTranslator[] getVMAspectTranslators(){
        synchronized(vmTranslators){
            final AspectTranslator[] result
                 = new AspectTranslator[vmTranslators.size()];
            final List[] lists = (List[])vmTranslators.values()
                .toArray(new List[vmTranslators.size()]);
            for(int i = 0; i < lists.length; i++){
                result[i] = (AspectTranslator)lists[i].get(0);
            }
            return result;
        }
    }
    
    /**
     * ThreadContextレベルでクラスロード時にクラスファイル変換を行うAspectTranslatorを登録する。<p>
     *
     * @param translator 登録するAspectTranslator
     */
    public void addAspectTranslator(AspectTranslator translator){
        synchronized(translators){
            List list = null;
            if(translators.containsKey(translator.getAspectKey())){
                list = (List)translators.get(translator.getAspectKey());
            }else{
                list = new ArrayList();
                translators.put(translator.getAspectKey(), list);
            }
            if(!list.contains(translator)){
                list.add(translator);
            }
        }
    }
    
    /**
     * ThreadContextレベルでクラスロード時にクラスファイル変換を行うAspectTranslatorを登録解除する。<p>
     *
     * @param translator 登録解除するAspectTranslator
     */
    public void removeAspectTranslator(AspectTranslator translator){
        synchronized(translators){
            if(translators.containsKey(translator.getAspectKey())){
                final List list = (List)translators
                    .get(translator.getAspectKey());
                list.remove(translator);
                if(list.size() == 0){
                    translators.remove(translator.getAspectKey());
                }
            }
        }
    }
    
    /**
     * ThreadContextレベルで登録されているAspectTranslatorを取得する。<p>
     *
     * @return AspectTranslatorの配列
     */
    public AspectTranslator[] getAspectTranslators(){
        synchronized(translators){
            final AspectTranslator[] result
                 = new AspectTranslator[translators.size()];
            final List[] lists = (List[])translators.values()
                .toArray(new List[translators.size()]);
            for(int i = 0; i < lists.length; i++){
                result[i] = (AspectTranslator)lists[i].get(0);
            }
            return result;
        }
    }
    
    /**
     * {@link AspectTranslator}の変換対象でないクラスをロードするかどうかを設定する。<p>
     *
     * @param isLoad {@link AspectTranslator}の変換対象でないクラスをロードする場合true。デフォルトはfalse
     */
    public void setLoadNotTransformClass(boolean isLoad){
        isLoadNotTransformClass = isLoad;
    }
    
    /**
     * {@link AspectTranslator}の変換対象でないクラスをロードするかどうかを判定する。<p>
     *
     * @return trueの場合{@link AspectTranslator}の変換対象でないクラスをロードする
     */
    public boolean isLoadNotTransformClass(){
        return isLoadNotTransformClass;
    }
    
    /**
     * 指定されたクラスをこのクラスローダでロードする。<p>
     * {@link #isLoadNotTransformClass()}の値に関わらず、変換対象でないクラスもこのクラスローダで明示的にロードする。<br>
     * 
     * @param name クラス名
     * @return ロードしたクラスオブジェクト
     * @exception ClassNotFoundException 指定されたクラスが見つからない場合
     */
    public synchronized Class loadClassLocally(String name)
     throws ClassNotFoundException{
        return loadClass(name, false);
    }
    
    /**
     * 指定されたクラスをこのクラスローダでロードする。<p>
     * {@link #isLoadNotTransformClass()}の値に関わらず、変換対象でないクラスもこのクラスローダで明示的にロードする。<br>
     * 
     * @param name クラス名
     * @param resolve true の場合は、クラスを解釈処理する
     * @return ロードしたクラスオブジェクト
     * @exception ClassNotFoundException 指定されたクラスが見つからない場合
     */
    public synchronized Class loadClassLocally(String name, boolean resolve)
     throws ClassNotFoundException{
        return loadClass(name, resolve, true);
    }
    
    /**
     * 指定されたクラスをロードする。<p>
     * 登録された{@link AspectTranslator}の変換対象となっているクラスは、このクラスローダでロードする。そうでないクラスは、親クラスローダであるスレッドコンテキストローダに委譲する。但し、{@link #isLoadNotTransformClass()}の値がtrueの場合は、変換対象でないクラスもこのクラスローダでロードする。<br>
     * 
     * @param name クラス名
     * @param resolve true の場合は、クラスを解釈処理する
     * @return ロードしたクラスオブジェクト
     * @exception ClassNotFoundException 指定されたクラスが見つからない場合
     */
    protected synchronized Class loadClass(String name, boolean resolve)
     throws ClassNotFoundException{
        return loadClass(name, resolve, false);
    }
    
    /**
     * 指定されたクラスをロードする。<p>
     * 
     * @param name クラス名
     * @param resolve true の場合は、クラスを解釈処理する
     * @param isLocally trueの場合は、{@link #isLoadNotTransformClass()}の値に関わらず、変換対象でないクラスもこのクラスローダで明示的にロードする
     * @return ロードしたクラスオブジェクト
     * @exception ClassNotFoundException 指定されたクラスが見つからない場合
     */
    protected synchronized Class loadClass(
        String name,
        boolean resolve,
        boolean isLocally
    ) throws ClassNotFoundException{
        if(vmTranslators.size() == 0 && translators.size() == 0
             && !isLoadNotTransformClass){
            return super.loadClass(name, resolve);
        }
        if(isNonLoadableClassName(name)){
            return super.loadClass(name, resolve);
        }
        final boolean isNonTranslatableClass = isNonTranslatableClassName(name);
        if(isNonTranslatableClass && !isLoadNotTransformClass){
            return super.loadClass(name, resolve);
        }
        final Class loadedClass = findLoadedClass(name);
        if(loadedClass != null){
            return loadedClass;
        }
        
        final URL classUrl = getClassURL(name);
        if(classUrl == null){
            return super.loadClass(name, resolve);
        }
        final byte[] bytecode = loadByteCode(classUrl);
        if(bytecode == null){
            return super.loadClass(name, resolve);
        }
        final URL codeSourceUrl = getCodeSourceURL(name, classUrl);
        if(codeSourceUrl == null){
            return super.loadClass(name, resolve);
        }
        final ProtectionDomain domain = getProtectionDomain(codeSourceUrl);
        boolean isTransform = false;
        byte[] transformedBytes = bytecode;
        if(!isNonTranslatableClass){
            synchronized(vmTranslators){
                final Object[] keys = vmTranslators.keySet().toArray();
                for(int i = 0, max = keys.length; i < max; i++){
                    final AspectTranslator translator = (AspectTranslator)
                        ((List)vmTranslators.get(keys[i])).get(0);
                    final byte[] tmpBytes = translator.transform(
                        this,
                        name,
                        domain,
                        transformedBytes
                    );
                    if(tmpBytes != null){
                        isTransform = true;
                        transformedBytes = tmpBytes;
                    }
                }
            }
            synchronized(translators){
                final Object[] keys = translators.keySet().toArray();
                for(int i = 0, max = keys.length; i < max; i++){
                    final AspectTranslator translator = (AspectTranslator)
                        ((List)translators.get(keys[i])).get(0);
                    final byte[] tmpBytes = translator.transform(
                        this,
                        name,
                        domain,
                        transformedBytes
                    );
                    if(tmpBytes != null){
                        isTransform = true;
                        transformedBytes = tmpBytes;
                    }
                }
            }
        }
        if(isTransform || isLocally || isLoadNotTransformClass){
            definePackage(name);
            final Class clazz = defineClass(
                name,
                transformedBytes,
                0,
                transformedBytes.length,
                domain
            );
            if(resolve){
                resolveClass(clazz);
            }
            return clazz;
        }else{
            int innerClassIndex = name.lastIndexOf('$');
            if(innerClassIndex != -1
                && name.length() - 1 != innerClassIndex
                && findLoadedClass(name.substring(0, innerClassIndex)) != null
            ){
                return loadClass(name, resolve, true);
            }
            return super.loadClass(name, resolve);
        }
    }
    
    /**
     * ロード対象とならないクラスを判定する。<p>
     * 以下のクラスは、如何なる場合も変換対象とならない。<br>
     * <ul>
     *   <li>"org.omg."から始まるクラス</li>
     *   <li>"org.w3c."から始まるクラス</li>
     *   <li>"org.xml.sax."から始まるクラス</li>
     *   <li>"sunw."から始まるクラス</li>
     *   <li>"sun."から始まるクラス</li>
     *   <li>"java."から始まるクラス</li>
     *   <li>"javax."から始まるクラス</li>
     *   <li>"com.sun."から始まるクラス</li>
     *   <li>"javassist."から始まるクラス</li>
     * </ul>
     * 
     * @param classname クラス名
     * @return ロード対象とならないクラスの場合、true
     */
    public static boolean isNonLoadableClassName(String classname){
      return classname.startsWith("org.omg.")
              || classname.startsWith("org.w3c.")
              || classname.startsWith("org.xml.sax.")
              || classname.startsWith("sunw.")
              || classname.startsWith("sun.")
              || classname.startsWith("java.")
              || classname.startsWith("javax.")
              || classname.startsWith("com.sun.")
              || classname.equals("jp.ossc.nimbus.core.NimbusClassLoader")
              || classname.equals("jp.ossc.nimbus.core.AspectTranslator");
    }
    
    /**
     * 変換対象とならないクラスを判定する。<p>
     * 以下のクラスは、如何なる場合も変換対象とならない。<br>
     * <ul>
     *   <li>"jp.ossc.nimbus.service.aop."から始まるクラス</li>
     * </ul>
     * 
     * @param classname クラス名
     * @return 変換対象とならないクラスの場合、true
     */
    public static boolean isNonTranslatableClassName(String classname){
      return classname.startsWith("jp.ossc.nimbus.service.aop.");
    }
    
    /**
     * 指定されたクラス名のパッケージを定義する。<p>
     * パッケージが既に存在する場合には、何もしない。
     *
     * @param className クラス名
     */
    protected void definePackage(String className){
        int index = className.lastIndexOf('.');
        if(index == -1){
            return;
        }
        try{
            definePackage(
                className.substring(0, index),
                null,
                null,
                null,
                null,
                null,
                null,
                null
            );
        }catch(IllegalArgumentException alreadyDone){
        }
    }
    
    /**
     * 指定されたクラスのクラスファイルのURLを取得する。<p>
     *
     * @param classname クラス名
     * @return クラスファイルのURL
     */
    protected URL getClassURL(String classname){
        final String classRsrcName = classname.replace('.', '/') + CLASS_EXTEND;
        return getResource(classRsrcName);
    }
    
    /**
     * 指定されたURLのクラスファイルのバイトコードを取得する。<p>
     *
     * @param classURL クラスファイルのURL
     * @return クラスファイルのバイトコード。読み込めない場合はnullを返す。
     */
    protected byte[] loadByteCode(URL classURL){
        byte[] bytecode = null;
        InputStream is = null;
        try{
            is = classURL.openStream();
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] tmp = new byte[1024];
            int read = 0;
            while((read = is.read(tmp)) > 0){
                baos.write(tmp, 0, read);
            }
            bytecode = baos.toByteArray();
        }catch(IOException e){
            return null;
        }finally{
            if(is != null){
                try{
                    is.close();
                }catch(IOException e){
                }
            }
        }
        return bytecode;
    }
    
    /**
     * 指定されたクラスのクラスファイルの{@link CodeSource}のURLを取得する。<p>
     * 
     * @param classname クラス
     * @param classURL クラスファイルのURL
     * @return CodeSourceの位置を決めるURL
     */
    protected URL getCodeSourceURL(String classname, URL classURL){
        final String classRsrcName = classname.replace('.', '/') + CLASS_EXTEND;
        String urlAsString = classURL.toString();
        final int index = urlAsString.indexOf(classRsrcName);
        if(index == -1){
            return classURL;
        }
        urlAsString = urlAsString.substring(0, index);
        try{
            return new URL(urlAsString);
        }catch(MalformedURLException e){
            return null;
        }
    }
    
    /**
     * 指定されたURLの{@link CodeSource}に対応する{@link PermissionCollection}を持った{@link ProtectionDomain}を取得する。<p>
     *
     * @param codesourceUrl CodeSourceの位置を決めるURL
     * @return 指定されたURLのCodeSourceに対応するPermissionCollection
     */
    protected ProtectionDomain getProtectionDomain(URL codesourceUrl){
    	Certificate[] certificates = null;
        final CodeSource cs = new CodeSource(codesourceUrl, certificates);
        final PermissionCollection permissions
             = Policy.getPolicy().getPermissions(cs);
        return new ProtectionDomain(cs, permissions);
    }
}