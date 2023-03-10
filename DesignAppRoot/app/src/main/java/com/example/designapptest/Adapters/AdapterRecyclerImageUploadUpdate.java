package com.example.designapptest.Adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.designapptest.Controller.Interfaces.INotifyChangeImage;
import com.example.designapptest.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterRecyclerImageUploadUpdate extends RecyclerView.Adapter<AdapterRecyclerImageUploadUpdate.ViewHolder> {
    Context context;
    int resource;
    List<String> listImagePath;
    INotifyChangeImage iNotifyChangeImage;

    public AdapterRecyclerImageUploadUpdate(Context context, int resource, List<String> listImagePath, INotifyChangeImage iNotifyChangeImage){
        this.context=context;
        this.resource=resource;
        this.listImagePath=listImagePath;
        this.iNotifyChangeImage = iNotifyChangeImage;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgUpload,imgDelete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUpload =itemView.findViewById(R.id.img_upload);
            imgDelete=itemView.findViewById(R.id.img_delete);
        }
    }

    @NonNull
    @Override
    public AdapterRecyclerImageUploadUpdate.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(resource,viewGroup,false);
        AdapterRecyclerImageUploadUpdate.ViewHolder viewHolder = new AdapterRecyclerImageUploadUpdate.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterRecyclerImageUploadUpdate.ViewHolder viewHolder, int i) {
        Uri uri = Uri.parse(listImagePath.get(i));
        Picasso.get().load(uri).centerCrop().fit().into(viewHolder.imgUpload);
        // viewHolder.imgUpload.setImageURI(uri);

//        try {
//            Bitmap bitmap = MediaStore.Images.Media.getBitmap((context).getContentResolver(),uri);
//            viewHolder.imgUpload.setImageBitmap(bitmap);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        //Set tag ????? tr??nh vi???c t??i s??? d???ng c???a recycler view
        viewHolder.imgDelete.setTag(i);
        viewHolder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = (int) v.getTag();
                listImagePath.remove(index);
                //Th??ng b??o c?? thay ?????i d??? li???u
                notifyDataSetChanged();

                iNotifyChangeImage.isNotifyChange(listImagePath);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listImagePath.size();
    }
}
