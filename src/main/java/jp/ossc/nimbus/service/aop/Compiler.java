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
package jp.ossc.nimbus.service.aop;

import java.io.*;
import java.util.*;
import java.util.zip.*;
import java.net.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.io.*;

/**
 * アスペクトコンパイラ。<p>
 * {@link NimbusClassLoader}に登録された{@link AspectTranslator}を使って、クラスファイルにアスペクトを織り込むコンパイラである。<br>
 * NimbusClassLoaderによって、クラスロード時にアスペクトを織り込む動的アスペクトに対して、このコンパイラでアプリケーションを実行する前に事前にアスペクトを織り込んだクラスファイルを生成しておくのが静的アスペクトである。<br>
 * 動的アスペクトは、事前にコンパイルする手間は必要ないが、アプリケーションサーバ等の複雑なクラスローダ構成を持つシステムにおいては、クラスのリンクエラーを招く危険がある。それに対して、静的アスペクトは、事前にコンパイルする手間が必要だが、事前にコンパイルするためクラスローダに依存する事はなく、安全にアスペクトを織り込む事ができる。<br>
 * コンパイルコマンドの詳細は、{@link #main(String[])}を参照。<br>
 *
 * @author M.Takata
 */
public class Compiler implements java.io.Serializable{
    
    private static final long serialVersionUID = -7456674395942064160L;
    
    private static final String USAGE_RESOURCE
         = "jp/ossc/nimbus/service/aop/CompilerUsage.txt";
    
    private static final String CLASS_EXTEND = ".class";
    
    private String destPath;
    private boolean isVerbose;
    
    /**
     * 空のインスタンスを生成する。<p>
     */
    public Compiler(){
    }
    
    /**
     * 指定されたディレクトリにコンパイル結果を出力するコンパイラを生成する。<p>
     *
     * @param dest 出力ディレクトリ
     * @param verbose コンパイルの詳細を表示するかどうかのフラグ。trueの場合、詳細を出力する。
     */
    public Compiler(String dest, boolean verbose){
        destPath = dest;
        isVerbose = verbose;
    }
    
    /**
     * 出力ディレクトリを設定する。<p>
     *
     * @param dest 出力ディレクトリ
     */
    public void setDestinationDirectory(String dest){
        destPath = dest;
    }
    
    /**
     * 出力ディレクトリを取得する。<p>
     *
     * @return 出力ディレクトリ
     */
    public String getDestinationDirectory(){
        return destPath;
    }
    
    /**
     * コンパイルの詳細をコンソールに出力するかどうかを設定する。<p>
     *
     * @param verbose コンパイルの詳細を表示するかどうかのフラグ。trueの場合、詳細を出力する。
     */
    public void setVerbose(boolean verbose){
        isVerbose = verbose;
    }
    
    /**
     * コンパイルの詳細をコンソールに出力するかどうかを判定する。<p>
     *
     * @return コンパイルの詳細を表示するかどうかのフラグ。trueの場合、詳細を出力する。
     */
    public boolean isVerbose(){
        return isVerbose;
    }
    
    /**
     * 指定したサービス定義ファイルパスリストのサービス定義をロードする。<p>
     *
     * @param servicePaths サービス定義ファイルパスリスト
     */
    public static void loadServices(List servicePaths){
        if(servicePaths != null){
            for(int i = 0, max = servicePaths.size(); i < max; i++){
                ServiceManagerFactory.loadManager((String)servicePaths.get(i));
            }
            ServiceManagerFactory.checkLoadManagerCompleted();
        }
    }
    
    /**
     * 指定したサービス定義ファイルパスリストのサービス定義をアンロードする。<p>
     *
     * @param servicePaths サービス定義ファイルパスリスト
     */
    public static void unloadServices(List servicePaths){
        if(servicePaths != null){
            for(int i = servicePaths.size(); --i >= 0;){
                ServiceManagerFactory
                    .unloadManager((String)servicePaths.get(i));
            }
        }
    }
    
