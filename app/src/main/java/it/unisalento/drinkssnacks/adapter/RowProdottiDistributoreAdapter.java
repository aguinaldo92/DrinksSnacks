package it.unisalento.drinkssnacks.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import it.unisalento.drinkssnacks.R;
import it.unisalento.drinkssnacks.activity.AcquistaActivity;
import it.unisalento.drinkssnacks.activity.DettagliProdottoActivity;
import it.unisalento.drinkssnacks.model.ProdottoDistributoreModel;
import it.unisalento.drinkssnacks.singleton.AppSingleton;


/**
 * Created by aguinaldo on 28/04/2017.
 */

public class RowProdottiDistributoreAdapter extends ArrayAdapter {
    public static final String EXTRA_PRODOTTODISTRIBUTOREMODELS = RowProdottiDistributoreAdapter.class.getCanonicalName() + "_prodottoDistributoreModels";
    public static final String EXTRA_IDDISTRIBUTORE = RowProdottiDistributoreAdapter.class.getCanonicalName() + "_idDistributore";
    private static final String TAG = RowProdottiDistributoreAdapter.class.getSimpleName();
    private final Context context;
    private final String baseUrlImage = "http://distributori.ddns.net:8080/distributori/";
    private List<ProdottoDistributoreModel> prodottoDistributoreModels;
    private ImageLoader imageLoader;
    private int idDistributore;

    public RowProdottiDistributoreAdapter( Context context, @LayoutRes int resource, List<ProdottoDistributoreModel> prodottoDistributoreModels, int idDistributore) {
        super(context, resource, prodottoDistributoreModels);
        this.context = context;
        this.prodottoDistributoreModels = prodottoDistributoreModels;
        this.idDistributore = idDistributore;
    }

    @Override
    public int getCount() {
        return prodottoDistributoreModels.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = AppSingleton.getInstance(context).getImageLoader();
        View rowView = inflater.inflate(R.layout.row_activity_list_prodotti_distributore, parent, false);
        TextView textViewNomeProdotto = (TextView) rowView.findViewById(R.id.row_activity_text_nome_prodotto);
        TextView textViewNomeProduttore = (TextView) rowView.findViewById(R.id.row_activity_text_nome_produttore);
        // TextView textViewPrezzo = (TextView) rowView.findViewById(R.id.row_activity_text_prezzo);
        TextView textViewScaffale = (TextView) rowView.findViewById(R.id.row_activity_text_scaffale);
        //TextView textViewPosto = (TextView) rowView.findViewById(R.id.row_activity_text_posto);
        TextView textViewQuantita = (TextView) rowView.findViewById(R.id.row_activity_text_quantita);
        Button btnAcquista = (Button) rowView.findViewById(R.id.row_activity_btn_acquista);
        NetworkImageView imageProdottoView = (NetworkImageView) rowView.findViewById(R.id.row_activity_img_prodotto);

        imageProdottoView.setImageUrl(baseUrlImage + prodottoDistributoreModels.get(position).getFoto64(), imageLoader);
        textViewNomeProdotto.setText(prodottoDistributoreModels.get(position).getNome());
        textViewNomeProduttore.setText(prodottoDistributoreModels.get(position).getProduttore());
        btnAcquista.setText(String.valueOf(prodottoDistributoreModels.get(position).getPrezzo()));
        textViewScaffale.setText("Scaf:" + prodottoDistributoreModels.get(position).getScaffale() + " Posto:" + prodottoDistributoreModels.get(position).getPosto());
        textViewQuantita.setText("Q.tà: " + prodottoDistributoreModels.get(position).getQuantita());
        //textViewPosto.setText("Posto:"+prodottoDistributoreModels.get(position).getPosto());

        btnAcquista.setTag(String.valueOf(position));
        rowView.setTag(String.valueOf(position));

        btnAcquista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Integer position = Integer.parseInt(v.getTag().toString());
                    Intent intent = new Intent(context, AcquistaActivity.class);
                    intent.putExtra(EXTRA_PRODOTTODISTRIBUTOREMODELS, prodottoDistributoreModels.get(position));
                    intent.putExtra(EXTRA_IDDISTRIBUTORE, idDistributore);
                    context.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Integer position = Integer.parseInt(v.getTag().toString());
                    Intent intent = new Intent(context, DettagliProdottoActivity.class);
                    intent.putExtra(EXTRA_PRODOTTODISTRIBUTOREMODELS, prodottoDistributoreModels.get(position));
                    intent.putExtra(EXTRA_IDDISTRIBUTORE, idDistributore);
                    context.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return rowView;
    }

}