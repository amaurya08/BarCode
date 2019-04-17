package heartbeat.barcode;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import heartbeat.barcode.Adapters.AadharAdapter;
import heartbeat.barcode.PojoClasses.PersonPojo;
import heartbeat.barcode.database.DBClass;
import heartbeat.barcode.database.UserDetails;

//implementing onclicklistener
public class OldEntries extends AppCompatActivity implements View.OnClickListener {

    //All UI Components used
    private ListView lv;
    private String file_Name;
    //Adapter and array List
    AadharAdapter aadharAdapter;
    ArrayList<PersonPojo> arrayList;
    //qr code scanner object
    IntentIntegrator qrScan;

    //To initialize the All the UI and adapter,arrays
    protected void init() {

        lv = (ListView) findViewById(R.id.lv);
        qrScan = new IntentIntegrator(this);

        arrayList = new ArrayList<>();
        aadharAdapter = new AadharAdapter(OldEntries.this, R.layout.list_item, arrayList);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_entries);

        Button buttonScan = (Button) findViewById(R.id.buttonScan);
        buttonScan.setOnClickListener(this);

        Intent i = getIntent();
        file_Name = i.getStringExtra("file_name");
        file_Name = getIntent().getStringExtra("file_name");
        init();
        lv.setAdapter(aadharAdapter);
        fetchValues();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int pos = position;
                final PersonPojo temp =  arrayList.get(pos);

