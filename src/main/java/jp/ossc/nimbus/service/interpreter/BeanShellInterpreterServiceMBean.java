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
package jp.ossc.nimbus.service.interpreter;

import java.util.Map;

import jp.ossc.nimbus.core.*;

/**
 * {@link BeanShellInterpreterService}のMBeanインタフェース。<p>
 * 
 * @author M.Takata
 */
public interface BeanShellInterpreterServiceMBean extends ServiceBaseMBean{
    
    /**
     * インタープリタに、サービス変数を設定する。<p>
     *
     * @param name 変数名
     * @param serviceName サービス名
     */
    public void setVariableServiceName(String name, ServiceName serviceName);
    
    /**
     * インタープリタに設定するサービス変数を取得する。<p>
     *
     * @param name 変数名
     * @return サービス名
     */
    public ServiceName getVariableServiceName(String name);
    
    /**
     * インタープリタに、変数を設定する。<p>
     *
     * @param name 変数名
     * @param val 変数
     */
    public void setVariableObject(String name, Object val);
    
    /**
     * インタープリタに設定する変数を取得する。<p>
     *
     * @param name 変数名
     * @return 変数
     */
    public Object getVariableObject(String name);
    
    /**
     * インタープリタに、変数を設定する。<p>
     *
     * @param name 変数名
     * @param val 変数
     */
    public void setVariableInt(String name, int val);
    
    /**
     * インタープリタに設定する変数を取得する。<p>
     *
     * @param name 変数名
     * @return 変数
     */
    public int getVariableInt(String name);
    
    /**
     * インタープリタに、変数を設定する。<p>
     *
     * @param name 変数名
     * @param val 変数
     */
    public void setVariableLong(String name, long val);
    
    /**
     * インタープリタに設定する変数を取得する。<p>
     *
     * @param name 変数名
     * @return 変数
     */
    public long getVariableLong(String name);
    
    /**
     * インタープリタに、変数を設定する。<p>
     *
     * @param name 変数名
     * @param val 変数
     */
    public void setVariableFloat(String name, float val);
    
    /**
     * インタープリタに設定する変数を取得する。<p>
     *
     * @param name 変数名
     * @return 変数
     */
    public float getVariableFloat(String name);
    
    /**
     * インタープリタに、変数を設定する。<p>
     *
     * @param name 変数名
     * @param val 変数
     */
    public void setVariableDouble(String name, double val);
    
    /**
     * インタープリタに設定する変数を取得する。<p>
     *
     * @param name 変数名
     * @return 変数
     */
    public double getVariableDouble(String name);
    
    /**
     * インタープリタに、変数を設定する。<p>
     *
     * @param name 変数名
     * @param val 変数
     */
    public void setVariableBoolean(String name, boolean val);
    
    /**
     * インタープリタに設定する変数を取得する。<p>
     *
     * @param name 変数名
     * @return 変数
     */
    public boolean getVariableBoolean(String name);
    
    /**
     * インタープリタに設定する変数マップを取得する。<p>
     *
     * @return 変数マップ
     */
    public Map getVariables();
    
    /**
     * インタープリタがコードを評価する際に使用するクラスローダを設定する。<p>
     *
     * @param loader クラスローダ
     */
    public void setClassLoader(ClassLoader loader);
    
    /**
     * インタープリタがコードを評価する際に使用するクラスローダを取得する。<p>
     *
     * @return クラスローダ
     */
    public ClassLoader getClassLoader();
    
    /**
     * インタープリタに読み込ませたいソースファイル名を設定する。<p>
     * 
     * @param names ソースファイル名
     */
    public void setSourceFileNames(String[] names);
    
    /**
     * インタープリタに読み込ませたいソースファイル名を取得する。<p>
     * 
     * @return ソースファイル名
     */
    public String[] getSourceFileNames();
    
    /**
     * {@link Interpreter#evaluate(String)}のたびにインタープリタを生成するかどうかを設定する。<p>
     * デフォルトは、trueで毎回生成する。<br>
     * 
     * @param isNew 生成する場合true
     */
    public void setNewInterpreterByEvaluate(boolean isNew);
    
    /**
     * {@link Interpreter#evaluate(String)}のたびにインタープリタを生成するかどうかを判定する。<p>
     * 
     * @return 生成する場合true
     */
    public boolean isNewInterpreterByEvaluate();
    
    public Object evaluate(String code) throws EvaluateException;
}