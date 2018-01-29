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
// パッケージ
// インポート
package jp.ossc.nimbus.service.journal.editor;

import java.lang.reflect.*;
import java.io.Serializable;

import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;

/**
 * {@link Throwable}をフォーマットするジャーナルエディタ。<p>
 * 
 * @version $Name:  $
 * @author K.Nagai
 * @since 1.0
 */
public class ThrowableJournalEditorService extends
        ImmutableJournalEditorServiceBase implements
        ThrowableJournalEditorServiceMBean, Serializable {
    
    private static final long serialVersionUID = 2145141927079525859L;
    
    private static final String SERVLET_EXCEPTION_NAME = "javax.servlet.ServletException";
    private static final String GET_ROOT_CAUSE_METHOD = "getRootCause";
    private static final String JMS_EXCEPTION_NAME = "javax.jms.JMSException";
    private static final String GET_LINKED_EXCEPTION_METHOD = "getLinkedException";
    private static final String LINE_SEP = System.getProperty("line.separator");

    private static final String TAB = "\t";

    /** 原因(Cause)を追って出力するかどうか */
    private boolean isOutputCause = true;

    public void setOutputCause(boolean output) {
        isOutputCause = output;
    }

    /** 原因(Cause)を追って出力する際TABを入れるかどうか */
    private boolean bOutputTab = true;

    public boolean getOutputCause() {
        return isOutputCause;
    }

    protected String toString(EditorFinder finder, Object key, Object value,
            StringBuilder buf) {
        Throwable e = (Throwable) value;
        if (e != null) {
            buf.append("Exception occuers :").append(e.toString()).append(LINE_SEP);
            final StackTraceElement[] elemss = e.getStackTrace();
            if (elemss != null) {
                for (int i = 0; i < elemss.length; i++) {
                    if (bOutputTab) buf.append(TAB);
                    if (elemss[i] != null) {
                        buf.append(elemss[i].toString()).append(LINE_SEP);
                    } else {
                        buf.append("null").append(LINE_SEP);
                    }
                }
            }
            if (isOutputCause) {
                for (Throwable ee = getCause(e); ee != null; ee = getCause(ee)) {
                    buf.append("Caused by:").append(ee.toString()).append(LINE_SEP);
                    final StackTraceElement[] elems = ee.getStackTrace();
                    if (elems != null) {
                        for (int i = 0; i < elems.length; i++) {
                            if (bOutputTab) buf.append(TAB);
                            if (elems[i] != null) {
                                buf.append(elems[i].toString()).append(LINE_SEP);
                            } else {
                                buf.append("null").append(LINE_SEP);
                            }
                        }
                    }
                }
            }
        } else {
            buf.append("Exception occuers :").append(e).append(LINE_SEP);
        }
        return buf.toString();
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
     * @return bOutputTab を戻します。
     */
    public boolean getOutputTab() {
        return bOutputTab;
    }

    /**
     * @param outputTab
     *            bOutputTab を設定。
     */
    public void setOutputTab(boolean outputTab) {
        bOutputTab = outputTab;
    }
}