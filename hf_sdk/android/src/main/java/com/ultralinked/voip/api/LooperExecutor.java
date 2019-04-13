/*
 * libjingle
 * Copyright 2015 Google Inc.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * specified, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.ultralinked.voip.api;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.concurrent.Executor;


public class LooperExecutor extends Thread implements Executor {
  private static final String TAG = "LooperExecutor";
  // Object used to signal that looper thread has started and Handler instance
  // associated with looper thread has been allocated.
  // 对象表示循环线程已开始并用循环线程关联到已经分配的处理实例上
  private final Object looperStartedEvent = new Object();
  private Handler handler = null;
  private boolean running = false;
  private long threadId;

  @Override
  public void run() {
    Looper.prepare();
    synchronized (looperStartedEvent) {
      Log.d(TAG, "Looper thread started.");
      handler = new Handler();
      // 这里handler对象是实例化LooperExecutor对象所在线程的Handler
      threadId = Thread.currentThread().getId();
      looperStartedEvent.notify();//looperStartedEvent通知
    }
    Looper.loop();// 跳转到requestStart()中的looperStartedEvent.wait()后执行
  }

  public synchronized void requestStart() {
    if (running) {
      return;
    }
    running = true;
    handler = null;
    start();
    // Wait for Hander allocation.
    synchronized (looperStartedEvent) {
      while (handler == null) {
        try {
          looperStartedEvent.wait();//跳转到run()开始执行
        } catch (InterruptedException e) {
          Log.e(TAG, "Can not start looper thread");
          running = false;
        }
      }
    }
  }

  public synchronized void requestStop() {
    if (!running) {
      return;
    }
    running = false;
    // post到创建的线程结束Looper
    handler.post(new Runnable() {
      @Override
      public void run() {
        Looper.myLooper().quit();
        Log.d(TAG, "Looper thread finished.");
      }
    });
  }

  // Checks if current thread is a looper thread.
  public boolean checkOnLooperThread() {
    return (Thread.currentThread().getId() == threadId);
  }

  @Override
  public synchronized void execute(final Runnable runnable) {
    if (!running) {
      Log.w(TAG, "Running looper executor without calling requestStart()");
      return;
    }
    if (checkOnLooperThread()) {
      // 当前执行代码在创建LooperExecutor对象线程中，那就直接在当前线程执行
      runnable.run();
    } else {
      // 当前执行代码不在创建LooperExecutor对象线程中，那就post到创建LooperExecutor对象的线程中去执行
      handler.post(runnable);
    }
  }
}
