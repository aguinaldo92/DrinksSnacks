package it.unisalento.drinkssnacks.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import it.unisalento.drinkssnacks.R;
import it.unisalento.drinkssnacks.adapter.RowProdottiDistributoreAdapter;
import it.unisalento.drinkssnacks.model.ProdottoDistributoreModel;

public class AcquistaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acquista);
        Intent intent = getIntent();
        ProdottoDistributoreModel prodottoDistributoreModel = intent.getParcelableExtra(RowProdottiDistributoreAdapter.EXTRA_MESSAGE);
        TextView textViewNomeProdotto = (TextView) findViewById(R.id.acquista_activity_text_nome_prodotto);
        textViewNomeProdotto.setText(prodottoDistributoreModel.getNome());
    }
}
