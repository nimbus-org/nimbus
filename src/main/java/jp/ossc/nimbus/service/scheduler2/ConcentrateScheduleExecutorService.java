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
package jp.ossc.nimbus.service.scheduler2;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.io.CSVReader;
import jp.ossc.nimbus.service.beancontrol.interfaces.*;

/**
 * 集配信スケジュール実行。<p>
 * {@link Schedule#getInput() スケジュールの入力}に指定された集配信リクエスト文字列を解析して、{@link ConcentrateRequest 集配信リクエスト}に格納し、スケジュールの入力に設定する。
 * ここで、 集配信リクエスト文字列のフォーマットは、"GET|PUT,source,destination"である。<br>
 * また、スケジュールの入力をJSON形式にする場合のフォーマットは、以下。<br>
 * <pre>
 * {
 *     "processType":"GET|PUT",
 *     "source":"sourceの値",
 *     "destination":"destinationの値"
 * }
 * </pre>
 * また、空の{@link ConcentrateResponse 集配信レスポンス}を生成し、{@link Schedule#getOutput() スケジュールの出力}に設定する。
 * スケジュールのタスク名に指定された文字列を解析して、業務フロー名と{@link ConcentrateRequest#getKey() 集配信リクエストのキー}を取得し、業務フローを実行する。<br>
 * ここで、 タスク名文字列のフォーマットは、"業務フロー名[:集配信リクエストのキー]"である。<br>
 * 業務フローでは、スケジュールの入力として渡された集配信リクエストの情報を使って、集配信処理を行う。<br>
 * 収集（GET）の業務フローでは、{@link ConcentrateRequest#getSource() 収集元}からファイルを取得し、ローカルに保存する。
 * 集配信レスポンスの{@link ConcentrateResponse#addFile(File)}、{@link ConcentrateResponse#addFile(File, boolean)}を使って、保存したローカルファイルを集配信レスポンスに格納する。
 * ローカルに保存されたファイルは、{@link ConcentrateBackupManager}によってバックアップされたのち、このサービスによって{@link ConcentrateRequest#getDestination() 宛先}へと移動される。<br>
 * 配信（PUT）の業務フローでは、ローカルの{@link ConcentrateRequest#getSource() 配信元}からファイルを取得し、{@link ConcentrateRequest#getDestination() 宛先}へと配信する。
 * 集配信レスポンスの{@link ConcentrateResponse#addFile(File)}、{@link ConcentrateResponse#addFile(File, boolean)}を使って、配信元ローカルファイルを集配信レスポンスに格納する。
 * 配信元ローカルファイルは、{@link ConcentrateBackupManager}によってバックアップされたのち、このサービスによって削除される。<br>
 * 最後に、{@link ConcentrateBackupManager#backup(String, Date, String, File, boolean, Object)}の戻り値を、{@link Schedule#getOutput() スケジュールの出力}に設定する。
 * ConcentrateBackupManagerが設定されていない場合は、{@link ConcentrateResponse#getFiles()}をリストに詰めて、{@link Schedule#getOutput() スケジュールの出力}に設定する。<br>
 *
 * @author M.Takata
 */
