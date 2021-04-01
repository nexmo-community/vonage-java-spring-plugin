# Vonage Spring

<img src="https://developer.nexmo.com/assets/images/Vonage_Nexmo.svg" height="48px" alt="Nexmo is now known as Vonage" />

Welcome to Vonage Spring. Vonage Spring is a middleware plugin for Spring designed to make spring developers lives easier, primarily around webhook handling.
This library enables you to setup your app to only accept validated SMS webhooks. It Also enables you to annotate your Spring routes to make parsing web requests easier. This library also enables you to build custom middleware for your app usin Voange Webhook types


## Welcome to Vonage

If you're new to Vonage, you can [sign up for a Vonage API account](https://dashboard.nexmo.com/sign-up?utm_source=DEV_REL&utm_medium=github&utm_campaign=github-repo) and get some free credit to get you started.




<!-- add other sections as appropriate for your repo type -->

## How to Use

There are three primary functions this library provides

### Annotated Webhook Parsing

The default paraemter annotations `@RequestBody` and `RequestParameter` from spring don't unfortunatly allow you to automatically parse inbound webhook objects. The vonage-spring library helps us get around this by introducing the `VonageWebhook` annotation, simply annotate a parameter in a spring route with the `VonageWebhook` annotation, and it will automatically parse the webhook for you - whichever method/content-type it's using. This is intended to work for Vonage webhook objects, however it will generalize out to other webhook types as well. Usage of the annotation is quite simple:

```java
@RestController
public class InboundSmsController {
    @PostMapping("/webhooks/inbound-sms")
    public Object InboundSms(@VonageWebhook MessageEvent msg){
        System.out.println(msg.getMsisdn());
        System.out.println(msg.getText());
        return null;
    }
    @GetMapping("/webhooks/inbound-sms")
    public Object InboundSmsGet(@VonageWebhook MessageEvent msg){
        System.out.println(msg.getMsisdn());
        System.out.println(msg.getText());
        return null;
    }
}
```

Will automatically parse the webhook out of the body or query of the request into the appropriate object. In order to use the `VonageWebhook` annotation, you must register its Resolver with you web config:


```java
@EnableWebMvc
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers){
        resolvers.add(new VonageArgumentResolver());
    }
}
```
### Verified SMS Handling

The Vonage SMS API enables you to add [signatures](https://developer.nexmo.com/concepts/guides/signing-messages) to your SMS webhooks. This allows you to validate the origin of your inbound SMS messages are in fact from Vonage.

To have the vonage-spring library validate your inbound SMS webhooks use the `VerifiedSmsHandler` interceptor by adding the following to your Web Config

```java
@EnableWebMvc
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new VerifiedSmsHandler("VONAGE_SIGNATURE_SECRET", HashUtil.HashType.HASH_TYPE)).addPathPatterns("/path/to/sms/route");
    }
}
```

### Build Your Own Vonage Middleware

If there are any workflows you want to add as middleware to your app you can use the `VonageWebhookHandler` interceptor to intercept webhooks before they are handed to your app. To use this you will need to implement a `VonageWebhookDelegate` object to handle the webhooks, and then pass it into the `VonageWebhookHandler` for it's use.

```java
@EnableWebMvc
@Configuration
public class WebConfig implements WebMvcConfigurer {
    // add class extending VonageWebhookDelegate
    public class SmsWebhookHandler extends VonageWebhookDelegate<MessageEvent>{
        @Override
        public boolean handleWebhook(MessageEvent webhookObject, HttpServletRequest request, HttpServletResponse response) {
            System.out.println(webhookObject.getMessageId());
            System.out.println(webhookObject.getMsisdn());
            System.out.println(webhookObject.getTo());
            System.out.println(webhookObject.getText());
            return false;
        }
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //register delegate as an interceptor with spring inside of the WebConfig
        registry.addInterceptor(new VonageWebhookHandler(new SmsWebhookHandler(), MessageEvent.class, false)).addPathPatterns("/webhook/inbound-sms");       
        
    }
}
```

## Getting Help



We love to hear from you so if you have questions, comments or find a bug in the project, let us know! You can either:

* Open an issue on this repository
* Tweet at us! We're [@VonageDev on Twitter](https://twitter.com/VonageDev)
* Or [join the Vonage Developer Community Slack](https://developer.nexmo.com/community/slack)

## Further Reading

* Check out the Developer Documentation at <https://developer.nexmo.com>

<!-- add links to the api reference, other documentation, related blog posts, whatever someone who has read this far might find interesting :) -->

