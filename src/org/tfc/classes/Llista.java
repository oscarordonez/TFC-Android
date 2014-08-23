package org.tfc.classes;

public class Llista {

    private String llista_id;
    private String nom_llista;
    private String dia_llista;

    public Llista(String StrLlista_id, String StrNom_llista, String StrDia_llista){
        this.llista_id = StrLlista_id; // ACS_id
        this.nom_llista = StrNom_llista;
        this.dia_llista = StrDia_llista;
    }

    public String getLlista_id() {
        return llista_id;
    }

    public String getNom_llista() {
        return nom_llista;
    }
    public String getDia_llista() {
        return dia_llista;
    }
}