    /**
     * 指定したクラス名リストのクラスをコンパイルする。<p>
     *
     * @param classNames クラス名リスト
     * @return 指定された全てのクラスのコンパイルが成功した場合はtrue
     * @exception IOException クラスファイルの読み込み及び書き込みに失敗した場合
     * @see #compile(String)
     */
    public boolean compile(List classNames) throws IOException{
        final Iterator names = classNames.iterator();
        boolean result = true;
        while(names.hasNext()){
            if(!compile((String)names.next())){
                result = false;
            }
        }
        return result;
    }
    
    /**
     * 指定したクラス名のクラスをコンパイルする。<p>
     * クラス名の指定は、末尾に"*"を付ける事で、指定されたクラス名から始まる複数のクラス名を指定する事ができる。<br>
     * また、指定したクラスは、クラスパスから検索される。
     *
     * @param className クラス名
     * @return 指定された全てのクラスのコンパイルが成功した場合はtrue
     * @exception IOException クラスファイルの読み込み及び書き込みに失敗した場合
     */
    public boolean compile(String className) throws IOException{
        final String[] clazz = getClassNames(className);
        if(clazz == null || clazz.length == 0){
            if(isVerbose){
                System.out.println("Class not found. : " + className);
            }
            return false;
        }
        boolean result = true;
        for(int i = 0; i < clazz.length; i++){
            if(!compileInner(clazz[i])){
                result = false;
            }
        }
        return result;
    }
    
    private String[] getClassNames(String name) throws IOException{
        if(name.endsWith("*")){
            final List classpaths = parsePaths(
                System.getProperty("java.class.path")
            );
            if(classpaths.size() == 0){
                classpaths.add(".");
            }
            final Set classNames = new HashSet();
            for(int i = 0, max = classpaths.size(); i < max; i++){
                final File file = new File((String)classpaths.get(i));
                if(!file.exists()){
                    continue;
                }
                if(file.isDirectory()){
                    getClassNamesFromDir(file, name, classNames);
                }else{
                    getClassNamesFromJar(file, name, classNames);
                }
            }
            return (String[])classNames.toArray(new String[classNames.size()]);
        }else{
            return new String[]{name};
        }
    }
    
    private Set getClassNamesFromDir(File dir, String name, Set classNames){
        if(name.endsWith("**")){
            String packageName = name.substring(0, name.length() - 2);
            RecurciveSearchFile searchDir = new RecurciveSearchFile(
                dir,
                packageName.replace('.', '/')
            );
            final File[] classFiles = searchDir.listAllTreeFiles(
                new FilenameFilter(){
                    public boolean accept(File dir, String name){
                        return name.endsWith(CLASS_EXTEND);
                    }
                }
            );
            if(classFiles != null){
                final int dirLength = dir.getAbsolutePath().length();
                for(int i = 0; i < classFiles.length; i++){
                    String tmpName = classFiles[i]
                        .getAbsolutePath().substring(dirLength);
                    tmpName = tmpName.replace('/', '.');
                    tmpName = tmpName.replace('\\', '.');
                    if(tmpName.charAt(0) == '.'){
                        tmpName = tmpName.substring(1);
                    }
                    tmpName = tmpName.substring(0, tmpName.length() - 6);
                    classNames.add(tmpName);
                }
            }
        }else{
            String className = name;
            String packageName = null;
            if(name.lastIndexOf('.') != -1){
                packageName = name.substring(
                    0,
                    name.lastIndexOf('.') + 1
                );
                className = name.substring(name.lastIndexOf('.') + 1);
            }else{
                packageName = "";
            }
            File searchDir = null;
            if(packageName.length() == 0){
                searchDir = dir;
            }else{
                searchDir = new File(dir, packageName.replace('.', '/'));
            }
            final String startName = className.length() == 1
                 ? "" : className.substring(0, className.length() - 1);
            final File[] classFiles = searchDir.listFiles(
                new FilenameFilter(){
                    public boolean accept(File dir, String name){
                        if(!name.endsWith(CLASS_EXTEND)){
                            return false;
                        }
                        return name.startsWith(startName);
                    }
                }
            );
            if(classFiles != null){
                for(int i = 0; i < classFiles.length; i++){
                    String tmpName = packageName + classFiles[i].getName();
                    tmpName = tmpName.substring(0, tmpName.length() - 6);
                    classNames.add(tmpName);
                }
            }
        }
        return classNames;
    }
    
