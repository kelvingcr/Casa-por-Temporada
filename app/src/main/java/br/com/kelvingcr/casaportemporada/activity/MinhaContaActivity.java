package br.com.kelvingcr.casaportemporada.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import br.com.kelvingcr.casaportemporada.FirebaseHelper;
import br.com.kelvingcr.casaportemporada.R;
import br.com.kelvingcr.casaportemporada.model.Usuario;

public class MinhaContaActivity extends AppCompatActivity {

    private EditText edit_nome, edit_telefone, edit_email;
    private ProgressBar progressBar;

    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minha_conta);
        initComponents();
        recuperaDados();

    }

    private void initComponents() {
        edit_nome = findViewById(R.id.edit_nome);
        edit_telefone = findViewById(R.id.edit_telefone);
        edit_email = findViewById(R.id.edit_email);
        progressBar = findViewById(R.id.progressBar);

        TextView textView = findViewById(R.id.textTitulo);
        textView.setText("Meus dados");

        findViewById(R.id.ib_salvar).setOnClickListener(view -> {
            validaDados();
        });
        findViewById(R.id.ib_voltar).setOnClickListener(view -> {
            finish();
        });
    }

    private void recuperaDados(){
        DatabaseReference reference = FirebaseHelper.getDatabaseReference()
                .child("usuarios")
                .child(FirebaseHelper.getIdAuthFirebase());

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usuario = snapshot.getValue(Usuario.class);
                configDados();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void configDados() {
        edit_nome.setText(usuario.getNome());
        edit_telefone.setText(usuario.getTelefone());
        edit_email.setText(usuario.getEmail());

        progressBar.setVisibility(View.GONE);
    }


    public void validaDados() {
        String nome = edit_nome.getText().toString();
        String telefone = edit_telefone.getText().toString();


        if (!nome.isEmpty()) {
            if (!telefone.isEmpty()) {

                usuario.setNome(nome);
                usuario.setTelefone(telefone);

                usuario.salvar();

                Toast.makeText(this, "Dados atualizados com sucesso!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);

            } else {
                edit_telefone.requestFocus();
                edit_telefone.setError("Informe seu telefone.");
            }
        } else {
            edit_nome.requestFocus();
            edit_nome.setError("Informe seu nome.");
        }
    }

    public void deslogar(View view) {
        FirebaseHelper.getAuth().signOut();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}