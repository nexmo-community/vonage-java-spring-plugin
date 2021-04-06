package vonage.spring

/**
 * Annotate a parameter in a method that you would like to parse either the body of, or query of into. This will
 * Parse inbound webhooks in a way that we would expect to see Voange Webhooks, this will generalize to most payloads
 * that are JSON/Url-encoded-form or even queries
 *
 * To use add a VonageArgumentResolver to the list of resolvers using the addArgumentResolvers method in you WebMvcConfigurer
 *
 * @author Steve Lorello
 * @since 0.1
 * @see VonageArgumentResolver
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class VonageWebhook {
}