    private Set getClassNamesFromJar(File jar, String name, Set classNames)
     throws IOException{
        if(!jar.exists()){
            return classNames;
        }
        if(name.endsWith("**")){
            String packageName = name.substring(0, name.length() - 2);
            final String searchDir = packageName.replace('.', '/');
            final ZipFile zipFile = new ZipFile(jar);
            final Enumeration entries = zipFile.entries();
            while(entries.hasMoreElements()){
                final ZipEntry entry = (ZipEntry)entries.nextElement();
                if(entry.isDirectory()){
                    continue;
                }
                final String entryName = entry.getName();
                if(!entryName.startsWith(searchDir)
                     || !entryName.endsWith(CLASS_EXTEND)){
                    continue;
                }
                String tmpName = entryName.replace('/', '.');
                tmpName = tmpName.substring(0, tmpName.length() - 6);
                classNames.add(tmpName);
            }
        }else{
            String packageName = null;
            String className = name;
            if(name.lastIndexOf('.') != -1){
                packageName = name.substring(
                    0,
                    name.lastIndexOf('.') + 1
                );
                className = name.substring(name.lastIndexOf('.') + 1);
            }else{
                packageName = "";
            }
            final String searchDir = packageName.replace('.', '/');
            final String startName = className.length() == 1
                 ? "" : className.substring(0, className.length() - 1);
            final ZipFile zipFile = new ZipFile(jar);
            final Enumeration entries = zipFile.entries();
            while(entries.hasMoreElements()){
                final ZipEntry entry = (ZipEntry)entries.nextElement();
                if(entry.isDirectory()){
                    continue;
                }
                final String entryName = entry.getName();
                if(!entryName.startsWith(searchDir)
                     || entryName.indexOf('/', searchDir.length()) != -1
                     || !entryName.endsWith(CLASS_EXTEND)){
                    continue;
                }
                final int index = entryName.indexOf(startName, searchDir.length());
                if(index != -1){
                    String tmpName = packageName + entryName.substring(index);
                    tmpName = tmpName.substring(0, tmpName.length() - 6);
                    classNames.add(tmpName);
                }
            }
        }
        
        return classNames;
    }
    
    private boolean compileInner(String className) throws IOException{
        if(isNonTranslatableClassName(className)){
            if(isVerbose){
                System.out.println("Non translatable class. : " + className);
            }
            return false;
        }
        final ClassLoader loader
             = Thread.currentThread().getContextClassLoader();
        final String classRsrcName = className.replace('.', '/') + CLASS_EXTEND;
        final URL classURL = loader.getResource(classRsrcName);
        if(classURL == null){
            if(isVerbose){
                System.out.println("Class not found. : " + className);
            }
            return false;
        }
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
        }finally{
            if(is != null){
                try{
                    is.close();
                }catch(IOException e){
                }
            }
        }
        
