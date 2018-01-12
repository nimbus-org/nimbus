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

import java.io.Externalizable;
import java.io.ObjectOutput;
import java.io.ObjectInput;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.server.UID;

public class GlobalUID implements Externalizable, Comparable, Cloneable{
    
    private static final long serialVersionUID = -3680686440449795400L;
    protected UID uid;
    protected InetAddress address;
    
    public GlobalUID() throws UnknownHostException{
        this((InetAddress)null);
    }
    
    public GlobalUID(String localAddress) throws UnknownHostException{
        uid = new UID();
        if(localAddress == null){
            address = InetAddress.getLocalHost();
        }else{
            address = InetAddress.getByName(localAddress);
        }
    }
    
    public GlobalUID(InetAddress localAddress) throws UnknownHostException{
        uid = new UID();
        if(localAddress == null){
            address = InetAddress.getLocalHost();
        }else{
            address = localAddress;
        }
    }
    
    public UID getUID(){
        return uid;
    }
    
    public InetAddress getAddress(){
        return address;
    }
    
    public boolean equals(Object obj){
        if(!(obj instanceof GlobalUID)){
            return false;
        }
        GlobalUID cmp = (GlobalUID)obj;
        if(!uid.equals(cmp.uid)){
            return false;
        }
        if(!address.equals(cmp.address)){
            return false;
        }
        return true;
    }
    
    public int hashCode(){
        return uid.hashCode() + address.hashCode();
    }
    
    public String toString(){
        final StringBuffer buf = new StringBuffer();
        buf.append(address).append(':');
        buf.append(uid.toString());
        return buf.toString();
    }
    
    public int compareTo(Object obj){
        if(!(obj instanceof GlobalUID)){
            return -1;
        }
        GlobalUID cmp = (GlobalUID)obj;
        int result = address.toString().compareTo(cmp.address.toString());
        if(result != 0){
            return result;
        }
        return uid.toString().compareTo(cmp.uid.toString());
    }
    
    public Object clone(){
        try{
            return super.clone();
        }catch(CloneNotSupportedException e){
            return null;
        }
    }
    
    public void writeExternal(ObjectOutput out) throws IOException{
        out.writeObject(uid);
        out.writeObject(address);
    }
    
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException{
        uid = (UID)in.readObject();
        address = (InetAddress)in.readObject();
    }
}
