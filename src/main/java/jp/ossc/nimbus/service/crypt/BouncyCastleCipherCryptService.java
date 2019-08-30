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
package jp.ossc.nimbus.service.crypt;

import java.io.*;
import java.util.*;
import java.security.*;
import java.security.spec.*;

import jp.ossc.nimbus.beans.*;
import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.util.converter.*;
import jp.ossc.nimbus.service.interpreter.ScriptEngineInterpreterService;

import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.X509Extension;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;

/**
 * JCE(Java Cryptographic Extension)実装である<a href="http://www.bouncycastle.org/java.html">Bouncy Castle</a>を使用して、暗号化機能を提供するサービスである。<p>
 *
 * @author M.Takata
 */
public class  BouncyCastleCipherCryptService extends CipherCryptService
 implements BouncyCastleCipherCryptServiceMBean{
    
    protected String publicKeyStringPEM;
    protected String publicKeyFilePEM;
    protected String privateKeyStringPEM;
    protected String privateKeyFilePEM;
    
    protected JcaPEMKeyConverter pemKeyConverter;
    
    public void setPublicKeyStringPEM(String str){
        publicKeyStringPEM = str;
    }
    public String getPublicKeyStringPEM(){
        return publicKeyStringPEM;
    }
    public void setPublicKeyFilePEM(String path){
        publicKeyFilePEM = path;
    }
    public String getPublicKeyFilePEM(){
        return publicKeyFilePEM;
    }
    
    public void setPrivateKeyStringPEM(String str){
        privateKeyStringPEM = str;
    }
    public String getPrivateKeyStringPEM(){
        return privateKeyStringPEM;
    }
    public void setPrivateKeyFilePEM(String path){
        privateKeyFilePEM = path;
    }
    public String getPrivateKeyFilePEM(){
        return privateKeyFilePEM;
    }
    
    public void createService() throws Exception{
        Provider provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
        if(provider == null){
            provider = new BouncyCastleProvider();
            Security.addProvider(provider);
        }
        pemKeyConverter = new JcaPEMKeyConverter();
        pemKeyConverter.setProvider(provider);
        
        keyGeneratorProviderName = BouncyCastleProvider.PROVIDER_NAME;
        cipherProviderName = BouncyCastleProvider.PROVIDER_NAME;
        messageDigestProviderName = BouncyCastleProvider.PROVIDER_NAME;
        macProviderName = BouncyCastleProvider.PROVIDER_NAME;
        signatureProviderName = BouncyCastleProvider.PROVIDER_NAME;
        
        super.createService();
    }
    
    public KeyPair createKeyPair() throws Exception{
        if(publicKeyStringPEM != null || publicKeyFilePEM != null
            || privateKeyStringPEM != null || privateKeyFilePEM != null){
            if(publicKeyStringPEM != null){
                publicKey = createPublicKeyFromPEM(publicKeyStringPEM);
            }else if(publicKeyFilePEM != null){
                File file = findFile(publicKeyFilePEM, false);
                publicKey = createPublicKeyFromPEM(new BufferedReader(new FileReader(file)));
            }
            if(privateKeyStringPEM != null){
                privateKey = createPrivateKeyFromPEM(privateKeyStringPEM);
            }else if(privateKeyFilePEM != null){
                File file = findFile(privateKeyFilePEM, false);
                privateKey = createPrivateKeyFromPEM(new BufferedReader(new FileReader(file)));
            }
            return new KeyPair(publicKey, privateKey);
        }else{
            return super.createKeyPair();
        }
    }
    
    /**
     * 指定されたPEM(Privacy-enhanced mail)形式文字列から格納されていた公開鍵を生成する。<p>
     *
     * @param pem PEM(Privacy-enhanced mail)形式文字列
     * @return 格納されていた公開鍵
     * @exception Exception 格納されていた公開鍵の生成に失敗した場合
     */
    public PublicKey createPublicKeyFromPEM(String pem) throws Exception{
        return createPublicKeyFromPEM(new StringReader(pem));
    }
    
    /**
     * 指定されたPEM(Privacy-enhanced mail)形式文字列のReaderから格納されていた公開鍵を生成する。<p>
     *
     * @param reader PEM(Privacy-enhanced mail)形式文字列を読み取るReader
     * @return 格納されていた公開鍵
     * @exception Exception 格納されていた公開鍵の生成に失敗した場合
     */
    public PublicKey createPublicKeyFromPEM(Reader reader) throws Exception{
        Object info = createObjectFromPEM(reader);
        if(info instanceof PEMKeyPair){
            return pemKeyConverter.getKeyPair((PEMKeyPair)info).getPublic();
        }else{
            return pemKeyConverter.getPublicKey((SubjectPublicKeyInfo)info);
        }
    }
    
    /**
     * 指定されたPEM(Privacy-enhanced mail)形式文字列から格納されていた非公開鍵を生成する。<p>
     *
     * @param pem PEM(Privacy-enhanced mail)形式文字列
     * @return 格納されていた非公開鍵
     * @exception Exception 格納されていた非公開鍵の生成に失敗した場合
     */
    public PrivateKey createPrivateKeyFromPEM(String pem) throws Exception{
        return createPrivateKeyFromPEM(new StringReader(pem));
    }
    
    /**
     * 指定されたPEM(Privacy-enhanced mail)形式文字列のReaderから格納されていた非公開鍵を生成する。<p>
     *
     * @param reader PEM(Privacy-enhanced mail)形式文字列を読み取るReader
     * @return 格納されていた非公開鍵
     * @exception Exception 格納されていた非公開鍵の生成に失敗した場合
     */
    public PrivateKey createPrivateKeyFromPEM(Reader reader) throws Exception{
        Object info = createObjectFromPEM(reader);
        if(info instanceof PEMKeyPair){
            return pemKeyConverter.getKeyPair((PEMKeyPair)info).getPrivate();
        }else{
            return pemKeyConverter.getPrivateKey((PrivateKeyInfo)info);
        }
    }
    
    /**
     * 指定されたPEM(Privacy-enhanced mail)形式文字列から格納されていたオブジェクトを生成する。<p>
     *
     * @param pem PEM(Privacy-enhanced mail)形式文字列
     * @return 格納されていたオブジェクト
     * @exception Exception キーの生成に失敗した場合
     */
    protected Object createObjectFromPEM(String pem) throws Exception{
        return createObjectFromPEM(new StringReader(pem));
    }
    
    /**
     * 指定されたPEM(Privacy-enhanced mail)形式文字列のReaderから格納されていたオブジェクトを生成する。<p>
     *
     * @param reader PEM(Privacy-enhanced mail)形式文字列を読み取るReader
     * @return 格納されていたオブジェクト
     * @exception Exception 格納されていたオブジェクトの生成に失敗した場合
     */
    protected Object createObjectFromPEM(Reader reader) throws Exception{
        PEMParser parser = new PEMParser(reader);
        try{
            return parser.readObject();
        }finally{
            parser.close();
        }
    }
    
    /**
     * 指定された鍵を指定されたファイルにPEM(Privacy-enhanced mail)形式で書き出す。<p>
     *
     * @param key 鍵
     * @param filePath ファイルパス
     * @exception IOException 書き出しに失敗した場合
     */
    public void writeKeyToPEM(Key key, String filePath) throws IOException{
        OutputStream os = new BufferedOutputStream(new FileOutputStream(findFile(filePath, true)));
        try{
            writeKeyToPEM(key, os);
            os.flush();
        }finally{
            os.close();
        }
    }
    
    /**
     * 指定された鍵を指定されたストリームにPEM(Privacy-enhanced mail)形式で書き出す。<p>
     *
     * @param key 鍵
     * @param os 出力ストリーム
     * @exception IOException 書き出しに失敗した場合
     */
    public void writeKeyToPEM(Key key, OutputStream os) throws IOException{
        byte[] bytes = keyToPEM(key).getBytes();
        os.write(bytes);
    }
    
    // BouncyCastleCipherCryptService のJavaDoc
    public String privateKeyToPEM(){
        return getPrivateKey() == null ? null : keyToPEM(getPrivateKey());
    }
    
    // BouncyCastleCipherCryptService のJavaDoc
    public String publicKeyToPEM(){
        return getPublicKey() == null ? null : keyToPEM(getPublicKey());
    }
    
    /**
     * 指定された鍵をPEM(Privacy-enhanced mail)形式に変換する。<p>
     *
     * @param key 鍵
     * @return PEM形式の鍵
     */
    public String keyToPEM(Key key){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PEMWriter pemWriter = null;
        try{
            pemWriter = new PEMWriter(new OutputStreamWriter(baos));
            pemWriter.writeObject(key);
        }catch(IOException e){
        }finally{
            try{
                pemWriter.close();
            }catch(IOException e){}
        }
        return new String(baos.toByteArray());
    }
    
    /**
     * 証明書署名要求を生成する。<p>
     *
     * @param commonName 名前
     * @param countryCode 国コード
     * @param state 州や都道府県名などの代表的な所在地
     * @param locality 市町村名などの細かい所在地
     * @param organization 組織名
     * @param organizationalUnit 組織内の部署名
     * @param subjectAltNames サブジェクト代替名称の配列
     * @return 証明書署名要求のPKCS#10形式の文字列
     * @exception Exception 証明書署名要求の生成に失敗した場合
     */
    public String createCertificateSigningRequest(
        String commonName,
        String countryCode,
        String state,
        String locality,
        String organization,
        String organizationalUnit,
        GeneralName[] subjectAltNames
    ) throws Exception{
        return createCertificateSigningRequest(
            getKeyPair(),
            signatureAlgorithm,
            signatureAlgorithmParameterSpec,
            commonName,
            countryCode,
            state,
            locality,
            organization,
            organizationalUnit,
            subjectAltNames
        );
    }
    
    /**
     * 証明書署名要求を生成する。<p>
     *
     * @param keyPair 鍵ペア
     * @param signatureAlgorithm 署名アルゴリズム
     * @param signatureAlgorithmParameterSpec 署名アルゴリズムパラメータ
     * @param commonName 名前
     * @param countryCode 国コード
     * @param state 州や都道府県名などの代表的な所在地
     * @param locality 市町村名などの細かい所在地
     * @param organization 組織名
     * @param organizationalUnit 組織内の部署名
     * @param subjectAltNames サブジェクト代替名称の配列
     * @return 証明書署名要求のPKCS#10形式の文字列
     * @exception Exception 証明書署名要求の生成に失敗した場合
     */
    public String createCertificateSigningRequest(
        KeyPair keyPair,
        String signatureAlgorithm,
        AlgorithmParameterSpec signatureAlgorithmParameterSpec,
        String commonName,
        String countryCode,
        String state,
        String locality,
        String organization,
        String organizationalUnit,
        GeneralName[] subjectAltNames
    ) throws Exception{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writeCertificateSigningRequest(
            keyPair,
            signatureAlgorithm,
            signatureAlgorithmParameterSpec,
            commonName,
            countryCode,
            state,
            locality,
            organization,
            organizationalUnit,
            subjectAltNames,
            baos
        );
        return new String(baos.toByteArray());
    }
    
    /**
     * 証明書署名要求をファイルに書き出す。<p>
     *
     * @param commonName 名前
     * @param countryCode 国コード
     * @param state 州や都道府県名などの代表的な所在地
     * @param locality 市町村名などの細かい所在地
     * @param organization 組織名
     * @param organizationalUnit 組織内の部署名
     * @param subjectAltNames サブジェクト代替名称の配列
     * @param filePath PKCS#10形式の証明書署名要求を書き込むファイルのパス
     * @exception Exception 証明書署名要求の生成に失敗した場合
     */
    public void writeCertificateSigningRequest(
        String commonName,
        String countryCode,
        String state,
        String locality,
        String organization,
        String organizationalUnit,
        GeneralName[] subjectAltNames,
        String filePath
    ) throws Exception{
        writeCertificateSigningRequest(
            getKeyPair(),
            signatureAlgorithm,
            signatureAlgorithmParameterSpec,
            commonName,
            countryCode,
            state,
            locality,
            organization,
            organizationalUnit,
            subjectAltNames,
            filePath
        );
    }
    
    /**
     * 証明書署名要求をファイルに書き出す。<p>
     *
     * @param keyPair 鍵ペア
     * @param signatureAlgorithm 署名アルゴリズム
     * @param signatureAlgorithmParameterSpec 署名アルゴリズムパラメータ
     * @param commonName 名前
     * @param countryCode 国コード
     * @param state 州や都道府県名などの代表的な所在地
     * @param locality 市町村名などの細かい所在地
     * @param organization 組織名
     * @param organizationalUnit 組織内の部署名
     * @param subjectAltNames サブジェクト代替名称の配列
     * @param filePath PKCS#10形式の証明書署名要求を書き込むファイルのパス
     * @exception Exception 証明書署名要求の生成に失敗した場合
     */
    public void writeCertificateSigningRequest(
        KeyPair keyPair,
        String signatureAlgorithm,
        AlgorithmParameterSpec signatureAlgorithmParameterSpec,
        String commonName,
        String countryCode,
        String state,
        String locality,
        String organization,
        String organizationalUnit,
        GeneralName[] subjectAltNames,
        String filePath
    ) throws Exception{
        OutputStream os = new BufferedOutputStream(new FileOutputStream(findFile(filePath, true)));
        try{
            writeCertificateSigningRequest(
                keyPair,
                signatureAlgorithm,
                signatureAlgorithmParameterSpec,
                commonName,
                countryCode,
                state,
                locality,
                organization,
                organizationalUnit,
                subjectAltNames,
                os
            );
            os.flush();
        }finally{
            os.close();
        }
    }
    
    /**
     * 証明書署名要求をストリームに書き出す。<p>
     *
     * @param keyPair 鍵ペア
     * @param signatureAlgorithm 署名アルゴリズム
     * @param signatureAlgorithmParameterSpec 署名アルゴリズムパラメータ
     * @param commonName 名前
     * @param countryCode 国コード
     * @param state 州や都道府県名などの代表的な所在地
     * @param locality 市町村名などの細かい所在地
     * @param organization 組織名
     * @param organizationalUnit 組織内の部署名
     * @param subjectAltNames サブジェクト代替名称の配列
     * @param os PKCS#10形式の証明書署名要求を書き込む出力ストリーム
     * @exception Exception 証明書署名要求の生成に失敗した場合
     */
    public void writeCertificateSigningRequest(
        KeyPair keyPair,
        String signatureAlgorithm,
        AlgorithmParameterSpec signatureAlgorithmParameterSpec,
        String commonName,
        String countryCode,
        String state,
        String locality,
        String organization,
        String organizationalUnit,
        GeneralName[] subjectAltNames,
        OutputStream os
    ) throws Exception{
        X500NameBuilder sbjBuilder = new X500NameBuilder(BCStyle.INSTANCE);
        if(commonName != null){
            sbjBuilder.addRDN(BCStyle.CN, commonName);
        }
        if(countryCode != null){
            sbjBuilder.addRDN(BCStyle.C, countryCode);
        }
        if(state != null){
            sbjBuilder.addRDN(BCStyle.ST, state);
        }
        if(locality != null){
            sbjBuilder.addRDN(BCStyle.L, locality);
        }
        if(organization != null){
            sbjBuilder.addRDN(BCStyle.O, organization);
        }
        if(organizationalUnit != null){
            sbjBuilder.addRDN(BCStyle.OU, organizationalUnit);
        }
        X500Name subject = sbjBuilder.build();
        JcaPKCS10CertificationRequestBuilder csrBuilder = new JcaPKCS10CertificationRequestBuilder(subject, keyPair.getPublic());
        Vector oids = new Vector();
        Vector attributeValues = new Vector();
        if(subjectAltNames != null && subjectAltNames.length > 0){
            oids.add(X509Extensions.SubjectAlternativeName);
            attributeValues.add(new X509Extension(subject == null, new DEROctetString(new GeneralNames(subjectAltNames))));
        }
        if(oids.size() > 0){
            csrBuilder.addAttribute(PKCSObjectIdentifiers.pkcs_9_at_extensionRequest, new X509Extensions(oids, attributeValues));
        }
        JcaContentSignerBuilder jcsBuilder = signatureAlgorithmParameterSpec == null ? new JcaContentSignerBuilder(signatureAlgorithm) : new JcaContentSignerBuilder(signatureAlgorithm, signatureAlgorithmParameterSpec);
        ContentSigner signer = jcsBuilder.build(keyPair.getPrivate());
        PKCS10CertificationRequest csrRequest = csrBuilder.build(signer);
        OutputStreamWriter osw = new OutputStreamWriter(os);
        JcaPEMWriter pemWriter = new JcaPEMWriter(osw);
        try{
            pemWriter.writeObject(csrRequest);
            pemWriter.flush();
        }finally{
            osw.flush();
        }
    }
    
    protected static void usage(){
        System.out.println("コマンド使用方法：");
        System.out.println(" java jp.ossc.nimbus.service.crypt.BouncyCastleCipherCryptService [options] [source code]");
        System.out.println();
        System.out.println("[options]");
        System.out.println();
        System.out.println(" [-servicedir=path filter]");
        System.out.println("  このサービスを定義したサービス定義ファイルのディレクトリとサービス定義ファイルを特定するフィルタを指定します。");
        System.out.println();
        System.out.println(" [-servicepath=paths]");
        System.out.println("  このサービスを定義したサービス定義ファイルのパスを指定します。");
        System.out.println("  パスセパレータ区切りで複数指定可能です。");
        System.out.println();
        System.out.println(" [-servicename=name]");
        System.out.println("  このサービスのサービス名を指定します。");
        System.out.println("  指定しない場合はNimbus#Cryptとみなします。");
        System.out.println();
        System.out.println(" [-attributename=value]");
        System.out.println("  このサービスの属性とその値を設定します。");
        System.out.println("  但し、servicepathを指定した場合は、無効です。");
        SimpleProperty[] props = SimpleProperty.getProperties(BouncyCastleCipherCryptService.class);
        for(int i = 0; i < props.length; i++){
            if(props[i].isWritable(BouncyCastleCipherCryptService.class)){
                System.out.println("    " + props[i].getPropertyName());
            }
        }
        System.out.println();
        System.out.println(" [-help]");
        System.out.println("  ヘルプを表示します。");
        System.out.println();
        System.out.println("[source code]");
        System.out.println(" 実行するソースコードを指定します。");
        System.out.println(" スクリプト内変数として\"crypt\"で、このクラスのインスタンスが参照可能です。");
        System.out.println();
        System.out.println(" 使用例 : ");
        System.out.println("    java -classpath nimbus.jar jp.ossc.nimbus.service.crypt.BouncyCastleCipherCryptService -storePath=.keystore -storePassword=changeit -keyAlias=key1 -keyPassword=test crypt.doEncode('test')");
    }
    
    /**
     * このクラスを初期化して、指定されたスクリプトを実行する。<p>
     *
     * @param args このクラスの初期化パラメータと、実行スクリプトを指定する。<br>初期化パラメータは、-属性名=値で指定する。スクリプトは、スクリプト内変数として"crypt"で、このクラスのインスタンスが参照可能である。
     * @exception Exception 初期化またはスクリプトの実行に失敗した場合
     */
    public static void main(String[] args) throws Exception{
        
        if(args.length == 0 || (args.length != 0 && args[0].equals("-help"))){
            usage();
            System.exit(-1);
            return;
        }
        String script = null;
        List serviceDirs = null;
        List servicePaths = null;
        String serviceNameStr = "Nimbus#Crypt";
        ServiceMetaData serviceData = new ServiceMetaData();
        serviceData.setName("Crypt");
        serviceData.setCode(BouncyCastleCipherCryptService.class.getName());
        for(int i = 0; i < args.length; i++){
            if(args[i].charAt(0) == '-'){
                if(args[i].indexOf("=") == -1){
                    usage();
                    throw new IllegalArgumentException("Illegal attribute parameter : " + args[i]);
                }
                String name = args[i].substring(1, args[i].indexOf("="));
                String value = args[i].substring(args[i].indexOf("=") + 1);
                if("servicedir".equals(name)){
                    if(serviceDirs == null){
                        serviceDirs = new ArrayList();
                    }
                    serviceDirs.add(new String[]{value, args[++i]});
                }else if("servicepath".equals(name)){
                    servicePaths = parsePaths(value);
                }else if("servicename".equals(name)){
                    serviceNameStr = value;
                }else{
                    AttributeMetaData attrData = new AttributeMetaData(serviceData);
                    attrData.setName(name);
                    attrData.setValue(value);
                    serviceData.addAttribute(attrData);
                }
            }else{
                script = args[i];
                break;
            }
        }
        if(script == null){
            usage();
            System.exit(-1);
            return;
        }
        if(servicePaths == null && serviceDirs == null){
            ServiceManagerFactory.DEFAULT_LOGGER.setSystemDebugEnabled(false);
            ServiceManagerFactory.DEFAULT_LOGGER.setDebugEnabled(false);
            ServiceManagerFactory.DEFAULT_LOGGER.setSystemInfoEnabled(false);
            ServiceManagerFactory.registerManager("Nimbus");
            ServiceManagerFactory.registerService("Nimbus", serviceData);
            ServiceManager manager = ServiceManagerFactory.findManager("Nimbus");
            manager.create();
            manager.start();
        }else{
            if(serviceDirs != null){
                for(int i = 0, imax = serviceDirs.size(); i < imax; i++){
                    String[] params = (String[])serviceDirs.get(i);
                    if(!ServiceManagerFactory.loadManagers(params[0], params[1])){
                        System.out.println("Service load error. path=" + params[0] + ", filter=" + params[1]);
                        Thread.sleep(1000);
                        System.exit(-1);
                    }
                }
            }
            if(servicePaths != null){
                for(int i = 0, imax = servicePaths.size(); i < imax; i++){
                    if(!ServiceManagerFactory.loadManager((String)servicePaths.get(i))){
                        System.out.println("Service load error." + servicePaths.get(i));
                        Thread.sleep(1000);
                        System.exit(-1);
                    }
                }
            }
        }
        
        if(!ServiceManagerFactory.checkLoadManagerCompleted()){
            Thread.sleep(1000);
            System.exit(-1);
            return;
        }
        
        ServiceNameEditor editor = new ServiceNameEditor();
        editor.setAsText(serviceNameStr);
        ServiceName serviceName = (ServiceName)editor.getValue();
        
        BouncyCastleCipherCryptService crypt = (BouncyCastleCipherCryptService)ServiceManagerFactory.getServiceObject(serviceName);
        
        ScriptEngineInterpreterService interpreter = new ScriptEngineInterpreterService();
        interpreter.create();
        interpreter.start();
        Map variables = new HashMap();
        variables.put("crypt", crypt);
        System.out.println(interpreter.evaluate(script, variables));
    }
}