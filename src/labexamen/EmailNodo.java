    /*
     * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
     * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
     */
    package labexamen;

    /**
     *
     * @author moiza
     */

    public class EmailNodo {
        public Long posicion;   
        public String remitente;   
        public String asunto;        
        public Boolean leido;       
        public EmailNodo siguiente;  


        public EmailNodo(Long posicion, String remitente, String asunto, Boolean leido) {
            this.posicion = posicion;
            this.remitente = remitente;
            this.asunto = asunto;
            this.leido = leido;
            this.siguiente = null;  
        }

    }
