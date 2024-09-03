import ui.Md4Form;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;

public class Ui {
    private final JFrame frame;
    private final Md4Form md4Form = new Md4Form();
    private final Md4 md4 = new Md4();
    private final JFileChooser fileChooser;

    public Ui() {
        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("MD4");

        var menuBar = new JMenuBar();
        var menu = getjMenu();
        menuBar.add(menu);
        frame.setJMenuBar(menuBar);

        addListeners();

        frame.add(md4Form.getPanel());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JMenu getjMenu() {
        var menu = new JMenu("File");
        var item = new JMenuItem("Hash File");
        item.addActionListener(e -> {
            int returnVal = fileChooser.showOpenDialog(frame);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                var file = fileChooser.getSelectedFile();
                try {
                    var bytes = Files.readAllBytes(file.toPath());
                    bytes = md4.engineDigest(bytes);
                    md4Form.getHash().setText(bytesToString(bytes));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        menu.add(item);
        return menu;
    }

    private void addListeners() {
        md4Form.setTextChanged(s -> {
            var bytes = md4.engineDigest(s.getBytes());
            md4Form.getHash().setText(bytesToString(bytes));
        });
    }

    private static String bytesToString(byte[] bytes) {
        var sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
