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
package jp.ossc.nimbus.util.net;

import java.util.*;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.Principal;
import java.security.PrivateKey;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLContext;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509KeyManager;
import java.security.cert.X509Certificate;

import jp.ossc.nimbus.beans.*;

/**
 * SSLサーバソケットファクトリ。<p>
 * java.security.KeyStoreの鍵と証明書を使ったSSL通信を行うSSLServerSocketを生成するファクトリ。<br>
 * このファクトリから生成されるサーバソケットは、{@link #setServerSocketProperty(String, Object)}で、予め設定されたプロパティが設定される。<br>
 *
 * @author M.Takata
 */
public class SSLServerSocketFactory extends javax.net.ssl.SSLServerSocketFactory{
    
    /**
     * 使用するセキュアソケットプロトコルのデフォルト値。<p>
     */
    public static final String DEFAULT_PROTOCOL = "TLS";
    
    /**
     * キーストア形式のデフォルト値。<p>
     */
    public static final String DEFAULT_KEYSTORE_TYPE = "JKS";
    
    /**
     * javax.net.ssl.KeyManagerFactoryに指定するアルゴリズムのデフォルト値。<p>
     */
    public static final String DEFAULT_ALGORITHM = "SunX509";
    
    protected javax.net.ssl.SSLServerSocketFactory serverSocketFactory;
    
    protected Map serverSocketProperties;
    protected Map socketProperties;
    
    protected String protocol = DEFAULT_PROTOCOL;
    
    protected String keyAlias;
    protected String keyStoreType = DEFAULT_KEYSTORE_TYPE;
    protected String keyStoreAlgorithm = DEFAULT_ALGORITHM;
    protected String keyStoreFile = System.getProperty("user.home") + "/.keystore";
    protected String keyStorePassword = "changeit";
    protected String keyPassword = "";
    
    protected String trustKeyStoreType = DEFAULT_KEYSTORE_TYPE;
    protected String trustKeyStoreAlgorithm = DEFAULT_ALGORITHM;
    protected String trustKeyStoreFile;
    protected String trustKeyStorePassword;
    
    protected boolean initialized = false;
    
    /**
     * 使用するセキュアソケットプロトコルを設定する。<p>
     * デフォルトは、{@link #DEFAULT_PROTOCOL}。<br>
     *
     * @param protocol セキュアソケットプロトコル
     */
    public void setProtocol(String protocol){
        this.protocol = protocol;
    }
    
    /**
     * 使用するセキュアソケットプロトコルを取得する。<p>
     *
     * @return セキュアソケットプロトコル
     */
    public String getProtocol(){
        return protocol;
    }
    
    /**
     * キーストア形式を設定する。<p>
     * デフォルトは、{@link #DEFAULT_KEYSTORE_TYPE}。<br>
     *
     * @param storeType キーストア形式
     */
    public void setKeyStoreType(String storeType){
        keyStoreType = storeType;
    }
    
    /**
     * キーストア形式を取得する。<p>
     *
     * @return キーストア形式
     */
    public String getKeyStoreType(){
        return keyStoreType;
    }
    
    /**
     * javax.net.ssl.KeyManagerFactoryに指定するアルゴリズムを設定する。<p>
     * デフォルトは、{@link #DEFAULT_ALGORITHM}。<br>
     *
     * @param algorithm アルゴリズム
     */
    public void setKeyStoreAlgorithm(String algorithm){
        keyStoreAlgorithm = algorithm;
    }
    
    /**
     * javax.net.ssl.KeyManagerFactoryに指定するアルゴリズムを取得する。<p>
     *
     * @return アルゴリズム
     */
    public String getKeyStoreAlgorithm(){
        return keyStoreAlgorithm;
    }
    
    /**
     * キーストアファイルのパスを設定する。<p>
     * デフォルトは、ユーザホームディレクトリの.keystore。<br>
     *
     * @param path キーストアファイルのパス
     */
    public void setKeyStoreFile(String path){
        keyStoreFile = path;
    }
    
    /**
     * キーストアファイルのパスを取得する。<p>
     *
     * @return キーストアファイルのパス
     */
    public String getKeyStoreFile(){
        return keyStoreFile;
    }
    
