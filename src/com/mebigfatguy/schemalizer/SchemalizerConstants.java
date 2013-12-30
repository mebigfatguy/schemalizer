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

public interface SchemalizerConstants
{
	public static final String XSD_NAMESPACE = "http://www.w3.org/2001/XMLSchema";
	public static final String XSD_INSTANCE_NAMESPACE = "http://www.w3.org/2001/XMLSchema-instance";
	public static final String XSD_SCHEMA = "xsd:schema";
	public static final String XSD_ELEMENT = "xsd:element";
	public static final String XSD_ATTRIBUTE = "xsd:attribute";
	public static final String XSD_SIMPLETYPE = "xsd:simpleType";
	public static final String XSD_COMPLEXTYPE = "xsd:complexType";
	public static final String XSD_SIMPLECONTENT = "xsd:simpleContent";
	public static final String XSD_EXTENSION = "xsd:extension";
	public static final String XSD_SEQUENCE = "xsd:sequence";
	public static final String XSD_ALL = "xsd:all";
	public static final String XSD_RESTRICTION = "xsd:restriction";
	public static final String XSD_ENUMERATION = "xsd:enumeration";
	public static final String XSD_PATTERN = "xsd:pattern";
	public static final String XSD_STRING = "xsd:string";
	public static final String XSD_INTEGER = "xsd:integer";
	public static final String XSD_BOOLEAN = "xsd:boolean";
	public static final String XSD_DOUBLE = "xsd:double";
	public static final String XSD_DATE = "xsd:date";
	public static final String XSD_DATETIME = "xsd:dateTime";
	public static final String XSD_ANYURI = "xsd:anyURI";
	public static final String XSD_LANGUAGE = "xsd:language";
	public static final String XSD_UNBOUNDED = "unbounded";
	
	public static final String ID = "id";
	
	public static final String XMLNS = "xmlns";
	public static final String XSD = "xsd";
	
	public static final String NAME = "name";
	public static final String TYPE = "type";
	public static final String BASE = "base";
	public static final String VALUE = "value";
	public static final String MIXED = "mixed";
	public static final String MINOCCURS = "minOccurs";
	public static final String MAXOCCURS = "maxOccurs";
	public static final String USE = "use";
	public static final String OPTIONAL = "optional";
	public static final String REQUIRED = "required";
	
	public static final String CLASS = "Class";
	public static final String COMPLEX = "Complex";
	public static final String SIMPLE = "Simple";
	
	public static final String SCH_CAT = "sch:cat";
	
	public static final String SIMPLE_CATEGORY = "SIMPLE";
	public static final String COMPLEX_EMPTY_CATEGORY = "COMPLEX_EMPTY";
	public static final String COMPLEX_TEXT_ONLY_CATEGORY = "COMPLEX_TEXT_ONLY";
	public static final String COMPLEX_ELEMENTS_ONLY_CATEGORY = "COMPLEX_ELEMENTS_ONLY";
	public static final String COMPLEX_MIXED_CATEGORY = "COMPLEX_MIXED";
}
