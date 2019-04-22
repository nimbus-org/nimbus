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
package jp.ossc.nimbus.service.publish.udp;

import java.io.IOException;
import java.io.DataOutput;
import java.io.DataInput;
import java.io.ObjectOutput;
import java.io.ObjectInput;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

import jp.ossc.nimbus.service.io.Externalizer;

/**
 * ウィンドウクラス。<p>
 * 
 * @author M.Takata
 */
public class Window extends WindowId{
    
    private static final int HEADER_LENGTH = 4 + 2 + 2 + 1 + 4;
    
    private short windowCount;
    private byte[] data;
    private List windows;
    private Set windowSet;
    private MessageImpl message;
    private long receiveTime;
    private boolean isLost;
    private boolean isFirst;
    
    public Window(){
    }
    
    public int getSequence(){
        return sequence;
    }
    
    public short getWindowCount(){
        return windowCount;
    }
    
    public void setWindowCount(short count){
        windowCount = count;
    }
    
    public short getWindowNo(){
        return windowNo;
    }
    
    public byte[] getData(){
        return data;
    }
    
    public long getReceiveTime(){
        return receiveTime;
    }
    
    public void setLost(boolean isLost){
        this.isLost = isLost;
    }
    
    public boolean isLost(){
        return isLost;
    }
    
    public void setFirst(boolean isFirst){
        this.isFirst = isFirst;
    }
    
    public boolean isFirst(){
        return isFirst;
    }
    
