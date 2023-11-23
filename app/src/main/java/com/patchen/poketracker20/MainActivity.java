package com.patchen.poketracker20;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    // Define the base URL for the PokeAPI
    private static final String BASE_URL = "https://pokeapi.co/api/v2/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void search(View view) {
        EditText searchEditText = findViewById(R.id.searchET);
        String pokemonName = searchEditText.getText().toString();

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
                // Handle failure, e.g., show an error message
            }
        });
    }

    private void updateUI(Pokemon pokemon) {
        // Update the TextViews and ImageView with Pokemon data
        // Example:
        TextView nationalNumberTextView = findViewById(R.id.nationalNumberTextView);
        nationalNumberTextView.setText("Number: " + pokemon.getId());

        // Repeat for other TextViews and ImageView
    }

    private void addToWatchlist(Pokemon pokemon) {
        // Add the Pokemon to the watchlist (ListView)
        // Example:
        ListView watchlistListView = findViewById(R.id.wtachlistListView);
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) watchlistListView.getAdapter();
        adapter.add(pokemon.getName());
    }
}
