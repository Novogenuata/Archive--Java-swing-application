import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;

public class ReplaceDialog extends JDialog {
    DocumentContainer documentContainer;
    private JTextField searchField, replaceField;

    public ReplaceDialog(DocumentContainer documentContainer) {
        addComponents();
        addListeners();
        this.documentContainer = documentContainer;
        setTitle("Remplacer");
        setSize(500, 200);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(5, 5, 5, 5);
        panel.add(new JLabel("Recherche:"), c);
        c.gridx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        searchField = new JTextField(10);
        panel.add(searchField, c);

        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Remplacer par:"), c);
        c.gridx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        replaceField = new JTextField(10);
        panel.add(replaceField, c);

        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.add(buttonPanel, c);
        JButton searchButton = new JButton("Suivant");
        searchButton.setPreferredSize(new Dimension(120, 30));
        buttonPanel.add(searchButton);

        JButton replaceButton = new JButton("Remplacer");
        replaceButton.setPreferredSize(new Dimension(120, 30));
        buttonPanel.add(replaceButton);

        JButton replaceAllButton = new JButton("Remplacer tout");
        replaceAllButton.setPreferredSize(new Dimension(120, 30));
        buttonPanel.add(replaceAllButton);

        JButton closeButton = new JButton("Annuler");
        closeButton.setPreferredSize(new Dimension(120, 30));
        buttonPanel.add(closeButton);

        panel.add(new JPanel(), c);
        c.gridy = 3;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        panel.add(closeButton, c);

        setContentPane(panel);

        searchButton.addActionListener(e -> {
            String query = searchField.getText();
            documentContainer.selectNext(query);
        });

        replaceButton.addActionListener(e -> {
            String query = searchField.getText();
            String replacement = replaceField.getText();
            documentContainer.replaceNext(query, replacement);
        });

        replaceAllButton.addActionListener(e -> {
            String query = searchField.getText();
            String replacement = replaceField.getText();
            try {
                documentContainer.replaceAll(query, replacement);
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        });

        closeButton.addActionListener(e -> dispose());

    }

    private void addComponents() {
        documentContainer = new DocumentContainer();
    }

    private void addListeners() {
    }
}
