package com.grpc.calculator.service;

import com.grpc.calculator.*;
import io.grpc.stub.StreamObserver;

public class CalculatorServiceImpl extends CalculatorServiceGrpc.CalculatorServiceImplBase {

    @Override
    public void add(AddRequest request, StreamObserver<AddResponse> responseObserver) {
        int result = request.getNum1() + request.getNum2();

        responseObserver.onNext(AddResponse.newBuilder()
                                            .setResult(result)
                                            .build());

        responseObserver.onCompleted();
    }

    @Override
    public void factorization(FactorizationRequest request, StreamObserver<FactorizationResponse> responseObserver) {
        int reqNum = request.getReqNum();
        int k = 2;

        while (reqNum > 1) {

            if (reqNum % k == 0) {
                reqNum /= k;

                FactorizationResponse response = FactorizationResponse.newBuilder()
                                                                        .setResult(k)
                                                                        .build();

                responseObserver.onNext(response);
            } else {
                k++;
            }

        }

        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<AverageRequest> average(StreamObserver<AverageResponse> responseObserver) {
        StreamObserver<AverageRequest> requestStreamObserver = new StreamObserver<AverageRequest>() {

            int sum = 0;
            int counter = 0;

            // client로부터 request가 들어왔을 때
            @Override
            public void onNext(AverageRequest value) {
                sum += value.getReqNum();
                counter++;
            }

            // client로부터 error를 받았을 때
            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            // client가 모든 request를 보냈음을 알렸을 때 (client에서 onCompleted() 호출)
            @Override
            public void onCompleted() {
                responseObserver.onNext(AverageResponse.newBuilder()
                                                        .setResult((double)sum / counter)
                                                        .build());
                responseObserver.onCompleted();
            }
        };

        return requestStreamObserver;
    }
}
