package com.isorasoft.phusan.activity.main;

import android.app.Dialog;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.isorasoft.mllibrary.adapter.BaseRecyclerAdapter;
import com.isorasoft.mllibrary.utils.DialogUtils;
import com.isorasoft.phusan.BaseActivity;
import com.isorasoft.phusan.R;
import com.isorasoft.phusan.dataaccess.AnalyticInfo;
import com.isorasoft.phusan.model.Contact;
import com.isorasoft.phusan.model.FirstNumber;

import java.io.Serializable;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by xuanhung on 6/3/18.
 */

public class ListContactActivity extends BaseActivity {

    BaseRecyclerAdapter<ViewHolder, Contact> adapter;
    ArrayList<Contact> listContact = new ArrayList<>();
    ArrayList<FirstNumber> listFirstNumber = new ArrayList<>();

    ArrayList<String> listOld = new ArrayList<>();
    ArrayList<String> listNew = new ArrayList<>();


    @BindView(R.id.rvListContact)
    RecyclerView rvListContact;

    @BindView(R.id.btnUpdateAll)
    Button btnUpdateAll;

    @OnClick(R.id.btnUpdateAll)
    void onClick(View v){
        v.getParent().requestDisallowInterceptTouchEvent(true);
        View view1 = getLayoutInflater().inflate(R.layout.dialog_confirm_base, null);
        TextView tvTitle = (TextView) view1.findViewById(R.id.tvTitle);
        TextView tvOK = (TextView) view1.findViewById(R.id.tvOK);
        TextView tvNo = (TextView) view1.findViewById(R.id.tvNo);
        tvOK.setText(getString(R.string.common_accept));
        tvNo.setText(getString(R.string.common_cancel));
        String title = getString(R.string.do_you_want_update_all);
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
                dialog.dismiss();
                updateAll();
            }
        });

        tvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_contact);
        ButterKnife.bind(getActivity());

        if(getIntent().getExtras() != null){
            if(getIntent().getExtras().get("ListContact") != null){
                listContact = (ArrayList<Contact>) getIntent().getSerializableExtra("ListContact");
            }
            if(getIntent().getExtras().get("ListFirstNumber") != null){
                listFirstNumber = (ArrayList<FirstNumber>) getIntent().getSerializableExtra("ListFirstNumber");
            }
        }
        else {
            initData();
        }

        rvListContact.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new BaseRecyclerAdapter<ViewHolder, Contact>(R.layout.item_contact, new BaseRecyclerAdapter.BaseViewHolder<ViewHolder, Contact>() {
            @Override
            public ViewHolder getViewHolder(View v) {
                return new ViewHolder(v);
            }

            @Override
            public void bindData(ViewHolder viewHolder, Contact data, int position) {
                viewHolder.tvName.setText(data.getName());
                viewHolder.tvPhoneNumber.setText(data.getPhoneNumber());
            }
        }, listContact, new BaseRecyclerAdapter.OnClickListener() {
            @Override
            public void onClick(View v, int position, Object o) {
                Contact contact = (Contact) o;
                String number = contact.getPhoneNumber();
                for(FirstNumber firstNumber : listFirstNumber){
                    if (number.startsWith(firstNumber.getOldValue())){
                        number = number.replaceFirst(firstNumber.getOldValue(), firstNumber.getNewValue());

                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        View view1 = getLayoutInflater().inflate(R.layout.dialog_confirm_base, null);
                        TextView tvTitle = (TextView) view1.findViewById(R.id.tvTitle);
                        TextView tvOK = (TextView) view1.findViewById(R.id.tvOK);
                        TextView tvNo = (TextView) view1.findViewById(R.id.tvNo);
                        tvOK.setText(getString(R.string.common_accept));
                        tvNo.setText(getString(R.string.common_cancel));
                        String title = getString(R.string.phone_number) + " <font color='red'>" + contact.getPhoneNumber() + "</font> " + getString(R.string.will_update)
                                + " <font color='blue'>" + number + "</font>!";
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
                                dialog.dismiss();
                                update(contact, firstNumber);

                            }
                        });

                        tvNo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        break;
                    }
                }
            }
        });

        adapter.bindData(rvListContact);
    }

    public void updateAll() {

        for(FirstNumber firstNumber : listFirstNumber){
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
            adapter.notifyDataSetChanged();
        }

        Toast.makeText(getActivity(), getString(R.string.update_success), Toast.LENGTH_LONG).show();
    }

    public void update(Contact contact, FirstNumber firstNumber) {
        String number = contact.getPhoneNumber();
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
            adapter.notifyDataSetChanged();
            Toast.makeText(getActivity(), getString(R.string.update_success), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initData() {
        listOld.add("0162"); listNew.add("032");
        listOld.add("0163"); listNew.add("033");
        listOld.add("0164"); listNew.add("034");
        listOld.add("0165"); listNew.add("035");
        listOld.add("0166"); listNew.add("036");
        listOld.add("0167"); listNew.add("037");
        listOld.add("0168"); listNew.add("038");
        listOld.add("0169"); listNew.add("039");

        listOld.add("0120"); listNew.add("070");
        listOld.add("0121"); listNew.add("079");
        listOld.add("0122"); listNew.add("077");
        listOld.add("0126"); listNew.add("076");
        listOld.add("0128"); listNew.add("078");


        listOld.add("0123"); listNew.add("083");
        listOld.add("0124"); listNew.add("084");
        listOld.add("0125"); listNew.add("085");
        listOld.add("0127"); listNew.add("081");
        listOld.add("0129"); listNew.add("082");

        listOld.add("0186"); listNew.add("056");
        listOld.add("0186"); listNew.add("058");

        listOld.add("0199"); listNew.add("059");

        for(int i=0; i<listOld.size(); i++){
            FirstNumber firstNumber = new FirstNumber(listOld.get(i), listNew.get(i));
            listFirstNumber.add(firstNumber);
        }

        getData();
    }

    private void getData() {
        ContentResolver cr = getActivity().getContentResolver();
        Cursor cur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        while(cur.moveToNext()){

            String number = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            if(number == null || number.length() != 11 || number.startsWith("+"))
                continue;
            Contact contact = new Contact();
            contact.setId(cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)));
            contact.setName(cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
            contact.setPhoneNumber(cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));

            String firstNumber = number.substring(0,4);
            if(!listOld.contains(firstNumber))
                continue;

            listContact.add(contact);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tvName)
        TextView tvName;

        @BindView(R.id.tvPhoneNumber)
        TextView tvPhoneNumber;

        public ViewHolder(View v){
            super(v);
            ButterKnife.bind(this, v);
        }
    }


    @Override
    public AnalyticInfo.Screen getScreen() {
        return null;
    }
}
