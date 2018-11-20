package com.example.usuario.capturarvideo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    Button capturar;
    VideoView videoViewCapturar;
    String rutaVideoActual;
    static final int VENGO_DE_LA_CAMARA_CON_FICHERO = 2;
    static final int PEDI_PERMISOS_DE_ESCRITURA = 2;
    MediaController mediaController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        capturar=findViewById(R.id.buttonCapturar);
        videoViewCapturar=findViewById(R.id.videoView);
        videoViewCapturar.setVisibility(View.VISIBLE);
        capturar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pedirPermisoParaEscribirYHacerFoto();


            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
       if ((requestCode == VENGO_DE_LA_CAMARA_CON_FICHERO) && (resultCode == RESULT_OK)) {
            //He hecho la foto y la he guardado, así que la foto estará en  rutaFotoActual
           //videoViewCapturar.setVideoURI(data.getData());
           //videoViewCapturar.start();

           mediaController=new MediaController(this);
           mediaController.setAnchorView(videoViewCapturar);
           videoViewCapturar.setMediaController(mediaController);


           videoViewCapturar.setVideoURI(Uri.parse(rutaVideoActual));
           videoViewCapturar.start();

        }
    }

    public void capturarVideo() {


        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        File ficheroVideo = null;
        try {
            ficheroVideo = crearFicheroVideo();
        } catch (IOException e) {
            e.printStackTrace();
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(ficheroVideo));
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,5);
        if (intent.resolveActivity(getPackageManager()) != null) {

            startActivityForResult(intent, VENGO_DE_LA_CAMARA_CON_FICHERO);
        } else {
            Toast.makeText(this, getResources().getString(R.string.mensaje), Toast.LENGTH_SHORT).show();
        }
    }

    File crearFicheroVideo() throws IOException {
        String fechaYHora = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String nombreFichero = "Ejemplo_" + fechaYHora;
        File carpetaParaVideo = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
        File video = File.createTempFile(nombreFichero, ".mp4", carpetaParaVideo);
        rutaVideoActual = video.getAbsolutePath();
        return video;
    }


    void pedirPermisoParaEscribirYHacerFoto() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Aquí puedo explicar para qué quiero el permiso

            } else {

                // No explicamos nada y pedimos el permiso

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PEDI_PERMISOS_DE_ESCRITURA);

                // El resultado de la petición se recupera en onRequestPermissionsResult
            }
        } else {//Tengo los permisos
            capturarVideo();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PEDI_PERMISOS_DE_ESCRITURA: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Tengo los permisos: hago la foto:

                    this.capturarVideo();

                } else {

                    //No tengo permisos: Le digo que no se puede hacer nada
                    Toast.makeText(this, getResources().getString(R.string.mensaje1), Toast.LENGTH_SHORT).show();
                }
                return;
            }

            //Pondría aquí más "case" si tuviera que pedir más permisos.
        }
    }
}