    /**
     * キーストアのパスワードを設定する。<p>
     * デフォルトは、changeit。<br>
     *
     * @param password キーストアのパスワード
     */
    public void setKeyStorePassword(String password){
        keyStorePassword = password;
    }
    
    /**
     * キーストアのパスワードを取得する。<p>
     *
     * @return キーストアのパスワード
     */
    public String getKeyStorePassword(){
        return keyStorePassword;
    }
    
    /**
     * サーバー側のセキュアソケットを認証するときの秘密鍵の別名を設定する。<p>
     * この別名を指定しない場合は、公開鍵のタイプおよびピアによって認識される証明書発行局のリストに基づいて、秘密鍵が選択される。<br>
     *
     * @param alias 秘密鍵の別名
     */
    public void setKeyAlias(String alias){
        this.keyAlias = alias;
    }
    
    /**
     * サーバー側のセキュアソケットを認証するときの秘密鍵の別名を取得する。<p>
     *
     * @return 秘密鍵の別名
     */
    public String getKeyAlias(){
        return keyAlias;
    }
    
    /**
     * 秘密鍵をキーストアから読み出す時に使用する、秘密鍵のパスワードを設定する。<p>
     *
     * @param password 秘密鍵のパスワード
     */
    public void setKeyPassword(String password){
        keyPassword = password;
    }
    
    /**
     * 秘密鍵をキーストアから読み出す時に使用する、秘密鍵のパスワードを取得する。<p>
     *
     * @return 秘密鍵のパスワード
     */
    public String getKeyPassword(){
        return keyPassword;
    }
    
    /**
     * 証明書発行局と関連する信頼データのソースとなるキーストアの形式を設定する。<p>
     * デフォルトは、{@link #DEFAULT_KEYSTORE_TYPE}。<br>
     *
     * @param storeType キーストア形式
     */
    public void setTrustKeyStoreType(String storeType){
        trustKeyStoreType = storeType;
    }
    
    /**
     * 証明書発行局と関連する信頼データのソースとなるキーストアの形式を取得する。<p>
     *
     * @return キーストア形式
     */
    public String getTrustKeyStoreType(){
        return trustKeyStoreType;
    }
    
    /**
     * javax.net.ssl.TrustManagerFactoryに指定するアルゴリズムを設定する。<p>
     * デフォルトは、{@link #DEFAULT_ALGORITHM}。<br>
     *
     * @param algorithm アルゴリズム
     */
    public void setTrustKeyStoreAlgorithm(String algorithm){
        trustKeyStoreAlgorithm = algorithm;
    }
    
    /**
     * javax.net.ssl.TrustManagerFactoryに指定するアルゴリズムを取得する。<p>
     *
     * @return アルゴリズム
     */
    public String getTrustKeyStoreAlgorithm(){
        return trustKeyStoreAlgorithm;
    }
    
    /**
     * 証明書発行局と関連する信頼データのソースとなるキーストアファイルのパスを設定する。<p>
     * デフォルトは、システムプロパティ"javax.net.ssl.trustStore"。<br>
     *
     * @param path キーストアファイルのパス
     */
    public void setTrustKeyStoreFile(String path){
        trustKeyStoreFile = path;
    }
    
    /**
     * 証明書発行局と関連する信頼データのソースとなるキーストアファイルのパスを取得する。<p>
     *
     * @return キーストアファイルのパス
     */
    public String getTrustKeyStoreFile(){
        return trustKeyStoreFile;
    }
    
    /**
     * 証明書発行局と関連する信頼データのソースとなるキーストアのパスワードを設定する。<p>
     * デフォルトは、システムプロパティ"javax.net.ssl.trustStorePassword"。<br>
     *
     * @param password キーストアのパスワード
     */
    public void setTrustKeyStorePassword(String password){
        trustKeyStorePassword = password;
    }
    
    /**
     * 証明書発行局と関連する信頼データのソースとなるキーストアのパスワードを取得する。<p>
     *
     * @return キーストアのパスワード
     */
    public String getTrustKeyStorePassword(){
        return trustKeyStorePassword;
    }
    
    /**
     * {@link SSLServerSocket}にプロパティを設定する。<p>
     *
     * @param props プロパティマップ
     */
    public void setServerSocketProperties(Map props){
        if(props == null || props.size() == 0){
            if(serverSocketProperties != null){
                serverSocketProperties = null;
            }
            return;
        }
        final Iterator names = props.keySet().iterator();
        while(names.hasNext()){
            String name = (String)names.next();
            setServerSocketProperty(name, props.get(name));
        }
    }
    
