import java.awt.Color;
import java.awt.Component;
import java.awt.TextArea;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.Highlight;
import javax.swing.text.Highlighter.HighlightPainter;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

public class TextDocument extends JScrollPane {

    public JEditorPane editorPane;
    private Document doc;
    private boolean hasChanged, isLoadedAndHasFile;
    public String filename;
    private File file;
    private UndoManager undoManager;
    public DocumentContainer cont;
    private String text;
    private int cursorPosition, selectionIndex;

    public TextDocument(DocumentContainer cont) {
        editorPane = new JEditorPane();
        editorPane.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                cursorPosition = e.getDot();
            }
        });
        doc = editorPane.getDocument();
        this.cont = cont;
        setViewportView(editorPane);
        hasChanged = false;
        undoManager = new UndoManager();
        doc.addUndoableEditListener(undoManager);
        doc.addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                hasChanged = true;
                filename = !isFileNull() ? file.getName() + "*" : "Untitled" + cont.getSelectedIndex() + " *";
                try {
                    if (isFileNull()) {
                        cont.setTitleAt(cont.getSelectedIndex(), filename);
                    } else if (isLoadedAndHasFile) {
                        cont.setTitleAt(cont.getSelectedIndex(), filename);
                    }
                    if (!isFileNull())
                        isLoadedAndHasFile = true;

                } catch (Exception eeeeee) {
                }
            }

            public void removeUpdate(DocumentEvent e) {
                hasChanged = true;
                filename = !isFileNull() ? file.getName() + "*" : "Untitled" + cont.getSelectedIndex() + " *";
            }

            public void changedUpdate(DocumentEvent e) {
                hasChanged = true;
                filename = !isFileNull() ? file.getName() + "*" : "Untitled" + cont.getSelectedIndex() + " *";
            }
        });

    }

    public void setHasChanged(boolean hasChanged) {
        this.hasChanged = hasChanged;
    }

    private boolean isFileNull() {
        return file == null;
    }

    public int getCursorPosition() {
        return cursorPosition;
    }

    public TextDocument(File file, DocumentContainer cont) throws IOException, BadLocationException {
        this(cont);
        this.cont = cont;
        Scanner scan = new Scanner(file);
        this.file = file;

        String content = "";

        try {
            do {
                content += scan.nextLine();
            } while (scan.hasNextLine());
        } catch (Exception e) {
        }
        scan.close();

        EditorKit kit = editorPane.getEditorKitForContentType("text/plain");
        kit.read(new FileReader(file), doc, 0);
    }

    public void registerEditListener(UndoableEditListener listener) {
        doc.addUndoableEditListener(listener);
    }

    public String getText() {
        return text;
    }

    public String getFilename() {
        if (file != null) {
            return file.getName();
        } else {
            return null;
        }
    }

    public void copy() {
        editorPane.copy();
    }

    public void cut() {
        editorPane.cut();
    }

    public void paste() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable transferable = clipboard.getContents(null);
        if (transferable != null && transferable.isDataFlavorSupported(java.awt.datatransfer.DataFlavor.stringFlavor)) {
            try {
                String text = (String) transferable.getTransferData(java.awt.datatransfer.DataFlavor.stringFlavor);
                editorPane.replaceSelection(text);

                hasChanged = true;
            } catch (Exception e) {

            }
        }
    }

    public boolean save() {
        if (file == null) {
            return saveAs();
        } else {
            try {
                filename = file.getName();
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                editorPane.write(writer);
                writer.close();
                hasChanged = false;
                cont.setTitleAt(cont.getSelectedIndex(), filename);
                return true;
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error saving file.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
    }

    public boolean saveAs() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save As");
        chooser.setFileFilter(new FileNameExtensionFilter("Text Documents (*.txt)", "txt"));
        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            file = chooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".txt")) {
                file = new File(file.getAbsolutePath() + ".txt");
                filename = file.getName();
                cont.setTitleAt(cont.getSelectedIndex(), filename);
            }

            return save();
        } else {
            return false;
        }
    }

    public boolean close() {
        if (hasChanged) { // why is it always executing?? ohh nvm got it
            Component parentComponent = getParent();

            String refFilename = filename.contains("*") ? filename.replace("*", "") : filename;

            if (parentComponent != null && parentComponent.isVisible()) {
                int result = JOptionPane.showConfirmDialog(parentComponent,
                        "Souhaitez-vous sauvegarder " + refFilename + "?",
                        "Sauvegarder",
                        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                if (result == JOptionPane.YES_OPTION) {
                    if (!save()) {
                        return false;
                    }
                } else if (result == JOptionPane.CANCEL_OPTION) {
                    return false;
                }
            } else {
                return true;
            }
        }
        return true;
    }

    public void undo() {
        if (undoManager.canUndo()) {
            try {
                undoManager.undo();
            } catch (CannotUndoException e) {
                System.out.println("Failure");
            }
        }
    }

    public void redo() {
        if (undoManager.canRedo()) {
            try {
                undoManager.redo();
            } catch (CannotRedoException e) {
                System.out.println("Failure");
            }
        }
    }

    int offset = 0;

    public boolean selectNext(String toSearch) {
        int cursorPosition = getCursorPosition();

        if (editorPane.getText() == null || cursorPosition < 0 || cursorPosition > editorPane.getText().length()) {
            return false;
        }

        int nextPosition = editorPane.getText().indexOf(toSearch, offset);

        while (nextPosition != -1 && nextPosition < cursorPosition) {
            nextPosition = editorPane.getText().indexOf(toSearch, nextPosition + 1);
        }

        if (nextPosition == -1) {
            offset = 0;
            return false;
        }

        setSelectionRange(nextPosition, nextPosition + toSearch.length());
        offset = nextPosition + toSearch.length();

        return true;
    }

    private void setSelectionRange(int cursorPosition2, int i) {
        editorPane.setSelectionStart(cursorPosition2);
        editorPane.setSelectionEnd(i);
    }

    public boolean replaceNext(String toSearch, String toReplace) {
        int startSelection = editorPane.getSelectionStart();
        int nextPosition = editorPane.getText().indexOf(toSearch);

        while (nextPosition != -1 && nextPosition < startSelection) {
            nextPosition = editorPane.getText().indexOf(toSearch, nextPosition + toSearch.length());
        }

        if (nextPosition == -1) {
            offset = 0;
            return false;
        }

        editorPane.setSelectionStart(nextPosition);
        editorPane.setSelectionEnd(nextPosition + toSearch.length());
        editorPane.replaceSelection(toReplace);
        offset = nextPosition + toReplace.length();

        return true;
    }

    public boolean replaceAll(String toSearch, String toReplace) throws BadLocationException {
        String text = doc.getText(0, doc.getLength());
        String newText = text.replaceAll(toSearch, toReplace);
        if (newText.equals(text)) {
            return false;
        }
        doc.remove(0, doc.getLength());
        doc.insertString(0, newText, null);
        return true;
    }

    public static File openDialog() {
        JFileChooser chooser = new JFileChooser();
        int option = chooser.showOpenDialog(null);
        if (option == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        }
        return null;
    }

    public boolean getHasChanged() {
        return hasChanged;
    }

}
