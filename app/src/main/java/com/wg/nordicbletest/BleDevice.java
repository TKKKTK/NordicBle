package com.wg.nordicbletest;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

import no.nordicsemi.android.support.v18.scanner.ScanResult;

public class BleDevice implements Parcelable {
   private String name;
   private String address;
   private BluetoothDevice device;

   public BleDevice(ScanResult result){
      device = result.getDevice();
      name = device.getName();
      address = device.getAddress();
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getAddress() {
      return address;
   }

   public void setAddress(String address) {
      this.address = address;
   }

   public BluetoothDevice getDevice() {
      return device;
   }

   public void setDevice(BluetoothDevice device) {
      this.device = device;
   }

   private BleDevice(Parcel in){
      device = in.readParcelable(BluetoothDevice.class.getClassLoader());
      name = in.readString();
      address = in.readString();
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel parcel, int i) {
      parcel.writeParcelable(device,i);
      parcel.writeString(name);
      parcel.writeString(address);
   }

   public static final Creator<BleDevice> CREATOR = new Creator<BleDevice>() {
      @Override
      public BleDevice createFromParcel(final Parcel source) {
         return new BleDevice(source);
      }

      @Override
      public BleDevice[] newArray(final int size) {
         return new BleDevice[size];
      }
   };

}
