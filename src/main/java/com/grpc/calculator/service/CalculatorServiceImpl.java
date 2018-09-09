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
}
