package com.example.repaso1examenfinal;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.repaso1examenfinal.entities.Pelicula;
import com.example.repaso1examenfinal.services.PeliculaService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.repaso1examenfinal.databinding.ActivityMapsBinding;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    private double latitud, longitud;

    private Pelicula pelicula;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Intent intent = getIntent();
        String peliculaJson = intent.getStringExtra("COORDENADAS_PELICULA");

        if(peliculaJson!=null){
            pelicula = new Gson().fromJson(peliculaJson, Pelicula.class);
        }
        if(peliculaJson == null) return;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://6352ca44a9f3f34c3749009a.mockapi.io/")// -> Aquí va la URL sin el Path
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PeliculaService service = retrofit.create(PeliculaService.class);
        service.getUbicaciones(pelicula.id).enqueue(new Callback<List<Pelicula.Ubicaciones>>() {
            @Override
            public void onResponse(Call<List<Pelicula.Ubicaciones>> call, Response<List<Pelicula.Ubicaciones>> response) {
                List<Pelicula.Ubicaciones> ubicaciones = response.body();
                Log.i("MAIN_APP", new Gson().toJson(ubicaciones));

                for(int i = 0;i<ubicaciones.size();i++){
                    LatLng peliculaUbicacion = new LatLng(ubicaciones.get(i).latitud, ubicaciones.get(i).longitud);
                    mMap.addMarker(new MarkerOptions().position(peliculaUbicacion).title(pelicula.titulo + " " + i));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(peliculaUbicacion));
                }
            }

            @Override
            public void onFailure(Call<List<Pelicula.Ubicaciones>> call, Throwable t) {
                Log.i("MAIN_APP", "Error al obtener ubiccaciones");
            }
        });

        // Add a marker in Sydney and move the camera
        /*LatLng peliculaUbicacion = new LatLng(latitud, longitud);
        mMap.addMarker(new MarkerOptions().position(peliculaUbicacion).title(pelicula.titulo));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(peliculaUbicacion));*/
    }
}