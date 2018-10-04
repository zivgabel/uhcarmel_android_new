package il.co.gabel.android.uhcarmel.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import il.co.gabel.android.uhcarmel.R;

public class ConfirmOrderSendDialogFragment  extends BaseDialogFragment {

    private int selectedBranch=0;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_new_order_branches_title)
                .setSingleChoiceItems(R.array.new_order_branches, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedBranch=which;
                    }
                })
                .setPositiveButton(R.string.dialog_new_order_confirm, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mListener.onDialogPositiveClick(ConfirmOrderSendDialogFragment.this);
            }
        })
                .setNegativeButton(R.string.dialog_new_order_dismiss, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogNegativeClick(ConfirmOrderSendDialogFragment.this);
                    }
                });
        return builder.create();
    }

    public int getSelectedBranch() {
        return selectedBranch;
    }
}
