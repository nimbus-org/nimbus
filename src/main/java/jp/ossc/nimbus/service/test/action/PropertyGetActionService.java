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

import java.io.BufferedReader;
import java.io.Reader;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.beans.PropertyFactory;
import jp.ossc.nimbus.beans.Property;
import jp.ossc.nimbus.service.test.TestAction;
import jp.ossc.nimbus.service.test.TestActionEstimation;
import jp.ossc.nimbus.service.test.ChainTestAction;
import jp.ossc.nimbus.service.test.TestContext;

/**
 * オブジェクトのプロパティ値を取得するテストアクション。<p>
 * 動作の詳細は、{@link #execute(TestContext, String, Reader)}を参照。<br>
 * 
 * @author M.Takata
 */
public class PropertyGetActionService extends ServiceBase implements TestAction, ChainTestAction.TestActionProcess, TestActionEstimation, PropertyGetActionServiceMBean{
    
    private static final long serialVersionUID = 7682175982096275432L;
    protected double expectedCost = 0d;
    
    /**
     * リソースの内容を読み込んで、オブジェクトのプロパティ値を取得する。<p>
     * リソースのフォーマットは、以下。<br>
     * <pre>
     * property
     * targetObjectId
     * </pre>
     * propertyは、取得するプロパティ文字列を指定する。プロパティ文字列は、{@link PropertyFactory#createProperty(String)}で解釈される。<br>
     * targetObjectIdは、プロパティの取得対象となるオブジェクトを指定するもので、同一テストケース中に、このTestActionより前に、プロパティの取得対象となるオブジェクトを戻すテストアクションが存在する場合は、そのアクションIDを指定する。また、同一シナリオ中に、このTestActionより前に、プロパティの取得対象となるオブジェクトを戻すテストアクションが存在する場合は、テストケースIDとアクションIDをカンマ区切りで指定する。<br>
     *
     * @param context コンテキスト
     * @param actionId アクションID
     * @param resource リソース
     * @return 取得したプロパティ値
     */
    public Object execute(TestContext context, String actionId, Reader resource) throws Exception{
        return execute(context, actionId, null, resource);
    }
    
    /**
     * リソースの内容を読み込んで、オブジェクトのプロパティ値を取得する。<p>
     * リソースのフォーマットは、以下。<br>
     * <pre>
     * property
     * targetObjectId
     * </pre>
     * propertyは、取得するプロパティ文字列を指定する。プロパティ文字列は、{@link PropertyFactory#createProperty(String)}で解釈される。<br>
     * targetObjectIdは、プロパティの取得対象となるオブジェクトを指定するもので、同一テストケース中に、このTestActionより前に、プロパティの取得対象となるオブジェクトを戻すテストアクションが存在する場合は、そのアクションIDを指定する。また、同一シナリオ中に、このTestActionより前に、プロパティの取得対象となるオブジェクトを戻すテストアクションが存在する場合は、テストケースIDとアクションIDをカンマ区切りで指定する。preResultを使用する場合は、空行を指定する。<br>
     *
     * @param context コンテキスト
     * @param actionId アクションID
     * @param preResult プロパティの取得対象となるオブジェクト
     * @param resource リソース
     * @return 取得したプロパティ値
     */
    public Object execute(TestContext context, String actionId, Object preResult, Reader resource) throws Exception{
        BufferedReader br = new BufferedReader(resource);
        Object targetObject = preResult;
        Property property = null;
        try{
            final String propStr = br.readLine();
            if(propStr == null || propStr.length() == 0){
                throw new Exception("Unexpected EOF on property");
            }
            property = PropertyFactory.createProperty(propStr);
            property.setIgnoreNullProperty(true);
            final String targetObjectId = br.readLine();
            if(targetObjectId != null && targetObjectId.length() != 0){
                Object actionResult = null;
                if(targetObjectId.indexOf(",") == -1){
                    actionResult = context.getTestActionResult(targetObjectId);
                }else{
                    String[] ids = targetObjectId.split(",");
                    if(ids.length != 2){
                        throw new Exception("Illegal targetObjectId format. id=" + targetObjectId);
                    }
                    actionResult = context.getTestActionResult(ids[0], ids[1]);
                }
                if(actionResult == null){
                    throw new Exception("TestActionResult not found. id=" + targetObjectId);
                }
                targetObject = actionResult;
            }
        }finally{
            br.close();
            br = null;
        }
        return property.getProperty(targetObject);
    }
    
    public void setExpectedCost(double cost) {
        expectedCost = cost;
    }
    
    public double getExpectedCost() {
        return expectedCost;
    }
}
