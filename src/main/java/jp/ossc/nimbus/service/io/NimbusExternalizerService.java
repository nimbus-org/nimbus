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

import sun.misc.Unsafe;
import sun.reflect.ReflectionFactory;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.lang.reflect.*;
import java.security.AccessController;

import jp.ossc.nimbus.util.ClassMappingTree;

/**
 * Nimbus直列化サービス。<p>
 * 
 * @author M.Takata
 */
public class NimbusExternalizerService extends SerializableExternalizerService
 implements NimbusExternalizerServiceMBean, Externalizer, Serializable{
    
    private static final long serialVersionUID = 8609142347326523361L;
    
    private static final int CHAR_BUF_SIZE = 256;
    
    private Class[] disabledNumberCompressionClasses;
    private Class[] immutableClasses;
    private boolean isUseNumberCompression = false;
    private int referenceTableInitialSize = 10;
    private float referenceTableExpandRatio = 3.0f;
    private float referenceTableLoadFactor = 3.0f;
    private boolean isUseIntReferenceTable = false;
    private boolean isUseLongReferenceTable = false;
    
    private transient ConcurrentMap metaClassMap;
    private transient ClassMappingTree disabledNumberCompressionClassMap;
    private transient Set immutableClassSet;
    
    public void setDisabledNumberCompressionClasses(Class[] classes){
        disabledNumberCompressionClasses = classes;
    }
    public Class[] getDisabledNumberCompressionClasses(){
        return disabledNumberCompressionClasses;
    }
    
    public void setImmutableClasses(Class[] classes){
        immutableClasses = classes;
    }
    public Class[] getImmutableClasses(){
        return immutableClasses;
    }
    
    public void setUseNumberCompression(boolean isUse){
        isUseNumberCompression = isUse;
    }
    public boolean isUseNumberCompression(){
        return isUseNumberCompression;
    }
    
    public void setUseIntReferenceTable(boolean isUse){
        isUseIntReferenceTable = isUse;
    }
    public boolean isUseIntReferenceTable(){
        return isUseIntReferenceTable;
    }
    
    public void setUseLongReferenceTable(boolean isUse){
        isUseLongReferenceTable = isUse;
    }
    public boolean isUseLongReferenceTable(){
        return isUseLongReferenceTable;
    }
    
    public void setReferenceTableInitialSize(int size){
        referenceTableInitialSize = size;
    }
    public int getReferenceTableInitialSize(){
        return referenceTableInitialSize;
    }
    
    public void setReferenceTableExpandRatio(float ratio){
        referenceTableExpandRatio = ratio;
    }
    public float getReferenceTableExpandRatio(){
        return referenceTableExpandRatio;
    }
    
    public void setReferenceTableLoadFactor(float factor){
        referenceTableLoadFactor = factor;
    }
    public float getReferenceTableLoadFactor(){
        return referenceTableLoadFactor;
    }
    
    public void createService() throws Exception{
        metaClassMap = new ConcurrentHashMap();
    }
    
    public void startService() throws Exception{
        initTransientFields();
    }
    
    public void stopService() throws Exception{
        metaClassMap.clear();
    }
    
    public void destroyService() throws Exception{
        disabledNumberCompressionClassMap = null;
        metaClassMap = null;
    }
    
    protected ObjectOutput createObjectOutput(OutputStream out) throws IOException{
        return new NimbusObjectOutputStream(out);
    }
    protected ObjectInput createObjectInput(InputStream in) throws IOException{
        return new NimbusObjectInputStream(in);
    }
    
    private void initTransientFields(){
        if(disabledNumberCompressionClasses != null && disabledNumberCompressionClasses.length != 0){
            disabledNumberCompressionClassMap = new ClassMappingTree();
            for(int i = 0; i < disabledNumberCompressionClasses.length; i++){
                disabledNumberCompressionClassMap.add(disabledNumberCompressionClasses[i], disabledNumberCompressionClasses[i]);
            }
        }
        immutableClassSet = new HashSet();
        immutableClassSet.add(String.class);
        immutableClassSet.add(Byte.class);
        immutableClassSet.add(Short.class);
        immutableClassSet.add(Integer.class);
        immutableClassSet.add(Long.class);
        immutableClassSet.add(Float.class);
        immutableClassSet.add(Double.class);
        immutableClassSet.add(Boolean.class);
        immutableClassSet.add(Character.class);
        immutableClassSet.add(java.math.BigInteger.class);
        immutableClassSet.add(java.math.BigDecimal.class);
        immutableClassSet.add(java.util.Locale.class);
        immutableClassSet.add(java.util.UUID.class);
        if(immutableClasses != null && immutableClasses.length != 0){
            for(int i = 0; i < immutableClasses.length; i++){
                immutableClassSet.add(immutableClasses[i]);
            }
        }
    }
    
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException{
        in.defaultReadObject();
        if(getState() >= CREATED && getState() != DESTROYED){
            metaClassMap = new ConcurrentHashMap();
        }
        initTransientFields();
    }
    
    protected MetaClass findMetaClass(Class clazz) throws IOException{
        MetaClass metaClass = (MetaClass)metaClassMap.get(clazz);
        if(metaClass == null){
            synchronized(clazz){
                metaClass = (MetaClass)metaClassMap.get(clazz);
                if(metaClass == null){
                    boolean isDisabledNumberCompression = !isUseNumberCompression;
                    if(!isDisabledNumberCompression && disabledNumberCompressionClassMap != null){
                        isDisabledNumberCompression = disabledNumberCompressionClassMap.getValueList(clazz).size() != 0;
                    }
                    metaClass = new MetaClass(this, clazz, immutableClassSet.contains(clazz), isDisabledNumberCompression);
                    metaClassMap.putIfAbsent(clazz, metaClass);
                }
            }
        }
        return metaClass;
    }
    
    public class NimbusObjectOutputStream extends ObjectOutputStream{
        
        private final char[] cbuf = new char[CHAR_BUF_SIZE];
        private final OutputStream os;
        private ReferenceTable classNameTable = new ReferenceTable();
        private ReferenceTable referenceTable = new ReferenceTable(referenceTableInitialSize, referenceTableExpandRatio, referenceTableLoadFactor);
        private ReferenceTable immutableReferenceTable = new EqualsReferenceTable(referenceTableInitialSize, referenceTableExpandRatio, referenceTableLoadFactor);
        
        private IntReferenceTable intReferenceTable = new IntReferenceTable(referenceTableInitialSize, referenceTableExpandRatio, referenceTableLoadFactor);
        private LongReferenceTable longReferenceTable = new LongReferenceTable(referenceTableInitialSize, referenceTableExpandRatio, referenceTableLoadFactor);
        
        private Stack currentObjectStack = new Stack();
        private MetaClass currentMetaClass;
        
        public NimbusObjectOutputStream(OutputStream os) throws IOException{
            super();
            this.os = os;
        }
        
        protected int registerClassName(String className){
            return classNameTable.assign(className);
        }
        
        protected int registerReference(MetaClass metaClass, Object obj){
            if(metaClass.isImmutable()){
                return immutableReferenceTable.assign(obj);
            }else{
                return referenceTable.assign(obj);
            }
        }
        
        public void defaultWriteObject() throws IOException{
            Object currentObject = currentObjectStack.peek();
            currentMetaClass.defaultWriteObject(currentObject, this);
        }
        protected void writeObjectOverride(Object obj) throws IOException{
            MetaClass preMetaClass = currentMetaClass;
            currentObjectStack.push(obj);
            try{
                MetaClass.writeClass(obj, this);
                if(obj != null){
                    currentMetaClass = findMetaClass(obj.getClass());
                    currentMetaClass.writeObject(obj, this);
                }
            }finally{
                currentMetaClass = preMetaClass;
                currentObjectStack.pop();
            }
        }
        public void write(int b) throws IOException{
            os.write(b);
        }
        public void write(byte[] b) throws IOException{
            os.write(b);
        }
        public void write(byte[] b, int off, int len) throws IOException{
            os.write(b, off, len);
        }
        public void writeBoolean(boolean v) throws IOException{
            os.write(v ? 1 : 0);
        }
        public void writeByte(int v) throws IOException{
            os.write(v);
        }
        public void writeShort(int v) throws IOException{
            os.write((byte)(v >>> 8));
            os.write((byte)v);
        }
        public void writeChar(int v) throws IOException{
            os.write((byte)(v >>> 8));
            os.write((byte)v);
        }
        public void writeInt(int v) throws IOException{
            writeInt(v, currentMetaClass != null && currentMetaClass.isDisabledNumberCompression(), isUseIntReferenceTable);
        }
        protected void writeInt(int v, boolean isDisabledNumberCompression, boolean isUseReferenceTable) throws IOException{
            final int referenceId = isUseReferenceTable ? intReferenceTable.assign(v) : 1;
            if(referenceId > 0){
                if(isUseReferenceTable){
                    os.write(MetaClass.TAG_VALUE);
                    writeInt(referenceId, isDisabledNumberCompression, false);
                }
                if(isDisabledNumberCompression){
                    os.write((byte)(v >>> 24));
                    os.write((byte)(v >>> 16));
                    os.write((byte)(v >>> 8));
                    os.write((byte)(v));
                }else{
                    if(v == Integer.MIN_VALUE){
                        os.write((byte)0);
                    }else if(v >= Byte.MIN_VALUE && v <= Byte.MAX_VALUE){
                        os.write((byte)1);
                        os.write((byte)v);
                    }else if(v >= Short.MIN_VALUE && v <= Short.MAX_VALUE){
                        os.write((byte)2);
                        os.write((byte)(v >>> 8));
                        os.write((byte)v);
                    }else{
                        os.write((byte)3);
                        os.write((byte)(v >>> 24));
                        os.write((byte)(v >>> 16));
                        os.write((byte)(v >>> 8));
                        os.write((byte)(v));
                    }
                }
            }else{
                os.write(MetaClass.TAG_REFERENCE);
                writeInt(-referenceId, isDisabledNumberCompression, false);
            }
        }
        public void writeLong(long v) throws IOException{
            writeLong(v, currentMetaClass != null && currentMetaClass.isDisabledNumberCompression(), isUseLongReferenceTable);
        }
        protected void writeLong(long v, boolean isDisabledNumberCompression, boolean isUseReferenceTable) throws IOException{
            final int referenceId = isUseReferenceTable ? longReferenceTable.assign(v) : 1;
            if(referenceId > 0){
                if(isUseReferenceTable){
                    os.write(MetaClass.TAG_VALUE);
                    writeInt(referenceId, isDisabledNumberCompression, false);
                }
                if(isDisabledNumberCompression){
                    os.write((byte)(v >>> 56));
                    os.write((byte)(v >>> 48));
                    os.write((byte)(v >>> 40));
                    os.write((byte)(v >>> 32));
                    os.write((byte)(v >>> 24));
                    os.write((byte)(v >>> 16));
                    os.write((byte)(v >>> 8));
                    os.write((byte)v);
                }else{
                    if(v == Long.MIN_VALUE){
                        os.write((byte)0);
                    }else if(v >= Byte.MIN_VALUE && v <= Byte.MAX_VALUE){
                        os.write((byte)1);
                        os.write((byte)v);
                    }else if(v >= Short.MIN_VALUE && v <= Short.MAX_VALUE){
                        os.write((byte)2);
                        os.write((byte)(v >>> 8));
                        os.write((byte)v);
                    }else if(v >= Integer.MIN_VALUE && v <= Integer.MAX_VALUE){
                        os.write((byte)3);
                        os.write((byte)(v >>> 24));
                        os.write((byte)(v >>> 16));
                        os.write((byte)(v >>> 8));
                        os.write((byte)(v));
                    }else{
                        os.write((byte)4);
                        os.write((byte)(v >>> 56));
                        os.write((byte)(v >>> 48));
                        os.write((byte)(v >>> 40));
                        os.write((byte)(v >>> 32));
                        os.write((byte)(v >>> 24));
                        os.write((byte)(v >>> 16));
                        os.write((byte)(v >>> 8));
                        os.write((byte)v);
                    }
                }
            }else{
                os.write(MetaClass.TAG_REFERENCE);
                writeInt(-referenceId, isDisabledNumberCompression, false);
            }
        }
        public void writeFloat(float v) throws IOException{
            writeInt(Float.floatToIntBits(v), true, isUseIntReferenceTable);
        }
        public void writeDouble(double v) throws IOException{
            writeLong(Double.doubleToLongBits(v), true, isUseLongReferenceTable);
        }
        public void writeBytes(String s) throws IOException{
            for(int i = 0; i < s.length(); i++){
                writeByte(s.charAt(i));
            }
        }
        public void writeChars(String s) throws IOException{
            for(int i = 0; i < s.length(); i++){
                writeChar(s.charAt(i));
            }
        }
        public void writeUTF(String s) throws IOException{
            writeUTF(s, getUTFLength(s));
        }
        private void writeUTF(String s, long utflen) throws IOException{
            if(utflen > 0xFFFFL){
                throw new UTFDataFormatException();
            }
            writeShort((int)utflen);
            if(utflen == (long) s.length()){
                writeBytes(s);
            }else{
                writeUTFBody(s);
            }
        }
        public void writeLongUTF(String s) throws IOException{
            writeLongUTF(s, getUTFLength(s));
        }
        public void writeLongUTF(String s, long utflen) throws IOException{
            writeLong(utflen, true, false);
            if(utflen == (long)s.length()){
                writeBytes(s);
            }else{
                writeUTFBody(s);
            }
        }
        private void writeUTFBody(String s) throws IOException{
            int len = s.length();
            for(int off = 0; off < len; ){
                int csize = Math.min(len - off, CHAR_BUF_SIZE);
                s.getChars(off, off + csize, cbuf, 0);
                for(int cpos = 0; cpos < csize; cpos++){
                    char c = cbuf[cpos];
                    if(c <= 0x007F && c != 0){
                        os.write(c);
                    }else if(c > 0x07FF){
                        os.write(0xE0 | ((c >> 12) & 0x0F));
                        os.write(0x80 | ((c >> 6) & 0x3F));
                        os.write(0x80 | ((c >> 0) & 0x3F));
                    }else{
                        os.write(0xC0 | ((c >> 6) & 0x1F));
                        os.write(0x80 | ((c >> 0) & 0x3F));
                    }
                }
                off += csize;
            }
        }
        public void writeString(String s) throws IOException{
            final long utflen = getUTFLength(s);
            if(utflen <= 0xFFFF){
                os.write((byte)1);
                writeUTF(s, utflen);
            }else{
                os.write((byte)2);
                writeLongUTF(s, utflen);
            }
        }
        
        private long getUTFLength(String s){
            int len = s.length();
            long utflen = 0;
            for(int off = 0; off < len; ){
                int csize = Math.min(len - off, CHAR_BUF_SIZE);
                s.getChars(off, off + csize, cbuf, 0);
                for(int cpos = 0; cpos < csize; cpos++){
                    char c = cbuf[cpos];
                    if(c >= 0x0001 && c <= 0x007F){
                        utflen++;
                    }else if (c > 0x07FF){
                        utflen += 3;
                    }else{
                        utflen += 2;
                    }
                }
                off += csize;
            }
            return utflen;
        }
        
        public void flush() throws IOException{
            os.flush();
        }
        
        public void close() throws IOException{
            os.flush();
            os.close();
        }
    }
    
    public class NimbusObjectInputStream extends ObjectInputStream{
        
        private InputStream is;
        private DataInputStream din;
        private ReferenceTable classNameTable = new ReferenceTable();
        private ReferenceTable referenceTable = new ReferenceTable(referenceTableInitialSize, referenceTableExpandRatio, referenceTableLoadFactor);
        private ReferenceTable immutableReferenceTable = new EqualsReferenceTable(referenceTableInitialSize, referenceTableExpandRatio, referenceTableLoadFactor);
        private IntReferenceTable intReferenceTable = isUseIntReferenceTable ? new IntReferenceTable(referenceTableInitialSize, referenceTableExpandRatio, referenceTableLoadFactor) : null;
        private LongReferenceTable longReferenceTable = isUseLongReferenceTable ? new LongReferenceTable(referenceTableInitialSize, referenceTableExpandRatio, referenceTableLoadFactor) : null;
        private Stack currentObjectStack = new Stack();
        private MetaClass currentMetaClass;
        
        public NimbusObjectInputStream(InputStream is) throws IOException{
            super();
            
            this.is = is;
            din = new DataInputStream(this);
        }
        protected Class registerClassName(int id, String className) throws ClassNotFoundException{
            Class clazz = Class.forName(className, false, Thread.currentThread().getContextClassLoader());
            classNameTable.insert(clazz, id);
            return clazz;
        }
        protected Class lookupClass(int id){
            return(Class)classNameTable.getReference(id);
        }
        protected void registerReference(MetaClass metaClass, int id, Object reference){
            if(metaClass.isImmutable()){
                immutableReferenceTable.insert(reference, id);
            }else{
                referenceTable.insert(reference, id);
            }
        }
        protected Object lookupReference(MetaClass metaClass, int id){
            if(metaClass.isImmutable()){
                return immutableReferenceTable.getReference(id);
            }else{
                return referenceTable.getReference(id);
            }
        }
        protected Object readObjectOverride() throws IOException, ClassNotFoundException{
            final Class clazz = MetaClass.readClass(this, !isUseNumberCompression);
            if(clazz == null){
                return null;
            }
            final byte instanceType = MetaClass.readInstanceType(this);
            MetaClass metaClass = findMetaClass(clazz);
            if(MetaClass.isReference(instanceType)){
                return metaClass.getReference(this);
            }else{
                Object obj = null;
                try{
                    obj = metaClass.newInstance(this);
                }catch(Exception e){
                    throw (IOException) new InvalidClassException(
                        clazz.getName(),
                        "unable to create instance"
                    ).initCause(e);
                }
                return readObjectOverrideInternal(metaClass, obj);
            }
        }
        protected Object readObjectOverrideInternal(MetaClass metaClass, Object obj) throws IOException, ClassNotFoundException{
            MetaClass preMetaClass = currentMetaClass;
            try{
                currentObjectStack.push(obj);
                currentMetaClass = metaClass;
                obj = metaClass.readObject(obj, this);
                return obj;
            }finally{
                currentMetaClass = preMetaClass;
                currentObjectStack.pop();
            }
        }
        public void defaultReadObject() throws IOException, ClassNotFoundException{
            Object currentObject = currentObjectStack.peek();
            currentMetaClass.defaultReadObject(currentObject, this);
        }
        public int read() throws IOException{
            return is.read();
        }
        public byte readByte() throws IOException{
            final int v = is.read();
            if(v < 0){
                throw new EOFException();
            }
            return (byte)v;
        }
        public int readUnsignedByte() throws IOException{
            final int v = is.read();
            if(v < 0){
                throw new EOFException();
            }
            return v;
        }
        public int read(byte[] b) throws IOException{
            return is.read(b);
        }
        public int read(byte[] b, int off, int len) throws IOException{
            return is.read(b, off, len);
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
            return (readUnsignedByte() != 0);
        }
        public short readShort() throws IOException{
            final int v1 = readUnsignedByte();
            final int v2 = readUnsignedByte();
            return (short)((v2 & 0xFF) + (v1 << 8));
        }
        public int readUnsignedShort() throws IOException{
            return readShort() & 0xFFFF;
        }
        public char readChar() throws IOException{
            final int v1 = readUnsignedByte();
            final int v2 = readUnsignedByte();
            return (char)((v2 & 0xFF) + (v1 << 8));
        }
        public int readInt() throws IOException{
            return readInt(currentMetaClass != null && currentMetaClass.isDisabledNumberCompression(), isUseIntReferenceTable);
        }
        protected int readInt(boolean isDisabledNumberCompression, boolean isUseReferenceTable) throws IOException{
            int referenceId = 0;
            if(isUseReferenceTable){
                final byte tag = readByte();
                referenceId = readInt(isDisabledNumberCompression, false);
                switch(tag){
                case MetaClass.TAG_REFERENCE:
                    return intReferenceTable.getReference(referenceId);
                case MetaClass.TAG_VALUE:
                    break;
                }
            }
            int result = 0;
            int v1 = 0;
            int v2 = 0;
            int v3 = 0;
            int v4 = 0;
            if(isDisabledNumberCompression){
                v1 = readUnsignedByte();
                v2 = readUnsignedByte();
                v3 = readUnsignedByte();
                v4 = readUnsignedByte();
                result = ((v4 & 0xFF)      )
                       + ((v3 & 0xFF) <<  8)
                       + ((v2 & 0xFF) << 16)
                       + ((v1       ) << 24);
            }else{
                final int type = readUnsignedByte();
                switch(type){
                case 0:
                    result = Integer.MIN_VALUE;
                    break;
                case 1:
                    result = readUnsignedByte();
                    break;
                case 2:
                    v1 = readUnsignedByte();
                    v2 = readUnsignedByte();
                    result = (v2 & 0xFF) + (v1 << 8);
                    break;
                case 3:
                    v1 = readUnsignedByte();
                    v2 = readUnsignedByte();
                    v3 = readUnsignedByte();
                    v4 = readUnsignedByte();
                    result = ((v4 & 0xFF)      )
                           + ((v3 & 0xFF) <<  8)
                           + ((v2 & 0xFF) << 16)
                           + ((v1       ) << 24);
                    break;
                default:
                    throw new StreamCorruptedException("Invalid number type." + type);
                }
            }
            if(isUseReferenceTable){
                intReferenceTable.insert(result, referenceId);
            }
            return result;
        }
        public long readLong() throws IOException{
            return readLong(currentMetaClass != null && currentMetaClass.isDisabledNumberCompression(), isUseLongReferenceTable);
        }
        protected long readLong(boolean isDisabledNumberCompression, boolean isUseReferenceTable) throws IOException{
            int referenceId = 0;
            if(isUseReferenceTable){
                final byte tag = readByte();
                referenceId = readInt(isDisabledNumberCompression, false);
                switch(tag){
                case MetaClass.TAG_REFERENCE:
                    return longReferenceTable.getReference(referenceId);
                case MetaClass.TAG_VALUE:
                    break;
                }
            }
            long result = 0l;
            int v1 = 0;
            int v2 = 0;
            int v3 = 0;
            int v4 = 0;
            int v5 = 0;
            int v6 = 0;
            int v7 = 0;
            int v8 = 0;
            if(isDisabledNumberCompression){
                    v1 = readUnsignedByte();
                    v2 = readUnsignedByte();
                    v3 = readUnsignedByte();
                    v4 = readUnsignedByte();
                    v5 = readUnsignedByte();
                    v6 = readUnsignedByte();
                    v7 = readUnsignedByte();
                    v8 = readUnsignedByte();
                    result = ((v8 & 0xFFL)      )
                           + ((v7 & 0xFFL) <<  8)
                           + ((v6 & 0xFFL) << 16)
                           + ((v5 & 0xFFL) << 24)
                           + ((v4 & 0xFFL) << 32)
                           + ((v3 & 0xFFL) << 40)
                           + ((v2 & 0xFFL) << 48)
                           + (((long) v1 ) << 56);
            }else{
                final int type = readUnsignedByte();
                switch(type){
                case 0:
                    result = Long.MIN_VALUE;
                    break;
                case 1:
                    result = readUnsignedByte();
                    break;
                case 2:
                    v1 = readUnsignedByte();
                    v2 = readUnsignedByte();
                    result = (v2 & 0xFF) + (v1 << 8);
                    break;
                case 3:
                    v1 = readUnsignedByte();
                    v2 = readUnsignedByte();
                    v3 = readUnsignedByte();
                    v4 = readUnsignedByte();
                    result = ((v4 & 0xFF)      )
                           + ((v3 & 0xFF) <<  8)
                           + ((v2 & 0xFF) << 16)
                           + ((v1       ) << 24);
                    break;
                case 4:
                    v1 = readUnsignedByte();
                    v2 = readUnsignedByte();
                    v3 = readUnsignedByte();
                    v4 = readUnsignedByte();
                    v5 = readUnsignedByte();
                    v6 = readUnsignedByte();
                    v7 = readUnsignedByte();
                    v8 = readUnsignedByte();
                    result = ((v8 & 0xFFL)      )
                           + ((v7 & 0xFFL) <<  8)
                           + ((v6 & 0xFFL) << 16)
                           + ((v5 & 0xFFL) << 24)
                           + ((v4 & 0xFFL) << 32)
                           + ((v3 & 0xFFL) << 40)
                           + ((v2 & 0xFFL) << 48)
                           + (((long) v1 ) << 56);
                    break;
                default:
                    throw new StreamCorruptedException("Invalid number type." + type);
                }
            }
            if(isUseReferenceTable){
                longReferenceTable.insert(result, referenceId);
            }
            return result;
        }
        public float readFloat() throws IOException{
            return Float.intBitsToFloat(readInt(true, isUseIntReferenceTable));
        }
        public double readDouble() throws IOException{
            return Double.longBitsToDouble(readLong(true, isUseLongReferenceTable));
        }
        public String readLine() throws IOException{
            return din.readLine();
        }
        public String readUTF() throws IOException{
            return readUTFBody(readUnsignedShort());
        }
        public String readLongUTF() throws IOException{
            return readUTFBody(readLong(true, false));
        }
        private String readUTFBody(long utflen) throws IOException{
            StringBuilder sbuf = new StringBuilder();
            while(utflen > 0){
                int b1, b2, b3;
                b1 = readUnsignedByte() & 0xFF;
                utflen--;
                char c = 0;
                switch(b1 >> 4){
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                    c = (char) b1;
                    break;
                case 12:
                case 13:
                    b2 = readUnsignedByte();
                    utflen--;
                    if((b2 & 0xC0) != 0x80){
                        throw new UTFDataFormatException();
                    }
                    c = (char)(((b1 & 0x1F) << 6) | ((b2 & 0x3F) << 0));
                    break;
                case 14:
                    b2 = readUnsignedByte();
                    b3 = readUnsignedByte();
                    utflen-=2;
                    if((b2 & 0xC0) != 0x80 || (b3 & 0xC0) != 0x80){
                        throw new UTFDataFormatException();
                    }
                    c = (char) (((b1 & 0x0F) << 12)
                              | ((b2 & 0x3F) << 6)
                              | ((b3 & 0x3F) << 0));
                    break;
                default:
                    throw new UTFDataFormatException();
                }
                sbuf.append(c);
            }
            return sbuf.toString();
        }
        
        public String readString() throws IOException {
            String str = null;
            byte tc = readByte();
            switch(tc){
            case 1:
                str = readUTF();
                break;
            case 2:
                str = readLongUTF();
                break;
            default:
                throw new StreamCorruptedException("Invalid type code." + tc);
            }
            return str;
        }
        
        public int skipBytes(int n) throws IOException{
            return (int)is.skip((long)n);
        }
        public long skip(long n) throws IOException{
            return is.skip(n);
        }
        public int available() throws IOException{
            return is.available();
        }
        public void close() throws IOException{
            is.close();
        }
    }
    
    private static class MetaClass{
        
        private static final ReflectionFactory reflFactory = (ReflectionFactory)AccessController.doPrivileged(new ReflectionFactory.GetReflectionFactoryAction());
        private static final ObjectStreamField[] NO_FIELDS = new ObjectStreamField[0];
        
        protected static final byte TAG_NULL = (byte)0;
        protected static final byte TAG_REFERENCE = (byte)1;
        protected static final byte TAG_VALUE = (byte)2;
        protected static final byte TAG_ARRAY = (byte)3;
        
        private NimbusExternalizerService externalizer;
        private MetaClass superMetaClass;
        private Class clazz;
        
        private boolean isExternalizable;
        private Constructor constructor;
        private FieldReflector fieldReflector;
        private Method writeObjectMethod;
        private Method readObjectMethod;
        private Method readObjectNoDataMethod;
        private Method writeReplaceMethod;
        private Method readResolveMethod;
        private boolean isImmutable;
        private boolean isDisabledNumberCompression;
        private List classTree;
        
        public MetaClass(
            NimbusExternalizerService externalizer,
            Class clazz,
            boolean isImmutable,
            boolean isDisabledNumberCompression
        ) throws IOException{
            this.externalizer = externalizer;
            this.clazz = clazz;
            if(clazz.isArray()){
                return;
            }
            if(Proxy.isProxyClass(clazz)){
                throw new IOException("Proxy is not supported. " + clazz.getName());
            }
            Class superClass = clazz.getSuperclass();
            if(superClass != null && Serializable.class.isAssignableFrom(superClass)){
                superMetaClass = externalizer.findMetaClass(superClass);
            }
            if(Externalizable.class.isAssignableFrom(clazz)){
                isExternalizable = true;
                constructor = getExternalizableConstructor(clazz);
            }else if(Serializable.class.isAssignableFrom(clazz)){
                constructor = getSerializableConstructor(clazz);
                writeObjectMethod = getPrivateMethod(
                    clazz,
                    "writeObject",
                    new Class[]{ObjectOutputStream.class},
                    Void.TYPE
                );
                readObjectMethod = getPrivateMethod(
                    clazz,
                    "readObject",
                    new Class[]{ObjectInputStream.class},
                    Void.TYPE
                );
                readObjectNoDataMethod = getPrivateMethod(
                    clazz,
                    "readObjectNoData",
                    null,
                    Void.TYPE
                );
                fieldReflector = new FieldReflector(externalizer, clazz, getSerialFields(clazz));
            }
            writeReplaceMethod = getInheritableMethod(clazz, "writeReplace", null, Object.class);
            readResolveMethod = getInheritableMethod(clazz, "readResolve", null, Object.class);
            this.isImmutable = isImmutable;
            this.isDisabledNumberCompression = isDisabledNumberCompression;
            if(!String.class.equals(clazz) && !isExternalizable){
                MetaClass metaClass = this;
                while(metaClass.superMetaClass != null){
                    if(classTree == null){
                        classTree = new ArrayList();
                    }
                    classTree.add(metaClass.superMetaClass);
                    metaClass = metaClass.superMetaClass;
                }
            }
        }
        
        public boolean isImmutable(){
            return isImmutable;
        }
        
        public boolean isDisabledNumberCompression(){
            return isDisabledNumberCompression;
        }
        
        public static void writeClass(Object obj, NimbusObjectOutputStream oos) throws IOException{
            if(obj == null){
                oos.write(TAG_NULL);
                return;
            }
            final String className = obj.getClass().getName();
            final int classNameId = oos.registerClassName(className);
            if(classNameId > 0){
                oos.write(TAG_VALUE);
                oos.writeInt(classNameId, false, false);
                oos.writeString(className);
            }else{
                oos.write(TAG_REFERENCE);
                oos.writeInt(-classNameId, false, false);
            }
        }
        public void writeObject(Object obj, NimbusObjectOutputStream oos) throws IOException{
            final int referenceId = oos.registerReference(this, obj);
            if(referenceId > 0){
                Class objClass = obj.getClass();
                if(objClass.isArray()){
                    oos.write(TAG_ARRAY);
                    final int length = Array.getLength(obj);
                    oos.writeInt(length, isDisabledNumberCompression, false);
                    oos.writeInt(referenceId, isDisabledNumberCompression, false);
                    objClass = objClass.getComponentType();
                    if(objClass.isPrimitive()){
                        if(Byte.TYPE.equals(objClass)){
                            for(int i = 0; i < length; i++){
                                oos.write(Array.getByte(obj, i));
                            }
                        }else if(Short.TYPE.equals(objClass)){
                            for(int i = 0; i < length; i++){
                                oos.writeShort(Array.getShort(obj, i));
                            }
                        }else if(Integer.TYPE.equals(objClass)){
                            for(int i = 0; i < length; i++){
                                oos.writeInt(Array.getInt(obj, i), isDisabledNumberCompression, externalizer.isUseIntReferenceTable);
                            }
                        }else if(Long.TYPE.equals(objClass)){
                            for(int i = 0; i < length; i++){
                                oos.writeLong(Array.getLong(obj, i), isDisabledNumberCompression, externalizer.isUseLongReferenceTable);
                            }
                        }else if(Float.TYPE.equals(objClass)){
                            for(int i = 0; i < length; i++){
                                oos.writeFloat(Array.getFloat(obj, i));
                            }
                        }else if(Double.TYPE.equals(objClass)){
                            for(int i = 0; i < length; i++){
                                oos.writeDouble(Array.getDouble(obj, i));
                            }
                        }else if(Boolean.TYPE.equals(objClass)){
                            for(int i = 0; i < length; i++){
                                oos.writeBoolean(Array.getBoolean(obj, i));
                            }
                        }
                    }else{
                        for(int i = 0; i < length; i++){
                            Object element = Array.get(obj, i);
                            oos.writeObject(element);
                        }
                    }
                }else{
                    if(writeReplaceMethod != null){
                        obj = invokeWriteReplace(obj);
                        if(obj == null){
                            oos.write(TAG_NULL);
                            return;
                        }else if(!clazz.equals(obj.getClass())){
                            oos.writeObject(obj);
                            return;
                        }
                    }
                    oos.write(TAG_VALUE);
                    oos.writeInt(referenceId, isDisabledNumberCompression, false);
                    
                    if(obj instanceof String){
                        oos.writeString((String)obj);
                    }else if(isExternalizable){
                        ((Externalizable)obj).writeExternal(oos);
                    }else{
                        if(classTree != null){
                            for(int i = classTree.size(); --i >= 0;){
                                MetaClass metaClass = (MetaClass)classTree.get(i);
                                if(metaClass.writeObjectMethod != null){
                                    metaClass.invokeWriteObject(obj, oos);
                                }else{
                                    metaClass.defaultWriteObject(obj, oos);
                                }
                            }
                        }
                        
                        if(writeObjectMethod != null){
                            invokeWriteObject(obj, oos);
                        }else{
                            oos.defaultWriteObject();
                        }
                    }
                }
            }else{
                oos.write(TAG_REFERENCE);
                oos.writeInt(-referenceId, isDisabledNumberCompression, false);
            }
        }
        
        public void defaultWriteObject(Object obj, NimbusObjectOutputStream oos) throws IOException{
            if(fieldReflector == null){
                return;
            }
            fieldReflector.writeFields(obj, oos);
        }
        
        public static Class readClass(NimbusObjectInputStream ois, boolean isDisabledNumberCompression) throws IOException, ClassNotFoundException{
            final byte tag = ois.readByte();
            int classNameId = 0;
            switch(tag){
            case TAG_NULL:
                return null;
            case TAG_VALUE:
                return ois.registerClassName(ois.readInt(false, false), ois.readString());
            case TAG_REFERENCE:
                return ois.lookupClass(ois.readInt(false, false));
            default:
                throw new StreamCorruptedException("Invalid tag." + tag);
            }
        }
        public static byte readInstanceType(NimbusObjectInputStream ois) throws IOException{
            return ois.readByte();
        }
        public static boolean isReference(byte instanceType){
            return instanceType == TAG_REFERENCE;
        }
        public Object getReference(NimbusObjectInputStream ois) throws IOException{
            final int referenceId = ois.readInt(isDisabledNumberCompression, false);
            return ois.lookupReference(this, referenceId);
        }
        public Object newInstance(NimbusObjectInputStream ois) throws IOException, InstantiationException, InvocationTargetException, UnsupportedOperationException{
            if(clazz.isArray()){
                return Array.newInstance(clazz.getComponentType(), ois.readInt(isDisabledNumberCompression, false));
            }else if(clazz.equals(String.class)){
                return null;
            }else if(constructor != null){
                try{
                    return constructor.newInstance();
                }catch(IllegalAccessException ex){
                    throw (InternalError)new InternalError().initCause(ex);
                }
            }else{
                throw new UnsupportedOperationException();
            }
        }
        public void defaultReadObject(Object obj, NimbusObjectInputStream ois) throws IOException, ClassNotFoundException{
            if(fieldReflector == null){
                return;
            }
            fieldReflector.readFields(obj, ois);
        }
        public Object readObject(Object obj, NimbusObjectInputStream ois) throws IOException, ClassNotFoundException{
            final int referenceId = ois.readInt(isDisabledNumberCompression, false);
            if(obj != null){
                ois.registerReference(this, referenceId, obj);
            }
            if(clazz.isArray()){
                final Class componentType = clazz.getComponentType();
                if(componentType.isPrimitive()){
                    if(Byte.TYPE.equals(componentType)){
                        for(int i = 0, imax = Array.getLength(obj); i < imax; i++){
                            Array.setByte(obj, i, ois.readByte());
                        }
                    }else if(Short.TYPE.equals(componentType)){
                        for(int i = 0, imax = Array.getLength(obj); i < imax; i++){
                            Array.setShort(obj, i, ois.readShort());
                        }
                    }else if(Character.TYPE.equals(componentType)){
                        for(int i = 0, imax = Array.getLength(obj); i < imax; i++){
                            Array.setChar(obj, i, ois.readChar());
                        }
                    }else if(Integer.TYPE.equals(componentType)){
                        for(int i = 0, imax = Array.getLength(obj); i < imax; i++){
                            Array.setInt(
                                obj,
                                i,
                                ois.readInt(isDisabledNumberCompression, externalizer.isUseIntReferenceTable)
                            );
                        }
                    }else if(Long.TYPE.equals(componentType)){
                        for(int i = 0, imax = Array.getLength(obj); i < imax; i++){
                            Array.setLong(
                                obj,
                                i,
                                ois.readLong(isDisabledNumberCompression, externalizer.isUseLongReferenceTable)
                            );
                        }
                    }else if(Float.TYPE.equals(componentType)){
                        for(int i = 0, imax = Array.getLength(obj); i < imax; i++){
                            Array.setFloat(obj, i, ois.readFloat());
                        }
                    }else if(Double.TYPE.equals(componentType)){
                        for(int i = 0, imax = Array.getLength(obj); i < imax; i++){
                            Array.setDouble(obj, i, ois.readDouble());
                        }
                    }else if(Boolean.TYPE.equals(componentType)){
                        for(int i = 0, imax = Array.getLength(obj); i < imax; i++){
                            Array.setBoolean(obj, i, ois.readBoolean());
                        }
                    }
                }else{
                    for(int i = 0, imax = Array.getLength(obj); i < imax; i++){
                        Array.set(obj, i, ois.readObject());
                    }
                }
            }else{
                if(readResolveMethod != null){
                    obj = invokeReadResolve(obj);
                    ois.registerReference(this, referenceId, obj);
                    if(obj == null){
                        return null;
                    }else if(!clazz.equals(obj.getClass())){
                        return ois.readObjectOverrideInternal(externalizer.findMetaClass(obj.getClass()), obj);
                    }
                }
                if(String.class.equals(clazz)){
                    obj = ois.readString();
                    ois.registerReference(this, referenceId, obj);
                }else if(isExternalizable){
                    ((Externalizable)obj).readExternal(ois);
                }else{
                    if(classTree != null){
                        for(int i = classTree.size(); --i >= 0;){
                            MetaClass metaClass = (MetaClass)classTree.get(i);
                            if(metaClass.readObjectMethod != null){
                                metaClass.invokeReadObject(obj, ois);
                            }else{
                                metaClass.defaultReadObject(obj, ois);
                            }
                        }
                    }
                    if(readObjectMethod != null){
                        invokeReadObject(obj, ois);
                    }else{
                        ois.defaultReadObject();
                    }
                }
            }
            return obj;
        }
        
        private static ObjectStreamField[] getSerialFields(Class clazz) throws InvalidClassException{
            ObjectStreamField[] fields;
            if(Serializable.class.isAssignableFrom(clazz)
                && !Externalizable.class.isAssignableFrom(clazz)
                && !Proxy.isProxyClass(clazz)
                && !clazz.isInterface()
            ){
                if((fields = getDeclaredSerialFields(clazz)) == null){
                    fields = getDefaultSerialFields(clazz);
                }
                Arrays.sort(fields);
            }else{
                fields = NO_FIELDS;
            }
            return fields;
        }
        
        private static ObjectStreamField[] getDeclaredSerialFields(Class clazz) throws InvalidClassException{
            ObjectStreamField[] serialPersistentFields = null;
            try{
                Field f = clazz.getDeclaredField("serialPersistentFields");
                final int mask = Modifier.PRIVATE | Modifier.STATIC | Modifier.FINAL;
                if((f.getModifiers() & mask) == mask){
                    f.setAccessible(true);
                    serialPersistentFields = (ObjectStreamField[])f.get(null);
                }
            }catch(Exception ex){}
            
            if(serialPersistentFields == null){
                return null;
            }else if(serialPersistentFields.length == 0){
                return NO_FIELDS;
            }
            
            ObjectStreamField[] boundFields = new ObjectStreamField[serialPersistentFields.length];
            Set fieldNames = new HashSet(serialPersistentFields.length);
            for(int i = 0; i < serialPersistentFields.length; i++){
                ObjectStreamField spf = serialPersistentFields[i];
                String fname = spf.getName();
                if(fieldNames.contains(fname)){
                    throw new InvalidClassException("multiple serializable fields named " + fname);
                }
                fieldNames.add(fname);
                
                try{
                    Field f = clazz.getDeclaredField(fname);
                    if(f.getType() == spf.getType() && (f.getModifiers() & Modifier.STATIC) == 0){
                        boundFields[i] = new ObjectStreamField(f.getName(), f.getType(), spf.isUnshared());
                    }
                }catch(NoSuchFieldException ex){
                }
                if(boundFields[i] == null){
                    boundFields[i] = new ObjectStreamField(fname, spf.getType(), spf.isUnshared());
                }
            }
            return boundFields;
        }
        
        private static ObjectStreamField[] getDefaultSerialFields(Class clazz){
            Field[] clFields = clazz.getDeclaredFields();
            final List list = new ArrayList();
            final int mask = Modifier.STATIC | Modifier.TRANSIENT;
            for(int i = 0; i < clFields.length; i++){
                if((clFields[i].getModifiers() & mask) == 0){
                    list.add(new ObjectStreamField(clFields[i].getName(), clFields[i].getType(), false));
                }
            }
            final int size = list.size();
            return (size == 0) ? NO_FIELDS : (ObjectStreamField[])list.toArray(new ObjectStreamField[size]);
        }
        
        private void invokeWriteObject(Object obj, ObjectOutputStream oos) throws IOException, UnsupportedOperationException{
            if(writeObjectMethod != null){
                try{
                    writeObjectMethod.invoke(obj, new Object[]{oos});
                }catch(InvocationTargetException ex){
                    Throwable th = ex.getTargetException();
                    if(th instanceof IOException){
                        throw (IOException)th;
                    }else{
                        throwMiscException(th);
                    }
                }catch(IllegalAccessException ex){
                    throw (InternalError)new InternalError().initCause(ex);
                }
            }else{
                throw new UnsupportedOperationException();
            }
        }
        
        private Object invokeWriteReplace(Object obj) throws IOException, UnsupportedOperationException{
            if(writeReplaceMethod != null){
                try{
                    return writeReplaceMethod.invoke(obj, (Object[]) null);
                }catch(InvocationTargetException ex){
                    Throwable th = ex.getTargetException();
                    if(th instanceof ObjectStreamException){
                        throw (ObjectStreamException)th;
                    }else{
                        throwMiscException(th);
                        throw (InternalError)new InternalError().initCause(th);
                    }
                }catch(IllegalAccessException ex){
                    throw (InternalError)new InternalError().initCause(ex);
                }
            }else{
                throw new UnsupportedOperationException();
            }
        }
        
        private Object invokeReadResolve(Object obj) throws IOException, UnsupportedOperationException{
            if(readResolveMethod != null){
                try{
                    return readResolveMethod.invoke(obj, (Object[])null);
                }catch(InvocationTargetException ex){
                    Throwable th = ex.getTargetException();
                    if(th instanceof ObjectStreamException){
                        throw (ObjectStreamException)th;
                    }else{
                        throwMiscException(th);
                        throw (InternalError)new InternalError().initCause(th);
                    }
                }catch(IllegalAccessException ex){
                    throw (InternalError)new InternalError().initCause(ex);
                }
            }else{
                throw new UnsupportedOperationException();
            }
        }
        
        private void invokeReadObject(Object obj, ObjectInputStream in)throws ClassNotFoundException, IOException, UnsupportedOperationException{
            if(readObjectMethod != null){
                try{
                    readObjectMethod.invoke(obj, new Object[]{in});
                }catch(InvocationTargetException ex){
                    Throwable th = ex.getTargetException();
                    if(th instanceof ClassNotFoundException){
                        throw (ClassNotFoundException)th;
                    }else if(th instanceof IOException){
                        throw (IOException)th;
                    }else{
                        throwMiscException(th);
                    }
                }catch(IllegalAccessException ex){
                    throw (InternalError)new InternalError().initCause(ex);
                }
            }else{
                throw new UnsupportedOperationException();
            }
        }
        
        private void invokeReadObjectNoData(Object obj) throws IOException, UnsupportedOperationException{
            if(readObjectNoDataMethod != null){
                try{
                    readObjectNoDataMethod.invoke(obj, (Object[])null);
                }catch(InvocationTargetException ex){
                    Throwable th = ex.getTargetException();
                    if(th instanceof ObjectStreamException){
                        throw (ObjectStreamException)th;
                    }else{
                        throwMiscException(th);
                    }
                }catch(IllegalAccessException ex){
                    throw (InternalError)new InternalError().initCause(ex);
                }
            }else{
                throw new UnsupportedOperationException();
            }
        }
        
        private static void throwMiscException(Throwable th) throws IOException{
            if(th instanceof RuntimeException){
                throw (RuntimeException)th;
            }else if(th instanceof Error){
                throw (Error)th;
            }else{
                IOException ex = new IOException("Unexpected exception occurred.");
                ex.initCause(th);
                throw ex;
            }
        }
        
        private static Constructor getExternalizableConstructor(Class clazz){
            try{
                Constructor cons = clazz.getDeclaredConstructor((Class[]) null);
                cons.setAccessible(true);
                return ((cons.getModifiers() & Modifier.PUBLIC) != 0) ? cons : null;
            }catch(NoSuchMethodException ex){
                return null;
            }
        }
        
        private static Constructor getSerializableConstructor(Class clazz){
            Class initClass = clazz;
            while(Serializable.class.isAssignableFrom(initClass)){
                if((initClass = initClass.getSuperclass()) == null){
                    return null;
                }
            }
            try{
                Constructor cons = initClass.getDeclaredConstructor((Class[]) null);
                final int mods = cons.getModifiers();
                if((mods & Modifier.PRIVATE) != 0
                    || ((mods & (Modifier.PUBLIC | Modifier.PROTECTED)) == 0
                            && !packageEquals(clazz, initClass))
                ){
                    return null;
                }
                cons = reflFactory.newConstructorForSerialization(clazz, cons);
                cons.setAccessible(true);
                return cons;
            }catch(NoSuchMethodException ex){
                return null;
            }
        }
        
        private static Method getPrivateMethod(
            Class clazz,
            String name,
            Class[] argTypes,
            Class returnType
        ){
            try{
                Method meth = clazz.getDeclaredMethod(name, argTypes);
                meth.setAccessible(true);
                int mods = meth.getModifiers();
                return ((meth.getReturnType() == returnType)
                    && ((mods & Modifier.STATIC) == 0) && ((mods & Modifier.PRIVATE) != 0)) ? meth : null;
            }catch(NoSuchMethodException ex){
                return null;
            }
        }
        
        private static Method getInheritableMethod(
            Class clazz,
            String name,
            Class[] argTypes,
            Class returnType
        ){
            Method method = null;
            Class defClazz = clazz;
            while(defClazz != null){
                try{
                    method = defClazz.getDeclaredMethod(name, argTypes);
                    break;
                }catch(NoSuchMethodException ex){
                    defClazz = defClazz.getSuperclass();
                }
            }
            
            if((method == null) || (method.getReturnType() != returnType)){
                return null;
            }
            method.setAccessible(true);
            final int mods = method.getModifiers();
            if((mods & (Modifier.STATIC | Modifier.ABSTRACT)) != 0){
                return null;
            }else if((mods & (Modifier.PUBLIC | Modifier.PROTECTED)) != 0){
                return method;
            }else if((mods & Modifier.PRIVATE) != 0){
                return (clazz == defClazz) ? method : null;
            }else{
                return packageEquals(clazz, defClazz) ? method : null;
            }
        }
        
        private static boolean packageEquals(Class clazz1, Class clazz2){
            return clazz1.getClassLoader() == clazz2.getClassLoader() && getPackageName(clazz1).equals(getPackageName(clazz2));
        }
        
        private static String getPackageName(Class clazz){
            String s = clazz.getName();
            int i = s.lastIndexOf('[');
            if(i >= 0){
                s = s.substring(i + 2);
            }
            i = s.lastIndexOf('.');
            return (i >= 0) ? s.substring(0, i) : "";
        }
    }
    
    private static class FieldReflector{
        
        private static final Unsafe unsafe;
        
        static{
            try{
                Constructor unsafeConstructor = Unsafe.class.getDeclaredConstructor();
                unsafeConstructor.setAccessible(true);
                unsafe = (Unsafe)unsafeConstructor.newInstance();
            }catch(Exception e){
                throw (InternalError)new InternalError().initCause(e);
            }
        }
        
        private NimbusExternalizerService externalizer;
        private final ObjectStreamField[] fields;
        private final long[] readKeys;
        private final long[] writeKeys;
        private final char[] typeCodes;
        private final Class[] types;
        
        public FieldReflector(NimbusExternalizerService externalizer, Class clazz, ObjectStreamField[] fields){
            this.externalizer = externalizer;
            this.fields = fields;
            final int nfields = fields.length;
            readKeys = new long[nfields];
            writeKeys = new long[nfields];
            typeCodes = new char[nfields];
            
            ArrayList typeList = new ArrayList();
            Set usedKeys = new HashSet();
            for(int i = 0; i < nfields; i++){
                ObjectStreamField f = fields[i];
                Field rf = null;
                try{
                    rf = clazz.getDeclaredField(f.getName());
                }catch(NoSuchFieldException ex){
                    throw (InternalError)new InternalError().initCause(ex);
                }
                final long key = (rf != null) ? unsafe.objectFieldOffset(rf) : Unsafe.INVALID_FIELD_OFFSET;
                readKeys[i] = key;
                writeKeys[i] = usedKeys.add(key) ? key : Unsafe.INVALID_FIELD_OFFSET;
                typeCodes[i] = f.getTypeCode();
                typeList.add((rf != null) ? rf.getType() : null);
            }
            types = (Class[])typeList.toArray(new Class[typeList.size()]);
        }
        
        public ObjectStreamField[] getFields(){
            return fields;
        }
        
        public void writeFields(Object obj, NimbusObjectOutputStream oos) throws IOException{
            if(obj == null){
                throw new NullPointerException();
            }
            for(int i = 0; i < readKeys.length; i++){
                final long key = readKeys[i];
                switch(typeCodes[i]){
                case 'Z':
                    oos.writeBoolean(unsafe.getBoolean(obj, key));
                    break;
                case 'B':
                    oos.writeByte(unsafe.getByte(obj, key));
                    break;
                case 'C':
                    oos.writeChar(unsafe.getChar(obj, key));
                    break;
                case 'S':
                    oos.writeShort(unsafe.getShort(obj, key));
                    break;
                case 'I':
                    oos.writeInt(unsafe.getInt(obj, key), !externalizer.isUseNumberCompression, externalizer.isUseIntReferenceTable);
                    break;
                case 'F':
                    oos.writeFloat(unsafe.getFloat(obj, key));
                    break;
                case 'J':
                    oos.writeLong(unsafe.getLong(obj, key), !externalizer.isUseNumberCompression, externalizer.isUseLongReferenceTable);
                    break;
                case 'D':
                    oos.writeDouble(unsafe.getDouble(obj, key));
                    break;
                case 'L':
                case '[':
                    oos.writeObject(unsafe.getObject(obj, key));
                    break;
                default:
                    throw new InternalError();
                }
            }
        }
        
        public void readFields(Object obj, NimbusObjectInputStream ois) throws IOException, ClassNotFoundException{
            if(obj == null){
                throw new NullPointerException();
            }
            for(int i = 0; i < writeKeys.length; i++){
                final long key = writeKeys[i];
                
                if (key == Unsafe.INVALID_FIELD_OFFSET){
                    continue;
                }
                switch(typeCodes[i]){
                case 'Z':
                    unsafe.putBoolean(obj, key, ois.readBoolean());
                    break;
                case 'B':
                    unsafe.putByte(obj, key, ois.readByte());
                    break;
                case 'C':
                    unsafe.putChar(obj, key, ois.readChar());
                    break;
                case 'S':
                    unsafe.putShort(obj, key, ois.readShort());
                    break;
                case 'I':
                    unsafe.putInt(obj, key, ois.readInt(!externalizer.isUseNumberCompression, externalizer.isUseIntReferenceTable));
                    break;
                case 'F':
                    unsafe.putFloat(obj, key, ois.readFloat());
                    break;
                case 'J':
                    unsafe.putLong(obj, key, ois.readLong(!externalizer.isUseNumberCompression, externalizer.isUseLongReferenceTable));
                    break;
                case 'D':
                    unsafe.putDouble(obj, key, ois.readDouble());
                    break;
                case 'L':
                case '[':
                    Object val = ois.readObject();
                    if(val != null && !types[i].isInstance(val)){
                        throw new ClassCastException(
                            "cannot assign instance of " +
                            val.getClass().getName() + " to field " +
                            obj.getClass().getName() + "." +
                            fields[i].getName() + " of type " +
                            fields[i].getType().getName() + " in instance of " +
                            obj.getClass().getName()
                        );
                    }
                    unsafe.putObject(obj, key, val);
                    break;
                default:
                    throw new InternalError();
                }
            }
        }
    }
    
    private static class ReferenceTable{
        protected int size;
        protected int[] ids;
        protected int[] next;
        protected Object[] references;
        protected int threshold;
        protected final float loadFactor;
        protected final float expandRatio;
        
        public ReferenceTable(){
            this(10, 3.0f, 3.0f);
        }
        
        public ReferenceTable(int initialCapacity, float expandRatio, float loadFactor){
            ids = new int[initialCapacity];
            next = new int[initialCapacity];
            references = new Object[initialCapacity];
            this.loadFactor = loadFactor;
            this.expandRatio = expandRatio;
            threshold = (int)(initialCapacity * loadFactor);
        }
        
        public int assign(Object reference){
            int id = lookup(reference);
            if(id != 0){
                return -id;
            }
            id = size + 1;
            insert(reference, id);
            return id;
        }
        
        public int lookup(Object reference){
            if(size == 0){
                return 0;
            }
            int index = hash(reference) % ids.length;
            for(int id = ids[index]; id > 0; id = next[id - 1]){
                if(compareObject(reference, references[id - 1])){
                    return id;
                }
            }
            return 0;
        }
        
        public int size(){
            return size;
        }
        
        public Object getReference(int id){
            return references[id - 1];
        }
        
        protected boolean compareObject(Object o1, Object o2){
            return o1 == o2;
        }
        
        public void insert(Object reference, int id){
            insertInner(reference, id, false);
        }
        protected void insertInner(Object reference, int id, boolean resize){
            if(id > next.length){
                growEntries();
            }
            if(!resize && size > threshold){
                growIds();
            }
            final int index = hash(reference) % ids.length;
            if(!resize){
                references[id - 1] = reference;
            }
            next[id - 1] = ids[index];
            ids[index] = id;
            if(!resize){
                size++;
            }
        }
        
        protected void growIds(){
            if(ids.length == Integer.MAX_VALUE){
                throw new OutOfMemoryError("Ids can not grow.");
            }
            int newLength = (int)(ids.length * expandRatio);
            if(newLength < ids.length){
                newLength = Integer.MAX_VALUE;
            }
            ids = new int[newLength];
            threshold = (int)(ids.length * loadFactor);
            for(int i = 1; i <= size; i++){
                insertInner(references[i - 1], i, true);
            }
        }
        
        protected void growEntries(){
            if(next.length == Integer.MAX_VALUE){
                throw new OutOfMemoryError("Entries can not grow.");
            }
            int newLength = next.length << 1;
            if(newLength < next.length){
                newLength = Integer.MAX_VALUE;
            }
            int[] newNext = new int[newLength];
            System.arraycopy(next, 0, newNext, 0, size);
            next = newNext;
            
            Object[] newRefs = new Object[newLength];
            System.arraycopy(references, 0, newRefs, 0, size);
            references = newRefs;
        }
        
        protected int hash(Object reference){
            return System.identityHashCode(reference) & 0x7FFFFFFF;
        }
    }
    
    private static class EqualsReferenceTable extends ReferenceTable{
        
        public EqualsReferenceTable(){
            this(10, 3.0f, 3.0f);
        }
        
        public EqualsReferenceTable(int initialCapacity, float expandRatio, float loadFactor){
            super(initialCapacity, expandRatio, loadFactor);
        }
        
        protected boolean compareObject(Object o1, Object o2){
            if(o1 == null && o2 == null){
                return true;
            }else if((o1 == null && o2 != null) || (o1 != null && o2 == null)){
                return false;
            }else if(o1 == o2){
                return true;
            }
            if(o1.getClass().equals(o2.getClass())){
                return o1.equals(o2);
            }else{
                return false;
            }
        }
        
        protected int hash(Object reference){
            return reference.hashCode() & 0x7FFFFFFF;
        }
    }
    
    private static class IntReferenceTable{
        protected int size;
        protected int[] ids;
        protected int[] next;
        protected int[] references;
        protected int threshold;
        protected final float loadFactor;
        protected final float expandRatio;
        
        public IntReferenceTable(){
            this(10, 3.0f, 3.0f);
        }
        
        public IntReferenceTable(int initialCapacity, float expandRatio, float loadFactor){
            ids = new int[initialCapacity];
            next = new int[initialCapacity];
            references = new int[initialCapacity];
            this.loadFactor = loadFactor;
            this.expandRatio = expandRatio;
            threshold = (int)(initialCapacity * loadFactor);
        }
        
        public int assign(int reference){
            int id = lookup(reference);
            if(id != 0){
                return -id;
            }
            id = size + 1;
            insert(reference, id);
            return id;
        }
        
        public int lookup(int reference){
            if(size == 0){
                return 0;
            }
            final int index = hash(reference) % ids.length;
            for(int id = ids[index]; id > 0; id = next[id - 1]){
                if(reference == references[id - 1]){
                    return id;
                }
            }
            return 0;
        }
        
        public int size(){
            return size;
        }
        
        public int getReference(int id){
            return references[id - 1];
        }
        
        public void insert(int reference, int id){
            insertInner(reference, id, false);
        }
        protected void insertInner(int reference, int id, boolean resize){
            if(id > next.length){
                growEntries();
            }
            if(!resize && size > threshold){
                growIds();
            }
            final int index = hash(reference) % ids.length;
            if(!resize){
                references[id - 1] = reference;
            }
            next[id - 1] = ids[index];
            ids[index] = id;
            if(!resize){
                size++;
            }
        }
        
        protected void growIds(){
            if(ids.length == Integer.MAX_VALUE){
                throw new OutOfMemoryError("Ids can not grow.");
            }
            int newLength = (int)(ids.length * expandRatio);
            if(newLength < ids.length){
                newLength = Integer.MAX_VALUE;
            }
            ids = new int[newLength];
            threshold = (int)(ids.length * loadFactor);
            for(int i = 1; i <= size; i++){
                insertInner(references[i - 1], i, true);
            }
        }
        
        protected void growEntries(){
            if(next.length == Integer.MAX_VALUE){
                throw new OutOfMemoryError("Entries can not grow.");
            }
            int newLength = next.length << 1;
            if(newLength < next.length){
                newLength = Integer.MAX_VALUE;
            }
            int[] newNext = new int[newLength];
            System.arraycopy(next, 0, newNext, 0, size);
            next = newNext;
            
            int[] newRefs = new int[newLength];
            System.arraycopy(references, 0, newRefs, 0, size);
            references = newRefs;
        }
        
        protected int hash(int reference){
            return reference & 0x7FFFFFFF;
        }
    }
    
    private static class LongReferenceTable{
        protected int size;
        protected int[] ids;
        protected int[] next;
        protected long[] references;
        protected int threshold;
        protected final float loadFactor;
        protected final float expandRatio;
        
        public LongReferenceTable(){
            this(10, 3.0f, 3.0f);
        }
        
        public LongReferenceTable(int initialCapacity, float expandRatio, float loadFactor){
            ids = new int[initialCapacity];
            next = new int[initialCapacity];
            references = new long[initialCapacity];
            this.loadFactor = loadFactor;
            this.expandRatio = expandRatio;
            threshold = (int)(initialCapacity * loadFactor);
        }
        
        public int assign(long reference){
            int id = lookup(reference);
            if(id != 0){
                return -id;
            }
            id = size + 1;
            insert(reference, id);
            return id;
        }
        
        public int lookup(long reference){
            if(size == 0){
                return 0;
            }
            final int index = hash(reference) % ids.length;
            for(int id = ids[index]; id > 0; id = next[id - 1]){
                if(reference == references[id - 1]){
                    return id;
                }
            }
            return 0;
        }
        
        public int size(){
            return size;
        }
        
        public long getReference(int id){
            return references[id - 1];
        }
        
        public void insert(long reference, int id){
            insertInner(reference, id, false);
        }
        protected void insertInner(long reference, int id, boolean resize){
            if(id > next.length){
                growEntries();
            }
            if(!resize && size > threshold){
                growIds();
            }
            final int index = hash(reference) % ids.length;
            if(!resize){
                references[id - 1] = reference;
            }
            next[id - 1] = ids[index];
            ids[index] = id;
            if(!resize){
                size++;
            }
        }
        
        protected void growIds(){
            if(ids.length == Integer.MAX_VALUE){
                throw new OutOfMemoryError("Ids can not grow.");
            }
            int newLength = (int)(ids.length * expandRatio);
            if(newLength < ids.length){
                newLength = Integer.MAX_VALUE;
            }
            ids = new int[newLength];
            threshold = (int)(ids.length * loadFactor);
            for(int i = 1; i <= size; i++){
                insertInner(references[i - 1], i, true);
            }
        }
        
        protected void growEntries(){
            if(next.length == Integer.MAX_VALUE){
                throw new OutOfMemoryError("Entries can not grow.");
            }
            int newLength = next.length << 1;
            if(newLength < next.length){
                newLength = Integer.MAX_VALUE;
            }
            int[] newNext = new int[newLength];
            System.arraycopy(next, 0, newNext, 0, size);
            next = newNext;
            
            long[] newRefs = new long[newLength];
            System.arraycopy(references, 0, newRefs, 0, size);
            references = newRefs;
        }
        
        protected int hash(long reference){
            return ((int)(reference ^ (reference >>> 32))) & 0x7FFFFFFF;
        }
    }
}