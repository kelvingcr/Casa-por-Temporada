package br.com.kelvingcr.casaportemporada.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;

import br.com.kelvingcr.casaportemporada.FirebaseHelper;

public class Anuncio implements Serializable {

    private String id;
    private String idUsuario;
    private String titulo;
    private String descricao;
    private String quarto;
    private String banheiro;
    private String garagen;
    private boolean status;
    private String urlImagem;

    public Anuncio() {
        DatabaseReference reference = FirebaseHelper.getDatabaseReference();
        this.setId(reference.push().getKey());
    }

    public void salvarAnuncio() {
        DatabaseReference reference = FirebaseHelper.getDatabaseReference()
                .child("anuncios")
                .child(FirebaseHelper.getIdAuthFirebase())
                .child(this.getId());

        reference.setValue(this);

        DatabaseReference anuncioPublico = FirebaseHelper.getDatabaseReference()
                .child("anuncios_publicos")
                .child(this.getId());

        anuncioPublico.setValue(this);

    }


    public void deletarAnuncio() {
        DatabaseReference reference = FirebaseHelper.getDatabaseReference()
                .child("anuncios")
                .child(FirebaseHelper.getIdAuthFirebase())
                .child(this.getId());

        reference.removeValue().addOnCompleteListener(task -> { // verifica se os dados foram deletados com sucesso antes de deletar a imagem
            if (task.isSuccessful()) {

                StorageReference storageReference = FirebaseHelper.getStorageReference()
                        .child("imagens")
                        .child("anuncios")
                        .child(this.getId() + ".jpeg");
                storageReference.delete();

                DatabaseReference anuncios_publicos = FirebaseHelper.getDatabaseReference()
                        .child("anuncios_publicos")
                        .child(this.getId());

                anuncios_publicos.removeValue();
            }
        });

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getQuartos() {
        return quarto;
    }

    public void setQuartos(String quartos) {
        this.quarto = quartos;
    }

    public String getBanheiros() {
        return banheiro;
    }

    public void setBanheiros(String banheiros) {
        this.banheiro = banheiros;
    }

    public String getGaragens() {
        return garagen;
    }

    public void setGaragens(String garagens) {
        this.garagen = garagens;
    }

    public boolean isStatus() {
        return status;
    }

    public String getUrlImagem() {
        return urlImagem;
    }

    public void setUrlImagem(String urlImagem) {
        this.urlImagem = urlImagem;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
