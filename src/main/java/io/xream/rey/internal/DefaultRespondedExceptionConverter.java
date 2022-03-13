package io.xream.rey.internal;

import io.xream.internal.util.ExceptionUtil;
import io.xream.internal.util.JsonX;
import io.xream.rey.api.RespondedExceptionConverter;
import io.xream.rey.api.ReyHttpStatus;
import io.xream.rey.exception.ReyInternalException;
import io.xream.rey.exception.ReyRuntimeException;
import io.xream.rey.proto.RemoteExceptionProto;
import io.xream.rey.proto.RemoteExceptionUnknown;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.List;

/**
 * @author Sim
 */
public class DefaultRespondedExceptionConverter implements RespondedExceptionConverter {

    @Override
    public void convertNot200ToException(int status, String uri, String response) throws ReyInternalException{
        if (status == ReyHttpStatus.TO_CLIENT.getStatus()) {
            RemoteExceptionProto proto = JsonX.toObject(response, RemoteExceptionProto.class);
            throw ReyInternalException.createToClient(uri, proto);
        }
    }

    @Override
    public void convertRespondedException(ReyInternalException rie) throws ReyInternalException{

        Throwable e= rie.getCause();

        final String uri = rie.getUri();

        if (e instanceof ResourceAccessException){
            ResourceAccessException rae = (ResourceAccessException)e;
            Throwable t = rae.getRootCause();
            String str = rae.getLocalizedMessage();
            String[] arr = str.split(";");
            final String message = arr[0];
            if (t instanceof ConnectException) {
                throw ReyInternalException.create(-1 ,message, io.xream.internal.util.ExceptionUtil.getStack(e),null,uri);
            }else if (t instanceof SocketTimeoutException) {
                throw ReyInternalException.create(-2 ,message, io.xream.internal.util.ExceptionUtil.getStack(e),null,uri);
            }
        }else if (e instanceof HttpClientErrorException){
            HttpClientErrorException ee = (HttpClientErrorException)e;
            String str = ee.getLocalizedMessage();
            str = adaptJson(str);
            RemoteExceptionUnknown unknown = JsonX.toObject(str,RemoteExceptionUnknown.class);
            List<RemoteExceptionProto.ExceptionTrace> traces = unknown.getExceptionTraces();
            if (traces == null) { //unknown
                String stack = io.xream.internal.util.ExceptionUtil.getStack(e);
                throw ReyInternalException.create(
                        ee.getStatusCode().value(),
                        unknown.getError(),
                        stack,
                        null,
                        unknown.getPath());
            }else {
                throw ReyInternalException.createToClient(ee.getStatusCode().value(),
                        unknown.getError(),
                        uri,
                        traces
                );
            }
        }else if (e instanceof HttpServerErrorException) {
            HttpServerErrorException ee = (HttpServerErrorException)e;
            ee.printStackTrace();
            String str = ee.getLocalizedMessage();
            str = adaptJson(str);
            RemoteExceptionUnknown unknown = JsonX.toObject(str,RemoteExceptionUnknown.class);
            List<RemoteExceptionProto.ExceptionTrace> traces = unknown.getExceptionTraces();

            if (traces == null) { //unknown
                String stack = io.xream.internal.util.ExceptionUtil.getStack(e);
                throw ReyInternalException.create(
                        ee.getStatusCode().value(),
                        unknown.getError(),
                        stack,
                        null,
                        unknown.getPath());
            }else {
                throw ReyInternalException.createToClient(ee.getStatusCode().value(),
                        unknown.getError(),
                        uri,
                        traces
                );
            }

        }else if (e instanceof IllegalArgumentException) {
            throw ReyInternalException.create(
                    400,
                    e.getMessage(),
                    ExceptionUtil.getStack(e),
                    null,
                    uri);
        }

        throw new ReyRuntimeException(e);
    }

}
