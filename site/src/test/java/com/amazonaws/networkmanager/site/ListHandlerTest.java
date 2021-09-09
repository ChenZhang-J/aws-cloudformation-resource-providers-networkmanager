package com.amazonaws.networkmanager.site;

import software.amazon.awssdk.services.networkmanager.model.GetSitesRequest;
import software.amazon.awssdk.services.networkmanager.model.GetSitesResponse;
import software.amazon.awssdk.services.networkmanager.model.ResourceNotFoundException;
import software.amazon.cloudformation.proxy.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ListHandlerTest extends TestBase {
    private ListHandler handler;
    private ResourceModel model;

    @BeforeEach
    public void setup() {
        handler = new ListHandler();
        model = buildSimpleResourceModel();
    }

    @Test
    public void handleRequest_SimpleSuccess() {
        final GetSitesResponse getSitesResponse = GetSitesResponse.builder()
                .sites(buildSimpleSite())
                .build();
        doReturn(getSitesResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(GetSitesRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, context, logger);

        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
    }

    /**
     * A Read call MUST return a NotFound error code if the resource never existed
     */
    @Test
    public void handleRequest_ResourceNotFound() {
        final ResourceNotFoundException exception = ResourceNotFoundException.builder().build();
        doThrow(exception)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(GetSitesRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, context, logger);

        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.NotFound);
    }
}
