package com.example.onlinefoodordering.Fragment;
import android.app.Dialog;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.text.SimpleDateFormat;
import java.util.*;
import com.example.onlinefoodordering.R;
import com.example.onlinefoodordering.adapter.WalletAdapter;
import com.example.onlinefoodordering.model.TransactionModel;

public class WalletFragment extends Fragment {

    private TextView walletBalance;
    private RecyclerView recyclerView;
    private WalletAdapter adapter;
    private List<TransactionModel> transactionList;
    private DatabaseReference dbRef;
    private String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);

        walletBalance = view.findViewById(R.id.wallet_balance);
        recyclerView = view.findViewById(R.id.wallet_recycler_view);
        Button btnAddMoney = view.findViewById(R.id.btn_add_money);

        userId = FirebaseAuth.getInstance().getUid();
        // WalletFragment.java ke andar
        dbRef = FirebaseDatabase.getInstance("https://onlinefoodorderingapp-7c2c0-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        transactionList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new WalletAdapter(transactionList);
        recyclerView.setAdapter(adapter);

        fetchWalletData();
        fetchTransactions();

        btnAddMoney.setOnClickListener(v -> showAddMoneyDialog());

        return view;
    }

    private void fetchWalletData() {
        dbRef.child("Users").child(userId).child("walletBalance")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            walletBalance.setText("₹" + snapshot.getValue().toString());
                        } else {
                            walletBalance.setText("₹0.00");
                        }
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void fetchTransactions() {
        dbRef.child("WalletTransactions").child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        transactionList.clear();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            TransactionModel model = data.getValue(TransactionModel.class);
                            transactionList.add(0, model); // Newest on top
                        }
                        adapter.notifyDataSetChanged();
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void showAddMoneyDialog() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_add_money);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        EditText etAmount = dialog.findViewById(R.id.et_amount);
        Button btnProceed = dialog.findViewById(R.id.btn_proceed);

        // Chip logic
        dialog.findViewById(R.id.chip_100).setOnClickListener(v -> etAmount.setText("100"));
        dialog.findViewById(R.id.chip_500).setOnClickListener(v -> etAmount.setText("500"));
        dialog.findViewById(R.id.chip_1000).setOnClickListener(v -> etAmount.setText("1000"));

        btnProceed.setOnClickListener(v -> {
            String amount = etAmount.getText().toString();
            if (!amount.isEmpty()) {
                updateBalanceInFirebase(amount);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void updateBalanceInFirebase(String amount) {
        // 1. Update Wallet Balance
        dbRef.child("Users").child(userId).child("walletBalance")
                .runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                        double currentVal = 0;
                        if (currentData.getValue() != null) {
                            currentVal = Double.parseDouble(currentData.getValue().toString());
                        }
                        currentData.setValue(currentVal + Double.parseDouble(amount));
                        return Transaction.success(currentData);
                    }
                    @Override public void onComplete(DatabaseError e, boolean b, DataSnapshot s) {}
                });

        // 2. Add Transaction Entry
        String txnId = dbRef.push().getKey();
        String date = new SimpleDateFormat("dd MMM, yyyy • hh:mm a", Locale.getDefault()).format(new Date());
        TransactionModel txn = new TransactionModel(txnId, "Added to Wallet", date, amount, "Credit");
        dbRef.child("WalletTransactions").child(userId).child(txnId).setValue(txn);
    }
}