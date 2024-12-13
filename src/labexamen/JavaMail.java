/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package labexamen;

/**
 *
 * @author moiza
 */
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class JavaMail {
    private RandomAccessFile userFile;
    private CurrentUser currentUser; 
    private final String userDirectory = "usuarios"; 

    public JavaMail(String userFileName) throws IOException {
        File directory = new File(userDirectory);
        if (!directory.exists()) {
            boolean created = directory.mkdir();
            if (!created) {
                throw new IOException("No se pudo crear la carpeta 'usuarios'.");
            }
        }

        File userFilePath = new File(directory, userFileName);
        if (!userFilePath.exists()) {
            boolean created = userFilePath.createNewFile();
            if (!created) {
                throw new IOException("No se pudo crear el archivo 'usuarios.eml'.");
            }
        }

        this.userFile = new RandomAccessFile(userFilePath, "rw");
        this.currentUser = null;
    }

    public boolean login(String username, String password) throws IOException {
        userFile.seek(0);

        while (userFile.getFilePointer() < userFile.length()) {
            String storedUsername = userFile.readUTF();
            String storedPassword = userFile.readUTF();

            if (storedUsername.equals(username) && storedPassword.equals(password)) {
                File emailFile = new File(userDirectory, username + "_emails.eml");
                currentUser = new CurrentUser(username, emailFile);
                currentUser.loadFromFile();
                return true;
            }
        }
        return false;
    }

    public void crearAccount(String username, String password) throws IOException {
    userFile.seek(0);

    while (userFile.getFilePointer() < userFile.length()) {
        String storedUsername = userFile.readUTF();
        if (storedUsername.equals(username)) {
            throw new IllegalArgumentException("El nombre de usuario ya existe.");
        }
    }

    userFile.seek(userFile.length());
    userFile.writeUTF(username);
    userFile.writeUTF(password);

    File emailFile = new File(userDirectory, username + "_emails.eml");
    if (!emailFile.createNewFile()) {
        throw new IOException("No se pudo crear el archivo de emails para el usuario.");
    }
}

    public void createEmail(String from, String subject, String content, String toUsername) throws IOException {
    if (currentUser == null) {
        throw new IllegalStateException("Login Primero.");
    }

    File recipientFile = new File("usuarios/" + toUsername + "_emails.eml");
    if (!recipientFile.exists()) {
        throw new IOException("El destinatario no existe.");
    }

    try (RandomAccessFile recipientEmailFile = new RandomAccessFile(recipientFile, "rw")) {
        recipientEmailFile.seek(recipientEmailFile.length()); 
        recipientEmailFile.writeUTF(from);        
        recipientEmailFile.writeUTF(subject);     
        recipientEmailFile.writeBoolean(false);   
        recipientEmailFile.writeLong(System.currentTimeMillis()); 
        recipientEmailFile.writeUTF(content);   
    }
}

    public String getInboxAsString() throws IOException {
    if (currentUser == null) {
        throw new IllegalStateException("Login Primero.");
    }

    StringBuilder inbox = new StringBuilder();
    EmailNodo actual = currentUser.getPrimerEmail();
    int posicion = 1;

    while (actual != null) {
        inbox.append(posicion)
            .append(" - ")
            .append(actual.remitente) 
            .append(" - ")
            .append(actual.asunto)
            .append(" - ")
            .append(actual.leido ? "LEÍDO" : "NO LEÍDO")
            .append("\n");
        actual = actual.siguiente; 
        posicion++;
    }

    return inbox.toString();
}

    public List<String> getUserList() throws IOException {
        List<String> userList = new ArrayList<>();
        userFile.seek(0);

        while (userFile.getFilePointer() < userFile.length()) {
            String storedUsername = userFile.readUTF();
            userFile.readUTF(); 
            userList.add(storedUsername);
        }

        return userList;
    }

    public String readEmail(int pos) throws IOException {
        if (currentUser == null) {
            throw new IllegalStateException("Login Primero.");
        }

        EmailNodo current = currentUser.getPrimerEmail();
        int position = 1;

        while (current != null) {
            if (position == pos) {
                currentUser.readEmail(pos); 
                return "De: " + current.remitente + "\nAsunto: " + current.asunto + "\nContenido: (Recuperado del archivo)";
            }
            current = current.siguiente;
            position++;
        }

        throw new NoSuchElementException("No existe un email en la posición especificada.");
    }
}