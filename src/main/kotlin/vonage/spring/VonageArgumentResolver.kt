package vonage.spring

import com.fasterxml.jackson.databind.ObjectMapper
import com.vonage.client.incoming.MessageEvent
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import java.text.SimpleDateFormat
import java.util.stream.Collectors
import javax.servlet.http.HttpServletRequest

class VonageArgumentResolver : HandlerMethodArgumentResolver {
    private val dateFormatMap = hashMapOf<Class<out Any>, String>(
        MessageEvent::class.java to "yyyy-MM-dd HH:mm:ss"
    )
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(VonageWebhook::class.java)
    }

    /**
     * Resolves arguments to a method along the lines of how we'd expect Vonage Webhooks to be parsed.
     */
    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any? {
        val om = ObjectMapper()
        val request = webRequest.getNativeRequest(HttpServletRequest::class.java)
        val c = parameter.parameterType
        if(dateFormatMap.containsKey(c)){
            om.dateFormat = SimpleDateFormat(dateFormatMap[c])
        }
        val obj: Any
        if(request.contentType != null && request.contentType.contains(JSON_CONTENT_TYPE)){

            obj = om.readValue(request.reader.lines().collect(Collectors.joining()), c)
        }
        else{
            val params = request.parameterMap.map{it.key to it.value.first()}.toMap()
            obj = om.convertValue(params,c)
        }
        return obj
    }
}