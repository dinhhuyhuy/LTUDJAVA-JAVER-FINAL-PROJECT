package chatapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.SwingUtilities;

import user.Login;

public class Client {
    Socket client;
    final int PORT = 500;
    PrintWriter pw;
    BufferedReader br;

    public Client() {
        try {
            client = new Socket(InetAddress.getLocalHost(), PORT);
            // client = new Socket("localhost", PORT);
            OutputStream clientOut = client.getOutputStream();
            pw = new PrintWriter(new OutputStreamWriter(clientOut, "UTF-8"), true);

            // Create an input stream of the client socket
            InputStream clientIn = client.getInputStream();
            br = new BufferedReader(new InputStreamReader(clientIn, "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Client socket is created " + client);
        connect();
    }

    public void connect() {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                System.out.println("Into login");
                Login loginForm = new Login(client, pw, br);
                loginForm.setVisible(true);
            }
        });

    }

    public static void main(String[] args) {
        new Client();
    }

}
