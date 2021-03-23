package vonage.spring

import com.fasterxml.jackson.databind.ObjectMapper
import com.vonage.client.incoming.MessageEvent
import org.apache.commons.logging.LogFactory
import org.springframework.web.servlet.HandlerInterceptor
import java.text.SimpleDateFormat
import java.util.stream.Collectors
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

const val JSON_CONTENT_TYPE = "application/json"

class VonageWebhookHandler<T : Any>(private val delegate:VonageWebhookDelegate<T>, val c: Class<T>, val invokeNextHandler: Boolean) : HandlerInterceptor{
    private val LOG = LogFactory.getLog(this::class.java)
    private val dateFormatMap = hashMapOf<Class<out Any>, String>(
            MessageEvent::class.java to "yyyy-MM-dd HH:mm:ss"
    )
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {

        var proceedToNextHandler = delegate.proceedToNextHandlerOnSuccessfulParsing()
        val om = ObjectMapper()
        if(dateFormatMap.containsKey(c)){
            om.dateFormat = SimpleDateFormat(dateFormatMap[c])
        }
        var obj: T
        try{
            if(request.contentType.contains(JSON_CONTENT_TYPE)){

                obj = om.readValue(request.reader.lines().collect(Collectors.joining()), this.c)
            }
            else{
                val params = HashMap<String,String>()
                for(entry in request.parameterMap){
                    params[entry.key] = entry.value.first()
                }
                obj = om.convertValue(params,c)
            }

            response.status = HttpServletResponse.SC_OK
            delegate.handleWebhook(obj, request, response)
        }
        catch(e: Exception){
            LOG.error(e)
            // set the status to 400 as we've had a problem parsing the request,
            // whether we move onto to the next handler in the pipeline is up to the user
            response.status = HttpServletResponse.SC_BAD_REQUEST
            proceedToNextHandler = delegate.proceedToNextHandlerOnFailedParsing()
        }
        return proceedToNextHandler
    }
}