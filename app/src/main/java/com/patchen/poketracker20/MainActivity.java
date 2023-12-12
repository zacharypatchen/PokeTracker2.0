package com.patchen.poketracker20;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    ImageView image;
    private static final String BASE_URL = "https://pokeapi.co/api/v2/";
    List<Pokemon> watchlist = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up item click listener for the watchlist
        ListView watchlistListView = findViewById(R.id.wtachlistListView);
        watchlistListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Retrieve the selected Pokemon object from the list
                Pokemon selectedPokemon = watchlist.get(position);
                updateUI(selectedPokemon);
            }
        });
    }
    private boolean checkName(String name){
        return !name.matches(".*[%&*()@!;:<>].*");
    }
    public void search(View view) {
        EditText searchEditText = findViewById(R.id.searchET);
        String pokemonName = searchEditText.getText().toString();
        if(checkName(pokemonName)) {

            // Make a network request using Retrofit
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            PokemonService pokemonService = retrofit.create(PokemonService.class);
            Call<Pokemon> call = pokemonService.getPokemon(pokemonName.toLowerCase());

            call.enqueue(new Callback<Pokemon>() {
                @Override
                public void onResponse(Call<Pokemon> call, Response<Pokemon> response) {
                    if (response.isSuccessful()) {
                        Pokemon pokemon = response.body();
                        updateUI(pokemon);
                        addToWatchlist(pokemon);
                    }
                }

                @Override
                public void onFailure(Call<Pokemon> call, Throwable t) {
                    errorMessage();
                }
            });
        }else{
            errorMessage();
        }
    }
    private void errorMessage(){
    Toast.makeText(this, "Error: Pokemon not found", Toast.LENGTH_SHORT).show();
    }
    private void updateUI(Pokemon pokemon) {
        TextView nationalNumberTextView = findViewById(R.id.nationalNumberTextView);
        TextView weightTextView = findViewById(R.id.weightTextView);
        TextView heightTextView = findViewById(R.id.heightTextView);
        TextView xpTextView = findViewById(R.id.baseXpTextView);
        TextView nameTextView = findViewById(R.id.nameTextView);
         image = findViewById(R.id.pokemonImageView);
        nationalNumberTextView.setText("Number: " + pokemon.getId());
        weightTextView.setText("Weight: "+ pokemon.getWeight());
        heightTextView.setText("Height: "+ pokemon.getHeight());
        xpTextView.setText("Base XP: "+ pokemon.getBaseExperience());
        nameTextView.setText("Name: "+ pokemon.getName());
        if (pokemon.getImageURL() != null) {
            Picasso.get().load(pokemon.getImageURL()).into(image);
        }
    }

    private void addToWatchlist(Pokemon pokemon) {
        ListView watchlistListView = findViewById(R.id.wtachlistListView);
        if (watchlistListView.getAdapter() == null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
            watchlistListView.setAdapter(adapter);
        }
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) watchlistListView.getAdapter();
        adapter.add(pokemon.getName() + " " + "ID: " + pokemon.getId());

        // Add the Pokemon object to the watchlist
        watchlist.add(pokemon);
    }

}