public class ConcentrateScheduleExecutorService extends BeanFlowScheduleExecutorService
 implements ConcentrateScheduleExecutorServiceMBean{
    
    private static final long serialVersionUID = -2690240488435859910L;
    
    protected ServiceName concentrateBackupManagerServiceName;
    protected ConcentrateBackupManager concentrateBackupManager;
    
    {
        type = ConcentrateScheduleExecutorServiceMBean.DEFAULT_EXECUTOR_TYPE;
    }
    
    public void setConcentrateBackupManagerServiceName(ServiceName name){
        concentrateBackupManagerServiceName = name;
    }
    public ServiceName getConcentrateBackupManagerServiceName(){
        return concentrateBackupManagerServiceName;
    }
    
    public void setConcentrateBackupManager(ConcentrateBackupManager manager){
        concentrateBackupManager = manager;
    }
    
    public ConcentrateBackupManager getConcentrateBackupManager(){
        return concentrateBackupManager;
    }
    
    public void startService() throws Exception{
        if(concentrateBackupManagerServiceName != null){
            concentrateBackupManager = (ConcentrateBackupManager)ServiceManagerFactory
                .getServiceObject(concentrateBackupManagerServiceName);
        }
        super.startService();
    }
    
    protected Schedule executeInternal(Schedule schedule) throws Throwable{
        Object rawInput = schedule.getInput();
        try{
            ConcentrateRequest request = null;
            ConcentrateResponse response = null;
            if(rawInput instanceof String){
                final String inputStr = (String)rawInput;
                final String[] params = CSVReader.toArray(
                    inputStr,
                    ',',
                    '\\',
                    '"',
                    "",
                    null,
                    true,
                    false,
                    true,
                    false
                );
                if(params.length != 3){
                    throw new IllegalArgumentException("Input is illegal : " + rawInput);
                }
                final String key = toKey(schedule);
                final int processType = ConcentrateRequest.toProcessType(params[0]);
                if(processType == 0){
                    throw new IllegalArgumentException("ProcessType is illegal : " + rawInput);
                }
                final String source = params[1];
                if(source == null || source.length() == 0){
                    throw new IllegalArgumentException("Source is null : " + rawInput);
                }
                final String dest = params[2];
                if(dest == null || dest.length() == 0){
                    throw new IllegalArgumentException("Destination is null : " + rawInput);
                }
                request = new ConcentrateRequest(key, processType, source, dest);
            }else if(rawInput instanceof Map){
                Map jsonMap = (Map)rawInput;
                final String key = toKey(schedule);
                final int processType = ConcentrateRequest.toProcessType((String)jsonMap.get("processType"));
                if(processType == 0){
                    throw new IllegalArgumentException("ProcessType is illegal : " + rawInput);
                }
                final String source = (String)jsonMap.get("source");
                if(source == null || source.length() == 0){
                    throw new IllegalArgumentException("Source is null : " + rawInput);
                }
                final String dest = (String)jsonMap.get("destination");
                if(dest == null || dest.length() == 0){
                    throw new IllegalArgumentException("Destination is null : " + rawInput);
                }
                request = new ConcentrateRequest(key, processType, source, dest);
            }
            schedule.setInput(request);
            response = new ConcentrateResponse();
            schedule.setOutput(response);
            schedule = super.executeInternal(schedule);
            Object output = schedule.getOutput();
            if(request != null && output != null && output instanceof ConcentrateResponse){
                response = (ConcentrateResponse)output;
                if(response.getOutput() != null){
                    schedule.setOutput(response.getOutput());
                }
                final File[] files = response.getFiles();
                final String rootDirPath = response.getRootDirectory();
                final File rootDir = rootDirPath == null ? null : new File(rootDirPath);
                if(files != null && files.length != 0){
                    if(concentrateBackupManager != null){
                        Object result = null;
                        for(int i = 0; i < files.length; i++){
                            File file = files[i];
                            final Date date = response.getDate(file);
                            final String key = response.getKey(file);
                            final boolean isCompressed = response.getFileCompressed(file);
                            if(rootDir != null && !file.isAbsolute()){
                                file = new File(rootDir, file.getPath());
                            }
                            if(response.getGroup() == null
                                && date == null
                                && key == null
                            ){
                                result = concentrateBackupManager.backup(
                                    toFlowName(schedule),
                                    null,
                                    ConcentrateRequest.toProcessTypeString(request.getProcessType()),
                                    file,
                                    isCompressed,
                                    result
                                );
                            }else{
                                result = concentrateBackupManager.backup(
                                    response.getGroup(),
                                    date,
                                    key,
                                    file,
                                    isCompressed,
                                    result
                                );
                            }
                        }
                        if(response.getOutput() == null){
                            schedule.setOutput(result);
                        }
                    }else if(response.getOutput() == null){
                        List list = new ArrayList();
                        for(int i = 0; i < files.length; i++){
                            list.add(files[i]);
                        }
                        schedule.setOutput(list);
                    }
                    File dir = null;
                    switch(request.getProcessType()){
                    case ConcentrateRequest.PROCESS_TYPE_VALUE_GET:
                        File destFile = new File(request.getDestination());
                        String tmpDest = request.getDestination().replaceAll("\\\\", "/");
                        if(files.length == 1&& !tmpDest.endsWith("/")){
                            if(files[0].exists()){
                                dir = destFile.getParentFile();
                                if(dir != null && !dir.exists()){
                                    dir.mkdirs();
                                }
                                files[0].renameTo(destFile);
                            }
                        }else{
                            final File destDir = destFile;
                            if(destDir != null && !destDir.exists()){
                                destDir.mkdirs();
                            }
                            for(int i = 0; i < files.length; i++){
                                File file = files[i];
                                File targetFile = null;
                                if(rootDir == null){
                                    targetFile = file;
                                    destFile = new File(destDir, file.getName());
                                }else{
                                    if(file.isAbsolute()){
                                        targetFile = file.getCanonicalFile();
                                    }else{
                                        targetFile = new File(rootDir, file.getPath()).getCanonicalFile();
                                    }
                                    String targetPath = targetFile.getPath();
                                    String rootPath = rootDir.getCanonicalPath();
                                    if(!targetPath.startsWith(rootPath)){
                                        throw new ConcentrateBackupException(
                                            "File not exists in root directory. file=" + targetPath + ", rootDir=" + rootPath
                                        );
                                    }
                                    destFile = new File(destDir, targetPath.substring(rootPath.length()));
                                    dir = destFile.getParentFile();
                                    if(dir != null && !dir.exists()){
                                        dir.mkdirs();
                                    }
                                }
                                if(targetFile.exists()){
                                    targetFile.renameTo(destFile);
                                }
                            }
                        }
                        break;
                    case ConcentrateRequest.PROCESS_TYPE_VALUE_FORWARD:
                    case ConcentrateRequest.PROCESS_TYPE_VALUE_PUT:
                    default:
                        if(response.isClean()){
                            for(int i = 0; i < files.length; i++){
                                if(files[i].exists()){
                                    files[i].delete();
                                }
                            }
                        }
                        break;
                    }
                }else if(response.getOutput() == null){
                    schedule.setOutput(null);
                }
            }
        }finally{
            schedule.setInput(rawInput);
        }
        return schedule;
    }
    
    protected void checkPreExecute(Schedule schedule) throws Exception{
        final BeanFlowInvoker invoker = beanFlowInvokerFactory.createFlow(
            toFlowName(schedule)
        );
        if(invoker == null){
            throw new IllegalArgumentException("BeanFlow is not found : " + toFlowName(schedule));
        }
    }
    
    protected BeanFlowInvoker getBeanFlowInvoker(Schedule schedule) throws Throwable{
        return beanFlowInvokerFactory.createFlow(toFlowName(schedule));
    }
    
    protected String toFlowName(Schedule schedule){
        String flowName = schedule.getTaskName();
        int index = flowName.indexOf(':');
        if(index != -1){
            flowName = flowName.substring(0, index);
        }
        return flowName;
    }
    
    protected String toKey(Schedule schedule){
        String key = schedule.getTaskName();
        int index = key.indexOf(':');
        if(index != -1){
            key = key.substring(index + 1);
        }
        return key;
    }
}