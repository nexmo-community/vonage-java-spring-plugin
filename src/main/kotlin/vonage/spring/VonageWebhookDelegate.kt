package vonage.spring

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

abstract class VonageWebhookDelegate<T:Any> {
    abstract fun handleWebhook(webhookObject: T, request: HttpServletRequest, response: HttpServletResponse) : Boolean
    open fun proceedToNextHandlerOnFailedParsing() : Boolean{
        return false
    }
    open fun proceedToNextHandlerOnSuccessfulParsing():Boolean{
        return true
    }
}