    public static List toWindows(MessageImpl message, ServerConnectionImpl sc, int windowSize) throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if(sc.externalizer == null){
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeByte(message instanceof MulticastMessageImpl ? 2 : 1);
            message.writeExternal(oos);
            oos.flush();
        }else{
            message.externalizer = sc.externalizer;
            ObjectOutput oo = sc.externalizer.createObjectOutput(baos);
            oo.writeByte(message instanceof MulticastMessageImpl ? 2 : 1);
            message.writeExternal(oo);
            oo.flush();
        }
        List result = new ArrayList();
        byte[] tmp = baos.toByteArray();
        int offset = 0;
        short no = 0;
        do{
            Window window = sc.createWindow();
            window.isFirst = message.isFirst();
            window.sequence = message.getSequence();
            window.windowNo = no;
            final int dataLength = Math.min(windowSize - HEADER_LENGTH, tmp.length - offset);
            window.data = new byte[dataLength];
            System.arraycopy(tmp, offset, window.data, 0, dataLength);
            result.add(window);
            offset += dataLength;
            no++;
        }while(tmp.length > offset);
        for(int i = 0, imax = result.size(); i < imax; i++){
            ((Window)result.get(i)).windowCount = (short)imax;
        }
        return result;
    }
    
    public synchronized boolean addWindow(Window window){
        if(isComplete()){
            return true;
        }
        if(windows == null){
            windows = new ArrayList(windowCount);
            windowSet = Collections.synchronizedSet(new HashSet(windowCount));
        }
        if(windows.size() == 0){
            windowSet.add(this);
            windows.add(this);
        }
        if(windowSet != null && windowSet.contains(window)){
            return false;
        }
        if(window.isLost()){
            isLost = true;
            return true;
        }
        windows.add(window);
        windowSet.add(window);
        if(windowCount <= windows.size()){
            Collections.sort(windows);
            return true;
        }else{
            return false;
        }
    }
    
    public boolean isComplete(){
        return windowCount == 1 || (windows != null && windowCount <= windows.size());
    }
    
    public List getWindows(){
        return windows;
    }
    
    public synchronized List getMissingWindowIds(List result){
        if(isComplete() || isLost()){
            return result;
        }
        if(result == null){
            result = new ArrayList();
        }
        if(windows == null){
            for(int i = 0; i < windowCount; i++){
                if(i != windowNo){
                    result.add(new WindowId(sequence, (short)i));
                }
            }
        }else{
            Collections.sort(windows);
            short currentNo = -1;
            for(int i = 0, imax = windows.size(); i < imax; i++){
                Window window = (Window)windows.get(i);
                if(window.windowNo - currentNo != 1){
                    for(int j = currentNo + 1; j < window.windowNo; j++){
                        result.add(new WindowId(sequence, (short)j));
                    }
                }
                currentNo = window.windowNo;
            }
            if(currentNo < windowCount - 1){
                for(int i = currentNo + 1; i < windowCount; i++){
                    result.add(new WindowId(sequence, (short)i));
                }
            }
        }
        return result;
    }
    
    public MessageImpl getMessage(ClientConnectionImpl cc) throws IOException, ClassNotFoundException{
        if(message != null){
            return message;
        }
        if(isLost()){
            message = cc.createMessage(1);
            message.setSequence(sequence);
            message.setLost(true);
            message.setReceiveTime(System.currentTimeMillis());
            return message;
        }
        if(!isComplete()){
            return null;
        }
        synchronized(this){
            if(message != null){
                return message;
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            long recTime = -1;
            if(windows == null || windows.size() == 0){
                baos.write(data);
                recTime = receiveTime;
            }else{
                for(int i = 0, imax = windows.size(); i < imax; i++){
                    Window w = (Window)windows.get(i);
                    if(recTime < w.getReceiveTime()){
                        recTime = w.getReceiveTime();
                    }
                    baos.write(w.data);
                }
            }
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            if(cc.externalizer == null){
                ObjectInputStream ois = new ObjectInputStream(bais);
                message = cc.createMessage(ois.readByte());
                message.readExternal(ois);
            }else{
                ObjectInput oi = cc.externalizer.createObjectInput(bais);
                message = cc.createMessage(oi.readByte());
                message.externalizer = cc.externalizer;
                message.readExternal(oi);
            }
            message.setReceiveTime(recTime);
        }
        return message;
    }
    
    public void write(DataOutput out) throws IOException{
        out.writeInt(sequence);
        out.writeShort(windowCount);
        out.writeShort(windowNo);
        out.writeBoolean(isFirst);
        out.writeInt(data == null ? 0 : data.length);
        if(data != null && data.length != 0){
            out.write(data);
        }
    }
    
    public void read(DataInput in) throws IOException{
        sequence = in.readInt();
        windowCount = in.readShort();
        windowNo = in.readShort();
        isFirst = in.readBoolean();
        int length = in.readInt();
        if(length != 0){
            data = new byte[length];
            in.readFully(data, 0, length);
        }
        receiveTime = System.currentTimeMillis();
    }
    
    public void clear(){
        super.clear();
        windowCount = 0;
        data = null;
        if(windows != null){
            windows.clear();
        }
        if(windowSet != null){
            windowSet.clear();
        }
        message = null;
        receiveTime = 0;
        isLost = false;
        isFirst = false;
    }
    
    public Object clone(){
        Window clone = null;
        try{
            clone = (Window)super.clone();
        }catch(CloneNotSupportedException e){
            return null;
        }
        if(windows != null){
            clone.windows = new ArrayList(windowCount);
            clone.windowSet = Collections.synchronizedSet(new HashSet(windowCount));
            for(int i = 0, imax = windows.size(); i < imax; i++){
                Window w = (Window)((Window)windows.get(i)).clone();
                clone.windows.add(w);
                clone.windowSet.add(w);
            }
        }
        message = null;
        return clone;
    }
    
    public String toString(){
        StringBuilder buf = new StringBuilder();
        buf.append(super.toString());
        buf.deleteCharAt(buf.length() - 1);
        buf.append(", windowCount=").append(windowCount);
        buf.append(", isLost=").append(isLost);
        buf.append(", isFirst=").append(isFirst);
        buf.append(", data=").append(data == null ? null : String.valueOf(data.length));
        buf.append('}');
        return buf.toString();
    }
}
