package org.tfc.classes;

public class Llista {

    private String llista_id;
    private String nom_llista;
    private String lloc_llista;

    public Llista(String StrLlista_id, String StrNom_llista, String StrLloc_llista){
        this.llista_id = StrLlista_id; // ACS_id
        this.nom_llista = StrNom_llista;
        this.lloc_llista = StrLloc_llista;
    }

    public String getLlista_id() {
        return llista_id;
    }

    public String getNom_llista() {
        return nom_llista;
    }
    public String getLloc_llista() {
        return lloc_llista;
    }
}
