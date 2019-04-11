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
package jp.ossc.nimbus.service.scheduler2.aws;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.services.glue.AWSGlueClient;
import com.amazonaws.services.glue.model.BatchStopJobRunRequest;
import com.amazonaws.services.glue.model.Crawler;
import com.amazonaws.services.glue.model.GetCrawlerRequest;
import com.amazonaws.services.glue.model.GetCrawlerResult;
import com.amazonaws.services.glue.model.GetJobRunRequest;
import com.amazonaws.services.glue.model.GetJobRunResult;
import com.amazonaws.services.glue.model.JobRun;
import com.amazonaws.services.glue.model.StartCrawlerRequest;
import com.amazonaws.services.glue.model.StartJobRunRequest;
import com.amazonaws.services.glue.model.StartJobRunResult;
import com.amazonaws.services.glue.model.StopCrawlerRequest;

import jp.ossc.nimbus.service.scheduler2.Schedule;
import jp.ossc.nimbus.service.scheduler2.ScheduleStateControlException;
import jp.ossc.nimbus.util.converter.BeanJSONConverter;
import jp.ossc.nimbus.util.converter.DateFormatConverter;

/**
 * AWS Glueを呼び出すスケジュール実行。<p>
 *
 * @author M.Ishida
 */
public class AWSGlueScheduleExecutorService extends AWSWebServiceScheduleExecutorService implements AWSGlueScheduleExecutorServiceMBean {
    
    private static final long serialVersionUID = 591226491501738321L;
    
    protected int waitPollingInterval = 1;
    protected String crawlerReadyString = DEFAULT_CRAWLER_READY_STR;
    protected String crawlerRunningString = DEFAULT_CRAWLER_RUNNING_STR;
    protected String jobSucceededString = DEFAULT_JOB_SUCCEEDED_STR;
    protected String jobStoppedString = DEFAULT_JOB_STOPPED_STR;
    protected String jobRunningString = DEFAULT_JOB_RUNNING_STR;
    
    protected Map executeScheduleMap;
    
    {
        type = DEFAULT_EXECUTOR_TYPE;
    }
    
    public void setWaitPollingInterval(int interval) {
        waitPollingInterval = interval;
    }
    
    public int getWaitPollingInterval() {
        return waitPollingInterval;
    }
    
    public String getCrawlerReadyString() {
        return crawlerReadyString;
    }
    
    public void setCrawlerReadyString(String str) {
        crawlerReadyString = str;
    }
    
    public String getCrawlerRunningString() {
        return crawlerRunningString;
    }
    
    public void setCrawlerRunningString(String str) {
        crawlerRunningString = str;
    }
    
    public String getJobSucceededString() {
        return jobSucceededString;
    }
    
    public void setJobSucceededString(String str) {
        jobSucceededString = str;
    }
    
    public String getJobStoppedString() {
        return jobStoppedString;
    }
    
    public void setJobStoppedString(String str) {
        jobStoppedString = str;
    }
    
    public String getJobRunningString() {
        return jobRunningString;
    }
    
    public void setJobRunningString(String str) {
        jobRunningString = str;
    }
    
    public void createService() throws Exception {
        super.createService();
        executeScheduleMap = Collections.synchronizedMap(new HashMap());
    }
    
    public void startService() throws Exception {
        super.startService();
        
        BeanJSONConverter beanJSONConverter = new BeanJSONConverter();
        DateFormatConverter dfc = new DateFormatConverter();
        dfc.setFormat("yyyy/MM/dd HH:mm:ss.SSS");
        dfc.setConvertType(DateFormatConverter.DATE_TO_STRING);
        beanJSONConverter.setFormatConverter(java.util.Date.class, dfc);
        addAutoInputConvertMappings(beanJSONConverter);
        addAutoOutputConvertMappings(beanJSONConverter);
    }
    
    public void destroyService() throws Exception {
        super.destroyService();
        executeScheduleMap = null;
    }
    
