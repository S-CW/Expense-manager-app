package com.example.moneymanager;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.moneymanager.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link IncomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class  IncomeFragment extends Fragment {

//    Firebase database
    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;

//    Recycler view
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter adapter;

//    Textview
    private TextView incomeTotalSum;

//    Update edit text
    private EditText editAmount;
    private EditText editCategory;
    private EditText editNote;

//    Button for update and delete
    private Button btnUpdate;
    private Button btnDelete;

//    Data item value
    private String category;
    private String note;
    private float amount;
    private String post_key;
    private static DecimalFormat df = new DecimalFormat("0.00");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.fragment_income, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String userEmail = mUser.getEmail().replace(".", ",");

        mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeDatabase").child(userEmail);
        incomeTotalSum = myView.findViewById(R.id.income_txt_result);
        recyclerView = myView.findViewById(R.id.recycler_id_income);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                float totalAmount = 0;

                for (DataSnapshot mySnapshot: snapshot.getChildren()) {
                    Data data = mySnapshot.getValue(Data.class);
                    totalAmount += data.getAmount();
                }

                    String strTotalAmount = df.format(totalAmount);
                    incomeTotalSum.setText("$ " + strTotalAmount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return myView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>().setQuery(mIncomeDatabase, Data.class).build();

        adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Data model) {
                holder.setAmount(model.getAmount());
                holder.setCategory(model.getCategory());
                holder.setNote(model.getNote());
                holder.setDate(model.getDate());

//                Popup dialog for update and delete
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        post_key = getRef(position).getKey();

                        category = model.getCategory();
                        note = model.getNote();
                        amount = model.getAmount();

                        updateDataItem(model.getDate());
                    }
                });
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.income_recycler_data, parent, false));
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setCategory(String category) {
            TextView mCategory = mView.findViewById(R.id.category_txt_income);
            mCategory.setText(category);
        }

        public void setNote(String note) {
            TextView mNote = mView.findViewById(R.id.note_txt_income);
            mNote.setText(note);
        }

        private void setDate(String date) {
            TextView mDate = mView.findViewById(R.id.date_txt_income);
            mDate.setText(date);
        }

        private void setAmount(float amount) {
            TextView mAmount = mView.findViewById(R.id.amount_txt_income);
            String strAmount = df.format(amount);

            mAmount.setText("$ " + strAmount);
        }
    }

    private void updateDataItem(String header) {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        View myView = inflater.inflate(R.layout.update_data_item, null);
        myDialog.setView(myView);

        TextView headerTextView = myView.findViewById(R.id.custom_header);
        headerTextView.setText(header);

        editAmount = myView.findViewById(R.id.amount_edit);
        editCategory = myView.findViewById(R.id.category_edit);
        editNote = myView.findViewById(R.id.note_edit);

//         Set data to edit text..
        editCategory.setText(category);
        editCategory.setSelection(category.length());

        editNote.setText(note);
        editNote.setSelection(note.length());

        editAmount.setText(String.valueOf(amount));
        editAmount.setSelection(String.valueOf(amount).length());

        btnUpdate = myView.findViewById(R.id.btn_update);
        btnDelete = myView.findViewById(R.id.btn_delete);

        AlertDialog dialog = myDialog.create();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = editCategory.getText().toString().trim();
                note = editNote.getText().toString().trim();

                String strAmount = editAmount.getText().toString().trim();

                float myAmount = Float.parseFloat(strAmount);
                String mDate = DateFormat.getDateInstance().format(new Date());
                Data data = new Data(myAmount, category, note, post_key, mDate);

                mIncomeDatabase.child(post_key).setValue(data);

                dialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIncomeDatabase.child(post_key).removeValue();
                dialog.dismiss();
            }
        });

        dialog.show();

    }
}



