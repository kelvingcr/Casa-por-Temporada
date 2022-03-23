package br.com.kelvingcr.casaportemporada.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import br.com.kelvingcr.casaportemporada.FirebaseHelper;
import br.com.kelvingcr.casaportemporada.R;
import br.com.kelvingcr.casaportemporada.model.Anuncio;
import br.com.kelvingcr.casaportemporada.model.Usuario;

public class DetalheActivity extends AppCompatActivity {

    private EditText edit_quartos, edit_banheiros, edit_garagens;
    private TextView textTitulo, textDescricao, text_titulo;
    private ImageView img_anuncio;
    private Anuncio anuncio;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe);
        initComponents();
        text_titulo.setText("Detalhes do anúncio");

        Bundle bundle = getIntent().getExtras(); // verifica se esta vindo algo
        if (bundle != null) {
            anuncio = (Anuncio) bundle.getSerializable("anuncio");
            recuperaAnunciante();
            configDados();

        }


    }

    private void initComponents() {
        textTitulo = findViewById(R.id.textTitulo);
        textDescricao = findViewById(R.id.textDescricao);

        edit_quartos = findViewById(R.id.edit_quartos);
        edit_banheiros = findViewById(R.id.edit_banheiros);
        edit_garagens = findViewById(R.id.edit_garagens);
        img_anuncio = findViewById(R.id.img_anuncio);
        text_titulo = findViewById(R.id.text_titulo);

        findViewById(R.id.ib_voltar).setOnClickListener(view -> finish());
    }

    private void configDados() {
        Picasso.get().load(anuncio.getUrlImagem()).into(img_anuncio);

        textTitulo.setText(anuncio.getTitulo());
        textDescricao.setText(anuncio.getDescricao());

        edit_banheiros.setText(anuncio.getBanheiros());
        edit_garagens.setText(anuncio.getGaragens());
        edit_quartos.setText(anuncio.getQuartos());

    }

    private void recuperaAnunciante() {
        DatabaseReference reference = FirebaseHelper.getDatabaseReference()
                .child("usuarios")
                .child(anuncio.getIdUsuario());

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usuario = snapshot.getValue(Usuario.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void ligar(View view) {
        if (usuario != null) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + usuario.getTelefone()));
            startActivity(intent);
        } else {
            Toast.makeText(this, "Carregando informações, aguarde...", Toast.LENGTH_SHORT).show();
        }
    }

}