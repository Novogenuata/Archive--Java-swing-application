import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class DocumentContainer extends JTabbedPane {
    // private ArrayList<Document> openDocuments;

    public DocumentContainer() {
        super();
        createNewDocument();
    }

    public void createNewDocument() {

        TextDocument newDocument = new TextDocument(this);
        addTab("Untitled" + (getSelectedIndex() + 1), newDocument);
        setSelectedComponent(newDocument);

    }

    public void openDocument() {

        File file = TextDocument.openDialog();
        if (file != null) {
            try {
                TextDocument doc = new TextDocument(file, this);
                addTab(doc.getFilename(), doc);
                setSelectedComponent(doc);
                doc.setHasChanged(false);
            } catch (IOException | BadLocationException e) {
                JOptionPane.showMessageDialog(this, "Error opening file: " + e.getMessage());
            }
        }
    }

    public void saveCurrentDocument() {
        int selectedTabIndex = getSelectedIndex();
        if (selectedTabIndex != -1) {
            Component selectedTab = getSelectedComponent();
            if (selectedTab instanceof TextDocument) {
                TextDocument selectedDocument = (TextDocument) selectedTab;
                boolean saved = selectedDocument.save();
                if (saved) {

                    setTitleAt(selectedTabIndex, selectedDocument.getFilename());
                }
            }
        }
    }

    public void saveAsCurrentDocument() {
        int selectedIndex = getSelectedIndex();
        if (selectedIndex != -1) {
            Component selectedComponent = getComponentAt(selectedIndex);
            if (selectedComponent instanceof TextDocument) {
                TextDocument textDoc = (TextDocument) selectedComponent;
                textDoc.saveAs();
            }
        }
    }

    public boolean closeCurrentDocument() {
        int selectedIndex = getSelectedIndex();
        if (selectedIndex >= 0) {
            TextDocument selectedDoc = (TextDocument) getComponentAt(selectedIndex);
            System.out.println("Closing document: ");
            if (selectedDoc.close()) {
                System.out.println("Document closed successfully.");
                remove(selectedIndex);
                if (getTabCount() == 0) {
                    createNewDocument();
                }
                return true;
            } else {
                System.out.println("Document close cancelled.");
                return false;
            }
        }

        System.out.println("No document selected.");
        return false;
    }

    public boolean closeAllDocument() {
        boolean allDocumentsClosed = true;
        int documentCount = getTabCount();

        for (int i = documentCount - 1; i >= 0; i--) {
            TextDocument textDocument = (TextDocument) getComponentAt(i);
            if (!textDocument.close()) {
                allDocumentsClosed = false;
                break;
            } else {
                remove(i);
            }

        }
        if (allDocumentsClosed) {
            createNewDocument();
        }

        return allDocumentsClosed;
    }

    void undoCurrentDocument() {
        int selectedTabIndex = this.getSelectedIndex();
        TextDocument currentDocument = (TextDocument) this.getComponentAt(selectedTabIndex);
        currentDocument.undo();
    }

    void redoCurrentDocument() {
        int selectedTabIndex = this.getSelectedIndex();
        TextDocument currentDocument = (TextDocument) this.getComponentAt(selectedTabIndex);
        currentDocument.redo();
    }

    void copyInCurrentDocument() {
        int selectedIndex = getSelectedIndex();
        if (selectedIndex != -1) {
            Component selectedComponent = getComponentAt(selectedIndex);
            if (selectedComponent instanceof TextDocument) {
                ((TextDocument) selectedComponent).copy();
            }
        }
    }

    void cutInCurrentDocument() {
        int selectedIndex = getSelectedIndex();
        if (selectedIndex != -1) {
            Component selectedComponent = getComponentAt(selectedIndex);
            if (selectedComponent instanceof TextDocument) {
                ((TextDocument) selectedComponent).cut();
            }
        }
    }

    public void pasteInCurrentDocument() {
        int index = getSelectedIndex();
        if (index != -1) {
            Component selectedComponent = getComponentAt(index);
            if (selectedComponent instanceof TextDocument) {
                TextDocument document = (TextDocument) selectedComponent;
                document.paste();
            }
        }
    }

    public void selectNext(String toSearch) {
        int selectedIndex = getSelectedIndex();

        if (selectedIndex != -1) {
            Component selectedComponent = getComponentAt(selectedIndex);
            if (selectedComponent instanceof TextDocument) {
                ((TextDocument) selectedComponent).selectNext(toSearch);
            }
        }

    }

    public void replaceNext(String toSearch, String toReplace) {
        int selectedIndex = getSelectedIndex();
        if (selectedIndex != -1) {
            Component selectedComponent = getComponentAt(selectedIndex);
            if (selectedComponent instanceof TextDocument) {
                ((TextDocument) selectedComponent).replaceNext(toSearch, toReplace);
            }
        }
    }

    public void replaceAll(String toSearch, String toReplace) throws BadLocationException {
        int selectedIndex = getSelectedIndex();
        if (selectedIndex != -1) {
            Component selectedComponent = getComponentAt(selectedIndex);
            if (selectedComponent instanceof TextDocument) {
                ((TextDocument) selectedComponent).replaceAll(toSearch, toReplace);
            }
        }
    }

}
