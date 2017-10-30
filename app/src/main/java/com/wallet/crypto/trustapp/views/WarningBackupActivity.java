package com.wallet.crypto.trustapp.views;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.wallet.crypto.trustapp.R;
import com.wallet.crypto.trustapp.controller.Controller;

public class WarningBackupActivity extends AppCompatActivity {

    Button mBackupButton;
    Button mLaterButton;
    String mAddress;
    String mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warning_backup);

        final Controller controller = Controller.get();

        mAddress = this.getIntent().getStringExtra(Controller.KEY_ADDRESS);
        mPassword = this.getIntent().getStringExtra(Controller.KEY_PASSWORD);
        assert(!mAddress.isEmpty());
        assert(!mPassword.isEmpty());

        mBackupButton = findViewById(R.id.backup_button);
        mBackupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String keystoreJson = controller.clickExportAccount(WarningBackupActivity.this, mAddress, mPassword);
                if (keystoreJson.isEmpty()) {
                    Toast.makeText(WarningBackupActivity.this, "Unable to export", Toast.LENGTH_SHORT).show();
                } else {
                    showKeystore(keystoreJson);
                }
            }
        });

        mLaterButton = findViewById(R.id.later_button);
        mLaterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(WarningBackupActivity.this)
                        .setTitle(getString(R.string.title_watchout))
                        .setMessage(getString(R.string.unrecoverable_message))
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                WarningBackupActivity.this.finish();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });
    }

    private void showKeystore(final String keystoreJson) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(keystoreJson)
                .setTitle(getString(R.string.message_save_this));

        // Add the buttons
        builder.setPositiveButton(R.string.copy, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(getString(R.string.keystore_keyword), keystoreJson);
                clipboard.setPrimaryClip(clip);

                Toast.makeText(WarningBackupActivity.this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        builder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}