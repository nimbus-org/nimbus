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
package jp.ossc.nimbus.service.aop;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;

/**
 * �T�[�u���b�g�t�B���^�Ăяo���̃R���e�L�X�g���B<p>
 * {@link jp.ossc.nimbus.servlet.InterceptorChainCallFilter}����Ăяo���ꂽ{@link Interceptor}�ɓn�����R���e�L�X�g���ł���B<br>
 *
 * @author M.Takata
 */
public class ServletFilterInvocationContext
 extends DefaultMethodInvocationContext{
    
    private static final long serialVersionUID = 7531826445273995344L;
    
    private static final String METHOD_NAME = "doFilter";
    
    /**
     * �C���X�^���X�𐶐�����B<p>
     *
     * @param request ���N�G�X�g���
     * @param response ���X�|���X���
     * @param chain �t�B���^�`�F�[��
     */
    public ServletFilterInvocationContext(
        ServletRequest request,
        ServletResponse response,
        FilterChain chain
    ){
        setTargetObject(chain);
        try{
            setTargetMethod(
                FilterChain.class.getMethod(
                    METHOD_NAME,
                    new Class[]{
                        ServletRequest.class,
                        ServletResponse.class
                    }
                )
            );
        }catch(NoSuchMethodException e){
        }
        setParameters(new Object[]{request, response});
    }
    
    /**
     * �t�B���^�`�F�[�����擾����B<p>
     *
     * @return �t�B���^�`�F�[��
     */
    public FilterChain getFilterChain(){
        return (FilterChain)getTargetObject();
    }
    
    /**
     * ���N�G�X�g�����擾����B<p>
     *
     * @return ���N�G�X�g���
     */
    public ServletRequest getServletRequest(){
        return (ServletRequest)getParameters()[0];
    }
    
    /**
     * ���N�G�X�g����ݒ肷��B<p>
     *
     * @param request ���N�G�X�g���
     */
    public void setServletRequest(ServletRequest request){
        getParameters()[0] = request;
    }
    
    /**
     * ���X�|���X�����擾����B<p>
     *
     * @return ���X�|���X���
     */
    public ServletResponse getServletResponse(){
        return (ServletResponse)getParameters()[1];
    }
    
    /**
     * ���X�|���X����ݒ肷��B<p>
     *
     * @param response ���X�|���X���
     */
    public void setServletResponse(ServletResponse response){
        getParameters()[1] = response;
    }
    
    public String toString(){
        final StringBuilder buf = new StringBuilder(getClass().getName());
        buf.append('@').append(Integer.toHexString(hashCode()));
        buf.append('{');
        if(getServletRequest() != null){
            buf.append("remoteAddr=")
                .append(getServletRequest().getRemoteAddr());
            if(getServletRequest() instanceof HttpServletRequest){
                buf.append(",requestURL=")
                    .append(((HttpServletRequest)getServletRequest())
                        .getRequestURL());
            }
        }
        buf.append('}');
        return buf.toString();
    }
}
