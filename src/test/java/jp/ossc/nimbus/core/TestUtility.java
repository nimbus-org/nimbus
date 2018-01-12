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
package jp.ossc.nimbus.core;

import java.net.*;
import java.io.*;

public class TestUtility{
    
    public static File copyOnTemp(String resource) throws IOException{
        final File dest = File.createTempFile(resource, null);
        dest.deleteOnExit();
        return copy(resource, dest.getCanonicalPath());
    }
    
    public static File copyOnClassPath(String resource) throws IOException{
        return copy(resource, getURLOnClassPath(resource).getFile());
    }
    
    public static File copyOnClassPath(String resource, String dest)
     throws IOException{
        return copy(resource, getURLOnClassPath(dest).getFile());
    }
    
    public static File copy(String resource, String dest) throws IOException{
        final URL url = getResource(resource);
        if(url == null){
            throw new FileNotFoundException(resource);
        }
        final InputStream is = url.openStream();
        final BufferedInputStream bis = new BufferedInputStream(is);
        final FileOutputStream fos
             = new FileOutputStream(dest);
        final byte[] bytes = new byte[1024];
        boolean isEOF = false;
        while(!isEOF){
            final int length = bis.read(bytes, 0, 1024);
            isEOF = length == -1;
            if(!isEOF){
                fos.write(bytes, 0, length);
            }
        }
        bis.close();
        fos.close();
        return new File(dest);
    }
    private static final String RESOURCE_DIR = "resources/";
    private static final String PACKAGE_DIR = "jp/ossc/nimbus/core/";
    private static URL CLASS_PATH_URL;
    static{
        final String className = TestUtility.class.getName();
        final String classFilePath = className.replace('.', '/') + ".class";
        final URL url = TestUtility.class.getClassLoader()
            .getResource(classFilePath);
        final String file = url.getFile();
        final String classpath = file.substring(0, file.length() - classFilePath.length());
        try{
            CLASS_PATH_URL =  new File(classpath).toURL();
        }catch(MalformedURLException e){
        }
    }
    private static URL getResource(String resource){
        return getURLOnClassPath(PACKAGE_DIR + RESOURCE_DIR + resource);
    }
    private static URL getURLOnClassPath(String resource){
        try{
            return new URL(CLASS_PATH_URL, resource);
        }catch(MalformedURLException e){
            return null;
        }
    }
}