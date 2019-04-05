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

import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;

import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.service.scheduler2.AbstractScheduleExecutorServiceMBean;

/**
 * {@link AWSLambdaScheduleExecutorService}のMBeanインタフェース。<p>
 * 
 * @author M.Ishida
 */
public interface AWSLambdaScheduleExecutorServiceMBean extends AbstractScheduleExecutorServiceMBean {
    
    /**
     * デフォルトのスケジュール実行種別。<p>
     */
    public static final String DEFAULT_EXECUTOR_TYPE = "AWS_LAMBDA";
    
    /**
     * デフォルトのエンコーディング。<p>
     */
    public static final String DEFAULT_ENCODING = "UTF-8";
    
    /**
     * AWSLambdaのサービス名を取得する。<p>
     * 
     * @return AWSLambdaのサービス名
     */
    public ServiceName getAWSLambdaServiceName();
    
    /**
     * AWSLambdaのサービス名を設定する。<p>
     * 
     * @param serviceName AWSLambdaのサービス名
     */
    public void setAWSLambdaServiceName(ServiceName serviceName);
    
    /**
     * AWSLambdaClientBuilderのサービス名を取得する。<p>
     * 
     * @return AWSLambdaClientBuilderのサービス名
     */
    public ServiceName getAWSLambdaClientBuilderServiceName();

    /**
     * AWSLambdaClientBuilderのサービス名を設定する。<p>
     * 
     * @param serviceName AWSLambdaClientBuilderのサービス名
     */
    public void setAWSLambdaClientBuilderServiceName(ServiceName serviceName);
    
    /**
     * Lambdaへのリクエスト時のClientExecutionTimeoutを取得する。<p>
     * 
     * @return Lambdaへのリクエスト時のClientExecutionTimeout
     */
    public int getClientExecutionTimeout();

    /**
     * Lambdaへのリクエスト時のClientExecutionTimeoutを設定する。<p>
     * 
     * @param timeout Lambdaへのリクエスト時のClientExecutionTimeout
     */
    public void setClientExecutionTimeout(int timeout);

    /**
     * Lambdaへのリクエスト時のRequestTimeoutを取得する。<p>
     * 
     * @return Lambdaへのリクエスト時のRequestTimeout
     */
    public int getRequestTimeout();

    /**
     * Lambdaへのリクエスト時のRequestTimeoutを設定する。<p>
     * 
     * @param timeout Lambdaへのリクエスト時のRequestTimeout
     */
    public void setRequestTimeout(int timeout);

    /**
     * LambdaからのレスポンスのByteBufferを文字列に変換する際のエンコーディングを取得する。<p>
     * 
     * @return エンコーディング
     */
    public String getEncoding();

    /**
     * LambdaからのレスポンスのByteBufferを文字列に変換する際のエンコーディングを設定する。<p>
     * 
     * @param encoding エンコーディング
     */
    public void setEncoding(String encoding);

    /**
     * AWSLambdaを取得する。<p>
     * 
     * @return AWSLambda
     */
    public AWSLambda getAWSLambda();
    
    /**
     * AWSLambdaを設定する。<p>
     * 
     * @param awsLambda AWSLambda
     */
    public void setAWSLambda(AWSLambda awsLambda);
    
    /**
     * AWSLambdaClientBuilderを取得する。<p>
     * 
     * @return AWSLambdaClientBuilder
     */
    public AWSLambdaClientBuilder getAWSLambdaClientBuilder();

    /**
     * AWSLambdaClientBuilderを設定する。<p>
     * 
     * @param builder AWSLambdaClientBuilder
     */
    public void setAWSLambdaClientBuilder(AWSLambdaClientBuilder builder);
}