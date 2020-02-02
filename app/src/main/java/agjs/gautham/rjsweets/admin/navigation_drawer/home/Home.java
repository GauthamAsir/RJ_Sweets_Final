package agjs.gautham.rjsweets.admin.navigation_drawer.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import agjs.gautham.rjsweets.Common;
import agjs.gautham.rjsweets.Interface.ItemClickListener;
import agjs.gautham.rjsweets.Model.Sweet;
import agjs.gautham.rjsweets.Model.Token;
import agjs.gautham.rjsweets.R;
import agjs.gautham.rjsweets.SweetsDetail;
import agjs.gautham.rjsweets.admin.ViewHolder.MenuViewHolderAdmin;
import agjs.gautham.rjsweets.user.navigation_drawer.home_user.MenuViewHolder;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

import static android.app.Activity.RESULT_OK;

public class Home extends Fragment {

    private DatabaseReference sweets;

    private RecyclerView recycler_menu;

    private FirebaseRecyclerAdapter<Sweet, MenuViewHolderAdmin> adapter;

    FirebaseStorage storage;
    StorageReference storageReference;

    //Add New Menu Item
    TextInputLayout sweetName, sweetDescription, sweetPrice, sweetDiscount, sweetAvaQuantity;
    Button bt_select,bt_update;

    Uri saveUri;
    private final int PICK_IMAGE_REQUEST= 71;
    ImageView img;

    AlertDialog.Builder alertDialog;
    AlertDialog alert;

