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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class TypeGraphTest
{
    @Test
	public void testAddSubElements() throws Exception
	{
		TypeGraph tg = new TypeGraph("Test");
		List<String> els = new ArrayList<String>();
		els.add("A");
		els.add("B");
		tg.addSubElements(els);
		els.clear();
		els.add("A");
		els.add("C");
		tg.addSubElements(els);
		els.clear();
		els.add("C");
		els.add("D");
		els.add("E");
		tg.addSubElements(els);
	}
}
