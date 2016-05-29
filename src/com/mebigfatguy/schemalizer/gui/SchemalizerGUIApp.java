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
package com.mebigfatguy.schemalizer.gui;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import org.w3c.dom.Document;

import com.mebigfatguy.schemalizer.Schemalizer;

public class SchemalizerGUIApp extends JFrame {
    private static final long serialVersionUID = 3813340272404881555L;

    public static final String BUNDLE_ROOT = "com.mebigfatguy.schemalizer.gui.schemalizergui";
    public static final int DEF_WIDTH = 350;
    public static final int DEF_HEIGHT = 450;
    public static final int DEF_POS_X = 5;
    public static final int DEF_POS_Y = 5;
    public static final int DEF_INC_X = 20;
    public static final int DEF_INC_Y = 25;

    private transient ResourceBundle bundle;
    private JMenu fileMenu;
    private JMenuItem newItem;
    private JMenuItem openItem;
    private JMenuItem closeItem;
    private JMenuItem saveItem;
    private JMenuItem saveAsItem;
    private JMenuItem pageSetupItem;
    private JMenuItem printItem;
    private JMenuItem quitItem;
    private JMenu schemalizerMenu;
    private JMenuItem genOneItem;
    private JMenuItem genAllItem;
    private JMenu windowsMenu;
    protected JDesktopPane desktop;
    protected File chooserDir;

    public static void main(String[] args) {
        SchemalizerGUIApp app = new SchemalizerGUIApp();
        app.setVisible(true);
    }

