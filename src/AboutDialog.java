import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

class AboutDialog extends JDialog {
    AboutDialog(){
        Container contentPane = this.getContentPane();
        JLabel info = new JLabel("此下载器是基于酒石酸菌的简易多线程下载器制作而成的");
        JLabel info2 = new JLabel("实际上只是在它的基础上写了个GUI");
        JButton openSoursePage = new JButton("打开源码页面");
        openSoursePage.addActionListener((l)->{
            try {
                Desktop.getDesktop().browse(new URI("https://github.com/TartaricAcid/SimpleMultithreadedDownloader"));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        });
        contentPane.add(info);
        contentPane.add(info2);
        contentPane.add(openSoursePage);
        setLayout(null);
        info2.setBounds(0, 65, 500, 65);
        info2.setFont(Main.defaultfont);
        info.setFont(Main.defaultfont);
        info.setBounds(0, 0, 500, 65);
        this.setBounds(750, 300, 500, 300);
        openSoursePage.setBounds(65,200,150,30);
        transferFocusUpCycle();
        setAutoRequestFocus(true);
        this.setVisible(true);
    }
}
