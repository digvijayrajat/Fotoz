package com.digvijay.fotoz.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.digvijay.fotoz.R;
import com.digvijay.fotoz.utils.ExifUtil;
import com.digvijay.fotoz.utils.InfoUtil;
import com.digvijay.fotoz.utils.MediaType;

import java.util.ArrayList;
import java.util.List;


import static android.content.Context.CLIPBOARD_SERVICE;

public class InfoRecyclerViewAdapter extends RecyclerView.Adapter {

    private static final int INFO_VIEW_TYPE = 0;
    private static final int COLOR_VIEW_TYPE = 1;
    private static final int LOCATION_VIEW_TYPE = 2;
    private static final int TAGS_VIEW_TYPE = 3;

    private ArrayList<InfoUtil.InfoItem> infoItems;

    public interface OnDataRetrievedCallback {
        void onDataRetrieved();

        void failed();

        Context getContext();
    }

    public boolean exifSupported(Context context, String path) {
        String mimeType = MediaType.getMimeType(context, Uri.parse(path));
        return mimeType != null && MediaType.doesSupportExifMimeType(mimeType);
    }

    public void retrieveData(final String path, final boolean showColors, final OnDataRetrievedCallback callback) {
        if (path == null) {
            callback.failed();
            return;
        }

        AsyncTask.execute(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                infoItems = new ArrayList<>();
                if (showColors) {
                    infoItems.add(new InfoUtil.ColorsItem(path));
                }

                //infoItems.add(new InfoUtil.TagsItem(albumItem));

                Context context = callback.getContext();


                infoItems.add(new InfoUtil.InfoItem(context.getString(R.string.info_filename), path)
                        .setIconRes(R.drawable.ic_insert_drive_file_black_24dp));
                infoItems.add(new InfoUtil.InfoItem(context.getString(R.string.info_filepath), path)
                        .setIconRes(R.drawable.ic_folder_open_black_24dp));
                infoItems.add(InfoUtil.retrieveFileSize(context, Uri.parse(path)).setIconRes(R.drawable.ic_storage_black_24dp));

                ExifInterface exif = null;
                if (exifSupported(context, path)) {
                    exif = ExifUtil.getExifInterface(context, Uri.parse(path));
                }

                //infoItems.add(InfoUtil.retrieveDimensions(context, exif).setIconRes(R.drawable.ic_photo_size_select_large_black_24dp));
                infoItems.add(InfoUtil.retrieveFormattedDate(context, exif)
                        .setIconRes(R.drawable.ic_date_range_black_24dp));

                if (exif != null) {
                    infoItems.add(InfoUtil.retrieveLocation(context, exif)
                            .setIconRes(R.drawable.ic_my_location_black_24dp));
                    infoItems.add(InfoUtil.retrieveFocalLength(context, exif)
                            .setIconRes(R.drawable.ic_nature_people_black_24dp));
                    infoItems.add(InfoUtil.retrieveExposure(context, exif)
                            .setIconRes(R.drawable.ic_timelapse_black_24dp));
                    infoItems.add(InfoUtil.retrieveModelAndMake(context, exif)
                            .setIconRes(R.drawable.ic_camera_alt_black_24dp));

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        infoItems.add(InfoUtil.retrieveAperture(context, exif)
                                .setIconRes(R.drawable.ic_camera_black_24dp));
                        infoItems.add(InfoUtil.retrieveISO(context, exif)
                                .setIconRes(R.drawable.ic_iso_black_24dp));
                    }
                }



                callback.onDataRetrieved();
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        InfoUtil.InfoItem infoItem = infoItems.get(position);
        if (infoItem instanceof InfoUtil.LocationItem) {
            return LOCATION_VIEW_TYPE;
        }
        else
            return INFO_VIEW_TYPE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutRes;
        switch (viewType) {
            default:
                layoutRes = R.layout.info_item;
                break;
        }
        View v = LayoutInflater.from(parent.getContext())
                .inflate(layoutRes, parent, false);
        switch (viewType) {
            case INFO_VIEW_TYPE:
                return new InfoHolder(v);
            case LOCATION_VIEW_TYPE:
                return new LocationHolder(v);
            default:
                break;
        }
        return new InfoHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        InfoUtil.InfoItem infoItem = infoItems.get(position);

        ((InfoHolder) holder).bind(infoItem);

    }

