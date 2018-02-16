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
package jp.ossc.nimbus.util.net;

import java.util.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.Socket;
import java.net.InetAddress;

import jp.ossc.nimbus.beans.*;

/**
 * サーバソケット。<p>
 * このサーバソケットでaccept()されたソケットは、{@link #setSocketProperty(String, Object)}で、予め設定されたプロパティが設定される。<br>
 *
 * @author M.Takata
 * @see ServerSocketFactory
 */
public class ServerSocket extends java.net.ServerSocket{
    
    protected Map socketProperties;
    
    public ServerSocket() throws IOException{
        super();
    }
    
    public ServerSocket(int port) throws IOException{
        super(port);
    }
    
    public ServerSocket(int port, int backlog) throws IOException{
        super(port, backlog);
    }
    
    public ServerSocket(int port, int backlog, InetAddress bindAddr) throws IOException{
        super(port, backlog, bindAddr);
    }
    
    public Socket accept() throws IOException{
        return applySocketProperties(super.accept());
    }

    /**
     * {@link java.net.Socket}にプロパティを設定する。<p>
     *
     * @param props プロパティマップ
     */
    public void setSocketProperties(Map props){
        if(props == null || props.size() == 0){
            if(socketProperties != null){
                socketProperties = null;
            }
            return;
        }
        final Iterator names = props.keySet().iterator();
        while(names.hasNext()){
            String name = (String)names.next();
            setSocketProperty(name, props.get(name));
        }
    }
    
    /**
     * {@link Socket}にプロパティを設定する。<p>
     *
     * @param name プロパティ名
     * @param value 値
     */
    public void setSocketProperty(String name, Object value){
        if(socketProperties == null){
            socketProperties = new LinkedHashMap();
        }
        final Property prop = PropertyFactory.createProperty(name);
        socketProperties.put(prop, value);
    }
    
    /**
     * {@link Socket}のプロパティを取得する。<p>
     *
     * @param name プロパティ名
     * @return 値
     */
    public Object getSocketProperty(String name){
        if(socketProperties == null){
            return null;
        }
        final Iterator props = socketProperties.keySet().iterator();
        while(props.hasNext()){
            final Property prop = (Property)props.next();
            if(prop.getPropertyName().equals(name)){
                return socketProperties.get(prop);
            }
        }
        return null;
    }
    
    protected Socket applySocketProperties(
        Socket socket
    ) throws IOException{
        try{
            if(socketProperties != null && socketProperties.size() != 0){
                final Iterator props = socketProperties.keySet().iterator();
                while(props.hasNext()){
                    final Property prop = (Property)props.next();
                    prop.setProperty(socket, socketProperties.get(prop));
                }
            }
        }catch(InvocationTargetException e){
            Throwable target = e.getTargetException();
            if(target instanceof IOException){
                throw (IOException)target;
            }else if(target instanceof RuntimeException){
                throw (RuntimeException)target;
            }else if(target instanceof Error){
                throw (Error)target;
            }else{
                throw new UndeclaredThrowableException(target);
            }
        }catch(NoSuchPropertyException e){
            throw new UndeclaredThrowableException(e);
        }
        return socket;
    }
}