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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class SchemaTypeSampleTest {
    private Document d;

    @Before
    public void setUp() throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        d = db.newDocument();
    }

    @Test
    public void testSimpleSchemaTypeSample() throws Exception {
        Element root = d.createElement("root");
        Text data = d.createTextNode("simple");
        root.appendChild(data);

        SchemaTypeSample s = new SchemaTypeSample(root);
        Assert.assertEquals("root", s.getName());
        Assert.assertEquals(null, s.getAttributes());
        Assert.assertEquals(null, s.getSubElements());
        Assert.assertEquals("simple", s.getValue());
    }

    @Test
    public void testComplexEmptyTypeSample() throws Exception {
        Element root = d.createElement("root");
        root.setAttribute("one", "true");

        SchemaTypeSample s = new SchemaTypeSample(root);
        Assert.assertEquals("root", s.getName());
        Assert.assertEquals("one", s.getAttributes().iterator().next());
        Assert.assertEquals(null, s.getSubElements());
        Assert.assertEquals(null, s.getValue());
    }

    @Test
    public void testComplexTextOnlyTypeSample() throws Exception {
        Element root = d.createElement("root");
        Text data = d.createTextNode("textonly");
        root.appendChild(data);
        root.setAttribute("one", "true");

        SchemaTypeSample s = new SchemaTypeSample(root);
        Assert.assertEquals("root", s.getName());
        Assert.assertEquals("one", s.getAttributes().iterator().next());
        Assert.assertEquals(null, s.getSubElements());
        Assert.assertEquals("textonly", s.getValue());
    }

    @Test
    public void testComplexElementsOnlyTypeSample() throws Exception {
        Element root = d.createElement("root");
        Element child = d.createElement("child");
        root.appendChild(child);
        root.setAttribute("one", "true");

        SchemaTypeSample s = new SchemaTypeSample(root);
        Assert.assertEquals("root", s.getName());
        Assert.assertEquals("child", s.getSubElements().get(0));
        Assert.assertEquals("one", s.getAttributes().iterator().next());
        Assert.assertEquals(null, s.getValue());
    }

    @Test
    public void testComplexMixedTypeSample() throws Exception {
        Element root = d.createElement("root");
        Element child = d.createElement("child");
        Text data = d.createTextNode("textonly");
        root.appendChild(data);
        root.appendChild(child);
        root.setAttribute("one", "true");

        SchemaTypeSample s = new SchemaTypeSample(root);
        Assert.assertEquals("root", s.getName());
        Assert.assertEquals("child", s.getSubElements().get(0));
        Assert.assertEquals("textonly", s.getValue());
    }
}
