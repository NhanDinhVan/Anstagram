//package Utils;
//
//import com.twilio.Twilio;
//import com.twilio.rest.api.v2010.account.Message;
//
//public class SmsSender {
//    public static final String ACCOUNT_SID = "your_account_sid";
//    public static final String AUTH_TOKEN = "your_auth_token";
//
//    static {
//        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
//    }
//
//    public static void sendOtpSms(String phoneNumber, String otp) {
//        Message message = Message.creator(
//                new com.twilio.type.PhoneNumber(phoneNumber),
//                new com.twilio.type.PhoneNumber("your_twilio_number"),
//                "Your OTP is: " + otp)
//            .create();
//
//        System.out.println("SMS sent to: " + phoneNumber);
//    }
//}
