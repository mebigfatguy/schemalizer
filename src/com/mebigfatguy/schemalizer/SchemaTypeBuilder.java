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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SchemaTypeBuilder implements SchemalizerConstants
{
	public static final int MAX_OCCURS_LIMIT = 5;
	private Document d;
	private Element schemaRoot;
	
	public SchemaTypeBuilder()
		throws SchemalizerException
	{
		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			d = db.newDocument();
			schemaRoot = d.createElementNS(XSD_NAMESPACE, XSD_SCHEMA);
			d.appendChild(schemaRoot);

		}
		catch (Exception e)
		{
			throw new SchemalizerException(e);
		}
	}
	
	public void addTypeGraph(TypeGraph graph)
		throws SchemalizerException
	{
		Element typeNode;
		String category = graph.getTypeCategory();
		
		if (SIMPLE_CATEGORY.equals(category))
		{
			typeNode = buildSimpleType(graph, SchemalizerUtils.getSchemaTypeName(graph.getName()));
		}
		else if (COMPLEX_EMPTY_CATEGORY.equals(category))
		{
			typeNode = d.createElementNS(XSD_NAMESPACE, XSD_COMPLEXTYPE);
			typeNode.setAttribute(NAME, SchemalizerUtils.getSchemaTypeName(graph.getName()));
			appendAttributes(typeNode, graph);
		}
		else if (COMPLEX_TEXT_ONLY_CATEGORY.equals(category))
		{
			String typeName = SchemalizerUtils.getSchemaTypeName(graph.getName());
			String simpleTypeName = "Simple" + typeName;
			typeNode = buildSimpleType(graph, simpleTypeName);
			schemaRoot.appendChild(typeNode);
			typeNode = d.createElementNS(XSD_NAMESPACE, XSD_COMPLEXTYPE);
			typeNode.setAttribute(NAME, typeName);
			Element simpleContent = d.createElementNS(XSD_NAMESPACE, XSD_SIMPLECONTENT);
			typeNode.appendChild(simpleContent);
			Element extension = d.createElementNS(XSD_NAMESPACE, XSD_EXTENSION);
			extension.setAttribute(BASE, simpleTypeName);
			simpleContent.appendChild(extension);
			appendAttributes(extension, graph);
		}
		else if (COMPLEX_ELEMENTS_ONLY_CATEGORY.equals(category))
		{
			typeNode = d.createElementNS(XSD_NAMESPACE, XSD_COMPLEXTYPE);
			typeNode.setAttribute(NAME, SchemalizerUtils.getSchemaTypeName(graph.getName()));
			appendElements(typeNode, graph);
			appendAttributes(typeNode, graph);
		}
		else if (COMPLEX_MIXED_CATEGORY.equals(category))
		{
			typeNode = d.createElementNS(XSD_NAMESPACE, XSD_COMPLEXTYPE);
			typeNode.setAttribute(NAME, SchemalizerUtils.getSchemaTypeName(graph.getName()));
			typeNode.setAttribute(MIXED, "true");
			appendElements(typeNode, graph);
			appendAttributes(typeNode, graph);
		}
		else
			throw new SchemalizerException("Invalid type category: " + category);
		
		schemaRoot.appendChild(typeNode);
	}
	
	public void finish(String documentType)
	{
		Element docEl = d.createElementNS(XSD_NAMESPACE, XSD_ELEMENT);
		docEl.setAttribute(NAME, documentType);
		docEl.setAttribute(TYPE, SchemalizerUtils.getSchemaTypeName(documentType));
		schemaRoot.appendChild(docEl);
		
		removeSimpleTypeAliases();
	}
	
	public Document getDocument()
	{
		return d;
	}
	
	private Element buildSimpleType(TypeGraph graph, String typeName)
	{
		SimpleTypeInferrer inferer = new SimpleTypeInferrer(graph.getValues());
		Validator simpleTypeValidator = inferer.findSimpleType();
				
		Element typeNode = d.createElementNS(XSD_NAMESPACE, XSD_SIMPLETYPE);
		typeNode.setAttribute(NAME, typeName);
		Element restriction = d.createElementNS(XSD_NAMESPACE, XSD_RESTRICTION);
		restriction.setAttribute(BASE, inferer.getBaseType(simpleTypeValidator.toString()));
		typeNode.appendChild(restriction);
		
		simpleTypeValidator.addSubElements(d, restriction);

		return typeNode;
	}
	
	private void appendElements(Element root, TypeGraph graph)
	{
		Element indicator;
		
		if (graph.isAmbiguous())
			indicator = d.createElementNS(XSD_NAMESPACE, XSD_ALL);
		else
			indicator = d.createElementNS(XSD_NAMESPACE, XSD_SEQUENCE);
		root.appendChild(indicator);
		List<String> subElements = graph.getSubElements();
		Iterator<String> it = subElements.iterator();
		while (it.hasNext())
		{
			String subEl = it.next();
			Element sub = d.createElementNS(XSD_NAMESPACE, XSD_ELEMENT);
			sub.setAttribute(NAME, subEl);
			sub.setAttribute(TYPE, SchemalizerUtils.getSchemaTypeName(subEl));
			
			int maxOccurs = graph.getMaxOccurs(subEl);
			if (maxOccurs > MAX_OCCURS_LIMIT)
			{
				sub.setAttribute(MAXOCCURS, XSD_UNBOUNDED);
				maxOccurs = Integer.MAX_VALUE;
			}
			else
				sub.setAttribute(MAXOCCURS, String.valueOf(maxOccurs));
			
			int minOccurs = graph.getMinOccurs(subEl);
			if (maxOccurs == Integer.MAX_VALUE)
			{
				if (minOccurs > 1)
					minOccurs = 1;
			}
			sub.setAttribute(MINOCCURS, String.valueOf(minOccurs));
			indicator.appendChild(sub);
		}
	}
	
	private void appendAttributes(Element root, TypeGraph graph)
	{
		{
			Set<String> atts = graph.getRequiredAttributes();
			Iterator<String> it = atts.iterator();
			while (it.hasNext())
			{
				Element attEl = d.createElementNS(XSD_NAMESPACE, XSD_ATTRIBUTE);
				String name = it.next();
				attEl.setAttribute(NAME, name);
				attEl.setAttribute(TYPE, SchemalizerUtils.getSchemaTypeName(name));
				attEl.setAttribute(USE, REQUIRED);
				root.appendChild(attEl);
			}
		}
		{
			Set<String> atts = graph.getOptionalAttributes();
			Iterator<String> it = atts.iterator();
			while (it.hasNext())
			{
				Element attEl = d.createElementNS(XSD_NAMESPACE, XSD_ATTRIBUTE);
				String name = it.next();
				attEl.setAttribute(NAME, name);
				attEl.setAttribute(TYPE, SchemalizerUtils.getSchemaTypeName(name));
				attEl.setAttribute(USE, OPTIONAL);
				root.appendChild(attEl);
			}
		}		
	}
	
	private void removeSimpleTypeAliases()
	{
		Map<String,String> simpleToBaseMap = new HashMap<String,String>();
		Node n = schemaRoot.getFirstChild();
		while (n != null)
		{
			Node removeNode = null;
			if ((n instanceof Element) && (XSD_SIMPLETYPE.equals(n.getNodeName())))
			{
				Element simpleType = (Element)n;
				Element restriction = (Element)n.getFirstChild();
				if (!restriction.hasChildNodes())
				{
					simpleToBaseMap.put(simpleType.getAttribute(NAME), restriction.getAttribute(BASE));
					removeNode = n;
				}
			}
			n = n.getNextSibling();
			if (removeNode != null)
				removeNode.getParentNode().removeChild(removeNode);
		}
		
		DOMIterator it = new DOMIterator(d);
		while (it.hasNext())
		{
			n = it.next();
			String nodeName = n.getNodeName();
			if (n instanceof Element)
			{
				if (XSD_ELEMENT.equals(nodeName) || XSD_ATTRIBUTE.equals(nodeName))
				{
					Element e = (Element)n;
					String baseType = simpleToBaseMap.get(e.getAttribute(TYPE));
					if (baseType != null)
						e.setAttribute(TYPE, baseType);
				} 
				else if (XSD_EXTENSION.equals(nodeName)) 
				{
					Element e = (Element)n;
					String baseType = simpleToBaseMap.get(e.getAttribute(BASE));
					if (baseType != null)
						e.setAttribute(BASE, baseType);
				}
			}
		}
	}
}