    Sweet newSweet;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.nav_home_admin, container, false);

        //Init Paper to Remember
        Paper.init(getActivity());

        //Init Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        sweets = database.getReference("Sweets");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        FloatingActionButton fab = root.findViewById(R.id.btn_add_sweet_admin);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        //Load menu
        recycler_menu = root.findViewById(R.id.recycle_menu_admin);
        recycler_menu.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recycler_menu.setLayoutManager(layoutManager);

        loadMenu();

        //Update Token
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(getActivity(), new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String tokenRefreshed = instanceIdResult.getToken();
                updateToken(tokenRefreshed);
            }
        });

        return root;
    }

    private void updateToken(String token) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("Tokens");
        Token data = new Token(token, true); //Sending From Server App So True
        tokens.child(Common.USER_Phone).setValue(data);
    }

    private void showDialog() {

        alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Add new Sweet");
        alertDialog.setMessage("Please Fill The Information's");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_new_menu_item = inflater.inflate(R.layout.add_new_menu_item, null);

        sweetName = add_new_menu_item.findViewById(R.id.edtName);
        sweetDescription = add_new_menu_item.findViewById(R.id.edtDescription);
        sweetDiscount = add_new_menu_item.findViewById(R.id.edtDiscount);
        sweetPrice = add_new_menu_item.findViewById(R.id.edtPrice);
        sweetAvaQuantity = add_new_menu_item.findViewById(R.id.edtAvaQuantity);

        alertDialog.setView(add_new_menu_item);
        alertDialog.setIcon(R.drawable.ic_sweet_add);

        img = add_new_menu_item.findViewById(R.id.imgs);

        bt_select = add_new_menu_item.findViewById(R.id.bt_select);
        bt_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        bt_update = add_new_menu_item.findViewById(R.id.bt_update);
        bt_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        alertDialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                uploadImage();
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alert = alertDialog.create();
        alert.show();

    }

    private void uploadImage() {
        if (saveUri != null){

            final AlertDialog dialog = new SpotsDialog.Builder()
                    .setContext(getActivity())
                    .setCancelable(false)
                    .setMessage("Uploading...")
                    .build();

            dialog.show();

            String imageName = UUID.randomUUID().toString();

            final StorageReference imageFolder = storageReference.child("sweets/"+imageName);

            final String name = sweetName.getEditText().getText().toString();
            final String description = sweetDescription.getEditText().getText().toString();
            final String discount = sweetDiscount.getEditText().getText().toString();
            final String price = sweetPrice.getEditText().getText().toString();
            final String avaQuantity = sweetAvaQuantity.getEditText().getText().toString();

            if (validateName(name) && validateDescription(description)
                    && validateDiscount(discount) && validatePrice(price) && validateDiscount(avaQuantity)) {

                imageFolder.putFile(saveUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                dialog.dismiss();
                                Toast.makeText(getActivity(), "Uploaded !", Toast.LENGTH_SHORT).show();
                                imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(final Uri uri) {

                                        sweets.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                int size = (int) dataSnapshot.getChildrenCount();
                                                Log.d("SIZE",String.valueOf(size));

                                                newSweet = new Sweet(description,
                                                        discount,
                                                        uri.toString(),
                                                        name,
                                                        price,
                                                        avaQuantity);

                                                String id = String.valueOf(size + Integer.parseInt("2"));

                                                sweets.child(id).push();
                                                sweets.child(id).setValue(newSweet);
                                                View view = getActivity().findViewById(R.id.fragment_container);
                                                alert.dismiss();
                                                dialog.dismiss();
                                                Snackbar.make(view,name+" Added Succesfully", Snackbar.LENGTH_SHORT).show();

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        dialog.setMessage("Uploading " + progress + "%");
                    }
                });
            } else {
                dialog.dismiss();
                Toast.makeText(getActivity(), "Update Failed, Fields were empty", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(getActivity(), "Update Failed, No Images Selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null ){

            saveUri = data.getData();
            Picasso.get().load(saveUri).into(img);
            bt_select.setText("Image Selected");
        }
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

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Sweet Image"),PICK_IMAGE_REQUEST);
    }

    private void loadMenu() {

        final AlertDialog dlg = new SpotsDialog.Builder()
                .setContext(getActivity())
                .setCancelable(false)
                .setMessage("Loading Sweets For You...")
                .build();

        dlg.show();
        adapter = new FirebaseRecyclerAdapter<Sweet, MenuViewHolderAdmin>
                (Sweet.class, R.layout.menu_item, MenuViewHolderAdmin.class, sweets) {
            @Override
            protected void populateViewHolder(MenuViewHolderAdmin menuViewHolder, final Sweet model, int i) {

                menuViewHolder.txtMenuName.setText(model.getName());
                Picasso.get().load(model.getImage())
                        .into(menuViewHolder.imageView);

                if (dlg.isShowing()){
                    dlg.dismiss();
                }

                if (model.getAvaQuantity().equals("0")){
                    menuViewHolder.txtAvailableQuantity.setVisibility(View.VISIBLE);
                }else {
                    menuViewHolder.txtAvailableQuantity.setVisibility(View.GONE);
                }

                menuViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        Intent sweetsDetail = new Intent(getActivity(), SweetsDetail.class);
                        sweetsDetail.putExtra("SweetId", adapter.getRef(position).getKey());
                        sweetsDetail.putExtra("AvailableQuantity",model.getAvaQuantity());
                        sweetsDetail.putExtra("AppType","admin");
                        startActivity(sweetsDetail);
                        getActivity().overridePendingTransition(R.anim.slide_up, R.anim.slide_down );

                    }
                });
            }
        };
        recycler_menu.setAdapter(adapter);
    }

    //  Update/Delete
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getTitle().equals(Common.UPDATE)){

            Intent intent = new Intent(getActivity(),UpdateSweets.class);
            intent.putExtra("Key",adapter.getRef(item.getOrder()).getKey());
            Log.d("KEY",adapter.getRef(item.getOrder()).getKey());
            intent.putExtra("Url",adapter.getItem(item.getOrder()).getImage());
            startActivity(intent);

        }else if (item.getTitle().equals(Common.DELETE)){
            showDeleteDialog(adapter.getRef(item.getOrder()).getKey());
        }

        return super.onContextItemSelected(item);
    }

    private void showDeleteDialog(final String key) {

        alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Delete Sweet");
        alertDialog.setMessage("Do You Really want to Delete ?");
        alertDialog.setIcon(R.drawable.ic_delete);

        alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                sweets.child(key).removeValue();
                Toast.makeText(getActivity(),"Item Deleted",Toast.LENGTH_SHORT).show();
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }
}