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
package jp.ossc.nimbus.service.beancontrol;

import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import jp.ossc.nimbus.core.Utility;
import jp.ossc.nimbus.core.Service;
import jp.ossc.nimbus.service.log.LogService;
import jp.ossc.nimbus.service.interpreter.Interpreter;

/**
 * �Ɩ��t���[�R���p�C���[�B<p>
 * �Ɩ��t���[��`�t�@�C�������O�ɃR���p�C�����āA���@���`�F�b�N����B<br>
 *
 * @author M.Takata
 * @see DefaultBeanFlowInvokerFactoryService
 */
public class Compiler{
    
    private static final String USAGE_RESOURCE
         = "jp/ossc/nimbus/service/beancontrol/CompilerUsage.txt";
    
    private boolean isVerbose;
    private Interpreter testInterpreter = null;
    
    /**
     * ��̃C���X�^���X�𐶐�����B<p>
     */
    public Compiler(){
    }
    
    /**
     * �R���p�C���𐶐�����B<p>
     *
     * @param verbose �R���p�C���̏ڍׂ�\�����邩�ǂ����̃t���O�Btrue�̏ꍇ�A�ڍׂ��o�͂���B
     */
    public Compiler(boolean verbose){
        isVerbose = verbose;
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
    
    public void setTestInterpreter(Interpreter interpreter){
        testInterpreter = interpreter;
    }
    
    /**
     * �w�肵���Ɩ��t���[��`�t�@�C�����X�g�̊e�t�@�C�����R���p�C������B<p>
     *
     * @param definitions �Ɩ��t���[��`�t�@�C�����X�g
     * @param clazz BeanFlowInvoker�����N���X
     * @exception Exception �Ɩ��t���[��`�t�@�C���̓ǂݍ��݂Ɏ��s�����ꍇ
     */
    public void compile(List definitions, Class clazz) throws Exception{
        List flowDirs = new ArrayList();
        List flowPaths = new ArrayList();
        for(int i = 0; i < definitions.size(); i++){
            String definition = (String)definitions.get(i);
            File file = new File(definition);
            if(!file.exists()){
                throw new FileNotFoundException(file.getPath());
            }
            if(file.isDirectory()){
                flowDirs.add(definition);
            }else{
                flowPaths.add(definition);
            }
        }
        DefaultBeanFlowInvokerFactoryService factory = new DefaultBeanFlowInvokerFactoryService();
        LogService logger = new LogService();
        logger.create();
        if(isVerbose){
            logger.setSystemDebugEnabled(true);
            logger.setSystemInfoEnabled(true);
            logger.setSystemWarnEnabled(true);
            logger.setSystemErrorEnabled(true);
            logger.setSystemFatalEnabled(true);
        }else{
            logger.setSystemDebugEnabled(false);
            logger.setSystemInfoEnabled(false);
            logger.setSystemWarnEnabled(false);
            logger.setSystemErrorEnabled(false);
            logger.setSystemFatalEnabled(false);
        }
        logger.start();
        factory.setLogger(logger);
        factory.create();
        factory.setDirPaths((String[])flowDirs.toArray(new String[flowDirs.size()]));
        factory.setPaths((String[])flowPaths.toArray(new String[flowPaths.size()]));
        if(testInterpreter != null){
            factory.setTestInterpreter(testInterpreter);
        }
        if(clazz != null){
            factory.setBeanFlowInvokerAccessClass(clazz);
        }
        factory.setValidate(true);
        factory.start();
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
    
    /**
     * �R���p�C���R�}���h�����s����B<p>
     * <pre>
     * �R�}���h�g�p���@�F
     *   java jp.ossc.nimbus.service.beancontrol.Compiler [options] [beanflow files]
     * 
     * [options]
     * 
     *  [-v]
     *    ���s�̏ڍׂ�\�����܂��B
     * 
     *  [-class]
     *    jp.ossc.nimbus.service.beancontrol.interfaces.BeanFlowInvoker�̎����N���X�B
     * 
     *  [-testInterpreterClass]
     *    jp.ossc.nimbus.service.interpreter.Interpreter�̎����N���X�B
     * 
     *  [-help]
     *    �w���v��\�����܂��B
     * 
     *  [beanflow files]
     *    �R���p�C������Ɩ��t���[��`�t�@�C���A�܂��̓f�B���N�g�����w�肵�܂��B
     *    �X�y�[�X��؂�ŕ����w��\�ł��B
     * 
     * �g�p�� : 
     *    java -classpath classes;lib/nimbus.jar jp.ossc.nimbus.service.beancontrol.Compiler beanflow-def.xml
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
        
        List paths = new ArrayList();
        boolean verbose = false;
        String className = null;
        String testInterpreterClassName = null;
        for(int i = 0; i < args.length; i++){
            if(args[i].equals("-v")){
                verbose = true;
            }else if(args[i].equals("-class")){
                className = (args.length > i + 1) ? args[i + 1] : null;
                i++;
            }else if(args[i].equals("-testInterpreterClass")){
                testInterpreterClassName = (args.length > i + 1) ? args[i + 1] : null;
                i++;
            }else{
                paths.add(args[i]);
            }
        }
        
        try{
            final Compiler compiler = new Compiler(verbose);
            if(testInterpreterClassName != null){
                Class clazz = Utility.convertStringToClass(testInterpreterClassName);
                Interpreter interpreter = (Interpreter)clazz.newInstance();
                if(interpreter instanceof Service){
                    ((Service)interpreter).create();
                    ((Service)interpreter).start();
                }
                compiler.setTestInterpreter(interpreter);
            }
            compiler.compile(paths, className == null ? null : Utility.convertStringToClass(className));
            System.out.println("Compile is completed.");
        }catch(Exception e){
            System.out.println("Compile is not completed.");
            if(verbose){
                e.printStackTrace();
            }else{
                System.out.println(e.toString());
                System.out.println("If you want to know details, specify option v.");
            }
        }
    }
}