/*
 * Copyright (c) 2007-2018 Siemens AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */

package com.siemens.ct.exi.main.datatype;

import java.io.IOException;

import javax.xml.namespace.QName;

import com.siemens.ct.exi.core.EXIFactory;
import com.siemens.ct.exi.core.FidelityOptions;
import com.siemens.ct.exi.core.context.QNameContext;
import com.siemens.ct.exi.core.datatype.Datatype;
import com.siemens.ct.exi.core.datatype.IntegerDatatype;
import com.siemens.ct.exi.core.datatype.ListDatatype;
import com.siemens.ct.exi.core.datatype.NBitUnsignedIntegerDatatype;
import com.siemens.ct.exi.core.datatype.strings.StringDecoder;
import com.siemens.ct.exi.core.datatype.strings.StringEncoder;
import com.siemens.ct.exi.core.exceptions.EXIException;
import com.siemens.ct.exi.core.helpers.DefaultEXIFactory;
import com.siemens.ct.exi.core.io.channel.DecoderChannel;
import com.siemens.ct.exi.core.io.channel.EncoderChannel;
import com.siemens.ct.exi.core.types.BuiltInType;
import com.siemens.ct.exi.core.types.TypeDecoder;
import com.siemens.ct.exi.core.types.TypeEncoder;
import com.siemens.ct.exi.core.types.TypedTypeDecoder;
import com.siemens.ct.exi.core.types.TypedTypeEncoder;
import com.siemens.ct.exi.core.values.IntegerValue;
import com.siemens.ct.exi.core.values.ListValue;
import com.siemens.ct.exi.core.values.StringValue;
import com.siemens.ct.exi.core.values.Value;
import com.siemens.ct.exi.core.values.ValueType;
import com.siemens.ct.exi.grammars.GrammarFactory;
import com.siemens.ct.exi.main.types.DatatypeMappingTest;

public class ListTest extends AbstractTestCase {

	public ListTest(String testName) {
		super(testName);
	}

	public void testListInteger1() throws IOException, EXIException {
		StringValue s = new StringValue("100 34 56 -23 1567");
		ListDatatype ldtInteger = new ListDatatype(new IntegerDatatype(null),
				null);
		TypeDecoder typeDecoder = new TypedTypeDecoder();
		TypeEncoder typeEncoder = new TypedTypeEncoder();

		// Bit
		{
			boolean valid = typeEncoder.isValid(ldtInteger, s);
			assertTrue(valid);
			EncoderChannel bitEC = getBitEncoder();
			typeEncoder.writeValue(null, bitEC, null);
			bitEC.flush();
			DecoderChannel dc = getBitDecoder();
			Value v1 = typeDecoder.readValue(ldtInteger, null, dc, null);
			assertTrue(v1.getValueType() == ValueType.LIST);
			ListValue lv1 = (ListValue) v1;
			assertTrue(s.equals(lv1.toString()));
		}

		// Byte
		{
			boolean valid = typeEncoder.isValid(ldtInteger, s);
			assertTrue(valid);
			EncoderChannel byteEC = getByteEncoder();
			typeEncoder.writeValue(null, byteEC, null);
			Value v2 = typeDecoder.readValue(ldtInteger, null,
					getByteDecoder(), null);
			assertTrue(v2.getValueType() == ValueType.LIST);
			ListValue lv2 = (ListValue) v2;
			assertTrue(s.equals(lv2.toString()));
		}

	}

	public void testListIntegerLexical1() throws IOException, EXIException {
		StringValue s = new StringValue("0100 034 56 -23 1567");
		ListDatatype ldtInteger = new ListDatatype(new IntegerDatatype(null),
				null);
		TypeEncoder typeEncoder = new TypedTypeEncoder();

		boolean valid = typeEncoder.isValid(ldtInteger, s);
		assertTrue(valid);

		EXIFactory exiFactory = DefaultEXIFactory.newInstance();
		exiFactory.setFidelityOptions(FidelityOptions.createAll());
		exiFactory.setGrammars(GrammarFactory.newInstance()
				.createXSDTypesOnlyGrammars());

		StringEncoder stringEncoder = exiFactory.createStringEncoder();
		StringDecoder stringDecoder = exiFactory.createStringDecoder();
		QName context = new QName("", "intList");
		QNameContext qncContext = new QNameContext(0, 0, context);

		TypeEncoder te = exiFactory.createTypeEncoder();
		TypeDecoder td = exiFactory.createTypeDecoder();

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		te.isValid(ldtInteger, s);
		te.writeValue(qncContext, bitEC, stringEncoder);
		bitEC.flush();
		Value v1 = td.readValue(ldtInteger, qncContext, getBitDecoder(),
				stringDecoder);
		assertTrue(s.equals(v1.toString()));

		// Byte
		EncoderChannel byteEC = getByteEncoder();
		te.isValid(ldtInteger, s);
		te.writeValue(qncContext, byteEC, stringEncoder);
		Value v2 = td.readValue(ldtInteger, qncContext, getByteDecoder(),
				stringDecoder);
		assertTrue(s.equals(v2.toString()));
	}

