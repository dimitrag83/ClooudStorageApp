package com.goudela.dimitra.sdy61_ge5_106304;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.widget.TextView;
import android.widget.Toast;

import com.goudela.dimitra.provider.R;

import static android.content.ContentValues.TAG;

public class TextEditorActivity extends Activity implements View.OnClickListener {

    private static EditText editText;
    private static TextView title;


    private static final int CREATE_REQUEST_CODE = 40;
    private static final int OPEN_REQUEST_CODE = 41;
    private static final int SAVE_REQUEST_CODE = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_editor);

        TextView title = (TextView) findViewById(R.id.titleTxt);

        Button btnNew =(Button)findViewById(R.id.btn_new);
        Button btnOpen =(Button)findViewById(R.id.btn_open);
        Button btnSave =(Button)findViewById(R.id.btn_save);

        btnNew.setOnClickListener(this);
        btnOpen.setOnClickListener(this);
        btnSave.setOnClickListener(this);

    }

    @Override
    protected void onStart()
    {
        super.onStart();
        editText = (EditText) findViewById(R.id.txt_Editor);
        title = (TextView) findViewById(R.id.titleTxt);

    }

private void newFile()
    {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, "newfile.txt");
        startActivityForResult(intent, CREATE_REQUEST_CODE);

    }

    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        Uri currentUri = null;

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CREATE_REQUEST_CODE) {
                if (resultData != null) {
                    Toast.makeText(getApplicationContext(), "File created", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == SAVE_REQUEST_CODE) {
                if (resultData != null) {
                    currentUri = resultData.getData();
                    writeFileContent(currentUri);
                    Toast.makeText(getApplicationContext(), "File saved", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == OPEN_REQUEST_CODE) {
                if (resultData != null) {
                    currentUri = resultData.getData();
                    try {
                        String content =
                                readFileContent(currentUri);
                                editText.setText(content);
                        Toast.makeText(getApplicationContext(), "File opened", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        // Handle error here
                    }
                }
            }
        }
    }


    private void writeFileContent(Uri uri)
    {
        try{
            ParcelFileDescriptor pfd =
                    this.getContentResolver().
                            openFileDescriptor(uri, "w");

            FileOutputStream fileOutputStream =
                    new FileOutputStream(pfd.getFileDescriptor());

            String textContent =
                    editText.getText().toString();

            fileOutputStream.write(textContent.getBytes());

            fileOutputStream.close();
            pfd.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openFile()
    {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        startActivityForResult(intent, OPEN_REQUEST_CODE);
    }

    public void saveFile()
    {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        startActivityForResult(intent, SAVE_REQUEST_CODE);
    }

    private String readFileContent(Uri uri) throws IOException {

        InputStream inputStream =
                getContentResolver().openInputStream(uri);
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(
                        inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String currentline;
        while ((currentline = reader.readLine()) != null) {
            stringBuilder.append(currentline + "\n");
        }
        inputStream.close();
        return stringBuilder.toString();
    }

    // TODO: 17/05/2017 check for wifi connection
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_new:
                if (checkWifi())
                newFile();
                else
                    Toast.makeText(this,"Cannot create file, no wifi connection",Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_open:
                if (checkWifi())
                openFile();
                else
                    Toast.makeText(this,"Cannot open file, no wifi connection",Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_save:
                if (checkWifi())
                saveFile();
               else
                Toast.makeText(this,"Cannot save file, no wifi connection",Toast.LENGTH_SHORT).show();
                break;
        }
    }


    public boolean checkWifi(){

        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (!wifi.isWifiEnabled()) {
            Toast.makeText(this,"Please turn on wifi to continue",Toast.LENGTH_SHORT).show();
            return false;
        } else
            return true;
    }
}
