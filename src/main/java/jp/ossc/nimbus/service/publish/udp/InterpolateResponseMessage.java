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

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectInput;

/**
 * サーバからの補完応答メッセージクラス。<p>
 * 
 * @author M.Takata
 */
public class InterpolateResponseMessage extends ServerMessage{
    
    private Map windowsByMessageId;
    private Map windowByWindowId;
    private List windows;
    
    public InterpolateResponseMessage(){
        super(MESSAGE_INTERPOLATE_RES);
    }
    
    public List getWindows(){
        return windows;
    }
    
    public void addWindow(Window window){
        if(windows == null){
            windows = new ArrayList();
        }
        windows.add(window);
    }
    
    public void addWindows(List windows){
        if(windows == null){
            return;
        }
        for(int i = 0; i < windows.size(); i++){
            addWindow((Window)windows.get(i));
        }
    }
    
    public List getWindows(MessageId id){
        return windowsByMessageId == null ? null : (List)windowsByMessageId.get(id);
    }
    
    public void addWindows(MessageId id, List windows){
        if(windowsByMessageId == null){
            windowsByMessageId = new HashMap();
        }
        windowsByMessageId.put(id, windows);
    }
    
    public Window getWindow(WindowId id){
        return windowByWindowId == null ? null : (Window)windowByWindowId.get(id);
    }
    
    public void addWindow(WindowId id, Window window){
        if(windowByWindowId == null){
            windowByWindowId = new HashMap();
        }
        windowByWindowId.put(id, window);
    }
    
    public void writeExternal(ObjectOutput out) throws IOException{
        super.writeExternal(out);
        if(windows == null){
            out.writeInt(0);
        }else{
            out.writeInt(windows.size());
            for(int i = 0, imax = windows.size(); i < imax; i++){
                ((Window)windows.get(i)).write(out);
            }
        }
        if(windowsByMessageId == null){
            out.writeInt(0);
        }else{
            out.writeInt(windowsByMessageId.size());
            Iterator entries = windowsByMessageId.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                MessageId id = (MessageId)entry.getKey();
                out.writeInt(id.sequence);
                List windows = (List)entry.getValue();
                out.writeInt(windows.size());
                for(int i = 0, imax = windows.size(); i < imax; i++){
                    ((Window)windows.get(i)).write(out);
                }
            }
        }
        if(windowByWindowId == null){
            out.writeInt(0);
        }else{
            out.writeInt(windowByWindowId.size());
            Iterator windows = windowByWindowId.values().iterator();
            while(windows.hasNext()){
                Window window = (Window)windows.next();
                window.write(out);
            }
        }
    }
    
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException{
        super.readExternal(in);
        int size = in.readInt();
        if(size != 0){
            for(int i = 0; i < size; i++){
                Window window = new Window();
                window.read(in);
                addWindow(window);
            }
        }
        size = in.readInt();
        if(size != 0){
            for(int i = 0; i < size; i++){
                MessageId id = new MessageId(in.readInt());
                int size2 = in.readInt();
                List windows = new ArrayList(size2);
                for(int j = 0; j < size2; j++){
                    Window window = new Window();
                    window.read(in);
                    windows.add(window);
                }
                addWindows(id, windows);
            }
        }
        size = in.readInt();
        if(size != 0){
            for(int i = 0; i < size; i++){
                Window window = new Window();
                window.read(in);
                addWindow(window, window);
            }
        }
    }
    
    public String toString(){
        StringBuffer buf = new StringBuffer(super.toString());
        buf.append('{');
        buf.append("windows=").append(windows);
        buf.append(", windowsByMessageId=").append(windowsByMessageId);
        buf.append(", windowByWindowId=").append(windowByWindowId);
        buf.append('}');
        return buf.toString();
    }
}