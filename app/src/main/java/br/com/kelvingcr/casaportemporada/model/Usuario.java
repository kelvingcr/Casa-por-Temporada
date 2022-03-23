package br.com.kelvingcr.casaportemporada.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import br.com.kelvingcr.casaportemporada.FirebaseHelper;

public class Usuario {

    private String id;
    private String nome;
    private String email;
    private String telefone;
    private String senha;

  /*  public Usuario() {
        DatabaseReference reference = FirebaseHelper.getDatabaseReference();
        this.setId(reference.push().getKey()); // gera um novo id para cada instancia
    }


    NÃO SERÁ USADO ESSE METODO POIS QUANDO CRIAMOS UMA USER NO FIREBASE ELE AUTOMATICAMENTE GERA UM ID (Metodo: cadastrarUsuario();) class: CriarConta
    */

    public void salvar(){
        DatabaseReference reference = FirebaseHelper.getDatabaseReference()
                .child("usuarios")
                .child(this.getId());

        reference.setValue(this);

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
