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
package jp.ossc.nimbus.service.io;

import java.io.*;
import java.util.*;

import jp.ossc.nimbus.core.Utility;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.KryoException;

/**
 * Kryo直列化サービス。<p>
 * 
 * @author M.Takata
 */
public class KryoExternalizerService extends SerializableExternalizerService
 implements KryoExternalizerServiceMBean, Externalizer, Serializable{
    
    private static final long serialVersionUID = 4705570169021024055L;
    
    private Class[] registerClasses;
    private Properties defaultSerializerMapping;
    private Map defaultSerializerMap;
    
    private transient ThreadLocal kryoThreadLocal;
    
    public void setRegisterClasses(Class[] classes){
        registerClasses = classes;
    }
    public Class[] getRegisterClasses(){
        return registerClasses;
    }
    
    public void setDefaultSerializerMapping(Properties mapping){
        defaultSerializerMapping = mapping;
    }
    public Properties getDefaultSerializerMapping(){
        return defaultSerializerMapping;
    }
    
    /**
     * 特定のクラスの直列化を行うcom.esotericsoftware.kryo.Serializerのマッピングを設定する。<p>
     *
     * @param typeName 対象のクラス名
     * @param serializer 直列化を行うSerializer
     */
    public void setDefaultSerializer(String typeName, Serializer serializer) throws ClassNotFoundException{
        setDefaultSerializer(
            Utility.convertStringToClass(typeName),
            serializer
        );
    }
    
    /**
     * 特定のクラスの直列化を行うcom.esotericsoftware.kryo.Serializerのマッピングを設定する。<p>
     *
     * @param type 対象のクラス
     * @param serializer 直列化を行うSerializer
     */
    public void setDefaultSerializer(Class type, Serializer serializer) throws ClassNotFoundException{
        defaultSerializerMap.put(type, serializer);
    }
    
    public void setObjectOutputClass(Class clazz){
        throw new UnsupportedOperationException();
    }
    
    public void setObjectInputClass(Class clazz){
        throw new UnsupportedOperationException();
    }
    
    public void createService() throws Exception{
        super.createService();
        kryoThreadLocal = new KryoThreadLocal();
        defaultSerializerMap = new HashMap();
    }
    public void startService() throws Exception{
        super.startService();
        initKryo(new Kryo());
    }
    
    public void destroyService() throws Exception{
        kryoThreadLocal = null;
        defaultSerializerMap = null;
        super.destroyService();
    }
    
    public ObjectOutput createObjectOutput(OutputStream out) throws IOException{
        return new KryoObjectOutput(getKryo(), out);
    }
    public ObjectInput createObjectInput(InputStream in) throws IOException{
        return new KryoObjectInput(getKryo(), in);
    }
    
    private Kryo getKryo(){
        return (Kryo)kryoThreadLocal.get();
    }
    
    private void initKryo(Kryo kryo) throws Exception{
        if(registerClasses != null){
            for(int i = 0; i < registerClasses.length; i++){
                kryo.register(registerClasses[i]);
            }
        }
        if(defaultSerializerMapping != null){
            Iterator entries = defaultSerializerMapping.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                kryo.addDefaultSerializer(
                    Utility.convertStringToClass((String)entry.getKey()),
                    Utility.convertStringToClass((String)entry.getValue())
                );
            }
        }
        if(defaultSerializerMap != null && defaultSerializerMap.size() != 0){
            Iterator entries = defaultSerializerMap.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                kryo.addDefaultSerializer((Class)entry.getKey(), (Serializer)entry.getValue());
            }
        }
    }
    
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException{
        in.defaultReadObject();
        if(getState() >= CREATED && getState() != DESTROYED){
            kryoThreadLocal = new KryoThreadLocal();
        }
    }
    
    private class KryoThreadLocal extends ThreadLocal{
        protected Object initialValue(){
            Kryo kryo = new Kryo();
            try{
                initKryo(kryo);
            }catch(Exception e){}
            return kryo;
        }
    }
    
    public static class JavaExternalizableSerializer extends Serializer implements Serializable{
        
        private static final long serialVersionUID = -6997034498349804925L;
        
        public Object read(Kryo kryo, Input input, java.lang.Class type){
            try{
                Externalizable ext = (Externalizable)type.newInstance();
                ext.readExternal(new KryoObjectInput(kryo, input));
                return ext;
            }catch(Exception e){
                throw new KryoException("Error during Java deserialization.", e);
            }
        }
        
        public void write(Kryo kryo, Output output, Object object){
            try{
                Externalizable ext = (Externalizable)object;
                ext.writeExternal(new KryoObjectOutput(kryo, output));
            }catch(Exception e){
                throw new KryoException("Error during Java serialization.", e);
            }
        }
    }
    
    public static class JavaStandardSerializer extends Serializer implements Serializable{
        
        private static final long serialVersionUID = 3913136620807469067L;
        
        public Object read(Kryo kryo, Input input, java.lang.Class type){
            try{
                KryoObjectInput in = new KryoObjectInput(kryo, input);
                int length = in.readInt();
                byte[] bytes = new byte[length];
                in.readFully(bytes);
                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
                return ois.readObject();
            }catch(Exception e){
                throw new KryoException("Error during Java deserialization.", e);
            }
        }
        
        public void write(Kryo kryo, Output output, Object object){
            try{
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(object);
                oos.flush();
                byte[] bytes = baos.toByteArray();
                output.writeInt(bytes.length);
                output.writeBytes(bytes);
            }catch(Exception e){
                throw new KryoException("Error during Java serialization.", e);
            }
        }
    }
    
    private static class KryoObjectOutput implements ObjectOutput{
        private Kryo kryo;
        private Output output;
        public KryoObjectOutput(Kryo kryo, OutputStream os){
            this(kryo, new Output(os));
        }
        public KryoObjectOutput(Kryo kryo, Output out){
            this.kryo = kryo;
            output = out;
        }
        public void writeObject(Object obj) throws IOException{
            kryo.writeClassAndObject(output, obj);
        }
        public void write(int b) throws IOException{
            output.writeByte(b);
        }
        public void write(byte[] b) throws IOException{
            output.writeBytes(b);
        }
        public void write(byte[] b, int off, int len) throws IOException{
            output.writeBytes(b, off, len);
        }
        public void writeBoolean(boolean v) throws IOException{
            output.writeBoolean(v);
        }
        public void writeByte(int v) throws IOException{
            output.writeByte(v);
        }
        public void writeShort(int v) throws IOException{
            output.writeShort(v);
        }
        public void writeChar(int v) throws IOException{
            output.writeChar((char)v);
        }
        public void writeInt(int v) throws IOException{
            output.writeInt(v);
        }
        public void writeLong(long v) throws IOException{
            output.writeLong(v);
        }
        public void writeFloat(float v) throws IOException{
            output.writeFloat(v);
        }
        public void writeDouble(double v) throws IOException{
            output.writeDouble(v);
        }
        public void writeBytes(String s) throws IOException{
            for(int i = 0; i < s.length(); i++){
                output.writeByte((int)s.charAt(i));
            }
        }
        public void writeChars(String s) throws IOException{
            for(int i = 0; i < s.length(); i++){
                output.writeChar(s.charAt(i));
            }
        }
        public void writeUTF(String s) throws IOException{
            output.writeString(s);
        }
        public void flush() throws IOException{
            output.flush();
        }
        public void close() throws IOException{
            output.close();
        }
    }
    
    private static class KryoObjectInput implements ObjectInput{
        private Kryo kryo;
        private Input input;
        private DataInputStream din;
        public KryoObjectInput(Kryo kryo, InputStream is){
            this(kryo, new Input(is));
        }
        public KryoObjectInput(Kryo kryo, Input in){
            this.kryo = kryo;
            input = in;
            din = new DataInputStream(input);
        }
        public Object readObject() throws ClassNotFoundException, IOException{
            return kryo.readClassAndObject(input);
        }
        public int read() throws IOException{
            return input.readByte();
        }
        public byte readByte() throws IOException{
            return input.readByte();
        }
        public int readUnsignedByte() throws IOException{
            return input.readByteUnsigned();
        }
        public int read(byte[] b) throws IOException{
            return input.read(b);
        }
        public int read(byte[] b, int off, int len) throws IOException{
            return input.read(b, off, len);
        }
        public void readFully(byte[] b) throws IOException{
            readFully(b, 0, b.length);
        }
        public void readFully(byte[] b, int off, int len) throws IOException{
            int n = 0;
            while(n < len){
                int count = read(b, off + n, len - n);
                if(count < 0){
                    throw new EOFException();
                }
                n += count;
            }
        }
        public boolean readBoolean() throws IOException{
            return input.readBoolean();
        }
        public short readShort() throws IOException{
            return input.readShort();
        }
        public int readUnsignedShort() throws IOException{
            return input.readShortUnsigned();
        }
        public char readChar() throws IOException{
            return input.readChar();
        }
        public int readInt() throws IOException{
            return input.readInt();
        }
        public long readLong() throws IOException{
            return input.readLong();
        }
        public float readFloat() throws IOException{
            return input.readFloat();
        }
        public double readDouble() throws IOException{
            return input.readDouble();
        }
        public String readLine() throws IOException{
            return din.readLine();
        }
        public String readUTF() throws IOException{
            return input.readString();
        }
        public int skipBytes(int n) throws IOException{
            return (int)input.skip((long)n);
        }
        public long skip(long n) throws IOException{
            return input.skip(n);
        }
        public int available() throws IOException{
            return input.available();
        }
        public void close() throws IOException{
            input.close();
        }
    }
}