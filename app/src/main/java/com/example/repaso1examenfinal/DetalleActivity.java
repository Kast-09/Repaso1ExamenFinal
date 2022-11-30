package com.example.repaso1examenfinal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.repaso1examenfinal.entities.Image;
import com.example.repaso1examenfinal.entities.ImageResponse;
import com.example.repaso1examenfinal.entities.Pelicula;
import com.example.repaso1examenfinal.services.ImagenService;
import com.example.repaso1examenfinal.services.PeliculaService;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetalleActivity extends AppCompatActivity {

    EditText etEditTitulo, etEditSinopsis;
    ImageView ivEditFoto;
    TextView tvEditLatitud, tvEditLongitud;

    Pelicula pelicula;

    String encoded, link;

    Double latitud, longitud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);

        etEditTitulo = findViewById(R.id.etEditTitulo);
        etEditSinopsis = findViewById(R.id.etEditSinopsis);
        ivEditFoto = findViewById(R.id.ivEditFoto);
        tvEditLatitud = findViewById(R.id.tvEditLatitud);
        tvEditLongitud = findViewById(R.id.tvEditLongitud);

        Intent intent = getIntent();
        String peliculaJson = intent.getStringExtra("PELICULA_DATA");
        Log.i("MAIN_APP", new Gson().toJson(peliculaJson));

        if(peliculaJson!=null){
            pelicula = new Gson().fromJson(peliculaJson, Pelicula.class);
            etEditTitulo.setText(pelicula.titulo);
            etEditSinopsis.setText(pelicula.sinopsis);
            Picasso.get().load(pelicula.imagen).into(ivEditFoto);
            //tvEditLatitud.setText("Latidud: "+pelicula.latitud);
            //tvEditLongitud.setText("Longitud: "+pelicula.longitud);
        }
        if(peliculaJson == null) return;
    }

    public void editFoto(View view){
        if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            abrirCamara();
        }
        else{
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
        }
    }

    public void abrirCamara(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000 && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ivEditFoto.setImageBitmap(imageBitmap);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();

            encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

            Log.i("MAIN_APP", encoded);
            obtenerLink();
        }
    }

    public void obtenerLink(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.imgur.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Image image = new Image();
        image.image = encoded;

        ImagenService service = retrofit.create(ImagenService.class);
        service.create(image).enqueue(new Callback<ImageResponse>() {
            @Override
            public void onResponse(Call<ImageResponse> call, Response<ImageResponse> response) {
                Log.i("MAIN_APP", String.valueOf(response.code()));
                ImageResponse data = response.body();
                link = data.data.link;
                Log.i("MAIN_APP", new Gson().toJson(data));
                Log.i("MAIN_APP", link);
            }
            @Override
            public void onFailure(Call<ImageResponse> call, Throwable t) {
                Log.i("MAIN_APP", "NO SE OBTUVO ENLACE");
            }
        });
    }

    public void editarUbicacion(View view){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1001);
        }
        else {
            locationStart();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1001){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                locationStart();
                return;
            }
        }
    }

    private void locationStart() {
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        DetalleActivity.Localizacion Local = new DetalleActivity.Localizacion();
        Local.setMainActivity(this);
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) Local);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) Local);
    }

    /* Aqui empieza la Clase Localizacion */
    public class Localizacion implements LocationListener {

        DetalleActivity detalleActivity;
        public DetalleActivity getMainActivity() {
            return detalleActivity;
        }

        public void setMainActivity(DetalleActivity mainActivity) {
            this.detalleActivity = mainActivity;
        }

        @Override
        public void onLocationChanged(Location loc) {
            // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
            // debido a la deteccion de un cambio de ubicacion
            loc.getLatitude();
            loc.getLongitude();
            latitud = loc.getLatitude();
            longitud = loc.getLongitude();
            tvEditLatitud.setText("Latitud: " + latitud);
            tvEditLongitud.setText("Longitud: " + longitud);
        }

        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado
            Toast.makeText(getApplicationContext(), "GPS Desactivado", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es activado
            Toast.makeText(getApplicationContext(), "GPS Activado", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.d("debug", "LocationProvider.AVAILABLE");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                    break;
            }
        }
    }

    public void verUbicacion(View view){
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("COORDENADAS_PELICULA", new Gson().toJson(pelicula));
        startActivity(intent);
    }

    public void editarPelicula(View view){
        Pelicula peliculaEdit = new Pelicula();
        if(etEditTitulo.getText().toString() != null
            && etEditSinopsis.getText().toString() != null){
            peliculaEdit.titulo = etEditTitulo.getText().toString();
            peliculaEdit.sinopsis = etEditSinopsis.getText().toString();
            peliculaEdit.imagen = link;
            //peliculaEdit.latitud = latitud;
            //peliculaEdit.longitud = longitud;

            Pelicula.Ubicaciones ubicaciones = new Pelicula.Ubicaciones();
            ubicaciones.peliculaId = peliculaEdit.id;
            ubicaciones.latitud = latitud;
            ubicaciones.longitud = longitud;

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://6352ca44a9f3f34c3749009a.mockapi.io/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            PeliculaService service = retrofit.create(PeliculaService.class);
            service.update(pelicula.id, peliculaEdit).enqueue(new Callback<Pelicula>() {
                @Override
                public void onResponse(Call<Pelicula> call, Response<Pelicula> response) {

                    Log.i("MAIN_APP", String.valueOf(response.code()));
                    Toast.makeText(getApplicationContext(), "Se editó correctamente", Toast.LENGTH_SHORT).show();

                    service.createUbicacion(pelicula.id, ubicaciones).enqueue(new Callback<Pelicula.Ubicaciones>() {
                        @Override
                        public void onResponse(Call<Pelicula.Ubicaciones> call, Response<Pelicula.Ubicaciones> response) {
                            Log.i("MAIN_APP", String.valueOf(response.code()));
                            Log.i("MAIN_APP", "Se guardo nueva ubicación");
                        }

                        @Override
                        public void onFailure(Call<Pelicula.Ubicaciones> call, Throwable t) {
                            Log.i("MAIN_APP", "No se guardo nueva ubicación");
                        }
                    });

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onFailure(Call<Pelicula> call, Throwable t) {
                    Log.i("MAIN_APP", "No se editó");
                    Toast.makeText(getApplicationContext(), "No se editó", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
            Toast.makeText(getApplicationContext(), "No se puede editar datos vacíos", Toast.LENGTH_SHORT).show();
        }
    }

    public void eliminarPelicula(View view){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://6352ca44a9f3f34c3749009a.mockapi.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PeliculaService service = retrofit.create(PeliculaService.class);
        service.delete(pelicula.id).enqueue(new Callback<Pelicula>() {
            @Override
            public void onResponse(Call<Pelicula> call, Response<Pelicula> response) {
                Log.i("MAIN_APP", String.valueOf(response.code()));
                Toast.makeText(getApplicationContext(), "Se elimino correctamente", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<Pelicula> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "No se elimino", Toast.LENGTH_SHORT).show();
            }
        });
    }
}