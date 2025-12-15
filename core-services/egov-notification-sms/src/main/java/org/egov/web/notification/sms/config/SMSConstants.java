package org.egov.web.notification.sms.config;

import org.springframework.stereotype.Component;

@Component
public class SMSConstants {

    public static final String SENDER_PASSWORD_IDENTIFIER = "senderPwdId";
    public static final String SENDER_USERNAME_IDENTIFIER = "senderUsernameId";
    public static final String SENDER_SENDERID_IDENTIFIER = "senderIdentifier";
    public static final String SENDER_SECUREKEY_IDENTIFIER = "senderSecureKeyId";

    public static final String SENDER_MESSAGE_IDENTIFIER = "senderMessageId";
    public static final String SENDER_MOBNO_IDENTIFIER = "senderMobileNoId";
    
    public static final String REGISTERD_CONTENT = "Dear+User%2C+You+have+successfully+registered+%26+logged+in+to+the+OBPAS+portal+%28UPYOG+-+Assam%29.+Please+proceed+to+fill+the+Common+Application+Form.";


}