package ericz.motivation;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewGoalFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewGoalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewGoalFragment extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private String mParam1;
    private String mParam2;
    private FloatingActionButton button;
    private OnFragmentInteractionListener mListener;

    public NewGoalFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static Fragment newInstance() {
        NewGoalFragment fragment = new NewGoalFragment();
        return (Fragment) fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_new_goal, container, false);

        Button okButton = view.findViewById(R.id.okButton);
        Button cancelButton = view.findViewById(R.id.cancelButton);




        okButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getFragmentManager().beginTransaction().remove(NewGoalFragment.this).commit();
                NewGoalFragment.this.button = view.findViewById(R.id.floatingActionButton);

            }
        });

        return view;


    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
