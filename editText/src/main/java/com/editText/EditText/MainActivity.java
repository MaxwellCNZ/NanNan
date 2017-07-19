package com.editText.EditText;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private HtmlEditText htmlEditText;
    private static final int RESULT_PICK = 101;//从相册中选择

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        htmlEditText = (HtmlEditText) findViewById(R.id.het);
        htmlEditText.setOnChoosePicListener(new HtmlEditText.OnChoosePicListener() {
            @Override
            public void onChoose() {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(intent, "请选择图片"), RESULT_PICK);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case RESULT_PICK:
                //从相册中选择
                if (resultCode == RESULT_OK && data != null) {
                    Uri uri = data.getData();
                    String bitmapPath = PathFromUriUtil.getRealFilePathFromUri(this, uri);
                    htmlEditText.setUploadPath(new HtmlFile(bitmapPath, null));
                }
                break;
        }
    }
}
