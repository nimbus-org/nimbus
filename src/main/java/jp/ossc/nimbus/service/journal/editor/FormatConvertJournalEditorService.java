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
package jp.ossc.nimbus.service.journal.editor;

import java.io.Serializable;

import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;
import jp.ossc.nimbus.util.converter.FormatConverter;

/**
 * フォーマットコンバーターを使用し、オブジェクトをフォーマットするエディタ。
 * <p>
 *
 * @author M.Ishida
 */
public class FormatConvertJournalEditorService extends ImmutableJournalEditorServiceBase implements
        FormatConvertJournalEditorServiceMBean, Serializable {

    private static final long serialVersionUID = 7578630921939702576L;

    protected ServiceName formatConverterServiceName;
    protected FormatConverter formatConverter;

    public ServiceName getFormatConverterServiceName() {
        return formatConverterServiceName;
    }

    public void setFormatConverterServiceName(ServiceName name) {
        formatConverterServiceName = name;
    }

    public FormatConverter getFormatConverter() {
        return formatConverter;
    }

    public void setFormatConverter(FormatConverter converter) {
        formatConverter = converter;
    }

    public void startService() throws Exception {

        if (formatConverterServiceName == null && formatConverter == null) {
            throw new IllegalArgumentException(
                    "It is necessary to specify FormatConverterServiceName or FormatConverter.");
        }
        if (formatConverterServiceName != null) {
            formatConverter = (FormatConverter) ServiceManagerFactory.getServiceObject(formatConverterServiceName);
        }
    }

    protected String toString(EditorFinder finder, Object key, Object value, StringBuilder buf) {
        if (value == null) {
            return NULL_STRING;
        }
        buf.append(formatConverter.convert(value));
        return buf.toString();
    }

}