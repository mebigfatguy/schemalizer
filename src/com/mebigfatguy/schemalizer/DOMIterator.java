/*
 * Copyright 2005-2010 Dave Brosius
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
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class DOMIterator implements Iterator<Node>
{
	List<Node> nodes = new ArrayList<Node>();
	
	public DOMIterator(Document d)
	{
		nodes.add(d.getDocumentElement());
	}
	
	public boolean hasNext()
	{
		return !nodes.isEmpty();
	}
	
	public Node next()
		throws NoSuchElementException
	{
		if (nodes.isEmpty())
			throw new NoSuchElementException("No more elements for iterator");
		
		Node n = nodes.remove(nodes.size() - 1);
		if (n instanceof Element)
		{
			NamedNodeMap nnm = n.getAttributes();
			int len = nnm.getLength();
			for (int i = len-1; i >= 0; i--)
			{
				nodes.add(nnm.item(i));
			}
			
			Node child = n.getLastChild();
			while (child != null)
			{
				if (child instanceof Element)
				{
					nodes.add(child);
				}
				
				child = child.getPreviousSibling();
			}
		}
		
		return n;
	}
	
	public void remove()
	{
		throw new UnsupportedOperationException();
	}
}
