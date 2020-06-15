package com.example.dochat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.security.Key;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;
import static com.google.firebase.storage.FirebaseStorage.getInstance;


public class ProfileFragment extends Fragment {

    //Firebase
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    //storage
    StorageReference storageReference;
   String storagePath = "PersonalMedico_Perfil_Portada_Imgs/";

    //vistas from xml
    ImageView avatarTv,Coverphoto;
    TextView nameprofile,especialidadprofile,emailprofile;
    FloatingActionButton floatingActionButton;

    ProgressDialog pd;

    //permisos
    private static final int CAMERA_REQUEST_CODE =100;
    private static final int STORAGE_REQUEST_CODE =200;
    private static final int IMAGE_PICK_GALLERY_CODE =300;
    private static final int IMAGE_PICK_CAMERA_CODE =400;

    String cameraPermisos[];
    String storagePermisos[];

    //uri de la imagen
    Uri image_uri;
    // chequiar perfil y foto de portada
    String profileOrCoverPhoto;



    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view=  inflater.inflate(R.layout.fragment_profile, container, false);

        //inicializar firebase
        firebaseAuth = FirebaseAuth.getInstance();
        user  =  firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("PersonalMedico");
        storageReference = getInstance().getReference();//firebase storage referencia

        //inicializar permisos
        cameraPermisos = new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermisos = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        //iniciar vistas
        avatarTv =view.findViewById(R.id.pic_profile);
        Coverphoto =view.findViewById(R.id.cover);
        nameprofile= view.findViewById(R.id.showusername);
        especialidadprofile=view.findViewById(R.id.showespecialidad);
        emailprofile=view.findViewById(R.id.showemail);
        floatingActionButton= view.findViewById(R.id.btedit);

        //iniciar mensaje
        pd = new ProgressDialog(getActivity());

        //aqui obtendremos al informacion del usuario logeado
        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    // get data
                    String name = ""+ ds.child("name").getValue();
                    String apellido = ""+ ds.child("apellido").getValue();
                    String edad = ""+ ds.child("edad").getValue();
                    String image = ""+ ds.child("image").getValue();
                    String rut = ""+ ds.child("rut").getValue();
                    String especialidad = ""+ ds.child("Especialidad").getValue();
                    String email = ""+ ds.child("email").getValue();
                    String cover = ""+ ds.child("cover").getValue();

                    //set data

                    nameprofile.setText(name);
                    especialidadprofile.setText(especialidad);
                    emailprofile.setText(email);

                    try {

                        Picasso.get().load(image).into(avatarTv);
                    }
                    catch (Exception e){

                        Picasso.get().load(R.drawable.ic_profile).into(avatarTv);
                    }

