package labexamen;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main {

    private JavaMail javaMail;

    public Main() {
        try {
            javaMail = new JavaMail("usuarios.eml");
            showInitialScreen();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al inicializar el sistema: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showInitialScreen() {
        JFrame frame = new JFrame("JavaMail Login/Register");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);
        frame.setLayout(new FlowLayout());
        frame.getContentPane().setBackground(Color.LIGHT_GRAY);
        frame.setLocationRelativeTo(null);

        JButton btnLogin = new JButton("Log In");
        JButton btnRegister = new JButton("Register");

        btnLogin.setFont(new Font("Arial", Font.BOLD, 16));
        btnRegister.setFont(new Font("Arial", Font.BOLD, 16));

        frame.add(btnLogin);
        frame.add(btnRegister);

        btnRegister.addActionListener(e -> {
            String username = JOptionPane.showInputDialog(frame, "Enter Username:", "Register", JOptionPane.PLAIN_MESSAGE);
            if (username != null && !username.trim().isEmpty()) {
                String password = JOptionPane.showInputDialog(frame, "Enter Password:", "Register", JOptionPane.PLAIN_MESSAGE);
                if (password != null && !password.trim().isEmpty()) {
                    try {
                        javaMail.crearAccount(username, password);
                        JOptionPane.showMessageDialog(frame, "Account created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(frame, "Error while creating account: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Password cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Username cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnLogin.addActionListener(e -> {
            String username = JOptionPane.showInputDialog(frame, "Enter Username:", "Log In", JOptionPane.PLAIN_MESSAGE);
            if (username != null && !username.trim().isEmpty()) {
                String password = JOptionPane.showInputDialog(frame, "Enter Password:", "Log In", JOptionPane.PLAIN_MESSAGE);
                if (password != null && !password.trim().isEmpty()) {
                    try {
                        if (javaMail.login(username, password)) {
                            JOptionPane.showMessageDialog(frame, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            frame.dispose();
                            showMailPanel(username);
                        } else {
                            JOptionPane.showMessageDialog(frame, "Invalid credentials.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(frame, "Error while logging in: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frame, "Unexpected error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Password cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Username cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        frame.setVisible(true);
    }

    private void showMailPanel(String username) {
        JFrame frame = new JFrame("JavaMail - Welcome " + username);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(Color.LIGHT_GRAY);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1, 10, 10));
        panel.setBackground(Color.LIGHT_GRAY);

        JButton btnSendEmail = new JButton("Send Email");
        JButton btnInbox = new JButton("Inbox");
        JButton btnLogout = new JButton("Logout");

        btnSendEmail.setFont(new Font("Arial", Font.BOLD, 16));
        btnInbox.setFont(new Font("Arial", Font.BOLD, 16));
        btnLogout.setFont(new Font("Arial", Font.BOLD, 16));

        panel.add(btnSendEmail);
        panel.add(btnInbox);
        panel.add(btnLogout);

        frame.add(panel, BorderLayout.CENTER);

        btnSendEmail.addActionListener(e -> {
            frame.dispose();
            showSendEmailPanel(username);
        });

        btnInbox.addActionListener(e -> {
            frame.dispose();
            showInboxPanel(username);
        });

        btnLogout.addActionListener(e -> {
            frame.dispose();
            showInitialScreen();
        });

        frame.setVisible(true);
    }

    private void showSendEmailPanel(String username) {
        JFrame frame = new JFrame("Send Email");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 400);
        frame.getContentPane().setBackground(Color.LIGHT_GRAY);
        frame.setLocationRelativeTo(null);

        DefaultListModel<String> userListModel = new DefaultListModel<>();
        JList<String> userList = new JList<>(userListModel);
        JScrollPane scrollPane = new JScrollPane(userList);

        try {
            List<String> users = javaMail.getUserList();
            for (String user : users) {
                File userFile = new File("usuarios/" + user + "_emails.eml");
                if (userFile.exists() && !user.equals(username)) {
                    userListModel.addElement(user);
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "Error retrieving users: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        JButton btnSend = new JButton("Send");
        btnSend.setFont(new Font("Arial", Font.BOLD, 16));
        btnSend.addActionListener(e -> {
            String selectedUser = userList.getSelectedValue();
            if (selectedUser != null) {
                String subject = JOptionPane.showInputDialog(frame, "Enter Subject:", "Send Email", JOptionPane.PLAIN_MESSAGE);
                String content = JOptionPane.showInputDialog(frame, "Enter Content:", "Send Email", JOptionPane.PLAIN_MESSAGE);

                if (subject != null && content != null) {
                    try {
                        javaMail.createEmail(username + "@javamail.org", subject, content, selectedUser);
                        JOptionPane.showMessageDialog(frame, "Email sent successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        frame.dispose();
                        showMailPanel(username);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(frame, "Error while sending email: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a recipient.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.setLayout(new BorderLayout());
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(btnSend, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void showInboxPanel(String username) {
        JFrame frame = new JFrame("Inbox - " + username);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.getContentPane().setBackground(Color.LIGHT_GRAY);
        frame.setLocationRelativeTo(null);

        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> emailList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(emailList);

        try {
            String[] emails = javaMail.getInboxAsString().split("\\n");
            for (String email : emails) {
                listModel.addElement(email);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "Error retrieving inbox: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        JButton btnView = new JButton("View");
        btnView.setFont(new Font("Arial", Font.BOLD, 16));
        btnView.addActionListener(e -> {
            int selectedIndex = emailList.getSelectedIndex();
            if (selectedIndex != -1) {
                try {
                    String emailContent = javaMail.readEmail(selectedIndex + 1);
                    JOptionPane.showMessageDialog(frame, emailContent, "Email Details", JOptionPane.INFORMATION_MESSAGE);
                    frame.dispose();
                    showInboxPanel(username);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame, "Error retrieving email: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } catch (IllegalStateException ex) {
                    JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Please select an email to view.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnBack = new JButton("Back");
        btnBack.setFont(new Font("Arial", Font.BOLD, 16));
        btnBack.addActionListener(e -> {
            frame.dispose();
            showMailPanel(username);
        });

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        buttonPanel.add(btnView);
        buttonPanel.add(btnBack);

        frame.setLayout(new BorderLayout());
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new Main();
    }
}
