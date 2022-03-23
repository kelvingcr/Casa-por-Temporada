package br.com.kelvingcr.casaportemporada.activity.autenticacao;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import br.com.kelvingcr.casaportemporada.FirebaseHelper;
import br.com.kelvingcr.casaportemporada.R;

public class RecuperarContaActivity extends AppCompatActivity {

    private TextView text_titulo;
    private EditText edit_email;
    private ProgressBar progressBar;
    private Button btn_recuperar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_conta);
        initComponents();
        configCliques();
        text_titulo.setText("Recuperar senha");
    }

    private void initComponents() {
        text_titulo = findViewById(R.id.text_titulo);
        ;
        edit_email = findViewById(R.id.edit_email);
        ;
        progressBar = findViewById(R.id.progressBar);
        ;
        btn_recuperar = findViewById(R.id.btn_recuperar);
        ;
    }

    private void configCliques() {
        findViewById(R.id.ib_voltar).setOnClickListener(view -> {
            finish();
        });

    }

    public void validaDados(View view) {

        String email = edit_email.getText().toString();

        if (!email.isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);
            recuperarSenha(email);
        } else {
            edit_email.requestFocus();
            edit_email.setError("Informe seu e-mail.");
        }

    }

    private void recuperarSenha(String email) {
        FirebaseHelper.getAuth().sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Toast.makeText(this, "E-mail enviado com sucesso!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }else{
                String erro = task.getException().getMessage();
                Toast.makeText(this, erro, Toast.LENGTH_SHORT).show();
            }
        });
    }

}