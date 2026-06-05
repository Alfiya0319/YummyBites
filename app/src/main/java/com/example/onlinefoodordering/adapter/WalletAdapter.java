package com.example.onlinefoodordering.adapter;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import com.example.onlinefoodordering.R;
import com.example.onlinefoodordering.model.TransactionModel;


public class WalletAdapter extends RecyclerView.Adapter<WalletAdapter.ViewHolder> {

    private List<TransactionModel> transactionList;

    public WalletAdapter(List<TransactionModel> transactionList) {
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wallet_transaction_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TransactionModel txn = transactionList.get(position);

        holder.title.setText(txn.getTitle());
        holder.date.setText(txn.getDate());
        holder.txnId.setText("ID: " + txn.getTxnId());

        if (txn.getType().equals("Credit")) {
            holder.amount.setText("+ ₹" + txn.getAmount());
            holder.amount.setTextColor(Color.parseColor("#4CAF50")); // Green
            holder.status.setTextColor(Color.parseColor("#4CAF50"));
            holder.iconContainer.setCardBackgroundColor(Color.parseColor("#F0F9F1"));
            // holder.icon.setImageResource(R.drawable.ic_arrow_downward); // Add your icon
        } else {
            holder.amount.setText("- ₹" + txn.getAmount());
            holder.amount.setTextColor(Color.RED);
            holder.status.setTextColor(Color.RED);
            holder.iconContainer.setCardBackgroundColor(Color.parseColor("#FEEBEB"));
            // holder.icon.setImageResource(R.drawable.ic_arrow_upward); // Add your icon
        }
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, date, amount, txnId, status;
        CardView iconContainer;
        ImageView icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.txn_title);
            date = itemView.findViewById(R.id.txn_date);
            amount = itemView.findViewById(R.id.txn_amount);
            txnId = itemView.findViewById(R.id.txn_id);
            status = itemView.findViewById(R.id.txn_status);
            iconContainer = itemView.findViewById(R.id.icon_container);
            icon = itemView.findViewById(R.id.txn_icon);
        }
    }
}