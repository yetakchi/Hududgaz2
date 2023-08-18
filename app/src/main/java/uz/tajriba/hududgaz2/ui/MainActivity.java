package uz.tajriba.hududgaz2.ui;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.IOException;
import java.io.InputStream;

import uz.tajriba.hududgaz2.R;
import uz.tajriba.hududgaz2.app.ResultHandler;
import uz.tajriba.hududgaz2.ui.components.DialogComponent;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ResultHandler handler;

    private Button btnSys;
    private Button btnDoc;
    private Button btnReset;

    private DialogComponent dialogComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setContentActivity();
    }

    private void setContentActivity() {
        ImageView logo = findViewById(R.id.logo_img);
        btnReset = findViewById(R.id.btn_recovery);
        btnSys = findViewById(R.id.system);
        btnDoc = findViewById(R.id.document);

        dialogComponent = new DialogComponent(this);

        btnSys.setOnClickListener(this);
        btnDoc.setOnClickListener(this);
        btnReset.setOnClickListener(this::reset);
        btnReset.setVisibility(View.INVISIBLE);
        setLogo(logo);

        handler = new ResultHandler();
        handler.setOnClickListener(new ResultHandler.OnResultListener() {
            @Override
            public void onStart() {
                showLoader();
                btnReset.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(String str) {
                hideLoader();

                System.out.println(str);
                Snackbar.make((View) btnReset.getParent(), str, Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String error) {
                hideLoader();

                System.out.println(error);
                Snackbar.make((View) btnReset.getParent(), getString(R.string.error), Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void setLogo(ImageView logo) {
        try {
            AssetManager assetManager = getAssets();
            InputStream stream = assetManager.open("logo.png");
            Drawable d = Drawable.createFromStream(stream, null);
            logo.setImageDrawable(d);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void reset(View view) {
        view.setVisibility(View.INVISIBLE);
        btnSys.setEnabled(true);
        btnDoc.setEnabled(true);

        handler.onReset();
    }

    @Override
    public void onClick(View btn) {
        Button button = (Button) btn;
        scanCode(button.getText().toString());

        String tag = button.getTag().toString();
        if (tag.equals(btnDoc.getTag().toString()))
            handler.onDocumentClick();
        else
            handler.onSystemClick();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        onResultReturn(result);
    }

    private void scanCode(String... args) {
        if (args.length == 0) {
            return;
        }

        String text = args[0];

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CapActivity.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES); // ALL_CODE_TYPES
        integrator.setPrompt(String.format(getString(R.string.document_check), text.toUpperCase()));
        integrator.initiateScan();
    }

    private void onResultReturn(IntentResult result) {
        if (result == null) {
            Toast.makeText(this, getText(R.string.no_result), Toast.LENGTH_SHORT).show();
            return;
        }

        String content = result.getContents();
        if (content != null) {
            handler.onReceiveResult(content);
            btnSys.setEnabled(!handler.getSystemState());
            btnDoc.setEnabled(!handler.getDocumentState());
            btnReset.setVisibility(View.VISIBLE);

            Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
        } else {
            dialogComponent.alert(this::scanCode);
        }
    }

    private void showLoader() {
        this.dialogComponent.startLoadingDialog();
    }

    private void hideLoader() {
        this.dialogComponent.dismissDialog();
    }
}