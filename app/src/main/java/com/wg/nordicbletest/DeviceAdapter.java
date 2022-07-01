package com.wg.nordicbletest;

import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> {

    private List<BleDevice> devices;
    private OnItemClickListener onItemClickListener;

    @FunctionalInterface
    public interface OnItemClickListener{
        void onItemClick(final BleDevice bleDevice);
    }

    public void  setOnItemClickListener(final OnItemClickListener listener){
        onItemClickListener = listener;
    }

    public DeviceAdapter(List<BleDevice> bleDevices) {
        devices = bleDevices;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
         BleDevice bluetoothDevice = devices.get(position);
         String name = bluetoothDevice.getName();
         String address = bluetoothDevice.getAddress();
         holder.NameText.setText(name == null ? "UnKnown" : name );
         holder.AddressText.setText(address);
         holder.deviceContaner.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 if (onItemClickListener != null){
                     int position = holder.getLayoutPosition();
                     final BleDevice bleDevice = devices.get(position);
                     onItemClickListener.onItemClick(bleDevice);
                 }
             }
         });
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView NameText;
        TextView AddressText;
        LinearLayout deviceContaner;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            NameText = (TextView) itemView.findViewById(R.id.device_name);
            AddressText = (TextView) itemView.findViewById(R.id.device_address);
            deviceContaner = (LinearLayout) itemView.findViewById(R.id.item_contenter);
        }
    }
}
