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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TypeGraph implements SchemalizerConstants
{
    private static final Integer ZERO = Integer.valueOf(0);
    
	private final String elementName;
	private boolean isAmbiguous;
	private final Set<String> requiredAttributes;
	private final Set<String> optionalAttributes;
	private final Map<String, Integer> values;
	private List<String> subElements;
	private final Map<String, Set<String>> followMap;
	private final Map<String, Integer> minOccurs;
	private final Map<String, Integer> maxOccurs;
	
	public TypeGraph(String name)
	{
		elementName = name;
		isAmbiguous = false;
		requiredAttributes = new LinkedHashSet<String>();
		optionalAttributes = new LinkedHashSet<String>();
		values = new LinkedHashMap<String, Integer>();
		
		subElements = null;
		minOccurs = new HashMap<String, Integer>();
		maxOccurs = new HashMap<String, Integer>();
		followMap = new HashMap<String,Set<String>>();
	}
	
	public void addAttributes(Set<String> attTypeNames)
	{
		if ((attTypeNames != null) && ((requiredAttributes.isEmpty()) || (optionalAttributes.isEmpty())))
		{
			requiredAttributes.addAll(attTypeNames);
		}
		else
		{
			if (attTypeNames != null)
			{
				Iterator<String> it = attTypeNames.iterator();
				while (it.hasNext())
				{
					String attType = it.next();
					if (requiredAttributes.contains(attType) || optionalAttributes.contains(attType))
						continue;
					
					optionalAttributes.add(attType);
				}
			}
			Iterator<String> it = requiredAttributes.iterator();
			while (it.hasNext())
			{
				String attType = it.next();
				if ((attTypeNames != null) && !attTypeNames.contains(attType))
				{
					it.remove();
					optionalAttributes.add(attType);
				}
			}
		}
	}
	
	public void addValue(String value)
	{
		if (value == null)
			return;
		
		if (value.trim().length() == 0)
			return;
		
		Integer repeat = values.get(value);
		if (repeat == null)
			values.put(value, Integer.valueOf(1));
		else
			values.put(value, Integer.valueOf(1 + repeat.intValue()));
	}
	
	public void addSubElements(List<String> sampleElements)
	{
		updateFollows(sampleElements);
		if (subElements == null)
		{
			subElements = new ArrayList<String>();
			if (sampleElements != null)
			{
				Iterator<String> it = sampleElements.iterator();
				String lastSubEl = "";
				while (it.hasNext())
				{
					String subEl = it.next();
					if (!lastSubEl.equals(subEl))
					{							
						if (findElementPosition(subEl, 0) >= 0) 
						{
							isAmbiguous = true;
							maxOccurs.put(subEl, Integer.valueOf(maxOccurs.get(subEl).intValue() + 1));
						}
						else
						{
							subElements.add(subEl);
							lastSubEl = subEl;
							Integer one = Integer.valueOf(1);
							minOccurs.put(subEl, one);
							maxOccurs.put(subEl, one);
						}
					}
					else
					{
						Integer inc = Integer.valueOf(maxOccurs.get(subEl).intValue() + 1);
						maxOccurs.put(subEl, inc);
						minOccurs.put(subEl, inc);
					}
				}
			}
		}
		else
		{
			adjustOccurs(sampleElements);
			sampleElements = removeDuplicates(sampleElements);
			
			int curInsPos = 0;
			Iterator<String> it = sampleElements.iterator();
			while (it.hasNext())
			{
				if (isAmbiguous)
					curInsPos = 0;
				
				String curEl = it.next();
				int newPos = findElementPosition(curEl, curInsPos);
				
				if (isAmbiguous)
				{
					if (newPos == -1)
						subElements.add(curEl);
				}
				else
				{
					if (newPos == -1) 
					{
						newPos = findElementPosition(curEl, 0);
						if (newPos == -1)
						{
							subElements.add(curInsPos, curEl);
							curInsPos++;
						}
						else
						{
							pushElement(newPos, curInsPos);
							curInsPos++;
						}
					}
					else if (newPos < curInsPos)
					{
						pushElement(newPos, curInsPos);
						curInsPos++;
					}
					else
					{
						curInsPos = newPos+1;					
					}
				}
			}
		}
	}
	
	public String getTypeCategory()
	{
		if (subElements.size() > 0)
		{
			if (values.size() > 0)
				return COMPLEX_MIXED_CATEGORY;
			return COMPLEX_ELEMENTS_ONLY_CATEGORY;
		}
		else if ((!requiredAttributes.isEmpty()) || (!optionalAttributes.isEmpty()))
		{
			if (values.size() > 0)
				return COMPLEX_TEXT_ONLY_CATEGORY;
			return COMPLEX_EMPTY_CATEGORY;
		}
		return SIMPLE_CATEGORY;	
	}
	
	public String getName()
	{
		return elementName;
	}
	
	public boolean isAmbiguous()
	{
		return isAmbiguous;
	}
	
	public Set<String> getRequiredAttributes()
	{
		return requiredAttributes;
	}
	
	public Set<String> getOptionalAttributes()
	{
		return optionalAttributes;
	}
	
	public List<String> getSubElements()
	{
		return subElements;
	}
	
	public Map<String, Integer> getValues()
	{
		return values;
	}
	
	public int getMinOccurs(String el)
	{
		Integer min = minOccurs.get(el);
		if (min == null)
			return 0;
		
		return min.intValue();
	}
	
	public int getMaxOccurs(String el)
	{
		Integer max = maxOccurs.get(el);
		if (max == null)
			return 1;
		
		return max.intValue();	
	}
	
	private void updateFollows(List<String> sampleElements)
	{
		if (sampleElements == null)
			return;
		
		String lastEl = "";
		Iterator<String> it = sampleElements.iterator();
		while (it.hasNext())
		{
			String el = it.next();
			if (!el.equals(lastEl))
			{
				if (lastEl.length() > 0)
				{
					Set<String> followEls = followMap.get(lastEl);
					if (followEls == null)
					{
						followEls = new HashSet<String>();
						followMap.put(lastEl, followEls);
					}
					followEls.add(el);
				}
				lastEl = el;
			}
		}
	}
	
	private void adjustOccurs(List<String> sampleElements)
	{
		if (sampleElements != null)
		{
			sampleElements.add(""); //end of list marker
			String lastEl = "";
			int occurCnt = 0;
			Iterator<String> it = sampleElements.iterator();
			while (it.hasNext())
			{
				String el = it.next();
				if (!el.equals(lastEl))
				{
					if (lastEl.length() > 0)
					{
						Integer max = maxOccurs.get(lastEl);
						if (max == null)
						{
							maxOccurs.put(lastEl, Integer.valueOf(occurCnt));
							minOccurs.put(lastEl, ZERO);
						}
						else
						{
							if (occurCnt > max.intValue())
								maxOccurs.put(lastEl, Integer.valueOf(occurCnt));
						}
					}
	
					lastEl = el;
					occurCnt = 0;
				}
				occurCnt++;
			}
			sampleElements.remove(sampleElements.size() - 1);
		}
		
		{
			Set<String> ss = new HashSet<String>();
			if (sampleElements != null)
				ss.addAll(sampleElements);
			Iterator<Map.Entry<String,Integer>> it = minOccurs.entrySet().iterator();
			while (it.hasNext())
			{
				Map.Entry<String,Integer> entry = it.next();
				String el = entry.getKey();
				if (!ss.contains(el))
					minOccurs.put(el, ZERO);
			}
		}
	}
	
	private int findElementPosition(String element, int startAt)
	{
		for (int i = startAt; i < subElements.size(); i++)
		{
			if (subElements.get(i).equals(element))
				return i;
		}
		return -1;
	}
	
	private List<String> removeDuplicates(List<String> els)
	{
		List<String> cleanEls = new ArrayList<String>();
		
		if (els != null)
			cleanEls.addAll(els);
		Iterator<String> it = cleanEls.iterator();
		String lastEl = "";
		while (it.hasNext())
		{
			String el = it.next();
			if (el.equals(lastEl))
			{
				it.remove();
			}
			else
				lastEl = el;
		}
		return cleanEls;
	}
	
	private void pushElement(int oldPos, int newPos)
	{
		if (oldPos >= newPos)
			return;
		
		String pushEl = subElements.get(oldPos);
		Set<String> follows = followMap.get(pushEl);
		
		if (follows != null)
		{
			for (int i = oldPos+1; i < newPos; i++)
			{
				String el = subElements.get(i);
				if (follows.contains(el))
				{
					isAmbiguous = true;
					return;
				}
			}
		}
		
		subElements.remove(oldPos);
		if (newPos >= subElements.size())
			subElements.add(pushEl);
		else
			subElements.set(newPos, pushEl);
	}
}
