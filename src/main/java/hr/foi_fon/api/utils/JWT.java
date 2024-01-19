package hr.foi_fon.api.utils;

import com.auth0.jwt.algorithms.Algorithm;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWTCreator;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWT {
    public String generateToken(String email, String userId, String firstName) {
        try {
            Map<String, Object> headerClaims = new HashMap<>();
            Map<String, Object> payloadClaims = new HashMap<>();

            payloadClaims.put("email", email);
            payloadClaims.put("userId", userId);
            payloadClaims.put("firstName", firstName);

            //1 day
            Date expiresAt = new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000);
            payloadClaims.put("expire", expiresAt);
            System.out.println("TEST"+payloadClaims);

            Method initMethod = JWTCreator.class.getDeclaredMethod("init");
            initMethod.setAccessible(true);
            JWTCreator.Builder builder = (JWTCreator.Builder) initMethod.invoke(null);

            builder.withHeader(headerClaims);
            builder.withPayload(payloadClaims);
            String secretKey = "J32DS8923ND78921NJD72430DSNsg42";
            return builder.sign(Algorithm.HMAC256(secretKey));
        } catch (Exception e) {
            return null;
        }
    }
}
