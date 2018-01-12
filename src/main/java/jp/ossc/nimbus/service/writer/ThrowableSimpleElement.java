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
package jp.ossc.nimbus.service.writer;

import java.lang.reflect.*;

/**
 * 例外記述要素。<p>
 * 設定された例外をスタックトレース付きで出力する記述要素クラスである。<br>
 *
 * @author K.Nagai
 */
public class ThrowableSimpleElement extends SimpleElement {
    
    private static final long serialVersionUID = -6342673505293153134L;
    
    private static final String LINE_SEP = System.getProperty("line.separator");
    private static final String TAB = "\t";
    private static final String MSG_TITLE = "Exception occuers :";
    private static final String MSG_CAUSE = "Caused by:";
    private static final String SERVLET_EXCEPTION_NAME = "javax.servlet.ServletException";
    private static final String GET_ROOT_CAUSE_METHOD = "getRootCause";
    private static final String JMS_EXCEPTION_NAME = "javax.jms.JMSException";
    private static final String GET_LINKED_EXCEPTION_METHOD = "getLinkedException";
    
    private boolean isOutputCause = true;
    private boolean isOutputTab = true;
    
    /**
     * 空のインスタンスを生成する。<p>
     */
    public ThrowableSimpleElement(){
    }
    
    /**
     * インスタンスを生成する。<p>
     *
     * @param value 変換対象のオブジェクト
     */
    public ThrowableSimpleElement(Object value){
        super(value);
    }
    
    /**
     * インスタンスを生成する。<p>
     *
     * @param key キー
     * @param value 変換対象のオブジェクト
     */
    public ThrowableSimpleElement(Object key, Object value){
        super(key, value);
    }
    
    /**
     * この要素が保持する例外の原因となる例外を{@link #toString()}で出力するかどうかを設定する。<p>
     * デフォルトは、true。
     *
     * @param output 出力する場合true
     */
    public void setOutputCause(boolean output){
        isOutputCause = output;
    }
    
    /**
     * この要素が保持する例外の原因となる例外を{@link #toString()}で出力するかどうかを判定する。<p>
     *
     * @return trueの場合出力する
     */
    public boolean getOutputCause() {
        return isOutputCause;
    }
    
    /**
     * スタックトレースの出力時にタブでインデントするかどうかを設定する。<p>
     * デフォルトは、true。
     *
     * @param outputTab タブでインデントする場合は、true
     */
    public void setOutputTab(boolean outputTab) {
        isOutputTab = outputTab;
    }
    
    /**
     * スタックトレースの出力時にタブでインデントするかどうかを判定する。<p>
     *
     * @return タブでインデントする場合は、true
     */
    public boolean getOutputTab() {
        return isOutputTab;
    }
    
    /**
     * この例外のスタックトレースや原因となる例外のスタックトレース文字列にして取得する。<p>
     * 
     * @return この例外のスタックトレースや原因となる例外のスタックトレース文字列
     */
    public String toString(){
        if(mValue == null){
            return super.toString();
        }
        Throwable e = (Throwable)mValue;
        final StringBuffer buf = new StringBuffer();
        buf.append(MSG_TITLE).append(e).append(LINE_SEP);
        final StackTraceElement[] elemss = e.getStackTrace();
        if(elemss != null){
            for(int i = 0, max = elemss.length; i < max; i++){
                if(isOutputTab){
                    buf.append(TAB);
                }
                buf.append(elemss[i]);
                if(i != max - 1){
                    buf.append(LINE_SEP);
                }
            }
        }
        if(isOutputCause){
            for(Throwable ee = getCause(e); ee != null; ee = getCause(ee)){
                buf.append(LINE_SEP).append(MSG_CAUSE)
                    .append(ee).append(LINE_SEP);
                final StackTraceElement[] elems = ee.getStackTrace();
                if(elems != null){
                    for(int i = 0, max = elems.length; i < max; i++){
                        if(isOutputTab){
                            buf.append(TAB);
                        }
                        buf.append(elems[i]);
                        if(i != max - 1){
                            buf.append(LINE_SEP);
                        }
                    }
                }
            }
        }
        return convertString(buf.toString());
    }
    
    private Throwable getCause(Throwable th){
        Throwable cause = null;
        String thClassName = th.getClass().getName();
        if(thClassName.equals(SERVLET_EXCEPTION_NAME)){
            // 例外がServletExceptionの場合は、ルートの原因を取得
            try{
                cause = (Throwable)th.getClass()
                    .getMethod(GET_ROOT_CAUSE_METHOD, (Class[])null).invoke(th, (Object[])null);
            }catch(NoSuchMethodException e){
            }catch(IllegalAccessException e){
            }catch(InvocationTargetException e){
            }
        }else if(thClassName.equals(JMS_EXCEPTION_NAME)){
            // 例外がJMSExceptionの場合は、リンク例外を取得
            try{
                cause = (Exception)th.getClass()
                    .getMethod(GET_LINKED_EXCEPTION_METHOD, (Class[])null).invoke(th, (Object[])null);
            }catch(NoSuchMethodException e){
            }catch(IllegalAccessException e){
            }catch(InvocationTargetException e){
            }
        }else{
            cause = th.getCause();
        }
        return cause == th ? null : cause;
    }
    
    /**
     * この要素のオブジェクトをそのまま取得する。<p>
     * {@link #getValue()}と同じ値を返す。<br>
     * 
     * @return この要素のオブジェクト
     */
    public Object toObject(){
        return getValue();
    }
}
