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

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class SimpleTypeInferrerTest implements SchemalizerConstants {
    @Test
    public void testIntegerSimpleType() {
        final Integer one = Integer.valueOf(1);
        Map<String, Integer> values = new HashMap<String, Integer>() {
            private static final long serialVersionUID = 7615889025060953148L;

            {
                put("1", one);
                put("-1", one);
                put("0", one);
                put("1345", one);
            }
        };
        SimpleTypeInferrer inferer = new SimpleTypeInferrer(values);
        Assert.assertEquals(XSD_INTEGER, inferer.findSimpleType());
    }

    @Test
    public void testDoubleSimpleType() {
        final Integer one = Integer.valueOf(1);
        Map<String, Integer> values = new HashMap<String, Integer>() {
            private static final long serialVersionUID = -5747457654510238037L;

            {
                put("1", one);
                put("-1.1", one);
                put("0.111112", one);
                put("1345.", one);
            }
        };
        SimpleTypeInferrer inferer = new SimpleTypeInferrer(values);
        Assert.assertEquals(XSD_DOUBLE, inferer.findSimpleType());
    }

    @Test
    public void testAnyURISimpleType() {
        final Integer one = Integer.valueOf(1);
        Map<String, Integer> values = new HashMap<String, Integer>() {
            private static final long serialVersionUID = -9214591051624779578L;

            {
                put("http://www.sourceforge.net/projects/schemalizer", one);
                put("http://www.google.com", one);
                put("http://www.mebigfatguy.com", one);
                put("http://java.sun.com", one);
            }
        };
        SimpleTypeInferrer inferer = new SimpleTypeInferrer(values);
        Assert.assertEquals(XSD_ANYURI, inferer.findSimpleType());
    }

    @Test
    public void testEnumerationSimpleType() {
        final Integer five = Integer.valueOf(5);
        Map<String, Integer> values = new HashMap<String, Integer>() {
            private static final long serialVersionUID = -8414822282906361983L;

            {
                put("boo", five);
                put("hoo", five);
                put("moo", five);
                put("roo", five);
            }
        };
        SimpleTypeInferrer inferer = new SimpleTypeInferrer(values);
        Assert.assertEquals(XSD_ENUMERATION, inferer.findSimpleType());
    }

    @Test
    public void testDateSimpleType() {
        final Integer one = Integer.valueOf(1);
        Map<String, Integer> values = new HashMap<String, Integer>() {
            private static final long serialVersionUID = -7156245379613527344L;

            {
                put("2005-03-25", one);
                put("2005-12-25", one);
                put("1999-12-31", one);
                put("2004-01-01", one);
                put("2000-01-01", one);
            }
        };
        SimpleTypeInferrer inferer = new SimpleTypeInferrer(values);
        Assert.assertEquals(XSD_DATE, inferer.findSimpleType());
    }

    @Test
    public void testDateTimeSimpleType() {
        final Integer one = Integer.valueOf(1);
        Map<String, Integer> values = new HashMap<String, Integer>() {
            private static final long serialVersionUID = 8983291346775494636L;

            {
                put("2005-03-25T01-30-00", one);
                put("2005-12-25T13-30-00", one);
                put("1999-12-31T08-30-30", one);
                put("2004-01-01T23-59-59", one);
                put("2000-01-01T20-00-00", one);
            }
        };
        SimpleTypeInferrer inferer = new SimpleTypeInferrer(values);
        Assert.assertEquals(XSD_DATETIME, inferer.findSimpleType());
    }

    @Test
    public void testBooleanSimpleType() {
        final Integer one = Integer.valueOf(1);
        Map<String, Integer> values = new HashMap<String, Integer>() {
            private static final long serialVersionUID = -5441330605826951378L;

            {
                put("true", one);
                put("false", one);
                put("false", one);
                put("false", one);
                put("true", one);
            }
        };
        SimpleTypeInferrer inferer = new SimpleTypeInferrer(values);
        Assert.assertEquals(XSD_BOOLEAN, inferer.findSimpleType());
    }

    @Test
    public void testLanguageSimpleType() {
        final Integer one = Integer.valueOf(1);
        Map<String, Integer> values = new HashMap<String, Integer>() {
            private static final long serialVersionUID = 7660788175236272177L;

            {
                put("en", one);
                put("fr", one);
                put("da", one);
                put("es", one);
                put("zh", one);
            }
        };
        SimpleTypeInferrer inferer = new SimpleTypeInferrer(values);
        Assert.assertEquals(XSD_LANGUAGE, inferer.findSimpleType());
    }
}
