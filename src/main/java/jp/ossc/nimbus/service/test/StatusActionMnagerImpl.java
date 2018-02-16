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
package jp.ossc.nimbus.service.test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * アクションの結果を管理するステータスクラス。<p>
 * 
 * @author M.Ishida
 */
public class StatusActionMnagerImpl extends StatusBaseImpl implements StatusActionMnager, java.io.Serializable{
    
    private String currentActionId;
    private Throwable throwable;
    private List testActionContextList;
    
    /**
     * 指定された実行ユーザでのステータスを生成する。<p>
     *
     * @param userId 実行ユーザ
     */
    public StatusActionMnagerImpl(String userId){
        super(userId);
        testActionContextList = new ArrayList();
    }
    
    /**
     * 現在のアクションIDを設定する。<p>
     * 
     * @param actionId アクションID
     */
    public void setCurrentActionId(String actionId) {
        currentActionId = actionId;
    }
    
    public String getCurrentActionId() {
        return this.currentActionId;
    }
    
    /**
     * アクションを実行した結果、発生した例外を設定する。<p>
     *
     * @param throwable アクションを実行した結果、発生した例外
     */
    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
        if (throwable != null) {
            super.setResult(false);
        }
    }
    
    public Throwable getThrowable() {
        return throwable;
    }
    
    public boolean getActionResult(String actionId) {
        for (int i = 0; i < testActionContextList.size(); i++) {
            TestActionContext context = (TestActionContext) testActionContextList.get(i);
            if(context.getId().equals(actionId)){
                return context.isSuccess();
            }
        }
        return true;
    }
    
    public Map getActionResultMap() {
        Map result = new LinkedHashMap();
        for (int i = 0; i < testActionContextList.size(); i++) {
            TestActionContext context = (TestActionContext) testActionContextList.get(i);
            result.put(context.getId(), new Boolean(context.isSuccess()));
        }
        return result;
    }
    
    /**
     * {@link TestActionContext}を追加する。<p>
     *
     * @param context TestActionContext
     */
    public void addTestActionContext(TestActionContext context) {
        testActionContextList.add(context);
    }
    
    public List getTestActionContexts() {
        return testActionContextList;
    }
    
}
