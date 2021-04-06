package vonage.spring

import com.vonage.client.incoming.CallDirection
import com.vonage.client.incoming.CallStatus
import com.vonage.client.incoming.MessageEvent
import com.vonage.client.sms.messages.Message
import com.vonage.client.incoming.CallEvent
import org.junit.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class VonageWebhookHandlerTest {
    class SampleDelegate : VonageWebhookDelegate<MessageEvent>() {
        override fun handleWebhook(
            msg: MessageEvent,
            request: HttpServletRequest,
            response: HttpServletResponse
        ): Boolean {
            assertEquals("12018675309", msg.msisdn)
            assertEquals("19738675309", msg.to)
            assertEquals("Testing get type", msg.text)
            assertEquals("17000002A9E9F4E0", msg.messageId)
            assertEquals("TESTING",msg.keyword)
            assertEquals(SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2021-03-18 19:04:59"), msg.messageTimestamp)
            assertEquals("1616094479", msg.timestamp)
            assertEquals("231a9c15-6732-4ea7-81d1-3e564f3a89b2", msg.nonce)
            return true
        }

        override fun proceedToNextHandlerOnFailedParsing(): Boolean {
            return false
        }

    }

    class SampleDelegateISO8601 : VonageWebhookDelegate<CallEvent>() {
        override fun handleWebhook(
            evt: CallEvent,
            request: HttpServletRequest,
            response: HttpServletResponse
        ): Boolean {
            assertEquals("12018675309", evt.from)
            assertEquals("19738675309", evt.to)
            assertEquals("9aa6161d22317b86256ecac63a31c2a9", evt.uuid)
            assertEquals(Date.from(Instant.parse("2021-03-18T21:00:07.036Z")), evt.timestamp)
            assertEquals<CallStatus>(CallStatus.fromString("completed"), evt.status)
            assertEquals("CON-5f6293a3-bbb0-4886-b591-b5ba75c04755", evt.conversationUuid)
            assertEquals(CallDirection.fromString("inbound"), evt.direction)
            return true
        }

        override fun proceedToNextHandlerOnFailedParsing(): Boolean {
            return false
        }

    }
    @Test
    fun testJsonParsing(){
        // Arrange
        val response = MockHttpServletResponse()

        val handler = VonageWebhookHandler<MessageEvent>(SampleDelegate(), MessageEvent::class.java, true)
        val json: String = "{\n" +
                "    \"msisdn\": \"12018675309\",\n" +
                "    \"to\": \"19738675309\",\n" +
                "    \"messageId\": \"17000002A9E9F4E0\",\n" +
                "    \"text\": \"Testing get type\",\n" +
                "    \"type\": \"text\",\n" +
                "    \"keyword\": \"TESTING\",\n" +
                "    \"message-timestamp\": \"2021-03-18 19:04:59\",\n" +
                "    \"timestamp\": \"1616094479\",\n" +
                "    \"nonce\": \"231a9c15-6732-4ea7-81d1-3e564f3a89b2\"\n" +
                "}"
        val request = MockHttpServletRequest().apply{
            contentType = "application/json"
            setContent(json.toByteArray())
        }

        //act
        val result = handler.preHandle(request, response, handler)

        //assert

        assertTrue { result }
        assertEquals(HttpServletResponse.SC_OK, response.status)
    }

    @Test
    fun testParameterParsing(){
        //arrange
        val request = MockHttpServletRequest().apply{
            addParameter("msisdn","12018675309")
            addParameter("to","19738675309")
            addParameter("text", "Testing get type")
            addParameter("messageId","17000002A9E9F4E0")
            addParameter("keyword","TESTING")
            addParameter("message-timestamp","2021-03-18 19:04:59")
            addParameter("timestamp","1616094479")
            addParameter("nonce","231a9c15-6732-4ea7-81d1-3e564f3a89b2")
            contentType = "application/www-x-form-urlencoded"
        }
        val handler = VonageWebhookHandler<MessageEvent>(SampleDelegate(), MessageEvent::class.java, true)
        val response = MockHttpServletResponse()
        //act
        val result = handler.preHandle(request,response, handler)
        //assert
        assertTrue { result }
        assertEquals(HttpServletResponse.SC_OK, response.status)
    }

    @Test
    fun testParameterFailedParsing(){
        val request = MockHttpServletRequest().apply {
            contentType = "application/www-x-form-urlencoded"
            addParameter("msisdn","12018675309")
            addParameter("to","19738675309")
            addParameter("text", "Testing get type")
            addParameter("messageId","17000002A9E9F4E0")
            addParameter("keyword","TESTING")
            addParameter("message-timestamp","malformed-timestamp")
            addParameter("timestamp","1616094479")
            addParameter("nonce","231a9c15-6732-4ea7-81d1-3e564f3a89b2")
        }
        val response = MockHttpServletResponse()
        val handler = VonageWebhookHandler<MessageEvent>(SampleDelegate(), MessageEvent::class.java, true)

        //act
        val result = handler.preHandle(request,response,handler)

        //assert
        assertFalse { result }
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.status)
    }

    @Test
    fun testParametersWithISO8601Timestamp(){

        val response = MockHttpServletResponse()
        val handler = VonageWebhookHandler<CallEvent>(SampleDelegateISO8601(), CallEvent::class.java, true)
        val json: String = "{\n" +
                "    \"headers\": {},\n" +
                "    \"uuid\": \"9aa6161d22317b86256ecac63a31c2a9\",\n" +
                "    \"network\": \"US-VIRTUAL-BANDWIDTH\",\n" +
                "    \"duration\": \"0\",\n" +
                "    \"start_time\": null,\n" +
                "    \"rate\": \"0.00450000\",\n" +
                "    \"price\": \"0.00000000\",\n" +
                "    \"from\": \"12018675309\",\n" +
                "    \"to\": \"19738675309\",\n" +
                "    \"conversation_uuid\": \"CON-5f6293a3-bbb0-4886-b591-b5ba75c04755\",\n" +
                "    \"status\": \"completed\",\n" +
                "    \"direction\": \"inbound\",\n" +
                "    \"timestamp\": \"2021-03-18T21:00:07.036Z\"\n" +
                "}"
        val request = MockHttpServletRequest().apply {
            contentType = "application/json"
            setContent(json.toByteArray())
        }

        val result = handler.preHandle(request,response,handler)
        assertTrue { result }
        assertEquals(HttpServletResponse.SC_OK, response.status)
    }
}