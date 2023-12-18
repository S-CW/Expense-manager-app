package com.example.moneymanager;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moneymanager.Model.Data;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashboardFragment extends Fragment {

//    Floating button
    private FloatingActionButton fab_main_btn;
    private FloatingActionButton fab_income_btn;
    private FloatingActionButton fab_expense_btn;

//    Floating button textview..
    private TextView fab_income_txt;
    private TextView fab_expense_txt;

//    boolean
    private boolean isOpen = false;

//    Animation
    private Animation fadeOpen, fadeClose;

//    Dashboard income and expense
    private TextView totalIncomResult;
    private TextView totalExpenseResult;

//    Firebase...
    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;
    private DatabaseReference mExpenseDatabase;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeDatabase").child(uid);
        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseDatabase").child(uid);

//        Connect floating button to layout
        fab_main_btn = myView.findViewById(R.id.fb_main_plus_btn);
        fab_income_btn = myView.findViewById(R.id.income_ft_btn);
        fab_expense_btn = myView.findViewById(R.id.expense_ft_btn);

//        Connect floating text
        fab_income_txt = myView.findViewById(R.id.income_ft_text);
        fab_expense_txt = myView.findViewById(R.id.expense_ft_text);

//        Total income and expense
        totalIncomResult = myView.findViewById(R.id.income_set_result);
        totalExpenseResult = myView.findViewById(R.id.expense_set_result);

//        Animation connect..
        fadeOpen = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_open);
        fadeClose = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_close);

        fab_main_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                addData();

                if (isOpen) {
                    fab_income_btn.startAnimation(fadeClose);
                    fab_expense_btn.startAnimation(fadeClose);
                    fab_income_btn.setClickable(false);
                    fab_expense_btn.setClickable(false);

                    fab_income_txt.startAnimation(fadeClose);
                    fab_expense_txt.startAnimation(fadeClose);
                    fab_income_txt.setClickable(false);
                    fab_expense_txt.setClickable(false);
                    isOpen = false;
                } else {
                    fab_income_btn.startAnimation(fadeOpen);
                    fab_expense_btn.startAnimation(fadeOpen);
                    fab_income_btn.setClickable(true);
                    fab_expense_btn.setClickable(true);

                    fab_income_txt.startAnimation(fadeOpen);
                    fab_expense_txt.startAnimation(fadeOpen);
                    fab_income_txt.setClickable(true);
                    fab_expense_txt.setClickable(true);
                    isOpen = true;
                }
            }
        });

//        Calculate total income..
        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalSum = 0;
                for (DataSnapshot mySnapshot:snapshot.getChildren()) {
                    Data data = mySnapshot.getValue(Data.class);

                    totalSum += data.getAmount();

                }

                String stResult = String.valueOf(totalSum);
                totalIncomResult.setText(stResult + ".00");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalSum = 0;
                for (DataSnapshot mySnapshot: snapshot.getChildren()) {
                    Data data = mySnapshot.getValue(Data.class);

                    totalSum += data.getAmount();
                }

                String stResult = String.valueOf(totalSum);
                totalExpenseResult.setText(stResult + ".00");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return myView;
    }

//    Floating button animation
    private void ftAnimation() {
        if (isOpen) {
            fab_income_btn.startAnimation(fadeClose);
            fab_expense_btn.startAnimation(fadeClose);
            fab_income_btn.setClickable(false);
            fab_expense_btn.setClickable(false);

            fab_income_txt.startAnimation(fadeClose);
            fab_expense_txt.startAnimation(fadeClose);
            fab_income_txt.setClickable(false);
            fab_expense_txt.setClickable(false);
            isOpen = false;
        } else {
            fab_income_btn.startAnimation(fadeOpen);
            fab_expense_btn.startAnimation(fadeOpen);
            fab_income_btn.setClickable(true);
            fab_expense_btn.setClickable(true);

            fab_income_txt.startAnimation(fadeOpen);
            fab_expense_txt.startAnimation(fadeOpen);
            fab_income_txt.setClickable(true);
            fab_expense_txt.setClickable(true);
            isOpen = true;
        }
    }

    private void addData() {
//        Fab Button income..
        fab_income_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ftAnimation();
                incomeDataInsert();
            }
        });

//        Fab Butoon expense..
        fab_expense_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ftAnimation();
                expenseDataInsert();
            }
        });
    }

    public void incomeDataInsert() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        View myView = inflater.inflate(R.layout.custom_layout_for_insertdata, null);

        myDialog.setView(myView);
        AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

        TextView headerTextView = myView.findViewById(R.id.custom_header);
        headerTextView.setText("INCOME");

        EditText editAmount = myView.findViewById(R.id.amount_edit);
        EditText editType = myView.findViewById(R.id.type_edit);
        EditText editNote = myView.findViewById(R.id.note_edit);

        Button btn_save = myView.findViewById(R.id.btn_save);
        Button btn_cancel = myView.findViewById(R.id.btn_cancel);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type = editType.getText().toString().trim();
                String amount = editAmount.getText().toString().trim();
                String note = editNote.getText().toString().trim();

                if (TextUtils.isEmpty(type)) {
                    editType.setError("Required Field...");
                    return;
                }

                if (TextUtils.isEmpty(amount)) {
                    editAmount.setError("Required Field...");
                    return;
                }

                int ourAmountInt = Integer.parseInt(amount);

                if (TextUtils.isEmpty(note)) {
                    editNote.setError("Required Field...");
                    return;
                }

//                Save data to firebasedatabase
                String id = mIncomeDatabase.push().getKey();
                String mDate = DateFormat.getDateInstance().format(new Date());
                Data data = new Data(ourAmountInt, type, note, id, mDate);

                mIncomeDatabase.child(id).setValue(data);

                Toast.makeText(getActivity(), "Income ADDED", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });

        dialog.show();

    }

    public void expenseDataInsert() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        View myView = inflater.inflate(R.layout.custom_layout_for_insertdata, null);

        myDialog.setView(myView);
        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

        TextView headerTextView = myView.findViewById(R.id.custom_header);
        headerTextView.setText("EXPENSE");

        EditText amount = myView.findViewById(R.id.amount_edit);
        EditText type = myView.findViewById(R.id.type_edit);
        EditText note = myView.findViewById(R.id.note_edit);

        Button btn_save = myView.findViewById(R.id.btn_save);
        Button btn_cancel = myView.findViewById(R.id.btn_cancel);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tmAmount = amount.getText().toString().trim();
                String tmType = type.getText().toString().trim();
                String tmNote = note.getText().toString().trim();

                if (TextUtils.isEmpty(tmAmount)) {
                    amount.setError("Required Field..");
                    return;
                }

                int intAmount = Integer.parseInt(tmAmount);

                if (TextUtils.isEmpty(tmType)) {
                    type.setError("Required Field..");
                    return;
                }

                if (TextUtils.isEmpty(tmNote)) {
                    note.setError("Required Field..");
                    return;
                }

                String id = mExpenseDatabase.push().getKey();
                String mDate = DateFormat.getDateInstance().format(new Date());
                Data data = new Data(intAmount, tmType, tmNote, id, mDate);

                mExpenseDatabase.child(id).setValue(data);

                Toast.makeText(getActivity(), "Expense ADDED", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}