package vonage.spring

import com.fasterxml.jackson.databind.ObjectMapper
import com.vonage.client.auth.RequestSigning
import com.vonage.client.auth.hashutils.HashUtil
import com.vonage.client.incoming.MessageEvent
import org.springframework.web.servlet.HandlerInterceptor
import java.util.stream.Collectors
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Helper class to verify inbound SMS messages prior to forwarding on SMS to your endpoint. Will set status code to
 * 202 ACCEPTED if verification is passed. Otherwise it will set the status code to 401 UNAUTHORIZED and will stop
 * stop the middleware chain.
 */
class VerifiedSmsHandler(val signatureSecret: String, val hashType: HashUtil.HashType) : HandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        var moveToNext = true
        super.preHandle(request,response,handler)
        if(RequestSigning.verifyRequestSignature(request,signatureSecret,hashType)){
            response.status = HttpServletResponse.SC_ACCEPTED
        }
        else{
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            moveToNext = false
        }
        return moveToNext
    }
}