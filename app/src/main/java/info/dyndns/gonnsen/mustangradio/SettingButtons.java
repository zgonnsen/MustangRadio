package info.dyndns.gonnsen.mustangradio;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import info.dyndns.gonnsen.mustangradio.Library.UsbSerialDriver;
import info.dyndns.gonnsen.mustangradio.Library.UsbSerialPort;
import info.dyndns.gonnsen.mustangradio.Library.UsbSerialProber;

/**
 * Created by zgonnsenadm on 5/6/15.
 */
public class SettingButtons extends Fragment implements View.OnClickListener {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    UsbManager mUsbManager;
    TextView HeaderText;
    UsbDeviceConnection connection;
    private static final String TAG = "myApp";


    byte message_press[] = new byte[16];
    byte message_release[] = new byte[16];

    private int[] buttonIds = new int[] {
            R.id.IO,R.id.CATFOLD, R.id.text, R.id.direct,
            R.id.Clock, R.id.Color, R.id.Sound, R.id.Sirius,
            R.id.SeekUp, R.id.SeekDown, R.id.SeekLeft, R.id.SeekRight
    };
    private Button[] buttonArray = new Button[buttonIds.length];


    public static UsbSerialPort port= null;
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static SettingButtons newInstance(int sectionNumber) {
        SettingButtons fragment = new SettingButtons();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        Log.v(TAG, "Started Setting Buttons");

        MainActivity activity = (MainActivity) getActivity();
        mUsbManager = activity.mUsbManager;

        HeaderText = (TextView) rootView.findViewById(R.id.SettingText);
        HeaderText.setText(Open_USB_Port());

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
            case R.id.IO:
                response =getString(R.string.buttonIO);
                break;
            case R.id.CATFOLD:
                response =getString(R.string.buttonCat);
                break;
            case R.id.text:
                response =getString(R.string.buttonText);
                break;
            case R.id.direct:
                response =getString(R.string.buttonDirect);
                break;
            case R.id.Clock:
                response =getString(R.string.buttonClock);
                break;
            case R.id.Sound:
                response =getString(R.string.buttonSound);
                break;
            case R.id.Sirius:
                response =getString(R.string.buttonSirius);
                break;
            case R.id.SeekUp:
                response =getString(R.string.buttonSeekUp);
                break;
            case R.id.SeekDown:
                response =getString(R.string.buttonSeekDown);
                break;
            case R.id.SeekLeft:
                response =getString(R.string.buttonSeekLeft);
                break;
            case R.id.SeekRight:
                response =getString(R.string.buttonSeekRight);
                break;
            case R.id.Color:
                response ="Have to work on the color";
                break;

        }
        String press = response + press_ending;
        String release = response + release_ending;

        Log.v(TAG, "String for the Press button: " + press);
        Log.v(TAG, "String for the Release button: " + release);

        message_press =press.getBytes();
        message_release = release.getBytes();

        HeaderText.setText("");
        HeaderText.setText(Open_USB_Port());
        Write_USB_Port(message_press);
        Open_USB_Port();
        Write_USB_Port(message_release);

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

}