                    try {

                        Picasso.get().load(cover).into(Coverphoto);
                    }
                    catch (Exception e){


                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //boton edit
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditProfileDialog();
            }
        });








       return view;
    }
    private  boolean checkStoragePermiso(){
        //chequear si los permisos del storage estan habilitado o no
        //retornar verdadero si es enabled
        //retornar falso si es not enabled
        boolean result = ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result;
    }
    private  void requestStoragePermisos(){
        requestPermissions(storagePermisos,STORAGE_REQUEST_CODE);
    }


    private  boolean checkCamaraPermiso(){
        //chequear si los permisos del storage estan habilitado o no
        //retornar verdadero si es enabled
        //retornar falso si es not enabled
        boolean result = ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.CAMERA)==(PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }
    private  void requestCamaraPermisos(){
       requestPermissions(cameraPermisos,CAMERA_REQUEST_CODE);
    }


    private void showEditProfileDialog() {
        //opciones de edicion de perfil ,editar foto,editar nombre,editar especialdiad
        String options []={"Editar foto de perfil","Editar Foto de portada","Editar Nombre","Editar Especialidad"};
        //mensaje de alerta
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //editar titulo
        builder.setTitle("Elegir Accion");
        //set items to dialog
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if (which== 0){
                    //Editar Perfil Click
                    pd.setMessage("Actualizando Foto De Perfil....");
                    profileOrCoverPhoto = "image";
                    showImagePicDialog();
                }
                else if(which==1){
                    //editar portada
                    pd.setMessage("Actualizando Foto De Portada....");
                    profileOrCoverPhoto="cover";
                    showImagePicDialog();


                }
                else if(which==2){
                    //editar nombre
                    pd.setMessage("Actualizando Nombre....");
                    showNameUpadateDialog("name");

                }
                else if(which==3){
                    //editar especialidad
                    pd.setMessage("Actualizando Especialidad....");
                    showNameUpadateDialog("Especialidad");

                }
            }
        });
        //crear y mostrar el mensaje de aletar
        builder.create().show();
    }

    private void showNameUpadateDialog(final String key) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Actualizado "+key);//actualiza el nombre o la especialidad
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10,10,10,10);
        //AGREGAR EDITAR TEXTO
        final EditText editText = new EditText(getActivity());
        editText.setHint("Enter "+key);
        linearLayout.addView(editText);
        builder.setView(linearLayout);
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                //input text desde el editar texto
                String value = editText.getText().toString().trim();
                if (!TextUtils.isEmpty(value)){
                    pd.show();
                    HashMap<String,Object> result = new HashMap<>();
                    result.put(key,value);
                    databaseReference.child(user.getUid()).updateChildren(result).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pd.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(getActivity(),"Actualizando..."+e.getMessage(),Toast.LENGTH_SHORT).show();

                        }
                    });
                }
                else {
                    Toast.makeText(getActivity(),"Por favor entre"+ key,Toast.LENGTH_SHORT).show();

                }
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {

            }
        });
        builder.create().show();
    }

    private void showImagePicDialog() {
       //opcion de subir foto de galeria o subir foto de la camara
        String options []={"Camara","Galeria"};
        //mensaje de alerta
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //editar titulo
        builder.setTitle("Seleccionar Imagen De");
        //set items to dialog
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if (which== 0){
                    //Camara
                    if(!checkCamaraPermiso()){
                        requestCamaraPermisos();
                    }else {
                        pickFromCamera();
                    }


                }
                else if(which==1){
                    //Galeria
                    if (!checkStoragePermiso()){
                        requestStoragePermisos();
                    }else {
                        pickFromGallery();
                    }


                }

            }
        });
        //crear y mostrar el mensaje de aletar
        builder.create().show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean cameraaceptada = grantResults[0]== PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageaceptada = grantResults[1]== PackageManager.PERMISSION_GRANTED;
                    if (cameraaceptada&&writeStorageaceptada){
                        //permisos habilitado
                        pickFromCamera();
                    }
                    else {
                        //permisos no habilitados
                        Toast.makeText(getActivity(),"Por favor habilite los permisos de camara",Toast.LENGTH_SHORT);
                    }
                }
            }
            break;
            case  STORAGE_REQUEST_CODE:{
                if (grantResults.length>0){

                    boolean writeStorageaceptada = grantResults[1]== PackageManager.PERMISSION_GRANTED;
                    if (writeStorageaceptada){
                        //permisos habilitado
                        pickFromGallery();
                    }
                    else {
                        //permisos no habilitados
                        Toast.makeText(getActivity(),"Por favor habilite los permisos de storage",Toast.LENGTH_SHORT);
                    }
                }
            }
            break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode==RESULT_OK){
            if (requestCode==IMAGE_PICK_GALLERY_CODE){
                image_uri = data.getData();
                uploadProfileCoverPhoto(image_uri);
            }
            if (requestCode== IMAGE_PICK_CAMERA_CODE){
                uploadProfileCoverPhoto(image_uri);
            }
        }
    }

    private void uploadProfileCoverPhoto(final Uri uri) {
        pd.show();

        String filePathandname=storagePath+""+profileOrCoverPhoto+"_"+user.getUid();
        StorageReference storageReference1 = storageReference.child(filePathandname);
        storageReference1.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //image es subida al storage , y ahora captura la url del usuario en la base de datos
                Task<Uri> uriTask= taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                Uri downloadUri= uriTask.getResult();

                //chequear si la imagen fue subida o no
                if (uriTask.isSuccessful()){
                    //imagen subida
                    //agregar y subir url del usuario databse
                    HashMap<String,Object> results = new HashMap<>();
                    results.put(profileOrCoverPhoto,downloadUri.toString());
                    databaseReference.child(user.getUid()).updateChildren(results).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pd.dismiss();
                            Toast.makeText(getActivity(),"Imagen Subida...",Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(getActivity(),"Error al subir imagen...",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    //error
                    pd.dismiss();
                    Toast.makeText(getActivity(),"Ocurrio un error",Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void pickFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION,"temp Description");
        //put image uri
        image_uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        //intent to start camera
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(cameraIntent,IMAGE_PICK_CAMERA_CODE);
    }

    private void pickFromGallery() {
        //pick from gallery
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,IMAGE_PICK_GALLERY_CODE);
    }
}