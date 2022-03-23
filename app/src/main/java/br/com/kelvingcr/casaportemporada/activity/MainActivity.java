package br.com.kelvingcr.casaportemporada.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.tsuryo.swipeablerv.SwipeLeftRightCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.kelvingcr.casaportemporada.FirebaseHelper;
import br.com.kelvingcr.casaportemporada.R;
import br.com.kelvingcr.casaportemporada.activity.autenticacao.LoginActivity;
import br.com.kelvingcr.casaportemporada.adapter.AdapterAnuncios;
import br.com.kelvingcr.casaportemporada.model.Anuncio;
import br.com.kelvingcr.casaportemporada.model.Filtro;

public class MainActivity extends AppCompatActivity implements AdapterAnuncios.onClick {

    private ImageButton ib_menu;

    private final int REQUEST_FILTRO = 100;

    private List<Anuncio> anuncioList = new ArrayList<>();
    private AdapterAnuncios adapterAnuncios;
    private RecyclerView rv_anuncios;

    private TextView text_info;
    private ProgressBar progressBar;

    private Filtro filtro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();

        configRv();
        configCliques();

        recuperaAnuncios();
    }


    @Override
    protected void onStart() {
        super.onStart();
        // startActivity(new Intent(this, FormAnuncioActivity.class));
    }

    private void configCliques() {

        ib_menu.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(this, ib_menu);
            popupMenu.getMenuInflater().inflate(R.menu.menu_home, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getItemId() == R.id.menu_filtrar) {
                    Intent intent = new Intent(this, FiltrarAnunciosActivity.class);
                    intent.putExtra("filtro", filtro);
                    startActivityForResult(intent, REQUEST_FILTRO);

                } else if (menuItem.getItemId() == R.id.menu_meus_anuncios) {
                    if (FirebaseHelper.getAutenticado()) {
                        startActivity(new Intent(this, MeusAnunciosActivity.class));
                    } else {
                        showDialogLogin();
                    }

                } else if (menuItem.getItemId() == R.id.menu_minha_conta) {
                    if (FirebaseHelper.getAutenticado()) {
                        startActivity(new Intent(this, MinhaContaActivity.class));
                    } else {
                        showDialogLogin();
                    }
                }
                return true;
            });
            popupMenu.show();
        });

    }

    private void configRv() {
        rv_anuncios.setLayoutManager(new LinearLayoutManager(this));
        rv_anuncios.setHasFixedSize(true);
        adapterAnuncios = new AdapterAnuncios(anuncioList, this);
        rv_anuncios.setAdapter(adapterAnuncios);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_FILTRO) {
                filtro = (Filtro) data.getSerializableExtra("filtro");
                if (filtro.getQtdQuarto() > 0 || filtro.getQtdBanheiro() > 0 || filtro.getQtdGaragem() > 0) {
                    //Recupera os anuncios com base nos filtros
                    recuperaAnunciosFiltro();
                }
            } else {
                recuperaAnuncios();
            }
        }
    }

    private void recuperaAnunciosFiltro() {

        DatabaseReference reference = FirebaseHelper.getDatabaseReference()
                .child("anuncios_publicos");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    anuncioList.clear();
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        Anuncio anuncio = snap.getValue(Anuncio.class);

                        int quarto = Integer.parseInt(anuncio.getQuartos());
                        int banheiro = Integer.parseInt(anuncio.getBanheiros());
                        int garagem = Integer.parseInt(anuncio.getGaragens());

                        if (quarto >= filtro.getQtdQuarto() &&
                                banheiro >= filtro.getQtdBanheiro() &&
                                garagem >= filtro.getQtdGaragem()) {
                            anuncioList.add(anuncio);
                        }

                    }
                }

                if (anuncioList.size() == 0) {
                    text_info.setText("Nenhum anuncio encontrado.");
                } else {
                    text_info.setText("");
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

    private void recuperaAnuncios() {
        DatabaseReference reference = FirebaseHelper.getDatabaseReference()
                .child("anuncios_publicos");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                anuncioList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        Anuncio anuncio = snap.getValue(Anuncio.class);
                        if (anuncio.isStatus()) {
                            anuncioList.add(anuncio);
                        }
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

    private void showDialogLogin() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Autenticação");
        builder.setMessage("Voce não está autenticado no app, deseja autenticar agora?");
        builder.setCancelable(false);
        builder.setNegativeButton("Não", (dialog, which) -> dialog.dismiss());
        builder.setPositiveButton("Sim", (dialog, which) -> {
            startActivity(new Intent(this, LoginActivity.class));
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    /*
    private void showDialogKey(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Licença");
        builder.setMessage("Voce não foi encontrado nenhuma licença, deseja autenticar agora?");
        builder.setCancelable(false);
        builder.setNegativeButton("Não", (dialog, which) -> {
            dialog.dismiss();
            finish();
            System.exit(0);
        });
        builder.setPositiveButton("Sim", (dialog, which) -> {
            Toast.makeText(this, "Licenciado", Toast.LENGTH_SHORT).show();
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    } */


    private void initComponents() {
        ib_menu = findViewById(R.id.ib_menu);
        rv_anuncios = findViewById(R.id.rv_anuncios);
        text_info = findViewById(R.id.text_info);
        progressBar = findViewById(R.id.progressBar);
    }

    @Override
    public void OnClickListener(Anuncio anuncio) {
        Intent intent = new Intent(this, DetalheActivity.class);
        intent.putExtra("anuncio", anuncio);
        startActivity(intent);
    }
}