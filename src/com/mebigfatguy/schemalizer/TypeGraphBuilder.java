/*
 * Copyright 2005-2016 Dave Brosius
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

import java.util.Iterator;
import java.util.Set;

public class TypeGraphBuilder {
    public TypeGraph buildGraph(Set<SchemaTypeSample> typeSamples) throws SchemalizerException {
        if (typeSamples.isEmpty())
            throw new SchemalizerException("No samples to build a TypeGraph with");

        TypeGraph tg = new TypeGraph(typeSamples.iterator().next().getName());
        Iterator<SchemaTypeSample> it = typeSamples.iterator();
        while (it.hasNext()) {
            SchemaTypeSample sample = it.next();
            tg.addAttributes(sample.getAttributes());
            tg.addValue(sample.getValue());
            tg.addSubElements(sample.getSubElements());
        }

        return tg;
    }
}
