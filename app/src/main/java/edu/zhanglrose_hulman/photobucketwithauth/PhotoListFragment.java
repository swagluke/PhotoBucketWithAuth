package edu.zhanglrose_hulman.photobucketwithauth;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * Created by lukezhang on 7/20/16.
 */
public class PhotoListFragment extends Fragment {

    private OnLogoutListener mListener;
    private Callback mCallback;
    public PhotoAdapter mPhotoAdapter;

    public PhotoListFragment()
    {
        // Need to have the empty one.
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /*View rootView = inflater.inflate(R.layout.fragment_password, container, false);
        // Setup Toolbar
        Toolbar mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.app_name);
        getActivity().getMenuInflater().inflate(R.menu.main, mToolbar.getMenu());
        mToolbar.setOnMenuItemClickListener(this);
        mToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        final View fab = rootView.findViewById(R.id.fab_add);
        fab.setOnClickListener(this);
        */
        RecyclerView view = (RecyclerView)inflater.inflate(R.layout.fragment_pic_list, container, false);
        //Toolbar mToolbar = (Toolbar) view.findViewById(R.id.toolbar);

        view.setLayoutManager(new LinearLayoutManager(getContext()));

        final View fab = view.findViewById(R.id.fab_add);
        //FloatingActionButton fab = (view.findViewById(R.id.fab_add));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddEditDeleteDialog(null);
            }
        });

        mPhotoAdapter = new PhotoAdapter(mCallback, getContext(), this);
        view.setAdapter(mPhotoAdapter);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Callback) {
            mCallback = (Callback) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement PhotoListFragment.Callback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    public interface Callback {
        void onDisplay(Photo weatherpic);
    }

    public void showAddEditDeleteDialog(final Photo photo) {
        DialogFragment df = new DialogFragment() {
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getString(photo == null ? R.string.dialog_add_title : R.string.dialog_edit_title));
                View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_insert, null, false);
                builder.setView(view);
                final EditText captionEditText = (EditText) view.findViewById(R.id.dialog_add_caption_text);
                final EditText urlEditText = (EditText) view.findViewById(R.id.dialog_add_url_text);
                if (photo != null) {
                    // pre-populate
                    captionEditText.setText(photo.getCaption());
                    urlEditText.setText(photo.getUrl());

                    TextWatcher textWatcher = new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            // empty
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            // empty
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            String caption = captionEditText.getText().toString();
                            String url = urlEditText.getText().toString();
                            mPhotoAdapter.update(photo, caption, url);
                        }

                    };

                    captionEditText.addTextChangedListener(textWatcher);
                    urlEditText.addTextChangedListener(textWatcher);

                    builder.setNeutralButton("DELETE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mPhotoAdapter.remove(photo);
                        }
                    });
                }
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String caption = "";
                        String url = "";
                        if (photo == null) {
                            caption = captionEditText.getText().toString();
                            url = urlEditText.getText().toString();
                            if (url.equals("")) {
                                url = Util.randomImageUrl();
                                mPhotoAdapter.add(new Photo(caption, url));
                            }
                        }

                    }
                });
                builder.setNegativeButton(android.R.string.cancel, null);

                return builder.create();
            }
        };
        df.show(getActivity().getSupportFragmentManager(), "add");
    }

    public interface OnLogoutListener {
        void onLogout();
    }
}
