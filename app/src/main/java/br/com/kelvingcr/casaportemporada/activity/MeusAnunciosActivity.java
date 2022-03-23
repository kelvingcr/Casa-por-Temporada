package br.com.kelvingcr.casaportemporada.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.tsuryo.swipeablerv.SwipeLeftRightCallback;
import com.tsuryo.swipeablerv.SwipeableRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.kelvingcr.casaportemporada.FirebaseHelper;
import br.com.kelvingcr.casaportemporada.R;
import br.com.kelvingcr.casaportemporada.adapter.AdapterAnuncios;
import br.com.kelvingcr.casaportemporada.model.Anuncio;

public class MeusAnunciosActivity extends AppCompatActivity implements AdapterAnuncios.onClick {

    private List<Anuncio> anuncioList = new ArrayList<>();

    private ProgressBar progressBar;
    private TextView text_info, text_titulo;
    private SwipeableRecyclerView rv_anuncios;
    private AdapterAnuncios adapterAnuncios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_anuncios);
        initComponents();
        configRv();
        recuperarAnuncios();
        configCliques();
    }

    private void configRv() {
        rv_anuncios.setLayoutManager(new LinearLayoutManager(this));
        rv_anuncios.setHasFixedSize(true);
        adapterAnuncios = new AdapterAnuncios(anuncioList, this);
        rv_anuncios.setAdapter(adapterAnuncios);


        rv_anuncios.setListener(new SwipeLeftRightCallback.Listener() {
            @Override
            public void onSwipedLeft(int position) {

            }

            @Override
            public void onSwipedRight(int position) {
                showDialogDelete(position);
            }
        });
    }

    private void showDialogDelete(int pos){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete anúncio");
        builder.setMessage("Aperte em sim para confirmar ou não para cancelar.");
        builder.setCancelable(false);
        builder.setNegativeButton("Não", (dialog, which) -> {
            dialog.dismiss();
            adapterAnuncios.notifyDataSetChanged();
        });
        builder.setPositiveButton("Sim", (dialog, which) -> {

            Anuncio anuncio = anuncioList.get(pos);
            anuncio.deletarAnuncio();
            adapterAnuncios.notifyItemRemoved(pos);
            anuncioList.remove(pos);
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void recuperarAnuncios() {
        DatabaseReference reference = FirebaseHelper.getDatabaseReference()
                .child("anuncios")
                .child(FirebaseHelper.getIdAuthFirebase());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    anuncioList.clear();
                    for(DataSnapshot snap : snapshot.getChildren()){
                        Anuncio anuncio = snap.getValue(Anuncio.class);
                        anuncioList.add(anuncio);
                    }
                    text_info.setText("");
                } else {
                    text_info.setText("Nenhum anúncio cadastrado.");
                }
                progressBar.setVisibility(View.GONE);

                Collections.reverse(anuncioList);
                adapterAnuncios.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initComponents() {

        progressBar = findViewById(R.id.progressBar);
        text_titulo = findViewById(R.id.textTitulo);
        text_info = findViewById(R.id.text_info);
        rv_anuncios = findViewById(R.id.rv_anuncios);

        text_titulo.setText("Meus Anúncios");

    }

    private void configCliques(){
        findViewById(R.id.ib_add).setOnClickListener(view -> {
            startActivity(new Intent(this, FormAnuncioActivity.class));
        });

        findViewById(R.id.ib_voltar).setOnClickListener(view -> finish());
    }

    @Override
    public void OnClickListener(Anuncio anuncio) {
        Intent intent = new Intent(this, FormAnuncioActivity.class);
        intent.putExtra("anuncio", anuncio);
        startActivity(intent);
    }
}