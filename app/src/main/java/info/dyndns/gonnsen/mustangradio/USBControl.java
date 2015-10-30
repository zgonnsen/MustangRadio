package info.dyndns.gonnsen.mustangradio;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import info.dyndns.gonnsen.mustangradio.Library.UsbSerialDriver;
import info.dyndns.gonnsen.mustangradio.Library.UsbSerialPort;
import info.dyndns.gonnsen.mustangradio.Library.UsbSerialProber;

/**
 * Created by zgonnsenadm on 10/12/15.
 */
public class USBControl {

    UsbManager mUsbManager;
    UsbDeviceConnection connection;
    PendingIntent mPermissionIntent;
    private static final String TAG = "myApp";
    public static UsbSerialPort port= null;

    public String Open_USB_Port(){
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(mUsbManager);
        String message="null";
        if (availableDrivers.isEmpty()) {
            Log.v(TAG, "No USB Drivers connected");
            return "No Drivers Detected";
        }

        final UsbSerialDriver driver = availableDrivers.get(0);

        connection = mUsbManager.openDevice(driver.getDevice());
        if(connection == null){
            Log.v(TAG,"No USB connection found");
            return "No USB Available";
        }
        else{
            final List<UsbSerialPort> portList = driver.getPorts();
            port= portList.get(0);
            try {
                port.open(connection);
                port.setParameters(38400, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
                return "USB Connected";
            }
            catch (IOException e) {
                System.out.print("Can't Connect to ELM327!");
                //alertView("Connection Problem", "Can't find the ELM327");
            }

        }
        return message;
    }


    public String Write_USB_Port(byte[] message_press)  {
        String message ="";

        if (connection != null){
            try {
                //port.write(message,10);
                //byte[] Release =new byte[]{ (byte)0x48, 0x0d };
                port.write(message_press,100);
                message = " Sent: " + new String(message_press);
                Log.v(TAG, "Value Writing to the Press: " + new String(message_press));
                SystemClock.sleep(110);
                // new Thread(new Runnable() {
                //     @Override
                //     public void run() {
                //         try {
                //             Thread.sleep(1500);

                //         } catch (InterruptedException e) {
                //             e.printStackTrace();
                //         }
                //     }
                // }).start();
                //SystemClock.sleep(400);
                //Thread.sleep(400);
                //port.write(message_release, 100);

                //HeaderText.append(" Also Sent: " + new String(message_release));
                // port.write(concat(message,Release),10);


            } catch (IOException e) {
                Log.v(TAG, "Error Sending Command!");
                message = "Couldn't Send message";
                //alertView("Port Error", "Can't Send Message");
            }
            try {
                port.close();
                Log.v(TAG, "Port Successfully Closed");

            } catch (IOException e) {
                Log.v(TAG, "Couldn't close Port");
                //alertView("Port Error", "Can't Close Port");
            }

        }
        else{
            Log.v(TAG, "Not Connected to USB!");
            message = "Not Connected to USB";
        }
        return message;
    }


    private void alertView( String Title, String message, Context context) {
        final AlertDialog dialog = new AlertDialog.Builder(context).create();

        dialog.setTitle(Title);
        dialog.setIcon(R.drawable.exclamation5);
        dialog.setMessage(message);

        dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        dialoginterface.dismiss();
                    }
                });
        dialog.show();
    }


    private static final String ACTION_USB_PERMISSION =
            "com.android.example.USB_PERMISSION";
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbAccessory accessory = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
                    mUsbManager.requestPermission(accessory, mPermissionIntent);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if(accessory != null){
                            Open_USB_Port();
                        }
                    }
                    else {
                        Log.d(TAG, "permission denied for accessory " + accessory);
                    }
                }
            }
        }
    };
}
