package il.co.gabel.android.uhcarmel.ui.fragment;

import android.app.DialogFragment;
import android.content.Context;

public abstract  class BaseDialogFragment extends DialogFragment {
    public interface NoticeDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    protected NoticeDialogListener mListener;

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) getActivity();
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(getActivity().toString() + " must implement NoticeDialogListener");
        }
    }

}
