/*
 * Copyright (C) 2007, 2008 Siemens AG
 *
 * This program and its interfaces are free software;
 * you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.siemens.ct.exi.datatype.decoder;

import java.io.IOException;

import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.datatype.TypeCoder;
import com.siemens.ct.exi.datatype.stringtable.StringTableDecoder;
import com.siemens.ct.exi.io.channel.DecoderChannel;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.2.20081117
 */

public interface TypeDecoder extends TypeCoder {
	
	public StringTableDecoder getStringTable();
	
	public void setStringTable(StringTableDecoder stringTable);

	public String readTypeValidValue(Datatype datatype, DecoderChannel dc,
			String namespaceURI, String localName) throws IOException;

	public String readValueAsString(DecoderChannel dc, String namespaceURI,
			String localName) throws IOException;

	public String readStringAsLocalHit(DecoderChannel dc, String namespaceURI,
			String localName) throws IOException;

	public String readStringAsGlobalHit(DecoderChannel dc) throws IOException;

	public String readStringAsMiss(DecoderChannel dc, String namespaceURI,
			String localName, int slen) throws IOException;

}
