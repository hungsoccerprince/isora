package com.isorasoft.phusan.activity.fragment;

import android.app.Dialog;
import android.content.ContentProviderOperation;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.isorasoft.mllibrary.adapter.BaseRecyclerAdapter;
import com.isorasoft.mllibrary.utils.DialogUtils;
import com.isorasoft.phusan.R;
import com.isorasoft.phusan.activity.main.ListContactActivity;
import com.isorasoft.phusan.model.Contact;
import com.isorasoft.phusan.model.FirstNumber;

import java.io.Serializable;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by xuanhung on 6/2/18.
 */

public class UpdateFragment extends Fragment {

    BaseRecyclerAdapter<ViewHolder, FirstNumber> adapter;
    ArrayList<FirstNumber> listFirstNumber = new ArrayList<>();
    ArrayList<Contact> listContact = new ArrayList<>();
    String networkName;

    @BindView(R.id.rvListFirstNumber)
    RecyclerView rvListFirstNumber;

    @BindView(R.id.btnUpdateAll)
    Button btnUpdateAll;

    @OnClick(R.id.btnUpdateAll)
    void clickUpdateAll(View v){
        v.getParent().requestDisallowInterceptTouchEvent(true);
        View view1 = getLayoutInflater().inflate(R.layout.dialog_confirm_base, null);
        TextView tvTitle = (TextView) view1.findViewById(R.id.tvTitle);
        TextView tvOK = (TextView) view1.findViewById(R.id.tvOK);
        TextView tvNo = (TextView) view1.findViewById(R.id.tvNo);
        tvOK.setText(getString(R.string.common_accept));
        tvNo.setText(getString(R.string.common_update_now));
        String title = getString(R.string.do_you_want_view_list_contact_all1) + " <font color='blue'>" + networkName + "</font> " + getString(R.string.do_you_want_view_list_contact_all2);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tvTitle.setText(Html.fromHtml(title, Html.FROM_HTML_MODE_COMPACT));
        }else{
            tvTitle.setText(Html.fromHtml(title));
        }

        DialogUtils.Builder builder = new DialogUtils.Builder(getActivity(), view1);

        final Dialog dialog = builder.create();
        dialog.show();

        tvOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ListContactActivity.class);
                intent.putExtra("ListContact", (Serializable) listContact);
                intent.putExtra("ListFirstNumber", (Serializable) listFirstNumber);
                startActivity(intent);
                dialog.dismiss();
            }
        });

        tvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                updateAll(listFirstNumber);
            }
        });
    }

    private static final String log = UpdateFragment.class.getSimpleName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            listFirstNumber = (ArrayList<FirstNumber>) getArguments().getSerializable("ListFirstNumber");
            listContact = (ArrayList<Contact>) getArguments().getSerializable("ListContact");
            networkName = getArguments().getString("NetworkName");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_network, container, false);
        ButterKnife.bind(this, view);
        rvListFirstNumber.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new BaseRecyclerAdapter<ViewHolder, FirstNumber>(R.layout.item_first_number, new BaseRecyclerAdapter.BaseViewHolder<ViewHolder, FirstNumber>() {
            @Override
            public ViewHolder getViewHolder(View v) {
                return new ViewHolder(v);
            }

            @Override
            public void bindData(ViewHolder viewHolder, FirstNumber data, int position) {
                viewHolder.tvOldValue.setText(data.getOldValue());
                viewHolder.tvNewValue.setText(data.getNewValue());
            }
        }, listFirstNumber, new BaseRecyclerAdapter.OnClickListener() {
            @Override
            public void onClick(View v, int position, Object o) {

                FirstNumber firstNumber = (FirstNumber) o;
                v.getParent().requestDisallowInterceptTouchEvent(true);
                View view1 = getLayoutInflater().inflate(R.layout.dialog_confirm_base, null);
                TextView tvTitle = (TextView) view1.findViewById(R.id.tvTitle);
                TextView tvOK = (TextView) view1.findViewById(R.id.tvOK);
                TextView tvNo = (TextView) view1.findViewById(R.id.tvNo);
                tvOK.setText(getString(R.string.common_accept));
                tvNo.setText(getString(R.string.common_update_now));
                String title = getString(R.string.do_you_want_view_list_contact1) + " <font color='red'>" + firstNumber.getOldValue() + "</font> " + getString(R.string.do_you_want_view_list_contact2)
                        + " <font color='#0a7e07'>" + firstNumber.getNewValue() + "</font>. " + getString(R.string.do_you_want_view_list_contact3);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    tvTitle.setText(Html.fromHtml(title, Html.FROM_HTML_MODE_COMPACT));
                }else{
                    tvTitle.setText(Html.fromHtml(title));
                }

                DialogUtils.Builder builder = new DialogUtils.Builder(getActivity(), view1);

                final Dialog dialog = builder.create();
                dialog.show();

                tvOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), ListContactActivity.class);
                        ArrayList<Contact> contacts = new ArrayList<>();
                        for(Contact contact : listContact){
                            if(contact.getPhoneNumber().startsWith(firstNumber.getOldValue()))
                                contacts.add(contact);
                        }
                        intent.putExtra("ListContact", (Serializable) contacts);
                        ArrayList<FirstNumber> firstNumbers = new ArrayList<>();
                        firstNumbers.add(firstNumber);
                        intent.putExtra("ListFirstNumber", (Serializable) firstNumbers);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });

                tvNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        ArrayList<FirstNumber> firstNumbers = new ArrayList<>();
                        firstNumbers.add(firstNumber);
                        updateAll(firstNumbers);
                    }
                });
            }
        });

        adapter.bindData(rvListFirstNumber);

        return view;
    }

    public void updateAll(ArrayList<FirstNumber> firstNumbers) {

        for(FirstNumber firstNumber : firstNumbers){
            for(Contact contact : listContact) {
                String number = contact.getPhoneNumber();
                if (!number.startsWith(firstNumber.getOldValue()))
                    continue;

                number = number.replaceFirst(firstNumber.getOldValue(), firstNumber.getNewValue());

                ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

                // Number
                ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI);
                builder.withSelection(ContactsContract.Data.CONTACT_ID + "=?" + " AND " + ContactsContract.Data.MIMETYPE + "=?" + " AND " + ContactsContract.CommonDataKinds.Organization.TYPE + "=?", new String[]{contact.getId(), ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE, String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)});
                builder.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number);
                ops.add(builder.build());

                try {
                    getActivity().getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                    contact.setPhoneNumber(number);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        Toast.makeText(getActivity(), getString(R.string.update_success), Toast.LENGTH_LONG).show();

    }

    public void update(Contact contact)
    {
        String firstname = contact.getName() + "_" + contact.getId();
        String lastname = "";
        String number = contact.getPhoneNumber() + "_" + contact.getId();
        String photo_uri = "android.resource://com.my.package/drawable/default_photo";

        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        // Name
        ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI);
        builder.withSelection(ContactsContract.Data.CONTACT_ID + "=?" + " AND " + ContactsContract.Data.MIMETYPE + "=?", new String[]{contact.getId(), ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE});
        builder.withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, lastname);
        builder.withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, firstname);
        ops.add(builder.build());

//         Number
        builder = ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI);
        builder.withSelection(ContactsContract.Data.CONTACT_ID + "=?" + " AND " + ContactsContract.Data.MIMETYPE + "=?"+ " AND " + ContactsContract.CommonDataKinds.Organization.TYPE + "=?", new String[]{contact.getId(), ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE, String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)});
        builder.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number);
        ops.add(builder.build());


        // Picture
//        try
//        {
//            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(photo_uri));
//            ByteArrayOutputStream image = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.JPEG , 100, image);
//
//            builder = ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI);
//            builder.withSelection(ContactsContract.Data.CONTACT_ID + "=?" + " AND " + ContactsContract.Data.MIMETYPE + "=?", new String[]{String.valueOf(id), ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE});
//            builder.withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, image.toByteArray());
//            ops.add(builder.build());
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }

        // Update
        try
        {
            getActivity().getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tvOldValue)
        TextView tvOldValue;

        @BindView(R.id.tvNewValue)
        TextView tvNewValue;

        public ViewHolder(View v){
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    public static UpdateFragment getInstance(Serializable listData, Serializable listContact, String networkName){
        UpdateFragment updateFragment = new UpdateFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("ListFirstNumber", listData);
        bundle.putSerializable("ListContact", listContact);
        bundle.putString("NetworkName", networkName);
        updateFragment.setArguments(bundle);

        return updateFragment;
    }
}
