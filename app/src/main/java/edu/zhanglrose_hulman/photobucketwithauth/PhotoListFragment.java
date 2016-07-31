package edu.zhanglrose_hulman.photobucketwithauth;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by lukezhang on 7/20/16.
 */
public class PhotoListFragment extends Fragment implements Toolbar.OnMenuItemClickListener, View.OnClickListener {
    private Callback mCallback;
    public PhotoAdapter mPhotoAdapter;
    private boolean showAll = false;
    private String mUid;

    public PhotoListFragment() {
        // Need to have the empty one.
    }

    public static PhotoListFragment newInstance(String uid) {
        PhotoListFragment fragment = new PhotoListFragment();
        Bundle args = new Bundle();
        args.putString("user id", uid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle argument = getArguments();
        if (argument != null) {
            mUid = argument.getString("user id");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_pic_list, container, false);
        // Setup Toolbar
        Toolbar mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.app_name);
        getActivity().getMenuInflater().inflate(R.menu.main, mToolbar.getMenu());
        mToolbar.setOnMenuItemClickListener(this);
        mToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        final View fab = rootView.findViewById(R.id.fab_add);
        //FloatingActionButton fab = (view.findViewById(R.id.fab_add));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View rootView) {
                showAddEditDeleteDialog(null);
            }
        });
        RecyclerView photolist = (RecyclerView) rootView.findViewById(R.id.photo_list);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        photolist.setLayoutManager(manager);
        mPhotoAdapter = new PhotoAdapter(mCallback, getContext(), this, mUid);
        photolist.setAdapter(mPhotoAdapter);

        return rootView;
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

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        int id = menuItem.getItemId();
        switch (id) {
            case R.id.action_logout:
                Log.d("PK", "LOGOUT Menu Item Clicked!");
                mCallback.log_out();
                return true;
            case R.id.action_toggle_mode:
                if (showAll) {
                    menuItem.setTitle("SHOW ALL");
                } else {
                    menuItem.setTitle("SHOW MINE");
                }
                showAll = !showAll;
                mPhotoAdapter.toggleShowAll(showAll);
        }
        return false;
    }

    public interface Callback {
        void onDisplay(Photo weatherpic);

        void log_out();
    }

    public void showAddEditDeleteDialog(final Photo photo) {
        if (photo!=null && !mUid.equals(photo.getUid())){
            Toast.makeText(getView(). getContext(), "This weatherpic belongs to another user", Toast.LENGTH_SHORT).show();
            return;
        }
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
                        String uid = mUid;
                        if (photo == null) {
                            caption = captionEditText.getText().toString();
                            url = urlEditText.getText().toString();
                            if (url.equals("")) {
                                url = Util.randomImageUrl();
                                mPhotoAdapter.add(new Photo(caption, url,uid));
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
}
