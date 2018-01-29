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
 * �A�X�y�N�g�R���p�C���B<p>
 * {@link NimbusClassLoader}�ɓo�^���ꂽ{@link AspectTranslator}���g���āA�N���X�t�@�C���ɃA�X�y�N�g��D�荞�ރR���p�C���ł���B<br>
 * NimbusClassLoader�ɂ���āA�N���X���[�h���ɃA�X�y�N�g��D�荞�ޓ��I�A�X�y�N�g�ɑ΂��āA���̃R���p�C���ŃA�v���P�[�V���������s����O�Ɏ��O�ɃA�X�y�N�g��D�荞�񂾃N���X�t�@�C���𐶐����Ă����̂��ÓI�A�X�y�N�g�ł���B<br>
 * ���I�A�X�y�N�g�́A���O�ɃR���p�C�������Ԃ͕K�v�Ȃ����A�A�v���P�[�V�����T�[�o���̕��G�ȃN���X���[�_�\�������V�X�e���ɂ����ẮA�N���X�̃����N�G���[�������댯������B����ɑ΂��āA�ÓI�A�X�y�N�g�́A���O�ɃR���p�C�������Ԃ��K�v�����A���O�ɃR���p�C�����邽�߃N���X���[�_�Ɉˑ����鎖�͂Ȃ��A���S�ɃA�X�y�N�g��D�荞�ގ����ł���B<br>
 * �R���p�C���R�}���h�̏ڍׂ́A{@link #main(String[])}���Q�ƁB<br>
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
     * ��̃C���X�^���X�𐶐�����B<p>
     */
    public Compiler(){
    }
    
    /**
     * �w�肳�ꂽ�f�B���N�g���ɃR���p�C�����ʂ��o�͂���R���p�C���𐶐�����B<p>
     *
     * @param dest �o�̓f�B���N�g��
     * @param verbose �R���p�C���̏ڍׂ�\�����邩�ǂ����̃t���O�Btrue�̏ꍇ�A�ڍׂ��o�͂���B
     */
    public Compiler(String dest, boolean verbose){
        destPath = dest;
        isVerbose = verbose;
    }
    
    /**
     * �o�̓f�B���N�g����ݒ肷��B<p>
     *
     * @param dest �o�̓f�B���N�g��
     */
    public void setDestinationDirectory(String dest){
        destPath = dest;
    }
    
    /**
     * �o�̓f�B���N�g�����擾����B<p>
     *
     * @return �o�̓f�B���N�g��
     */
    public String getDestinationDirectory(){
        return destPath;
    }
    
    /**
     * �R���p�C���̏ڍׂ��R���\�[���ɏo�͂��邩�ǂ�����ݒ肷��B<p>
     *
     * @param verbose �R���p�C���̏ڍׂ�\�����邩�ǂ����̃t���O�Btrue�̏ꍇ�A�ڍׂ��o�͂���B
     */
    public void setVerbose(boolean verbose){
        isVerbose = verbose;
    }
    
    /**
     * �R���p�C���̏ڍׂ��R���\�[���ɏo�͂��邩�ǂ����𔻒肷��B<p>
     *
     * @return �R���p�C���̏ڍׂ�\�����邩�ǂ����̃t���O�Btrue�̏ꍇ�A�ڍׂ��o�͂���B
     */
    public boolean isVerbose(){
        return isVerbose;
    }
    
    /**
     * �w�肵���T�[�r�X��`�t�@�C���p�X���X�g�̃T�[�r�X��`�����[�h����B<p>
     *
     * @param servicePaths �T�[�r�X��`�t�@�C���p�X���X�g
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
     * �w�肵���T�[�r�X��`�t�@�C���p�X���X�g�̃T�[�r�X��`���A�����[�h����B<p>
     *
     * @param servicePaths �T�[�r�X��`�t�@�C���p�X���X�g
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
     * �w�肵���N���X�����X�g�̃N���X���R���p�C������B<p>
     *
     * @param classNames �N���X�����X�g
     * @return �w�肳�ꂽ�S�ẴN���X�̃R���p�C�������������ꍇ��true
     * @exception IOException �N���X�t�@�C���̓ǂݍ��݋y�я������݂Ɏ��s�����ꍇ
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
     * �w�肵���N���X���̃N���X���R���p�C������B<p>
     * �N���X���̎w��́A������"*"��t���鎖�ŁA�w�肳�ꂽ�N���X������n�܂镡���̃N���X�����w�肷�鎖���ł���B<br>
     * �܂��A�w�肵���N���X�́A�N���X�p�X���猟�������B
     *
     * @param className �N���X��
     * @return �w�肳�ꂽ�S�ẴN���X�̃R���p�C�������������ꍇ��true
     * @exception IOException �N���X�t�@�C���̓ǂݍ��݋y�я������݂Ɏ��s�����ꍇ
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
     * �g�p���@��W���o�͂ɕ\������B<p>
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
     * ���\�[�X�𕶎���Ƃ��ēǂݍ��ށB<p>
     *
     * @param name ���\�[�X��
     * @exception IOException ���\�[�X�����݂��Ȃ��ꍇ
     */
    private static String getResourceString(String name) throws IOException{
        
        // ���\�[�X�̓��̓X�g���[�����擾
        InputStream is = Compiler.class.getClassLoader()
            .getResourceAsStream(name);
        
        // ���b�Z�[�W�̓ǂݍ���
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
     * ���j�R�[�h�G�X�P�[�v��������܂�ł���\���̂��镶������f�t�H���g�G���R�[�f�B���O�̕�����ɕϊ�����B<p>
     *
     * @param str ���j�R�[�h�G�X�P�[�v��������܂�ł���\���̂��镶����
     * @return �f�t�H���g�G���R�[�f�B���O�̕�����
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
     * �ϊ��ΏۂƂȂ�Ȃ��N���X�𔻒肷��B<p>
     * �ȉ��̃N���X�́A�@���Ȃ�ꍇ���ϊ��ΏۂƂȂ�Ȃ��B<br>
     * <ul>
     *   <li>"javassist."����n�܂�N���X</li>
     *   <li>"org.omg."����n�܂�N���X</li>
     *   <li>"org.w3c."����n�܂�N���X</li>
     *   <li>"org.xml.sax."����n�܂�N���X</li>
     *   <li>"sunw."����n�܂�N���X</li>
     *   <li>"sun."����n�܂�N���X</li>
     *   <li>"java."����n�܂�N���X</li>
     *   <li>"javax."����n�܂�N���X</li>
     *   <li>"com.sun."����n�܂�N���X</li>
     *   <li>"jp.ossc.nimbus.service.aop."����n�܂�N���X</li>
     * </ul>
     * 
     * @param classname �N���X��
     * @return �ϊ��ΏۂƂȂ�Ȃ��N���X�̏ꍇ�Atrue
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
     * �R���p�C���R�}���h�����s����B<p>
     * <pre>
     * �R�}���h�g�p���@�F
     *   java jp.ossc.nimbus.service.aop.Compiler [options] [class files]
     * 
     * [options]
     * 
     *  [-servicepath paths]
     *    �R���p�C���ɕK�v�ȃA�X�y�N�g���`�����T�[�r�X��`�t�@�C���̃p�X���w�肵�܂��B
     *    ���̎w��͕K�{�ł��B
     *    �Z�~�R����(;)��؂�ŕ����w��\�ł��B
     * 
     *  [-d directory]
     *    �o�͐�̃f�B���N�g�����w�肵�܂��B
     *    ���̃I�v�V�����̎w�肪�Ȃ��ꍇ�́A���s���̃J�����g�ɏo�͂��܂��B
     * 
     *  [-v]
     *    ���s�̏ڍׂ�\�����܂��B
     * 
     *  [-help]
     *    �w���v��\�����܂��B
     * 
     *  [class names]
     *    �R���p�C������N���X�����w�肵�܂��B
     *    �����Ŏw�肷��N���X�́A�N���X�p�X�ɑ��݂��Ȃ���΂Ȃ�܂���B
     *    �X�y�[�X��؂�ŕ����w��\�ł��B
     * 
     * �g�p�� : 
     *    java -classpath classes;lib/javassist-3.0.jar;lib/nimbus.jar jp.ossc.nimbus.service.aop.Compiler -servicepath aspect-service.xml sample.Sample1 sample.Sample2 hoge.Fuga*
     * </pre>
     *
     * @param args �R�}���h����
     * @exception Exception �R���p�C�����ɖ�肪���������ꍇ
     */
    public static void main(String[] args) throws Exception{
        
        if(args.length != 0 && args[0].equals("-help")){
            // �g�p���@��\������
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