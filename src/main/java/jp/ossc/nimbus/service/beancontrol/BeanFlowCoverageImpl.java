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
package jp.ossc.nimbus.service.beancontrol;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.io.StringWriter;
import java.io.PrintWriter;

public class BeanFlowCoverageImpl implements BeanFlowCoverage, Serializable{
    
    private static final long serialVersionUID = -6844140461181542390L;
    
    protected String elementName;
    protected boolean isCovered;
    protected BeanFlowCoverage parent;
    protected List children;
    
    public BeanFlowCoverageImpl(){
    }
    
    public BeanFlowCoverageImpl(String name){
        elementName = name;
    }
    
    public BeanFlowCoverageImpl(BeanFlowCoverageImpl parent){
        this.parent = parent;
        if(parent != null){
            parent.addChild(this);
        }
    }
    
    public String getElementName(){
        return elementName;
    }
    public void setElementName(String name){
        elementName = name;
    }
    
    public boolean isCovered(){
        return isCovered;
    }
    public void cover(){
        isCovered = true;
    }
    
    public long getElementCount(){
        long count = 1;
        if(children != null){
            for(int i = 0, imax = children.size(); i < imax; i++){
                count += ((BeanFlowCoverage)children.get(i)).getElementCount();
            }
        }
        return count;
    }
    
    public long getCoveredElementCount(){
        long count = isCovered ? 1 : 0;
        if(children != null){
            for(int i = 0, imax = children.size(); i < imax; i++){
                count += ((BeanFlowCoverage)children.get(i)).getCoveredElementCount();
            }
        }
        return count;
    }
    
    public BeanFlowCoverage getParent(){
        return parent;
    }
    
    public List getChildren(){
        return children;
    }
    public void addChild(BeanFlowCoverage child){
        if(children == null){
            children = new ArrayList();
        }
        children.add(child);
    }
    
    public void reset(){
        isCovered = false;
        if(children != null){
            for(int i = 0, imax = children.size(); i < imax; i++){
                ((BeanFlowCoverage)children.get(i)).reset();
            }
        }
    }
    
    protected void toString(PrintWriter writer, String indent){
        if(!isCovered){
            writer.print("!");
        }
        writer.print(indent);
        writer.println(elementName);
        if(children != null){
            indent = indent + "    ";
            for(int i = 0, imax = children.size(); i < imax; i++){
                ((BeanFlowCoverageImpl)children.get(i)).toString(writer, indent);
            }
        }
    }
    
    public String toString(){
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        if(!isCovered){
            pw.print("!");
        }
        pw.println(elementName);
        if(children != null){
            for(int i = 0, imax = children.size(); i < imax; i++){
                ((BeanFlowCoverageImpl)children.get(i)).toString(pw, "    ");
            }
        }
        return sw.toString();
    }
}