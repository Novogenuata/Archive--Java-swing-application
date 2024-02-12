import javax.swing.*;
import javax.swing.JPanel;

public class SeachDialog extends JDialog {
    DocumentContainer documentContainer;
    private JTextField searchField;

    public SeachDialog(DocumentContainer documentContainer) {
        addComponents();
        addListeners();

        this.documentContainer = documentContainer;
        setTitle("Recherche");
        setSize(300, 120);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        add(panel);
        JLabel searchLabel = new JLabel("Recherche");
        panel.add(searchLabel);
        searchField = new JTextField(20);
        panel.add(searchField);
        JButton searchButton = new JButton("Suivant");
        panel.add(searchButton);
        searchButton.addActionListener(e -> {
            String query = searchField.getText();
            documentContainer.selectNext(query);
        });
        JButton closeButton = new JButton("Annuler");
        panel.add(closeButton);
        closeButton.addActionListener(e -> dispose());
    }

    private void addComponents() {
        documentContainer = new DocumentContainer();
    }

    private void addListeners() {

    }
}
