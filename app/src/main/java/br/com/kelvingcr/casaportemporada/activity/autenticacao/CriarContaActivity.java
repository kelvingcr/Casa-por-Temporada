package br.com.kelvingcr.casaportemporada.activity.autenticacao;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import br.com.kelvingcr.casaportemporada.FirebaseHelper;
import br.com.kelvingcr.casaportemporada.R;
import br.com.kelvingcr.casaportemporada.activity.MainActivity;
import br.com.kelvingcr.casaportemporada.model.Usuario;

public class CriarContaActivity extends AppCompatActivity {

    private TextView text_titulo;
    private EditText edit_nome, edit_email, edit_telefone, edit_senha;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_conta);
        initComponents();
        configCliques();
    }

    private void initComponents() {
        text_titulo = findViewById(R.id.textTitulo);

        edit_nome = findViewById(R.id.edit_nome);

        edit_email = findViewById(R.id.edit_email);

        edit_telefone = findViewById(R.id.edit_telefone);

        edit_senha = findViewById(R.id.edit_senha);

        progressBar = findViewById(R.id.progressBar);

    }


    public void configCliques() {

        ImageButton imageButton = findViewById(R.id.ib_voltar);
        imageButton.setOnClickListener(view -> finish());
    }

    public void validaCadrastrar(View view) {
        String nome = edit_nome.getText().toString();
        String email = edit_email.getText().toString();
        String telefone = edit_telefone.getText().toString();
        String senha = edit_senha.getText().toString();

        if (!nome.isEmpty()) {
            if (!email.isEmpty()) {
                if (!telefone.isEmpty()) {
                    if (!senha.isEmpty()) {

                        progressBar.setVisibility(View.VISIBLE);

                        Usuario usuario = new Usuario();
                        usuario.setNome(nome);
                        usuario.setEmail(email);
                        usuario.setTelefone(telefone);
                        usuario.setSenha(senha);

                        cadastrarUsuario(usuario);

                    } else {
                        edit_nome.requestFocus();
                        edit_nome.setError("Infome sua senha");
                    }
                } else {
                    edit_nome.requestFocus();
                    edit_nome.setError("Infome seu telefone");
                }

            } else {
                edit_nome.requestFocus();
                edit_nome.setError("Infome seu e-mail");
            }

        } else {
            edit_nome.requestFocus();
            edit_nome.setError("Infome seu nome");
        }


    }

    private void cadastrarUsuario(Usuario usuario){
        FirebaseHelper.getAuth().createUserWithEmailAndPassword(usuario.getEmail(), usuario.getSenha())
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){

                        String idUser = task.getResult().getUser().getUid();
                        usuario.setId(idUser);
                        usuario.salvar();
                        finish();
                        startActivity(new Intent(this, MainActivity.class));
                    }else{
                        String error = task.getException().getMessage();
                        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                    }
                });

    }
}