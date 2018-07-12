package szekelyistvan.com.colorpalette.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import szekelyistvan.com.colorpalette.R;
import szekelyistvan.com.colorpalette.util.PaletteAsyncQueryHandler;

import static szekelyistvan.com.colorpalette.provider.PaletteContract.PaletteEntry.CONTENT_URI_FAVORITE;

public class DeleteDialog extends DialogFragment{

    private  DeleteDialogListener listener;

    public interface DeleteDialogListener {
        void onFavoriteListDelete();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        setCancelable(false);

        builder.setMessage("Do you delete favorite list?")
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new PaletteAsyncQueryHandler(getActivity()
                                .getContentResolver())
                                .startDelete(0, null, CONTENT_URI_FAVORITE, null, null);
                        listener.onFavoriteListDelete();
                    }
                })
                .setNegativeButton(R.string.cancel_dialog, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (DeleteDialogListener) context;
        } catch (ClassCastException e){
            throw new ClassCastException(context.toString() + getString(R.string.implement_dialog));
        }
    }
}
