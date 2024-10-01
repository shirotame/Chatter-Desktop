package chatter.desktop.ui;

import java.io.PrintStream;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import chatter.desktop.client.Client;
import chatter.desktop.tools.ApplicationOutputStream;
import chatter.desktop.tools.ServerConnectionException;

public class Application extends JFrame implements KeyListener {
    private static Integer DEFAULT_PORT = 4004;
    private transient Client client;
    private String nickname;
    private String hostIp;
    private Integer port;
    private JTextField textField;
    private JTextArea chatBox = new JTextArea(20, 40);

    public Application() {
        super("Чат");
        makeUi();
    }

    private void makeUi() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (client != null)
                    disconnectFromServer();
            }
        });

        JPanel mainPanel = new JPanel();
        JPanel upperPanel = new JPanel();
        JPanel chatPanel = new JPanel();

        chatPanel.setLayout(new GridBagLayout());
        upperPanel.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();

        JTextField nicknameField = new JTextField(33);
        JTextField hostIpField = new JTextField(33);
        textField = new JTextField(33);

        chatBox.setEditable(false);

        JScrollPane scrollPanel = new JScrollPane(chatBox);

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;

        chatPanel.add(scrollPanel, constraints);

        constraints.insets.top = 10;
        constraints.gridwidth = 1;
        constraints.gridx = 0;
        constraints.gridy = 1;
        
        chatPanel.add(new JLabel("Сообщение"), constraints);

        constraints.insets.top = 0;
        constraints.gridx = 0;
        constraints.gridy = 2;

        textField.addKeyListener(this);
        chatPanel.add(textField, constraints);

        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.fill = GridBagConstraints.NONE;

        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(e -> sendMessage());
        chatPanel.add(sendButton, constraints);

        constraints.gridwidth = 2;
        constraints.gridx = 0;
        constraints.gridy = 0;

        upperPanel.add(new JLabel("Адрес и порт (порт по умолчанию 4004)"), constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;

        upperPanel.add(hostIpField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;

        upperPanel.add(new JLabel("Ник"), constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;

        upperPanel.add(nicknameField, constraints);

        JButton connectButton = new JButton("Connect");
        JButton disconnectButton = new JButton("Disconnect");
        disconnectButton.setEnabled(false);

        connectButton.addActionListener(e -> {
            if (hostIpField.getText().isEmpty() || nicknameField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Введите адрес и/или ник!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
            else if (nicknameField.getText().length() > 52) {
                JOptionPane.showMessageDialog(this, "Твоя мать шлюха (максимум 52 симовлов)", "UserEblanException", JOptionPane.ERROR_MESSAGE);
            }
            else {
                hostIp = hostIpField.getText().split(":")[0];
                port = hostIpField.getText().split(":").length != 1 ? Integer.parseInt(hostIpField.getText().split(":")[1]) : DEFAULT_PORT;
                nickname = nicknameField.getText();
                try {
                    PrintStream printStream = new PrintStream(new ApplicationOutputStream(chatBox));
                    client = new Client(nickname, hostIp, port, printStream);

                    client.sendInitialMsg();

                    disconnectButton.setEnabled(true);
                    connectButton.setEnabled(false);
                }
                catch (ServerConnectionException ex) {
                    chatBox.append(ex.getMessage() + "\n");
                }
            }
        });

        constraints.gridwidth = 1;
        constraints.insets.top = 10;
        constraints.insets.bottom = 10;
        constraints.insets.left = 40;
        constraints.gridx = 0;
        constraints.gridy = 5;

        upperPanel.add(connectButton, constraints);

        disconnectButton.addActionListener(e -> {
                disconnectFromServer();

                connectButton.setEnabled(true);
                disconnectButton.setEnabled(false);
        });

        constraints.gridx = 1;
        constraints.gridy = 5;

        upperPanel.add(disconnectButton, constraints);

        mainPanel.add(upperPanel);
        mainPanel.add(chatPanel);

        getContentPane().add(mainPanel);
    }

    private void sendMessage() {
        String msg = textField.getText();
        if (msg.isEmpty()) {
            return;
        }
        if (msg.length() > 2000) {
            JOptionPane.showMessageDialog(this, "Твоя мать шлюха (максимум 2000 симовлов)", "UserEblanException", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (client != null) {
            client.sendMessage(msg);
        }
        textField.setText("");
    }

    private void disconnectFromServer() {
        client.sendDisconnectMsg();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == KeyEvent.VK_ENTER) {
            sendMessage();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // not needed
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // not needed
    }
}