    /**
     * {@link SSLServerSocket}にプロパティを設定する。<p>
     *
     * @param name プロパティ名
     * @param value 値
     */
    public void setServerSocketProperty(String name, Object value){
        if(serverSocketProperties == null){
            serverSocketProperties = new LinkedHashMap();
        }
        final Property prop = PropertyFactory.createProperty(name);
        serverSocketProperties.put(prop, value);
    }
    
    /**
     * {@link SSLServerSocket}のプロパティを取得する。<p>
     *
     * @param name プロパティ名
     * @return 値
     */
    public Object getServerSocketProperty(String name){
        if(serverSocketProperties == null){
            return null;
        }
        final Iterator props = serverSocketProperties.keySet().iterator();
        while(props.hasNext()){
            final Property prop = (Property)props.next();
            if(prop.getPropertyName().equals(name)){
                return serverSocketProperties.get(prop);
            }
        }
        return null;
    }
    
    protected synchronized void init() throws IOException{
        if(initialized){
            return;
        }
        try{
            SSLContext context = SSLContext.getInstance(protocol); 
            context.init(
                getKeyManagers(),
                getTrustManagers(),
                new SecureRandom()
            );
            serverSocketFactory = context.getServerSocketFactory();
        }catch(RuntimeException e){
            throw e;
        }catch(Exception e){
            if(e instanceof IOException){
                throw (IOException)e;
            }
            e.printStackTrace();
            throw new IOException(e.toString());
        }
        initialized = true;
    }
    
    protected KeyManager[] getKeyManagers() throws Exception {
        
        KeyManager[] keyManager = null;
        
        KeyStore store = getKeyStore();
        
        if(keyAlias != null && !store.isKeyEntry(keyAlias)) {
            throw new IOException("KeyAlias is not entried. " + keyAlias);
        }
        
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(keyStoreAlgorithm);
        keyManagerFactory.init(store, keyPassword.toCharArray());
        
        keyManager = keyManagerFactory.getKeyManagers();
        if(keyAlias != null){
            if(DEFAULT_KEYSTORE_TYPE.equals(keyStoreType)) {
                keyAlias = keyAlias.toLowerCase();
            }
            for(int i = 0; i < keyManager.length; i++) {
                keyManager[i] = new X509KeyManagerWrapper((X509KeyManager)keyManager[i], keyAlias);
            }
        }
        
        return keyManager;
    }
    
    protected TrustManager[] getTrustManagers() throws Exception{
        TrustManager[] trustManager = null;
        
        KeyStore trustStore = getTrustStore();
        if(trustStore != null) {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(trustKeyStoreAlgorithm);
            trustManagerFactory.init(trustStore);
            trustManager = trustManagerFactory.getTrustManagers();
        }
        
        return trustManager;
    }
    
    protected KeyStore getKeyStore() throws IOException{
        return getStore(keyStoreType, keyStoreFile, keyStorePassword);
    }
    
    protected KeyStore getTrustStore() throws IOException{
        KeyStore trustStore = null;
        
        if(trustKeyStoreFile == null){
            trustKeyStoreFile = System.getProperty("javax.net.ssl.trustStore");
        }
        if(trustKeyStorePassword == null){
            trustKeyStorePassword = System.getProperty("javax.net.ssl.trustStorePassword");
        }
        if(trustKeyStorePassword == null){
            trustKeyStorePassword = keyStorePassword;
        }
        if(trustKeyStoreFile != null && trustKeyStorePassword != null){
            trustStore = getStore(
                trustKeyStoreType,
                trustKeyStoreFile,
                trustKeyStorePassword
            );
        }
        return trustStore;
    }
    
    private KeyStore getStore(
        String type,
        String path,
        String password
    ) throws IOException{
        
        KeyStore keyStore = null;
        InputStream is = null;
        try{
            keyStore = KeyStore.getInstance(type);
            File keyStoreFile = new File(path);
            is = new FileInputStream(keyStoreFile);
            
            keyStore.load(is, password.toCharArray());
            is.close();
            is = null;
        }catch(IOException e){
            throw e;
        }catch(Exception e){
            throw new IOException(
                "Exception trying to load keystore " + path
                    + " : " + e.toString()
            );
        }finally{
            if(is != null){
                try{
                    is.close();
                }catch(IOException e){}
            }
        }
        return keyStore;
    }
    
