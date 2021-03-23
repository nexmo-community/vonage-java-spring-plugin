package vonage.spring
import com.vonage.client.auth.RequestSigning
import com.vonage.client.auth.hashutils.HashUtil
import org.apache.http.NameValuePair
import org.apache.http.message.BasicNameValuePair
import org.junit.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import java.time.Instant
import java.util.HashMap
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class VerifiedSmsHandlerTest {

    private fun constructDummyRequest() : HttpServletRequest
    {
        val request = MockHttpServletRequest()
        val params = HashMap<String, Array<String>>()
        val nameValuePair = ArrayList<NameValuePair>(3)
        params.put("a", Array(1){"alphabet"})
        params.put("b", Array(1){"bananas"})
        params.put("timestamp", Array(1){ Instant.now().epochSecond.toString()})
        nameValuePair.add(BasicNameValuePair("a","alphabet"))
        nameValuePair.add(BasicNameValuePair("b","bananas"))
        RequestSigning.constructSignatureForRequestParameters(nameValuePair,"abcde")
        for(p in nameValuePair){
            request.addParameter(p.name,p.value)
        }
        return request
    }

    @Test
    fun testVerifiedSms(){
        val handler = VerifiedSmsHandler("abcde", HashUtil.HashType.MD5)
        val request = constructDummyRequest()
        val response = MockHttpServletResponse()
        val result = handler.preHandle(request,response, handler)
        assertTrue { result }
        assertEquals(HttpServletResponse.SC_ACCEPTED, response.status)
    }

    @Test
    fun testFailedVerifySms(){
        val handler = VerifiedSmsHandler("incorrectSecret", HashUtil.HashType.MD5)
        val request = constructDummyRequest()
        val response = MockHttpServletResponse()
        val result = handler.preHandle(request,response, handler)
        assertFalse { result }
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.status)
    }
}