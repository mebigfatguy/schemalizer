/*
 * Copyright 2005-2014 Dave Brosius
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

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

public class Schemalizer implements SchemalizerConstants
{
	private SamplesDatabase samples = new SamplesDatabase();
	private String documentType = null;
	
	/**
	 * add a sample xml file to use for generating the schema file
	 * @param xmlStream the xml file presented as a stream
	 * 
	 * @throws SchemalizerException
	 */
	public void addSample(InputStream xmlStream)
		throws SchemalizerException
	{
		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setCoalescing(true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document d = db.parse(xmlStream);
			validateDocumentType(d);
			samples.addDocumentTypes(d);
		}
		catch (SchemalizerException se)
		{
			throw se;
		}
		catch (Exception e)
		{
			throw new SchemalizerException("Unable to add xml sample", e);
		}
	}
	
	/**
	 * add a sample xml file to use for generating the schema file
	 * @param xmlDocument the xml file presented as a DOM Document
	 * 
	 * @throws SchemalizerException
	 */
	public void addSample(Document xmlDocument)
		throws SchemalizerException
	{
		validateDocumentType(xmlDocument);
		samples.addDocumentTypes(xmlDocument);
	}
		
	/**
	 * builds the schema definition and returns it as a DOM document
	 * 
	 * @return the DOM document that represents the schema
	 * @throws SchemalizerException
	 */
	public Document build()
		throws SchemalizerException
	{
		try
		{
			if (documentType == null)
				throw new SchemalizerException("No document sample type added to schemalizer");
			
			SchemaTypeBuilder schemaTypeBuilder = new SchemaTypeBuilder();
			
			TypeGraphBuilder builder = new TypeGraphBuilder();
			Iterator<Set<SchemaTypeSample>> it = samples.getSamplesIterator();
			while (it.hasNext())
			{
				Set<SchemaTypeSample> typeSamples = it.next();
				TypeGraph tg = builder.buildGraph(typeSamples);
				schemaTypeBuilder.addTypeGraph(tg);
			}
			
			schemaTypeBuilder.finish(documentType);
			return schemaTypeBuilder.getDocument();
		}
		catch (Exception e)
		{
			throw new SchemalizerException(e);
		}
    }
	
	/**
	 * builds the schema definition and writes it to the stream
	 * 
	 * @param xsdStream the stream to write the schema data to
	 * @throws SchemalizerException
	 */
    public void build( OutputStream xsdStream )
    	throws SchemalizerException
    {
    	try
    	{
	    	Document d = build();
	        emitXML(d, xsdStream);
    	}
    	catch (Exception e)
    	{
    		throw new SchemalizerException("Unable to build schema definition", e);
    	}
    }

    /**
     * makes sure that this document is the same top level top of other samples
     * @param d the document to check
     * 
     * @throws SchemalizerException if the document type doesn't match
     */
    private void validateDocumentType(Document d)
		throws SchemalizerException
	{
		String type = d.getDocumentElement().getNodeName();
		int colonPos = type.indexOf(':');
		if (colonPos >= 0)
			type = type.substring(colonPos + 1);
		
		if (documentType == null)
			documentType = type;
		else if (!documentType.equals(type))
			throw new SchemalizerException("Sample doesn't match type of previous samples {" + documentType + "," + type + "}");
	}
            
    /**
     * outputs the xsd document to a stream
     * @param xsd the document
     * @param xsdStream the output stream
     * @throws TransformerException
     */
	private void emitXML(Document xsd, OutputStream xsdStream)
    throws TransformerException
    {
	    TransformerFactory tf = TransformerFactory.newInstance();
	    tf.setAttribute("indent-number", Integer.valueOf(4));
	    Transformer t = tf.newTransformer();
	    t.setOutputProperty(OutputKeys.INDENT, "yes");
	    t.setOutputProperty(OutputKeys.METHOD, "xml");
	    t.setOutputProperty(OutputKeys.MEDIA_TYPE, "text/xml");
	
	    t.transform(new DOMSource(xsd), new StreamResult(new OutputStreamWriter(xsdStream)));
    }

}
