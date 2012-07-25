package com.snow.fbtest.web;

import com.britesnow.snow.util.JsonUtil;
import com.britesnow.snow.web.RequestContext;
import com.britesnow.snow.web.handler.annotation.WebActionHandler;
import com.britesnow.snow.web.handler.annotation.WebModelHandler;
import com.britesnow.snow.web.param.annotation.WebModel;
import com.google.inject.Singleton;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

@Singleton
public class FaceBookCreditsCallbackWebHandler {
    @WebActionHandler
    protected void faceBookCallback(@WebModel Map model, RequestContext rc) throws ServletException, IOException {
        //facebook code
        HttpServletRequest request = rc.getReq();
        String appId = "xxxxxxx";
        String secretKey = "xxxxxxxxxx";
        String errorReason = request.getParameter("error_reason");
        String signedRequest = request.getParameter("signed_request");
        String responseType = request.getParameter("response_type");      /*
        * Parse the signed_request to verify it's from Facebook     */
        Map requestMap = parseSignedRequest(signedRequest, secretKey);
        // Grab values passed to this callback
        String method = request.getParameter("method");
        String order_id = request.getParameter("order_id");
        JSONObject item = new JSONObject();
        JSONObject returnData = new JSONObject();
        JSONArray itemArray = new JSONArray();
        String returnvalue = "";
        if (requestMap == null) {
            // Handle an unauthenticated request here
            System.out.println("ERROR: Handle an unauthenticated request here");
        }
        if (method.equalsIgnoreCase("payments_status_update")) {
            //grab the order status
            String nextState = "";
            String status = request.getParameter("status");
            String orderId = request.getParameter("order_id");

            Map userjson = null;
            try {
                userjson = (Map) JsonUtil.toMapAndList(request.getParameter("order_details"));
            } catch (Exception pe) {
            }
            // Write your apps logic here for validating and recording a
            // purchase here.
            // Generally you will want to move states from `placed` -> `settled`
            // // here, then grant the purchasing user's in-game item to them.
            if (status.equalsIgnoreCase("placed")) {
                nextState = "settled";
                item.put("status", nextState);
                item.put("order_id", orderId);
                //display date or add code to insert into a database
                JSONArray itemsArray = (JSONArray) userjson.get("items");
                for (int i = 0; i < itemsArray.size(); i++) {
                    JSONObject itemObj = (JSONObject) itemsArray.get(i);
                    System.out.println("item[" + i + "]= Buyer(" + userjson.get("buyer") + ") purchased Qty(" + userjson.get("amount") + ") " + itemObj.get("title") + " @ $" + itemObj.get("price"));
                }
            }
            // Compose returning data
            returnData.put("content", item);
            returnData.put("method", "payments_status_update");

        } else if (method.equalsIgnoreCase("payments_get_items")) {
            String item_info = request.getParameter("order_info");
            //remove escape characters
            item_info = item_info.replaceAll("\"", "");
            if (item_info.equalsIgnoreCase("abc123")) {
                // Per the credits api documentation, you should pass in an item
                // reference and then query your internal DB for the proper
                // information. Then set the item information here to be
                // returned to facebook then shown to the user for confirmation.
                item.put("title", "BFF Locket");
                item.put("price", 1);
                item.put("description", "This is a BFF Locket...");
                item.put("image_url", "http://www.facebook.com/images/gifts/21.png");
                item.put("product_url", "http://www.facebook.com/images/gifts/21.png");
            } else {              // For the sake of the sample, we will default to this item if
                // the `order_info` reference passed from your JS call is not matched
                // above.
                item.put("title", "A Facebook Hat");
                item.put("price", 1);
                item.put("description", "The coolest hat you\'ve ever seen.");
                item.put("image_url", "http://www.facebook.com/images/gifts/740.png");
                item.put("product_url", "http://www.facebook.com/images/gifts/740.png");
            }
            itemArray.add(item);
            returnData.put("content", itemArray);
            returnData.put("method", "payments_get_items");
        }
        //return output data
        model.put("_jsonData", returnData);
    }


    //decode input string to base 64
    private byte[] base64UrlDecode(String input) {
        return new Base64(true).decode(input.replace("-", "+").replace("_", "/").trim());
    }

    private String base64UrlEncode(byte[] input) {
        Base64 encoder = new Base64();
        String encodedInput = "";
        try {
            encodedInput = encoder.encodeBase64URLSafeString(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encodedInput;
    }     /*     * http://javaboutique.internet.com/tutorials/InitForms/special.html     */

    private String replace(String s, String one, String another) {
        // In a string replace one substring with another
        if (s.equals("")) return "";
        String res = "";
        int i = s.indexOf(one, 0);
        int lastpos = 0;
        while (i != -1) {
            res += s.substring(lastpos, i) + another;
            lastpos = i + one.length();
            i = s.indexOf(one, lastpos);
        }
        res += s.substring(lastpos);
        // the rest
        return res;
    }

    private Map parseSignedRequest(String signedRequest, String secretKey) {
        Map data = null;
        if (signedRequest != null) {
            String[] split = signedRequest.split("\\.", 2);
            //Get signature and payload data portions of signed request string
            String encoded_sig = split[0];
            String payload = split[1];
            //parse json object
            try {
                data = (Map) JsonUtil.toMapAndList(new String(base64UrlDecode(payload)));
            } catch (Exception e) {
                e.printStackTrace();
            }
            String algorithm = (String) data.get("algorithm");
            String userID = ((String) data.get("user_id"));
            String authToken = ((String) data.get("oauth_token"));
            String signature = "";
            String expectedSignature = "";
            if (!algorithm.equalsIgnoreCase("HMAC-SHA256")) {
                System.out.println("ERROR: unknown algorithm");
                return null;
            }
            byte[] sig = base64UrlDecode(encoded_sig);
            try {
                //Decode
                Mac mac = Mac.getInstance("HmacSHA256");
                SecretKeySpec key = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
                mac.init(key);
                byte[] expectedSig = mac.doFinal(payload.getBytes("UTF-8"));
                signature = base64UrlEncode(sig);
                expectedSignature = base64UrlEncode(expectedSig);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!signature.equalsIgnoreCase("")) {
                if (!signature.equalsIgnoreCase(expectedSignature)) {
                    System.out.println("ERROR: Bad signed JSON signature");
                    return null;
                }
            }
        }//end if
        return data;
    }

    @WebModelHandler(startsWith = "facebook-credits")
    public void buyItems(@WebModel Map model, RequestContext rc) {
        String orderId = rc.getReq().getParameter("order_id");
        String status = rc.getReq().getParameter("status");
        String errorCode = rc.getReq().getParameter("error_code");
        String errorMessage = rc.getReq().getParameter("error_message");
        String html;
        if (orderId != null && status != null) {

            html = String.format("Transaction Completed! </br></br> " +
                    "Data returned from Facebook: </br>") +
                    "<b>Order ID: </b>" + orderId + "</br>" +
                    "<b>Status: </b>" + status;

        } else if (errorCode != null && errorMessage != null) {
            html = "Transaction Failed! </br></br>" +
                    "Error message returned from Facebook:</br>" + errorCode + ":" + errorMessage;
        }
    }
}
