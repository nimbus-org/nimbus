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

/**
 * テストステータス例外。
 * <p>
 *
 * @author M.Ishida
 */
public class TestStatusException extends TestException {
    
    private String scenarioGroupId;
    private String scenarioId;
    private String userId;
    
    /**
     * エラーメッセージを持ったインスタンスを生成するコンストラクタ。
     * <p>
     *
     * @param message エラーメッセージ
     */
    public TestStatusException(String message) {
        super(message);
    }
    
    /**
     * コンストラクタ。
     * <p>
     * 
     * @param userId 例外が発生した際のユーザID
     * @param scenarioGroupId 例外が発生した際のシナリオグループID
     * @param scenarioId 例外が発生した際のシナリオID
     */
    public TestStatusException(String userId, String scenarioGroupId, String scenarioId) {
        super();
        this.scenarioGroupId = scenarioGroupId;
        this.scenarioId = scenarioId;
        this.userId = userId;
    }
    
    /**
     * エラーメッセージを持ったインスタンスを生成するコンストラクタ。
     * <p>
     *
     * @param message エラーメッセージ
     * @param userId 例外が発生した際のユーザID
     * @param scenarioGroupId 例外が発生した際のシナリオグループID
     * @param scenarioId 例外が発生した際のシナリオID
     */
    public TestStatusException(String message, String userId, String scenarioGroupId, String scenarioId) {
        super(message);
        this.scenarioGroupId = scenarioGroupId;
        this.scenarioId = scenarioId;
        this.userId = userId;
    }
    
    /**
     * この例外の原因となった例外を持ったインスタンスを生成するコンストラクタ。
     * <p>
     *
     * @param cause 原因となった例外
     * @param userId 例外が発生した際のユーザID
     * @param scenarioGroupId 例外が発生した際のシナリオグループID
     * @param scenarioId 例外が発生した際のシナリオID
     */
    public TestStatusException(Throwable cause, String userId, String scenarioGroupId, String scenarioId) {
        super(cause);
        this.scenarioGroupId = scenarioGroupId;
        this.scenarioId = scenarioId;
        this.userId = userId;
    }
    
    /**
     * エラーメッセージと、この例外の原因となった例外を持ったインスタンスを生成するコンストラクタ。
     * <p>
     *
     * @param message エラーメッセージ
     * @param cause 原因となった例外
     * @param userId 例外が発生した際のユーザID
     * @param scenarioGroupId 例外が発生した際のシナリオグループID
     * @param scenarioId 例外が発生した際のシナリオID
     */
    public TestStatusException(String message, Throwable cause, String userId, String scenarioGroupId, String scenarioId) {
        super(message, cause);
        this.scenarioGroupId = scenarioGroupId;
        this.scenarioId = scenarioId;
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String id) {
        userId = id;
    }
    
    public String getScenarioGroupId() {
        return scenarioGroupId;
    }

    public void setScenarioGroupId(String id) {
        scenarioGroupId = id;
    }

    public String getScenarioId() {
        return scenarioId;
    }

    public void setScenarioId(String id) {
        scenarioId = id;
    }

}