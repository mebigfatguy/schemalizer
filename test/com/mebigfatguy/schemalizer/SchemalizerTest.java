/*
 * Copyright 2005-2006 Dave Brosius
 *
 * Licensed under the GNU Lesser General Public License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/copyleft/lesser.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mebigfatguy.schemalizer;

import java.io.ByteArrayOutputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class SchemalizerTest
{
	private DocumentBuilder db = null;
	
	@Before
	public void setUp() throws Exception
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		db = dbf.newDocumentBuilder();
	}
	
	@Test
	public void testSimpleDocument() throws Exception
	{
		Document d = db.newDocument();
		Element root = d.createElement("root");
		d.appendChild(root);
		
		Element el = d.createElement("simple");
		root.appendChild(el);		
		el.appendChild(d.createTextNode("empty"));
		
		el = d.createElement("simple");
		root.appendChild(el);		
		el.appendChild(d.createTextNode("empty"));
		
		el = d.createElement("simple");
		root.appendChild(el);		
		el.appendChild(d.createTextNode("empty"));
		
		el = d.createElement("simple");
		root.appendChild(el);		
		el.appendChild(d.createTextNode("empty"));
		
		el = d.createElement("simple");
		root.appendChild(el);
		el.appendChild(d.createTextNode("empty"));
		
		Schemalizer s = new Schemalizer();
		s.addSample(d);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		s.build(baos);
		String xsd = new String(baos.toByteArray());
		String cr = System.getProperty("line.separator");
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + cr +
						  "<xsd:schema xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" + cr +
						  "    <xsd:complexType name=\"RootClass\">" + cr +
						  "        <xsd:sequence>" + cr +
						  "            <xsd:element maxOccurs=\"5\" minOccurs=\"5\" name=\"simple\" type=\"SimpleClass\"/>" + cr +
						  "        </xsd:sequence>" + cr +
					      "    </xsd:complexType>" + cr +
					      "    <xsd:simpleType name=\"SimpleClass\">" + cr +
					      "        <xsd:restriction base=\"xsd:string\">" + cr +
					      "            <xsd:enumeration value=\"empty\"/>" + cr +
					      "        </xsd:restriction>" + cr +
					      "    </xsd:simpleType>" + cr +
					      "    <xsd:element name=\"root\" value=\"RootClass\"/>" + cr +
						  "</xsd:schema>" + cr;
		
		Assert.assertEquals(expected, xsd);
	}
	
	@Test
	public void testComplexEmptyDocument() throws Exception
	{
		Document d = db.newDocument();
		Element complexEmpty = d.createElement("empty");
		complexEmpty.setAttribute("att", "attvalue");
		d.appendChild(complexEmpty);
		
		Schemalizer s = new Schemalizer();
		s.addSample(d);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		s.build(baos);
		String xsd = new String(baos.toByteArray());
		String cr = System.getProperty("line.separator");
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + cr +
						  "<xsd:schema xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" + cr +
						  "    <xsd:simpleType name=\"AttClass\">" + cr +
						  "        <xsd:restriction base=\"xsd:string\">" + cr +
						  "            <xsd:enumeration value=\"attvalue\"/>" + cr +
						  "        </xsd:restriction>" + cr +
						  "    </xsd:simpleType>" + cr +
						  "    <xsd:complexType name=\"EmptyClass\">" + cr +
						  "        <xsd:attribute name=\"att\" type=\"AttClass\" use=\"required\"/>" + cr +
						  "    </xsd:complexType>" + cr +
						  "    <xsd:element name=\"empty\" value=\"EmptyClass\"/>" + cr +
						  "</xsd:schema>" + cr;
		
		Assert.assertEquals(expected, xsd);
	}
	
	@Test
	public void testComplexTextOnlyDocument() throws Exception
	{
		Document d = db.newDocument();
		Element complexEmpty = d.createElement("empty");
		complexEmpty.setAttribute("att", "attvalue");
		d.appendChild(complexEmpty);
		Text data = d.createTextNode("text");
		complexEmpty.appendChild(data);
		
		Schemalizer s = new Schemalizer();
		s.addSample(d);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		s.build(baos);
		String xsd = new String(baos.toByteArray());
		String cr = System.getProperty("line.separator");
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + cr +
							"<xsd:schema xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" + cr +
								"<xsd:simpleType name=\"AttClass\">" + cr +
									"<xsd:restriction base=\"xsd:string\">" + cr +
										"<xsd:enumeration value=\"attvalue\"/>" + cr +
									"</xsd:restriction>" + cr +
								"</xsd:simpleType>" + cr +
								"<xsd:simpleType name=\"SimpleEmptyClass\">" + cr +
									"<xsd:restriction base=\"xsd:string\">" + cr +
										"<xsd:enumeration value=\"text\"/>" + cr +
									"</xsd:restriction>" + cr +
								"</xsd:simpleType>" + cr +
								"<xsd:complexType name=\"EmptyClass\">" + cr +
									"<xsd:simpleContent>" + cr +
										"<xsd:extension base=\"SimpleEmptyClass\">" + cr +
											"<xsd:attribute name=\"att\" type=\"AttClass\" use=\"required\"/>" + cr +
										"</xsd:extension>" + cr +
									"</xsd:simpleContent>" + cr +
								"</xsd:complexType>" + cr +
								"<xsd:element name=\"empty\" value=\"EmptyClass\"/>" + cr +
							"</xsd:schema>" + cr;
		
		Assert.assertEquals(expected, xsd);
	}
	
	@Test
	public void testComplexElementsOnlyDocument() throws Exception
	{
		Document d = db.newDocument();
		Element complexElementsOnly = d.createElement("main");
		complexElementsOnly.setAttribute("att", "attvalue");
		d.appendChild(complexElementsOnly);
		Element child = d.createElement("child");
		complexElementsOnly.appendChild(child);
		
		Schemalizer s = new Schemalizer();
		s.addSample(d);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		s.build(baos);
		String xsd = new String(baos.toByteArray());
		String cr = System.getProperty("line.separator");
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + cr +
							"<xsd:schema xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" + cr +
								"<xsd:simpleType name=\"AttClass\">" + cr +
									"<xsd:restriction base=\"xsd:string\">" + cr +
										"<xsd:enumeration value=\"attvalue\"/>" + cr +
									"</xsd:restriction>" + cr +
								"</xsd:simpleType>" + cr +
								"<xsd:complexType name=\"MainClass\">" + cr +
									"<xsd:sequence>" + cr +
										"<xsd:element maxOccurs=\"1\" minOccurs=\"1\" name=\"child\" type=\"ChildClass\"/>" + cr +
									"</xsd:sequence>" + cr +
									"<xsd:attribute name=\"att\" type=\"AttClass\" use=\"required\"/>" + cr +
								"</xsd:complexType>" + cr +
								"<xsd:simpleType name=\"ChildClass\">" + cr +
									"<xsd:restriction base=\"xsd:string\"/>" + cr +
								"</xsd:simpleType>" + cr +
								"<xsd:element name=\"main\" value=\"MainClass\"/>" + cr +
							"</xsd:schema>" + cr;
		
		Assert.assertEquals(expected, xsd);
	}
	
	@Test
	public void testComplexMixedDocument() throws Exception
	{
		Document d = db.newDocument();
		Element complexMixed = d.createElement("main");
		complexMixed.setAttribute("att", "attvalue");
		d.appendChild(complexMixed);
		Element child = d.createElement("child");
		complexMixed.appendChild(child);
		Text data = d.createTextNode("test");
		complexMixed.appendChild(data);
		
		Schemalizer s = new Schemalizer();
		s.addSample(d);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		s.build(baos);
		String xsd = new String(baos.toByteArray());
		String cr = System.getProperty("line.separator");
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + cr +
							"<xsd:schema xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" + cr +
								"<xsd:simpleType name=\"AttClass\">" + cr +
									"<xsd:restriction base=\"xsd:string\">" + cr +
										"<xsd:enumeration value=\"attvalue\"/>" + cr +
									"</xsd:restriction>" + cr +
								"</xsd:simpleType>" + cr +
								"<xsd:complexType mixed=\"true\" name=\"MainClass\">" + cr +
									"<xsd:sequence>" + cr +
										"<xsd:element maxOccurs=\"1\" minOccurs=\"1\" name=\"child\" type=\"ChildClass\"/>" + cr +
									"</xsd:sequence>" + cr +
									"<xsd:attribute name=\"att\" type=\"AttClass\" use=\"required\"/>" + cr +
								"</xsd:complexType>" + cr +
								"<xsd:simpleType name=\"ChildClass\">" + cr +
									"<xsd:restriction base=\"xsd:string\"/>" + cr +
								"</xsd:simpleType>" + cr +
								"<xsd:element name=\"main\" value=\"MainClass\"/>" + cr +
							"</xsd:schema>" + cr;
		
		Assert.assertEquals(expected, xsd);
	}	
	
	@Test
	public void testSampleDocument() throws Exception
	{
		Document d = db.newDocument();
		Element book = d.createElement("Book");
		book.setAttribute("name", "War and Peace");
		book.setAttribute("isbn", "1295-23513-293952-249");
		d.appendChild(book);
		
		Element chapter = d.createElement("Chapter");
		chapter.setAttribute("id", "1");
		Element title = d.createElement("Title");
		Text data = d.createTextNode("War");
		title.appendChild(data);
		chapter.appendChild(title);
		book.appendChild(chapter);
		
		chapter = d.createElement("Chapter");
		chapter.setAttribute("id", "2");
		book.appendChild(chapter);
		title = d.createElement("Title");
		data = d.createTextNode("and");
		title.appendChild(data);
		chapter.appendChild(title);
		book.appendChild(chapter);
		
		chapter = d.createElement("Chapter");
		chapter.setAttribute("id", "3");
		book.appendChild(chapter);
		title = d.createElement("Title");
		data = d.createTextNode("Peace");
		title.appendChild(data);
		chapter.appendChild(title);
		book.appendChild(chapter);
		
		Schemalizer s = new Schemalizer();
		s.addSample(d);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		s.build(baos);
		String xsd = new String(baos.toByteArray());
		String cr = System.getProperty("line.separator");
		String expected = 	"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + cr +
							"<xsd:schema xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" + cr +
								"<xsd:simpleType name=\"IsbnClass\">" + cr +
									"<xsd:restriction base=\"xsd:string\">" + cr +
										"<xsd:enumeration value=\"1295-23513-293952-249\"/>" + cr +
				                    "</xsd:restriction>" + cr + 
				                "</xsd:simpleType>" + cr +
				                "<xsd:complexType name=\"ChapterClass\">" + cr +
				                	"<xsd:sequence>" + cr +
				                		"<xsd:element maxOccurs=\"1\" minOccurs=\"1\" name=\"Title\" type=\"TitleClass\"/>" + cr +
				                    "</xsd:sequence>" + cr +
				                    "<xsd:attribute name=\"id\" type=\"IdClass\" use=\"required\"/>" + cr +
				                "</xsd:complexType>" + cr +
				                "<xsd:simpleType name=\"NameClass\">" + cr +
				                	"<xsd:restriction base=\"xsd:string\">" + cr +
				                		"<xsd:enumeration value=\"War and Peace\"/>" + cr +
				                    "</xsd:restriction>" + cr +
				                "</xsd:simpleType>" + cr +
				                "<xsd:simpleType name=\"TitleClass\">" + cr +
				                	"<xsd:restriction base=\"xsd:string\">" + cr +
				                	"<xsd:enumeration value=\"and\"/>" + cr +
			                		"<xsd:enumeration value=\"Peace\"/>" + cr +
			                		"<xsd:enumeration value=\"War\"/>" + cr +
				                    "</xsd:restriction>" + cr +
				                "</xsd:simpleType>" + cr +
				                "<xsd:complexType name=\"BookClass\">" + cr +
				                	"<xsd:sequence>" + cr +
				                		"<xsd:element maxOccurs=\"3\" minOccurs=\"1\" name=\"Chapter\" type=\"ChapterClass\"/>" + cr +
				                    "</xsd:sequence>" + cr +
				                    "<xsd:attribute name=\"isbn\" type=\"IsbnClass\" use=\"required\"/>" + cr +
				                    "<xsd:attribute name=\"name\" type=\"NameClass\" use=\"required\"/>" + cr +
				                "</xsd:complexType>" + cr +
				                "<xsd:simpleType name=\"IdClass\">" + cr +
				                	"<xsd:restriction base=\"xsd:string\">" + cr +
				                		"<xsd:enumeration value=\"3\"/>" + cr +
				                		"<xsd:enumeration value=\"2\"/>" + cr +
				                		"<xsd:enumeration value=\"1\"/>" + cr +
				                    "</xsd:restriction>" + cr +
				                "</xsd:simpleType>" + cr +
				                "<xsd:element name=\"Book\" value=\"BookClass\"/>" + cr +
				            "</xsd:schema>";
				
		Assert.assertEquals(expected, xsd);

	}
}

