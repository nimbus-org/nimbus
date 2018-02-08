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
package jp.ossc.nimbus.service.aop.interceptor.servlet;

import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import jp.ossc.nimbus.service.aop.*;

/**
 * ���N�G�X�g�����R�[�h�ݒ�C���^�[�Z�v�^�B<p>
 * ISO8859_1�ŃG���R�[�h���ꂽ���N�G�X�g�p�����[�^��C�ӂ̕����R�[�h�ɕϊ�����C���^�[�Z�v�^�ł���B<br>
 *
 * @author M.Takata
 */
public class SetCharacterEncodingInterceptorService
 extends ServletFilterInterceptorService
 implements SetCharacterEncodingInterceptorServiceMBean{
    
    private static final long serialVersionUID = 6562650000038918758L;
    
    protected static final String ISO8859_1 = "ISO8859_1";
    protected static final String QUERY_DELIM = "&";
    
    protected String characterEncoding;
    protected boolean isEncodeQuery;
    
    // SetCharacterEncodingInterceptorServiceMBean��JavaDoc
    public void setCharacterEncoding(String encoding){
        characterEncoding = encoding;
    }
    
    // SetCharacterEncodingInterceptorServiceMBean��JavaDoc
    public String getCharacterEncoding(){
        return characterEncoding;
    }
    
    // SetCharacterEncodingInterceptorServiceMBean��JavaDoc
    public void setEncodeQuery(boolean isEncode){
        isEncodeQuery = isEncode;
    }
    
    // SetCharacterEncodingInterceptorServiceMBean��JavaDoc
    public boolean isEncodeQuery(){
        return isEncodeQuery;
    }
    
    /**
     * ���N�G�X�g�̕����R�[�h��ݒ肵�āA���̃C���^�[�Z�v�^���Ăяo���B<p>
     * �T�[�r�X���J�n����Ă��Ȃ��ꍇ�́A���������Ɏ��̃C���^�[�Z�v�^���Ăяo���B<br>
     *
     * @param context �Ăяo���̃R���e�L�X�g���
     * @param chain ���̃C���^�[�Z�v�^���Ăяo�����߂̃`�F�[��
     * @return �Ăяo�����ʂ̖߂�l
     * @exception Throwable �Ăяo����ŗ�O�����������ꍇ�A�܂��͂��̃C���^�[�Z�v�^�ŔC�ӂ̗�O�����������ꍇ�B�A���A�{���Ăяo����鏈����throw���Ȃ�RuntimeException�ȊO�̗�O��throw���Ă��A�Ăяo�����ɂ͓`�d����Ȃ��B
     */
    public Object invokeFilter(
        ServletFilterInvocationContext context,
        InterceptorChain chain
    ) throws Throwable{
        if(getState() == STARTED){
            final ServletRequest request = context.getServletRequest();
            if(request.getCharacterEncoding() == null && characterEncoding != null){
                request.setCharacterEncoding(characterEncoding);
            }
            if(isEncodeQuery && request instanceof HttpServletRequest){
                final HttpServletRequest httpReq = (HttpServletRequest)request;
                final String query = httpReq.getQueryString();
                if(query != null){
                    final Map queryMap = parseQueryString(query);
                    final Iterator itr = queryMap.keySet().iterator();
                    while(itr.hasNext()){
                        final String key = (String)itr.next();
                        final String[] qvals = (String[])queryMap.get(key);
                        
                        if(qvals != null && qvals.length > 0){
                            String[] vals = request.getParameterValues(key);
                            if(vals == null){
                                continue;
                            }
                            
                            for(int i = 0; i < qvals.length; i++){
                                for(int j = 0; j < vals.length; j++){
                                    if(qvals[i].equals(vals[j])){
                                        vals[j] = new String(
                                            vals[j].getBytes(ISO8859_1),
                                            request.getCharacterEncoding()
                                        );
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return chain.invokeNext(context);
    }
    
    protected static Map parseQueryString(String str){
        final Map params = new HashMap();
        final StringBuffer sb = new StringBuffer();
        final StringTokenizer tokens = new StringTokenizer(str, QUERY_DELIM);
        while(tokens.hasMoreTokens()){
            final String pair = tokens.nextToken();
            final int pos = pair.indexOf('=');
            
            String key = null;
            String val = null;
            if(pos == -1){
                key = parseName(pair, sb);
                val = "";
            }else{
                key = parseName(pair.substring(0, pos), sb);
                val = parseName(pair.substring(pos + 1, pair.length()), sb);
            }
            
            String valArray[] = null;
            if(params.containsKey(key)){
                final String[] oldVals = (String[])params.get(key);
                valArray = new String[oldVals.length + 1];
                for(int i = 0; i < oldVals.length; i++){
                   valArray[i] = oldVals[i];
                }
                valArray[oldVals.length] = val;
            }else{
                valArray = new String[1];
                valArray[0] = val;
            }
            params.put(key, valArray);
        }
        return params;
    }
    
    protected static String parseName(String str, StringBuffer sb){
        sb.setLength(0);
        for(int i = 0, max = str.length(); i < max; i++){
            final char c = str.charAt(i);
            switch(c){
            case '+':
                sb.append(' ');
                break;
            case '%':
                try{
                    sb.append(
                        (char)Integer.parseInt(str.substring(i + 1, i + 3), 16)
                    );
                    i += 2;
                }catch(NumberFormatException e){
                    sb.append(c);
                }catch(StringIndexOutOfBoundsException e){
                    String rest = str.substring(i);
                    sb.append(rest);
                    if(rest.length() == 2){
                        i++;
                    }
                }
                break;
            default:
                sb.append(c);
                break;
            }
        }
        return sb.toString();
    }
}