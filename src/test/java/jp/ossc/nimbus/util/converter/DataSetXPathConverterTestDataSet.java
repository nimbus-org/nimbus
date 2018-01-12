/*
 * This software is distributed under following license based on modified BSD
 * style license.
 * ----------------------------------------------------------------------
 * 
 * Copyright 2008 The Nimbus Project. All rights reserved.
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
package jp.ossc.nimbus.util.converter;

import jp.ossc.nimbus.beans.dataset.DataSet;
import jp.ossc.nimbus.beans.dataset.XpathPropertySchema;

public class DataSetXPathConverterTestDataSet extends DataSet {
    
    private static final long serialVersionUID = 1L;
    
    private static final String RS_SEPARATOR = "\n";
    private static final String RS_DELIMITTER = ":";
    private static final String PS_DELIMITTER = ",";
    private static final String OMT_OPTION = PS_DELIMITTER + PS_DELIMITTER + PS_DELIMITTER + PS_DELIMITTER;
    
    private static final String SCHEMA_IMPL = XpathPropertySchema.class.getName();

    public static final String PROPERTY0 = "property0";
    public static final String PROPERTY1 = "property1";
    public static final String PROPERTY2 = "property2";
    public static final String PROPERTY3 = "property3";
    public static final String PROPERTY4 = "property4";
    public static final String PROPERTY5 = "property5";
    public static final String PROPERTY6 = "property6";

    private static final String XPATH_PROPERTY1 = "/root/@attribute";
    private static final String XPATH_PROPERTY2 = "/root/children/child[@attribute='ATTR2-3']/text()";
    private static final String XPATH_PROPERTY3 = "/root/children[position()=1]/@attribute";
    private static final String XPATH_PROPERTY4 = "/root/children/child[position()=1]/text()";
    private static final String XPATH_PROPERTY5 = "/root/children/child[position()=2]/text()";
    private static final String XPATH_PROPERTY6 = "/root/children/child[position()=3]/text()";
    
    private static final String SCHEMA_HEADER = 
        RS_DELIMITTER + PROPERTY0 + PS_DELIMITTER + String.class.getName() + RS_SEPARATOR +
        SCHEMA_IMPL + RS_DELIMITTER + PROPERTY1 + PS_DELIMITTER + String.class.getName() + OMT_OPTION + XPATH_PROPERTY1 + RS_SEPARATOR +
        SCHEMA_IMPL + RS_DELIMITTER + PROPERTY2 + PS_DELIMITTER + String.class.getName() + OMT_OPTION + XPATH_PROPERTY2 + RS_SEPARATOR +
        SCHEMA_IMPL + RS_DELIMITTER + PROPERTY3 + PS_DELIMITTER + String.class.getName() + OMT_OPTION + XPATH_PROPERTY3;
    
    private static final String SCHEMA_RECORD_LIST = 
        SCHEMA_IMPL + RS_DELIMITTER + PROPERTY4 + PS_DELIMITTER + String.class.getName() + OMT_OPTION + XPATH_PROPERTY4 + RS_SEPARATOR +
        SCHEMA_IMPL + RS_DELIMITTER + PROPERTY5 + PS_DELIMITTER + String.class.getName() + OMT_OPTION + XPATH_PROPERTY5 + RS_SEPARATOR +
        SCHEMA_IMPL + RS_DELIMITTER + PROPERTY6 + PS_DELIMITTER + String.class.getName() + OMT_OPTION + XPATH_PROPERTY6 + RS_SEPARATOR;
    
    public DataSetXPathConverterTestDataSet() {
        super();
        setHeaderSchema(SCHEMA_HEADER);
        setRecordListSchema(SCHEMA_RECORD_LIST);
    }

}
