package vonage.spring

import com.vonage.client.incoming.CallDirection
import com.vonage.client.incoming.CallEvent
import com.vonage.client.incoming.CallStatus
import com.vonage.client.incoming.MessageEvent
import org.junit.Test
import org.springframework.core.MethodParameter
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.context.request.ServletWebRequest
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*
import kotlin.test.assertEquals

class VonageArgumentResolverTest {

    fun ExampleFunction(@VonageWebhook msgEvent: MessageEvent){

    }

    fun ExampleFunction2(@VonageWebhook callEvent: CallEvent){

    }

    @Test
    fun testArgumentResolverWithUrlContent(){
        val request = MockHttpServletRequest()
        request.addParameter("msisdn","12018675309")
        request.addParameter("to","19738675309")
        request.addParameter("text", "Testing get type")
        request.addParameter("messageId","17000002A9E9F4E0")
        request.addParameter("keyword","TESTING")
        request.addParameter("message-timestamp","2021-03-18 19:04:59")
        request.addParameter("timestamp","1616094479")
        request.addParameter("nonce","231a9c15-6732-4ea7-81d1-3e564f3a89b2")
        val method = VonageArgumentResolverTest::class.java.declaredMethods.find { m->m.name == "ExampleFunction" }
        val methodParam = MethodParameter(method,0)
        val resolver = VonageArgumentResolver()
        val nwr = ServletWebRequest(request)
        val msg = resolver.resolveArgument(methodParam, null,nwr,null) as MessageEvent
        assertEquals("12018675309", msg.msisdn)
        assertEquals("19738675309", msg.to)
        assertEquals("Testing get type", msg.text)
        assertEquals("17000002A9E9F4E0", msg.messageId)
        assertEquals("TESTING",msg.keyword)
        assertEquals(SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2021-03-18 19:04:59"), msg.messageTimestamp)
        assertEquals("1616094479", msg.timestamp)
        assertEquals("231a9c15-6732-4ea7-81d1-3e564f3a89b2", msg.nonce)
    }

    @Test
    fun testArgumentResolverWith(){
        val request = MockHttpServletRequest()
        request.contentType="application/json"
        val json: String = "{\n" +
        "    \"msisdn\": \"12018675309\",\n" +
                "    \"to\": \"19738675309\",\n" +
                "    \"messageId\": \"17000002A9E9F4E0\",\n" +
                "    \"text\": \"Testing get type\",\n" +
                "    \"type\": \"text\",\n" +
                "    \"keyword\": \"TESTING\",\n" +
                "    \"message-timestamp\": \"2021-03-18 19:04:59\",\n" +
                "    \"timestamp\": \"1616094012\",\n" +
                "    \"nonce\": \"231a9c15-6732-4ea7-81d1-3e564f3a89b2\"\n" +
                "}"
        request.setContent(json.toByteArray())
        val method = VonageArgumentResolverTest::class.java.declaredMethods.find { m->m.name == "ExampleFunction" }
        val methodParam = MethodParameter(method,0)
        val resolver = VonageArgumentResolver()
        val nwr = ServletWebRequest(request)
        val msg = resolver.resolveArgument(methodParam, null,nwr,null) as MessageEvent
        assertEquals("12018675309", msg.msisdn)
        assertEquals("19738675309", msg.to)
        assertEquals("Testing get type", msg.text)
        assertEquals("17000002A9E9F4E0", msg.messageId)
        assertEquals("TESTING",msg.keyword)
        assertEquals(SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2021-03-18 19:04:59"), msg.messageTimestamp)
        assertEquals("1616094012", msg.timestamp)
        assertEquals("231a9c15-6732-4ea7-81d1-3e564f3a89b2", msg.nonce)
    }

    @Test
    fun testArgumentResolveWithISO8601DateType(){
        val request = MockHttpServletRequest()
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
        request.contentType="application/json"
        System.out.println(this)
        request.setContent(json.toByteArray())
        val method = VonageArgumentResolverTest::class.java.declaredMethods.find { m->m.name == "ExampleFunction2" }
        val methodParam = MethodParameter(method,0)
        val resolver = VonageArgumentResolver()
        val nwr = ServletWebRequest(request)
        val evt = resolver.resolveArgument(methodParam, null,nwr,null) as CallEvent
        assertEquals("12018675309", evt.from)
        assertEquals("19738675309", evt.to)
        assertEquals("9aa6161d22317b86256ecac63a31c2a9", evt.uuid)
        assertEquals(Date.from(Instant.parse("2021-03-18T21:00:07.036Z")), evt.timestamp)
        assertEquals<CallStatus>(CallStatus.fromString("completed"), evt.status)
        assertEquals("CON-5f6293a3-bbb0-4886-b591-b5ba75c04755", evt.conversationUuid)
        assertEquals(CallDirection.fromString("inbound"), evt.direction)
    }
}