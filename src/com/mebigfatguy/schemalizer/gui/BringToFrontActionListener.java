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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

class BringToFrontActionListener implements ActionListener
{
	private JDesktopPane desktop;
	public BringToFrontActionListener(JDesktopPane dtop)
	{
		desktop = dtop;
	}
	
    public void actionPerformed(ActionEvent ae)
    {
        try
        {
	        String title = ae.getActionCommand();
	        JInternalFrame[] frames = desktop.getAllFrames();
	        for (int i = 0; i < frames.length; i++)
	        {
	            JInternalFrame f = frames[i];
	            if (f.getTitle().equals(title))
	            {
	                f.toFront();
	                f.setSelected(true);
	            	break;
	            }
	        }
        }
        catch (Exception e)
        {}
    }
}