    public SchemalizerGUIApp() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Schemalizer");
        bundle = ResourceBundle.getBundle(BUNDLE_ROOT);
        JMenuBar mb = new JMenuBar();
        fileMenu = new JMenu(bundle.getString("schemalizergui.file_menu"));
        newItem = new JMenuItem(bundle.getString("schemalizergui.new_item"));
        newItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    openFile(null);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(SchemalizerGUIApp.this, e.getClass().getName() + " " + e.getMessage());
                }
            }
        });
        fileMenu.add(newItem);
        openItem = new JMenuItem(bundle.getString("schemalizergui.open_item"));
        openItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (chooserDir == null) {
                    chooserDir = new File(System.getProperty("user.dir"));
                }
                JFileChooser chooser = new JFileChooser(chooserDir);
                chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                chooser.setMultiSelectionEnabled(true);
                chooser.setFileFilter(new XMLFileFilter());

                if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(SchemalizerGUIApp.this)) {
                    try {
                        File[] files = chooser.getSelectedFiles();
                        for (File file : files) {
                            if (file.isDirectory()) {
                                File[] subXMLFiles = file.listFiles(new XMLFileFilter());
                                for (File subXMLFile : subXMLFiles) {
                                    if (subXMLFile.isFile()) {
                                        openFile(subXMLFile);
                                    }
                                }
                            } else {
                                openFile(file);
                            }
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(SchemalizerGUIApp.this, e.getClass().getName() + " " + e.getMessage());
                    }
                    chooserDir = chooser.getCurrentDirectory();
                }
            }
        });
        fileMenu.add(openItem);
        closeItem = new JMenuItem(bundle.getString("schemalizergui.close_item"));
        closeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                XMLFileFrame iframe = (XMLFileFrame) desktop.getSelectedFrame();
                if (iframe != null) {
                    iframe.close();
                }
            }
        });
        fileMenu.add(closeItem);
        fileMenu.addSeparator();
        saveItem = new JMenuItem(bundle.getString("schemalizergui.save_item"));
        fileMenu.add(saveItem);
        saveAsItem = new JMenuItem(bundle.getString("schemalizergui.saveas_item"));
        fileMenu.add(saveAsItem);
        fileMenu.addSeparator();
        pageSetupItem = new JMenuItem(bundle.getString("schemalizergui.pagesetup_item"));
        fileMenu.add(pageSetupItem);
        printItem = new JMenuItem(bundle.getString("schemalizergui.print_item"));
        fileMenu.add(printItem);
        fileMenu.addSeparator();
        quitItem = new JMenuItem(bundle.getString("schemalizergui.quit_item"));
        quitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                dispose();
                System.exit(0);
            }
        });
        fileMenu.add(quitItem);
        mb.add(fileMenu);

        schemalizerMenu = new JMenu("Schemalizer");
        genOneItem = new JMenuItem(bundle.getString("schemalizergui.schemalizerone_item"));
        genOneItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    JInternalFrame[] frames = desktop.getAllFrames();
                    if (frames.length > 0) {
                        XMLFileFrame frame = (XMLFileFrame) frames[0];
                        XMLFileFrame xsdFrame = openFile(null);
                        generateSchema(new XMLFileFrame[] { frame }, xsdFrame);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(SchemalizerGUIApp.this, e.getClass().getName() + " " + e.getMessage());
                }
            }
        });
        schemalizerMenu.add(genOneItem);
        genAllItem = new JMenuItem(bundle.getString("schemalizergui.schemalizerall_item"));
        genAllItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    JInternalFrame[] frames = desktop.getAllFrames();
                    if (frames.length > 0) {
                        XMLFileFrame xsdFrame = openFile(null);
                        XMLFileFrame[] xmlFrames = new XMLFileFrame[frames.length];
                        System.arraycopy(frames, 0, xmlFrames, 0, frames.length);
                        generateSchema(xmlFrames, xsdFrame);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(SchemalizerGUIApp.this, e.getClass().getName() + " " + e.getMessage());
                }
            }
        });
        schemalizerMenu.add(genAllItem);
        mb.add(schemalizerMenu);

        windowsMenu = new JMenu(bundle.getString("schemalizergui.windows_menu"));
        mb.add(windowsMenu);

        setJMenuBar(mb);

        setSize(800, 600);
        center();

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                if (closeAllFrames()) {
                    dispose();
                    System.exit(0);
                }
            }
        });

        desktop = new JDesktopPane();
        getContentPane().add(desktop);
    }

    protected boolean closeAllFrames() {
        JInternalFrame[] frames = desktop.getAllFrames();
        for (JInternalFrame frame : frames) {
            XMLFileFrame f = (XMLFileFrame) frame;
            if (!f.isChanged()) {
                f.dispose();
            }
        }

        frames = desktop.getAllFrames();
        for (JInternalFrame frame : frames) {
            XMLFileFrame f = (XMLFileFrame) frame;
            f.terminate();
            if (f.isVisible()) {
                break;
            }
        }

        return desktop.getAllFrames().length == 0;
    }

    protected void generateSchema(XMLFileFrame[] inputFrames, XMLFileFrame xsdFrame) {
        try {
            Schemalizer sc = new Schemalizer();
            for (XMLFileFrame frame : inputFrames) {
                Document d = frame.getDocument();
                sc.addSample(d);
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            sc.build(baos);
            String s = new String(baos.toByteArray(), StandardCharsets.UTF_8);
            xsdFrame.setText(s);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(SchemalizerGUIApp.this, e.getClass().getName() + " " + e.getMessage());
        }
    }

    private void center() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Point center = ge.getCenterPoint();
        setLocation(center.x - (getWidth() / 2), center.y - (getHeight() / 2));
    }

    protected XMLFileFrame openFile(File f) throws IOException {
        XMLFileFrame frame;

        frame = new XMLFileFrame(f, bundle);
        frame.setVisible(true);
        JInternalFrame iframe = desktop.getSelectedFrame();
        Point p = null;
        if (iframe == null) {
            frame.setSize(DEF_WIDTH, DEF_HEIGHT);
            frame.setLocation(DEF_POS_X, DEF_POS_Y);
        } else {
            p = iframe.getLocation();
            p.x += DEF_INC_X;
            p.y += DEF_INC_Y;
            Dimension frameSize = iframe.getSize();
            frame.setSize(frameSize);
            Dimension deskSize = desktop.getSize();

            boolean clippedX, clippedY;
            do {
                clippedX = ((p.x + frameSize.width) > deskSize.width);
                clippedY = ((p.y + frameSize.height) > deskSize.height);
                if (clippedY) {
                    p.x += DEF_INC_X;
                    p.y = DEF_POS_Y;
                }
                if (clippedX) {
                    p.x = DEF_POS_X;
                    p.y = DEF_POS_Y;
                }

            } while ((clippedX || clippedY) && (p.x != DEF_POS_X));

            frame.setLocation(p.x, p.y);
        }
        desktop.add(frame);
        try {
            frame.setSelected(true);
        } catch (PropertyVetoException pve) {
        }
        frame.toFront();

        JMenuItem windowItem = new JMenuItem(frame.getTitle());
        windowItem.addActionListener(new BringToFrontActionListener(desktop));
        windowsMenu.add(windowItem);

        frame.addXMLFileClosedListener(new XMLFileClosedListener() {
            @Override
            public void fileClosed(XMLFileClosedEvent e) {
                removeFrameFromWindowsMenu((JInternalFrame) e.getSource());
            }
        });

        return frame;
    }

    protected void removeFrameFromWindowsMenu(JInternalFrame frame) {
        String title = frame.getTitle();
        int count = windowsMenu.getItemCount();
        for (int i = 0; i < count; i++) {
            JMenuItem item = windowsMenu.getItem(i);
            if (item.getText().equals(title)) {
                windowsMenu.remove(item);
                break;
            }
        }
    }

}
