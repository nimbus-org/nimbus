// jp.ossc.nimbus.service.msgresource.MessageResourceConstDefine.java
// Copyright (C) 2002-2005 by Nomura Research Institute,Ltd.  All Rights Reserved.
//
/*****************************************************************************/
/** 更新履歴																**/
/** 																		**/
/*****************************************************************************/
// パッケージ
// インポート
package jp.ossc.nimbus.service.msgresource;

/**
 *	
 *	@author	y-tokuda
 *	@version	1.00 作成：2003/10/28－ y-tokuda<BR>
 *				更新：
 */
public interface MessageResourceDefine {
	public static final String MESSAGE_TAG_NAME = "Message";
	public static final String BLFLOW_TAG_NAME = "BLFlow";
	public static final String SENDDATA_TAG_NAME = "SendData";
	public static final String RECVDATA_TAG_NAME = "RecvData";
	public static final String PROP_TAG_NAME = "prop";
	public static final String PAYLOAD_TAG_NAME = "payload";
	public static final String ESCAPECHAR_TAG_NAME = "escapechar";
	public static final String NEWLINEDEF_TAG_NAME = "newlinedef";
	public static final String BLFLOW_ATT_NAME = "pattern";
	public static final String DATATAG_ATT_NAME = "type";
	public static final String JMSTEXTMSG = "Text";
	public static final String JMSBYTESMSG = "Bytes";
	public static final String JMSSTREAMMSG = "Stream";
	public static final String JMSOBJECTMSG = "Object";
	public static final String JMSMAPMSG = "Map";
	public static final String DISP_ATT = "display";
	public static final String SELECT_KEY_ATT = "selectKey";
	public static final String PAYLOAD_ITEM = "item";
	public static final String PROP_ITEM = "item";
	public static final String PROP_ITEM_NAME_ATT = "name";
	public static final String PROP_ITEM_TYPE_ATT = "type";
	public static final String PROP_ITEM_WRAPPED_TYPE_ATT = "wrappedType";
	public static final String PROP_ITEM_RES_TYPE_ATT = "resourceType";
	public static final String PAYLOAD_ITEM_LENGTH_ATT = "length";
	public static final String PAYLOAD_ITEM_TYPE_ATT = "type";
	public static final String PAYLOAD_ITEM_WRAPPED_TYPE_ATT = "wrappedType";
	public static final String INPUT_FILE_TAG = "inputfile";
	public static final String FILE_VAL = "file";
	public static final String PAYLOAD_RES_TYPE_ATT = "resourceType";
	public static final String PAYLOAD_CLASS_NAME = "className";
	public static final String PAYLOAD_ATTRIBUTE = "attribute";
	public static final String PAYLOAD_ATTRIBUTE_NAME_ATT = "name";
	public static final String PAYLOAD_ATTRIBUTE_RESTYPE_ATT = "resourceType";
}
