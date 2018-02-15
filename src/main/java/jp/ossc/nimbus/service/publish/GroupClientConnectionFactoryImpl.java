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
package jp.ossc.nimbus.service.publish;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.io.Serializable;
import java.rmi.RemoteException;

/**
 * {@link ClientConnectionFactory}をグルーピングするClientConnectionFactoryインタフェース実装クラス。<p>
 * 
 * @author M.Takata
 */
public class GroupClientConnectionFactoryImpl implements ClientConnectionFactory, Serializable{
    
    private static final long serialVersionUID = -8701616559793730652L;
    
    private Map factories  = new HashMap();
    
    public GroupClientConnectionFactoryImpl(){}
    
    public void addClientConnectionFactory(String subject, Pattern keyPattern, ClientConnectionFactory factory){
        List list = (List)factories.get(factory);
        if(list == null){
            list = new ArrayList();
            factories.put(factory, list);
        }
        list.add(new MessageKey(subject, keyPattern));
    }
    
    public ClientConnection getClientConnection() throws ConnectionCreateException, RemoteException{
        GroupClientConnectionImpl connection = new GroupClientConnectionImpl();
        
        final Iterator entries = factories.entrySet().iterator();
        while(entries.hasNext()){
            Map.Entry entry = (Map.Entry)entries.next();
            ClientConnectionFactory factory = (ClientConnectionFactory)entry.getKey();
            ClientConnection con = factory.getClientConnection();
            List list = (List)entry.getValue();
            for(int i = 0, imax = list.size(); i < imax; i++){
                MessageKey messageKey = (MessageKey)list.get(i);
                connection.addClientConnection(messageKey.subject, messageKey.keyPattern, con);
            }
        }
        return connection;
    }
    
    public int getClientCount() throws RemoteException{
        int result = 0;
        final Iterator factories = this.factories.keySet().iterator();
        while(factories.hasNext()){
            ClientConnectionFactory factory = (ClientConnectionFactory)factories.next();
            result += factory.getClientCount();
        }
        return result;
    }
    
    public static class MessageKey implements Serializable{
        private static final long serialVersionUID = 5276366941694918834L;
        
        public String subject;
        public Pattern keyPattern;
        public MessageKey(){}
        public MessageKey(String subject, Pattern keyPattern){
            this.subject = subject;
            this.keyPattern = keyPattern;
        }
    }
}