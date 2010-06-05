/*
 * HelloAsyncCalls.java.java
 *
 * Created on 03-12-2010 11:33:18 PM
 *
 * Copyright 2010 Jonathan Colt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.colt.nicity.asynchro.examples;

import com.colt.nicity.asynchro.AsyncCall;
import com.colt.nicity.asynchro.AsyncResponses;
import com.colt.nicity.asynchro.CallStack;
import com.colt.nicity.core.process.IAsyncResponse;
import com.colt.nicity.core.lang.IOut;
import com.colt.nicity.core.lang.SysOut;

public class HelloAsyncCalls {

    static public void main(String[] _args) {
        get(new SysOut(), 10, new IAsyncResponse<double[]>() {

            public void response(IOut _, double[] _response) {
                System.out.println("Response");
                for (int i = 0; i < _response.length; i++) {
                    System.out.println(i + "):" + _response[i]);
                }
            }

            public void error(IOut _, Throwable _t) {
                System.out.print("Error:" + _t);
            }
        });
    }
    static private CallStack callStack = new CallStack();

    static public void get(IOut _, int _count,
        final IAsyncResponse<double[]> _got) {
        double[] responses = new double[_count];
        AsyncResponses<Integer, Double, double[]> calls = new AsyncResponses<Integer, Double, double[]>(
            callStack, responses) {

            @Override
            public void response(IOut _, AsyncCall<Integer> _call,
                Double _response, IAsyncResponse _doneProcessingResponse) {
                _.out("Storing..." + _call.hashObject());
                getCallsResponses()[_call.hashObject()] = _response;
                _doneProcessingResponse.response(_, _response);
            }

            public void response(IOut _, double[] _response) {
                _got.response(_, getCallsResponses());
            }

            public void error(IOut _, Throwable _t) {
                _got.error(_, _t);
            }
        };
        for (int i = 0; i < responses.length; i++) {
            calls.enqueue(_, new AsyncCall<Integer>(i) {

                public void invoke(IOut _) {
                    getRandom(_, response());
                }
            });
        }
        calls.close(_);
    }

    // a method that sleeps for a random duration and then produces a random number
    static public void getRandom(final IOut _, final IAsyncResponse _response) {
        new Thread() {

            @Override
            public void run() {
                try {
                    _.out("Sleeping");
                    Thread.sleep(1000 + (long) (Math.random() * 3000));
                }
                catch (Exception x) {
                }
                _.out("Producing...");
                _response.response(_, Math.random());
            }
        }.start();
    }
}
