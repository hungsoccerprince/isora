package com.isorasoft.phusan.dataaccess;

/**
 * Created by MaiNam on 11/24/2016.
 */


import android.content.Context;

import com.isorasoft.phusan.constants.Constants;
import com.isorasoft.mllibrary.utils.SharedPreferencesUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.exceptions.RealmError;

public class RealmInfo {
    private static final String TAG = RealmInfo.class.getSimpleName();

    public static void backup(Context mContext, Realm realm) {
        try {
            realm.writeCopyTo(new File(mContext.getExternalFilesDir(null) + "default.realm"));
        } catch (RealmError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Realm getRealm(Context mContext) {
        try {
            return Realm.getDefaultInstance();
        } catch (RealmError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
//
//    public byte[] loadkey(Context context) {
//
//
//        byte[] content = new byte[64];
//        try {
//            if (ks == null) {
//                createNewKeys(context);
//            }
//
//            ks = KeyStore.getInstance("AndroidKeyStore");
//            ks.load(null);
//
//            content= ks.getCertificate(ALIAS).getEncoded();
//            Log.e(TAG, "key original :" + Arrays.toString(content));
//        } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//        content = Arrays.copyOfRange(content, 0, 64);
//        return content;
//    }
//
//    public void createNewKeys(Context context) throws KeyStoreException {
//
//        firstloadKeyStore();
//        try {
//            // Create new key if needed
//            if (!ks.containsAlias(ALIAS)) {
//                Calendar start = Calendar.getInstance();
//                Calendar end = Calendar.getInstance();
//                end.add(Calendar.YEAR, 1);
//                KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context)
//                        .setAlias(ALIAS)
//                        .setSubject(new X500Principal("CN=Sample Name, O=Android Authority"))
//                        .setSerialNumber(BigInteger.ONE)
//                        .setStartDate(start.getTime())
//                        .setEndDate(end.getTime())
//                        .build();
//                KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
//                generator.initialize(spec);
//
//                KeyPair keyPair = generator.generateKeyPair();
//                Log.e("TAG, "key :" + keyPair.getPrivate().getEncoded().toString());
//
//            }
//        } catch (Exception e) {
//            Log.e(TAG, Log.getStackTraceString(e));
//        }
//    }

    public static void closeRealm(Realm realm) {
        try {
            if (realm != null && !realm.isClosed())
                realm.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void copyBundledRealmFile(Context context, InputStream inputStream, String outFileName) {
        FileOutputStream outputStream = null;
        try {
            String currentDbName = SharedPreferencesUtil.getString(context, Constants.CURRENT_DATABASE, "");
            if (currentDbName.equals(outFileName)) {
                return;
            }

            RealmInfo.deleCurrentVersion(context, currentDbName);

            File file = new File(context.getFilesDir(), outFileName);
            try {
                outputStream = new FileOutputStream(file);
                byte[] buf = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buf)) > 0) {
                    outputStream.write(buf, 0, bytesRead);
                }
                SharedPreferencesUtil.setSharedPreferences(context, SharedPreferencesUtil.EnumType.String, Constants.CURRENT_DATABASE, outFileName);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RealmError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void deleCurrentVersion(Context context, String currentName) {
        try {
            RealmConfiguration config = new RealmConfiguration.Builder()
                    .name(currentName).deleteRealmIfMigrationNeeded().build();
            Realm.deleteRealm(config);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (RealmError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }
    }
}