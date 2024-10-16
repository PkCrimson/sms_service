package com.side.auth.utils;

import com.aliyun.dysmsapi20180501.Client;
import com.aliyun.dysmsapi20180501.models.SendMessageWithTemplateRequest;
import com.aliyun.dysmsapi20180501.models.SendMessageWithTemplateResponse;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teaopenapi.models.OpenApiRequest;
import com.aliyun.teaopenapi.models.Params;
import com.aliyun.teautil.models.RuntimeOptions;
import com.google.gson.Gson;
import com.side.auth.entities.SmsTemplate;
import com.side.auth.entities.SmsParam;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.aliyun.openapiutil.Client.query;

@Component
public class AliyunSMSUtil {

    private static final String ACCESS_KEY_ID = "your_access_key_id";
    private static final String ACCESS_KEY_SECRET = "your_access_key_secret";

    // Registered Sender sign required if you are using the sms service in CN
    private static final String ALIYUN_SENDER_SIGN = "your_aliyun_sender_sign";
    // Registered Template code required if you are using the sms service in CN
    private static final String TEMPLATE_CODE = "your_template_code";

    /**
     * Initialize the Client with the AccessKey of the account
     * @param accessKeyId
     * @param accessKeySecret
     * @return Client
     * @throws Exception
     */
    private static Client createClient(String accessKeyId, String accessKeySecret) throws Exception {
        Config config = new Config()
                // Required, your AccessKey ID
                .setAccessKeyId(accessKeyId)
                // Required, your AccessKey secret
                .setAccessKeySecret(accessKeySecret);
        // See https://api.alibabacloud.com/product/Dysmsapi.
        config.endpoint = "dysmsapi.ap-southeast-1.aliyuncs.com";
        return new Client(config);
    }

    /**
     * API Info
     * @param
     * @return OpenApi.Params
     */
    private static Params createApiInfo() throws Exception {
        Params params = new Params()
                // API Name
                .setAction("SendMessageToGlobe")
                // API Version
                .setVersion("2018-05-01")
                // Protocol
                .setProtocol("HTTPS")
                // HTTP Method
                .setMethod("POST")
                .setAuthType("AK")
                .setStyle("RPC")
                // API PATH
                .setPathname("/")
                // Request body content format
                .setReqBodyType("json")
                // Response body content format
                .setBodyType("json");
        return params;
    }

    public static void smsSender(SmsParam smsParam) throws Exception {

        // Declare the language to determine which sms service implement,
        // AliYun Sms Service is separated to National area and China area
        SmsTemplate smsTemplate = SmsTemplate.valueOf(smsParam.getLanguage());

        switch (smsTemplate){
            case EN, ZH_HK -> {
                final String text = smsTemplate.getText().replace("#{code}", smsParam.getCode());
                sendSMStoNational(
                        smsParam.getCountryCode()+smsParam.getPhoneNumber(),
                        smsTemplate.getSubject(),
                        text);
            }
            case ZH_CN -> {
                sendSMStoCN(smsParam);
            }
        }
    }



    private static Map<String, ?> sendSMStoNational(String to, String subject, String msg) throws Exception {
        Client client = createClient(ACCESS_KEY_ID, ACCESS_KEY_SECRET);
        Params params = createApiInfo();

        Map<String, Object> queries = new HashMap<>();
        queries.put("To", to);
        queries.put("From", subject);
        queries.put("Message", msg);
        // runtime options
        RuntimeOptions runtime = new RuntimeOptions();
        OpenApiRequest request = new OpenApiRequest()
                .setQuery(query(queries));
        // The return value is of Map type, and three types of data can be obtained from Map: response body, response headers, HTTP status code.
        Map<String, ?> response = client.callApi(params, request, runtime);
        return response;
    }

    private static SendMessageWithTemplateResponse sendSMStoCN(SmsParam smsParam) throws Exception {

        String json = new Gson().toJson(Map.of("code",smsParam.getCode()));

        Client client = createClient(ACCESS_KEY_ID, ACCESS_KEY_SECRET);

        SendMessageWithTemplateRequest sendMessageWithTemplateRequest = new SendMessageWithTemplateRequest()
                .setTo(smsParam.getCountryCode() +smsParam.getPhoneNumber())
                .setFrom(ALIYUN_SENDER_SIGN)
                .setTemplateCode(TEMPLATE_CODE)
                .setTemplateParam(json);

        return client.sendMessageWithTemplateWithOptions(sendMessageWithTemplateRequest, new RuntimeOptions());
    }


}