	// encodes special chars as well
	public void testListIntegerLexical2() throws IOException, EXIException {
		char special = '\u03D7';
		StringValue s = new StringValue("0100" + special);
		ListDatatype ldtInteger = new ListDatatype(new IntegerDatatype(null),
				null);

		// boolean valid = ldtInteger.isValid(s);
		// assertTrue(valid);

		EXIFactory exiFactory = DefaultEXIFactory.newInstance();
		exiFactory.setFidelityOptions(FidelityOptions.createAll());
		exiFactory.setGrammars(GrammarFactory.newInstance()
				.createXSDTypesOnlyGrammars());

		StringEncoder stringEncoder = exiFactory.createStringEncoder();
		StringDecoder stringDecoder = exiFactory.createStringDecoder();
		QName context = new QName("", "intList");
		QNameContext qncContext = new QNameContext(0, 0, context);

		TypeEncoder te = exiFactory.createTypeEncoder();
		TypeDecoder td = exiFactory.createTypeDecoder();

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		te.isValid(ldtInteger, s);
		te.writeValue(qncContext, bitEC, stringEncoder);
		bitEC.flush();
		Value v1 = td.readValue(ldtInteger, qncContext, getBitDecoder(),
				stringDecoder);
		assertTrue(s.equals(v1.toString()));

		// Byte
		EncoderChannel byteEC = getByteEncoder();
		te.isValid(ldtInteger, s);
		te.writeValue(qncContext, byteEC, stringEncoder);
		Value v2 = td.readValue(ldtInteger, qncContext, getByteDecoder(),
				stringDecoder);
		assertTrue(s.equals(v2.toString()));
	}

	public void testListNBit1() throws IOException, EXIException {
		StringValue s = new StringValue("+1 0 127 -127");
		String sRes = "1 0 127 -127";
		IntegerValue min = IntegerValue.valueOf(-128);
		IntegerValue max = IntegerValue.valueOf(127);
		ListDatatype ldtInteger = new ListDatatype(
				new NBitUnsignedIntegerDatatype(min, max, null), null);
		TypeDecoder typeDecoder = new TypedTypeDecoder();
		TypeEncoder typeEncoder = new TypedTypeEncoder();

		// Bit
		{
			boolean valid = typeEncoder.isValid(ldtInteger, s);
			assertTrue(valid);
			EncoderChannel bitEC = getBitEncoder();
			typeEncoder.writeValue(null, bitEC, null);
			bitEC.flush();
			Value v1 = typeDecoder.readValue(ldtInteger, null, getBitDecoder(),
					null);
			assertTrue(v1.getValueType() == ValueType.LIST);
			ListValue lv1 = (ListValue) v1;
			assertTrue(sRes.equals(lv1.toString()));
		}

		// Byte
		{
			boolean valid = typeEncoder.isValid(ldtInteger, s);
			assertTrue(valid);
			EncoderChannel byteEC = getByteEncoder();
			typeEncoder.writeValue(null, byteEC, null);
			Value v2 = typeDecoder.readValue(ldtInteger, null,
					getByteDecoder(), null);
			assertTrue(v2.getValueType() == ValueType.LIST);
			ListValue lv2 = (ListValue) v2;
			assertTrue(sRes.equals(lv2.toString()));
		}

	}

	public void testListGMonthDayUnion1() throws IOException, EXIException {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:simpleType name='gMonthDay'>"
				+ "  <xs:restriction base='xs:gMonthDay'>"
				+ "   <xs:enumeration value='--01-01'/>"
				+ "   <xs:enumeration value='--05-01'/>"
				+ "   <xs:enumeration value='--05-08'/>"
				+ "   <xs:enumeration value='--07-14'/>"
				+ "   <xs:enumeration value='--08-15'/>"
				+ "   <xs:enumeration value='--11-01'/>"
				+ "   <xs:enumeration value='--11-11'/>"
				+ "   <xs:enumeration value='--12-25'/>"
				+ "  </xs:restriction>"
				+ " </xs:simpleType>"
				+ ""
				+ "  <xs:simpleType name='List'>"
				+ "    <xs:list itemType='gMonthDay'/>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"List", "");
		TypeEncoder typeEncoder = new TypedTypeEncoder();

		assertTrue(dt.getBuiltInType() == BuiltInType.LIST);
		// EnumerationDatatype enumDt = (EnumerationDatatype) dt;

		assertTrue(typeEncoder.isValid(dt, new StringValue(
				"  --12-25  --08-15  --01-01  --07-14   ")));

		assertFalse(typeEncoder.isValid(dt, new StringValue("00")));
	}

