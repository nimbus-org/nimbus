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

import jp.ossc.nimbus.beans.*;
import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.util.converter.*;
import jp.ossc.nimbus.service.interpreter.ScriptEngineInterpreterService;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMWriter;

/**
 * JCE(Java Cryptographic Extension)実装である<a href="http://www.bouncycastle.org/java.html">Bouncy Castle</a>を使用して、暗号化機能を提供するサービスである。<p>
 *
 * @author M.Takata
 */
public class  BouncyCastleCipherCryptService extends CipherCryptService
 implements BouncyCastleCipherCryptServiceMBean{
    
    public void createService() throws Exception{
        if(Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null){
            Security.addProvider(new BouncyCastleProvider());
        }
        keyGeneratorProviderName = BouncyCastleProvider.PROVIDER_NAME;
        cipherProviderName = BouncyCastleProvider.PROVIDER_NAME;
        messageDigestProviderName = BouncyCastleProvider.PROVIDER_NAME;
        macProviderName = BouncyCastleProvider.PROVIDER_NAME;
        signatureProviderName = BouncyCastleProvider.PROVIDER_NAME;
        super.createService();
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
     * 指定された鍵をPEM形式に変換する。<p>
     *
     * @param key 鍵
     * @return PEM形式の鍵
     */
    public static String keyToPEM(Key key){
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
    
    protected static void usage(){
        System.out.println("コマンド使用方法：");
        System.out.println(" java jp.ossc.nimbus.service.crypt.BouncyCastleCipherCryptService [options] [source code]");
        System.out.println();
        System.out.println("[options]");
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
                if("servicepath".equals(name)){
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
        if(servicePaths == null){
            ServiceManagerFactory.DEFAULT_LOGGER.setSystemDebugEnabled(false);
            ServiceManagerFactory.DEFAULT_LOGGER.setDebugEnabled(false);
            ServiceManagerFactory.DEFAULT_LOGGER.setSystemInfoEnabled(false);
            ServiceManagerFactory.registerManager("Nimbus");
            ServiceManagerFactory.registerService("Nimbus", serviceData);
            ServiceManager manager = ServiceManagerFactory.findManager("Nimbus");
            manager.create();
            manager.start();
        }else{
            for(int i = 0, imax = servicePaths.size(); i < imax; i++){
                if(!ServiceManagerFactory.loadManager((String)servicePaths.get(i))){
                    System.out.println("Service load error." + servicePaths.get(i));
                    Thread.sleep(1000);
                    System.exit(-1);
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