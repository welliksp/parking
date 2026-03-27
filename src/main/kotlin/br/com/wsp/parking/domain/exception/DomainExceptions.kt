package br.com.wsp.parking.domain.exception

open class DomainException(
    message: String,
    val errorType: String = "DOMAIN_ERROR",
    cause: Throwable? = null
) : RuntimeException(message, cause)


class ResourceNotFoundException(
    message: String,
    cause: Throwable? = null
) : DomainException(message, "RESOURCE_NOT_FOUND", cause)


class BusinessException(
    message: String,
    cause: Throwable? = null
) : DomainException(message, "BUSINESS_ERROR", cause)


class UnavailableResourceException(
    message: String,
    cause: Throwable? = null
) : DomainException(message, "UNAVAILABLE_RESOURCE", cause)


class InvalidInputException(
    message: String,
    cause: Throwable? = null
) : DomainException(message, "INVALID_INPUT", cause)

