package com.grpc.calculator.client;

import com.grpc.calculator.AddRequest;
import com.grpc.calculator.AddResponse;
import com.grpc.calculator.CalculatorServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class CalculatorClient {

    public static void main(String[] args) {
        runAdd(1, 3);
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
}
