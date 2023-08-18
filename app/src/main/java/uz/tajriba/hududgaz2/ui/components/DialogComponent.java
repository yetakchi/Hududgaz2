package uz.tajriba.hududgaz2.ui.components;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;

import uz.tajriba.hududgaz2.R;


public class DialogComponent {

    private final Context context;
    private AlertDialog dialog;

    public DialogComponent(Context context) {
        this.context = context;
    }

    public void startLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        builder.setView(inflater.inflate(R.layout.view_loader, null));
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void dismissDialog() {
        dialog.dismiss();
    }

    public void alert(OnDialogClick dialogClick) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getText(R.string.no_result));
        builder.setPositiveButton(context.getText(R.string.retry), (dialogInterface, i) -> dialogClick.onClick());

        AlertDialog dialog = builder.create();
        // dialog.setCancelable(false);
        dialog.show();
    }

    public interface OnDialogClick {
        void onClick();
    }
}
