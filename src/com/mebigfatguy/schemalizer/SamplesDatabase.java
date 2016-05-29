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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SamplesDatabase implements SchemalizerConstants {
    private Map<String, Set<SchemaTypeSample>> samples;

    public SamplesDatabase() {
        samples = new HashMap<String, Set<SchemaTypeSample>>();
    }

    public void addDocumentTypes(Document xmlDoc) throws SchemalizerException {

        Iterator<Node> domIt = new DOMIterator(xmlDoc);
        while (domIt.hasNext()) {
            Node n = domIt.next();

            if ((n instanceof Attr) && (XMLNS.equals(n.getPrefix()) || XSD_INSTANCE_NAMESPACE.equals(n.getNamespaceURI())))
                continue;

            String xpath = getXPath(n);

            SchemaTypeSample sample = new SchemaTypeSample(n);
            Set<SchemaTypeSample> typeSamples = samples.get(xpath);
            if (typeSamples == null) {
                typeSamples = new HashSet<SchemaTypeSample>();
                samples.put(xpath, typeSamples);
            }
            typeSamples.add(sample);
        }
    }

    public Iterator<Set<SchemaTypeSample>> getSamplesIterator() {
        return samples.values().iterator();
    }

    private static String getXPath(Node n) throws SchemalizerException {
        if (n instanceof Document)
            return "";

        if (n instanceof Attr) {
            String parent = getXPath(((Attr) n).getOwnerElement());
            return parent + "/@" + n.getNodeName();
        } else if (n instanceof Element) {
            String parent = getXPath(n.getParentNode());
            return parent + "/" + n.getNodeName();
        }
        throw new SchemalizerException("Invalid node type " + n.getNodeType() + " processed: " + n.getNodeName());
    }

}
