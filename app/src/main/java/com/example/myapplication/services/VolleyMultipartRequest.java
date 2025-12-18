package com.example.myapplication.services;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class VolleyMultipartRequest extends Request<JSONObject> {

    private final String boundary = "volleyBoundary" + System.currentTimeMillis();
    private final Response.Listener<JSONObject> mListener;
    private final Map<String, DataPart> mByteData;
    private Map<String, String> mParams;

    public VolleyMultipartRequest(
            int method,
            String url,
            Map<String, DataPart> byteData,
            Response.Listener<JSONObject> listener,
            Response.ErrorListener errorListener
    ) {
        super(method, url, errorListener);
        this.mListener = listener;
        this.mByteData = byteData;
        this.mParams = new HashMap<>(); // ✅ TEXT PART
    }

    public VolleyMultipartRequest(
            int method,
            String url,
            Map<String, String> params,
            Map<String, DataPart> byteData,
            Response.Listener<JSONObject> listener,
            Response.ErrorListener errorListener
    ) {
        super(method, url, errorListener);
        this.mListener = listener;
        this.mByteData = byteData;
        this.mParams = params;
    }

    @Override
    public String getBodyContentType() {
        return "multipart/form-data; boundary=" + boundary;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            // ===== TEXT PART =====
            if (mParams != null) {
                for (Map.Entry<String, String> entry : mParams.entrySet()) {
                    bos.write(("--" + boundary + "\r\n").getBytes());
                    bos.write(("Content-Disposition: form-data; name=\""
                            + entry.getKey() + "\"\r\n").getBytes());
                    bos.write(("Content-Type: text/plain; charset=UTF-8\r\n\r\n").getBytes());
                    bos.write(entry.getValue().getBytes("UTF-8"));
                    bos.write("\r\n".getBytes());
                }
            }

            // ===== FILE PART =====
            if (mByteData != null) {
                for (Map.Entry<String, DataPart> entry : mByteData.entrySet()) {
                    DataPart dataPart = entry.getValue();
                    bos.write(("--" + boundary + "\r\n").getBytes());
                    bos.write(("Content-Disposition: form-data; name=\""
                            + entry.getKey() + "\"; filename=\""
                            + dataPart.getFileName() + "\"\r\n").getBytes());
                    bos.write(("Content-Type: "
                            + dataPart.getType() + "\r\n\r\n").getBytes());
                    bos.write(dataPart.getContent());
                    bos.write("\r\n".getBytes());
                }
            }

            bos.write(("--" + boundary + "--\r\n").getBytes());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return bos.toByteArray();
    }

    // ✅ CHỈ GIỮ 1 setParams
    public void setParams(Map<String, String> params) {
        this.mParams = params;
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(
                    response.data,
                    HttpHeaderParser.parseCharset(response.headers)
            );
            JSONObject jsonObject = new JSONObject(jsonString);
            return Response.success(jsonObject, HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        mListener.onResponse(response);
    }

    // ===== DATA PART =====
    public static class DataPart {
        private final String fileName;
        private final byte[] content;
        private final String type;

        public DataPart(String fileName, byte[] content, String type) {
            this.fileName = fileName;
            this.content = content;
            this.type = type;
        }

        public String getFileName() {
            return fileName;
        }

        public byte[] getContent() {
            return content;
        }

        public String getType() {
            return type;
        }
    }
}