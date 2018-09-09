package com.grpc.calculator.client;

import com.grpc.calculator.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.Iterator;

public class CalculatorClient {

    public static void main(String[] args) {
        runFactorization(2394);
    }

    private static ManagedChannel getChannel() {
        return ManagedChannelBuilder.forAddress("localhost", 8888)
                                    .usePlaintext()
                                    .build();
    }

    private static CalculatorServiceGrpc.CalculatorServiceBlockingStub getBlockingStub(ManagedChannel channel) {
        return CalculatorServiceGrpc.newBlockingStub(channel);
    }

    private static void runAdd(int num1, int num2) {
        ManagedChannel channel = getChannel();
        CalculatorServiceGrpc.CalculatorServiceBlockingStub blockingStub = getBlockingStub(channel);

        System.out.println("gRPC client started");

        AddRequest addRequest = AddRequest.newBuilder()
                .setNum1(num1)
                .setNum2(num2)
                .build();

        AddResponse addResponse = blockingStub.add(addRequest);

        System.out.printf("%d + %d = %d\n", num1, num2, addResponse.getResult());

        channel.shutdown();
        System.out.println("gRPC client shutdown");
    }

    private static void runFactorization(int num) {
        ManagedChannel channel = getChannel();
        CalculatorServiceGrpc.CalculatorServiceBlockingStub blockingStub = getBlockingStub(channel);

        System.out.println("gRPC client started");

        FactorizationRequest request = FactorizationRequest.newBuilder()
                                                            .setReqNum(num)
                                                            .build();

        Iterator<FactorizationResponse> responseIterator = blockingStub.factorization(request);

        System.out.printf("%d = ", num);

        responseIterator.forEachRemaining( (response) -> {
            System.out.printf("%d * ", response.getResult());
        } );

        channel.shutdown();
        System.out.println("\ngRPC client shutdown");
    }

}
