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

import java.io.Reader;
import java.io.Serializable;

/**
 * テストアクションコンテキストクラス。<p>
 * 
 * @author M.Ishida
 */
public class TestActionContextImpl extends TestPhaseExecutableImpl implements TestActionContext, TestPhaseExecutable, Serializable {
    
    private static final long serialVersionUID = 327965690413192133L;
    
    private String id;
    private String title;
    private String description;
    private int type;
    private transient Reader[] resources;
    private transient Object action;
    private long retryInterval;
    private int retryCount;
    private boolean success = true;
    private double expectedCost = Double.NaN;
    private double cost = Double.NaN;
    private Throwable throwable;
    
    public TestActionContextImpl() {
        super();
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public int getType() {
        return type;
    }
    
    public void setType(int type) {
        this.type = type;
    }
    
    public Reader[] getResources() {
        return resources;
    }
    
    public void setResources(Reader[] resources) {
        this.resources = resources;
    }
    
    public Object getAction() {
        return action;
    }
    
    public void setAction(Object action) {
        this.action = action;
    }
    
    public long getRetryInterval() {
        return retryInterval;
    }
    
    public void setRetryInterval(long retryInterval) {
        this.retryInterval = retryInterval;
    }
    
    public int getRetryCount() {
        return retryCount;
    }
    
    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public double getExpectedCost() {
        return expectedCost;
    }
    
    public void setExpectedCost(double expectedCost) {
        this.expectedCost = expectedCost;
    }
    
    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public Throwable getThrowable() {
        return throwable;
    }
    
    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }
    
    public void clearState() {
        success = true;
        throwable = null;
    }

}
