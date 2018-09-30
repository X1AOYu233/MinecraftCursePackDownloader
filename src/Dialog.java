import com.sun.istack.internal.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

class Dialog extends JDialog {
    Dialog(String infoStr){
        Container contentPane = this.getContentPane();
        JLabel info = new JLabel();
        contentPane.add(info);
        info.setFont(Main.defaultfont);
        info.setText(infoStr);
        info.setBounds(10, 10, 100, 65);
        this.setBounds(750, 300, 400, 300);
        transferFocusUpCycle();
        setAutoRequestFocus(true);
        this.setVisible(true);

    }
}
