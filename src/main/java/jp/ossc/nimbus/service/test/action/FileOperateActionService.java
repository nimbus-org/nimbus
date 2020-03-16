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
package jp.ossc.nimbus.service.test.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.Utility;
import jp.ossc.nimbus.io.RecurciveSearchFile;
import jp.ossc.nimbus.service.test.TestContext;
import jp.ossc.nimbus.service.test.TestAction;
import jp.ossc.nimbus.service.test.TestActionEstimation;

/**
 * ファイル操作を行うテストアクション。<p>
 * 動作の詳細は、{@link #execute(TestContext, String, Reader)}を参照。<br>
 * 
 * @author M.Takata
 */
public class FileOperateActionService extends ServiceBase implements TestAction,TestActionEstimation, FileOperateActionServiceMBean{
    
    private static final long serialVersionUID = -7746000195947141887L;
    protected double expectedCost = Double.NaN;

    /**
     * リソースの内容を読み込んで、ファイル操作を行う。<p>
     * リソースのフォーマットは、以下。<br>
     * <pre>
     * operateType
     * filePaths
     * </pre>
     * operateTypeは、MOVE、COPY、DELETE、CLEAR、LSのいずれかを指定する。MOVEは、ファイル移動。COPYはファイルコピー。DELETEは、ファイル削除。CLEARは、ファイルの中身を空にする。LSは、指定されたパスのファイルリストを取得する。<br>
     * filePathsは、operateTypeによって、記述方法が異なる。また、filePathsは、複数行指定可能である。<br>
     * <ul>
     * <li>operateTypeが"MOVE"の場合<br>移動元ファイルパスと移動先ディレクトリパスの2行を指定する。移動先ディレクトリパスが指定されていない場合は、{@link TestContext#getCurrentDirectory()}に同じファイル名で移動する。</li>
     * <li>operateTypeが"COPY"の場合<br>コピー元ファイルパスとコピー先ファイルパスの2行を指定する。コピー先ファイルパスが指定されていない場合は、{@link TestContext#getCurrentDirectory()}に同じファイル名でコピーする。</li>
     * <li>operateTypeが"DELETE"の場合<br>削除ファイルパスを指定する。</li>
     * <li>operateTypeが"CLEAR"の場合<br>対象ファイルパスを指定する。</li>
     * <li>operateTypeが"LS"の場合<br>対象ファイルパスを指定する。</li>
     * </ul>
     *
     * @param context コンテキスト
     * @param actionId アクションID
     * @param resource リソース
     * @return LSの場合のみ見つかったファイルのリスト。それ以外は、null
     */
    public Object execute(TestContext context, String actionId, Reader resource) throws Exception{
        BufferedReader br = new BufferedReader(resource);
        Object result = null;
        try{
            String operateType = br.readLine();
            if("MOVE".equals(operateType)){
                String srcPath = br.readLine();
                if(srcPath == null || srcPath.length() == 0){
                    throw new Exception("Unexpected EOF on srcPath");
                }
                do{
                    List srcFiles = toFiles(srcPath, context);
                    if(srcFiles != null){
                        String dstPath = br.readLine();
                        File dstDir = null;
                        if(dstPath == null || dstPath.length() == 0){
                            dstDir = context.getCurrentDirectory();
                        }else{
                            dstDir = new File(replaceProperty(dstPath));
                            if(!dstDir.isAbsolute()){
                                dstDir = new File(context.getCurrentDirectory(), dstDir.getPath());
                            }
                        }
                        if(dstDir.exists()){
                            if(dstDir.isDirectory()){
                                move(srcFiles, dstDir);
                            }else{
                                throw new Exception("Not directory. path=" + dstDir.getPath());
                            }
                        }else{
                            if(dstDir.mkdirs()){
                                move(srcFiles, dstDir);
                            }else{
                                throw new Exception("Can' not create directory. path=" + dstDir.getPath());
                            }
                        }
                    }
                }while((srcPath = br.readLine()) != null && srcPath.length() != 0);
            }else if("COPY".equals(operateType)){
                String srcPath = br.readLine();
                if(srcPath == null || srcPath.length() == 0){
                    throw new Exception("Unexpected EOF on srcPath");
                }
                do{
                    List srcFiles = toFiles(srcPath, context);
                    if(srcFiles != null){
                        String dstPath = br.readLine();
                        File dstDir = null;
                        if(dstPath == null || dstPath.length() == 0){
                            dstDir = context.getCurrentDirectory();
                        }else{
                            dstDir = new File(replaceProperty(dstPath));
                            if(!dstDir.isAbsolute()){
                                dstDir = new File(context.getCurrentDirectory(), dstDir.getPath());
                            }
                        }
                        if(dstDir.exists()){
                            if(dstDir.isDirectory()){
                                copy(srcFiles, dstDir);
                            }else{
                                throw new Exception("Not directory. path=" + dstDir.getPath());
                            }
                        }else{
                            if(dstDir.mkdirs()){
                                copy(srcFiles, dstDir);
                            }else{
                                throw new Exception("Can' not create directory. path=" + dstDir.getPath());
                            }
                        }
                    }
                }while((srcPath = br.readLine()) != null && srcPath.length() != 0);
            }else if("DELETE".equals(operateType)){
                String filePath = br.readLine();
                if(filePath == null || filePath.length() == 0){
                    throw new Exception("Unexpected EOF on filePath");
                }
                do{
                    List files = toFiles(filePath, context);
                    if(files != null){
                        delete(files);
                    }
                }while((filePath = br.readLine()) != null && filePath.length() != 0);
            }else if("CLEAR".equals(operateType)){
                String filePath = br.readLine();
                if(filePath == null || filePath.length() == 0){
                    throw new Exception("Unexpected EOF on filePath");
                }
                do{
                    List files = toFiles(filePath, context);
                    if(files != null){
                        clear(files);
                    }
                }while((filePath = br.readLine()) != null && filePath.length() != 0);
            }else if("LS".equals(operateType)){
                String filePath = br.readLine();
                if(filePath == null || filePath.length() == 0){
                    throw new Exception("Unexpected EOF on filePath");
                }
                List fileList = null;
                do{
                    List files = toFiles(filePath, context);
                    if(files != null){
                        fileList = ls(files, fileList);
                    }
                }while((filePath = br.readLine()) != null && filePath.length() != 0);
                result = fileList;
            }else{
                throw new Exception("Illegal operateType : " + operateType);
            }
        }finally{
            br.close();
            br = null;
        }
        return result;
    }
    
