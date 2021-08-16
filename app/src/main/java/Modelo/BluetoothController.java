package Modelo;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Handler;
import android.widget.Button;

public class BluetoothController {
    private static BluetoothAdapter bluetoothAdapter;
    private static String address;
    private static ChatUtils chatUtils;

    public BluetoothController() {}

    public static void SetBluetoothAdapter(BluetoothAdapter ba) {
        bluetoothAdapter = ba;
    }

    public static BluetoothAdapter GetBluetoothAdapter() {
        return bluetoothAdapter;
    }

    public static void SetAddress(String ad) {
        address = ad;
    }

    public static String GetAddress() {
        return address;
    }

    public static void CreateChatUtils(Context context, Handler handler, Button button) {
        //chatUtils = new ChatUtils(context, handler, button, button);
    }

    public static ChatUtils GetChatUtils() {
        return chatUtils;
    }

}
