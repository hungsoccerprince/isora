package com.isorasoft.phusan.dataaccess;

import com.google.gson.JsonObject;
import com.isorasoft.phusan.constants.Constants;
import com.isorasoft.phusan.constants.ServerConstants;
import com.isorasoft.phusan.constants.TimeoutConstants;
import com.isorasoft.mllibrary.utils.ClientUtils;
import com.isorasoft.mllibrary.utils.ConvertUtils;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Created by MaiNam on 2/28/2017.
 */
public class AppInfo {
    public static JsonObject getAppVersion() throws SocketTimeoutException, UnknownHostException {
        try {
            ClientUtils.DataResponse response = ClientUtils.getDataHeader(RequestFormInfo.getRequestFormNew(), ServerConstants.GET_APP_VERSION + "/1", TimeoutConstants.DEFAULT_TIMEOUT);
            if (response.isOK()) {
                JsonObject jsonObject = ConvertUtils.toJsonObject(response.getBody());
                if(jsonObject.get(ServerConstants.FIELD_CODE) != null && ConvertUtils.toInt(jsonObject.get(ServerConstants.FIELD_CODE))== Constants.CODE_SUCCESS_DETAIL)
                    return ConvertUtils.toJsonObject(jsonObject.get(ServerConstants.FIELD_DATA));
            }

        } catch (SocketTimeoutException e) {
            throw e;
        } catch (UnknownHostException e) {
            throw e;
        } catch (Exception e) {
        }
        return null;

    }

    public static JsonObject getPhoneSupport() throws SocketTimeoutException, UnknownHostException {
        try {
            ClientUtils.DataResponse response = ClientUtils.getDataHeader(RequestFormInfo.getRequestFormNew(), ServerConstants.GET_PHONE_SUPPORT, TimeoutConstants.DEFAULT_TIMEOUT);
            if (response.isOK()) {
                JsonObject jsonObject = ConvertUtils.toJsonObject(response.getBody());
                if(jsonObject.get(ServerConstants.FIELD_CODE) != null && ConvertUtils.toInt(jsonObject.get(ServerConstants.FIELD_CODE))== Constants.CODE_SUCCESS_DETAIL)
                    return ConvertUtils.toJsonObject(jsonObject.get(ServerConstants.FIELD_DATA));
            }

        } catch (SocketTimeoutException e) {
            throw e;
        } catch (UnknownHostException e) {
            throw e;
        } catch (Exception e) {
        }
        return null;

    }

}
