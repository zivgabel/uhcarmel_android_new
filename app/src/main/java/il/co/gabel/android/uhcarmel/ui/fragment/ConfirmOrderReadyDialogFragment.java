package il.co.gabel.android.uhcarmel.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import il.co.gabel.android.uhcarmel.R;

public class ConfirmOrderReadyDialogFragment extends BaseDialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_confirm_order_ready).setPositiveButton(R.string.dialog_confirm_delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Send the positive button event back to the host activity
                mListener.onDialogPositiveClick(ConfirmOrderReadyDialogFragment.this);
            }
        })
                .setNegativeButton(R.string.dialog_dismiss_delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the negative button event back to the host activity
                        mListener.onDialogNegativeClick(ConfirmOrderReadyDialogFragment.this);
                    }
                });
        return builder.create();
    }


}
