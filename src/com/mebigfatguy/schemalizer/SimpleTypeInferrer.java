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

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SimpleTypeInferrer implements SchemalizerConstants
{
	public static final double ENUM_RATIO_LIMIT = 5.0;
	public static final int ENUM_SIZE_LIMIT = 4;
	public static final int PATTERN_SIZE_LIMIT = 3;
	
	Set<String> values;
	int totalCount;
	int uniqueCount;
	
	public SimpleTypeInferrer(Map<String, Integer> v)
	{
		values = v.keySet();
		totalCount = 0;
		Iterator<Integer> it = v.values().iterator();
		while (it.hasNext())
		{
			totalCount += it.next().intValue();
		}
		uniqueCount = values.size();
	}
	
	public Validator findSimpleType()
	{
		if (values.isEmpty())
			return new StringValidator();
		
		Map<String, Validator> validators = getValidators();
		
		Iterator<String> it = values.iterator();
		while (it.hasNext())
		{
			if (validators.size() < 2)
				break;
			
			String value = it.next();
			Iterator<Validator> vit = validators.values().iterator();
			while (vit.hasNext())
			{
				Validator v = vit.next();
				if (!v.isValid(value))
					vit.remove();
			}
		}
		
		if (validators.isEmpty())
			return new StringValidator();
		return validators.values().iterator().next();
	}
	
	public void buildValidatorSubElements()
	{
		
	}
	
	public String getBaseType(String simpleType)
	{
		if (XSD_ENUMERATION.equals(simpleType))
			return XSD_STRING;
		
		return simpleType;
	}
	
	private Map<String, Validator> getValidators()
	{
		Map<String, Validator> validators = new LinkedHashMap<String, Validator>();
		validators.put(XSD_BOOLEAN, new BooleanValidator());
		validators.put(XSD_ANYURI, new AnyURIValidator());
		validators.put(XSD_DATETIME, new DateTimeValidator());
		validators.put(XSD_DATE, new DateValidator());
		validators.put(XSD_INTEGER, new IntegerValidator());
		validators.put(XSD_DOUBLE, new DoubleValidator());
		validators.put(XSD_LANGUAGE, new LanguageValidator());
		validators.put(XSD_PATTERN, new ConstantLengthPatternValidator());
		validators.put(XSD_ENUMERATION, new EnumerationValidator());
		
		return validators;
	}
	
	class BaseValidator implements Validator
	{
		public boolean isValid(String value)
		{
			if (value.length() == 0)
				return false;
			if (!value.trim().equals(value))
				return false;
			return true;
		}
		
		public void addSubElements(Document d, Element group)
		{			
		}
	}

	class IntegerValidator extends BaseValidator
	{
		@Override
		public String toString()
		{
			return XSD_INTEGER;
		}
		
		@Override
		public boolean isValid(String value)
		{
			if (!super.isValid(value))
				return false;
			try
			{
				Integer.parseInt(value);
				return true;
			}
			catch (NumberFormatException nfe)
			{
				return false;
			}		
		}
	}
	
	class DoubleValidator extends BaseValidator
	{
		@Override
		public String toString()
		{
			return XSD_DOUBLE;
		}
		
		@Override
		public boolean isValid(String value)
		{
			if (!super.isValid(value))
				return false;
			try
			{
				Double.parseDouble(value);
				return true;
			}
			catch (NumberFormatException nfe)
			{
				return false;
			}		
		}
	}

	class BooleanValidator extends BaseValidator
	{
		@Override
		public String toString()
		{
			return XSD_BOOLEAN;
		}
		
		@Override
		public boolean isValid(String value)
		{
			if (!super.isValid(value))
				return false;
			
			return ("true".equals(value) || "false".equals(value));		
		}
	}
	
	class DateValidator extends BaseValidator
	{
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		
		@Override
		public String toString()
		{
			return XSD_DATE;
		}
		
		@Override
		public boolean isValid(String value)
		{
			if (!super.isValid(value))
				return false;
			
			try
			{
				df.parse(value);
				return true;
			}
			catch (ParseException pe)
			{
				return false;
			}
		}
	}

	class DateTimeValidator extends BaseValidator
	{
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss");
		
		@Override
		public String toString()
		{
			return XSD_DATETIME;
		}
		
		@Override
		public boolean isValid(String value)
		{
			if (!super.isValid(value))
				return false;
			
			try
			{
				df.parse(value);
				return true;
			}
			catch (ParseException pe)
			{
				return false;
			}
		}
	}
	
	class AnyURIValidator extends BaseValidator
	{
		@Override
		public String toString()
		{
			return XSD_ANYURI;
		}
		
		@Override
		public boolean isValid(String value)
		{
			if (!super.isValid(value))
				return false;
			
			try
			{
				new URL(value);
				return true;
			}
			catch (MalformedURLException mue)
			{
				return false;
			}
		}
	}
	
	class LanguageValidator extends BaseValidator
	{
		private final Set<String> languages = new HashSet<String>(Arrays.asList(Locale.getISOLanguages()));
		
		@Override
		public String toString()
		{
			return XSD_LANGUAGE;
		}
		
		@Override
		public boolean isValid(String value)
		{
			if (!super.isValid(value))
				return false;
			
			return languages.contains(value);
		}
	}

	class EnumerationValidator extends BaseValidator
	{
		@Override
		public String toString()
		{
			return XSD_ENUMERATION;
		}
		
		@Override
		public boolean isValid(String value)
		{
			if (!super.isValid(value))
				return false;
			
			if (totalCount < ENUM_SIZE_LIMIT)
				return false;
			
			double uniqueRatio = ((double)uniqueCount) / ((double) totalCount);

			if ((uniqueRatio * uniqueCount) > ENUM_RATIO_LIMIT)
				return false;
			
			for (int i = 0; i < value.length(); i++)
				if (value.charAt(i) < ' ')
					return false;
			
			return true;
		}
		
		@Override
		public void addSubElements(Document d, Element group)
		{
			Iterator<String> it = values.iterator();
			while (it.hasNext())
			{
				Element enumeration = d.createElementNS(XSD_NAMESPACE, XSD_ENUMERATION);
				enumeration.setAttribute(VALUE, it.next());
				group.appendChild(enumeration);
			}
		}
	}
	
	class ConstantLengthPatternValidator extends BaseValidator
	{
		private static final int CHARACTER = -1;
		private static final int DIGIT = -2;
		private static final int SPACE = -3;
		
		private List<Integer> pattern = null;
		
		@Override
		public String toString()
		{
			return XSD_STRING;
		}	
		
		@Override
		public boolean isValid(String value)
		{
			if (!super.isValid(value))
				return false;
			
			if (totalCount < PATTERN_SIZE_LIMIT)
				return false;

			boolean check = true;
			if (pattern == null)
			{
				pattern = new ArrayList<Integer>();
				check = false;
			}
			
			if ((!check) || (pattern.size() == value.length()))
			{
				boolean hasSpecialChar = false;
				for (int i = 0; i < value.length(); i++)
				{
					char c = value.charAt(i);
					int category;
					int type = Character.getType(c);
					if ((type == Character.LOWERCASE_LETTER)
					||  (type == Character.UPPERCASE_LETTER)
					||  (type == Character.OTHER_LETTER))
						category = CHARACTER;
					else if (Character.isDigit(c))
						category = DIGIT;
					else if (c == ' ')
						category = SPACE;
					else if ((Character.isWhitespace(c))
					||       (Character.isISOControl(c)))
						return false;
					else
					{
						category = c;
						hasSpecialChar = true;
					}
					
					if (check)
					{
						if (category != pattern.get(i).intValue())
							return false;
					}
					else
					{
						pattern.add(Integer.valueOf(category));	
						if (!hasSpecialChar)
							return false;
					}
				}
			}
			else
				return false;
			
			return true;
		}
		
		@Override
		public void addSubElements(Document d, Element group)
		{
			Element patternEl = d.createElementNS(XSD_NAMESPACE, XSD_PATTERN);
			
			StringBuilder buffer = new StringBuilder(pattern.size() * 2);
			Iterator<Integer> it = pattern.iterator();
			int oldCategory = 0;
			int count = 0;
			while (it.hasNext())
			{
				int category = it.next().intValue();
				if (oldCategory == category)
					count++;
				else
				{
					if (count > 0)
						addCategoryToPattern(buffer, oldCategory, count);
					count = 1;
					oldCategory = category;
				}	
			}
			if (count > 0)
				addCategoryToPattern(buffer, oldCategory, count);
						
			patternEl.setAttribute(VALUE, buffer.toString());
			group.appendChild(patternEl);

		}
		
		private void addCategoryToPattern(StringBuilder buffer, int category, int count)
		{
			if (category == CHARACTER)
				buffer.append("\\c");
			else if (category == DIGIT)
				buffer.append("\\d");
			else
			{
				switch (category)
				{
					case '(':
					case ')':
					case '{':
					case '}':
					case '[':
					case ']':
					case '\\':
					case '*':
					case '+':
					case '.':
					case '|':
					case '^':
					case '?':
					case '-':
						buffer.append('\\');
					break;
				}
				buffer.append((char)category);
			}
				
			
			if (count > 1)
			{
				buffer.append('{');
				buffer.append(count);
				buffer.append('}');
			}
		}
	}
	
	class StringValidator extends BaseValidator
	{
		@Override
		public String toString()
		{
			return XSD_STRING;	
		}
	}
}