    protected void move(List files, File dir) throws IOException{
        for(int i = 0; i < files.size(); i++){
            File file = (File)files.get(i);
            if(file.isDirectory()){
                File destDir = new File(dir, file.getName());
                if(!destDir.exists()){
                    destDir.mkdirs();
                }
                RecurciveSearchFile.copyAllTree(file, destDir);
                RecurciveSearchFile.deleteAllTree(file);
            }else{
                RecurciveSearchFile.dataCopy(file, new File(dir, file.getName()));
                file.delete();
            }
        }
    }
    
    protected void copy(List files, File dir) throws IOException{
        for(int i = 0; i < files.size(); i++){
            File file = (File)files.get(i);
            if(file.isDirectory()){
                File destDir = new File(dir, file.getName());
                if(!destDir.exists()){
                    destDir.mkdirs();
                }
                RecurciveSearchFile.copyAllTree(file, destDir);
            }else{
                RecurciveSearchFile.dataCopy(file, new File(dir, file.getName()));
            }
        }
    }
    
    protected void delete(List files) throws IOException{
        for(int i = 0; i < files.size(); i++){
            File file = (File)files.get(i);
            if(file.isDirectory()){
                if(!RecurciveSearchFile.deleteAllTree(file)){
                    throw new IOException("Can not delete files. path=" + file);
                }
            }else{
                if(!file.delete()){
                    throw new IOException("Can not delete files. path=" + file);
                }
            }
        }
    }
    
    protected void clear(List files) throws IOException{
        for(int i = 0; i < files.size(); i++){
            File file = (File)files.get(i);
            if(!file.isDirectory()){
                FileOutputStream fos = new FileOutputStream(file);
                fos.close();
            }
        }
    }
    
    protected List ls(List files, List fileList) throws IOException{
        for(int i = 0; i < files.size(); i++){
            File file = (File)files.get(i);
            if(file.exists()){
                if(fileList == null){
                    fileList = new ArrayList();
                }
                if(file.isDirectory()){
                    File[] childlen = file.listFiles();
                    for(int j = 0; j < childlen.length; j++){
                        fileList.add(childlen[j]);
                    }
                }else{
                    fileList.add(file);
                }
            }
        }
        return fileList;
    }
    
    protected List toFiles(String path, TestContext context) throws IOException{
        List result = null;
        path = replaceProperty(path);
        File file = new File(path);
        if(file.exists()){
            result = new ArrayList();
            result.add(file);
        }else{
            File parentFile = file;
            while((parentFile = parentFile.getParentFile()) != null && !parentFile.exists());
            
            if(parentFile == null || ".".equals(parentFile.getPath())){
                parentFile = new File(context.getCurrentDirectory().getAbsolutePath());
            }else{
                path = path.substring(parentFile.getPath().length() + 1);
            }
            try{
                RecurciveSearchFile rsf = new RecurciveSearchFile(parentFile);
                File[] files = rsf.listAllTreeFiles(path, RecurciveSearchFile.SEARCH_TYPE_ALL);
                if(files != null && files.length != 0){
                    result = new ArrayList();
                    for(int i = 0; i < files.length; i++){
                        result.add(files[i]);
                    }
                }
            }catch(PatternSyntaxException e){
            }
        }
        return result;
    }
    
    protected String replaceProperty(String textValue){
        
        // システムプロパティの置換
        textValue = Utility.replaceSystemProperty(textValue);
        
        // サービスローダ構成プロパティの置換
        if(getServiceLoader() != null){
            textValue = Utility.replaceServiceLoderConfig(
                textValue,
                getServiceLoader().getConfig()
            );
        }
        
        // マネージャプロパティの置換
        if(getServiceManager() != null){
            textValue = Utility.replaceManagerProperty(
                getServiceManager(),
                textValue
            );
        }
        
        // サーバプロパティの置換
        textValue = Utility.replaceServerProperty(textValue);
        
        return textValue;
    }
    
    public void setExpectedCost(double cost) {
        expectedCost = cost;
    }
    
    public double getExpectedCost() {
        return expectedCost;
    }
}
