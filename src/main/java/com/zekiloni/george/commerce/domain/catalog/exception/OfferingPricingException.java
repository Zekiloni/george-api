package com.zekiloni.george.commerce.domain.catalog.exception;

/**
 * Thrown when an offering's pricing cannot be resolved for the requested
 * duration / unit, or when its catalog configuration is internally inconsistent
 * (no pricing, mixed currencies, missing billing config).
 */
public class OfferingPricingException extends RuntimeException {
    public OfferingPricingException(String message) {
        super(message);
    }
}