    public ServerSocket createServerSocket() throws IOException{
        if(!initialized){
            init();
        }
        return applyServerSocketProperties(
            new SSLServerSocketWrapper(
                (SSLServerSocket)serverSocketFactory.createServerSocket()
            )
        );
    }
    
    public ServerSocket createServerSocket(int port) throws IOException{
        if(!initialized){
            init();
        }
        return applyServerSocketProperties(
            new SSLServerSocketWrapper(
                (SSLServerSocket)serverSocketFactory.createServerSocket(port)
            )
        );
    }
    
    public ServerSocket createServerSocket(int port, int backlog) throws IOException{
        if(!initialized){
            init();
        }
        return applyServerSocketProperties(
            new SSLServerSocketWrapper(
                (SSLServerSocket)serverSocketFactory.createServerSocket(port, backlog)
            )
        );
    }
    
    public ServerSocket createServerSocket(int port, int backlog, InetAddress bindAddr) throws IOException{
        if(!initialized){
            init();
        }
        return applyServerSocketProperties(
            new SSLServerSocketWrapper(
                (SSLServerSocket)serverSocketFactory.createServerSocket(port, backlog, bindAddr)
            )
        );
    }
    
    public String[] getDefaultCipherSuites(){
        if(!initialized){
            try{
                init();
            }catch(IOException e){
                return new String[0];
            }
        }
        return serverSocketFactory.getDefaultCipherSuites();
    }
    
    public String[] getSupportedCipherSuites(){
        if(!initialized){
            try{
                init();
            }catch(IOException e){
                return new String[0];
            }
        }
        return serverSocketFactory.getSupportedCipherSuites();
    }
    
    protected ServerSocket applyServerSocketProperties(
        SSLServerSocketWrapper serverSocket
    ) throws IOException{
        try{
            if(socketProperties != null && socketProperties.size() != 0){
                final Iterator names = socketProperties.keySet().iterator();
                while(names.hasNext()){
                    final String name = (String)names.next();
                    serverSocket.setSocketProperty(
                        name,
                        socketProperties.get(name)
                    );
                }
            }
            if(serverSocketProperties != null && serverSocketProperties.size() != 0){
                final Iterator props = serverSocketProperties.keySet().iterator();
                while(props.hasNext()){
                    final Property prop = (Property)props.next();
                    prop.setProperty(serverSocket, serverSocketProperties.get(prop));
                }
            }
        }catch(InvocationTargetException e){
            Throwable target = e.getTargetException();
            if(target instanceof IOException){
                throw (IOException)target;
            }else if(target instanceof RuntimeException){
                throw (RuntimeException)target;
            }else if(target instanceof Error){
                throw (Error)target;
            }else{
                throw new UndeclaredThrowableException(target);
            }
        }catch(NoSuchPropertyException e){
            throw new UndeclaredThrowableException(e);
        }
        return serverSocket;
    }
    
    private static class X509KeyManagerWrapper implements X509KeyManager{
        
        private X509KeyManager keyManager;
        private String serverKeyAlias;
        
        public X509KeyManagerWrapper(X509KeyManager mgr, String serverKeyAlias){
            keyManager = mgr;
            this.serverKeyAlias = serverKeyAlias;
        }
        
        public String chooseClientAlias(
            String[] keyType,
            Principal[] issuers,
            Socket socket
        ){
            return keyManager.chooseClientAlias(keyType, issuers, socket);
        }
        
        public String chooseServerAlias(
            String keyType,
            Principal[] issuers,
            Socket socket
        ){
            return serverKeyAlias;
        }
        
        public X509Certificate[] getCertificateChain(String alias){
            return keyManager.getCertificateChain(alias);
        }
        
        public String[] getClientAliases(String keyType, Principal[] issuers){
            return keyManager.getClientAliases(keyType, issuers);
        }
        
        public String[] getServerAliases(String keyType, Principal[] issuers){
            return keyManager.getServerAliases(keyType, issuers);
        }
        
        public PrivateKey getPrivateKey(String alias) {
            return keyManager.getPrivateKey(alias);
        }
    }
}