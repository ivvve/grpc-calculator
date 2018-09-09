package com.grpc.calculator.service;

import com.grpc.calculator.AddRequest;
import com.grpc.calculator.AddResponse;
import com.grpc.calculator.Calculator;
import com.grpc.calculator.CalculatorServiceGrpc;
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
}