    @Override
    public int getItemCount() {
        return infoItems.size();
    }


    /*ViewHolder classes*/
    static class InfoHolder extends RecyclerView.ViewHolder {

        TextView type, value;
        ImageView icon;

        InfoHolder(View itemView) {
            super(itemView);
            type = itemView.findViewById(R.id.tag);
            value = itemView.findViewById(R.id.value);
            icon = itemView.findViewById(R.id.icon);
            setTextColors();
        }

        void bind(InfoUtil.InfoItem infoItem) {
            type.setText(infoItem.getType());
            if (ExifUtil.NO_DATA.equals(infoItem.getValue())) {
                value.setText(R.string.unknown);
            } else {
                value.setText(infoItem.getValue());
            }
            setIcon(infoItem);
        }

        void setIcon(InfoUtil.InfoItem infoItem) {
            icon.setImageResource(infoItem.getIconRes());
        }

        void setTextColors() {
            Context context = type.getContext();
            type.setTextColor(ContextCompat.getColor(context,R.color.colorAccent));
            value.setTextColor(ContextCompat.getColor(context,R.color.colorAccent));
            icon.setColorFilter(ContextCompat.getColor(context,R.color.colorAccent));
        }
    }

    static class LocationHolder extends InfoHolder {

        private InfoUtil.LocationItem locationItem;

        private String featureName;

        LocationHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void bind(InfoUtil.InfoItem infoItem) {
            type.setText(infoItem.getType());
            setIcon(infoItem);
            if (infoItem instanceof InfoUtil.LocationItem) {
                locationItem = (InfoUtil.LocationItem) infoItem;

                if (!ExifUtil.NO_DATA.equals(locationItem.getValue())) {
                    value.setText(locationItem.getValue());
                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            launchLocation();
                        }
                    });
                    retrieveAddress(itemView.getContext(), locationItem.getValue());
                } else {
                    value.setText(R.string.unknown);
                    itemView.setOnClickListener(null);
                    itemView.setClickable(false);
                }
            }
        }

        private void retrieveAddress(final Context context, final String locationString) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    String valueText = locationItem.getValue();
                    String[] parts = locationString.split(",");
                    try {
                        double lat = Double.parseDouble(parts[0]);
                        double lng = Double.parseDouble(parts[1]);

                        Address address = InfoUtil.retrieveAddress(context, lat, lng);
                        if (address != null) {
                            featureName = address.getFeatureName();
                            valueText = null;
                            if (address.getLocality() != null) {
                                valueText = address.getLocality();
                            }
                            if (address.getAdminArea() != null) {
                                if (valueText != null) {
                                    valueText += ", " + address.getAdminArea();
                                } else {
                                    valueText = address.getAdminArea();
                                }
                            }
                            if (valueText == null) {
                                valueText = locationString;
                            }

                        }
                    } catch (NumberFormatException ignored) {
                    }

                    final String finalValueText = valueText;
                    value.post(new Runnable() {
                        @Override
                        public void run() {
                            if (ExifUtil.NO_DATA.equals(finalValueText)) {
                                value.setText(R.string.unknown);
                            } else {
                                value.setText(finalValueText);
                            }
                        }
                    });
                }
            });
        }

        private void launchLocation() {
            String location = "geo:0,0?q=" + locationItem.getValue();
            if (featureName != null) {
                location += "(" + featureName + ")";
            }
            Uri gmUri = Uri.parse(location);
            Intent intent = new Intent(Intent.ACTION_VIEW)
                    .setData(gmUri)
                    .setPackage("com.google.android.apps.maps");

            Context context = itemView.getContext();
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            }
        }
    }




}