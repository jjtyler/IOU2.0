package edu.tusculum.jtyler.iou20;
/**
 * IOU2.0 Created by User on 5/3/2015 .
 */
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;


public class CreateOrEditActivity extends ActionBarActivity implements View.OnClickListener {


    private ExampleDBHelper dbHelper ;
    EditText nameEditText;
    EditText reasonEditText;
    EditText amountEditText;

    Button saveButton;
    LinearLayout buttonLayout;
    Button editButton, deleteButton;

    int personID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        personID = getIntent().getIntExtra(Main.KEY_EXTRA_CONTACT_ID, 0);

        setContentView(R.layout.activity_edit);
        nameEditText = (EditText) findViewById(R.id.editTextName);
        reasonEditText = (EditText) findViewById(R.id.editTextReason);
        amountEditText = (EditText) findViewById(R.id.editTextAmount);

        saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(this);
        buttonLayout = (LinearLayout) findViewById(R.id.buttonLayout);
        editButton = (Button) findViewById(R.id.editButton);
        editButton.setOnClickListener(this);
        deleteButton = (Button) findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(this);

        dbHelper = new ExampleDBHelper(this);

        if(personID > 0) {
            saveButton.setVisibility(View.GONE);
            buttonLayout.setVisibility(View.VISIBLE);

            Cursor rs = dbHelper.getPerson(personID);
            rs.moveToFirst();
            String personName = rs.getString(rs.getColumnIndex(ExampleDBHelper.PERSON_COLUMN_NAME));
            String personReason = rs.getString(rs.getColumnIndex(ExampleDBHelper.PERSON_COLUMN_REASON));
            int personAmount = rs.getInt(rs.getColumnIndex(ExampleDBHelper.PERSON_COLUMN_AMOUNT));
            if (!rs.isClosed()) {
                rs.close();
            }

            nameEditText.setText(personName);
            nameEditText.setFocusable(false);
            nameEditText.setClickable(false);

            //noinspection RedundantCast
            reasonEditText.setText((CharSequence) personReason);
            reasonEditText.setFocusable(false);
            reasonEditText.setClickable(false);

            //noinspection RedundantCast
            amountEditText.setText((CharSequence) (personAmount + ""));
            amountEditText.setFocusable(false);
            amountEditText.setClickable(false);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.saveButton:
                persistPerson();
                return;
            case R.id.editButton:
                saveButton.setVisibility(View.VISIBLE);
                buttonLayout.setVisibility(View.GONE);
                nameEditText.setEnabled(true);
                nameEditText.setFocusableInTouchMode(true);
                nameEditText.setClickable(true);

                reasonEditText.setEnabled(true);
                reasonEditText.setFocusableInTouchMode(true);
                reasonEditText.setClickable(true);

                amountEditText.setEnabled(true);
                amountEditText.setFocusableInTouchMode(true);
                amountEditText.setClickable(true);
                return;
            case R.id.deleteButton:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.deletePerson)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dbHelper.deletePerson(personID);
                                Toast.makeText(getApplicationContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), Main.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                AlertDialog d = builder.create();
                d.setTitle("Delete Person?");
                d.show();
                //noinspection UnnecessaryReturnStatement
                return;
        }
    }

    public void persistPerson() {
        if(personID > 0) {
            if(dbHelper.updatePerson(personID, nameEditText.getText().toString(),
                    reasonEditText.getText().toString(),
                    Integer.parseInt(amountEditText.getText().toString()))) {
                Toast.makeText(getApplicationContext(), "Person Update Successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), Main.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            else {
                Toast.makeText(getApplicationContext(), "Person Update Failed", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            if(dbHelper.insertPerson(nameEditText.getText().toString(),
                    reasonEditText.getText().toString(),
                    Integer.parseInt(amountEditText.getText().toString()))) {
                Toast.makeText(getApplicationContext(), "Person Inserted", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getApplicationContext(), "Could not Insert person", Toast.LENGTH_SHORT).show();
            }
            Intent intent = new Intent(getApplicationContext(), Main.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }
}
