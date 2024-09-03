package ui;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class Md4Form {
    private JPanel panel;
    private JSplitPane splitPane;
    private JTextArea text;
    private JTextArea hash;

    private TextChanged textChanged;

    public Md4Form() {
//        splitPane.setDividerLocation();
        splitPane.setResizeWeight(0.1);
        text.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (textChanged != null) textChanged.textChanged(text.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (textChanged != null) textChanged.textChanged(text.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (textChanged != null) textChanged.textChanged(text.getText());
            }
        });
    }

    public JPanel getPanel() {
        return panel;
    }

    public JTextArea getText() {
        return text;
    }

    public JTextArea getHash() {
        return hash;
    }

    public void setTextChanged(TextChanged textChanged) {
        this.textChanged = textChanged;
    }

    public interface TextChanged {
        void textChanged(String text);
    }
}