    protected Schedule executeInternal(Schedule schedule) throws Throwable {
        executeScheduleMap.put(schedule.getId(), schedule);
        try{
            Schedule result = super.executeInternal(schedule);
            AWSGlueClient client = (AWSGlueClient) webServiceClient;
            AmazonWebServiceRequest request = (AmazonWebServiceRequest) schedule.getInput();
            
            if(request instanceof StartCrawlerRequest){
                GetCrawlerRequest getCrawlerRequest = new GetCrawlerRequest();
                getCrawlerRequest.setName(((StartCrawlerRequest) request).getName());
                GetCrawlerResult getCrawlerResult = client.getCrawler(getCrawlerRequest);
                Crawler crawler = getCrawlerResult.getCrawler();
                while (!crawlerReadyString.equals(crawler.getState())){
                    try{
                        Thread.sleep(waitPollingInterval * 1000);
                    }catch (Exception e){
                    }
                    getCrawlerResult = client.getCrawler(getCrawlerRequest);
                    crawler = getCrawlerResult.getCrawler();
                }
                schedule.setOutput(crawler.getLastCrawl());
            }else if(request instanceof StartJobRunRequest){
                GetJobRunRequest getJobRunRequest = new GetJobRunRequest();
                getJobRunRequest.setJobName(((StartJobRunRequest) request).getJobName());
                getJobRunRequest.setRunId(((StartJobRunResult) schedule.getOutput()).getJobRunId());
                
                GetJobRunResult getJobRunResult = client.getJobRun(getJobRunRequest);
                JobRun jobRun = getJobRunResult.getJobRun();
                while (!jobSucceededString.equals(jobRun.getJobRunState()) && !jobStoppedString.equals(jobRun.getJobRunState())){
                    try{
                        Thread.sleep(waitPollingInterval * 1000);
                    }catch (Exception e){
                    }
                    getJobRunResult = client.getJobRun(getJobRunRequest);
                    jobRun = getJobRunResult.getJobRun();
                }
            }
            return result;
        }finally{
            executeScheduleMap.remove(schedule.getId());
        }
    }
    
    public boolean controlState(String id, int cntrolState) throws ScheduleStateControlException {
        Schedule schedule = (Schedule) executeScheduleMap.get(id);
        if(schedule != null && cntrolState == Schedule.CONTROL_STATE_ABORT){
            AWSGlueClient client = (AWSGlueClient) webServiceClient;
            AmazonWebServiceRequest request = (AmazonWebServiceRequest) schedule.getInput();
            if(request instanceof StartCrawlerRequest){
                String name = ((StartCrawlerRequest) request).getName();
                GetCrawlerRequest getCrawlerRequest = new GetCrawlerRequest();
                getCrawlerRequest.setName(name);
                GetCrawlerResult getCrawlerResult = client.getCrawler(getCrawlerRequest);
                Crawler crawler = getCrawlerResult.getCrawler();
                if(crawlerRunningString.equals(crawler.getState())){
                    StopCrawlerRequest stopCrawlerRequest = new StopCrawlerRequest();
                    stopCrawlerRequest.setName(name);
                    client.stopCrawler(stopCrawlerRequest);
                }
                return true;
            }else if(request instanceof StartJobRunRequest){
                String jobName = ((StartJobRunRequest) request).getJobName();
                String jobRunId = ((StartJobRunResult) schedule.getOutput()).getJobRunId();
                GetJobRunRequest getJobRunRequest = new GetJobRunRequest();
                getJobRunRequest.setJobName(jobName);
                getJobRunRequest.setRunId(jobRunId);
                
                GetJobRunResult getJobRunResult = client.getJobRun(getJobRunRequest);
                JobRun jobRun = getJobRunResult.getJobRun();
                if(jobRunningString.equals(jobRun.getJobRunState())){
                    BatchStopJobRunRequest batchStopJobRunRequest = new BatchStopJobRunRequest();
                    batchStopJobRunRequest.setJobName(jobName);
                    Collection runIds = new ArrayList();
                    runIds.add(jobRunId);
                    batchStopJobRunRequest.setJobRunIds(runIds);
                    client.batchStopJobRun(batchStopJobRunRequest);
                }
                return true;
            }
        }
        return false;
    }
}