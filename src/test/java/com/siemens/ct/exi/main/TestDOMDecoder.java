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

package com.siemens.ct.exi.main;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Node;

import com.siemens.ct.exi.core.EXIFactory;
import com.siemens.ct.exi.main.api.dom.DOMBuilder;
import com.siemens.ct.exi.main.data.AbstractTestCase;

public class TestDOMDecoder extends AbstractTestDecoder {
	protected TransformerFactory tf;
	protected boolean isFragment;
	protected DOMBuilder domBuilder;

	public TestDOMDecoder(EXIFactory ef) throws ParserConfigurationException {
		super();

		tf = TransformerFactory.newInstance();

		domBuilder = new DOMBuilder(ef);
		isFragment = ef.isFragment();
	}

	// @Override
	// public void setupEXIReader(EXIFactory ef) throws Exception {
	// domBuilder = new DOMBuilder(ef);
	// isFragment = ef.isFragment();
	// }

	public static void nodeToWriter(Node n, Writer writer)
			throws TransformerException {
		// set up a transformer
		TransformerFactory transfac = TransformerFactory.newInstance();
		Transformer trans = transfac.newTransformer();
		// output options
		trans.setOutputProperty(OutputKeys.METHOD, "xml");
		// due to fragments
		trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		// remaining keys
		trans.setOutputProperty(OutputKeys.ENCODING, AbstractTestCase.ENCODING); // "ASCII"
		// "UTF-8"
		trans.setOutputProperty(OutputKeys.INDENT, "yes");

		// // TEST DOCTYPE
		// if ( n.getNodeType() == Node.DOCUMENT_NODE ) {
		// Document doc = (Document)n;
		// DocumentType dt = doc.getDoctype();
		//
		// String publicID = dt.getPublicId();
		// if ( publicID != null && publicID.length() > 0 ) {
		// trans.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, publicID);
		// }
		// String systemID = dt.getSystemId();
		// if (systemID != null && systemID.length() > 0) {
		// trans.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, systemID);
		// }
		// }

		// create string from xml tree
		StreamResult result = new StreamResult(writer);
		DOMSource source = new DOMSource(n);
		trans.transform(source, result);
	}

	@Override
	public void decodeTo(InputStream exiDocument, OutputStream xmlOutput)
			throws Exception {
		// decode to DOM
		Node doc;
		if (isFragment) {
			doc = domBuilder.parseFragment(exiDocument);
		} else {
			doc = domBuilder.parse(exiDocument);
		}
		// create string from xml tree
		StringWriter sw = new StringWriter();
		nodeToWriter(doc, sw);
		String xmlString = sw.toString();

		// System.out.println(xmlString);
		xmlOutput.write(xmlString.getBytes());
	}

	public static void main(String[] args) throws Exception {
		// create test-decoder
		TestDOMDecoder testDecoder = new TestDOMDecoder(
				TestDOMDecoder.getQuickTestEXIactory());

		// get factory
		// EXIFactory ef = testDecoder.getQuickTestEXIactory();

		// exi document
		InputStream exiDocument = new FileInputStream(
				QuickTestConfiguration.getExiLocation());

		// decoded xml output
		String decodedXMLLocation = QuickTestConfiguration.getExiLocation()
				+ ".xml";
		OutputStream xmlOutput = new FileOutputStream(decodedXMLLocation);

		// decode EXI to XML
		// testDecoder.setupEXIReader(ef);
		testDecoder.decodeTo(exiDocument, xmlOutput);

		System.out.println("[DEC_DOM] "
				+ QuickTestConfiguration.getExiLocation() + " --> "
				+ decodedXMLLocation);
	}

}
