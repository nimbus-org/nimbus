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
import java.util.zip.*;
import java.lang.reflect.*;

import jp.ossc.nimbus.core.*;


import org.xerial.snappy.SnappyOutputStream;
import org.xerial.snappy.SnappyInputStream;
import net.jpountz.lz4.LZ4BlockOutputStream;
import net.jpountz.lz4.LZ4BlockInputStream;


/**
 * 直列化可能オブジェクト直列化サービス。<p>
 * 
 * @author M.Takata
 */
public class SerializableExternalizerService extends ServiceBase
 implements SerializableExternalizerServiceMBean, Externalizer, Serializable{
    
    private static final long serialVersionUID = -2894857230847782064L;
    
    protected int compressMode = COMPRESS_MODE_NONE;
    protected int compressLevel = Deflater.DEFAULT_COMPRESSION;
    protected int compressMethod = ZipOutputStream.DEFLATED;
    protected int compressThreshold = -1;
    protected int bufferSize;
    protected Class objectOutputClass;
    protected transient Constructor objectOutputConstructor;
    protected Class objectInputClass;
    protected transient Constructor objectInputConstructor;
    protected boolean isBufferedOutputStream = false;
    protected int outputStreamInitialBufferSize = 1024;
    protected float outputStreamBufferExpandRatio = 2.0f;
    protected int outputStreamMaxBufferSize = 1024 * 10;
    protected boolean isBufferedInputStream = false;
    protected int inputStreamInitialBufferSize = 1024;
    
    public void setCompressMode(int mode){
        compressMode = mode;
    }
    public int getCompressMode(){
        return compressMode;
    }
    
    public void setCompressLevel(int level){
        compressLevel = level;
    }
    public int getCompressLevel(){
        return compressLevel;
    }
    
    public void setCompressMethod(int method){
        compressMethod = method;
    }
    public int getCompressMethod(){
        return compressMethod;
    }
    
    public void setCompressThreshold(int threshold){
        compressThreshold = threshold;
    }
    public int getCompressThreshold(){
        return compressThreshold;
    }
    
    public void setBufferSize(int size){
        bufferSize = size;
    }
    public int getBufferSize(){
        return bufferSize;
    }
    
    public boolean isBufferedOutputStream(){
        return isBufferedOutputStream;
    }
    public void setBufferedOutputStream(boolean isBuffered){
        isBufferedOutputStream = isBuffered;
    }
    
    public void setOutputStreamInitialBufferSize(int size){
        outputStreamInitialBufferSize = size;
        if(outputStreamMaxBufferSize < outputStreamInitialBufferSize){
            outputStreamMaxBufferSize = outputStreamInitialBufferSize;
        }
    }
    public int getOutputStreamInitialBufferSize(){
        return outputStreamInitialBufferSize;
    }
    
    public void setOutputStreamBufferExpandRatio(float ratio){
        outputStreamBufferExpandRatio = ratio;
    }
    public float getOutputStreamBufferExpandRatio(){
        return outputStreamBufferExpandRatio;
    }
    
    public void setOutputStreamMaxBufferSize(int size){
        outputStreamMaxBufferSize = size;
    }
    public int getOutputStreamMaxBufferSize(){
        return outputStreamMaxBufferSize;
    }
    
    public boolean isBufferedInputStream(){
        return isBufferedInputStream;
    }
    public void setBufferedInputStream(boolean isBuffered){
        isBufferedInputStream = isBuffered;
    }
    
    public void setInputStreamInitialBufferSize(int size){
        inputStreamInitialBufferSize = size;
    }
    public int getInputStreamInitialBufferSize(){
        return inputStreamInitialBufferSize;
    }
    
    public void setObjectOutputClass(Class clazz){
        if(clazz == null){
            objectOutputConstructor = null;
            objectOutputClass = null;
        }else{
            try{
                objectOutputConstructor = clazz.getConstructor(new Class[]{OutputStream.class});
            }catch(NoSuchMethodException e){
                throw new IllegalArgumentException("No support ObjectOutputClass." + clazz);
            }
            objectOutputClass = clazz;
        }
    }
    public Class getObjectOutputClass(){
        return objectOutputClass;
    }
    
    public void setObjectInputClass(Class clazz){
        if(clazz == null){
            objectInputConstructor = null;
            objectInputClass = null;
        }else{
            try{
                objectInputConstructor = clazz.getConstructor(new Class[]{InputStream.class});
            }catch(NoSuchMethodException e){
                throw new IllegalArgumentException("No support ObjectInputClass." + clazz);
            }
            objectInputClass = clazz;
        }
    }
    public Class getObjectInputClass(){
        return objectInputClass;
    }
    
    protected ObjectOutput createObjectOutput(OutputStream out) throws IOException{
        ObjectOutput output = null;
        if(objectOutputClass == null){
            output = new ObjectOutputStream(out);
        }else{
            try{
                output = (ObjectOutput)objectOutputConstructor.newInstance(new Object[]{out});
            }catch(InstantiationException e){
                throw new IOException("ObjectOutput can not instanciate." + objectOutputClass.getName() + " cause " + e.toString());
            }catch(IllegalAccessException e){
                throw new IOException("ObjectOutput can not instanciate." + objectOutputClass.getName() + " cause " + e.toString());
            }catch(InvocationTargetException e){
                throw new IOException("ObjectOutput can not instanciate." + objectOutputClass.getName() + " cause " + e.getTargetException().toString());
            }
        }
        return output;
    }
    
    public void writeExternal(Object obj, OutputStream out) throws IOException{
        if(isBufferedOutputStream && compressMode == COMPRESS_MODE_NONE){
            out = new ExpandableBufferedOutputStream(out);
        }
        if(compressMode != COMPRESS_MODE_NONE && compressThreshold == -1){
            DeflaterOutputStream dos = null;
            Deflater deflater = null;
            ObjectOutput output = null;
            switch(compressMode){
            case COMPRESS_MODE_SNAPPY:
                SnappyOutputStream sos = new SnappyOutputStream(out);
                output = createObjectOutput(sos);
                writeInternal(obj, output);
                sos.flush();
                break;
            case COMPRESS_MODE_LZ4:
                LZ4BlockOutputStream lzos = new LZ4BlockOutputStream(out);
                output = createObjectOutput(lzos);
                writeInternal(obj, output);
                lzos.flush();
                lzos.finish();
                break;
            default:
                switch(compressMode){
                case COMPRESS_MODE_ZLIB:
                    deflater = new Deflater(compressLevel);
                    dos = bufferSize > 0 ? new DeflaterOutputStream(out, deflater, bufferSize) : new DeflaterOutputStream(out, deflater);
                    break;
                case COMPRESS_MODE_ZIP:
                    ZipOutputStream zos = new ZipOutputStream(out);
                    zos.setLevel(compressLevel);
                    zos.setMethod(compressMethod);
                    zos.putNextEntry(new ZipEntry("a"));
                    dos = zos;
                    break;
                case COMPRESS_MODE_GZIP:
                    dos = bufferSize > 0 ? new GZIPOutputStream(out, bufferSize) : new GZIPOutputStream(out);
                    break;
                default:
                    throw new IOException("Unknown compress mode : " + compressMode);
                }
                output = createObjectOutput(dos);
                try{
                    writeInternal(obj, output);
                    if(compressMode == COMPRESS_MODE_ZIP){
                        ((ZipOutputStream)dos).closeEntry();
                    }
                    dos.finish();
                }finally{
                    if(deflater != null){
                        deflater.end();
                    }
                }
            }
        }else{
            ObjectOutput output = createObjectOutput(out);
            writeExternal(obj, output);
        }
    }
    
    public void writeExternal(Object obj, ObjectOutput out) throws IOException{
        try{
            if(compressMode == COMPRESS_MODE_NONE){
                writeInternal(obj, out);
                return;
            }
            if(obj == null){
                out.writeBoolean(false);
                out.writeInt(0);
                return;
            }
            final ByteArrayOutputStream baos
                = bufferSize > 0 ? new ByteArrayOutputStream(bufferSize)
                    : new ByteArrayOutputStream();
            final ObjectOutput tmpOut = createObjectOutput(baos);
            writeInternal(obj, tmpOut);
            tmpOut.flush();
            final byte[] noCompressedBytes = baos.toByteArray();
            if(compressThreshold >= noCompressedBytes.length){
                out.writeBoolean(false);
                out.writeInt(noCompressedBytes.length);
                out.write(noCompressedBytes);
                return;
            }
            baos.reset();
            DeflaterOutputStream dos = null;
            Deflater deflater = null;
            byte[] compressedBytes = null;
            switch(compressMode){
            case COMPRESS_MODE_SNAPPY:
                SnappyOutputStream sos = new SnappyOutputStream(baos);
                sos.write(noCompressedBytes);
                sos.flush();
                sos.close();
                compressedBytes = baos.toByteArray();
                break;
            case COMPRESS_MODE_LZ4:
                LZ4BlockOutputStream lzos = new LZ4BlockOutputStream(baos);
                lzos.write(noCompressedBytes);
                lzos.flush();
                lzos.finish();
                lzos.close();
                compressedBytes = baos.toByteArray();
                break;
            default:
                switch(compressMode){
                case COMPRESS_MODE_ZLIB:
                    deflater = new Deflater(compressLevel);
                    dos = bufferSize > 0 ? new DeflaterOutputStream(baos, deflater, bufferSize) : new DeflaterOutputStream(baos, deflater);
                    break;
                case COMPRESS_MODE_ZIP:
                    ZipOutputStream zos = new ZipOutputStream(baos);
                    zos.setLevel(compressLevel);
                    zos.setMethod(compressMethod);
                    zos.putNextEntry(new ZipEntry("a"));
                    dos = zos;
                    break;
                case COMPRESS_MODE_GZIP:
                    dos = bufferSize > 0 ? new GZIPOutputStream(baos, bufferSize) : new GZIPOutputStream(baos);
                    break;
                default:
                    throw new IOException("Unknown compress mode : " + compressMode);
                }
                try{
                    dos.write(noCompressedBytes, 0, noCompressedBytes.length);
                    if(compressMode == COMPRESS_MODE_ZIP){
                        ((ZipOutputStream)dos).closeEntry();
                    }
                    dos.finish();
                }finally{
                    if(deflater != null){
                        deflater.end();
                    }
                    try{
                        dos.close();
                    }catch(IOException e){}
                    baos.close();
                }
                compressedBytes = baos.toByteArray();
            }
            
            if(noCompressedBytes.length <= compressedBytes.length){
                out.writeBoolean(false);
                out.writeInt(noCompressedBytes.length);
                out.write(noCompressedBytes);
            }else{
                out.writeBoolean(true);
                out.writeInt(compressedBytes.length);
                out.write(compressedBytes);
            }
        }finally{
            out.flush();
        }
    }
    
    protected void writeInternal(Object obj, ObjectOutput out) throws IOException{
        out.writeObject(obj);
    }
    
    public Object readExternal(InputStream in) throws IOException, ClassNotFoundException{
        if(isBufferedInputStream){
            in = new BufferedInputStream(in, inputStreamInitialBufferSize);
        }
        if(compressMode != COMPRESS_MODE_NONE && compressThreshold == -1){
            InputStream is = null;
            Inflater inflater = null;
            switch(compressMode){
            case COMPRESS_MODE_ZLIB:
                inflater = new Inflater();
                is = bufferSize > 0 ? new InflaterInputStream(in, inflater, bufferSize) : new InflaterInputStream(in);
                break;
            case COMPRESS_MODE_ZIP:
                is = new ZipInputStream(in);
                ((ZipInputStream)is).getNextEntry();
                break;
            case COMPRESS_MODE_GZIP:
                is = bufferSize > 0 ? new GZIPInputStream(in, bufferSize) : new GZIPInputStream(in);
                break;
            case COMPRESS_MODE_SNAPPY:
                is = new SnappyInputStream(in);
                break;
            case COMPRESS_MODE_LZ4:
                is = new LZ4BlockInputStream(in);
                break;
            default:
                throw new IOException("Unknown compress mode : " + compressMode);
            }
            final ObjectInput oi = createObjectInput(is);
            try{
                return readInternal(oi);
            }finally{
                if(inflater != null){
                    inflater.end();
                }
            }
        }else{
            ObjectInput input = createObjectInput(in);
            return readExternal(input);
        }
    }
    
    protected ObjectInput createObjectInput(InputStream in) throws IOException{
        ObjectInput input = null;
        if(objectInputClass == null){
            input = new ObjectInputStream(in);
        }else{
            try{
                input = (ObjectInput)objectInputConstructor.newInstance(new Object[]{in});
            }catch(InstantiationException e){
                throw new IOException("ObjectInput can not instanciate." + objectInputClass.getName() + " cause " + e.toString());
            }catch(IllegalAccessException e){
                throw new IOException("ObjectInput can not instanciate." + objectInputClass.getName() + " cause " + e.toString());
            }catch(InvocationTargetException e){
                throw new IOException("ObjectInput can not instanciate." + objectInputClass.getName() + " cause " + e.getTargetException().toString());
            }
        }
        return input;
    }
    
    public Object readExternal(ObjectInput in) throws IOException, ClassNotFoundException{
        if(compressMode == COMPRESS_MODE_NONE){
            return readInternal(in);
        }
        final boolean isCompressed = in.readBoolean();
        final int length = in.readInt();
        if(length == 0){
            return null;
        }
        final byte[] bytes = new byte[length];
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int readLen = 0;
        int offset = 0;
        while((readLen = in.read(bytes, offset, length - offset)) != -1){
            baos.write(bytes, offset, readLen);
            offset+=readLen;
            if(length - offset == 0){
                break;
            }
        }
        
        final ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        InputStream is = null;
        Inflater inflater = null;
        if(isCompressed){
            switch(compressMode){
            case COMPRESS_MODE_ZLIB:
                inflater = new Inflater();
                is = bufferSize > 0 ? new InflaterInputStream(bais, inflater, bufferSize) : new InflaterInputStream(bais);
                break;
            case COMPRESS_MODE_ZIP:
                is = new ZipInputStream(bais);
                ((ZipInputStream)is).getNextEntry();
                break;
            case COMPRESS_MODE_GZIP:
                is = bufferSize > 0 ? new GZIPInputStream(bais, bufferSize) : new GZIPInputStream(bais);
                break;
            case COMPRESS_MODE_SNAPPY:
                is = new SnappyInputStream(bais);
                break;
            case COMPRESS_MODE_LZ4:
                is = new LZ4BlockInputStream(bais);
                break;
            default:
                throw new IOException("Unknown compress mode : " + compressMode);
            }
        }else{
            is = bais;
        }
        final ObjectInput oi = createObjectInput(is);
        try{
            return readInternal(oi);
        }finally{
            if(inflater != null){
                inflater.end();
            }
            try{
                is.close();
            }catch(IOException e){}
            bais.close();
        }
    }
    
    protected Object readInternal(ObjectInput in) throws IOException, ClassNotFoundException{
        return in.readObject();
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
        in.defaultReadObject();
        if(objectOutputClass != null){
            setObjectOutputClass(objectOutputClass);
        }
        if(objectInputClass != null){
            setObjectInputClass(objectInputClass);
        }
    }
    
    protected class ExpandableBufferedOutputStream extends FilterOutputStream{
        protected byte buf[];
        protected int count;
        
        public ExpandableBufferedOutputStream(OutputStream out){
            super(out);
            if(outputStreamInitialBufferSize <= 0){
                throw new IllegalArgumentException("outputStreamInitialBufferSize size <= 0");
            }
            buf = new byte[outputStreamInitialBufferSize];
        }
        
        protected void flushBuffer(boolean isInner) throws IOException{
            if(count > 0){
                out.write(buf, 0, count);
                count = 0;
            }
            if(isInner && buf.length < outputStreamMaxBufferSize){
                int newLength = (int)(buf.length * outputStreamBufferExpandRatio);
                if(newLength < buf.length){
                    newLength = Integer.MAX_VALUE;
                }
                buf = new byte[Math.min(newLength, outputStreamMaxBufferSize)];
            }
        }
        
        public synchronized void write(int b) throws IOException{
            if(count >= buf.length){
                flushBuffer(true);
            }
            buf[count++] = (byte)b;
        }
        
        public synchronized void write(byte b[], int off, int len) throws IOException{
            if(len >= buf.length){
                flushBuffer(true);
                out.write(b, off, len);
                return;
            }
            if(len > buf.length - count){
                flushBuffer(true);
            }
            System.arraycopy(b, off, buf, count, len);
            count += len;
        }
        
        public synchronized void flush() throws IOException{
            flushBuffer(false);
            out.flush();
        }
    }
}