	public void testListEnum1() throws IOException, EXIException {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:simpleType name='listOfIDsEnum'>"
				+ "  <xs:restriction>"
				+ "    <xs:simpleType>"
				+ "      <xs:list>"
				+ "        <xs:simpleType>"
				+ "          <xs:restriction base='xs:ID'/>"
				+ "        </xs:simpleType>"
				+ "      </xs:list>"
				+ "    </xs:simpleType>"
				+ "    <xs:enumeration value='AB BC CD'/>"
				+ "    <xs:enumeration value='EF FG GH'/>"
				+ "    <xs:enumeration value='IJ JK KL'/>"
				+ "  </xs:restriction>" + "  </xs:simpleType>" + "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"listOfIDsEnum", "");
		TypeEncoder typeEncoder = new TypedTypeEncoder();

		assertTrue(dt.getBuiltInType() == BuiltInType.LIST);
		ListDatatype listDt = (ListDatatype) dt;
		assertTrue(listDt.getListDatatype().getBuiltInType() == BuiltInType.STRING);

		assertTrue(typeEncoder.isValid(dt, new StringValue("  AB  BC  CD   ")));
		assertTrue(typeEncoder.isValid(dt, new StringValue("  AB  BC     ")));
		assertTrue(typeEncoder.isValid(dt, new StringValue("  KL   ")));

		// assertFalse(dt.isValid(new StringValue("00")));
		assertTrue(typeEncoder.isValid(dt, new StringValue("00")));
	}

	public void testListEnum2() throws IOException, EXIException {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:simpleType name='listOfIntsEnum'>"
				+ "  <xs:restriction>"
				+ "    <xs:simpleType>"
				+ "      <xs:list>"
				+ "        <xs:simpleType>"
				+ "          <xs:restriction base='xs:int'/>"
				+ "        </xs:simpleType>"
				+ "      </xs:list>"
				+ "    </xs:simpleType>"
				+ "    <xs:enumeration value='1 2 3'/>"
				+ "    <xs:enumeration value='4 5 6'/>"
				+ "    <xs:enumeration value='7 8 9'/>"
				+ "  </xs:restriction>"
				+ "  </xs:simpleType>" + "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"listOfIntsEnum", "");
		TypeEncoder typeEncoder = new TypedTypeEncoder();

		assertTrue(dt.getBuiltInType() == BuiltInType.LIST);
		ListDatatype listDt = (ListDatatype) dt;
		assertTrue(listDt.getListDatatype().getBuiltInType() == BuiltInType.INTEGER);

		assertTrue(typeEncoder.isValid(dt, new StringValue("  1  2  3   ")));
		assertTrue(typeEncoder.isValid(dt, new StringValue("  4  5   6  ")));
		assertTrue(typeEncoder.isValid(dt, new StringValue("  9   ")));
		assertTrue(typeEncoder.isValid(dt, new StringValue(" 123")));

		assertFalse(typeEncoder.isValid(dt, new StringValue("XXX")));
	}

	public void testListFloat1() throws IOException, EXIException {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='List'>"
				+ "    <xs:list itemType='xs:float'/>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"List", "");
		TypeEncoder typeEncoder = new TypedTypeEncoder();

		assertTrue(dt.getBuiltInType() == BuiltInType.LIST);
		// EnumerationDatatype enumDt = (EnumerationDatatype) dt;

		assertTrue(typeEncoder.isValid(dt, new StringValue(
				"  1e4 -10000 5.234e-2   ")));

		assertFalse(typeEncoder.isValid(dt, new StringValue("bla")));
	}

	public void testListFloat2() throws IOException, EXIException {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='List'>"
				+ "    <xs:list itemType='xs:float'/>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"List", "");
		TypeEncoder typeEncoder = new TypedTypeEncoder();

		assertTrue(dt.getBuiltInType() == BuiltInType.LIST);
		// EnumerationDatatype enumDt = (EnumerationDatatype) dt;

		assertTrue(typeEncoder.isValid(dt, new StringValue(
				"  1e4 -10000 5.234e-2 \n 11.22 \t\t 4 \r\n999  ")));

		assertFalse(typeEncoder.isValid(dt, new StringValue("bla")));
	}

}