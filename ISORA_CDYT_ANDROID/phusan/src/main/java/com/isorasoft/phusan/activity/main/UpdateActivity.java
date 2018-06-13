package com.isorasoft.phusan.activity.main;

import android.app.ActionBar;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import com.isorasoft.mllibrary.adapter.BaseViewPagerAdapter;
import com.isorasoft.mllibrary.utils.AndroidPermissionUtils;
import com.isorasoft.mllibrary.utils.ConvertUtils;
import com.isorasoft.phusan.BaseActivity;
import com.isorasoft.phusan.R;
import com.isorasoft.phusan.activity.fragment.UpdateFragment;
import com.isorasoft.phusan.dataaccess.AnalyticInfo;
import com.isorasoft.phusan.model.Contact;
import com.isorasoft.phusan.model.FirstNumber;

import java.io.Serializable;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UpdateActivity extends BaseActivity {

    public static String log = UpdateActivity.class.getSimpleName();

    ContentResolver cr;
    Cursor cur;
    UpdateFragment viettelFragment, mobiFragment, vinaFragment, vnmFragment, gMobileFragment;

    ArrayList<Contact>  contacts = new ArrayList<>();
    ArrayList<FirstNumber> listViettel = new ArrayList<>();
    ArrayList<FirstNumber> listMobi= new ArrayList<>();
    ArrayList<FirstNumber> listVina = new ArrayList<>();
    ArrayList<FirstNumber> listVnm = new ArrayList<>();
    ArrayList<FirstNumber> listGMobile = new ArrayList<>();

    ArrayList<Contact> listContactViettel = new ArrayList<>();
    ArrayList<Contact> listContactMobi = new ArrayList<>();
    ArrayList<Contact> listContactVina = new ArrayList<>();
    ArrayList<Contact> listContactVnm = new ArrayList<>();
    ArrayList<Contact> listContactGMobile = new ArrayList<>();

    ArrayList<String> listViettelOld = new ArrayList<>();
    ArrayList<String> listViettelNew = new ArrayList<>();
    ArrayList<String> listMobiOld = new ArrayList<>();
    ArrayList<String> listMobiNew = new ArrayList<>();
    ArrayList<String> listVinaOld = new ArrayList<>();
    ArrayList<String> listVinaNew = new ArrayList<>();
    ArrayList<String> listVnmOld = new ArrayList<>();
    ArrayList<String> listVnmNew = new ArrayList<>();
    ArrayList<String> listGMobileOld = new ArrayList<>();
    ArrayList<String> listGMobileNew = new ArrayList<>();


    @BindView(R.id.viewPager)
    TabLayout viewPager;

    @BindView(R.id.pager)
    ViewPager mViewPager;

    @OnClick(R.id.btnFinish)
    void back(View v){
        getActivity().finish();
    }

    @Override
    public AnalyticInfo.Screen getScreen() {
        return null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        ButterKnife.bind(getActivity());

        initData();
        requestPermission(new AndroidPermissionUtils.OnCallbackRequestPermission() {

            @Override
            public void onSuccess() {
                getData();
            }

            @Override
            public void onFailed() {
                Log.d(log, "Permission false");
            }
        }, AndroidPermissionUtils.TypePermission.PERMISSION_READ_CONNTACT, AndroidPermissionUtils.TypePermission.PERMISSION_WRITE_CONNTACT);

        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {
            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {

            }
        };

        ArrayList<String> strings = new ArrayList<>();
        strings.add("Viettel");
        strings.add("Mobi fone");
        strings.add("Vina phone");
        strings.add("Vietnam Mobile");
        strings.add("GMobile");

        BaseViewPagerAdapter adapter = new BaseViewPagerAdapter(getActivity(), getSupportFragmentManager(), ConvertUtils.toArrayList(Fragment.class,
                viettelFragment = UpdateFragment.getInstance((Serializable) listViettel, (Serializable) listContactViettel, "Viettel"),
                mobiFragment = UpdateFragment.getInstance((Serializable) listMobi, (Serializable) listContactMobi, "Mobifone"),
                vinaFragment = UpdateFragment.getInstance((Serializable) listVina, (Serializable) listContactVina, "Vinaphone"),
                vnmFragment = UpdateFragment.getInstance((Serializable) listVnm, (Serializable) listContactVnm , "Vietnammobile"),
                gMobileFragment = UpdateFragment.getInstance((Serializable) listGMobile, (Serializable) listContactGMobile, "Gmobile")
                ), strings);

        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(5);

    }

    private void initData() {
        listViettelOld.add("0162"); listViettelNew.add("032");
        listViettelOld.add("0163"); listViettelNew.add("033");
        listViettelOld.add("0164"); listViettelNew.add("034");
        listViettelOld.add("0165"); listViettelNew.add("035");
        listViettelOld.add("0166"); listViettelNew.add("036");
        listViettelOld.add("0167"); listViettelNew.add("037");
        listViettelOld.add("0168"); listViettelNew.add("038");
        listViettelOld.add("0169"); listViettelNew.add("039");

        listMobiOld.add("0120"); listMobiNew.add("070");
        listMobiOld.add("0121"); listMobiNew.add("079");
        listMobiOld.add("0122"); listMobiNew.add("077");
        listMobiOld.add("0126"); listMobiNew.add("076");
        listMobiOld.add("0128"); listMobiNew.add("078");


        listVinaOld.add("0123"); listVinaNew.add("083");
        listVinaOld.add("0124"); listVinaNew.add("084");
        listVinaOld.add("0125"); listVinaNew.add("085");
        listVinaOld.add("0127"); listVinaNew.add("081");
        listVinaOld.add("0129"); listVinaNew.add("082");

        listVnmOld.add("0186"); listVnmNew.add("056");
        listVnmOld.add("0186"); listVnmNew.add("058");

        listGMobileOld.add("0199");
        listGMobileNew.add("059");

        for(int i=0; i<listViettelOld.size(); i++){
            FirstNumber firstNumber = new FirstNumber(listViettelOld.get(i), listViettelNew.get(i));
            listViettel.add(firstNumber);
        }

        for(int i=0; i<listMobiOld.size(); i++){
            FirstNumber firstNumber = new FirstNumber(listMobiOld.get(i), listMobiNew.get(i));
            listMobi.add(firstNumber);
        }

        for(int i=0; i<listVinaOld.size(); i++){
            FirstNumber firstNumber = new FirstNumber(listVinaOld.get(i), listVinaNew.get(i));
            listVina.add(firstNumber);
        }

        for(int i=0; i<listVnmOld.size(); i++){
            FirstNumber firstNumber = new FirstNumber(listVnmOld.get(i), listVnmNew.get(i));
            listVnm.add(firstNumber);
        }

        for(int i=0; i<listGMobileOld.size(); i++){
            FirstNumber firstNumber = new FirstNumber(listGMobileOld.get(i), listGMobileNew.get(i));
            listGMobile.add(firstNumber);
        }

    }

    private void getData() {
        cr = getActivity().getContentResolver();
        cur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        Log.d(log, String.valueOf(cur.getCount()));

        while(cur.moveToNext()){

            String number = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            if(number == null || number.length() != 11 || number.startsWith("+"))
                continue;
            Contact contact = new Contact();
            contact.setId(cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)));
            contact.setName(cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
            contact.setPhoneNumber(cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));

            String firstNumber = number.substring(0,4);

            if(listViettelOld.contains(firstNumber))
                listContactViettel.add(contact);
            else if(listMobiOld.contains(firstNumber))
                listContactMobi.add(contact);
            else if(listVinaOld.contains(firstNumber))
                listContactVina.add(contact);
            else if(listVnmOld.contains(firstNumber))
                listContactVnm.add(contact);
            else if(listGMobileOld.contains(firstNumber))
                listContactGMobile.add(contact);

        }
    }


}
