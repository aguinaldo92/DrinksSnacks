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


    static class ViewHolder {
        public TextView textViewNomeProdotto;
        TextView textViewNomeProduttore;
        public TextView textViewScaffale;
        public TextView textViewQuantita;
        public Button btnAcquista;
        public NetworkImageView imageProdottoView;
    }

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
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            imageLoader = AppSingleton.getInstance(context).getImageLoader();
            rowView = inflater.inflate(R.layout.row_activity_list_prodotti_distributore, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.textViewNomeProdotto = (TextView) rowView.findViewById(R.id.row_activity_text_nome_prodotto);
            viewHolder.textViewNomeProduttore = (TextView) rowView.findViewById(R.id.row_activity_text_nome_produttore);
            // TextView textViewPrezzo = (TextView) rowView.findViewById(R.id.row_activity_text_prezzo);
            viewHolder.textViewScaffale = (TextView) rowView.findViewById(R.id.row_activity_text_scaffale);
            //TextView textViewPosto = (TextView) rowView.findViewById(R.id.row_activity_text_posto);
            viewHolder.textViewQuantita = (TextView) rowView.findViewById(R.id.row_activity_text_quantita);
            viewHolder.btnAcquista = (Button) rowView.findViewById(R.id.row_activity_btn_acquista);
            viewHolder.imageProdottoView = (NetworkImageView) rowView.findViewById(R.id.row_activity_img_prodotto);
            rowView.setTag(R.id.holderTag,viewHolder);
        }
        ViewHolder holder = (ViewHolder) rowView.getTag(R.id.holderTag);
        holder.imageProdottoView.setImageUrl(baseUrlImage + prodottoDistributoreModels.get(position).getFoto64(), imageLoader);
        holder.textViewNomeProdotto.setText(prodottoDistributoreModels.get(position).getNome());
        holder.textViewNomeProduttore.setText(prodottoDistributoreModels.get(position).getProduttore());
        holder.btnAcquista.setText(String.valueOf(prodottoDistributoreModels.get(position).getPrezzo()));
        holder.textViewScaffale.setText("Scaf:" + prodottoDistributoreModels.get(position).getScaffale() + " Posto:" + prodottoDistributoreModels.get(position).getPosto());
        holder.textViewQuantita.setText("Q.tà: " + prodottoDistributoreModels.get(position).getQuantita());
        //textViewPosto.setText("Posto:"+prodottoDistributoreModels.get(position).getPosto());

        holder.btnAcquista.setTag(R.id.positionTag,String.valueOf(position));
        rowView.setTag(R.id.positionTag,String.valueOf(position));

        holder.btnAcquista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Integer position = Integer.parseInt(v.getTag(R.id.positionTag).toString());
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
                    Integer position = Integer.parseInt(v.getTag(R.id.positionTag).toString());
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