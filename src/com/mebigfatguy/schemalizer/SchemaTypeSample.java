/*
 * Copyright 2005-2015 Dave Brosius
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

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class SchemaTypeSample implements SchemalizerConstants {
    private final String elementName;
    private Set<String> attributes;
    private List<String> subElements;
    private String value;

    public SchemaTypeSample(Node n) throws SchemalizerException {
        elementName = n.getNodeName();
        if (n instanceof Attr) {
            attributes = null;
            subElements = null;
            value = n.getNodeValue();
        } else if (n instanceof Element) {
            NamedNodeMap nnm = ((Element) n).getAttributes();
            if (nnm.getLength() > 0) {
                attributes = new LinkedHashSet<String>();
                for (int i = 0; i < nnm.getLength(); i++) {
                    Node a = nnm.item(i);
                    if (!XMLNS.equals(a.getPrefix()) && !XSD_INSTANCE_NAMESPACE.equals(a.getNamespaceURI()))
                        attributes.add(a.getNodeName());
                }
            } else
                attributes = null;

            value = "";
            subElements = new ArrayList<String>();
            Node child = n.getFirstChild();
            while (child != null) {
                if (child instanceof Text)
                    value += child.getNodeValue();
                else if (child instanceof Element)
                    subElements.add(child.getNodeName());
                child = child.getNextSibling();
            }
            if (subElements.isEmpty())
                subElements = null;

            if (value.length() == 0)
                value = null;

        } else
            throw new SchemalizerException("Unexpected node type " + n.getNodeType() + " parsed for node " + n.getNodeName());
    }

    public String getName() {
        return elementName;
    }

    public Set<String> getAttributes() {
        return attributes;
    }

    public List<String> getSubElements() {
        return subElements;
    }

    public String getValue() {
        return value;
    }
}
