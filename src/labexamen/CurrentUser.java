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
import java.util.NoSuchElementException;
import java.util.Date;

public class CurrentUser {
    public String username;
    public RandomAccessFile emailFile;
    public EmailNodo primerEmail;

    public CurrentUser(String username, File emailFile) throws IOException {
        this.username = username;
        this.emailFile = new RandomAccessFile(emailFile, "rw");
        this.primerEmail = null;
    }

    public void add(EmailNodo email) {
        if (primerEmail == null) {
            primerEmail = email;
        } else {
            EmailNodo actual = primerEmail;
            while (actual.siguiente != null) {
                actual = actual.siguiente;
            }
            actual.siguiente = email;
        }
    }

    public void loadFromFile() throws IOException {
    primerEmail = null; 
    emailFile.seek(0); 
    while (emailFile.getFilePointer() < emailFile.length()) {
        long posicion = emailFile.getFilePointer();
        String remitente = emailFile.readUTF();
        String asunto = emailFile.readUTF();   
        boolean leido = emailFile.readBoolean(); 
        long fecha = emailFile.readLong();      
        String contenido = emailFile.readUTF(); 

        EmailNodo nuevoEmail = new EmailNodo(posicion, remitente, asunto, leido);
        add(nuevoEmail); 
    }
}

    public void inbox() {
        EmailNodo actual = primerEmail;
        int posicion = 1;
        int contador = 0;

        while (actual != null) {
            System.out.printf("%d - %s - %s - %s\n",
                posicion,
                actual.remitente,
                actual.asunto,
                actual.leido ? "LEÍDO" : "NO LEÍDO"
            );
            actual = actual.siguiente;
            posicion++;
            contador++;
        }

        System.out.printf("Total de emails: %d\n", contador);
    }

    public long gotEmail(String envia, String asunto, String contenido) throws IOException {
    emailFile.seek(emailFile.length()); 
    long posicion = emailFile.getFilePointer();

    emailFile.writeUTF(envia);        
    emailFile.writeUTF(asunto);      
    emailFile.writeBoolean(false);    
    emailFile.writeLong(new Date().getTime()); 
    emailFile.writeUTF(contenido);  

    return posicion; 
}
    
    public EmailNodo getPrimerEmail() {
        return primerEmail;
    }

    public void readEmail(int posicion) throws IOException {
        EmailNodo actual = primerEmail;
        int indice = 1;

        while (actual != null && indice < posicion) {
            actual = actual.siguiente;
            indice++;
        }

        if (actual == null) {
            throw new NoSuchElementException("No existe un email en la posición especificada.");
        }

        emailFile.seek(actual.posicion);
        String remitente = emailFile.readUTF();
        String asunto = emailFile.readUTF();
        boolean leido = emailFile.readBoolean();
        long fecha = emailFile.readLong();
        String contenido = emailFile.readUTF();

        System.out.printf("De: %s\nAsunto: %s\nContenido: %s\nFecha: %s\n",
            remitente, asunto, contenido, new Date(fecha).toString());

        if (!leido) {
            emailFile.seek(actual.posicion + remitente.length() + asunto.length() + 2 * 2 + 8);
            emailFile.writeBoolean(true);
            actual.leido = true;
        }
    }
}