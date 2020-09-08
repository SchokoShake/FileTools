import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

public class FileGUI extends JFrame
{
    private JTextField t1, t2;
    private JLabel info1, info2;
    private JButton button;
    public JProgressBar progressBar;
    private JLabel message;

    public static void main(String[] args)
    {
        new FileGUI();
    }

    public FileGUI()
    {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // find Desktop dir
        FileSystemView filesys = FileSystemView.getFileSystemView();
        String desktopDir = String.valueOf(filesys.getHomeDirectory());

        info1 = new JLabel("Verzeichnis zum Untersuchen");
        info1.setBounds(10, 100, 200, 30);
        add(info1);

        t1 = new JTextField(desktopDir);
        t1.setBounds(200, 100, 200, 30);
        add(t1);

        info2 = new JLabel("Pfad zu Textdatei");
        info2.setBounds(10, 150, 200, 30);
        add(info2);

        t2 = new JTextField(desktopDir);
        t2.setBounds(200, 150, 200, 30);
        add(t2);

        button = new JButton("Search");
        button.setBounds(50, 200, 200, 30);
        add(button);

        message = new JLabel();
        message.setBounds(50, 250, 200, 30);
        add(message);

        progressBar = new JProgressBar();
        progressBar.setBounds(50, 300, 200, 30);
        progressBar.setVisible(false);
        add(progressBar);


        setSize(500, 400);
        setLayout(null);
        setVisible(true);

        button.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String pathT1 = t1.getText();
                String pathT2 = t2.getText();
                FindDuplicatesTask task=new FindDuplicatesTask(pathT1,pathT2);
                task.addPropertyChangeListener(
                        new PropertyChangeListener() {
                            public  void propertyChange(PropertyChangeEvent evt) {
                                progressBar.setIndeterminate(false);
                                if ("progress".equals(evt.getPropertyName())) {
                                    progressBar.setValue((Integer)evt.getNewValue());
                                    if((Integer)evt.getNewValue()==100){
                                        message.setText("Fertig!");
                                    }
                                }
                            }
                        });
                if (isValidDir(pathT1) && isValidDir(pathT2))
                {
                    System.out.println(pathT1);
                    System.out.println(pathT2);

                    message.setText("Dies kann einige Minuten dauern");
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);

                    task.execute();

                } else if (!isValidDir(pathT1))
                {
                    info1.setText("Pfad Ungültig!!");
                }
                if (!isValidDir(pathT2))
                {
                    info2.setText("Pfad Ungültig!!");
                }

            }
        });
    }



    public static boolean isValidDir(String path)
    {
        if (isValidPath(path))
        {
            return new File(path).isDirectory();
        }
        return false;
    }

    public static boolean isValidPath(String path)
    {
        try
        {
            Paths.get(path);
        } catch (InvalidPathException | NullPointerException ex)
        {
            return false;
        }
        return true;
    }
}

