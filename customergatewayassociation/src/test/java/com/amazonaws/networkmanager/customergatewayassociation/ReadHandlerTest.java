package com.amazonaws.networkmanager.customergatewayassociation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.networkmanager.model.GetCustomerGatewayAssociationsRequest;
import software.amazon.awssdk.services.networkmanager.model.GetCustomerGatewayAssociationsResponse;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class ReadHandlerTest extends TestBase {
    private ReadHandler handler;
    private ResourceModel model;

    @BeforeEach
    public void setup() {
        handler = new ReadHandler();
        model = buildResourceModel();
    }

    @Test
    public void handleRequest_SimpleSuccess() {
        final GetCustomerGatewayAssociationsResponse getCustomerGatewaysAssociationResponse = GetCustomerGatewayAssociationsResponse.builder()
                .customerGatewayAssociations(buildCustomerGatewayAssociation())
                .build();
        doReturn(getCustomerGatewaysAssociationResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(GetCustomerGatewayAssociationsRequest.class), any());
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
        final GetCustomerGatewayAssociationsResponse getCustomerGatewaysAssociationResponse = GetCustomerGatewayAssociationsResponse.builder()
                // an empty customerGateway association response
                .build();
        doReturn(getCustomerGatewaysAssociationResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(GetCustomerGatewayAssociationsRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, context, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.NotFound);
    }
}
