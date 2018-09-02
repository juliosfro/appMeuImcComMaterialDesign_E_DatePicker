package br.edu.projetosandroid.meuimc.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;

import br.edu.projetosandroid.meuimc.R;

public class TelaPrincipalController extends AppCompatActivity {
    //criando objetos
    private CardView _cardCadastrarPessoa, _cardListarPessoas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle(R.string.title_telaPrincipal);
        setContentView(R.layout.activity_tela_principal);

        // Set up the login form.
        _cardCadastrarPessoa = (CardView) findViewById(R.id.card_CadastrarPessoa);
        _cardListarPessoas = (CardView) findViewById(R.id.card_listarPessoa);

        _cardCadastrarPessoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(Home.this, "Home.java", Toast.LENGTH_SHORT).show();
                Intent it = new Intent(TelaPrincipalController.this, TelaCadastrarPessoaController.class);
                startActivity(it);
            }
        });

        _cardListarPessoas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(TelaPrincipalController.this, TelaListarPessoaController.class);
                startActivity(it);
            }
        });
    }

}
