package heartbeat.barcode;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Calendar;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import heartbeat.barcode.Adapters.fileAdapter;
import heartbeat.barcode.PojoClasses.filePojo;
import heartbeat.barcode.database.DBClass;
import heartbeat.barcode.database.UserDetails;


public class FirstActivity extends AppCompatActivity {

    //All View Componenets Here
    ArrayList<filePojo> arrayList;
    protected FloatingActionButton fb;
    protected ListView lv;
    protected fileAdapter fileadapter;
    //all overridden methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        checkstatus();
        init();
        lv.setAdapter(fileadapter);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createFileDialog();
            }
        });
        fetchValues();
        methodListener();
    }
    //Initializers Function

    protected void init() {
        lv = (ListView) findViewById(R.id.doc_lv);
        arrayList = new ArrayList<>();
        fileadapter = new fileAdapter(FirstActivity.this, R.layout.file_list_item, arrayList);
        fb = (FloatingActionButton) findViewById(R.id.floating);
    }

    private void checkstatus() {
        if (ContextCompat.checkSelfPermission(FirstActivity.this, Manifest.permission.CAMERA) < 0 || ContextCompat.checkSelfPermission(FirstActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) < 0 || ContextCompat.checkSelfPermission(FirstActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) < 0)
            showPermission();
    }

    private void showPermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(FirstActivity.this);
        builder.setTitle("Allow Application following permissions ?");
        getResources().getString(R.string.camera);
        String permission = getResources().getString(R.string.camera) + "\n" + "\t\t" + getResources().getString(R.string.camera_desc) + "\n\n" + getResources().getString(R.string.storage) + "\n" + "\t\t" + getResources().getString(R.string.storage_desc);
        builder.setMessage(permission);
        builder.setCancelable(false);
        builder.setPositiveButton("Give Permission", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String string[] = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                ActivityCompat.requestPermissions(FirstActivity.this, string, 1);
            }
        });
        builder.create().show();
    }

    private void methodListener() {
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String[] items = {"View", "Delete", "Share ARTH", "Share ARTH YBL"};
                final int pos = position;

                AlertDialog.Builder builder = new AlertDialog.Builder(FirstActivity.this);
                builder.setTitle("Select Action");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final filePojo f = arrayList.get(pos);
                        if (which == 0) {
                            Intent i = new Intent(FirstActivity.this, OldEntries.class);
                            i.putExtra("file_name", f.getFilename());
                            startActivity(i);

                        }
                        if (which == 1) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(FirstActivity.this);
                            builder.setTitle("Deletion Action");
                            builder.setMessage("Do you want to delete this file ?");
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //add_new DBClass(FirstActivity.this).getWritableDatabase().rawQuery("DELETE FROM "+ UserDetails.TABLE_NAME + " WHERE "+UserDetails.FILE_NAME+" = '"+f.getFilename()+"'",null);

                                    if (new DBClass(FirstActivity.this).getWritableDatabase().delete(UserDetails.TABLE_NAME, "" + UserDetails.FILE_NAME + " = '" + f.getFilename() + "'", null) > 0) {
                                        Toast.makeText(FirstActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                                        fetchValues();

                                    } else
                                        Toast.makeText(FirstActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                    dialog.cancel();
                                }
                            }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(FirstActivity.this, "Canceled Deletion Operation", Toast.LENGTH_SHORT).show();
                                    dialog.cancel();
                                }
                            });
                            builder.create().show();
                        }
                        if (which == 2) {
                            try {
                                List<String[]> templist = new ArrayList<>();
                                Cursor cursor = new DBClass(FirstActivity.this).getReadableDatabase().rawQuery("SELECT * FROM " + UserDetails.TABLE_NAME + " WHERE " + UserDetails.FILE_NAME + " = '" + f.getFilename() + "'", null);
                                while (cursor.moveToNext()) {
                                    String dob;
                                    if (cursor.getString(14).equals(""))
                                        dob = cursor.getString(13);
                                    else
                                        dob = cursor.getString(14);

                                    String uid = cursor.getString(1);
                                    String name = cursor.getString(2);
                                    String house = cursor.getString(3);
                                    String street = cursor.getString(4);
                                    String lm = cursor.getString(5);
                                    String loc = cursor.getString(6);
                                    String vtc = cursor.getString(7);
                                    String po = cursor.getString(8);
                                    String dist = cursor.getString(9);
                                    String subdist = cursor.getString(10);
                                    String statename = cursor.getString(11);
                                    String pincode = cursor.getString(12);
                                    String fatherName = cursor.getString(15);
                                    String uid2 = uid.substring(0, 4) + " " + uid.substring(4, 8) + " " + uid.substring(8, 12);

                                    String address = house + " " + street + " " + lm + " " + loc + " " + vtc + " " + po + " " + dist + " " + subdist + " " + statename + " " + pincode;
                                    templist.add(new String[]{"", "", "", "", "", "", "", name, "", "", "", "", "", "", fatherName, "", "", "", "", "", "", "", "", dob, "", "", "AADHAR CARD", uid2,
                                            "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
                                            address, dist, statename, pincode});
                                }
                                cursor.close();
                                CSVWriter writer = new CSVWriter(new FileWriter("//sdcard//" + f.getFilename() + ".csv"), ',');
                                String column[] = new String[]{"Segment Identifier", "Credit Request Type", "Credit Report Transaction ID", "Credit Inquiry Purpose Type",
                                        "Credit Inquiry Purpose Type Description", "Credit Inquiry Stage", "Credit Report Transaction Date Time",
                                        "Applicant Name1", "Applicant Name2", "Applicant Name3", "Applicant Name4", "Applicant Name5", "Member Father Name",
                                        "Member Mother Name", "Member Spouse Name", "Member relationshid Type 1", "Member relationshid Name 1",
                                        "Member relationshid Type 2", "Member relationshid Name 2", "Member relationshid Type 3", "Member relationshid Name 3",
                                        "Member relationshid Type 4", "Member relationshid Name 4", "Applicant Birth Date (DD-MM-YYYY)", "Applicant Age",
                                        "Applicant Age as on date (DD-MM-YYYY)", "Applicant ID Type 1", "Applicant ID 1", "Applicant ID Type 2", "Applicant ID 2",
                                        "Acct Open Date", "Application-ID/ Account-No", "Branch ID", "Member ID", "Kendra ID", "Applied for Amount/ Current Balance",
                                        "Key PersonPojo Name", "Key PersonPojo Relation", "Nominee Name", "Nominee Relationship Type", "Applicant Telephone Number Type1 ",
                                        "Applicant Telephone Number 1", "Applicant Telephone Number Type2", "Applicant Telephone Number 2", "Applicant Address Type 1",
                                        "Applicant Address1", "Applicant Address1  City", "Applicant Address1  State", "Applicant Address1  PIN Code",
                                        "Applicant Address Type 2", "Applicant Address2", "Applicant Address2  City", "Applicant Address2  State",
                                        "Applicant Address2  PIN Code"};
                                writer.writeNext(column);
                                writer.writeAll(templist);
                                writer.close();

                                // String filelocation = getFilesDir().getPath().toString()+"/" + f.getFilename();
                                  /*
                                Set< String > keyid = data.keySet();
                                int rowid = 0;
                                for (String key : keyid)
                                {
                                    row = spreadsheet.createRow(rowid++);
                                    Object [] objectArr = data.get(key);
                                    int cellid = 0;
                                    for (Object obj : objectArr)
                                    {
                                        Cell cell = row.createCell(cellid++);
                                        cell.setCellValue((String)obj);
                                    }
                                }                                */
                                File Root = Environment.getExternalStorageDirectory();
                                String filelocation = Root.getAbsolutePath() + "/" + f.getFilename() + ".csv";
                                File file = new File(filelocation);


                                Uri u1 = FileProvider.getUriForFile(FirstActivity.this, "heartbeat.barcode", file);


                                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                                //sendIntent.putExtra(Intent.EXTRA_SUBJECT, Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "\\" + Calendar.getInstance().get(Calendar.MONTH) + "\\" + Calendar.getInstance().get(Calendar.YEAR) + "");

                                sendIntent.putExtra(Intent.EXTRA_SUBJECT, "File Dated" + Calendar.getInstance().get(Calendar.DATE) + "\\" + Calendar.getInstance().get(Calendar.MONTH) + "\\" + Calendar.getInstance().get(Calendar.YEAR));

                                sendIntent.putExtra(Intent.EXTRA_STREAM, u1);

                                sendIntent.setType("text/csv");
                                startActivity(sendIntent);

                            } catch (Exception e) {
                                String s = e.toString();
                                Toast.makeText(FirstActivity.this, "" + s, Toast.LENGTH_SHORT).show();
                            }
                        }
                        if (which == 3) {
                            try {
                                List<String[]> templist = new ArrayList<>();
                                Cursor cursor = new DBClass(FirstActivity.this).getReadableDatabase().rawQuery("SELECT * FROM " + UserDetails.TABLE_NAME + " WHERE " + UserDetails.FILE_NAME + " = '" + f.getFilename() + "'", null);
                                while (cursor.moveToNext()) {
                                    String dob;
                                    if (cursor.getString(14).equals(""))
                                        dob = cursor.getString(13);
                                    else
                                        dob = cursor.getString(14);
                                    String uid = cursor.getString(1);
                                    String name = cursor.getString(2);
                                    String house = cursor.getString(3);
                                    String street = cursor.getString(4);
                                    String lm = cursor.getString(5);
                                    String loc = cursor.getString(6);
                                    String vtc = cursor.getString(7);
                                    String po = cursor.getString(8);
                                    String dist = cursor.getString(9);
                                    String subdist = cursor.getString(10);
                                    String statename = cursor.getString(11);
                                    String pincode = cursor.getString(12);
                                    String fatherName = cursor.getString(15);
                                    String gender = cursor.getString(17);
                                    String filename = cursor.getString(16);

                                    String uid2 = uid.substring(0, 4) + " " + uid.substring(4, 8) + " " + uid.substring(8, 12);

                                    String address = house + " " + street + " " + lm + " " + loc + " " + vtc + " " + po + " " + dist + " " + subdist + " " + statename + " " + pincode;

                                    templist.add(new String[]{"", "", "", filename, "", filename, "", "", "", "", "", "", "", "", "", "",
                                            "", "", "", "", name, "", "", address, dist, dist, statename, pincode, gender, "AADHAR CARD", uid2, "", "", "", "", "", dob, "", fatherName});
                                }


                                CSVWriter writer = new CSVWriter(new FileWriter("//sdcard//" + f.getFilename() + ".csv"), ',');
                                String column[] = new String[]{"BC Code", "Bc Branch Code", "Centre Code", "Centre Name", "Group Code", "Group Name", "YBL Branch Code", "YBL Product Code", "YBL Cust ID", "Old Cust ID", "Reason for Reallocation", "BC Unique Member ID", "MEMBER ID", "Group Meeting Date", "MODE", "WEEK ID", "WEEK NAME", "GROUP GEOGRAPHIC CLASSIFICATION", "MEMBER RECORD FLAG", "Topup loan Flag", "Member Name", "MEMBER SCORE", "MEMBER LOAN APPLIED", "House no Street Name", "Landmark/Village/City", "District", "Member State", "Member Pincode", "MEMBER GENDER", "Member ID Type", "Member ID No.", "MEMBER ADDRESS TYPE", "MEMBER ADDRESS ID NO.", "Member Education", "Member Phone1", "Member Age", "Member Date of Birth", "Member Relation", "Relative Name", "Relative Age", "MEMBER LOAN CYCLE WITH BC", "MEMBER LOAN CYCLE WITH YBL", "EXPECTED DISBURSEMENT DATE", "APPLICATION DATE", "LOAN TENURE", "LOAN PURPOSE", "GROUP TOTAL MEMBERS", "BC AGENT CODE", "Bank Account number (Non-YBL)", "Member Religion", "Extra 1", "Extra 2", "Group Record Flag", "Existing Member Id", "Existing Group Id", "Additional KYC Type", "Additional KYC No", "Internal System Group Code", "Category", "Farmer Category", "Type of Ownership", "Member Loan Eligibility", "Alternate name of member", "Asset Ownership Indicator/ Poverty index", "Number of dependents", "Annual Family Expenses", "Probable Meeting day", "Probable Meeting Time", "Marital status type", "Occupation", "Total Family Income", "Member Social Strata", "Center Leader Indicator", "Bank Account Bank Name 1", "Bank Branch Name 1", "Bank Account Number 1", "Bank IFSC Code 1", "Bank Account Bank Name 2", "Bank Branch Name 2", "Bank Account Number 2", "Bank IFSC Code 2", "Mothers name"

                                };
                                writer.writeNext(column);
                                writer.writeAll(templist);
                                writer.close();

                                // String filelocation = getFilesDir().getPath().toString()+"/" + f.getFilename();
                                  /*
                                Set< String > keyid = data.keySet();
                                int rowid = 0;
                                for (String key : keyid)
                                {
                                    row = spreadsheet.createRow(rowid++);
                                    Object [] objectArr = data.get(key);
                                    int cellid = 0;
                                    for (Object obj : objectArr)
                                    {
                                        Cell cell = row.createCell(cellid++);
                                        cell.setCellValue((String)obj);
                                    }
                                }                                */
                                File Root = Environment.getExternalStorageDirectory();
                                String filelocation = Root.getAbsolutePath() + "/" + f.getFilename() + ".csv";
                                File file = new File(filelocation);

                                Uri u1 = FileProvider.getUriForFile(FirstActivity.this, "heartbeat.barcode", file);


                                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_SUBJECT, "YBL File Dated" + Calendar.getInstance().get(Calendar.DATE) + "\\" + Calendar.getInstance().get(Calendar.MONTH) + "\\" + Calendar.getInstance().get(Calendar.YEAR));
                                sendIntent.putExtra(Intent.EXTRA_STREAM, u1);
                                sendIntent.setType("text/csv");
                                startActivity(sendIntent);

                            } catch (Exception e) {
                                String s = e.toString();
                                Toast.makeText(FirstActivity.this, "" + s, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }).create().show();
            }
        });
    }

    private void fetchValues() {
        arrayList.clear();
        Cursor cursor = UserDetails.distinctSelection(new DBClass(FirstActivity.this).getReadableDatabase(), UserDetails.FILE_NAME);
        while (cursor.moveToNext()) {
            arrayList.add(new filePojo(cursor.getString(0)));
        }
        cursor.close();
        fileadapter.notifyDataSetChanged();
    }

    //Method to crate a dialog
    private void createFileDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.create_file_dialog, null);
        builder.setView(view);
        builder.setTitle("Enter File Name");


        final EditText tx = (EditText) view.findViewById(R.id.filename);
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
                        Matcher m = p.matcher(tx.getText().toString());

                        if (tx.getText().toString().isEmpty() || m.find()) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(FirstActivity.this);
                            builder.setMessage("Enter Valid Name").setPositiveButton("Go Back", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).setTitle("File Name Error");

                            builder.create().show();
                        } else if (checkFileName(tx.getText().toString())) {
                            Toast.makeText(FirstActivity.this, "File Name Already Exist", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent i = new Intent(FirstActivity.this, NewEntries.class);
                            i.putExtra("file_name", "" + tx.getText().toString());
                            startActivity(i);
                        }
                    }
                }).setCancelable(false);

        AlertDialog ad = builder.create();
        ad.show();
    }

    private boolean checkFileName(String s) {
        Cursor cursor = new DBClass(FirstActivity.this).getReadableDatabase().rawQuery("SELECT " + UserDetails.FILE_NAME + " FROM " + UserDetails.TABLE_NAME, null);
        while (cursor.moveToNext()) {
            if (cursor.getString(0).equals(s)) {
                return true;
            }
        }
        cursor.close();
        return false;
    }
}
