package br.com.wsp.parking.infra.web.advice

import br.com.wsp.parking.domain.exception.BusinessException
import br.com.wsp.parking.domain.exception.DomainException
import br.com.wsp.parking.domain.exception.InvalidInputException
import br.com.wsp.parking.domain.exception.ResourceNotFoundException
import br.com.wsp.parking.domain.exception.UnavailableResourceException
import br.com.wsp.parking.infra.web.dto.response.ErrorResponse
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.ConstraintViolationException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.resource.NoResourceFoundException

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ApiResponse(
        responseCode = "400",
        description = "Validação de entrada falhou",
        content = [Content(schema = Schema(implementation = ErrorResponse::class))]
    )
    fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val fieldErrors = ex.bindingResult.allErrors
            .filterIsInstance<FieldError>()
            .associate { it.field to (it.defaultMessage ?: "Inválido") }

        val errorResponse = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            type = "VALIDATION_ERROR",
            message = "Validação de entrada falhou",
            details = "Um ou mais campos contêm valores inválidos",
            path = request.requestURI,
            fieldErrors = fieldErrors
        )

        log.warn("Erro de validação: ${ex.message}", ex)
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    /**
     * Tratamento para violações de constraints
     */
    @ExceptionHandler(ConstraintViolationException::class)
    @ApiResponse(
        responseCode = "400",
        description = "Restrição de validação violada",
        content = [Content(schema = Schema(implementation = ErrorResponse::class))]
    )
    fun handleConstraintViolation(
        ex: ConstraintViolationException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val fieldErrors = ex.constraintViolations
            .associate { it.propertyPath.toString() to it.message }

        val errorResponse = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            type = "CONSTRAINT_VIOLATION",
            message = "Restrição de validação violada",
            details = ex.message,
            path = request.requestURI,
            fieldErrors = fieldErrors
        )

        log.warn("Violação de constraint: ${ex.message}", ex)
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    /**
     * Tratamento para ResourceNotFoundException
     */
    @ExceptionHandler(ResourceNotFoundException::class)
    @ApiResponse(
        responseCode = "404",
        description = "Recurso não encontrado",
        content = [Content(schema = Schema(implementation = ErrorResponse::class))]
    )
    fun handleResourceNotFound(
        ex: ResourceNotFoundException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.NOT_FOUND.value(),
            type = ex.errorType,
            message = ex.message ?: "Recurso não encontrado",
            path = request.requestURI
        )

        log.warn("Recurso não encontrado: ${ex.message}")
        return ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
    }

    /**
     * Tratamento para UnavailableResourceException
     */
    @ExceptionHandler(UnavailableResourceException::class)
    @ApiResponse(
        responseCode = "409",
        description = "Recurso indisponível",
        content = [Content(schema = Schema(implementation = ErrorResponse::class))]
    )
    fun handleUnavailableResource(
        ex: UnavailableResourceException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.CONFLICT.value(),
            type = ex.errorType,
            message = ex.message ?: "Recurso indisponível",
            details = "Não há recursos disponíveis para completar a operação",
            path = request.requestURI
        )

        log.warn("Recurso indisponível: ${ex.message}")
        return ResponseEntity(errorResponse, HttpStatus.CONFLICT)
    }

    /**
     * Tratamento para BusinessException
     */
    @ExceptionHandler(BusinessException::class)
    @ApiResponse(
        responseCode = "422",
        description = "Erro de negócio",
        content = [Content(schema = Schema(implementation = ErrorResponse::class))]
    )
    fun handleBusinessException(
        ex: BusinessException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.UNPROCESSABLE_ENTITY.value(),
            type = ex.errorType,
            message = ex.message ?: "Erro ao processar a requisição",
            path = request.requestURI
        )

        log.warn("Erro de negócio: ${ex.message}")
        return ResponseEntity(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Tratamento para InvalidInputException
     */
    @ExceptionHandler(InvalidInputException::class)
    @ApiResponse(
        responseCode = "400",
        description = "Entrada inválida",
        content = [Content(schema = Schema(implementation = ErrorResponse::class))]
    )
    fun handleInvalidInput(
        ex: InvalidInputException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            type = ex.errorType,
            message = ex.message ?: "Entrada inválida",
            path = request.requestURI
        )

        log.warn("Entrada inválida: ${ex.message}")
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    /**
     * Tratamento genérico para DomainException
     */
    @ExceptionHandler(DomainException::class)
    @ApiResponse(
        responseCode = "400",
        description = "Erro de domínio",
        content = [Content(schema = Schema(implementation = ErrorResponse::class))]
    )
    fun handleDomainException(
        ex: DomainException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            type = ex.errorType,
            message = ex.message ?: "Erro ao processar a requisição",
            path = request.requestURI
        )

        log.warn("Erro de domínio: ${ex.message}", ex)
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    /**
     * Tratamento genérico para IllegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException::class)
    @ApiResponse(
        responseCode = "400",
        description = "Argumento inválido",
        content = [Content(schema = Schema(implementation = ErrorResponse::class))]
    )
    fun handleIllegalArgument(
        ex: IllegalArgumentException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            type = "INVALID_ARGUMENT",
            message = ex.message ?: "Argumento inválido",
            path = request.requestURI
        )

        log.warn("Argumento inválido: ${ex.message}")
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    /**
     * Tratamento genérico para IllegalStateException
     */
    @ExceptionHandler(IllegalStateException::class)
    @ApiResponse(
        responseCode = "409",
        description = "Estado inválido",
        content = [Content(schema = Schema(implementation = ErrorResponse::class))]
    )
    fun handleIllegalState(
        ex: IllegalStateException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.CONFLICT.value(),
            type = "INVALID_STATE",
            message = ex.message ?: "Estado inválido para essa operação",
            path = request.requestURI
        )

        log.warn("Estado inválido: ${ex.message}")
        return ResponseEntity(errorResponse, HttpStatus.CONFLICT)
    }

    /**
     * Tratamento para NoResourceFoundException (favicon, etc)
     */
    @ExceptionHandler(NoResourceFoundException::class)
    fun handleNoResourceFound(
        ex: NoResourceFoundException,
        request: HttpServletRequest
    ): ResponseEntity<Void> {
        // Não loga para evitar poluir os logs com requisições de favicon
        return ResponseEntity.notFound().build()
    }

    /**
     * Tratamento genérico para todas as exceções não capturadas
     */
    @ExceptionHandler(Exception::class)
    @ApiResponse(
        responseCode = "500",
        description = "Erro interno do servidor",
        content = [Content(schema = Schema(implementation = ErrorResponse::class))]
    )
    fun handleGlobalException(
        ex: Exception,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            type = "INTERNAL_ERROR",
            message = "Erro interno do servidor",
            details = ex.message,
            path = request.requestURI
        )

        log.error("Erro não esperado: ${ex.message}", ex)
        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}

