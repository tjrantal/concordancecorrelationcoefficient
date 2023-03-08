%Written by Timo Rantalainen tjrantal at gmail dot com 2013 - 2023 Released
%to the public domain

close all;
clear all;
clc;
javaaddpath('build/libs/concordancecorrelationcoefficient-1.0.1.jar');	%In Octave
%javaclasspath('build/libs/concordancecorrelationcoefficient-1.0.1.jar'); %In Matlab, very useful for testing because you don't need to restart Matlab after recompiling. You do for javaaddpath

%Examples using both the time-varying signal, and constant and equal sample
%rates
dt = 0.01;
t = 0:dt:10;
t2 = t(1:3:end);
t2([50:100,140:170]) = [];
testSig1 =  sin(t); %test signal one used against both the same constant sample rate and the time-varying
testSig2 =  cos(t2);    %Time-varying test signal2
testSig3 = cos(t);  %Constant rate test signal2
% t = 0:0.1:1;

considerIndices = int32(0.2*length(testSig2)):int32(0.8*length(testSig2));
javaConc = javaObject('timo.jyu.TimeVaryingConcordanceCorrelationCoefficient',testSig1,testSig2(considerIndices),t,t2(considerIndices),dt/3);
% figure,plot(javaConc.coefficients)
[ignore, mInd] = max(javaConc.coefficients);
synchOffs = t(mInd);    %Due to including the time stamps, the offset is just the time stamp of t1 at the mInd
%Test the version with an equal and constant sample rate
considerIndices2 = int32(0.2*length(testSig3)):int32(0.8*length(testSig3));
javaConcOrig = javaObject('timo.jyu.ConcordanceCorrelationCoefficient',testSig1,testSig3(considerIndices2));
%Calculate lag, eliminate 20% data from both ends to enable sliding the signals with respect to each other
[ignore, mInd] = max(javaConcOrig.coefficients);
synchOffsInd = double(mInd-considerIndices2(1)+1);  %Synch offset based on indices

%Visualise the match
figure
ah = [];
subplot(2,1,1)
plot(t,testSig1,'k','DisplayName','signal 1 constant rate');
hold on;
plot(t2,testSig2,'r','DisplayName','signal 2 varying rate');
plot(t,testSig3,'b--','DisplayName','signal 3 matches signal 1 sample instants');
title('Original signals')
legend();
xlabel('time [s]');
ylabel('amplitude [au]');
ah(1) = gca();
subplot(2,1,2)
plot(t,testSig1,'k');
hold on;
plot(t2+synchOffs,testSig2,'r');
ah(2) = gca();
plot(t+t(synchOffsInd),testSig3,'b--');
title(sprintf('Synchronised varying offset %.02f equal rate offset %.02f s', synchOffs,t(synchOffsInd)));
xlabel('time [s]');
ylabel('amplitude [au]');
linkaxes(ah,'x');
