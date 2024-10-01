package chatter.desktop.client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.URL;

import chatter.desktop.tools.ServerConnectionException;

/**
Class for creating client session with java.net.Socket, 
using System.out and System.err for output the messages from server.
 */
public class Client {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String hostIp;
    private int port;
    private String nickname;

    /**
    Creates client socket and connecting to provided hostIp:port.
    @param nickname - clients nickname, used for formatting message string
    @param hostIp - IP socket connects to
    @param port - port socket connects to
    @param printStream - PrintStream that will be used for displaying messages from server
    @throws ServerConnectionException if connection to server fails
     */
    public Client(String nickname, String hostIp, int port, PrintStream printStream) throws ServerConnectionException {
        this.hostIp = hostIp;
        this.port = port;
        this.nickname = nickname;

        System.setOut(printStream);
        System.setErr(printStream);

        initialization(fetchExternalIp());

        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            
            new ReadMsg().start();
        } catch (IOException e) {
            System.err.println("Socket closed");
        }
        
    }

    /**
    Sending message to server .
    @param message - message string
     */
    public void sendMessage(String message) {
        new WriteMsg(message).start();
    }

    /**
    Sending initial message to server (server will anounce that client is connected).
     */
    public void sendInitialMsg() {
        new WriteMsg(true).start();
    }

    /**
    Sending disconnect message to server (server will anounce that client is disconnected),
    and closing client socket.
     */
    public void sendDisconnectMsg() {
        try {
            if (!socket.isClosed()) {
                out.writeUTF("dscmsg");
                out.flush();

                socket.close();
                in.close();
                out.close();
            }
        } catch (IOException | NullPointerException e) {
            System.err.println("Conncetion closed");
        }
    }

    private void initialization(String externalIp) throws ServerConnectionException {
        try {
            this.socket = new Socket(this.hostIp, this.port);
            DataOutputStream d = new DataOutputStream(socket.getOutputStream());
            d.writeByte(1);
            d.flush();
            d.writeUTF(this.nickname);
            d.flush();
            d.writeUTF(externalIp);
            d.flush();
        }
        catch (IOException ex) {
            throw new ServerConnectionException("Server connection failed");
        }
    }

    private String fetchExternalIp() {
        String extIp;
        URL service;
        try {
            service = new URL("https://checkip.amazonaws.com");
        } catch (Exception ex) {
            extIp = "unknown";
            return extIp;
        }
        try (BufferedReader inputFromCheckIp = new BufferedReader(new InputStreamReader(service.openStream()));) {
            extIp = inputFromCheckIp.readLine();
        } catch (Exception ex) {
            extIp = "unknown";
        }
        return extIp;
    }

    private class ReadMsg extends Thread {    
        @Override
        public void run() {
            String str;
            try {
                while (true) {
                    str = in.readUTF();
                    System.out.println(str);
                }
            } catch (IOException e) {
                System.err.println("Socket closed");
                Client.this.sendDisconnectMsg();
            }
        }
    }
    
    private class WriteMsg extends Thread {
        private String message = null;
        private boolean isInitial = false;

        public WriteMsg(String message) {
            this.message = message;
        }

        public WriteMsg(boolean isInitial) {
            this.isInitial = isInitial;
        }

        @Override
        public void run() {
            try {
                if (isInitial) {
                    out.writeUTF("initmsg");
                    out.flush();
                    this.interrupt();
                }
                else {
                    out.writeUTF(message);
                    out.flush();
                }
            } catch (IOException e) {
                System.err.println("Socket closed");
                Client.this.sendDisconnectMsg();
            }
        }
    }
}
