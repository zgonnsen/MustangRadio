package info.dyndns.gonnsen.mustangradio;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.app.AlertDialog;

import java.io.IOException;
import java.util.List;
import java.lang.Object;
import java.util.ArrayList;
import android.util.Log;

import info.dyndns.gonnsen.mustangradio.Library.HexDump;
import info.dyndns.gonnsen.mustangradio.Library.UsbSerialDriver;
import info.dyndns.gonnsen.mustangradio.Library.UsbSerialPort;
import info.dyndns.gonnsen.mustangradio.Library.UsbSerialProber;
/**
 * Created by zgonnsenadm on 5/6/15.
 */
public class RadioButtons extends Fragment implements View.OnClickListener {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    UsbManager mUsbManager;
    TextView HeaderText;
    UsbDeviceConnection connection;
    PendingIntent mPermissionIntent;
    private static final String TAG = "myApp";


    byte message_press[] = new byte[16];
    byte message_release[] = new byte[16];
    byte[] CR =new byte[]{ (byte)0x0D };

    private int[] buttonIds = new int[] {
            R.id.radio1,R.id.radio2,R.id.radio3,
            R.id.radio4,R.id.radio5,R.id.radio6,
            R.id.radio7,R.id.radio8,R.id.radio9,
            R.id.radio0,
            R.id.Volleft,R.id.Scan,R.id.Tune_left,
            R.id.Tune_right,R.id.Phone,R.id.Aux,
            R.id.VolRight,R.id.Menu,R.id.OK,
            R.id.iPhone,R.id.Android,R.id.AMFM
    };
    private Button[] buttonArray = new Button[buttonIds.length];


    public static UsbSerialPort port= null;
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static RadioButtons newInstance(int sectionNumber) {
        RadioButtons fragment = new RadioButtons();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public RadioButtons() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        Log.v(TAG, "Started Radio Buttons");

        MainActivity activity = (MainActivity) getActivity();
        mUsbManager = activity.mUsbManager;

        mPermissionIntent = PendingIntent.getBroadcast(container.getContext(), 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        this.getActivity().registerReceiver(mUsbReceiver, filter);

        /* Check for USB availability */


        HeaderText = (TextView) rootView.findViewById(R.id.section_label);
        HeaderText.setText(Open_USB_Port());

        /* Configuring the ELM327 */
        String Settings1 =getString(R.string.Settings_String1);
        String Settings2 =getString(R.string.Settings_String2);
        Log.v(TAG,"Sending out Settings:" + Settings1 + " " + Settings2);
        Write_USB_Port(Settings1.getBytes());

        for (int i=0; i<buttonIds.length; i++){

            buttonArray[i] = (Button) rootView.findViewById(buttonIds[i]);
            buttonArray[i].setOnClickListener(this);

        }

        return rootView;
    }


    public void onClick(View v) {
        String response="null";
        String press_ending = getString(R.string.normal_press_ending);
        String release_ending = getString(R.string.normal_release_ending);
        switch (v.getId()) {
            case R.id.radio1:
                response=getString(R.string.button1);
                break;
            case R.id.radio2:
                response =getString(R.string.button2);
                break;
            case R.id.radio3:
                response =getString(R.string.button3);
                break;
            case R.id.radio4:
                response =getString(R.string.button4);
                break;
            case R.id.radio5:
                response =getString(R.string.button5);
                break;
            case R.id.radio6:
                response =getString(R.string.button6);
                break;
            case R.id.radio7:
                response =getString(R.string.button7);
                break;
            case R.id.radio8:
                response =getString(R.string.button8);
                break;
            case R.id.radio9:
                response =getString(R.string.button9);
                break;
            case R.id.radio0:
                response =getString(R.string.button0);
                break;
            case R.id.Volleft:
                response =getString(R.string.buttonVolLeft);
                break;
            case R.id.VolRight:
                response =getString(R.string.buttonVolRight);
                break;
            case R.id.Scan:
                response =getString(R.string.buttonScan);
                break;
            case R.id.Menu:
                response =getString(R.string.buttonMenu);
                break;
            case R.id.Tune_left:
                response =getString(R.string.buttonTuneLeft);
                press_ending = getString(R.string.odd_press_ending);
                release_ending = getString(R.string.odd_release_ending);
                break;
            case R.id.Tune_right:
                response =getString(R.string.buttonTuneRight);
                press_ending = getString(R.string.odd_press_ending);
                release_ending = getString(R.string.odd_release_ending);
                break;
            case R.id.Phone:
                response =getString(R.string.buttonPhone);
                press_ending = getString(R.string.odd_press_ending);
                release_ending = getString(R.string.odd_release_ending);
                break;
            case R.id.Aux:
                response =getString(R.string.buttonAux);
                break;
            case R.id.OK:
                response =getString(R.string.buttonOK);
                press_ending = getString(R.string.odd_press_ending);
                release_ending = getString(R.string.odd_release_ending);
                break;
            case R.id.AMFM:
                response =getString(R.string.buttonAMFM);
                break;
            case R.id.iPhone:
                response =getString(R.string.buttoniPhone);
                break;
            case R.id.Android:
                response =getString(R.string.buttonAndroid);
                break;
        }
        String press = response + press_ending;
        String release = response + release_ending;

        Log.v(TAG, "String for the Press button: " + press);
        Log.v(TAG, "String for the Release button: " + release);

        message_press =press.getBytes();
        message_release = release.getBytes();

        HeaderText.setText("");
        Open_USB_Port();
        HeaderText.append(Write_USB_Port(message_press));
        Open_USB_Port();
        HeaderText.append(Write_USB_Port(message_release));


    }

    public String Open_USB_Port(){
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(mUsbManager);
        String message="null";
        if (availableDrivers.isEmpty()) {
            Log.v(TAG,"No USB Drivers connected");
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
                alertView("Connection Problem", "Can't find the ELM327");
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
            SystemClock.sleep(120);
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
            alertView("Port Error", "Can't Send Message");
        }
            try {
            port.close();
            Log.v(TAG, "Port Successfully Closed");

        } catch (IOException e) {
            Log.v(TAG, "Couldn't close Port");
            alertView("Port Error", "Can't Close Port");
        }

        }
        else{
            Log.v(TAG, "Not Connected to USB!");
            message = "Not Connected to USB";
        }
        return message;
    }

    private void alertView( String Title, String message ) {
        final AlertDialog dialog = new AlertDialog.Builder(this.getActivity()).create();

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