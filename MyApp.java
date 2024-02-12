import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.filechooser.FileFilter;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.*;
import java.io.File;

public class MyApp extends JFrame {

    DocumentContainer documentContainer;
    SeachDialog seachDialog;
    ReplaceDialog replaceDialog;

    public MyApp() {
        this.setTitle("UnSticky Notes");
        this.setSize(800, 600);
        this.setLocationRelativeTo(null);

        addComponents();
        addMenus();
        addListeners();

        this.setVisible(true);
    }

    public void addMenus() {
        var menuBar = new JMenuBar();
        var menuFile = new JMenu("File");
        var menuEdition = new JMenu("Edition");
        var menuTools = new JMenu("Outils");
        var menuHelp = new JMenu("Help");

        // a propos
        var menuHelpDesc = new JMenuItem("A Propos");
        menuHelpDesc.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        menuHelpDesc.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog dialog = new JDialog();
                dialog.setTitle("A propos");
                dialog.setSize(300, 200);
                dialog.setLocationRelativeTo(null);
                dialog.setResizable(false);
                JTextArea label = new JTextArea(
                        "UnSticky Notes \nSigourney Wamback \n2023");
                label.setEditable(false);
                label.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));
                dialog.add(label);
                dialog.setVisible(true);

            }
        });

        // editor
        var menuCopy = new JMenuItem("Copy");
        var menuUndo = new JMenuItem("Undo");
        var menuRedo = new JMenuItem("Redo");
        var menuCut = new JMenuItem("Cut");
        var menuPaste = new JMenuItem("Paste");

        menuCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
        menuCopy.addActionListener(e -> {
            documentContainer.copyInCurrentDocument();
        });
        menuUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
        menuUndo.addActionListener(e -> {
            documentContainer.undoCurrentDocument();
        });
        menuRedo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK));
        menuRedo.addActionListener(e -> {
            documentContainer.redoCurrentDocument();
        });
        menuCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
        menuCut.addActionListener(e -> {
            documentContainer.cutInCurrentDocument();
        });
        menuPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
        menuPaste.addActionListener(e -> {
            documentContainer.pasteInCurrentDocument();
        });
        // tools
        var menuSearch = new JMenuItem("Rechercher");
        var menuReplace = new JMenuItem("Remplacer");
        menuSearch.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK));
        menuSearch.addActionListener(e -> {
            seachDialog.setVisible(true);
        });
        menuReplace.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        menuReplace.addActionListener(e -> {
            replaceDialog.setVisible(true);
        });

        // File
        var menuFileNew = new JMenuItem("Nouveau");
        var menuFileOpen = new JMenuItem("Ouvrir");
        var menuFileQuit = new JMenuItem("Quitter");
        var menuFileSave = new JMenuItem("Sauvegarder");
        var menuFileSaveUnder = new JMenuItem("Sauvegarder sous...");
        var menuFileClose = new JMenuItem("Fermer");
        var menuFileCloseAll = new JMenuItem("Fermer tout");

        menuFileNew.addActionListener(e -> {
            // JOptionPane.showMessageDialog(null, "Fichier nouveau!");
            documentContainer.createNewDocument();
        });

        menuFileNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        menuFileOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        menuFileSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        menuFileSave.addActionListener(e -> {
            documentContainer.saveCurrentDocument();
        });
        menuFileSave.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        menuFileSaveUnder.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        menuFileSaveUnder.addActionListener(e -> {
            documentContainer.saveAsCurrentDocument();
        });
        menuFileClose.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK));
        menuFileClose.addActionListener(e -> {
            documentContainer.closeCurrentDocument();
        });
        menuFileCloseAll.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        menuFileCloseAll.addActionListener(e -> {
            documentContainer.closeAllDocument();
        });
        menuFileOpen.addActionListener(e -> {
            documentContainer.openDocument();
        });

        menuFileQuit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK));
        menuFileQuit.addActionListener(e -> {
            var choix = JOptionPane.showConfirmDialog(super.rootPane, "Êtes-vous sur de vouloir quitter?",
                    "Quitter?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            System.out.println(choix);
            if (choix == JOptionPane.YES_OPTION) {
                System.exit(0);
            }

        });

        menuFile.add(menuFileNew);
        menuFile.addSeparator();
        menuFile.add(menuFileSave);
        menuFile.add(menuFileSaveUnder);
        menuFile.addSeparator();
        menuFile.add(menuFileOpen);
        menuFile.add(menuFileClose);
        menuFile.add(menuFileCloseAll);
        menuFile.addSeparator();
        menuFile.add(menuFileQuit);
        // documentContainer.openDocument();
        add(documentContainer);

        menuBar.add(menuFile);
        menuBar.add(menuEdition);
        menuHelp.add(menuHelpDesc);
        menuEdition.add(menuCopy);
        menuEdition.add(menuPaste);
        menuEdition.add(menuCut);
        menuEdition.addSeparator();
        menuEdition.add(menuUndo);
        menuEdition.add(menuRedo);
        menuBar.add(menuTools);
        menuTools.add(menuSearch);
        menuTools.add(menuReplace);
        menuBar.add(menuHelp);
        this.setJMenuBar(menuBar);
    }

    public void addComponents() {
        documentContainer = new DocumentContainer();
        seachDialog = new SeachDialog(documentContainer);
        replaceDialog = new ReplaceDialog(documentContainer);
    }

    public void addListeners() {

        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {

                int option = JOptionPane.showConfirmDialog(null, "voulez-vous sauvegarder avant de quitter?",
                        "sauvegarder?", JOptionPane.YES_NO_CANCEL_OPTION);

                if (option == JOptionPane.YES_OPTION) {
                    documentContainer.saveCurrentDocument();
                    e.getWindow().dispose();
                }

                else if (option == JOptionPane.NO_OPTION) {
                    e.getWindow().dispose();
                }

                else if (option == JOptionPane.CANCEL_OPTION) {
                    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                }
            }

        });

    }

}