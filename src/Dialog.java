import javax.swing.*;
import java.awt.*;

class Dialog extends JDialog {
    Dialog(String infoStr) {
        Container contentPane = this.getContentPane();
        JLabel info = new JLabel();
        contentPane.add(info);
        info.setFont(Main.defaultfont);
        info.setText(infoStr);
        info.setBounds(10, 10, 100, 65);
        this.setBounds(750, 300, 400, 300);
        transferFocusUpCycle();
        setDefaultLookAndFeelDecorated(true);
        setAutoRequestFocus(true);
        this.setVisible(true);
    }
}
