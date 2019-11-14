/*
 * Copyright 2015 MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mongodb.reactivestreams.client;

import com.mongodb.client.model.changestream.ChangeStreamDocument;
import org.bson.Document;
import org.junit.Test;
import org.reactivestreams.Subscription;

import static org.junit.Assert.assertFalse;

public class SubscriptionExceptionTest extends DatabaseTestCase {

    @Test
    public void testSubscriptionExceptionOnCancel() throws InterruptedException {
        final boolean[] exceptionFound = {false};

        try {
            collection.watch().subscribe(new TestSubscriber<ChangeStreamDocument<Document>>() {
                @Override
                public void onSubscribe(final Subscription s) {
                    try {
                        s.request(1);
                        new Thread() {
                            public void run() {
                                try {
                                    Thread.sleep(100);
                                    s.cancel();
                                } catch (InterruptedException e) {
                                }
                            }
                        }.start();
                    } catch (Exception e) {
                        exceptionFound[0] = true;
                    }
                }
            });
            Thread.sleep(2000);
        } catch (Exception e) {
            exceptionFound[0] = true;
        }
        assertFalse(exceptionFound[0]);
    }
}