                AlertDialog.Builder builder = new AlertDialog.Builder(OldEntries.this);
                String [] items={"View Aadhar Card"," Delete"};
                builder.setTitle("Select Action");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 1) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(OldEntries.this);
                            builder.setTitle("Deletion Action");
                            builder.setMessage("Do you want to delete this Entry ?");
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //add_new DBClass(FirstActivity.this).getWritableDatabase().rawQuery("DELETE FROM "+ UserDetails.TABLE_NAME + " WHERE "+UserDetails.FILE_NAME+" = '"+f.getFilename()+"'",null);

                                    if (new DBClass(OldEntries.this).getWritableDatabase().delete(UserDetails.TABLE_NAME, "" + UserDetails.UID + " = '" + temp.getUid() +"' AND "+UserDetails.FILE_NAME+" = '" + file_Name + "' ", null) > 0) {
                                        Toast.makeText(OldEntries.this, "Deleted", Toast.LENGTH_SHORT).show();
                                        fetchValues();
                                    } else
                                        Toast.makeText(OldEntries.this, "Error", Toast.LENGTH_SHORT).show();
                                    dialog.cancel();
                                }
                            }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(OldEntries.this, "Canceled Deletion Operation", Toast.LENGTH_SHORT).show();
                                    dialog.cancel();
                                }
                            });
                            builder.create().show();
                        }
                        else{
                            final PersonPojo temp=arrayList.get(pos);
                            String address=temp.getHouse()+" "+temp.getStreet()+" "+temp.getLm()+" "+temp.getLoc()+" "+temp.getVtc()+" "+temp.getPo()+" "+temp.getSubdist()+" "+temp.getDist()+" "+temp.getStatename()+" "+temp.getPincode();
                            //String uid, String name, String address, String yob, String dob, String fatherName
                            showInfoDialog(temp.getUid(), temp.getName(), address, temp.getYob(), temp.getDob(), temp.getFatherName());
                        }
                    }
                }).create().show();
            }
        });
    }


    @Override
    public void onClick(View view) {
        //initiating the qr code scan
        qrScan.initiateScan();
    }

    //Getting the scan results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                try {
                    //converting the data to string
                    String xml = result.getContents();
                    XmlPullParserFactory pullParserFactory;
                    pullParserFactory = XmlPullParserFactory.newInstance();
                    XmlPullParser parser = pullParserFactory.newPullParser();
                    InputStream is = new ByteArrayInputStream(xml.getBytes());
                    parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                    parser.setInput(is, null);
                    try {
                        parseXML(parser);
                        fetchValues();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                    String xml = result.getContents();
                    fetchValues();
                    //if control comes here
                    //that means the encoded format not matches
                    //in this case you can display whatever data is available on the qrcode
                    //to a toast
                    Toast.makeText(this, ""+xml, Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, "Card Not Accepted", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    private void fetchValues() {
        arrayList.clear();
        Cursor cursor = new DBClass(OldEntries.this).getReadableDatabase().rawQuery("SELECT * FROM " + UserDetails.TABLE_NAME + " WHERE " + UserDetails.FILE_NAME + " = '" + file_Name + "'", null);
        while (cursor.moveToNext()) {
            arrayList.add(new PersonPojo(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7),cursor.getString(8),cursor.getString(9),cursor.getString(10),cursor.getString(11),cursor.getString(12),cursor.getString(13),cursor.getString(14),cursor.getString(15),cursor.getString(16)));
        }
        cursor.close();
        aadharAdapter.notifyDataSetChanged();
    }

    private void parseXML(XmlPullParser parser) throws XmlPullParserException, IOException {

        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    Log.d("Debug", "Start of Document");
                    break;
                case XmlPullParser.START_TAG:


                    String uid = "" + parser.getAttributeValue("", "uid");

                    String name = "" + parser.getAttributeValue("", "name");

                    String house = "" + parser.getAttributeValue("", "house"); //
                    if(house.equals("null"))
                        house="";

                    String street = "" + parser.getAttributeValue("", "street");
                    if(street.equals("null"))
                        street="";

                    String lm = "" + parser.getAttributeValue("", "lm");
                    if(lm.equals("null"))
                        lm="";

                    String loc = "" + parser.getAttributeValue("", "loc");
                    if(loc.equals("null"))
                        loc="";

                    String vtc = "" + parser.getAttributeValue("", "vtc"); //
                    if(vtc.equals("null"))
                        vtc="";

                    String po = "" + parser.getAttributeValue("", "po");
                    if(po.equals("null"))
                        po="";

                    String dist = "" + parser.getAttributeValue("", "dist"); //
                    if(dist.equals("null"))
                        dist="";

                    String subdist = "" + parser.getAttributeValue("", "subdist");
                    if(subdist.equals("null"))
                        subdist="";

                    String state = "" + parser.getAttributeValue("", "state"); //
                    if(state.equals("null"))
                        state="";

                    String pincode = "" + parser.getAttributeValue("", "pc"); //
                    if(pincode.equals("null"))
                        pincode="";

                    String yob = "" + parser.getAttributeValue("", "yob");
                    if(yob.equals("null"))
                        yob="";

                    String dob = "" + parser.getAttributeValue("", "dob");
                    if(dob.equals("null"))
                        dob="";

                    String fatherName = parser.getAttributeValue("", "co");
                    if(fatherName.equals("null"))
                        fatherName="";

                    fatherName= fatherName.substring(4, fatherName.length());

                    String gender = parser.getAttributeValue("", "gender");
                    if(gender.equals("null"))
                        gender="";
/*
                    if (yob == null)
                        yob = "N.A.";

                    if (dob == null)
                        dob = "N.A.";

                    if (street == null)
                        dob = "N.A.";

                    if (house.isEmpty())
                        house = "";

                    if (lm == "null" )
                        lm = "";

                    if (loc == "null" )
                        loc = "";

                    if (vtc == "null" )
                        vtc = "";

                    if (po == "null" )
                        po = "";

                    if (dist == "null")
                        dist = "";

                    if (subdist == "null" )
                        subdist = "";

                    if (state == "null" )
                        state = "";

                    if (pincode == "null" )
                        pincode = "";
*/

                    if(checkDuplicateEntry(uid,file_Name)){
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Duplicate Entry Found");
                        builder.setMessage("This Aadhar Card is already Scanned");
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.create().show();
                    }
                    else
                    {
                        //String address = house + " " + street + " " + lm + " " + loc + " " + vtc + " " + po + " " + subdist + " " + dist + " " + state + ", " + pincode;
                        ContentValues cv = new ContentValues();
                        cv.put(UserDetails.UID, uid);
                        cv.put(UserDetails.NAME, name);

                        cv.put(UserDetails.HOUSE,house);
                        cv.put(UserDetails.STREET,street);
                        cv.put(UserDetails.LM,lm);
                        cv.put(UserDetails.LOC,loc);
                        cv.put(UserDetails.VTC,vtc);
                        cv.put(UserDetails.PO,po);
                        cv.put(UserDetails.SUBDIST,subdist);
                        cv.put(UserDetails.DIST,dist);
                        cv.put(UserDetails.STATE_NAME,state);
                        cv.put(UserDetails.PINCODE,pincode);

                        cv.put(UserDetails.YOB, yob);
                        cv.put(UserDetails.DOB, dob);

                        cv.put(UserDetails.FATHER_NAME, fatherName);
                       // cv.put(UserDetails.DATE_MODIFIED, Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "\\" + Calendar.getInstance().get(Calendar.MONTH) + "\\" + Calendar.getInstance().get(Calendar.YEAR) + "");
                        //cv.put(UserDetails.DATE_MODIFIED,"30\\06\\17");
                        cv.put(UserDetails.FILE_NAME, file_Name);
                        cv.put(UserDetails.GENDEDR, gender);

                        if (UserDetails.insert(new DBClass(OldEntries.this).getWritableDatabase(), cv) > 0) {
                            Toast.makeText(this, "Card Added", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(this, "There is some error ! Retry", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case XmlPullParser.END_TAG:
                    Log.d("Debug", "Ednd of Document");
            }
            eventType = parser.next();
        }
    }
    private boolean checkDuplicateEntry(String uid,String filename) {
        Cursor cursor= new  DBClass(OldEntries.this).getReadableDatabase().rawQuery("SELECT "+UserDetails.UID+" , "+UserDetails.FILE_NAME+" FROM "+ UserDetails.TABLE_NAME,null);
        while(cursor.moveToNext()){
            if(cursor.getString(0).equals(uid)&&cursor.getString(1).equals(filename)) {
                cursor.close();
                return true;
            }
        }
        cursor.close();
        return false;
    }
    public void showInfoDialog(String uid, String name, String address, String yob, String dob, String fatherName) {
        TextView  namev, tyob, tdob, father, addresst;

        LayoutInflater layoutInflater = this.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.details_dialog, null);


        namev = (TextView) view.findViewById(R.id.uid_name);
        namev.setText(name);

        addresst = (TextView) view.findViewById(R.id.address);
        addresst.setText(address);

        tyob = (TextView) view.findViewById(R.id.yob);
        tyob.setText(yob);

        tdob = (TextView) view.findViewById(R.id.dob_name);
        tdob.setText(dob);

        father = (TextView) view.findViewById(R.id.co_name);
        father.setText(fatherName);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setTitle("" + uid);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    //TO Check Whether there is any unsaved data or not
    @Override
    public void onBackPressed() {
        startActivity(new Intent(OldEntries.this, FirstActivity.class));
        super.onBackPressed();
    }
}