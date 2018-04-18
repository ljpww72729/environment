package com.ww.lp.environment.components.volleymw;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.orhanobut.logger.Logger;
import com.ww.lp.environment.data.BaseResult;
import com.ww.lp.environment.data.ErrorResult;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
/**
 * <p>***********************************************************************
 * <p> Author: lipeng
 * <p> CreateData: 2017-11-24
 * <p> Version: 1.0.9
 * <p> Description: volley多任务下载帮助类
 * <p>
 * <p>***********************************************************************
 */

public class VolleyMultipartRequest<T> extends Request<T> {
    private final String twoHyphens = "--";
    private final String lineEnd = "\r\n";
    private final String boundary = "volley-" + System.currentTimeMillis();

    private Response.Listener<T> mListener;
    private Response.ErrorListener mErrorListener;
    private Map<String, String> mHeaders;
    private Map<String, String> param;
    private Map<String, DataPart> fileParam;
    private final Class<T> clazz;

    /**
     * Default constructor with predefined header and post method.
     *
     * @param url           request destination
     * @param headers       predefined custom header
     * @param listener      on success achieved 200 code from request
     * @param errorListener on error http or library timeout
     */
    public VolleyMultipartRequest(String url, Map<String, String> headers, Class<T> clazz,
                                  Response.Listener<T> listener,
                                  Response.ErrorListener errorListener) {
        super(Method.POST, url, errorListener);
        this.mListener = listener;
        this.mErrorListener = errorListener;
        this.mHeaders = headers;
        this.clazz = clazz;
    }

    public VolleyMultipartRequest(String url, Map<String, String> param, Map<String, DataPart> fileParam, Class<T> clazz, Response.Listener<T> listener,
                                  Response.ErrorListener errorListener) {
        this(url, null, param, fileParam, clazz, listener, errorListener);
    }

    /**
     * 文件上传处理
     *
     * @param url           地址url
     * @param headers       请求头
     * @param param         参数
     * @param fileParam     文件列表
     * @param clazz         响应结果实体类
     * @param listener      成功监听
     * @param errorListener 失败监听
     */
    public VolleyMultipartRequest(String url, Map<String, String> headers, Map<String, String> param, Map<String, DataPart> fileParam, Class<T> clazz, Response.Listener<T> listener,
                                  Response.ErrorListener errorListener) {
        super(Method.POST, url, errorListener);
        this.mHeaders = headers;
        this.param = param;
        this.fileParam = fileParam;
        this.clazz = clazz;
        this.mListener = listener;
        this.mErrorListener = errorListener;
    }

    /**
     * Constructor with option method and default header configuration.
     *
     * @param method        method for now accept POST and GET only
     * @param url           request destination
     * @param listener      on success event handler
     * @param errorListener on error event handler
     */
    public VolleyMultipartRequest(int method, String url, Class<T> clazz,
                                  Response.Listener<T> listener,
                                  Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.mListener = listener;
        this.mErrorListener = errorListener;
        this.clazz = clazz;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return (mHeaders != null) ? mHeaders : super.getHeaders();
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return param;
    }

    @Override
    public String getBodyContentType() {
        return "multipart/form-data;boundary=" + boundary;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        try {
            // populate text payload
            Map<String, String> params = getParams();
            if (params != null && params.size() > 0) {
                textParse(dos, params, getParamsEncoding());
            }

            // populate data byte payload
            Map<String, DataPart> data = getByteData();
            if (data != null && data.size() > 0) {
                dataParse(dos, data);
            }

            // close multipart form data after text and file data
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Custom method handle data payload.
     *
     * @return Map data part label with data byte
     */
    protected Map<String, DataPart> getByteData() throws AuthFailureError {
        return fileParam;
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        Gson gson = new Gson();
        String json = null;
        try {
            json = new String(
                    response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            Logger.json(json);
            BaseResult baseResult = gson.fromJson(json, BaseResult.class);
            if (baseResult.getStatus() == 400){
                ErrorResult errorResult = gson.fromJson(json, ErrorResult.class);
                String message = "未知错误，请稍后重试!";
                if (errorResult.getErrors() != null && errorResult.getErrors().size() > 0){
                    message = errorResult.getErrors().get(0).getMessage();
                }
                return Response.error(new VolleyError(message));
            } else {
                return Response.success(gson.fromJson(json, clazz),
                        HttpHeaderParser.parseCacheHeaders(response));
            }
        } catch (JsonSyntaxException e) {
            // TODO: 16/11/27 此处有待优化，如何更顺滑的体验
            return Response.error(new VolleyError("未知错误，请稍后重试!"));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new VolleyError("未知错误，请稍后重试!"));
        }

    }

    @Override

    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }

    @Override
    public void deliverError(VolleyError error) {
        mErrorListener.onErrorResponse(error);
    }

    /**
     * Parse string map into data output stream by key and value.
     *
     * @param dataOutputStream data output stream handle string parsing
     * @param params           string inputs collection
     * @param encoding         encode the inputs, default UTF-8
     */
    private void textParse(DataOutputStream dataOutputStream, Map<String, String> params, String encoding) throws IOException {
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                buildTextPart(dataOutputStream, entry.getKey(), entry.getValue());
            }
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + encoding, uee);
        }
    }

    /**
     * Parse data into data output stream.
     *
     * @param dataOutputStream data output stream handle file attachment
     * @param data             loop through data
     */
    private void dataParse(DataOutputStream dataOutputStream, Map<String, DataPart> data) throws IOException {
        for (Map.Entry<String, DataPart> entry : data.entrySet()) {
            buildDataPart(dataOutputStream, entry.getValue(), entry.getKey());
        }
    }

    /**
     * Write string data into header and data output stream.
     *
     * @param dataOutputStream data output stream handle string parsing
     * @param parameterName    name of input
     * @param parameterValue   value of input
     */
    private void buildTextPart(DataOutputStream dataOutputStream, String parameterName, String parameterValue) throws IOException {
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" + parameterName + "\"" + lineEnd);
        //dataOutputStream.writeBytes("Content-Type: text/plain; charset=UTF-8" + lineEnd);
        //dataOutputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);
        dataOutputStream.writeBytes(lineEnd);
        dataOutputStream.writeBytes(parameterValue + lineEnd);
    }

    /**
     * Write data file into header and data output stream.
     *
     * @param dataOutputStream data output stream handle data parsing
     * @param dataFile         data byte as DataPart from collection
     * @param inputName        name of data input
     */
    private void buildDataPart(DataOutputStream dataOutputStream, DataPart dataFile, String inputName) throws IOException {
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" +
                inputName + "\"; filename=\"" + dataFile.getFileName() + "\"" + lineEnd);
        if (dataFile.getType() != null && !dataFile.getType().trim().isEmpty()) {
            dataOutputStream.writeBytes("Content-Type: " + dataFile.getType() + lineEnd);
        }
        dataOutputStream.writeBytes(lineEnd);

        ByteArrayInputStream fileInputStream = new ByteArrayInputStream(dataFile.getContent());
        int bytesAvailable = fileInputStream.available();

        int maxBufferSize = 1024 * 1024;
        int bufferSize = Math.min(bytesAvailable, maxBufferSize);
        byte[] buffer = new byte[bufferSize];

        int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

        while (bytesRead > 0) {
            dataOutputStream.write(buffer, 0, bufferSize);
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        }

        dataOutputStream.writeBytes(lineEnd);
    }


}
