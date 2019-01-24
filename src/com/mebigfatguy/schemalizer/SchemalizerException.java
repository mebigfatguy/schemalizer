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

public class SchemalizerException extends Exception {
    private static final long serialVersionUID = 2756172880993707955L;

    public SchemalizerException(String reason) {
        super(reason);
    }

    public SchemalizerException(String reason, Throwable cause) {
        super(reason, cause);
    }

    public SchemalizerException(Throwable cause) {
        super(cause);
    }
}