        boolean isTransform = false;
        byte[] transformedBytes = bytecode;
        AspectTranslator[] translators
             = NimbusClassLoader.getVMAspectTranslators();
        for(int i = 0; i < translators.length; i++){
            final byte[] tmpBytes = translators[i].transform(
                loader,
                className,
                null,
                transformedBytes
            );
            if(tmpBytes != null){
                isTransform = true;
                transformedBytes = tmpBytes;
            }
        }
        translators = NimbusClassLoader.getInstance().getAspectTranslators();
        for(int i = 0; i < translators.length; i++){
            final byte[] tmpBytes = translators[i].transform(
                loader,
                className,
                null,
                transformedBytes
            );
            if(tmpBytes != null){
                isTransform = true;
                transformedBytes = tmpBytes;
            }
        }
        if(!isTransform){
            return true;
        }else if(isVerbose){
            System.out.println("Compile " + className);
        }
        File destDir = null;
        if(destPath != null){
            String packageName = null;
            if(className.lastIndexOf('.') != -1){
                packageName = className.substring(
                    0,
                    className.lastIndexOf('.')
                );
            }
            if(packageName != null){
                destDir = new File(destPath, packageName.replace('.', '/'));
                if(!destDir.exists()){
                    destDir.mkdirs();
                }
            }
        }
        File classFile = null;
        if(className.lastIndexOf('.') == -1){
            classFile = new File(destDir, className + CLASS_EXTEND);
        }else{
            classFile = new File(
                destDir,
                className.substring(className.lastIndexOf('.') + 1)
                     + CLASS_EXTEND
            );
        }
        OutputStream os = null;
        try{
            os = new FileOutputStream(classFile);
            os.write(transformedBytes);
        }finally{
            if(os != null){
                try{
                    os.close();
                }catch(IOException e){
                }
            }
        }
        return true;
    }
    
    /**
     * 使用方法を標準出力に表示する。<p>
     */
    private static void usage(){
        try{
            System.out.println(
                getResourceString(USAGE_RESOURCE)
            );
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    /**
     * リソースを文字列として読み込む。<p>
     *
     * @param name リソース名
     * @exception IOException リソースが存在しない場合
     */
    private static String getResourceString(String name) throws IOException{
        
        // リソースの入力ストリームを取得
        InputStream is = Compiler.class.getClassLoader()
            .getResourceAsStream(name);
        
        // メッセージの読み込み
        StringBuilder buf = new StringBuilder();
        BufferedReader reader = null;
        final String separator = System.getProperty("line.separator");
        try{
            reader = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while((line = reader.readLine()) != null){
                buf.append(line).append(separator);
            }
        }finally{
            if(reader != null){
                try{
                    reader.close();
                }catch(IOException e){
                }
            }
        }
        return unicodeConvert(buf.toString());
    }
    
    /**
     * ユニコードエスケープ文字列を含んでいる可能性のある文字列をデフォルトエンコーディングの文字列に変換する。<p>
     *
     * @param str ユニコードエスケープ文字列を含んでいる可能性のある文字列
     * @return デフォルトエンコーディングの文字列
     */
    private static String unicodeConvert(String str){
        char c;
        int len = str.length();
        StringBuilder buf = new StringBuilder(len);
        
        for(int i = 0; i < len; ){
            c = str.charAt(i++);
            if(c == '\\'){
                c = str.charAt(i++);
                if(c == 'u'){
                    int value = 0;
                    for(int j = 0; j < 4; j++){
                        c = str.charAt(i++);
                        switch(c){
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            value = (value << 4) + (c - '0');
                            break;
                        case 'a':
                        case 'b':
                        case 'c':
                        case 'd':
                        case 'e':
                        case 'f':
                            value = (value << 4) + 10 + (c - 'a');
                            break;
                        case 'A':
                        case 'B':
                        case 'C':
                        case 'D':
                        case 'E':
                        case 'F':
                            value = (value << 4) + 10 + (c - 'A');
                            break;
                        default:
                            throw new IllegalArgumentException(
                                "Failed to convert unicode : " + c
                            );
                        }
                    }
                    buf.append((char)value);
                }else{
                    switch(c){
                    case 't':
                        c = '\t';
                        break;
                    case 'r':
                        c = '\r';
                        break;
                    case 'n':
                        c = '\n';
                        break;
                    case 'f':
                        c = '\f';
                        break;
                    default:
                    }
                    buf.append(c);
                }
            }else{
                buf.append(c);
            }
        }
        return buf.toString();
    }
    
    private static List parsePaths(String paths){
        final List result = new ArrayList();
        if(paths == null || paths.length() == 0){
            return result;
        }
        final String separator = System.getProperty("path.separator");
        if(paths.indexOf(separator) == -1){
            result.add(paths);
            return result;
        }
        String tmpPaths = paths;
        int index = -1;
        while((index = tmpPaths.indexOf(separator)) != -1){
            result.add(tmpPaths.substring(0, index));
            if(index != tmpPaths.length() - 1){
                tmpPaths = tmpPaths.substring(index + separator.length());
            }else{
                tmpPaths = null;
                break;
            }
        }
        if(tmpPaths != null && tmpPaths.length() != 0){
            result.add(tmpPaths);
        }
        return result;
    }
    
    /**
     * 変換対象とならないクラスを判定する。<p>
     * 以下のクラスは、如何なる場合も変換対象とならない。<br>
     * <ul>
     *   <li>"javassist."から始まるクラス</li>
     *   <li>"org.omg."から始まるクラス</li>
     *   <li>"org.w3c."から始まるクラス</li>
     *   <li>"org.xml.sax."から始まるクラス</li>
     *   <li>"sunw."から始まるクラス</li>
     *   <li>"sun."から始まるクラス</li>
     *   <li>"java."から始まるクラス</li>
     *   <li>"javax."から始まるクラス</li>
     *   <li>"com.sun."から始まるクラス</li>
     *   <li>"jp.ossc.nimbus.service.aop."から始まるクラス</li>
     * </ul>
     * 
     * @param classname クラス名
     * @return 変換対象とならないクラスの場合、true
     */
    protected boolean isNonTranslatableClassName(String classname){
      return classname.startsWith("javassist.")
              || classname.startsWith("org.omg.")
              || classname.startsWith("org.w3c.")
              || classname.startsWith("org.xml.sax.")
              || classname.startsWith("sunw.")
              || classname.startsWith("sun.")
              || classname.startsWith("java.")
              || classname.startsWith("javax.")
              || classname.startsWith("com.sun.")
              || classname.startsWith("jp.ossc.nimbus.service.aop.");
    }
    
    /**
     * コンパイルコマンドを実行する。<p>
     * <pre>
     * コマンド使用方法：
     *   java jp.ossc.nimbus.service.aop.Compiler [options] [class files]
     * 
     * [options]
     * 
     *  [-servicepath paths]
     *    コンパイルに必要なアスペクトを定義したサービス定義ファイルのパスを指定します。
     *    この指定は必須です。
     *    セミコロン(;)区切りで複数指定可能です。
     * 
     *  [-d directory]
     *    出力先のディレクトリを指定します。
     *    このオプションの指定がない場合は、実行時のカレントに出力します。
     * 
     *  [-v]
     *    実行の詳細を表示します。
     * 
     *  [-help]
     *    ヘルプを表示します。
     * 
     *  [class names]
     *    コンパイルするクラス名を指定します。
     *    ここで指定するクラスは、クラスパスに存在しなければなりません。
     *    スペース区切りで複数指定可能です。
     * 
     * 使用例 : 
     *    java -classpath classes;lib/javassist-3.0.jar;lib/nimbus.jar jp.ossc.nimbus.service.aop.Compiler -servicepath aspect-service.xml sample.Sample1 sample.Sample2 hoge.Fuga*
     * </pre>
     *
     * @param args コマンド引数
     * @exception Exception コンパイル中に問題が発生した場合
     */
    public static void main(String[] args) throws Exception{
        
        if(args.length != 0 && args[0].equals("-help")){
            // 使用方法を表示する
            usage();
            return;
        }
        
        boolean option = false;
        String key = null;
        String dest = null;
        List servicePaths = null;
        boolean verbose = false;
        final List classNames = new ArrayList();
        for(int i = 0; i < args.length; i++){
            if(option){
                if(key.equals("-d")){
                    dest = args[i];
                }else if(key.equals("-servicepath")){
                    servicePaths = parsePaths(args[i]);
                }
                option = false;
                key = null;
            }else{
                if(args[i].equals("-d")){
                    option = true;
                    key = args[i];
                }else if(args[i].equals("-servicepath")){
                    option = true;
                    key = args[i];
                }else if(args[i].equals("-v")){
                    verbose = true;
                }else{
                  classNames.add(args[i]);
                }
            }
        }
        
        final Compiler compiler = new Compiler(dest, verbose);
        loadServices(servicePaths);
        try{
            if(compiler.compile(classNames)){
                System.out.println("Compile is completed.");
            }else{
                System.out.println("Compile is not completed.");
                if(!verbose){
                    System.out.println("If you want to know details, specify option v.");
                }
            }
        }finally{
            unloadServices(servicePaths);
        }
    }
}