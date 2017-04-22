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
package com.mebigfatguy.schemalizer.gui;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.URL;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLFileFrame extends JInternalFrame {
    private static final long serialVersionUID = -3579998452119053796L;

    private static int frameNo = 0;

    private final JEditorPane editor;
    protected transient ResourceBundle bundle;
    protected File source;
    private final Set<XMLFileClosedListener> closeListeners;
    private boolean isXSD;
    protected boolean changed;

    public XMLFileFrame(File f, final ResourceBundle bdl) throws IOException {
        super((f == null) ? "" : (f.getName() + " - [" + f.getPath() + "]"), true, true, true, true);

        bundle = bdl;
        if (f == null) {
            setTitle(MessageFormat.format(bundle.getString("schemalizergui.untitled_label"), new Object[] { Integer.valueOf(++frameNo) }));
        }

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        closeListeners = new HashSet<>();
        changed = false;
        isXSD = false;
        source = f;
        editor = new JEditorPane();
        editor.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent de) {
                changed = true;
            }

            @Override
            public void removeUpdate(DocumentEvent de) {
                changed = true;
            }

            @Override
            public void changedUpdate(DocumentEvent de) {
                changed = true;
            }
        });
        getContentPane().add(new JScrollPane(editor));

        if (f != null) {
            editor.setPage(new URL("file:///" + f.getPath()));
        }

        this.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                close();
            }

            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
                fireXMLFileClosed();
            }
        });
    }

    public void close() {
        if (changed) {
            String message = MessageFormat.format(bundle.getString("schemalizergui.savechanges_label"), XMLFileFrame.this.getTitle());
            int choice = JOptionPane.showConfirmDialog(XMLFileFrame.this, message);
            if (choice == JOptionPane.OK_OPTION) {
                if (source == null) {
                    source = chooseSaveLocation(XMLFileFrame.this.getTitle());
                }

                if (source != null) {
                    try {
                        saveFile();
                        dispose();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(XMLFileFrame.this.getDesktopPane(), ex.getClass().getName() + " " + ex.getMessage());
                    }
                }
            } else if (choice == JOptionPane.NO_OPTION) {
                dispose();
            }
        } else {
            dispose();
        }
    }

    protected File chooseSaveLocation(String name) {
        JFileChooser chooser = new JFileChooser();
        if (isXSD) {
            name += ".xsd";
        } else {
            name += ".xml";
        }
        chooser.setSelectedFile(new File(System.getProperty("user.dir"), name));

        File destFile = null;
        int option;

        do {
            option = chooser.showSaveDialog(this);
            if (option == JFileChooser.APPROVE_OPTION) {
                destFile = chooser.getSelectedFile();
                if (destFile.exists()) {
                    int ok = JOptionPane.showConfirmDialog(XMLFileFrame.this.getDesktopPane(),
                            MessageFormat.format(bundle.getString("schemalizergui.replacefile_label"), new Object[] { destFile.getPath() }));
                    if (ok != JOptionPane.YES_OPTION) {
                        option = JFileChooser.ERROR_OPTION;
                    }
                }
            }
        } while (option == JFileChooser.ERROR_OPTION);

        return (JFileChooser.APPROVE_OPTION == option) ? destFile : null;
    }

    protected void saveFile() throws IOException {
        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(source.toPath()))) {
            pw.print(editor.getText());
            pw.flush();
            changed = false;
        }
    }

    public Document getDocument() throws ParserConfigurationException, SAXException, IOException {
        String xml = editor.getText();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        return db.parse(new InputSource(new StringReader(xml)));
    }

    public void setText(String data) {
        editor.setText(data);
        changed = true;
        isXSD = true;
    }

    public boolean isXSD() {
        return isXSD;
    }

    public boolean isChanged() {
        return changed;
    }

    public void terminate() {
        this.fireInternalFrameEvent(InternalFrameEvent.INTERNAL_FRAME_CLOSING);
    }

    public void fireXMLFileClosed() {
        synchronized (closeListeners) {
            if (closeListeners.isEmpty()) {
                return;
            }
            Iterator<XMLFileClosedListener> it = closeListeners.iterator();
            XMLFileClosedEvent closeEvent = new XMLFileClosedEvent(this);
            while (it.hasNext()) {
                it.next().fileClosed(closeEvent);
            }
        }
    }

    public void addXMLFileClosedListener(XMLFileClosedListener l) {
        synchronized (closeListeners) {
            closeListeners.add(l);
        }
    }

    public void removeXMLFileClosedListener(XMLFileClosedListener l) {
        synchronized (closeListeners) {
            closeListeners.remove(l);
        }
    }
}