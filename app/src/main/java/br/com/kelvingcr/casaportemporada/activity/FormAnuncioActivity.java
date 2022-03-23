package br.com.kelvingcr.casaportemporada.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

import br.com.kelvingcr.casaportemporada.FirebaseHelper;
import br.com.kelvingcr.casaportemporada.R;
import br.com.kelvingcr.casaportemporada.model.Anuncio;

public class FormAnuncioActivity extends AppCompatActivity {

    private static final int REQUEST_GALERIA = 100;

    private EditText edit_titulo, edit_descricao, edit_quartos, edit_banheiros, edit_garagens;
    private CheckBox cb_disponivel;
    private ImageView img_produto;
    private String caminhoImagem;
    private Bitmap imagem;
    private Anuncio anuncio;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_anuncio);
        initComponents();
        configCliques();

        Bundle bundle = getIntent().getExtras(); // verifica se esta vindo algo
        if (bundle != null) {
            anuncio = (Anuncio) bundle.getSerializable("anuncio");
            configDados();
        }
    }

    private void configDados() {
        Picasso.get().load(anuncio.getUrlImagem()).into(img_produto);
        edit_titulo.setText(anuncio.getTitulo());
        edit_descricao.setText(anuncio.getDescricao());
        edit_banheiros.setText(anuncio.getBanheiros());
        edit_garagens.setText(anuncio.getGaragens());
        edit_quartos.setText(anuncio.getQuartos());
        cb_disponivel.setChecked(anuncio.isStatus());
    }


    private void initComponents() {
        edit_titulo = findViewById(R.id.edit_titulo);
        edit_descricao = findViewById(R.id.edit_descricao);
        edit_quartos = findViewById(R.id.edit_quartos);
        edit_banheiros = findViewById(R.id.edit_banheiros);
        edit_garagens = findViewById(R.id.edit_garagens);
        cb_disponivel = findViewById(R.id.cb_disponivel);
        img_produto = findViewById(R.id.img_produto);
        progressBar = findViewById(R.id.progressBar);

        findViewById(R.id.ib_salvar).setOnClickListener(view -> {
            salvarAnuncio();
        });


    }

    public void verificaPermissacaoGaleria(View view) {
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                abrirGaleria();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(FormAnuncioActivity.this, "Permissão Negada.", Toast.LENGTH_SHORT).show();
            }
        };

        showDialogPermissao(permissionListener, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE});
    }

    private void showDialogPermissao(PermissionListener permissionListener, String[] permissoes) {
        TedPermission.create().setPermissionListener(permissionListener)
                .setDeniedTitle("Permissões")
                .setDeniedMessage("Voce negou a permissão para acessar a galeria do aplicativo, deseja permitir?")
                .setDeniedCloseButtonText("Não")
                .setGotoSettingButtonText("Sim")
                .setPermissions(permissoes).check();
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_GALERIA);
    }

    public void salvarAnuncio() {
        String titulo = edit_titulo.getText().toString();
        String descricao = edit_descricao.getText().toString();
        String quartos = edit_quartos.getText().toString();
        String banheiros = edit_banheiros.getText().toString();
        String garagens = edit_garagens.getText().toString();


        if (!titulo.isEmpty()) {
            if (!descricao.isEmpty()) {
                if (!quartos.isEmpty()) {
                    if (!banheiros.isEmpty()) {
                        if (!garagens.isEmpty()) {

                            if (anuncio == null) anuncio = new Anuncio();

                            anuncio.setIdUsuario(FirebaseHelper.getIdAuthFirebase());
                            anuncio.setTitulo(titulo);
                            anuncio.setDescricao(descricao);
                            anuncio.setQuartos(quartos);
                            anuncio.setBanheiros(banheiros);
                            anuncio.setGaragens(garagens);
                            anuncio.setStatus(cb_disponivel.isChecked());

                            if (caminhoImagem != null) { //
                                salvarImagemAnuncio();
                            } else {
                                if (anuncio.getUrlImagem() != null) { // se já tiver uma imagem é um updade, ele salva o anuncio sem precisar salvar a imagem de novo
                                    anuncio.salvarAnuncio();
                                    progressBar.setVisibility(View.VISIBLE);
                                    finish();
                                } else {
                                    Toast.makeText(this, "Selecione uma imagem para o anuncio", Toast.LENGTH_SHORT).show();
                                }
                            }

                        } else {
                            edit_garagens.requestFocus();
                            edit_garagens.setError("Informe a quantidade de garagens");
                        }
                    } else {
                        edit_banheiros.requestFocus();
                        edit_banheiros.setError("Informe a quantidade de banheiros");
                    }
                } else {
                    edit_quartos.requestFocus();
                    edit_quartos.setError("Informe a quantidade de quartos");
                }

            } else {
                edit_descricao.requestFocus();
                edit_descricao.setError("Informe uma desrição");
            }

        } else {
            edit_titulo.requestFocus();
            edit_titulo.setError("Informe um titulo");
        }
    }

    private void salvarImagemAnuncio() {

        progressBar.setVisibility(View.VISIBLE);

        StorageReference storageReference = FirebaseHelper.getStorageReference()
                .child("imagens")
                .child("anuncios")
                .child(anuncio.getId() + ".jpeg");

        UploadTask uploadTask = storageReference.putFile(Uri.parse(caminhoImagem));
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            storageReference.getDownloadUrl().addOnCompleteListener(task -> {

                String imgImagem = task.getResult().toString(); //Retorna a url da imagem salva no firebase
                anuncio.setUrlImagem(imgImagem);

                anuncio.salvarAnuncio();

                finish();

            }).addOnFailureListener(e -> {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            });
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_GALERIA) {

                Uri localImagemSeleciona = data.getData();
                caminhoImagem = localImagemSeleciona.toString();

                if (Build.VERSION.SDK_INT < 28) {
                    try {
                        imagem = MediaStore.Images.Media.getBitmap(getBaseContext().getContentResolver(), localImagemSeleciona);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    ImageDecoder.Source source = ImageDecoder.createSource(getBaseContext().getContentResolver(), localImagemSeleciona);
                    try {
                        imagem = ImageDecoder.decodeBitmap(source);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                img_produto.setImageBitmap(imagem);

            }
        }
    }

    private void configCliques() {
        findViewById(R.id.ib_voltar).setOnClickListener(view -> {
            finish();
        });
    }

}