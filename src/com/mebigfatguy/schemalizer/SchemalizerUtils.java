/*
 * Copyright 2005-2019 Dave Brosius
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

import java.util.Locale;

public class SchemalizerUtils implements SchemalizerConstants {
    private SchemalizerUtils() {
    }

    public static String getSchemaTypeName(String elementName) {
        return elementName.substring(0, 1).toUpperCase(Locale.getDefault()) + ((elementName.length() > 1) ? elementName.substring(1) : "") + CLASS;
    }
}
