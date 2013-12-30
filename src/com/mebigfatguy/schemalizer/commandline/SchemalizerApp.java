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
package com.mebigfatguy.schemalizer.commandline;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import com.mebigfatguy.schemalizer.Schemalizer;
import com.mebigfatguy.schemalizer.SchemalizerException;


public class SchemalizerApp
{

	public static void main(String[] args)
	{
		try
		{
			Schemalizer sc = new Schemalizer();
			for (int i = 0; i < args.length; i++)
			{
				File f = new File(args[i]);
				processFile(sc, f);
			}
			
			sc.build(System.out);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void processFile( Schemalizer sc, File f)
		throws Exception
	{
		if (!f.exists())
			throw new SchemalizerException("Invalid file specified: " + f.getPath());
		
		if (f.isDirectory())
		{
			File[] files = f.listFiles();
			for (int i = 0; i < files.length; i++)
			{
				if (files[i].isFile() && files[i].getName().endsWith(".xml"))
				{
					processFile(sc, files[i]);
				}
			}
		}
		else
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document d = db.parse(f);
			sc.addSample(d);
		}
	}
}
