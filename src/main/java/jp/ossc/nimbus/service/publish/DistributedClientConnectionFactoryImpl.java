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

import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;
import java.rmi.RemoteException;

/**
 * {@link ClientConnectionFactory}を分散するClientConnectionFactoryインタフェース実装クラス。<p>
 * 
 * @author M.Takata
 */
public class DistributedClientConnectionFactoryImpl implements ClientConnectionFactory, Serializable{
    
    private static final long serialVersionUID = 1325341302957549196L;
    
    private List factories  = new ArrayList();
    
    public DistributedClientConnectionFactoryImpl(){}
    
    public void addClientConnectionFactory(ClientConnectionFactory factory){
        factories.add(factory);
    }
    
    public ClientConnection getClientConnection() throws ConnectionCreateException, RemoteException{
        DistributedClientConnectionImpl connection = new DistributedClientConnectionImpl();
        
        for(int i = 0, imax = factories.size(); i < imax; i++){
            ClientConnectionFactory factory = (ClientConnectionFactory)factories.get(i);
            connection.addClientConnection(factory.getClientConnection());
        }
        return connection;
    }
    
    public int getClientCount() throws RemoteException{
        int result = 0;
        for(int i = 0, imax = factories.size(); i < imax; i++){
            ClientConnectionFactory factory = (ClientConnectionFactory)factories.get(i);
            result += factory.getClientCount();
        }
        return result;
    }
}