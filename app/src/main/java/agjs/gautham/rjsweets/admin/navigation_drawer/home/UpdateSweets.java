package agjs.gautham.rjsweets.admin.navigation_drawer.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import agjs.gautham.rjsweets.Model.Sweet;
import agjs.gautham.rjsweets.R;

public class UpdateSweets extends AppCompatActivity {

    TextInputLayout sweetName, sweetDescription, sweetPrice, sweetDiscount, sweetAvaQuantity;
    Button bt_select, bt_update;

    Uri saveUri;
    private final int PICK_IMAGE_REQUEST= 71;

    private DatabaseReference sweets;

    String key, image_url;
    ImageView imgs;

    Sweet item;

    Sweet newSweet;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_menu_item);

        //Init Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        sweets = database.getReference("Sweets");

        sweetName = findViewById(R.id.edtName);
        sweetDescription = findViewById(R.id.edtDescription);
        sweetDiscount = findViewById(R.id.edtDiscount);
        sweetPrice = findViewById(R.id.edtPrice);
        sweetAvaQuantity = findViewById(R.id.edtAvaQuantity);

        imgs = findViewById(R.id.imgs);

        bt_select = findViewById(R.id.bt_select);

        if (getIntent() != null) {
            key = getIntent().getStringExtra("Key");
            image_url = getIntent().getStringExtra("Url");
            Picasso.get().load(image_url).into(imgs);

            sweets.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    item = dataSnapshot.child(key).getValue(Sweet.class);

                    sweetName.getEditText().setText(item.getName());
                    sweetDescription.getEditText().setText(item.getDescription());
                    sweetDiscount.getEditText().setText(item.getDiscount());
                    sweetPrice.getEditText().setText(item.getPrice());
                    sweetAvaQuantity.getEditText().setText(item.getAvaQuantity());

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        bt_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        bt_update = findViewById(R.id.bt_update);
        bt_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String name = sweetName.getEditText().getText().toString();
                final String description = sweetDescription.getEditText().getText().toString();
                final String discount = sweetDiscount.getEditText().getText().toString();
                final String price = sweetPrice.getEditText().getText().toString();
                final String avaQuantity = sweetAvaQuantity.getEditText().getText().toString();

                if (validateName(name) && validateDescription(description)
                        && validateDiscount(discount) && validatePrice(price) && validateDiscount(avaQuantity)) {

                    newSweet = new Sweet(description,
                            discount,
                            image_url,
                            name,
                            price,
                            avaQuantity);

                    sweets.child(key).setValue(newSweet);

                    Toast.makeText(UpdateSweets.this,"Updated Succesfully",Toast.LENGTH_LONG).show();
                    finish();

                }

            }

        });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Sweet Image"),PICK_IMAGE_REQUEST);
    }

    boolean validateName(String name){
        if (name.isEmpty()){
            sweetName.setError("Field can't be Empty !");
            return false;
        }else {
            sweetName.setError(null);
            sweetName.clearFocus();
            return true;
        }
    }

    boolean validateDescription(String description){
        if (description.isEmpty()){
            sweetDescription.setError("Field can't be Empty !");
            return false;
        }else {
            sweetDescription.setError(null);
            sweetDescription.clearFocus();
            return true;
        }
    }

    boolean validateDiscount(String discount){
        if (discount.isEmpty()){
            sweetDiscount.setError("Field can't be Empty !");
            return false;
        }else {
            sweetDiscount.setError(null);
            sweetDiscount.clearFocus();
            return true;
        }
    }

    boolean validatePrice(String price){
        if (price.isEmpty()){
            sweetPrice.setError("Field can't be Empty !");
            return false;
        }else {
            sweetPrice.setError(null);
            sweetPrice.clearFocus();
            return true;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null ){

            saveUri = data.getData();
            bt_select.setText("Image Selected");
        }
    }

}
