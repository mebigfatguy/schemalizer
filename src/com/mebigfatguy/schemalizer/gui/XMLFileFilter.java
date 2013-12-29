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
package com.mebigfatguy.schemalizer.gui;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class XMLFileFilter extends FileFilter implements java.io.FileFilter
{
	@Override
	public boolean accept(File f)
	{
		if (f.isDirectory())
			return true;
		
		return (f.getName().endsWith(".xml"));
	}
	
	@Override
	public String getDescription()
	{
		return "XML Files (*.xml)";
	}
}