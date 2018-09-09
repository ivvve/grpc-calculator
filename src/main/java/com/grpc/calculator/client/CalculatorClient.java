package com.grpc.calculator.client;

import com.grpc.calculator.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CalculatorClient {

    public static void main(String[] args) throws Exception {
        runAverage();
    }

    private static ManagedChannel getChannel() {
        return ManagedChannelBuilder.forAddress("localhost", 8888)
                                    .usePlaintext()
                                    .build();
    }

    private static CalculatorServiceGrpc.CalculatorServiceBlockingStub getBlockingStub(ManagedChannel channel) {
        return CalculatorServiceGrpc.newBlockingStub(channel);
    }

    private static CalculatorServiceGrpc.CalculatorServiceStub getAsyncStub(ManagedChannel channel) {
        return CalculatorServiceGrpc.newStub(channel);
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

    private static void runAverage() throws InterruptedException {
        ManagedChannel channel = getChannel();
        CalculatorServiceGrpc.CalculatorServiceStub asyncStub = getAsyncStub(channel);

        System.out.println("gRPC client started");

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<AverageRequest> requestStreamObserver = asyncStub.average(new StreamObserver<AverageResponse>() {
            // server로부터 response가 왔을 때 (여기서는 평균값을 한번 주기 때문에 한번만 호출)
            @Override
            public void onNext(AverageResponse value) {
                System.out.println(" = " + value.getResult());
            }

            // server로부터 error를 받았을 때
            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            // server가 모든 response를 보냈음을 알렸을 때 (server에서 onCompleted() 호출)
            @Override
            public void onCompleted() {
                latch.countDown();
                System.out.println("response complete");
            }
        });

        Random random = new Random();

        for (int i = 0; i < 10; i++) {
            int randomNum = random.nextInt(100);
            System.out.print(" + " + randomNum);

            AverageRequest request = AverageRequest.newBuilder()
                                                    .setReqNum(randomNum)
                                                    .build();

            // server에 request를 보냄
            requestStreamObserver.onNext(request);
        }

        // server에게 request를 모두 보냈음을 알림
        requestStreamObserver.onCompleted();

        // count가 0이 될때까지 기다린다
        latch.await(3, TimeUnit.SECONDS);

        channel.shutdown();
        System.out.println("client shutdown");
    }

}
