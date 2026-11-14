package fr.nordlys.shoploc.merchants.domain.exception;

/** The user's role does not allow this operation. */
public class OperationNotAllowedException extends RuntimeException {

    public OperationNotAllowedException(String message) {
        super(message);
    }
}
